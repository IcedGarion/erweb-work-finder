package it.erweb.crawler.main;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import it.erweb.crawler.model.*;

public class Main
{
	public static void main(String[] args)
	{
		SessionFactory factory;
		Session session = null;
		Transaction tx = null;
		
		 try
		 {
	         factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
	     }
		 catch(Exception ex)
		 { 
	         System.err.println("Failed to create sessionFactory object : \n" + ex);
	         ex.printStackTrace();
	         return;
		 }

		try
		{
			//session = factory.openSession();
			//tx = session.beginTransaction();

			//crud
			List<Utente> utenti = factory.openSession().createQuery("from Utente u").list();
			for(Utente u : utenti)
				System.out.println("Nome : " + u.getUsername());
		   
			//tx.commit();
		}
		catch (Exception e)
		{
			/*
		   if (tx!=null)
			   tx.rollback();
		   */
		   e.printStackTrace(); 
		}
		/*
		finally
		{
		   session.close();
		}
		*/
	}
}
