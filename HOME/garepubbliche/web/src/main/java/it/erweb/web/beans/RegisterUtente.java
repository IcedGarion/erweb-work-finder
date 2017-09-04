package it.erweb.web.beans;

import java.util.Date;

import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;

import it.erweb.web.dbManager.UtenteService;
import it.erweb.web.model.Utente;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class RegisterUtente
{
	@ManagedProperty("#{UtenteService}")
	private UtenteService utenteService;

	public Utente utente = new Utente();

	public UtenteService getUtenteService()
	{
		return this.utenteService;
	}

	public void setUtenteService(UtenteService usrServ)
	{
		this.utenteService = usrServ;
	}

	public Utente getUtente()
	{
		return this.utente;
	}

	public void setUtente(Utente usr)
	{
		this.utente = usr;
	}

	public String register()
	{
		// Calling Business Service
		this.utente.setDtNotifica(new Date());
		this.utenteService.createUtente(this.utente);
		
		// Add message
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage("The Employee " + this.utente.getUsername() + " Is Registered Successfully"));
		return "";
	}

}