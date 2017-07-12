package it.erweb.crawler.dbManager.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
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

	public Date getLastDate()
	{
		ArrayList<Object> ret = (ArrayList<Object>) entityManager.createQuery("SELECT p.dtInserimento FROM Pubblicazione p WHERE p.dtInserimento >= ALL (SELECT p1.dtInserimento from Pubblicazione p1 WHERE p1.stato = 'SCARICATA') ").getResultList();
		if(ret.size() == 0)
			return Date.from(Instant.EPOCH);
		else
			return (Date) ret.get(0);
	}
}
