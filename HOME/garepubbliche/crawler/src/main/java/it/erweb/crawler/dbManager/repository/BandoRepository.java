package it.erweb.crawler.dbManager.repository;

import java.util.List;

import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.JPAManager;
import it.erweb.crawler.model.Bando;


/**
 *	Contains the methods for manipulating a Bando object in the database
 */
public class BandoRepository extends JPAManager
{		
	/**
	 * Gets a list of all Bans with Stato "DA_PARSIFICARE"
	 * 
	 * @return	A list of Bans
	 * @throws JPAException
	 */
	public static List<Bando> getAllDaParsificare() throws JPAException
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

	/**
	 * 	Updates the TESTO property of a Ban
	 * 
	 * @param ban	the Ban to be updated
	 * @param newTesto	the new TESTO property value
	 */
	public static void updateText(Bando ban, String newTesto)
	{
		Bando banDb = entityManager.find(Bando.class, ban.getCdBando());
		 
		entityManager.getTransaction().begin();
		banDb.setTesto(newTesto);
		entityManager.getTransaction().commit();			
	}

	/**
	 *  Updates the CIG property of a Ban
	 * 
	 * @param ban	the Ban to be updated
	 * @param newCig	the new value of CIG property
	 */
	public static void updateCig(Bando ban, String newCig)
	{
		Bando banDb = entityManager.find(Bando.class, ban.getCdBando());
		 
		entityManager.getTransaction().begin();
		banDb.setCig(newCig);
		entityManager.getTransaction().commit();				
	}

	/**
	 * Updates the property OBJECT of a Ban
	 * 
	 * @param ban	the Ban to be updated
	 * @param newOggetto	the new value of the OGGETTO Ban's property
	 */
	public static void updateObject(Bando ban, String newOggetto)
	{
		Bando banDb = entityManager.find(Bando.class, ban.getCdBando());
		 
		entityManager.getTransaction().begin();
		banDb.setOggetto(newOggetto);
		entityManager.getTransaction().commit();
	}

	/**
	 * Updates the STATO property of a Ban
	 * 
	 * @param ban	the Ban to be modified
	 * @param newStato	the new value of STATO
	 */
	public static void updateState(Bando ban, String newStato)
	{
		Bando banDb = entityManager.find(Bando.class, ban.getCdBando());
		 
		entityManager.getTransaction().begin();
		banDb.setStato(newStato);
		entityManager.getTransaction().commit();				
	}

	public static List<Bando> getAllParsificatiNuovi() throws Exception
	{
		throw new Exception("Not yet implemented");
	}
}