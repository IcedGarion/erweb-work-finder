package it.erweb.crawler.main;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import it.erweb.crawler.model.*;

public class Main
{
	public static void main(String[] args)
	{
		//crea entity manager factory jpa usando persistence unit name (persistence.xml)
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("garepubbliche-crawler");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Utente userProva = new Utente();
		userProva.setDtNotifica(new Date());
		userProva.setUsername("JPA prova");
		userProva.setPassword("1234");
		userProva.setEmail("jpa@prova.it");
		
		//insert new Utente
		//entityManager.getTransaction().begin();
		//entityManager.persist(userProva);
		//entityManager.getTransaction().commit();
		//entityManager.close();
		
		//entityManager.getTransaction().begin();
		List<Utente> result = entityManager.createQuery( "from Utente", Utente.class ).getResultList();
		for ( Utente usr : result )
		    System.out.println(usr.getCdUtente() + ", " + usr.getUsername() + ", " + usr.getPassword() + ", "
		    		 + usr.getEmail() + ", " + usr.getDtNotifica());
		//entityManager.getTransaction().commit();
		entityManager.close();
	}
}
