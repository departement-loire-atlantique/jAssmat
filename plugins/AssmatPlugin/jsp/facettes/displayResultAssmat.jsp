<%@page import="fr.cg44.plugin.socle.Point"%>
<%@page import="fr.cg44.plugin.tools.maps.Geolocation"%>
<%@page import="com.google.gson.JsonElement"%>
<%@page import="com.google.gson.JsonParser"%>
<%@page import="fr.cg44.plugin.socle.ApiUtil"%>
<%@page import="fr.cg44.plugin.assmat.selector.RelaisMamSelectorCommune"%>
<%@page import="fr.trsb.cd44.solis.beans.AssmatSolis"%>
<%@page import="fr.trsb.cd44.solis.manager.SolisManager"%>
<%@page import="fr.cg44.plugin.assmat.comparator.AssmatSearchDistanceComparator"%>
<%@page import="fr.cg44.plugin.assmat.PointAssmat"%>
<%@page import="fr.cg44.plugin.assmat.beans.DispoAssmat"%>
<%@page import="fr.cg44.plugin.assmat.AssmatUtil"%>
<%@page import="fr.cg44.plugin.assmat.managers.ProfilManager"%>
<%@page import="fr.cg44.plugin.socle.SocleUtils"%>
<%@page import="fr.cg44.plugin.assmat.managers.AssmatSearchDAO"%>
<%@page import="fr.cg44.plugin.assmat.beans.AssmatSearch"%>
<%@page import="com.jalios.io.IOUtil"%><%
%><%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@page import="com.google.gson.JsonObject"%><%
%><%@page import="com.google.gson.JsonArray"%><%


request.setAttribute("inFO", Boolean.TRUE);

%><%@ include file='/jcore/doInitPage.jspf' %><%
%><%@ include file="/jcore/portal/doPortletParams.jspf" %><%
%><%@ taglib prefix="trsb" uri="/WEB-INF/plugins/AssmatPlugin/TagTRSBglp.tld"%><%


response.setContentType("application/json");

PortletRechercheFacettes  boxTmp = (PortletRechercheFacettes) (channel.getPublication(request.getParameter("boxId"))).clone();  
PortletRechercheFacettes box = new PortletRechercheFacettes(boxTmp);



// Pager
boolean hasPager = box.getPager();
Integer pager = getIntParameter("page", 1);
int maxResult = box.getMaxResults(); 




%><%

//Coordonnées géographiques (commune ou adresse)
String longitude = getUntrustedStringParameter("longitude","");
String latitude = getUntrustedStringParameter("latitude","");

// Commune : Code insee
//Integer codeInsee = getIntParameter("commune", 0);
String adressString = getStringParameter("adresse", "0", ".*");


if("aroundMe".equals(adressString)) {  
  JsonParser parserJson = new JsonParser();
  String jsonString = ApiUtil.getJsonObjectFromApi("https://api-adresse.data.gouv.fr/reverse/?lat=" + latitude + "&lon="+ longitude, null).toString();
  JsonElement json =   parserJson.parse(jsonString); 
  adressString = json.getAsJsonObject().get("features").getAsJsonArray().get(0).getAsJsonObject().get("properties").getAsJsonObject().get("citycode").getAsString();
}


String codeInseeString = adressString.substring(0, 5);
Integer codeInsee =  Integer.parseInt(codeInseeString);

// A partir de
long time = getLongParameter("mois", new Date().getTime());
Date dateTime = new Date(time);


// Adresse 
String adresse = "";
if(Util.notEmpty(getUntrustedStringParameter("adresse",null))){
  adresse = getUntrustedStringParameter("adresse","");
}

// Rayon (Distance)
int distance = getIntParameter("rayon", 0);


//QUARTIERS
List<String> listeIdQuartiers =  new ArrayList<String>();
String[] codesQuartiers = getStringParameterValues("quartier", ".*");
if (Util.notEmpty(codesQuartiers)) {
    listeIdQuartiers.addAll(Arrays.asList(codesQuartiers));
}

//MICRO QUARTIERS
// List<String> listeIdMicroQuartiers =  new ArrayList<String>();
// String[] codesMicroQuartiers = getStringParameterValues("microQuartier", ".*");
// if (Util.notEmpty(codesMicroQuartiers)) {
//     listeIdMicroQuartiers.addAll(Arrays.asList(codesMicroQuartiers));
// }
    
    
// Age de l'enfant
String trancheAgeKey = getUntrustedStringParameter("age", null);
String[] tabTrancheAge = Util.decodeStringArray(trancheAgeKey);


// LIEU D'EXERCICE / SPECIFICITE /  TYPE D'ACCUEIl
String[] cid = getStringParameterValues("cids", ".*");
HashSet<Category> categorySet = new HashSet<Category>();
categorySet.addAll(AssmatUtil.getCategorySetFromIds(cid));

HashMap<String, Boolean> hashMapBooleanChamps = new HashMap<String, Boolean>();
hashMapBooleanChamps = AssmatUtil.getHashMapFromSelectedCategories(categorySet);



// Checkbox selon les choix de l'utilisateur (seulement les dispos, les non renseignées ect ..)
String[] dispoTab = request.getParameterValues("dispo");

boolean withDispo = false;
boolean withDispoFuture = false;
boolean withNonDispo = false;
boolean withDispoNonRenseigne = false;

if(Util.notEmpty(dispoTab)) {
  for(String itDispo : dispoTab) {
    if("withDispo".equals(itDispo)) {
      withDispo = true;
    }else if("withDispoFuture".equals(itDispo)) {
      withDispoFuture = true;
    }else if("withNonDispo".equals(itDispo)) {
      withNonDispo = true;
    }else if("withDispoNonRenseigne".equals(itDispo)) {
      withDispoNonRenseigne = true;
    }
  }
  // Si aucune dispo de cochée alors tout cocher
} else {
  withDispo = true;
  withDispoFuture = true;
  withNonDispo = true;
  withDispoNonRenseigne = true;
}



//Criteres de recherche
boolean lieuDomicile = Util.notEmpty(hashMapBooleanChamps.get("lieuDomicile")) ? hashMapBooleanChamps.get("lieuDomicile") : false;
boolean lieuMam = Util.notEmpty(hashMapBooleanChamps.get("lieuMam")) ? hashMapBooleanChamps.get("lieuMam") : false;
boolean accueilPerisco = Util.notEmpty(hashMapBooleanChamps.get("accueilPeriscolaire")) ? hashMapBooleanChamps.get("accueilPeriscolaire") : false;
boolean accueilMercredi = Util.notEmpty(hashMapBooleanChamps.get("accueilMercredi")) ? hashMapBooleanChamps.get("accueilMercredi") : false;
boolean accueilVacances = Util.notEmpty(hashMapBooleanChamps.get("accueilVacances")) ? hashMapBooleanChamps.get("accueilVacances") : false;
boolean accueilAvant7h = Util.notEmpty(hashMapBooleanChamps.get("accueilAvant7h")) ? hashMapBooleanChamps.get("accueilAvant7h") : false;
boolean accueilApres20h = Util.notEmpty(hashMapBooleanChamps.get("accueilApres20h")) ? hashMapBooleanChamps.get("accueilApres20h") : false;
boolean accueilSamedi = Util.notEmpty(hashMapBooleanChamps.get("accueilSamedi")) ? hashMapBooleanChamps.get("accueilSamedi") : false;
boolean accueilDimanche = Util.notEmpty(hashMapBooleanChamps.get("accueilDimanche")) ? hashMapBooleanChamps.get("accueilDimanche") : false;
boolean accueilNuit = Util.notEmpty(hashMapBooleanChamps.get("accueilNuit")) ? hashMapBooleanChamps.get("accueilNuit") : false;
boolean specHandicape = Util.notEmpty(hashMapBooleanChamps.get("specHandicape")) ? hashMapBooleanChamps.get("specHandicape") : false;
boolean specPartiel = Util.notEmpty(hashMapBooleanChamps.get("specPartiel")) ? hashMapBooleanChamps.get("specPartiel") : false;
boolean specDepannages = Util.notEmpty(hashMapBooleanChamps.get("specDepannages")) ? hashMapBooleanChamps.get("specDepannages") : false;
boolean accueilAtypique = Util.notEmpty(hashMapBooleanChamps.get("accueilAtypique")) ? hashMapBooleanChamps.get("accueilAtypique") : false;





double geoLong = 0.0;
double geoLat = 0.0;


if ((distance>0 || Util.notEmpty(adresse)) && Util.notEmpty(longitude) && Util.notEmpty(latitude) && (adresse.length() > 5 || distance > 0  )) {
  geoLong = Double.parseDouble(longitude);
  geoLat = Double.parseDouble(latitude);
}



String nomAssmat = getUntrustedStringParameter("nomassmat", "");

%>



<%

SimpleDateFormat formaterDispoFutur = new SimpleDateFormat("MMMM yyyy"); 
String dateRechercheMoisAnnee = formaterDispoFutur.format(dateTime);



// Si la recherche porte sur une date dans le future
Date currentDate = new Date();
Boolean isPresentSearch = dateTime.getMonth() == currentDate.getMonth() && dateTime.getYear() == currentDate.getYear();


// Si le visteur est un RAM
Boolean isRam = AssmatUtil.getMemberIsRam(loggedMember);
Boolean isContribPower = AssmatUtil.getMemberIsContribPower(loggedMember);


// Set des Assmat avec dispo
Set<AssmatSearch> resultSetDispo = new HashSet<AssmatSearch>();
resultSetDispo = AssmatSearchDAO.getResultSearchDispo(null, codeInsee,null, codesQuartiers, null, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, tabTrancheAge, dateTime, distance, geoLong, geoLat);
//resultSetDispo = AssmatSearchDAO.getResultSearchDispo(commune, codeInsee,adresse, codesQuartiers, codesMicroQuartiers, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, tabTrancheAge, dateTime, distance, geoLong, geoLat);
logger.trace("resultSetDispo : " + resultSetDispo.size());


// Set des Assmat avec dispo future
Set<AssmatSearch> resultSetDispoFutur = new HashSet<AssmatSearch>();
resultSetDispoFutur = AssmatSearchDAO.getResultSearchFutur(null, codeInsee,null, codesQuartiers, null, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, tabTrancheAge, dateTime, distance, geoLong, geoLat);
logger.trace("resultSetDispoFutur : " + resultSetDispoFutur.size());


//Set des Assmat non dispo
Set<AssmatSearch> resultSetNonDispoContact = new HashSet<AssmatSearch>();
resultSetNonDispoContact = AssmatSearchDAO.getResultSearchNonDispos(null, codeInsee,null, codesQuartiers, null, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, tabTrancheAge, dateTime, distance, geoLong, geoLat);
logger.trace("resultSetNonDispoContact : " + resultSetNonDispoContact.size());



// Set des Assmat avec dispo non renseignées (seulement pour les RAM)
Set<AssmatSearch> resultSetDispoNonRenseigne = new HashSet<AssmatSearch>();
if(isRam || isContribPower) {
  resultSetDispoNonRenseigne = AssmatSearchDAO.getResultSearchDispoNonRenseigne(null, codeInsee, null, codesQuartiers, null, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, tabTrancheAge, dateTime, distance, geoLong, geoLat);
  logger.trace("resultSetDispoNonRenseigne : " + resultSetDispoNonRenseigne.size());
}


// La date de dispo futur la plus PROCHE (dans le future) pour chaque assmat
Map<Long, Date> resultDateDispoFutur = AssmatSearchDAO.getResultDispoFutur(null, codeInsee, null, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, tabTrancheAge, dateTime, distance, geoLong, geoLat);
logger.trace("resultDateDispoFutur : " + resultDateDispoFutur.size());



// Liste des dispos pour les dispos de la recherche par assmat
Map<Long, Set<DispoAssmat>> resultDispoRechercheMap = AssmatSearchDAO.getResultDispo(null, codeInsee, null, codesQuartiers, null, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, tabTrancheAge, dateTime, distance, geoLong, geoLat, 1);
logger.trace("resultDispoRechercheMap : "+resultDispoRechercheMap.size());
session.setAttribute("resultDispoRechercheMap", resultDispoRechercheMap);



// Liste des dispos pour les dispos futures par assmat
Map<Long, Set<DispoAssmat>> resultDispoFuturMap = AssmatSearchDAO.getResultDispo(null, codeInsee, null, codesQuartiers, null, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, tabTrancheAge, dateTime, distance, geoLong, geoLat, 2);
logger.trace("resultDispoFuturMap : "+resultDispoFuturMap.size());
session.setAttribute("resultDispoFuturMap", resultDispoFuturMap);


// Coordonnées géographiques
PointAssmat pointGeo = null;
if(geoLong!=0.0 && geoLat!=0.0){
 pointGeo = new PointAssmat(latitude , longitude, "user");
 request.setAttribute("userLocation", pointGeo);
 session.setAttribute("userLocation", pointGeo);
}



// Map liant une assmatSearch avec un point (couleur ect ..)
String hashKey = Integer.toString((int) (Math.random() * 1000) + 1);
Map<AssmatSearch,PointAssmat> assmatPoints = new HashMap<AssmatSearch,PointAssmat>();
TreeMap<AssmatSearch,PointAssmat> assmatPointsTriee = new TreeMap<AssmatSearch,PointAssmat>(new AssmatSearchDistanceComparator(pointGeo, assmatPoints, hashKey, isPresentSearch, resultDispoRechercheMap));
logger.trace("assmatPointsTriee : " + assmatPointsTriee.size());



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
logger.debug("resultSetDispo : " + resultSetDispo.size());
logger.debug("resultSetDispoFutur : " + resultSetDispoFutur.size());
logger.debug("resultSetNonDispoContact : " + resultSetNonDispoContact.size());
logger.debug("resultSetDispoNonRenseigne : " + resultSetDispoNonRenseigne.size());





ProfilASSMAT profilAMsearch = null;
PointAssmat pointAssmat = null;
SolisManager solisMgr = SolisManager.getInstance();
AssmatSolis assmatSolis = null;

Set<Publication> setProfilAM = new TreeSet<Publication>();




//Les assmat dispo
if(withDispo){
	for(AssmatSearch amSearch :resultSetDispo ){ 
// 	  logger.trace("amSearch  / "+amSearch.getNomAssmat());
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






// Calcul du relais MAM
String idCategMam = channel.getProperty("$plugin.assmatplugin.categ.relaiam");
Category categRAM= channel.getCategory(idCategMam); 
Set<FicheLieu> setPlace =(Set<FicheLieu>) JcmsUtil.applyDataSelector(channel.getAllDataSet(FicheLieu.class), new RelaisMamSelectorCommune(SocleUtils.getCommuneFromCode(codeInsee+""),categRAM));








logger.trace("Relais : " + setPlace);







// Mise en session pour l'export en PDF
session.removeAttribute("isSelection"); 
session.setAttribute("urlRecherche", ServletUtil.getUrl(request));
session.setAttribute("assmatPoints", assmatPointsTriee);
if(loggedMember != null){
  session.setAttribute("logMemberId", loggedMember.getId());
}

Map<String, String[]> paramsMap = new HashMap<String, String[]>();
paramsMap.putAll(request.getParameterMap());


paramsMap.put("cityName", new String[]{request.getParameter("commune[text]")});
paramsMap.put("adresse", new String[]{request.getParameter("adresse[text]")});
paramsMap.put("codeInsee", new String[]{request.getParameter("commune")});
paramsMap.put("distance", new String[]{request.getParameter("rayon")});
paramsMap.put("age", new String[]{getUntrustedStringParameter("age", null)});
paramsMap.put("month", new String[]{request.getParameter("mois")});




session.setAttribute("paramsMap", paramsMap);
request.setAttribute("assmatPoints", assmatPointsTriee);










// RESULATS DE RECHERCHE







Set<Map.Entry<AssmatSearch,PointAssmat>> assmatResultSet = assmatPointsTriee.entrySet();
logger.trace("assmatResultSet : "+assmatResultSet.size());



%>




<jalios:buffer name="assmatTuileResult"><%

    %><section class="ds44-card ds44-js-card ds44-card--contact ds44-box ds44-bgGray ds44-cardIsFocus">

        <div class="ds44-card__section">

            <div class="ds44-innerBoxContainer">
                <p role="heading" aria-level="2" class="h4-like ds44-cardTitle"><%= glp("jcmsplugin.assmatplugin.recherche.am.result.title.2", assmatResultSet.size()) %></p>

                <jalios:if predicate="<%= withDispo %>">
	                <p class="ds44-docListElem ds44-mt-std">
	                    <i class="icon icon-check ds44-docListIco" aria-hidden="true"></i><trsb:glp key="RECHERCHE-AM-FILTER-WITH-DISPO" parameter='<%= new String[]{resultSetDispo.size() + ""} %>'  />
	                </p>
                </jalios:if>
                <jalios:if predicate="<%= withDispoFuture %>">
	                <p class="ds44-docListElem ds44-mt-std">
	                    <i class="icon icon-time ds44-docListIco" aria-hidden="true"></i><trsb:glp key="RECHERCHE-AM-FILTER-WITH-DISPO-FUTUR" parameter='<%= new String[]{resultSetDispoFutur.size() + ""} %>' />
	                </p>
                </jalios:if>
                <jalios:if predicate="<%= withNonDispo %>">
	                <p class="ds44-docListElem ds44-mt-std">
	                    <i class="icon icon-visuel ds44-docListIco" aria-hidden="true"></i><trsb:glp key="RECHERCHE-AM-FILTER-NO-DISPO-CONTACT" parameter='<%= new String[]{resultSetNonDispoContact.size() + ""} %>' />
	                </p>
                </jalios:if>
                <jalios:if predicate="<%= withDispoNonRenseigne && (isRam || isContribPower) %>">
                    <p class="ds44-docListElem ds44-mt-std">
                        <i class="icon icon-visuel ds44-docListIco" aria-hidden="true"></i><trsb:glp key="RECHERCHE-AM-FILTER-WITH-DISPO-NON-RENSEIGNEES" parameter='<%= new String[]{resultSetDispoNonRenseigne.size() + ""} %>' />
                    </p>
                </jalios:if>
            </div>
 
        </div>
    </section><%

%></jalios:buffer>


<%



JsonArray jsonArray = new JsonArray();
JsonArray jsonArrayModal = new JsonArray();
JsonObject jsonObject = new JsonObject();



// Gestion du pager
if(hasPager) {
  jsonObject.addProperty("page-index", pager);
} else {
  jsonObject.addProperty("max-result", maxResult);
}

jsonObject.addProperty("html-nb-result", assmatTuileResult); 
jsonObject.addProperty("nb-result", assmatResultSet.size());
jsonObject.addProperty("nb-result-per-page", maxResult);


jsonObject.add("result", jsonArray);
jsonObject.add("result-modal", jsonArrayModal);


ProfilManager profilMgr = ProfilManager.getInstance();
ProfilASSMAT itProfilAM = null;



// Reprise de l'ancien site pour le regroupement des points trop proche en un seul point sur la carte


Map<Point,List<ProfilASSMAT>> publicationPoints = new HashMap<Point,List<ProfilASSMAT>>();

// Regroupe les points dans une map avec les profilAssmat correspondants
for (Map.Entry<AssmatSearch, PointAssmat> entry : assmatResultSet) {
  
  AssmatSearch assmat = entry.getKey();
  PointAssmat point = entry.getValue();
  
  if (point.getLatitude() == 0 && point.getLongitude() == 0) {
    continue;
  }
  
  ProfilASSMAT profilAM = profilMgr.getProfilASSMATbyAssmatSearch(assmat);
  if (Util.notEmpty(point)) {
    
    List<ProfilASSMAT> liste = new ArrayList<ProfilASSMAT>();
    
    if (publicationPoints.containsKey(point)) {
      liste = publicationPoints.get(point);
    }
    
    liste.add(profilAM);
    publicationPoints.put(point, liste);
    
  } 
}



List<Point> points = new ArrayList(publicationPoints.keySet());
ListIterator<Point> itListPoint = points.listIterator();
List<Point> generalClosenessPoints = new ArrayList<Point>();

// Regroupe les points proches
ProfilASSMAT profilAM;
Point point;
double jLongitude;
double jLatitude;



Map<Point, Point> closenessPointMap = new HashMap<Point, Point>(); 
while (itListPoint.hasNext()) {
  
  point = itListPoint.next();
  if (!generalClosenessPoints.contains(point)) {
      
    profilAM = publicationPoints.get(point).get(0);
    jLongitude = point.getLongitude();
    jLatitude = point.getLatitude();

    ListIterator<Point> itClosenessPoints = points.listIterator(itListPoint.nextIndex());    
    List<Point> closenessPoints = Geolocation.getClosenessPoints((Point) point, itClosenessPoints);
    
    if(Util.notEmpty(closenessPoints)) {
      generalClosenessPoints.addAll(closenessPoints);
      
      for(Point itSimpleClosenessPoints : closenessPoints) {
        closenessPointMap.put(itSimpleClosenessPoints, point);
      }    
    }   
  }  
}

// Fin de reprise regroupement des points proches

// Spécifique facettes - indicateur lien de retour

session.setAttribute("isSearchFacetLink", true);

%>


<jalios:foreach collection="<%= assmatResultSet %>" name="itEntry" type="Map.Entry<AssmatSearch, PointAssmat>" max='<%= maxResult %>' skip='<%= (pager - 1) * maxResult  %>'>


<%
itProfilAM = profilMgr.getProfilASSMATbyAssmatSearch(itEntry.getKey());
if(Util.notEmpty(itProfilAM)){
  AssmatSearch itSearch = itEntry.getKey();
  PointAssmat itPoint = itEntry.getValue();
  
  request.setAttribute("point", itPoint);
  
  
  
  %><jalios:buffer name="itPubListGabarit"><%  
      %><jalios:media data="<%= itProfilAM %>" /><%
  %>
  </jalios:buffer><%
     
  // Changement des coordonnée du point vers le point le plus proche (plusieurs points proches sont fusionné)
  String itLatitude = Double.toString(itPoint.getLatitude());
  String itLongitude = Double.toString(itPoint.getLongitude());
  if(closenessPointMap.containsKey(itPoint)) {
    itLatitude = Double.toString(closenessPointMap.get(itPoint).getLatitude());
    itLongitude = Double.toString(closenessPointMap.get(itPoint).getLongitude());
  }
      
      
  // Ajout du résultat au json
  jsonArray.add(SocleUtils.publicationToJsonObject(itProfilAM, itLatitude, itLongitude, itPoint.getCouleurPoint(), itPubListGabarit, itPubListGabarit, null));

}
%>

	
</jalios:foreach>


<jalios:foreach name="itRelais" type="FicheLieu" collection="<%= setPlace %>"><%

  %><jalios:buffer name="itPubListGabarit"><%  
      %><jalios:media data="<%= itRelais %>" /><%
  %>
  </jalios:buffer><%
  
  
 //Ajout du résultat modal au json 
 jsonArrayModal.add(SocleUtils.publicationToJsonObject(itRelais, null, null, null, itPubListGabarit, null, null));

%>  
</jalios:foreach>



<%



%><%= jsonObject %>
