package it.erweb.web.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionManager
{
	private static final String USER_ID_ATTRIBUTE = "cdUtente";
	private static final String USER_NAME_ATTRIBUTE = "username";
	private static final String LOGIN_MESSAGE_ATTRIBUTE = "loginMessage";
	
	public static Object getSessionUser()
	{
		return getFacesSession().getAttribute(USER_ID_ATTRIBUTE);
	}
	
	public static Object getSessionUser(HttpServletRequest request)
	{
		//usato dal filtro (-> no facesContext)		
		return request.getSession(true).getAttribute(USER_ID_ATTRIBUTE);
	}
	
	public static void removeSessionUser()
	{
		getFacesSession().removeAttribute(USER_ID_ATTRIBUTE);
	}

	public static void setSessionUser(long cdUtente, String username)
	{
		HttpSession session = getFacesSession();
		session.setAttribute(USER_ID_ATTRIBUTE, cdUtente);
		session.setAttribute(USER_NAME_ATTRIBUTE, username);
	}
	
	public static void setLoginMessage(HttpServletRequest request, String message)
	{
		//usato dal filtro (-> no facesContext)
		request.getSession(true).setAttribute(LOGIN_MESSAGE_ATTRIBUTE, message);
	}
	
	public static Object getLoginMessage()
	{
		return getFacesSession().getAttribute(LOGIN_MESSAGE_ATTRIBUTE);
	}
	
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
