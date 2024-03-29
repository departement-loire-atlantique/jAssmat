<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%@ taglib prefix="ds" tagdir="/WEB-INF/tags"%>
<%--
  @Summary: "Password Reset Request" form to ask for a new password, when the site is private
--%>

<% PortletJsp portlet = (PortletJsp)request.getAttribute(PortalManager.PORTAL_PUBLICATION); %><%
%><%@ include file='/jcore/doInitPage.jsp' %><%

 // Keep doHeader.jsp before handler, it is required for proper redirection (prevent double submit)
%><%@ include file='/jcore/doHeader.jsp' %>

<jsp:useBean id="formHandler" scope="request" class="fr.cg44.plugin.assmat.handler.ResetPasswordAssmatHandler">
    <jsp:setProperty name="formHandler" property="request"  value="<%= request %>"/>
    <jsp:setProperty name="formHandler" property="response" value="<%= response %>"/>
    <jsp:setProperty name="formHandler" property="*" />
</jsp:useBean>
<%
//UUID unique pour les champs
String uuid = UUID.randomUUID().toString();

%>

<main role="main" id="content">
    <div class="ds44-container-large">
    <ds:titleNoImage title='<%= encodeForHTMLAttribute(glp("jcmsplugin.assmatplugin.ui.fo.resetpass.request.title")) %>' breadcrumb="true"></ds:titleNoImage>

    <div class="ds44-img50 ds44--xxl-padding-tb">
        <div class="ds44-inner-container">
            <div class="ds44-grid12-offset-1">
                <section class="ds44-box ds44-theme">
                    <div class="ds44-innerBoxContainer">


<%-- Password reset REQUEST --%>
<jalios:select>

    <jalios:if predicate="<%= formHandler.isResetFormDisplayed() %>">
    
       <%
       request.setAttribute("title", glp("ui.fo.resetpass.reset.title"));
       
       if (formHandler.validate()) {
         sendRedirect(channel.getUrl());
         return;
       }
       %>
       
        <form action="<%= ServletUtil.getResourcePath(request) %>" method="post" name="resetForm" data-no-encoding="true">
	        
	        <%@ include file='/plugins/SoclePlugin/jsp/doMessageBoxCustom.jspf' %>
	        
	        <input type="hidden" name="portal" value="<%= getDataIdParameter("portal") %>" data-technical-field/>
	        <input type="hidden" name="jsp" value="<%= ResourceHelper.getMailPassword() %>" data-technical-field/>
	        <input type="hidden" name="passwordResetToken" value="<%= encodeForHTMLAttribute(formHandler.getPasswordResetToken()) %>" data-technical-field/>
	         <input type="hidden" name="noSendRedirect" value="true" data-technical-field>
	
	        <jalios:if predicate="<%= HttpUtil.isCSRFEnabled() %>">
	            <input type="hidden" name="csrftoken" value="<%= HttpUtil.getCurrentCSRFToken(request) %>" data-technical-field/>
	        </jalios:if>    
	    
	        <h2><%= glp("jcmsplugin.assmatplugin.ui.fo.resetpass.reset.title") %></h2>
	
	        <p><%= glp("ui.fo.resetpass.reset.txt", encodeForHTML(formHandler.getMember().getFullName()), encodeForHTML(formHandler.getMember().getLogin())) %></p>
	        <p><strong><%= glp("jcmsplugin.assmatplugin.espaceperso.motdepasscomporte") %></strong></p>
	        
	        <%-- Password ------------------------------------------------------------ --%>
	        <% String passwordLabel = glp("ui.fo.login.lbl.passwd"); %>
	        <% uuid = UUID.randomUUID().toString(); %>
	        <div class="ds44-form__container">
	            <div class="ds44-posRel">
	                <label for="password" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= passwordLabel %><sup aria-hidden="true">*</sup></span></span></label>
	                
	                <input type="password" id="password" name="password1" value="" class="ds44-inpStd" title="<%= encodeForHTMLAttribute(glp("jcmsplugin.socle.facette.champ-obligatoire.title", passwordLabel)) %>" required autocomplete="current-password" />
	                
	                <button class="ds44-showPassword" type="button">
	                    <i class="icon icon-visuel icon--sizeL" aria-hidden="true"></i>
	                    <span class="visually-hidden"><%= glp("jcmsplugin.assmatplugin.label.afficher", passwordLabel) %></span>
	                </button>
	        
	                <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", passwordLabel) %></span></button>
	            </div>
	        </div>
	        
	        <%-- Confirm Password ------------------------------------------------------------ --%>     
	        <% String confirmPasswordLabel = glp("ui.fo.resetpass.reset.password2.placeholder"); %>
	        <div class="ds44-form__container">
	            <div class="ds44-posRel">
	                <label for="password_verif" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= confirmPasswordLabel %><sup aria-hidden="true">*</sup></span></span></label>
	            
	                <input type="password" id="password_verif" name="password2" value="" class="ds44-inpStd" title="<%= encodeForHTMLAttribute(glp("jcmsplugin.socle.facette.champ-obligatoire.title", confirmPasswordLabel)) %>" data-field-compare="#password" required autocomplete="current-password" />
	            
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
	    
	    </form>       
    </jalios:if>
    
    <jalios:default>
    
       <%
       request.setAttribute("title", glp("jcmsplugin.assmatplugin.ui.fo.resetpass.request.title"));
       
       if (formHandler.validate()) {
           sendRedirect(channel.getUrl()+ResourceHelper.getLogin());
           return;
       }
       %>    

		<p><%= glp("jcmsplugin.assmatplugin.ui.fo.resetpass.request.txt") %></p>
		                        
		<form action="<%= ServletUtil.getResourcePath(request) %>" method="post" name="requestResetForm" data-no-encoding="true">
		    <%@ include file='/plugins/SoclePlugin/jsp/doMessageBoxCustom.jspf' %>
			<%
			uuid = UUID.randomUUID().toString();
			String emailAssmat = formHandler.getEmail();
			if(Util.isEmpty(emailAssmat)) emailAssmat = "";
			%>
			
			<%-- email ------------------------------------------------------------ --%>
	        <% String emailLabel = glp("jcmsplugin.assmatplugin.inscription.champ.lbl.email"); %>
			<div class="ds44-form__container">
			    <div class="ds44-posRel">
			        <label for="form-element-<%= uuid %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= emailLabel %></span></span></label>
			        <input type="email" id="form-element-<%= uuid %>" name="email" value="<%=emailAssmat %>" class="ds44-inpStd" title='<%= encodeForHTMLAttribute(emailLabel) %>' autocomplete="email" aria-describedby="explanation-form-element-<%= uuid %>" data-bkp-aria-describedby="explanation-form-element-<%= uuid %>">
			        <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", emailLabel) %></span></button>
			    </div>
			    <div class="ds44-field-information" aria-live="polite">
			        <ul class="ds44-field-information-list ds44-list">
			            <li id="explanation-form-element-<%= uuid %>" class="ds44-field-information-explanation"><%= glp("jcmsplugin.socle.form.exemple.email") %></li>
			        </ul>
			    </div>
			</div>
			
			<%
			uuid = UUID.randomUUID().toString();
			String telAssmat = formHandler.getTelephone();
			if(Util.isEmpty(telAssmat)) telAssmat = "";                        
			%>
			
			<p class="ds44-box-heading" style="padding-bottom: 20px">ou</p>
			
			
			<%-- Téléphone ------------------------------------------------------------ --%>
	        <% String telephoneLabel = glp("jcmsplugin.assmatplugin.inscription.champ.lbl.mobile"); %>
			<div class="ds44-form__container">
			    <div class="ds44-posRel">
			        <label for="form-element-<%= uuid %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= telephoneLabel %></span></span></label>
			        <input type="text" id="form-element-<%= uuid %>" name="telephone" value="<%= telAssmat %>" class="ds44-inpStd" title='<%= encodeForHTMLAttribute(telephoneLabel) %>' autocomplete="tel-national" aria-describedby="explanation-form-element-<%= uuid %>" data-bkp-aria-describedby="explanation-form-element-<%= uuid %>">
			        <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", telephoneLabel) %></span></button>
			    </div>
			    
			    <div class="ds44-field-information" aria-live="polite">
			        <ul class="ds44-field-information-list ds44-list">
			            <li id="explanation-form-element-<%= uuid %>" class="ds44-field-information-explanation"><%= glp("jcmsplugin.socle.form.exemple.tel") %></li>
			        </ul>
			    </div>
			</div>
			
			<input type="hidden" name="portal" value="<%= getDataIdParameter("portal") %>" data-technical-field/>
			  
			<jalios:if predicate="<%= HttpUtil.isCSRFEnabled() %>">
			    <input type="hidden" name="csrftoken" value="<%= HttpUtil.getCurrentCSRFToken(request) %>" data-technical-field/>
			</jalios:if>    
			
			<button class="ds44-btnStd ds44-btn--invert" data-submit-value="true" data-submit-key="opRequestReset" title='<%= glp("jcmsplugin.socle.valider")%>'>
			    <span class="ds44-btnInnerText"><%= glp("jcmsplugin.socle.valider")%></span><i class="icon icon-long-arrow-right" aria-hidden="true"></i>
			</button>
		
		
		</form> 
		

    </jalios:default>


</jalios:select>

         </div>

                </section>
            </div>
        </div>
    </div>
</div>

</main>
