<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%@ taglib prefix="ds" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@page import="fr.cg44.plugin.assmat.util.DemarcheUtil"%>
<%@page import="io.swagger.client.ApiException"%>
<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@page import="fr.cg44.plugin.assmat.selector.RelaisMamSelectorIDSolis"%>
<%@page import="fr.cg44.plugin.assmat.comparator.DeclarationAccueilDateModifComparator"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.administrabletexteplugin.tag.TrsbGlp"%>
<%@page import="org.joda.time.format.DateTimeFormat"%>
<%@page import="org.joda.time.format.DateTimeFormatter"%>
<%@page import="io.swagger.client.model.AccueilDTO"%>
<%@page import="io.swagger.client.model.DeclarationAccueilDTO"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@ include file='/jcore/doInitPage.jsp' %>
<%@ include file='/jcore/portal/doPortletParams.jsp' %>

<%
PortletJsp box = (PortletJsp) portlet;

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

// Lien pour la déclaration d'accueil (terminer une déclaration ou modifier une déclaration)
Publication declarationAccueilPortlet = channel.getPublication(channel.getProperty("plugin.assmatplugin.portlet.declararer-accueil.id"));
String lienDeclarationAccueil = "";
if(declarationAccueilPortlet != null) {
  lienDeclarationAccueil = declarationAccueilPortlet.getDisplayUrl(userLocale);
}

Publication AccueilEspacePersoPortlet = channel.getPublication(channel.getProperty("jcmsplugin.assmatplugin.socle.portail.param.id"));
String lienRetourModal = AccueilEspacePersoPortlet.getDisplayUrl(userLocale);

//Lien pour la déclaration de fin d'accueil
Publication declarationFinAccueilPortlet = channel.getPublication(channel.getProperty("plugin.assmatplugin.portlet.declararer-fin-accueil.id"));
String lienDeclarationFinAccueil = "";
if(declarationFinAccueilPortlet != null) {
  lienDeclarationFinAccueil = declarationFinAccueilPortlet.getDisplayUrl(userLocale);
}

// Format d'affichage de la fate
DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("dd/MM/YYYY");
SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

// Service swarger pour répérer les déclarations
Set<AccueilDTO> declarationBrouillonList = null; 
Set<AccueilDTO> declarationEnCoursList = null;
boolean isSwaggerOk = true;

try {	
	//liste des declaration à l'état brouillon
	declarationBrouillonList = new TreeSet<AccueilDTO>(new DeclarationAccueilDateModifComparator());
	declarationBrouillonList.addAll(DemarcheUtil.getListAccueils(profil.getNum_agrement(), "brouillon"));
	
	//liste des declaration à l'état en cours
	declarationEnCoursList = new TreeSet<AccueilDTO>(new DeclarationAccueilDateModifComparator());
	declarationEnCoursList.addAll(DemarcheUtil.getListAccueils(profil.getNum_agrement(), "en cours"));
} catch(ApiException e) {
  logger.warn("Web service swagger indisponible", e);
  isSwaggerOk = false;
}

%>
  
<%-- chapo --%>
<p class="ds44-introduction"><trsb:glp key="ACCUEIL-PROFIL-INTRO"></trsb:glp></p>
	     
<%-- zone réservée à l’affichage d’une information spécifique --%>
<ds:box type="alerte" boxClasses="ds44-mb3">
    <p><trsb:glp key="ACCUEIL-PROFIL_HTML"></trsb:glp></p>
</ds:box>

<%-- 3.3.2.2 Date de renouvellement de l'agrément -  --%>
<% 
Boolean hasRenouvellementDom = false;
Date agrementRenouvellementDomDate = assmatSolis.getDateProchainRenouvellement();

if(Util.notEmpty(agrementRenouvellementDomDate) && Util.notEmpty(assmatSolis.getExerceDomicile()) && assmatSolis.getExerceDomicile()) {
  // Ajoute 2 mois à la date de renouvellement
  GregorianCalendar calendar = new GregorianCalendar();
  calendar.setTime(new Date());
  calendar.add(Calendar.MONTH, 2);
  if(calendar.getTime().after(agrementRenouvellementDomDate)) {
    hasRenouvellementDom = true;
    }
}
    
Boolean hasRenouvellementMam = false;
Date agrementRenouvellementMamDate = assmatSolis.getDateProchainRenouvellementMam(); 

if(Util.notEmpty(agrementRenouvellementMamDate) && Util.notEmpty(assmatSolis.getExerceMam()) && assmatSolis.getExerceMam()) {
  // Ajoute 2 mois à la date de renouvellement
  GregorianCalendar calendar = new GregorianCalendar();
  calendar.setTime(new Date());
  calendar.add(Calendar.MONTH, 2);
  
  if(calendar.getTime().after(agrementRenouvellementMamDate)) {
    hasRenouvellementMam = true;
    }
}
%>

<%-- Affichage d'une alerte si agrément arrive bientôt à échéance --%>
<%-- Renouvellement Domicile --%>
<jalios:if predicate="<%= hasRenouvellementDom %>">
    <% String agrementRenouvellementDomDateString = simpleDateFormat.format(agrementRenouvellementDomDate); %>
    <c:set var="date" value="<%= new String[]{agrementRenouvellementDomDateString} %>" scope="request" />
    <ds:box type="bordure" boxClasses="mbm">
        <p><trsb:glp key="ASS-ACC-ME-PER-DOM-HTML" parameter="${date}"></trsb:glp></p>
    </ds:box>		     
</jalios:if>

<%-- Renouvellement MAM --%>
<jalios:if predicate="<%= hasRenouvellementMam %>">
    <% String agrementRenouvellementMamDateString = simpleDateFormat.format(agrementRenouvellementMamDate); %>
    <c:set var="date" value="<%=new String[]{agrementRenouvellementMamDateString}%>" scope="request" /> 
    <ds:box type="bordure">
        <p><trsb:glp key="ASS-ACC-ME-PER-MAM-HTML" parameter="${date}"></trsb:glp></p>
    </ds:box>
</jalios:if>

<%-- Message d'alerte si swagger KO (pas de communication avec le webservice de l'appli "Démarches Assmat" --%>
<jalios:if predicate="<%= !isSwaggerOk %>">
    <ds:box type="alerte" boxClasses="ds44-mb3">
        <p><trsb:glp key="SWAGGER-RESP-ERR" /></p>
    </ds:box>
</jalios:if> 

<%-- Affichage des déclarations d'accueil --%>
<jalios:if predicate="<%= isGroupAuthorized && isSwaggerOk %>">
    <%-- 2.3.2.2  Bloc des démarches enregistrées en brouillon --%>  
    <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/demarches/declarerAccueil/accueilsBrouillon.jspf'%>
         
    <%-- 2.3.2.3 Bloc des accueils en cours --%>
    <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/demarches/declarerAccueil/accueilsEnCours.jspf'%>
</jalios:if>
	     
	     
<%-- Bloc "Mes interlocuteurs" --%>
<%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/accueilProfil_Interlocuteurs.jspf'%>