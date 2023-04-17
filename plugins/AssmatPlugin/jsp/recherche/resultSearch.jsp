<%@ page contentType="text/html; charset=UTF-8"%>

<%
//Commune
String commune = getUntrustedStringParameter("cityName",null);
int codeInsee = getIntParameter("codeInsee",0);

CityManager cityManager = CityManager.INSTANCE;
City city = null;
String communeCode = null;
if(Util.notEmpty(codeInsee)){
    try {
        city =(City) cityManager.getCityByCode(String.valueOf(codeInsee), City.class);
        communeCode = city.getLibelleDeVoie();
       } catch (UnknowCityException e){
           logger.warn("La commune <"+commune+"> <"+codeInsee+"> n'a pas été trouvé.");
       }
}


// DISTANCE
int distance = getIntParameter("distance", -1);

//Age de l'enfant
int trancheAge1Key= getIntParameter("age1", -1);
long timeAge1= getLongParameter("month1", -1);

int trancheAge2Key= getIntParameter("age2", -1);
long timeAge2= getLongParameter("month2", -1);

int trancheAge3Key= getIntParameter("age3", -1);
long timeAge3= getLongParameter("month3", -1);

int trancheAge4Key= getIntParameter("age4", -1);
long timeAge4= getLongParameter("month4", -1);

//Création de la map
HashMap<Integer, Date> hashMapAgeDate = new HashMap<Integer, Date>();
if(trancheAge1Key != -1 && timeAge1 !=-1){
  hashMapAgeDate.put(trancheAge1Key, new Date(timeAge1));
}
if(trancheAge2Key != -1 && timeAge2 !=-1){
  hashMapAgeDate.put(trancheAge2Key, new Date(timeAge2));
}
if(trancheAge3Key != -1 && timeAge3 !=-1){
  hashMapAgeDate.put(trancheAge3Key, new Date(timeAge3));
}
if(trancheAge4Key != -1 && timeAge4 !=-1){
  hashMapAgeDate.put(trancheAge4Key, new Date(timeAge4));
}



//LIEU D'EXERCICE

HashSet<Category> categorySet = new HashSet<Category>();
String[] idCategLieu = getStringParameterValues("lieuexercice", ".*");
if(Util.notEmpty(idCategLieu)){
  categorySet.addAll(AssmatUtil.getCategorySetFromIds(idCategLieu));
}

// PAR TYPE D'ACCUEIl
String[] idCategType = getStringParameterValues("typeaccueil", ".*");
if(Util.notEmpty(idCategType)){
  categorySet.addAll(AssmatUtil.getCategorySetFromIds(idCategType));
}

//PAR SPECIFICITE
String[] idCategSpecificite = getStringParameterValues("specificite", ".*");
if(Util.notEmpty(idCategSpecificite)){
  categorySet.addAll(AssmatUtil.getCategorySetFromIds(idCategSpecificite));
}
HashMap<String, Boolean> hashMapBooleanChamps= new HashMap<String, Boolean>();
hashMapBooleanChamps= AssmatUtil.getHashMapFromSelectedCategories(categorySet);




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

//Nom de l'assmat 
String nomAssmat = getUntrustedStringParameter("nomassmat", null);

// AssmatSearchDAO.getResultSearch(commune, lieuDomicile, lieuMam, accueilPeriscolaire, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, hashMapAgeDate, etatDispo)
//RECHERCHE
Set<AssmatSearch> resultSet = new HashSet<AssmatSearch>();
resultSet= AssmatSearchDAO.getResultSearch(commune, lieuDomicile, lieuMam, accueilPerisco, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, hashMapAgeDate, null);





%>

<%@ page contentType="text/html; charset=UTF-8"%>

<div class="span7 introRecherche introResult">

<p>Résultat de votre recherche :<br></p>
<p><%=resultSet.size() %> assistantes maternelles</p>
</div>

<div class="span5">

<jalios:include id='r1_58309' />

</div>

<%for(AssmatSearch assmat :resultSet ){ %>

<%=assmat.getNomAssmat() %>
<%}%>