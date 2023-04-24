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
  
Set<FicheLieu> setPlace = (Set<FicheLieu>) JcmsUtil.applyDataSelector(channel.getAllDataSet(FicheLieu.class), new RelaisMamSelectorIDSolis(loggedMember.getExtraData("extra.Member.jcmsplugin.assmatplugin.idsolis.lieu")));
String idPortailRAM = channel.getProperty("plugin.assmatplugin.portal.ram.id");

%>
<jalios:if predicate="<%=Util.notEmpty(setPlace) %>">
<div class="blockMonProfilRAM">
<h2>Mon profil</h2>
<jalios:foreach name="place" type="Place" collection="<%=setPlace %>">


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

<% 

String idPortletPass= channel.getProperty("plugin.assmatplugin.portlet.ram.changepass");
Publication publi = channel.getPublication(idPortletPass);

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
FicheLieu uniteAgrement = null;
if(Util.notEmpty(setPlaceUA)){
  uniteAgrement = Util.getFirst(setPlaceUA);
}
%>




<div class="linkRAM">
<ul>
<jalios:if predicate="<%=Util.notEmpty(uniteAgrement) %>">

<%
Publication portailContact = channel.getPublication(channel.getProperty("plugin.assmatplugin.portail.contact.coordram.id"));


%>


<li><a href="<%=portailContact.getDisplayUrl(userLocale)%>?idUA=<%=uniteAgrement.getId()%>">Actualiser les coordonnées</a></li>
</jalios:if>
<li><a href="<%=publi.getDisplayUrl(userLocale) %>?portal=<%=idPortailRAM%>">Changer de mot de passe</a></li>
<li><a href="front/logout.jsp">Déconnexion</a></li>
</ul>
</div>
</jalios:if>
</div>
<%}
}
%>