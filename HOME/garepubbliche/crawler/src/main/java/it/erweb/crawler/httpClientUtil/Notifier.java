package it.erweb.crawler.httpClientUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import it.erweb.crawler.configurations.PropertiesManager;
import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.repository.BandoRepository;
import it.erweb.crawler.dbManager.repository.NotificaRepository;
import it.erweb.crawler.dbManager.repository.UtenteRepository;
import it.erweb.crawler.model.Bando;
import it.erweb.crawler.model.Notifica;
import it.erweb.crawler.model.Utente;

/**
 * Functions to send notifications via HTTP messages
 */
public class Notifier
{
	private static Logger logger = Logger.getLogger(Notifier.class.getName());
	
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
			//sendMail(developerMail, "problema", msg);
			//logger.info...
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
					cookMailAndUpdate(cdUtente, bansToNotify);
					
					exCdUtente = cdUtente;
					bansToNotify = new ArrayList<>();
				}
				//(se utente e' sempre lo stesso fa solo la add in fondo)
				
				bansToNotify.add(note.getId().getCdBando());
			}
			
			//infine invia i rimanenti bandi all'ultimo utente della lista
			cookMailAndUpdate(cdUtente, bansToNotify);
		}
		
		return;
	}
	
	/*
	 * Extracts the list of bans'cds and prepares the email: every user's got a list of bans to be sent
	 */
	private static void cookMailAndUpdate(long cdUtente, List<Long> bansCd) throws JPAException
	{
		String userEmailAddr, emailTxt, emailObj;
		Utente usr;
		Bando ban;
		boolean sent = false;
		
		//prepara la mail inserendo indirizzo destinatario e altri dati
		usr = UtenteRepository.getById(cdUtente);
		userEmailAddr = usr.getEmail();
		emailTxt = PropertiesManager.EMAIL_NOTIFICATIONBAN_HEAD + "\n";
		emailObj = PropertiesManager.EMAIL_NOTIFICATIONBAN_SUBJECT;
		
		//e, per ogni bando da inviare, aggiunge l'url alla mail
		for(long cd : bansCd)
		{
			ban = BandoRepository.getById(cd);
			emailTxt += ban.getUrl() + "\n";
		}
		emailTxt += PropertiesManager.EMAIL_NOTIFICATIONBAN_TAIL;
		
		//infine invia la mail
		sent = sendMail(userEmailAddr, emailObj, emailTxt);
		
		//se e' riuscito a inviare, aggiorna o stato di tutte le notifiche spedite a "INVIATO"
		if(sent)
		{
			for(long cd : bansCd)
			{
				NotificaRepository.updateState(cdUtente, cd);
			}
		}
		
		return;
	}
	
	/**
	 * Sends a new Mail to the specified destinatary, with the specified subject and email text
	 * 
	 * @param dest		Recipient's email address
	 * @param subject	subject of the current mail
	 * @param text		mail's body content
	 */
	public static boolean sendMail(String dest, String subject, String text)
	{
		String sourceAddr, smtpHost, sourceUser, sourcePass;
		Properties properties;
		Session session;
		boolean ret = false;
		
		//configure parameters
		sourceAddr = PropertiesManager.EMAIL_SOURCE_ADDRESS;
		smtpHost = PropertiesManager.EMAIL_SMTP_HOST;
		sourceUser = PropertiesManager.EMAIL_AUTH_USERNAME;
		sourcePass = PropertiesManager.EMAIL_AUTH_PASSWORD;		
		
		//Session properties
		properties = new Properties();
		properties.setProperty("mail.smtp.host", smtpHost);
		properties.setProperty("mail.smtp.socketFactory.port", "465");
		properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.port", "465");

		//set up authentication
		session = Session.getDefaultInstance(properties, new javax.mail.Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(sourceUser, sourcePass);
			}
		});

		//configure message and send
		try
		{
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sourceAddr));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(dest));
			message.setSubject(subject);
			message.setText(text);

			Transport.send(message);
			logger.info("Mail sent:\nDestination: " + dest + "\nSubject: " + subject + "Text: " + text);
			ret = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ret;
	}
}
