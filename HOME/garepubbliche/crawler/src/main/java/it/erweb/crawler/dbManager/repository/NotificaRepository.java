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
	public static void insertNotifica(Utente usr, Bando ban) throws JPAException
	{
		Notifica newNote;
		NotificaPK key;
		
		newNote = new Notifica();
		newNote.setDtNotifica(new Date());
		newNote.setStato("DA_INVIARE");
		//chiave primaria composta da utente + bando
		key = new NotificaPK();
		key.setCdBando(ban.getCdBando());
		key.setCdUtente(usr.getCdUtente());
		newNote.setId(key);
		
		create(newNote);
		
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

		notifies = read("SELECT n FROM Notifica n where n.stato = 'DA_INVIARE' ORDER BY n.id.cdUtente");
	
		return notifies;
	}

	/**
	 *  Updates a Notifica's state from "DA_INVIARE" to "INVIATO".
	 *  A Notifica's unique identifier is composed by both cdUtente and cdBando
	 * 
	 * @param cdUtente		Utente's identifier
	 * @param cdBando		Bando's identifier			
	 * @throws JPAException 
	 */
	public static void updateState(long cdUtente, long cdBando) throws JPAException
	{
		Notifica updateNote;
		NotificaPK key;
		
		updateNote = new Notifica();
		updateNote.setStato("INVIATO");
		updateNote.setDtNotifica(new Date());
		//chiave primaria composta da utente + bando
		key = new NotificaPK();
		key.setCdBando(cdBando);
		key.setCdUtente(cdUtente);
		updateNote.setId(key);
		
		update(updateNote);
		
		return;
	}
}
