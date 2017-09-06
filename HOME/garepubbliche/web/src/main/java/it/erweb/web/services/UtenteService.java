package it.erweb.web.services;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.erweb.web.data.Utente;
import it.erweb.web.repository.JPAException;
import it.erweb.web.repository.JpaDAO;

/**
 *  Backend Service for Operations on Utente
 */
@Component
public class UtenteService
{	
	@Autowired
	private JpaDAO jpaDao;
	
	public void setJpaDAO(JpaDAO jpaDao)
	{
		this.jpaDao = jpaDao;
	}
	
	public JpaDAO getJpaDAO()
	{
		return this.jpaDao;
	}
	
	public void createUtente(Utente usr)
	{
		try
		{
			jpaDao.<Utente>create(usr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean loginCheck(String username, String password) throws JPAException
	{
		long cdUtente;
		String query = "SELECT u.cdUtente FROM Utente u WHERE u.username = '" + username +  "' AND u.password = '" + password +  "'";
		List<Long> cdUtenti;
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);

		//cerca se esiste gia' l'oggetto in session (=> gia' loggato)
		if(session.getAttribute("cdUtente") != null)
		{
			return false;
		}
		
		//nuova login
		cdUtenti = jpaDao.read(query);
		if(cdUtenti.size() != 1)
		{
			return false;
		}
		
		//se trova una corrispondenza nel db, salva cdUtente in session
		cdUtente = cdUtenti.get(0);
		session.setAttribute("cdUtente", cdUtente);

		return true;
	}
}
