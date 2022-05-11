<%--
  @Summary: "Password Reset Request" form to ask for a new password, when the site is private
--%><%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%
%><%@ include file='/jcore/doInitPage.jsp' %><%
%><%@page import="fr.cg44.plugin.tools.modal.ModalCreator"%><%
%><%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%><%
  jcmsContext.addCSSHeader("css/jalios/ux/jalios-login.css");
      
  jcmsContext.addJavaScript("plugins/AssmatPlugin/js/plugin.js");
 // Keep doHeader.jsp before handler, it is required for proper redirection (prevent double submit)
%><%@ include file='/jcore/doHeader.jsp' %><%
%><jsp:useBean id="formHandler" scope="request" class="fr.cg44.plugin.assmat.handler.ResetPasswordAssmatHandler"><%
  %><jsp:setProperty name="formHandler" property="request"  value="<%= request %>"/><%
  %><jsp:setProperty name="formHandler" property="response" value="<%= response %>"/><%
  %><jsp:setProperty name="formHandler" property="*" /><%
%></jsp:useBean><% 


String password = request.getParameter("password1");
Boolean isValidePassword = true;
//V�rifie que le mot de passe remplie bien les pr�requis de s�curit�
if(!AssmatUtil.checkPassword(password) && Util.notEmpty(password)){
  jcmsContext.addMsg(new JcmsMessage(com.jalios.jcms.context.JcmsMessage.Level.WARN , AssmatUtil.getMessage("PASSWORD-ERROR")));
  isValidePassword = false;
}
boolean validate = formHandler.validate();
logger.warn("validate : " +validate);
logger.warn(channel.getSecuredBaseUrl(request));
logger.warn(ResourceHelper.getLogin());
logger.warn(channel.getUrl());
  if (isValidePassword && validate && Util.isEmpty(password)) {
	logger.warn("isValidePassword && validate && Util.isEmpty(password)...");
    sendRedirect(channel.getUrl()+ResourceHelper.getLogin()+"?portal="+getDataIdParameter("portal"));
    return;
  }
  if (isValidePassword && validate && !Util.isEmpty(password)) {
	logger.warn("isValidePassword && validate && !Util.isEmpty(password)...");
    sendRedirect(channel.getUrl());
    return;
  }
  request.setAttribute("mailSubAdminMenu", "true");
  if (formHandler.isResetRequestFormDisplayed()) {
    request.setAttribute("title", glp("jcmsplugin.assmatplugin.ui.fo.resetpass.request.title"));    
  } else if (formHandler.isResetFormDisplayed()) {
    request.setAttribute("title", glp("ui.fo.resetpass.reset.title"));   
  }
  
//CSS
 jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/PortletLogin/portletLoginFullDisplay.css");
 jcmsContext.addCSSHeader("plugins/AssmatPlugin/css/plugin.css");
  
%>
<div class="mail-password">

<%@ include file='/jcore/doMessageBox.jsp' %>



<%-- Password reset REQUEST --%>
<%
if (formHandler.isResetRequestFormDisplayed()) { 
  
  String inputIdEmail = "email";
  String inputIdTelephone = "telephone";
  Category currentCategory = PortalManager.getDisplayContext(channel.getCurrentJcmsContext()).getCurrentCategory();
  Data reqPortal = getDataParameter("portal");
  
  // Url de redirection
  // Si on est dans une cat�gorie, on reste sur cette cat�gorie 
  String redirectUrl = Util.getString(getValidHttpUrl("redirect"), ServletUtil.getBaseUrl(request) + "index.jsp");
 
  if(ModalCreator.isModalPortal()) {
    Category cat = channel.getCategory(channel.getProperty("plugin.corporateidentity.form.portalLoginRedirect", null));
    if(Util.notEmpty(cat)) {
      redirectUrl = ServletUtil.getBaseUrl(request) + cat.getDisplayUrl(channel.getCurrentUserLocale());
      }
  } else {
    if(Util.notEmpty(currentCategory)) {
      redirectUrl = ServletUtil.getBaseUrl(request) + currentCategory.getDisplayUrl(channel.getCurrentUserLocale());
    }
 }
  
 
%>
<div class="forget-password form-cg <%= (ModalCreator.isModalPortal())? "modal-portal":"" %>">
  <div class="form-cg-gray">
    <h2><%= glp("jcmsplugin.assmatplugin.ui.fo.resetpass.request.title") %></h2>
									
			<form action="<%= channel.getSecuredBaseUrl(request) %><%= ResourceHelper.getMailPassword() %>?portal=<%= getDataIdParameter("portal") %>" method="post" name="requestResetForm"><%
			  %><input type="hidden" name="portal" value="<%= getDataIdParameter("portal") %>" /><%
        %><!--<input type="hidden" name="jsp" value="<%= ResourceHelper.getLogin() %>" />--><%
			  %><!--<input type="hidden" name="jsp" value="<%=encodeForHTMLAttribute(redirectUrl) %>" />--><%
			  %><!-- <input type="hidden" name="redirect" value="<%= encodeForHTMLAttribute(redirectUrl) %>" /> --><%
			
			  
			%><jalios:if predicate="<%= HttpUtil.isCSRFEnabled() %>">
		    	<input type="hidden" name="csrftoken" value="<%= HttpUtil.getCurrentCSRFToken(request) %>"/>
			</jalios:if><%	
			  
        %><div><p><%= glp("jcmsplugin.assmatplugin.ui.fo.resetpass.request.txt") %></p></div><%
				 %><div class="e-mail row-fluid">
	          
		        <div class="label span5"><%
						 %><label for="<%= inputIdEmail %>"><%= glp("ui.fo.resetpass.request.email.label") %></label><%
						 %><span><%= glp("plugin.corporateidentity.form.login.subtitle") %></span><%
					%></div><%
					
					%><div class="input cg-input span7"><%
					  String value = formHandler.getEmail();
					  if(Util.isEmpty(value)) value = "";
					   
					  %><input class="formTextfield" type="text"  value="<%= value %>" name="<%= inputIdEmail %>" id="<%= inputIdEmail %>" /><%
					 
					%></div>
					</div>
					
					<div class="e-mail row-fluid">
           
            <div class="label span5"><%
                     %><label for="<%= inputIdEmail %>"><%= glp("jcmsplugin.assmatplugin.ui.fo.resetpass.request.phone.label") %></label><%
                     %><span><%= glp("jcmsplugin.assmatplugin.form.login.subtitle.tel") %></span><%
                %></div><%
                
                %><div class="input cg-input span7"><%
                  String telephone = formHandler.getTelephone();
                  if(Util.isEmpty(telephone)) telephone = "";
                   
                  %><input class="formTextfield" type="text" value="<%= telephone %>" name="<%= inputIdTelephone %>" id="<%= inputIdTelephone %>" /><%
                 
                %></div>
        </div>
					
		    <%
		    
        %><div class="submit"><%
           %><%@ include file='/plugins/CorporateIdentityPlugin/jsp/style/getBackgroundStyle.jspf' %>
           <label for="opRequestReset">
           	<input type="submit" id="opRequestReset" name="opRequestReset" value="<%= glp("plugin.tools.form.validate") %>" class='submitButton'/>
           	<span class="input-box" <%= backgroundStyle %>><span class="spr-recherche-ok"></span></span>
           </label>
         </div>
         
         <div class="clear"></div>
		    
			</form><%
  %></div>
  </div><%
}

%>

<%-- Password RESET --%>
<%
if (formHandler.isResetFormDisplayed()) {

  String inputIdPassword1 = "ResetPasswordPwd1Input";
  String inputWidgetPassword1CustomAttribute = "id=\"" + inputIdPassword1 + "\" autocomplete=\"off\"";
  String inputWidgetPassword2CustomAttribute = "autocomplete=\"off\"";
  
%>
<form action="<%= channel.getSecuredBaseUrl(request) %><%= ServletUtil.getResourcePath(request) %>" method="post" name="resetForm" class="form-cg-gray">
  <input type="hidden" name="portal" value="<%= getDataIdParameter("portal") %>" />
  <input type="hidden" name="jsp" value="<%= ResourceHelper.getMailPassword() %>" />
  <input type="hidden" name="passwordResetToken" value="<%= encodeForHTMLAttribute(formHandler.getPasswordResetToken()) %>" />
	<jalios:if predicate="<%= HttpUtil.isCSRFEnabled() %>">
		<input type="hidden" name="csrftoken" value="<%= HttpUtil.getCurrentCSRFToken(request) %>"/>
	</jalios:if>	
  <div class="box tint login  contact boxBordure boxResetLoginAssmat form-cg" style="margin: 0 auto;">
  
    <div class="box-body form-cg-gray">
      <h3 class="br txt-center"><% /* %>Login<% */ %><%= glp("jcmsplugin.assmatplugin.ui.fo.resetpass.reset.title") %></h3>
      <p><%= glp("ui.fo.resetpass.reset.txt", encodeForHTML(formHandler.getMember().getFullName()), encodeForHTML(formHandler.getMember().getLogin())) %></p>
      
      <div class="alert alert-block alertPass hide  alert-cg">
        <h4><%=glp("msg.message-box.warning")%></h4>
        <p></p>
      </div>
      
      <table class='peer'>
        <tr> 
          <td class="formLabel">
            <label style="color:black;" class="labelBlack" for="<%= inputIdPassword1 %>"><% /* %>E-mail<% */ %><%= glp("ui.fo.resetpass.reset.password1.label") %></label>
          </td>
          <td> 
            <jalios:widget  editor          ='<%= AbstractWidget.UI_EDITOR_PASSWORD %>'
                            widgetName      ='<%= "password1" %>'
                            formName        ='<%= "resetForm" %>'
                            placeholder     ='<%= glp("ui.fo.resetpass.reset.password1.placeholder") %>'
                            customAttributes='<%= inputWidgetPassword1CustomAttribute %>'
                            printLabel      ='<%= false %>'
                            size            ='<%= 30 %>'
                             css="cg-input"
            />
          </td>
        </tr>
        <tr> 
          <td class="formLabel">
            
          </td>
          <td> 
            <jalios:widget  editor          ='<%= AbstractWidget.UI_EDITOR_PASSWORD %>'
                            widgetName      ='<%= "password2" %>'
                            formName        ='<%= "resetForm" %>'
                            placeholder     ='<%= glp("ui.fo.resetpass.reset.password2.placeholder") %>'
                            customAttributes='<%= inputWidgetPassword2CustomAttribute %>'
                            printLabel      ='<%= false %>'
                            size            ='<%= 30 %>'
                            css="cg-input"
            />
          </td>
        </tr>
        
        
        <tr>
          <td>                           
          </td>
          <td>           
             <div class="blocLabel">
                <div class="" style="min-height: 1px;"></div>
              </div>
              
              <div class="visible-password">
                  <input id="visible-password" type="checkbox" />
                  <label for="visible-password" style="font-weight: normal; font-size: 14px;"><span style="position: relative; top: -4px;"><trsb:glp key="CONNEXION-AM-VISIBLE-PASSWORD" /></span> </label>
              </div>
          </td>
        </tr>
        
        
        <tr> 
          <td colspan="2" class="txt-right"> 
            <p class="submit">
              <label for="submit"> 
                <input type="submit" id="submit" name="opReset" value="<%= glp("jcmsplugin.assmatplugin.ui.fo.resetpass.valide") %>" class="submitButton"> 
               <span class="input-box" style="background-color: #aec900"><span class="spr-recherche-ok"></span></span>
              </label> 
          <input type="hidden" name="noSendRedirect" value="true"> 
        </p>
            
            
          </td>
        </tr>
      </table>
    </div>
   
  </div>

</form>
<%
} // isResetFormDisplayed
%>
</div>


<jalios:javascript>

jQuery( "#ResetPasswordPwd1Input" ).focusout(function(event) {  
    jQuery.plugin.AssmatPlugin.verifyMotDePasse("#ResetPasswordPwd1Input", "<%=AssmatUtil.getMessage("ERROR-MDP-SECURITY") %>");   
 });
 
 jQuery('#visible-password').change(function () {
    if(this.checked){
      document.body.querySelector('input[name="password1"]').type='text';
      document.body.querySelector('input[name="password2"]').type='text';
    }else {      
      document.body.querySelector('input[name="password1"]').type='password';
      document.body.querySelector('input[name="password2"]').type='password';
    }    
});
 
</jalios:javascript>

<%@ include file='/jcore/doFooter.jsp' %>
