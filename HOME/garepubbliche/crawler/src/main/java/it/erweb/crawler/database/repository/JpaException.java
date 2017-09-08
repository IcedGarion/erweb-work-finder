package it.erweb.crawler.database.repository;

/**
 *  Exception representing a problem with database interaction
 */
public class JpaException extends Exception
{
	public JpaException(String msg)
	{
		super(msg);
	}
}
