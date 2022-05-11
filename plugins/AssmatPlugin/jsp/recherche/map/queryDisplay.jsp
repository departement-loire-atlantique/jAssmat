<%@page import="fr.cg44.plugin.assmat.PointAssmat"%>
<%@ include file='/jcore/doInitPage.jsp' %><%
%><%@ include file='/jcore/portal/doPortletParams.jsp' %><%
%><%@ page import="java.util.ArrayList" %><%
%><% 

List<Publication> publications = (ArrayList<Publication>) request.getAttribute("publications"); 
PointAssmat point = (PointAssmat) request.getAttribute("pointAssmat");

%><%
%><% jcmsContext.setTemplateUsage("geolocationInfoWindow"); %><%
%>

<% for(Publication publication : publications){ %><%
  %>
  <% 
 
  
  request.setAttribute("publication", publication); 
  request.setAttribute("pointAssmat", point); 
  

  
  
  
  
  %><%
%><jsp:include page='<%= "/"+publication.getTemplatePath(jcmsContext) %>' flush='true' />
<div style="margin-bottom: 25px;"></div>   
<%
%><% } %>