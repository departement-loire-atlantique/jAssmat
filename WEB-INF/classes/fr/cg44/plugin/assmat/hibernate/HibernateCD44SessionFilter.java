package fr.cg44.plugin.assmat.hibernate;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


import com.jalios.jcms.Channel;
import com.jalios.jcms.servlet.JcmsServletFilter;

/**
 * Classe permettant d'ouvrir une session hibernate sur les table solis
 */
public class HibernateCD44SessionFilter extends JcmsServletFilter implements Filter{


  @Override
  public void doFilter(ServletRequest paramServletRequest, ServletResponse paramServletResponse, FilterChain paramFilterChain) throws IOException, ServletException {
    Channel localChannel = Channel.getChannel();
    if ((localChannel != null) && (!localChannel.isFailSafeMode())) {
        HibernateCD44Util.beginTransaction();
    }
    paramFilterChain.doFilter(paramServletRequest, paramServletResponse);
    HibernateCD44Util.closeSession();
  }
  

  @Override
  public void init(FilterConfig arg0) throws ServletException {
  }
  
  
  @Override
  public void destroy() {
  }

}
