package it.erweb.crawler.dbManager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import it.erweb.crawler.model.AbstractModel;

/**
 * Contains general CRUD methods for JPA and db intraction
 */
public abstract class JPAManager
{
	protected static final String PERSISTANCE_UNIT_NAME = "garepubbliche-crawler";
	protected static EntityManagerFactory entityManagerFactory;
	protected static EntityManager entityManager;
	
	//join
	//result = db.read("select u.username, e.expplus from Utente u join Expreg e on u.cdUtente = e.utente");
	
	//reads: ritorna un elemento lista per ogni riga; ogni elemento è array Obj: una cella per colonna
	public static void init()
	{
		entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTANCE_UNIT_NAME);
		entityManager = entityManagerFactory.createEntityManager();
	}
	
	public static void create(AbstractModel obj)
	{	
		entityManager.getTransaction().begin();
		entityManager.persist(obj);
		entityManager.getTransaction().commit();
	}

	public static List<Object> read(String query) throws JPAException
	{
		List<Object> result;
		
		try
		{
			result = entityManager.createQuery(query).getResultList();
		}
		catch(Exception e)
		{
			throw new JPAException("Error in query read: " + query + "\n" + e.getMessage());
		}
		
		return result;
	}

	public static int update(String query) throws JPAException
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
			throw new JPAException("Error in query update: " + query + "\n" + e.getMessage());
		}
		
		return result;
	}

	public static void delete(AbstractModel obj) throws JPAException
	{
		try
		{
			entityManager.getTransaction().begin();
			entityManager.remove(obj);
			entityManager.getTransaction().commit();
		}
		catch(Exception e)
		{
			throw new JPAException("Error in delete:\n" + e.getMessage());
		}
	}	
	
	public static void close()
	{
		entityManager.close();
		entityManagerFactory.close();
	}
}
