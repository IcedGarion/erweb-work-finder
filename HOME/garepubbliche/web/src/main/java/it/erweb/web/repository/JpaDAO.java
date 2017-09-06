package it.erweb.web.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.erweb.web.data.AbstractModel;

/**
 * Contains general CRUD methods for JPA and db intraction
 */
@Component
public class JpaDAO	
{
	@PersistenceContext
	private EntityManager entityManager;
		
	@Transactional
	public <T extends AbstractModel> void create(T obj)
	{	
		this.entityManager.persist(obj);
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

	@Transactional
	public <T extends AbstractModel> void delete(T obj) throws JPAException
	{
		try
		{
			this.entityManager.remove(obj);
		}
		catch(Exception e)
		{
			throw new JPAException("Error in delete:\n" + e.getMessage());
		}
	}	
}