package it.erweb.crawler.dbManager.repository;

import java.util.Date;
import java.util.List;

import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.JPAManager;
import it.erweb.crawler.model.Bando;
import it.erweb.crawler.model.Notifica;
import it.erweb.crawler.model.NotificaPK;
import it.erweb.crawler.model.Utente;

public class NotificaRepository extends JPAManager<Notifica>
{
	/**
	 * 	Adds a new notification for a specific user and a specific ban
	 * 
	 * @param usr		the user to be notified
	 * @param ban		the ban to notify
	 * @throws JPAException
	 */
	public static void insertNotify(Utente usr, Bando ban) throws JPAException
	{
		Notifica notify;
		NotificaPK key;
		
		notify = new Notifica();
		key = new NotificaPK();
		key.setCdBando(ban.getCdBando());
		key.setCdUtente(usr.getCdUtente());
		notify.setId(key);
		notify.setDtNotifica(new Date());
		notify.setStato("DA_INVIARE");
		JPAManager.create(notify);
		
		return;
	}
	
	/**
	 *  Displays all pending notifications that wait to be sent
	 * 
	 * @return	A list of notifications
	 * @throws JPAException
	 */
	public static List<Notifica> getAllPendingNotifications() throws JPAException
	{
		List<Notifica> notifies;

		notifies = JPAManager.read("SELECT n FROM Notifica n where n.stato = 'DA_INVIARE' ORDER BY n.id.cdUtente");
	
		return notifies;
	}
}
