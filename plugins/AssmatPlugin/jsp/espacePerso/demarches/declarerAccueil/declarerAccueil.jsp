<%@page import="fr.cg44.plugin.assmat.util.DemarcheUtil"%>
<%@page import="fr.cg44.plugin.assmat.handler.CreneauHorraire"%>
<%@page import="org.joda.time.format.DateTimeFormat"%>
<%@page import="org.joda.time.format.DateTimeFormatter"%>
<%@page import="io.swagger.client.model.DeclarationAccueilDTO"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file='/jcore/doInitPage.jsp'%>
<%@ include file='/jcore/portal/doPortletParams.jsp'%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%-- Formulaire à étape permettant de déclarer un accueil --%>

<%

  ProfilManager profilMgr  = ProfilManager.getInstance();
  ProfilASSMAT profilAM =  profilMgr.getProfilASSMAT(loggedMember);

	if(Util.isEmpty(profilAM)){
	  sendForbidden(request, response);
	  return;
	}

  PortletJsp box = (PortletJsp) portlet;

  DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("dd/MM/YYYY");

%>
<jsp:useBean id='formHandler' scope='page' class='fr.cg44.plugin.assmat.handler.DeclarerAccueilAssmatHandler'>
  <jsp:setProperty name='formHandler' property='request' value='<%=request%>' />
  <jsp:setProperty name='formHandler' property='response' value='<%=response%>' />
  <jsp:setProperty name='formHandler' property="noRedirect" value="true" />
  <jsp:setProperty name='formHandler' property='*' />
</jsp:useBean>


<%
// si il existe un id de declaration alors récupérer la declaration en session (Si session expirée alors récupérer du web service)
// modification en cours d'un déclaration courante (pas de idDeclaration indique une nouvelle declaration)
if(Util.notEmpty(request.getParameter("idDeclaration"))) {
  if (session.getAttribute("declaration") == null) {
    Integer idDecla = HttpUtil.getIntParameter(request, "idDeclaration", 0);
    DeclarationAccueilDTO declarationExpireSession = DemarcheUtil.getDeclarationAccueilById(idDecla);
    formHandler.setDeclaration(declarationExpireSession);
  }else {
    formHandler.setDeclaration((DeclarationAccueilDTO)session.getAttribute("declaration"));
  }
    
}

%>

<%

  if (formHandler.validate()) {   
    return;
  }
  jcmsContext.addJavaScript("plugins/ToolsPlugin/js/facets/tooltipCategories.js");
  int step = formHandler.getFormStep();
  int stepCount = formHandler.getFormStepCount();

  String token = request.getParameter("token");

  jcmsContext.addJavaScript("plugins/AssmatPlugin/js/plugin.js");
  jcmsContext.addJavaScript("plugins/AssmatPlugin/js/comportement.js");
  
  jcmsContext.addJavaScript("plugins/AgendaPlugin/js/pickadatejs/legacy.js");
  jcmsContext.addJavaScript("plugins/AgendaPlugin/js/pickadatejs/picker.js");
  jcmsContext.addJavaScript("plugins/AgendaPlugin/js/pickadatejs/picker.date.js");
  if("fr".equals(userLang)){
    jcmsContext.addJavaScript("plugins/AgendaPlugin/js/pickadatejs/translations/fr_FR.js");
  }  
  
  SolisManager solisMgr = SolisManager.getInstance();
  AssmatSolis assmatSolis= null;
  if(Util.notEmpty(solisMgr.getAssmatSolisByNumAgrement(profilAM.getNum_agrement()))){
     assmatSolis= Util.getFirst(solisMgr.getAssmatSolisByNumAgrement(profilAM.getNum_agrement()));
  }else{
    sendForbidden(request, response);
  }
  
  
  

  Boolean isModif = Util.notEmpty(request.getParameter("idModifEnCours"));
  
  DeclarationAccueilDTO declarationForm = formHandler.getDeclaration();
  
  Boolean choixAccueilRegulier = declarationForm.getPlanningRegulier();
  Boolean choixAccueilOccasionnel = declarationForm.getAccueilOccasionnel();
  Boolean choixSemainesTypes = declarationForm.getPlanningSemainesTypes();
  
  Boolean opFinish = request.getParameter("opFinish") != null;

%>



<div class="headstall container-fluid formulaireActivation">
  <div class="row-fluid">
    <!-- COLONNE GAUCHE -->
    
    <!-- FIN COLONNE GAUCHE -->
    <!-- COLONNE DROITE -->
    <div class="span12 label">
      <div class="row-fluid title">
      
      
      
      <jalios:select>      
	      <jalios:if predicate="<%= isModif %>">
			      <div class="headerEspacePerso">
			        <h1><trsb:glp key="ASS-MODDEC-GEN-TITRE-HTML" /></h1>     
			      </div>
			      <div class="clear"></div>
	      </jalios:if>
	      <jalios:default>
	          <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/header.jspf' %>
	      </jalios:default>
      </jalios:select>
      
      
      
      
      </div>
    </div>
    
    
    <div class="row-fluid">
    <div class="span12 label">
      
        <div class="ajax-refresh-div">
        
	        <!-- Ce texte introductif est affiché uniquement sur l’écran du premier onglet -->      
	        <jalios:if predicate="<%= step == DeclarerAccueilAssmatHandler.ENFANT_ACCUEILLI %>">
	          <div style="margin-top: 35px;">
	          
	             <jalios:select>
			             <jalios:if predicate="<%= isModif %>">		                 
			                 <trsb:glp key="ASS-MODDEC-ENF-TEX-HTML" />
			             </jalios:if>
			             <jalios:default>
			                 <trsb:glp key="ASS-DEC-ENF-TEX-HTML" />
			             </jalios:default>
	             </jalios:select>
	             
	          </div>
	        </jalios:if>
        
          <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/demarches/declarerAccueil/etapes.jspf'%>

          <div class="mesDemarches declarerAccueil form-cg">
             <div class="saisieDossier enfantaccueilli form-cg-gray form-cg-white">
              <%@ include file='/jcore/doMessageBox.jsp'%>
              <form method="post"
                action="<%=ServletUtil.getResourcePath(request)%>"
                name="formAccueil" id="formAccueil" class="formAccueil">

                <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/demarches/declarerAccueil/enfantAccueilli.jspf'%> 
                <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/demarches/declarerAccueil/lieuAccueil.jspf'%>
                <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/demarches/declarerAccueil/modalites.jspf'%>
                 
                <%if(choixAccueilRegulier != null && choixAccueilRegulier){ %>
                   <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/demarches/declarerAccueil/planning.jspf'%> 
                <%}else if (choixSemainesTypes != null && choixSemainesTypes) { %>
                    <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/demarches/declarerAccueil/planningVariable.jspf'%> 
                <%}else { %>
                    <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/demarches/declarerAccueil/planningOccasionnel.jspf'%>                   
                <%} %>                 
                <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/demarches/declarerAccueil/validation.jspf'%>
                <!--  Ecriture des champ cachés             -->
                <%=formHandler.getFormStepHiddenFields()%>
                
                  <% String idDeclaration = declarationForm.getIdDeclaration()+""; %>
                  <input type="hidden" name="idDeclaration" value='<%= Util.notEmpty(idDeclaration) ? idDeclaration : "" %>'/>
                  
                  
                   <input type="hidden" value='<%= Util.notEmpty(request.getParameter("idModifEnCours")) ? request.getParameter("idModifEnCours") : ""  %>' name="idModifEnCours"/>
                
                
                <jalios:if predicate="<%= HttpUtil.isCSRFEnabled() %>">
			              <input type="hidden" name="csrftoken" value="<%= getCSRFToken() %>"/>			            
                 </jalios:if> 

              </form>
           
          </div>

  </div>
        </div>
      </div>
    </div>
  </div>
</div>
