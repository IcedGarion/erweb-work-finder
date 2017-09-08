package it.erweb.crawler.database.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import it.erweb.crawler.database.model.AbstractModel;

/**
 * Contains general CRUD methods for JPA and db intraction
 */
public class JpaDao<T>
{
	protected static final String PERSISTANCE_UNIT_NAME = "garepubbliche-crawler";
	protected static EntityManagerFactory entityManagerFactory;
	protected static EntityManager entityManager;

	/**
	 *  Initialize the class, creating the entityManager
	 * 
	 * @throws JpaException
	 */
	public static void init() throws JpaException
	{
		try
		{
			entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTANCE_UNIT_NAME);
			entityManager = entityManagerFactory.createEntityManager();
		}
		catch(Exception e)
		{
			throw new JpaException("No database connection!");
		}
	}
		
	/**
	 * Inserts into the database the specified generic entity
	 * 
	 * @param entity  the new entity to be created
	 */
	protected static <T extends AbstractModel> void create(T entity)
	{	
		try
		{
			entityManager.getTransaction().begin();
			entityManager.persist(entity);
			entityManager.getTransaction().commit();
		}
		catch(Exception e)
		{
			//se non riesce a inserire (magari perche' esiste gia') ignora e potra' ripetere
			entityManager.getTransaction().rollback();
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
	protected static <T> List<T> read(String query) throws JpaException
	{
		List<T> result;
		
		try
		{
			result = entityManager.createQuery(query).getResultList();
		}
		catch(Exception e)
		{
			throw new JpaException("Error in query read: " + query + "\n" + e.getMessage());
		}
		
		return result;
	}

	/**
	 *  Updates the specified entity: tries to find it in the database and overwrites it
	 * 
	 * @param entity		the entity to be updated
	 * @throws JpaException
	 */
	protected static <T extends AbstractModel> void update(T entity) throws JpaException
	{
		try
		{
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
		}
		catch(Exception e)
		{
			throw new JpaException("Error in query update\n" + e.getMessage());
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
	protected static int update(String query) throws JpaException
	{
		int result;
		
		try
		{
			entityManager.getTransaction().begin();
			result = entityManager.createQuery(query).executeUpdate();
			entityManager.getTransaction().commit();
		}
		catch(Exception e)
		{
			throw new JpaException("Error in query update: " + query + "\n" + e.getMessage());
		}
		
		return result;
	}

	/**
	 * Searches the database for the specified entity and removes it
	 * 
	 * @param entity			the entity to be removed
	 * @throws JpaException
	 */
	protected static <T extends AbstractModel> void delete(T entity) throws JpaException
	{
		try
		{
			entityManager.getTransaction().begin();
			entityManager.remove(entity);
			entityManager.getTransaction().commit();
		}
		catch(Exception e)
		{
			throw new JpaException("Error in delete:\n" + e.getMessage());
		}
	}	
	
	public static void close()
	{
		entityManager.close();
		entityManagerFactory.close();
	}
}
