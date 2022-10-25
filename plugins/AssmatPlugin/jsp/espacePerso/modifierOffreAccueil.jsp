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

<jsp:useBean id='formHandler' scope='page' class='fr.cg44.plugin.assmat.handler.ProfilOffreAccueilHandler'>
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

String accueilTempsPartiel = profil.getAccueilTempsPartiel();
String accueilPeriscolaire = profil.getAccueilPeriscolaire();
String accueilMercredi = profil.getAccueilMercredi();
String accueilPendantLesVacancesSco = profil.getAccueilPendantLesVacancesSco();
String horairesAtypiques = profil.getHorairesAtypiques();
String avant7h = profil.getAvant7h();
String apres20h = profil.getApres20h();
String leSamedi = profil.getLeSamedi();
String leDimanche = profil.getLeDimanche();
String laNuit = profil.getLaNuit();
String accepteDepannage = profil.getAccepteDepannage();
String precisionsDepannage = profil.getPrecisionsDepannage();
String accueilEnfantHandicap = profil.getAccueilEnfantHandicap();
String precisionsEnfantHandicap = profil.getPrecisionsEnfantHandicap();
String logementAccessible = profil.getLogementAccessible();
String precisionsLogementAccessible = profil.getPrecisionsLogementAccessible();


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
  
  
    <h3 class="title-bar-container dotted-portlet"><trsb:glp key="OFFRE-INTRO-HTML"></trsb:glp></h3>
    
        
    <div class="blocQuestion">
      
      <%-- Accueil --%>
      <h3><trsb:glp key="OFFRE-TYPE-HTML"></trsb:glp></h3>  
	    
	    
	    <%-- Accueil partie GAUCHE --%>
	    <div class="blocSelection">
			  <p><trsb:glp key="OFFRE-PARTIEL-HTML"></trsb:glp></p>
			  <input type="radio" name="accueilTempsPartiel" id="ouiTpsPartiel" class="radio" value="true"
			    <%if(Util.notEmpty(accueilTempsPartiel) && "true".equals(accueilTempsPartiel)){ %> checked="checked" <%} %>  />
			  <label for="ouiTpsPartiel" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.true") %></label>
			  <input type="radio" name="accueilTempsPartiel" id="nonTpsPartiel" class="radio" value="false"
			    <%if(Util.notEmpty(accueilTempsPartiel) && "false".equals(accueilTempsPartiel)){ %> checked="checked" <%} %> />
			  <label for="nonTpsPartiel" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.false") %></label>
			
			  <p><trsb:glp key="OFFRE-PERISCO-HTML"></trsb:glp></p>
			  <input type="radio" name="accueilPeriscolaire" id="ouiPeriscolaire" class="radio" value="true"
			    <%if(Util.notEmpty(accueilPeriscolaire) && "true".equals(accueilPeriscolaire)){ %> checked="checked" <%} %>>
			  <label for="ouiPeriscolaire" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.true") %></label>
			  <input type="radio" name="accueilPeriscolaire" id="nonPeriscolaire" class="radio" value="false"
			    <%if(Util.notEmpty(accueilPeriscolaire) && "false".equals(accueilPeriscolaire)){ %> checked="checked" <%} %>>
			  <label for="nonPeriscolaire" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.false") %></label>
			
			  <p><trsb:glp key="OFFRE-MERC-HTML"></trsb:glp></p>
			  <input type="radio" name="accueilMercredi" id="ouiMercredi" class="radio" value="true"
			    <%if(Util.notEmpty(accueilMercredi) && "true".equals(accueilMercredi)){ %> checked="checked" <%} %>>
			  <label for="ouiMercredi" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.true") %></label>
			  <input type="radio" name="accueilMercredi" id="nonMercredi" class="radio" value="false"
			    <%if(Util.notEmpty(accueilMercredi) && "false".equals(accueilMercredi)){ %> checked="checked" <%} %>>
			  <label for="nonMercredi" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.false") %></label>
			
			  <p><trsb:glp key="OFFRE-VAC-HTML"></trsb:glp></p>
			  <input type="radio" name="accueilPendantLesVacancesSco" id="ouiVacances" class="radio" value="true"
			    <%if(Util.notEmpty(accueilPendantLesVacancesSco) && "true".equals(accueilPendantLesVacancesSco)){ %> checked="checked" <%} %>>
			  <label for="ouiVacances" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.true") %></label>
			  <input type="radio" name="accueilPendantLesVacancesSco" id="nonVacances" class="radio" value="false"
			    <%if(Util.notEmpty(accueilPendantLesVacancesSco) && "false".equals(accueilPendantLesVacancesSco)){ %> checked="checked" <%} %>>
			  <label for="nonVacances" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.false") %></label>
	    </div>
	    
	    
	    <%-- Accueil partie DROITE --%>
	    <div class="blocSelection2">
			  <p><trsb:glp key="OFFRE-ATYP-HTML"></trsb:glp></p>
			  <input type="radio" name="horairesAtypiques" id="ouiHorairesAtypiques" class="radio" value="true"
			    <%if(Util.notEmpty(horairesAtypiques) && "true".equals(horairesAtypiques)){ %> checked="checked" <%} %>
			    onclick="javascript:jQuery.plugin.AssmatPlugin.toggle('precisezAtypiques', 'show');">
			  <label for="ouiHorairesAtypiques" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.true") %></label>
			  <input type="radio" name="horairesAtypiques" id="nonHorairesAtypiques" class="radio" value="false"
			    <%if(Util.notEmpty(horairesAtypiques) && "false".equals(horairesAtypiques)){ %> checked="checked" <%} %>
			    onclick="javascript:jQuery.plugin.AssmatPlugin.toggle('precisezAtypiques', 'hide');">
			  <label for="nonHorairesAtypiques" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.false") %></label>
			
			  <span  id="precisezAtypiques">
			    <h3 class="title-bar-container dotted-portlet smallPadding"><trsb:glp key="OFFRE-ATYP-PLUS-HTML"></trsb:glp></h3>
			  
			    <p><trsb:glp key="OFFRE-ATYP-AV7-HTML"></trsb:glp></p>
			    <input type="radio" name="avant7h" id="ouiMatin" class="radio" value="true"
			      <%if(Util.notEmpty(avant7h) && "true".equals(avant7h)){ %> checked="checked" <%} %>>
			    <label for="ouiMatin" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.true") %></label>
			    <input type="radio" name="avant7h" id="nonMatin" class="radio" value="false"
			      <%if(Util.notEmpty(avant7h) && "false".equals(avant7h)){ %> checked="checked" <%} %>>
			    <label for="nonMatin" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.false") %></label>
			  
			    <p><trsb:glp key="OFFRE-ATYP-AP20-HTML"></trsb:glp></p>
			    <input type="radio" name="apres20h" id="ouiSoir" class="radio" value="true"
			      <%if(Util.notEmpty(apres20h) && "true".equals(apres20h)){ %> checked="checked" <%} %>>
			    <label for="ouiSoir" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.true") %></label>
			    <input type="radio" name="apres20h" id="nonSoir" class="radio" value="false"
			      <%if(Util.notEmpty(apres20h) && "false".equals(apres20h)){ %> checked="checked" <%} %>>
			    <label for="nonSoir" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.false") %></label>
			  
			    <p><trsb:glp key="OFFRE-ATYP-SAM-HTML"></trsb:glp></p>
			    <input type="radio" name="leSamedi" id="ouiSamedi" class="radio" value="true"
			      <%if(Util.notEmpty(leSamedi) && "true".equals(leSamedi)){ %> checked="checked" <%} %>>
			    <label for="ouiSamedi" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.true") %></label>
			    <input type="radio" name="leSamedi" id="nonSamedi" class="radio" value="false"
			      <%if(Util.notEmpty(leSamedi) && "false".equals(leSamedi)){ %> checked="checked" <%} %>>
			    <label for="nonSamedi" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.false") %></label>
			  
			    <p><trsb:glp key="OFFRE-ATYP-DIM-HTML"></trsb:glp></p>
			    <input type="radio" name="leDimanche" id="ouiDimanche" class="radio" value="true"
			      <%if(Util.notEmpty(leDimanche) && "true".equals(leDimanche)){ %> checked="checked" <%} %>>
			    <label for="ouiDimanche" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.true") %></label>
			    <input type="radio" name="leDimanche" id="nonDimanche" class="radio" value="false"
			      <%if(Util.notEmpty(leDimanche) && "false".equals(leDimanche)){ %> checked="checked" <%} %>>
			    <label for="nonDimanche" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.false") %></label>
			  
			    <p><trsb:glp key="OFFRE-ATYP-NUIT-HTML"></trsb:glp></p>
			    <input type="radio" name="laNuit" id="ouiNuit" class="radio" value="true"
			      <%if(Util.notEmpty(laNuit) && "true".equals(laNuit)){ %> checked="checked" <%} %>>
			    <label for="ouiNuit" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.true") %></label>
			    <input type="radio" name="laNuit" id="nonNuit" class="radio" value="false"
			      <%if(Util.notEmpty(laNuit) && "false".equals(laNuit)){ %> checked="checked" <%} %>>
			    <label for="nonNuit" style="margin-top: 4px; margin-left: 0px; margin-right: 8px;"><%= glp("ui.com.lbl.false") %></label>
			  </span>
			  <jalios:javascript>
			    <% if((Util.notEmpty(horairesAtypiques) && "false".equals(horairesAtypiques)) || Util.isEmpty(horairesAtypiques)) {%>
			      document.getElementById('precisezAtypiques').hide()
			    <%} else { %>
			      document.getElementById('precisezAtypiques').show()
			    <% } %>
			  </jalios:javascript>			
			</div>

	  </div> 
    
    <%-- URGENCE ou REMPLACEMENT--%>
    <div class="blocQuestion reponseBooleenne">
		  <h3><trsb:glp key="OFFRE-URG-HTML"></trsb:glp></h3>
		  <input type="radio" name="accepteDepannage" id="ouiUrgence" class="radio" value="true"
		    <%if(Util.notEmpty(accepteDepannage) && "true".equals(accepteDepannage)){ %> checked="checked" <%} %>
		    onclick="javascript:jQuery.plugin.AssmatPlugin.toggle('precisionsDepannage', 'show');">
		  <label for="ouiUrgence" style="margin-top: 0px;"><%= glp("ui.com.lbl.true") %></label>
		  <input type="radio" name="accepteDepannage" id="nonUrgence" class="radio" value="false"
		    <%if(Util.notEmpty(accepteDepannage) && "false".equals(accepteDepannage)){ %> checked="checked" <%} %>
		    onclick="javascript:jQuery.plugin.AssmatPlugin.toggle('precisionsDepannage', 'hide');">
		  <label for="nonUrgence" style="margin-top: 0px;"><%= glp("ui.com.lbl.false") %></label>
		
		  <div class="precisions" id="precisionsDepannage">
		    <label for="precisionsDepannage"><trsb:glp key="OFFRE-URG-PREC-HTML"></trsb:glp></label>
		    <textarea rows="5" cols="80" name="precisionsDepannage"><% if(Util.notEmpty(precisionsDepannage)){ %><%=precisionsDepannage%><% } %></textarea>
		  </div>
		  <jalios:javascript>
		    <% if((Util.notEmpty(accepteDepannage) && "false".equals(accepteDepannage)) || Util.isEmpty(accepteDepannage)) {%>
		      jQuery.plugin.AssmatPlugin.toggle('precisionsDepannage', 'hide');
		    <%} else { %>
		      jQuery.plugin.AssmatPlugin.toggle('precisionsDepannage', 'show');
		    <% } %>
		  </jalios:javascript>
		</div>
    
    
    <%-- SITUATION DE HANDICAP --%>
    <div class="blocQuestion reponseBooleenne">
		  <h3><trsb:glp key="OFFRE-HANDI-HTML"></trsb:glp></h3>
		  <input type="radio" name="accueilEnfantHandicap" id="ouiHandicap" class="radio" value="true"
		    <%if(Util.notEmpty(accueilEnfantHandicap) && "true".equals(accueilEnfantHandicap)){ %> checked="checked" <%} %>
		    onclick="javascript:jQuery.plugin.AssmatPlugin.toggle('precisionsEnfantHandicap', 'show');">
		  <label for="ouiHandicap" style="margin-top: 0px;"><%= glp("ui.com.lbl.true") %></label>
		  <input type="radio" name="accueilEnfantHandicap" id="nonHandicap" class="radio" value="false"
		    <%if(Util.notEmpty(accueilEnfantHandicap) && "false".equals(accueilEnfantHandicap)){ %> checked="checked" <%} %>
		    onclick="javascript:jQuery.plugin.AssmatPlugin.toggle('precisionsEnfantHandicap', 'hide');">
		  <label for="nonHandicap" style="margin-top: 0px;"><%= glp("ui.com.lbl.false") %></label>
		
		  <div class="precisions" id="precisionsEnfantHandicap">
		    <label for="precisionsEnfantHandicap"><trsb:glp key="OFFRE-HANDI-PREC-HTML"></trsb:glp></label>
		    <textarea rows="5" cols="80" name="precisionsEnfantHandicap"><% if(Util.notEmpty(precisionsEnfantHandicap)){ %><%=precisionsEnfantHandicap%><% } %></textarea>
		  </div>
		  <jalios:javascript>
		    <% if((Util.notEmpty(accueilEnfantHandicap) && "false".equals(accueilEnfantHandicap)) || Util.isEmpty(accueilEnfantHandicap)) {%>
		      jQuery.plugin.AssmatPlugin.toggle('precisionsEnfantHandicap', 'hide');
		    <%} else { %>
		      jQuery.plugin.AssmatPlugin.toggle('precisionsEnfantHandicap', 'show');
		    <% } %>
		  </jalios:javascript>
		</div>
    
    
    <%-- MOBILITE REDUITE --%>
    <div class="blocQuestion reponseBooleenne">
		  <h3><trsb:glp key="OFFRE-ACCESS-HTML"></trsb:glp></h3>
		
		  <input type="radio" name="logementAccessible" id="ouiLogementAdapte" class="radio" value="true"
		    <%if(Util.notEmpty(logementAccessible) && "true".equals(logementAccessible)){ %> checked="checked" <%} %>
		    onclick="javascript:jQuery.plugin.AssmatPlugin.toggle('precisionsLogementAccessible', 'show');">
		  <label for="ouiLogementAdapte" style="margin-top: 0px;"><%= glp("ui.com.lbl.true") %></label>
		  <input type="radio" name="logementAccessible" id="nonLogementAdapte" class="radio" value="false"
		    <%if(Util.notEmpty(logementAccessible) && "false".equals(logementAccessible)){ %> checked="checked" <%} %>
		    onclick="javascript:jQuery.plugin.AssmatPlugin.toggle('precisionsLogementAccessible', 'hide');">
		  <label for="nonLogementAdapte" style="margin-top: 0px;"><%= glp("ui.com.lbl.false") %></label>
		
		  <div class="precisions" id="precisionsLogementAccessible">
		    <label for="precisionsLogementAccessible"><trsb:glp key="OFFRE-ACCESS-PREC-HTML"></trsb:glp></label>
		    <textarea rows="5" cols="80" name="precisionsLogementAccessible"><% if(Util.notEmpty(precisionsLogementAccessible)){ %><%=precisionsLogementAccessible%><% } %></textarea>
		  </div>
		  <jalios:javascript>
		    <% if((Util.notEmpty(logementAccessible) && "false".equals(logementAccessible)) || Util.isEmpty(logementAccessible)) {%>
		      jQuery.plugin.AssmatPlugin.toggle('precisionsLogementAccessible', 'hide');
		    <%} else { %>
		      jQuery.plugin.AssmatPlugin.toggle('precisionsLogementAccessible', 'show');
		    <% } %>
		  </jalios:javascript>
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
      <input type="hidden" name="opUpdate" value="true" />
    </p>
    
  </form>


</div>
</div>
</div>
