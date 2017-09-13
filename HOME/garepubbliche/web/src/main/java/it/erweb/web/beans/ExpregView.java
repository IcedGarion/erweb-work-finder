package it.erweb.web.beans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;

import it.erweb.web.services.ExpregService;

@ManagedBean
@SessionScoped
public class ExpregView implements Serializable
{
	@ManagedProperty("#{expregService}")
	private ExpregService expregService;
	
	private String expregPlus;
	
	private String expregMinus;
		
	/**
	 * 	Prepares the expregs, loads current user's preferences
	 */
	@PostConstruct
    public void init()
	{
		expregPlus = expregService.createPlus();
		expregMinus = expregService.createMinus();
    }
	
	/**
	 * Prepares the expregs, loads current user's preferences: event called on page load
	 * 
	 * @param event	Event calling the action
	 */
	public void init(ComponentSystemEvent event)
	{
		init();
	}
	
	public void setExpregService(ExpregService expregServ)
	{
		this.expregService = expregServ;
	}
	
	public ExpregService getExpregService()
	{
		return this.expregService;
	}
	
	public void setExpregPlus(String plus)
	{
		this.expregPlus = plus;
	}
	
	public void setExpregMinus(String minus)
	{
		this.expregMinus = minus;
	}
	
	public String getExpregPlus()
	{
		return this.expregPlus;
	}
	
	public String getExpregMinus()
	{
		return this.expregMinus;
	}
	
	/**
	 *  Updates current user's PLUS expreg, reading data from the view form and preparing the view "ok" message
	 * 
	 * @param actionEvent
	 */
	public void updatePlus(ActionEvent actionEvent)
	{
		expregService.updatePlus(expregPlus);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Espressione regolare PLUS aggiornata", null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	/**
	 * Updates current user's MINUS expreg, reading data from the view form and preparing the view "ok" message
	 * 
	 * @param actionEvent
	 */
	public void updateMinus(ActionEvent actionEvent)
	{
		expregService.updateMinus(expregMinus);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Espressione regolare MINUS aggiornata", null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}