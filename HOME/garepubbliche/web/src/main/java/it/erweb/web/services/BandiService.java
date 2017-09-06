package it.erweb.web.services;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.erweb.web.data.Bando;
import it.erweb.web.repository.JPAException;
import it.erweb.web.repository.JpaDAO;

@Component
//@ManagedBean(name = "bandiService")
//@ApplicationScoped
public class BandiService
{
	@Autowired
	private JpaDAO jpaDao;
	
	public void setJpaDAO(JpaDAO jpaDao)
	{
		this.jpaDao = jpaDao;
	}
	
	public JpaDAO getJpaDAO()
	{
		return this.jpaDao;
	}
	
	//bisogna ottenere username / cdUtente (in session?)
	public List<Bando> createUserBans()
	{
		List<Bando> ret = new ArrayList<>();
		long cdUtente = 2;
		String query = 
				"SELECT b FROM Bando b, Notifica n " + 
				"WHERE n.id.cdBando = b.cdBando " +
				"AND n.id.cdUtente = '" + cdUtente + "'" +
				"ORDER BY b.dtInserimento";
		
		try
		{
			ret = jpaDao.read(query);
		}
		catch(JPAException e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
}
