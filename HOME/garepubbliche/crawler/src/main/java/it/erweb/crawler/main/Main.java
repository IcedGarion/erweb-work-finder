package it.erweb.crawler.main;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import it.erweb.crawler.configurations.PropertiesManager;
import it.erweb.crawler.dbManager.JPAException;
import it.erweb.crawler.dbManager.repository.ExpregRepository;
import it.erweb.crawler.dbManager.repository.UtenteRepository;
import it.erweb.crawler.httpClientUtil.HttpGetter;
import it.erweb.crawler.model.*;

public class Main
{
	public static void main(String[] args) throws JPAException
	{
		Properties prop;
		UtenteRepository udb = new UtenteRepository();
		ExpregRepository edb = new ExpregRepository();
		List<Object> result = null;
		Utente userProva = new Utente();
		userProva.setDtNotifica(new Date());
		userProva.setUsername("a");
		String html = "", homeURL = "http://www.gazzettaufficiale.it/";
		

		PropertiesManager.setProperty("prova", "123");
		prop = PropertiesManager.loadProperties();
		System.out.println(prop.getProperty("prova"));

		
		/*
		try
		{
			html = HttpGetter.get(homeURL);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println(html);
		
		*/
		return;
	}
}
