package it.erweb.crawler.dbManager.repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.JPAManager;
import it.erweb.crawler.model.Pubblicazione;

/**
 *	Contains the methods for manipulating a Pubblicazione object in the database
 */
public class PubblicazioneRepository extends JPAManager
{
	/**
	 * 	Returns a list of all Publications whose State is "DA_SCARICARE"
	 * 
	 * @return	A list of publications
	 * @throws JPAException
	 */
	public static List<Pubblicazione> getAllDaScaricare() throws JPAException
	{
		List<Pubblicazione> result;
		
		try
		{
			result = JPAManager.read("SELECT p FROM Pubblicazione p WHERE p.stato = 'DA_SCARICARE'", Pubblicazione.class);
		}
		catch(Exception e)
		{
			throw new JPAException("Error in query read:\n" + e.getMessage());
		}
		
		return result;
	}

	/**
	 * 	Updates the STATO property of Pubblicazione
	 * 
	 * @param pubblicazione		the publication to be updated
	 * @param newState			the new state
	 */
	public static void updateState(Pubblicazione pubblicazione, String newState)
	{
		Pubblicazione pubDb = entityManager.find(Pubblicazione.class, pubblicazione.getCdPubblicazione());
		 
		entityManager.getTransaction().begin();
		pubDb.setStato(newState);
		entityManager.getTransaction().commit();		
	}

	/**
	 * 	Gets the most recent date from all the Publications' insertions dates
	 * 
	 * @return	the most recent date, or EPOCH if no valid date found
	 * @throws JPAException 
	 */
	public static Date getLastDate() throws JPAException
	{
		Date ret;
		
		List<Date> lastDate = JPAManager.read(
				"SELECT p.dtInserimento FROM Pubblicazione p WHERE p.dtInserimento >= ALL ("
				+ "SELECT p1.dtInserimento from Pubblicazione p1 WHERE p1.stato = 'SCARICATA') ", Date.class);
		
		//se la tabella pubblicazioni e' vuota, ritorna epoch
		//cosi' se c'e' una nuova pubblicazione sul sito sara' per forza piu' recente e quindi la scarica
		if(lastDate.size() == 0)
			ret = Date.from(Instant.EPOCH);
		else
			ret = (Date) lastDate.get(0);
		
		return ret;
	}
}
