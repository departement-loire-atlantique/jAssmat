<%@page import="fr.cg44.plugin.assmat.managers.AssmatSearchDAO"%>
<%@page import="fr.cg44.plugin.assmat.selector.UniteAgrementSelectorCommune"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.assmat.selector.RelaisMamSelectorIDSolis"%>
<%@ include file='/jcore/doInitPage.jsp' %>
<%@ include file='/jcore/portal/doPortletParams.jsp' %>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>

<%-- La portlet ne s'affiche qu'en mode connecté --> loggeMember != null --%>
<%
if(!AssmatUtil.isMemberRAM(loggedMember)){
  return;
}
  
//On recupere les RAM du membre 
Set<FicheLieu> setPlace = (Set<FicheLieu>) JcmsUtil.applyDataSelector(channel.getAllDataSet(FicheLieu.class), new RelaisMamSelectorIDSolis(loggedMember.getExtraData("extra.Member.jcmsplugin.assmatplugin.idsolis.lieu")));

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
	
	if(Util.notEmpty(tabCity)) {
	  cityList.addAll(Arrays.asList(tabCity));
    }
  
	Set<FicheLieu> setPlaceUA = null;
	Integer idRam = Integer.parseInt(loggedMember.getExtraData("extra.Member.jcmsplugin.assmatplugin.idsolis.lieu").replaceAll("RAM_", "")); 
	String idSolisUa = "UA_" + AssmatSearchDAO.getCorresRamUa(idRam) ;
	
	setPlaceUA = (Set<FicheLieu>) JcmsUtil.applyDataSelector(channel.getAllDataSet(FicheLieu.class), new RelaisMamSelectorIDSolis(idSolisUa));
	  
	%>
	
	<section class="ds44-box ds44-bgGray">
	    <div class="ds44-innerBoxContainer">
	        <p role="heading" aria-level="2" class="ds44-box-heading"><trsb:glp key="ESPACE-RAM-MES-INTERLOCUTEURS" ></trsb:glp></p>
	        
	        <jalios:foreach name="place" type="FicheLieu" collection="<%=setPlaceUA %>">
		
	            <p><trsb:glp key="ESPACE-RAM-A-UNITE-AGREMENT" ></trsb:glp></p>
				
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
	        </jalios:foreach>
	
	        <p><%= AssmatUtil.getMessage("CONTACT-CAF-INTERLOCUTEUR-RAM-1",true) %></p>
	        <p><%= AssmatUtil.getMessage("CONTACT-CAF-INTERLOCUTEUR-RAM-2",true) %></p>
	
	    </div>
	</section>
	

</jalios:if>
