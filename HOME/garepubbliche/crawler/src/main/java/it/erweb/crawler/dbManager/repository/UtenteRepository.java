package it.erweb.crawler.dbManager.repository;

import java.util.List;

import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.JPAManager;

public class UtenteRepository extends JPAManager
{
	public List<Object> getAllUsers() throws JPAException
	{
		List<Object> result;
		
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
