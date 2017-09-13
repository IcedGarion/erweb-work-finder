package it.erweb.web.beans;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;

import it.erweb.web.data.Utente;
import it.erweb.web.repository.JPAException;
import it.erweb.web.services.UserService;
import it.erweb.web.util.SessionManager;

/**
 *  Faces Bean for managing all User's frontend operations: login, logout, create
 */
@ManagedBean
@SessionScoped
public class UserView
{
	@ManagedProperty("#{userService}")
	private UserService utenteService;

	//per la register
	public Utente utente = new Utente();
	
	//per login
	private String username;

	private String password;
	
	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}
	    		
	public UserService getUtenteService()
	{
		return utenteService;
	}

	public void setUtenteService(UserService usrServ)
	{
		utenteService = usrServ;
	}

	public Utente getUtente()
	{
		return utente;
	}

	public void setUtente(Utente usr)
	{
		utente = usr;
	}

	/**
	 *  Creates a new User and inserts it in the database
	 * 
	 * @return A string representing the next view page to be displayed:
	 * 			index (if the registration succeded) or the current page (register) if not
	 */
	public String register()
	{
		Utente creato;
		
		try
		{	
			//crea utente chiamando il servizio
			creato = utenteService.createUtente(utente);

			//se utente e' stato creato, manda alla index
			if(creato != null)
			{
				//salva l'utente appena creato come proprieta', cosi' da averne i dati
				utente = creato;
				return "/views/index.xhtml";	
			}
			
			//altrimenti rimanda alla create con messaggio di errore
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Username gia' esistente!"));
			
			return "";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Errore"));
			
			return "";
		}
	}
	
	/**
	 *  Checks for the form credentials and inserts some data in session if the check succeded
	 * 
	 * @return	A string representing a mapping fo the next page to be displayed: index on login success, login page if not
	 * @throws JPAException
	 */
	public String login() throws JPAException
	{
		FacesMessage message = null;
		Utente loggedIn = null;
		
		loggedIn = utenteService.loginCheck(username, password);
		//se verifica i dati nel db
		if(loggedIn != null)
		{
			//salva l'utente appena loggato come proprieta', cosi' da averne i dati
			utente = loggedIn;
			return "/views/index.xhtml";
		}
		
		//altrimenti rimane sulla stessa pagina con messaggio
		message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Username o password non corretti", "Invalid credentials");
		FacesContext.getCurrentInstance().addMessage(null, message);
		
		return "";
	}
	
	/**
	 *  Removes session data and redirects to login page
	 * 
	 * @return		A string representing the mapping to login page
	 * @throws JPAException
	 */
	public String logout() throws JPAException
	{	
		SessionManager.removeSessionUser();
			
		return "/views/login.xhtml";
	}
	
	/**
	 * Updates current user's email, reading from the view prefs form and preparing the view "ok" message
	 * AJAX
	 * 
	 * @param actionEvent
	 */
	public void updateMail(ActionEvent actionEvent)
	{
		utenteService.updateMail(utente.getEmail());
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Email aggiornata", null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	/**
	 * Updates current user's Password, reading from the view prefs form and preparing the view "ok" message
	 * AJAX
	 * 
	 * @param actionEvent
	 */
	public void updatePassword(ActionEvent actionEvent)
	{
		utenteService.updatePassword(utente.getPassword());
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Password aggiornata", null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	/**
	 *  Checks for login error messages, set by Security Filter, and creates custom Faces message to be displayed
	 *  	on login page
	 *  EVENT
	 * 
	 * @param event
	 */
	public void messageCheck(ComponentSystemEvent event)
	{
		//cerca se qualcuno (filter) ha inserito un messaggio in session:
		//se si, lo rimuove da session e lo aggiunge alla view
		Object sessionMsg = SessionManager.getLoginMessage();
		String msg;
		
		if(sessionMsg != null)
		{
			msg = (String) sessionMsg;
			SessionManager.removeLoginMessage();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, null);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}
}