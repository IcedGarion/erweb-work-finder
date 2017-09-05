package it.erweb.web.beans;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import it.erweb.web.model.Bando;
import it.erweb.web.services.BandiService;

@ManagedBean
@SessionScoped
public class ListBandi
{
	@ManagedProperty("#{bandiService}")
	private BandiService bandiService;
	
	public List<Bando> banList;
	
	public void setBandiService(BandiService banServ)
	{
		this.bandiService = banServ;
	}
	
	public BandiService getBandiService()
	{
		return this.bandiService;
	}
	
	public void setBanList(List<Bando> bList)
	{
		this.banList = bList;
	}
	
	public List<Bando> getBanList()
	{
		return this.banList;
	}
	
	//getUsersBans
}
