package it.erweb.crawler.configurations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

public class PropertiesManager
{
	private static FileInputStream input;
	private static FileOutputStream output;
	private static Properties prop;
	private static final String PROPERTIES_FILE = "src/main/java/resources/crawler.config";
	
	public static Properties loadProperties()
	{
		prop = new Properties();
		
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
		
		return prop;
	}
	
	public static void setProperty(String name, String value)
	{
		prop = new Properties();

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
