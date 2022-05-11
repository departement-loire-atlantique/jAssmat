<%@page import="fr.cg44.plugin.corporateidentity.comparator.ComparatorLieuxHorsSolisEnPremier"%>
<%@page import="org.jfree.data.ComparableObjectSeries"%>
<%@page import="fr.cg44.plugin.tools.facetedsearch.exception.UnknowDelegationException"%>
<%@page import="fr.cg44.plugin.tools.facetedsearch.manager.DelegationManager"%><%
%><%@page import="fr.cg44.plugin.tools.ToolsUtil"%><%
%><%@page import="fr.cg44.plugin.corporateidentity.tools.PlaceFinder"%><%
%><%@page import="fr.cg44.plugin.corporateidentity.pqf.PqfCssHelper"%><%
%><%@page import="fr.cg44.plugin.tools.category.CategoriesParameters"%><%
%><%@ page contentType="text/html; charset=UTF-8" %><%
%><%@ include file='/jcore/doInitPage.jsp' %><%
%><%@ include file='/jcore/portal/doPortletParams.jsp' %><%
%><%@page import="fr.cg44.plugin.tools.facetedsearch.FacetedSearchUtil"%><%
%><%@ page import="java.util.TreeSet" %><%

  PortletQueryForeachGoogleMaps box = (PortletQueryForeachGoogleMaps) portlet;
  %><%@ include file='/plugins/ToolsPlugin/types/PortletQueryForeachGoogleMaps/includeJsPqfGoogleMaps.jspf' %><%
	jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/PortletQueryForeachGoogleMaps/pqfInfiniteScrollAndMap.css");
  jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/PortletQueryForeachIntroduction/departmentCloseToYouQueryDisplay.css");

	Delegation delegation = null;
	
	 // Booléen pour afficher les onglets à gauche
	 boolean tabOnLeft = false;
	 // Booléen pour inverser les onglets
	 boolean invertTab = false;
	
	try {
	  String delegationId = getUntrustedStringParameter("delegations", "");
	  if(Util.notEmpty(delegationId)) {
	    delegation = (Delegation) DelegationManager.INSTANCE.getDelegationByCode(delegationId, Delegation.class);
	  }
	} catch(UnknowDelegationException e) {}


//Lancer la requête avant l'appel de la presentation
%><%@ include file='/plugins/CorporateIdentityPlugin/types/PortletQueryForeachGoogleMaps/doBeginRequest.jspf' %><%

%>

<jalios:if predicate="<%= Util.isEmpty(delegation) %>">
	<div class="close-to-you-query-display">
	
	  
	
	     <%@include file='/plugins/ToolsPlugin/types/PortletQueryForeachIntroduction/presentationPqf.jspf' %><%
	     
	     // Portlet d'introduction
	     if(box.getIntroductionPortlet() != null && jcmsContext.getCtxCategories().length == 0){
	       PortletQueryForeachGoogleMaps introductionPortlet = box.getIntroductionPortlet();
	       request.setAttribute("box", introductionPortlet);
	       %><jsp:include page='<%= "/"+channel.getTypeEntry(PortletQueryForeachGoogleMaps.class).getTemplateEntry("box.integrated").getPath() %>' flush='true' /><%
	     }%>
	     
	   
	   
	</div>
</jalios:if>
<%

// * On affichage la Google Maps avec le contour du département
if(Util.notEmpty(delegation)) {
  // * On affichage le menu liste/colonne
	%><%@ include file='/plugins/CorporateIdentityPlugin/jsp/style/getBackgroundStyle.jspf' %><%
	
	 
	//Menu des onglets
	%><div class="infinite-scroll-or-map tabbable"><%
	
		 String idTabList = "list";
		 String idTabMap = "map";
		
		 String mapId = "#"+idTabMap;
		 String listId = "#"+idTabList;

     
     //Récupération de l'onglet courant
     String tab = (String) session.getAttribute(currentCategory.getId());
     if(Util.isEmpty(tab)){	 
        if (box.getOngletCarteParDefaut()){
    		session.setAttribute(currentCategory.getId(), mapId); 
        }
     }
  	 
		 request.setAttribute("force-search-label", box.getSearchLabel(userLang));
		
		%><%@ include file='/plugins/CorporateIdentityPlugin/types/PortletQueryForeachGoogleMaps/onglets.jspf' %><%
	
	%><div class="tab-content">
	    <div class="tab-pane active" id="<%= idTabList %>"><%
	    // Tab1 => Affichage de la pqfInfiniteScroll
	    // Détails sur la récupération du path : http://community.jalios.com/jcms/1706_DBForumTopic/fr/demande-d-explication-sur-le-fonction-des-gabarits-sous-jcms-6
	    %><jsp:include page='<%= "/"+channel.getTypeEntry(PortletQueryForeach.class).getTemplateEntry("box.pqfInfiniteScroll").getPath() %>' flush='true' />  
	    </div>

	    <div class="tab-pane ajax-refresh" id="<%= idTabMap %>"><%
	    // Récupération du box display des Pqf Google
	      
	    // Si aucune catégorie n'est cochée, on prend seulement  la délégation pour l'affichage carte
        /*if(Util.isEmpty(jcmsContext.getCtxCategories()) && Util.isEmpty(request.getParameter("cidParent"))) {
          TreeSet collectionBis = new TreeSet();
          collectionBis.add(delegation);
          request.setAttribute("collectionToDisplay", collectionBis);
        }*/
	      
	    //Ajout de la délégation dans les résultats de recherche.
        collection.add(delegation);
        request.setAttribute("collectionToDisplay", collection);
        
	      %><jsp:include page='<%= "/"+channel.getTypeEntry(PortletQueryForeachGoogleMaps.class).getTemplateEntry("box.defaultTabUse").getPath() %>' flush='true' />  
	    </div>
	  </div>
	</div><%
	
	%><%@ include file='/plugins/CorporateIdentityPlugin/types/PortletQueryForeachGoogleMaps/doEndRequest.jspf' %><%
	
}
%>