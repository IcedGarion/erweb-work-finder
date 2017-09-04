package it.erweb.web.beans;

import java.util.Date;

import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;

import it.erweb.web.dbManager.UtenteService;
import it.erweb.web.model.Utente;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class RegisterUtente
{
	@ManagedProperty("#{utenteService}")
	private UtenteService utenteService;

	public Utente utente = new Utente();
	    		
	public UtenteService getUtenteService()
	{
		return utenteService;
	}

	@Autowired
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
					new FacesMessage("The Employee " + utente.getUsername() + " Is Registered Successfully"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

}