<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file='/jcore/doInitPage.jsp'%>
<%@ include file='/jcore/portal/doPortletParams.jsp' %>
<%@ page import="fr.cg44.plugin.assmat.managers.ProfilManager" %>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%-- Formulaire à étape permettant de créer les espaces de covnersation --%>

<%
PortletJsp box = (PortletJsp) portlet;

ProfilManager profilMngr = ProfilManager.getInstance();
ProfilASSMAT profileAssmat = (ProfilASSMAT) profilMngr.getProfilASSMAT(channel.getCurrentLoggedMember());

if(Util.isEmpty(profileAssmat)){
	sendForbidden(request, response);
}

if(Util.notEmpty(profileAssmat)){
  
  SolisManager solisManager = SolisManager.getInstance();
  Boolean aide_caf = Util.getFirst(solisManager.getAssmatSolisByNumAgrement(profileAssmat.getNum_agrement())).getAideCaf() ;
  if(aide_caf == null) {
   aide_caf = false;
  }
  
%>
<jsp:useBean id='formHandler' scope='page' class='fr.cg44.plugin.assmat.handler.ParametrageAssmatHandler'>
	<jsp:setProperty name='formHandler' property='request' value='<%= request %>' />
	<jsp:setProperty name='formHandler' property='response' value='<%= response %>' />
	<jsp:setProperty name='formHandler' property='*' />
	<jsp:setProperty name='formHandler' property='id' value="<%= profileAssmat.getId() %>" />
</jsp:useBean>
<%
if (formHandler.validate()) {
   request.setAttribute("modal.redirect", request.getAttribute("workspaceURL")); 
   return; 
} 

String adresseMail = formHandler.getAdresseMail();
if(!hasParameter("adresseMail")){
   adresseMail = loggedMember.getEmail();
   formHandler.setAdresseMail(adresseMail);
}

int etape = formHandler.getFormStep();
if(!hasParameter("formStep")){
   etape = profileAssmat.getEtapeDeValidation();
}
int step = etape;
int stepCount = formHandler.getFormStepCount();


  jcmsContext.addCSSHeader("plugins/EServicePlugin/css/types/AbstractPortletSkinable/titleBarDottedTitleElementBorderBot.css");
  jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/headstall/model1.css");
  jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/AbstractPortletSkinable/titleBar.css");
  jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/common.css");
  jcmsContext.addCSSHeader("plugins/AssmatPlugin/css/plugin.css");
  jcmsContext.addJavaScript("plugins/AssmatPlugin/js/plugin.js");
%>

<div class="headstall container-fluid formulaireActivation">
	<div class="row-fluid">
		<!-- COLONNE GAUCHE -->
		<div class="span2 iconEtape">
			<img alt="etape1" src="plugins/AssmatPlugin/img/icon-espace-perso-form.png" />
		</div>
		<!-- FIN COLONNE GAUCHE -->
		<!-- COLONNE DROITE -->
		<div class="span10 label">
			<div class="row-fluid title">
				<div class="label">
					<h1><%= box.getDisplayTitle(userLang) %></h1>

					<div class="menu compte hidden-phone printHide">

						<span class="profil"><%= loggedMember.getFirstName() %> <%= loggedMember.getName() %></span>

						<div
							class="sep-visible-desktop dropdown no-separator dropdown-partage accesCompte">
							<a href="<%= channel.getProperty("jcms.resource.logout") %>"
								title="Déconnexion" class="compte"><%= glp("plugin.corporateidentity.header.deconnexion") %></a>
						</div>

					</div>

				</div>
			</div>
		</div>
		<div class="span9 label">
			<div class="row-fluid">
				<div class="ajax-refresh-div">
					<%@ include file='/plugins/AssmatPlugin/jsp/parametrage/etapes.jspf'%>

					<div class="formActivation form-cg">
						<div class="form-cg-gray">
							<%@ include file='/jcore/doMessageBox.jsp'%>
							<form method="post" action="<%= ServletUtil.getResourcePath(request) %>" class="formContact">
				        		<%@ include file='/plugins/AssmatPlugin/jsp/parametrage/headerTitle.jspf' %>
				        		<%@ include file='/plugins/AssmatPlugin/jsp/parametrage/visibilite.jspf'%>
								<%@ include file='/plugins/AssmatPlugin/jsp/parametrage/contacts.jspf'%>
								<%@ include file='/plugins/AssmatPlugin/jsp/parametrage/offre.jspf'%>
								<%@ include file='/plugins/AssmatPlugin/jsp/parametrage/disponibilite.jspf'%>
								<%@ include file='/plugins/AssmatPlugin/jsp/parametrage/autorisation.jspf'%>

								<%=formHandler.getFormStepHiddenFields()%>
                                  <% if (isLogged && HttpUtil.isCSRFEnabled()) { %>
                                  <input type="hidden" name="csrftoken" value="<%= getCSRFToken() %>" />
                                  <% } %>
							</form>
						</div>
					</div>
					<!--  Ecriture des champ cachés             -->
				</div>
			</div>
		</div>
	</div>
</div>
<%}else{ %>

<div class="alert alert-block fade in alert-cg"><button type="button" class="close" data-dismiss="alert"><span class="spr-modal-close"></span></button><h4>Attention</h4><p>Aucun profilAM retrouvé pour l'utilisateur <%=loggedMember %></p></div>

<%} %>
