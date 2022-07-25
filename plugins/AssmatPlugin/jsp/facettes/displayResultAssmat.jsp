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


response.setContentType("application/json");

PortletRechercheFacettes  boxTmp = (PortletRechercheFacettes) (channel.getPublication(request.getParameter("boxId"))).clone();  
PortletRechercheFacettes box = new PortletRechercheFacettes(boxTmp);



// Pager
boolean hasPager = box.getPager();
Integer pager = getIntParameter("page", 1);
int maxResult = box.getMaxResults(); 




%><%


// Commune : Code insee
Integer codeInsee = getIntParameter("commune", 0);

// A partir de
long time = getLongParameter("mois", -1);
Date dateTime = new Date(time);


// Adresse 
// Ne sert plus, seulement longitude latitude utilisé avec rayon.
String adresse = "";

// Rayon (Distance)
int distance = getIntParameter("rayon", 0);



// QUARTIERS et MICRO QUARTIERS
// TODO encore utilisé ?
    
    
// Age de l'enfant
// N'est plus utilisé
//String trancheAgeKey = getUntrustedStringParameter("age", "-1");
//String[] tabTrancheAge = Util.decodeStringArray(trancheAgeKey);


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
  // Si aucune dispo de cochée alors afficher avec diso et dispo future
} else {
  withDispo = true;
  withDispoFuture = true;
  withNonDispo = true;
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



// Coordonnées géographiques (commune ou adresse)
String longitude = getUntrustedStringParameter("longitude","");
String latitude = getUntrustedStringParameter("latitude","");

double geoLong = 0.0;
double geoLat = 0.0;


if ((distance>0 || Util.notEmpty(adresse)) && Util.notEmpty(longitude) && Util.notEmpty(latitude)) {
  geoLong = Double.parseDouble(longitude);
  geoLat = Double.parseDouble(latitude);
}


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
resultSetDispo = AssmatSearchDAO.getResultSearchDispo(null, codeInsee,null, null, null, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, null, null, dateTime, distance, geoLong, geoLat);
//resultSetDispo = AssmatSearchDAO.getResultSearchDispo(commune, codeInsee,adresse, codesQuartiers, codesMicroQuartiers, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, tabTrancheAge, dateTime, distance, geoLong, geoLat);
logger.warn("resultSetDispo : " + resultSetDispo.size());


// Set des Assmat avec dispo future
Set<AssmatSearch> resultSetDispoFutur = new HashSet<AssmatSearch>();
resultSetDispoFutur = AssmatSearchDAO.getResultSearchFutur(null, codeInsee,null, null, null, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, null, null, dateTime, distance, geoLong, geoLat);
logger.warn("resultSetDispoFutur : " + resultSetDispoFutur.size());


//Set des Assmat non dispo
Set<AssmatSearch> resultSetNonDispoContact = new HashSet<AssmatSearch>();
resultSetNonDispoContact = AssmatSearchDAO.getResultSearchNonDispos(null, codeInsee,null, null, null, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, null, null, dateTime, distance, geoLong, geoLat);
logger.warn("resultSetNonDispoContact : " + resultSetNonDispoContact.size());



// Set des Assmat avec dispo non renseignées (seulement pour les RAM)
Set<AssmatSearch> resultSetDispoNonRenseigne = new HashSet<AssmatSearch>();
if(isRam || isContribPower) {
  resultSetDispoNonRenseigne = AssmatSearchDAO.getResultSearchDispoNonRenseigne(null, codeInsee, null, null, null, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, null, null, dateTime, distance, geoLong, geoLat);
  logger.warn("resultSetDispoNonRenseigne : " + resultSetDispoNonRenseigne.size());
}


// La date de dispo futur la plus PROCHE (dans le future) pour chaque assmat
Map<Long, Date> resultDateDispoFutur = AssmatSearchDAO.getResultDispoFutur(null, codeInsee, null, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, null, null, dateTime, distance, geoLong, geoLat);
logger.warn("resultDateDispoFutur : " + resultDateDispoFutur.size());



// Liste des dispos pour les dispos de la recherche par assmat
Map<Long, Set<DispoAssmat>> resultDispoRechercheMap = AssmatSearchDAO.getResultDispo(null, codeInsee, null, null, null, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, null, null, dateTime, distance, geoLong, geoLat, 1);
logger.warn("resultDispoRechercheMap : "+resultDispoRechercheMap.size());
session.setAttribute("resultDispoRechercheMap", resultDispoRechercheMap);



// Liste des dispos pour les dispos futures par assmat
Map<Long, Set<DispoAssmat>> resultDispoFuturMap = AssmatSearchDAO.getResultDispo(null, codeInsee, null, null, null, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, null, null, dateTime, distance, geoLong, geoLat, 2);
logger.warn("resultDispoFuturMap : "+resultDispoFuturMap.size());
session.setAttribute("resultDispoFuturMap", resultDispoFuturMap);


// Coordonnées géographiques
PointAssmat pointGeo = null;
if(geoLong!=0.0 && geoLat!=0.0){
 pointGeo = new PointAssmat(latitude , longitude, "user");
 request.setAttribute("userLocation", pointGeo);
 session.setAttribute("userLocation", pointGeo);
}



// Map liant une assmatSearch avec un point (couleur ect ..)
// TODO
String hashKey = "TODO";
Map<AssmatSearch,PointAssmat> assmatPoints = new HashMap<AssmatSearch,PointAssmat>();
TreeMap<AssmatSearch,PointAssmat> assmatPointsTriee = new TreeMap<AssmatSearch,PointAssmat>(new AssmatSearchDistanceComparator(pointGeo, assmatPoints, hashKey, isPresentSearch, resultDispoRechercheMap));
logger.warn("assmatPointsTriee : " + assmatPointsTriee.size());



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



int nbAssmatTotal = resultSetDispo.size() + resultSetDispoFutur.size() + resultSetNonDispoContact.size() ; //+ resultSetDispoNonRenseigne.size();
logger.warn("resultSetDispo : " + resultSetDispo.size());
logger.warn("resultSetDispoFutur : " + resultSetDispoFutur.size());
logger.warn("resultSetNonDispoContact : " + resultSetNonDispoContact.size());
logger.warn("resultSetDispoNonRenseigne : " + resultSetDispoNonRenseigne.size());





ProfilASSMAT profilAMsearch = null;
PointAssmat pointAssmat = null;
SolisManager solisMgr = SolisManager.getInstance();
AssmatSolis assmatSolis = null;

Set<Publication> setProfilAM = new TreeSet<Publication>();




//Les assmat dispo
if(withDispo){
	for(AssmatSearch amSearch :resultSetDispo ){ 
// 	  logger.warn("amSearch  / "+amSearch.getNomAssmat());
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
Set<Place> setPlace =(Set<Place>) JcmsUtil.applyDataSelector(channel.getAllDataSet(Place.class), new RelaisMamSelectorCommune(SocleUtils.getCommuneFromCode(codeInsee+""),categRAM));





logger.warn("Relais : " + setPlace);







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
paramsMap.put("age", new String[]{"-1"});
paramsMap.put("month", new String[]{request.getParameter("mois")});




session.setAttribute("paramsMap", paramsMap);
request.setAttribute("assmatPoints", assmatPointsTriee);










// RESULATS DE RECHERCHE

// TODO Nombre de résultata par dsiponibilité : envoyer l'information dans le JSON pour que le javascript mette l'information à jour








Set<Map.Entry<AssmatSearch,PointAssmat>> assmatResultSet = assmatPointsTriee.entrySet();
logger.warn("assmatResultSet : "+assmatResultSet.size());







JsonArray jsonArray = new JsonArray();
JsonObject jsonObject = new JsonObject();



// Gestion du pager
if(hasPager) {
  jsonObject.addProperty("page-index", pager);
} else {
  jsonObject.addProperty("max-result", maxResult);
}


jsonObject.addProperty("nb-result", assmatResultSet.size());
jsonObject.addProperty("nb-result-per-page", maxResult);


jsonObject.add("result", jsonArray);



ProfilManager profilMgr = ProfilManager.getInstance();
ProfilASSMAT itProfilAM = null;
%>




<jalios:foreach collection="<%= assmatResultSet %>" name="itEntry" type="Map.Entry<AssmatSearch, PointAssmat>" max='<%= maxResult %>' skip='<%= (pager - 1) * maxResult  %>'>


<%
itProfilAM = profilMgr.getProfilASSMATbyAssmatSearch(itEntry.getKey());
if(Util.notEmpty(itProfilAM)){
  AssmatSearch itSearch = itEntry.getKey();
  PointAssmat itPoint = itEntry.getValue();

  // Ajout du résultat au json
  jsonArray.add(SocleUtils.publicationToJsonObject(itProfilAM,  Double.toString(itPoint.getLatitude()), Double.toString(itPoint.getLongitude()), itPoint.getCouleurPoint(), itCounter + " " + itSearch.getNomAssmat(), itSearch.getNomAssmat(), null));

}
%>
	
</jalios:foreach>






<%



%><%= jsonObject %>