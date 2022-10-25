<%@page import="fr.cg44.plugin.assmat.AssmatUtil.SelectionLogin"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
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
	return;
}
%>

<jsp:useBean id='formHandler' scope='page' class='fr.cg44.plugin.assmat.handler.ProfilPrefsContactHandler'>
  <jsp:setProperty name='formHandler' property='request' value='<%= request %>'/>
  <jsp:setProperty name='formHandler' property='response' value='<%= response %>'/>
  <jsp:setProperty name='formHandler' property="noRedirect" value="true" />
  <jsp:setProperty name='formHandler' property="profil" value='<%= profil %>' />
  <jsp:setProperty name='formHandler' property="member" value='<%= loggedMember %>' />
  <jsp:setProperty name='formHandler' property='*' />  
</jsp:useBean>


<%
if (formHandler.validate()) {
  //return;
}

String emailAssmat = loggedMember.getEmail() != null ? loggedMember.getEmail() : "" ;
String telephoneMobileAssmat = profil.getAuthor().getMobile() != null ? profil.getAuthor().getMobile() : "" ;
String canalEnvoi = profil.getCanalDeCommunicationSite();
SelectionLogin typeLogin = SelectionLogin.getTypeLoginByValue(profil.getTypeLogin());
int numeroDossierAssmat = profil.getNum_agrement(); 

boolean isCanalMail = AssmatUtil.SelectionPreferenceReception.MAIL.getValue().equals(canalEnvoi);
boolean isCanalTel = AssmatUtil.SelectionPreferenceReception.TELEPHONE.getValue().equals(canalEnvoi);
%>

<%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/header.jspf' %>
<%@ include file='/jcore/doMessageBox.jsp' %>

<div class="headstall container-fluid formulaireActivation">
<div class="formActivation form-cg form-espace-perso">
<div class="form-cg-gray form-cg-white">

  <form method="post" action="<%= ServletUtil.getResourcePath(request) %>" 
    name="formContact" id="formContact" class="formContact formEspacePerso">

    <p class="info"><trsb:glp key="CONTACTS-EXEMPLES-HTML"></trsb:glp></p>
    <br>
    
    
    <div class="blocLabel" style="text-align: left;">
    <p class=""><trsb:glp key="CONTACTS-PREF-HTML"></trsb:glp></p>
    </div>

    <%-- Radio box par e-mail ou par SMS --%>
    <div class="blocChamp multipleRadio civilite">
      <%-- Par e-mail --%>
      <span>
        <input required="required"  <%if(isCanalMail){ %>checked="checked"  <%} %> type="radio" name="choixCanalComm" id="email" class="radio" 
          value='<%= AssmatUtil.SelectionPreferenceReception.MAIL.getValue() %>' style="background: transparent;" />
        <label for="email">
          <trsb:glp key="CONTACTS-PREF-EMAIL-HTML" ></trsb:glp>
        </label>
      </span>
      <%-- Par SMS --%>
      <span>
        <input type="radio" <%if(isCanalTel){ %>checked="checked"  <%} %> name="choixCanalComm" id="tel" class="radio" 
        value='<%= AssmatUtil.SelectionPreferenceReception.TELEPHONE.getValue() %>' />
        <label for="tel">
          <trsb:glp  key="CONTACTS-PREF-SMS-HTML" ></trsb:glp><br />
        </label> 
      </span>
    </div>

    <div class="clear"></div>
		<h3 class="title-bar-container dotted-portlet" style="margin-top: 20px;">Modifier si besoin vos coordonnées</h3>

    <div class="alert alert-block alertPass hide  alert-cg">
			<h4><%=glp("msg.message-box.warning")%></h4>
      <p></p>
    </div>
        
    <%-- Votre adresse e-mail --%>
		<div class="blocComplet droit">								
			<div class="blocLabel blocForm">
				<label for="email"><trsb:glp key="CONTACTS-EMAIL-HTML"></trsb:glp></label>
			</div>
			<div class="blocChamp blocForm">
				<input type="text" name="email" id="email" value="<%= emailAssmat %>" class="fullwidth fullwidthInfo">
				<button class="cg-tooltip buttonHelp" data-category-id="none"
					aria-label="<trsb:glp attribute="true" key="CONTACTS-EMAIL-BULLE-HTML" ></trsb:glp>"
					data-color="<%=dataColor%>">
					<img alt="?" src="s.gif" class="spr-interrogation">
				</button>
			</div>
    </div>

    <%-- et/ou --%>
    <div class="blocComplet gauche simple">					
      <div class="blocLabel blocForm">
        <p>et/ou</p>
      </div>
    </div>

    <%-- Votre téléphone mobile --%>
    <div class="blocComplet droit">      
      <div class="blocLabel blocForm">
        <label for="telephone"><trsb:glp key="CONTACTS-TEL-PORTABLE-HTML"></trsb:glp></label>
      </div>
      <div class="blocChamp blocForm">
        <input type="text" name="telMobile" id="telephone" value="<%= telephoneMobileAssmat %>" class="fullwidth fullwidthInfo" />
        <button class="cg-tooltip buttonHelp" data-category-id="none"
					aria-label="<trsb:glp attribute="true" key="CONTACTS-TEL-BULLE-HTML" ></trsb:glp>"
					data-color="<%=dataColor%>">
					<img alt="?" src="s.gif" class="spr-interrogation">
				</button>
			</div>				
    </div>



    <div class="borderDot title-bar-container dotted-portlet"></div>

    <p class="submit">
      <label for="submit"> 
        <input type="submit" id="submit" name="opCreate" 
          value="<trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp>" class="submitButton" /> 
		    <span class="input-box" style="background-color: #aec900" />
          <span class="spr-recherche-ok"></span>
		    </span>
		  </label> 
		  <input type="hidden" name="noSendRedirect" value="true" /> 
		  <input type="hidden" name="numeroAgrement" value="<%= numeroDossierAssmat %>" /> 
      <input type="hidden" name="opUpdate" value="true" />
    </p>
    
  </form>


</div>
</div>
</div>