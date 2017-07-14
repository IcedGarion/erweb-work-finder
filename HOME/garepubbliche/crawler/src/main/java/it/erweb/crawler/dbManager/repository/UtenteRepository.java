package it.erweb.crawler.dbManager.repository;

import java.util.Date;
import java.util.List;

import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.JPAManager;
import it.erweb.crawler.model.Utente;

/**
 *	Contains the methods for manipulating a Utente object in the database
 */
public class UtenteRepository extends JPAManager
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
			result = entityManager.createQuery("SELECT u FROM Utente u").getResultList();
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
	 * @param usr	the user to be updated
	 */
	public static void updateDtNotificaNow(Utente usr)
	{
		Utente usrDb = entityManager.find(Utente.class, usr.getCdUtente());
		 
		entityManager.getTransaction().begin();
		usrDb.setDtNotifica(new Date());
		entityManager.getTransaction().commit();
	}
}
