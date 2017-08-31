package it.erweb.web.dbManager.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.erweb.web.dbManager.JPAException;
import it.erweb.web.dbManager.repository.JpaDAORepository;
import it.erweb.web.model.Utente;

@Service
public class UtenteManager
{
	@Autowired
	private JpaDAORepository jpaDao;
	
	@Transactional
	public void createUtente(Utente usr)
	{
		this.jpaDao.<Utente>create(usr);
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
		
		result = this.jpaDao.<Utente>read("SELECT u FROM Utente u");

		return result;
	}
}
