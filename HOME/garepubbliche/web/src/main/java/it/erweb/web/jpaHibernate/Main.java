package it.erweb.web.jpaHibernate;

import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import it.erweb.web.dbManager.JPAException;
import it.erweb.web.dbManager.services.UtenteManager;
import it.erweb.web.model.Utente;

public class Main
{
	public static void main(String[] args) throws JPAException
	{
		ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring.xml");
/*
		UtenteManager userManager = (UtenteManager) context.getBean("utenteManager");

		List<Utente> list = userManager.getAllUsers();

		Utente user = new Utente();
		user.setUsername("springProva");
		user.setPassword("spring");
		user.setDtNotifica(new Date());
		user.setEmail("spring@prova.com");
		userManager.createUtente(user);
		System.out.println("User inserted!");

		list = userManager.getAllUsers();
*/
	}
}
