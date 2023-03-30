
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

<jsp:useBean id='formHandler' scope='page' class='fr.cg44.plugin.assmat.handler.ProfilOffreAccueilHandler'>
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

String accueilTempsPartiel = profil.getAccueilTempsPartiel();
String accueilPeriscolaire = profil.getAccueilPeriscolaire();
String accueilMercredi = profil.getAccueilMercredi();
String accueilPendantLesVacancesSco = profil.getAccueilPendantLesVacancesSco();
String horairesAtypiques = profil.getHorairesAtypiques();
String avant7h = profil.getAvant7h();
String apres20h = profil.getApres20h();
String leSamedi = profil.getLeSamedi();
String leDimanche = profil.getLeDimanche();
String laNuit = profil.getLaNuit();
String accepteDepannage = profil.getAccepteDepannage();
String precisionsDepannage = profil.getPrecisionsDepannage();
String accueilEnfantHandicap = profil.getAccueilEnfantHandicap();
String precisionsEnfantHandicap = profil.getPrecisionsEnfantHandicap();
String logementAccessible = profil.getLogementAccessible();
String precisionsLogementAccessible = profil.getPrecisionsLogementAccessible();


int numeroDossierAssmat = profil.getNum_agrement(); 
//UUID unique pour les champs
String uuid = UUID.randomUUID().toString();
%>

<%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/header.jspf' %>
<%@ include file='/plugins/SoclePlugin/jsp/doMessageBoxCustom.jspf' %>

<div class="headstall container-fluid formulaireActivation">
<div class="formActivation form-cg form-espace-perso">
<div class="form-cg-gray form-cg-white">

  <form method="post" action="<%= ServletUtil.getResourcePath(request) %>"
    name="formContact" id="formContact" class="formContact formEspacePerso">

    <div class="ds44-introduction"><trsb:glp key="OFFRE-INTRO-HTML"></trsb:glp></div>
    
        
    <div class="blocQuestion">
      
      <%-- Accueil --%>
      <h3><trsb:glp key="OFFRE-TYPE-HTML"></trsb:glp></h3>


<div class="ds44-flex">

<div class="blocSelection ds44-fg1 ds44-w100">
    <p><trsb:glp key="OFFRE-PARTIEL-HTML"></trsb:glp></p>
    <% uuid = UUID.randomUUID().toString(); %>
    <div id="form-element-<%= uuid %>" data-name="accueilTempsPartiel" class="ds44-form__radio_container" data-required="false">
        <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
        <div class="ds44-form__container ds44-checkBox-radio_list ">
            <input type="radio" <%if(Util.notEmpty(accueilTempsPartiel) && "true".equals(accueilTempsPartiel)){ %> checked="checked" <%} %> name="accueilTempsPartiel" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><%= glp("ui.com.lbl.true") %></label>
        </div>
        <div class="ds44-form__container ds44-checkBox-radio_list ">
            <input type="radio" <%if(Util.notEmpty(accueilTempsPartiel) && "false".equals(accueilTempsPartiel)){ %> checked="checked" <%} %> name="accueilTempsPartiel" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><%= glp("ui.com.lbl.false") %></label>
        </div>
    </div>

    <p><trsb:glp key="OFFRE-PERISCO-HTML"></trsb:glp></p>
    <% uuid = UUID.randomUUID().toString(); %>
    <div id="form-element-<%= uuid %>" data-name="accueilPeriscolaire" class="ds44-form__radio_container" data-required="false">
        <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
        <div class="ds44-form__container ds44-checkBox-radio_list ">
            <input type="radio" <%if(Util.notEmpty(accueilPeriscolaire) && "true".equals(accueilPeriscolaire)){ %> checked="checked" <%} %> name="accueilPeriscolaire" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><%= glp("ui.com.lbl.true") %></label>
        </div>
        <div class="ds44-form__container ds44-checkBox-radio_list ">
            <input type="radio" <%if(Util.notEmpty(accueilPeriscolaire) && "false".equals(accueilPeriscolaire)){ %> checked="checked" <%} %> name="accueilPeriscolaire" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><%= glp("ui.com.lbl.false") %></label>
        </div>
    </div>

    <p><trsb:glp key="OFFRE-MERC-HTML"></trsb:glp></p>
    <% uuid = UUID.randomUUID().toString(); %>
    <div id="form-element-<%= uuid %>" data-name="accueilMercredi" class="ds44-form__radio_container" data-required="false">
        <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
        <div class="ds44-form__container ds44-checkBox-radio_list ">
            <input type="radio" <%if(Util.notEmpty(accueilMercredi) && "true".equals(accueilMercredi)){ %> checked="checked" <%} %> name="accueilMercredi" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><%= glp("ui.com.lbl.true") %></label>
        </div>
        <div class="ds44-form__container ds44-checkBox-radio_list ">
            <input type="radio" <%if(Util.notEmpty(accueilMercredi) && "false".equals(accueilMercredi)){ %> checked="checked" <%} %> name="accueilMercredi" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><%= glp("ui.com.lbl.false") %></label>
        </div>
    </div>

    <p><trsb:glp key="OFFRE-VAC-HTML"></trsb:glp></p>
    <% uuid = UUID.randomUUID().toString(); %>
    <div id="form-element-<%= uuid %>" data-name="accueilPendantLesVacancesSco" class="ds44-form__radio_container" data-required="false">
        <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
        <div class="ds44-form__container ds44-checkBox-radio_list ">
            <input type="radio" <%if(Util.notEmpty(accueilPendantLesVacancesSco) && "true".equals(accueilPendantLesVacancesSco)){ %> checked="checked" <%} %> name="accueilPendantLesVacancesSco" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><%= glp("ui.com.lbl.true") %></label>
        </div>
        <div class="ds44-form__container ds44-checkBox-radio_list ">
            <input type="radio" <%if(Util.notEmpty(accueilPendantLesVacancesSco) && "false".equals(accueilPendantLesVacancesSco)){ %> checked="checked" <%} %> name="accueilPendantLesVacancesSco" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><%= glp("ui.com.lbl.false") %></label>
        </div>
    </div>

</div>

<div class="blocSelection2 ds44-fg1 ds44-w100">
    <p><trsb:glp key="OFFRE-ATYP-HTML"></trsb:glp></p>
    <% uuid = UUID.randomUUID().toString(); %>
    <div id="form-element-<%= uuid %>" data-name="horairesAtypiques" class="ds44-form__radio_container" data-required="false">
        <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
        <div class="ds44-form__container ds44-checkBox-radio_list ">
            <input type="radio" <%if(Util.notEmpty(horairesAtypiques) && "true".equals(horairesAtypiques)){ %> checked="checked" <%} %> name="horairesAtypiques" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><%= glp("ui.com.lbl.true") %></label>
        </div>
        <div class="ds44-form__container ds44-checkBox-radio_list ">
            <input type="radio" <%if(Util.notEmpty(horairesAtypiques) && "false".equals(horairesAtypiques)){ %> checked="checked" <%} %> name="horairesAtypiques" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><%= glp("ui.com.lbl.false") %></label>
        </div>
    </div>

    <span<% if((Util.notEmpty(horairesAtypiques) && "false".equals(horairesAtypiques)) || Util.isEmpty(horairesAtypiques)) {%> class="hidden" <% } %> data-enabled-by-field="#name-radio-form-element-<%= uuid %>-true">
        <h3 class="h3-like"><trsb:glp key="OFFRE-ATYP-PLUS-HTML"></trsb:glp></h3>
    
        <p><trsb:glp key="OFFRE-ATYP-AV7-HTML"></trsb:glp></p>
        <% uuid = UUID.randomUUID().toString(); %>
        <div id="form-element-<%= uuid %>" data-name="avant7h" class="ds44-form__radio_container" data-required="false">
            <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
            <div class="ds44-form__container ds44-checkBox-radio_list ">
                <input type="radio" <%if(Util.notEmpty(avant7h) && "true".equals(avant7h)){ %> checked="checked" <%} %> name="avant7h" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><%= glp("ui.com.lbl.true") %></label>
            </div>
            <div class="ds44-form__container ds44-checkBox-radio_list ">
                <input type="radio" <%if(Util.notEmpty(avant7h) && "false".equals(avant7h)){ %> checked="checked" <%} %> name="avant7h" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><%= glp("ui.com.lbl.false") %></label>
            </div>
        </div>
    
        <p><trsb:glp key="OFFRE-ATYP-AP20-HTML"></trsb:glp></p>
        <% uuid = UUID.randomUUID().toString(); %>
        <div id="form-element-<%= uuid %>" data-name="apres20h" class="ds44-form__radio_container" data-required="false">
            <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
            <div class="ds44-form__container ds44-checkBox-radio_list ">
                <input type="radio" <%if(Util.notEmpty(apres20h) && "true".equals(apres20h)){ %> checked="checked" <%} %> name="apres20h" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><%= glp("ui.com.lbl.true") %></label>
            </div>
            <div class="ds44-form__container ds44-checkBox-radio_list ">
                <input type="radio" <%if(Util.notEmpty(apres20h) && "false".equals(apres20h)){ %> checked="checked" <%} %> name="apres20h" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><%= glp("ui.com.lbl.false") %></label>
            </div>
        </div>
    
        <p><trsb:glp key="OFFRE-ATYP-SAM-HTML"></trsb:glp></p>
        <% uuid = UUID.randomUUID().toString(); %>
        <div id="form-element-<%= uuid %>" data-name="leSamedi" class="ds44-form__radio_container" data-required="false">
            <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
            <div class="ds44-form__container ds44-checkBox-radio_list ">
                <input type="radio" <%if(Util.notEmpty(leSamedi) && "true".equals(leSamedi)){ %> checked="checked" <%} %> name="leSamedi" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><%= glp("ui.com.lbl.true") %></label>
            </div>
            <div class="ds44-form__container ds44-checkBox-radio_list ">
                <input type="radio" <%if(Util.notEmpty(leSamedi) && "false".equals(leSamedi)){ %> checked="checked" <%} %> name="leSamedi" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><%= glp("ui.com.lbl.false") %></label>
            </div>
        </div>
    
        <p><trsb:glp key="OFFRE-ATYP-DIM-HTML"></trsb:glp></p>
        <% uuid = UUID.randomUUID().toString(); %>
        <div id="form-element-<%= uuid %>" data-name="leSamedi" class="ds44-form__radio_container" data-required="false">
            <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
            <div class="ds44-form__container ds44-checkBox-radio_list ">
                <input type="radio" <%if(Util.notEmpty(leDimanche) && "true".equals(leDimanche)){ %> checked="checked" <%} %> name="leDimanche" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><%= glp("ui.com.lbl.true") %></label>
            </div>
            <div class="ds44-form__container ds44-checkBox-radio_list ">
                <input type="radio" <%if(Util.notEmpty(leDimanche) && "false".equals(leDimanche)){ %> checked="checked" <%} %> name="leDimanche" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><%= glp("ui.com.lbl.false") %></label>
            </div>
        </div>
    
        <p><trsb:glp key="OFFRE-ATYP-NUIT-HTML"></trsb:glp></p>
        <% uuid = UUID.randomUUID().toString(); %>
        <div id="form-element-<%= uuid %>" data-name="laNuit" class="ds44-form__radio_container" data-required="false">
            <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
            <div class="ds44-form__container ds44-checkBox-radio_list ">
                <input type="radio" <%if(Util.notEmpty(laNuit) && "true".equals(laNuit)){ %> checked="checked" <%} %> name="laNuit" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><%= glp("ui.com.lbl.true") %></label>
            </div>
            <div class="ds44-form__container ds44-checkBox-radio_list ">
                <input type="radio" <%if(Util.notEmpty(laNuit) && "false".equals(laNuit)){ %> checked="checked" <%} %> name="laNuit" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><%= glp("ui.com.lbl.false") %></label>
            </div>
        </div>
    </span>
</div>

</div>

</div>

<div class="blocQuestion reponseBooleenne">
    <h3><trsb:glp key="OFFRE-URG-HTML"></trsb:glp></h3>
    <% uuid = UUID.randomUUID().toString(); %>
    <div id="form-element-<%= uuid %>" data-name="accepteDepannage" class="ds44-form__radio_container" data-required="false">
        <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
        <div class="ds44-form__container ds44-checkBox-radio_list ">
            <input type="radio" <%if(Util.notEmpty(accepteDepannage) && "true".equals(accepteDepannage)){ %> checked="checked" <%} %> name="accepteDepannage" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><%= glp("ui.com.lbl.true") %></label>
        </div>
        <div class="ds44-form__container ds44-checkBox-radio_list ">
            <input type="radio" <%if(Util.notEmpty(accepteDepannage) && "false".equals(accepteDepannage)){ %> checked="checked" <%} %> name="accepteDepannage" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><%= glp("ui.com.lbl.false") %></label>
        </div>
    </div>

    <div class="ds44-form__container<% if((Util.notEmpty(accepteDepannage) && "false".equals(accepteDepannage)) || Util.isEmpty(accepteDepannage)) {%> hidden <% } %>" data-enabled-by-field="#name-radio-form-element-<%= uuid %>-true">
        <div class="ds44-posRel">
            <label for="form-element-<%= uuid %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= glp("jcmsplugin.assmatplugin.label.precisions") %></span></span></label>
            <textarea rows="5" cols="1" id="form-element-<%= uuid %>" name="precisionsDepannage" class="ds44-inpStd" title='<%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.votremessage") %>'><% if(Util.notEmpty(precisionsDepannage)){ %><%=precisionsDepannage%><% } %></textarea>
        </div>
    </div>
</div>
<div class="blocQuestion reponseBooleenne">
    <h3><trsb:glp key="OFFRE-HANDI-HTML"></trsb:glp></h3>
    <% uuid = UUID.randomUUID().toString(); %>
    <div id="form-element-<%= uuid %>" data-name="accueilEnfantHandicap" class="ds44-form__radio_container" data-required="false">
        <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
        <div class="ds44-form__container ds44-checkBox-radio_list ">
            <input type="radio" <%if(Util.notEmpty(accueilEnfantHandicap) && "true".equals(accueilEnfantHandicap)){ %> checked="checked" <%} %> name="accueilEnfantHandicap" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><%= glp("ui.com.lbl.true") %></label>
        </div>
        <div class="ds44-form__container ds44-checkBox-radio_list ">
            <input type="radio" <%if(Util.notEmpty(accueilEnfantHandicap) && "false".equals(accueilEnfantHandicap)){ %> checked="checked" <%} %> name="accueilEnfantHandicap" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><%= glp("ui.com.lbl.false") %></label>
        </div>
    </div>
    <div class="ds44-form__container<% if((Util.notEmpty(accueilEnfantHandicap) && "false".equals(accueilEnfantHandicap)) || Util.isEmpty(accueilEnfantHandicap)) {%> hidden <% } %>" data-enabled-by-field="#name-radio-form-element-<%= uuid %>-true">
        <div class="ds44-posRel">
            <label for="form-element-<%= uuid %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= glp("jcmsplugin.assmatplugin.label.precisions") %></span></span></label>
            <textarea rows="5" cols="1" id="form-element-<%= uuid %>" name="precisionsEnfantHandicap" class="ds44-inpStd" title='<%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.votremessage") %>'><% if(Util.notEmpty(precisionsEnfantHandicap)){ %><%=precisionsEnfantHandicap%><% } %></textarea>
        </div>
    </div>
</div>
<div class="blocQuestion reponseBooleenne">
    <h3><trsb:glp key="OFFRE-ACCESS-HTML"></trsb:glp></h3>
    <% uuid = UUID.randomUUID().toString(); %>
    <div id="form-element-<%= uuid %>" data-name="logementAccessible" class="ds44-form__radio_container" data-required="false">
        <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
        <div class="ds44-form__container ds44-checkBox-radio_list ">
            <input type="radio" <%if(Util.notEmpty(logementAccessible) && "true".equals(logementAccessible)){ %> checked="checked" <%} %> name="logementAccessible" value="true" id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><%= glp("ui.com.lbl.true") %></label>
        </div>
        <div class="ds44-form__container ds44-checkBox-radio_list ">
            <input type="radio" <%if(Util.notEmpty(logementAccessible) && "false".equals(logementAccessible)){ %> checked="checked" <%} %> name="logementAccessible" value="false" id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" /><label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><%= glp("ui.com.lbl.false") %></label>
        </div>
    </div>

    <div class="ds44-form__container<% if((Util.notEmpty(logementAccessible) && "false".equals(logementAccessible)) || Util.isEmpty(logementAccessible)) {%> hidden <% } %>" data-enabled-by-field="#name-radio-form-element-<%= uuid %>-true">
        <div class="ds44-posRel">
            <label for="form-element-<%= uuid %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><%= glp("jcmsplugin.assmatplugin.label.precisions") %></span></span></label>
            <textarea rows="5" cols="1" id="form-element-<%= uuid %>" name="precisionsLogementAccessible" class="ds44-inpStd" title='<%= glp("jcmsplugin.assmatplugin.inscription.champ.lbl.votremessage") %>'><% if(Util.notEmpty(precisionsLogementAccessible)){ %><%=precisionsLogementAccessible%><% } %></textarea>
        </div>
    </div>
</div>
    
    <div class="ds44-form__container">
        <button data-send-native class="ds44-btnStd ds44-btn--invert" data-submit-value="true" data-submit-key="opCreate" title='<trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp>'><trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp></button>
        <input type="hidden" name="noSendRedirect" value="true" data-technical-field/> 
        <input type="hidden" name="opUpdate" value="true" />
        <input type="hidden" name="csrftoken" value="<%= HttpUtil.getCSRFToken(request) %>" data-technical-field>
    </div>
    
  </form>


</div>
</div>
</div>
