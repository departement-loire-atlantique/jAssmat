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

    <%-- TODO --%>
    <div class="blocQuestion disponibilite">
  
      <h3 class="h4-like"><trsb:glp key="DISPO-INTRO-HTML"></trsb:glp></h3>
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
            À domicile : 
          </jalios:if>
          <jalios:if predicate='<%= dispos.get(cptDispo).getAgrement().contains("mam-") %>'>
            En MAM : 
          </jalios:if>
            1 place <%= AssmatUtil.getTitlePlace(dispos.get(cptDispo).getLibelle(), dispos.get(cptDispo).getLibelleComplementaire(), dispos.get(cptDispo).getTrancheDage()) %></p>      
        </h4>
        <jalios:select>     
          <jalios:if predicate='<%= dispos.get(cptDispo).getSaisieDisponible() %>'>
            <% uuid = UUID.randomUUID().toString(); %>
            <div id="form-element-<%= uuid %>" data-name="etatDispo<%= cptDispo + 1 %>" class="ds44-form__radio_container ds44-form__container">
               <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
               <div class="ds44-form__container ds44-checkBox-radio_list">
                  <input type="radio" <% if(Util.notEmpty(etatDispo) && etatDispo.equals("1")) { %> checked <% } %> name="etatDispo<%= cptDispo + 1 %>" 
                      value="1" id="dispo-toggleable-oui-<%= cptDispo + 1 %>" class="ds44-radio"/>
                  <label id="label-radio-form-element-<%= uuid %>-one" for="dispo-toggleable-oui-<%= cptDispo + 1 %>" class="ds44-radioLabel"><trsb:glp key="DISPO-OUI-HTML" ></trsb:glp></label>
               </div>
               <div class="ds44-form__container ds44-checkBox-radio_list ">
                  <input type="radio" <% if(Util.notEmpty(etatDispo) && etatDispo.equals("2")) { %> checked <% } %> name="etatDispo<%= cptDispo + 1 %>" 
                      value="2" id="dispo-toggleable-oui-plus-<%= cptDispo + 1 %>" class="ds44-radio" 
                      required aria-describedby="mandatory-message-form-element-<%= uuid %>"/>
                  <label id="label-radio-form-element-<%= uuid %>-two" for="dispo-toggleable-oui-plus-<%= cptDispo + 1 %>" class="ds44-radioLabel"><trsb:glp key="DISPO-OUI-PLUS-HTML"></trsb:glp></label>
               </div>
               <div class="ds44-form__container ds44-checkBox-radio_list ">
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
          
          String moisCourantDispo = "";
          String anneeCouranteDispo = "";
          // récupération de la donnée actuelle. Si vide, il faut forcer une valeur par défaut
          switch(cptDispo) {
          case 0:
            moisCourantDispo = formHandler.getMoisDispo1();
            anneeCouranteDispo = formHandler.getAnneeDispo1();
            break;
          case 1:
            moisCourantDispo = formHandler.getMoisDispo2();
            anneeCouranteDispo = formHandler.getAnneeDispo2();
            break;
          case 2:
            moisCourantDispo = formHandler.getMoisDispo3();
            anneeCouranteDispo = formHandler.getAnneeDispo3();
            break;
          case 3:
            moisCourantDispo = formHandler.getMoisDispo4();
            anneeCouranteDispo = formHandler.getAnneeDispo4();
            break;
          case 4:
            moisCourantDispo = formHandler.getMoisDispo5();
            anneeCouranteDispo = formHandler.getAnneeDispo5();
            break;
          case 5:
            moisCourantDispo = formHandler.getMoisDispo6();
            anneeCouranteDispo = formHandler.getAnneeDispo6();
            break;
          case 6:
            moisCourantDispo = formHandler.getMoisDispo7();
            anneeCouranteDispo = formHandler.getAnneeDispo7();
            break;
          case 7:
            moisCourantDispo = formHandler.getMoisDispo8();
            anneeCouranteDispo = formHandler.getAnneeDispo8();
            break;
          case 8:
            moisCourantDispo = formHandler.getMoisDispo9();
            anneeCouranteDispo = formHandler.getAnneeDispo9();
            break;
          case 9:
            moisCourantDispo = formHandler.getMoisDispo10();
            anneeCouranteDispo = formHandler.getAnneeDispo10();
            break;
          }
          
          if (Util.isEmpty(moisCourantDispo)) moisCourantDispo = "";
          if (Util.isEmpty(anneeCouranteDispo)) anneeCouranteDispo = "";
          %>
           
          <%-- Par defaut ou si non renseigné coche disponibilité inconnue --%>
          <input class="hidden" type="radio" name="etatDispo<%= cptDispo + 1 %>" id="categorie3DispoInconnu<%= cptDispo + 1 %>" value="0"<% if(Util.isEmpty(etatDispo) || etatDispo.equals("0")) { %> checked <% } %> data-technical-field/>
        
          <div id="Formulaires<%= cptDispo + 1 %>" data-enabled-by-dispo-toggleable-oui-<%= cptDispo + 1 %> data-enabled-by-dispo-toggleable-oui-plus-<%= cptDispo + 1 %> class='<%= hideEtaDispo1 && hideEtaDispo2 ? " hidden" : "" %>' style="clear: both;"> 
          
              <% uuid = UUID.randomUUID().toString(); %>
              <div data-enabled-by-dispo-toggleable-oui-plus-<%= cptDispo + 1 %>><p aria-level="2" class="h4-like"><trsb:glp key="DISPO-OUI-PLUS-DATE"></trsb:glp></p></div>
              <div class='ds44-form__container<%= hideEtaDispo2 ? " hidden" : "" %>' data-enabled-by-dispo-toggleable-oui-plus-<%= cptDispo + 1 %>>
                   <div class="ds44-select__shape ds44-inpStd">
                      <p class="ds44-selectLabel" aria-hidden="true"><%= glp("ui.com.lbl.month") %></p>
                      <div id="form-element-<%= uuid %>" data-name="moisDispo<%= cptDispo + 1 %>" class="ds44-js-select-standard ds44-selectDisplay"></div>
                      <button type="button" id="button-form-element-<%= uuid %>" class="ds44-btnIco ds44-posAbs ds44-posRi ds44-btnOpen" aria-expanded="false" title='<%= glp("ui.com.lbl.month") %>'><i class="icon icon-down icon--sizeXL" aria-hidden="true"></i><span id="button-message-form-element-<%= uuid %>" class="visually-hidden"><trsb:glp key="DISPO-OUI-PLUS-DATE" attribute="true"></trsb:glp></span></button>
                      <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeXL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", glp("ui.com.lbl.month")) %></span></button>
                   </div>
                   <div class="ds44-select-container hidden">
                      <div class="ds44-listSelect">
                         <ul class="ds44-list" role="listbox" id="listbox-form-element-<%= uuid %>" aria-labelledby="button-message-form-element-<%= uuid %>">
                            <% for (int monthCounter = 0; monthCounter < 12; monthCounter++) { 
                              noMonthIsCurrentMonth = Util.isEmpty(moisCourantDispo) && monthCounter == Integer.parseInt(moisActuel);
                            %>
                            <li class='ds44-select-list_elem<% if((Util.notEmpty(moisCourantDispo) && moisCourantDispo.equals(Integer.toString(monthCounter))) || noMonthIsCurrentMonth) { %> selected_option <% } %>' 
                            data-value="<%= monthCounter %>" tabindex="0" role="option" 
                            <% if((Util.notEmpty(moisCourantDispo) && moisCourantDispo.equals(Integer.toString(monthCounter))) || noMonthIsCurrentMonth) { %> aria-selected="true" <% } %>>
                               <%= glp("plugin.assmatplugin.month." + monthCounter) %>
                            </li>
                            <% } %>
                         </ul>
                      </div>
                   </div>
              </div>
              <% uuid = UUID.randomUUID().toString(); %>
              <div class='ds44-form__container<%= hideEtaDispo2 ? " hidden" : "" %>' data-enabled-by-dispo-toggleable-oui-plus-<%= cptDispo + 1 %>>
                   <div class="ds44-select__shape ds44-inpStd">
                      <p class="ds44-selectLabel" aria-hidden="true"><%= glp("jcmsplugin.assmatplugin.parametrage.annee") %></p>
                      <div id="form-element-<%= uuid %>" data-name="anneeDispo<%= cptDispo + 1 %>" class="ds44-js-select-standard ds44-selectDisplay"></div>
                      <button type="button" id="button-form-element-<%= uuid %>" class="ds44-btnIco ds44-posAbs ds44-posRi ds44-btnOpen" aria-expanded="false" title='<%= glp("ui.com.lbl.year") %>'><i class="icon icon-down icon--sizeXL" aria-hidden="true"></i><span id="button-message-form-element-<%= uuid %>" class="visually-hidden"><trsb:glp key="DISPO-OUI-PLUS-DATE" attribute="true"></trsb:glp></span></button>
                      <button class="ds44-reset" type="button"><i class="icon icon-cross icon--sizeXL" aria-hidden="true"></i><span class="visually-hidden"><%= glp("jcmsplugin.socle.facette.effacer-contenu-champ", glp("ui.com.lbl.year")) %></span></button>
                   </div>
                   <div class="ds44-select-container hidden">
                      <div class="ds44-listSelect">
                         <ul class="ds44-list" role="listbox" id="listbox-form-element-<%= uuid %>" aria-labelledby="button-message-form-element-<%= uuid %>">
                            <% for(int cptAnnee = Integer.parseInt(anneeActuelle); cptAnnee < Integer.parseInt(anneeActuelle) + 11; cptAnnee++) { 
                            noYearIsCurrentYear = Util.isEmpty(anneeCouranteDispo) && cptAnnee == Integer.parseInt(anneeActuelle);
                            %>
                            <li class='ds44-select-list_elem<% if((Util.notEmpty(anneeCouranteDispo) && anneeCouranteDispo.equals(Integer.toString(cptAnnee))) || noYearIsCurrentYear) { %> selected_option <% } %>' 
                            data-value="<%= cptAnnee %>" tabindex="0" role="option" 
                            <% if((Util.notEmpty(anneeCouranteDispo) && anneeCouranteDispo.equals(Integer.toString(cptAnnee))) || noYearIsCurrentYear) { %> aria-selected="true" <% } %>>
                               <%= cptAnnee %>
                            </li>
                            <% } %>
                         </ul>
                      </div>
                   </div>
              </div>
              
              <% uuid = UUID.randomUUID().toString(); %>
              <jalios:buffer name="precisionsLbl"><trsb:glp key="DISPO-OUI-PLUS-PREC-HTML" attribute="true"></trsb:glp></jalios:buffer>
              <div class='ds44-form__container<%= hideEtaDispo2 ? " hidden" : "" %>' data-enabled-by-dispo-toggleable-oui-plus-<%= cptDispo + 1 %>>
                   <div class="ds44-posRel">
                      <label for="form-element-<%= uuid %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><trsb:glp key="DISPO-OUI-PLUS-PREC-HTML" attribute="true"></trsb:glp></span></span></label>
                      <textarea rows="5" cols="1" id="form-element-<%= uuid %>" name="precisionPlaceFuture<%= cptDispo + 1 %>" class="ds44-inpStd" title="<%= precisionsLbl %>"><%= precisionPlaceFuture1 %></textarea>
                   </div>
              </div>
            
          </div>
    
          <% uuid = UUID.randomUUID().toString(); %>
          <div class='<%= hideEtaDispo1 ? " hidden" : "" %>' data-enabled-by-dispo-toggleable-oui-<%= cptDispo + 1 %>>
              <jalios:buffer name="precisionsLbl"><trsb:glp key="DISPO-OUI-PREC-HTML" attribute="true"></trsb:glp></jalios:buffer>
              <div class='ds44-form__container<%= hideEtaDispo1 ? " hidden" : "" %>'>
                   <div class="ds44-posRel">
                      <label for="form-element-<%= uuid %>" class="ds44-formLabel"><span class="ds44-labelTypePlaceholder"><span><trsb:glp key="DISPO-OUI-PREC-HTML" attribute="true"></trsb:glp></span></span></label>
                      <textarea rows="5" cols="1" id="form-element-<%= uuid %>" name="precisionsPlaceDisponible<%= cptDispo + 1 %>" class="ds44-inpStd" title="<%= precisionsLbl %>"><%= precisionsPlaceDisponible1 %></textarea>
                   </div>
              </div>
          </div>
    
          <div id="dispoActuelle<%= cptDispo + 1 %>" data-enabled-by-dispo-toggleable-oui-<%= cptDispo + 1 %> data-enabled-by-dispo-toggleable-oui-plus-<%= cptDispo + 1 %> class='<%= hideEtaDispo1 && hideEtaDispo2 ? " hidden" : "" %>'>
               
            <table class="tabDispos tabDispos_checkbox mbm" id="tableauSemainier<%= cptDispo + 1 %>">
              <caption role="heading" aria-level="2"><trsb:glp key="DISPO-OUI-SEM-HTML"></trsb:glp></caption>
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
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="lundi_avant_ecole<%= cptDispo + 1 %>" name="lundi_avant_ecole<%= cptDispo + 1 %>" value="true" <% if(lundi_avant_ecole1.equals("true")) { %> checked <% } %> /><label for="lundi_avant_ecole<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-lundi_avant_ecole<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.1") %> - <trsb:glp key="DISPO-OUI-SEM-AV"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="mardi_avant_ecole<%= cptDispo + 1 %>" name="mardi_avant_ecole<%= cptDispo + 1 %>" value="true" <% if(mardi_avant_ecole1.equals("true")) { %> checked <% } %> /><label for="mardi_avant_ecole<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-mardi_avant_ecole<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.2") %> - <trsb:glp key="DISPO-OUI-SEM-AV"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="mercredi_avant_ecole<%= cptDispo + 1 %>" name="mercredi_avant_ecole<%= cptDispo + 1 %>" value="true" <% if(mercredi_avant_ecole1.equals("true")) { %> checked <% } %> /><label for="mercredi_avant_ecole<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-mercredi_avant_ecole<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.3") %> - <trsb:glp key="DISPO-OUI-SEM-AV"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="jeudi_avant_ecole<%= cptDispo + 1 %>" name="jeudi_avant_ecole<%= cptDispo + 1 %>" value="true" <% if(jeudi_avant_ecole1.equals("true")) { %> checked <% } %> /><label for="jeudi_avant_ecole<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-jeudi_avant_ecole<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.4") %> - <trsb:glp key="DISPO-OUI-SEM-AV"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="vendredi_avant_ecole<%= cptDispo + 1 %>" name="vendredi_avant_ecole<%= cptDispo + 1 %>" value="true" <% if(vendredi_avant_ecole1.equals("true")) { %> checked <% } %> /><label for="vendredi_avant_ecole<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-vendredi_avant_ecole<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.5") %> - <trsb:glp key="DISPO-OUI-SEM-AV"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="samedi_avant_ecole<%= cptDispo + 1 %>" name="samedi_avant_ecole<%= cptDispo + 1 %>" value="true" <% if(samedi_avant_ecole1.equals("true")) { %> checked <% } %> /><label for="samedi_avant_ecole<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-samedi_avant_ecole<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.6") %> - <trsb:glp key="DISPO-OUI-SEM-AV"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="dimanche_avant_ecole<%= cptDispo + 1 %>" name="dimanche_avant_ecole<%= cptDispo + 1 %>" value="true" <% if(dimanche_avant_ecole1.equals("true")) { %> checked <% } %> /><label for="dimanche_avant_ecole<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-dimanche_avant_ecole<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.7") %> - <trsb:glp key="DISPO-OUI-SEM-AV"></trsb:glp></label></div></td>
              </tr>
              <tr>
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-MAT"></trsb:glp></th>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="lundi_matin<%= cptDispo + 1 %>" name="lundi_matin<%= cptDispo + 1 %>" value="true" <% if(lundi_matin1.equals("true")) { %> checked <% } %> /><label for="lundi_matin<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-lundi_matin<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.1") %> - <trsb:glp key="DISPO-OUI-SEM-MAT"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="mardi_matin<%= cptDispo + 1 %>" name="mardi_matin<%= cptDispo + 1 %>" value="true" <% if(mardi_matin1.equals("true")) { %> checked <% } %> /><label for="mardi_matin<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-mardi_matin<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.2") %> - <trsb:glp key="DISPO-OUI-SEM-MAT"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="mercredi_matin<%= cptDispo + 1 %>" name="mercredi_matin<%= cptDispo + 1 %>" value="true" <% if(mercredi_matin1.equals("true")) { %> checked <% } %> /><label for="mercredi_matin<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-mercredi_matin<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.3") %> - <trsb:glp key="DISPO-OUI-SEM-MAT"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="jeudi_matin<%= cptDispo + 1 %>" name="jeudi_matin<%= cptDispo + 1 %>" value="true" <% if(jeudi_matin1.equals("true")) { %> checked <% } %> /><label for="jeudi_matin<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-jeudi_matin<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.4") %> - <trsb:glp key="DISPO-OUI-SEM-MAT"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="vendredi_matin<%= cptDispo + 1 %>" name="vendredi_matin<%= cptDispo + 1 %>" value="true" <% if(vendredi_matin1.equals("true")) { %> checked <% } %> /><label for="vendredi_matin<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-vendredi_matin<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.5") %> - <trsb:glp key="DISPO-OUI-SEM-MAT"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="samedi_matin<%= cptDispo + 1 %>" name="samedi_matin<%= cptDispo + 1 %>" value="true" <% if(samedi_matin1.equals("true")) { %> checked <% } %> /><label for="samedi_matin<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-samedi_matin<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.6") %> - <trsb:glp key="DISPO-OUI-SEM-MAT"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="dimanche_matin<%= cptDispo + 1 %>" name="dimanche_matin<%= cptDispo + 1 %>" value="true" <% if(dimanche_matin1.equals("true")) { %> checked <% } %> /><label for="dimanche_matin<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-dimanche_matin<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.7") %> - <trsb:glp key="DISPO-OUI-SEM-MAT"></trsb:glp></label></div></td>
              </tr>
              <tr>
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-MIDI"></trsb:glp></th>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="lundi_midi<%= cptDispo + 1 %>" name="lundi_midi<%= cptDispo + 1 %>" value="true" <% if(lundi_midi1.equals("true")) { %> checked <% } %> /><label for="lundi_midi<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-lundi_midi<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.1") %> - <trsb:glp key="DISPO-OUI-SEM-MIDI"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="mardi_midi<%= cptDispo + 1 %>" name="mardi_midi<%= cptDispo + 1 %>" value="true" <% if(mardi_midi1.equals("true")) { %> checked <% } %> /><label for="mardi_midi<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-mardi_midi<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.2") %> - <trsb:glp key="DISPO-OUI-SEM-MIDI"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="mercredi_midi<%= cptDispo + 1 %>" name="mercredi_midi<%= cptDispo + 1 %>" value="true" <% if(mercredi_midi1.equals("true")) { %> checked <% } %> /><label for="mercredi_midi<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-mercredi_midi<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.3") %> - <trsb:glp key="DISPO-OUI-SEM-MIDI"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="jeudi_midi<%= cptDispo + 1 %>" name="jeudi_midi<%= cptDispo + 1 %>" value="true" <% if(jeudi_midi1.equals("true")) { %> checked <% } %> /><label for="jeudi_midi<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-jeudi_midi<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.4") %> - <trsb:glp key="DISPO-OUI-SEM-MIDI"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="vendredi_midi<%= cptDispo + 1 %>" name="vendredi_midi<%= cptDispo + 1 %>" value="true" <% if(vendredi_midi1.equals("true")) { %> checked <% } %> /><label for="vendredi_midi<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-vendredi_midi<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.5") %> - <trsb:glp key="DISPO-OUI-SEM-MIDI"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="samedi_midi<%= cptDispo + 1 %>" name="samedi_midi<%= cptDispo + 1 %>" value="true" <% if(samedi_midi1.equals("true")) { %> checked <% } %> /><label for="samedi_midi<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-samedi_midi<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.6") %> - <trsb:glp key="DISPO-OUI-SEM-MIDI"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="dimanche_midi<%= cptDispo + 1 %>" name="dimanche_midi<%= cptDispo + 1 %>" value="true" <% if(dimanche_midi1.equals("true")) { %> checked <% } %> /><label for="dimanche_midi<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-dimanche_midi<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.7") %> - <trsb:glp key="DISPO-OUI-SEM-MIDI"></trsb:glp></label></div></td>
              </tr>
              <tr>
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-APM"></trsb:glp></th>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="lundi_pm<%= cptDispo + 1 %>" name="lundi_pm<%= cptDispo + 1 %>" value="true" <% if(lundi_pm1.equals("true")) { %> checked <% } %> /><label for="lundi_pm<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-lundi_pm<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.1") %> - <trsb:glp key="DISPO-OUI-SEM-APM"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="mardi_pm<%= cptDispo + 1 %>" name="mardi_pm<%= cptDispo + 1 %>" value="true" <% if(mardi_pm1.equals("true")) { %> checked <% } %> /><label for="mardi_pm<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-mardi_pm<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.2") %> - <trsb:glp key="DISPO-OUI-SEM-APM"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="mercredi_pm<%= cptDispo + 1 %>" name="mercredi_pm<%= cptDispo + 1 %>" value="true" <% if(mercredi_pm1.equals("true")) { %> checked <% } %> /><label for="mercredi_pm<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-mercredi_pm<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.3") %> - <trsb:glp key="DISPO-OUI-SEM-APM"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="jeudi_pm<%= cptDispo + 1 %>" name="jeudi_pm<%= cptDispo + 1 %>" value="true" <% if(jeudi_pm1.equals("true")) { %> checked <% } %> /><label for="jeudi_pm<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-jeudi_pm<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.4") %> - <trsb:glp key="DISPO-OUI-SEM-APM"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="vendredi_pm<%= cptDispo + 1 %>" name="vendredi_pm<%= cptDispo + 1 %>" value="true" <% if(vendredi_pm1.equals("true")) { %> checked <% } %> /><label for="vendredi_pm<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-vendredi_pm<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.5") %> - <trsb:glp key="DISPO-OUI-SEM-APM"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="samedi_pm<%= cptDispo + 1 %>" name="samedi_pm<%= cptDispo + 1 %>" value="true" <% if(samedi_pm1.equals("true")) { %> checked <% } %> /><label for="samedi_pm<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-samedi_pm<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.6") %> - <trsb:glp key="DISPO-OUI-SEM-APM"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="dimanche_pm<%= cptDispo + 1 %>" name="dimanche_pm<%= cptDispo + 1 %>" value="true" <% if(dimanche_pm1.equals("true")) { %> checked <% } %> /><label for="dimanche_pm<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-dimanche_pm<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.7") %> - <trsb:glp key="DISPO-OUI-SEM-APM"></trsb:glp></label></div></td>
              </tr>
              <tr>
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-APEC"></trsb:glp></th>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="lundi_apres_ecole<%= cptDispo + 1 %>" name="lundi_apres_ecole<%= cptDispo + 1 %>" value="true" <% if(lundi_apres_ecole1.equals("true")) { %> checked <% } %> /><label for="lundi_apres_ecole<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-lundi_apres_ecole<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.1") %> - <trsb:glp key="DISPO-OUI-SEM-APEC"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="mardi_apres_ecole<%= cptDispo + 1 %>" name="mardi_apres_ecole<%= cptDispo + 1 %>" value="true" <% if(mardi_apres_ecole1.equals("true")) { %> checked <% } %> /><label for="mardi_apres_ecole<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-mardi_apres_ecole<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.2") %> - <trsb:glp key="DISPO-OUI-SEM-APEC"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="mercredi_apres_ecole<%= cptDispo + 1 %>" name="mercredi_apres_ecole<%= cptDispo + 1 %>" value="true" <% if(mercredi_apres_ecole1.equals("true")) { %> checked <% } %> /><label for="mercredi_apres_ecole<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-mercredi_apres_ecole<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.3") %> - <trsb:glp key="DISPO-OUI-SEM-APEC"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="jeudi_apres_ecole<%= cptDispo + 1 %>" name="jeudi_apres_ecole<%= cptDispo + 1 %>" value="true" <% if(jeudi_apres_ecole1.equals("true")) { %> checked <% } %> /><label for="jeudi_apres_ecole<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-jeudi_apres_ecole<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.4") %> - <trsb:glp key="DISPO-OUI-SEM-APEC"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="vendredi_apres_ecole<%= cptDispo + 1 %>" name="vendredi_apres_ecole<%= cptDispo + 1 %>" value="true" <% if(vendredi_apres_ecole1.equals("true")) { %> checked <% } %> /><label for="vendredi_apres_ecole<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-vendredi_apres_ecole<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.5") %> - <trsb:glp key="DISPO-OUI-SEM-APEC"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="samedi_apres_ecole<%= cptDispo + 1 %>" name="samedi_apres_ecole<%= cptDispo + 1 %>" value="true" <% if(samedi_apres_ecole1.equals("true")) { %> checked <% } %> /><label for="samedi_apres_ecole<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-samedi_apres_ecole<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.6") %> - <trsb:glp key="DISPO-OUI-SEM-APEC"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="dimanche_apres_ecole<%= cptDispo + 1 %>" name="dimanche_apres_ecole<%= cptDispo + 1 %>" value="true" <% if(dimanche_apres_ecole1.equals("true")) { %> checked <% } %> /><label for="dimanche_apres_ecole<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-dimanche_apres_ecole<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.7") %> - <trsb:glp key="DISPO-OUI-SEM-APEC"></trsb:glp></label></div></td>
              </tr>
              <tr>
                <th scope="row"><trsb:glp key="DISPO-OUI-SEM-NUIT"></trsb:glp></th>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="lundi_nuit<%= cptDispo + 1 %>" name="lundi_nuit<%= cptDispo + 1 %>" value="true" <% if(lundi_nuit1.equals("true")) { %> checked <% } %> /><label for="lundi_nuit<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-lundi_nuit<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.1") %> - <trsb:glp key="DISPO-OUI-SEM-NUIT"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="mardi_nuit<%= cptDispo + 1 %>" name="mardi_nuit<%= cptDispo + 1 %>" value="true" <% if(mardi_nuit1.equals("true")) { %> checked <% } %> /><label for="mardi_nuit<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-mardi_nuit<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.2") %> - <trsb:glp key="DISPO-OUI-SEM-NUIT"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="mercredi_nuit<%= cptDispo + 1 %>" name="mercredi_nuit<%= cptDispo + 1 %>" value="true" <% if(mercredi_nuit1.equals("true")) { %> checked <% } %> /><label for="mercredi_nuit<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-mercredi_nuit<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.3") %> - <trsb:glp key="DISPO-OUI-SEM-NUIT"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="jeudi_nuit<%= cptDispo + 1 %>" name="jeudi_nuit<%= cptDispo + 1 %>" value="true" <% if(jeudi_nuit1.equals("true")) { %> checked <% } %> /><label for="jeudi_nuit<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-jeudi_nuit<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.4") %> - <trsb:glp key="DISPO-OUI-SEM-NUIT"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="vendredi_nuit<%= cptDispo + 1 %>" name="vendredi_nuit<%= cptDispo + 1 %>" value="true" <% if(vendredi_nuit1.equals("true")) { %> checked <% } %> /><label for="vendredi_nuit<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-vendredi_nuit<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.5") %> - <trsb:glp key="DISPO-OUI-SEM-NUIT"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="samedi_nuit<%= cptDispo + 1 %>" name="samedi_nuit<%= cptDispo + 1 %>" value="true" <% if(samedi_nuit1.equals("true")) { %> checked <% } %> /><label for="samedi_nuit<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-samedi_nuit<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.6") %> - <trsb:glp key="DISPO-OUI-SEM-NUIT"></trsb:glp></label></div></td>
                <td><div class="ds44-form__container ds44-checkBox-radio_list "><input type="checkbox" class="ds44-checkbox" id="dimanche_nuit<%= cptDispo + 1 %>" name="dimanche_nuit<%= cptDispo + 1 %>" value="true" <% if(dimanche_nuit1.equals("true")) { %> checked <% } %> /><label for="dimanche_nuit<%= cptDispo + 1 %>" class="ds44-boxLabel" id="label-dimanche_nuit<%= cptDispo + 1 %>"><%= glp("jcmsplugin.socle.infolocale.label.day.7") %> - <trsb:glp key="DISPO-OUI-SEM-NUIT"></trsb:glp></label></div></td>
              </tr>
            </table>
            
            <p data-enabled-by-dispo-toggleable-oui-<%= cptDispo + 1 %> class="h4-like" aria-level="2"><trsb:glp key="DISPO-OUI-REMP-HTML"></trsb:glp></p>
            <div id="form-element-<%= uuid %>" data-name="dispoDepannagePlaceDisponible<%= cptDispo + 1 %>" class="ds44-form__radio_container" data-enabled-by-dispo-toggleable-oui-<%= cptDispo + 1 %>>
            <% uuid = UUID.randomUUID().toString(); %>
               <p id="mandatory-message-form-element-<%= uuid %>" class="ds44-mandatory_message"><%= glp("jcmsplugin.socle.pageutile.message-case") %></p>
               <div class="ds44-form__container ds44-checkBox-radio_list">
                  <input type="radio" name="dispoDepannagePlaceDisponible<%= cptDispo + 1 %>" value="true" <% if(dispoDepannagePlaceDisponible1.equals("true")) { %> checked <% } %> 
                  id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>" data-enabled-by-precision-<%= cptDispo + 1 %>/>
                  <label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><%= glp("ui.com.lbl.true") %></label>
               </div>
               <div class="ds44-form__container ds44-checkBox-radio_list">
                  <input type="radio" name="dispoDepannagePlaceDisponible<%= cptDispo + 1 %>" value="false" <% if(dispoDepannagePlaceDisponible1.equals("false")) { %> checked <% } %> 
                  id="name-radio-form-element-<%= uuid %>-true" class="ds44-radio" aria-describedby="mandatory-message-form-element-<%= uuid %>"/>
                  <label id="label-radio-form-element-<%= uuid %>-true" for="name-radio-form-element-<%= uuid %>-true" class="ds44-radioLabel"><%= glp("ui.com.lbl.false") %></label>
               </div>
            </div>
        
          </div>
        </div>
        <div class="mbm">
            <p data-enabled-by-dispo-toggleable-oui-plus-<%= cptDispo + 1 %> data-enabled-by-dispo-toggleable-non-<%= cptDispo + 1 %> id="declaration<%= cptDispo + 1 %>"><trsb:glp key="DISPO-OUI-PLUS-LEG-HTML"></trsb:glp></p>
        </div>
        <% } %>
      </div>
      
      
      <div class="ds44-form__container">
        <button data-send-native class="ds44-btnStd" data-submit-value="true" data-submit-key="opCreate" title='<trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp>'><trsb:glp key="SAVE-BOUTON-HTML" attribute="true"></trsb:glp></button>
        <input type="hidden" name="noSendRedirect" value="true" data-technical-field/> 
        <input type="hidden" name="opUpdate" value="true" />
        <input type="hidden" name="csrftoken" value="<%= HttpUtil.getCSRFToken(request) %>" data-technical-field>
      </div>       
    
  </form>