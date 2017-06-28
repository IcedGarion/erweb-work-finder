package it.erweb.crawler.parser;

import java.util.ArrayList;

import it.erweb.crawler.configurations.PropertiesManager;

public class HtmlParser
{
	
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
	
	public static ArrayList<String> getPublicationsURL(String html)
	{
		ArrayList<String> ret = new ArrayList<String>();
		int startIndex = 0;
		char current;
		String url = "";
		
		while(startIndex != -1)
		{
			startIndex = html.indexOf(PropertiesManager.PUBLICATION_BAN_PATTERN);
			current = html.charAt(startIndex);
			while(current != '\"')
			{
				url += current;
				current = html.charAt(++startIndex);
			}
			
			ret.add(url);
		}
		
		return ret;
	}
}
