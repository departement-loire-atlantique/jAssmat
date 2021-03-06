<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.tools.googlemaps.proxy.ProxyTarget"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@ include file='/jcore/doInitPage.jsp' %>
<%@ include file='/jcore/portal/doPortletParams.jsp' %>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>

<%
PortletJsp box = (PortletJsp) portlet;

ProfilManager profilMngr = ProfilManager.getInstance();
ProfilASSMAT profil = (ProfilASSMAT) profilMngr.getProfilASSMAT(channel.getCurrentLoggedMember());

if(Util.isEmpty(profil)){
	sendForbidden(request, response);
	return;
}

SolisManager solisManager = SolisManager.getInstance();
Boolean aide_caf = Util.getFirst(solisManager.getAssmatSolisByNumAgrement(profil.getNum_agrement())).getAideCaf() ;
if(aide_caf == null) {
 aide_caf = false;
}

%>

<%
// Handler interne car juste un seul champ modifié
if(Util.notEmpty(request.getParameter("opUpdate"))) {
  profil.setVisibiliteSite(HttpUtil.getBooleanParameter(request, "visibiliteSite", true));
  jcmsContext.addMsg(new JcmsMessage(com.jalios.jcms.context.JcmsMessage.Level.INFO, AssmatUtil.getMessage("PROFIL-CHANGE-SAVE")));
}
%>


<%
String dataColor= ProxyTarget.getMainColor(); 

Boolean visibiliteSite = profil.getVisibiliteSite();
%>

<%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/header.jspf' %>
<%@ include file='/jcore/doMessageBox.jsp' %>

<div class="headstall container-fluid formulaireActivation">
<div class="formActivation form-cg form-espace-perso">
<div class="form-cg-gray form-cg-white">

  <form method="post" action="<%= ServletUtil.getResourcePath(request) %>" 
    name="formContact" id="formContact" class="formContact formEspacePerso">

    <div class="alert alert-block alertPass hide  alert-cg">
      <h4><%=glp("msg.message-box.warning")%></h4>
      <p></p>
    </div>

    <div class="blocComplet gauche">
      <div class="blocLabel" style="width: auto; float: none; text-align: left;">
        <p class=""><trsb:glp key='<%= "VISIB-" + (aide_caf ? "AIDE-CAF-" : "")  + "AFF-HTML" %>'></trsb:glp></p>
      </div>

      <div class="blocChamp multipleRadio afficher-profile blocForm" style="margin-left: 0;">
        <span>
          <input type="radio" name="visibiliteSite" id="oui" class="radio" value="true"
            <%if(visibiliteSite){ %>  checked="checked" <%} %> style="background: transparent; float: none;" />
          <label for="email"><%=glp("jcmsplugin.assmatplugin.parametrage.champ.afficher-profile.oui") %></label>
        </span>
        <span style="margin-left: 20px;">
          <input type="radio" name="visibiliteSite" id="non" class="radio" value="false"
            <%if(!visibiliteSite){ %> checked="checked" <%} %>  <% if(aide_caf) { %> disabled <%} %>  style="background: transparent; float: none;" />
          <label for="tel"><%=glp("jcmsplugin.assmatplugin.parametrage.champ.afficher-profile.non") %><br /></label> 
        </span>
      </div>
    </div>
    
    
    <p><trsb:glp key='<%= "VISIB-" + (aide_caf ? "AIDE-CAF-" : "")  + "AFF-LEG-HTML" %>'></trsb:glp></p>  
  
    <%-- Contribuer dans VISIB-AFF-LEG-HTML et VISIB-REFUS-TEXTE-HTML ? pas de clé dans invision --%>  
<!-- 	  <div class="alert alert-block fade in alert-cg"> -->
<!-- 	    <h4>A savoir !</h4> -->
<!-- 	    <p>Même sans apparaître dans le moteur de recherche, nous vous invitons à utiliser le site pour réaliser vos démarches en ligne (déclarer un accueil, signaler un changement à l'unité agrément...). Ces fonctionnalités seront disponibles à partir de mars 2017 en vous connectant à votre espace personnel.</p> -->
<!-- 	  </div> -->

	  
	  
    <div class="borderDot title-bar-container dotted-portlet"></div>

    <p class="submit">
      <label for="submit"> 
        <input type="submit" id="submit" name="opCreate" 
          value="<trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp>" class="submitButton" /> 
        <span class="input-box" style="background-color: #aec900" />
          <span class="spr-recherche-ok"></span>
        </span>
      </label> 
      <input type="hidden" name="noSendRedirect" value="true" /> 
      <input type="hidden" name="opUpdate" value="true" />
    </p>
    
  </form>


</div>
</div>
</div>