package it.erweb.crawler.expregMatcher;

import java.util.Date;

import it.erweb.crawler.model.Bando;
import it.erweb.crawler.model.Expreg;
import it.erweb.crawler.model.Utente;

/**
 * A class for manage the user's regular expressions matching with the bans
 */
public class Matcher
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
	 * 	Does the match between the given user (and his regular expressions) and the given ban
	 * 
	 * @param usr	the user owning the regular expressions
	 * @param ban	the ban that needs to be matched, containing the main text and, optionally, the object
	 * @return		true if all the regular expressions produce a positive match, false otherwise
	 * @throws Exception
	 */
	public static boolean tryMatch(Utente usr, Bando ban)
	{
		String expPlus, expMinus, banObj, banTxt;
		Expreg expregs;
		boolean ret = false;

		expregs = usr.getExpreg();
		expPlus = expregs.getExpplus();
		expMinus = expregs.getExpminus();
		banObj = ban.getOggetto();
		banTxt = ban.getTesto();

		// se non c'e' l'oggetto, cerca solo nel testo
		if(banObj == null || banObj == "")
		{
			banObj = banTxt;
		}

		// prova prima il match con le expregs negative (che NON devono matchare!)
		if(!(banObj.matches(expMinus)) || (expMinus.equals("")))
		{
			// se passa, prova anche le negative
			if(banObj.matches(expPlus))
			{
				ret = true;
			}
		}
		
		// se non passa in entrambi gli if, ret rimane false
		return ret;
	}
}
