<%@page import="fr.cg44.plugin.assmat.util.DemarcheUtil"%>
<%@page import="io.swagger.client.ApiException"%>
<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@page import="fr.cg44.plugin.assmat.selector.RelaisMamSelectorIDSolis"%>
<%@page import="fr.cg44.plugin.assmat.comparator.DeclarationAccueilDateModifComparator"%>
<%@page import="fr.cg44.plugin.assmat.comparator.DeclarationAccueilDateFinComparator"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.administrabletexteplugin.tag.TrsbGlp"%>
<%@page import="org.joda.time.format.DateTimeFormat"%>
<%@page import="org.joda.time.format.DateTimeFormatter"%>
<%@page import="io.swagger.client.model.AccueilDTO"%>
<%@page import="io.swagger.client.model.DeclarationAccueilDTO"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@ include file='/jcore/doInitPage.jsp' %>
<%@ include file='/jcore/portal/doPortletParams.jsp' %>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>

<%
PortletJsp box = (PortletJsp) portlet;
%>
<%
ProfilManager profilMngr = ProfilManager.getInstance();
ProfilASSMAT profil = (ProfilASSMAT) profilMngr.getProfilASSMAT(channel.getCurrentLoggedMember());

if(Util.isEmpty(profil)){
  sendForbidden(request, response);
  return;
}

SolisManager solisMgr = SolisManager.getInstance();
AssmatSolis assmatSolis = Util.getFirst(solisMgr.getAssmatSolisByNumAgrement(profil.getNum_agrement()));

// 0011633: Déclaration d'accueil : activation du pilote 
Boolean isGroupAuthorized = false;

String groupAuthorizedId = channel.getProperty("plugin.assmatplugin.declaration.pilote.group.id");
if(Util.notEmpty(groupAuthorizedId)) {
  Group groupAuthorized = channel.getGroup(groupAuthorizedId) ;
  if(groupAuthorized != null && loggedMember.belongsToGroup(groupAuthorized) ) {
    isGroupAuthorized = true;
  }
}

%>



<%

// Lien pour la déclaration d'accueil (terminer une déclaration ou modifier une déclaration)
Publication declarationAccueilPortlet = channel.getPublication(channel.getProperty("plugin.assmatplugin.portlet.declararer-accueil.id"));
String lienDeclarationAccueil = "";
if(declarationAccueilPortlet != null) {
  lienDeclarationAccueil = declarationAccueilPortlet.getDisplayUrl(userLocale);
}

String lienRetourModal = currentCategory.getDisplayUrl(userLocale);

//Lien pour la déclaration de fin d'accueil
Publication declarationFinAccueilPortlet = channel.getPublication(channel.getProperty("plugin.assmatplugin.portlet.declararer-fin-accueil.id"));
String lienDeclarationFinAccueil = "";
if(declarationFinAccueilPortlet != null) {
  lienDeclarationFinAccueil = declarationFinAccueilPortlet.getDisplayUrl(userLocale);
}

// Format d'affichage de la fate
DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("dd/MM/YYYY");

// Service swarger pour répérer les déclarations
Set<AccueilDTO> declarationBrouillonList = null; 
Set<AccueilDTO> declarationEnCoursList = null;
Set<AccueilDTO> declarationHistoriqueList = null;
boolean isSwaggerOk = true;
try {   
    
    //liste des declaration à l'état brouillon
    declarationBrouillonList = new TreeSet<AccueilDTO>(new DeclarationAccueilDateModifComparator());
    declarationBrouillonList.addAll(DemarcheUtil.getListAccueils(profil.getNum_agrement(), "brouillon"));
    
    //liste des declaration à l'état en cours
    declarationEnCoursList = new TreeSet<AccueilDTO>(new DeclarationAccueilDateModifComparator());
    declarationEnCoursList.addAll(DemarcheUtil.getListAccueils(profil.getNum_agrement(), "en cours"));
    
    //Liste des declaration sur un an pour l'historique
    declarationHistoriqueList = new TreeSet<AccueilDTO>(new DeclarationAccueilDateFinComparator());
    declarationHistoriqueList.addAll(DemarcheUtil.getListAccueils(profil.getNum_agrement(), "historique"));
    Set<AccueilDTO> declarationHistoriqueRemoveList = new TreeSet<AccueilDTO>(new DeclarationAccueilDateFinComparator());
    // les accueils dont la date de fin d’accueil date de moins d’un an et uniquement celles saisies via le site internet
    if(Util.notEmpty(declarationHistoriqueList)) {
      
      GregorianCalendar calAn = new GregorianCalendar(); // Création d'un nouveau calendrier
      calAn.setTime(new Date()); // Initialisation du calendrier avec la date du jour
      calAn.add(GregorianCalendar.YEAR, -1); // On retranche 1 année
      
        for(AccueilDTO itAccueil : declarationHistoriqueList) {
          if(itAccueil.getDateFinAccueil().toDate().before(calAn.getTime())) {
            declarationHistoriqueRemoveList.add(itAccueil);
          }
        }
        declarationHistoriqueList.removeAll(declarationHistoriqueRemoveList);
  }
    
} catch(ApiException e) {
  logger.warn("Web service swagger indisponible", e);
  isSwaggerOk = false;
}

%>



  
  
  <!-- Corps de la page -->
 
      
      
         <!-- Texte introductif  -->
         <p class="ds44-introduction"><trsb:glp key="ASS-MACC-INTRO-HTML"/></p>
         
         <jalios:if predicate="<%= isGroupAuthorized && isSwaggerOk %>">
         
               <%-- 2.3.2.2  Bloc des démarches enregistrées en brouillon --%>  
               <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/demarches/declarerAccueil/accueilsBrouillon.jspf'%>
               
               <%-- 2.3.2.3 Bloc des accueils en cours --%>
               <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/demarches/declarerAccueil/accueilsEnCours.jspf'%>
               
               <%-- 4 Historique des accueils --%>
               <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/demarches/declarerAccueil/accueilsHistorique.jspf'%>
               
         </jalios:if>   
                
         <%-- BLOCK SWAGGER KO --%>
         
         <jalios:if predicate="<%= !isSwaggerOk %>">
           <p><trsb:glp key="SWAGGER-RESP-ERR" /></p>
         </jalios:if>

     
 
  <!-- FIN corps de la page -->
    



