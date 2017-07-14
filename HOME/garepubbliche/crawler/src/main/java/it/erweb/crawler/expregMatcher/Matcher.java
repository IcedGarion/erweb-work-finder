package it.erweb.crawler.expregMatcher;

import java.util.List;

import it.erweb.crawler.model.Bando;
import it.erweb.crawler.model.Expreg;
import it.erweb.crawler.model.Utente;

public class Matcher
{

	public static boolean tryMatch(Utente usr, Bando ban) throws Exception
	{
		String expPlus, expMinus;
		List<Expreg> expregs;
		
		try
		{
			expregs = usr.getExpregs();
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		throw new Exception("Not yet implemented");
	}
}
