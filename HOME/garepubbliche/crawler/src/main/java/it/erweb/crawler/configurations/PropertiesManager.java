package it.erweb.crawler.configurations;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Contains all configuration properties, stored in crawler.config
 */
public class PropertiesManager
{
	private static final String PROPERTIES_FILE = "src/main/java/resources/crawler.config";
	public static int SYS_HTTP_GET_FREQUENCY;				//intervallo tra una richiesta e l'altra
	public static int SYS_BAN_PARSING_TRIALS_TIMEOUT;		//max numero di tentativi parsing bando
	public static String GAZZETTA_HOME_URL;					//home page gazzetta	(dismesso)
	public static String PUBLICATIONS_HOME_URL;				//home page pubblicazioni
	public static String PUBLICATIONS_HOME_PATTERN;			//pattern per trovare home page pubblicazioni (dismesso)
	public static String PUBLICATION_NUMBER_PATTERN;		//pattern per trovare numero pubblicazione (in home page pub)
	public static String PUBLICATION_DETAIL_PATTERN;		//pattern per trovare url singole pubblicazioni
	public static String PUBLICATION_BAN_DIVID_PATTERN;		//div id del bando all'interno di una pubblicazione
	public static String BAN_CD_ESTERNO_PATTERN;			//pattern per trovare codice esterno bando, all'interno di pubblicazi
	public static int BAN_CD_ESTERNO_LENGTH;				//lunghezza pattern
	public static String BAN_CIG_PATTERN;					//pattern per trovare cig all'interno di pubblicazione
	public static int BAN_CIG_LENGTH;						//lunghezza pattern
	public static String BAN_CIG_REGEX;						//regex per matchare cig
	public static String BAN_DIVCLASS;						//div class del testo del bando, all'interno di bando
	public static String VALIDATOR_TRAIN_PATH;				//path del file di training del validator weka
	public static String VALIDATOR_TEST_PATH;				//path del file di test del validator weka
	public static String[] BAN_OBJ_PATTERNS;				//patterns per trovare oggetto bando, all'interno di bando
	public static int BAN_OBJ_MAX_CHARS;					//stima massima totale caratteri di un oggetto di un bando
	public static int BAN_OBJ_MIN_CHARS;					//stima minima totale caratteri di un oggetto di un bando
	public static int BAN_OBJ_MAX_TITLE_CHARS;				//stima massima del totale caratteri del titolo di un bando
	public static int BAN_OBJ_MIN_TITLE_CHARS;				//stima minima del totale caratteri del titolo di un bando
	public static int BAN_OBJ_PADDING_LINES;				//stima linee lette in eccesso per trovare ogg, che si possono elimin
	public static String BAN_OBJ_JUNK_HEAD;						//regex di parole che si possono togliere dall'oggetto del bando
	public static String BAN_OBJ_JUNK_BODY;
	
	/**
	 * Loads all properties of configuration file in variables
	 */
	public static void loadProperties()
	{
		FileInputStream input = null;
		Properties prop = new Properties();
		
		//legge il file
		try
		{
			input = new FileInputStream(PROPERTIES_FILE);
			prop.load(input);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(input != null)
			{
				try
				{
					input.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		//salva le properties lette
		SYS_HTTP_GET_FREQUENCY = Integer.parseInt(prop.getProperty("SYS_HTTP_GET_FREQUENCY"));
		SYS_BAN_PARSING_TRIALS_TIMEOUT = Integer.parseInt(prop.getProperty("SYS_BAN_PARSING_TRIALS_TIMEOUT"));
		GAZZETTA_HOME_URL = prop.getProperty("GAZZETTA_HOME_URL");
		PUBLICATIONS_HOME_PATTERN = prop.getProperty("PUBLICATIONS_HOME_PATTERN");
		PUBLICATIONS_HOME_URL = prop.getProperty("PUBLICATIONS_HOME_URL");
		PUBLICATION_BAN_DIVID_PATTERN = prop.getProperty("PUBLICATION_BAN_DIVID_PATTERN");
		PUBLICATION_DETAIL_PATTERN = prop.getProperty("PUBLICATION_DETAIL_PATTERN");
		PUBLICATION_NUMBER_PATTERN = prop.getProperty("PUBLICATION_NUMBER_PATTERN");
		BAN_CD_ESTERNO_PATTERN = prop.getProperty("BAN_CD_ESTERNO_PATTERN");
		BAN_CD_ESTERNO_LENGTH = Integer.parseInt(prop.getProperty("BAN_CD_ESTERNO_LENGTH"));
		BAN_CIG_PATTERN = prop.getProperty("BAN_CIG_PATTERN");
		BAN_CIG_LENGTH = Integer.parseInt(prop.getProperty("BAN_CIG_LENGTH"));
		BAN_CIG_REGEX = prop.getProperty("BAN_CIG_REGEX");
		BAN_DIVCLASS = prop.getProperty("BAN_DIVCLASS");
		VALIDATOR_TRAIN_PATH = prop.getProperty("VALIDATOR_TRAIN_PATH");
		VALIDATOR_TEST_PATH = prop.getProperty("VALIDATOR_TEST_PATH");
		BAN_OBJ_PATTERNS = prop.getProperty("BAN_OBJ_PATTERN").split(",");
		BAN_OBJ_MAX_CHARS = Integer.parseInt(prop.getProperty("BAN_OBJ_MAX_CHARS"));
		BAN_OBJ_MIN_CHARS = Integer.parseInt(prop.getProperty("BAN_OBJ_MIN_CHARS"));
		BAN_OBJ_MAX_TITLE_CHARS = Integer.parseInt(prop.getProperty("BAN_OBJ_MAX_TITLE_CHARS"));
		BAN_OBJ_MIN_TITLE_CHARS = Integer.parseInt(prop.getProperty("BAN_OBJ_MIN_TITLE_CHARS"));
		BAN_OBJ_PADDING_LINES = Integer.parseInt(prop.getProperty("BAN_OBJ_PADDING_LINES"));
		BAN_OBJ_JUNK_HEAD = prop.getProperty("BAN_OBJ_JUNK_HEAD");
		BAN_OBJ_JUNK_BODY = prop.getProperty("BAN_OBJ_JUNK_BODY");
	}
	
	public static void setProperty(String name, String value)
	{
		FileOutputStream output = null;
		Properties prop = new Properties();

		try
		{
			output = new FileOutputStream(PROPERTIES_FILE);
			prop.setProperty(name, value);
			prop.store(output, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(output != null)
			{
				try
				{
					output.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}		
	}
}
