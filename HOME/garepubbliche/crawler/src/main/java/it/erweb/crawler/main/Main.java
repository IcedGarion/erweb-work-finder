package it.erweb.crawler.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import java.util.logging.Logger;
import it.erweb.crawler.configurations.PropertiesManager;
import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.httpClientUtil.HttpGetter;
import it.erweb.crawler.model.*;
import it.erweb.crawler.parser.HtmlParser;
import it.erweb.crawler.weka.BandoValidator;

public class Main
{
	private static Logger logger = Logger.getLogger(Main.class.getName());


	public static void main(String[] args) throws JPAException, FileNotFoundException
	{
		String html = "", pubURL = "";
		int i = 0;
		ArrayList<Pubblicazione> publications = new ArrayList<Pubblicazione>();
		ArrayList<String> publicationsHtml = new ArrayList<String>();
		ArrayList<Bando> Bans = new ArrayList<Bando>();
		PrintWriter r = new PrintWriter(new File("testoBandi"));
		
		//inizializza configurazioni
		logger.info("Starting Crawler...");
		init();
		logger.info("OK\n");
		
		try
		{
			/*
			//prende homepage
			logger.info("Connecting to " + PropertiesManager.GAZZETTA_HOME_URL + "...");
			html = HttpGetter.get(PropertiesManager.GAZZETTA_HOME_URL);
			logger.info("OK\n");
			
			//ricava url della pagina delle pubblicazioni (5a sezione) dalla home
			logger.info("Searching for publications page...");
			pubURL += HtmlParser.getHomePublicationsURL(html);
			logger.info("OK\n");
			*/
			
			//si connette alla pagina delle pubblicazioni
			logger.info("Connecting to publications page: " + pubURL + " ...");
			html = HttpGetter.get(PropertiesManager.PUBLICATIONS_HOME_URL);
			logger.info("OK\n");
			
			//ricava le prime informazioni sulle pubblicazioni disponibili e le salva nel DB
			logger.info("Searching for publications...");
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
			
			//scorre le pubblicazioni e ricava i bandi, e li inserisce nel DB
			logger.info("Parsing all publications...\n");
			for(String pub : publicationsHtml)
			{
				logger.info("Parsing publication n. " + (i + 1) + " ...");
				//passa anche la Pubblicaziome, per collegare i bandi alla relativa pubblicazione
				Bans.addAll(HtmlParser.getPublicationBans(pub, publications.get(i)));	
				i++;
			}
			logger.info("OK\n");
			
			i=0;
			//scarica tutti i bandi salvati
			logger.info("Downloading all bans...\n");
			for(Bando ban : Bans)
			{
				logger.info("Connecting to: " + ban.getUrl() + " ...");
				html = HttpGetter.get(ban.getUrl());
	
				//INIZIALMENTE SALVA TUTTO HTML COME TESTO DEL BANDO
				ban.setTesto(html);	
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				if((i++) >= 200)
					break;
				
				
				
				
				
				
				
				
				
				
			}
			logger.info("OK\n");
			
	
			i = 0;
			logger.info("Parsing all bans...\n");
			for(Bando ban : Bans)
			{				
				logger.info("Parsing ban n. " + (++i) + "...\n");
				HtmlParser.parseBan(ban);
				
				
				
				
				
				if(i >= 200)
					break;
		
				
				
				//SCRIVE SU FILE IL TESTO DEI BANDI (DEBUG)
				

					r.write(i + ": " + ban.getOggetto() + "\n");
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
			
			//carica il file e fa il train del validatore oggetti
			BandoValidator.train();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
