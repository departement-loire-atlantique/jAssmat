<%--
   @Summary: Login form for public channel
   @Category: Authentication
   @Deprecated: False
   @Customizable: True
   @Requestable: True
--%><%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.tools.googlemaps.proxy.Proxy"%>
<%@page import="fr.cg44.plugin.tools.modal.ModalCreator"%><%
%><%@ page import="com.jalios.jcms.authentication.handlers.DelegationAuthenticationHandler" %><%
%><%@ include file='/jcore/doInitPage.jsp' %>
<%@ page contentType="text/html; charset=UTF-8"%>
<%

jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/PortletLogin/portletLoginFullDisplay.css");


String idPortailASSO = channel.getProperty("plugin.assmatplugin.portal.asso.id");
Publication portalASSO = channel.getPublication(idPortailASSO);


jcmsContext.addJavaScript("plugins/AssmatPlugin/js/plugin.js");
%><%@ include file='/jcore/doHeader.jsp' %>

<jsp:useBean id='formHandler' scope='page' class='fr.cg44.plugin.assmat.handler.ChangePasswordRAMHandler'>
  <jsp:setProperty name='formHandler' property='request' value='<%=request%>' />
  <jsp:setProperty name='formHandler' property='response' value='<%=response%>' />
  <jsp:setProperty name='formHandler' property="noRedirect" value="true" />
  <jsp:setProperty name='formHandler' property='*' />
</jsp:useBean>

<%
if (formHandler.validate()) {
  
  %>
  <div class="headstall container-fluid formulaireActivation">
  <div class="row-fluid">
    <!-- COLONNE GAUCHE -->
    <div class="span2 iconEtape">
      <img alt="etape1"
        src="plugins/AssmatPlugin/img/icon-activation-form.png" />
    </div>
    <!-- FIN COLONNE GAUCHE -->
    <!-- COLONNE DROITE -->
    <div class="span10 label">
      <div class="row-fluid title">
        <div class="label">
          <h1>Votre mot de passe à bien été mis à jour</h1>
        </div>
      </div>
    </div>
    <div class="span9 label">
      <div class="row-fluid">
        <div class="ajax-refresh-div">
          <%@ include file='/jcore/doMessageBox.jsp'%>
        </div>
      </div>
    </div>
  </div>
</div>
  
  <%
  return;
}
%>

<%
  
  
  // R�cup�ration de la cat�gorie courante
  Category currentCategory = PortalManager.getDisplayContext(channel.getCurrentJcmsContext()).getCurrentCategory();
  
%><div class="portlet-login-full-display title-bar-container dotted-title <%= (ModalCreator.isModalPortal())? "modal-portal":"" %>"><%
   %><%
   
   %>
   <%@ include file='/jcore/doMessageBox.jsp' %>
   <div class="form-cg formChangePassRAM">
        <div class="form-cg-gray container-fluid">
        
        <p><%= AssmatUtil.getMessage("CHANGE-PASSWORD-ASSO-SYND",true) %></p>
         <div class="alert alert-block alertPass hide alert-cg alert-warn"><h4><%=glp("msg.message-box.warning")%></h4>
            <p></p>
            </div>
        <%-- Standard LOGIN --%>
        <form method="post"
                action="<%=ServletUtil.getResourcePath(request)%>?portal=<%=idPortailRAM %>"
                name="formContact" id="formChangePass" class="formChangePass">
           
          <div class="row-fluid">
          
            <div class="span5"><label for="password">Mot de passe : </label></div>
            
             <div class="span7"><input type="password" id="password" name="password"/></div>
          </div>
          <div class="row-fluid">
          
            <div class="span5"><label for="newpassword">Nouveau mot de passe : </label></div>
            
             <div class="span7"><input type="password" id="newpassword" name="newpassword"/></div>
          </div>
       
       <div class="row-fluid">
          
            <div class="span5"><label for="confirmnewpassword">Confirmation nouveau mot de passe : </label></div>
            
             <div class="span7"><input type="password" id="confirmnewpassword" name="confirmnewpassword"/></div>
          </div>
          <div class="submit-and-forget">
            <input type="hidden" name="opReset" value="true"> 
            <div class="submit">
                <label for="submitLogin">
                  <input type='submit' id='submitLogin' name="<%= channel.getAuthMgr().getOpLoginParameter() %>" value="<%= glp("plugin.tools.form.validate") %>"  class='submitButton'/>
            
            </label>
               
                <jalios:if predicate="<%= !channel.getAuthMgr().isShowingPersistentOption() %>">
                 <input type="hidden" name="<%= channel.getAuthMgr().getPersistentParameter() %>" value="<%= channel.getAuthMgr().getDefaultPersistentValue() %>" />
                </jalios:if>
              </div>
            </div>
            
  <jalios:javascript>

 jQuery( "#formChangePass" ).submit(function( event ) {     
    jQuery.plugin.AssmatPlugin.verifyMotDePasse("#newpassword", "<%=AssmatUtil.getMessage("ERROR-MDP-SECURITY") %>");   
 });

jQuery( "#formChangePass" ).focusout(function(event) {    
    jQuery.plugin.AssmatPlugin.verifyMotDePasse("#newpassword",  "<%=AssmatUtil.getMessage("ERROR-MDP-SECURITY") %>");   
 });
 
 
 
 jQuery( "#formChangePass" ).submit(function( event ) {     
    jQuery.plugin.AssmatPlugin.verifyMotDePasse("#confirmnewpassword", "<%=AssmatUtil.getMessage("ERROR-MDP-SECURITY") %>");   
 });

jQuery( "#formChangePass" ).focusout(function(event) {    
    jQuery.plugin.AssmatPlugin.verifyMotDePasse("#confirmnewpassword",  "<%=AssmatUtil.getMessage("ERROR-MDP-SECURITY") %>");   
 });

</jalios:javascript>         
       
         </form>
     <div class="clear"></div>
    </div>
  </div>
</div>
<%@ include file='/jcore/doFooter.jsp' %>
