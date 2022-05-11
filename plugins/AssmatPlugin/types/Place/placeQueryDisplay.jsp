<%@page import="fr.cg44.plugin.tools.ToolsUtil"%><%
%><%@page import="fr.cg44.plugin.tools.modal.ModalGenerator"%>
<%@ page contentType="text/html; charset=UTF-8" %><%
%><%@ include file='/jcore/doInitPage.jsp' %><% 

   Place obj = (Place) request.getAttribute(PortalManager.PORTAL_PUBLICATION); 
   
   jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/Place/placeQueryDisplay.css");
   
	%><%@ include file='/plugins/CorporateIdentityPlugin/jsp/style/getBackgroundStyle.jspf' %><%
%><div class="place-box-display-container">
  <div class="header">
    <h2><%= obj.getTitle() %><jalios:edit pub="<%= obj %>" /></h2>
    <jalios:if predicate="<%= Util.notEmpty(obj.getCity()) %>"><p <%= backgroundStyle %>><%= obj.getCity().getTitle() %></p></jalios:if>
  </div>
  <div class="row-fluid content">
    <div class="span6">
      <jalios:if predicate="<%= Util.notEmpty(obj.getServiceOrHubOrUnit()) %>"><p><%= obj.getServiceOrHubOrUnit() %><jalios:edit pub="<%= obj %>" fields="serviceOrHubOrUnit"/></p></jalios:if><%
      %><jalios:if predicate="<%= Util.notEmpty(obj.getStreet()) %>"><p class="pre"><%= obj.getStreet().trim() %><jalios:edit pub="<%= obj %>" fields="street"/></p></jalios:if><%
      %><jalios:if predicate="<%= Util.notEmpty(obj.getPostalBox()) %>"><p><%= obj.getPostalBox() %><jalios:edit pub="<%= obj %>" fields="postalBox"/></p></jalios:if><%
      %><jalios:if predicate="<%= Util.notEmpty(obj.getZipCode()) || Util.notEmpty(obj.getCity()) %>"><%
        %><p>
            <jalios:if predicate="<%= Util.notEmpty(obj.getZipCode())  %>"><%= obj.getZipCode() %><jalios:edit pub="<%= obj %>" fields="zipCode"/></jalios:if>
            <jalios:if predicate="<%= Util.notEmpty(obj.getCity())  %>"><%= obj.getCity().getTitle() %><jalios:edit pub="<%= obj %>" fields="city"/></jalios:if>
        </p>
      </jalios:if>
      <%@include file="/plugins/AssmatPlugin/jsp/common/doContact.jspf" %><%
        // Lien en savoir plus
        // 8308: Affichage résultat vue liste - lien en savoir plus => ne s'affiche pas pour les collèges
		Category collegeCategory = channel.getCategory(channel.getProperty("plugin.corporateidentity.place.collegeCategory"));
		Category[] categories = obj.getCategories();
		String idCollege = "";
		List<String> listeCommune = new ArrayList<String>();
		String listeCommuneStr = "";
		
		boolean isCollege = false;
		if (Util.notEmpty(categories)) {
			for (Category cat : categories) {
				if (cat.equals(collegeCategory) || cat.hasAncestor(collegeCategory)) {
					isCollege = true;
				}
			}
		}
		if(isCollege){ 
			  idCollege = obj.getId();
			  //System.out.println("idCollege : " + idCollege);
			  TreeSet<AdresseCollege> publications = channel.getAllPublicationSet(AdresseCollege.class, channel.getDefaultAdmin());
			  for (AdresseCollege pub : publications) {
				  //System.out.println("pub.getIdCollege() : " + pub.getIdCollege());
				  if(pub.getIdCollege().contains(idCollege)){
					  //System.out.println("pub OK : " + pub.getCommune());
					  if(!listeCommune.contains(pub.getCommune())){
						  listeCommune.add(pub.getCommune());
					  }
				  }
			  }
			  //System.out.println(listeCommune);
			  if(Util.notEmpty(listeCommune)){
				  if(listeCommune.size()>1){
					  Collections.sort(listeCommune);
				  }
				  listeCommuneStr = listeCommune.toString().substring(1, listeCommune.toString().length()-1);
			  }
			  
		  }
      %>
      <jalios:if predicate="<%= isCollege %>">
      	<div class="accueilEleve">
      	<%if(Util.notEmpty(listeCommuneStr)){%>
      	<%= glp("plugin.corporateidentity.place.college.accueilEleve",listeCommuneStr) %>
      	<%}else{ %>
      	<%= glp("plugin.corporateidentity.place.college.accueilEleve","") %>
      	<%} %>
      	</div>
      </jalios:if>
      <jalios:if predicate="<%= !isCollege && Util.notEmpty(obj.getAbstract()) %>">
      	<div class="link"><span class="spr-puce"></span><%= new ModalGenerator(obj, jcmsContext, glp("plugin.corporateidentity.place.more"), glp("plugin.corporateidentity.place.moreOn", obj.getTitle())).createModal() %></div>
      </jalios:if>      
    </div>
    <div class="span6 hidden-phone"><%
        try { 
          %><jsp:include page='<%= "/"+ToolsUtil.getIntegratedMapTemplate(request, "small") %>' flush="true" /><%
        } catch(FileNotFoundException fnfe) {
          logger.error("Google map template for integrated map was not found.", fnfe);
        }
    %></div>
  </div>
</div>