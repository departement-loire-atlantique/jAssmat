<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@page import="fr.cg44.plugin.assmat.SemainierUtil"%>
<%@page import="fr.cg44.plugin.assmat.Semainier"%>
<%@page import="fr.cg44.plugin.tools.googlemaps.proxy.ProxyTarget"%>
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
}

%>

<jsp:useBean id='formHandler' scope='page' class='generated.EditSignalerDemenagementHandler'>
  <jsp:setProperty name='formHandler' property='request' value='<%= request %>'/>
  <jsp:setProperty name='formHandler' property='response' value='<%= response %>'/>
  <jsp:setProperty name='formHandler' property='*' />
  <jsp:setProperty name='formHandler' property='certifieExactitude' value='<%= Util.notEmpty(request.getParameter("certifieExactitudeDepartement")) ? true : false  %>'/>
</jsp:useBean>


<jsp:useBean id='formHandler2' scope='page' class='generated.EditSignalerDemenagementAutreDeparteHandler'>
  <jsp:setProperty name='formHandler2' property='request' value='<%= request %>'/>
  <jsp:setProperty name='formHandler2' property='response' value='<%= response %>'/> 
  <jsp:setProperty name='formHandler2' property='*' />
  <jsp:setProperty name='formHandler2' property='certifieExactitude' value='<%= Util.notEmpty(request.getParameter("certifieExactitudeAutreDepartement")) ? true : false %>'/>
</jsp:useBean>

<%

if(Util.notEmpty(request.getParameter("opCreateLoireAtlantique"))){
	if (formHandler.validate()) {
	  // TODO afficher le ok 
	  //sendRedirect(ServletUtil.getResourcePath(request));
	}
}

if(Util.notEmpty(request.getParameter("opCreateAutre"))){
  if (formHandler2.validate()) {
    // TODO afficher le ok 
    //sendRedirect(ServletUtil.getResourcePath(request));
  }
}

AssmatSolis solisAssmat = Util.getFirst(SolisManager.getInstance().getAssmatSolisByNumAgrement(profil.getNum_agrement()));

%>

<!-- DEBUT DECLARER SIGNALER UN DEMENAGEMENT / MES DEMARCHES -->


<div class="mesDemarches signalerDemenagement form-cg">

<%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/header.jspf' %>
<%@ include file='/jcore/doMessageBox.jsp' %>
	
	<div class="saisieDossier form-cg-gray form-cg-white">
		<form action="<%= ServletUtil.getResourcePath(request) %>" method="post">
			<div class="listeInfos">
				<p class="important">Votre adresse actuelle :</p>
				<p><%= solisAssmat.getAdresseDomicile() %></p>
				<p><%= solisAssmat.getCpDomicile() %> <%= solisAssmat.getCommuneDomicile() %></p>
				<jalios:if predicate="<%= Util.notEmpty(loggedMember.getMobile()) %>">
          <p>Mobile : <%= loggedMember.getMobile()%></p>
        </jalios:if>
				<jalios:if predicate="<%= Util.notEmpty(loggedMember.getPhone()) %>">
				  <p>T??l : <%= loggedMember.getPhone() %></p>
				</jalios:if>
			</div>

			<div class="borderDot title-bar-container dotted-portlet"></div>

			<h3 class="titreSaisie">Si vous d??m??nagez dans le d??partement de Loire-Atlantique :</h3>
			<p>Vous devez communiquer 15 jours avant votre d??m??nagement votre
				nouvelle adresse ?? l'unit?? agr??ment, soit par lettre recommand??e,
				soit via le formulaire ci-dessous. Un professionnel de l'unit??
				agr??ment se rendra chez vous pour v??rifier que ce nouveau logement
				pr??sente les garanties requises ?? l'accueil des enfants.</p>

			<div class="borderDot title-bar-container dotted-portlet"></div>

      <input type="hidden" name="title" value="<%= loggedMember %>">
      <% if (isLogged && HttpUtil.isCSRFEnabled()) { %>
        <input type="hidden" name="csrftoken" value="<%= getCSRFToken() %>" />
      <% } %>     


			<div class="ligneSaisie">
				<label for="nouvelleAdresse" class="important">Nouvelle
					adresse* :</label> <input type="text" name="nouvelleAdresse"
					id="nouvelleAdresse" value='<%= formHandler.getAvailableNouvelleAdresse() != null ? formHandler.getAvailableNouvelleAdresse() : "" %>'>
			</div>

			<div class="ligneSaisie">
				<label for="nouveauCodePostal" class="important">Code
					postal* :</label> <input type="text" name="codePostal"
					id="nouveauCodePostal" value='<%= formHandler.getAvailableCodePostal() != null ? formHandler.getAvailableCodePostal() : "" %>'>
			</div>

			<div class="ligneSaisie">
				<label for="nouvelleCommune" class="important">Commune* :</label> <input
					type="text" name="commune" id="nouvelleCommune" value='<%= formHandler.getAvailableCommune() != null ? formHandler.getAvailableCommune() : "" %>'>
			</div>

			<div class="ligneSaisie">
				<label for="dateDemenagement" class="important">Date pr??vue
					pour le d??m??nagement* :</label> <input type="date" name="datePrevuePourLeDemenagement"
					id="dateDemenagement" value='<%= formHandler.getAvailableDatePrevuePourLeDemenagement() != null ? formHandler.getAvailableDatePrevuePourLeDemenagement() : "" %>'>
			</div>

			<div class="ligneSaisie">
				<label for="nouveauTel" class="important">Nouveau num??ro de
					t??l??phone :</label> <input type="tel" name="nouveauNumeroDeTelephone" id="nouveauTel" value='<%= formHandler.getAvailableNouveauNumeroDeTelephone() != null ? formHandler.getAvailableNouveauNumeroDeTelephone() : "" %>'>
			</div>

			<div class="ligneSaisie">
				<label for="precisions" class="important">Pr??cisions :</label>
				<textarea rows="5" cols="80" name="precisions"><%= formHandler.getAvailablePrecisions() != null ? formHandler.getAvailablePrecisions(): "" %></textarea>
			</div>
      

      <div class="listeVerticale">
				<input type="checkbox" name="certifieExactitudeDepartement"
					id="certificationInformations" <%= formHandler.getAvailableCertifieExactitude() ? "checked":"" %>> 
				<label
					for="certificationInformations">Je certifie l'exactitude des
					informations transmises*</label>
      </div>
      
			<div class="borderDot title-bar-container dotted-portlet"></div>

			<p class="champsObligatoires">* Champs obligatoires</p>

			<p class="submit">
				<label for="submit"> <input type="submit" id="submit"
					name="opCreate" value="Valider" class="submitButton">
					<span class="input-box" style="background-color: #aec900"><span
						class="spr-recherche-ok"></span></span>
				</label>
			</p>
			
			<input type='hidden' name='opCreateLoireAtlantique' value='true' />  			
			<input type='hidden' name='redirect' value='<%= ServletUtil.getResourcePath(request) %>' />  
			
		</form>
		

	  <div class="borderDot title-bar-container dotted-portlet"></div>
  
  
    <form action="<%= ServletUtil.getResourcePath(request) %>" method="post">    
      <h3 class="titreSaisie">Si vous d??m??nagez dans un autre d??partement :</h3>
      
      <div class="nouvelleResidence">
				<p>Merci de nous indiquer dans quel d??partement sera votre
					nouvelle r??sidence* :</p>

        <input type="hidden" name="title" value="<%= loggedMember %>">
        <% if (isLogged && HttpUtil.isCSRFEnabled()) { %>
          <input type="hidden" name="csrftoken" value="<%= getCSRFToken() %>" />
        <% } %>  


        <% Category departementValueCat = Util.getFirst(formHandler2.getDepartementCatSet()); %>      
        <% Category rootDepartement = formHandler2.getDepartementRoot(); %>
				<div class="form-select situation">
					<span class="input-box" style="background-color: #aec900"><span
						class="spr-select_arrow"></span></span> <select name="cids" id="subject">
						<option value=""></option>
						<jalios:foreach collection="<%= rootDepartement.getChildrenSet() %>" name="itCat" type="Category">
						    <option value="<%= itCat.getId() %>" <%= JcmsUtil.isSameId(itCat, departementValueCat) ? "selected": "" %>><%= itCat.getName() %></option>						
						</jalios:foreach>
					</select>
				</div>
	
				
			</div>
      
      <div class="note">
				<p>NB : Si vous souhaitez continuer ?? exercer
					dans ce nouveau d??partement, vous devez informer au moins 15 jours
					avant votre d??m??nagement, par lettre recommand??e avec accus?? de
					r??ception le Pr??sident du conseil d??partemental de votre nouvelle
					r??sidence en indiquant votre nouvelle adresse et en joignant une
					copie de votre attestation d'agr??ment.</p>
				<p>Nos services transmettront votre dossier au Pr??sident du
					conseil d??partemental de votre nouveau d??partement de r??sidence,
					d??s que celui-ci en fera la demande.</p>
			</div>
      
      <div class="listeVerticale">
				<input type="checkbox" name="certifieExactitudeAutreDepartement"
					id="certificationInformations" <%= formHandler2.getAvailableCertifieExactitude() ? "checked" : "" %>> 
				<label
					for="certificationInformations">Je certifie l'exactitude des
					informations transmises*</label>
      </div>
      
			<div class="borderDot title-bar-container dotted-portlet"></div>

			<p class="champsObligatoires">* Champs obligatoires</p>

			<p class="submit">
				<label for="submit"> <input type="submit" id="submit"
					name="opCreate" value="Valider" class="submitButton">
					<span class="input-box" style="background-color: #aec900"><span
						class="spr-recherche-ok"></span></span>
				</label>
			</p>
			
			<input type='hidden' name='opCreateAutre' value='true' />       
      <input type='hidden' name='redirect' value='<%= ServletUtil.getResourcePath(request) %>' />  

		</form>
	</div>

</div>