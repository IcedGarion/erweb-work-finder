package it.erweb.web.services;

import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.erweb.web.data.Utente;
import it.erweb.web.repository.JPAException;
import it.erweb.web.repository.JpaDAO;
import it.erweb.web.util.PasswordUtil;

/**
 *  Backend Service for operations on Utente etities
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
	
	/**
	 *  Inserts the newly created User into the database
	 * 
	 * @param usr	new user who wants to register
	 * @return		true if the registration succeded, false if current username already exists in the database
	 */
	public boolean createUtente(Utente usr)
	{
		String md5, query = "SELECT u FROM Utente u where u.username = '" + usr.getUsername() + "'";
		String exPassword = usr.getPassword();
		List<Utente> duplicati;
		
		try
		{
			//per prima cosa controlla che l'utente non esista gia'
			duplicati = jpaDao.<Utente>read(query);
			if(duplicati.size() != 0)
			{
				return false;
			}
			
			//calcola md5 e lo setta come password
			md5 = PasswordUtil.computeHash(exPassword);
			usr.setPassword(md5);
			
			//setta altri campi...
			usr.setDtNotifica(new Date());
			
			//poi crea in db
			jpaDao.<Utente>create(usr);
			
			//infine chiama la loginCheck (con la ex password in chiaro), cosi' salva le cose in session
			loginCheck(usr.getUsername(), exPassword);
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			return false;
		}
	}
	
	/**
	 *  Checks for username + password presence in the database
	 * 
	 * @param username		user who wants to log-in's username
	 * @param password		user's corresponding password
	 * @return				true if username + password exists in the database, false otherwise
	 * @throws JPAException
	 */
	public boolean loginCheck(String username, String password) throws JPAException
	{
		long cdUtente;
		String query, md5Pass;
		List<Long> cdUtenti;
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);

		//cerca se esiste gia' l'oggetto in session (=> gia' loggato)
		if(session.getAttribute("cdUtente") != null)
		{
			return false;
		}
		
		//nuova login
		//prima calcola md5
		md5Pass = PasswordUtil.computeHash(password);
		
		//poi fa la query
		query = "SELECT u.cdUtente FROM Utente u WHERE u.username = '" + username +  "' AND u.password = '" + md5Pass +  "'";
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
