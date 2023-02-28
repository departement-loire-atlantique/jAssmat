<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
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
boolean autorisationSite = profil.getAutorisationSite();
boolean autorisationCAF = profil.getAutorisationCAF();
boolean autorisationRelais = profil.getAutorisationRelais();

//UUID unique pour les champs
String uuid = UUID.randomUUID().toString();
%>

<%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/header.jspf' %>
<%@ include file='/plugins/SoclePlugin/jsp/doMessageBoxCustom.jspf' %>

  <form method="post" action="<%= ServletUtil.getResourcePath(request) %>" 
    name="formContact" id="formContact" class="formContact formEspacePerso">
    
    <%-- autorisationSite --%>
    <p aria-level="2" class="h4-like"><trsb:glp key="AUTOR-DPT-HTML"></trsb:glp><sup aria-hidden="true">*</sup></p>
    <div id="form-element-<%= uuid %>" data-name="autorisationSite" class="ds44-form__radio_container ds44-form__container"  data-required="true">
       <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
       <div class="ds44-form__container ds44-checkBox-radio_list ">
          <input type="radio" <% if(autorisationSite){ %> checked <% } %> name="autorisationSite" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio"   required  aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><trsb:glp key="AUTOR-DPT-OUI-HTML"></trsb:glp></label>
       </div>
       <div class="ds44-form__container ds44-checkBox-radio_list ">
          <input type="radio" <% if(!autorisationSite) { %> checked <% } %> name="autorisationSite" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio"   required  aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><trsb:glp key="AUTOR-DPT-NON-HTML"></trsb:glp><br /></label>
       </div>
    </div>
 
    <%-- autorisationCAF --%>
    <% uuid = UUID.randomUUID().toString(); %>
    <p aria-level="2" class="h4-like"><trsb:glp key="AUTOR-CAF-HTML"></trsb:glp><sup aria-hidden="true">*</sup></p>
    <div id="form-element-<%= uuid %>" data-name="autorisationCAF" class="ds44-form__radio_container ds44-form__container"  data-required="true">
       <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
       <div class="ds44-form__container ds44-checkBox-radio_list ">
          <input type="radio" <% if(autorisationCAF){ %> checked <% } %> name="autorisationCAF" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio"   required  aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><trsb:glp key="AUTOR-CAF-OUI-HTML"></trsb:glp></label>
       </div>
       <div class="ds44-form__container ds44-checkBox-radio_list ">
          <input type="radio" <% if(!autorisationCAF) { %> checked <% } %> name="autorisationCAF" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio"   required  aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><trsb:glp key="AUTOR-CAF-NON-HTML"></trsb:glp></label>
       </div>
    </div>
    
    <%-- autorisationRelais --%>
    <% uuid = UUID.randomUUID().toString(); %>
    <p aria-level="2" class="h4-like"><trsb:glp key="AUTOR-RAM-HTML"></trsb:glp><sup aria-hidden="true">*</sup></p>
    <div id="form-element-<%= uuid %>" data-name="autorisationRelais" class="ds44-form__radio_container ds44-form__container"  data-required="true">
       <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
       <div class="ds44-form__container ds44-checkBox-radio_list ">
          <input type="radio" <% if(autorisationRelais){ %> checked <% } %> name="autorisationRelais" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio"   required  aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><trsb:glp key="AUTOR-RAM-OUI-HTML"></trsb:glp></label>
       </div>
       <div class="ds44-form__container ds44-checkBox-radio_list ">
          <input type="radio" <% if(!autorisationRelais) { %> checked <% } %> name="autorisationRelais" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio"   required  aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><trsb:glp key="AUTOR-RAM-NON-HTML"></trsb:glp></label>
       </div>
    </div>
    
    <%-- encart --%>      
    <div class="alert alert-block">
      <trsb:glp key="AUTOR-LEG-HTML"></trsb:glp>
    </div>
    
    <div class="ds44-form__container">
        <button data-send-native class="ds44-btnStd ds44-btn--invert" data-submit-value="true" data-submit-key="opCreate" title='<trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp>'><trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp></button>
        <input type="hidden" name="noSendRedirect" value="true" data-technical-field/> 
        <input type="hidden" name="opUpdate" value="true" />
        <input type="hidden" name="csrftoken" value="<%= HttpUtil.getCSRFToken(request) %>" data-technical-field>
    </div>
    
  </form>
