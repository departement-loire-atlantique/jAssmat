<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file='/jcore/doInitPage.jspf' %>
<%@ taglib prefix="ds" tagdir="/WEB-INF/tags"%>
<% 
  EditFormContactAssmatHandler formHandler = (EditFormContactAssmatHandler)request.getAttribute("formHandler");
  ServletUtil.backupAttribute(pageContext, "classBeingProcessed");
  request.setAttribute("classBeingProcessed", FormContactAssmat .class);
%>


<%-- Nom ------------------------------------------------------------ --%>
<% String nomLabel = channel.getTypeFieldLabel(FormContactAssmat.class, "name", userLang);%>
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
<% String prenomLabel = channel.getTypeFieldLabel(FormContactAssmat.class, "firstName", userLang);%>
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
<% String mailLabel = channel.getTypeFieldLabel(FormContactAssmat.class, "courriel", userLang);%>
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



<%-- Commune ------------------------------------------------------------ --%>
<% String dataUrl = "plugins/SoclePlugin/jsp/facettes/acSearchCommune.jsp"; %>

<div class="ds44-mb3">
	<ds:facetteAutoCompletion idFormElement='<%= ServletUtil.generateUniqueDOMId(request, glp("jcmsplugin.socle.facette.form-element")) %>' 
	        name='<%= "city" %>' 
	        request="<%= request %>" 
	        isFacetteObligatoire="<%= true %>" 
	        dataMode="select-only" 
	        dataUrl="<%= dataUrl %>" 
	        label='<%= glp("jcmsplugin.socle.facette.commune.default-label") %>' 
	        isLarge='<%= false %>'   
	      />	
</div>


<%-- Telephone ------------------------------------------------------------ --%>
<% String telephoneLabel = channel.getTypeFieldLabel(FormContactAssmat.class, "phone", userLang);%>
<div class="ds44-mb3">
    <div class="ds44-form__container">
        <div class="ds44-posRel">
            <label id="label-form-element-telephone" for="form-element-telephone" class="ds44-formLabel">
                <span class="ds44-labelTypePlaceholder"><span><%= telephoneLabel %></span></span>
            </label>
            <input type="text" id="form-element-telephone" name="phone" class="ds44-inpStd" title="<%= glp("jcmsplugin.socle.facette.champ-obligatoire.title", telephoneLabel) %>"
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




<%-- Sujet ------------------------------------------------------------ --%>
<% String sujetLabel = glp("jcmsplugin.socle.sujet"); %>
<%
TreeSet sujetCatSet = new TreeSet(Category.getOrderComparator(userLang));
sujetCatSet.addAll(formHandler.getSubjectRoot().getChildrenSet());
%>
<div class="ds44-mb3">
    <div class="ds44-form__container">
        <div class="ds44-select__shape ds44-inpStd">
            <p class="ds44-selectLabel" aria-hidden="true"><%= sujetLabel %><sup aria-hidden="true">*</sup></p>
            <div id="sujet" data-name="cids" class="ds44-js-select-standard ds44-selectDisplay" data-required="true"></div>
            <button type="button" id="button-form-element-sujet" class="ds44-btnIco ds44-posAbs ds44-posRi ds44-btnOpen" aria-expanded="false" title="<%= glp("jcmsplugin.socle.facette.champ-obligatoire.title", sujetLabel) %>"  ><i class="icon icon-down icon--sizeL" aria-hidden="true"></i><span id="button-message-form-element-sujet" class="visually-hidden"><%= sujetLabel %></span></button>
            <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", sujetLabel) %></span></button>
        
        </div>
    
        <div class="ds44-select-container hidden">
            <div class="ds44-listSelect">
                <ul class="ds44-list" role="listbox" id="listbox-form-element-sujet" aria-labelledby="button-message-form-element-sujet"  aria-required="true">
                    <jalios:foreach name="itCat" type="Category" collection="<%= sujetCatSet %>">
                        <li class="ds44-select-list_elem" role="option" data-value="<%= itCat.getId() %>" id="form-element-sujet-<%= itCounter %>" tabindex="0">
                            <%= itCat.getName(userLang) %>
                        </li>
                    </jalios:foreach>
                </ul>
            </div>
        </div>
    </div>
</div>



<%-- Message ------------------------------------------------------------ --%>
<% String messageLabel = channel.getTypeFieldLabel(FormContactAssmat.class, "message", userLang);%>
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