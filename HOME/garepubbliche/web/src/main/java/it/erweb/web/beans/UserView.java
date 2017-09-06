package it.erweb.web.beans;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import it.erweb.web.data.Utente;
import it.erweb.web.repository.JPAException;
import it.erweb.web.services.UtenteService;

/**
 *  Faces Bean for user interaction
 */
@ManagedBean
@SessionScoped
public class UserView
{
	@ManagedProperty("#{utenteService}")
	private UtenteService utenteService;

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
	    		
	public UtenteService getUtenteService()
	{
		return utenteService;
	}

	public void setUtenteService(UtenteService usrServ)
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

	public String register()
	{
		try
		{
			//A questo punto il form ha gia' settato le proprieta' di utente (ajax)
			utente.setDtNotifica(new Date());
			//crea utente chiamando il servizio
			utenteService.createUtente(utente);

			// Add message
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Utente " + utente.getUsername() + " Registrato!"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return "";
	}
	
	public String login() throws JPAException
	{
		//RequestContext context = RequestContext.getCurrentInstance();
		//boolean loggedIn = false;
		FacesMessage message = null;
			
		//se verifica i dati nel db
		if(utenteService.loginCheck(username, password))
		{
			return "/views/index.xhtml";
		}
		
		//altrimenti rimane sulla stessa pagina con messaggio
		message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Username o password non corretti", "Invalid credentials");
		FacesContext.getCurrentInstance().addMessage(null, message);
		//context.addCallbackParam("loggedIn", loggedIn);
		
		return "";
	}
	
	public String logout() throws JPAException
	{		
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
		
		session.removeAttribute("cdUtente");
			
		return "/views/login.xhtml";
	}

}