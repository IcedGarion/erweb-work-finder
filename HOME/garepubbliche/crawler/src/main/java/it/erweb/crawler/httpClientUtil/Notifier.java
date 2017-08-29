package it.erweb.crawler.httpClientUtil;

import java.util.ArrayList;
import java.util.List;

import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.repository.BandoRepository;
import it.erweb.crawler.dbManager.repository.UtenteRepository;
import it.erweb.crawler.model.Bando;
import it.erweb.crawler.model.Notifica;
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
			//sendMail(developerMail, msg);
		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 *  For every user in the list (who needs to be notified), creates a list of corresponding bans to notify
	 *  	and sends a mail with these bans for the specific user
	 * 
	 * @param notifications		A list of notifications (user + ban) to be sent
	 * @throws JPAException 
	 */
	public static void sendNotificationsMails(List<Notifica> notifications) throws JPAException
	{
		//parses the list in order to split the users
		List<Long> bansToNotify = null;
		long exCdUtente = 0, cdUtente = -1;
		
		//BASATO SULL'ORDERBY
		//continua ad aggiungere bandi alla lista finche' non cambia utente:
		//quando succede, invia tutti i bandi letti e ricomincia
		if(notifications.size() > 0)
		{
			for(Notifica note : notifications)
			{
				//salva cdUtente per prendere poi email
				cdUtente = note.getId().getCdUtente();
				
				//prima iterazione: crea lista
				if(exCdUtente == 0)
				{
					cdUtente = exCdUtente = note.getId().getCdUtente();
					bansToNotify = new ArrayList<>();
				}
				//cambio utente: invia + ricrea nuova lista
				else if(cdUtente != exCdUtente)
				{
					//invia per mail quanto letto finora: a cdUtente invia tutta la lista
					sendBansMail(cdUtente, bansToNotify);
					
					exCdUtente = cdUtente;
					bansToNotify = new ArrayList<>();
				}
				//(se utente e' sempre lo stesso fa solo la add in fondo)
				
				bansToNotify.add(note.getId().getCdBando());
			}
			
			//infine invia i rimanenti bandi all'ultimo utente della lista
			sendBansMail(cdUtente, bansToNotify);
		}
		
		return;
	}
	
	/*
	 * Extracts the list of bans'cd and prepares the email: every user's got a list of bans to be sent
	 */
	private static void sendBansMail(long cdUtente, List<Long> bansCd) throws JPAException
	{
		String email, emailTxt = "Nuovi Bandi!\n";
		Utente usr;
		Bando ban;
		
		//prepara la mail inserendo indirizzo destinatario,
		usr = UtenteRepository.getById(cdUtente);
		email = usr.getEmail();
		
		//e, per ogni bando da inviare, aggiunge l'url alla mail
		for(long cd : bansCd)
		{
			ban = BandoRepository.getById(cd);
			emailTxt += ban.getUrl() + "\n";
		}
		
		//infine invia la mail
		sendMail(email, emailTxt);
		
		return;
	}
	
	/*
	 * Sends a mail to dest with the body text
	 */
	private static void sendMail(String dest, String text)
	{
		//invia mail
		
		return;
	}
}
