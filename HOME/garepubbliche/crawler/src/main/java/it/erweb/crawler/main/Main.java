package it.erweb.crawler.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import it.erweb.crawler.configurations.PropertiesManager;
import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.repository.BandoRepository;
import it.erweb.crawler.dbManager.repository.PubblicazioneRepository;
import it.erweb.crawler.httpClientUtil.HttpGetter;
import it.erweb.crawler.model.*;
import it.erweb.crawler.parser.HtmlParser;
import it.erweb.crawler.weka.BandoObjValidator;

public class Main
{
	private static Logger logger = Logger.getLogger(Main.class.getName());
	
	public static void main(String[] args) throws JPAException, FileNotFoundException
	{
		PubblicazioneRepository pubRepo = new PubblicazioneRepository();
		BandoRepository banRepo = new BandoRepository();
		String html = "", pubURL = "";
		int i = 0;
		List<Pubblicazione> publications = new ArrayList<Pubblicazione>();
		List<String> publicationsHtml = new ArrayList<String>();
		List<Bando> Bans = new ArrayList<Bando>();
		PrintWriter r = new PrintWriter(new File("testoBandi"));
		boolean newAvailable;
		
		//inizializza configurazioni
		logger.info("Starting Crawler...");
		init();
		logger.info("OK\n");
		
		try
		{
/*
			//si connette alla pagina delle pubblicazioni
			logger.info("Connecting to publications page: " + pubURL + " ...");
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
			publications = pubRepo.getAllDaScaricare();
			logger.info("OK\n");

			//per ogni pubblicazione "DA_SCARICARE", ricava URL e scarica (aggiungendo gli html a una lista)
			logger.info("Downloading all publications...");
			for(Pubblicazione pub : publications)
			{
				logger.info("Connecting to: " + pub.getUrl() + " ...");
				Thread.sleep(PropertiesManager.SYS_HTTP_GET_FREQUENCY);
				publicationsHtml.add(HttpGetter.get(pub.getUrl()));
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
			Bans = banRepo.getAllDaParsificare();
			logger.info("OK\n");

			//scarica tutti i bandi salvati e aggiorna il DB
			logger.info("Downloading all bans...\n");
			for(Bando ban : Bans)
			{
				logger.info("Connecting to: " + ban.getUrl() + " ...");
				Thread.sleep(PropertiesManager.SYS_HTTP_GET_FREQUENCY);
				html = HttpGetter.get(ban.getUrl());
	
				//INIZIALMENTE SALVA TUTTO HTML COME TESTO DEL BANDO
				
				banRepo.updateText(ban, html);	
			}
			logger.info("OK\n");
			
			i = 0;
			logger.info("Parsing all bans...\n");
			for(Bando ban : Bans)
			{				
				logger.info("Parsing ban n. " + (++i) + "...\n");
				HtmlParser.parseBan(ban);
				
				//SCRIVE SU FILE IL TESTO DEI BANDI (DEBUG)
				
					
					r.write(i + (i>=10?"":" ") + ": CIG : " + (ban.getCig()==null?"nil\t\t\t":ban.getCig()+"\t" ) + "- " + ban.getOggetto() + "\n");
					r.flush();
				
			
				
				
			}
			logger.info("OK\n");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			r.close();
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
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
