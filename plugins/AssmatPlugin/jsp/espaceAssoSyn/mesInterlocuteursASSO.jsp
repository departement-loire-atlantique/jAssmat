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
   Set<FicheLieu> setPlace = (Set<FicheLieu>) JcmsUtil.applyDataSelector(channel.getAllDataSet(FicheLieu.class), new RelaisMamSelectorIDSolis(loggedMember.getExtraData("extra.Member.jcmsplugin.assmatplugin.idsolis.lieu")));



%>
<jalios:if predicate="<%=Util.notEmpty(setPlace) %>">


<%

//Catégorie Unité agrement
Category categUA = channel.getCategory(channel.getProperty("plugin.assmatplugin.categorie.unite.agrement.id"));

//On recupere la premiere fiche lieu (appartenance du membre)
FicheLieu firstPlace = Util.getFirst(setPlace);

//On créer un set de commune
List<City> cityList = new ArrayList<City>();


cityList.add(firstPlace.getCommune());

//On récupere les communes "autres"

City[] tabCity = firstPlace.getCities();
if(Util.notEmpty(tabCity)){
  cityList.addAll(Arrays.asList(tabCity));
 }
Set<FicheLieu> setPlaceUA = (Set<FicheLieu>) JcmsUtil.applyDataSelector(channel.getAllDataSet(FicheLieu.class), new UniteAgrementSelectorCommune(cityList, categUA));

%>

<div class="blockMonProfilRAM blockInterlocuteursRAM">
<h2>Mes interlocuteurs</h2>
<jalios:foreach name="place" type="FicheLieu" collection="<%=setPlaceUA %>">


<jalios:if predicate="<%=Util.notEmpty(place.getTitle()) %>">
  <p class="name"><%=place.getTitle() %></p>
</jalios:if>
<jalios:if predicate="<%=Util.notEmpty(place.getLibelleDeVoie()) %>">
<p class="street"><%=place.getLibelleDeVoie() %><br>
<%=place.getCodePostal() %> <%=place.getCommune() %></p>
</jalios:if>
<jalios:if predicate="<%= Util.notEmpty(place.getTelephone() ) %>"><%
 %><div class="phone"><%
    %><div><span class="bold"><%= glp("plugin.corporateidentity.common.tel") %></span> <%
      %><%
        %><jalios:foreach name="itPhone" type="String" array="<%= place.getTelephone()  %>"><%= (itCounter > 1)?" - ":""%><%= itPhone %></jalios:foreach><%
      %><%
 %></div></div><%
%></jalios:if>

<jalios:if predicate="<%= Util.notEmpty(place.getEmail()) %>"><%
%><div class="mail"><%
   %><%
     %><p><%
       %><jalios:foreach name="itMail" type="String" array="<%= place.getEmail() %>"><%
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