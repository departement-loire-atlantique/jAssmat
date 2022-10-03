<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file='/jcore/doInitPage.jspf' %>
<% 
  EditFormulaireDeContactRAMHandler formHandler = (EditFormulaireDeContactRAMHandler)request.getAttribute("formHandler");
  ServletUtil.backupAttribute(pageContext, "classBeingProcessed");
  request.setAttribute("classBeingProcessed", FormulaireDeContactRAM .class);
%>


<%-- Nom ------------------------------------------------------------ --%>
<% String nomLabel = channel.getTypeFieldLabel(FormulaireDeContactRAM.class, "name", userLang);%>
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
<% String prenomLabel = channel.getTypeFieldLabel(FormulaireDeContactRAM.class, "firstName", userLang);%>
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
<% String mailLabel = channel.getTypeFieldLabel(FormulaireDeContactRAM.class, "courriel", userLang);%>
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




<%-- Message ------------------------------------------------------------ --%>
<% String messageLabel = channel.getTypeFieldLabel(FormulaireDeContactRAM.class, "message", userLang);%>
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




<button
    class="jcms-js-submit ds44-btnStd ds44-btn--invert ds44-bntFullw ds44-bntALeft"
    title="<%= glp("jcmsplugin.socle.form.valider-envoi") %>">
    <span class="ds44-btnInnerText"><%= glp("jcmsplugin.socle.valider") %></span><i
        class="icon icon-long-arrow-right" aria-hidden="true"></i>
</button>


<jalios:include target="EDIT_PUB_MAINTAB" targetContext="div" />
<jalios:include jsp="/jcore/doEditExtraData.jsp" />
<% ServletUtil.restoreAttribute(pageContext , "classBeingProcessed"); %>