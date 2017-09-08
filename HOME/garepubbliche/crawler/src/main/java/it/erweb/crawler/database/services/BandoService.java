package it.erweb.crawler.database.services;

import java.util.Date;
import java.util.List;

import it.erweb.crawler.database.model.Bando;
import it.erweb.crawler.database.repository.JpaDao;
import it.erweb.crawler.database.repository.JpaException;


/**
 *	Contains the methods for manipulating a Bando object in the database
 */
public class BandoService extends JpaDao<Bando>
{		
	/**
	 * Gets a list of all Bans with Stato "DA_PARSIFICARE"
	 * 
	 * @return	A list of Bans
	 * @throws JpaException
	 */
	public static List<Bando> getAllDaParsificare() throws JpaException
	{
		return getByState("DA_PARSIFICARE");
	}

	/**
	 * Gets a list of all Bans with Stato "PARSIFICATO"
	 * 
	 * @return	A list of Bans
	 * @throws JpaException
	 */
	public static List<Bando> getAllParsificato() throws JpaException
	{
		return getByState("PARSIFICATO");
	}
	
	/**
	 * 	Summarize the two previous functions, which are quite the same
	 * 
	 * @param state	"DA_PARSIFICARE" "PARSIFICATO"
	 * @return	A list of bans matching the state
	 * @throws JpaException
	 */
	private static List<Bando> getByState(String state) throws JpaException
	{
		List<Bando> result;
		
		result = read("SELECT b FROM Bando b WHERE b.stato = '" + state + "' order by b.dtInserimento");		
		
		return result;
	}
	
	/**
	 * 	Updates the TESTO property of a Ban
	 * 
	 * @param ban	the Ban to be updated
	 * @param newTesto	the new TESTO property value
	 * @throws JpaException 
	 */
	public static void updateText(Bando ban, String newTesto) throws JpaException
	{
		ban.setTesto(newTesto);
		update(ban);			
	}

	/**
	 *  Updates the CIG property of a Ban
	 * 
	 * @param ban	the Ban to be updated
	 * @param newCig	the new value of CIG property
	 * @throws JpaException 
	 */
	public static void updateCig(Bando ban, String newCig) throws JpaException
	{
		ban.setCig(newCig);
		update(ban);				
	}

	/**
	 * Updates the property OBJECT of a Ban
	 * 
	 * @param ban	the Ban to be updated
	 * @param newOggetto	the new value of the OGGETTO Ban's property
	 * @throws JpaException 
	 */
	public static void updateObject(Bando ban, String newOggetto) throws JpaException
	{
		ban.setOggetto(newOggetto);
		update(ban);
	}

	/**
	 * Updates the STATO property of a Ban
	 * 
	 * @param ban	the Ban to be modified
	 * @param newStato	the new value of STATO
	 * @throws JpaException 
	 */
	public static void updateState(Bando ban, String newStato) throws JpaException
	{
		ban.setStato(newStato);
		update(ban);			
	}
	
	/**
	 * Updates the DT_INSERIMENTO property of a Ban
	 * 
	 * @param ban		the Ban to be modified
	 * @param newDate	the new value of DT_INSERIMENTO
	 * @throws JpaException 
	 */
	public static void updateDtInserimento(Bando ban, Date newDate) throws JpaException
	{
		ban.setDtInserimento(newDate);
		update(ban);			
	}

	/**
	 * Returns only the bans having DT_INSERIMENTO after the least recent users' DT_NOTIFICA
	 * 
	 * @return				A List of PARSIFICATO bans
	 * @throws JpaException
	 */
	public static List<Bando> getLatestParsificato() throws JpaException
	{
		List<Bando> result;
		Date firstNotify;

		//prende la data di notifica piu' vecchia fra tutti gli utenti	
		firstNotify = (Date) read("SELECT MIN(u.dtNotifica) FROM Utente u").get(0);
		
		//prende solo i bandi "parsificato" con data posteriore alla notifica piu' vecchia
		result = read(
				"SELECT b FROM Bando b WHERE b.stato = 'PARSIFICATO' "
				+ "AND b.dtInserimento > '" + firstNotify + "' order by b.dtInserimento");
		
		return result;
	}

	/**
	 *  Returns the entity identified by the ID
	 * 
	 * @param cdBando	the ID	
	 * @return			the entity Bando
	 * @throws JpaException 
	 */
	public static Bando getById(long cdBando) throws JpaException
	{
		List<Bando> result;
		
		result = read("SELECT b FROM Bando b WHERE b.cdBando = " + cdBando);

		return result.get(0);
	}

	/**
	 *  Inserts a new entry of Bando 
	 * 
	 * @param ban	the new Bando to be created
	 */
	public static void insertBando(Bando ban)
	{
		create(ban);
		
		return;
		
	}
}