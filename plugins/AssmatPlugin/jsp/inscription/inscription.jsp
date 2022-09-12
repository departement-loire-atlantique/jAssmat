<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file='/jcore/doInitPage.jsp'%>
<%@ include file='/jcore/portal/doPortletParams.jsp'%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%-- Formulaire à étape permettant de créer les espaces de covnersation --%>

<%
	PortletJsp box = (PortletJsp) portlet;
%>
<jsp:useBean id='formHandler' scope='page' class='fr.cg44.plugin.assmat.handler.InscriptionAssmatHandler'>
	<jsp:setProperty name='formHandler' property='request' value='<%=request%>' />
	<jsp:setProperty name='formHandler' property='response'	value='<%=response%>' />
	<jsp:setProperty name='formHandler' property="noRedirect" value="true" />
	<jsp:setProperty name='formHandler' property='*' />
</jsp:useBean>


<%
boolean notfoundCompte= false;
	if (formHandler.validate()) {
		request.setAttribute("modal.redirect", request.getAttribute("workspaceURL"));
		String idJspParam = channel.getProperty("jcmsplugin.assmatplugin.socle.jsp.parametrage");
		Publication jspParam = null;
		if(Util.notEmpty(idJspParam)){
		  jspParam = channel.getPublication(idJspParam);
		  jcmsContext.sendRedirect(jspParam);
		}
		
		return;
	}
	
	jcmsContext.addJavaScript("plugins/ToolsPlugin/js/facets/tooltipCategories.js");
	int step = formHandler.getFormStep();
	int stepCount = formHandler.getFormStepCount();

	String token = request.getParameter("token");

	String dataColor= "#FF0000"; 
	jcmsContext.addCSSHeader("plugins/EServicePlugin/css/types/AbstractPortletSkinable/titleBarDottedTitleElementBorderBot.css");
	jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/headstall/model1.css");
	jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/AbstractPortletSkinable/titleBar.css");
	jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/common.css");
	jcmsContext.addCSSHeader("plugins/AssmatPlugin/css/plugin.css");
	jcmsContext.addJavaScript("plugins/AssmatPlugin/js/plugin.js");
%>

<%
	if (Util.isEmpty(token)) {
%>
<main id="content">

	<div class="formulaireActivation ds44-container-large">
	
	    <h2><trsb:glp key="IDENT-TITRE"></trsb:glp></h2>
	
			<div class="span9">
				<div class="row-fluid">
					<div class="ajax-refresh-div">
						<%@ include
							file='/plugins/AssmatPlugin/jsp/inscription/etapes.jspf'%>
	
						<div class="formActivation form-cg">
							<div class="form-cg-gray">
								<%@ include file='/jcore/doMessageBox.jsp'%>
								<form method="post"
									action="<%=ServletUtil.getResourcePath(request)%>"
									name="formContact" id="formContact" class="formContact">
	
	
	
									<%@ include
										file='/plugins/AssmatPlugin/jsp/inscription/headerTitle.jspf'%>
									<%@ include
										file='/plugins/AssmatPlugin/jsp/inscription/identification.jspf'%>
									<%@ include
										file='/plugins/AssmatPlugin/jsp/inscription/verification.jspf'%>
									<%@ include
										file='/plugins/AssmatPlugin/jsp/inscription/contacts.jspf'%>
									<%@ include
										file='/plugins/AssmatPlugin/jsp/inscription/loginEtMotDePasse.jspf'%>
									<%@ include
										file='/plugins/AssmatPlugin/jsp/inscription/confirmation.jspf'%>
									<!--  Ecriture des champ cachés             -->
									<%=formHandler.getFormStepHiddenFields()%>
									<jalios:if predicate="<%= HttpUtil.isCSRFEnabled() %>">
					    	<input type="hidden" name="csrftoken" value="<%= getCSRFToken() %>"/>
						</jalios:if> 
	
								</form>
							</div>
						</div>
	
	
					</div>
				</div>
			</div>
	</div>
	
	<%
		} else {
			//Insscription valide
			if (!formHandler.isInscriptionOK()) {
	%>
	<div class="headstall container-fluid formulaireActivation">
		<div class="row-fluid">
			<!-- COLONNE GAUCHE -->
			<div class="span2 iconEtape">
				<img alt="etape1"
					src="plugins/AssmatPlugin/img/icon-activation-form.png" />
			</div>
			<!-- FIN COLONNE GAUCHE -->
			<!-- COLONNE DROITE -->
			<div class="span10">
				<div class="row-fluid title">
				   <h2>Inscription invalide</h2>
				</div>
			</div>
			<div class="span9">
				<div class="row-fluid">
					<div class="ajax-refresh-div">
						<%@ include file='/jcore/doMessageBox.jsp'%>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	
	<%
		}
		}
	%>

</main>