<%@page import="fr.cg44.plugin.tools.modal.ModalGenerator"%><%
%><%@ include file='/jcore/doInitPage.jsp' %><%
%><%@ include file='/jcore/portal/doPortletParams.jsp' %><%
%><% FicheLieu obj = (FicheLieu) request.getAttribute(PortalManager.PORTAL_PUBLICATION); %><%
%><%
// Type d'info-bulle
String classic = "classic";
String departmentCloseToYou = "departmentCloseToYou";
String classicWithImage = "classicWithImage";
boolean isCollege = false;
boolean isClassicWithImage = false;

String tooltipType = classic;
if(Util.notEmpty((String)request.getAttribute("tooltipType"))) {
  tooltipType = (String)request.getAttribute("tooltipType");
}

jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/infoWindowGoogleMaps.css"); %><%

Map<String, String> params = new HashMap<String, String>();
if(portlet != null){
  params.put("originPortlet", portlet.getId());
}

//Ajout MLEC pour ajouter le lien en savoir plus pour les fiches lieux de type Coll�ge
if (Util.notEmpty(obj.getTypesDeLieux(loggedMember))) { 
	Category collegeCategory = channel.getCategory(channel.getProperty("plugin.corporateidentity.place.collegeCategory"));
	Category[] cats = obj.getCategories();
	if(Util.notEmpty(cats)){
		for(Category cat : cats) {
			if(cat.equals(collegeCategory)){
				isCollege = true;
			}
		}
	}
}

//8661 : ajouter une image dans l'infobulle si infobulle "Classique + image" s�lectionn�
if(classicWithImage.equals(tooltipType)){
	if (Util.notEmpty(obj.getMainIllustration())) { 
		isClassicWithImage = true;
	}
}



%>
<jalios:select>
	<jalios:if predicate="<%= Util.notEmpty(obj.getDescription()) || isCollege %>">	
		<h2><%= new ModalGenerator(obj, jcmsContext, obj.getTitle(), glp("plugin.corporateidentity.place.moreOn", obj.getTitle())).setParams(params).isModalPortal(ModalGenerator.isModalPortal()).createModal() %></h2>
	</jalios:if>
	<jalios:default>
		<h2><%= obj.getTitle() %></h2>
	</jalios:default>
</jalios:select>
  <div class="contacts"><%
  //Rue
   %><jalios:if predicate="<%= Util.notEmpty(obj.getStreet()) %>"><p class="pre"><%= obj.getStreet().trim() %></p></jalios:if><%

   // Bo�te postale
  %><jalios:if predicate="<%= Util.notEmpty(obj.getPostalBox()) %>"><p><%= obj.getPostalBox() %></p></jalios:if><%
  
  //Ville et code postal
  %><jalios:if predicate="<%= Util.notEmpty(obj.getLibelleDeVoie()) || Util.notEmpty(obj.getCommune()) %>"><%
  %><p><%
    %><jalios:if predicate="<%= Util.notEmpty(obj.getLibelleDeVoie()) %>"><%= obj.getLibelleDeVoie() %></jalios:if><%
    %><jalios:if predicate="<%= Util.notEmpty(obj.getCommune()) && Util.notEmpty(obj.getLibelleDeVoie()) %>"> </jalios:if><%
    %><jalios:if predicate="<%= Util.notEmpty(obj.getCommune()) %>"><%= obj.getCommune().getTitle() %></jalios:if><%
  %></p><%
  %></jalios:if><%
  
  //Phones
  %><jalios:if predicate="<%= Util.notEmpty(obj.getTelephone()) %>"><%
	  %><p><%
	     if(departmentCloseToYou.equals(tooltipType)) { %><%= glp("plugin.corporateidentity.common.tel") %><% }
	     %><jalios:foreach name="itPhone" type="String" array="<%= obj.getTelephone() %>"><%= (itCounter > 1)?" - ":""%><%= itPhone %></jalios:foreach><%
	  %></p><%
  %></jalios:if><%
  
  // Fax
  %><jalios:if predicate="<%= Util.notEmpty(obj.getFax()) && departmentCloseToYou.equals(tooltipType) %>"><%
	  %><p><%
	     %><%= glp("plugin.corporateidentity.common.fax") %><%
	     %><jalios:foreach name="itFax" type="String" array="<%= obj.getFax() %>"><%= (itCounter > 1)?" - ":""%><%= itFax %></jalios:foreach><%
	  %></p><%
  %></jalios:if><%
      
  //Mails
  %><jalios:if predicate="<%= Util.notEmpty(obj.getEmail()) %>"><%
    String[] list = obj.getEmail();
    %><p><%
       %><jalios:foreach name="itMail" type="String" array="<%= list %>"><%
          String mail = itMail;
       	  Category cat = channel.getCategory(channel.getProperty("jcmsplugin.assmatplugin.formulaire.contact.ram"));
		  if(Util.notEmpty(cat)){
			String lien = cat.getDisplayUrl(userLocale) + "?mailRAM=" + encodeForHTML(mail) + "&titleRAM=" + encodeForURL(obj.getTitle());
          	%><%= (itCounter > 1)? " - ":"" %><a href='<%= lien %>'>Courriel</a><%
		  }
       %></jalios:foreach><%
    %></p><%
  %></jalios:if><%

	// website
	%><jalios:if predicate="<%= Util.notEmpty(obj.getSiteInternet()) %>"><%
	String[] websites = obj.getSiteInternet();
	%><p><%
	   %><jalios:foreach name="itWebsite" type="String" array="<%= websites %>"><%
	      %><%= (itCounter > 1)? " - ":"" %><a href="<%= itWebsite %>">Site internet</a><%
	   %></jalios:foreach><%
	%></p><%
	%></jalios:if><%

  %><jalios:if predicate="<%= isClassicWithImage %>"><%
 	%><p class="classicWithImage"><jalios:thumbnail path='<%= obj.getMainIllustration() %>' width='260' height='130'/></p><%
  %></jalios:if><% 
%></div><%



%>
<jalios:if predicate="<%= Util.notEmpty(obj.getDescription()) || isCollege %>">
	<div class="link"><%
	  %><img src="s.gif" class="bullet spr-puce" alt=""/><%
	  %><%= new ModalGenerator(obj, jcmsContext, glp("plugin.corporateidentity.place.more"), glp("plugin.corporateidentity.place.moreOn", obj.getTitle())).setParams(params).isModalPortal(ModalGenerator.isModalPortal()).createModal() %><%
	%></div>
</jalios:if>