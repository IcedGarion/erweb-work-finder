package it.erweb.web.dbManager.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import it.erweb.web.dbManager.JPAException;
import it.erweb.web.model.AbstractModel;

/**
 * Contains general CRUD methods for JPA and db intraction
 */
@Repository
public class JpaDAORepository
{
	@PersistenceContext
	private EntityManager entityManager;
		
	public <T extends AbstractModel> void create(T obj)
	{	
		try
		{
			this.entityManager.getTransaction().begin();
			this.entityManager.persist(obj);
			this.entityManager.getTransaction().commit();
		}
		catch(Exception e)
		{
			//se non riesce a inserire (magari perche' esiste gia') ignora e potra' ripetere
			this.entityManager.getTransaction().rollback();
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> read(String query) throws JPAException
	{
		List<T> result;
		
		try
		{
			result = this.entityManager.createQuery(query).getResultList();
		}
		catch(Exception e)
		{
			throw new JPAException("Error in query read: " + query + "\n" + e.getMessage());
		}
		
		return result;
	}

	public <T extends AbstractModel> void update(T entity) throws JPAException
	{
		try
		{
			this.entityManager.getTransaction().begin();
			this.entityManager.merge(entity);
			this.entityManager.getTransaction().commit();
		}
		catch(Exception e)
		{
			throw new JPAException("Error in query update\n" + e.getMessage());
		}
		
		return;
	}
	
	public int update(String query) throws JPAException
	{
		int result;
		
		try
		{
			this.entityManager.getTransaction().begin();
			result = this.entityManager.createQuery(query).executeUpdate();
			this.entityManager.getTransaction().commit();
		}
		catch(Exception e)
		{
			throw new JPAException("Error in query update: " + query + "\n" + e.getMessage());
		}
		
		return result;
	}

	public <T extends AbstractModel> void delete(T obj) throws JPAException
	{
		try
		{
			this.entityManager.getTransaction().begin();
			this.entityManager.remove(obj);
			this.entityManager.getTransaction().commit();
		}
		catch(Exception e)
		{
			throw new JPAException("Error in delete:\n" + e.getMessage());
		}
	}	
}
