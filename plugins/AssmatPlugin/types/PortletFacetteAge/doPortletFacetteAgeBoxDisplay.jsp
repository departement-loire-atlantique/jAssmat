<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.commons.lang.time.DateUtils"%>
<%@ include file='/jcore/doInitPage.jspf' %>
<%@ include file='/jcore/portal/doPortletParams.jspf' %>
<%@ include file='/plugins/SoclePlugin/jsp/facettes/commonParamsFacettes.jspf' %>
<%@ taglib prefix="ds" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>

<%
  PortletFacetteAge obj = (PortletFacetteAge)portlet; 
  String rechercheId = (String) request.getAttribute("rechercheId");
  String idFormElement = glp("jcmsplugin.socle.facette.form-element") + "-" + rechercheId + obj.getId();
  String labelChamp =  Util.notEmpty(obj.getLabel()) ? obj.getLabel() : JcmsUtil.glp(userLang, "jcmsplugin.socle.facette.age.default-label");


  Category categAge = channel.getCategory("$jcmsplugin.assmatplugin.categ.trancheage");
  TreeSet<Category> ageSet = new TreeSet<Category>(Category.getDeepOrderComparator(channel.getLanguage()));
  ageSet.addAll(categAge.getChildrenSet());
  
%>

<div class="ds44-form__container">

	<div class="ds44-select__shape ds44-inpStd">
        <p class="ds44-selectLabel" aria-hidden="true"><%= labelChamp %></p>
        <div id="<%= idFormElement %>" data-name="age<%= idFormElement %>" class="ds44-js-select-standard ds44-selectDisplay" ></div>
        <button type="button" id="button-<%= idFormElement %>" class="ds44-btnIco ds44-posAbs ds44-posRi ds44-btnOpen" aria-expanded="false"   ><i class="icon icon-down icon--sizeL" aria-hidden="true"></i><span id="button-message-<%= idFormElement %>" class="visually-hidden"><%= labelChamp %></span></button>
        <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= JcmsUtil.glp(userLang, "jcmsplugin.socle.facette.effacer-contenu-champ", labelChamp) %></span></button>    
    </div>
    

    <div class="ds44-select-container hidden">
        <div class="ds44-listSelect">
            <ul class="ds44-list" role="listbox" id="listbox-<%= idFormElement %>" aria-labelledby="button-message-<%= idFormElement %>" >                
                <jalios:foreach name="itCat" type="Category" collection="<%= ageSet %>">
                  <li class="ds44-select-list_elem" data-value='<%= itCat.getExtraData("extra.Category.jcmsplugin.assmatplugin.search.mapping") %>' tabindex="0" role="option">
                    <%= itCat.getName() %>
                  </li>
                </jalios:foreach>              
            </ul>
        </div>
    </div>

    

</div>




