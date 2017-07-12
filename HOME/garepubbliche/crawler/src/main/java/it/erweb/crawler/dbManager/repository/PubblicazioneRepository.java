package it.erweb.crawler.dbManager.repository;

import java.util.List;

import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.JPAManager;
import it.erweb.crawler.model.Pubblicazione;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *	Contains the methods for manipulating a Pubblicazione object in the database
 */
public class PubblicazioneRepository extends JPAManager
{
	public List<Pubblicazione> getAllDaScaricare() throws JPAException
	{
		List<Pubblicazione> result;
		
		try
		{
			result = entityManager.createQuery("SELECT p FROM Pubblicazione p WHERE p.stato = 'DA_SCARICARE'").getResultList();
		}
		catch(Exception e)
		{
			throw new JPAException("Error in query read:\n" + e.getMessage());
		}
		
		return result;
	}

	public void updateState(Pubblicazione pubblicazione, String string)
	{
		throw new NotImplementedException();		
	}
}
