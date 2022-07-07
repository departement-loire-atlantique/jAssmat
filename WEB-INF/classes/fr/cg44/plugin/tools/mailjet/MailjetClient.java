package fr.cg44.plugin.tools.mailjet;

import static com.jalios.jcms.Channel.getChannel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.jalios.jcms.Channel;
import com.jalios.util.HtmlUtil;
import com.jalios.util.Util;

import fr.cg44.plugin.tools.ToolsUtil;

public class MailjetClient {

	private static final Logger logger = Logger.getLogger(MailjetClient.class);

	private final static JSONParser PARSER_JSON = new JSONParser();

	private static final String key = Channel.getChannel().getProperty("plugin.tools.mailjet.api.key");
	private static String baseUrl = "https://api.mailjet.com/v3";

	private static String groupNameURL = baseUrl + "/REST/contactslist/:ID";
	private static String listrecipientURL = baseUrl + "/REST/listrecipient?ContactsList=:ID";
	private static String contactEmailURL = baseUrl + "/REST/contact/:ID";
	private static String addContactURL = baseUrl + "/REST/contact";
	private static String contactListsURL = baseUrl + "/REST/contact/:ID/managecontactslists";
	private static String sendMailURL = baseUrl + "/send";
	private static String createNewsletterURL = baseUrl + "/REST/newsletter";
	private static String addBodyToNewsletterURL = baseUrl + "/REST/newsletter/:ID/detailcontent";
	private static String sendTestNewsletterURL = baseUrl + "/REST/newsletter/:ID/test";
	private static String sendNewsletterURL = baseUrl + "/REST/newsletter/:ID/send";
	private static String lastCampaignsURL = baseUrl + "/REST/campaign?FromID=" + Channel.getChannel().getProperty("plugin.tools.mailjet.contact.id.supportjcms")+ "&Limit=20&Sort=ID+DESC";
	private static String allContactsGroupsURL = baseUrl + "/REST/contactslist?Limit=0";

	private static final int EXIT_SUCCESS = 0;
	private static final int EXIT_ERROR = 1;
	
	/**
	 * Retourne une instance du proxy JCMS
	 * 
	 * @return
	 */
	public static Proxy getProxy() {
		Integer proxyPort = getChannel().getIntegerProperty("http.proxyPort", 3128);
		String proxyHost = getChannel().getProperty("http.proxyHost");
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
		return proxy;
	}

	/**
	 * Retoune un Authenticator avec le user et mdp du proxy JCMS
	 * 
	 * @return
	 */
	public static Authenticator getAuthenticator() {
		Authenticator authenticator = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				String user = Util.notEmpty(getChannel().getProperty("http.proxy.login")) ? getChannel().getProperty("http.proxy.login") : "";
				String password = Util.notEmpty(getChannel().getProperty("http.proxy.password")) ? getChannel().getProperty("http.proxy.password") : "";
				return (new PasswordAuthentication(user, password.toCharArray()));
			}
		};
		return authenticator;
	}

	/**
	 * Récupère un objet JSON à partir d'une URL de l'API Mailjet
	 * @param strUrl URL de l'API Mailjet
	 * @return l'objet Json
	 */
	private static JSONObject getJsonFromMailjetAPI(String strUrl) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = getJsonFromMailjetAPI(strUrl, "GET", null, null);
		} catch (IOException e) {
			logger.warn("Erreur d'interrogation de l'API de emailing (erreur dans l'url)", e);
		}
		return jsonObject;
	}
	
	/**
	 * Récupère un objet JSON à partir d'une URL de l'API Mailjet
	 * @param strUrl URL de l'API Mailjet
	 * @param strMethod Méthode "GET" ou "POST"
	 * @param requestJson L'objet Json
	 * @param strContentType Content-Type
	 * @return
	 * @throws IOException 
	 */
	private static JSONObject getJsonFromMailjetAPI(String strUrl, String strMethod, JSONObject requestJson, String strContentType) throws IOException {
	
		JSONObject jsonObject = new JSONObject();
		
		// création de la connection
		try {
			URL url = new URL(strUrl);
			HttpsURLConnection myURLConnection;

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			}
			};

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
						
			if (Util.notEmpty(getChannel().getProperty("http.proxyHost"))) {
				myURLConnection = (HttpsURLConnection) url.openConnection(getProxy());
			} else {
				myURLConnection = (HttpsURLConnection) url.openConnection();
			}
			
			
			
			myURLConnection.setSSLSocketFactory(sc.getSocketFactory());
			myURLConnection.setHostnameVerifier(allHostsValid);
			
			Authenticator.setDefault(getAuthenticator());
			myURLConnection.setRequestProperty("Authorization", "Basic " + Base64.encode(key.getBytes()));

			if (Util.notEmpty(strContentType)) {
				myURLConnection.setRequestProperty("Content-Type", "application/json");
			}
			
			if (Util.notEmpty(strContentType)) {
				myURLConnection.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(myURLConnection.getOutputStream());
				wr.write(requestJson.toString());
				wr.flush();
			}
			
			// envoi de la requête
			InputStream httpStream = myURLConnection.getInputStream();
			
			StringWriter writer = new StringWriter();
			IOUtils.copy(httpStream, writer, "UTF-8");
			jsonObject = (JSONObject) PARSER_JSON.parse(writer.toString());

			httpStream.close();

		} catch (MalformedURLException e) {
			logger.warn("Erreur d'interrogation de l'API de emailing (erreur dans l'url)", e);
		} catch (ParseException e) {
			logger.warn("Erreur d'interrogation de l'API de emailing (JSON illisible)", e);
		} catch (NoSuchAlgorithmException e) {
			logger.warn("Erreur d'interrogation de l'API de emailing", e);
		} catch (KeyManagementException e) {
			logger.warn("Erreur d'interrogation de l'API de emailing", e);
		} catch (IOException e) {
			logger.warn("Erreur d'interrogation de l'API de emailing (IOException) : "+strUrl);
		}
		
		
		return jsonObject;		
	}
	
	/**
	 * Récupère la valeur correspondant à la clé du Json Mailjet
	 * @param key Clé
	 * @return Valeur de la clé Json
	 */
	public static String getValueFromMailJetJson(JSONObject jsonObject, String key) {
		String value = new String();
		JSONArray datas = (JSONArray) jsonObject.get("Data");
		JSONObject data = (JSONObject) datas.get(0);
		value = (data.get(key)).toString();
		return value;
	}
	
	
	/**
	 * Récupère le nom d'un groupe depuis sont ID
	 * 
	 * @param id
	 *            du groupe
	 * @return nom du groupe
	 */
	public static String getGroupName(String id) {

		if (Util.isEmpty(id)) {
			return null;
		}
		JSONObject jsonObject = getJsonFromMailjetAPI(groupNameURL.replace(":ID", id));
		JSONArray datas = (JSONArray) jsonObject.get("Data");
		JSONObject data = (JSONObject) datas.get(0);
		String groupName = (String) data.get("Name");
		
		return groupName;

	}
	
	/**
	 * Récupère l'email d'un contact à partir de son ID
	 * 
	 * @param id
	 *            du contact
	 * @return email du contact
	 */
	public static String getEmailFromContact(String id) {

		if (Util.isEmpty(id)) {
			return null;
		}
		JSONObject jsonObject = getJsonFromMailjetAPI(contactEmailURL.replace(":ID", id));
		JSONArray datas = (JSONArray) jsonObject.get("Data");
		JSONObject data = (JSONObject) datas.get(0);
		String email = (String) data.get("Email");
		
		return email;

	}
	
	/**
	 * Récupère l'id d'un contact à partir de son email
	 * 
	 * @param email
	 *            du contact
	 * @return id du contact
	 */
	public static String getIdFromContact(String email) {

		if (Util.isEmpty(email)) {
			return null;
		}
		JSONObject jsonObject = getJsonFromMailjetAPI(contactEmailURL.replace(":ID", email));
		JSONArray datas = (JSONArray) jsonObject.get("Data");
		JSONObject data = (JSONObject) datas.get(0);
		Long id = (Long) data.get("ID");
		
		return id.toString();
	}
	
	/**
	 * Ajout d'un contact dans un ou plusieurs groupes
	 * 
	 * @param email 
	 * @param groups (ID séparés par des virgules)
	 * @return true si ajout ok, false sinon
	 */

	public static String addContact(String email, String strGroups) {
		
		// Cas si l'email n'est pas déjà crée
		
		JSONObject jsonContact = new JSONObject();
		jsonContact.put("Email", email);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = getJsonFromMailjetAPI(addContactURL, "POST", jsonContact, "application/json");
		} catch (IOException e) {
			// logger.warn("Erreur d'interrogation de l'API de emailing (erreur dans l'url)", e);
		}
		
		JSONObject jsonLists = new JSONObject();
		String[] groups = StringUtils.split(strGroups, ",");
		JSONArray jsonArrayLists = new JSONArray();
		for (String group: groups) {
			JSONObject jsonList = new JSONObject();
			jsonList.put("ListID", group);
			jsonList.put("Action", "addforce");
			jsonArrayLists.add(jsonList);
		}
		jsonLists.put("ContactsLists", jsonArrayLists);
		
		try {
			jsonObject = getJsonFromMailjetAPI(contactListsURL.replace(":ID", getIdFromContact(email)), "POST", jsonLists, "application/json");
		} catch (IOException e) {
			logger.warn("Erreur d'interrogation de l'API de emailing (erreur dans l'url)", e);
		}
		return jsonObject.toString();
	}
	
	/**
	 * Récupère les emails des contacts appartenant à un groupe
	 * 
	 * @param id du groupe
	 * @return emails
	 */
	public static ArrayList<String> getEmailsFromGroup(String id) {

		if (Util.isEmpty(id)) {
			return null;
		}
		JSONObject jsonObject = getJsonFromMailjetAPI(listrecipientURL.replace(":ID", id));
		JSONArray datas = (JSONArray) jsonObject.get("Data");
		ArrayList<String> emails = new ArrayList<String>();
		Iterator i = datas.iterator();
		while (i.hasNext()) {
            JSONObject data = (JSONObject) i.next();
            emails.add(getEmailFromContact(data.get("ContactID").toString()));
        }
		
		return emails;

	}

	/**
	 * Récupère la liste des campagnes (= envois des newsletters)
	 * 
	 * @return Tableau JSON des campagnes
	 */
	public static JSONArray getCampaignsArray() {

		JSONObject jsonObject = getJsonFromMailjetAPI(lastCampaignsURL);
		return (JSONArray) jsonObject.get("Data");

	}
	
	/**
	 * Envoie un mail à un groupe
	 * 
	 * @param groupId ID du groupe
	 * @param senderMail Email de l'expéditeur
	 * @param subject Sujet du mail
	 * @param message Corps du mail en HTML
	 */
	public static void sendMail(String groupId, String senderMail, String senderName, String subject, String message) {
		
		JSONObject jsonMail = new JSONObject();
		jsonMail.put("FromEmail", senderMail);
		jsonMail.put("FromName", senderName);
		jsonMail.put("Subject", subject);
		jsonMail.put("Text-part", HtmlUtil.html2text(message));
		jsonMail.put("Html-part", message);
		jsonMail.put("Recipients", getJsonArrayEmailsFromGroup(groupId));
		JSONObject result = new JSONObject();
		try {
			result = getJsonFromMailjetAPI(sendMailURL, "POST", jsonMail, "application/json");
		} catch (IOException e) {
			logger.warn("Erreur lors de l'envoi du mail par l'API Mailjet", e);
		}
	}

	
	/**
	 * Envoie une newsletter à un groupe
	 * 
	 * @param groupId ID du groupe
	 * @param senderMail Email de l'expéditeur
	 * @param subject Sujet du mail
	 * @param message Corps du mail en HTML
	 */
	public static Integer sendNewsletter(String groupId, String senderMail, String senderName, String subject, String message, boolean isTest) {
		
		// Création de la newsletter
		JSONObject jsonNewsletter = new JSONObject();
		jsonNewsletter.put("Locale", "fr_FR");
		jsonNewsletter.put("Sender", senderName);
		jsonNewsletter.put("SenderEmail", senderMail);
		jsonNewsletter.put("Subject", subject);
		jsonNewsletter.put("ContactsListID", groupId);
		jsonNewsletter.put("Title", subject);
		jsonNewsletter.put("EditMode", "html");
		JSONObject result = new JSONObject();
		String newsletterID = new String();
		try {
			result = getJsonFromMailjetAPI(createNewsletterURL, "POST", jsonNewsletter, "application/json");
			newsletterID = getValueFromMailJetJson(result, "ID");
		} catch (IOException e) {
			logger.warn("Erreur lors de la préparation de la newsletter par l'API Mailjet", e);
			return EXIT_ERROR;
		}
		
		// Ajout d'un corps de texte à la newsletter
		JSONObject jsonBody = new JSONObject();
		jsonBody.put("Html-part", message);
		jsonBody.put("Text-part", HtmlUtil.html2text(message));
		try {
			result = getJsonFromMailjetAPI(addBodyToNewsletterURL.replace(":ID", newsletterID), "POST", jsonBody, "application/json");
		} catch (IOException e) {
			logger.warn("Erreur lors de l'ajout d'un corps de texte à la newsletter par l'API Mailjet", e);
			return EXIT_ERROR;
		}
		
		// Envoi de la newsletter
		try {
			if (isTest) {
				JSONObject jsonRecipients = new JSONObject();
				JSONArray jsonArrayRecipients = new JSONArray();
				JSONObject jsonRecipient = new JSONObject();
				jsonRecipient.put("Email", "support.jcms@loire-atlantique.fr");
				jsonRecipient.put("Name", "Département de Loire-Atlantique");
				jsonArrayRecipients.add(jsonRecipient);
				jsonRecipients.put("Recipients", jsonArrayRecipients);
				result = getJsonFromMailjetAPI(sendTestNewsletterURL.replace(":ID", newsletterID), "POST", jsonRecipients, "application/json");
			} else {
				JSONObject jsonVide = new JSONObject();
				result = getJsonFromMailjetAPI(sendNewsletterURL.replace(":ID", newsletterID), "POST", jsonVide, "application/json");
				
			}
			return EXIT_SUCCESS;
		} catch (IOException e) {
			logger.warn("Erreur lors de l'envoi de la newsletter par l'API Mailjet", e);
			return EXIT_ERROR;
		}
		
	}
	
	/**
	 * Retourne un JSON contenant les emails des contacts du groupe donné
	 * @param groupId ID du groupe
	 * @return JSON avec les emails des contacts
	 */
	private static JSONArray getJsonArrayEmailsFromGroup(String groupId) {

		ArrayList<String> mails = getEmailsFromGroup(groupId);
		JSONArray listmails = new JSONArray();
		for (String mail: mails) {
			JSONObject jsonMail = new JSONObject();
			jsonMail.put("Email", mail);
			listmails.add(jsonMail);
		}
		
		return listmails;
	}


/**
 * Récupère tous les groupes de contacts correspondant à la clé API courante
 * 
 * @return groupes 
 */
public static Map<String, String> getGroupList() {
	JSONObject jsonObject = getJsonFromMailjetAPI(allContactsGroupsURL);
	JSONArray datas = (JSONArray) jsonObject.get("Data");
	Map<String, String> groupList = new TreeMap<String, String>();
	Iterator i = datas.iterator();
	while (i.hasNext()) {
        JSONObject data = (JSONObject) i.next();
        groupList.put(data.get("ID").toString(),data.get("Name").toString());
    }
	
	return groupList;

}	

/**
 * Récupère tous les groupes de contacts de la clé API courante dont les ID sont autorisés
 * 
 * @return groupes autorisés 
 */
public static Map<String, String> getAllowedGroupList() {
	try{
		JSONObject jsonObject = getJsonFromMailjetAPI(allContactsGroupsURL);
		JSONArray datas = (JSONArray) jsonObject.get("Data");
		String[] allowedGroups = Channel.getChannel().getStringArrayProperty("plugin.tools.mailjet.allowedGroupIds", new String[] {});
		Map<String, String> groupList = new TreeMap<String, String>();
		Iterator i = datas.iterator();
		while (i.hasNext()) {
	        JSONObject data = (JSONObject) i.next();
	        // On met le groupe dans la liste s'il est autorisé
	        if (ToolsUtil.inArray(allowedGroups, data.get("ID").toString())) {
	        	groupList.put(data.get("ID").toString(),data.get("Name").toString());
	        }
	    }
		
		return groupList;
	}
	catch(Exception e){
		logger.warn("Erreur lors de la récupération des groupes Mailjet via l'API");
		return null;
		
	}
}

private static String getContactsLists = baseUrl + "/REST/contact/:ID/getcontactslists";
public static boolean GetContactsLists(String email, String[] groups) {

	if (Util.isEmpty(email)) {
		return false;
	}

	JSONObject jsonObject = getJsonFromMailjetAPI(getContactsLists.replace(":ID", email));
	JSONArray datas = (JSONArray) jsonObject.get("Data");
	String[] allowedGroups = groups;
	if(Util.notEmpty(datas)){
 	Iterator i = datas.iterator();
 	while (i.hasNext()) {
 		JSONObject data = (JSONObject) i.next();
 		if (data.containsKey("ListID") && ToolsUtil.inArray(allowedGroups, data.get("ListID").toString())) {
 			return true;
 		}
 	}
	}
	return false;
}




/**
 * Supprimer un contact dans un groupes
 * 
 * @param email 
 * @param groups (ID séparés par des virgules)
 * @return true si ajout ok, false sinon
 */

public static String removeContact(String email, String strGroups) {
 

 JSONObject jsonObject = new JSONObject();
 
 
 JSONObject jsonLists = new JSONObject();
 String[] groups = StringUtils.split(strGroups, ",");
 JSONArray jsonArrayLists = new JSONArray();
 for (String group: groups) {
  JSONObject jsonList = new JSONObject();
  jsonList.put("ListID", group);
  jsonList.put("Action", "remove");
  jsonArrayLists.add(jsonList);
 }
 jsonLists.put("ContactsLists", jsonArrayLists);
 
 try {
  jsonObject = getJsonFromMailjetAPI(contactListsURL.replace(":ID", getIdFromContact(email)), "POST", jsonLists, "application/json");
 } catch (IOException e) {
  logger.warn("Erreur d'interrogation de l'API de emailing (erreur dans l'url)", e);
 }
 return jsonObject.toString();
}


}
