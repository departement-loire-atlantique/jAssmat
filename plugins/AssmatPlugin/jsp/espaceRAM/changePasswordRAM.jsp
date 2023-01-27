<%--
   @Summary: Login form for public channel
   @Category: Authentication
   @Deprecated: False
   @Customizable: True
   @Requestable: True
--%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@ page import="com.jalios.jcms.authentication.handlers.DelegationAuthenticationHandler" %>
<%@ include file='/jcore/doInitPage.jsp' %>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%@ taglib prefix="ds" tagdir="/WEB-INF/tags"%>

<% PortletJsp portlet = (PortletJsp)request.getAttribute(PortalManager.PORTAL_PUBLICATION); %><%
%><%@ include file='/front/doFullDisplay.jspf' %>

<%
if(Util.isEmpty(loggedMember)){
  return;
}
%>

<%
String idPortailRAM = channel.getProperty("jcmsplugin.assmatplugin.socle.portal.colonne");

Publication portalRAM = channel.getPublication(idPortailRAM);

%><%@ include file='/jcore/doHeader.jsp' %>

<jsp:useBean id='formHandler' scope='page' class='fr.cg44.plugin.assmat.handler.ChangePasswordRAMHandler'>
  <jsp:setProperty name='formHandler' property='request' value='<%=request%>' />
  <jsp:setProperty name='formHandler' property='response' value='<%=response%>' />
  <jsp:setProperty name='formHandler' property="noRedirect" value="true" />
  <jsp:setProperty name='formHandler' property='*' />
</jsp:useBean>


<main role="main" id="content">
    <div class="ds44-container-large">
    <ds:titleNoImage title='<%= encodeForHTMLAttribute(glp("jcmsplugin.assmatplugin.ui.fo.resetpass.reset.title")) %>' breadcrumb="true"></ds:titleNoImage>

    <div class="ds44-img50 ds44--xxl-padding-tb">
        <div class="ds44-inner-container">
            <div class="ds44-grid12-offset-1">
                <section class="ds44-box ds44-theme">
                    <div class="ds44-innerBoxContainer">
<%
if (formHandler.validate()) {
  // Si OK, affiche un lien de retour vers la rubrique "Relais"
  Category relaisCat = (Category) channel.getCategory(channel.getProperty("plugin.assmatplugin.categ.ram.id"));
%>
    <%@ include file='/plugins/SoclePlugin/jsp/doMessageBoxCustom.jspf' %>

    <p class="mtm"><jalios:link data='<%= relaisCat %>'><%= glp("jcmsplugin.assmatplugin.form.resetpass.lien-retour-label") %></jalios:link></p>
  <%
  return;
}
%>

    
<%@ include file='/plugins/SoclePlugin/jsp/doMessageBoxCustom.jspf' %>
<form action="<%= ServletUtil.getResourcePath(request) %>" method="post" name="resetForm" data-no-encoding="true">
    
    <p><strong><%= glp("jcmsplugin.assmatplugin.espaceperso.motdepasscomporte") %></strong></p>
     
    <%-- Password ------------------------------------------------------------ --%>
    <% String passwordLabel = AssmatUtil.getMessage("ESPACE-RAM-MOT-DE-PASSE"); %>
    <div class="ds44-form__container">
        <div class="ds44-posRel">
            <label for="password" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= passwordLabel %><sup aria-hidden="true">*</sup></span></span></label>
            
            <input type="password" id="password" name="password" value="" class="ds44-inpStd" title="<%= encodeForHTMLAttribute(glp("jcmsplugin.socle.facette.champ-obligatoire.title", passwordLabel)) %>" required />
            
            <button class="ds44-showPassword" type="button">
                <i class="icon icon-visuel icon--sizeL" aria-hidden="true"></i>
                <span class="visually-hidden"><%= glp("jcmsplugin.assmatplugin.label.afficher", passwordLabel) %></span>
            </button>
    
            <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", passwordLabel) %></span>
            </button>
        </div>
    </div>

    <%-- New Password ------------------------------------------------------------ --%>
    <% String newPasswordLabel = AssmatUtil.getMessage("ESPACE-RAM-NEW-MOT-DE-PASSE"); %>
    <div class="ds44-form__container">
        <div class="ds44-posRel">
            <label for="newpassword" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= newPasswordLabel %><sup aria-hidden="true">*</sup></span></span></label>
            
            <input type="password" id="newpassword" name="newpassword" value="" class="ds44-inpStd" title="<%= encodeForHTMLAttribute(glp("jcmsplugin.socle.facette.champ-obligatoire.title", newPasswordLabel)) %>" required />
            
            <button class="ds44-showPassword" type="button">
                <i class="icon icon-visuel icon--sizeL" aria-hidden="true"></i>
                <span class="visually-hidden"><%= glp("jcmsplugin.assmatplugin.label.afficher", newPasswordLabel) %></span>
            </button>
    
            <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", newPasswordLabel) %></span>
            </button>
        </div>
    </div>
    
    <%-- Confirm Password ------------------------------------------------------------ --%>     
    <% String confirmPasswordLabel = AssmatUtil.getMessage("ESPACE-RAM-NEW-CONFIRM-MOT-DE-PASSE"); %>
    <div class="ds44-form__container">
        <div class="ds44-posRel">
            <label for="confirmnewpassword" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= confirmPasswordLabel %><sup aria-hidden="true">*</sup></span></span></label>
        
            <input type="password" id="confirmnewpassword" name="confirmnewpassword" value="" class="ds44-inpStd" title="<%= encodeForHTMLAttribute(glp("jcmsplugin.socle.facette.champ-obligatoire.title", confirmPasswordLabel)) %>" data-field-compare="#newpassword" required />
        
            <button class="ds44-showPassword" type="button">
                <i class="icon icon-visuel icon--sizeL" aria-hidden="true"></i>
                <span class="visually-hidden"><%= glp("jcmsplugin.assmatplugin.label.afficher", confirmPasswordLabel) %></span>
            </button>
    
            <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", confirmPasswordLabel) %></span></button>
    
        </div>
    
    </div>      

    <button class="ds44-btnStd ds44-btn--invert" data-submit-value="true" data-submit-key="opReset" title='<%= glp("jcmsplugin.socle.valider")%>'>
        <span class="ds44-btnInnerText"><%= glp("jcmsplugin.socle.valider")%></span><i class="icon icon-long-arrow-right" aria-hidden="true"></i>
    </button>
    
    <jalios:if predicate="<%= HttpUtil.isCSRFEnabled() %>">
        <input type="hidden" name="csrftoken" value="<%= HttpUtil.getCurrentCSRFToken(request) %>" data-technical-field />
    </jalios:if>
    
    <input type="hidden" name="noSendRedirect" value="true" data-technical-field />             

</form>   





         </div>

                </section>
            </div>
        </div>
    </div>
</div>

</main>
