package it.erweb.web.services;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.erweb.web.data.Expreg;
import it.erweb.web.data.Utente;
import it.erweb.web.repository.JpaDao;
import it.erweb.web.util.SessionManager;

@Service
public class ExpregService implements Serializable
{
	@Autowired
	private JpaDao jpaDao;
	
	public void setJpaDAO(JpaDao jpaDao)
	{
		this.jpaDao = jpaDao;
	}
	
	public JpaDao getJpaDAO()
	{
		return this.jpaDao;
	}
	
	/**
	 *  Returns the PLUS regex corresponding to the current logged-in user
	 * 
	 * @return 	A string representing the PLUS regex
	 */
	public String createPlus()
	{
		boolean isPlus = true;
		return getRegex(isPlus);
	}
	
	/**
	 *  Returns the MINUS regex corresponding to the current logged-in user
	 * 
	 * @return 	A string representing the MINUS regex
	 */
	public String createMinus()
	{
		boolean isPlus = false;
		return getRegex(isPlus);
	}
	
	//Summarize the 2 functions above: a call with 'true' parameter results in searching for expplus
	private String getRegex(boolean isPlus)
	{
		String ret = "<VUOTA>";
		String type = isPlus? "expplus" : "expminus";
		long cdUtente;
		
		try
		{
			//prende cdUtente da session e cerca nel db le espressioni corrisponenti a quell'utente
			cdUtente = (long) SessionManager.getSessionUser();
			ret = jpaDao.<String>read("SELECT e." + type + " FROM Expreg e WHERE e.utente.cdUtente = " + cdUtente).get(0);
		}
		catch(Exception e)
		{
			//ret rimane 'vuota'
			//e.printStackTrace();
		}
		
		if(ret.equals(""))
		{
			ret = "<VUOTA>"; 
		}
		
		return ret;
	}
	
	/**
	 *  Updates current user's PLUS regex, storing the new one on the database
	 * 
	 * @param newRegex    the new regular expression to be stored
	 */
	public void updatePlus(String newRegex)
	{
		boolean isPlus = true;
		update(isPlus, newRegex);
	}
	
	/**
	 *  Updates current user's MINUS regex, storing the new one in the database
	 * 
	 * @param newRegex    the new regular expression to be stored
	 */
	public void updateMinus(String newRegex)
	{
		boolean isPlus = false;
		update(isPlus, newRegex);
	}
	
	//Refactors the regex update functions: all-in-one
	private void update(boolean isPlus, String newRegex)
	{
		Expreg old;
		List<Expreg> olds;
		Utente current;
		long cdUtente;
		
		try
		{
			cdUtente = (long) SessionManager.getSessionUser();
			
			//prende i dati della vecchia expreg
			olds = jpaDao.<Expreg>read("SELECT e FROM Expreg e WHERE e.utente.cdUtente = " + cdUtente);
			if(olds.size() > 0)
			{
				old = olds.get(0);
			}
			else
			{
				//se non c'e' ancora nessuna expreg associata la crea
				old = new Expreg();
				old.setExpminus("");
				old.setExpplus("");
				//cerca utente da associare
				current = jpaDao.<Utente>read("SELECT u FROM Utente u WHERE u.cdUtente = " + cdUtente).get(0);
				old.setUtente(current);
			}
			
			//modifica la "vecchia" expreg coi nuovi dati
			if(isPlus)
			{
				old.setExpplus(newRegex);
			}
			else
			{
				old.setExpminus(newRegex);
			}
			
			//poi aggiorna
			jpaDao.<Expreg>update(old);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}