//package it.erweb.crawler.httpClientUtil;
//
//import it.erweb.crawler.model.Bando;
//import it.erweb.crawler.model.Utente;
//
///**
// * Functions to send notifications via HTTP messages
// */
//public class Notifier
//{
//	/**
//	 * Notifies the developers something has gone wrong
//	 * 
//	 * @param msg	the message to send (e.g. an exception message)
//	 * @throws Exception 
//	 */
//	public static void notifyDev(String msg)
//	{
//		try
//		{
//			throw new Exception("Not yet implemented");
//		}
//		catch(Exception e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 *	Temporary: once a ban has matched with a user's expregs, notifies the user in some way (Prints for now)
//	 *
//	 * @param usr	the user that wants to be notified
//	 * @param ban	the ban to be notified
//	 * @throws Exception
//	 */
//	public static void notifyUser(Utente usr, Bando ban) throws Exception
//	{
//		System.out.println("\nUtente: " + usr.getUsername() + " match con bando : " + ban.getCdBando());
//	}
//}
