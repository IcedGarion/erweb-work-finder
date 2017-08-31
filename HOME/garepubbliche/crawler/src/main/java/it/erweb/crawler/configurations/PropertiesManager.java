package it.erweb.crawler.configurations;

import java.io.FileInputStream;
import java.util.Properties;

/**
 *  A class for managing configurations
 */
public class PropertiesManager
{
	private static final String PROPERTIES_FILE = "src/main/java/resources/crawler.config";
	public static int SYS_HTTP_GET_FREQUENCY;				//intervallo tra una richiesta e l'altra
	public static String GAZZETTA_HOME_URL;					//home page gazzetta
	public static String PUBLICATIONS_HOME_URL;				//home page pubblicazioni
	public static String PUBLICATION_DOUBLE_NUMBER_PATTERN;	//pattern per trovare numero pubblicazione (a 2 cifre) (in home page pub)
	public static String PUBLICATION_TRIPLE_NUMBER_PATTERN;	//a 3 cifre
	public static String PUBLICATION_DETAIL_PATTERN;		//pattern per trovare url singole pubblicazioni
	public static String PUBLICATION_BAN_DIVID_PATTERN;		//div id del bando all'interno di una pubblicazione
	public static String BAN_CD_ESTERNO_PATTERN;			//pattern per trovare codice esterno bando, all'interno di pubblicazione
																//lol (?!(IS)) => negative lookahead: lol not followed by IS
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
	public static String BAN_OBJ_JUNK_HEAD;					//regex di parole che si possono togliere in testa all'oggetto del bando
	public static String BAN_OBJ_JUNK_BODY;					//regex di parole che si possono togliere dall'oggetto del bando
	public static char GAZZETTA_URL_TERMINATOR;				//terminatore degli url: (" oppure ')
	public static int PUBLICATIONS_URL_LENGTH;				//lunghezza massima url di una pubblicazione
	public static String EMAIL_SOURCE_ADDRESS;				//indirizzo di posta che invia le notifiche
	public static String EMAIL_SMTP_HOST;					//default smtp server
	public static String EMAIL_AUTH_USERNAME;				//username autenticazione con smtp
	public static String EMAIL_AUTH_PASSWORD;				//password autenticazione cin smtp
	public static String EMAIL_NOTIFICATIONBAN_SUBJECT;		//oggetto della mail
	public static String EMAIL_NOTIFICATIONBAN_HEAD;		//prime righe della mail
	public static String EMAIL_NOTIFICATIONBAN_TAIL;		//ultime righe della mail
	
	/**
	 * Loads all properties contained in the configuration file
	 */
	public static void loadProperties()
	{
		Properties prop = new Properties();
		
		//legge il file
		try(FileInputStream input = new FileInputStream(PROPERTIES_FILE))
		{
			prop.load(input);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//salva le properties lette
		SYS_HTTP_GET_FREQUENCY = Integer.parseInt(prop.getProperty("SYS_HTTP_GET_FREQUENCY"));
		GAZZETTA_HOME_URL = prop.getProperty("GAZZETTA_HOME_URL");
		PUBLICATIONS_HOME_URL = prop.getProperty("PUBLICATIONS_HOME_URL");
		PUBLICATION_BAN_DIVID_PATTERN = prop.getProperty("PUBLICATION_BAN_DIVID_PATTERN");
		PUBLICATION_DETAIL_PATTERN = prop.getProperty("PUBLICATION_DETAIL_PATTERN");
		PUBLICATION_DOUBLE_NUMBER_PATTERN = prop.getProperty("PUBLICATION_DOUBLE_NUMBER_PATTERN");
		PUBLICATION_TRIPLE_NUMBER_PATTERN = prop.getProperty("PUBLICATION_TRIPLE_NUMBER_PATTERN");
		BAN_CD_ESTERNO_PATTERN = prop.getProperty("BAN_CD_ESTERNO_PATTERN");
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
		GAZZETTA_URL_TERMINATOR = prop.getProperty("GAZZETTA_URL_TERMINATOR").charAt(0);
		PUBLICATIONS_URL_LENGTH = Integer.parseInt(prop.getProperty("PUBLICATIONS_URL_LENGTH"));
		EMAIL_SOURCE_ADDRESS = prop.getProperty("EMAIL_SOURCE_ADDRESS");
		EMAIL_SMTP_HOST = prop.getProperty("EMAIL_SMTP_HOST");
		EMAIL_AUTH_USERNAME= prop.getProperty("EMAIL_AUTH_USERNAME");
		EMAIL_AUTH_PASSWORD = prop.getProperty("EMAIL_AUTH_PASSWORD");
		EMAIL_NOTIFICATIONBAN_SUBJECT = prop.getProperty("EMAIL_NOTIFICATIONBAN_SUBJECT");
		EMAIL_NOTIFICATIONBAN_HEAD = prop.getProperty("EMAIL_NOTIFICATIONBAN_HEAD");
		EMAIL_NOTIFICATIONBAN_TAIL = prop.getProperty("EMAIL_NOTIFICATIONBAN_TAIL");
	}
}
