<%@page import="fr.cg44.plugin.assmat.selector.UniteAgrementSelectorCommune"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.assmat.selector.RelaisMamSelectorIDSolis"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@ include file='/jcore/doInitPage.jsp' %>
<%@ include file='/jcore/portal/doPortletParams.jsp' %>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>

<%
if(Util.notEmpty(loggedMember)){
  
  if(AssmatUtil.isMemberASSO(loggedMember)){
    
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
Set<Place> setPlaceUA = (Set<Place>) JcmsUtil.applyDataSelector(channel.getAllDataSet(Place.class), new UniteAgrementSelectorCommune(cityList, categUA));

%>

<div class="blockMonProfilRAM blockInterlocuteursRAM">
<h2>Mes interlocuteurs</h2>
<jalios:foreach name="place" type="Place" collection="<%=setPlaceUA %>">


<jalios:if predicate="<%=Util.notEmpty(place.getTitle()) %>">
  <p class="name"><%=place.getTitle() %></p>
</jalios:if>
<jalios:if predicate="<%=Util.notEmpty(place.getStreet()) %>">
<p class="street"><%=place.getStreet() %><br>
<%=place.getZipCode() %> <%=place.getCity() %></p>
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
           %><%= (itCounter > 1)? " - ":"" %><a href="mailto:<%= itMail %>"><%= itMail %></a><%
       %></jalios:foreach><%
     %></p><%
%></div><%
%></jalios:if>





</jalios:foreach>

 <p><%= AssmatUtil.getMessage("CONTACT-CAF-INTERLOCUTEUR-RAM-1",true) %></p>
 
  <p><%= AssmatUtil.getMessage("CONTACT-CAF-INTERLOCUTEUR-RAM-2",true) %></p>

</jalios:if>
</div>
<%}
}
%>