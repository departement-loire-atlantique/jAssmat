package fr.cg44.plugin.assmat.policyfilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.jalios.jcms.Category;
import com.jalios.jcms.Channel;
import com.jalios.jcms.Data;
import com.jalios.jcms.HttpUtil;
import com.jalios.jcms.Member;
import com.jalios.jcms.Publication;
import com.jalios.jcms.ResourceHelper;
import com.jalios.jcms.Workflow;
import com.jalios.jcms.context.JcmsContext;
import com.jalios.jcms.dbmember.DBMember;
import com.jalios.jcms.policy.BasicPortalPolicyFilter;
import com.jalios.jcms.portlet.DisplayContext;
import com.jalios.jcms.portlet.PortalElement;
import com.jalios.jcms.portlet.PortalInterface;
import com.jalios.jcms.portlet.PortalManager;
import com.jalios.jcms.portlet.PortalManager.DisplayContextParameters;
import com.jalios.util.ServletUtil;
import com.jalios.util.Util;

import fr.cg44.plugin.assmat.managers.ProfilManager;
import generated.InscriptionAM;
import generated.ProfilASSMAT;

/**
 * 
 * @author tlegat
 * 
 */
public class AssmatPortalPolicyFilter extends BasicPortalPolicyFilter {

	private static Logger logger = Logger.getLogger(AssmatPortalPolicyFilter.class);

	private static Channel channel = Channel.getChannel();

	// Catégorie d'accueil du socle
	private String idCategAccueil = channel.getProperty("jcmsplugin.assmatplugin.general.categ.accueil");

	private boolean isSocle = channel.getBooleanProperty("jcmsplugin.assmatplugin.version-site.socle", true);
	
	/**
	 * Redéfini le portail par défaut si le membre n'a pas terminé de remplir son
	 * profil
	 */
	@Override
	public PortalInterface getDefaultPortal(PortalInterface portal, Member mbr) {
	
			ProfilASSMAT profil = ProfilManager.getInstance().getProfilASSMAT(mbr);
	
			String idPortal1colonne = channel.getProperty("jcmsplugin.assmatplugin.socle.portal.colonne");
			
			if (Util.notEmpty(profil)) {
				// On redirige vers le portail 1 colonne (gestion du profil)
				portal = (PortalInterface) channel.getData(idPortal1colonne);
			} 
			// Le membre n'as pas de profil, on le redirige vers l'accueil
		
		return portal;
	}

	/**
	 * Redéfini la catégorie par défaut si le membre n'a pas terminé de remplir
	 * son profil
	 */
	@Override
	public Category getHomeCategory(Category category, Member mbr) {
	
			ProfilASSMAT profil = ProfilManager.getInstance().getProfilASSMAT(mbr);
	
			String idCategGestion = channel.getProperty("jcmsplugin.assmatplugin.socle.categ.gestion");
			String idCategEspacePerso = channel.getProperty("jcmsplugin.assmatplugin.socle.categ.accueil.perso");
	
			if (Util.notEmpty(profil)) {
				// Si le compte n'est pas activé
				if (!profil.getProfilRenseigne()) {
					category = channel.getCategory(idCategGestion);
				} else {
					// Si le compte est activé
					category = channel.getCategory(idCategEspacePerso);
				}
			} else {
				// Si aucun compte
				category = channel.getCategory(idCategAccueil);
			}
		
		return category;
	}

	/**
	 * Redirige le membre vers la page de configuration du profil si son profil
	 * n'est pas actif
	 */
	@Override
	public void checkDisplayContext(DisplayContext displayContext) {
	
			ProfilASSMAT profil = ProfilManager.getInstance().getProfilASSMAT(channel.getCurrentLoggedMember());



			String idCategLog = channel.getProperty("jcmsplugin.assmatplugin.socle.confirm.catLoginRedirect");
			Category categLogin = null;
			if (Util.notEmpty(idCategLog)) {
				categLogin = channel.getCategory(idCategLog);
			}
			if (Util.notEmpty(displayContext.getCurrentCategory())) {
				// Si la catégorie est pas la catégorie gérant la redirection a la
				// connection on ne fait rien
				if (categLogin.equals(displayContext.getCurrentCategory())) {
					return;
				}
			}

			if (Util.notEmpty(profil)) {
				if (!profil.getProfilRenseigne()) {
					// Quand à l'état de profil connecté mais profil non totalement renseigné et que l'on souhaite sur le formulaire de contact isOK!
					String categoryContactFormSocle = "local_56376";
					if(Util.notEmpty(displayContext) && Util.notEmpty(displayContext.getCurrentCategory()) && !categoryContactFormSocle.equals(displayContext.getCurrentCategory().getId())){
						// Portlet JSP de paramétrage du profil
						String idJspParam = channel.getProperty("jcmsplugin.assmatplugin.socle.jsp.parametrage");
						
						if(Util.notEmpty(displayContext.getPortalCategory())){
							displayContext.setPublication(channel.getPublication(idJspParam));
							displayContext.setPortal(PortalManager.getDefaultPortal());
						}
					}
				} 
			}
		
	}
	
	
	  /* Empêche l'affichage d'une inscription AM
	   * Renvoie une erreur 403
	   * 
	   */
	  @Override
	  public void filterDisplayContext(DisplayContextParameters paramDisplayContextParameters) {
		  String id = paramDisplayContextParameters.id;
		  Data data = channel.getData(id);
		  JcmsContext jcmsContext = channel.getCurrentJcmsContext();
		  HttpServletResponse response = channel.getCurrentServletResponse();
		  HttpServletRequest request = channel.getCurrentServletRequest();
		  if (data instanceof InscriptionAM && jcmsContext.isInFrontOffice()) {
			  try {
				response.sendError(403);
			} catch (Exception e) {
				logger.warn("Tentative d'accès à une inscriptionAM (id "+id+")");
			}
		  }
		  
		 
	  }	
}
