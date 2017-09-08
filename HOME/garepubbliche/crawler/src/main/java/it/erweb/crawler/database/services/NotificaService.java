package it.erweb.crawler.database.services;

import java.util.Date;
import java.util.List;

import it.erweb.crawler.database.model.Bando;
import it.erweb.crawler.database.model.Notifica;
import it.erweb.crawler.database.model.NotificaPK;
import it.erweb.crawler.database.model.Utente;
import it.erweb.crawler.database.repository.JpaDao;
import it.erweb.crawler.database.repository.JpaException;

public class NotificaService extends JpaDao<Notifica>
{
	/**
	 * 	Adds a new notification for a specific user and a specific ban
	 * 
	 * @param usr		the user to be notified
	 * @param ban		the ban to notify
	 * @throws JpaException
	 */
	public static void insertNotifica(Utente usr, Bando ban) throws JpaException
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
	 * @throws JpaException
	 */
	public static List<Notifica> getAllPendingNotifications() throws JpaException
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
	 * @throws JpaException 
	 */
	public static void updateState(long cdUtente, long cdBando) throws JpaException
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
