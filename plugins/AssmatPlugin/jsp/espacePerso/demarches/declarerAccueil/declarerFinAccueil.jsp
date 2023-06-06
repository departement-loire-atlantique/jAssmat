<%@page import="fr.cg44.plugin.socle.SocleUtils"%>
<%@page import="fr.cg44.plugin.assmat.util.DemarcheUtil"%>
<%@page import="io.swagger.client.ApiException"%>
<%@page import="fr.cg44.plugin.assmat.handler.DeclarerAccueilAssmatHandler"%>
<%@page import="org.joda.time.DateTime"%>
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
%>


<%

Integer idDeclaration = Integer.parseInt(request.getParameter("idDeclaration"));




// Lien pour la déclaration de fin accueil
Publication declarationFinAccueilPortlet = channel.getPublication(channel.getProperty("plugin.assmatplugin.portlet.declararer-fin-accueil.id"));
String lienDeclarationFinAccueil = "";
if(declarationFinAccueilPortlet != null) {
  lienDeclarationFinAccueil = declarationFinAccueilPortlet.getDisplayUrl(userLocale);
}


// Format d'affichage de la fate
DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("dd/MM/YYYY");


// La déclaration a mettre en fin d'accueil
DeclarationAccueilDTO declaration = null;
try {
  declaration = DemarcheUtil.getDeclarationAccueilById(idDeclaration);
}catch (ApiException e) {
  //logger.warn("Déclaration non trouvée " + idDeclaration, e);
  return;
}

// Si modification fin d'accueil et que la déclaration appartient bien à l'assmat loggée
Boolean isModifFin = "fin".equalsIgnoreCase(declaration.getType()) && profil.getNum_agrement() == declaration.getNumDossier() && "valide".equalsIgnoreCase(declaration.getEtatDeclaration());

// liste des declaration à l'état en cours
// Vérifie que la déclaration est bien à en cours et appartient bien a l assmat loggée
Boolean isExistDeclaration = false;
List<AccueilDTO> declarationEnCoursList = DemarcheUtil.getListAccueils(profil.getNum_agrement(), "en cours");
for(AccueilDTO itAccueil : declarationEnCoursList) {
  if(itAccueil.getIdDeclaration().equals(declaration.getIdDeclaration())) {
    isExistDeclaration = true;
  }
}


String nomEnfant = declaration.getNomEnfant();
String prenomEnfant = declaration.getPrenomEnfant();
String sexeEnfant = declaration.getSexeEnfant();

String dateNaissanceEnfant = "";
if(Util.notEmpty(declaration.getDateNaissanceEnfant())) {
  dateNaissanceEnfant = DATE_TIME_FORMAT.print(declaration.getDateNaissanceEnfant());
}

String dateDebutAccueilString = "";
if(Util.notEmpty(declaration.getDateDebutAccueil())) {
  dateDebutAccueilString = DATE_TIME_FORMAT.print(declaration.getDateDebutAccueil());
}


String dateFinAccueilString = "";
if(isModifFin) {
  dateFinAccueilString = DATE_TIME_FORMAT.print(declaration.getDateFinAccueil());
}

Boolean isSend = false;

%>


<% 
  
// Si validation de l'utilisateur
if (Util.notEmpty(request.getParameter("opFishish"))) {

  dateFinAccueilString = request.getParameter("dateFinAccueil");
  
  
  // Erreur si la date est vide
  if (Util.isEmpty(dateFinAccueilString)) {
    jcmsContext.addMsg(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DFAC-DATFAC-ERR-HTML")));
  } else {
    // Conversion vers le bon format
    dateFinAccueilString = AssmatUtil.convertFormDateToAssmatProfilDate(dateFinAccueilString);
    
    // Erreur si le format de la date est incorrect
    if (!DeclarerAccueilAssmatHandler.isValideDateFormat(dateFinAccueilString)) {
      jcmsContext.addMsg(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DFAC-DATFAC-FOR-HTML")));
    } else {

      DateTime dateFinAccueil = DATE_TIME_FORMAT.parseDateTime(dateFinAccueilString);
      Date currentDate = new Date();
      DateTime currentDateTime = new DateTime(currentDate);
      
      // Contrôle que la date de fin d’accueil saisie est antérieure à la date du jour
      if (dateFinAccueil.isAfterNow()) {          
        jcmsContext.addMsg(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DFAC-DATFAC-JOUR-HTML")));
      } else {
        
        DateTime dateDebutAccueil = DATE_TIME_FORMAT.parseDateTime(dateDebutAccueilString);
        
        // la date de fin d’accueil doit être postérieure ou égale à la date de début d’accueil 
        if(dateFinAccueil.isBefore(dateDebutAccueil)){
          jcmsContext.addMsg(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DFAC-ERR-HTML")));
        }else {
        
	        isSend = true;
	        declaration.setIdDeclaration(null);
	        declaration.setType("fin");
	        declaration.setEtatDeclaration("valide");
	        declaration.setSemaineTypes(null);
	        declaration.setDateFinAccueil(dateFinAccueil);        
	        if(isModifFin) {          
	          DemarcheUtil.modifierDeclarationAccueil(idDeclaration, declaration);
	        }else {
	          DemarcheUtil.creerDeclarationAccueil(declaration);
	        } 
        }
        
      }
    }

  }

}
%>
  
  <!--  Titre de la page -->
        <jalios:select>
          <jalios:if predicate="<%= isModifFin %>">
              <h2><trsb:glp key="ASS-MODDFAC-TITRE1-HTML"/></h1>
          </jalios:if>
          <jalios:default>
            <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/header.jspf' %>
          </jalios:default>
        </jalios:select>
  <!-- FIN titre de la page -->
  
  
	     <!-- Présentation de l’espace -->
	     <jalios:if predicate="<%= !isSend %>">	        
	         <p>
	           <!-- Déclaration de fin : présentation de l’espace -->
	           <jalios:if predicate="<%= isExistDeclaration %>">
	             <trsb:glp key="ASS-DFAC-TEXT-HTML" />
	           </jalios:if>
	           <!-- Modification de fin : présentation de l’espace -->
	           <jalios:if predicate="<%= isModifFin %>">
	             <trsb:glp key="ASS-MODDFAC-TEXT-HTML" />
	           </jalios:if>
	         </p>      	     	     
	     </jalios:if>
	     

	    
	     <%@ include file='/plugins/SoclePlugin/jsp/doMessageBoxCustom.jspf' %>
	    
	    
	    
	    <jalios:if predicate="<%= !isSend && (isExistDeclaration || isModifFin) %>">
	    
		     <form method="post" action="<%= ServletUtil.getResourcePath(request) %>" data-no-encoding="true">
			    <p>
			        <trsb:glp key="ASS-DFAC-INFO-HTML" />
			    </p>
			    <p>
			        <trsb:glp key="ASS-DFAC-DECL-HTML" parameter='<%= new String[]{prenomEnfant, nomEnfant, sexeEnfant} %>'/>
			    </p>
			    <p>
			        <%= glp("jcmsplugin.assmatplugin.declarationaccueil.nele", dateNaissanceEnfant) %>
			    </p>
			    <p>
			        <trsb:glp key="ASS-DFAC-DEBAC-HTML" parameter='<%= new String[]{dateDebutAccueilString} %>'/>
			    </p>
			    <% String uuid = UUID.randomUUID().toString(); %>
	            <jalios:buffer name="labelField">
	            <trsb:glp key="ASS-DFAC-DATFAC-HTML" attribute="true"/>
	            </jalios:buffer>
	            <div class="ds44-form__container">
		            <div class="ds44-posRel">
		                <label for="form-element-<%= uuid %>" class="ds44-formLabel ds44-datepicker"><span class="ds44-labelTypePlaceholder"><span><%= labelField %></span></span></label>
		                <div data-name="dateFinAccueil" class="ds44-datepicker__shape ds44-inpStd" data-required="true">
		                    <input id="form-element-<%= uuid %>" type="text" inputmode="numeric" pattern="[0-9]*" maxlength="2" title='<%= glp("jcmsplugin.socle.facette.champ-obligatoire.title", glp("jcmsplugin.socle.facette.date.exemple.jour", labelField)) %>' value="<%= SocleUtils.getDayFromDate(dateFinAccueilString) %>" data-is-date="true"  required  aria-describedby="explanation-form-element-<%= uuid %>">
		                    <span>/</span>
		                    <input type="text" inputmode="numeric" pattern="[0-9]*" maxlength="2" title='<%= glp("jcmsplugin.socle.facette.champ-obligatoire.title", glp("jcmsplugin.socle.facette.date.exemple.mois", labelField)) %>'  value="<%= SocleUtils.getMonthFromDate(dateFinAccueilString) %>" data-is-date="true"  required  aria-describedby="explanation-form-element-<%= uuid %>">
		                    <span>/</span>
		                    <input type="text" inputmode="numeric" pattern="[0-9]*" maxlength="4" title='<%= glp("jcmsplugin.socle.facette.champ-obligatoire.title", glp("jcmsplugin.socle.facette.date.exemple.annee", labelField)) %>'  value="<%= SocleUtils.getYearFromDate(dateFinAccueilString) %>" data-is-date="true"  required  aria-describedby="explanation-form-element-<%= uuid %>">
		                </div>
		                <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", labelField) %></span></button>
		                <span class="ds44-calendar" aria-hidden="true" aria-describedby=""><i class="icon icon-date icon--large" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.date.calendrier.afficher", labelField) %></span></span>
		                <div  class="vanilla-calendar hidden"></div>
		            </div>
		            <div class="ds44-field-information" aria-live="polite">
		                <ul class="ds44-field-information-list ds44-list">
		                    <li id="explanation-form-element-<%= uuid %>" class="ds44-field-information-explanation"><trsb:glp key="ASS-DFAC-DATFAC-EX-HTML"/></li>
		                </ul>
		            </div>
		        </div>

			    <div class="ds44-form__container">
			        <button type="submit" class="ds44-btnStd ds44-btn--invert" name="submit" value="<trsb:glp attribute="true" key="ASS-DFAC-BT-VAL-HTML"></trsb:glp>"><span class="ds44-btnInnerText"><trsb:glp key="ASS-DFAC-BT-VAL-HTML" attribute="true"></trsb:glp></span></button>			      			       
			        <input type="hidden" name="noSendRedirect" value="true" data-technical-field/>
			        <input type="hidden" name="opFishish" value="true" data-technical-field/>
			        <input type="hidden" name="idDeclaration" value="<%= declaration.getIdDeclaration() %>" data-technical-field/>
			        <input type="hidden" name="csrftoken" value="<%= getCSRFToken() %>" data-technical-field/>
			    </div>
			</form>

			 
			 
			 </jalios:if>
			 
			 
			 <jalios:if predicate="<%= !isExistDeclaration && !isModifFin %>">
			   <p><%= glp("jcmsplugin.assmatplugin.declarationaccueil.notfound") %></p>
			 </jalios:if>
			 
	   <jalios:if predicate="<%= isSend %>">
         <p>
            <jalios:if predicate="<%= isExistDeclaration %>">
              <trsb:glp key="ASS-DFAC-FIN-HTML" />
            </jalios:if>
            <jalios:if predicate="<%= isModifFin %>">
              <trsb:glp key="ASS-MODDFAC-FIN-HTML" />
            </jalios:if>
         </p>
       </jalios:if>
			
  <!-- FIN corps de la page -->
