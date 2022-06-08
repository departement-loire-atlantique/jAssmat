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


<div class="headstall container-fluid demarche fin-accueil">
  
  <!--  Titre de la page -->
  <div class="row-fluid">    
    <div class="span12 label">
      <div class="row-fluid title">
        <jalios:select>
          <jalios:if predicate="<%= isModifFin %>">
            <div class="headerEspacePerso">
              <h1><trsb:glp key="ASS-MODDFAC-TITRE1-HTML"/></h1>
            </div>
          </jalios:if>
          <jalios:default>
            <%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/header.jspf' %>
          </jalios:default>
        </jalios:select>
      </div>
    </div>   
  </div> 
  <!-- FIN titre de la page -->
  
  
  <!-- Corps de la page -->
  <div class="row-fluid">
	  <div class="span12 label">
	  
	  
	     <!-- Présentation de l’espace -->
	     <jalios:if predicate="<%= !isSend %>">	        
	         <p class="titleAccueilPerso">
	           <!-- Déclaration de fin : présentation de l’espace -->	           
	           <jalios:if predicate="<%= isExistDeclaration %>">
	             <trsb:glp key="ASS-DFAC-TEXT-HTML" />
	           </jalios:if>
	           <!-- Modification de fin : présentation de l’espace -->     
	           <jalios:if predicate="<%= isModifFin %>"> 
	             <trsb:glp key="ASS-MODDFAC-TEXT-HTML" />
	           </jalios:if>	           
	         </p>      	     	     
	         <div style="border-bottom: 2px dotted #000000; margin-top: 55px; margin-bottom: 20px;" ></div>	     
	     </jalios:if>
	     

	    
	     <%@ include file='/jcore/doMessageBox.jsp'%>
	    
	    
	    
	    <jalios:if predicate="<%= !isSend && (isExistDeclaration || isModifFin) %>">
	    
		     <form action="" method="post" style="margin-left: 55px;">
			     			     
			     <p>
			       <trsb:glp key="ASS-DFAC-INFO-HTML" />
			     </p>
			     
			     <p>
			       <trsb:glp key="ASS-DFAC-DECL-HTML" parameter='<%= new String[]{prenomEnfant, nomEnfant, sexeEnfant} %>'/>
			     </p>
			     
			     <p>
		         Né le <%= dateNaissanceEnfant %>
		       </p>
		       
		       <p>       
	            <trsb:glp key="ASS-DFAC-DEBAC-HTML" parameter='<%= new String[]{dateDebutAccueilString} %>'/>
	         </p>
	         
	         
	         <p>       
	            <label for="dateFinAccueil"> <trsb:glp key="ASS-DFAC-DATFAC-HTML"/> </label>           
	            <input type="string" value='<%= Util.notEmpty(dateFinAccueilString) ? dateFinAccueilString : "" %>' name="dateFinAccueil" id="dateFinAccueil">  
	            <trsb:glp key="ASS-DFAC-DATFAC-EX-HTML"/>               
	         </p>
	         
	         
	         
	         <div style="border-bottom: 2px dotted #000000; margin-top: 20px; margin-bottom: 10px;" ></div>
	         
	         <div class="submit bouton-valider" style="float: right;">
	         
		         <label for="submit"> 
		           <input type="submit" id="submit" name="opFishish" value="<trsb:glp attribute="true" key="ASS-DFAC-BT-VAL-HTML" />" class="submitButton">
		           <span class="input-box">
		               <span class="spr-recherche-ok"></span>
		           </span>
		         </label> 
	                
	           <input type="hidden" name="idDeclaration" value="<%= declaration.getIdDeclaration() %>">
	                 
	         </div>    
				 
				 </form>
			 
			 
			 </jalios:if>
			 
			 
			 <jalios:if predicate="<%= !isExistDeclaration && !isModifFin %>">
			   <p> Déclaration d'accueil non trouvée </p>
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
			
	     
	  </div>
  </div>
  <!-- FIN corps de la page -->
    

</div>

