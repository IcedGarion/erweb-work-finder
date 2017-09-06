package it.erweb.web.beans;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import it.erweb.web.repository.JPAException;
import it.erweb.web.services.UtenteService;

@ManagedBean
@SessionScoped
public class UserLoginView
{
	@ManagedProperty("#{utenteService}")
	private UtenteService utenteService;
	
	private String username;

	private String password;
	
	public UtenteService getUtenteService()
	{
		return utenteService;
	}

	public void setUtenteService(UtenteService usrServ)
	{
		utenteService = usrServ;
	}

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
}
