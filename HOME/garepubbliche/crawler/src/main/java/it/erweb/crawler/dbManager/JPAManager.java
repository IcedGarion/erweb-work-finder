package it.erweb.crawler.dbManager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import it.erweb.crawler.model.AbstractModel;

/**
 * Contains general CRUD methods for JPA and db intraction
 */
public abstract class JPAManager<T>
{
	protected static final String PERSISTANCE_UNIT_NAME = "garepubbliche-crawler";
	protected static EntityManagerFactory entityManagerFactory;
	protected static EntityManager entityManager;
	
	//join
	//result = db.read("select u.username, e.expplus from Utente u join Expreg e on u.cdUtente = e.utente");
	//reads: ritorna un elemento lista per ogni riga; ogni elemento è array Obj: una cella per colonna
	
	public static void init() throws JPAException
	{
		try
		{
			entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTANCE_UNIT_NAME);
			entityManager = entityManagerFactory.createEntityManager();
		}
		catch(Exception e)
		{
			throw new JPAException("No database connection!");
		}
	}
	
	public static <T extends AbstractModel> void create(T obj)
	{	
		try
		{
			entityManager.getTransaction().begin();
			entityManager.persist(obj);
			entityManager.getTransaction().commit();
		}
		catch(Exception e)
		{
			//se non riesce a inserire (magari perche' esiste gia') ignora e potra' ripetere
			entityManager.getTransaction().rollback();
		}
	}

	public static <T> List<T> read(String query) throws JPAException
	{
		List<T> result;
		
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

	public static <T extends AbstractModel> void update(T entity) throws JPAException
	{
		try
		{
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
		}
		catch(Exception e)
		{
			throw new JPAException("Error in query update\n" + e.getMessage());
		}
		
		return;
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

	public static <T extends AbstractModel> void delete(T obj) throws JPAException
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
