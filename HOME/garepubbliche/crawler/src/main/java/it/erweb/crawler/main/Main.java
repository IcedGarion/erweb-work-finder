package it.erweb.crawler.main;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Logger;

import it.erweb.crawler.configurations.PropertiesManager;
import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.JPAManager;
import it.erweb.crawler.dbManager.repository.TmpTgazattoRepo;
import it.erweb.crawler.model.*;
import it.erweb.crawler.parser.StringParser;
import it.erweb.crawler.weka.BandoObjValidator;

/**
 * Crawler's main class
 */
public class Main
{
	private static Logger logger = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) throws JPAException, FileNotFoundException
	{
		List<Object> list;
		int i = 0, length, k = 0, patternLength;
		long tot, origin, weka;
		Tgazatto t;
		String oggetto = "";
		String query = "select ( select count(*) from Tgazatto) as tot, "
				+ "( select count(oggetto) from Tgazatto WHERE oggetto != '' ) as originale, "
				+ "( select count(oggettoWeka) as weka from Tgazatto WHERE oggettoWeka != '' ) as weka";
		
		//inizializza configurazioni
		logger.info("Starting Crawler...");
		init();
		logger.info("OK\n");
		
		try
		{
			/*
			//pulisce il database per una nuova prova
			JPAManager.update("update Tgazatto set oggettoWeka = ''");
			
			//recupera tutti i bandi dal db
			list = JPAManager.read("Select p from Tgazatto p");
			length = list.size();
			patternLength = PropertiesManager.BAN_OBJ_PATTERNS.length;

			for(Object o : list)
			{
				oggetto = "";
				System.out.println("Parsing tgazatto " + k++ + " of " + length);
				t = (Tgazatto) o;

				//per ogni bando, cerca oggetto dal testo (con weka) (Fa la stessa cosa di HtmlParser)
				while((oggetto.equals("")) && (i < patternLength))
				{
					oggetto = StringParser.tryGetObject(t.getContent(), PropertiesManager.BAN_OBJ_PATTERNS[i]);
					i++;
				}
				if(oggetto.equals(""))
				{
					oggetto = StringParser.tryGetObjectTitle(t.getContent());
				}
				
				if(! oggetto.equals(""))
				{
					TmpTgazattoRepo.updateWeka(t, oggetto);
				}
				
				System.out.println("Parsed");
			}
			
		System.out.println("End Parsing");
		*/
		//query per numero di oggetti nulli
		tot = Long.parseLong(JPAManager.read("select count(t) from Tgazatto t").get(0).toString());
		origin = Long.parseLong(JPAManager.read("select count(oggetto) from Tgazatto t where oggetto != ''").get(0).toString());
		weka = Long.parseLong(JPAManager.read("select count(oggettoWeka) from Tgazatto t where oggettoWeka != ''").get(0).toString());
		System.out.println("Weka results:\nTotale\t\t\tOggetti non nulli originali\t\t\tOggetti non nulli con Weka\n"
				+ tot + "\t\t\t" + origin + "\t\t\t\t\t\t" + weka + "\n");
			
			
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
		catch(JPAException ex)
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
