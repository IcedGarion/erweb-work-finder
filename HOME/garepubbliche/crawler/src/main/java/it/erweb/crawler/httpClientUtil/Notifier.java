package it.erweb.crawler.httpClientUtil;

import it.erweb.crawler.model.Bando;
import it.erweb.crawler.model.Utente;

/**
 * Functions to send notifications via HTTP messages
 */
public class Notifier
{
	/**
	 * Notifies the developers something has gone wrong
	 * 
	 * @param msg	the message to send (e.g. an exception message)
	 * @throws Exception 
	 */
	public static void notifyDev(String msg)
	{
		try
		{
			throw new Exception("Not yet implemented");
		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void notifyUser(Utente usr, Bando ban) throws Exception
	{
		System.out.println("\nUtente: " + usr.getUsername() + " match con oggetto bando:\n"
				+ ban.getOggetto()==null|ban.getOggetto()==""?ban.getTesto():ban.getOggetto());
	}
}
