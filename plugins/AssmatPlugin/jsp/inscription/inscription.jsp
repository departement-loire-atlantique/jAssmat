<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file='/jcore/doInitPage.jsp'%>
<%@ include file='/jcore/portal/doPortletParams.jsp'%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%-- Formulaire à étape permettant de créer les espaces de covnersation --%>

<%
PortletJsp box = (PortletJsp) portlet;

/* Lors de la soumission du formulaire, c'est la valeur de la case à cocher qui est renvoyée (fieldValue[0])
   quand on revient sur une page précédente ou sur une page suivante, c'est la valeur du champ caché qui en renvoyé (fieldValue)
   On doit donc tester la valeur de ces 2 paramètres.
   On doit aussi regarder si le tél/email ont été modifiés. On doit donc tester la valeur des champs cachés
   du formulaire et des champs visibles.
   
*/
String civiliteParam = null;
String typeEnvoiParam = null;
String choixLoginParam = null;
String emailParam = null;
String telephoneParam = null;

if(Util.notEmpty(request.getParameter("emailModifiable"))){
  emailParam = request.getParameter("emailModifiable");
}else if(Util.notEmpty(request.getParameter("email"))){
  emailParam = request.getParameter("email");
}

if(Util.notEmpty(request.getParameter("telephoneModifiable"))){
  telephoneParam = request.getParameter("telephoneModifiable");
}else if(Util.notEmpty(request.getParameter("telephone"))){
  telephoneParam = request.getParameter("telephone");
}

if(Util.notEmpty(request.getParameter("civilite[0]"))){
  civiliteParam = request.getParameter("civilite[0]");
}else if(Util.notEmpty(request.getParameter("civilite"))){
  civiliteParam = request.getParameter("civilite");
}

if(Util.notEmpty(request.getParameter("typeenvoi[0]"))){
  typeEnvoiParam = request.getParameter("typeenvoi[0]");
}else if(Util.notEmpty(request.getParameter("typeenvoi"))){
  typeEnvoiParam = request.getParameter("typeenvoi");
}

if(Util.notEmpty(request.getParameter("choixLogin[0]"))){
  choixLoginParam = request.getParameter("choixLogin[0]");
}else if(Util.notEmpty(request.getParameter("choixLogin"))){
  choixLoginParam = request.getParameter("choixLogin");
}

%>

<jsp:useBean id='formHandler' scope='page' class='fr.cg44.plugin.assmat.handler.InscriptionAssmatHandler'>
    <jsp:setProperty name='formHandler' property='*' />
	<jsp:setProperty name='formHandler' property='request' value='<%=request%>' />
	<jsp:setProperty name='formHandler' property='response'	value='<%=response%>' />
	<jsp:setProperty name='formHandler' property="noRedirect" value="true" />
	<jsp:setProperty name='formHandler' property="civilite" value='<%= civiliteParam %>' />
	<jsp:setProperty name='formHandler' property="typeenvoi" value='<%= typeEnvoiParam %>' />
	<jsp:setProperty name='formHandler' property="choixLogin" value='<%= choixLoginParam %>' />
	<jsp:setProperty name='formHandler' property="email" value='<%= emailParam %>' />
	<jsp:setProperty name='formHandler' property="telephone" value='<%= telephoneParam %>' />
</jsp:useBean>


<%
//UUID unique pour les champs
String uuid = UUID.randomUUID().toString();

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

	<div class="ds44-container-large">
	   <div class="ds44-inner-container">
	    <h2><trsb:glp key="IDENT-TITRE"></trsb:glp></h2>
	
				<div class="row-fluid">
					<div class="ajax-refresh-div">
						<%@ include
							file='/plugins/AssmatPlugin/jsp/inscription/etapes.jspf'%>

								<%@ include file='/plugins/SoclePlugin/jsp/doMessageBoxCustom.jspf' %>
								<p><%= glp("jcmsplugin.socle.facette.champs-obligatoires") %></p>

								<form method="post"
									action="<%= ServletUtil.getResourcePath(request) %>"
									name="formContact" id="formContact" data-no-encoding="true">
									
		                            <input type="hidden" name="csrftoken" value="<%= HttpUtil.getCSRFToken(request) %>" data-technical-field>
		                            
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
	
								</form>

					</div>
				</div>
			</div>
	</div>
	
	<%
		} else {
			//Insscription valide
			if (!formHandler.isInscriptionOK()) {
	%>
	<main id="content">

    <div class="ds44-container-large">
       <div class="ds44-inner-container">
		<div class="container-fluid" >
			<div class="row-fluid">
				<div>
					<div class="row-fluid title">
					   <h2 class="h2-like"><%= glp("jcmsplugin.assmatplugin.inscription.invalide") %></h2>
					</div>
				</div>
				<div>
					<div class="row-fluid">
						<div class="ajax-refresh-div">
							<%@ include file='/jcore/doMessageBox.jsp'%>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	
	<%
		}
		}
	%>
    </div>
</main>