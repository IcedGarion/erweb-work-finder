package it.erweb.crawler.database.services;

import java.util.Date;

import it.erweb.crawler.database.model.Bando;
import it.erweb.crawler.database.model.Expreg;
import it.erweb.crawler.database.model.Utente;
import it.erweb.crawler.database.repository.JpaDao;

/**
 *	Contains the methods for manipulating a Expreg object in the database
 */
public class ExpregService extends JpaDao<Expreg>
{
	/**
	 * 	Checks if user's last notification date is before the ban's insertion date: this assures to avoid
	 * 	having an user notified several times for the same ban
	 * 
	 * @param usr	the user that wants to be (potentially) notified
	 * @param ban	the ban to be (potentially) notified
	 * @return	true if the ban's date strictly follows the user's last notification date; false otherwise
	 */
	public static boolean checkDate(Utente usr, Bando ban)
	{
		Date inserimento = ban.getDtInserimento(), notifica = usr.getDtNotifica();
		return (inserimento.after(notifica) || inserimento.equals(notifica));
	}
	
	/**
	 * 	Tries the match between the given user (his regular expressions) and the given ban
	 * 
	 * @param usr	the user owning the regular expressions
	 * @param ban	the ban that needs to be matched, containing the main text and, optionally, the object
	 * @return		true if all the regular expressions produce a positive match, false otherwise
	 * @throws Exception
	 */
	public static boolean tryMatchUserExpreg(Utente usr, Bando ban)
	{
		String expPlus, expMinus, banObj, banTxt;
		Expreg expregs;
		boolean ret = false;

		expregs = usr.getExpreg();
		//magari utente non ha ancora creato nessuna expreg
		if(expregs == null)
		{
			return ret;
		}
		
		expPlus = expregs.getExpplus();
		expMinus = expregs.getExpminus();
		banObj = ban.getOggetto();
		banTxt = ban.getTesto();

		// se non c'e' l'oggetto, cerca solo nel testo
		if(banObj == null || banObj.equals(""))
		{
			banObj = banTxt;
		}

		// prova prima il match con le expregs negative (che NON devono matchare!)
		if(!(banObj.matches(expMinus)) || (expMinus.equals("")))
		{
			// se passa, prova anche le positive
			if(banObj.matches(expPlus))
			{
				ret = true;
			}
		}
		
		return ret;
	}
}
