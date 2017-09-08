package it.erweb.web.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.erweb.web.data.Bando;
import it.erweb.web.repository.JPAException;
import it.erweb.web.repository.JpaDAO;

/**
 *  Backend service managing backend operations with Bando entities
 */
@Component
public class BandiService implements Serializable
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
	
	/**
	 *  Searches the database and creates a list of Bans that need to be notified to the user
	 * 
	 * @return  a list of Bans
	 */
	public List<Bando> createUserBans()
	{
		List<Bando> ret = new ArrayList<>();
		long cdUtente;
		String query;
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
		
		//recupera cdUtente da session (se loggato)
		if(session.getAttribute("cdUtente") != null)
		{
			cdUtente = (long) session.getAttribute("cdUtente");
			//prende tutti i bandi da notificare a quell'utente
			query = "SELECT b FROM Bando b, Notifica n " + "WHERE n.id.cdBando = b.cdBando " + "AND n.id.cdUtente = "
					+ cdUtente + " AND n.stato = 'DA_INVIARE' ORDER BY b.dtInserimento";

			try
			{
				ret = jpaDao.read(query);
			}
			catch(JPAException e)
			{
				//se per caso capita un'eccezione, la lista rimane vuota
			}
		}
		
		return ret;
	}
}
