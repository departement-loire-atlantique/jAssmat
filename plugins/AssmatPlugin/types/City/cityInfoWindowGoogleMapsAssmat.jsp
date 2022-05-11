<%@page import="fr.cg44.plugin.tools.modal.ModalGenerator"%><%
%><%@ include file='/jcore/doInitPage.jsp' %><%
%><% City obj = (City) request.getAttribute(PortalManager.PORTAL_PUBLICATION); %><%
%><% 

jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/infoWindowGoogleMaps.css");
jcmsContext.addJavaScript("plugins/AssmatPlugin/js/plugin.js");%><%


   
    %>

    
    <h2><%=obj.getTitle() %></h2><%
        
    %><div class="link"><%
      %><img src="s.gif" class="bullet spr-puce" alt=""/>
      <a id="linkMapCity" attr-city-id="<%=obj.getId() %>" attr-city-insee="<%=obj.getCityCode() %>" attr-city-name="<%=obj.getTitle() %>" onclick="jQuery.plugin.AssmatPlugin.getCitySearch()" >Toutes les assistantes maternelles</a>
      </div>
 

