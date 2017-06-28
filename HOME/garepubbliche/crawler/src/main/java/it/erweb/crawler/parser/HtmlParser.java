package it.erweb.crawler.parser;

import java.util.ArrayList;

import it.erweb.crawler.configurations.PropertiesManager;

/**
 * Searches html for URLs or specific info 
 */
public class HtmlParser
{
	
	/**
	 * Gets the publications home page, given the gazzetta home page
	 * 
	 * @param html	gazzetta home page html
	 * @return		a String representing the publications URL
	 */
	public static String getHomePublicationsURL(String html)
	{
		String ret = "";
		int startIndex;
		char current;
		
		startIndex = html.indexOf(PropertiesManager.HOME_PUBLICATIONS_PATTERN);
		current = html.charAt(startIndex);
		while(current != '\"')
		{
			ret += current;
			current = html.charAt(++startIndex);
		}
		
		return PropertiesManager.GAZZETTA_HOME_URL + ret;
	}
	
	/**
	 * Gets all the publications URLs, given the publications home page
	 * 
	 * @param html	publications page
	 * @return		A list of publications URLs
	 */
	public static ArrayList<String> getPublicationsURL(String html)
	{
		ArrayList<String> ret = new ArrayList<String>();
		int startIndex = 0, offset = 0;
		char current;
		String url = "";
		
		startIndex = html.indexOf(PropertiesManager.PUBLICATION_BAN_PATTERN, offset);
		while(startIndex != -1)
		{
			current = html.charAt(startIndex);
			while(current != '\"')
			{
				url += current;
				current = html.charAt(++startIndex);
			}
			
			ret.add(PropertiesManager.GAZZETTA_HOME_URL + url);
			offset = startIndex;
			startIndex = html.indexOf(PropertiesManager.PUBLICATION_BAN_PATTERN, offset);
			url = "";
		}
		
		return ret;
	}
}
