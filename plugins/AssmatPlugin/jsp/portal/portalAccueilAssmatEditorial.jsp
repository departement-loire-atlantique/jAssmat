<%@page import="com.jalios.jcms.handler.QueryHandler"%>
<%@page import="fr.cg44.plugin.assmat.beans.AssmatSearch"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="fr.cg44.plugin.tools.AccessibilityLinks"%>
<%@ include file='/jcore/doInitPage.jsp' 
%><%@ include file='/jcore/portal/doPortletParams.jsp'  
%><% 
  PortalJspCollection box = (PortalJspCollection) portlet;
  ServletUtil.backupAttribute(pageContext , "ShowChildPortalElement");
//Drag and Drop required a custom class wrapper 'dnd-container' and the wrapper ID. They also REQUIRED a DOM ID
 String dndCSS = (isLogged && loggedMember.canWorkOn(box)) ? "ID_"+box.getId()+" dnd-container" : ""; 
 jcmsContext.addCSSHeader("plugins/EServicePlugin/css/portal/portal.css"); 
  %>
  
<%@ include file='/types/AbstractCollection/doIncludePortletCollection.jsp'%>
<jalios:include target="OLD_BROWSER"/>
<%= AccessibilityLinks.createAccessibilityBlock(AccessibilityLinks.NAVIGATION_ID,AccessibilityLinks.SEARCH_ID, AccessibilityLinks.CONTENT_ID,AccessibilityLinks.FOOTER_ID)%>
<div class="portail portail-accueil-editorial">
   
  <%= getPortlet(bufferMap,"header") %>
  
  <main id="main_content" tabindex="-1"> 
  <div class="container">
    <% String alertContent=getPortlet(bufferMap, "alert");
    if(Util.notEmpty(alertContent)){
    %>
      <div class="alerts-container"><%-- PQF des alertes--%>
        <%=alertContent%>
      </div><%
      }%>
      <div class="block-spacer">
        <%=getPortlet(bufferMap, "breadcrumb")%>
      </div>
      
     
      <div class="row-fluid">
        <div class="span12" id="<%= AccessibilityLinks.CONTENT_ID%>">
          <%=getPortlets(bufferMap, "content", "")%>
        </div>
      </div>
      
      
    </div>
  </main>
  <%= getPortlet(bufferMap,"footer") %>
  
</div>