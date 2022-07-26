<%@page import="fr.cg44.plugin.socle.SocleUtils"%>
<%@page import="fr.trsb.cd44.solis.manager.QuartierDAO"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil.SelectionEtatDispo"%>
<%@page import="fr.cg44.plugin.assmat.selector.RelaisMamSelectorIDSolis"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil.SelectionEtatDispo"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil.SelectionEtatDispo"%>
<%@page import="fr.cg44.plugin.assmat.comparator.DisponibiliteComparator"%>
<%@page import="fr.cg44.plugin.assmat.selector.RelaisMamSelectorCommune"%>
<%@page import="fr.cg44.plugin.assmat.beans.AssmatSearch"%>
<%@page import="fr.cg44.plugin.assmat.SemainierUtil"%>
<%@page import="fr.cg44.plugin.assmat.Semainier"%>
<%@page import="java.lang.reflect.Field"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.assmat.PointAssmat"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="fr.cg44.plugin.tools.ToolsUtil"%>
<%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%>
<%@ taglib prefix="ds" tagdir="/WEB-INF/tags"%><%
%><%@ page contentType="text/html; charset=UTF-8"%><%
%><%@ include file='/jcore/doInitPage.jsp'%>

<%
ProfilASSMAT obj = (ProfilASSMAT) request.getAttribute(PortalManager.PORTAL_PUBLICATION);
SolisManager solisMgr = SolisManager.getInstance();
AssmatSolis asmmatSolis =Util.getFirst(solisMgr.getAssmatSolisByNumAgrement(obj.getNum_agrement()));
PointAssmat pointAssmat = new PointAssmat(String.valueOf(obj.getLatitudeAssmat()),String.valueOf(obj.getLongitudeAssmat()), "#AEC900", "Avec des disponibilités");

PointAssmat pointUser =(PointAssmat) session.getAttribute("userLocation");

NumberFormat nf = new DecimalFormat("0.#");

ProfilManager profilMgr = ProfilManager.getInstance();

Date dateModifDispo = profilMgr.getDateModifDispo(obj.getAuthor());

TreeMap<AssmatSearch,PointAssmat> assmatPoints =(TreeMap<AssmatSearch,PointAssmat>) session.getAttribute("assmatPoints");

//Si on vient de la recherche on retourne sur la recherche, sinon sur la selection
String urlRecherche =(String) session.getAttribute("urlRecherche");
String urlRetour = urlRecherche;

double distanceMam = -1;
double distanceDom = -1;

boolean hasQuartierMam = "44109".equals(asmmatSolis.getCodeInseeMam()) &&  Util.notEmpty(asmmatSolis.getIdMicroQuartierMam()) && asmmatSolis.getExerceMam();
boolean hasQuartierDom = "44109".equals(asmmatSolis.getCodeInsee()) &&  Util.notEmpty(asmmatSolis.getIdMicroQuartierDom()) && asmmatSolis.getExerceDomicile();


boolean isSelection = false;
if(Util.notEmpty(session.getAttribute("isSelection"))){
	isSelection = true;
  urlRetour =(String) session.getAttribute("urlSelection");
}

request.setAttribute("dataId", obj.getId());

ProfilASSMAT profilPrecedent = null;
ProfilASSMAT profilSuivant = null;

boolean isGood = false;
boolean isTerminer = false;

//0011253: Défilement des fiches dans ma sélection 
//Supprimer les boutons de défilement pour l'instant de fiche à fiche pour l'instant et prévoir une évolution

if(Util.notEmpty(assmatPoints) && !isSelection){

  if(Util.notEmpty(pointUser)) {
    if(Util.notEmpty(asmmatSolis.getLatitudeMam()) && Util.notEmpty(asmmatSolis.getLongitudeMam()) && asmmatSolis.getExerceMam()) {
      distanceMam = AssmatUtil.getDistance(asmmatSolis.getLatitudeMam().doubleValue(), asmmatSolis.getLongitudeMam().doubleValue(),(double) pointUser.getLatitude(), (double)pointUser.getLongitude()); 
    }
    if(Util.notEmpty(asmmatSolis.getLatitude()) && Util.notEmpty(asmmatSolis.getLongitude()) && asmmatSolis.getExerceDomicile()) {
      distanceDom = AssmatUtil.getDistance(asmmatSolis.getLatitude().doubleValue(), asmmatSolis.getLongitude().doubleValue(),(double) pointUser.getLatitude(), (double)pointUser.getLongitude());   
    }
  }
  
	for (Map.Entry<AssmatSearch, PointAssmat> entry : assmatPoints.entrySet())
	{
	  if(isGood && !isTerminer){
	    profilSuivant = profilMgr.getProfilASSMATbyAssmatSearch(entry.getKey());
	    isTerminer= true; 
	  }
	 if(!isTerminer){
	 if(obj==profilMgr.getProfilASSMATbyAssmatSearch(entry.getKey())){
	   isGood = true;
	  }
	 if(!isGood){
	   profilPrecedent = profilMgr.getProfilASSMATbyAssmatSearch(entry.getKey());
	 }
	 
	 }  
	}
}

String idPortalRecherche = channel.getProperty("jcmsplugin.assmatplugin.portal.recherche.am"); 
Publication portalRecherche = channel.getPublication(idPortalRecherche);

if(Util.isEmpty(urlRetour)){
    urlRetour = portalRecherche.getDisplayUrl(userLocale);
}

SimpleDateFormat formater = null;
formater = new SimpleDateFormat("dd/MM/yyyy");


jcmsContext.addCSSHeader("plugins/EServicePlugin/css/portal/portal.css");
Set<ProfilASSMAT> listeobj = (Set<ProfilASSMAT>) request.getSession().getAttribute("listeobjSelection");
PortalJspCollection portalSelection = (PortalJspCollection) channel.getPublication("$jcmsplugin.assmatplugin.portail.selection.recherche");
  jcmsContext.addJavaScript("plugins/ToolsPlugin/js/facets/tooltipCategories.js");
  jcmsContext.addCSSHeader("plugins/EServicePlugin/css/types/AbstractPortletSkinable/titleBarDottedTitleElementBorderBot.css");
  jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/headstall/model1.css");
  jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/types/AbstractPortletSkinable/titleBar.css");
  jcmsContext.addCSSHeader("plugins/CorporateIdentityPlugin/css/common.css");
  
  jcmsContext.addCSSHeader("plugins/AssmatPlugin/css/plugin.css");
  jcmsContext.addJavaScript("plugins/AssmatPlugin/js/plugin.js");

  Boolean hasDispo = profilMgr.hasDispo(obj.getAuthor()); 
  Boolean showContactDispo = !obj.getAfficherContactUniquementSiD() || hasDispo;
  
  AssmatSolis assmatSolis = Util.getFirst(solisMgr.getAssmatSolisByNumAgrement(obj.getNum_agrement()));

  Double longitude = obj.getLongitudeAssmat();
  Double latitude = obj.getLatitudeAssmat();
  String localisation = Util.notEmpty(longitude) && Util.notEmpty(latitude) ? SocleUtils.formatOpenStreetMapLink(latitude.toString(), longitude.toString()) : null;
  
  boolean logementAccessible = Util.notEmpty(obj.getLogementAccessible()) && "true".equalsIgnoreCase(obj.getLogementAccessible());
  boolean accueilTmpPartiel = Util.notEmpty(obj.getAccueilTempsPartiel()) && "true".equalsIgnoreCase(obj.getAccueilTempsPartiel());
  boolean accueilAccepteRemplacement = Util.notEmpty(obj.getAccepteDepannage()) && "true".equalsIgnoreCase(obj.getAccepteDepannage());
%>

<main id="content" role="main">

<jalios:include target="SOCLE_ALERTE"/>

    <article class="ds44-container-large">
    
    <%-- bouton Retour a la liste --%>
    <%@ include file="/plugins/SoclePlugin/jsp/facettes/doRetourListe.jspf" %>
    
    <ds:titleSimple pub="<%= obj %>" title="<%= obj.getTitle(userLang) %>" breadcrumb="false"></ds:titleSimple>
    
    <div class="ds44-img50 ds44--l-padding-tb">
            <div class="ds44-inner-container">
                <div class="ds44-grid12-offset-1">
                    <section class="ds44-box ds44-theme">
                        <div class="ds44-innerBoxContainer">
                            <div class="grid-2-small-1 ds44-grid12-offset-1">
                                <div class="col">
                                    <jalios:if predicate="<%= Util.notEmpty(assmatSolis.getAdresseMam()) && Util.notEmpty(assmatSolis.getCpMam()) && Util.notEmpty(assmatSolis.getCommuneMam()) %>">
							          <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-marker ds44-docListIco" aria-hidden="true"></i><%=assmatSolis.getAdresseMam() %><br /> <%=assmatSolis.getCpMam() %> <%=assmatSolis.getCommuneMam() %></p>
							        </jalios:if>
							        <jalios:if predicate="<%= Util.notEmpty(localisation) %>">
                                      <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-map ds44-docListIco" aria-hidden="true"></i>
					                    <a href='<%= localisation%>' 
					                        title='<%= HttpUtil.encodeForHTMLAttribute(glp("jcmsplugin.socle.ficheaide.localiser-carte.label")+" : " + obj.getTitle(userLang) + " " + glp("jcmsplugin.socle.accessibily.newTabLabel"))%>' 
					                        target="_blank">
					                        <%= glp("jcmsplugin.socle.ficheaide.localiser-carte.label") %> 
					                    </a>
                                      </p>
                                    </jalios:if>
							        <jalios:if predicate="<%= Util.notEmpty(asmmatSolis.getExerceDomicile()) && asmmatSolis.getExerceDomicile() %>">
							          <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-tag ds44-docListIco" aria-hidden="true"></i>A <trsb:glp key="VERIF-LIEU-EXERCICE-DOM"/></p>
							        </jalios:if>
                                </div>
                                <div class="col ds44--xl-padding-l">
                                    <%--
                                    phone
                                    Lien contact
                                    Dernière mise à jour
                                    --%>
                                    <jalios:if predicate="<%= Util.notEmpty(obj.getTelephoneFixe()) && AssmatUtil.getBooleanFromString(obj.getVisbiliteTelephoneFixe()) %>">
							          <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-phone ds44-docListIco" aria-hidden="true"></i><%= obj.getTelephoneFixe() %></p>
							        </jalios:if>
							        <jalios:if predicate="<%= Util.notEmpty(obj.getAuthor().getEmail()) && AssmatUtil.getBooleanFromString(obj.getVisibiliteAdresseEmail()) %>">
							          <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-mail ds44-docListIco" aria-hidden="true"></i><a href="mailto:<%= obj.getAuthor().getEmail() %>" title='<%= glp("jcmsplugin.socle.contactmail", assmatSolis.getNomAssmat() + " " + assmatSolis.getPrenomAssmat(), obj.getAuthor().getEmail()) %>'><%= glp("jcmsplugin.socle.contactmail.label") %></a></p>
							        </jalios:if>
							        <%-- mise à jour --%>
							        <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-time ds44-docListIco" aria-hidden="true"></i><%= glp("plugin.assmatplugin.profilassmat.lastupdate", formater.format(obj.getMdate())) %></p>
                                </div>
                            </div>
                        </div>
                    </section>
                </div>
            </div>
        </div>
        
        <div class="ds44-inner-container ds44-mtb3">
            <div class="ds44-grid12-offset-2">
                <h2 class="h2-like ds44-mt3"><trsb:glp key="PROFIL-ASSMAT-LIBELLE-DISPONIBILITE-HTML" ></trsb:glp></h2>
                <p>TODO</p>
                
                <h2 class="h2-like ds44-mt3"><%= glp("jcmsplugin.socle.titre.endetails") %></h2>
                <%--
                Agrément / Formation
                Type d'accueil / spécificités
                 --%>
                 
				<div class="grid-2-small-1">
				   <div class="col mrs ds44-mtb3">
                      <h2 class="h3-like" id="idTitre-list1"><trsb:glp key="PROFIL-ASSMAT-LIBELLE-AGREMENT-HTML" ></trsb:glp> :</h2>
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
                   </div>
                   <div class="col mls ds44-mtb3">
                      <h2 class="h3-like" id="idTitre-list2"><trsb:glp  key="PROFIL-ASSMAT-LIBELLE-FORMATION-HTML" ></trsb:glp> :</h2>
				      <trsb:glp  key="PROFIL-ASSMAT-CONTENT-FORMATION-HTML" ></trsb:glp>
                   </div>
                   
                   <div class="col mrs ds44-mtb3">
                      <jalios:if predicate='<%=
                                 (
                                    Util.notEmpty(obj.getAccueilPeriscolaire()) && "true".equalsIgnoreCase(obj.getAccueilPeriscolaire()) ||
                                    Util.notEmpty(obj.getAccueilMercredi()) && "true".equalsIgnoreCase(obj.getAccueilMercredi()) ||
                                    Util.notEmpty(obj.getAccueilPendantLesVacancesSco()) && "true".equalsIgnoreCase(obj.getAccueilPendantLesVacancesSco()) ||
                                    Util.notEmpty(obj.getHorairesAtypiques()) && "true".equalsIgnoreCase(obj.getHorairesAtypiques())
                                 )%>'>
                         <h2 class="h3-like" id="idTitre-list1"><trsb:glp key="PROFIL-ASSMAT-LIBELLE-TYPE-ACCUEIL-HTML" ></trsb:glp> :</h2>
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
                   </div>
                   <jalios:if predicate="<%= logementAccessible && accueilTmpPartiel && accueilAccepteRemplacement %>">
	                   <div class="col mls ds44-mtb3">
	                      <h2 class="h3-like" id="idTitre-list2"><trsb:glp key="PROFIL-ASSMAT-LIBELLE-SPECIFICITE-HTML" ></trsb:glp> :</h2>
	                      <jalios:if predicate='<%= logementAccessible %>'>
	                        <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-handicap-moteur ds44-docListIco" aria-hidden="true"></i><trsb:glp key="PROFIL-ASSMAT-CONTENT-SPECIFICITE-ACCESSIBLE" /></p>
					      </jalios:if>
					      <jalios:if predicate='<%= accueilTmpPartiel %>'>
					        <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-time ds44-docListIco" aria-hidden="true"></i><trsb:glp key="PROFIL-ASSMAT-CONTENT-SPECIFICITE-PARTIEL" /></p>
					      </jalios:if>
					      <jalios:if predicate='<%= accueilAccepteRemplacement %>'>
					        <p class="ds44-docListElem ds44-mt-std"><i class="icon icon-user ds44-docListIco" aria-hidden="true"></i><trsb:glp key="PROFIL-ASSMAT-CONTENT-SPECIFICITE-REMPLACEMENT" /></p>
					      </jalios:if>
	                   </div>
                   </jalios:if>
				</div>
                
            </div>    
        </div>
    
    </article>
    
</main>
