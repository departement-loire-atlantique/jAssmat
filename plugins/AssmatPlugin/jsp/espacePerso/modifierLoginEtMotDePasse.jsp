<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil.SelectionLogin"%>

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

%>
<jsp:useBean id='formHandler' scope='page' class='fr.cg44.plugin.assmat.handler.ProfilLoginMdpHandler'>
  <jsp:setProperty name='formHandler' property='request' value='<%= request %>'/>
  <jsp:setProperty name='formHandler' property='response' value='<%= response %>'/>
  <jsp:setProperty name='formHandler' property="noRedirect" value="true" />
  <jsp:setProperty name='formHandler' property="profil" value='<%= profil %>' />
  <jsp:setProperty name='formHandler' property="member" value='<%= loggedMember %>' />
  <jsp:setProperty name='formHandler' property='*' />
</jsp:useBean>


<%
if (formHandler.validate()) {
  	return; 
  } 


  if(Util.notEmpty(profil)){
    
   boolean loginMail=false;
   boolean loginTel= false;
   boolean loginAgrement= false;
   
  		String emailAssmat = loggedMember.getEmail();
  		String telephoneMobileAssmat = profil.getAuthor().getMobile();
  		SelectionLogin typeLogin = SelectionLogin.getTypeLoginByValue(profil.getTypeLogin());
  		int numeroDossierAssmat = profil.getNum_agrement();
  %>

<%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/header.jspf' %>
<%@ include file='/plugins/SoclePlugin/jsp/doMessageBoxCustom.jspf' %>

<form method="post" action="<%= ServletUtil.getResourcePath(request) %>"  name="formContact" id ="formContact" data-no-encoding="true">

       <h3 class="h3-like"><%=glp("jcmsplugin.assmatplugin.espaceperso.modiflogin") %></h3>
              
            <%if(SelectionLogin.MAIL.equals(typeLogin)){ %>
            <p><b><%=glp("jcmsplugin.assmatplugin.espaceperso.utilisemail") %></b> <%=emailAssmat %> </p>
            
            <%}else if(SelectionLogin.NUMERO_DOSSIER.equals(typeLogin)){ %>
            <p><b><%=glp("jcmsplugin.assmatplugin.espaceperso.utiliseagr") %></b> <%=numeroDossierAssmat %> </p>
            
            <%} else if(SelectionLogin.TELEPHONE.equals(typeLogin)){ %>
            <p><b><%=glp("jcmsplugin.assmatplugin.espaceperso.utilisetel") %></b> <%=telephoneMobileAssmat %> </p>
            <%} %>
            
            <p class="info"><%=glp("jcmsplugin.assmatplugin.espaceperso.declaremail") %></p>
            <p class="info ds44-mt2"><b><%=glp("jcmsplugin.assmatplugin.espaceperso.declarelog") %></b></p>
            <% String uuid = UUID.randomUUID().toString(); %>
                <div id="form-element-<%= uuid %>" data-name="choixLogin" class="ds44-form__radio_container ds44-form__container" data-required="true">
                  <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
                  <div class="ds44-flex">
	                  <div class="ds44-fg1 ds44-txtRight ds44-mt1">
	                    <p class=""><trsb:glp key="LIBELLE-PRERENCE-UTILISATION-LOGIN-HTML" ></trsb:glp></p>
	                  </div>
		              <% 
		                Member mbrMailFind = channel.getMemberFromLogin(emailAssmat);
		                Member mbrMobileFind = channel.getMemberFromLogin(telephoneMobileAssmat);
		              %>
		              
		              <div class="ds44-ml-std ds44-fg3">
		               <jalios:if predicate="<%= Util.notEmpty(emailAssmat) && ( Util.isEmpty(mbrMailFind) || JcmsUtil.isSameId(mbrMailFind, loggedMember))%>">
		               <div class="ds44-form__container ds44-checkBox-radio_list ">
		                  <input <%if(SelectionLogin.MAIL.equals(typeLogin)){%> checked="checked" <%} %> type="radio" name="choixLogin" value="2" id="name-radio-form-element-<%= uuid %>-mail" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" />
		                  <label id="label-radio-form-element-<%= uuid %>-mail" for="name-radio-form-element-<%= uuid %>-mail" class="ds44-radioLabel">
		                    <%=glp("jcmsplugin.assmatplugin.espaceperso.monmail") %> <%=emailAssmat%>
		                  </label>
		                  <span class="simpletooltip_container" data-hashtooltip-id="<%= uuid %>-mail-aide">
                              <button type="button" class="js-simple-tooltip button" data-is-initialized="true" data-simpletooltip-content-id="tooltip-case_<%= uuid %>-mail-aide" data-hashtooltip-id="<%= uuid %>-mail-aide" aria-describedby="label_simpletooltip_<%= uuid %>-mail-aide">
                              <i class="icon icon-help" aria-hidden="true"></i><span class="visually-hidden">Aide : <%=glp("jcmsplugin.assmatplugin.espaceperso.monmail") %> <%=emailAssmat%></span>
                              </button><span id="label_simpletooltip_<%= uuid %>-mail-aide" class="simpletooltip js-simple-tooltip bottom" role="tooltip" data-hashtooltip-id="<%= uuid %>-mail-aide" aria-hidden="true">
                              <trsb:glp key="HELP-PREFERENCE-LOGIN-MAIL-HTML" ></trsb:glp>
                              </span>
                          </span>
		               </div>
		               </jalios:if>
		               <jalios:if predicate="<%= Util.notEmpty(telephoneMobileAssmat) &&  ( Util.isEmpty(mbrMobileFind) || JcmsUtil.isSameId(mbrMobileFind, loggedMember)) %>">
		               <div class="ds44-form__container ds44-checkBox-radio_list ">
		                  <input <%if(SelectionLogin.TELEPHONE.equals(typeLogin)){%> checked="checked" <%} %> type="radio" name="choixLogin" value="1" id="name-radio-form-element-<%= uuid %>-tel" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" />
		                  <label id="label-radio-form-element-<%= uuid %>-tel" for="name-radio-form-element-<%= uuid %>-tel" class="ds44-radioLabel">
		                      <%=glp("jcmsplugin.assmatplugin.espaceperso.monnum") %> <%=telephoneMobileAssmat %>
		                  </label>
		                  <span class="simpletooltip_container" data-hashtooltip-id="<%= uuid %>-tel-aide">
                              <button type="button" class="js-simple-tooltip button" data-is-initialized="true" data-simpletooltip-content-id="tooltip-case_<%= uuid %>-tel-aide" data-hashtooltip-id="<%= uuid %>-tel-aide" aria-describedby="label_simpletooltip_<%= uuid %>-tel-aide">
                              <i class="icon icon-help" aria-hidden="true"></i><span class="visually-hidden">Aide : <%=glp("jcmsplugin.assmatplugin.espaceperso.monnum") %> <%=telephoneMobileAssmat %></span>
                              </button><span id="label_simpletooltip_<%= uuid %>-tel-aide" class="simpletooltip js-simple-tooltip bottom" role="tooltip" data-hashtooltip-id="<%= uuid %>-tel-aide" aria-hidden="true">
                              <trsb:glp key="LOGIN-PREF-CONNEXION-TEL-BULLE-HTML" ></trsb:glp>
                              </span>
                          </span>
		               </div>
		               </jalios:if>
		               <div class="ds44-form__container ds44-checkBox-radio_list ">
		                  <input <%if(SelectionLogin.NUMERO_DOSSIER.equals(typeLogin)){%> checked="checked" <%} %> type="radio" name="choixLogin" value="3" id="name-radio-form-element-<%= uuid %>-dossier" class="ds44-radio"  aria-describedby="mandatory-message-form-element-<%= uuid %>" />
		                  <label id="label-radio-form-element-<%= uuid %>-dossier" for="name-radio-form-element-<%= uuid %>-dossier" class="ds44-radioLabel">
		                      <%=glp("jcmsplugin.assmatplugin.espaceperso.monagr") %> <%=numeroDossierAssmat %> 
		                  </label>
		                  <span class="simpletooltip_container" data-hashtooltip-id="<%= uuid %>-dossier-aide">
                              <button type="button" class="js-simple-tooltip button" data-is-initialized="true" data-simpletooltip-content-id="tooltip-case_<%= uuid %>-dossier-aide" data-hashtooltip-id="<%= uuid %>-dossier-aide" aria-describedby="label_simpletooltip_<%= uuid %>-dossier-aide">
                              <i class="icon icon-help" aria-hidden="true"></i><span class="visually-hidden">Aide : <%=glp("jcmsplugin.assmatplugin.espaceperso.monagr") %> <%=numeroDossierAssmat %></span>
                              </button><span id="label_simpletooltip_<%= uuid %>-dossier-aide" class="simpletooltip js-simple-tooltip bottom" role="tooltip" data-hashtooltip-id="<%= uuid %>-dossier-aide" aria-hidden="true">
                              <trsb:glp key="LOGIN-PREF-CONNEXION-DOSSIER-BULLE-HTML" ></trsb:glp>
                              </span>
                          </span>
		               </div>
		             </div>
	             </div>
             </div>

              <h3 class="title-bar-container dotted-portlet"><%=glp("jcmsplugin.assmatplugin.espaceperso.modifmdp") %></h3>
              <p class="instructionsSaisie"><%=glp("jcmsplugin.assmatplugin.espaceperso.motdepasscomporte") %></p>
                           
            <div class="ds44-form__container">
			   <div class="ds44-posRel">
			      <label for="password" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.login.motpasse") %></span></span></label>
			      <input type="password" id="password" name="password" value="" class="ds44-inpStd" title='<%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.login.motpasse") %>' autocomplete="current-password" aria-describedby="explanation-password" />
			      <button class="ds44-showPassword" type="button">
			      <i class="icon icon-visuel icon--sizeL" aria-hidden="true"></i>
			      <span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.afficher-contenu-champ", glp("jcmsplugin.assmatplugin.inscription.champ.lbl.login.motpasse")) %></span>
			      </button>
			      <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", glp("jcmsplugin.assmatplugin.inscription.champ.lbl.login.motpasse")) %></span></button>
			   </div>
			</div>
			<div class="ds44-form__container">
			   <div class="ds44-posRel">
			      <label for="passwordConfirm" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.login.motpasseconf") %></span></span></label>
			      <input type="password" id="passwordConfirm" name="passwordConfirm" value="" class="ds44-inpStd" title='<%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.login.motpasseconf") %>' data-field-compare="#password" autocomplete="current-password" />
			      <button class="ds44-showPassword" type="button">
			      <i class="icon icon-visuel icon--sizeL" aria-hidden="true"></i>
			      <span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.afficher-contenu-champ", glp("jcmsplugin.assmatplugin.inscription.champ.lbl.login.motpasseconf")) %></span>
			      </button>
			      <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", glp("jcmsplugin.assmatplugin.inscription.champ.lbl.login.motpasseconf")) %></span></button>
			   </div>
			</div>
			
			<div class="ds44-form__container">
		        <button data-send-native class="ds44-btnStd ds44-btn--invert" data-submit-value="true" data-submit-key="opCreate" title='<trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp>'><trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp></button>
		        <input type="hidden" name="noSendRedirect" value="true" data-technical-field/> 
		        <input type="hidden" name="numeroAgrement" value="<%=numeroDossierAssmat %>" data-technical-field/>
		        <input type="hidden" name="opUpdate" value="true" data-technical-field/>
		        <input type="hidden" name="csrftoken" value="<%= getCSRFToken() %>" data-technical-field/>
		    </div>

  </form> 
<%
}
%>