package it.erweb.web.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionManager
{
	public static Object getSessionUser()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
		
		return session.getAttribute("cdUtente");
	}
	
	public static Object getSessionUser(HttpServletRequest request)
	{
		HttpSession session = (HttpSession) request.getSession(true);
		
		return session.getAttribute("cdUtente");
	}
	
	public static void removeSessionUser()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
		
		session.removeAttribute("cdUtente");
	}

	public static void setSessionUser(long cdUtente, String username)
	{
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);

		session.setAttribute("cdUtente", cdUtente);
		session.setAttribute("username", username);
	}
}
