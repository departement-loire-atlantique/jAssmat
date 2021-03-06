<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.assmat.comparator.DisponibiliteComparator"%>
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
  return;
}
%>

<jsp:useBean id='formHandler' scope='page' class='fr.cg44.plugin.assmat.handler.ProfilActualiserDispoHandler'>
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

List<Disponibilite> dispos = ProfilManager.getInstance().getDisponibilitesList(loggedMember, null);
if(Util.notEmpty(dispos)) {
  Collections.sort(dispos, new DisponibiliteComparator());
}

String anneeActuelle = "2017";
Calendar cal = Calendar.getInstance();
if(Util.notEmpty(cal)){
  cal.setTime(new Date());
  anneeActuelle = String.valueOf(cal.get(Calendar.YEAR));
}



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
      
    
    <div class="blocQuestion disponibilite">
  
      <h3 class="title-bar-container dotted-portlet"><trsb:glp key="DISPO-INTRO-HTML"></trsb:glp></h3>
      <%
      int maxDispo = 10;
      if(dispos != null && dispos.size() < maxDispo){
        maxDispo = dispos.size();
      }
      if(dispos == null) {
        maxDispo = 0;
      }
      for(int cptDispo = 0; cptDispo < maxDispo; cptDispo++){
        Disponibilite d = dispos.get(cptDispo);
        String etatDispo = "";              
        if(Util.notEmpty(d.getEtatDispo())){
          etatDispo = d.getEtatDispo();
        }
        String precisionPlaceFuture1 = "";              
        if(Util.notEmpty(d.getPrecisionPlaceFuture())){
          precisionPlaceFuture1 = d.getPrecisionPlaceFuture();
        }
        String precisionsPlaceDisponible1 = "";               
        if(Util.notEmpty(d.getPrecisionsPlaceDisponible())){
          precisionsPlaceDisponible1 = d.getPrecisionsPlaceDisponible();
        }
        String precisionsDepannagePlaceDisponible1 = "";              
        if(Util.notEmpty(d.getPrecisionsDepannagePlaceDisponible())){
          precisionsDepannagePlaceDisponible1 = d.getPrecisionsDepannagePlaceDisponible();
        }
    
        cal = Calendar.getInstance();
        String moisDispo = String.valueOf(cal.get(Calendar.MONTH));
        String anneeDispo = String.valueOf(cal.get(Calendar.YEAR));
        if(Util.notEmpty(d.getDateDispoPlaceFuture())){
          cal.setTime(d.getDateDispoPlaceFuture());
          moisDispo = String.valueOf(cal.get(Calendar.MONTH));
          anneeDispo = String.valueOf(cal.get(Calendar.YEAR));
        }
        
        String dispoDepannagePlaceDisponible1 = String.valueOf(d.getDispoDepannagePlaceDisponible());
    
        String lundi_avant_ecole1 =           "";
        String mardi_avant_ecole1 =           "";
        String mercredi_avant_ecole1 =          "";
        String jeudi_avant_ecole1 =           "";
        String vendredi_avant_ecole1 =          "";
        String samedi_avant_ecole1 =          "";
        String dimanche_avant_ecole1 =          "";
        String lundi_matin1 =               "";
        String mardi_matin1 =               "";
        String mercredi_matin1 =            "";
        String jeudi_matin1 =               "";
        String vendredi_matin1 =            "";
        String samedi_matin1 =              "";
        String dimanche_matin1 =            "";
        String lundi_midi1 =              "";
        String mardi_midi1 =              "";
        String mercredi_midi1 =             "";
        String jeudi_midi1 =              "";
        String vendredi_midi1 =             "";
        String samedi_midi1 =               "";
        String dimanche_midi1 =             "";
        String lundi_pm1 =                "";
        String mardi_pm1 =                "";
        String mercredi_pm1 =               "";
        String jeudi_pm1 =                "";
        String vendredi_pm1 =               "";
        String samedi_pm1 =               "";
        String dimanche_pm1 =               "";
        String lundi_apres_ecole1 =           "";
        String mardi_apres_ecole1 =           "";
        String mercredi_apres_ecole1 =          "";
        String jeudi_apres_ecole1 =           "";
        String vendredi_apres_ecole1 =          "";
        String samedi_apres_ecole1 =          "";
        String dimanche_apres_ecole1 =          "";
        String lundi_nuit1 =              "";
        String mardi_nuit1 =              "";
        String mercredi_nuit1 =             "";
        String jeudi_nuit1 =              "";
        String vendredi_nuit1 =             "";
        String samedi_nuit1 =               "";
        String dimanche_nuit1 =             "";
    
        Semainier s = SemainierUtil.decoded(d.getSemainierPlaceDisponible());
        if(Util.notEmpty(s)){
          lundi_avant_ecole1 =          String.valueOf(s.isLundi_avant_ecole());
          mardi_avant_ecole1 =          String.valueOf(s.isMardi_avant_ecole());
          mercredi_avant_ecole1 =         String.valueOf(s.isMercredi_avant_ecole());
          jeudi_avant_ecole1 =          String.valueOf(s.isJeudi_avant_ecole());
          vendredi_avant_ecole1 =         String.valueOf(s.isVendredi_avant_ecole());
          samedi_avant_ecole1 =           String.valueOf(s.isSamedi_avant_ecole());
          dimanche_avant_ecole1 =         String.valueOf(s.isDimanche_avant_ecole());
          lundi_matin1 =              String.valueOf(s.isLundi_matin());
          mardi_matin1 =              String.valueOf(s.isMardi_matin());
          mercredi_matin1 =             String.valueOf(s.isMercredi_matin());
          jeudi_matin1 =              String.valueOf(s.isJeudi_matin());
          vendredi_matin1 =             String.valueOf(s.isVendredi_matin());
          samedi_matin1 =             String.valueOf(s.isSamedi_matin());
          dimanche_matin1 =             String.valueOf(s.isDimanche_matin());
          lundi_midi1 =               String.valueOf(s.isLundi_midi());
          mardi_midi1 =               String.valueOf(s.isMardi_midi());
          mercredi_midi1 =            String.valueOf(s.isMercredi_midi());
          jeudi_midi1 =               String.valueOf(s.isJeudi_midi());
          vendredi_midi1 =            String.valueOf(s.isVendredi_midi());
          samedi_midi1 =              String.valueOf(s.isSamedi_midi());
          dimanche_midi1 =            String.valueOf(s.isDimanche_midi());
          lundi_pm1 =               String.valueOf(s.isLundi_pm());
          mardi_pm1 =               String.valueOf(s.isMardi_pm());
          mercredi_pm1 =              String.valueOf(s.isMercredi_pm());
          jeudi_pm1 =               String.valueOf(s.isJeudi_pm());
          vendredi_pm1 =              String.valueOf(s.isVendredi_pm());
          samedi_pm1 =              String.valueOf(s.isSamedi_pm());
          dimanche_pm1 =              String.valueOf(s.isDimanche_pm());
          lundi_apres_ecole1 =          String.valueOf(s.isLundi_apres_ecole());
          mardi_apres_ecole1 =          String.valueOf(s.isMardi_apres_ecole());
          mercredi_apres_ecole1 =         String.valueOf(s.isMercredi_apres_ecole());
          jeudi_apres_ecole1 =          String.valueOf(s.isJeudi_apres_ecole());
          vendredi_apres_ecole1 =         String.valueOf(s.isVendredi_apres_ecole());
          samedi_apres_ecole1 =           String.valueOf(s.isSamedi_apres_ecole());
          dimanche_apres_ecole1 =         String.valueOf(s.isDimanche_apres_ecole());
          lundi_nuit1 =               String.valueOf(s.isLundi_nuit());
          mardi_nuit1 =               String.valueOf(s.isMardi_nuit());
          mercredi_nuit1 =            String.valueOf(s.isMercredi_nuit());
          jeudi_nuit1 =               String.valueOf(s.isJeudi_nuit());
          vendredi_nuit1 =            String.valueOf(s.isVendredi_nuit());
          samedi_nuit1 =              String.valueOf(s.isSamedi_nuit());
          dimanche_nuit1 =            String.valueOf(s.isDimanche_nuit());
        }%>
      <div class="categorie">
        <input type="hidden" name="idDispo<%= cptDispo + 1 %>" value="<%=dispos.get(cptDispo).getId()%>" />
        <h4 class="categorie title-bar-container dotted-portlet">
          <jalios:if predicate='<%= dispos.get(cptDispo).getAgrement().contains("dom-") %>'>
            ?? domicile : 
          </jalios:if>
          <jalios:if predicate='<%= dispos.get(cptDispo).getAgrement().contains("mam-") %>'>
            En MAM : 
          </jalios:if>
            1 place <%= AssmatUtil.getTitlePlace(dispos.get(cptDispo).getLibelle(), dispos.get(cptDispo).getLibelleComplementaire(), dispos.get(cptDispo).getTrancheDage()) %></p>      
        </h4>
        <jalios:select>     
          <jalios:if predicate='<%= dispos.get(cptDispo).getSaisieDisponible() %>'>
            <div class="choice-dispo">
              <input type="radio" name="etatDispo<%= cptDispo + 1 %>" id="categorie3Dispo<%= cptDispo + 1 %>" class="radio" value="1"
                <% if(Util.notEmpty(etatDispo) && etatDispo.equals("1")) { %> checked <% } %>
                onclick="javascript:jQuery.plugin.AssmatPlugin.toggle('Formulaires<%= cptDispo + 1 %>', 'show');
                  jQuery.plugin.AssmatPlugin.toggle('aPartir<%= cptDispo + 1 %>', 'hide');
                  jQuery.plugin.AssmatPlugin.toggle('dispoActuelle<%= cptDispo + 1 %>', 'show');
                  jQuery.plugin.AssmatPlugin.toggle('dispoActuellePrecisions<%= cptDispo + 1 %>', 'show');
                  jQuery.plugin.AssmatPlugin.toggle('depannageRemplacement<%= cptDispo + 1 %>', 'show');  
                  jQuery.plugin.AssmatPlugin.toggle('declaration<%= cptDispo + 1 %>', 'hide');">
              <label for="categorie3Dispo<%= cptDispo + 1 %>"><trsb:glp key="DISPO-OUI-HTML"></trsb:glp></label>
              <input type="radio" name="etatDispo<%= cptDispo + 1 %>" id="categorie3DispoFutures<%= cptDispo + 1 %>" class="radio" value="2"
                <% if(Util.notEmpty(etatDispo) && etatDispo.equals("2")) { %> checked <% } %>
                onclick="javascript:jQuery.plugin.AssmatPlugin.toggle('Formulaires<%= cptDispo + 1 %>', 'show');
                  jQuery.plugin.AssmatPlugin.toggle('dispoActuelle<%= cptDispo + 1 %>', 'show');
                  jQuery.plugin.AssmatPlugin.toggle('dispoActuellePrecisions<%= cptDispo + 1 %>', 'hide');
                  jQuery.plugin.AssmatPlugin.toggle('aPartir<%= cptDispo + 1 %>', 'show');
                  jQuery.plugin.AssmatPlugin.toggle('depannageRemplacement<%= cptDispo + 1 %>', 'hide');
                  jQuery.plugin.AssmatPlugin.toggle('declaration<%= cptDispo + 1 %>', 'show');">
              <label for="categorie3DispoFutures<%= cptDispo + 1 %>"><trsb:glp key="DISPO-OUI-PLUS-HTML"></trsb:glp></label>
              <input type="radio" name="etatDispo<%= cptDispo + 1 %>" id="categorie3NoDispo<%= cptDispo + 1 %>" class="radio" value="3"
                <% if(Util.notEmpty(etatDispo) && etatDispo.equals("3")) { %> checked <% } %>
                onclick="javascript:jQuery.plugin.AssmatPlugin.toggle('Formulaires<%= cptDispo + 1 %>', 'hide');
                  jQuery.plugin.AssmatPlugin.toggle('declaration<%= cptDispo + 1 %>', 'show');"> 
              <label for="categorie3NoDispo<%= cptDispo + 1 %>"><trsb:glp key="DISPO-NON-HTML"></trsb:glp></label>              
            </div>
           </jalios:if>
      
           <jalios:default>
             <p><trsb:glp key="DISPONIBILITES-PAS-DE-SAISIE-HTML"></trsb:glp></p>
           </jalios:default>            
          </jalios:select>
           
          <%-- Par defaut ou si non renseign?? coche disponibilit?? inconnue --%>
          <input class="hidden" type="radio" name="etatDispo<%= cptDispo + 1 %>" id="categorie3DispoInconnu<%= cptDispo + 1 %>" class="radio" value="0" 
            <% if(Util.isEmpty(etatDispo) || etatDispo.equals("0")) { %> checked <% } %> />
        
          <div id="Formulaires<%= cptDispo + 1 %>" style="clear: both;">
        
         
          <div class="aPartir" id="aPartir<%= cptDispo + 1 %>">
            <p class="textSelection"><trsb:glp key="DISPO-OUI-PLUS-DATE"></trsb:glp></p>
            <div class="form-select mois">
              <span class="input-box" style="background-color: #aec900"><span class="spr-select_arrow"></span></span>
              <select name="moisDispo<%= cptDispo + 1 %>" id="mois">
                <option value="00" <% if((Util.notEmpty(moisDispo)  && moisDispo.equals("0")) ) { %> selected <% } %>>Janvier</option>
                <option value="01" <% if((Util.notEmpty(moisDispo)  && moisDispo.equals("1")) ) { %> selected <% } %>>F??vrier</option>
                <option value="02" <% if((Util.notEmpty(moisDispo)  && moisDispo.equals("2")) ) { %> selected <% } %>>Mars</option>
                <option value="03" <% if((Util.notEmpty(moisDispo)  && moisDispo.equals("3")) ) { %> selected <% } %>>Avril</option>
                <option value="04" <% if((Util.notEmpty(moisDispo)  && moisDispo.equals("4")) ) { %> selected <% } %>>Mai</option>
                <option value="05" <% if((Util.notEmpty(moisDispo)  && moisDispo.equals("5")) ) { %> selected <% } %>>Juin</option>
                <option value="06" <% if((Util.notEmpty(moisDispo)  && moisDispo.equals("6")) ) { %> selected <% } %>>Juillet</option>
                <option value="07" <% if((Util.notEmpty(moisDispo)  && moisDispo.equals("7")) ) { %> selected <% } %>>Ao??t</option>
                <option value="08" <% if((Util.notEmpty(moisDispo)  && moisDispo.equals("8")) ) { %> selected <% } %>>Septembre</option>
                <option value="09" <% if((Util.notEmpty(moisDispo)  && moisDispo.equals("9")) ) { %> selected <% } %>>Octobre</option>
                <option value="10" <% if((Util.notEmpty(moisDispo)  && moisDispo.equals("10")) ) { %> selected <% } %>>Novembre</option>
                <option value="11" <% if((Util.notEmpty(moisDispo)  && moisDispo.equals("11")) ) { %> selected <% } %>>D??cembre</option>
              </select>
            </div>
            
            <div class="form-select annee">
              <span class="input-box" style="background-color: #aec900"><span class="spr-select_arrow"></span></span>
              <select name="anneeDispo<%= cptDispo + 1 %>" id="annee">
                <% for(int cptAnnee = Integer.parseInt(anneeActuelle); cptAnnee < Integer.parseInt(anneeActuelle) + 11; cptAnnee++) { %>
                  <option value="<%=cptAnnee%>" <% if(Util.notEmpty(anneeDispo) && anneeDispo.equals(Integer.toString(cptAnnee))) { %> selected <% } %>><%= cptAnnee %></option>
                <% } %>
              </select>
            </div>
            
            <div class="precisions" style="clear: both;">
              <label for="precisionPlaceFuture<%= cptDispo + 1 %>" class="renseignements labelPrecisions"><trsb:glp key="DISPO-OUI-PLUS-PREC-HTML"></trsb:glp></label>
              <textarea type="text" name="precisionPlaceFuture<%= cptDispo + 1 %>" class="precisions" ><%= precisionPlaceFuture1 %></textarea>
            </div>
            
          </div>
    
    
    
          <div id="dispoActuellePrecisions<%= cptDispo + 1 %>" class="precisions" style="clear: both;">
             <label for="precisionsPlaceDisponible<%= cptDispo + 1 %>" class="renseignements labelPrecisions"><trsb:glp key="DISPO-OUI-PREC-HTML"></trsb:glp></label>
             <textarea type="text" name="precisionsPlaceDisponible<%= cptDispo + 1 %>" class="precisions" ><%= precisionsPlaceDisponible1 %></textarea>
          </div>
    
          <div id="dispoActuelle<%= cptDispo + 1 %>">
           
    
            <p class="renseignements important"><trsb:glp key="DISPO-OUI-SEM-HTML"></trsb:glp></p>
    
            <table class="semainier" id="tableauSemainier<%= cptDispo + 1 %>">
              <tr>
                <th scope="col"></th>
                <th scope="col">L</th>
                <th scope="col">M</th>
                <th scope="col">M</th>
                <th scope="col">J</th>
                <th scope="col">V</th>
                <th scope="col">S</th>
                <th scope="col">D</th>
              </tr>
              <tr>
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-AV"></trsb:glp></th>
                <td><input type="checkbox" id="lundi_avant_ecole<%= cptDispo + 1 %>" name="lundi_avant_ecole<%= cptDispo + 1 %>" value="true" <% if(lundi_avant_ecole1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="mardi_avant_ecole<%= cptDispo + 1 %>" name="mardi_avant_ecole<%= cptDispo + 1 %>" value="true" <% if(mardi_avant_ecole1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="mercredi_avant_ecole<%= cptDispo + 1 %>" name="mercredi_avant_ecole<%= cptDispo + 1 %>" value="true" <% if(mercredi_avant_ecole1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="jeudi_avant_ecole<%= cptDispo + 1 %>" name="jeudi_avant_ecole<%= cptDispo + 1 %>" value="true" <% if(jeudi_avant_ecole1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="vendredi_avant_ecole<%= cptDispo + 1 %>" name="vendredi_avant_ecole<%= cptDispo + 1 %>" value="true" <% if(vendredi_avant_ecole1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="samedi_avant_ecole<%= cptDispo + 1 %>" name="samedi_avant_ecole<%= cptDispo + 1 %>" value="true" <% if(samedi_avant_ecole1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="dimanche_avant_ecole<%= cptDispo + 1 %>" name="dimanche_avant_ecole<%= cptDispo + 1 %>" value="true" <% if(dimanche_avant_ecole1.equals("true")) { %> checked <% } %> /></td>
              </tr>
              <tr>
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-MAT"></trsb:glp></th>
                <td><input type="checkbox" id="lundi_matin<%= cptDispo + 1 %>" name="lundi_matin<%= cptDispo + 1 %>" value="true" <% if(lundi_matin1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="mardi_matin<%= cptDispo + 1 %>" name="mardi_matin<%= cptDispo + 1 %>" value="true" <% if(mardi_matin1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="mercredi_matin<%= cptDispo + 1 %>" name="mercredi_matin<%= cptDispo + 1 %>" value="true" <% if(mercredi_matin1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="jeudi_matin<%= cptDispo + 1 %>" name="jeudi_matin<%= cptDispo + 1 %>" value="true" <% if(jeudi_matin1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="vendredi_matin<%= cptDispo + 1 %>" name="vendredi_matin<%= cptDispo + 1 %>" value="true" <% if(vendredi_matin1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="samedi_matin<%= cptDispo + 1 %>" name="samedi_matin<%= cptDispo + 1 %>" value="true" <% if(samedi_matin1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="dimanche_matin<%= cptDispo + 1 %>" name="dimanche_matin<%= cptDispo + 1 %>" value="true" <% if(dimanche_matin1.equals("true")) { %> checked <% } %> /></td>
              </tr>
              <tr>
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-MIDI"></trsb:glp></th>
                <td><input type="checkbox" id="lundi_midi<%= cptDispo + 1 %>" name="lundi_midi<%= cptDispo + 1 %>" value="true" <% if(lundi_midi1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="mardi_midi<%= cptDispo + 1 %>" name="mardi_midi<%= cptDispo + 1 %>" value="true" <% if(mardi_midi1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="mercredi_midi<%= cptDispo + 1 %>" name="mercredi_midi<%= cptDispo + 1 %>" value="true" <% if(mercredi_midi1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="jeudi_midi<%= cptDispo + 1 %>" name="jeudi_midi<%= cptDispo + 1 %>" value="true" <% if(jeudi_midi1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="vendredi_midi<%= cptDispo + 1 %>" name="vendredi_midi<%= cptDispo + 1 %>" value="true" <% if(vendredi_midi1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="samedi_midi<%= cptDispo + 1 %>" name="samedi_midi<%= cptDispo + 1 %>" value="true" <% if(samedi_midi1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="dimanche_midi<%= cptDispo + 1 %>" name="dimanche_midi<%= cptDispo + 1 %>" value="true" <% if(dimanche_midi1.equals("true")) { %> checked <% } %> /></td>
              </tr>
              <tr>
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-APM"></trsb:glp></th>
                <td><input type="checkbox" id="lundi_pm<%= cptDispo + 1 %>" name="lundi_pm<%= cptDispo + 1 %>" value="true" <% if(lundi_pm1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="mardi_pm<%= cptDispo + 1 %>" name="mardi_pm<%= cptDispo + 1 %>" value="true" <% if(mardi_pm1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="mercredi_pm<%= cptDispo + 1 %>" name="mercredi_pm<%= cptDispo + 1 %>" value="true" <% if(mercredi_pm1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="jeudi_pm<%= cptDispo + 1 %>" name="jeudi_pm<%= cptDispo + 1 %>" value="true" <% if(jeudi_pm1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="vendredi_pm<%= cptDispo + 1 %>" name="vendredi_pm<%= cptDispo + 1 %>" value="true" <% if(vendredi_pm1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="samedi_pm<%= cptDispo + 1 %>" name="samedi_pm<%= cptDispo + 1 %>" value="true" <% if(samedi_pm1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="dimanche_pm<%= cptDispo + 1 %>" name="dimanche_pm<%= cptDispo + 1 %>" value="true" <% if(dimanche_pm1.equals("true")) { %> checked <% } %> /></td>
              </tr>
              <tr>
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-APEC"></trsb:glp></th>
                <td><input type="checkbox" id="lundi_apres_ecole<%= cptDispo + 1 %>" name="lundi_apres_ecole<%= cptDispo + 1 %>" value="true" <% if(lundi_apres_ecole1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="mardi_apres_ecole<%= cptDispo + 1 %>" name="mardi_apres_ecole<%= cptDispo + 1 %>" value="true" <% if(mardi_apres_ecole1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="mercredi_apres_ecole<%= cptDispo + 1 %>" name="mercredi_apres_ecole<%= cptDispo + 1 %>" value="true" <% if(mercredi_apres_ecole1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="jeudi_apres_ecole<%= cptDispo + 1 %>" name="jeudi_apres_ecole<%= cptDispo + 1 %>" value="true" <% if(jeudi_apres_ecole1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="vendredi_apres_ecole<%= cptDispo + 1 %>" name="vendredi_apres_ecole<%= cptDispo + 1 %>" value="true" <% if(vendredi_apres_ecole1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="samedi_apres_ecole<%= cptDispo + 1 %>" name="samedi_apres_ecole<%= cptDispo + 1 %>" value="true" <% if(samedi_apres_ecole1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="dimanche_apres_ecole<%= cptDispo + 1 %>" name="dimanche_apres_ecole<%= cptDispo + 1 %>" value="true" <% if(dimanche_apres_ecole1.equals("true")) { %> checked <% } %> /></td>
              </tr>
              <tr>
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-NUIT"></trsb:glp></th>
                <td><input type="checkbox" id="lundi_nuit<%= cptDispo + 1 %>" name="lundi_nuit<%= cptDispo + 1 %>" value="true" <% if(lundi_nuit1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="mardi_nuit<%= cptDispo + 1 %>" name="mardi_nuit<%= cptDispo + 1 %>" value="true" <% if(mardi_nuit1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="mercredi_nuit<%= cptDispo + 1 %>" name="mercredi_nuit<%= cptDispo + 1 %>" value="true" <% if(mercredi_nuit1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="jeudi_nuit<%= cptDispo + 1 %>" name="jeudi_nuit<%= cptDispo + 1 %>" value="true" <% if(jeudi_nuit1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="vendredi_nuit<%= cptDispo + 1 %>" name="vendredi_nuit<%= cptDispo + 1 %>" value="true" <% if(vendredi_nuit1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="samedi_nuit<%= cptDispo + 1 %>" name="samedi_nuit<%= cptDispo + 1 %>" value="true" <% if(samedi_nuit1.equals("true")) { %> checked <% } %> /></td>
                <td><input type="checkbox" id="dimanche_nuit<%= cptDispo + 1 %>" name="dimanche_nuit<%= cptDispo + 1 %>" value="true" <% if(dimanche_nuit1.equals("true")) { %> checked <% } %> /></td>
              </tr>
            </table>
    
                
            <div id="depannageRemplacement<%= cptDispo + 1 %>" class="depannageRemplacement">
              <div id="depannageRemplacementInput<%= cptDispo + 1 %>" class="depannageRemplacementInput<%= cptDispo + 1 %>">
              <p class="important"><trsb:glp key="DISPO-OUI-REMP-HTML"></trsb:glp></p>
    
              <input type="radio"  onclick="javascript:jQuery.plugin.AssmatPlugin.toggle('precision<%= cptDispo + 1 %>', 'show');" name="dispoDepannagePlaceDisponible<%= cptDispo + 1 %>" class="radio" value="true" <% if(dispoDepannagePlaceDisponible1.equals("true")) { %> checked <% } %>>
              <label for="dispoDepannagePlaceDisponible1"><%= glp("ui.com.lbl.true") %></label>
              <input onclick="javascript:jQuery.plugin.AssmatPlugin.toggle('precision<%= cptDispo + 1 %>', 'hide');"  type="radio" name="dispoDepannagePlaceDisponible<%= cptDispo + 1 %>" class="radio" value="false" <% if(dispoDepannagePlaceDisponible1.equals("false")) { %> checked <% } %>>
              <label for="dispoDepannagePlaceDisponible1"><%= glp("ui.com.lbl.false") %></label>
              </div>
              <div id ="precision<%= cptDispo + 1 %>" class="precisions" style="clear: both;">
                <label for="precisions" class="renseignements labelPrecisions"><trsb:glp key="DISPO-OUI-REMP-PREC-HTML"></trsb:glp></label>
                <textarea type="text" name="precisionsDepannagePlaceDisponible<%= cptDispo + 1 %>" class="precisions"><%= precisionsDepannagePlaceDisponible1 %></textarea>
              </div>
            </div>
        
          </div>
        </div>
    <div style="clar:both"></div>
        <p class="texte-declaration" id="declaration<%= cptDispo + 1 %>" style="clear: both;"><trsb:glp key="DISPO-OUI-PLUS-LEG-HTML"></trsb:glp></p>
    
      </div>
      <jalios:javascript>
      
  <%if(dispoDepannagePlaceDisponible1.equals("false")) { %>
  jQuery( "#precision<%= cptDispo + 1 %>" ).hide();
   <% } %>
   
  
        <% if(Util.notEmpty(etatDispo) && etatDispo.equals(AssmatUtil.SelectionEtatDispo.IMMEDIATE.getValue())) { %>
          jQuery.plugin.AssmatPlugin.toggle('Formulaires<%= cptDispo + 1 %>', 'show');
          jQuery.plugin.AssmatPlugin.toggle('aPartir<%= cptDispo + 1 %>', 'hide');
          jQuery.plugin.AssmatPlugin.toggle('dispoActuellePrecisions<%= cptDispo + 1 %>', 'show');
          jQuery.plugin.AssmatPlugin.toggle('dispoActuelle<%= cptDispo + 1 %>', 'show');
          jQuery.plugin.AssmatPlugin.toggle('declaration<%= cptDispo + 1 %>', 'hide');      
        <% } else if(Util.notEmpty(etatDispo) && etatDispo.equals(AssmatUtil.SelectionEtatDispo.FUTURE.getValue())) { %>        
          jQuery.plugin.AssmatPlugin.toggle('Formulaires<%= cptDispo + 1 %>', 'show');
          jQuery.plugin.AssmatPlugin.toggle('declaration<%= cptDispo + 1 %>', 'show');
          jQuery.plugin.AssmatPlugin.toggle('dispoActuellePrecisions<%= cptDispo + 1 %>', 'hide');
          jQuery.plugin.AssmatPlugin.toggle('declaration<%= cptDispo + 1 %>', 'show');  
          jQuery.plugin.AssmatPlugin.toggle('depannageRemplacement<%= cptDispo + 1 %>', 'hide');    
        <% } else if(Util.notEmpty(etatDispo) && etatDispo.equals(AssmatUtil.SelectionEtatDispo.NON_DISPO.getValue())) { %>
          jQuery.plugin.AssmatPlugin.toggle('Formulaires<%= cptDispo + 1 %>', 'hide');
          jQuery.plugin.AssmatPlugin.toggle('declaration<%= cptDispo + 1 %>', 'show');
        <% } %>
        <% if(Util.notEmpty(etatDispo) && etatDispo.equals(AssmatUtil.SelectionEtatDispo.INCONNU.getValue())) { %>
          jQuery.plugin.AssmatPlugin.toggle('Formulaires<%= cptDispo + 1 %>', 'hide');
          jQuery.plugin.AssmatPlugin.toggle('declaration<%= cptDispo + 1 %>', 'hide');
        <% } %>
      </jalios:javascript>
      <% } %>         
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
