package it.erweb.crawler.httpClientUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
					cookMail(cdUtente, bansToNotify);
					
					exCdUtente = cdUtente;
					bansToNotify = new ArrayList<>();
				}
				//(se utente e' sempre lo stesso fa solo la add in fondo)
				
				bansToNotify.add(note.getId().getCdBando());
			}
			
			//infine invia i rimanenti bandi all'ultimo utente della lista
			cookMail(cdUtente, bansToNotify);
		}
		
		return;
	}
	
	/*
	 * Extracts the list of bans'cds and prepares the email: every user's got a list of bans to be sent
	 */
	private static void cookMail(long cdUtente, List<Long> bansCd) throws JPAException
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
	public static void sendMail(String dest, String text)
	{
		String from;
		Properties properties;
		Session session;
		
		//configures parameters
		from = "insertUser@insertDomain.com";
		
		//System properties
		properties = new Properties();
		properties.setProperty("mail.smtp.host", "smtp.gmail.com");
		properties.setProperty("mail.smtp.socketFactory.port", "465");
		properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.port", "465");

		session = Session.getDefaultInstance(properties,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("usrname (from)","password (from)");
				}
			});
		
		//Default Session object
		//session = Session.getDefaultInstance(properties);

		try
		{
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(dest));
			message.setSubject("ERWEB Garepubbliche - Nuovi bandi");
			message.setText(text);

			Transport.send(message);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return;
	}
}
