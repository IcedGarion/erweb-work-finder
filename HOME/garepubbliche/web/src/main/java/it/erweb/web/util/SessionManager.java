package it.erweb.web.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Utility class for managing session attributes
 */
public class SessionManager
{
	private static final String USER_ID_ATTRIBUTE = "cdUtente";
	private static final String USER_NAME_ATTRIBUTE = "username";
	private static final String LOGIN_MESSAGE_ATTRIBUTE = "loginMessage";
	
	/**
	 * Gets the current user's ID (cdUtente) stored in session 
	 * 
	 * @return	Object representing user ID, to be casted
	 */
	public static Object getSessionUser()
	{
		return getFacesSession().getAttribute(USER_ID_ATTRIBUTE);
	}
	
	/**
	 * Gets the current user's ID (cdUtente) stored in session: this overload needs current HttpServletRequest
	 * 
	 * @param request	current HttpServletRequest containing also the HttpSession
	 * @return			Object representing user ID, to be casted	
	 */
	public static Object getSessionUser(HttpServletRequest request)
	{
		//usato dal filtro (-> no facesContext)		
		return request.getSession(true).getAttribute(USER_ID_ATTRIBUTE);
	}
	
	/**
	 * 	Removes the current user's ID attribute from the session: logout action
	 */
	public static void removeSessionUser()
	{
		getFacesSession().removeAttribute(USER_ID_ATTRIBUTE);
	}

	/**
	 * Stores in session the just-logged-in user's ID and Username
	 * 
	 * @param cdUtente		User's ID to be stored in session	
	 * @param username		User's username to be stored in session
	 */
	public static void setSessionUser(long cdUtente, String username)
	{
		HttpSession session = getFacesSession();
		session.setAttribute(USER_ID_ATTRIBUTE, cdUtente);
		session.setAttribute(USER_NAME_ATTRIBUTE, username);
	}
	
	/**
	 *  Saves a message in session (login error message). This overload requires current HttpServletRequest
	 * 
	 * @param request	current HttpServletRequest
	 * @param message	the message to be stored in session
	 */
	public static void setLoginMessage(HttpServletRequest request, String message)
	{
		//usato dal filtro (-> no facesContext)
		request.getSession(true).setAttribute(LOGIN_MESSAGE_ATTRIBUTE, message);
	}
	
	/**
	 * Retrieves the previously saved message from session (login error message)
	 * 
	 * @return	An object representing a message, to be casted
	 */
	public static Object getLoginMessage()
	{
		return getFacesSession().getAttribute(LOGIN_MESSAGE_ATTRIBUTE);
	}
	
	/**
	 * Deletes the message stored in session (login error message)
	 */
	public static void removeLoginMessage()
	{
		getFacesSession().removeAttribute(LOGIN_MESSAGE_ATTRIBUTE);
	}
	
	//raccoglie le funzioni e crea la session
	private static HttpSession getFacesSession()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		return (HttpSession) context.getExternalContext().getSession(true);
	}
}
