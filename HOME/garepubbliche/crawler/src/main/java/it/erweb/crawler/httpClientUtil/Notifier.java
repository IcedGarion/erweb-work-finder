package it.erweb.crawler.httpClientUtil;

import java.util.ArrayList;
import java.util.List;

import it.erweb.crawler.model.Bando;
import it.erweb.crawler.model.Notifica;

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
	
	/**
	 *  For every user in the list, send a mail containing all the user's bans to be notified
	 * 
	 * @param notifications		A list of notifications to be sent
	 */
	public static void sendNotificationsMails(List<Notifica> notifications)
	{
		//parses the list in order to split the users
		List<Bando> bansToNotify;
		long cdUtente = 0;
		
		//BASATO SULL'ORDERBY
		//continua ad aggiungere bandi alla lista finche' non cambia utente:
		//quando succede, invia tutti i bandi letti ei ricomincia
		for(Notifica note : notifications)
		{
			//prima volta
			if(cdUtente == 0)
			{
				cdUtente = note.getId().getCdUtente();
				bansToNotify = new ArrayList<>();
			}
			//utente e' sempre lo stesso
			else if(note.getId().getCdUtente() == cdUtente)
			{
				
			}
			//cambio utente
			else
			{
				//invia per mail quanto letto finora
				
				bansToNotify = new ArrayList<>();
			}
		}
		
		return;
	}
}
