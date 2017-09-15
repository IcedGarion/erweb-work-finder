package it.erweb.web.configurations;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class Initializer implements ApplicationContextAware
{
	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException
	{
		Properties prop = new Properties();

		EntityManagerFactory emFactory = (EntityManagerFactory) arg0.getBean("entityManagerFactory");
		prop.setProperty("hibernate.connection.username", "erweb");
		prop.setProperty("hibernate.connection.password", "garepubbliche");
		prop.setProperty("hibernate.connection.url", "jdbc:mysql://localhost/erweb");
		emFactory = Persistence.createEntityManagerFactory("garepubbliche-web", prop);
	}
}
