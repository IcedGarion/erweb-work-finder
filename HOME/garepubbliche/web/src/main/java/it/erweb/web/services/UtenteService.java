package it.erweb.web.services;

import java.util.List;

import javax.faces.bean.ManagedProperty;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.erweb.web.model.Utente;
import it.erweb.web.repository.JPAException;
import it.erweb.web.repository.JpaDAO;

/**
 *  Backend Service for Operations on Utente
 */
@Component
public class UtenteService
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
	
	public void createUtente(Utente usr)
	{
		//System.out.println("JPADAO:\n" + jpaDao);
		//this.em.persist(usr);
		try
		{
			jpaDao.<Utente>create(usr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 	Returns a list of all users stored in the database
	 * 
	 * @return	a list of users
	 * @throws JPAException
	 */
	public List<Utente> getAllUsers() throws JPAException
	{
		List<Utente> result;
		
		result = jpaDao.<Utente>read("SELECT u FROM Utente u");

		return result;
	}
}
