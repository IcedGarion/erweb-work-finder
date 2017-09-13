package it.erweb.web.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionManager
{
	private static final String USER_ID_ATTRIBUTE = "cdUtente";
	private static final String USER_NAME_ATTRIBUTE = "username";
	
	public static Object getSessionUser()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
		
		return session.getAttribute(USER_ID_ATTRIBUTE);
	}
	
	public static Object getSessionUser(HttpServletRequest request)
	{
		HttpSession session = (HttpSession) request.getSession(true);
		
		return session.getAttribute(USER_ID_ATTRIBUTE);
	}
	
	public static void removeSessionUser()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
		
		session.removeAttribute(USER_ID_ATTRIBUTE);
	}

	public static void setSessionUser(long cdUtente, String username)
	{
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);

		session.setAttribute(USER_ID_ATTRIBUTE, cdUtente);
		session.setAttribute(USER_NAME_ATTRIBUTE, username);
	}
}
