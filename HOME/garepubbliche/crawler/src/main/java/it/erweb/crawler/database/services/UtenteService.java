package it.erweb.crawler.database.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.erweb.crawler.database.model.Utente;
import it.erweb.crawler.database.repository.JpaDao;
import it.erweb.crawler.database.repository.JpaException;

/**
 *	Contains the methods for manipulating a Utente object in the database
 */
public class UtenteService extends JpaDao<Utente>
{
	/**
	 * 	Returns a list of all users stored in the database
	 * 
	 * @return	a list of users
	 * @throws JpaException
	 */
	public static List<Utente> getAllUsers() throws JpaException
	{
		List<Utente> result;
		
		result = read("SELECT u FROM Utente u");

		return result;
	}

	/**
	 * Updates the current user's last notification date
	 * 
	 * @param usr		the user to be updated
	 * @param bandoDate new date to insert
	 * @param secOffset offset (seconds) to be added to the new date
	 * @throws JpaException 
	 */
	public static void updateDtNotifica(Utente usr, Date bandoDate, int secOffset) throws JpaException
	{
		Date exNotifica = usr.getDtNotifica(), newDate = bandoDate;
		
		//aggiunge offset alla data
		Calendar cal = Calendar.getInstance();
		cal.setTime(newDate);
        cal.add(Calendar.SECOND, secOffset);
        newDate = cal.getTime();
		
		//aggiorna data notifica solo se piu' recente
		if(newDate.after(exNotifica))
		{
			usr.setDtNotifica(newDate);
			update(usr);
		}
	}

	/**
	 *  Returns the entity identified by the ID 
	 * 
	 * @param cdUtente	ID of the entity
	 * @return			Utente odentified by ID
	 * @throws JpaException 
	 */
	public static Utente getById(long cdUtente) throws JpaException
	{
		List<Utente> result;
		
		result = read("SELECT u FROM Utente u WHERE u.cdUtente = " + cdUtente);

		return result.get(0);
	}
}
