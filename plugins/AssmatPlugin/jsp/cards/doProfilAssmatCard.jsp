<%@page import="java.lang.reflect.Field"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="fr.cg44.plugin.socle.infolocale.entities.DateInfolocale"%>
<%@page import="fr.cg44.plugin.socle.infolocale.util.InfolocaleUtil"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="fr.cg44.plugin.socle.SocleUtils"%>
<%@ taglib prefix="ds" tagdir="/WEB-INF/tags"%>
<%@ include file='/jcore/doInitPage.jspf' %><%
%><%@ page import="com.jalios.jcms.taglib.card.*" %><%
%><%@ include file='/jcore/media/mediaTemplateInit.jspf' %><%
%><%

if (data == null) {
  return;
}

ProfilASSMAT obj = (ProfilASSMAT) data;
SolisManager solisMgr = SolisManager.getInstance();
AssmatSolis assmatSolis = Util.getFirst(solisMgr.getAssmatSolisByNumAgrement(obj.getNum_agrement()));

SimpleDateFormat formater = null;
formater = new SimpleDateFormat("dd/MM/yyyy");

String cssCard = Util.notEmpty(request.getAttribute("cssCard")) ? request.getAttribute("cssCard").toString() : "";

boolean logementAccessible = Util.notEmpty(profilAM.getLogementAccessible()) && "true".equalsIgnoreCase(profilAM.getLogementAccessible());
boolean accueilTmpPartiel = Util.notEmpty(profilAM.getAccueilTempsPartiel()) && "true".equalsIgnoreCase(profilAM.getAccueilTempsPartiel());
boolean accueilAccepteRemplacement = Util.notEmpty(profilAM.getAccepteDepannage()) && "true".equalsIgnoreCase(profilAM.getAccepteDepannage());

%>

<section class="ds44-card ds44-card--fiche <%= cssCard %>">
  <div class="ds44-card__section ds44-mb3">
      
      <h3 class="h2-like"><%= assmatSolis.getNomAssmat() %> <%= assmatSolis.getPrenomAssmat() %></h3>
      <section class="ds44-box ds44-theme ds44-mt4">
        <div class="ds44-innerBoxContainer">
          <jalios:if predicate="<%= Util.notEmpty(assmatSolis.getAdresseMam()) && Util.notEmpty(assmatSolis.getCpMam()) && Util.notEmpty(assmatSolis.getCommuneMam()) %>">
          <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-marker ds44-docListIco" aria-hidden="true"></i><%=assmatSolis.getAdresseMam() %><br /> <%=assmatSolis.getCpMam() %> <%=assmatSolis.getCommuneMam() %></p>
          </jalios:if>
          <jalios:if predicate="<%= Util.notEmpty(obj.getTelephoneFixe()) && AssmatUtil.getBooleanFromString(obj.getVisbiliteTelephoneFixe()) %>">
          <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-phone ds44-docListIco" aria-hidden="true"></i><%= obj.getTelephoneFixe() %></p>
          </jalios:if>
          <jalios:if predicate="<%= Util.notEmpty(obj.getAuthor().getEmail()) && AssmatUtil.getBooleanFromString(obj.getVisibiliteAdresseEmail()) %>">
          <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-mail ds44-docListIco" aria-hidden="true"></i><a href="mailto:<%= obj.getAuthor().getEmail() %>" title='<%= glp("jcmsplugin.socle.contactmail", assmatSolis.getNomAssmat() + " " + assmatSolis.getPrenomAssmat(), obj.getAuthor().getEmail()) %>'><%= glp("jcmsplugin.socle.contactmail.label") %></a></p>
          </jalios:if>
          <%-- Site internet -> uniquement sur des lieux --%>
          <jalios:if predicate="<%= Util.notEmpty(asmmatSolis.getExerceDomicile()) && asmmatSolis.getExerceDomicile() %>">
          <p class="ds44-docListElem"><i class="icon icon-tag ds44-docListIco" aria-hidden="true"></i>A <trsb:glp key="VERIF-LIEU-EXERCICE-DOM"></p>
          </jalios:if>
       </div>
      </section>

      <h4 class="h3-like ds44-mt3"><trsb:glp key="PROFIL-ASSMAT-LIBELLE-DISPONIBILITE-HTML" ></trsb:glp></h4>
      TODO
      
      <h4 class="h3-like ds44-mt3"><trsb:glp key="PROFIL-ASSMAT-LIBELLE-AGREMENT-HTML" ></trsb:glp></h4>
      <ul class="ds44-uList">
        <li><strong><trsb:glp key="PROFIL-ASSMAT-CONTENT-AGREMENT-PREMIER" /></strong><br/><%=formater.format(assmatSolis.getDatePremierAgrement()) %></li>
        <li><strong><trsb:glp key="PROFIL-ASSMAT-CONTENT-AGREMENT-TYPE" /></strong>
            <jalios:select>
                <jalios:if predicate='<%= assmatSolis.getExerceDomicile() %>'>
                    A <trsb:glp key="VERIF-LIEU-EXERCICE-DOM"></trsb:glp>
                    <ul class="ds44-uList">
                        <% for(int itPlace=1; itPlace<=8; itPlace++) { %>                               
                              <%
                              // agrementTrancheAgeKey
                              Field agrementTracheAgeKeyField = ReflectUtil.getField(assmatSolis.getClass(), "place"+itPlace+"AgrementTrancheAgeKey");
                              String agremenTracheAgeKey = (String) ReflectUtil.getFieldValue(assmatSolis, agrementTracheAgeKeyField);
                              
                              // placeTracheAgeKey
                              Field placeTracheAgeKeyField = ReflectUtil.getField(assmatSolis.getClass(), "place"+itPlace+"TrancheAgeKey");
                              Integer placeTracheAgeKey = (Integer) ReflectUtil.getFieldValue(assmatSolis, placeTracheAgeKeyField);    
                                  
                              // PlaceNbPlaces
                              Field placeNbPlacesField = ReflectUtil.getField(assmatSolis.getClass(), "place"+itPlace+"NbPlaces");
                              Integer placeNbPlaces = (Integer) ReflectUtil.getFieldValue(assmatSolis, placeNbPlacesField);
            
                              // placeTrancheAge
                              Field placeTrancheAgeField = ReflectUtil.getField(assmatSolis.getClass(), "place"+itPlace+"TrancheAge");
                              String placeTrancheAge = (String) ReflectUtil.getFieldValue(assmatSolis, placeTrancheAgeField);
            
                              // PlaceLibCompl
                              Field placeLibComplField = ReflectUtil.getField(assmatSolis.getClass(), "place"+itPlace+"LibCompl");
                              String placeLibCompl = (String) ReflectUtil.getFieldValue(assmatSolis, placeLibComplField);
                                  
                              // SaisieDisponibilite
                              Field placeSaisieDisponibiliteField = ReflectUtil.getField(assmatSolis.getClass(), "place"+itPlace+"SaisieDisponibilite");
                              Boolean placeSaisieDisponibilite = (Boolean) ReflectUtil.getFieldValue(assmatSolis, placeSaisieDisponibiliteField);
                              %>
                                
                              <% if(Util.notEmpty(agremenTracheAgeKey) && agremenTracheAgeKey.contains("dom-") && placeSaisieDisponibilite ){%>                   
                              <li>
                                  <%= glp("jcmsplugin.assmatplugin.inscription.verification.place", placeNbPlaces)%>
                                  <%= AssmatUtil.getTitlePlace(placeTrancheAge, placeLibCompl, placeTracheAgeKey) %>
                              </li>
                              <%} %>                                
                         <%} %> 
                    </ul>
                </jalios:if>
                
                <jalios:if predicate='<%=  assmatSolis.getExerceMam() != null && assmatSolis.getExerceMam() %>'>  
                   En <trsb:glp key="VERIF-LIEU-EXERCICE-MAM"></trsb:glp>
                   <ul class="ds44-uList">
                       <li>
                         <% for(int itPlace=1; itPlace<=8; itPlace++) { %>                          
                              <%
                              // agrementTrancheAgeKey
                              Field agrementTracheAgeKeyField = ReflectUtil.getField(assmatSolis.getClass(), "place"+itPlace+"AgrementTrancheAgeKey");
                              String agremenTracheAgeKey = (String) ReflectUtil.getFieldValue(assmatSolis, agrementTracheAgeKeyField);
                              
                              // placeTracheAgeKey
                              Field placeTracheAgeKeyField = ReflectUtil.getField(assmatSolis.getClass(), "place"+itPlace+"TrancheAgeKey");
                              Integer placeTracheAgeKey = (Integer) ReflectUtil.getFieldValue(assmatSolis, placeTracheAgeKeyField);    
                              
                              // PlaceNbPlaces
                              Field placeNbPlacesField = ReflectUtil.getField(assmatSolis.getClass(), "place"+itPlace+"NbPlaces");
                              Integer placeNbPlaces = (Integer) ReflectUtil.getFieldValue(assmatSolis, placeNbPlacesField);
        
                              // placeTrancheAge
                              Field placeTrancheAgeField = ReflectUtil.getField(assmatSolis.getClass(), "place"+itPlace+"TrancheAge");
                              String placeTrancheAge = (String) ReflectUtil.getFieldValue(assmatSolis, placeTrancheAgeField);
        
                              // PlaceLibCompl
                              Field placeLibComplField = ReflectUtil.getField(assmatSolis.getClass(), "place"+itPlace+"LibCompl");
                              String placeLibCompl = (String) ReflectUtil.getFieldValue(assmatSolis, placeLibComplField);
                              
                              // SaisieDisponibilite
                              Field placeSaisieDisponibiliteField = ReflectUtil.getField(assmatSolis.getClass(), "place"+itPlace+"SaisieDisponibilite");
                              Boolean placeSaisieDisponibilite = (Boolean) ReflectUtil.getFieldValue(assmatSolis, placeSaisieDisponibiliteField);
                              %>
                            
                              <% if(Util.notEmpty(agremenTracheAgeKey) && agremenTracheAgeKey.contains("mam-") && placeSaisieDisponibilite){%>                   
                               <li>
                                    <%= glp("jcmsplugin.assmatplugin.inscription.verification.place", placeNbPlaces) %>
                                    <%= AssmatUtil.getTitlePlace(placeTrancheAge, placeLibCompl, placeTracheAgeKey) %>
                               </li>
                              <%} %>                           
                         <%} %> 
                       </li>
                   </ul>
                </jalios:if>
            </jalios:select>
        </li>
      </ul>
      
      <h4 class="h3-like ds44-mt3"><trsb:glp  key="PROFIL-ASSMAT-LIBELLE-FORMATION-HTML" ></trsb:glp></h4>
      <trsb:glp  key="PROFIL-ASSMAT-CONTENT-FORMATION-HTML" ></trsb:glp>
      
      <jalios:if predicate='<%=
                 (
                    Util.notEmpty(obj.getAccueilPeriscolaire()) && "true".equalsIgnoreCase(obj.getAccueilPeriscolaire()) ||
                    Util.notEmpty(obj.getAccueilMercredi()) && "true".equalsIgnoreCase(obj.getAccueilMercredi()) ||
                    Util.notEmpty(obj.getAccueilPendantLesVacancesSco()) && "true".equalsIgnoreCase(obj.getAccueilPendantLesVacancesSco()) ||
                    Util.notEmpty(obj.getHorairesAtypiques()) && "true".equalsIgnoreCase(obj.getHorairesAtypiques())
                 )%>'>
         <h4 class="h3-like ds44-mt3"><trsb:glp key="PROFIL-ASSMAT-LIBELLE-TYPE-ACCUEIL-HTML" ></trsb:glp></h4>
         <ul class="ds44-uList">
	         <%if(Util.notEmpty(obj.getAccueilPeriscolaire()) && "true".equalsIgnoreCase(obj.getAccueilPeriscolaire())){ %>
	           <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-PERISCOLAIRE" /></li>
	         <%} %> 
	         <%if(Util.notEmpty(obj.getAccueilMercredi()) && "true".equalsIgnoreCase(obj.getAccueilMercredi())){ %>
	           <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-MERCREDI" /></li>
	         <%} %> 
	         <%if(Util.notEmpty(obj.getAccueilPendantLesVacancesSco()) && "true".equalsIgnoreCase(obj.getAccueilPendantLesVacancesSco())){ %>
	           <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-VACANCES" /></li>
	         <%} %> 
	         <%if(Util.notEmpty(obj.getHorairesAtypiques()) && "true".equalsIgnoreCase(obj.getHorairesAtypiques())){ %>
	             <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE" />
		             <ul class="ds44-uList">
				         <%if(Util.notEmpty(obj.getAvant7h()) && "true".equalsIgnoreCase(obj.getAvant7h())){ %>
				            <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-MATIN" /></li>
				         <%} %>
				         <%if(Util.notEmpty(obj.getApres20h()) && "true".equalsIgnoreCase(obj.getApres20h())){ %>
				            <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-SOIR" /></li>
				         <%} %>
				         <%if(Util.notEmpty(obj.getLeSamedi()) && "true".equalsIgnoreCase(obj.getLeSamedi())){ %>
				            <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-SAMEDI" /></li>
				         <%} %>
				         <%if(Util.notEmpty(obj.getLeDimanche()) && "true".equalsIgnoreCase(obj.getLeDimanche())){ %>
				            <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-DIMANCHE" /></li>
				         <%} %>
				         <%if(Util.notEmpty(obj.getLaNuit()) && "true".equalsIgnoreCase(obj.getLaNuit())){ %>
				            <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-NUIT" /></li>
				         <%} %>            
			         </ul>
	             </li>
	         <%} %>          
         </ul>
      </jalios:if>
      
      <h4 class="h3-like ds44-mt3"><trsb:glp  key="PROFIL-ASSMAT-LIBELLE-TYPE-ACCUEIL-HTML" ></trsb:glp></h4>
      <ul  class="ds44-uList">
	      <%if(Util.notEmpty(profilAM.getAccueilPeriscolaire()) && "true".equalsIgnoreCase(profilAM.getAccueilPeriscolaire())){ %>
	          <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-PERISCOLAIRE" /></li>
	      <%} %> 
	      <%if(Util.notEmpty(profilAM.getAccueilMercredi()) && "true".equalsIgnoreCase(profilAM.getAccueilMercredi())){ %>
	          <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-MERCREDI" /></li>
	      <%} %> 
	      <%if(Util.notEmpty(profilAM.getAccueilPendantLesVacancesSco()) && "true".equalsIgnoreCase(profilAM.getAccueilPendantLesVacancesSco())){ %>
	          <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-VACANCES" /></li>
	      <%} %> 
	      <%if(Util.notEmpty(profilAM.getHorairesAtypiques()) && "true".equalsIgnoreCase(profilAM.getHorairesAtypiques())){ %>
		      <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE" />
		      <ul class="ds44-uList">
			      <%if(Util.notEmpty(profilAM.getAvant7h()) && "true".equalsIgnoreCase(profilAM.getAvant7h())){ %>
			         <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-MATIN" /></li>
			      <%} %>
			      <%if(Util.notEmpty(profilAM.getApres20h()) && "true".equalsIgnoreCase(profilAM.getApres20h())){ %>
			         <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-SOIR" /></li>
			      <%} %>
			      <%if(Util.notEmpty(profilAM.getLeSamedi()) && "true".equalsIgnoreCase(profilAM.getLeSamedi())){ %>
			         <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-SAMEDI" /></li>
			      <%} %>
			      <%if(Util.notEmpty(profilAM.getLeDimanche()) && "true".equalsIgnoreCase(profilAM.getLeDimanche())){ %>
			         <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-DIMANCHE" /></li>
			      <%} %>
			      <%if(Util.notEmpty(profilAM.getLaNuit()) && "true".equalsIgnoreCase(profilAM.getLaNuit())){ %>
			         <li><trsb:glp key="PROFIL-ASSMAT-CONTENT-TYPE-ACCUEIL-ATYPIQUE-NUIT" /></li>
			      <%} %>                
		      </ul>
		      </li>
	      <%} %> 
      </ul>
      
      <jalios:if predicate="<%= logementAccessible && accueilTmpPartiel && accueilAccepteRemplacement %>">
	      <h4 class="h3-like ds44-mt3"><trsb:glp key="PROFIL-ASSMAT-LIBELLE-SPECIFICITE-HTML" ></trsb:glp></h4>
	      <jalios:if predicate='<%= logementAccessible %>'>
	        <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-handicap-moteur ds44-docListIco" aria-hidden="true"></i><trsb:glp key="PROFIL-ASSMAT-CONTENT-SPECIFICITE-ACCESSIBLE" /></p>
	      </jalios:if>
	      <jalios:if predicate='<%= accueilTmpPartiel %>'>
	        <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-time ds44-docListIco" aria-hidden="true"></i><trsb:glp key="PROFIL-ASSMAT-CONTENT-SPECIFICITE-PARTIEL" /></p>
	      </jalios:if>
	      <jalios:if predicate='<%= accueilAccepteRemplacement %>'>
	        <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-user ds44-docListIco" aria-hidden="true"></i><trsb:glp key="PROFIL-ASSMAT-CONTENT-SPECIFICITE-REMPLACEMENT" /></p>
	      </jalios:if>
      </jalios:if>
  </div>
</section>
