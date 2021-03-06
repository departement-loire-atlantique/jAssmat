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
  
Set<Place> setPlace = (Set<Place>) JcmsUtil.applyDataSelector(channel.getAllDataSet(Place.class), new RelaisMamSelectorIDSolis(loggedMember.getExtraData("extra.Member.jcmsplugin.assmatplugin.idsolis.lieu")));
String idPortailRAM = channel.getProperty("plugin.assmatplugin.portal.ram.id");

%>
<jalios:if predicate="<%=Util.notEmpty(setPlace) %>">
<div class="blockMonProfilRAM">
<h2>Mon profil</h2>
<jalios:foreach name="place" type="Place" collection="<%=setPlace %>">


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

<% 

String idPortletPass= channel.getProperty("plugin.assmatplugin.portlet.ram.changepass");
Publication publi = channel.getPublication(idPortletPass);

//Cat??gorie Unit?? agrement
Category categUA = channel.getCategory(channel.getProperty("plugin.assmatplugin.categorie.unite.agrement.id"));

//On recupere la premiere fiche lieu (appartenance du membre)
Place firstPlace = Util.getFirst(setPlace);

//On cr??er un set de commune
List<City> cityList = new ArrayList<City>();


cityList.add(firstPlace.getCity());

//On r??cupere les communes "autres"

City[] tabCity = firstPlace.getCities();
if(Util.notEmpty(tabCity)){
cityList.addAll(Arrays.asList(tabCity));
}
Set<Place> setPlaceUA = (Set<Place>) JcmsUtil.applyDataSelector(channel.getAllDataSet(Place.class), new UniteAgrementSelectorCommune(cityList, categUA));
Place uniteAgrement = null;
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


<li><a href="<%=portailContact.getDisplayUrl(userLocale)%>?idUA=<%=uniteAgrement.getId()%>">Actualiser les coordonn??es</a></li>
</jalios:if>
<li><a href="<%=publi.getDisplayUrl(userLocale) %>?portal=<%=idPortailRAM%>">Changer de mot de passe</a></li>
<li><a href="front/logout.jsp">D??connexion</a></li>
</ul>
</div>
</jalios:if>
</div>
<%}
}
%>