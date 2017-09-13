package it.erweb.web.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.erweb.web.util.SessionManager;

public class UserRoleFilter implements Filter
{    
    @Override
    public void init(FilterConfig cfg) throws ServletException
    { }

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain next) throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) req;
        String path = ((HttpServletRequest) request).getRequestURI();
                
        //esclude dal controllo login, register e "/"
        if(path.matches("^.+?login.xhtml$")
        		|| path.matches("^.+?register.xhtml$")
        		|| path.equals(request.getContextPath() + "/"))
        {
            next.doFilter(request, response);
        }
        else
        {        	
        	//se c'e' cdUtente in session, utente e' loggato e puo' proseguire
        	if(SessionManager.getSessionUser(request) != null)
    		{
        		next.doFilter(req, response);
    		}
    		//altrimenti rimanda alla login
        	else
        	{	
        		//imposta messaggio di "errore"
        		SessionManager.setLoginMessage(request, "Necessaria login per continuare");
        		
        		HttpServletResponse r = (HttpServletResponse) response;
        		r.sendRedirect(request.getContextPath() + "/views/login.xhtml");
        	}
        }
        
        return; 
    }

    @Override
    public void destroy()
    { }
}