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

  request.setAttribute("isSelectFacette", true);

  PortletFacetteDispo obj = (PortletFacetteDispo)portlet; 
  String rechercheId = (String) request.getAttribute("rechercheId");	
  String idFormElement = glp("jcmsplugin.socle.facette.form-element") + "-" + rechercheId + obj.getId();
  String labelChamp =  Util.notEmpty(obj.getLabel()) ? obj.getLabel() : JcmsUtil.glp(userLang, "jcmsplugin.assmatplugin.facette.disponibilites.default-label");

  //Si le visteur est un RAM
  Boolean isRam = AssmatUtil.getMemberIsRam(loggedMember);
  Boolean isContribPower = AssmatUtil.getMemberIsContribPower(loggedMember);
  
%>

<div class="ds44-form__container">


	<div class="ds44-select__shape ds44-inpStd">
		<p class="ds44-selectLabel" aria-hidden="true"><%= labelChamp %></p>
		<div id="<%= idFormElement %>" data-name="dispo<%= idFormElement %>" class="ds44-js-select-checkbox ds44-selectDisplay" data-auto-submit="true"></div>
		<button type="button" id="button-<%= idFormElement %>" class="ds44-btnIco ds44-posAbs ds44-posRi ds44-btnOpen" aria-expanded="false"> <i class="icon icon-down icon--sizeL" aria-hidden="true"></i><span id="button-message-<%= idFormElement %>" class="visually-hidden"><%= labelChamp %></span></button>
		<button class="ds44-reset" type="button"> <i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= JcmsUtil.glp(userLang, "jcmsplugin.socle.facette.effacer-contenu-champ", labelChamp) %></span></button>
	</div>




	<div class="ds44-select-container hidden">
		<div class="ds44-flex-container ds44--m-padding">
			<button class="ds44-btnStd ds44-bgGray ds44-btnStd--plat ds44-fg1"
				type="button" aria-describedby="button-message-<%= idFormElement %>">
				<span class="ds44-btnInnerText"><%= JcmsUtil.glp(userLang, "jcmsplugin.socle.tout-cocher") %></span><i
					class="icon icon-check icon--medium" aria-hidden="true"></i>
			</button>
			<button
				class="ds44-btnStd ds44-bgGray ds44-btnStd--plat ds44-fg1 ds44-border-left--light"
				type="button" aria-describedby="button-message-<%= idFormElement %>">
				<span class="ds44-btnInnerText"><%= JcmsUtil.glp(userLang, "jcmsplugin.socle.tout-decocher") %></span><i
					class="icon icon-cross icon--medium" aria-hidden="true"></i>
			</button>
		</div>
		<div class="ds44-listSelect">
			<ul class="ds44-list" id="listbox-<%= idFormElement %>">
				<li class="ds44-select-list_elem">

					<div class="ds44-form__container ds44-checkBox-radio_list ">
						<input type="checkbox" id="name-check-<%= idFormElement %>-1"
							name="<%= idFormElement %>-1" value="withDispo" class="ds44-checkbox" /><label
							for="name-check-<%= idFormElement %>-1" class="ds44-boxLabel"
							id="name-check-label-<%= idFormElement %>-1"><trsb:glp key="RECHERCHE-AM-FILTER-WITH-DISPO" /></label>
					</div>
				</li>
				
				<li class="ds44-select-list_elem">

					<div class="ds44-form__container ds44-checkBox-radio_list ">
						<input type="checkbox" id="name-check-<%= idFormElement %>-2"
							name="<%= idFormElement %>-2" value="withDispoFuture" class="ds44-checkbox" /><label
							for="name-check-<%= idFormElement %>-2" class="ds44-boxLabel"
							id="name-check-label-<%= idFormElement %>-2"><trsb:glp key="RECHERCHE-AM-FILTER-WITH-DISPO-FUTUR" /></label>
					</div>
				</li>
				
				<li class="ds44-select-list_elem">
					<div class="ds44-form__container ds44-checkBox-radio_list ">
						<input type="checkbox" id="name-check-<%= idFormElement %>-3"
							name="<%= idFormElement %>-3" value="withNonDispo" class="ds44-checkbox" /><label
							for="name-check-<%= idFormElement %>-3" class="ds44-boxLabel"
							id="name-check-label-<%= idFormElement %>-3"><trsb:glp key="RECHERCHE-AM-FILTER-NO-DISPO-CONTACT" /></label>
					</div>

				</li>
				
				<jalios:if predicate="<%= (isRam || isContribPower) %>">
					<li class="ds44-select-list_elem">
						<div class="ds44-form__container ds44-checkBox-radio_list ">
							<input type="checkbox" id="name-check-<%= idFormElement %>-4"
								name="<%= idFormElement %>-4" value="withDispoNonRenseigne" class="ds44-checkbox" /><label
								for="name-check-<%= idFormElement %>-4" class="ds44-boxLabel"
								id="name-check-label-<%= idFormElement %>-4"><trsb:glp key="RECHERCHE-AM-FILTER-WITH-DISPO-NON-RENSEIGNEES" /></label>
						</div>
					</li>
				</jalios:if>
				
			</ul>
		</div>
		
		<button type="button" class="ds44-fullWBtn ds44-btnSelect ds44-theme"
			title='<%= glp("jcmsplugin.socle.facette.cat-lie.valider-selection.label", labelChamp) %>'>
			<span class="ds44-btnInnerText"><%= JcmsUtil.glp(userLang, "jcmsplugin.socle.valider") %></span><i
				class="icon icon-long-arrow-right ds44-noLineH" aria-hidden="true"></i>
		</button>
	</div>


</div>




