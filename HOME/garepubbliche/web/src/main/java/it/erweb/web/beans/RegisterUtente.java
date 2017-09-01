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
	private UtenteService usrServ;

	private Utente usr= new Utente();

	public UtenteService getUtenteService()
	{
		return this.usrServ;
	}

	public void setUtenteService(UtenteService usrServ)
	{
		this.usrServ = usrServ;
	}

	public Utente getEmployee()
	{
		return this.usr;
	}

	public void setEmployee(Utente usr)
	{
		this.usr = usr;
	}

	public String register()
	{
		// Calling Business Service
		this.usr.setDtNotifica(new Date());
		this.usrServ.createUtente(this.usr);
		// Add message
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage("The Employee " + this.usr.getUsername() + " Is Registered Successfully"));
		return "";
	}

}