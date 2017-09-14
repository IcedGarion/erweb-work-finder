package it.erweb.web.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.erweb.web.data.Bando;
import it.erweb.web.repository.JPAException;
import it.erweb.web.repository.JpaDao;
import it.erweb.web.util.SessionManager;

/**
 *  Backend service managing backend operations with Bando entities
 */
@Service
public class BandiService implements Serializable
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
	 *  Searches the database and creates a list of Bans that need to be notified to the user
	 * 
	 * @param filter			String indicating if the user requested all the bans, or the new ones only
	 * @return  a list of Bans
	 */
	public List<Bando> createUserBans(String filter)
	{
		List<Bando> ret = new ArrayList<>();
		long cdUtente;
		String query;
		Object session = SessionManager.getSessionUser();
		
		//recupera cdUtente da session (se loggato)
		if(session != null)
		{
			cdUtente = (long) session;
			
			//sceglie con un parametro se prendere tutti i bandi (che matchano con le expreg di quell'utente)
			//oppure se scegliere soltanto gli utlimi arrivati
			if(filter == null || filter.equals("new"))
			{
				query = "SELECT b FROM Bando b, Notifica n " + "WHERE n.id.cdBando = b.cdBando " + "AND n.id.cdUtente = "
						+ cdUtente + " AND n.stato = 'DA_INVIARE' ORDER BY b.dtInserimento";
			}
			else
			{
				query = "SELECT b FROM Bando b, Notifica n " + "WHERE n.id.cdBando = b.cdBando " + "AND n.id.cdUtente = "
						+ cdUtente + " ORDER BY b.dtInserimento";
			}

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