package it.erweb.crawler.dbManager.repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.JPAManager;
import it.erweb.crawler.model.Utente;

/**
 *	Contains the methods for manipulating a Utente object in the database
 */
public class UtenteRepository extends JPAManager<Utente>
{
	/**
	 * 	Returns a list of all users stored in the database
	 * 
	 * @return	a list of users
	 * @throws JPAException
	 */
	public static List<Utente> getAllUsers() throws JPAException
	{
		List<Utente> result;
		
		try
		{
			result = JPAManager.read("SELECT u FROM Utente u");
		}
		catch(Exception e)
		{
			throw new JPAException("Error:\n" + e.getMessage());
		}
		
		return result;
	}

	/**
	 * Updates the current user's last notification date
	 * 
	 * @param usr		the user to be updated
	 * @param bandoDate new date to insert
	 * @param secOffset offset (seconds) to be added to the new date
	 * @throws JPAException 
	 */
	public static void updateDtNotifica(Utente usr, Date bandoDate, int secOffset) throws JPAException
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
			JPAManager.update(usr);
		}
	}
}
