<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
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
<jsp:useBean id='formHandler' scope='page' class='fr.cg44.plugin.assmat.handler.ProfilLoginMdpHandler'>
  <jsp:setProperty name='formHandler' property='request' value='<%= request %>'/>
  <jsp:setProperty name='formHandler' property='response' value='<%= response %>'/>
  <jsp:setProperty name='formHandler' property="noRedirect" value="true" />
  <jsp:setProperty name='formHandler' property="profil" value='<%= profil %>' />
  <jsp:setProperty name='formHandler' property="member" value='<%= loggedMember %>' />
  <jsp:setProperty name='formHandler' property='*' />  
</jsp:useBean>


<%
if (formHandler.validate()) {
  	return; 
  } 


  if(Util.notEmpty(profil)){
    
	  String dataColor= ProxyTarget.getMainColor(); 
   boolean loginMail=false;
   boolean loginTel= false;
   boolean loginAgrement= false;
   
  		String emailAssmat = loggedMember.getEmail();
  		String telephoneMobileAssmat = profil.getAuthor().getMobile();
  		SelectionLogin typeLogin = SelectionLogin.getTypeLoginByValue(profil.getTypeLogin());
  		int numeroDossierAssmat = profil.getNum_agrement();
  %>

<%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/header.jspf' %>
 <%@ include file='/jcore/doMessageBox.jsp' %>
<div class="headstall container-fluid formulaireActivation">
<div class="form-cg">
                  <div class="form-cg-gray form-cg-white form-espace-perso">
<form method="post" action="<%= ServletUtil.getResourcePath(request) %>"  name="formContact" id ="formContact" class="formContact formEspacePerso">

       <h3 class="title-bar-container dotted-portlet"><%=glp("jcmsplugin.assmatplugin.espaceperso.modiflogin") %></h3>
              
            <%if(SelectionLogin.MAIL.equals(typeLogin)){ %>
            <p><b><%=glp("jcmsplugin.assmatplugin.espaceperso.utilisemail") %></b> <span class="surligne"><%=emailAssmat %></span> </p>
            
            <%}else if(SelectionLogin.NUMERO_DOSSIER.equals(typeLogin)){ %>
            <p><b><%=glp("jcmsplugin.assmatplugin.espaceperso.utiliseagr") %></b> <%=numeroDossierAssmat %> </p>
            
            <%} else if(SelectionLogin.TELEPHONE.equals(typeLogin)){ %>
            <p><b><%=glp("jcmsplugin.assmatplugin.espaceperso.utilisetel") %></b> <%=telephoneMobileAssmat %> </p>
            <%} %>
            
            
            <p class="info"><%=glp("jcmsplugin.assmatplugin.espaceperso.declaremail") %></p>
            <br>
            <p class="info"><b><%=glp("jcmsplugin.assmatplugin.espaceperso.declarelog") %></b></p>
            <br>
            <div class="blocComplet droit">
              <div class="blocLabel">
                <p class=""><trsb:glp key="LIBELLE-PRERENCE-UTILISATION-LOGIN-HTML" ></trsb:glp></p>
              </div>
    
              <div class="blocChamp multipleRadio">
              <% 
                Member mbrMailFind = channel.getMemberFromLogin(emailAssmat); 
              %>
              <%if(Util.notEmpty(emailAssmat) && ( Util.isEmpty(mbrMailFind) || JcmsUtil.isSameId(mbrMailFind, loggedMember)) ){ %>
              <div class="blocSimple">
                <input required="required" type="radio" <%if(SelectionLogin.MAIL.equals(typeLogin)){%> checked="checked" <%} %> name="choixLogin" id="email" class="radio"
                  value="2" style="background: transparent;">
                  <label for="email"><%=glp("jcmsplugin.assmatplugin.espaceperso.monmail") %> <%=emailAssmat%> 
                   <button class="cg-tooltip buttonHelp" data-category-id="none" aria-label="<trsb:glp key="HELP-PREFERENCE-LOGIN-MAIL-HTML" attribute="true"></trsb:glp>" data-color="<%=dataColor%>">
                <img alt="?" src="s.gif" class="spr-interrogation">
                </button>
                </label>
                </div>
                  <%} %>
                 
                <%
                  Member mbrMobileFind = channel.getMemberFromLogin(telephoneMobileAssmat); 
                %> 
                <%if(Util.notEmpty(telephoneMobileAssmat) &&  ( Util.isEmpty(mbrMobileFind) || JcmsUtil.isSameId(mbrMobileFind, loggedMember))  ){ %>
                <div class="blocSimple">
                  <input
                  type="radio" name="choixLogin" id="tel" class="radio" <%if(SelectionLogin.TELEPHONE.equals(typeLogin)){%> checked="checked" <%} %> value="1"><label
                  for="tel"><%=glp("jcmsplugin.assmatplugin.espaceperso.monnum") %> <%=telephoneMobileAssmat %>
                    <button class="cg-tooltip buttonHelp" data-category-id="nonez" aria-label="<trsb:glp attribute="true" key="LOGIN-PREF-CONNEXION-TEL-BULLE-HTML" ></trsb:glp>" 
                data-color="<%=dataColor%>">
                <img alt="?" src="s.gif" class="spr-interrogation">
                </button>   
                  
                </label> 
                </div>
                <%} %>
                <input type="radio" name="choixLogin" id="dossier" class="radio"  <%if(SelectionLogin.NUMERO_DOSSIER.equals(typeLogin)){%> checked="checked" <%} %>
                  value="3"><label for="dossier"><%=glp("jcmsplugin.assmatplugin.espaceperso.monagr") %> <%=numeroDossierAssmat %> 
                  <button class="cg-tooltip buttonHelp" data-category-id="nonea" aria-label="<trsb:glp attribute="true" key="LOGIN-PREF-CONNEXION-DOSSIER-BULLE-HTML" ></trsb:glp>" 
                data-color="<%=dataColor%>">
                <img alt="?" src="s.gif" class="spr-interrogation">
                </button>
                </label>
              </div>
          </div>
              <h3 class="title-bar-container dotted-portlet"><%=glp("jcmsplugin.assmatplugin.espaceperso.modifmdp") %></h3>
              <p class="instructionsSaisie"><%=glp("jcmsplugin.assmatplugin.espaceperso.motdepasscomporte") %></p>
              
              
            <div class="alert alert-block alertPass hide  alert-cg alert-warn"><h4><%=glp("msg.message-box.warning")%></h4>
            <p></p>
            </div>
             <div class="blocComplet droit">
              <div class="blocLabel">
                <label for="password"><%=glp("jcmsplugin.assmatplugin.espaceperso.motdepass") %></label> 
              </div>
            
              <div class="blocChamp">
                <input type="password"  name="password" id="password" class="fullwidth fullwidthInfo"> 
                <button class="cg-tooltip buttonHelp" data-category-id="nfdone" aria-label="<trsb:glp attribute="true" key="LOGIN-MDP-BULLE-HTML" ></trsb:glp>" 
                data-color="<%=dataColor%>">
                <img alt="?" src="s.gif" class="spr-interrogation">
                </button>
              </div>
            </div>
            <div class="blocComplet droit">
              <div class="blocLabel">
                <label for="confirm"><%=glp("jcmsplugin.assmatplugin.espaceperso.passconfirm") %></label>
              </div>

              <div class="blocChamp">
                <input type="password" name="passwordConfirm" id="passwordConfirm" class="fullwidth fullwidthInfo"> 
                <button class="cg-tooltip buttonHelp" data-category-id="nofde" aria-label="<trsb:glp attribute="true" key="LOGIN-MDP-CONF-BULLE-HTML" ></trsb:glp>" 
                data-color="<%=dataColor%>">
                <img alt="?" src="s.gif" class="spr-interrogation">
                </button>
              </div>
            </div>
              <div class="borderDot title-bar-container dotted-portlet"></div>

              <p class="submit">
                <label for="submit"> <input type="submit" id="submit"
                  name="opCreate" value="<trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp>" class="submitButton">
                  <span class="input-box" style="background-color: #aec900"><span
                    class="spr-recherche-ok"></span></span>
                </label> <input type="hidden" name="noSendRedirect" value="true">
                <input type="hidden" name="numeroAgrement" value="<%=numeroDossierAssmat %>" />
                 <input type="hidden" name="opUpdate" value="true">
              </p>

  </form> 
  </div>
  </div>
  </div>

    

<%
}
%>


<jalios:javascript>

jQuery( "#password" ).focusout(function(event) {    
    jQuery.plugin.AssmatPlugin.verifyMotDePasse("#password", "<%=AssmatUtil.getMessage("ERROR-MDP-SECURITY") %>");   
 });
 
</jalios:javascript>



