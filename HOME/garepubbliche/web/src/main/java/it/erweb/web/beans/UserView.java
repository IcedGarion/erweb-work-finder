package it.erweb.web.beans;

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
		boolean creato;
		
		try
		{	
			//crea utente chiamando il servizio
			creato = utenteService.createUtente(utente);

			//se utente e' stato creato, manda alla index
			if(creato)
			{
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
	
	public String login() throws JPAException
	{
		FacesMessage message = null;
			
		//se verifica i dati nel db
		if(utenteService.loginCheck(username, password))
		{
			return "/views/index.xhtml";
		}
		
		//altrimenti rimane sulla stessa pagina con messaggio
		message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Username o password non corretti", "Invalid credentials");
		FacesContext.getCurrentInstance().addMessage(null, message);
		
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