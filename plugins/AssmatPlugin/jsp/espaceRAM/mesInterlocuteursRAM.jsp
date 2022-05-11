<%@page import="fr.cg44.plugin.assmat.managers.AssmatSearchDAO"%>
<%@page import="fr.cg44.plugin.assmat.selector.UniteAgrementSelectorCommune"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.assmat.selector.RelaisMamSelectorIDSolis"%>
<%@ include file='/jcore/doInitPage.jsp' %>
<%@ include file='/jcore/portal/doPortletParams.jsp' %>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>

<%

  
  if(Util.notEmpty(loggedMember)){
    String idPortalRAM = channel.getProperty("plugin.assmatplugin.portal.ram.id");
    String idPortalSyndicat = channel.getProperty("plugin.assmatplugin.portal.asso.id");

    String idCurrentPortal = portal.getId();
    if( AssmatUtil.isMemberRAM(loggedMember) || AssmatUtil.isMemberAsso(loggedMember) ){
      
      boolean isMemberRam = AssmatUtil.isMemberRAM(loggedMember);
    
   //On recupere les RAM du membre 
   Set<Place> setPlace = (Set<Place>) JcmsUtil.applyDataSelector(channel.getAllDataSet(Place.class), new RelaisMamSelectorIDSolis(loggedMember.getExtraData("extra.Member.jcmsplugin.assmatplugin.idsolis.lieu")));



%>

<jalios:if predicate="<%=Util.notEmpty(setPlace) %>">


<%

//Catégorie Unité agrement
Category categUA = channel.getCategory(channel.getProperty("plugin.assmatplugin.categorie.unite.agrement.id"));


//On recupere la premiere fiche lieu (appartenance du membre)
Place firstPlace = Util.getFirst(setPlace);

//On créer un set de commune
List<City> cityList = new ArrayList<City>();


cityList.add(firstPlace.getCity());

//On récupere les communes "autres"

City[] tabCity = firstPlace.getCities();
if(Util.notEmpty(tabCity)){
  cityList.addAll(Arrays.asList(tabCity));
 }
Set<Place> setPlaceUA = null;


  //setPlaceUA = (Set<Place>) JcmsUtil.applyDataSelector(channel.getAllDataSet(Place.class), new UniteAgrementSelectorCommune(cityList, categUA));   

  Integer idRam = Integer.parseInt(loggedMember.getExtraData("extra.Member.jcmsplugin.assmatplugin.idsolis.lieu").replaceAll("RAM_", "")); 
  String idSolisUa = "UA_" + AssmatSearchDAO.getCorresRamUa(idRam) ;

  setPlaceUA = (Set<Place>) JcmsUtil.applyDataSelector(channel.getAllDataSet(Place.class), new RelaisMamSelectorIDSolis(idSolisUa));
  
%>


<div class="blockInterlocuteursRAM">
	<h2><trsb:glp key="ESPACE-RAM-MES-INTERLOCUTEURS" ></trsb:glp></h2>
	<jalios:foreach name="place" type="Place" collection="<%=setPlaceUA %>">
	
			<p class="enteteTitle bold"><trsb:glp key="ESPACE-RAM-A-UNITE-AGREMENT" ></trsb:glp></p>
			<jalios:if predicate="<%=Util.notEmpty(place.getTitle()) %>">
			  <p class="name"><%=place.getTitle() %></p>
			</jalios:if>
			<jalios:if predicate="<%=Util.notEmpty(place.getStreet()) %>">
			<p class="street"><%=place.getStreet() %></p>
			<p><%=place.getZipCode() %> <%=place.getCity() %></p>
			</jalios:if>
			<jalios:if predicate="<%= Util.notEmpty(place.getPhones() ) %>"><%
			 %><div class="phone"><%
			    %><div><span class="bold"><%= glp("plugin.corporateidentity.common.tel") %></span> <%
			      %><%
			        %><jalios:foreach name="itPhone" type="String" array="<%= place.getPhones()  %>"><%= (itCounter > 1)?" - ":""%><%= itPhone %></jalios:foreach><%
			      %><%
			 %></div></div><%
			%></jalios:if>
			
			<jalios:if predicate="<%= Util.notEmpty(place.getMails()) %>"><%
			%><div class="mail"><%
			   %><%
			     %><p><%
			       %><jalios:foreach name="itMail" type="String" array="<%= place.getMails() %>"><%
			           %><%= (itCounter > 1)? " - ":"" %><a class="bold" href="mailto:<%= itMail %>">Courriel</a><%
			       %></jalios:foreach><%
			     %></p><%
			%></div><%
			%></jalios:if>
			
			
			
			
			<br><br>
	</jalios:foreach>
	<%if(isMemberRam){ %>
	 <p><%= AssmatUtil.getMessage("CONTACT-CAF-INTERLOCUTEUR-RAM-1",true) %></p>
	 
	  <p><%= AssmatUtil.getMessage("CONTACT-CAF-INTERLOCUTEUR-RAM-2",true) %></p>
	<%}else{%>
	<p><%= AssmatUtil.getMessage("CONTACT-RAM-INTERLOCUTEUR-RAM",true) %></p>
	<%} %>
	
</div>
	

</jalios:if>

<%}
}
%>