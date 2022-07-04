package fr.cg44.plugin.assmat.webservice;

import static com.jalios.jcms.Channel.getChannel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

import com.jalios.jcms.Channel;
import com.jalios.jcms.Member;
import com.jalios.jcms.rest.JcmsRestResource;
import com.jalios.util.ReflectUtil;
import com.jalios.util.Util;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.URLUtils;
import fr.cg44.plugin.assmat.datacontroller.DispoASSMATDataController;
import fr.cg44.plugin.assmat.datacontroller.ProfilASSMATDataController;
import fr.cg44.plugin.assmat.managers.ProfilManager;
import fr.trsb.cd44.solis.beans.AssmatSolis;
import fr.trsb.cd44.solis.manager.SolisManager;
import generated.Disponibilite;
import generated.ProfilASSMAT;


/**
 * Service REST pour la mise à jour du contenu JCMS Profile AM par rapport à la table solis
 * 
 * @author m.formont
 *
 */
public class SolisRestResources extends JcmsRestResource {
	private static final Logger logger = Logger.getLogger(SolisRestResources.class);
	
	private static Boolean hasChangeDispo = false;
	private static Boolean isError = false;
	
  public SolisRestResources(Context context, Request request, Response response) {
    super(context, request, response);
        
    // Indique que l'on supporte la représentation text/xml de la réponse
    getVariants().add(new Variant(MediaType.TEXT_XML));
    // Indique que l'encodage du corps de la réponse est en UTF-8
    setXmlUTF8Encoding();
  }
  

  @Override
  protected void doPost(Representation entity) {
	  
    if (Util.isEmpty(getLoggedMember())) {
      // ie status code HTTP 401 Unauthorized
      getResponse().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
      return;
    }  
	    
    // Récupère les assmatsolis passés en paramètre du service REST (avec le param : id)
    // Cette liste d'id correspond aux assamat mises à jour dans solis
    // Lance la synchronisation des mofications avec la table solis_search et le contenu JCMS ProfilASSMAT

    // Erreur si pas de parametre id dans la requete 
    if(Util.isEmpty(queryString)) {
      getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Missing parameter 'id'");
      return;
    }
    
    // Flux JSON à retourner
    JSONArray jsonArrayResponse = new JSONArray();
    
    SolisManager solisManager = SolisManager.getInstance();
    ProfilManager profilManager = ProfilManager.getInstance();

    // Récupère les id des assmat solis passés en paramètre    
    Map<String, List<String>> params = URLUtils.splitQuery(queryString);
    List<String> idsParam = params.get("id");
    
    // Erreur si format du parametre id incorrect
    if( Util.isEmpty(idsParam) || hasIncorrectFormat(idsParam)) {
      getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Incorrect format parameter 'id'");
      return;
    }
    
    // La liste des assmatSolis qui ont été modifiées
    List<AssmatSolis> assmatSolisList = solisManager.getAssmatSolisByNumAgrement(idsParam);
    
    // Génère une liste de numéro de dossier des assmats trouvées
    List<String> assmatSolisNumDossierList = new ArrayList<String>();
    for(AssmatSolis itAssmat : assmatSolisList) {
      assmatSolisNumDossierList.add(""+itAssmat.getNumDossierAssmat());
    }
    // Si le numéro de dossier n'éxiste pas, l'indique dans la response Json
    for(String itId : idsParam) {
      if(!assmatSolisNumDossierList.contains(itId)) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", itId);
        jsonObject.put("status", "fail");
        jsonObject.put("message", "Le numéro de dossier est introuvable dans la base tampon Solis");
        jsonArrayResponse.put(jsonObject);
      }
    }
    
    // Pour chaque assmat mise à jour
    // synchroniser le profil AM et le Membre
    for(AssmatSolis assmat :  assmatSolisList) {
      hasChangeDispo = false;
      isError = false;
      Integer numDossier = assmat.getNumDossierAssmat();
      // Récupère le Profile AM du membre à partir du numéro d'agrément
      ProfilASSMAT profil = profilManager.getProfilASSMAT(numDossier);
      // Récupère le membre JCMS à partir du profile AM
      Member mbr = profilManager.getMember(profil);
      if(Util.notEmpty(mbr)) {
        // Met à jour le membre JCMS de l'assmat
        updateMember(profil, mbr, assmat, jsonArrayResponse);
        // Met à jour le profil AM de l'assmat
        updateProfil(profil, assmat, jsonArrayResponse);
        // Met à jour les disponibilités de l'assmat
        updateDispo(mbr, assmat);
        // Met à jour la table search des assmat pour la géolocalisation
        //ProfilManager.updateSearch(mbr, assmat);
        ProfilASSMATDataController profilAMController = new ProfilASSMATDataController();
        profilAMController.updateAssmatSearch(profil);
     
        // Invalidation du cache d'affichage pour les tooltip de la map & pour la page de résultats de recherche vue liste
        Channel.getChannel().getCacheManager().removeEntry("query_map_assmat_"+profil.getRowId()+"_true");
        Channel.getChannel().getCacheManager().removeEntry("query_map_assmat_"+profil.getRowId()+"_false");
        Channel.getChannel().getCacheManager().removeEntry("info_window_map_"+profil.getRowId()+"_true");
        Channel.getChannel().getCacheManager().removeEntry("info_window_map_"+profil.getRowId()+"_false");
        
        // Si aucune error ou warn alors envoi du Json success
        JSONObject jsonObject = new JSONObject();
        if(!isError) {      
          jsonObject.put("id", profil.getNum_agrement());
          jsonObject.put("status", "success");
          jsonArrayResponse.put(jsonObject);
        }
        
        // Envoi d'une alerte à l'assistance maternelle dans les étape 3 à 6 de updateDispo
        if(hasChangeDispo) {
          // Paramètre pour le sujet et le contenu du message (mail et sms)          
          String[] parameters = new String[]{};
          parameters = (String[]) Util.insertArray(parameters, 0, mbr.getFirstName());
          parameters = (String[]) Util.insertArray(parameters, 1, mbr.getName());
          // Sujet du mail
          String subject = AssmatUtil.getMessagePropertiesParametersValues("ALERTE_MODIF_AGREMENT_SUJET_MAIL", parameters);;      
          // Contenu du mail
          String contentMail = AssmatUtil.getMessagePropertiesParametersValues("ALERTE_MODIF_AGREMENT_CORPS_MAIL", parameters);
          // Contenu du sms
          String contentSms = AssmatUtil.getMessagePropertiesParametersValues("ALERTE_MODIF_AGREMENT_SMS", parameters);
          // Envoi du mail
          String from = AssmatUtil.getDefaultEmail();
          AssmatUtil.sendMessage(profil, subject, contentMail, contentSms, from);
        }
      }else {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", numDossier);
        jsonObject.put("status", "fail");
        jsonObject.put("message", "Personne non inscrite sur le site mais présente dans la base tampon Solis");
        jsonArrayResponse.put(jsonObject);
      }
    }   
    getResponse().setEntity(jsonArrayResponse.toString(), MediaType.APPLICATION_JSON);    
  }
  
  
  /**
   * Indique si un des id de la liste n'est pas dans un format correct
   * @param idsParam
   * @return true si la liste contient un id invalide
   */
  private boolean hasIncorrectFormat(List<String> idsParam) {
    for(String itIdParam : idsParam) {
      try {
        Integer.parseInt(itIdParam.trim());
      } catch (Exception e) {
    	  logger.info("<"+itIdParam+"> hasIncorrectFormat",e);
        return true;
      }
    }
    return false;
  }


  @Override
  public boolean allowGet() {
    // N'autorise pas la méthode HTTP GET
    return false;
  }
  
  
  @Override
  public boolean allowPost() {
    // Autorise la méthode HTTP POST
    return true;
  }
  
  
  /**
   * Gère le message de retour en Json
   */
  public static JSONObject addMessageErrorJson(int agrement, String status, String message, JSONObject jsonObject){    
    if(!isError && jsonObject == null) {
      jsonObject = new JSONObject();
      jsonObject.put("id", agrement);
      jsonObject.put("status", status);
      jsonObject.put("message", message);
      isError = true;
    }
    return jsonObject;
  }
  
  /**
   * Met à jour le membre JCMS par rappor à un contenu assmat
   * Champs mis à jour : Civilité, Nom, Prénom, email, mobile
   * @param mbr
   * @param assmat
   * @param jsonArrayResponse 
   */
  public static void updateMember(ProfilASSMAT profil, Member mbr, AssmatSolis assmat, JSONArray jsonArrayResponse) {
    JSONObject jsonObject = null;
    ProfilManager profilManager = ProfilManager.getInstance();
    Member clone = (Member) mbr.getUpdateInstance();
    clone.setSalutation(profilManager.getCivilite(assmat.getCiviliteAssmat()));
    clone.setName(assmat.getNomAssmat());
    clone.setFirstName(assmat.getPrenomAssmat());
      
    // MOBILE
    String mobile = assmat.getTelPortable();
    // Cas 1 - Si le téléphone portable est vide dans la base tampon et bien complété dans JCMS
    if(Util.isEmpty(mobile) && Util.notEmpty(mbr.getMobile())){
      jsonObject = addMessageErrorJson(profil.getNum_agrement(), "warn", "Le téléphone portable ne devrait pas être vide dans la base tampon Solis alors qu'il est bien renseigné dans le site. Ce numéro de téléphone n'a pas été mis à jour.", jsonObject);
    // Cas 2 - Si le téléphone portable est différent entre JCMS et la base tampon Solis
    }else if(Util.notEmpty(mobile) && Util.notEmpty(mbr.getMobile()) && !mobile.equals(mbr.getMobile())){
      jsonObject = addMessageErrorJson(profil.getNum_agrement(), "warn", "Le téléphone portable est différent entre le site et la base tampon Solis. Ce numéro de téléphone n'a pas été mis à jour.", jsonObject);
    }else {
      clone.setMobile(mobile);
    }
    
    // EMAIL
    String email = assmat.getEmailAssmat();
    // Cas 1 - Si le téléphone portable est vide dans la base tampon et bien complété dans JCMS
    if(Util.isEmpty(email) && Util.notEmpty(mbr.getEmail())){
      jsonObject = addMessageErrorJson(profil.getNum_agrement(), "warn", "L'email ne devrait pas être vide dans la base tampon Solis alors qu'il est bien renseigné dans le site. L'adresse email n'a pas été mise à jour.", jsonObject);
      // Cas 2 - Si le téléphone portable est différent entre JCMS et la base tampon Solis
    }else if(Util.notEmpty(email) && Util.notEmpty(mbr.getEmail()) && !email.equals(mbr.getEmail())){
      jsonObject = addMessageErrorJson(profil.getNum_agrement(), "warn", "L'email est différent entre le site et la base tampon Solis. Cette adresse email n'a pas été mise à jour.", jsonObject);
    }else {
      clone.setEmail(email);
    }    
    clone.performUpdate(getChannel().getDefaultAdmin());  
      
    // Ajoute le warn au message de retour du Json si un message existe
    if(isError) {
      jsonArrayResponse.put(jsonObject);
    }
  }
  
 
  /**
   * Met à jour le profil AM par rapport à un contenu assmat
   * Mettre à jour le nom des dispo par rapport au commune si la commune docile ou la commune mam a changé
   * et la visibilité de l'assmat sur le site
   * Champs mis à jour : tel fix, email, titre et la visibilité sur le site (par rapport à l'aide caf)
   * @param profil
   * @param assmat
   */
  public static void updateProfil(ProfilASSMAT profil, AssmatSolis assmat, JSONArray jsonArrayResponse){    
    JSONObject jsonObject = null;
    ProfilASSMAT clone = (ProfilASSMAT) profil.getUpdateInstance(); 
    // Mettre à jour le nom des dispo par rappor au commune si la commune docile ou la commune mam a changé  
    if((Util.notEmpty(assmat.getCommuneDomicile()) && !assmat.getCommuneDomicile().equals(profil.getCommune())) ||
    (Util.notEmpty(assmat.getCommuneMam()) && !assmat.getCommuneMam().equals(profil.getCommuneMam())) ||
    (Util.isEmpty(assmat.getCommuneDomicile()) && Util.notEmpty(profil.getCommune())) ||
    (Util.isEmpty(assmat.getCommuneMam()) && Util.notEmpty(profil.getCommuneMam()))){
      
      clone.setCommune(assmat.getCommuneDomicile());
      clone.setCommuneMam(assmat.getCommuneMam());
      
      
      
      ProfilManager profilManager = ProfilManager.getInstance();
      Member mbr = profilManager.getMember(profil);
      
      // Mise à jour de l'organization du membre
      Member cloneMbr = (Member) mbr.getUpdateInstance();
      StringBuilder stbd = AssmatUtil.getOrganization(mbr);
      cloneMbr.setOrganization(stbd.toString());
      cloneMbr.performUpdate(getChannel().getDefaultAdmin());
      
      // Mise à jour titre des disponibilités
      List<Disponibilite> dispoJcms = profilManager.getDisponibilitesList (mbr, null);      
      for(Disponibilite itDispo : dispoJcms) {
        itDispo.setTitle(AssmatUtil.getTitleDispo(itDispo.getTitle(), mbr, assmat.getCommuneDomicile(), assmat.getCommuneMam(), assmat.getNumDossierAssmat()+"", itDispo.getAgrement(), itDispo.getTrancheDage()+""));
      }      
    }
    
    // Met à jour la visibilité du profil par rapport au statut solis
    // Si l'assmat à une aide caf mais n'est pas visible sur le site alors elle devient visible. 
    if("A FORMER".equalsIgnoreCase(assmat.getStatut()) || "NON VALIDE".equalsIgnoreCase(assmat.getStatut()) || "ATA".equalsIgnoreCase(assmat.getStatut()) || "DOSSIER CLOS".equalsIgnoreCase(assmat.getStatut()) || "RETRAIT".equalsIgnoreCase(assmat.getStatut()) || "SUSPENDUE".equalsIgnoreCase(assmat.getStatut()) ){
      clone.setVisibiliteSite(false);
    }else if(assmat.getAideCaf() && !profil.getVisibiliteSite()) {
  	  clone.setVisibiliteSite(Boolean.TRUE);
    }
    
    // Met à jour le profil AM par rapport à un contenu assmat   
    clone.setTitle(AssmatUtil.getTitleProfilAssmat(clone.getTitle(),clone.getAuthor(), assmat.getCommuneDomicile(), assmat.getCommuneMam(), assmat.getNumDossierAssmat().toString()));   
  
    // TEL PRINCIPAL
    String telPrincipal = assmat.getTelPrincipal();
    // Cas 1 - Si le téléphone portable est vide dans la base tampon et bien complété dans JCMS
    if(Util.isEmpty(telPrincipal) && Util.notEmpty(profil.getTelephoneFixe())){
      jsonObject = addMessageErrorJson(profil.getNum_agrement(), "warn", "Le téléphone fixe (téléphone principal) ne devrait pas être vide dans la base tampon Solis alors qu'il est bien renseigné dans le site. Ce numéro de téléphone n'a pas été mis à jour.", jsonObject);      
      // Cas 2 - Si le téléphone portable est différent entre JCMS et la base tampon Solis
    }else if(Util.notEmpty(telPrincipal) && Util.notEmpty(profil.getTelephoneFixe()) && !telPrincipal.equals(profil.getTelephoneFixe())){
      jsonObject = addMessageErrorJson(profil.getNum_agrement(), "warn", "Le téléphone fixe (téléphone principal) est différent entre le site et la base tampon Solis. Ce numéro de téléphone n'a pas été mis à jour.", jsonObject);
    }else {
      clone.setTelephoneFixe(assmat.getTelPrincipal()); 
    }
    
    // Longitude et latitude MAM et domicile
    if(assmat.getLongitude() == null || assmat.getLatitude() == null) {
      clone.setLatitudeAssmat(0d);
      clone.setLongitudeAssmat(0d);
    }else {
      clone.setLatitudeAssmat(assmat.getLatitude().doubleValue());
      clone.setLongitudeAssmat(assmat.getLongitude().doubleValue());
    }
    
    if(assmat.getLongitudeMam() == null || assmat.getLatitudeMam() == null) {
      clone.setLatitudeMAM(0d);
      clone.setLongitudeMAM(0d);
    }else {
      clone.setLatitudeMAM(assmat.getLatitudeMam().doubleValue());
      clone.setLongitudeMAM(assmat.getLongitudeMam().doubleValue());
    }
    
    // Si message de warn l'ajoute au retour json
    if(jsonObject != null) {
      jsonArrayResponse.put(jsonObject);
    }
           
//    if(assmat.getAutorisationActivation() && !profil.getCompteActive()) {
//      clone.setCompteActive(true);
//    }else if(!assmat.getAutorisationActivation() && profil.getCompteActive()){
//      clone.setCompteActive(false);
//    }
    clone.performUpdate(getChannel().getDefaultAdmin());   
  }
  
  
  /**
   * Met à jour les disponibilités du membre par rapport à un contenu assmat
   * @param mbr
   * @param assmat
   */
  public static void updateDispo(Member mbr, AssmatSolis assmat) {  
    // Supprime les disponibilités JCMS de l'assmat qui ne sont plus dans solis par rapport à leur tranche d'age
    // en cas de suppresion complete d'une tranche d'age de l'assmat dans solis
    deleteDispoNotInSolis(mbr, assmat);    
    // Ajoute des disponibilités si nécéssaire ou en supprime 
    // Vérifie le nombre de place et ajoute ou supprime les dispo
    updateDispoChanceNbPlace(mbr, assmat);
  }
  
  
  /**
   * Vérifie le nombre de place par clé de tranche d'age dans solis et ajoute ou supprime les dispos nécéssaires dans JCMS
   * @param mbr
   * @param assmat
   */
  public static void updateDispoChanceNbPlace(Member mbr, AssmatSolis assmat) {
    ProfilManager profilManager = ProfilManager.getInstance();  
    ProfilASSMAT profilAM = profilManager.getProfilASSMAT(mbr);
    List<Disponibilite> dispoJcms = profilManager.getDisponibilitesList(mbr, null);
    
    // 0010809: Notification en cas de changement d'agrément
    // Ajouter une règle qui permet de n'envoyer la notif par mail ou sms que si l'am a au moins une disponibilité actuelle ou future de renseignée.
    boolean prevenirAssmat = false;
    if(Util.notEmpty(dispoJcms)){
    	for (Iterator<Disponibilite> it = dispoJcms.iterator(); it.hasNext() && !prevenirAssmat;) {
    		Disponibilite disponibilite = it.next();
    		if(Util.notEmpty(disponibilite)){
    			if(disponibilite.getEtatDispo().equals(AssmatUtil.SelectionEtatDispo.IMMEDIATE.getValue()) || disponibilite.getEtatDispo().equals(AssmatUtil.SelectionEtatDispo.FUTURE.getValue())){
    				prevenirAssmat = true;
    			}
    		}
    	}
    }
    
    // Récupère une map de disponibilité avec en clé la valeur de tranche d'age et en valeur la liste des disponibilités pour cette tranche d'age
    Map<String, List<Disponibilite>> dispoJcmsMap = getTrancheDispo(dispoJcms);   
    
    // Pour chacune des places de solis vérifie les disponibilités
    for(int itPlace=1; itPlace<=8; itPlace++) {

      // agrementTrancheAgeKey
      Field agrementTracheAgeKeyField = ReflectUtil.getField(assmat.getClass(), "place"+itPlace+"AgrementTrancheAgeKey");
      String agremenTracheAgeKey = (String) ReflectUtil.getFieldValue(assmat, agrementTracheAgeKeyField);
      
      // Si pas d'agrement dans solis saute la boucle
      if(Util.isEmpty(agremenTracheAgeKey)) {
        continue;
      }
      
      // placeTracheAgeKey
      Field placeTracheAgeKeyField = ReflectUtil.getField(assmat.getClass(), "place"+itPlace+"TrancheAgeKey");
      Integer placeTracheAgeKey = (Integer) ReflectUtil.getFieldValue(assmat, placeTracheAgeKeyField);      

      // PlaceNbPlaces
      Field placeNbPlacesField = ReflectUtil.getField(assmat.getClass(), "place"+itPlace+"NbPlaces");
      Integer placeNbPlaces = (Integer) ReflectUtil.getFieldValue(assmat, placeNbPlacesField);

      // placeTrancheAge
      Field placeTrancheAgeField = ReflectUtil.getField(assmat.getClass(), "place"+itPlace+"TrancheAge");
      String placeTrancheAge = (String) ReflectUtil.getFieldValue(assmat, placeTrancheAgeField);

      // PlaceLibCompl
      Field placeLibComplField = ReflectUtil.getField(assmat.getClass(), "place"+itPlace+"LibCompl");
      String placeLibCompl = (String) ReflectUtil.getFieldValue(assmat, placeLibComplField);

      // Saisie disponible
      Field placeSaisieField = ReflectUtil.getField(assmat.getClass(), "place"+itPlace+"SaisieDisponibilite");
      Boolean placeSaisie = (Boolean) ReflectUtil.getFieldValue(assmat, placeSaisieField);
      
      
      // Des disponibilité pour cet agrement tranche d'age éxiste dans JCMS pour cette assmat
      if(dispoJcmsMap.containsKey(agremenTracheAgeKey)) {
        // Récupère les siponibilité pour cette clé de tranche d'age
        List<Disponibilite> dispoList = dispoJcmsMap.get(agremenTracheAgeKey);
          
        
        List<Disponibilite> dispoListNew = new ArrayList<Disponibilite>();
        // 2 - Modification des libellés complémentaires
        for(Disponibilite itDispo : dispoList) {
          if(Util.isEmpty(placeLibCompl) && Util.notEmpty(itDispo.getLibelleComplementaire())) {
            itDispo.setLibelleComplementaire("");
            itDispo.performUpdate(getChannel().getDefaultAdmin());
          } else if(placeLibCompl != null && !placeLibCompl.equals(itDispo.getLibelleComplementaire())) {
            itDispo.setLibelleComplementaire(placeLibCompl);
            itDispo.performUpdate(getChannel().getDefaultAdmin());
          }
          
          // 3 - Si Modification du type de tranche dans l'agrement
          // 5 - Ou si modification du drapeau de saisie des disponibilite
          if(placeTracheAgeKey != itDispo.getTrancheDage() || (!placeSaisie.equals(itDispo.getSaisieDisponible()) ) ){
            itDispo.performDelete(getChannel().getDefaultAdmin());
            if(prevenirAssmat)
            	hasChangeDispo = true;
          }else {
            dispoListNew.add(itDispo);
          }          
        }
        
        // Création d'une disponibilité dans JCMS si une ou plusieurs dispo ont été ajoutées dans solis       
        if( dispoListNew.size() < placeNbPlaces ) {
          // 6 - Création d'autant de disponibilité que de différence entre JCMS et solis pour cette tranche d'age
          Integer nbPlaceManquante = placeNbPlaces - dispoListNew.size();
          AssmatUtil.createManyDispo(mbr, agremenTracheAgeKey, placeTracheAgeKey, placeTrancheAge, placeLibCompl, nbPlaceManquante, placeSaisie, profilAM);
          if(prevenirAssmat)
          	hasChangeDispo = true;
        }else if( dispoListNew.size() > placeNbPlaces ) {
          // 4 - Ou si nombre de diponibilité supérieur au nombre de place dans solis  
          // Alors on les supprimes toutes et on en re-créer le bon nombre
          // Suppression des dispos
            if(prevenirAssmat)
            	hasChangeDispo = true;
          for(Disponibilite itDispo : dispoListNew) {
            itDispo.performDelete(getChannel().getDefaultAdmin());
          }
          // Création des dispos
          AssmatUtil.createManyDispo(mbr, agremenTracheAgeKey, placeTracheAgeKey, placeTrancheAge, placeLibCompl, placeNbPlaces, placeSaisie, profilAM);
        }
      }else {
        // Aucun disponibilité trouvé il faut donc les créer
        AssmatUtil.createManyDispo(mbr, agremenTracheAgeKey, placeTracheAgeKey, placeTrancheAge, placeLibCompl, placeNbPlaces, placeSaisie, profilAM);
      } 
    }    
  }
 

  /**
   * Supprime les disponibilités de l'assmat qui ne sont plus dans solis par rapport à leur agrement tranche d'age  
   * en cas de suppresion complete d'un agrement tranche d'age de l'assmat dans solis
   * @param mbr
   * @param assmat
   */
  public static void deleteDispoNotInSolis(Member mbr, AssmatSolis assmat) {    
    ProfilManager profilManager = ProfilManager.getInstance();
    List<Disponibilite> dispoJcms = profilManager.getDisponibilitesList(mbr, null);
    // Création d'un set avec les agrement key de l'assmat
    Set<String> solisAgrementKeySet = new TreeSet<String>();
    for(int itPlace=1; itPlace<=8; itPlace++) {    
      // agrementTrancheAgeKey
      Field agrementTracheAgeKeyField = ReflectUtil.getField(assmat.getClass(), "place"+itPlace+"AgrementTrancheAgeKey");
      String agremenTracheAgeKey = (String) ReflectUtil.getFieldValue(assmat, agrementTracheAgeKeyField);      
      if(Util.notEmpty(agremenTracheAgeKey)) {
       solisAgrementKeySet.add(agremenTracheAgeKey);
     }
    }
    // 1 - Supprime toutes les disponibilités qui ont un agrement trache d'age non présente dans solis pour cette assmat
    for(Disponibilite itDispo : dispoJcms) {
      if(!solisAgrementKeySet.contains(itDispo.getAgrement())){
        itDispo.performDelete(getChannel().getDefaultAdmin());
      }
    }
  }


  /**
   * Retourne la liste des disponibiltés dans une map avec l'agrement tranche d'age en clé
   * @param dispoList
   * @return Une map des disponibilités triée par rapport à l'agrement tranche d'age à partir d'une liste
   */
  public static Map<String, List<Disponibilite>> getTrancheDispo(List<Disponibilite> dispoList) {
    Map<String, List<Disponibilite>> dispoMap = new HashedMap();
    for(Disponibilite itDispo : dispoList){
      String agrementKey = itDispo.getAgrement();
      if(dispoMap.containsKey(agrementKey)) {
        dispoMap.get(agrementKey).add(itDispo);        
      }else {
        ArrayList newList = new ArrayList<Disponibilite>();
        newList.add(itDispo);
        dispoMap.put(agrementKey, newList);
      }
    }
    return dispoMap;
  }

}
