package it.erweb.crawler.main;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import it.erweb.crawler.configurations.PropertiesManager;
import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.JPAManager;
import it.erweb.crawler.dbManager.repository.BandoRepository;
import it.erweb.crawler.dbManager.repository.PubblicazioneRepository;
import it.erweb.crawler.dbManager.repository.UtenteRepository;
import it.erweb.crawler.expregMatcher.Matcher;
import it.erweb.crawler.httpClientUtil.HttpGetter;
import it.erweb.crawler.httpClientUtil.Notifier;
import it.erweb.crawler.model.*;
import it.erweb.crawler.parser.HtmlParser;
import it.erweb.crawler.weka.BandoObjValidator;

/**
 * Crawler's main class
 */
public class Main
{
	private static Logger logger = Logger.getLogger(Main.class.getName());
	
	public static void main(String[] args) throws JPAException, FileNotFoundException
	{
		String html = "";
		int i = 0, j = 0, length, length2;
		List<Pubblicazione> publications = new ArrayList<Pubblicazione>();
		List<String> publicationsHtml = new ArrayList<String>();
		List<Bando> bans = new ArrayList<Bando>();
		List<Utente> users = new ArrayList<Utente>();
		boolean newAvailable, notify;
		
		//inizializza configurazioni
		logger.info("Starting Crawler...");
		init();
		logger.info("OK\n");
		
		try
		{
			//si connette alla pagina delle pubblicazioni
			logger.info("Connecting to publications page: " + PropertiesManager.PUBLICATIONS_HOME_URL + "...");
			html = HttpGetter.get(PropertiesManager.PUBLICATIONS_HOME_URL);
			logger.info("OK\n");
			
			//ricava le prime informazioni sulle pubblicazioni disponibili e le salva nel DB
			logger.info("Searching for publications...");
			newAvailable = HtmlParser.getPublications(html);
			logger.info("OK\n");
			
			if(! (newAvailable))
			{
				logger.info("There are no new publications!");
				return;
			}
			
			//carica dal DB tutte le pubblicazioni appena inserite ("DA_SCARICARE")
			logger.info("Retrieving publications...");
			publications = PubblicazioneRepository.getAllDaScaricare();
			logger.info("OK\n");

			//per ogni pubblicazione "DA_SCARICARE", ricava URL e scarica (aggiungendo gli html a una lista)
			i = 0;
			length = publications.size();
			logger.info("Downloading all publications...");
			for(Pubblicazione pub : publications)
			{
				logger.info("Downloading Publication " + (++i) + " of " + (length + 1) + "\nConnecting to: " + pub.getUrl() + "...");
				Thread.sleep(PropertiesManager.SYS_HTTP_GET_FREQUENCY);
				publicationsHtml.add(HttpGetter.get(pub.getUrl()));	
			}
			logger.info("OK\n");
	
			//scorre le pubblicazioni e ricava i bandi, li inserisce nel DB
			i = 0;
			length = publicationsHtml.size();
			logger.info("Parsing all publications...\n");
			for(String pub : publicationsHtml)
			{
				logger.info("Parsing publication n. " + (++i) + " of " + (length + 1) + "...");
				//passa anche la Pubblicaziome, per collegare i bandi alla relativa pubblicazione
				HtmlParser.getPublicationBans(pub, publications.get(i));	
			}
			logger.info("OK\n");
			
			//carica dal DB tutti i bandi appena inseriti ("DA_PARSIFICARE")
			logger.info("Retrieving all bans...");
			bans = BandoRepository.getAllDaParsificare();
			logger.info("OK\n");

			//scarica tutti i bandi appena recuperati e aggiorna il DB con il testo
			i = 0;
			length = bans.size();
			logger.info("Downloading all bans...\n");
			for(Bando ban : bans)
			{
				logger.info("Downloading ban n. " + (++i) + " of " + (length + 1) + "\nConnecting to: " + ban.getUrl() + "...");
				Thread.sleep(PropertiesManager.SYS_HTTP_GET_FREQUENCY);
				html = HttpGetter.get(ban.getUrl());
	
				//Inizialmente salva tutto l'html del bando come testo
				BandoRepository.updateText(ban, html);	
			}
			logger.info("OK\n");
			
			//processa i bandi di cui ora si conosce il testo, aggiornando oggetto, cig e stato nel DB
			i = 0;
			logger.info("Parsing all bans...");
			for(Bando ban : bans)
			{				
				logger.info("Parsing ban n. " + (++i) + " of " + (length + 1) + "...");
				HtmlParser.parseBan(ban);
			}
			logger.info("OK\n");
			
			
			//processa tutti gli utenti e per ognuno
			//cerca in tutti i bandi se qualche bando fa match 
			
			//recupera tutti gli utenti
			logger.info("Retrieving ll Users...");
			users = UtenteRepository.getAllUsers();
			logger.info("OK\n");
			
			//scorre tutti gli utenti: per ciascun utente, scorre tutti i bandi e tenta il match
			i = 0;
			j = 0;
			length = users.size();
			length2 = bans.size();
			logger.info("Matching all RegExps...");
			for(Utente usr : users)
			{
				logger.info("Processing User n. " + (++i) + " of " + length + "...");
				//prende solo i bandi la cui data e' successiva a ultima data notifica utente 
				for(Bando ban : BandoRepository.getAllParsificatiNuovi())
				{
					//prova con il match
					logger.info("Searching Ban n. " + (++j) + " of " + length2 + "; (User " + i + " of " + length + ")...");
					notify = Matcher.tryMatch(usr, ban);
					
					if(notify)
						Notifier.notifyUser(ban);
				}
			}
			logger.info("OK\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			JPAManager.close();
		}
		
		logger.info("Crawler has finished.");
		
		return;
	}
	
	private static void init()
	{		
		try
		{
			//legge il file di configurazione e salva le configs in PropertiesManager
			PropertiesManager.loadProperties();
			
			//carica il file di train e configura il validatore oggetti
			BandoObjValidator.train();
			
			//inizializza repository JPA db (e ache tutti gli implementers)
			JPAManager.init();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
