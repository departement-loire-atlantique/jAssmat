
<%@page import="fr.cg44.plugin.assmat.beans.DispoAssmat"%>
<%@page import="fr.cg44.plugin.assmat.selector.RelaisMamSelectorIDSolis"%>
<%@page import="net.sf.ehcache.CacheManager"%>
<%@page import="fr.cg44.plugin.assmat.comparator.AssmatSearchDistanceComparator"%>
<%@page import="fr.cg44.plugin.assmat.selector.RelaisMamSelectorCommune"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@page import="fr.cg44.plugin.assmat.PointAssmat"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil.SelectionEtatDispo"%>
<%@page import="fr.cg44.plugin.assmat.beans.AssmatSearch"%>
<%@page import="fr.cg44.plugin.assmat.managers.AssmatSearchDAO"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.tools.facetedsearch.manager.CityManager"%>
<%@ include file='/jcore/doInitPage.jsp'%>
<% jcmsContext.addJavaScript("https://api.tiles.mapbox.com/mapbox-gl-js/v0.49.0/mapbox-gl.js"); %>
<%
//Si la recherche n'a pas été lancer, on affiche l'accueil de la recherche
if(Util.isEmpty(getUntrustedStringParameter("isSearch", null))){ %>

  <%@ include file='/plugins/AssmatPlugin/jsp/recherche/accueilRecherche.jsp'%>

<%}else{ %>

<%@ include file='/plugins/AssmatPlugin/jsp/recherche/parameters.jspf'%>
<!-- A METTRE DANS RESULTSEARCH -->

<%

SimpleDateFormat formaterDispoFutur = new SimpleDateFormat("MMMM yyyy"); 
String dateRechercheMoisAnnee = formaterDispoFutur.format(dateTime);

HashMap hashMapAgeDate = new HashMap();


// Si la recherche porte sur une date dans le future
Date currentDate = new Date();
Boolean isPresentSearch = dateTime.getMonth() == currentDate.getMonth() && dateTime.getYear() == currentDate.getYear();

// Si le visteur est un RAM
Boolean isRam = AssmatUtil.getMemberIsRam(loggedMember);
Boolean isContribPower = AssmatUtil.getMemberIsContribPower(loggedMember);

// Set des Assmat avec dispo
Set<AssmatSearch> resultSetDispo = new HashSet<AssmatSearch>();
resultSetDispo = AssmatSearchDAO.getResultSearchDispo(commune, codeInsee,adresse, codesQuartiers, codesMicroQuartiers, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, tabTrancheAge, dateTime, distance, geoLong, geoLat);
logger.warn("resultSetDispo : "+resultSetDispo.size());

// Set des Assmat avec dispo future
Set<AssmatSearch> resultSetDispoFutur = new HashSet<AssmatSearch>();
resultSetDispoFutur = AssmatSearchDAO.getResultSearchFutur(commune, codeInsee, adresse, codesQuartiers, codesMicroQuartiers, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, tabTrancheAge, dateTime, distance, geoLong, geoLat);
logger.warn("resultSetDispoFutur : "+resultSetDispoFutur.size());

// Set des Assmat non dispo
Set<AssmatSearch> resultSetNonDispoContact= new HashSet<AssmatSearch>();
resultSetNonDispoContact = AssmatSearchDAO.getResultSearchNonDispos(commune, codeInsee, adresse, codesQuartiers, codesMicroQuartiers, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, tabTrancheAge, dateTime, distance, geoLong, geoLat);
logger.warn("resultSetNonDispoContact : "+resultSetNonDispoContact.size());

//Set des Assmat avec dispo non renseignées (seulement pour les RAM)
Set<AssmatSearch> resultSetDispoNonRenseigne = new HashSet<AssmatSearch>();
if(isRam || isContribPower) {
  resultSetDispoNonRenseigne = AssmatSearchDAO.getResultSearchDispoNonRenseigne(commune, codeInsee, adresse, codesQuartiers, codesMicroQuartiers, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, tabTrancheAge, dateTime, distance, geoLong, geoLat);
  logger.warn("resultSetDispoNonRenseigne : "+resultSetDispoNonRenseigne.size());
}

// La date de dispo futur la plus PROCHE (dans le future) pour chaque assmat
Map<Long, Date> resultDateDispoFutur = AssmatSearchDAO.getResultDispoFutur(commune, codeInsee, adresse, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, tabTrancheAge, dateTime, distance, geoLong, geoLat);
logger.warn("resultDateDispoFutur : "+resultDateDispoFutur.size());

// Liste des dispos pour les dispos de la recherche par assmat
Map<Long, Set<DispoAssmat>> resultDispoRechercheMap = AssmatSearchDAO.getResultDispo(commune, codeInsee, adresse, codesQuartiers, codesMicroQuartiers, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, tabTrancheAge, dateTime, distance, geoLong, geoLat, 1);
logger.warn("resultDispoRechercheMap : "+resultDispoRechercheMap.size());
session.setAttribute("resultDispoRechercheMap", resultDispoRechercheMap);

// Liste des dispos pour les dispos futures par assmat
Map<Long, Set<DispoAssmat>> resultDispoFuturMap = AssmatSearchDAO.getResultDispo(commune, codeInsee, adresse, codesQuartiers, codesMicroQuartiers, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, tabTrancheAge, dateTime, distance, geoLong, geoLat, 2);
logger.warn("resultDispoFuturMap : "+resultDispoFuturMap.size());
session.setAttribute("resultDispoFuturMap", resultDispoFuturMap);


// Coordonnées géographiques
PointAssmat pointGeo = null;
if(geoLong!=0.0 && geoLat!=0.0){
 pointGeo = new PointAssmat(latitude , longitude,"user");
 request.setAttribute("userLocation", pointGeo);
 session.setAttribute("userLocation", pointGeo);
}


//Map liant une assmatSearch avec un point (couleur ect ..)
Map<AssmatSearch,PointAssmat> assmatPoints = new HashMap<AssmatSearch,PointAssmat>();
TreeMap<AssmatSearch,PointAssmat> assmatPointsTriee = new TreeMap<AssmatSearch,PointAssmat>(new AssmatSearchDistanceComparator(pointGeo, assmatPoints, hashKey, isPresentSearch, resultDispoRechercheMap));
logger.warn("assmatPointsTriee : "+assmatPointsTriee.size());

// Retire les assmat déja présente dans les liste de résultats précédantes
resultSetDispoNonRenseigne.removeAll(resultSetDispo);
resultSetDispoNonRenseigne.removeAll(resultSetDispoFutur);
resultSetDispoNonRenseigne.removeAll(resultSetNonDispoContact);
resultSetDispoFutur.removeAll(resultSetDispo);
resultSetNonDispoContact.removeAll(resultSetDispoFutur);
resultSetNonDispoContact.removeAll(resultSetDispo);

// Retire les assmats ne souhaitant pas apparaitre sur le site
resultSetDispo = AssmatUtil.removeAssmatNonvisible(resultSetDispo);
resultSetDispoFutur = AssmatUtil.removeAssmatNonvisible(resultSetDispoFutur);
resultSetNonDispoContact = AssmatUtil.removeAssmatNonvisible(resultSetNonDispoContact);
resultSetDispoNonRenseigne = AssmatUtil.removeAssmatNonvisible(resultSetDispoNonRenseigne);

int nbAssmatTotal = resultSetDispo.size() + resultSetDispoFutur.size() + resultSetNonDispoContact.size() + resultSetDispoNonRenseigne.size();
logger.warn("resultSetDispo : "+resultSetDispo.size());
logger.warn("resultSetDispoFutur : "+resultSetDispoFutur.size());
logger.warn("resultSetNonDispoContact : "+resultSetNonDispoContact.size());
logger.warn("resultSetDispoNonRenseigne : "+resultSetDispoNonRenseigne.size());

ProfilASSMAT profilAMsearch = null;
PointAssmat pointAssmat = null;
SolisManager solisMgr = SolisManager.getInstance();
AssmatSolis assmatSolis = null;

Set<Publication> setProfilAM = new TreeSet<Publication>();


//Les assmat dispo
if(withDispo){
  for(AssmatSearch amSearch :resultSetDispo ){ 
	  logger.warn("amSearch  / "+amSearch.getNomAssmat());
    profilAMsearch = (ProfilASSMAT) channel.getPublication(amSearch.getDbprofilId());
    if(Util.notEmpty(profilAMsearch)){      
      if(profilAMsearch.getVisibiliteSite()){
        boolean isDomicile= amSearch.getIsDomicile();            
        // Quand consolidation pas bonne des latitude longitude, alors 0 0
        double geoLatitude = 0;
        double geoLongitude = 0;
        if(Util.notEmpty(amSearch.getLatitude())){
          geoLatitude = amSearch.getLatitude();
        }
        if(Util.notEmpty(amSearch.getLongitude())){
          geoLongitude = amSearch.getLongitude();
        }
        
       
        // la date la plus éloignée dans le temp si dispo dans a etat future
        Set<DispoAssmat> dispoList = resultDispoRechercheMap.get(amSearch.getJRowId());
        
        Date dispoDerniereDate = null;
        
        for(DispoAssmat itDispo : dispoList) {
          if( "2".equals(itDispo.getDisponible()) && itDispo.getDateDebut() != null ){
            Date itDate = itDispo.getDateDebut();
            if( itDate.after(new Date()) && ( dispoDerniereDate == null || itDate.getTime() > dispoDerniereDate.getTime()) ){
              dispoDerniereDate = itDate;
            }
          }
        }
        
        // 0012201 : Si recherche dans le future et que la dispo est une dispo future et que la date de de la dispo est après la date courante
        if( isPresentSearch || dispoDerniereDate == null){
          assmatPoints.put(amSearch, new PointAssmat(String.valueOf(geoLatitude),String.valueOf(geoLongitude), channel.getProperty("jcmsplugin.assmatplugin.recherche.am.result.recap.avec.dispo.popup.color"), glp("jcmsplugin.assmatplugin.recherche.am.result.recap.avec.dispo.popup"), 0, isDomicile ) );
        } else {    
          String dispoDate = formaterDispoFutur.format(dispoDerniereDate);   
          assmatPoints.put(amSearch, new PointAssmat(String.valueOf(geoLatitude),String.valueOf(geoLongitude), channel.getProperty("jcmsplugin.assmatplugin.recherche.am.result.recap.avec.dispo.popup.color"), glp("jcmsplugin.assmatplugin.recherche.am.result.recap.futur.dispo.popup", dispoDate), 0, isDomicile ) );      
        }
        
      }
    }
  }
}


//Les assmat dispo futur
if(withDispoFuture){
  for(AssmatSearch amSearch :resultSetDispoFutur ){ 
    profilAMsearch = (ProfilASSMAT) channel.getPublication(amSearch.getDbprofilId());
    if(Util.notEmpty(profilAMsearch)){            
			if(profilAMsearch.getVisibiliteSite()){
			    boolean isDomicile= amSearch.getIsDomicile();			    
			    // Quand consolidation pas bonne des latitude longitude, alors 0 0
			    double geoLatitude = 0;
			    double geoLongitude = 0;
			    if(Util.notEmpty(amSearch.getLatitude())){
			        geoLatitude = amSearch.getLatitude();
			    }
			    if(Util.notEmpty(amSearch.getLongitude())){
			        geoLongitude = amSearch.getLongitude();
			    }			   
			    // Récupère la date de la premiere dispo dans le future pour cette assmat
			    Date dateAMDispoFutur = resultDateDispoFutur.get(amSearch.getJRowId());             
			    String dateDispoFuturMoisAnnee = formaterDispoFutur.format(dateAMDispoFutur);			    			    
			    PointAssmat pointAssmatFuture = new PointAssmat(String.valueOf(geoLatitude), String.valueOf(geoLongitude), channel.getProperty("jcmsplugin.assmatplugin.recherche.am.result.recap.avec.dispo.futur.popup.color"), glp("jcmsplugin.assmatplugin.recherche.am.result.recap.futur.dispo.popup", dateDispoFuturMoisAnnee), 1, isDomicile);
			    pointAssmatFuture.setFirstFuturDate(dateAMDispoFutur);			    
			    assmatPoints.put( amSearch, pointAssmatFuture);
			}
    }
  }
}


//Les assmat non dispo
if(withNonDispo){
  for(AssmatSearch amSearch :resultSetNonDispoContact ){ 
	  profilAMsearch =(ProfilASSMAT) channel.getPublication(amSearch.getDbprofilId());
	  if(Util.notEmpty(profilAMsearch)){ 
	    if(profilAMsearch.getVisibiliteSite()){
	      boolean isDomicile= amSearch.getIsDomicile();	      
	      // Quand consolidation pas bonne des latitude longitude, alors 0 0
	      double geoLatitude = 0;
	      double geoLongitude = 0;
	      if(Util.notEmpty(amSearch.getLatitude())){
	          geoLatitude = amSearch.getLatitude();
	      }
	      if(Util.notEmpty(amSearch.getLongitude())){
	          geoLongitude = amSearch.getLongitude();
	      }      
	      Integer etatDispo = profilAMsearch.getAfficherContactUniquementSiD() ? 3 : 2;     
	      String nonDispoLabel = profilAMsearch.getAfficherContactUniquementSiD() ? glp("jcmsplugin.assmatplugin.recherche.am.result.recap.am.non.contact.popup") : glp("jcmsplugin.assmatplugin.recherche.am.result.recap.am.contact.popup");
	      assmatPoints.put( amSearch, new PointAssmat(String.valueOf(geoLatitude),String.valueOf(geoLongitude), channel.getProperty("jcmsplugin.assmatplugin.recherche.am.result.recap.am.contact.popup.color"), nonDispoLabel, etatDispo, isDomicile) );
	    }    
	  }
	}
}


//Les assmat dispo non renseigné
if(withDispoNonRenseigne){
	for(AssmatSearch amSearch :resultSetDispoNonRenseigne ){ 
	  profilAMsearch = (ProfilASSMAT) channel.getPublication(amSearch.getDbprofilId());
	  if(Util.notEmpty(profilAMsearch)){            
       if(profilAMsearch.getVisibiliteSite()){
         boolean isDomicile= amSearch.getIsDomicile();         
         // Quand consolidation pas bonne des latitude longitude, alors 0 0
         double geoLatitude = 0;
         double geoLongitude = 0;
         if(Util.notEmpty(amSearch.getLatitude())){
             geoLatitude = amSearch.getLatitude();
         }
         if(Util.notEmpty(amSearch.getLongitude())){
             geoLongitude = amSearch.getLongitude();
         }         
       assmatPoints.put( amSearch, new PointAssmat(String.valueOf(geoLatitude), String.valueOf(geoLongitude), channel.getProperty("jcmsplugin.assmatplugin.recherche.am.result.recap.avec.dispo.non.renseigne.popup.color"), glp("jcmsplugin.assmatplugin.recherche.am.result.recap.avec.dispo.non.renseigne.popup"), 4,isDomicile) );
     }      
	  }
	}
}

assmatPointsTriee.putAll(assmatPoints);

session.removeAttribute("isSelection"); 
session.setAttribute("urlRecherche", ServletUtil.getUrl(request));
session.setAttribute("assmatPoints", assmatPointsTriee);
if(loggedMember != null){
  session.setAttribute("logMemberId", loggedMember.getId());
}
session.setAttribute("paramsMap", request.getParameterMap());
request.setAttribute("assmatPoints", assmatPointsTriee);
%>

<%@ page contentType="text/html; charset=UTF-8"%>


<%
jcmsContext.addJavaScript("plugins/AssmatPlugin/js/plugin.js");
%>


<!-- RESULATS DE RECHERCHE -->

<div class="presentationSearch">
<div class="span7 introRecherche introResult">

<p class="titleResultatRecherche"><%= glp("jcmsplugin.assmatplugin.recherche.am.result.title.1") %><br/><%= glp("jcmsplugin.assmatplugin.recherche.am.result.title.2",nbAssmatTotal) %></p>

<div class="nbResultDispo">


<jalios:if predicate="<%= Util.notEmpty(resultSetDispo) || Util.notEmpty(resultSetDispoFutur) %>">
	<ul class="unstyled categories" style="border-bottom: dotted 2px #000000; padding-bottom: 15px;">
	<%if(Util.notEmpty(resultSetDispo)){ %>
	<%
	String[] parameterDispo = new String[]{resultSetDispo.size()+"", dateRechercheMoisAnnee};
	%>
	
	 <li class="">
	    <input class="noSubmitEven" type="checkbox"  name="avecDispo" <%if(withDispo){ %>checked  <%}%> id="avecDispo" onChange="jQuery.plugin.AssmatPlugin.changeBoxDispo(this, this.checked)" class="false-input"> 
	    <label for="avecDispo" class="checkbox-label checkbox-dispo"><trsb:glp key="RECHERCHE-AM-FILTER-WITH-DISPO" parameter="<%= parameterDispo %>" /></label><img class="iconeMarqueurAccueil" src ="plugins/ToolsPlugin/images/googlemaps/markers/drop/dropActive_aec900.png" />
	 </li>
	 <%} %>
	 <%if(Util.notEmpty(resultSetDispoFutur)){ %>
	  <%
	  String[] parameterDispoFuture = new String[]{resultSetDispoFutur.size()+""};
	  %>  
	  <li class="dispoFutureAM">
	     <input class="noSubmitEven" type="checkbox" name="withDispoFuture" <%if(withDispoFuture){ %>checked  <%}%> onChange="jQuery.plugin.AssmatPlugin.changeBoxDispo(this,this.checked)" id="futureDispo" class="false-input"> 
	     <label for="futureDispo" class="checkbox-label checkbox-dispo"><trsb:glp key="RECHERCHE-AM-FILTER-WITH-DISPO-FUTUR" parameter="<%= parameterDispoFuture %>" /></label> <img class="iconeMarqueurAccueil" src ="plugins/ToolsPlugin/images/googlemaps/markers/drop/drop_f78b00.png" />
	  </li>
	
	 <%} %>
	</ul>
</jalios:if> 
   
  <%if(Util.notEmpty(resultSetNonDispoContact)){ %>
  <%
  String[] parameterNonDispoContact = new String[]{resultSetNonDispoContact.size()+""};
  %> 
  <ul class="unstyled categories"> 
	  <li class="nonDispoAM">  
	    <%-- Par défaut la case 'sans disponibilité' n'est pas coché, comme placé avant l'input n'est pas priritaire sur celui-ci une fois un choix fait --%>
	    <input class="noSubmitEven" type="hidden" name="withNonDispo" value="false">
	  
	    <b><p class="voirAussi">Voir aussi</p></b> 
	    <input class="noSubmitEven" type="checkbox" name="withNonDispo" <%if(withNonDispo){ %>checked  <%}%> id="nonDispo" onChange="jQuery.plugin.AssmatPlugin.changeBoxDispo(this,this.checked)" class="false-input"> 
	    <label for="nonDispo" class="checkbox-label checkbox-dispo"><trsb:glp key="RECHERCHE-AM-FILTER-NO-DISPO-CONTACT" parameter="<%= parameterNonDispoContact %>" /></label> <img class="iconeMarqueurAccueil" src ="plugins/ToolsPlugin/images/googlemaps/markers/drop/dropActive_bebebe.png" />
	  </li> 
	</ul>
   <%} %>
     
  <%if(Util.notEmpty(resultSetDispoNonRenseigne) && (isRam || isContribPower)){ %>
	  <%
	  String[] parameterDispoNonRenseigne = new String[]{resultSetDispoNonRenseigne.size()+""};
	  %> 
		<ul class="unstyled categories">  
		  <li class="nonDispoRenseignee">
		     <input class="noSubmitEven" type="checkbox" name="withDispoNonRenseigne" <%if(withDispoNonRenseigne){ %>checked  <%}%> onChange="jQuery.plugin.AssmatPlugin.changeBoxDispo(this,this.checked)" id="nonRenseigne" class="false-input"> 
		     <label for="nonRenseigne" class="checkbox-label"><trsb:glp key="RECHERCHE-AM-FILTER-WITH-DISPO-NON-RENSEIGNEES" parameter="<%= parameterDispoNonRenseigne %>" /></label> <img class="iconeMarqueurAccueil" src ="plugins/ToolsPlugin/images/googlemaps/markers/drop/drop_bebebe.png" />
		  </li>
		</ul>
  <%} %>
  
 

</div>
<div style="clear:both"></div>
<p class="noteIntroRecherche"><trsb:glp key="INFORMATION-RESULTAT-RECHERCHE-AM" /></p> 
  
</div>

<div class="span5">

<%
// 0011728: Affichage relais dans recherche AM sur Nantes 
Set<FicheLieu> selectPlace = new HashSet<FicheLieu>();
if(Util.notEmpty(adresse) && Util.notEmpty(assmatPointsTriee.entrySet())) {
  AssmatSearch firstAssmatSeach = Util.getFirst(assmatPointsTriee.entrySet()).getKey();  
  ProfilASSMAT profilAM = (ProfilASSMAT) channel.getPublication(firstAssmatSeach.getDbprofilId()) ; 
  AssmatSolis asmmatSolis = Util.getFirst(solisMgr.getAssmatSolisByNumAgrement(profilAM.getNum_agrement()));  
  String idRam = "RAM_" + (firstAssmatSeach.getIsDomicile() ? asmmatSolis.getIdRam() : asmmatSolis.getIdRamMam()); 
  selectFicheLieu.add((FicheLieu) Util.getFirst(JcmsUtil.applyDataSelector(channel.getAllDataSet(FicheLieu.class), new RelaisMamSelectorIDSolis(idRam))));
}


// 0012395: Recherche par quartier Nantes - affichage relais  
if( (44109 == codeInsee) &&  ( (-10 ==  distance && Util.notEmpty(listeIdQuartiers))  || (-20 ==  distance && Util.notEmpty(listeIdMicroQuartiers)) ) ) {
  // Les relais par rapport au quartier recherché
  selectPlace = new HashSet<FicheLieu>();
  // La liste des id relais recherché
  List<String> listeIdRechercheQuartiers =  new ArrayList<String>();      
  // Liste Micro Quartier (récupère le quartier)
  if(-20 ==  distance && Util.notEmpty(listeIdMicroQuartiers)){
    for(String itIdMicroQuartier : listeIdMicroQuartiers) {
      listeIdRechercheQuartiers.add(itIdMicroQuartier.substring(0, 7));
    }
  }  
  // Liste de quartier 
  if( -10 == distance && Util.notEmpty(listeIdQuartiers) ) {
    listeIdRechercheQuartiers.addAll(listeIdQuartiers);
  }    
  Category quartiersNantesRootCat = channel.getCategory("p1_67469");
  for(Category itQuartierCat : quartiersNantesRootCat.getChildrenSet()) {
    String itIdQuartier = itQuartierCat.getExtraData("extra.Category.jcmsplugin.assmatplugin.search.mapping");
    if(Util.notEmpty(itIdQuartier) && listeIdRechercheQuartiers.contains(itIdQuartier)) {
      selectPlace.addAll( itQuartierCat.getPublicationSet(Place.class, loggedMember));
    }
  }  
}


%>

<%@ include file='/plugins/AssmatPlugin/jsp/recherche/relaisMam.jspf'%>

<!-- FIN -->
</div>
</div>

 <div class="carto">
   <% request.setAttribute("gMapsType", "large"); %>
                    
	<link href='https://api.tiles.mapbox.com/mapbox-gl-js/v0.45.0/mapbox-gl.css' rel='stylesheet' />
	
	<style>
		#osMap { height:600px; }
	    .mapboxgl-popup {
	        max-width: 400px;
	        line-height: 1.2em;
	        }
	</style>
   
    <div id='osMap'></div>
   
   <jalios:javascript>
  <jsp:include page="/plugins/AssmatPlugin/jsp/recherche/map/globalMapAssmatDisplay.jsp"  flush="true"/>
  </jalios:javascript>
                  <% request.removeAttribute("gMapsType"); %>          
</div>


<jalios:pager name="assmatPager"  declare='true' pageSize="10"
  action="init" size="<%=assmatPointsTriee.size()%>" linkCss=""/>
<%
ProfilManager profilMgr = ProfilManager.getInstance();
ProfilASSMAT profilAM = null;

jcmsContext.setTemplateUsage(TypeTemplateEntry.USAGE_DISPLAY_QUERY);
Set<Map.Entry<AssmatSearch,PointAssmat>> assmatResultSet = assmatPointsTriee.entrySet();
logger.warn("assmatResultSet : "+assmatResultSet.size());

%>
 <jalios:foreach collection="<%= assmatResultSet %>" name="entry" type="Map.Entry<AssmatSearch,PointAssmat>"
            max='<%=assmatPager.getPageSize()%>'
            skip='<%=assmatPager.getStart()%>'>
<%

  profilAM = profilMgr.getProfilASSMATbyAssmatSearch(entry.getKey());
  if(Util.notEmpty(profilAM)){
      AssmatSearch search = entry.getKey();
      PointAssmat point = entry.getValue();
      setProfilAM.add(profilAM);
    request.setAttribute("assmatSearch", search);
    request.setAttribute("point", point);
 %> 
    
    <jalios:include pub="<%= profilAM%>"/>
 <% request.removeAttribute("assmatSearch");  request.removeAttribute("point");
  }

%>

  </jalios:foreach>


 <% session.setAttribute("listeProfilAMSearch", assmatResultSet); %>
 
<jalios:pager name="assmatPager"  template="corporate" />
<%} %>
