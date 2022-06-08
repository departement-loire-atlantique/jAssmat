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
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%><%

jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/PortletLogin/portletLoginFullDisplay.css");

%><%@ include file='/jcore/doHeader.jsp' %><%
%><%
  request.setAttribute("title", glp("ui.fo.login.title"));
  Data reqPortal = getDataParameter("portal");
  String loginPortalId = reqPortal != null ? reqPortal.getId() : ((Data)request.getAttribute("Portal")).getId();
  
  // We assume this JSP is not embedded twice in a page.  
  String inputIdLogin    = "FrontLoginInputLogin";
  String inputIdPassword = "FrontLoginInputPassword";
  String inputIdMemorize = "FrontLoginInputMemorize";
  String inputWidgetLoginCustomAttribute = "id=\"" + inputIdLogin + "\"";
  String inputWidgetPasswordCustomAttribute = "id=\"" + inputIdPassword + "\"";
  
  // R�cup�ration de la cat�gorie courante
  Category currentCategory = PortalManager.getDisplayContext(channel.getCurrentJcmsContext()).getCurrentCategory();
  
%><div class="portlet-login-full-display title-bar-container dotted-title <%= (ModalCreator.isModalPortal())? "modal-portal":"" %>"><%
   %><h2><%= glp("plugin.assmatplugin.form.login.am.loginTitle") %></h2><%
   
   %>
   <%@ include file='/jcore/doMessageBox.jsp' %>
   <div class="form-cg">
        <div class="form-cg-gray container-fluid">
        <jalios:include target="LOGIN_FORM_HEADER" targetContext="div" /><%
          // Url de redirection
             // Si on est dans une cat�gorie, on reste sur cette cat�gorie 
             String redirectUrl = Util.getString(getValidHttpUrl("redirect"), ServletUtil.getBaseUrl(request) + "index.jsp");
             String redirectAccueilAssmat = Util.notEmpty(request.getParameter("redirectAccueilAssmat")) ? request.getParameter("redirectAccueilAssmat") : "";
            
          if (ModalCreator.isModalPortal()) {
            Category cat = channel.getCategory(channel.getProperty("jcmsplugin.assmatplugin.socle.confirm.catLoginRedirect", null));            
            if (Util.notEmpty(cat)) {
              redirectUrl = ServletUtil.getBaseUrl(request) + cat.getDisplayUrl(channel.getCurrentUserLocale());
              
              if(Util.notEmpty(redirectAccueilAssmat)) {
                Publication portalPerso = channel.getPublication(channel.getProperty("jcmsplugin.assmatplugin.socle.portail.param.id"));
                redirectUrl = redirectUrl + "?redirectForce=" + portalPerso.getDisplayUrl(userLocale);               
              }              
            }
          } 
        %>
        <%
        String identification = (String) session.getAttribute("identification");
        session.removeAttribute("identification");
        // Exception pour RAM ou Syndicat
       if(Util.notEmpty(identification)) {
        redirectUrl = getValidHttpUrl("redirect");
       }
        %>
        
        <jalios:select>
          
          <jalios:if predicate="<%= Util.notEmpty(identification) %>">                      
             <p><%= AssmatUtil.getMessage("CONNEXION-" + identification ,true) %></p>     
          </jalios:if>
          
          <jalios:default>
            <p><%= AssmatUtil.getMessage("CONNEXION-AM-DESCRIPTION",true) %></p>  
          </jalios:default>
        
        </jalios:select>
        
        <%
        // 11454 : Si pas de redirect connu alors allez vers la page d'accueil de gestion de profil d'un AM 
        if(redirectUrl == null) {
          Publication portalPerso = channel.getPublication(channel.getProperty("jcmsplugin.assmatplugin.socle.portail.param.id"));
          redirectUrl = portalPerso.getDisplayUrl(userLocale);
        }
        %>
        
        <%-- Standard LOGIN --%>
        <form action="<%= redirectUrl %>" method="post" name="login">
        
        
        
        <div class="login row-fluid">
          <div class="label span4"><%
             %><label for="<%= inputIdLogin %>"><%
               %><%= glp("plugin.corporateidentity.form.login.loginLabel") %><%
             %></label><%
             %><span></span><%
            %></div>
            <div class="span8"><%
              %><jalios:widget css="cg-input" editor='<%= AbstractWidget.UI_EDITOR_TEXTFIELD %>' widgetName='<%= channel.getAuthMgr().getLoginParameter() %>' size='<%= 14 %>' customAttributes='<%= inputWidgetLoginCustomAttribute %>'/><%
            %></div><%
         %></div><%
              
          %><div class="clear clear-margin"></div><%
          
          %><div class="password row-fluid">
             <div class="label span4"><%
               %><label for="<%= inputIdPassword %>"><%
                 %><%= glp("plugin.corporateidentity.form.login.passwordLabel") %><%
               %></label><%
             %></div><%
            
            %><div class="span8"><%
                %><jalios:widget css="cg-input" editor='<%= AbstractWidget.UI_EDITOR_PASSWORD %>' widgetName='<%= channel.getAuthMgr().getPasswordParameter() %>'  size='<%= 14 %>' customAttributes='<%= inputWidgetPasswordCustomAttribute %>' /><%
            %></div><%
          %></div><%
          
          %><div class="clear"></div><%
          
          // Rester connect�
          %>
          
          <div class="remember-me row-fluid">
		          <div class="label span4" style="min-height: 1px;">
		          </div>
		          <div class="span8">
				          <input style="display: inline-block !important; width: auto !important;" id="visible-password" type="checkbox" />
				          <label for="visible-password"> <trsb:glp key="CONNEXION-AM-VISIBLE-PASSWORD" /> </label>
              </div>
          </div>
          
          <jalios:if predicate="<%= channel.getAuthMgr().isShowingPersistentOption() %>">
            <div class="remember-me row-fluid">
              <div class="label span4" style="min-height: 1px;">
              </div>
              <div class="span8">
                  <input style="display: inline-block !important; width: auto !important;" id="<%= inputIdMemorize %>" type="checkbox" name="<%= channel.getAuthMgr().getPersistentParameter() %>" value="true" <%= channel.getAuthMgr().getDefaultPersistentValue() ? "checked=\"checked\"" : "" %> />
                  <label for="<%= inputIdMemorize %>"><%= glp("plugin.corporateidentity.form.login.rememberMe") %></label>
                </div>
            </div>
          </jalios:if>   
          
          <div class="clear"></div>  
          
          <div class="submit-and-forget">
            <a href="plugins/AssmatPlugin/jsp/login/mailPassword.jsp?portal=<%= loginPortalId %>"><%= glp("plugin.tools.form.forgotPassword") %></a><%
                String defaultColor = Proxy.getMainColor();
                String serviceStyle = (Util.notEmpty(defaultColor))? "style=\"background-color: "+defaultColor+"\"":"";
            %><div class="submit">
                <label for="submitLogin">
                  <input type='submit' id='submitLogin' name="<%= channel.getAuthMgr().getOpLoginParameter() %>" value="<%= glp("plugin.tools.form.validate") %>"  class='submitButton'/>
              <span class="input-box" <%= serviceStyle %>><span class="spr-recherche-ok"></span></span>
            </label>
                
              <%
             
                    
              %>
              <input type="hidden" name="redirectAccueilAssmat" value="<%= redirectAccueilAssmat %>" />
              <input type="hidden" name="loginType" value="am" />
              <input type="hidden" name="redirect" value="<%= encodeForHTMLAttribute(redirectUrl) %>" class="Form" />
                <input type="hidden" name="jsp" value="<%= ResourceHelper.getLogin() %>" />
                <input type="hidden" name="portal" value="<%= encodeForHTMLAttribute(loginPortalId) %>" />
                <jalios:if predicate="<%= !channel.getAuthMgr().isShowingPersistentOption() %>">
                 <input type="hidden" name="<%= channel.getAuthMgr().getPersistentParameter() %>" value="<%= channel.getAuthMgr().getDefaultPersistentValue() %>" />
                </jalios:if>
              </div>
            </div>
            
            <%--
        <div class="help row-fluid">
          <div class="label">
            <span><%= glp("plugin.corporateidentity.form.login.helpLabel") %>  <a style="text-decoration:underline;"  href="./presse" target="_parent">ici</a> </span>
          </div> 
        </div>  
         --%>
         </form>
      <jalios:include target="LOGIN_FORM_FOOTER" targetContext="div" /><%
      %><div class="clear"></div>
    </div>
  </div>
</div>

<jalios:javascript>

jQuery('#visible-password').change(function () {
    if(this.checked){
      document.getElementById('FrontLoginInputPassword').type='text';  
    }else {      
      document.getElementById('FrontLoginInputPassword').type='password';   
    }    
});

</jalios:javascript>

<%@ include file='/jcore/doFooter.jsp' %>

