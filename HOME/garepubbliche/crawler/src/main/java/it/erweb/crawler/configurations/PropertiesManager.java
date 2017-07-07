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
	public static String GAZZETTA_HOME_URL;
	public static String PUBLICATIONS_HOME_URL;
	public static String PUBLICATIONS_HOME_PATTERN;
	public static String PUBLICATION_DETAIL_PATTERN;
	public static String PUBLICATION_BAN_DIVID_PATTERN;
	public static String PUBLICATION_NUMBER_PATTERN;
	public static String BAN_CD_ESTERNO_PATTERN;
	public static int BAN_CD_ESTERNO_LENGTH;
	public static String BAN_CIG_PATTERN;
	public static int BAN_CIG_LENGTH;
	public static String BAN_DIVCLASS;
	public static String VALIDATOR_TRAIN_PATH;
	public static String VALIDATOR_TEST_PATH;

	
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
		BAN_DIVCLASS = prop.getProperty("BAN_DIVCLASS");
		VALIDATOR_TRAIN_PATH = prop.getProperty("VALIDATOR_TRAIN_PATH");
		VALIDATOR_TEST_PATH = prop.getProperty("VALIDATOR_TEST_PATH");
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
