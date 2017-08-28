package it.erweb.crawler.dbManager.repository;

import java.util.Date;
import java.util.List;

import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.JPAManager;
import it.erweb.crawler.model.Bando;


/**
 *	Contains the methods for manipulating a Bando object in the database
 */
public class BandoRepository extends JPAManager<Bando>
{		
	/**
	 * Gets a list of all Bans with Stato "DA_PARSIFICARE"
	 * 
	 * @return	A list of Bans
	 * @throws JPAException
	 */
	public static List<Bando> getAllDaParsificare() throws JPAException
	{
		return getByState("DA_PARSIFICARE");
	}

	/**
	 * Gets a list of all Bans with Stato "PARSIFICATO"
	 * 
	 * @return	A list of Bans
	 * @throws JPAException
	 */
	public static List<Bando> getAllParsificato() throws JPAException
	{
		return getByState("PARSIFICATO");
	}
	
	/**
	 * 	Summarize the two previous functions, which are quite the same
	 * 
	 * @param state	"DA_PARSIFICARE" "PARSIFICATO"
	 * @return	A list of bans matching the state
	 * @throws JPAException
	 */
	private static List<Bando> getByState(String state) throws JPAException
	{
		List<Bando> result;
		
		try
		{
			result = JPAManager.read("SELECT b FROM Bando b WHERE b.stato = '" + state + "' order by b.dtInserimento");
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
	 * @throws JPAException 
	 */
	public static void updateText(Bando ban, String newTesto) throws JPAException
	{
		ban.setTesto(newTesto);
		JPAManager.update(ban);			
	}

	/**
	 *  Updates the CIG property of a Ban
	 * 
	 * @param ban	the Ban to be updated
	 * @param newCig	the new value of CIG property
	 * @throws JPAException 
	 */
	public static void updateCig(Bando ban, String newCig) throws JPAException
	{
		ban.setCig(newCig);
		JPAManager.update(ban);				
	}

	/**
	 * Updates the property OBJECT of a Ban
	 * 
	 * @param ban	the Ban to be updated
	 * @param newOggetto	the new value of the OGGETTO Ban's property
	 * @throws JPAException 
	 */
	public static void updateObject(Bando ban, String newOggetto) throws JPAException
	{
		ban.setOggetto(newOggetto);
		JPAManager.update(ban);
	}

	/**
	 * Updates the STATO property of a Ban
	 * 
	 * @param ban	the Ban to be modified
	 * @param newStato	the new value of STATO
	 * @throws JPAException 
	 */
	public static void updateState(Bando ban, String newStato) throws JPAException
	{
		ban.setStato(newStato);
		JPAManager.update(ban);			
	}
	
	/**
	 * Updates the DT_INSERIMENTO property of a Ban
	 * 
	 * @param ban		the Ban to be modified
	 * @param newDate	the new value of DT_INSERIMENTO
	 * @throws JPAException 
	 */
	public static void updateDtInserimento(Bando ban, Date newDate) throws JPAException
	{
		ban.setDtInserimento(newDate);
		JPAManager.update(ban);			
	}

	/**
	 * Returns only the bans having DT_INSERIMENTO after the least recent users' DT_NOTIFICA
	 * 
	 * @return				A List of PARSIFICATO bans
	 * @throws JPAException
	 */
	public static List<Bando> getLatestParsificato() throws JPAException
	{
		List<Bando> result;
		Date firstNotify;

		//prende la data di notifica piu' vecchia fra tutti gli utenti	
		firstNotify = (Date) JPAManager.read("SELECT MIN(u.dtNotifica) FROM Utente u").get(0);
		
		//prende solo i bandi "parsificato" con data posteriore alla notifica piu' vecchia
		result = JPAManager.read(
				"SELECT b FROM Bando b WHERE b.stato = 'PARSIFICATO' "
				+ "AND b.dtInserimento > '" + firstNotify + "' order by b.dtInserimento");
		
		return result;
	}
}