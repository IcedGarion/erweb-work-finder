package it.erweb.web.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.erweb.web.data.Utente;
import it.erweb.web.repository.JPAException;
import it.erweb.web.repository.JpaDao;
import it.erweb.web.util.PasswordUtil;
import it.erweb.web.util.SessionManager;

/**
 *  Backend Service for operations on Utente etities
 */
@Service
public class UserService
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
	 *  Inserts the newly created User into the database
	 * 
	 * @param usr	new user who wants to register
	 * @return		true if the registration succeded, false if current username already exists in the database
	 */
	public Utente createUtente(Utente usr)
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
				return null;
			}
			
			//calcola md5 e lo setta come password
			md5 = PasswordUtil.computeHash(exPassword);
			usr.setPassword(md5);
			
			//setta altri campi...
			usr.setDtNotifica(new Date());

			//poi crea in db
			jpaDao.<Utente>create(usr);
			
			//infine chiama la loginCheck (con la ex password in chiaro), cosi' salva le cose in session
			return loginCheck(usr.getUsername(), exPassword);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			return null;
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
	public Utente loginCheck(String username, String password) throws JPAException
	{
		long cdUtente;
		String query, md5Pass;
		Utente loggedIn;
		Object session = SessionManager.getSessionUser();
		
		//cerca se esiste gia' l'oggetto in session (=> gia' loggato)
		if(session != null)
		{
			return null;
		}
		
		//nuova log7in
		//prima calcola md5
		md5Pass = PasswordUtil.computeHash(password);
		
		//poi fa la query
		query = "SELECT u FROM Utente u WHERE u.username = '" + username +  "' AND u.password = '" + md5Pass +  "'";
		List<Utente> loggedInList = jpaDao.read(query);
		if(loggedInList.size() != 1)
		{
			return null;
		}
		
		//se trova una corrispondenza nel db, salva cdUtente e username in session
		loggedIn = loggedInList.get(0);
		cdUtente = loggedIn.getCdUtente();
		SessionManager.setSessionUser(cdUtente, username);	//setta cdUtente e anche username (lo usera' solo il menu nav)

		return loggedIn;
	}
	
	public void updateMail(String newMail)
	{
		update("email", newMail);
	}
	
	public void updatePassword(String newPass)
	{
		update("password", newPass);
	}
	
	private void update(String key, String value)
	{
		long cdUtente;
		
		try
		{
			cdUtente = (long) SessionManager.getSessionUser();
			
			//prende i dati del vecchio utente
			Utente old = jpaDao.<Utente>read("SELECT u FROM Utente u WHERE u.cdUtente = " + cdUtente).get(0);
			
			//cerca il campo da modificare
			switch(key)
			{
				case "password":
					old.setPassword(PasswordUtil.computeHash(value));
					break;
				case "email":
					old.setEmail(value);
					break;
			}
						
			//poi aggiorna
			jpaDao.<Utente>update(old);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
