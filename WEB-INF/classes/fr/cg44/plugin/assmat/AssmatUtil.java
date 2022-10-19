package fr.cg44.plugin.assmat;

import static com.jalios.jcms.Channel.getChannel;
import fr.cg44.plugin.administrabletexteplugin.tag.TrsbGlp;
import fr.cg44.plugin.assmat.api.json.beans.Feature;
import fr.cg44.plugin.assmat.api.json.beans.Properties;
import fr.cg44.plugin.assmat.api.json.beans.ResponseAPIAdress;
import fr.cg44.plugin.assmat.beans.AssmatSearch;
import fr.cg44.plugin.assmat.managers.ProfilManager;
import fr.cg44.plugin.assmat.managers.SmsDAO;
import fr.cg44.plugin.socle.SocleUtils;
//import fr.cg44.plugin.tools.facetedsearch.exception.UnknowCityException;
import fr.cg44.plugin.tools.mailjet.MailjetClient;
import fr.trsb.cd44.solis.beans.AssmatSolis;
import fr.trsb.cd44.solis.manager.SolisManager;
import generated.City;
import generated.Disponibilite;
import generated.InscriptionAM;
import generated.ProfilASSMAT;

import org.apache.commons.codec.binary.Base64;

import io.swagger.client.ApiClient;
import io.swagger.client.api.DeclarationAccueilServiceApi;
import io.swagger.client.api.JasperControllerApi;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.mail.MessagingException;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.cfg.Configuration;

import com.jalios.jcms.Category;
import com.jalios.jcms.Channel;
import com.jalios.jcms.Group;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.Member;
import com.jalios.jcms.Publication;
import com.jalios.jcms.mail.MailMessage;
import com.jalios.util.HttpClientUtils;
import com.jalios.util.JProperties;
import com.jalios.util.MailUtil;
import com.jalios.util.ObjectIntTreeMap;
import com.jalios.util.Util;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class AssmatUtil {

	private static final Logger logger = Logger.getLogger(AssmatUtil.class);

	private static final Channel channel = Channel.getChannel();
	
	private static final ApiClient API_CLIENT = new ApiClient();

	private static final String PWD_REGEX = getChannel().getProperty("plugin.assmatplugin.pwd.regex");


	private static final String idGroupeRAM = Channel.getChannel().getProperty("jcmsplugin.assmatplugin.group.ram.id");

	private static final Group groupRAM = Channel.getChannel().getGroup(idGroupeRAM);

	private static final String idGroupeASSO = Channel.getChannel().getProperty("jcmsplugin.assmatplugin.group.asso.id");

	private static final Group groupASSO = Channel.getChannel().getGroup(idGroupeASSO);
	
	private static final boolean synchroOK = Channel.getChannel().getBooleanProperty("jcmsplugin.assmatplugin.synchroAM", true);

	// valueList="true|false|none" labelList="Oui|Non|Aucun"
	public static enum Selection {
		SELECTION_OUI(ProfilASSMAT.getVisbiliteTelephoneFixeValues()[0], ProfilASSMAT.getVisbiliteTelephoneFixeLabels()[0]), SELECTION_NON(ProfilASSMAT.getVisbiliteTelephoneFixeValues()[1], ProfilASSMAT.getVisbiliteTelephoneFixeLabels()[1]), SELECTION_NONE(ProfilASSMAT.getVisbiliteTelephoneFixeValues()[2], ProfilASSMAT.getVisbiliteTelephoneFixeLabels()[2]);

		private String label;
		private String value;

		Selection(String value, String label) {
			this.label = label;
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public String getValue() {
			return value;
		}
	}

	/**
	 * Enumeration pour le choix de login
	 * 
	 * @author c.rabiller
	 *
	 */
	public static enum SelectionLogin {
		TELEPHONE(InscriptionAM.getPreferenceDeLoginLabels()[0], InscriptionAM.getPreferenceDeLoginValues()[0]), MAIL(InscriptionAM.getPreferenceDeLoginLabels()[1], InscriptionAM.getPreferenceDeLoginValues()[1]), NUMERO_DOSSIER(InscriptionAM.getPreferenceDeLoginLabels()[2], InscriptionAM.getPreferenceDeLoginValues()[2]);

		private String label;
		private String value;

		SelectionLogin(String label, String value) {
			this.label = label;
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public String getValue() {
			return value;
		}

		public static SelectionLogin getTypeLoginByValue(final String typeLogin) {
			for (final SelectionLogin type : SelectionLogin.values()) {
				if (type.getValue().equalsIgnoreCase(typeLogin)) {
					return type;
				}
			}
			return null;
		}
	}

	/**
	 * Enumeration pour les dispo
	 * 
	 *
	 */
	public static enum SelectionEtatDispo {
		INCONNU(Disponibilite.getEtatDispoLabels()[0], Disponibilite.getEtatDispoValues()[0]), IMMEDIATE(Disponibilite.getEtatDispoLabels()[1], Disponibilite.getEtatDispoValues()[1]), FUTURE(Disponibilite.getEtatDispoLabels()[2], Disponibilite.getEtatDispoValues()[2]), NON_DISPO(Disponibilite.getEtatDispoLabels()[3], Disponibilite.getEtatDispoValues()[3]);

		private String label;
		private String value;

		SelectionEtatDispo(String label, String value) {
			this.label = label;
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public String getValue() {
			return value;
		}

		public static SelectionEtatDispo getEtatDispo(final String etatDispo) {
			for (final SelectionEtatDispo type : SelectionEtatDispo.values()) {
				if (type.getValue().equalsIgnoreCase(etatDispo)) {
					return type;
				}
			}
			return null;
		}
	}

	/**
	 * Enumeration pour les dispo {@link InscriptionAM}
	 * preferenceReceptionMessageDepart {@link ProfilASSMAT}
	 * canalDeCommunicationSite
	 */
	public static enum SelectionPreferenceReception {
		TELEPHONE(InscriptionAM.getPreferenceReceptionMessageDepartLabels()[0], InscriptionAM.getPreferenceReceptionMessageDepartValues()[0]), MAIL(InscriptionAM.getPreferenceReceptionMessageDepartLabels()[1], InscriptionAM.getPreferenceReceptionMessageDepartValues()[1]);

		private String label;
		private String value;

		SelectionPreferenceReception(String label, String value) {
			this.label = label;
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public String getValue() {
			return value;
		}

		public static SelectionPreferenceReception getTypeLoginByValue(final String typeLogin) {
			for (final SelectionPreferenceReception type : SelectionPreferenceReception.values()) {
				if (type.getValue().equalsIgnoreCase(typeLogin)) {
					return type;
				}
			}
			return null;
		}
	}

	private static final String TELEPHONE_PATTERN = "\\d{10}|\\d\\d \\d\\d \\d\\d \\d\\d \\d\\d";

	/**
	 * Verifie si une chaine de caractere est bien au format telephone
	 * 
	 * @param phone
	 * @return true si oui false si non
	 */
	public static boolean checkPhone(String phone) {
		if (Util.notEmpty(phone)) {
			if (Pattern.matches(TELEPHONE_PATTERN, phone)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Verifie si une chaine de caractere contient bien une lettre majuscule, 8
	 * caracteres et un chiffre
	 * 
	 * @return true si oui false si non
	 */
	public static boolean checkPassword(String password) {
		if (Util.notEmpty(password)) {
			if (Pattern.matches(PWD_REGEX, password)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Verifie si une chaine de caractere est bien au format mail
	 * 
	 * @param email
	 * @return true si oui false si non
	 */
	public static boolean checkEmail(String email) {
		if (Util.notEmpty(email)) {
			if(MailUtil.checkEmail(email)){
				return true;
			}
		}
		return false;
	}

	public static String getMessage(String nameProperty) {
		String message = getMessage(nameProperty, false);

		return message;
	}

	public static String getMessage(String nameProperty, boolean showEditButton) {
		TrsbGlp tag = new TrsbGlp();
		tag.setShowButtonEdit(showEditButton);
		tag.setKey(nameProperty);
		String message = tag.getHtmlLibelle();

		return message;
	}

	public static String getMessagePropertiesParametersValues(String nameProperty, String[] parametersValue) {
		TrsbGlp tag = new TrsbGlp();
		tag.setShowButtonEdit(false);
		tag.setKey(nameProperty);
		tag.setParameter(parametersValue);
		String message = tag.getHtmlLibelle();

		return message;
	}

	public static String getMessagePropertiesParameters(String nameProperty, String[] parametersProperty) {
		String[] propertyResult = new String[] {};
		int cpt = 0;
		for (String property : parametersProperty) {
			String libelle = getMessage(property);
			if (Util.notEmpty(libelle)) {
				propertyResult = (String[]) Util.insertArray(propertyResult, cpt, libelle);
			}
			cpt++;
		}

		String message = getMessagePropertiesParametersValues(nameProperty, propertyResult);

		return message;
	}

	public static boolean ajoutMailJet(String email, String strGroups) {


		String mailJet = MailjetClient.addContact(email, strGroups);

		return getBooleanFromString(mailJet);
	}


	/**
	 * Permet de supprimer un contact dans un groupe 
	 * @param email
	 * @param strGroups
	 * @return
	 */
	public static boolean removeMailJet(String email, String strGroups) {
		String tableauMail[] = {strGroups};
		if(isContainsGroupsMailJet(email, tableauMail)) {
			String mailJet = MailjetClient.removeContact(email, strGroups);
			return getBooleanFromString(mailJet);
		}
		return false;
	}

	public static boolean isContainsGroupsMailJet(String mail, String[] groups){
		if(Util.isEmpty(groups) || groups.length == 0){
			return false;
		}
		return MailjetClient.GetContactsLists(mail, groups);
	}

	public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;

		dist = dist * 1.609344;

		return dist;
	}

	private static double deg2rad(double deg) {
		return deg * Math.PI / 180.0;
	}

	private static double rad2deg(double rad) {
		return rad * 180 / Math.PI;
	}




	/**
	 * Retourne la date de debut
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateDebut(Date date) {
		Date dateDebut = new Date(date.getTime());
		dateDebut.setHours(0);
		dateDebut.setMinutes(0);
		dateDebut.setSeconds(0);

		return dateDebut;
	}

	/**
	 * Retourne la date de fin
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateFin(Date date) {
		Date dateFin = new Date(date.getTime());
		dateFin.setHours(23);
		dateFin.setMinutes(59);
		dateFin.setSeconds(59);

		return dateFin;
	}

	public static Date getDateDebutMonth(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);

		return cal.getTime();
	}

	/**
	 * Retourne la date de fin
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateFinMonth(Date date) {
		date = getDateFin(date);
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);

		int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, max);

		return cal.getTime();
	}

	public static ObjectIntTreeMap getDisposSearch(Integer place1TrancheAgeKey, Integer place2TrancheAgeKey, Integer place3TrancheAgeKey, Integer place4TrancheAgeKey) {
		ObjectIntTreeMap map = new ObjectIntTreeMap();
		if (Util.notEmpty(place1TrancheAgeKey))
			map.inc(place1TrancheAgeKey);

		if (Util.notEmpty(place2TrancheAgeKey))
			map.inc(place2TrancheAgeKey);

		if (Util.notEmpty(place3TrancheAgeKey))
			map.inc(place3TrancheAgeKey);

		if (Util.notEmpty(place4TrancheAgeKey))
			map.inc(place4TrancheAgeKey);

		return map;
	}

	/**
	 * Converti un string en boolean
	 * 
	 * @param value
	 * @return
	 */
	public static Boolean getBooleanFromString(String value) {
		if ("true".equalsIgnoreCase(value))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public static String toString(AssmatSolis solis) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		StringBuilder builder = new StringBuilder();
		if (Util.notEmpty(solis)) {
			builder.append("<h4>Récapitulatif des informations du compte</h4>").append("<ul>").append("<li>JRowId : ").append(Util.notEmpty(solis.getJRowId()) ? solis.getJRowId() : "").append("</li>").append("<li>civiliteAssmat : ").append(Util.notEmpty(solis.getCiviliteAssmat()) ? solis.getCiviliteAssmat() : "").append("</li>").append("<li>nomAssmat : ").append(Util.notEmpty(solis.getNomAssmat()) ? solis.getNomAssmat() : "").append("</li>").append("<li>prenomAssmat : ")
			.append(Util.notEmpty(solis.getPrenomAssmat()) ? solis.getPrenomAssmat() : "").append("</li>").append("<li>dateNaissAssmat : ").append(Util.notEmpty(solis.getDateNaissAssmat()) ? sdf.format(solis.getDateNaissAssmat()) : "").append("</li>").append("<li>numDossierAssmat : ").append(Util.notEmpty(solis.getNumDossierAssmat()) ? solis.getNumDossierAssmat() : "").append("</li>").append("<li>telPrincipal : ")
			.append(Util.notEmpty(solis.getTelPrincipal()) ? solis.getTelPrincipal() : "").append("</li>").append("<li>telPortable : ").append(Util.notEmpty(solis.getTelPortable()) ? solis.getTelPortable() : "").append("</li>").append("<li>emailAssmat : ").append(Util.notEmpty(solis.getEmailAssmat()) ? solis.getEmailAssmat() : "").append("</li>").append("<li>datePremierAgrement : ").append(Util.notEmpty(solis.getDatePremierAgrement()) ? sdf.format(solis.getDatePremierAgrement()) : "")
			.append("</li>").append("<li>formationInitiale : ").append(Util.notEmpty(solis.getFormationInitiale()) ? solis.getFormationInitiale() : "").append("</li>").append("<li>dateDernierRenouvellement : ").append(Util.notEmpty(solis.getDateDernierRenouvellement()) ? sdf.format(solis.getDateDernierRenouvellement()) : "").append("</li>").append("<li>dateProchainRenouvellement : ")
			.append(Util.notEmpty(solis.getDateProchainRenouvellement()) ? sdf.format(solis.getDateProchainRenouvellement()) : "").append("</li>").append("<li>dateDernierRenouvellementMam : ").append(Util.notEmpty(solis.getDateDernierRenouvellementMam()) ? sdf.format(solis.getDateDernierRenouvellementMam()) : "").append("</li>").append("<li>dateProchainRenouvellementMam : ")
			.append(Util.notEmpty(solis.getDateProchainRenouvellementMam()) ? sdf.format(solis.getDateProchainRenouvellementMam()) : "").append("</li>").append("<li>adresseDomicile : ").append(Util.notEmpty(solis.getAdresseDomicile()) ? solis.getAdresseDomicile() : "").append("</li>").append("<li>cpDomicile : ").append(Util.notEmpty(solis.getCpDomicile()) ? solis.getCpDomicile() : "").append("</li>").append("<li>communeDomicile : ")
			.append(Util.notEmpty(solis.getCommuneDomicile()) ? solis.getCommuneDomicile() : "").append("</li>").append("<li>exerceMam : ").append(Util.notEmpty(solis.getExerceMam()) ? solis.getExerceMam() : "").append("</li>").append("<li>nomMam : ").append(Util.notEmpty(solis.getNomMam()) ? solis.getNomMam() : "").append("</li>").append("<li>adresseMam : ").append(Util.notEmpty(solis.getAdresseMam()) ? solis.getAdresseMam() : "").append("</li>").append("<li>cpMam : ")
			.append(Util.notEmpty(solis.getCpMam()) ? solis.getCpMam() : "").append("</li>").append("<li>communeMam : ").append(Util.notEmpty(solis.getCommuneMam()) ? solis.getCommuneMam() : "").append("</li>").append("<li>enActivite : ").append(Util.notEmpty(solis.getEnActivite()) ? solis.getEnActivite() : "").append("</li>").append("<li>place1AgrementTrancheAgeKey : ").append(Util.notEmpty(solis.getPlace1AgrementTrancheAgeKey()) ? solis.getPlace1AgrementTrancheAgeKey() : "")
			.append("</li>").append("<li>place1TrancheAgeKey : ").append(Util.notEmpty(solis.getPlace1TrancheAgeKey()) ? solis.getPlace1TrancheAgeKey() : "").append("</li>").append("<li>place1TrancheAge : ").append(Util.notEmpty(solis.getPlace1TrancheAge()) ? solis.getPlace1TrancheAge() : "").append("</li>").append("<li>place1LibCompl : ").append(Util.notEmpty(solis.getPlace1LibCompl()) ? solis.getPlace1LibCompl() : "").append("</li>").append("<li>place1NbPlaces : ")
			.append(Util.notEmpty(solis.getPlace1NbPlaces()) ? solis.getPlace1NbPlaces() : "").append("</li>").append("<li>place1SaisieDisponibilite : ").append(Util.notEmpty(solis.getPlace1SaisieDisponibilite()) ? solis.getPlace1SaisieDisponibilite() : "").append("</li>").append("<li>place2AgrementTrancheAgeKey : ").append(Util.notEmpty(solis.getPlace2AgrementTrancheAgeKey()) ? solis.getPlace2AgrementTrancheAgeKey() : "").append("</li>").append("<li>place2TrancheAgeKey : ")
			.append(Util.notEmpty(solis.getPlace2TrancheAgeKey()) ? solis.getPlace2TrancheAgeKey() : "").append("</li>").append("<li>place2TrancheAge : ").append(Util.notEmpty(solis.getPlace2TrancheAge()) ? solis.getPlace2TrancheAge() : "").append("</li>").append("<li>place2LibCompl : ").append(Util.notEmpty(solis.getPlace2LibCompl()) ? solis.getPlace2LibCompl() : "").append("</li>").append("<li>place2NbPlaces : ")
			.append(Util.notEmpty(solis.getPlace2NbPlaces()) ? solis.getPlace2NbPlaces() : "").append("</li>").append("<li>place2SaisieDisponibilite : ").append(Util.notEmpty(solis.getPlace2SaisieDisponibilite()) ? solis.getPlace2SaisieDisponibilite() : "").append("</li>").append("<li>place3AgrementTrancheAgeKey : ").append(Util.notEmpty(solis.getPlace3AgrementTrancheAgeKey()) ? solis.getPlace3AgrementTrancheAgeKey() : "").append("</li>").append("<li>place3TrancheAgeKey : ")
			.append(Util.notEmpty(solis.getPlace3TrancheAgeKey()) ? solis.getPlace3TrancheAgeKey() : "").append("</li>").append("<li>place3TrancheAge : ").append(Util.notEmpty(solis.getPlace3TrancheAge()) ? solis.getPlace3TrancheAge() : "").append("</li>").append("<li>place3LibCompl : ").append(Util.notEmpty(solis.getPlace3LibCompl()) ? solis.getPlace3LibCompl() : "").append("</li>").append("<li>place3NbPlaces : ")
			.append(Util.notEmpty(solis.getPlace3NbPlaces()) ? solis.getPlace3NbPlaces() : "").append("</li>").append("<li>place3SaisieDisponibilite : ").append(Util.notEmpty(solis.getPlace3SaisieDisponibilite()) ? solis.getPlace3SaisieDisponibilite() : "").append("</li>").append("<li>place4AgrementTrancheAgeKey : ").append(Util.notEmpty(solis.getPlace4AgrementTrancheAgeKey()) ? solis.getPlace4AgrementTrancheAgeKey() : "").append("</li>").append("<li>place4TrancheAgeKey : ")
			.append(Util.notEmpty(solis.getPlace4TrancheAgeKey()) ? solis.getPlace4TrancheAgeKey() : "").append("</li>").append("<li>place4TrancheAge : ").append(Util.notEmpty(solis.getPlace4TrancheAge()) ? solis.getPlace4TrancheAge() : "").append("</li>").append("<li>place4LibCompl : ").append(Util.notEmpty(solis.getPlace4LibCompl()) ? solis.getPlace4LibCompl() : "").append("</li>").append("<li>place4NbPlaces : ")
			.append(Util.notEmpty(solis.getPlace4NbPlaces()) ? solis.getPlace4NbPlaces() : "").append("</li>").append("<li>place4SaisieDisponibilite : ").append(Util.notEmpty(solis.getPlace4SaisieDisponibilite()) ? solis.getPlace4SaisieDisponibilite() : "").append("</li>").append("<li>place5AgrementTrancheAgeKey : ").append(Util.notEmpty(solis.getPlace5AgrementTrancheAgeKey()) ? solis.getPlace5AgrementTrancheAgeKey() : "").append("</li>").append("<li>place5TrancheAgeKey : ")
			.append(Util.notEmpty(solis.getPlace5TrancheAgeKey()) ? solis.getPlace5TrancheAgeKey() : "").append("</li>").append("<li>place5TrancheAge : ").append(Util.notEmpty(solis.getPlace5TrancheAge()) ? solis.getPlace5TrancheAge() : "").append("</li>").append("<li>place5LibCompl : ").append(Util.notEmpty(solis.getPlace5LibCompl()) ? solis.getPlace5LibCompl() : "").append("</li>").append("<li>place5NbPlaces : ")
			.append(Util.notEmpty(solis.getPlace5NbPlaces()) ? solis.getPlace5NbPlaces() : "").append("</li>").append("<li>place5SaisieDisponibilite : ").append(Util.notEmpty(solis.getPlace5SaisieDisponibilite()) ? solis.getPlace5SaisieDisponibilite() : "").append("</li>").append("<li>place6AgrementTrancheAgeKey : ").append(Util.notEmpty(solis.getPlace6AgrementTrancheAgeKey()) ? solis.getPlace6AgrementTrancheAgeKey() : "").append("</li>").append("<li>place6TrancheAgeKey : ")
			.append(Util.notEmpty(solis.getPlace6TrancheAgeKey()) ? solis.getPlace6TrancheAgeKey() : "").append("</li>").append("<li>place6TrancheAge : ").append(Util.notEmpty(solis.getPlace6TrancheAge()) ? solis.getPlace6TrancheAge() : "").append("</li>").append("<li>place6LibCompl : ").append(Util.notEmpty(solis.getPlace6LibCompl()) ? solis.getPlace6LibCompl() : "").append("</li>").append("<li>place6NbPlaces : ")
			.append(Util.notEmpty(solis.getPlace6NbPlaces()) ? solis.getPlace6NbPlaces() : "").append("</li>").append("<li>place6SaisieDisponibilite : ").append(Util.notEmpty(solis.getPlace6SaisieDisponibilite()) ? solis.getPlace6SaisieDisponibilite() : "").append("</li>").append("<li>place7AgrementTrancheAgeKey : ").append(Util.notEmpty(solis.getPlace7AgrementTrancheAgeKey()) ? solis.getPlace7AgrementTrancheAgeKey() : "").append("</li>").append("<li>place7TrancheAgeKey : ")
			.append(Util.notEmpty(solis.getPlace7TrancheAgeKey()) ? solis.getPlace7TrancheAgeKey() : "").append("</li>").append("<li>place7TrancheAge : ").append(Util.notEmpty(solis.getPlace7TrancheAge()) ? solis.getPlace7TrancheAge() : "").append("</li>").append("<li>place7LibCompl : ").append(Util.notEmpty(solis.getPlace7LibCompl()) ? solis.getPlace7LibCompl() : "").append("</li>").append("<li>place7NbPlaces : ")
			.append(Util.notEmpty(solis.getPlace7NbPlaces()) ? solis.getPlace7NbPlaces() : "").append("</li>").append("<li>place7SaisieDisponibilite : ").append(Util.notEmpty(solis.getPlace7SaisieDisponibilite()) ? solis.getPlace7SaisieDisponibilite() : "").append("</li>").append("<li>place8AgrementTrancheAgeKey : ").append(Util.notEmpty(solis.getPlace8AgrementTrancheAgeKey()) ? solis.getPlace8AgrementTrancheAgeKey() : "").append("</li>").append("<li>place8TrancheAgeKey : ")
			.append(Util.notEmpty(solis.getPlace8TrancheAgeKey()) ? solis.getPlace8TrancheAgeKey() : "").append("</li>").append("<li>place8TrancheAge : ").append(Util.notEmpty(solis.getPlace8TrancheAge()) ? solis.getPlace8TrancheAge() : "").append("</li>").append("<li>place8LibCompl : ").append(Util.notEmpty(solis.getPlace8LibCompl()) ? solis.getPlace8LibCompl() : "").append("</li>").append("<li>place8NbPlaces : ")
			.append(Util.notEmpty(solis.getPlace8NbPlaces()) ? solis.getPlace8NbPlaces() : "").append("</li>").append("<li>place8SaisieDisponibilite : ").append(Util.notEmpty(solis.getPlace8SaisieDisponibilite()) ? solis.getPlace8SaisieDisponibilite() : "").append("</li>").append("<li>statut : ").append(Util.notEmpty(solis.getStatut()) ? solis.getStatut() : "").append("</li>").append("<li>autorisationActivation : ")
			.append(Util.notEmpty(solis.getAutorisationActivation()) ? solis.getAutorisationActivation() : "").append("</li>").append("<li>latitude : ").append(Util.notEmpty(solis.getLatitude()) ? solis.getLatitude() : "").append("</li>").append("<li>longitude : ").append(Util.notEmpty(solis.getLongitude()) ? solis.getLongitude() : "").append("</li>").append("<li>latitudeMam : ").append(Util.notEmpty(solis.getLatitudeMam()) ? solis.getLatitudeMam() : "").append("</li>")
			.append("<li>longitudeMam : ").append(Util.notEmpty(solis.getLongitudeMam()) ? solis.getLongitudeMam() : "").append("</li>").append("<li>idUa : ").append(Util.notEmpty(solis.getIdUa()) ? solis.getIdUa() : "").append("</li>").append("<li>exerceDomicile : ").append(Util.notEmpty(solis.getExerceDomicile()) ? solis.getExerceDomicile() : "").append("</li>").append("<li>aideCaf : ").append(Util.notEmpty(solis.getAideCaf()) ? solis.getAideCaf() : "").append("</li></ul>");
		}

		return builder.toString();
	}

	/**
	 * Envoi le message par mail ou portable suivant la configuration du profil avec un contenu mail et SMS identique
	 * assmat
	 * 
	 * @param profil
	 * @param subject
	 * @param content
	 * @param adresseFrom
	 */
	public static void sendMessage(ProfilASSMAT profil, String subject, String content, String adresseFrom) {		
		sendMessage(profil, subject, content, content, adresseFrom);	
	}


	/**
	 * Envoi le message par mail ou portable suivant la configuration du profil avec un contenu mail et SMS différent
	 * assmat
	 * 
	 * @param profil
	 * @param subject
	 * @param content
	 * @param adresseFrom
	 */
	public static void sendMessage(ProfilASSMAT profil, String subject, String contentMail, String contentSms, String adresseFrom) {
		// Envoi pas mail
		if (SelectionPreferenceReception.MAIL.getValue().equals(profil.getCanalDeCommunicationSite())) {
			sendMail(profil.getAuthor().getEmail(), subject, contentMail, adresseFrom);
		} else if (SelectionPreferenceReception.TELEPHONE.getValue().equals(profil.getCanalDeCommunicationSite())) {
			SmsDAO smsDao = new SmsDAO();
			smsDao.sendSMS(profil, subject, contentSms, contentSms);
		}
	}


	public static void sendMail(String to, String subject, String content, String from) {
		sendMail(to, subject, content, from, null);
	}


	public static void sendMail(String to, String subject, String content, String from, String replyTo) {
		HashSet<String> setTo = new HashSet<String>();
		setTo.add(to);
		content = retourALaLigne(content);
		sendMail(setTo, subject, content, from, replyTo);

	}


	/**
	 * Retourne une chaine de caractere avec des balise br au lieu des \n
	 * @param content la string a modifier
	 * @return un str avec des balises "br" a la place de \n
	 */
	private static String retourALaLigne(String content){
		return Util.replaceAll(content, "\n", "<br />");
	}

	/**
	 * méthode permettant de nous retourner le login choisi d'un formulaire
	 * d'inscription
	 * 
	 * @param inscription
	 * @return
	 */
	public static String getLoginFromInscriptionAM(InscriptionAM inscription) {
		if (Util.notEmpty(inscription)) {
			if (SelectionLogin.TELEPHONE.equals(SelectionLogin.getTypeLoginByValue(inscription.getPreferenceDeLogin()))) {
				return inscription.getTelephonePortable();
			}
			if (SelectionLogin.MAIL.equals(SelectionLogin.getTypeLoginByValue(inscription.getPreferenceDeLogin()))) {
				return inscription.getAdresseMail();
			}
			if (SelectionLogin.NUMERO_DOSSIER.equals(SelectionLogin.getTypeLoginByValue(inscription.getPreferenceDeLogin()))) {
				return Integer.toString(inscription.getNumeroDagrement());
			}
		}
		return null;
	}

	/**
	 * Envoi d'un email
	 * 
	 * @param to
	 * @param subject
	 * @param content
	 * @param from
	 */
	public static void sendMail(Set<String> to, String subject, String content, String from) {
		sendMail(to, subject, content, from, null);
	}


	public static void sendMail(Set<String> to, String subject, String content, String from, String replyTo) {
		// Création du mail
		MailMessage msg = new MailMessage();
		msg.setTo(to);
		msg.setFrom(from);
		if(Util.notEmpty(replyTo)) {
			msg.setReplyTo(replyTo);
		}
		msg.setSubject(subject);
		content = retourALaLigne(content);
		msg.setContentHtml(content);
		try {
			msg.send();
		} catch (MessagingException e) {
			logger.error(JcmsUtil.glpd("jcmsplugin.assmatplugin.inscription.error.mail.exception"),e);
		}   
	}

	/**
	 * Création d'une disponibilité à l'état inconnu
	 * 
	 * @param member
	 * @param keyPlace
	 * @param libellePlace
	 */
	public static void createDispo(Member member, String agrement, Integer keyPlace, String libellePlace, String libelleCompl, Boolean saisie, ProfilASSMAT profilAM) {		
		Disponibilite dispo = new Disponibilite();  		
		dispo.setTitle(AssmatUtil.getTitleDispo(dispo.getTitle(), member, profilAM.getCommune(), profilAM.getCommuneMam(), profilAM.getNum_agrement()+"", agrement, keyPlace+""));
		dispo.setAgrement(agrement);
		dispo.setTrancheDage(keyPlace);
		dispo.setAuthor(member);
		dispo.setEtatDispo(AssmatUtil.SelectionEtatDispo.INCONNU.getValue());
		if (Util.notEmpty(libellePlace)) {
			dispo.setLibelle(libellePlace);
		}
		if (Util.notEmpty(libelleCompl)) {
			dispo.setLibelleComplementaire(libelleCompl);
		}
		dispo.setSaisieDisponible(saisie);
		dispo.performCreate(member);
		logger.debug("La disponibilite <"+dispo.getTitle()+"> / <"+dispo.getId()+"> a été crée avec le membre <"+member+"> / <"+member.getId()+">");
	}

	/**
	 * Création de plusieurs disponibilités
	 * 
	 * @param mbr
	 * @param placeTracheAgeKey
	 * @param libellePlace
	 * @param placeNbPlaces
	 *            Nombre de disponibilité a créer
	 */
	public static void createManyDispo(Member mbr, String agrement, Integer placeTracheAgeKey, String libellePlace, String libelleCompl, Integer placeNbPlaces, Boolean saisie, ProfilASSMAT profil) {
		// Création des dispos
		for (int i = 0; i < placeNbPlaces; i++) {
			createDispo(mbr, agrement, placeTracheAgeKey, libellePlace, libelleCompl, saisie, profil);
		}
	}
	
	
	/**
	 * Initialisation de la conf pour la connexion par data source
	 * @param cfg
	 * @return
	 */
	public static Configuration initPropertiesDataSource(Configuration cfg) {
		Channel channel = Channel.getChannel();
		if (channel == null) {
			return cfg;
		}
		cfg.setProperty("hibernate.connection.datasource", channel.getProperty("hibernate.cfg.postgresql.prop.hibernate.connection.datasource"));
		cfg.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
		cfg.setProperty("hibernate.dialect", "org.hibernatespatial.postgis.PostgisDialect");
		//cfg.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
		cfg.setProperty("hibernate.current_session_context_class", "thread");
		cfg.setProperty("hibernate.cache.use_second_level_cache", "true");
		cfg.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.EhCacheProvider");
		cfg.setProperty("hibernate.cache.use_query_cache", "true");
		return cfg;
	}

	
	public static Configuration initProperties(Configuration cfg) {
		Channel channel = Channel.getChannel();
		if (channel == null) {
			return cfg;
		}
		boolean userJunit = Util.toBoolean(System.getProperty("jalios.junit"), false);

		String typeDatabase = channel.getProperty("channel.jcmsdb");
		if (userJunit) {
			typeDatabase = typeDatabase + "-unit";
		}

		logger.info("jcmsdb profile: " + typeDatabase);

		Map<String, Object> databaseInfoMap = channel.getSubProperties("hibernate.cfg." + typeDatabase + ".");
		Map<String, Object> commonInfoMap = channel.getSubProperties("hibernate.cfg.common.");

		JProperties jProperties = new JProperties();
		Object propertiesDatabase = databaseInfoMap.get("prop");
		Object propertiesCommon = commonInfoMap.get("prop");

		if (propertiesDatabase instanceof JProperties)
			jProperties.addProperties((JProperties) propertiesDatabase);
		if (propertiesCommon instanceof JProperties)
			jProperties.addProperties((JProperties) propertiesCommon);

		jProperties.remove("hibernate.connection.datasource");
		String urlDatabase = jProperties.getProperty("hibernate.connection.url");
		logger.info("hibernate.connection.url: " + (String) urlDatabase);

		String password = Util.reveal(jProperties.getProperty("hibernate.connection.password", null));
		if (password != null) {
			jProperties.put("hibernate.connection.password", password);
		}

		if (userJunit) {
			jProperties.setProperty("hibernate.hbm2ddl.auto", "create");
		}

		for (Iterator<Entry<String, String>> it = jProperties.entrySet().iterator(); it.hasNext();) {
			Entry itObj = it.next();
			String key = (String) itObj.getKey();
			String value = ((String) itObj.getValue()).trim();

			cfg.setProperty(key, value);
			if (logger.isDebugEnabled()) {
				logger.debug("init(): set property " + key + ": " + value);
			}

		}

		System.setProperty("net.sf.ehcache.skipUpdateCheck", "true");

		return cfg;
	}

	/**
	 * 
	 * @param oldTitle
	 * @param member
	 * @param commune
	 * @param numeroAgrement
	 * @return
	 */
	public static String getTitleProfilAssmat(String oldTitle, Member member, String communeDomicile, String communeMam, String numeroAgrement){
		try {
			StringBuilder stbd = new StringBuilder();
			if(Util.notEmpty(member))
				stbd.append(SolisManager.clean(member.getFullName()));

			if(Util.notEmpty(communeDomicile))
				stbd.append(" - "+communeDomicile);

			if(Util.notEmpty(communeMam))
				stbd.append(" - "+communeMam);

			if(Util.notEmpty(numeroAgrement))
				stbd.append(" - "+numeroAgrement);

			return stbd.toString();
		} catch (Exception e){
			logger.debug(e.getMessage(),e);
			return oldTitle;
		}
	}

	/**
	 * 
	 * @param oldTitle
	 * @param member
	 * @param commune
	 * @param numeroAgrement
	 * @param trancheAge
	 * @param ordre
	 * @return
	 */
	public static String getTitleDispo(String oldTitle, Member member, String commune, String communeMam, String numeroAgrement, String agrementId, String ageKey){
		try {
			StringBuilder stbd = new StringBuilder();
			if(Util.notEmpty(member))
				stbd.append(SolisManager.clean(member.getFullName()));

			if(Util.notEmpty(commune))
				stbd.append(" - "+commune);

			if(Util.notEmpty(communeMam))
				stbd.append(" - "+communeMam);

			if(Util.notEmpty(numeroAgrement))
				stbd.append(" - "+numeroAgrement);

			if(Util.notEmpty(agrementId))
				stbd.append(" - "+agrementId);

			if(Util.notEmpty(ageKey))
				stbd.append(" - "+ageKey);
			return stbd.toString();
		} catch (Exception e){
			logger.debug(e.getMessage(),e);
			return oldTitle;
		}
	}


	public static String getTitlePlace(String lib, String libCompl, Integer tranche) {
		StringBuilder stbd = new StringBuilder();
		if(tranche != 10 && Util.notEmpty(lib)){
			stbd.append("de");
			stbd.append(" " + lib);
		}
		if(Util.notEmpty(libCompl)) {
			stbd.append(" " + libCompl);
		}
		return stbd.toString();
	}

	/**
	 * Méthode permettant de synchroniser le login du membre avec ses préférences
	 * Note SGU 18/11/2021 : pour des tests on peut vouloir modifier les tel / email d'assmat sans que
	 * cela déclenche la procédure de synchro avec envoir de mail / SMS.
	 * On ajoute donc une prop au site pour empêche cela.
	 * @param member
	 */
	public static void synchroLoginMember(Member member){
		ProfilManager profilManager = ProfilManager.getInstance();  
		ProfilASSMAT profil = profilManager.getProfilASSMAT(member);
		if(Util.notEmpty(member) && Util.notEmpty(profil)){
			String loginMember = member.getLogin();
			// Récupère le login renseigné dans le profil AM suivant le choix Type login
			String loginProfil = profilManager.getLogin(profil);
			if(!loginMember.equals(loginProfil) && Util.notEmpty(loginProfil) && synchroOK) {
				Member clone = (Member) member.clone();
				clone.setLogin(loginProfil);
				clone.performUpdate(getChannel().getDefaultAdmin());

				if(Util.notEmpty(getGroupAssmat()) && Util.notEmpty(getGroupAssmat().getMemberSet()) && Util.notEmpty(getGroupAssmat().getMemberSet().contains(member))){
					// Envoi d'un message de changement de login
					String subject = AssmatUtil.getMessagePropertiesParametersValues("INSCRIPTION-PROFIL-CHANGE-LOGIN-SUBJECT-MAIL", new String[]{member.getSalutation(), member.getName(), member.getFirstName(), loginProfil});
					String content = AssmatUtil.getMessagePropertiesParametersValues("INSCRIPTION-PROFIL-CHANGE-LOGIN-CONTENT-MAIL", new String[]{member.getSalutation(), member.getName(), member.getFirstName(), loginProfil});
					String from = AssmatUtil.getDefaultEmail();
					AssmatUtil.sendMessage(profil, subject, content, from);
				}
			} 
		}
	}

	public static String getAdresseDomicile(AssmatSolis solis){
		if(Util.notEmpty(solis)){
			StringBuilder stbd = new StringBuilder();

			if(Util.notEmpty(solis.getAdresseDomicile()))
				stbd.append(solis.getAdresseDomicile() + " ");

			if(Util.notEmpty(solis.getCpDomicile()))
				stbd.append(solis.getCpDomicile() + " ");

			if(Util.notEmpty(solis.getCpDomicile()))	
				stbd.append(solis.getCommuneDomicile());

			return stbd.toString();
		}

		return "";
	}

	public static String getAdresseMAM(AssmatSolis solis){
		if(Util.notEmpty(solis)){
			StringBuilder stbd = new StringBuilder();

			if(Util.notEmpty(solis.getNomMam()))
				stbd.append(solis.getNomMam() + " ");

			if(Util.notEmpty(solis.getAdresseMam()))
				stbd.append(solis.getAdresseMam() + " ");

			if(Util.notEmpty(solis.getCpMam()))
				stbd.append(solis.getCpMam() + " ");

			if(Util.notEmpty(solis.getCommuneMam()))	
				stbd.append(solis.getCommuneMam());

			return stbd.toString();
		}

		return "";
	}

	public static Group getGroupAssmat(){
		Channel channel = Channel.getChannel();
		Group group = channel.getGroup(channel.getProperty("jcmsplugin.assmatplugin.groupe.assistante_maternelle"));
		if(Util.notEmpty(group)){
			return group;
		} else {
			logger.info("La propriété <jcmsplugin.assmatplugin.groupe.assistante_maternelle> est invalide");
		}
		return null;
	}


	/**
	 * Retourne un set de Category selon un tableau d'id passé en parametre
	 * 
	 * @param tabIds
	 * @return un set de Category
	 */
	public static HashSet<Category> getCategorySetFromIds(String[] tabIds){
		HashSet<Category> categorySet = new HashSet<Category>();
		if(Util.notEmpty(tabIds)){
			for(String idcateg : tabIds){
				categorySet.add(Channel.getChannel().getCategory(idcateg));
			}
		}
		return categorySet;

	}

	/**
	 * Return une map Nom en base correspodant a la catégorie/ valeur
	 * 
	 * @param categSet
	 * @return
	 */
	public static HashMap<String, Boolean> getHashMapFromSelectedCategories(HashSet<Category> categSet){
		HashMap<String, Boolean> hashMapBooleanChamps= new HashMap<String, Boolean>();

		if(Util.notEmpty(categSet)){
			for(Category itCateg : categSet){
				hashMapBooleanChamps.put(itCateg.getExtraData("extra.Category.jcmsplugin.assmatplugin.search.mapping"), true);
			}
		}

		return hashMapBooleanChamps;

	}

	public final static String SEPARATOR = " ";

	public static StringBuilder getOrganization(InscriptionAM inscription, String name, String firstname){
		StringBuilder stbd = new StringBuilder();
		if(Util.notEmpty(name))
			stbd.append(SolisManager.clean(name) + SEPARATOR);

		if(Util.notEmpty(firstname))
			stbd.append(SolisManager.clean(firstname) + SEPARATOR);

		if(Util.notEmpty(inscription.getCommune()))
			stbd.append(SolisManager.clean(inscription.getCommune()) + SEPARATOR);

		if(Util.notEmpty(inscription.getTelephonePortable()))
			stbd.append(inscription.getTelephonePortable() + SEPARATOR);

		if(Util.notEmpty(inscription.getAdresseMail()))
			stbd.append(inscription.getAdresseMail() + SEPARATOR);		

		if(Util.notEmpty(inscription.getNumeroDagrement()))
			stbd.append(inscription.getNumeroDagrement() + SEPARATOR);						

		return stbd;	
	}

	public static StringBuilder getOrganization(Member membre){
		StringBuilder stbd = new StringBuilder();
		if(Util.notEmpty(membre.getName())) {
			stbd.append(SolisManager.clean(membre.getName()) + SEPARATOR);
		}
		if(Util.notEmpty(membre.getFirstName())) {
			stbd.append(SolisManager.clean(membre.getFirstName()) + SEPARATOR);
		}

		ProfilManager profilManager = ProfilManager.getInstance();  
		ProfilASSMAT profil = profilManager.getProfilASSMAT(membre);

		// si le profil du membre existe
		if(Util.notEmpty(profil)){
			if(Util.notEmpty(profil.getCommune())) {
				stbd.append(SolisManager.clean(profil.getCommune()) + SEPARATOR);
			}
			if(Util.notEmpty(profil.getCommuneMam())) {
				stbd.append(SolisManager.clean(profil.getCommuneMam()) + SEPARATOR);
			} 
		}

		if(Util.notEmpty(membre.getMobile())) {
			stbd.append(membre.getMobile() + SEPARATOR);
		}	  

		if(Util.notEmpty(membre.getEmail())) {
			stbd.append(membre.getEmail() + SEPARATOR);
		}

		if(Util.notEmpty(profil) && Util.notEmpty(profil.getNum_agrement())){
			stbd.append(profil.getNum_agrement() + SEPARATOR);      
		}	      

		return stbd;	
	}

	public static Member getMemberFromMobile(String telephone){
		if (Util.isEmpty(telephone)) {
			return null;
		}

		Set<Member> localTreeSet = Channel.getChannel().getDataSet(Member.class);
		for (Iterator<Member> itMbr = localTreeSet.iterator(); itMbr.hasNext(); ) { 
			Member localMember = itMbr.next();
			String mobile = localMember.getMobile();
			if (!Util.isEmpty(mobile) && mobile.equalsIgnoreCase(telephone))
			{
				return localMember;
			}
		}
		return null;
	}


	private static String adresse_ws = "http://api-adresse.data.gouv.fr/search/?type=housenumber&q=";
	public static ResponseAPIAdress getResponseAPIAdress(String adresse){
		ResponseAPIAdress response = null;
		if(Util.notEmpty(adresse)){
			String content;
			try {
				String url = adresse_ws+Util.encodeUrl(adresse);
				content = HttpClientUtils.getContent(url);
				ObjectMapper mapper = new ObjectMapper();
				response = mapper.readValue(content, ResponseAPIAdress.class);
			} catch (HttpException e) {
				logger.error(e.getMessage(),e);
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}	
		}
		return response;
	}


	private static String adresse_housenumber_ws = "http://api-adresse.data.gouv.fr/search/?limit=5&type=housenumber&q=";
	public static ResponseAPIAdress getResponseAPIHouseNumberAdress(String adresse){
		ResponseAPIAdress response = null;
		if(Util.notEmpty(adresse)){
			String content;
			try {
				String url = adresse_housenumber_ws+Util.encodeUrl(adresse);
				content = HttpClientUtils.getContent(url);
				ObjectMapper mapper = new ObjectMapper();
				response = mapper.readValue(content, ResponseAPIAdress.class);
			} catch (HttpException e) {
				logger.error(e.getMessage(),e);
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			} 
		}
		return response;
	}
	
	private static String adresse_departement_ws = "http://api-adresse.data.gouv.fr/search/?limit=30&lat=47.27&lon=-1.59&q=";
	public static ResponseAPIAdress getResponseAPIDepartementAdress(String adresse){
		ResponseAPIAdress response = null;
		if(Util.notEmpty(adresse)){
			String content;
			try {
				String url = adresse_departement_ws+Util.encodeUrl(adresse);
				content = HttpClientUtils.getContent(url);
				ObjectMapper mapper = new ObjectMapper();
				response = mapper.readValue(content, ResponseAPIAdress.class);
			} catch (HttpException e) {
				logger.error(e.getMessage(),e);
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			} 
		}
		return response;
	}
	
	private static String adresse_inCity_ws = "http://api-adresse.data.gouv.fr/search/?limit=5&citycode=_CITYCODE_&q=_QUERY_";
	public static ResponseAPIAdress getResponseAPICityCodeAdress(String cityCode, String adresse){
		ResponseAPIAdress response = null;
		if(Util.notEmpty(cityCode) && Util.notEmpty(adresse)){
			String content;
			try {
				String url = adresse_inCity_ws.replaceAll("_CITYCODE_", cityCode).replaceAll("_QUERY_", Util.encodeUrl(adresse));
				content = HttpClientUtils.getContent(url);
				ObjectMapper mapper = new ObjectMapper();
				response = mapper.readValue(content, ResponseAPIAdress.class);
			} catch (HttpException e) {
				logger.error(e.getMessage(),e);
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			} 
		}
		return response;
	}	


	public static List<Double> getLatitudeLongitudeFromAdresse(String adresse){
		ResponseAPIAdress response = getResponseAPIAdress(adresse);
		if(Util.notEmpty(response) && Util.notEmpty(response.getFeatures()) && Util.notEmpty(response.getFeatures().get(0).getGeometry()) && Util.notEmpty(response.getFeatures().get(0).getGeometry().getCoordinates())){
			return response.getFeatures().get(0).getGeometry().getCoordinates();
		}
		return null;
	}


	public static ArrayList<Properties> getPropertiesFromAdresse(String adresse) {
		ResponseAPIAdress response = getResponseAPIHouseNumberAdress(adresse);
		if(Util.notEmpty(response) && Util.notEmpty(response.getFeatures()) && Util.notEmpty(response.getFeatures().get(0).getProperties())){
			ArrayList<Properties> listProperty = new ArrayList<Properties>();
			for(Feature itFeature : response.getFeatures() ) {
				if(Util.notEmpty(itFeature.getProperties())) {
					listProperty.add(itFeature.getProperties());
				}
			}
			return listProperty;
		}
		return null;
	}
	
	public static ArrayList<Properties> getPropertiesFromDepartementAdresse(String adresse) {
		ResponseAPIAdress response = getResponseAPIDepartementAdress(adresse);
		if(Util.notEmpty(response) && Util.notEmpty(response.getFeatures()) && Util.notEmpty(response.getFeatures().get(0).getProperties())){
			ArrayList<Properties> listProperty = new ArrayList<Properties>();
			for(Feature itFeature : response.getFeatures() ) {
				if(Util.notEmpty(itFeature.getProperties())) {
					Properties prop = itFeature.getProperties();
					if(Util.notEmpty(prop.getContext()) && prop.getContext().startsWith("44")){
						listProperty.add(itFeature.getProperties());
						if(listProperty.size() >=5) break;
					}
				}
			}
			return listProperty;
		}
		return null;
	}
	
	public static ArrayList<Properties> getPropertiesFromAdresseAndCity(String cityCode, String adresse) {
		ResponseAPIAdress response = getResponseAPICityCodeAdress(cityCode, adresse);
		if(Util.notEmpty(response) && Util.notEmpty(response.getFeatures()) && Util.notEmpty(response.getFeatures().get(0).getProperties())){
			ArrayList<Properties> listProperty = new ArrayList<Properties>();
			for(Feature itFeature : response.getFeatures() ) {
				if(Util.notEmpty(itFeature.getProperties())) {
					listProperty.add(itFeature.getProperties());
				}
			}
			return listProperty;
		}
		return null;
	}	


	public static String generateAssmatPassword(){
		String password= "";
		Random random = new Random();
		int Low = 10;
		int High = 100;
		int Result = random.nextInt(High-Low) + Low;
		password = Util.generatePassword(7);
		password = password+ Util.generatePassword(1).toUpperCase();
		password = password+ Integer.toString(Result);

		return password;

	}

	public static boolean isDisponibiliteDomicile(Disponibilite dispo){
		if(Util.isEmpty(dispo)){
			return false;
		}
		return dispo.getAgrement().contains("dom-");
	}

	public static boolean isDisponibiliteMAM(Disponibilite dispo){
		if(Util.isEmpty(dispo)){
			return false;
		}
		return dispo.getAgrement().contains("mam-");
	}

	public static com.vividsolutions.jts.geom.Point getGeometry(double latitude, double longitude){
		Geometry geom = null;
		WKTReader fromText = new WKTReader();
		GeometryFactory geoFactory  = new GeometryFactory();

		//Coordonnées domicile  
		Coordinate coordDom = new Coordinate(longitude,latitude);
		com.vividsolutions.jts.geom.Point pointDom = geoFactory.createPoint(coordDom);
		String[] tabDom = new String[]{pointDom.toString()}; 
		try {
			geom = fromText.read(assemble(tabDom));
		} catch (ParseException e) {
			throw new RuntimeException("Not a WKT string:" + tabDom);
		}		



		return (Point) geom;
	}



	public static String assemble(String[] args){
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i<args.length;i++){
			builder.append(args[i]).append(" ");
		}
		return builder.toString();
	}

	/**
	 * Retourne une commune en fonction du nom.
	 * @param title Nom de la commune.
	 * @param clazz Class de la commune.
	 * @return Commune associée au nom passé en paramètre.
	 * @throws UnknowCityException Une exception est levée si aucune commune
	 *  n'est associée au nom passé en paramètre.
	 */
	
	/* SGU 05/2022 : voir si utile et reprendre les méthodes de Socle.
	 * 
	  
	public static Publication getCityByName(String title, Class clazz) throws UnknowCityException{
		TreeSet<Publication> publications = Channel.getChannel().getAllDataSet(clazz);
		for(Publication publication: publications){
			if(compareCityName(publication.getTitle(),title)) {
				return publication;
			}
		}
		throw new UnknowCityException();
	}
	
	*/

	/**
	 * Retourne une commune en fonction du code insee.
	 * @param Code insee de la commune.
	 * @return Commune associée au code insee passé en paramètre.
	 * @throws UnknowCityException Une exception est levée si aucune commune
	 *  n'est associée au nom passé en paramètre.
	 */
	
	/* SGU 05/2022 : voir si utile et reprendre les méthodes de Socle.

	public static Publication getCityByInsee(String insee) throws UnknowCityException{
		TreeSet<City> citySet = Channel.getChannel().getAllDataSet(City.class);
		for(City itCity: citySet){
			if( Integer.toString(itCity.getCityCode()).equalsIgnoreCase(insee) ) {
				return itCity;
			}
		}
		throw new UnknowCityException();
	}

	 */
	/**
	 * Compare deux nom de ville en ne prennant pas en comptant les accents et - _
	 */
	public static boolean compareCityName(String city1, String city2){

		city1= SolisManager.getInstance().clean(city1);
		city2=SolisManager.getInstance().clean(city2);
		boolean isSame= false;
		if(city1.compareTo(city2) == 0){
			isSame= true;
		}
		return isSame;

	}

	public static String getDefaultEmail(){
		String from = getMessage("MAIL-DEFAULT-FROM");
		if("MAIL-DEFAULT-FROM".equals(from))
			from = Channel.getChannel().getDefaultEmail();

		return from;
	} 

	public static int getNumCommune(String commune){
			City city = (City) SocleUtils.getCommuneFromName(commune);
			if(Util.notEmpty(city)){
				return city.getCityCode();
			}

		return -1;

	}


	/**
	 * Methode permettant de savoir si un membre est un membre asso
	 * @param member
	 * @return
	 */
	public static boolean isMemberASSO(Member member){

		boolean isMember = false;

		if(Util.notEmpty(member)){

			if(Util.notEmpty(member.getGroups())){

				List<Group> grpList = Arrays.asList(member.getGroups());

				// Si le membre fait partie du groupe RAM
				if (grpList.contains(groupASSO)) {
					isMember = true;
				}

			}   
		} 
		return isMember; 
	}

	/**
	 * Methode permettant de savoir si un membre est un membre RAM
	 * @param member
	 * @return
	 */
	public static boolean isMemberRAM(Member member){

		boolean isMember = false;

		if(Util.notEmpty(member)){

			if(Util.notEmpty(member.getGroups())){

				List<Group> grpList = Arrays.asList(member.getGroups());

				// Si le membre fait partie du groupe RAM
				if (grpList.contains(groupRAM)) {
					isMember = true;
				}

			}   
		} 
		return isMember; 
	}

	/**
	 * Methode permettant de savoir si un membre est un membre asso
	 * @param member
	 * @return
	 */
	public static boolean isMemberAsso(Member member){
		return isMemberASSO(member);
	}

	/**
	 * Methode permettant d'enlever a un set d'assmat les asmmat dont la visibilité site est a non 
	 * @param resultSet
	 * @return
	 */
	public static Set<AssmatSearch> removeAssmatNonvisible(Set<AssmatSearch> resultSet){

		Set<AssmatSearch> resultSetFinal = new HashSet<AssmatSearch>();

		if(Util.notEmpty(resultSet)){ 

			resultSetFinal.addAll(resultSet);
			ProfilASSMAT profilAM = null;
			for(AssmatSearch am : resultSet){
				profilAM = (ProfilASSMAT) Channel.getChannel().getPublication(am.getDbprofilId());
				if(!profilAM.getVisibiliteSite()){
					resultSetFinal.remove(am);
				}     
			}

		}  
		return resultSetFinal;

	}


	/**
	 * Retourne le chemin d'un fichier csv
	 * @param reponse
	 * @return reponse
	 */
	/*
 public static String getLienTelechargementRAM(Member mbr,HttpServletRequest request){

     String urlDl = ServletUtil.getBaseUrl(request);
     String idSolis = mbr.getExtraData("extra.Member.jcmsplugin.assmatplugin.idsolis.lieu");
     String nomFichier ="exportAM";
     urlDl+= nomFichier + "_" + new String(Base64.encodeBase64(("ENCODING" +idSolis).getBytes())) +".csv";

     return urlDl;

  }
	 */

	private static final String EXPORT_DIR = Channel.getChannel().getRealPath("/upload");

	public static String getLienTelechargementRAM(Member mbr,String nameFile){
		String urlDl = EXPORT_DIR;
		if(Util.notEmpty(mbr)){
			String idSolis = Util.getString(mbr.getExtraData("extra.Member.jcmsplugin.assmatplugin.idsolis.lieu"),"");
			if(Util.notEmpty(idSolis)){
				urlDl = getUrlDlFile(nameFile,idSolis);
			} else {
				logger.info("L'idSolis est vide pour le membre <"+mbr.getFullName()+"> <"+mbr.getId()+">");
			}
		}
		return urlDl;

	}

	public static String getInfoFileExport(Member mbr, String nameFile) {
		String reponse = "";
		String sizeFile = "0";
		String dateText = "";
		if(Util.notEmpty(mbr)){
			// On recuepre la baseURL de la webapp
			String urlDl = EXPORT_DIR;
			String idSolis = Util.getString(mbr.getExtraData("extra.Member.jcmsplugin.assmatplugin.idsolis.lieu"),"");

			if(Util.notEmpty(idSolis)){
				urlDl = getUrlDlFile(nameFile,idSolis);
				if(!urlDl.equalsIgnoreCase(EXPORT_DIR)){
					File file = new File(urlDl);
					if(Util.notEmpty(file) && file.exists()){
						sizeFile = String.valueOf((file.length() / 1024));

						Date date = new Date(file.lastModified());
						SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
						dateText = df2.format(date);
						reponse = "(CSV - " + sizeFile + " KO - " + dateText + ")";
					}
				}
			} else {
				logger.info("L'idSolis est vide pour le membre <"+mbr.getFullName()+"> <"+mbr.getId()+">");
			}


		}
		return reponse;
	}

	private static String getUrlDlFile(String nomFichier, String idSolis){
		return getExportDir() + nomFichier + "_" + idSolis + "_" + new String(Base64.encodeBase64(("ENCODING" + idSolis).getBytes())) + ".xlsx";
	}

	private static String getExportDir(){
		return Channel.getChannel().getUrl() + "upload/exports_ram/";
	}
	/**
	 * Retourne les informations (Date + taille) d'un fichier d'export AM
	 * 
	 * @param chaine
	 * @return
	 */
	/*
  public static String getInfoFileExport(Member mbr, HttpServletRequest request) {

    String reponse = "";
    String sizeFile = "";

    // On recuepre la baseURL de la webapp
    String urlDl = Channel.getChannel().getWebappPath();
    String idSolis = mbr.getExtraData("extra.Member.jcmsplugin.assmatplugin.idsolis.lieu");
    String nomFichier = "exportAM";
    urlDl += nomFichier + "_" + new String(Base64.encodeBase64(("ENCODING" + idSolis).getBytes())) + ".csv";

    File file = new File(urlDl);

    sizeFile = String.valueOf((file.length() / 1024));

    Date date = new Date(file.lastModified());
    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
    String dateText = df2.format(date);

    reponse = "(CSV - " + sizeFile + " KO - " + dateText + ")";

    return reponse;
  }

	 */

	/**
	 * Remplace les entrée par des BR
	 * @param chaine
	 * @return
	 */
	public static String replaceCharSpaceBR (String chaine){
		chaine = Util.replaceAll(chaine, "\r\n", "<br />");
		return chaine;
	}


	public static DeclarationAccueilServiceApi getDeclarationAccueilServiceApi() {
		String serviceUrl = Channel.getChannel().getProperty("plugin.assmatplugin.swagger.demarches.url");
		API_CLIENT.setBasePath(serviceUrl);   
		DeclarationAccueilServiceApi declarationApi = new DeclarationAccueilServiceApi();
		declarationApi.setApiClient(API_CLIENT);
		return declarationApi;
	}

	public static JasperControllerApi getJasperControllerApi() {
		String serviceUrl = Channel.getChannel().getProperty("plugin.assmatplugin.swagger.demarches.url");
		API_CLIENT.setBasePath(serviceUrl);   
		JasperControllerApi jasperControllerApi = new JasperControllerApi();
		jasperControllerApi.setApiClient(API_CLIENT);
		return jasperControllerApi;
	}

	/**
	 * 
	 * Retourne le libellé du mois correspondant à la date donnée en paramètre précédé de l'article "de" ou "d'"
	 * 
	 * @param mois
	 * @return
	 */
	public static String getFutureDispoMoisLibelle(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);

		ArrayList<String> FutureDispoMoisLibelle = new ArrayList<String>();
		FutureDispoMoisLibelle.add("de janvier");
		FutureDispoMoisLibelle.add("de février");
		FutureDispoMoisLibelle.add("de mars");
		FutureDispoMoisLibelle.add("d'avril");
		FutureDispoMoisLibelle.add("de mai");
		FutureDispoMoisLibelle.add("de juin");
		FutureDispoMoisLibelle.add("de juillet");
		FutureDispoMoisLibelle.add("d'août");
		FutureDispoMoisLibelle.add("de septembre");
		FutureDispoMoisLibelle.add("d'octobre");
		FutureDispoMoisLibelle.add("de novembre");
		FutureDispoMoisLibelle.add("de décembre");

		return FutureDispoMoisLibelle.get(month);

	}

	/**
	 * 
	 * Retourne le libellé du mois et de l'année correspondants à la date donnée en paramètre précédé de l'article "de" ou "d'"
	 * 
	 * @param mois
	 * @return
	 */
	public static String getFutureDispoDateLibelle(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		return getFutureDispoMoisLibelle(date) + " " + year;
	}

	
	/**
	 * Indique si le membre fait partie de groupe des RAM
	 * @param loggedMember
	 * @return
	 */
	public static boolean getMemberIsRam(Member loggedMember) {
	  if(loggedMember == null) {
	    return false;
	  }
	  Group ramGroup = channel.getGroup(channel.getProperty("jcmsplugin.assmatplugin.group.ram.id"));
	  return loggedMember.belongsToGroup(ramGroup);
	}
	
	
 /**
  * Indique si le membre fait partie de groupe super contrib
  * @param loggedMember
  * @return
  */
 public static boolean getMemberIsContribPower(Member loggedMember) {
   if(loggedMember == null) {
     return false;
   }
   Group contribGroup = channel.getGroup("$id.plugin.corporateidentity.contributeur.pouvoir.group");
   return loggedMember.belongsToGroup(contribGroup);
 }
 
 
 /**
  * Indique si le membre peut voir la liste des micro quartiers
  * @param loggedMember
  * @return
  */
 public static boolean canSeeMicroQuartiers(Member loggedMember) {
   if(loggedMember == null) {
     return false;
   }
   Group contribGroup = channel.getGroup("$id.plugin.corporateidentity.contributeur.pouvoir.group");
   Group ramGroup = channel.getGroup(channel.getProperty("jcmsplugin.assmatplugin.group.ram.nantes.id"));
   return loggedMember.belongsToGroup(contribGroup)||loggedMember.belongsToGroup(ramGroup)||loggedMember.isAdmin();
 }
 
 /**
  * Renvoie un string de date depuis le format yyyy-MM-dd vers dd/MM/yyyy
  * @param formDateStr
  * @return
  */
	public static String convertFormDateToAssmatProfilDate(String formDateStr) {
	  if (Util.isEmpty(formDateStr)) return null;
	  SimpleDateFormat sdf = new SimpleDateFormat(Channel.getChannel().getProperty("jcmsplugin.assmat.format.date.form"));
	  Date itDate;
    try {
      itDate = sdf.parse(formDateStr);
      sdf = new SimpleDateFormat(Channel.getChannel().getProperty("jcmsplugin.assmat.format.date.profil"));   
      return sdf.format(itDate);
    } catch (java.text.ParseException e) {
      return formDateStr;
    }
	}

}