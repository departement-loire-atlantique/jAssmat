<%@page import="fr.cg44.plugin.assmat.AssmatUtil.SelectionLogin"%>
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
  return;
}
%>

<jsp:useBean id='formHandler' scope='page' class='fr.cg44.plugin.assmat.handler.ProfilContactsHandler'>
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

String dataColor= ProxyTarget.getMainColor(); 


String telephoneFixAssmat = profil.getTelephoneFixe() != null ? profil.getTelephoneFixe() : "" ;
String visibiliteTelephoneFixe = profil.getVisbiliteTelephoneFixe();

String telephoneMobileAssmat = profil.getAuthor().getMobile() != null ?  profil.getAuthor().getMobile() : "" ;
String visibiliteTelephonePortable = profil.getVisibiliteTelephonePortable();

String creneauAppels = profil.getCreneauHorairesDappel() != null ? profil.getCreneauHorairesDappel() : "";

String emailAssmat = loggedMember.getEmail() != null ? loggedMember.getEmail() : "" ;
String visibiliteAdresseEmail = profil.getVisibiliteAdresseEmail();

boolean afficherContactUniquementSiD = profil.getAfficherContactUniquementSiD();

SelectionLogin typeLogin = SelectionLogin.getTypeLoginByValue(profil.getTypeLogin());
int numeroDossierAssmat = profil.getNum_agrement(); 
%>

<%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/header.jspf' %>
<%@ include file='/jcore/doMessageBox.jsp' %>

<div class="headstall container-fluid formulaireActivation">
<div class="formActivation form-cg form-espace-perso">
<div class="form-cg-gray form-cg-white">

  <form method="post" action="<%= ServletUtil.getResourcePath(request) %>" 
    name="formContact" id="formContact" class="formContact formEspacePerso">

  
    <div class="alert alert-block alertPass hide  alert-cg">
      <h4><%=glp("msg.message-box.warning")%></h4>
      <p></p>
    </div>
  
  
    <h3 class="title-bar-container dotted-portlet"><trsb:glp key="CONTACT-INTROTEL-HTML"></trsb:glp></h3>
   
    <%-- Téléphone fix --%>
	  <div class="blocComplet gauche">
	    <div class="blocLabel">
	      <p class=""><trsb:glp key="CONTACT-TEL-HTML"></trsb:glp></p>
	    </div>
	    <div class="blocChamp multipleRadio civilite blocForm">
	      <span>
	        <input type="text" name="telFix" id="fixe" value='<%= telephoneFixAssmat %>' class="petit" />
	      </span>
	      <span>
	        <input type="radio" name="visibleFixMobile" id="oui_fixe" class="radio" value="true"
	          <%if(Util.notEmpty(visibiliteTelephoneFixe) && "true".equals(visibiliteTelephoneFixe) ){ %> checked="checked" <%} %> />
	        <label for="oui_fixe"><trsb:glp key="CONTACT-TEL-OUI-HTML"></trsb:glp></label>
	      </span>
	      <span>
	        <input type="radio" name="visibleFixMobile" id="non_fixe" class="radio" value="false"
	          <%if(Util.notEmpty(visibiliteTelephoneFixe) && "false".equals(visibiliteTelephoneFixe) ){ %> checked="checked" <%} %> />
	        <label for="non_fixe"><trsb:glp key="CONTACT-TEL-NON-HTML"></trsb:glp><br /></label> 
	      </span>
	    </div>
	  </div>   
   
    <%-- Téléphone mobile --%>
    <div class="blocComplet gauche">
	    <div class="blocLabel">
	      <p class=""><trsb:glp key="CONTACT-MOB-HTML"></trsb:glp></p>
	    </div>
	    <div class="blocChamp multipleRadio civilite blocForm">
	      <span>
	        <input type="text" name="telMobile" id="telephone" value='<%= telephoneMobileAssmat %>' class="petit" />
	      </span>
	      <span>
	        <input type="radio" name="visibleTelMobile" id="oui_portable" class="radio" value="true"
	          <%if(Util.notEmpty(visibiliteTelephonePortable) && "true".equals(visibiliteTelephonePortable) ){ %> checked="checked" <%} %> />
	        <label for="oui_portable"><trsb:glp key="CONTACT-MOB-OUI-HTML"></trsb:glp></label>
	      </span>
	      <span>
	        <input type="radio" name="visibleTelMobile" id="non_portable" class="radio" value="false"
	          <%if(Util.notEmpty(visibiliteTelephonePortable) && "false".equals(visibiliteTelephonePortable) ){%> checked="checked" <%} %> />
	        <label for="non_portable"><trsb:glp key="CONTACT-MOB-NON-HTML"></trsb:glp><br /></label> 
	      </span>
	    </div>
    </div>
   
    <%-- creneau appels --%>
    <div class="blocComplet gauche" id="creneau">
	    <div class="blocLabel">
	      <p class=""><trsb:glp key="CONTACT-TEL-HOR-HTML"></trsb:glp></p>
	    </div>
      <div class="blocChamp afficher-profile blocForm">
	      <span>
	        <input type="text" name="creneauAppels" id="creneau_input" value='<%= creneauAppels %>' class="petit">
	      </span>
	    </div>
    </div>
   
   
    <h3 class="title-bar-container dotted-portlet"><trsb:glp key="CONTACT-INTROMAIL-HTML"></trsb:glp></h3>
    
    <%-- email --%>
	  <div class="blocComplet gauche">	
	    <p><trsb:glp key="CONTACT-LEG-MAIL-HTML"></trsb:glp></p>
	
	    <div class="blocLabel">
	      <p class=""><trsb:glp key="CONTACT-MAIL-HTML"></trsb:glp></p>
	    </div>
	    
	    <div class="blocChamp multipleRadio civilite blocForm">
	      <span>
	        <input type="text" name="email" id="email" value='<%= emailAssmat %>' class="petit" />
	      </span>
	      <%-- visibilité email --%>
	      <span>
	        <input type="radio" name="visibleEmail" id="oui-mail" class="radio" value="true"
	          <%if(Util.notEmpty(visibiliteAdresseEmail) && "true".equals(visibiliteAdresseEmail) ){ %> checked="checked" <%} %> />
	        <label for="oui-mail"><trsb:glp key="CONTACT-MAIL-OUI-HTML"></trsb:glp></label>
	      </span>
	      <span>
	        <input type="radio" name="visibleEmail" id="non-mail" class="radio" value="false"
	          <%if(Util.notEmpty(visibiliteAdresseEmail) && "false".equals(visibiliteAdresseEmail) ){%> checked="checked" <%} %> />
	        <label for="non-mail"><trsb:glp key="CONTACT-MAIL-NON-HTML"></trsb:glp><br /></label> 
	      </span>
	    </div>
	  </div>
	  
	  
	  <h3 class="title-bar-container dotted-portlet"><trsb:glp key="CONTACT-DISPO-HTML"></trsb:glp></h3>
	  
	  <%-- afficher disponibilité --%>
	  <trsb:glp key="CONTACT-DISPO-LEG-HTML"></trsb:glp>	  
	  <div class="blocComplet gauche">
	    <div class="blocChamp multipleRadio civilite blocForm" style="margin-left: 0px; margin-top: 5px;">
	      <span>
	        <input type="radio" name="afficherContact" id="oui-si-dispo" class="radio" value="true"
	          <%if(afficherContactUniquementSiD){ %>  checked="checked" <%} %> style="background: transparent;">
	        <label for="oui-si-dispo"><trsb:glp key="CONTACT-DISPO-OUI-HTML"></trsb:glp></label>
	      </span>
	      <span>
	        <input type="radio" name="afficherContact" id="non-si-dispo" class="radio" value="false"
	          <%if(!afficherContactUniquementSiD){ %> checked="checked" <%} %>>
	        <label for="non-si-dispo"><trsb:glp key="CONTACT-DISPO-NON-HTML"></trsb:glp><br /></label> 
	      </span>
	    </div>
    </div>


    <div class="borderDot title-bar-container dotted-portlet" style="margin-top: 0px;"></div>

    <p class="submit">
      <label for="submit"> 
        <input type="submit" id="submit" name="opCreate" 
          value="<trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp>" class="submitButton" /> 
        <span class="input-box" style="background-color: #aec900" />
          <span class="spr-recherche-ok"></span>
        </span>
      </label> 
      <input type="hidden" name="noSendRedirect" value="true" /> 
      <input type="hidden" name="opUpdate" value="true" />
    </p>
    
  </form>


</div>
</div>
</div>
