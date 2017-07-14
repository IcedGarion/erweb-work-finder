package it.erweb.crawler.dbManager.repository;

import java.util.List;

import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.JPAManager;
import it.erweb.crawler.model.Expreg;
import it.erweb.crawler.model.Utente;

/**
 *	Contains the methods for manipulating a Expreg object in the database
 */
public class ExpregRepository extends JPAManager
{
	public static List<Expreg> getAllUsersExpr(Utente usr) throws JPAException
	{
		List<Expreg> result;
		String usrname;
		
		try
		{
			usrname = usr.getUsername();
			result = entityManager.createQuery("SELECT e.expplus, e.expminus FROM Expreg e "
					+ "JOIN Utente u ON e.utente = u.cdUtente WHERE u.username = '" + usrname + "'").getResultList();
		}
		catch(Exception e)
		{
			throw new JPAException("Error:\n" + e.getMessage());
		}
		
		return result;
	}
}
