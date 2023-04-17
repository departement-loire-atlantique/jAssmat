<%@page import="fr.cg44.plugin.assmat.selector.UniteAgrementSelectorCommune"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.assmat.selector.RelaisMamSelectorIDSolis"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@ include file='/jcore/doInitPage.jsp' %>
<%@ include file='/jcore/portal/doPortletParams.jsp' %>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%@ taglib prefix="ds" tagdir="/WEB-INF/tags"%>

<%-- Affichage uniquement pour les RAM 
     Note SGU 01/2023 : le code concernant les assos peut être supprimé.
     Elles ne sont plus gérées dans le site Assmat
--%>
<%
if(Util.notEmpty(loggedMember) && (AssmatUtil.isMemberRAM(loggedMember))){
  String idPortalRAM = channel.getProperty("jcmsplugin.assmatplugin.socle.portal.colonne");
   
  Set<FicheLieu> setPlace = (Set<FicheLieu>) JcmsUtil.applyDataSelector(channel.getAllPublicationSet(FicheLieu.class, loggedMember), new RelaisMamSelectorIDSolis(loggedMember.getExtraData("extra.Member.jcmsplugin.assmatplugin.idsolis.lieu")));
%>

<jalios:if predicate="<%=Util.notEmpty(setPlace) %>">

	<section class="ds44-box ds44-theme">
	    <div class="ds44-innerBoxContainer">
	        <p role="heading" aria-level="2" class="ds44-box-heading"><trsb:glp key="ESPACE-RAM-MON-PROFIL" ></trsb:glp></p>
	        
	        <jalios:foreach name="place" type="FicheLieu" collection="<%=setPlace %>">
	
	            <jalios:if predicate="<%=Util.notEmpty(place.getTitle()) %>">
	                <p class="ds44-docListElem mtm"><strong><i class="icon icon-user ds44-docListIco" aria-hidden="true"></i><%=place.getTitle() %></strong></p>
	            </jalios:if>
	            
				<jalios:if predicate="<%=Util.notEmpty(place.getStreet()) %>">
	                <div class="ds44-docListElem mtm"><i class="icon icon-marker ds44-docListIco" aria-hidden="true"></i><%=place.getStreet() %><br /> <%=place.getZipCode() %> <%=place.getCity() %></div>
				</jalios:if>
				
				<jalios:if predicate="<%= Util.notEmpty(place.getPhones() ) %>">
	                <p class="ds44-docListElem mtm"><i class="icon icon-phone ds44-docListIco" aria-hidden="true"></i>
	                    <jalios:foreach name="itPhone" type="String" array="<%= place.getPhones()  %>"><%= (itCounter > 1)?" - ":""%><%= itPhone %></jalios:foreach>
	                </p>
				</jalios:if>
				
				<jalios:if predicate="<%= Util.notEmpty(place.getMails()) %>">
	    			<p class="ds44-docListElem mtm"><i class="icon icon-mail ds44-docListIco" aria-hidden="true"></i>
				       <jalios:foreach name="itMail" type="String" array="<%= place.getMails() %>">
				           <%= (itCounter > 1)? " - ":"" %><a href="mailto:<%= itMail %>" aria-label='<%= HttpUtil.encodeForHTMLAttribute(glp("jcmsplugin.socle.ficheaide.contacter-x-par-mail.label", place.getTitle(), itMail))%>'><%= itMail %></a>
				      </jalios:foreach>
	                </p>
				</jalios:if>
				
				<jalios:if predicate="<%= Util.notEmpty(place.getWebsites()) %>">
	                <p class="ds44-docListElem mtm"><i class="icon icon-link ds44-docListIco" aria-hidden="true"></i>
	                    <jalios:foreach name="itSite" type="String" array="<%= place.getWebsites() %>"><%= (itCounter > 1)? " - ":"" %>
	                        <a href="<%= itSite %>" title='<%= HttpUtil.encodeForHTMLAttribute(glp("jcmsplugin.socle.lien.site.nouvelonglet", itSite)) %>' target="_blank"><%= itSite %></a>
	                    </jalios:foreach>
				     </p>
				</jalios:if>
	
	        </jalios:foreach>
	        
	        <hr class="mbs" aria-hidden="true">
	
			<% 
			String idPortletPass= channel.getProperty("plugin.assmatplugin.portlet.ram.changepass");
		
			Publication publi = channel.getPublication(idPortletPass);
			
			//Catégorie Unité agrement
			Category categUA = channel.getCategory(channel.getProperty("plugin.assmatplugin.categorie.unite.agrement.id"));
			
			//On recupere la premiere fiche lieu (appartenance du membre)
			FicheLieu firstPlace = Util.getFirst(setPlace);
			
			//On créer un set de commune
			List<City> cityList = new ArrayList<City>();
			
			cityList.add(firstPlace.getCity());
			
			//On récupere les communes "autres"
			
			City[] tabCity = firstPlace.getCities();
			if(Util.notEmpty(tabCity)){
			cityList.addAll(Arrays.asList(tabCity));
			}
			Set<FicheLieu> setPlaceUA = (Set<FicheLieu>) JcmsUtil.applyDataSelector(channel.getAllDataSet(FicheLieu.class), new UniteAgrementSelectorCommune(cityList, categUA));
			Place uniteAgrement = null;
			if(Util.notEmpty(setPlaceUA)){
			  uniteAgrement = Util.getFirst(setPlaceUA);
			}
			%>
			
			<ul>
				<jalios:if predicate="<%=Util.notEmpty(uniteAgrement) %>">
					<%
					Publication portailContact = channel.getPublication(channel.getProperty("plugin.assmatplugin.portail.contact.coordram.id"));
					%>
					<li><a href="<%=portailContact.getDisplayUrl(userLocale)%>?idUA=<%=uniteAgrement.getId()%>"><trsb:glp key="ESPACE-RAM-ACTUALISER-COORD-MEMBER" ></trsb:glp></a></li>
				</jalios:if>
				
				<li><a href="<%=publi.getDisplayUrl(userLocale) %>?portal=<%=idPortalRAM%>"><trsb:glp key="ESPACE-RAM-CHANGE-PASS-MEMBER" ></trsb:glp></a></li>
				<li><a href="front/logout.jsp?redirect=<%=HttpUtil.encodeForURL(ServletUtil.getUrl(request))%>"><trsb:glp key="ESPACE-RAM-DECONNEXION-MEMBER" ></trsb:glp></a></li>
			</ul>
			
        </div>
    </section>

</jalios:if>
<%
}
%>