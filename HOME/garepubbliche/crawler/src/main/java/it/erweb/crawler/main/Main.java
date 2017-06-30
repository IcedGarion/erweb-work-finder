package it.erweb.crawler.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import it.erweb.crawler.configurations.PropertiesManager;
import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.repository.ExpregRepository;
import it.erweb.crawler.dbManager.repository.UtenteRepository;
import it.erweb.crawler.httpClientUtil.HttpGetter;
import it.erweb.crawler.model.*;
import it.erweb.crawler.parser.HtmlParser;

public class Main
{
	private static Logger logger = Logger.getLogger(Main.class.getName());
	
	public static void main(String[] args) throws JPAException
	{
		String html = "", pubURL = "";
		int i = 0;
		ArrayList<Pubblicazione> publications = new ArrayList<Pubblicazione>();
		ArrayList<String> publicationsHtml = new ArrayList<String>();
		ArrayList<Bando> Bans = new ArrayList<Bando>();

		
		//inizializza configurazioni
		logger.info("Starting Crawler...");
		init();
		logger.info("OK\n");
		
		try
		{
			//prende homepage
			logger.info("Connecting to " + PropertiesManager.GAZZETTA_HOME_URL + "...");
			html = HttpGetter.get(PropertiesManager.GAZZETTA_HOME_URL);
			logger.info("OK\n");
			
			//ricava url della pagina delle pubblicazioni (5a sezione) dalla home
			logger.info("Searching for publications page...");
			pubURL += HtmlParser.getHomePublicationsURL(html);
			logger.info("OK\n");
			
			//si connette alla pagina delle pubblicazioni
			logger.info("Connecting to publications page: " + pubURL + " ...");
			html = HttpGetter.get(pubURL);
			logger.info("OK\n");
			
			//ricava gli url di tutte le pubblicazioni disponibili
			logger.info("Searching for publications urls...");
			publications = HtmlParser.getPublications(html);
			logger.info("OK\n");
			
			//scarica tutte le pubblicazioni
			logger.info("Downloading all publications...");
			for(Pubblicazione pub : publications)
			{
				logger.info("Connecting to: " + pub.getUrl() + " ...");
				publicationsHtml.add(HttpGetter.get(pub.getUrl()));
			}
			logger.info("OK\n");
			
			//ora inizia il vero parsing: si devono ricavare informazioni sul bando
			logger.info("Parsing all publications...\n");
			for(String pub : publicationsHtml)
			{
				logger.info("Parsing publication n. " + (++i) + " ...");
				//deve in qualche modo passre pub CD_PUBBLICAZIONE, per sapere di chi sono i bandi
				Bans.addAll(HtmlParser.getPublicationBans(pub));				
			}
			logger.info("OK\n");
			
			logger.info("Parsing all bans...\n");
			i = 0;
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
		
		return;
	}
	
	private static void init()
	{		
		try
		{
			//legge il file di configurazione e salva le configs in PropertiesManager
			PropertiesManager.loadProperties();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
