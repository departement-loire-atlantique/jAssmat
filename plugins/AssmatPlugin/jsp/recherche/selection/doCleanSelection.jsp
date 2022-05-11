<%@page import="fr.cg44.plugin.assmat.beans.AssmatSearch"%>
<%@page import="fr.cg44.plugin.assmat.managers.AssmatSearchDAO"%>
<%@ page contentType="text/html; charset=UTF-8"%><%
%><%@ include file='/jcore/doInitPage.jsp'%>
<%@ include file='/jcore/portal/doPortletParams.jsp'  
%><%@page import="java.util.TreeSet"%>
<%@page import="java.util.Set"%>
<%@page import="generated.ProfilASSMAT"%>
<%@page import="com.jalios.util.Util"%>
<%
// JSP Custom qui ajoute ou supprime du panier une assmatSearch

Set<Publication> idAssmatSearch = (Set<Publication>) request.getSession().getAttribute("listeProfilAMSelection");
if(Util.notEmpty(idAssmatSearch) && Util.notEmpty(request.getParameter("delete"))){
  idAssmatSearch.clear();
  %>
  <jalios:javascript>
  
  location.reload();
  </jalios:javascript>
  <%
  
}
session.setAttribute("listeProfilAMSelection", idAssmatSearch);
%>


	<%if(Util.notEmpty(idAssmatSearch)){ %>
	<a href="plugins/AssmatPlugin/jsp/recherche/selection/doCleanSelection.jsp?delete=true" class="ajax-refresh annuairePH-selection" title="Tout retirer de ma sélection" data-jalios-ajax-refresh="noscroll nofocus" onclick="return false;"> 
		<span class="jalios-icon jcmsplugin-bookmarks-topbar selection-am" > 
			 <img src="plugins/CorporateIdentityPlugin/images/annuaireph/selectioncheck.jpg">Tout retirer de ma sélection 
		</span>	
	</a>
<%}else{ %>

<a href="#" class="annuairePH-selection ajax-refresh"  title="Ajouter � ma s�lection" data-jalios-ajax-refresh="noscroll nofocus" onclick="return false;"> 
    <span class="jalios-icon jcmsplugin-bookmarks-topbar selection-am" > 
       <img src="plugins/CorporateIdentityPlugin/images/annuaireph/selectionvide.jpg"> Tout retirer de ma sélection
    </span>
  </a>

<%}%>