<%@page import="fr.cg44.plugin.socle.SocleUtils"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.assmat.comparator.DisponibiliteComparator"%>
<%@page import="fr.cg44.plugin.assmat.SemainierUtil"%>
<%@page import="fr.cg44.plugin.assmat.Semainier"%>
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

//UUID unique pour les champs
String uuid = UUID.randomUUID().toString();
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

List<Disponibilite> dispos = ProfilManager.getInstance().getDisponibilitesList(loggedMember, null);
if(Util.notEmpty(dispos)) {
  Collections.sort(dispos, new DisponibiliteComparator());
}

String anneeActuelle = "2017";
String moisActuel = "2";
Calendar cal = Calendar.getInstance();
if(Util.notEmpty(cal)){
  cal.setTime(new Date());
  anneeActuelle = String.valueOf(cal.get(Calendar.YEAR));
  moisActuel = String.valueOf(cal.get(Calendar.MONTH));
}



int numeroDossierAssmat = profil.getNum_agrement();

boolean noYearIsCurrentYear = false;
boolean noMonthIsCurrentMonth = false;
%>

<%@ include file='/plugins/AssmatPlugin/jsp/espacePerso/header.jspf' %>
<%@ include file='/plugins/SoclePlugin/jsp/doMessageBoxCustom.jspf' %>

  <form method="post" action="<%= ServletUtil.getResourcePath(request) %>" name="formDispos" id="formDispos">

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
    
        String[] avant_ecole = new String[]{"","","","","","",""};
        String[] matin = new String[]{"","","","","","",""};
        String[] midi = new String[]{"","","","","","",""};
        String[] pm = new String[]{"","","","","","",""};
        String[] apres = new String[]{"","","","","","",""};
        String[] nuit = new String[]{"","","","","","",""};
        
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
          
          avant_ecole = new String[]{lundi_avant_ecole1,mardi_avant_ecole1,mercredi_avant_ecole1,jeudi_avant_ecole1, vendredi_avant_ecole1, samedi_avant_ecole1, dimanche_avant_ecole1};
              
          lundi_matin1 =              String.valueOf(s.isLundi_matin());
          mardi_matin1 =              String.valueOf(s.isMardi_matin());
          mercredi_matin1 =             String.valueOf(s.isMercredi_matin());
          jeudi_matin1 =              String.valueOf(s.isJeudi_matin());
          vendredi_matin1 =             String.valueOf(s.isVendredi_matin());
          samedi_matin1 =             String.valueOf(s.isSamedi_matin());
          dimanche_matin1 =             String.valueOf(s.isDimanche_matin());
          
          matin = new String[]{lundi_matin1,mardi_matin1,mercredi_matin1,jeudi_matin1, vendredi_matin1, samedi_matin1, dimanche_matin1};
          
          lundi_midi1 =               String.valueOf(s.isLundi_midi());
          mardi_midi1 =               String.valueOf(s.isMardi_midi());
          mercredi_midi1 =            String.valueOf(s.isMercredi_midi());
          jeudi_midi1 =               String.valueOf(s.isJeudi_midi());
          vendredi_midi1 =            String.valueOf(s.isVendredi_midi());
          samedi_midi1 =              String.valueOf(s.isSamedi_midi());
          dimanche_midi1 =            String.valueOf(s.isDimanche_midi());
          
          midi = new String[]{lundi_midi1,mardi_midi1,mercredi_midi1,jeudi_midi1, vendredi_midi1, samedi_midi1, dimanche_midi1};
          
          lundi_pm1 =               String.valueOf(s.isLundi_pm());
          mardi_pm1 =               String.valueOf(s.isMardi_pm());
          mercredi_pm1 =              String.valueOf(s.isMercredi_pm());
          jeudi_pm1 =               String.valueOf(s.isJeudi_pm());
          vendredi_pm1 =              String.valueOf(s.isVendredi_pm());
          samedi_pm1 =              String.valueOf(s.isSamedi_pm());
          dimanche_pm1 =              String.valueOf(s.isDimanche_pm());
          
          pm = new String[]{lundi_pm1,mardi_pm1,mercredi_pm1,jeudi_pm1, vendredi_pm1, samedi_pm1, dimanche_pm1};
          
          lundi_apres_ecole1 =          String.valueOf(s.isLundi_apres_ecole());
          mardi_apres_ecole1 =          String.valueOf(s.isMardi_apres_ecole());
          mercredi_apres_ecole1 =         String.valueOf(s.isMercredi_apres_ecole());
          jeudi_apres_ecole1 =          String.valueOf(s.isJeudi_apres_ecole());
          vendredi_apres_ecole1 =         String.valueOf(s.isVendredi_apres_ecole());
          samedi_apres_ecole1 =           String.valueOf(s.isSamedi_apres_ecole());
          dimanche_apres_ecole1 =         String.valueOf(s.isDimanche_apres_ecole());
          
          apres = new String[]{lundi_apres_ecole1,mardi_apres_ecole1,mercredi_apres_ecole1,jeudi_apres_ecole1, vendredi_apres_ecole1, samedi_apres_ecole1, dimanche_apres_ecole1};
          
          lundi_nuit1 =               String.valueOf(s.isLundi_nuit());
          mardi_nuit1 =               String.valueOf(s.isMardi_nuit());
          mercredi_nuit1 =            String.valueOf(s.isMercredi_nuit());
          jeudi_nuit1 =               String.valueOf(s.isJeudi_nuit());
          vendredi_nuit1 =            String.valueOf(s.isVendredi_nuit());
          samedi_nuit1 =              String.valueOf(s.isSamedi_nuit());
          dimanche_nuit1 =            String.valueOf(s.isDimanche_nuit());
          
          nuit = new String[]{lundi_nuit1,mardi_nuit1,mercredi_nuit1,jeudi_nuit1, vendredi_nuit1, samedi_nuit1, dimanche_nuit1};
          
        }%>
      <div class="categorie mtl">
        <input type="hidden" name="idDispo<%= cptDispo + 1 %>" value="<%=dispos.get(cptDispo).getId()%>" />
        <h4 class="categorie title-bar-container dotted-portlet">        
          <jalios:if predicate='<%= dispos.get(cptDispo).getAgrement().contains("dom-") %>'>
            À domicile : 
          </jalios:if>
          <jalios:if predicate='<%= dispos.get(cptDispo).getAgrement().contains("mam-") %>'>
            En MAM : 
          </jalios:if>
            1 place <%= AssmatUtil.getTitlePlace(dispos.get(cptDispo).getLibelle(), dispos.get(cptDispo).getLibelleComplementaire(), dispos.get(cptDispo).getTrancheDage()) %></p>      
        </h4>
         <hr/>
        <jalios:select>     
          <jalios:if predicate='<%= dispos.get(cptDispo).getSaisieDisponible() %>'>
            <% uuid = UUID.randomUUID().toString(); %>
            <div id="form-element-<%= uuid %>" data-name="etatDispo<%= cptDispo + 1 %>" class="ds44-form__radio_container ds44-form__container">
<%--                <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p> --%>
               <div class="ds44-form__container ds44-checkBox-radio_list inbl">
                  <input type="radio" <% if(Util.notEmpty(etatDispo) && etatDispo.equals("1")) { %> checked <% } %> name="etatDispo<%= cptDispo + 1 %>" 
                      value="1" id="dispo-toggleable-oui-<%= cptDispo + 1 %>" class="ds44-radio"/>
                  <label id="label-radio-form-element-<%= uuid %>-one" for="dispo-toggleable-oui-<%= cptDispo + 1 %>" class="ds44-radioLabel"><trsb:glp key="DISPO-OUI-HTML" ></trsb:glp></label>
               </div>
               <div class="ds44-form__container ds44-checkBox-radio_list inbl">
                  <input type="radio" <% if(Util.notEmpty(etatDispo) && etatDispo.equals("2")) { %> checked <% } %> name="etatDispo<%= cptDispo + 1 %>" 
                      value="2" id="dispo-toggleable-oui-plus-<%= cptDispo + 1 %>" class="ds44-radio" 
                      required aria-describedby="mandatory-message-form-element-<%= uuid %>"/>
                  <label id="label-radio-form-element-<%= uuid %>-two" for="dispo-toggleable-oui-plus-<%= cptDispo + 1 %>" class="ds44-radioLabel"><trsb:glp key="DISPO-OUI-PLUS-HTML"></trsb:glp></label>
               </div>
               <div class="ds44-form__container ds44-checkBox-radio_list inbl">
                  <input type="radio" <% if(Util.notEmpty(etatDispo) && etatDispo.equals("3")) { %> checked <% } %> name="etatDispo<%= cptDispo + 1 %>" 
                      value="3" id="dispo-toggleable-non-<%= cptDispo + 1 %>" class="ds44-radio" 
                      required aria-describedby="mandatory-message-form-element-<%= uuid %>"/>
                  <label id="label-radio-form-element-<%= uuid %>-three" for="dispo-toggleable-non-<%= cptDispo + 1 %>" class="ds44-radioLabel"><trsb:glp key="DISPO-NON-HTML"></trsb:glp></label>
               </div>
            </div>
           </jalios:if>
      
           <jalios:default>
             <p><trsb:glp key="DISPONIBILITES-PAS-DE-SAISIE-HTML"></trsb:glp></p>
           </jalios:default>            
          </jalios:select>
          
          <%
          boolean hideEtaDispo1 = Util.isEmpty(etatDispo) || !(etatDispo.equals("1"));
          boolean hideEtaDispo2 = Util.isEmpty(etatDispo) || !(etatDispo.equals("2"));
          boolean hideEtaDispo3 = Util.isEmpty(etatDispo) || !(etatDispo.equals("3"));
          %>
           
          <%-- Par defaut ou si non renseigné coche disponibilité inconnue --%>
          <input class="hidden" type="radio" name="etatDispo<%= cptDispo + 1 %>" id="categorie3DispoInconnu<%= cptDispo + 1 %>" value="0" 
            <% if(Util.isEmpty(etatDispo) || etatDispo.equals("0")) { %> checked <% } %> data-technical-field/>
        
          <div id="Formulaires<%= cptDispo + 1 %>" data-fields-no-reset data-enabled-by-dispo-toggleable-oui-<%= cptDispo + 1 %> data-enabled-by-dispo-toggleable-oui-plus-<%= cptDispo + 1 %> class='<%= hideEtaDispo1 && hideEtaDispo2 ? " hidden" : "" %>' style="clear: both;"> 
          
              <% uuid = UUID.randomUUID().toString(); %>
              <p aria-level="2" class="h4-like hidden" data-fields-no-reset data-enabled-by-dispo-toggleable-oui-plus-<%= cptDispo + 1 %>><trsb:glp key="DISPO-OUI-PLUS-DATE"></trsb:glp></p>
              <div  class='ds44-form__container<%= hideEtaDispo2 ? " hidden" : "" %>' data-fields-no-reset data-enabled-by-dispo-toggleable-oui-plus-<%= cptDispo + 1 %>>
                   <div class="ds44-select__shape ds44-inpStd">
                      <p class="ds44-selectLabel" aria-hidden="true"><%= glp("ui.com.lbl.month") %></p>
                      <div id="form-element-<%= uuid %>" data-name="moisDispo<%= cptDispo + 1 %>" class="ds44-js-select-standard ds44-selectDisplay"></div>
                      <button type="button" id="button-form-element-<%= uuid %>" class="ds44-btnIco ds44-posAbs ds44-posRi ds44-btnOpen" aria-expanded="false" title='<%= glp("ui.com.lbl.month") %>'><i class="icon icon-down icon--sizeXL" aria-hidden="true"></i><span id="button-message-form-element-<%= uuid %>" class="visually-hidden"><trsb:glp key="DISPO-OUI-PLUS-DATE" attribute="true"></trsb:glp></span></button>
                      <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeXL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", glp("ui.com.lbl.month")) %></span></button>
                   </div>
                   <div class="ds44-select-container hidden">
                      <div class="ds44-listSelect">
                         <ul class="ds44-list" role="listbox" id="listbox-form-element-<%= uuid %>" aria-labelledby="button-message-form-element-<%= uuid %>">
                            <% for (int monthCounter = 0; monthCounter < 12; monthCounter++) { %>
                            <li class='ds44-select-list_elem<% if(Util.notEmpty(moisDispo) && moisDispo.equals(Integer.toString(monthCounter))) { %> selected_option <% } %>' 
                            data-value="<%= monthCounter %>" tabindex="0" role="option" 
                            <% if(Util.notEmpty(moisDispo) && moisDispo.equals(Integer.toString(monthCounter))) { %> aria-selected="true" <% } %>>
                               <%= glp("plugin.assmatplugin.month." + monthCounter) %>
                            </li>
                            <% } %>
                         </ul>
                      </div>
                   </div>
              </div>
              <% uuid = UUID.randomUUID().toString(); %>
              <div class='ds44-form__container hidden' data-fields-no-reset data-enabled-by-dispo-toggleable-oui-plus-<%= cptDispo + 1 %>>
                   <div class="ds44-select__shape ds44-inpStd">
                      <p class="ds44-selectLabel" aria-hidden="true"><%= glp("ui.com.lbl.year") %></p>
                      <div id="form-element-<%= uuid %>" data-name="anneeDispo<%= cptDispo + 1 %>" class="ds44-js-select-standard ds44-selectDisplay"></div>
                      <button type="button" id="button-form-element-<%= uuid %>" class="ds44-btnIco ds44-posAbs ds44-posRi ds44-btnOpen" aria-expanded="false" title='<%= glp("ui.com.lbl.year") %>'><i class="icon icon-down icon--sizeXL" aria-hidden="true"></i><span id="button-message-form-element-<%= uuid %>" class="visually-hidden"><trsb:glp key="DISPO-OUI-PLUS-DATE" attribute="true"></trsb:glp></span></button>
                      <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeXL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", glp("ui.com.lbl.year")) %></span></button>
                   </div>
                   <div class="ds44-select-container hidden">
                      <div class="ds44-listSelect">
                         <ul class="ds44-list" role="listbox" id="listbox-form-element-<%= uuid %>" aria-labelledby="button-message-form-element-<%= uuid %>">
                            <% for(int cptAnnee = Integer.parseInt(anneeActuelle); cptAnnee < Integer.parseInt(anneeActuelle) + 11; cptAnnee++) { %>
                            <li class='ds44-select-list_elem<% if(Util.notEmpty(anneeDispo) && anneeDispo.equals(Integer.toString(cptAnnee))) { %> selected_option <% } %>' 
                            data-value="<%= cptAnnee %>" tabindex="0" role="option" 
                            <% if(Util.notEmpty(anneeDispo) && anneeDispo.equals(Integer.toString(cptAnnee))) { %> aria-selected="true" <% } %>>
                               <%= cptAnnee %>
                            </li>
                            <% } %>
                         </ul>
                      </div>
                   </div>
              </div>
              <% uuid = UUID.randomUUID().toString(); %>
              <jalios:buffer name="precisionsLbl"><trsb:glp key="DISPO-OUI-PLUS-PREC-HTML" attribute="true"></trsb:glp></jalios:buffer>
              <div class='ds44-form__container<%= hideEtaDispo2 ? " hidden" : "" %>' data-fields-no-reset data-enabled-by-dispo-toggleable-oui-plus-<%= cptDispo + 1 %>>
                   <div class="ds44-posRel">
                      <label for="form-element-<%= uuid %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><trsb:glp key="DISPO-OUI-PLUS-PREC-HTML" attribute="true"></trsb:glp></span></span></label>
                      <textarea rows="5" cols="1" id="form-element-<%= uuid %>" name="precisionPlaceFuture<%= cptDispo + 1 %>" class="ds44-inpStd" title="<%= precisionsLbl %>"><%= precisionPlaceFuture1 %></textarea>
                   </div>
              </div>
            
          </div>
    
          <% uuid = UUID.randomUUID().toString(); %>
          <div class='<%= hideEtaDispo1 ? " hidden" : "" %>' data-fields-no-reset data-enabled-by-dispo-toggleable-oui-<%= cptDispo + 1 %>>
              <jalios:buffer name="precisionsLbl"><trsb:glp key="DISPO-OUI-PREC-HTML" attribute="true"></trsb:glp></jalios:buffer>
              <div class='ds44-form__container'>
                   <div class="ds44-posRel">
                      <label for="form-element-<%= uuid %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><trsb:glp key="DISPO-OUI-PREC-HTML" attribute="true"></trsb:glp></span></span></label>
                      <textarea rows="5" cols="1" id="form-element-<%= uuid %>" name="precisionsPlaceDisponible<%= cptDispo + 1 %>" class="ds44-inpStd" title="<%= precisionsLbl %>"><%= precisionsPlaceDisponible1 %></textarea>
                   </div>
              </div>
          </div>
    
            <div id="dispoActuelle<%= cptDispo + 1 %>" data-fields-no-reset data-enabled-by-dispo-toggleable-oui-<%= cptDispo + 1 %> data-enabled-by-dispo-toggleable-oui-plus-<%= cptDispo + 1 %> class='<%= hideEtaDispo1 && hideEtaDispo2 ? " hidden" : "" %>'>
           
     
            <p role="heading" aria-level="2"><trsb:glp key="DISPO-OUI-SEM-HTML"></trsb:glp></p>
            
            
            <%
             String[] jours = new String[] {"lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche"};
            %>
            
            <table class="tabDispos tabDispos_checkbox" id="tableauSemainier<%= cptDispo + 1 %>">
            
            <caption role="heading" aria-level="2" class="caption-side bottom mtm">
              <span class="visually-hidden">Semainier hebdomadaire de disponibilité des créneaux : Merci si possible de renseigner le semainier suivant pour préciser vos créneaux :</span>
            </caption>
            <tbody>
              <tr>
                  <td> </td>
                  <th scope="col" aria-label="Lundi">L</th>
                  <th scope="col" aria-label="Mardi">M</th>
                  <th scope="col" aria-label="Mercredi">M</th>
                  <th scope="col" aria-label="Jeudi">J</th>
                  <th scope="col" aria-label="Vendredi">V</th>
                  <th scope="col" aria-label="Samedi">S</th>
                  <th scope="col" aria-label="Dimanche">D</th>
              </tr>
              <tr>          
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-AV"></trsb:glp></th>                             
                <jalios:foreach array="<%= jours %>" name="itJour" type="String" >                
                    <td>
                        <div class="ds44-form__container ds44-checkBox-radio_list ">
                            <input <%= "true".equalsIgnoreCase(avant_ecole[itCounter-1]) ? "checked" : "" %>  type="checkbox" id='<%= itJour %>_avant_ecole<%= cptDispo + 1 %>' name='<%= itJour %>_avant_ecole<%= cptDispo + 1 %>' value="true" class="ds44-checkbox"     /><label for='<%= itJour %>_avant_ecole<%= cptDispo + 1 %>' class="ds44-boxLabel"><%= itJour %> - Avant l'école</label>
                        </div>
                    </td>               
                </jalios:foreach>                
              </tr>
              <tr>             
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-MAT"></trsb:glp></th>                      
                <jalios:foreach array="<%= jours %>" name="itJour" type="String">                
                    <td>
                        <div class="ds44-form__container ds44-checkBox-radio_list ">
                            <input <%= "true".equalsIgnoreCase(matin[itCounter-1]) ? "checked" : "" %> type="checkbox" id='<%= itJour %>_matin<%= cptDispo + 1 %>' name='<%= itJour %>_matin<%= cptDispo + 1 %>' value="true" class="ds44-checkbox"     /><label for='<%= itJour %>_matin<%= cptDispo + 1 %>' class="ds44-boxLabel"><%= itJour %>  - matin</label>
                        </div>
                    </td>               
                </jalios:foreach>                
              </tr>             
              <tr>
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-MIDI"></trsb:glp></th>              
                <jalios:foreach array="<%= jours %>" name="itJour" type="String">                
                    <td>
                        <div class="ds44-form__container ds44-checkBox-radio_list ">
                            <input <%= "true".equalsIgnoreCase(midi[itCounter-1]) ? "checked" : "" %> type="checkbox" id='<%= itJour %>_midi<%= cptDispo + 1 %>' name='<%= itJour %>_midi<%= cptDispo + 1 %>' value="true" class="ds44-checkbox"     /><label for='<%= itJour %>_midi<%= cptDispo + 1 %>' class="ds44-boxLabel"><%= itJour %>  - midi</label>
                        </div>
                    </td>               
                </jalios:foreach>                     
              </tr>
              
              <tr>
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-APM"></trsb:glp></th>              
                <jalios:foreach array="<%= jours %>" name="itJour" type="String">                
                    <td>
                        <div class="ds44-form__container ds44-checkBox-radio_list ">
                            <input <%= "true".equalsIgnoreCase(pm[itCounter-1]) ? "checked" : "" %> type="checkbox" id='<%= itJour %>_pm<%= cptDispo + 1 %>' name='<%= itJour %>_pm<%= cptDispo + 1 %>' value="true" class="ds44-checkbox"     /><label for='<%= itJour %>_pm<%= cptDispo + 1 %>' class="ds44-boxLabel"><%= itJour %>  - après-midi</label>
                        </div>
                    </td>               
                </jalios:foreach>                  
              </tr>
              <tr>
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-APEC"></trsb:glp></th>             
                <jalios:foreach array="<%= jours %>" name="itJour" type="String">                
                    <td>
                        <div class="ds44-form__container ds44-checkBox-radio_list ">
                            <input <%= "true".equalsIgnoreCase(apres[itCounter-1]) ? "checked" : "" %> type="checkbox" id='<%= itJour %>_apres_ecole<%= cptDispo + 1 %>' name='<%= itJour %>_apres_ecole<%= cptDispo + 1 %>' value="true" class="ds44-checkbox"     /><label for='<%= itJour %>_apres_ecole<%= cptDispo + 1 %>' class="ds44-boxLabel"><%= itJour %>  - périscolaire soir</label>
                        </div>
                    </td>               
                </jalios:foreach>  

              </tr>
              <tr>
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-NUIT"></trsb:glp></th>              
                <jalios:foreach array="<%= jours %>" name="itJour" type="String">                
                    <td>
                        <div class="ds44-form__container ds44-checkBox-radio_list ">
                            <input <%= "true".equalsIgnoreCase(nuit[itCounter-1]) ? "checked" : "" %> type="checkbox" id='<%= itJour %>_nuit<%= cptDispo + 1 %>' name='<%= itJour %>_nuit<%= cptDispo + 1 %>' value="true" class="ds44-checkbox"     /><label for='<%= itJour %>_nuit<%= cptDispo + 1 %>' class="ds44-boxLabel"><%= itJour %>  - nuit</label>
                        </div>
                    </td>               
                </jalios:foreach>
              </tr>
            </table>
            
            <p role="heading" data-fields-no-reset data-enabled-by-dispo-toggleable-oui-<%= cptDispo + 1 %> class="" aria-level="2"><trsb:glp key="DISPO-OUI-REMP-HTML"></trsb:glp></p>
            <div id="form-element-<%= uuid %>" data-name="dispoDepannagePlaceDisponible<%= cptDispo + 1 %>" class="ds44-form__radio_container" data-enabled-by-dispo-toggleable-oui-<%= cptDispo + 1 %>>
            <% uuid = UUID.randomUUID().toString(); %>
<%--                <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p> --%>
               <div class="ds44-form__container ds44-checkBox-radio_list inbl">
                  <input type="radio" name="dispoDepannagePlaceDisponible<%= cptDispo + 1 %>" value="true" <% if(dispoDepannagePlaceDisponible1.equals("true")) { %> checked <% } %> 
                  id="remplacement-toggleable-oui-<%= cptDispo + 1 %>" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" />
                  <label id="lbl-remplacement-toggleable-oui-<%= cptDispo + 1 %>" for="remplacement-toggleable-oui-<%= cptDispo + 1 %>" class="ds44-radioLabel"><%= glp("ui.com.lbl.true") %></label>
               </div>
               <div class="ds44-form__container ds44-checkBox-radio_list inbl">
                  <input type="radio" name="dispoDepannagePlaceDisponible<%= cptDispo + 1 %>" value="false" <% if(dispoDepannagePlaceDisponible1.equals("false")) { %> checked <% } %> 
                  id="name-radio-form-element-<%= uuid %>-false" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>"/>
                  <label id="label-radio-form-element-<%= uuid %>-false" for="name-radio-form-element-<%= uuid %>-false" class="ds44-radioLabel"><%= glp("ui.com.lbl.false") %></label>
               </div>
            </div>
            
            
            <% uuid = UUID.randomUUID().toString(); %>
            <div class='<%= hideEtaDispo1 ? " hidden" : "hidden" %>' data-fields-no-reset data-enabled-by-remplacement-toggleable-oui-<%= cptDispo + 1 %>>
              <jalios:buffer name="precisionsLbl"><trsb:glp key="DISPO-OUI-PREC-HTML" attribute="true"></trsb:glp></jalios:buffer>
              <div class='ds44-form__container'>
                   <div class="ds44-posRel">
                      <label for="form-element-<%= uuid %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><trsb:glp key="DISPO-OUI-REMP-PREC-HTML" attribute="true"></trsb:glp></span></span></label>
                      <textarea rows="5" cols="1" id="form-element-<%= uuid %>" name="precisionsDepannagePlaceDisponible<%= cptDispo + 1 %>" class="ds44-inpStd" title="<%= precisionsLbl %>"><%= precisionsDepannagePlaceDisponible1 %></textarea>
                   </div>
              </div>
            </div>
            
            
        
          </div>
        </div>
        
        <div style="clar:both"></div>
        <p class="hidden" data-fields-no-reset data-enabled-by-dispo-toggleable-oui-plus-<%= cptDispo + 1 %> data-enabled-by-dispo-toggleable-non-<%= cptDispo + 1 %> id="declaration<%= cptDispo + 1 %>" style="clear: both;"><trsb:glp key="DISPO-OUI-PLUS-LEG-HTML"></trsb:glp></p>  
     

      <% } %>
 
    </div>
      
      
      <div class="ds44-form__container">
        <button data-send-native class="ds44-btnStd ds44-btn--invert" data-submit-value="true" data-submit-key="opCreate" title='<trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp>'><trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp></button>
        <input type="hidden" name="noSendRedirect" value="true" data-technical-field/> 
        <input type="hidden" name="opUpdate" value="true" />
        <input type="hidden" name="csrftoken" value="<%= HttpUtil.getCSRFToken(request) %>" data-technical-field>
      </div>       
    
  </form>