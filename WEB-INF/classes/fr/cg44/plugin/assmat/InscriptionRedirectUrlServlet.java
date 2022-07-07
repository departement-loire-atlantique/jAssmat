package fr.cg44.plugin.assmat;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.jalios.jcms.Channel;
import com.jalios.jcms.Publication;
import com.jalios.util.Util;

/**
 * Servlet permettant de faire la redirection a la maniere des URL intuitives mais en transmettant les parametres
 * 
 * @author c.rabiller
 *
 */
@SuppressWarnings("serial")
public class InscriptionRedirectUrlServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(InscriptionRedirectUrlServlet.class);
	private Channel channel = Channel.getChannel();
	/**
	 * la catégorie vers ou rediriger
	 */
	Publication publi ;


	public void init() throws ServletException {
		logger.debug("-- Initialisation Servlet Rewriter --");
		String idJSP= channel.getProperty("jcmsplugin.assmatplugin.socle.jsp.inscription");
		this.publi = channel.getPublication(idJSP); 

		super.init();
	}

	/**
	 * La méthode doGet gère la redirection vers la catégorie en gardant les parametres present dans la requete
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.debug("-- DoGet Servlet Rewriter --");

		String urlCateg =channel.getUrl();
		//On recupere l'url de la categorie
		if(Util.notEmpty(publi)){
			urlCateg= publi.getDisplayUrl(channel.getLocale());

			//On recupere les parametres
			Enumeration<String> paramEnum= req.getParameterNames();

			//On les ajoute a l'url
			if(Util.notEmpty(paramEnum)){
				List<String> paramList = Collections.list(paramEnum);

				int iterator=0;
				for(String itParam :paramList){
					if(iterator==0){
						urlCateg=urlCateg+"?"+itParam+"="+req.getParameter(itParam);
					}else{
						urlCateg=urlCateg+"&"+itParam+"="+req.getParameter(itParam);
					} 
					iterator++;
				}
			}
		}
		resp.sendRedirect(urlCateg);

	}






}
