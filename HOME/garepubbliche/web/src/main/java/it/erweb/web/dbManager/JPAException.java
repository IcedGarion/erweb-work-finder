package it.erweb.web.dbManager;

/**
 *  Exception representing a problem with database interaction
 */
public class JPAException extends Exception
{
	public JPAException(String msg)
	{
		super(msg);
	}
}
