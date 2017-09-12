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
        
        //se non dovesse funzionare: escludi qua l'url di login, e applica come url-pattern "/" (in web.xml)
        if (path.startsWith("login.xhtml"))
        {
            next.doFilter(request, response);
        }
        
        //definire bene cosa vuol dire avere il ruolo (session != null)
        if(true)
        {
        	System.out.println("ruolo ok");
            HttpServletResponse r = (HttpServletResponse) response;
            r.sendRedirect(request.getContextPath() + "/signin.xhtml");
            return;
        }

        next.doFilter(req, response);
    }

    @Override
    public void destroy()
    { }
}