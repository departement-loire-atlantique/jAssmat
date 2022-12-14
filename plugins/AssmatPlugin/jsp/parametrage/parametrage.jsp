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
    <jsp:setProperty name='formHandler' property='*' />
	<jsp:setProperty name='formHandler' property='request' value='<%= request %>' />
	<jsp:setProperty name='formHandler' property='response' value='<%= response %>' />
	<jsp:setProperty name='formHandler' property='id' value="<%= profileAssmat.getId() %>" />
	<%-- Etape contacts --%>
	<jsp:setProperty name='formHandler' property="visbiliteTelephoneFixe" value='<%= new String[]{getUntrustedStringParameter("visbiliteTelephoneFixe[0]", "none")} %>' />
	<jsp:setProperty name='formHandler' property="visibiliteTelephonePortable" value='<%= new String[]{getUntrustedStringParameter("visibiliteTelephonePortable[0]", "none")} %>' />
	<jsp:setProperty name='formHandler' property="visibiliteAdresseEmail" value='<%= new String[]{getUntrustedStringParameter("visibiliteAdresseEmail[0]", "none")} %>' />
	<jsp:setProperty name='formHandler' property="afficherContactUniquementSiD" value='<%= true && Boolean.parseBoolean(getUntrustedStringParameter("afficherContactUniquementSiD[0]", "false")) %>' />
	<%-- Etape offres --%>
	<jsp:setProperty name='formHandler' property="accueilTempsPartiel" value='<%= new String[]{getUntrustedStringParameter("accueilTempsPartiel[0]", "none")} %>' />
	<jsp:setProperty name='formHandler' property="accueilPeriscolaire" value='<%= new String[]{getUntrustedStringParameter("accueilPeriscolaire[0]", "none")} %>' />
	<jsp:setProperty name='formHandler' property="accueilMercredi" value='<%= new String[]{getUntrustedStringParameter("accueilMercredi[0]", "none")} %>' />
	<jsp:setProperty name='formHandler' property="accueilPendantLesVacancesSco" value='<%= new String[]{getUntrustedStringParameter("accueilPendantLesVacancesSco[0]", "none")} %>' />
	<jsp:setProperty name='formHandler' property="horairesAtypiques" value='<%= new String[]{getUntrustedStringParameter("horairesAtypiques[0]", "none")} %>' />
	<jsp:setProperty name='formHandler' property="avant7h" value='<%= new String[]{getUntrustedStringParameter("avant7h[0]", "none")} %>' />
	<jsp:setProperty name='formHandler' property="apres20h" value='<%= new String[]{getUntrustedStringParameter("apres20h[0]", "none")} %>' />
	<jsp:setProperty name='formHandler' property="leSamedi" value='<%= new String[]{getUntrustedStringParameter("leSamedi[0]", "none")} %>' />
	<jsp:setProperty name='formHandler' property="leDimanche" value='<%= new String[]{getUntrustedStringParameter("leDimanche[0]", "none")} %>' />
	<jsp:setProperty name='formHandler' property="laNuit" value='<%= new String[]{getUntrustedStringParameter("laNuit[0]", "none")} %>' />
	<jsp:setProperty name='formHandler' property="accepteDepannage" value='<%= new String[]{getUntrustedStringParameter("accepteDepannage[0]", "none")} %>' />
	<jsp:setProperty name='formHandler' property="accueilEnfantHandicap" value='<%= new String[]{getUntrustedStringParameter("accueilEnfantHandicap[0]", "none")} %>' />
	<jsp:setProperty name='formHandler' property="logementAccessible" value='<%= new String[]{getUntrustedStringParameter("logementAccessible[0]", "none")} %>' />
</jsp:useBean>
<%
// champs en radio buttons faute à une non cohésion entre les données requête
// et le comportement natif code jalios

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
  
  String uuid = UUID.randomUUID().toString();
%>

<main id="content">

    <div class="ds44-container-large">
       <div class="ds44-inner-container ds44-mtb5">
        <h2><%= box.getDisplayTitle(userLang) %></h2>
    
                <div class="row-fluid">
                    <div class="ajax-refresh-div">
                        <%@ include
                            file='/plugins/AssmatPlugin/jsp/parametrage/etapes.jspf'%>

                                <%@ include file='/jcore/doMessageBox.jsp'%>
                                <p><%= glp("jcmsplugin.socle.facette.champs-obligatoires") %></p>
                                <form method="post"
                                    action="<%= ServletUtil.getResourcePath(request) %>"
                                    name="formContact" id="formContact" data-no-encoding="true">
                                    
                                    <%@ include file='/plugins/AssmatPlugin/jsp/parametrage/headerTitle.jspf' %>
	                                <%@ include file='/plugins/AssmatPlugin/jsp/parametrage/visibilite.jspf'%>
	                                <%@ include file='/plugins/AssmatPlugin/jsp/parametrage/contacts.jspf'%>
	                                <%@ include file='/plugins/AssmatPlugin/jsp/parametrage/offre.jspf'%>
	                                <%@ include file='/plugins/AssmatPlugin/jsp/parametrage/disponibilite.jspf'%>
	                                <%@ include file='/plugins/AssmatPlugin/jsp/parametrage/autorisation.jspf'%>
                                    
                                    <!--  Ecriture des champ cachés             -->
                                    <%=formHandler.getFormStepHiddenFields()%>
                                    
			                        <input type="hidden" name="csrftoken" value="<%= getCSRFToken() %>" data-technical-field/>
			                        <input type="hidden" name="formStep" value="<%= Util.notEmpty(step) ? step : 0 %>" data-technical-field/>
                                </form>
                    </div>
                </div>
            </div>
    </div>

<%}else{ %>

<div class="alert alert-block fade in alert-cg"><button type="button" class="close" data-dismiss="alert"><span class="spr-modal-close"></span></button><h4>Attention</h4><p>Aucun profilAM retrouvé pour l'utilisateur <%=loggedMember %></p></div>

<%} %>
