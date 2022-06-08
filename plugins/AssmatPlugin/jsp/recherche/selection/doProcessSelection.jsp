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


Set<Publication> setProfilAM = (Set<Publication>) request.getSession().getAttribute("listeProfilAMSelection");


boolean isSelected = false;

//R�cuperation de la Structure
Publication assmat = null;

if(Util.notEmpty(request.getAttribute("dataId"))){
  assmat =  (Publication) channel.getPublication((String) request.getAttribute("dataId"));

}else if(Util.notEmpty(request.getParameter("dataId"))){
  assmat =  (Publication) channel.getPublication((String) request.getParameter("dataId"));
}

//Si on a une structure en parametre
if(Util.notEmpty(assmat) && Util.notEmpty(request.getParameter("dataId"))){
	
	//Si la liste est vide (premier ajout dans le panier)
	if(Util.isEmpty(setProfilAM)){
	  setProfilAM = new TreeSet<Publication>();
	  setProfilAM.add(assmat);
		isSelected=true;
	}else{
		//Si la structure est deja dans le panier on la supprime
		if(setProfilAM.contains(assmat)){
		  setProfilAM.remove(assmat);
			isSelected=false;
		//Sinon on l'ajoute	
		}else{
		  setProfilAM.add(assmat);
			isSelected=true;
		}
	}
	

	session.setAttribute("listeProfilAMSelection", setProfilAM);
	}


	if(Util.notEmpty(assmat) && Util.isEmpty(request.getParameter("dataId")) && Util.notEmpty(setProfilAM)){
		if(setProfilAM.contains(assmat)){
			isSelected=true;
	}
	
}
	%>
	<div class="ajax-refresh-div ajaxSelectionPH">
		<% if (jcmsContext.isAjaxRequest()) { %>
			<script>
				<%if(Util.notEmpty(setProfilAM)){ %>
					jQuery(".nbAMpanier").text('<%=setProfilAM.size() %>');
				<%}else{%>
			  		jQuery(".nbAMpanier").text('0');
			  	<%}%>
		  	</script>
	  	<% }%>

	<%									
 	if(isSelected) { 
 %>
	 
	<a href="plugins/AssmatPlugin/jsp/recherche/selection/doProcessSelection.jsp?dataId=<%= assmat.getId() %>"  class="ajax-refresh removeAM-selection annuairePH-selection" title="Retirer de ma sélection" data-jalios-ajax-refresh="noscroll nofocus" onclick="return false;"> 
		<div class="img-selection">
			<img class="imgStarGreen" src="plugins/AssmatPlugin/img/picto-starGreen.png">
			<span class="jalios-icon jcmsplugin-bookmarks-topbar selection-am" > 
				 <img src="plugins/CorporateIdentityPlugin/images/annuaireph/selectioncheck.jpg">			 
			</span>	
		</div>
		<div class="txt-selection"><span class="txt-selection-principal">Retirer</span> de ma sélection</div> 
	</a>
	
	<% } else { %>
	
 
	<a href="plugins/AssmatPlugin/jsp/recherche/selection/doProcessSelection.jsp?dataId=<%= assmat.getId() %>"  class="annuairePH-selection  ajax-refresh"  title="Ajouter à ma sélection" data-jalios-ajax-refresh="noscroll nofocus" onclick="return false;"> 
		<div class="img-selection">
			<img class="imgStarGreen"  src="plugins/AssmatPlugin/img/picto-starGreen.png">
			<span class="jalios-icon jcmsplugin-bookmarks-topbar selection-am" > 
				 <img src="plugins/CorporateIdentityPlugin/images/annuaireph/selectionvide.jpg">			 
			</span>
		</div>
		<div class="txt-selection"><span class="txt-selection-principal">Ajouter</span> à ma sélection</div>
	</a>
	
<% } %>



	</div>

