package it.erweb.crawler.dbManager.repository;

import java.util.List;


import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.JPAManager;
import it.erweb.crawler.model.Bando;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *	Contains the methods for manipulating a Bando object in the database
 */
public class BandoRepository extends JPAManager
{	
	//altre funzioni specifiche molto usate (tipo "trova tutti i bandi con stato x")
	
	public List<Bando> getAllDaParsificare() throws JPAException
	{
		List<Bando> result;
		
		try
		{
			result = entityManager.createQuery("SELECT b FROM Bando b WHERE b.stato = 'DA_PARSIFICARE'").getResultList();
		}
		catch(Exception e)
		{
			throw new JPAException("Error in query read:\n" + e.getMessage());
		}
		
		return result;
	}

	public void updateText(Bando ban, String testoBando)
	{
		throw new NotImplementedException();				
	}

	public void updateCig(Bando ban, String cig)
	{
		throw new NotImplementedException();				
	}

	public void updateObject(Bando ban, String oggetto)
	{
		throw new NotImplementedException();				
	}

	public void updateState(Bando ban, String string)
	{
		throw new NotImplementedException();				
	}
}