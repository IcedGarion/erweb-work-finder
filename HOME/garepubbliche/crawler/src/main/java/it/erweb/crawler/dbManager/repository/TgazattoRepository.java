package it.erweb.crawler.dbManager.repository;

import it.erweb.crawler.dbManager.JPAManager;
import it.erweb.crawler.model.Tgazatto;

public class TgazattoRepository extends JPAManager
{
	public static void updateWeka(Tgazatto t, String newObj)
	{
		Tgazatto banDb = entityManager.find(Tgazatto.class, t.getId());
		 
		entityManager.getTransaction().begin();
		banDb.setOggettoWeka(newObj);
		entityManager.getTransaction().commit();
	}
}
