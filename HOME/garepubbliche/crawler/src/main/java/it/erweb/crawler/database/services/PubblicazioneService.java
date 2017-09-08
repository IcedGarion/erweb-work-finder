package it.erweb.crawler.database.services;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import it.erweb.crawler.database.model.Pubblicazione;
import it.erweb.crawler.database.repository.JpaDao;
import it.erweb.crawler.database.repository.JpaException;

/**
 *	Contains the methods for manipulating a Pubblicazione object in the database
 */
public class PubblicazioneService extends JpaDao<Pubblicazione>
{
	/**
	 * 	Returns a list of all Publications whose State is "DA_SCARICARE"
	 * 
	 * @return	A list of publications
	 * @throws JpaException
	 */
	public static List<Pubblicazione> getAllDaScaricare() throws JpaException
	{
		List<Pubblicazione> result;
		
		result = read("SELECT p FROM Pubblicazione p WHERE p.stato = 'DA_SCARICARE'");
		
		return result;
	}

	/**
	 * 	Updates the STATO property of Pubblicazione
	 * 
	 * @param pubblicazione		the publication to be updated
	 * @param newState			the new state
	 * @throws JpaException 
	 */
	public static void updateState(Pubblicazione pubblicazione, String newState) throws JpaException
	{
		pubblicazione.setStato(newState);
		update(pubblicazione);		
	}

	/**
	 * 	Gets the most recent date from all the Publications' insertions dates
	 * 
	 * @return	the most recent date, or EPOCH if no valid date found
	 * @throws JpaException 
	 */
	public static Date getLastDate() throws JpaException
	{
		Date ret;
		
		List<Date> lastDate = read(
				"SELECT p.dtInserimento FROM Pubblicazione p WHERE p.dtInserimento >= ALL ("
				+ "SELECT p1.dtInserimento from Pubblicazione p1 WHERE p1.stato = 'SCARICATA') ");
		
		//se la tabella pubblicazioni e' vuota, ritorna epoch
		//cosi' se c'e' una nuova pubblicazione sul sito sara' per forza piu' recente e quindi la scarica
		if(lastDate.size() == 0)
			ret = Date.from(Instant.EPOCH);
		else
			ret = (Date) lastDate.get(0);
		
		return ret;
	}
	
	/**
	 *  Inserts a new entry of Pubblicazione
	 * 
	 * @param pub	the given Pubblicazione to be inserted
	 */
	public static void insertPubblicazione(Pubblicazione pub)
	{
		create(pub);
		
		return;
	}
}
