package fr.cg44.plugin.assmat.managers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.apache.catalina.util.URLEncoder;
import org.apache.commons.httpclient.HttpURL;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jalios.jcms.Channel;
import com.jalios.jcms.HttpUtil;
import com.jalios.util.Util;
import com.orange.ceo.client.SmsClient;
import com.orange.ceo.client.bean.About;
import com.orange.ceo.client.bean.AboutResponse;
import com.orange.ceo.client.bean.GetAccountInformation;
import com.orange.ceo.client.bean.GetAccountInformationResponse;
import com.orange.ceo.client.bean.ListResults;
import com.orange.ceo.client.bean.ListResultsResponse;
import com.orange.ceo.client.bean.WSAccount;
import com.orange.ceo.client.bean.WSListFilter;
import com.orange.ceo.client.bean.WSResultReport;
import com.orange.ceo.client.bean.WSResultsReport;
import com.orange.ceo.client.stub.MultiDiffusionWSServiceStub;

import fr.cg44.plugin.assmat.beans.EnvoiSms;
import fr.cg44.plugin.assmat.hibernate.HibernateCD44Util;
import fr.cg44.plugin.assmat.util.SmsUtil;
import generated.ProfilASSMAT;

/**
 * 
 * Classe de gestion des SMS
 * 
 * - Gere l'envoi de SMS 
 * - Gere la mise à jour des statut des SMS en base
 * 
 * 
 * 
 * @author crabiller
 * 
 */
public class SmsDAO {
  private static final Logger logger = Logger.getLogger(SmsDAO.class);

  //Statut par defaut d'un sms quand il n'a pas pu etre envoyé 
  private static String DEFAUT_STATUT = "INSERTED IN DATABASE";
  private static String DEFAUT_ERROR_CODE = "999";
  private static String TIMED_OUT_ERROR_CODE = "Read timed out";

  
  /**
   * Retourne tout les sms envoyés (Stockés en base)
   * 
   * @return La liste des sms
   */
  
  public List<EnvoiSms> getAllEnvoiSms() {
    HibernateCD44Util.beginTransaction();
    List<EnvoiSms> list = HibernateCD44Util.getSession().createCriteria(EnvoiSms.class).list();
    HibernateCD44Util.commitTransaction();
    return list;
  }

  
  /**
   * 
   * Retourne les informations du compte qui accede aux webservices contact everyone 
   * 
   * @throws KeyManagementException
   * @throws NoSuchAlgorithmException
   * @throws AxisFault
   * @throws Exception
   */
  public WSAccount getAccountInformation() throws KeyManagementException, NoSuchAlgorithmException, AxisFault, Exception {
    SmsClient client = new SmsClient();
    MultiDiffusionWSServiceStub stub = new MultiDiffusionWSServiceStub();
    client.acceptAll(stub);

    GetAccountInformation getAccountInformation54 = (GetAccountInformation) client.getTestObject(GetAccountInformation.class);

    stub = client.setAuthentification(stub, Channel.getChannel().getProperty("jcmsplugin.assmatplugin.sms.login"),
        Channel.getChannel().getProperty("jcmsplugin.assmatplugin.sms.password"));

    getAccountInformation54.setCustId(SmsUtil.getCustId());

    GetAccountInformationResponse response = stub.getAccountInformation(getAccountInformation54);
    WSAccount account = response.getGetAccountInformationReturn();
    return account;
  }


  /**
   * 
   * Retourne les informations des sms à partir d'un tableau d'id de sms (sujet, etat, content , ...)
   * 
   * 
   * @param codeSms
   * @throws Exception
   */
  public WSResultsReport getSmsByCodes(String[] codeSms) throws Exception {    
    WSResultsReport reportsList = new WSResultsReport();
    String jeton = getJeton();
    for(String itCodeSms : codeSms){
      if(Util.notEmpty(itCodeSms)) {
       WSResultReport report = getStatusDiffusion(jeton, itCodeSms);
       reportsList.addReports(report);
      }
    }
    return reportsList;
  }


  /**
   * Envoi le SMS au numéro et avec le contenu en paramètre
   * @param numTel
   * @param subject
   * @param content
   * @param resumeContent
   * @param login
   * @param idProfil
   * @param newSms
   * @param idSms
   */
  public void send(String numTel, String subject, String content, String resumeContent, String login, String idProfil, boolean newSms, String idSms){    
    String jeton;
    String last_error_code = "";
    String strategy = "sms";   
    try {
      jeton = getJeton();
      String idGroup = getIdGroupe(jeton); 
      String idDiffusion = SmsDAO.diffusionSMS(jeton, idGroup, subject, content, numTel);
            
      //Si c'est un nouveau sms et non un sms que l'on renvoie)
      if(newSms){
        afterSendSms(login, numTel, subject, resumeContent, content, strategy, idDiffusion, last_error_code, idProfil, DEFAUT_STATUT);
      }else if(Util.notEmpty(idSms)) {
        updateIdDiffusionByIdBase(idSms, idDiffusion);
        updateSMSbyIdBase(idSms, DEFAUT_STATUT);
        updateErrorCodeSMSByIdBase(idSms, last_error_code);
      }                 
    } catch (Exception e) {
      if(newSms){
        last_error_code = DEFAUT_ERROR_CODE;
        // On stocke le sms en base avec le statut par defaut
        afterSendSms(login, numTel, subject, resumeContent, content, strategy, idSms, last_error_code, idProfil, DEFAUT_STATUT);    
        logger.error(e.getMessage(),e);
      } else {
    	  logger.warn("Impossible de reSend le SMS " + idSms, e);
      }
    }   
  }
  
  
  /**
   * 
   * Méthode procedant à l'envoi de sms
   * 
   * @param profil
   * @param subject
   * @param content
   * @param resumeContent
   */
  public void sendSMS(ProfilASSMAT profil, String subject, String content, String resumeContent) {
    if (Util.notEmpty(profil)) {
      sendSMS(profil.getAuthor().getName(), profil.getAuthor().getFirstName(), profil.getAuthor().getMobile(), subject, content, resumeContent, profil.getAuthor().getLogin(), profil);
    }
  }
  
  
  /**
   * 
   * Méthode procedant à l'envoi de sms
   * 
   * @param name
   * @param firstname
   * @param portable
   * @param subject
   * @param content
   * @param resumeContent
   * @param login
   * @param profil
   */
  public void sendSMS(String name, String firstname, String portable, String subject, String content, String resumeContent, String login,
      ProfilASSMAT profil) {   
    String numTel = portable;
    String idProfil="";
    if(Util.notEmpty(profil)){
      idProfil= profil.getId();
    }    
    send(numTel, subject, content, resumeContent, login, idProfil, true, null);
  }

  
  /**
   *  Enregistre le sms en base apres l'avoir envoyé (echec ou non)
   * @param login
   * @param subject
   * @param resumeContent
   * @param content
   * @param strategy
   * @param id_diffusion
   * @param last_error_code
   * @param localStatus
   */
  private void afterSendSms(String login, String numTel, String subject, String resumeContent, String content, String strategy, 
      String id_diffusion, String last_error_code, String idProfil, String localStatus) {
    EnvoiSms sms = new EnvoiSms();
    sms.setCreatedDate(new Date());
    sms.setUpdatedDate(new Date());
    sms.setCustId(SmsUtil.getCustId());
    sms.setLogin(login);
    sms.setAssmatId(idProfil);
    sms.setSendProfiles(numTel);
    sms.setSubject(subject);
    sms.setResumeContent(resumeContent);
    sms.setContent(content);
    sms.setStragegy(strategy);
    sms.setIdDiffusion(id_diffusion);
    sms.setLastErrorCode(last_error_code);
    sms.setStatutDiffusion(localStatus);
    HibernateCD44Util.add(sms);
  }



  /**
   * Méthode retournant tout les sms stockés en base n'ayant pas le statut
   * COMPLETED, CANCELED, REJECTED
   * 
   * 
   * @return La liste des sms n'ayant pas le statut COMPLETED, CANCELED, REJECTED
   */
  public List<EnvoiSms> getFailedSms() {
    HibernateCD44Util.beginTransaction();
    List<EnvoiSms> list = HibernateCD44Util.getSession().createCriteria(EnvoiSms.class).add(Restrictions.ne("statutDiffusion", "REJECTED"))
        .add(Restrictions.ne("statutDiffusion", "COMPLETED")).add(Restrictions.ne("statutDiffusion", "CANCELED")).list();
    HibernateCD44Util.commitTransaction();
    return list;
  }

  
  /**
   * Méthode retournant tout les sms stockés en base ayant en code d'erreur 999 (non envoyé)
   * 
   * 
   * @return La liste des sms non envoyé
   */
  public List<EnvoiSms> getSmsNonEnvoye() {
    HibernateCD44Util.beginTransaction();
    Criterion critDefaultError = Restrictions.eq("lastErrorCode", DEFAUT_ERROR_CODE);
    Criterion critTimedOutError = Restrictions.eq("lastErrorCode", TIMED_OUT_ERROR_CODE);
    Criterion idNull = Restrictions.isNull("idDiffusion");
    Criterion idVide = Restrictions.eq("idDiffusion", "");
    Criterion notComplete = Restrictions.ne("statutDiffusion", "COMPLETED");
    // Si l'id de diffusion est vide et le statut en erreur et n'est pas au statut COMPLETED, ce sms fait partie des sms non envoyés
    List<EnvoiSms> list = HibernateCD44Util.getSession().createCriteria(EnvoiSms.class).add(notComplete).add(Restrictions.or(idNull, idVide)).add(Restrictions.or(critDefaultError, critTimedOutError)).list();
    HibernateCD44Util.commitTransaction();
    return list;
  }
  
  
  /**
   * 
   * Méthode renvoyant les sms en état non envoyé (999)
   * 
   */
  public void reSendSms(){    
    //On recupere les sms NON envoye   
	List<EnvoiSms> listSMS=  getSmsNonEnvoye();
	
	if(Util.notEmpty(listSMS)) {
	    logger.debug("Nombre de SMS à renvoyer : " + listSMS.size());

	    try {
	      getJeton();
	    } catch (Exception e) {
	      logger.warn("Impossible de reSend les SMS car le token n'est pas récupérable (vérifier login/mdp et connexion au service orange)", e);
	      return;      
	    }
	    
	    
	    String idSMS= "";
	    if(Util.notEmpty(listSMS)){      
	      for(EnvoiSms sms : listSMS){        
	        if(Util.notEmpty(sms)){   
	          //On tente de renvoyé le sms
	          if(Util.notEmpty(sms.getId())){           
	            idSMS = Long.toString(sms.getId());
	          }
	          send(sms.getSendProfiles(), sms.getSubject(), sms.getContent(), sms.getResumeContent(), sms.getLogin(), sms.getAssmatId(), false, idSMS);
	          //On supprime le sms de la base (un nouveau a été créer)
	        } 
	      }     
	    }
	}    
  }
  
  
  
  /**
   * 
   * Met à jour le statut des sms en base (hors COMPLETED, CANCELED, REJECTED
   * 
   * 
   */
  @SuppressWarnings("null")
  public void updateAllSmsStatut() {

    // On recupere les sms a updater en base
    List<EnvoiSms> smsList = getFailedSms();
    String smsId = null;

    String[] smstab = new String[smsList.size()];
    if (Util.notEmpty(smsList)) {

    
      // On boucle sur les sms pour creer le tableaau d'id
      for (EnvoiSms sms : smsList) {
        // On recupere l'id du sms
        smsId = sms.getIdDiffusion();
        // Si l'id de diffusion est vide cela signifie qu'il na pas Ã©tÃ© envoyÃ©
        if (Util.notEmpty(smsId)) {
         smstab = (String[]) Util.insertArray(smstab, 0, smsId);
        }
       }
      // On recupere les sms
      WSResultsReport smsReports = null;
      try {
        smsReports = getSmsByCodes(smstab);
      } catch (Exception e) {
        logger.warn("Le SMS n'a pas pu être envoyé. Consulter le tableau de bord des SMS.", e);
      }
      if (Util.notEmpty(smsReports) && Util.notEmpty(smsReports.getReports())) {
        for (WSResultReport smsReport : smsReports.getReports()) {
          if (Util.notEmpty(smsReport)) {

            // on met a jour le sms correspondand en base
            updateSMSByID(smsReport.getMsgId(), smsReport.getStatus());

          }
        }
      }

    }
  }

  /**
   * Met a jour le statut d'un sms grace a son id en base
   * 
   * @param idSMS
   * @param idDiffusion
   */
  public void updateIdDiffusionByIdBase(String idSMS, String idDiffusion) {

    // On recupere le sms en base
    if (Util.notEmpty(getEnvoiSmsByIdBase(idSMS))) {

      EnvoiSms sms = Util.getFirst(getEnvoiSmsByIdBase(idSMS));

      // On lui set le nouveau statut
      sms.setIdDiffusion(idDiffusion);
    }
  }
  
  
  /**
   * Met a jour le statut d'un sms grace a son id de diffusion
   * 
   * @param idSMS
   * @param idDiffusion
   */
  public void updateIdDiffusionById(String idSMS, String idDiffusion) {

    // On recupere le sms en base
    if (Util.notEmpty(getEnvoiSmsById(idSMS))) {

      EnvoiSms sms = Util.getFirst(getEnvoiSmsById(idSMS));

      // On lui set le nouveau statut
      sms.setIdDiffusion(idDiffusion);
    }
  }
  
  /**
   * Met a jour le statut d'un sms grace a son id en base
   * 
   * @param idSMS
   * @param statut
   */
  public void updateSMSbyIdBase(String idSMS, String statut) {

    // On recupere le sms en base
    if (Util.notEmpty(getEnvoiSmsByIdBase(idSMS))) {

      EnvoiSms sms = Util.getFirst(getEnvoiSmsByIdBase(idSMS));

      // On lui set le nouveau statut
      sms.setStatutDiffusion(statut);
    }
  }

  /**
   * Met a jour le statut d'un sms grace a son id de diffusion
   * 
   * @param idSMS
   * @param statut
   */
  public void updateSMSByID(String idSMS, String statut) {

    // On recupere le sms en base
    if (Util.notEmpty(getEnvoiSmsById(idSMS))) {

      EnvoiSms sms = Util.getFirst(getEnvoiSmsById(idSMS));

      // On lui set le nouveau statut
      sms.setStatutDiffusion(statut);
    }
  }
  
  /**
   * Met a jour le code d'erreur d'un sms grace a son id en base
   * 
   * @param idSMS
   * @param errorCode
   */
  public void updateErrorCodeSMSByIdBase(String idSMS, String errorCode) {

    // On recupere le sms en base
    if (Util.notEmpty(getEnvoiSmsByIdBase(idSMS))) {

      EnvoiSms sms = Util.getFirst(getEnvoiSmsByIdBase(idSMS));

      // On lui set le nouveau statut
      sms.setLastErrorCode(errorCode);
    }
  }
  
  
  /**
   * Met a jour le code d'erreur d'un sms grace a son id de diffusion
   * 
   * @param idSMS
   * @param errorCode
   */
  public void updateErrorCodeSMSbyId(String idSMS, String errorCode) {

    // On recupere le sms en base
    if (Util.notEmpty(getEnvoiSmsById(idSMS))) {

      EnvoiSms sms = Util.getFirst(getEnvoiSmsById(idSMS));

      // On lui set le nouveau statut
      sms.setLastErrorCode(errorCode);
    }
  }
  
  /**
   * 
   * Retourne un sms en base selon son id en base 
   * 
   * @param id
   * @return Le sms 
   */
  public static List<EnvoiSms> getEnvoiSmsByIdBase(String id) {
   
    HibernateCD44Util.beginTransaction();
    Long idLong =    Long.parseLong(id);
    List<EnvoiSms> list = HibernateCD44Util.getSession().createCriteria(EnvoiSms.class).add(Restrictions.eq("id", idLong)).list();
    HibernateCD44Util.commitTransaction();

    return list;
  }
  
  
  /**
   * 
   * Retourne un sms en base selon son id de diffusion
   * 
   * @param id
   * @return Le sms 
   */
  public static List<EnvoiSms> getEnvoiSmsById(String id) {
    HibernateCD44Util.beginTransaction();
    List<EnvoiSms> list = HibernateCD44Util.getSession().createCriteria(EnvoiSms.class).add(Restrictions.eq("idDiffusion", id)).list();
    HibernateCD44Util.commitTransaction();

    return list;
  }
  
  /**
   * 
   * Retourne tout les sms en base trié par date de création  
   * @return Les sms 
   */
  public static List<EnvoiSms> getAllSms() {
    HibernateCD44Util.beginTransaction();
    List<EnvoiSms> list = HibernateCD44Util.getSession().createCriteria(EnvoiSms.class).addOrder(Order.desc("createdDate")).list();
    HibernateCD44Util.commitTransaction();

    return list;
  }
  
  /**
   * récupére le jeton d'authentification auprès de l'API.
   * @return
   * @throws Exception
   */
  public static String getJeton() throws Exception {
	String urlapi = Channel.getChannel().getProperty("jcmsplugin.assmatplugin.sms.url");
    String login = HttpUtil.encodeForURL(Channel.getChannel().getProperty("jcmsplugin.assmatplugin.sms.login"));
    String password = HttpUtil.encodeForURL(Channel.getChannel().getProperty("jcmsplugin.assmatplugin.sms.password"));
    String authentificationEndpoint = Channel.getChannel().getProperty("jcmsplugin.assmatplugin.sms.url.endpoint.authentification");    
    URL url = new URL(urlapi + authentificationEndpoint);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("POST");
    con.setRequestProperty("content-type", "application/x-www-form-urlencoded");
    //con.setRequestProperty("authorization", "bearer [Access-Token]");
    con.setRequestProperty("cache-control", "no-cache");
    con.setDoOutput(true);    
    String postJsonData = "username=" + login + "&password=" + password;    
    DataOutputStream dataOutputStream = new DataOutputStream(con.getOutputStream());
    dataOutputStream.writeBytes(postJsonData);
    dataOutputStream.flush();
    dataOutputStream.close();    
    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String output;
    StringBuilder response = new StringBuilder();
    while ((output = in.readLine()) != null){
      response.append(output);
    }
    in.close();    
    JSONObject json = new JSONObject(response.toString());        
    return json.getString("access_token");
  }
  
  
  /**
   * récupère l'identifiant de groupe de diffusion auprès de l'API.
   * @param jeton
   * @return
   * @throws JSONException 
   * @throws MalformedURLException
   */
  public static String getIdGroupe(String jeton) {
    String idGroup = "";
    String urlapi = Channel.getChannel().getProperty("jcmsplugin.assmatplugin.sms.url");
    String groupsEndpoint = Channel.getChannel().getProperty("jcmsplugin.assmatplugin.sms.url.endpoint.groups");
    try {
      URL url = new URL(urlapi + groupsEndpoint);   
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("content-type", "application/x-www-form-urlencoded");
      con.setRequestProperty("authorization", "bearer " + Util.encodeUrl(jeton));
      con.setRequestProperty("cache-control", "no-cache");
      con.setDoOutput(true);      
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String output;
      StringBuilder response = new StringBuilder();  
      while ((output = in.readLine()) != null){
          response.append(output);
      }  
      in.close();     
      JSONArray json = new JSONArray(response.toString()); 
      idGroup = json.getJSONObject(0).getString("id");      
    } catch (MalformedURLException e) {      
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }      
    return idGroup;
  }
  
  
  /**
   * Service de diffusion d'un SMS auprès de l'API.
   * @param jeton
   * @param idGroup
   * @param sujet
   * @param contenu
   * @param numTel
   */
  public static String diffusionSMS(String jeton, String idGroup, String sujet, String contenu, String numTel) throws Exception {    
    String idDiffusion = "";
    String urlapi = Channel.getChannel().getProperty("jcmsplugin.assmatplugin.sms.url");
    String diffusionrequestsEndpoint = Channel.getChannel().getProperty("jcmsplugin.assmatplugin.sms.url.endpoint.diffusionrequests");
    URL url = new URL(urlapi + diffusionrequestsEndpoint.replaceFirst("\\[id_group\\]", Util.encodeUrl(idGroup)));    
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("POST");
    con.setRequestProperty("content-type", "application/json");
    con.setRequestProperty("authorization", "bearer " + Util.encodeUrl(jeton));
    con.setRequestProperty("cache-control", "no-cache");
    con.setDoOutput(true); 
              
    JSONObject contentJson = new JSONObject();
    contentJson.put("name", SmsUtil.truncateSms(sujet));      
    JSONArray msisdns = new JSONArray();
    msisdns.put(0, numTel);      
    contentJson.put("msisdns", msisdns);           
    JSONObject smsParam = new JSONObject();
    smsParam.put("encoding", "GSM7");
    smsParam.put("body", SmsUtil.truncateSms(contenu));     
    contentJson.put("smsParam", smsParam);
        
    DataOutputStream dataOutputStream = new DataOutputStream(con.getOutputStream());
    dataOutputStream.write(contentJson.toString().getBytes());
    dataOutputStream.flush();
    dataOutputStream.close();
    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String output;
    StringBuilder response = new StringBuilder();
    while ((output = in.readLine()) != null){
        response.append(output);
    }
    in.close();
    JSONObject json = new JSONObject(response.toString()); 
    idDiffusion = json.getString("id");           
    
    return idDiffusion;
  }
  
  
  
  /**
   * 
   * @param jeton
   * @param idDiffusion
   * @return
   * @throws IOException 
   * @throws JSONException 
   */
  public static WSResultReport getStatusDiffusion(String jeton, String idDiffusion) throws Exception {    
    WSResultReport report = new WSResultReport();    
    WSResultsReport reportList = new WSResultsReport();
    reportList.addReports(report);
    String urlapi = Channel.getChannel().getProperty("jcmsplugin.assmatplugin.sms.url");
    String diffusionsEndpoint = Channel.getChannel().getProperty("jcmsplugin.assmatplugin.sms.url.endpoint.diffusions");
    URL url = new URL(urlapi + diffusionsEndpoint + "/" + idDiffusion);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();      
    con.setRequestMethod("GET");
    con.setRequestProperty("authorization", "bearer " + jeton);
    con.setRequestProperty("cache-control", "no-cache");
    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String output;
    StringBuilder response = new StringBuilder();
    while ((output = in.readLine()) != null){
        response.append(output);
    }
    in.close(); 
    JSONObject json = new JSONObject(response.toString());  
    String status = json.getJSONObject("state").getString("status");
    report.setMsgId(idDiffusion);
    report.setStatus(status);              
    return report;
  }

}
