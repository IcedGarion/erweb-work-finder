package it.erweb.web.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import it.erweb.web.repository.JPAException;
import it.erweb.web.services.UtenteService;

@ManagedBean
@SessionScoped
public class UserLogoutView
{
	@ManagedProperty("#{utenteService}")
	private UtenteService utenteService;

	public UtenteService getUtenteService()
	{
		return utenteService;
	}

	public void setUtenteService(UtenteService usrServ)
	{
		utenteService = usrServ;
	}

	public String logout() throws JPAException
	{		
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
		
		session.removeAttribute("cdUtente");
			
		return "/views/login.xhtml";
	}
}
