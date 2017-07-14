package it.erweb.crawler.dbManager.repository;

import java.util.List;

import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.JPAManager;
import it.erweb.crawler.model.Utente;

/**
 *	Contains the methods for manipulating a Utente object in the database
 */
public class UtenteRepository extends JPAManager
{
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
}
