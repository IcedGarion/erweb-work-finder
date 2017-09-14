package it.erweb.web.beans;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import it.erweb.web.data.Bando;
import it.erweb.web.services.BandiService;

/**
 * View Bean handling any frontend operation on a Bando
 */
@ManagedBean
@SessionScoped
public class BandiView implements Serializable
{
	public static final long serialVersionUID = -926624460350620171L;
	
	@ManagedProperty("#{bandiService}")
	private BandiService bandiService;
	
	private List<Bando> userBans;
	
	private String filter = "new";
	
	/**
	 *  Creates the list of current logged-in user's Bans that needs to be notified;
	 *  By default, only the last bans are loaded
	 */
    public void init()
	{
        userBans = bandiService.createUserBans(filter);
    }
	
	/**
	 *  Re-creates the list of current user's bans, after filter form submit
	 *  AJAX
	 * 
	 * @param actionEvent
	 */
	public void init(ActionEvent actionEvent)
	{
		init();
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
	
	public void setFilter(String filter)
	{
		this.filter = filter;
	}
	
	public String getFilter()
	{
		return this.filter;
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