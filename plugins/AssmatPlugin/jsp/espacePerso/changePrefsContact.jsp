<%@page import="fr.cg44.plugin.assmat.AssmatUtil.SelectionLogin"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@ include file='/jcore/doInitPage.jsp' %>
<%@ include file='/jcore/portal/doPortletParams.jsp' %>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>

<%
PortletJsp box = (PortletJsp) portlet;

ProfilManager profilMngr = ProfilManager.getInstance();
ProfilASSMAT profil = (ProfilASSMAT) profilMngr.getProfilASSMAT(channel.getCurrentLoggedMember());

if(Util.isEmpty(profil)){
	sendForbidden(request, response);
	return;
}

String choixCanalCommParam = null;

if(Util.notEmpty(request.getParameter("choixCanalComm[0]"))){
  choixCanalCommParam = request.getParameter("choixCanalComm[0]");
}else if(Util.notEmpty(request.getParameter("choixCanalComm"))){
  choixCanalCommParam = request.getParameter("choixCanalComm");
}

%>

<jsp:useBean id='formHandler' scope='page' class='fr.cg44.plugin.assmat.handler.ProfilPrefsContactHandler'>
  <jsp:setProperty name='formHandler' property='request' value='<%= request %>'/>
  <jsp:setProperty name='formHandler' property='response' value='<%= response %>'/>
  <jsp:setProperty name='formHandler' property="noRedirect" value="true" />
  <jsp:setProperty name='formHandler' property="profil" value='<%= profil %>' />
  <jsp:setProperty name='formHandler' property="member" value='<%= loggedMember %>' />
  <jsp:setProperty name='formHandler' property="choixCanalComm" value='<%= choixCanalCommParam %>' />
  <jsp:setProperty name='formHandler' property='*' />  
</jsp:useBean>


<%
if (formHandler.validate()) {
  //return;
}

String emailAssmat = loggedMember.getEmail() != null ? loggedMember.getEmail() : "" ;
String telAssmat = profil.getAuthor().getMobile() != null ? profil.getAuthor().getMobile() : "" ;
String canalEnvoi = profil.getCanalDeCommunicationSite();
SelectionLogin typeLogin = SelectionLogin.getTypeLoginByValue(profil.getTypeLogin());
int numeroDossierAssmat = profil.getNum_agrement(); 

boolean isCanalMail = AssmatUtil.SelectionPreferenceReception.MAIL.getValue().equals(canalEnvoi);
boolean isCanalTel = AssmatUtil.SelectionPreferenceReception.TELEPHONE.getValue().equals(canalEnvoi);

//UUID unique pour les champs
String uuid = UUID.randomUUID().toString();
%>

<%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/header.jspf' %>
<%@ include file='/plugins/SoclePlugin/jsp/doMessageBoxCustom.jspf' %>

  <form method="post" action="<%= ServletUtil.getResourcePath(request) %>" 
    name="formContact" id="formContact" data-no-encoding="true">
    
    <%-- Préférence de type d'envoi --%>
        <% uuid = UUID.randomUUID().toString(); %>
        <div id="form-element-<%= uuid %>" data-name="choixCanalComm" class="ds44-form__radio_container ds44-form__container"  data-required="true">
            <p aria-level="2" class="ds44-box-heading"><%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.typeenvoi") %><sup aria-hidden="true">*</sup></p>
            <%--                 <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p> --%>
            <div class="ds44-form__container ds44-checkBox-radio_list inbl">
                <input type="radio" name="choixCanalComm" value="<%= AssmatUtil.SelectionPreferenceReception.MAIL.getValue() %>" id="name-radio-form-element-<%= uuid %>-mail" <%if(isCanalMail){ %>checked="checked"<%} %> class="ds44-radio" required aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-mail" for="name-radio-form-element-<%= uuid %>-mail" class="ds44-radioLabel"><%=glp("jcmsplugin.assmatplugin.inscription.champ.typeenvoi.value.mail") %></label>
            </div>
            <div class="ds44-form__container ds44-checkBox-radio_list inbl">
                <input type="radio" name="choixCanalComm" value="<%= AssmatUtil.SelectionPreferenceReception.TELEPHONE.getValue() %>" id="name-radio-form-element-<%= uuid %>-telephone" <%if(isCanalTel){ %>checked="checked"<%} %> class="ds44-radio" required aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-telephone" for="name-radio-form-element-<%= uuid %>-telephone" class="ds44-radioLabel"><%=glp("jcmsplugin.assmatplugin.inscription.champ.typeenvoi.value.sms") %></label>
            </div>
        </div>
        <div class="mbl">
            <p>
                <b>
                    <trsb:glp key="CONTACTS-EXEMPLES-HTML-TITLE" ></trsb:glp>
                </b>
                <br>
                <i>
                    <trsb:glp key="CONTACTS-EXEMPLES-HTML" ></trsb:glp>
                </i>
            </p>
        </div>

    
    <p class="ds44-box-heading"><%= glp("jcmsplugin.assmatplugin.espacepro.modifiercoords") %></p>

		<%-- Email --%>
		<% uuid = UUID.randomUUID().toString(); %>
		<div class="ds44-form__container">
		    <!--                <p aria-level="2" class="h4-like"> -->
		    <%--                  <%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.email") %> --%>
		    <!--                </p> -->
		    <div class="w90 u-fl">
		        <div class="ds44-posRel">
		            <label for="form-element-<%= uuid %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.email") %></span></span></label>
		            <input type="email" id="form-element-<%= uuid %>" name="email" value="<%=emailAssmat %>" class="ds44-inpStd" title='<%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.email") %>' autocomplete="email" aria-describedby="explanation-form-element-<%= uuid %>" data-bkp-aria-describedby="explanation-form-element-<%= uuid %>">
		            <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", glp("jcmsplugin.assmatplugin.inscription.champ.lbl.email")) %></span></button>
		        </div>
		        <div class="ds44-field-information" aria-live="polite">
		            <ul class="ds44-field-information-list ds44-list">
		                <li id="explanation-form-element-<%= uuid %>" class="ds44-field-information-explanation"><%= glp("jcmsplugin.socle.form.exemple.email") %></li>
		            </ul>
		        </div>
		    </div>
		    <div class="w10 u-fl">
		        <span style="margin-left: 10px;" class="simpletooltip_container" data-hashtooltip-id="<%= uuid %>">
		            <button type="button" class="js-simple-tooltip button" data-is-initialized="true" data-simpletooltip-content-id="tooltip-case_<%= uuid %>" data-hashtooltip-id="<%= uuid %>" aria-describedby="label_simpletooltip_<%= uuid %>">
		            <i class="icon icon-help" aria-hidden="true"></i><span class="visually-hidden">Aide : <%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.email") %></span>
		            </button>
		            <span id="label_simpletooltip_<%= uuid %>" class="simpletooltip js-simple-tooltip bottom" role="tooltip" data-hashtooltip-id="<%= uuid %>" aria-hidden="true">
		                <trsb:glp key="CONTACTS-EMAIL-BULLE-HTML" ></trsb:glp>
		            </span>
		        </span>
		    </div>
		</div>
		
		<div class="u-clear"></div>
		
		<p class="ds44-box-heading" style="padding-bottom: 20px">et/ou</p>
		<%-- Téléphone --%>
		<% uuid = UUID.randomUUID().toString(); %>
		<div class="ds44-form__container">
		    <!--                <p aria-level="2" class="h4-like"> -->
		    <%--                  <%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.mobile") %>                   --%>
		    <!--                </p> -->
		    <div class="w90 u-fl">
		        <div class="ds44-posRel">
		            <label for="form-element-<%= uuid %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.mobile") %><sup aria-hidden="true">*</sup></span></span></label>
		            <input type="text" id="form-element-<%= uuid %>" name="telMobile" value="<%=telAssmat %>" class="ds44-inpStd" title='<%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.mobile") %>' autocomplete="tel-national" aria-describedby="explanation-form-element-<%= uuid %>" data-bkp-aria-describedby="explanation-form-element-<%= uuid %>">
		            <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", glp("jcmsplugin.assmatplugin.inscription.champ.lbl.mobile")) %></span></button>
		        </div>
		        <div class="ds44-field-information" aria-live="polite">
		            <ul class="ds44-field-information-list ds44-list">
		                <li id="explanation-form-element-<%= uuid %>" class="ds44-field-information-explanation"><%= glp("jcmsplugin.socle.form.exemple.tel") %></li>
		            </ul>
		        </div>
		    </div>
		    
		    <div class="w10 u-fl">
		        <span style="margin-left: 10px;" class="simpletooltip_container" data-hashtooltip-id="<%= uuid %>">
		            <button type="button" class="js-simple-tooltip button" data-is-initialized="true" data-simpletooltip-content-id="tooltip-case_<%= uuid %>" data-hashtooltip-id="<%= uuid %>" aria-describedby="label_simpletooltip_<%= uuid %>">
		            <i class="icon icon-help" aria-hidden="true"></i><span class="visually-hidden">Aide : <%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.email") %></span>
		            </button>
		            <span id="label_simpletooltip_<%= uuid %>" class="simpletooltip js-simple-tooltip bottom" role="tooltip" data-hashtooltip-id="<%= uuid %>" aria-hidden="true">
		                <trsb:glp key="CONTACTS-TEL-BULLE-HTML" ></trsb:glp>
		            </span>
		        </span>
		    </div>
		</div>
		
		<div class="u-clear"></div>
            
    <div class="ds44-form__container">
        <button data-send-native class="ds44-btnStd ds44-btn--invert" data-submit-value="true" data-submit-key="opCreate" title='<trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp>'><trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp></button>
        <input type="hidden" name="noSendRedirect" value="true" data-technical-field/> 
        <input type="hidden" name="opUpdate" value="true" />
        <input type="hidden" name="csrftoken" value="<%= HttpUtil.getCSRFToken(request) %>" data-technical-field>
        <input type="hidden" name="numeroAgrement" value="<%= numeroDossierAssmat %>" data-technical-field/>
    </div>
    
</form>