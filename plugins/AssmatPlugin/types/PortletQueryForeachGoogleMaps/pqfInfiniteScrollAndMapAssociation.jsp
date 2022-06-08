<%@page import="fr.cg44.plugin.tools.ToolsUtil"%>
<%@page import="fr.cg44.plugin.corporateidentity.pqf.PqfCssHelper"%><%
%><%@page import="fr.cg44.plugin.tools.category.CategoriesParameters"%><%
%><%@page import="fr.cg44.plugin.corporateidentity.annuairePH.*"%><%
%><%@page import="fr.cg44.plugin.corporateidentity.coderpa.CoderpaJcmsProperties"%><%
%><%@ page contentType="text/html; charset=UTF-8" %><%
%><%@ include file='/jcore/doInitPage.jsp' %><%
%><%@ include file='/jcore/portal/doPortletParams.jsp' %><%
%><%@page import="fr.cg44.plugin.tools.facetedsearch.FacetedSearchUtil"%><%
%><%@ page import="java.util.TreeSet" %><%

PortletQueryForeachGoogleMaps box = (PortletQueryForeachGoogleMaps) portlet;
%><%@ include file='/plugins/ToolsPlugin/types/PortletQueryForeachGoogleMaps/includeJsPqfGoogleMaps.jspf' %><%
jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/PortletQueryForeachGoogleMaps/pqfInfiniteScrollAndMap.css");
jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/coderpa/downloadPdf.css");
jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/StructurePersonnesHandicapeesEnf/structurePH.css");

//préchargement des Fichiers Javascript et CSS de pqf

%><%@ include file='/plugins/CorporateIdentityPlugin/jsp/style/getBackgroundStyle.jspf' %><%


//Menu des onglets
%><div class="infinite-scroll-or-map tabbable"><%
//lancer la requete  avant l'apel de la presentation
%><%@ include file='doBeginRequest.jspf' %><%
%><%@ include file='/plugins/ToolsPlugin/types/PortletQueryForeachIntroduction/presentationPqf.jspf' %><%
boolean showglossary = request.getParameter("facets")==null;

//On affiche ou pas les résultats de la première requête
request.setAttribute("hideDefaultRequest", !showglossary);

String idTabList = "list";
String idTabMap = "map";
String mapId = "#"+idTabMap;
String listId = "#"+idTabList;

// Booléen pour afficher les onglets à gauche
boolean tabOnLeft = false;
// Booléen pour inverser les onglets
boolean invertTab = false;

//Ajout du type de tooltip
String tooltipType = "classic";
if(Util.notEmpty(box.getTooltipType())) {
tooltipType = box.getTooltipType();
}
request.setAttribute("tooltipType", tooltipType);

//Récupération de l'onglet courant
String tab = (String) session.getAttribute(currentCategory.getId());
if(Util.isEmpty(tab)){   
   if (box.getOngletCarteParDefaut()){
    session.setAttribute(currentCategory.getId(), mapId); 
   }
}

%>

<%@ include file="/types/AbstractPortletSkinable/doSkinAdd.jspf" %>
<%@ include file='/plugins/CorporateIdentityPlugin/types/PortletQueryForeachGoogleMaps/onglets.jspf' %><%


if(!showglossary){ 
 

  %>
  <div class="tab-content annuairePH">
    <div class="tab-pane active" id="<%=idTabList %>"><%
    // Tab1 => Affichage de la pqfInfiniteScroll
    
    %><jsp:include page='<%= "/"+channel.getTypeEntry(PortletQueryForeach.class).getTemplateEntry("box.pqfPagerScroll").getPath() %>' flush='true' /><%  
    %></div><%
    %><div class="tab-pane ajax-refresh" id="<%=idTabMap %>"><%
      %>
      <%  
    %><%
    %>
      <jsp:include page='<%= "/"+channel.getTypeEntry(PortletQueryForeachGoogleMaps.class).getTemplateEntry("box.defaultTabUse").getPath() %>' flush='true' /></div>
   
  </div><%
  %><%@ include file='doEndRequest.jspf' %><%
  }
%></div>