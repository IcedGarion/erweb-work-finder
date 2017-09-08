package it.erweb.web.beans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import it.erweb.web.services.ExpregService;

@ManagedBean
@SessionScoped
public class ExpregView implements Serializable
{
	@ManagedProperty("#{expregService}")
	private ExpregService expregService;
	
	private String expregPlus;
	
	private String expregMinus;
	
	private static final long serialVersionUID = 4299482300749221518L;
	
	@PostConstruct
    public void init()
	{
		expregPlus = expregService.createPlus();
		expregMinus = expregService.createMinus();
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
	
	public void updatePlus(ActionEvent actionEvent)
	{
		expregService.updatePlus(expregPlus);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Espressione regolare PLUS aggiornata", null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public void updateMinus(ActionEvent actionEvent)
	{
		expregService.updateMinus(expregMinus);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Espressione regolare MINUS aggiornata", null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}