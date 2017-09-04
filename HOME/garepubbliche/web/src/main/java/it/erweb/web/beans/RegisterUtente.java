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
	@Autowired
	//@ManagedProperty("#{utenteService}")
	private UtenteService utenteService;

	public Utente utente = new Utente();
	
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
			// Calling Business Service
			utente.setDtNotifica(new Date());
			System.out.println("UtenteService:\n" + utenteService);
			System.out.println("EM:\n" + utenteService.getEm());
			System.out.println("UTENTE:\n" + utente.toString());

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