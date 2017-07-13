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
import it.erweb.crawler.httpClientUtil.HttpGetter;
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
		int i = 0;
		List<Pubblicazione> publications = new ArrayList<Pubblicazione>();
		List<String> publicationsHtml = new ArrayList<String>();
		List<Bando> Bans = new ArrayList<Bando>();
		boolean newAvailable;
		
		//inizializza configurazioni
		logger.info("Starting Crawler...");
		init();
		logger.info("OK\n");
		
		try
		{
/*
			//si connette alla pagina delle pubblicazioni
			logger.info("Connecting to publications page: " + PropertiesManager.PUBLICATIONS_HOME_URL + " ...");
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
*/			
			//carica dal DB tutte le pubblicazioni appena inserite ("DA_SCARICARE")
			logger.info("Retrieving publications...");
			publications = PubblicazioneRepository.getAllDaScaricare();
			logger.info("OK\n");

			//per ogni pubblicazione "DA_SCARICARE", ricava URL e scarica (aggiungendo gli html a una lista)
			logger.info("Downloading all publications...");
			for(Pubblicazione pub : publications)
			{
				logger.info("Connecting to: " + pub.getUrl() + " ...");
				Thread.sleep(PropertiesManager.SYS_HTTP_GET_FREQUENCY);
				publicationsHtml.add(HttpGetter.get(pub.getUrl()));
				
				
				
				break;
				
				
			}
			logger.info("OK\n");
	
			//scorre le pubblicazioni e ricava i bandi, li inserisce nel DB
			logger.info("Parsing all publications...\n");
			for(String pub : publicationsHtml)
			{
				logger.info("Parsing publication n. " + (i + 1) + " ...");
				//passa anche la Pubblicaziome, per collegare i bandi alla relativa pubblicazione
				HtmlParser.getPublicationBans(pub, publications.get(i));	
				i++;
			}
			logger.info("OK\n");
			
			//carica dal DB tutti i bandi appena inseriti ("DA_PARSIFICARE")
			logger.info("Retrieving bans...\n");
			Bans = BandoRepository.getAllDaParsificare();
			logger.info("OK\n");

			//scarica tutti i bandi appena recuperati e aggiorna il DB con il testo
			logger.info("Downloading all bans...\n");
			for(Bando ban : Bans)
			{
				logger.info("Connecting to: " + ban.getUrl() + " ...");
				Thread.sleep(PropertiesManager.SYS_HTTP_GET_FREQUENCY);
				html = HttpGetter.get(ban.getUrl());
	
				//Inizialmente salva tutto l'html del bando come testo
				BandoRepository.updateText(ban, html);	
			}
			logger.info("OK\n");
			
			//processa i bandi di cui ora si conosce il testo, aggiornando oggetto, cig e stato nel DB
			i = 0;
			logger.info("Parsing all bans...\n");
			for(Bando ban : Bans)
			{				
				logger.info("Parsing ban n. " + (++i) + "...\n");
				HtmlParser.parseBan(ban);
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
