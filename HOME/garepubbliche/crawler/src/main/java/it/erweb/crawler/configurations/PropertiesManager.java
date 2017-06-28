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
	public static String HOME_PUBLICATIONS_PATTERN;
	public static String PUBLICATION_BAN_PATTERN;
	
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
		HOME_PUBLICATIONS_PATTERN = prop.getProperty("HOME_PUBLICATIONS_PATTERN");
		PUBLICATION_BAN_PATTERN = prop.getProperty("PUBLICATION_BAN_PATTERN");
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
