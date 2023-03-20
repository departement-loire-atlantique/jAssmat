<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file='/jcore/doInitPage.jspf' %>
<% 
  EditFormulaireDeContactDuneAMHandler formHandler = (EditFormulaireDeContactDuneAMHandler)request.getAttribute("formHandler");
  ServletUtil.backupAttribute(pageContext, "classBeingProcessed");
  request.setAttribute("classBeingProcessed", FormulaireDeContactDuneAM .class);
%>


<%-- MemberId ------------------------------------------------------------ --%>
<%
String id = getUntrustedStringParameter("idMAM", "");
String mbrRamId = "";
Member mbrRam = channel.getMember(id);
String mailAMValues ="";
if(Util.notEmpty(mbrRam)){
  mailAMValues = mbrRam.getEmail();
  mbrRamId = mbrRam.getId();
 }
%>



<%-- ASSMAT --%>
<% String assmatLabel = glp("jcmsplugin.assmatplugin.form.nom.lbl"); %>
<div class="ds44-mb3">
    <div class="ds44-form__container">
        <div class="ds44-posRel">
            <label id="label-form-element-assmat" for="form-element-assmat" class="ds44-formLabel">
              <span class=""><span><%= assmatLabel %></span></span>
            </label>
            <input type="text" id="form-element-assmat" name="assmat"
                class="ds44-inpStd" title="<%= glp("jcmsplugin.assmatplugin.form.lbl.assistantematernelle.prefix", glp("jcmsplugin.socle.facette.champ-obligatoire.title", assmatLabel)) %>"
                readonly value="<%= mbrRam %>">
        </div>
    </div>
</div>



<%-- Nom ------------------------------------------------------------ --%>
<% String nomLabel = channel.getTypeFieldLabel(FormulaireDeContactDuneAM.class, "name", userLang);%>
<div class="ds44-mb3">
    <div class="ds44-form__container">
        <div class="ds44-posRel">
            <label id="label-form-element-nom" for="form-element-nom" class="ds44-formLabel">
              <span class="ds44-labelTypePlaceholder"><span><%= nomLabel %><sup aria-hidden="true">*</sup></span></span>
            </label>
            <input type="text" id="form-element-nom" name="name"
                class="ds44-inpStd" title="<%= glp("jcmsplugin.socle.facette.champ-obligatoire.title", nomLabel) %>"
                required autocomplete="family-name">
            <button class="ds44-reset" type="button" aria-describedby="label-form-element-nom">
                <i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", nomLabel) %></span>
            </button>
        </div>
    </div>
</div>


<%-- Prenom ------------------------------------------------------------ --%>
<% String prenomLabel = channel.getTypeFieldLabel(FormulaireDeContactDuneAM.class, "firstName", userLang);%>
<div class="ds44-mb3">
    <div class="ds44-form__container">
        <div class="ds44-posRel">
            <label id="label-form-element-prenom" for="form-element-prenom" class="ds44-formLabel">
              <span class="ds44-labelTypePlaceholder"><span><%= prenomLabel %><sup aria-hidden="true">*</sup></span></span>
            </label>
            <input type="text" id="form-element-prenom" name="firstName" class="ds44-inpStd" title="<%= glp("jcmsplugin.socle.facette.champ-obligatoire.title", prenomLabel) %>"
                required autocomplete="given-name">
            <button class="ds44-reset" type="button" aria-describedby="label-form-element-prenom">
                <i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", prenomLabel) %></span>
            </button>
        </div>
    </div>
</div>


<%-- Mail ------------------------------------------------------------ --%>
<% String mailLabel = channel.getTypeFieldLabel(FormulaireDeContactDuneAM.class, "courriel", userLang);%>
<div class="ds44-mb3">
    <div class="ds44-form__container">
        <div class="ds44-posRel">
            <label id="label-form-element-mail" for="form-element-mail" class="ds44-formLabel">
                <span class="ds44-labelTypePlaceholder"><span><%= mailLabel %><sup aria-hidden="true">*</sup></span></span>
            </label>
            <input type="email" id="form-element-mail" name="courriel" class="ds44-inpStd" title="<%= glp("jcmsplugin.socle.facette.champ-obligatoire.title", mailLabel) %>"
                required autocomplete="email" aria-describedby="explanation-form-element-mail">
            <button class="ds44-reset" type="button" aria-describedby="label-form-element-mail">
                <i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", mailLabel) %></span>
            </button>
        </div>
        <div class="ds44-field-information" aria-live="polite">
            <ul class="ds44-field-information-list ds44-list">
                <li id="explanation-form-element-mail" class="ds44-field-information-explanation"><%= glp("jcmsplugin.socle.form.exemple.email") %></li>
            </ul>
        </div>
    </div>
</div>



<%-- Telephone ------------------------------------------------------------ --%>
<% String telephoneLabel = channel.getTypeFieldLabel(FormulaireDeContactDuneAM.class, "phone", userLang);%>
<div class="ds44-mb3">
    <div class="ds44-form__container">
        <div class="ds44-posRel">
            <label id="label-form-element-telephone" for="form-element-telephone" class="ds44-formLabel">
                <span class="ds44-labelTypePlaceholder"><span><%= telephoneLabel %></span></span>
            </label>
            <input type="text" id="form-element-telephone" name="phone" class="ds44-inpStd" title="<%= telephoneLabel %>"
                autocomplete="tel-national" aria-describedby="explanation-form-element-telephone">
            <button class="ds44-reset" type="button" aria-describedby="label-form-element-telephone">
                <i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", telephoneLabel) %></span>
            </button>
        </div>
        <div class="ds44-field-information" aria-live="polite">
            <ul class="ds44-field-information-list ds44-list">
                <li id="explanation-form-element-telephone" class="ds44-field-information-explanation"><%= glp("jcmsplugin.socle.form.exemple.tel") %></li>
            </ul>
        </div>
    </div>
</div>



<%-- Message ------------------------------------------------------------ --%>
<% String messageLabel = channel.getTypeFieldLabel(FormulaireDeContactDuneAM.class, "message", userLang);%>
<div class="ds44-mb3">
    <div class="ds44-form__container">
        <div class="ds44-posRel">
            <label id="label-form-element-message" for="form-element-message" class="ds44-formLabel">
              <span class="ds44-labelTypePlaceholder"><span><%= messageLabel %><sup aria-hidden="true">*</sup></span></span>
            </label>           
            <textarea rows="5" cols="1" id="form-element-message" name="message" class="ds44-inpStd" title="<%= glp("jcmsplugin.socle.facette.champ-obligatoire.title", messageLabel) %>"  required  ></textarea>                        
        </div>
    </div>
</div>




<input type='hidden' name='memberId' value='<%= mbrRamId %>' data-technical-field />
<input type='hidden' name='mailam' value='<%= mailAMValues %>' data-technical-field />



<button
    class="jcms-js-submit ds44-btnStd ds44-btn--invert ds44-bntFullw ds44-bntALeft"
    title="<%= glp("jcmsplugin.socle.form.valider-envoi") %>">
    <span class="ds44-btnInnerText"><%= glp("jcmsplugin.socle.valider") %></span><i
        class="icon icon-long-arrow-right" aria-hidden="true"></i>
</button>


<jalios:include target="EDIT_PUB_MAINTAB" targetContext="div" />
<jalios:include jsp="/jcore/doEditExtraData.jsp" />
<% ServletUtil.restoreAttribute(pageContext , "classBeingProcessed"); %>