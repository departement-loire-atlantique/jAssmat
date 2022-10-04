<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.commons.lang.time.DateUtils"%>
<%@ include file='/jcore/doInitPage.jspf' %>
<%@ include file='/jcore/portal/doPortletParams.jspf' %>
<%@ include file='/plugins/SoclePlugin/jsp/facettes/commonParamsFacettes.jspf' %>
<%@ taglib prefix="ds" tagdir="/WEB-INF/tags"%>
<%
  PortletFacetteNomAssmat obj = (PortletFacetteNomAssmat)portlet; 

  String rechercheId = (String) request.getAttribute("rechercheId");	
  String idFormElement = glp("jcmsplugin.socle.facette.form-element") + "-" + rechercheId + obj.getId();
  String labelChamp =  Util.notEmpty(obj.getLabel()) ? obj.getLabel() : JcmsUtil.glp(userLang, "cmsplugin.assmatplugin.facette.nom.default-label");

	

%>

<div class="ds44-form__container">
    
	<div class="ds44-posRel">
	    <label for="<%= idFormElement %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= labelChamp %></span></span></label>    
	    <input type="text" id="<%= idFormElement %>" name="nomassmat<%= idFormElement %>" class="ds44-inpStd" title="Champ standard"  />	    
	    <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= JcmsUtil.glp(userLang, "jcmsplugin.socle.facette.effacer-contenu-champ", labelChamp) %></span></button>
	</div>

</div>
		

