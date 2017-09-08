package it.erweb.crawler.main;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import it.erweb.crawler.configurations.PropertiesManager;
import it.erweb.crawler.database.model.Bando;
import it.erweb.crawler.database.model.Notifica;
import it.erweb.crawler.database.model.Pubblicazione;
import it.erweb.crawler.database.model.Utente;
import it.erweb.crawler.database.repository.JpaDao;
import it.erweb.crawler.database.repository.JpaException;
import it.erweb.crawler.database.services.BandoService;
import it.erweb.crawler.database.services.ExpregService;
import it.erweb.crawler.database.services.NotificaService;
import it.erweb.crawler.database.services.PubblicazioneService;
import it.erweb.crawler.database.services.UtenteService;
import it.erweb.crawler.httpClientUtil.HttpGetter;
import it.erweb.crawler.httpClientUtil.Notifier;
import it.erweb.crawler.parser.HtmlParser;
import it.erweb.crawler.weka.BandoObjValidator;

/**
 * Crawler's main class
 */
public class Main
{
	private static Logger logger = Logger.getLogger(Main.class.getName());
	
	public static void main(String[] args) throws JpaException, FileNotFoundException
	{
		String html = "";
		int i = 0, j = 0, length, length2;
		List<Pubblicazione> publications = new ArrayList<Pubblicazione>();
		List<String> publicationsHtml = new ArrayList<String>();
		List<Bando> bans;
		List<Utente> users;
		List<Notifica> notifications;
		boolean newAvailable, notify;
		Date lastBanDate;
		
		//inizializza configurazioni
		logger.info("Starting Crawler...\n");
		init();
		
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
				logger.info("There are no new publications!\n\tSearching for old unfinished work...\n");
			else
				logger.info("New publications available!\n");
			
			//carica dal DB tutte le pubblicazioni appena inserite ("DA_SCARICARE")
			logger.info("Retrieving any publication to be downloaded...");
			publications = PubblicazioneService.getAllDaScaricare();
			logger.info("OK\n");

			//soltanto se ci sono delle pubblicazioni "DA_SCARICARE" nel db,
			//le scarica e aggiunge tutti i loro bandi al db;
			//potrebbero invece esserci solo pubblicazioni "SCARICATA" ma ancora bandi "DA_PARSIFICARE"
			if(publications.size() > 0)
			{
				//per ogni pubblicazione "DA_SCARICARE", ricava URL e scarica (aggiungendo gli html a una lista)
				i = 0;
				length = publications.size();
				logger.info("Downloading all publications...");
				for(Pubblicazione pub : publications)
				{
					logger.info("\tDownloading Publication " + (++i) + " of " + length + "\n\tConnecting to: " + pub.getUrl() + "...");
					Thread.sleep(PropertiesManager.SYS_HTTP_GET_FREQUENCY);
					publicationsHtml.add(HttpGetter.get(pub.getUrl()));	
				}
				logger.info("OK\n");
	
				//scorre le pubblicazioni e ricava i bandi, li inserisce nel DB
				i = 0;
				length = publicationsHtml.size();
				logger.info("Parsing all publications...");
				for(String pub : publicationsHtml)
				{
					logger.info("\tParsing publication n. " + (i + 1) + " of " + length + "...");
					//passa anche la Pubblicaziome, per collegare i bandi alla relativa pubblicazione
					HtmlParser.getPublicationBans(pub, publications.get(i++));	
				}
				logger.info("OK\n");
			}
			else
				logger.info("There are no old publications to be downloaded\n");
			
			//carica dal DB tutti i bandi appena inseriti ("DA_PARSIFICARE")
			//oppure potrebbero essercene di avanzati
			logger.info("Retrieving all bans to be parsed...");
			bans = BandoService.getAllDaParsificare();
			logger.info("OK\n");

			//scarica tutti i bandi appena recuperati e aggiorna il DB con il testo
			i = 0;
			length = bans.size();
			if(length > 0)
			{
				logger.info("Downloading all bans...\n");
				for(Bando ban : bans)
				{
					logger.info("\tDownloading ban n. " + (++i) + " of " + length + "\n\tConnecting to: " + ban.getUrl() + "...");
					Thread.sleep(PropertiesManager.SYS_HTTP_GET_FREQUENCY);
					html = HttpGetter.get(ban.getUrl());
					
					//Inizialmente salva tutto l'html del bando come testo
					BandoService.updateText(ban, html);	
				}
				logger.info("OK\n");
				
				//processa i bandi di cui ora si conosce il testo, aggiornando oggetto, cig e stato nel DB	
				i = 0;
				length = bans.size();
				logger.info("Parsing all bans...");
				for(Bando ban : bans)
				{				
					logger.info("\tParsing ban n. " + (++i) + " of " + length + "...");
					HtmlParser.parseBan(ban);
				}
				logger.info("OK\n");
			}
			else
				logger.info("There are no old bans that need to be downloaded\n");
			
			
			//processa tutti gli utenti e per ognuno
			//cerca in tutti i bandi se qualche bando fa match 
			//può farlo anche se non c'è nessun nuovo bando
		
			//recupera tutti gli utenti
			logger.info("Bans - Users Matching:\nRetrieving all Users...");
			users = UtenteService.getAllUsers();
			logger.info("OK\n");
			
			//recupera tutti i bandi parsificati
			logger.info("Retrieving all newest Bans...");
			bans = BandoService.getLatestParsificato();
			logger.info("OK\n");

			if(bans.size() > 0)
			{
				//scorre tutti gli utenti: per ciascun utente, scorre tutti i bandi e tenta il match
				i = 0;
				length = users.size();
				length2 = bans.size();
				lastBanDate = bans.get(0).getDtInserimento();
				logger.info("Matching all RegExps...");
				for(Utente usr : users)
				{
					j = 0;
					logger.info("\tProcessing User n. " + (++i) + " of " + length + "...");
					for(Bando ban : bans)
					{
						//tiene conto della data dell'ultimo bando
						lastBanDate = ban.getDtInserimento().after(lastBanDate) ? ban.getDtInserimento() : lastBanDate;
								
						//prima controlla la data: magari utente e' gia' stato notificato per quel bando
						if(ExpregService.checkDate(usr, ban))
						{
							//prova con il match
							logger.info("\tSearching Ban n. " + (++j) + " of " + length2 + ";\n\t\t(User " + i + " of " + length + ")...");
							notify = ExpregService.tryMatchUserExpreg(usr, ban);
						
							if(notify)
							{
								//se unbando fa match, lo inserisce nella tabella notifiche
								NotificaService.insertNotifica(usr, ban);
								logger.info("\tMatch: ban " + ban.getCdEsterno() + " with user " + usr.getUsername());
							}
						}
					}
					
					//in ogni caso, finito il blocco di bandi nuovi, aggiorna data ultima notifica utente:
					//mette data bando (simile per tutto il blocco) + 1 sec
					//(anche se non è stato veramente notificato,
					//serve per ricordarsi che l'utente non deve più essere potenzialmente notificato per bandi
					//più vecchi di quello corrente, perchè ormai già controllati)
					UtenteService.updateDtNotifica(usr, lastBanDate, 1);
				}
				logger.info("OK\n");
			}
			else
				logger.info("There is no new ban that needs to be matched\n");
			
			
			//Cerca fra tutte le notifiche in attesa e le invia per mail
			logger.info("Searching for pending notifications...");
			notifications = NotificaService.getAllPendingNotifications();
			
			if(notifications.size() > 0)
			{
				//legge tutta la tabella e processa le notifiche pendenti
				logger.info("\tSending mails...");
				Notifier.sendNotificationsMails(notifications);
				logger.info("OK\n");
			}
			else
				logger.info("There are no pending notifications\n");
			
			
			logger.info("Crawler has finished.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			JpaDao.close();
		}
		
		return;
	}
	
	private static void init()
	{		
		try
		{			
			//legge il file di configurazione e salva le configs in PropertiesManager
			logger.info("Loading configurations...");
			PropertiesManager.loadProperties();
			logger.info("OK\n");
			
			//carica il file di train e configura il validatore oggetti
			logger.info("Training WEKA BandoValidator...");
			BandoObjValidator.train();
			logger.info("OK\n");
			
			//inizializza repository JPA db (e anche tutti gli implementors)
			logger.info("Connecting to DATABASE...");
			JpaDao.init();
			logger.info("OK\n");
			
		}
		catch(JpaException ex)
		{
			logger.severe(ex.getMessage());
			System.exit(3);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
