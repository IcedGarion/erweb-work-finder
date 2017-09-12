package it.erweb.web.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.erweb.web.data.AbstractModel;

/**
 * Contains general CRUD methods for JPA and db intraction
 */
@Component
public class JpaDao	implements Serializable
{
	@PersistenceContext
	private EntityManager entityManager;
		
	/**
	 * Inserts into the database the specified generic entity
	 * 
	 * @param entity  the new entity to be created
	 */
	@Transactional
	public <T extends AbstractModel> void create(T entity)
	{	
		try
		{
			this.entityManager.persist(entity);
		}
		catch(PersistenceException e)
		{
			//detatched entity: serve merge
			this.entityManager.merge(entity);
		}
	}

	/**
	 *  Performs a custom read query (SELECT)
	 * 
	 * @param query		the custom query to be executed
	 * @return			a list containing the the query result
	 * @throws JpaException
	 */
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

	/**
	 *  Updates the specified entity: tries to find it in the database and overwrites it
	 * 
	 * @param entity		the entity to be updated
	 * @throws JpaException
	 */
	@Transactional
	public <T extends AbstractModel> void update(T entity) throws JPAException
	{
		try
		{
			this.entityManager.merge(entity);
		}
		catch(Exception e)
		{
			throw new JPAException("Error in query update\n" + e.getMessage());
		}
		
		return;
	}
	
	/**
	 * Updates the database executing the query UPDATE specified
	 * 
	 * @param query		the update query to be executed
	 * @return			number of rows modified
	 * @throws JpaException
	 */
	@Transactional
	public int update(String query) throws JPAException
	{
		int result;
		
		try
		{
			result = this.entityManager.createQuery(query).executeUpdate();
		}
		catch(Exception e)
		{
			throw new JPAException("Error in query update: " + query + "\n" + e.getMessage());
		}
		
		return result;
	}
}