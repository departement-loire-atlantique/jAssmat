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
%>

<%
// Handler interne car juste 3 champs modifiés sans besoin de controle d'intégrité
if(Util.notEmpty(request.getParameter("opUpdate"))) {
  profil.setAutorisationSite(AssmatUtil.getBooleanFromString(HttpUtil.getStringParameter (request, "autorisationSite", "false", HttpUtil.ALPHANUM_REGEX)));
  profil.setAutorisationCAF(AssmatUtil.getBooleanFromString(HttpUtil.getStringParameter(request, "autorisationCAF", "false", HttpUtil.ALPHANUM_REGEX)));
  profil.setAutorisationRelais(AssmatUtil.getBooleanFromString(HttpUtil.getStringParameter(request, "autorisationRelais", "false", HttpUtil.ALPHANUM_REGEX)));
  jcmsContext.addMsg(new JcmsMessage(com.jalios.jcms.context.JcmsMessage.Level.INFO, AssmatUtil.getMessage("PROFIL-CHANGE-SAVE")));
}
%>


<%
String dataColor= ProxyTarget.getMainColor(); 

boolean autorisationSite = profil.getAutorisationSite();
boolean autorisationCAF = profil.getAutorisationCAF();
boolean autorisationRelais = profil.getAutorisationRelais();
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
    
    <%-- autorisationSite --%>
    <div class="info-gauche cell-left">
      <p><trsb:glp key="AUTOR-DPT-HTML"></trsb:glp></p>
    </div>
    <div class="radio-droite radio-droite-large cell-right">
      <input type="radio" name="autorisationSite" id="ouiAutorisationSite" class="radio" value="true"
        <% if(autorisationSite){ %> checked <% } %>>
      <label for="ouiAutorisationSite"><trsb:glp key="AUTOR-DPT-OUI-HTML"></trsb:glp></label>
      <input type="radio" name="autorisationSite" id="nonAutorisationSite" class="radio" value="false"
        <% if(!autorisationSite) { %> checked <% } %>>
      <label for="nonAutorisationSite" ><trsb:glp key="AUTOR-DPT-NON-HTML"></trsb:glp></label>
    </div>
    
    <div class="borderDot title-bar-container dotted-portlet" style="margin-top: 10px; margin-bottom: 20px;"></div>
 
    <%-- autorisationCAF --%>
    <div class="info-gauche cell-left">
      <p><trsb:glp key="AUTOR-CAF-HTML"></trsb:glp></p>
    </div>
    <div class="radio-droite radio-droite-large cell-right">
      <input type="radio" name="autorisationCAF" id="ouiAutorisationCAF" class="radio" value="true"
        <% if(autorisationCAF) { %> checked <% } %>>
      <label for="ouiAutorisationCAF"><trsb:glp key="AUTOR-CAF-OUI-HTML"></trsb:glp></label>
      <input type="radio" name="autorisationCAF" id="nonAutorisationCAF" class="radio" value="false"
        <% if(!autorisationCAF) { %> checked <% } %>>
      <label for="nonAutorisationCAF"><trsb:glp key="AUTOR-CAF-NON-HTML"></trsb:glp></label>
    </div>

    <div class="borderDot title-bar-container dotted-portlet" style="margin-top: 10px; margin-bottom: 20px;"></div>
    
    <%-- autorisationRelais --%>
    <div class="info-gauche cell-left">
      <p><trsb:glp key="AUTOR-RAM-HTML"></trsb:glp></p>
    </div>
    <div class="radio-droite radio-droite-large cell-right">
      <input type="radio" name="autorisationRelais" id="ouiAutorisationRelais" class="radio" value="true"
        <% if(autorisationRelais) { %> checked <% } %>>
      <label for="ouiAutorisationRelais"><trsb:glp key="AUTOR-RAM-OUI-HTML"></trsb:glp></label>
      <input type="radio" name="autorisationRelais" id="nonAutorisationRelais" class="radio" value="false"
        <% if(!autorisationRelais){ %> checked <% } %>>
      <label for="nonAutorisationRelais"><trsb:glp key="AUTOR-RAM-NON-HTML"></trsb:glp></label>
    </div>
    
    <%-- encart --%>      
    <div class="alert alert-block fade in alert-cg">
      <trsb:glp key="AUTOR-LEG-HTML"></trsb:glp>
    </div>
    
    
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