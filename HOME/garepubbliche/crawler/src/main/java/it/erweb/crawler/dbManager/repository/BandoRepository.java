package it.erweb.crawler.dbManager.repository;

import java.util.List;


import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.JPAManager;

public class BandoRepository extends JPAManager
{	
	//altre funzioni specifiche molto usate (tipo "trova tutti i bandi con stato x")
	
	public List<Object> getAllDaParsificare() throws JPAException
	{
		List<Object> result;
		
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
}