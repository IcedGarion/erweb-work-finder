package it.erweb.web.services;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.erweb.web.data.Expreg;
import it.erweb.web.repository.JpaDao;

@Component
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
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
		long cdUtente;
		
		try
		{
			//prende cdUtente da session e cerca nel db le espressioni corrisponenti a quell'utente
			cdUtente = (long) session.getAttribute("cdUtente");
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
	
	public void updatePlus(String newRegex)
	{
		boolean isPlus = true;
		update(isPlus, newRegex);
	}
	
	public void updateMinus(String newRegex)
	{
		boolean isPlus = false;
		update(isPlus, newRegex);
	}
	
	private void update(boolean isPlus, String newRegex)
	{
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
		long cdUtente;
		
		try
		{
			cdUtente = (long) session.getAttribute("cdUtente");
			
			//prende i dati della vecchia expreg
			Expreg old = jpaDao.<Expreg>read("SELECT e FROM Expreg e WHERE e.utente.cdUtente = " + cdUtente).get(0);
			
			//modifica la vecchia expreg coi nuovi dati
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
