package it.erweb.web.dbManager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.erweb.web.model.Utente;

@Component
public class UtenteService
{
	@PersistenceContext
	private EntityManager em;
	
	public EntityManager getEm()
	{
		return em;
	}

	public void setEm(EntityManager em)
	{
		this.em = em;
	}
	
	@Transactional
	public void createUtente(Utente usr)
	{
		this.em.persist(usr);
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
		
		result = this.em.createQuery("SELECT u FROM Utente u").getResultList();

		return result;
	}
}
