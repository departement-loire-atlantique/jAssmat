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


  
  String uuid = UUID.randomUUID().toString();
%>


    <div class="ds44-container-large">
       <div class="ds44-inner-container ds44-mtb5">
        <h2><%= box.getDisplayTitle(userLang) %></h2>
    
                <div class="row-fluid">
                    
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

<%}else{ %>

<div class="alert alert-block fade in alert-cg"><button type="button" class="close" data-dismiss="alert"><span class="spr-modal-close"></span></button><h4>Attention</h4><p>Aucun profilAM retrouvé pour l'utilisateur <%=loggedMember %></p></div>

<%} %>
