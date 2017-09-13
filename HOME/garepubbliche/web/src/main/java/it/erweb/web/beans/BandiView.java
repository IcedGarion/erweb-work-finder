package it.erweb.web.beans;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import it.erweb.web.data.Bando;
import it.erweb.web.services.BandiService;

/**
 * View Bean handling any frontend operation on a Bando
 */
@ManagedBean
@SessionScoped
public class BandiView implements Serializable
{
	@ManagedProperty("#{bandiService}")
	private BandiService bandiService;
	
	private List<Bando> userBans;
	
	/**
	 *  Creates the list of current logged-in user's Bans that needs to be notified 
	 */
	@PostConstruct
    public void init()
	{
        userBans = bandiService.createUserBans();
    }
	
	public void setBandiService(BandiService banServ)
	{
		this.bandiService = banServ;
	}
	
	public BandiService getBandiService()
	{
		return this.bandiService;
	}
	
	public void setuserBans(List<Bando> bList)
	{
		this.userBans = bList;
	}
	
	/**
	 *  Returns to the view the whole list of current user's bans, created in PostConstruct
	 * 
	 * @return	A list of bans
	 */
	public List<Bando> getuserBans()
	{
		//aggiorna la lista (causa magari cambio utente logout)
		init();
		return this.userBans;
	}
}