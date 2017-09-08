package it.erweb.web.beans;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import it.erweb.web.data.Bando;
import it.erweb.web.services.BandiService;

@ManagedBean
@SessionScoped
public class BandiView implements Serializable
{
	@ManagedProperty("#{bandiService}")
	private BandiService bandiService;
	
	private List<Bando> userBans;
	
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
	
	public List<Bando> getuserBans()
	{
		return this.userBans;
	}
	
	//getUsersBans
}
