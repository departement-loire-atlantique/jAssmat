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

<jsp:useBean id='formHandler' scope='page' class='fr.cg44.plugin.assmat.handler.ProfilContactsHandler'>
  <jsp:setProperty name='formHandler' property='request' value='<%= request %>'/>
  <jsp:setProperty name='formHandler' property='response' value='<%= response %>'/>
  <jsp:setProperty name='formHandler' property="noRedirect" value="true" />
  <jsp:setProperty name='formHandler' property="profil" value='<%= profil %>' />
  <jsp:setProperty name='formHandler' property="member" value='<%= loggedMember %>' />
  <jsp:setProperty name='formHandler' property='*' />  
</jsp:useBean>


<%
if (formHandler.validate()) {
  //return;
} 

String telephoneFixAssmat = profil.getTelephoneFixe() != null ? profil.getTelephoneFixe() : "" ;
String visibiliteTelephoneFixe = profil.getVisbiliteTelephoneFixe();

String telephoneMobileAssmat = profil.getAuthor().getMobile() != null ?  profil.getAuthor().getMobile() : "" ;
String visibiliteTelephonePortable = profil.getVisibiliteTelephonePortable();

String creneauAppels = profil.getCreneauHorairesDappel() != null ? profil.getCreneauHorairesDappel() : "";

String emailAssmat = loggedMember.getEmail() != null ? loggedMember.getEmail() : "" ;
String visibiliteAdresseEmail = profil.getVisibiliteAdresseEmail();

boolean afficherContactUniquementSiD = profil.getAfficherContactUniquementSiD();

SelectionLogin typeLogin = SelectionLogin.getTypeLoginByValue(profil.getTypeLogin());
int numeroDossierAssmat = profil.getNum_agrement(); 

//UUID unique pour les champs
String uuid = UUID.randomUUID().toString();
%>

<%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/header.jspf' %>
<%@ include file='/jcore/doMessageBox.jsp' %>

  <form method="post" action="<%= ServletUtil.getResourcePath(request) %>" 
    name="formContact" id="formContact">

  
    <div class="alert alert-block">
      <h4><%=glp("msg.message-box.warning")%></h4>
      <p></p>
    </div>
  
    <p aria-level="3" class="h4-like"><trsb:glp key="CONTACT-INTROTEL-HTML"></trsb:glp></p>
   
    <%-- Téléphone fix --%>	  
	  <p aria-level="2" class="h4-like"><trsb:glp key="CONTACT-TEL-HTML"></trsb:glp></p>
            <div class="ds44-form__container">
                <div class="ds44-posRel">
                    <label for="form-element-<%= uuid %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.mobile") %></span></span></label>
                    <input type="text" id="form-element-<%= uuid %>" value='<%= telephoneFixAssmat %>' name="telFix" class="ds44-inpStd" title='<%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.mobile") %>' autocomplete="tel-national" aria-describedby="explanation-form-element-<%= uuid %>" data-bkp-aria-describedby="explanation-form-element-<%= uuid %>">
                    <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", glp("jcmsplugin.assmatplugin.inscription.champ.lbl.mobile")) %></span></button>
                </div>
                <div class="ds44-field-information" aria-live="polite">
                    <ul class="ds44-field-information-list ds44-list">
                        <li id="explanation-form-element-<%= uuid %>" class="ds44-field-information-explanation"><%= glp("jcmsplugin.socle.form.exemple.tel") %></li>
                    </ul>
                </div>
            </div>
            <% uuid = UUID.randomUUID().toString(); %>
            <div id="form-element-<%= uuid %>" data-name="visibleFixMobile" class="ds44-form__radio_container ds44-form__container"  data-required="true">
               <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
               <div class="ds44-form__container ds44-checkBox-radio_list ">
                  <input type="radio" <%if(Util.notEmpty(visibiliteTelephoneFixe) && "true".equals(visibiliteTelephoneFixe) ){ %> checked="checked" <%} %> name="visibleFixMobile" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio"   required  aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><trsb:glp key="CONTACT-TEL-OUI-HTML"></trsb:glp></label>
               </div>
               <div class="ds44-form__container ds44-checkBox-radio_list ">
                  <input type="radio" <%if(Util.notEmpty(visibiliteTelephoneFixe) && "false".equals(visibiliteTelephoneFixe) ){ %> checked="checked" <%} %> name="visibleFixMobile" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio"   required  aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><trsb:glp key="CONTACT-TEL-NON-HTML"></trsb:glp></label>
               </div>
            </div>
   
    <%-- Téléphone mobile --%>
    <% uuid = UUID.randomUUID().toString(); %>
    <p aria-level="2" class="h4-like"><trsb:glp key="CONTACT-MOB-HTML"></trsb:glp></p>
            <div class="ds44-form__container">
                <div class="ds44-posRel">
                    <label for="form-element-<%= uuid %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.mobile") %></span></span></label>
                    <input type="text" id="form-element-<%= uuid %>" value='<%= telephoneMobileAssmat %>' name="telMobile" class="ds44-inpStd" title='<%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.mobile") %>' autocomplete="tel-national" aria-describedby="explanation-form-element-<%= uuid %>" data-bkp-aria-describedby="explanation-form-element-<%= uuid %>">
                    <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", glp("jcmsplugin.assmatplugin.inscription.champ.lbl.mobile")) %></span></button>
                </div>
                <div class="ds44-field-information" aria-live="polite">
                    <ul class="ds44-field-information-list ds44-list">
                        <li id="explanation-form-element-<%= uuid %>" class="ds44-field-information-explanation"><%= glp("jcmsplugin.socle.form.exemple.tel") %></li>
                    </ul>
                </div>
            </div>
            <% uuid = UUID.randomUUID().toString(); %>
            <div id="form-element-<%= uuid %>" data-name="visibleTelMobile" class="ds44-form__radio_container ds44-form__container"  data-required="true">
		       <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
		       <div class="ds44-form__container ds44-checkBox-radio_list ">
		          <input type="radio" <%if(Util.notEmpty(visibiliteTelephonePortable) && "true".equals(visibiliteTelephonePortable) ){ %> checked="checked" <%} %> name="visibleTelMobile" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio"   required  aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><trsb:glp key="CONTACT-MOB-OUI-HTML"></trsb:glp></label>
		       </div>
		       <div class="ds44-form__container ds44-checkBox-radio_list ">
		          <input type="radio" <%if(Util.notEmpty(visibiliteTelephonePortable) && "false".equals(visibiliteTelephonePortable) ){%> checked="checked" <%} %> name="visibleTelMobile" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio"   required  aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><trsb:glp key="CONTACT-MOB-NON-HTML"></trsb:glp></label>
		       </div>
		    </div>
   
    <%-- creneau appels --%>
    <% uuid = UUID.randomUUID().toString(); %>
    <p aria-level="2" class="h4-like"><trsb:glp key="CONTACT-TEL-HOR-HTML"></trsb:glp></p>
    <div class="ds44-form__container">
	   <div class="ds44-posRel">
	      <label for="form-element-<%= uuid %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= glp("plugin.assmatplugin.profilassmat.form.creneaux") %></span></span></label>
	      <input type="text" id="form-element-<%= uuid %>" name="creneauAppels" class="ds44-inpStd" title='<%= glp("plugin.assmatplugin.profilassmat.form.creneaux") %>'  />
	      <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", glp("plugin.assmatplugin.profilassmat.form.creneaux")) %></span></button>
	   </div>
	</div>   
   
    <h3 class="h3-like"><trsb:glp key="CONTACT-INTROMAIL-HTML"></trsb:glp></h3>
    
    <%-- email --%>
    <% uuid = UUID.randomUUID().toString(); %>
            <div class="ds44-form__container">
                <p aria-level="2" class="h4-like">
                  <trsb:glp key="CONTACT-LEG-MAIL-HTML"></trsb:glp>
                </p>
                <div class="ds44-posRel">
                    <label for="form-element-<%= uuid %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.email") %></span></span></label>
                    <input type="email" id="form-element-<%= uuid %>" name="email" value='<%= emailAssmat %>' class="ds44-inpStd" title='<%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.email") %>' autocomplete="email" aria-describedby="explanation-form-element-<%= uuid %>" data-bkp-aria-describedby="explanation-form-element-<%= uuid %>">
                    <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", glp("jcmsplugin.assmatplugin.inscription.champ.lbl.email")) %></span></button>
                </div>
                <div class="ds44-field-information" aria-live="polite">
                    <ul class="ds44-field-information-list ds44-list">
                        <li id="explanation-form-element-<%= uuid %>" class="ds44-field-information-explanation"><%= glp("jcmsplugin.socle.form.exemple.email") %></li>
                    </ul>
                </div>
            </div>
            <% uuid = UUID.randomUUID().toString(); %>
            <div id="form-element-<%= uuid %>" data-name="visibleEmail" class="ds44-form__radio_container ds44-form__container"  data-required="true">
               <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
               <div class="ds44-form__container ds44-checkBox-radio_list ">
                  <input type="radio" <%if(Util.notEmpty(visibiliteAdresseEmail) && "true".equals(visibiliteAdresseEmail) ){ %> checked="checked" <%} %> name="visibleEmail" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio"   required  aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><trsb:glp key="CONTACT-MAIL-OUI-HTML"></trsb:glp></label>
               </div>
               <div class="ds44-form__container ds44-checkBox-radio_list ">
                  <input type="radio" <%if(Util.notEmpty(visibiliteAdresseEmail) && "false".equals(visibiliteAdresseEmail) ){%> checked="checked" <%} %> name="visibleEmail" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio"   required  aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><trsb:glp key="CONTACT-MAIL-NON-HTML"></trsb:glp></label>
               </div>
            </div>	  
	  
	  <h3 class="h3-liket"><trsb:glp key="CONTACT-DISPO-HTML"></trsb:glp></h3>
	  
	  <%-- afficher disponibilité --%>
	  <% uuid = UUID.randomUUID().toString(); %>
	  <trsb:glp key="CONTACT-DISPO-LEG-HTML"></trsb:glp>	  
	  <div id="form-element-<%= uuid %>" data-name="afficherContact" class="ds44-form__radio_container ds44-form__container"  data-required="true">
               <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
               <div class="ds44-form__container ds44-checkBox-radio_list ">
                  <input type="radio" <%if(afficherContactUniquementSiD){ %>  checked="checked" <%} %> name="afficherContact" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio"   required  aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><trsb:glp key="CONTACT-DISPO-OUI-HTML"></trsb:glp></label>
               </div>
               <div class="ds44-form__container ds44-checkBox-radio_list ">
                  <input type="radio" <%if(!afficherContactUniquementSiD){ %> checked="checked" <%} %> name="afficherContact" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio"   required  aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><trsb:glp key="CONTACT-DISPO-NON-HTML"></trsb:glp></label>
               </div>
            </div>   

    <p class="submit">
      <label for="submit"> 
        <input type="submit" id="submit" name="opCreate" 
          value="<trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp>" class="submitButton" /> 
        <span class="input-box" style="background-color: #aec900" />
          <span class="spr-recherche-ok"></span>
        </span>
      </label> 
      <input type="hidden" name="noSendRedirect" value="true" /> 
      <input type="hidden" name="opUpdate" value="true" />
    </p>
    
  </form>
