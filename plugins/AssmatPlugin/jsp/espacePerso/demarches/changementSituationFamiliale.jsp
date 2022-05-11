<%@page import="fr.cg44.plugin.assmat.selector.PlaceSelector"%>
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

<jsp:useBean id='formHandler' scope='page' class='generated.EditChangementSituationFamilialeHandler'>
  <jsp:setProperty name='formHandler' property='request' value='<%= request %>'/>
  <jsp:setProperty name='formHandler' property='response' value='<%= response %>'/>
  <jsp:setProperty name='formHandler' property='*' />
</jsp:useBean>

<%

if (formHandler.validate()) {
  // TODO afficher le ok 
  //sendRedirect(ServletUtil.getResourcePath(request));
}

AssmatSolis solisAssmat = Util.getFirst(SolisManager.getInstance().getAssmatSolisByNumAgrement(profil.getNum_agrement()));
String idUa = solisAssmat.getIdUa();
Set<Place> setPlace = (Set<Place>) JcmsUtil.applyDataSelector(channel.getPublicationSet(Place.class, channel.getDefaultAdmin()), new PlaceSelector(idUa));


%>

<!-- DEBUT CHANGEMENT DE SITUATION FAMILIALE / MES DEMARCHES -->

<div class="mesDemarches changementSituationFamiliale form-cg">

<%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/header.jspf' %>
<%@ include file='/jcore/doMessageBox.jsp' %>

	
	<div class="saisieDossier form-cg-gray form-cg-white">
		<form action="<%= ServletUtil.getResourcePath(request) %>" method="post">
			<p>Vous devez informer sans délais votre unité agrément de toute
				modification concernant votre situation familiale (naissance,
				séparation, nouvelle union, hébergement d'une personne majeure etc).
				Ce type de changement peut nécessiter une nouvelle visite à domicile
				afin d'évaluer vos nouvelles conditions d'accueil.</p>

			<div class="borderDot title-bar-container dotted-portlet"></div>

      <input type="hidden" name="title" value="<%= loggedMember %>">
      <% if (isLogged && HttpUtil.isCSRFEnabled()) { %>
        <input type="hidden" name="csrftoken" value="<%= getCSRFToken() %>" />
      <% } %> 

			<p class="intituleSaisie important">Merci de préciser la modification* :</p>
			<textarea rows="8" cols="80" name="modification"><%= Util.notEmpty(formHandler.getAvailableModification()) ? formHandler.getAvailableModification() : "" %></textarea>
      
      <jalios:if predicate="<%= Util.notEmpty(setPlace) %>">
      <div class="listeInfos uniteAgrement">
				<p class="important">Cette information sera transmise à votre
					unité d'agrément</p>
				<jalios:foreach collection="<%= setPlace %>" name="itPlace" type="Place">
				  <p> <%= itPlace.getTitle() %> </p>
				</jalios:foreach>
			</div>
			</jalios:if>
      
      <div class="listeVerticale">
				<input type="checkbox" name="certifieExactitude"
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
				</label> <input type="hidden" name="noSendRedirect" value="true"> <input
					type="hidden" name="id" value="c_5065">
			</p>
			
			<input type='hidden' name='redirect' value='<%= ServletUtil.getResourcePath(request) %>' />  

		</form>
	</div>

</div>