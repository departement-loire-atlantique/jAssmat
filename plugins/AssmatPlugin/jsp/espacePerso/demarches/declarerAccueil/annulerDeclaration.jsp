<%@page import="fr.cg44.plugin.assmat.util.DemarcheUtil"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@page import="io.swagger.client.model.DeclarationAccueilDTO"%>
<%@page import="io.swagger.client.ApiException"%>
<%@ include file='/jcore/doInitPage.jsp' %>
<%@ page contentType="text/html; charset=UTF-8"%>

<% 

  Integer idDeclaration = Integer.parseInt(request.getParameter("idDeclaration"));
  String redirect = request.getParameter("redirect"); 
  
  String etat = "annule";
  if(Util.notEmpty(request.getParameter("etat"))) {
    etat = request.getParameter("etat");
  }

  ProfilASSMAT profilAM = ProfilManager.getInstance().getProfilASSMAT(loggedMember);
  if(profilAM == null) {
    return;
  }
    

	// Manager du web service	
	
	try {
	  // Récupère la déclaration
	  DeclarationAccueilDTO declaration = DemarcheUtil.getDeclarationAccueilById(idDeclaration);
	  
	  // Vérifie qu'il s'agit bien d'une déclaration de cette assmat
	  if(declaration != null && declaration.getNumDossier() == profilAM.getNum_agrement()){	    
	    // Met la déclaration à l'état annulerpuis la envoie la modif au web service
	    declaration.setEtatDeclaration(etat);
	    DemarcheUtil.modifierDeclarationAccueil(declaration.getIdDeclaration(), declaration);	    
	  }  
    
	} catch (ApiException e) {
	  logger.error(e);
	}
	
	// Redirige sur la page de retour
	sendRedirect(redirect);

%>


