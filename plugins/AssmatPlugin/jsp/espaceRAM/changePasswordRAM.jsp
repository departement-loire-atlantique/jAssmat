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
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%

jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/PortletLogin/portletLoginFullDisplay.css");


String idPortailRAM = channel.getProperty("plugin.assmatplugin.portal.ram.id");
Publication portalRAM = channel.getPublication(idPortailRAM);


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
    
    <!-- FIN COLONNE GAUCHE -->
    <!-- COLONNE DROITE -->
    <div class="span10 label">
      <div class="row-fluid">
        <div class="label" style="margin-top: 20px;">
          <p>Votre mot de passe à bien été mis à jour.</p>
          Retour vers : <span style="text-decoration: underline;"><jalios:link data="<%= jcmsContext.getCurrentCategory().getParent() %>" /></span>
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
        
       
         <div class="alert alert-block alertPass hide alert-cg alert-warn"><h4><%=glp("msg.message-box.warning")%></h4>
            <p></p>
            </div>
        <%-- Standard LOGIN --%>
        <form method="post"
                action="<%=ServletUtil.getResourcePath(request)%>?portal=<%=idPortailRAM %>"
                name="formContact" id="formChangePass" class="formChangePass form-cg form-cg-gray">
           
          <div class="row-fluid">
          
            <div class="span5"><label for="password"><trsb:glp key="ESPACE-RAM-MOT-DE-PASSE" ></trsb:glp></label></div>
            
             <div class="span7"><input type="password" id="password" name="password"/></div>
          </div>
          <div class="row-fluid">
          
            <div class="span5"><label for="newpassword"><trsb:glp key="ESPACE-RAM-NEW-MOT-DE-PASSE" ></trsb:glp></label></div>
            
             <div class="span7"><input type="password" id="newpassword" name="newpassword"/></div>
          </div>
       
       <div class="row-fluid">
          
            <div class="span5"><label for="confirmnewpassword"><trsb:glp key="ESPACE-RAM-NEW-CONFIRM-MOT-DE-PASSE" ></trsb:glp></label></div>
            
             <div class="span7"><input type="password" id="confirmnewpassword" name="confirmnewpassword"/></div>
          </div>
          <div class="submit-and-forget">
            <input type="hidden" name="opReset" value="true"> 
            
            <p class="submit">
        <label for="submit">
          
          <input type="submit" id="submit" name="opCreate" value="Valider" class="submitButton">
          
          <span class="input-box" style="background-color:#AEC900"><span class="spr-recherche-ok"></span></span>
        </label>
        
        
          <input type="hidden" name="noSendRedirect" value="true">
          
      </p>
            
            
            
               
                <jalios:if predicate="<%= !channel.getAuthMgr().isShowingPersistentOption() %>">
                 <input type="hidden" name="<%= channel.getAuthMgr().getPersistentParameter() %>" value="<%= channel.getAuthMgr().getDefaultPersistentValue() %>" />
                </jalios:if>
             
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
