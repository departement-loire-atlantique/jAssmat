<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.commons.lang.time.DateUtils"%>
<%@ include file='/jcore/doInitPage.jspf' %>
<%@ include file='/jcore/portal/doPortletParams.jspf' %>
<%@ include file='/plugins/SoclePlugin/jsp/facettes/commonParamsFacettes.jspf' %>
<%@ taglib prefix="ds" tagdir="/WEB-INF/tags"%>
<%
  request.setAttribute("isSelectFacette", true);

  PortletFacetteMois obj = (PortletFacetteMois)portlet; 

  String rechercheId = (String) request.getAttribute("rechercheId");	
  String idFormElement = glp("jcmsplugin.socle.facette.form-element") + "-" + rechercheId + obj.getId();
  String labelChamp =  Util.notEmpty(obj.getLabel()) ? obj.getLabel() : JcmsUtil.glp(userLang, "jcmsplugin.assmatplugin.facette.mois.default-label");

	
  java.util.Date dateRef = new java.util.Date();
  SimpleDateFormat formatterTLE = new SimpleDateFormat("MMMM yyyy");
  java.util.Date dateTLE = new java.util.Date(dateRef.getTime());
%>

<div class="ds44-form__container">
    
    <div class="ds44-select__shape ds44-inpStd">
        <p class="ds44-selectLabel" aria-hidden="true"><%= obj.getLabel() %></p>
        <div id="<%= idFormElement %>" data-name="mois<%= idFormElement %>" class="ds44-js-select-standard ds44-selectDisplay" ></div>
        <button type="button" id="button-<%= idFormElement %>" class="ds44-btnIco ds44-posAbs ds44-posRi ds44-btnOpen" aria-expanded="false"   ><i class="icon icon-down icon--sizeL" aria-hidden="true"></i><span id="button-message-<%= idFormElement %>" class="visually-hidden"><%= labelChamp %></span></button>
        <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= JcmsUtil.glp(userLang, "jcmsplugin.socle.facette.effacer-contenu-champ", labelChamp) %></span></button>
    
    </div>
    
    <div class="ds44-select-container hidden">
        <div class="ds44-listSelect">
            <ul class="ds44-list" role="listbox" id='listbox-<%= idFormElement %>' aria-labelledby='button-message-<%= idFormElement %>' >                
                <%
                for (int iterator = 1; iterator < 13; iterator++) {
                %>
                  <li class="ds44-select-list_elem" data-value="<%=Long.toString(dateTLE.getTime()).substring(0, Long.toString(dateTLE.getTime()).length() - 8) + "00000000"%>" tabindex="0" role="option" >
                    <%=StringUtils.capitalize(formatterTLE.format(dateTLE))%>
                  </li>
				<%
				  dateTLE = DateUtils.addMonths(dateTLE, 1);
				}
				%>
                
            </ul>
        </div>
    </div>

</div>
		

