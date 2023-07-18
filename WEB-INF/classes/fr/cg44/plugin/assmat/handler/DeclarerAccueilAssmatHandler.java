package fr.cg44.plugin.assmat.handler;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jalios.jcms.Channel;
import com.jalios.jcms.Data;
import com.jalios.jcms.JcmsConstants;
import com.jalios.jcms.Member;
import com.jalios.jcms.context.JcmsMessage;
import com.jalios.jcms.context.JcmsMessage.Level;
import com.jalios.jcms.handler.EditDataHandler;
import com.jalios.util.ReflectUtil;
import com.jalios.util.Util;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.managers.ProfilManager;
import fr.cg44.plugin.assmat.util.DemarcheUtil;
import fr.cg44.plugin.assmat.util.PlanningUtil;
import fr.trsb.cd44.solis.manager.SolisManager;
import generated.ProfilASSMAT;
import io.swagger.client.ApiException;
import io.swagger.client.model.AccueilDTO;
import io.swagger.client.model.CreneauDTO;
import io.swagger.client.model.DeclarationAccueilDTO;
import io.swagger.client.model.SemaineTypeDTO;

public class DeclarerAccueilAssmatHandler extends EditDataHandler {

  private static final Logger logger = Logger.getLogger(DeclarerAccueilAssmatHandler.class);

  // La déclaration en mémoire
  protected DeclarationAccueilDTO declaration;
  
  
  // ETAPES DU FORMULAIRE
  public static final int ENFANT_ACCUEILLI = 0;
  public static final int LIEU_ACCUEIL = 1;
  public static final int MODALITES = 2;
  public static final int PLANNING = 3;
  public static final int VALIDATION = 4;
  
  
  /**
   * Enfant accueilli
   */
  
  protected String nomEnfant;
  protected String prenomEnfant;
  protected String sexeEnfant;
  protected String dateNaissanceEnfant;
  protected String dateDebutAccueil;
  protected String civiliteRepresentant1;
  protected String nomRepresentant1;
  protected String prenomRepresentant1;
  protected String telephoneRepresentant1;
  protected String adresseAutoCompleteRepresentant1;
  protected String adresseRepresentant1;
  protected Boolean saisieAdresseR1;
  protected String codePostalRepresentant1;
  protected String villeRepresentant1;
  protected String complementRepresentant1;
  protected String civiliteRepresentant2;
  protected String nomRepresentant2;
  protected String prenomRepresentant2;
  protected String telephoneRepresentant2;
  protected String adresseAutoCompleteRepresentant2;
  protected String adresseRepresentant2;
  protected Boolean saisieAdresseR2;
  protected String codePostalRepresentant2;
  protected String villeRepresentant2;
  protected String complementRepresentant2; 
  protected boolean saisieAdresseDif; 

    
  /**
   * Lieu d'accueil
   */
  
  protected String accueilDomicile;
  protected String adresseDomicile;
  protected String communeDomicile;
  protected String cpDomicile;
  
  protected String nomMam;
  protected String adresseMam;
  protected String communeMam;
  protected String cpMam;

  
  /**
   * Modalités
   */
  
  protected String planningRegulier;
  protected String accueilOccasionnel;
  protected String planningSemainesTypes;
  protected String uniquementVacances;
  protected String estProvisoire;
  protected String dateFinProvisoire;
  protected String motifProvisoire;
  

  
  
  
  protected static String REGEX_HEURE =  "([01]?[0-9]|2[0-3])(h|H)([0-5][0-9])?";

  public final static String[] JOURS = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};

  private static final int REGULIER = 1;
  private static final int VACANCES = 2;
  private static final int VARIABLE = 3;

  
  /**
   * Planning
   */
  
  // Planning regulier
  
  protected boolean regulLundi;
  protected boolean regulMardi;
  protected boolean regulMercredi;
  protected boolean regulJeudi;
  protected boolean regulVendredi;
  protected boolean regulSamedi;
  protected boolean regulDimanche;

  protected boolean regulLundiVac;
  protected boolean regulMardiVac;
  protected boolean regulMercrediVac;
  protected boolean regulJeudiVac;
  protected boolean regulVendrediVac;
  protected boolean regulSamediVac;
  protected boolean regulDimancheVac;

  
  protected String debcr1lundi;
  protected String fincr1lundi;
  protected String debcr2lundi;
  protected String fincr2lundi;
  protected String debcr3lundi;
  protected String fincr3lundi;

  protected String debcr1mardi;
  protected String fincr1mardi;
  protected String debcr2mardi;
  protected String fincr2mardi;
  protected String debcr3mardi;
  protected String fincr3mardi;
  
  protected String debcr1mercredi;
  protected String fincr1mercredi;
  protected String debcr2mercredi;
  protected String fincr2mercredi;
  protected String debcr3mercredi;
  protected String fincr3mercredi;

  protected String debcr1jeudi;
  protected String fincr1jeudi;
  protected String debcr2jeudi;
  protected String fincr2jeudi;
  protected String debcr3jeudi;
  protected String fincr3jeudi;

  protected String debcr1vendredi;
  protected String fincr1vendredi;
  protected String debcr2vendredi;
  protected String fincr2vendredi;
  protected String debcr3vendredi;
  protected String fincr3vendredi;

  protected String debcr1samedi;
  protected String fincr1samedi;
  protected String debcr2samedi;
  protected String fincr2samedi;
  protected String debcr3samedi;
  protected String fincr3samedi;

  protected String debcr1dimanche;
  protected String fincr1dimanche;
  protected String debcr2dimanche;
  protected String fincr2dimanche;
  protected String debcr3dimanche;
  protected String fincr3dimanche;


  //Planning regulier Vacances

  protected String debcr1lundiVac;
  protected String fincr1lundiVac;
  protected String debcr2lundiVac;
  protected String fincr2lundiVac;
  protected String debcr3lundiVac;
  protected String fincr3lundiVac;

  protected String debcr1mardiVac;
  protected String fincr1mardiVac;
  protected String debcr2mardiVac;
  protected String fincr2mardiVac;
  protected String debcr3mardiVac;
  protected String fincr3mardiVac;

  protected String debcr1mercrediVac;
  protected String fincr1mercrediVac;
  protected String debcr2mercrediVac;
  protected String fincr2mercrediVac;
  protected String debcr3mercrediVac;
  protected String fincr3mercrediVac;

  protected String debcr1jeudiVac;
  protected String fincr1jeudiVac;
  protected String debcr2jeudiVac;
  protected String fincr2jeudiVac;
  protected String debcr3jeudiVac;
  protected String fincr3jeudiVac;

  protected String debcr1vendrediVac;
  protected String fincr1vendrediVac;
  protected String debcr2vendrediVac;
  protected String fincr2vendrediVac;
  protected String debcr3vendrediVac;
  protected String fincr3vendrediVac;

  protected String debcr1samediVac;
  protected String fincr1samediVac;
  protected String debcr2samediVac;
  protected String fincr2samediVac;
  protected String debcr3samediVac;
  protected String fincr3samediVac;

  protected String debcr1dimancheVac;
  protected String fincr1dimancheVac;
  protected String debcr2dimancheVac;
  protected String fincr2dimancheVac;
  protected String debcr3dimancheVac;
  protected String fincr3dimancheVac;

  protected String accueilVacances;
  protected String planningDifferentVacances;
  protected String precision; 

  
  //PLANNING VARIABLE JOUR
  
  protected Boolean s1Lundi;
  protected Boolean s1Mardi;
  protected Boolean s1Mercredi;
  protected Boolean s1Jeudi;
  protected Boolean s1Vendredi;
  protected Boolean s1Samedi;
  protected Boolean s1Dimanche;

  protected Boolean s2Lundi;
  protected Boolean s2Mardi;
  protected Boolean s2Mercredi;
  protected Boolean s2Jeudi;
  protected Boolean s2Vendredi;
  protected Boolean s2Samedi;
  protected Boolean s2Dimanche;

  protected Boolean s3Lundi;
  protected Boolean s3Mardi;
  protected Boolean s3Mercredi;
  protected Boolean s3Jeudi;
  protected Boolean s3Vendredi;
  protected Boolean s3Samedi;
  protected Boolean s3Dimanche;

  protected Boolean s4Lundi;
  protected Boolean s4Mardi;
  protected Boolean s4Mercredi;
  protected Boolean s4Jeudi;
  protected Boolean s4Vendredi;
  protected Boolean s4Samedi;
  protected Boolean s4Dimanche;

  protected Boolean s5Lundi;
  protected Boolean s5Mardi;
  protected Boolean s5Mercredi;
  protected Boolean s5Jeudi;
  protected Boolean s5Vendredi;
  protected Boolean s5Samedi;
  protected Boolean s5Dimanche;

  protected Boolean s6Lundi;
  protected Boolean s6Mardi;
  protected Boolean s6Mercredi;
  protected Boolean s6Jeudi;
  protected Boolean s6Vendredi;
  protected Boolean s6Samedi;
  protected Boolean s6Dimanche;

  protected Boolean s7Lundi;
  protected Boolean s7Mardi;
  protected Boolean s7Mercredi;
  protected Boolean s7Jeudi;
  protected Boolean s7Vendredi;
  protected Boolean s7Samedi;
  protected Boolean s7Dimanche;

  protected Boolean s8Lundi;
  protected Boolean s8Mardi;
  protected Boolean s8Mercredi;
  protected Boolean s8Jeudi;
  protected Boolean s8Vendredi;
  protected Boolean s8Samedi;
  protected Boolean s8Dimanche;

  protected Boolean s9Lundi;
  protected Boolean s9Mardi;
  protected Boolean s9Mercredi;
  protected Boolean s9Jeudi;
  protected Boolean s9Vendredi;
  protected Boolean s9Samedi;
  protected Boolean s9Dimanche;

  protected Boolean s10Lundi;
  protected Boolean s10Mardi;
  protected Boolean s10Mercredi;
  protected Boolean s10Jeudi;
  protected Boolean s10Vendredi;
  protected Boolean s10Samedi;
  protected Boolean s10Dimanche;


  

  //PLANNING VARIABLE SEMAINE 1
  
  protected String s1debcr1lundi;
  protected String s1fincr1lundi;
  protected String s1debcr2lundi;
  protected String s1fincr2lundi;
  protected String s1debcr3lundi;
  protected String s1fincr3lundi;

  protected String s1debcr1mardi;
  protected String s1fincr1mardi;
  protected String s1debcr2mardi;
  protected String s1fincr2mardi;
  protected String s1debcr3mardi;
  protected String s1fincr3mardi;

  protected String s1debcr1mercredi;
  protected String s1fincr1mercredi;
  protected String s1debcr2mercredi;
  protected String s1fincr2mercredi;
  protected String s1debcr3mercredi;
  protected String s1fincr3mercredi;

  protected String s1debcr1jeudi;
  protected String s1fincr1jeudi;
  protected String s1debcr2jeudi;
  protected String s1fincr2jeudi;
  protected String s1debcr3jeudi;
  protected String s1fincr3jeudi;

  protected String s1debcr1vendredi;
  protected String s1fincr1vendredi;
  protected String s1debcr2vendredi;
  protected String s1fincr2vendredi;
  protected String s1debcr3vendredi;
  protected String s1fincr3vendredi;

  protected String s1debcr1samedi;
  protected String s1fincr1samedi;
  protected String s1debcr2samedi;
  protected String s1fincr2samedi;
  protected String s1debcr3samedi;
  protected String s1fincr3samedi;

  protected String s1debcr1dimanche;
  protected String s1fincr1dimanche;
  protected String s1debcr2dimanche;
  protected String s1fincr2dimanche;
  protected String s1debcr3dimanche;
  protected String s1fincr3dimanche;

  //PLANNING VARIABLE SEMAINE 2
  protected String s2debcr1lundi;
  protected String s2fincr1lundi;
  protected String s2debcr2lundi;
  protected String s2fincr2lundi;
  protected String s2debcr3lundi;
  protected String s2fincr3lundi;

  protected String s2debcr1mardi;
  protected String s2fincr1mardi;
  protected String s2debcr2mardi;
  protected String s2fincr2mardi;
  protected String s2debcr3mardi;
  protected String s2fincr3mardi;

  protected String s2debcr1mercredi;
  protected String s2fincr1mercredi;
  protected String s2debcr2mercredi;
  protected String s2fincr2mercredi;
  protected String s2debcr3mercredi;
  protected String s2fincr3mercredi;

  protected String s2debcr1jeudi;
  protected String s2fincr1jeudi;
  protected String s2debcr2jeudi;
  protected String s2fincr2jeudi;
  protected String s2debcr3jeudi;
  protected String s2fincr3jeudi;

  protected String s2debcr1vendredi;
  protected String s2fincr1vendredi;
  protected String s2debcr2vendredi;
  protected String s2fincr2vendredi;
  protected String s2debcr3vendredi;
  protected String s2fincr3vendredi;

  protected String s2debcr1samedi;
  protected String s2fincr1samedi;
  protected String s2debcr2samedi;
  protected String s2fincr2samedi;
  protected String s2debcr3samedi;
  protected String s2fincr3samedi;

  protected String s2debcr1dimanche;
  protected String s2fincr1dimanche;
  protected String s2debcr2dimanche;
  protected String s2fincr2dimanche;
  protected String s2debcr3dimanche;
  protected String s2fincr3dimanche;

  //PLANNING VARIABLE SEMAINE 3
  protected String s3debcr1lundi;
  protected String s3fincr1lundi;
  protected String s3debcr2lundi;
  protected String s3fincr2lundi;
  protected String s3debcr3lundi;
  protected String s3fincr3lundi;

  protected String s3debcr1mardi;
  protected String s3fincr1mardi;
  protected String s3debcr2mardi;
  protected String s3fincr2mardi;
  protected String s3debcr3mardi;
  protected String s3fincr3mardi;

  protected String s3debcr1mercredi;
  protected String s3fincr1mercredi;
  protected String s3debcr2mercredi;
  protected String s3fincr2mercredi;
  protected String s3debcr3mercredi;
  protected String s3fincr3mercredi;

  protected String s3debcr1jeudi;
  protected String s3fincr1jeudi;
  protected String s3debcr2jeudi;
  protected String s3fincr2jeudi;
  protected String s3debcr3jeudi;
  protected String s3fincr3jeudi;

  protected String s3debcr1vendredi;
  protected String s3fincr1vendredi;
  protected String s3debcr2vendredi;
  protected String s3fincr2vendredi;
  protected String s3debcr3vendredi;
  protected String s3fincr3vendredi;

  protected String s3debcr1samedi;
  protected String s3fincr1samedi;
  protected String s3debcr2samedi;
  protected String s3fincr2samedi;
  protected String s3debcr3samedi;
  protected String s3fincr3samedi;

  protected String s3debcr1dimanche;
  protected String s3fincr1dimanche;
  protected String s3debcr2dimanche;
  protected String s3fincr2dimanche;
  protected String s3debcr3dimanche;
  protected String s3fincr3dimanche;

  //PLANNING VARIABLE SEMAINE 4
  protected String s4debcr1lundi;
  protected String s4fincr1lundi;
  protected String s4debcr2lundi;
  protected String s4fincr2lundi;
  protected String s4debcr3lundi;
  protected String s4fincr3lundi;

  protected String s4debcr1mardi;
  protected String s4fincr1mardi;
  protected String s4debcr2mardi;
  protected String s4fincr2mardi;
  protected String s4debcr3mardi;
  protected String s4fincr3mardi;

  protected String s4debcr1mercredi;
  protected String s4fincr1mercredi;
  protected String s4debcr2mercredi;
  protected String s4fincr2mercredi;
  protected String s4debcr3mercredi;
  protected String s4fincr3mercredi;

  protected String s4debcr1jeudi;
  protected String s4fincr1jeudi;
  protected String s4debcr2jeudi;
  protected String s4fincr2jeudi;
  protected String s4debcr3jeudi;
  protected String s4fincr3jeudi;

  protected String s4debcr1vendredi;
  protected String s4fincr1vendredi;
  protected String s4debcr2vendredi;
  protected String s4fincr2vendredi;
  protected String s4debcr3vendredi;
  protected String s4fincr3vendredi;

  protected String s4debcr1samedi;
  protected String s4fincr1samedi;
  protected String s4debcr2samedi;
  protected String s4fincr2samedi;
  protected String s4debcr3samedi;
  protected String s4fincr3samedi;

  protected String s4debcr1dimanche;
  protected String s4fincr1dimanche;
  protected String s4debcr2dimanche;
  protected String s4fincr2dimanche;
  protected String s4debcr3dimanche;
  protected String s4fincr3dimanche;


  //PLANNING VARIABLE SEMAINE 5
  protected String s5debcr1lundi;
  protected String s5fincr1lundi;
  protected String s5debcr2lundi;
  protected String s5fincr2lundi;
  protected String s5debcr3lundi;
  protected String s5fincr3lundi;

  protected String s5debcr1mardi;
  protected String s5fincr1mardi;
  protected String s5debcr2mardi;
  protected String s5fincr2mardi;
  protected String s5debcr3mardi;
  protected String s5fincr3mardi;

  protected String s5debcr1mercredi;
  protected String s5fincr1mercredi;
  protected String s5debcr2mercredi;
  protected String s5fincr2mercredi;
  protected String s5debcr3mercredi;
  protected String s5fincr3mercredi;

  protected String s5debcr1jeudi;
  protected String s5fincr1jeudi;
  protected String s5debcr2jeudi;
  protected String s5fincr2jeudi;
  protected String s5debcr3jeudi;
  protected String s5fincr3jeudi;

  protected String s5debcr1vendredi;
  protected String s5fincr1vendredi;
  protected String s5debcr2vendredi;
  protected String s5fincr2vendredi;
  protected String s5debcr3vendredi;
  protected String s5fincr3vendredi;

  protected String s5debcr1samedi;
  protected String s5fincr1samedi;
  protected String s5debcr2samedi;
  protected String s5fincr2samedi;
  protected String s5debcr3samedi;
  protected String s5fincr3samedi;

  protected String s5debcr1dimanche;
  protected String s5fincr1dimanche;
  protected String s5debcr2dimanche;
  protected String s5fincr2dimanche;
  protected String s5debcr3dimanche;
  protected String s5fincr3dimanche;


  //PLANNING VARIABLE SEMAINE 6
  protected String s6debcr1lundi;
  protected String s6fincr1lundi;
  protected String s6debcr2lundi;
  protected String s6fincr2lundi;
  protected String s6debcr3lundi;
  protected String s6fincr3lundi;

  protected String s6debcr1mardi;
  protected String s6fincr1mardi;
  protected String s6debcr2mardi;
  protected String s6fincr2mardi;
  protected String s6debcr3mardi;
  protected String s6fincr3mardi;

  protected String s6debcr1mercredi;
  protected String s6fincr1mercredi;
  protected String s6debcr2mercredi;
  protected String s6fincr2mercredi;
  protected String s6debcr3mercredi;
  protected String s6fincr3mercredi;

  protected String s6debcr1jeudi;
  protected String s6fincr1jeudi;
  protected String s6debcr2jeudi;
  protected String s6fincr2jeudi;
  protected String s6debcr3jeudi;
  protected String s6fincr3jeudi;

  protected String s6debcr1vendredi;
  protected String s6fincr1vendredi;
  protected String s6debcr2vendredi;
  protected String s6fincr2vendredi;
  protected String s6debcr3vendredi;
  protected String s6fincr3vendredi;

  protected String s6debcr1samedi;
  protected String s6fincr1samedi;
  protected String s6debcr2samedi;
  protected String s6fincr2samedi;
  protected String s6debcr3samedi;
  protected String s6fincr3samedi;

  protected String s6debcr1dimanche;
  protected String s6fincr1dimanche;
  protected String s6debcr2dimanche;
  protected String s6fincr2dimanche;
  protected String s6debcr3dimanche;
  protected String s6fincr3dimanche;

  //PLANNING VARIABLE SEMAINE 7
  protected String s7debcr1lundi;
  protected String s7fincr1lundi;
  protected String s7debcr2lundi;
  protected String s7fincr2lundi;
  protected String s7debcr3lundi;
  protected String s7fincr3lundi;

  protected String s7debcr1mardi;
  protected String s7fincr1mardi;
  protected String s7debcr2mardi;
  protected String s7fincr2mardi;
  protected String s7debcr3mardi;
  protected String s7fincr3mardi;

  protected String s7debcr1mercredi;
  protected String s7fincr1mercredi;
  protected String s7debcr2mercredi;
  protected String s7fincr2mercredi;
  protected String s7debcr3mercredi;
  protected String s7fincr3mercredi;

  protected String s7debcr1jeudi;
  protected String s7fincr1jeudi;
  protected String s7debcr2jeudi;
  protected String s7fincr2jeudi;
  protected String s7debcr3jeudi;
  protected String s7fincr3jeudi;

  protected String s7debcr1vendredi;
  protected String s7fincr1vendredi;
  protected String s7debcr2vendredi;
  protected String s7fincr2vendredi;
  protected String s7debcr3vendredi;
  protected String s7fincr3vendredi;

  protected String s7debcr1samedi;
  protected String s7fincr1samedi;
  protected String s7debcr2samedi;
  protected String s7fincr2samedi;
  protected String s7debcr3samedi;
  protected String s7fincr3samedi;

  protected String s7debcr1dimanche;
  protected String s7fincr1dimanche;
  protected String s7debcr2dimanche;
  protected String s7fincr2dimanche;
  protected String s7debcr3dimanche;
  protected String s7fincr3dimanche;

  //PLANNING VARIABLE SEMAINE 8
  protected String s8debcr1lundi;
  protected String s8fincr1lundi;
  protected String s8debcr2lundi;
  protected String s8fincr2lundi;
  protected String s8debcr3lundi;
  protected String s8fincr3lundi;

  protected String s8debcr1mardi;
  protected String s8fincr1mardi;
  protected String s8debcr2mardi;
  protected String s8fincr2mardi;
  protected String s8debcr3mardi;
  protected String s8fincr3mardi;

  protected String s8debcr1mercredi;
  protected String s8fincr1mercredi;
  protected String s8debcr2mercredi;
  protected String s8fincr2mercredi;
  protected String s8debcr3mercredi;
  protected String s8fincr3mercredi;

  protected String s8debcr1jeudi;
  protected String s8fincr1jeudi;
  protected String s8debcr2jeudi;
  protected String s8fincr2jeudi;
  protected String s8debcr3jeudi;
  protected String s8fincr3jeudi;

  protected String s8debcr1vendredi;
  protected String s8fincr1vendredi;
  protected String s8debcr2vendredi;
  protected String s8fincr2vendredi;
  protected String s8debcr3vendredi;
  protected String s8fincr3vendredi;

  protected String s8debcr1samedi;
  protected String s8fincr1samedi;
  protected String s8debcr2samedi;
  protected String s8fincr2samedi;
  protected String s8debcr3samedi;
  protected String s8fincr3samedi;

  protected String s8debcr1dimanche;
  protected String s8fincr1dimanche;
  protected String s8debcr2dimanche;
  protected String s8fincr2dimanche;
  protected String s8debcr3dimanche;
  protected String s8fincr3dimanche;
  
  
  //PLANNING VARIABLE SEMAINE 9
  protected String s9debcr1lundi;
  protected String s9fincr1lundi;
  protected String s9debcr2lundi;
  protected String s9fincr2lundi;
  protected String s9debcr3lundi;
  protected String s9fincr3lundi;

  protected String s9debcr1mardi;
  protected String s9fincr1mardi;
  protected String s9debcr2mardi;
  protected String s9fincr2mardi;
  protected String s9debcr3mardi;
  protected String s9fincr3mardi;

  protected String s9debcr1mercredi;
  protected String s9fincr1mercredi;
  protected String s9debcr2mercredi;
  protected String s9fincr2mercredi;
  protected String s9debcr3mercredi;
  protected String s9fincr3mercredi;

  protected String s9debcr1jeudi;
  protected String s9fincr1jeudi;
  protected String s9debcr2jeudi;
  protected String s9fincr2jeudi;
  protected String s9debcr3jeudi;
  protected String s9fincr3jeudi;

  protected String s9debcr1vendredi;
  protected String s9fincr1vendredi;
  protected String s9debcr2vendredi;
  protected String s9fincr2vendredi;
  protected String s9debcr3vendredi;
  protected String s9fincr3vendredi;

  protected String s9debcr1samedi;
  protected String s9fincr1samedi;
  protected String s9debcr2samedi;
  protected String s9fincr2samedi;
  protected String s9debcr3samedi;
  protected String s9fincr3samedi;

  protected String s9debcr1dimanche;
  protected String s9fincr1dimanche;
  protected String s9debcr2dimanche;
  protected String s9fincr2dimanche;
  protected String s9debcr3dimanche;
  protected String s9fincr3dimanche;
  
  
  //PLANNING VARIABLE SEMAINE 10
  protected String s10debcr1lundi;
  protected String s10fincr1lundi;
  protected String s10debcr2lundi;
  protected String s10fincr2lundi;
  protected String s10debcr3lundi;
  protected String s10fincr3lundi;

  protected String s10debcr1mardi;
  protected String s10fincr1mardi;
  protected String s10debcr2mardi;
  protected String s10fincr2mardi;
  protected String s10debcr3mardi;
  protected String s10fincr3mardi;

  protected String s10debcr1mercredi;
  protected String s10fincr1mercredi;
  protected String s10debcr2mercredi;
  protected String s10fincr2mercredi;
  protected String s10debcr3mercredi;
  protected String s10fincr3mercredi;

  protected String s10debcr1jeudi;
  protected String s10fincr1jeudi;
  protected String s10debcr2jeudi;
  protected String s10fincr2jeudi;
  protected String s10debcr3jeudi;
  protected String s10fincr3jeudi;

  protected String s10debcr1vendredi;
  protected String s10fincr1vendredi;
  protected String s10debcr2vendredi;
  protected String s10fincr2vendredi;
  protected String s10debcr3vendredi;
  protected String s10fincr3vendredi;

  protected String s10debcr1samedi;
  protected String s10fincr1samedi;
  protected String s10debcr2samedi;
  protected String s10fincr2samedi;
  protected String s10debcr3samedi;
  protected String s10fincr3samedi;

  protected String s10debcr1dimanche;
  protected String s10fincr1dimanche;
  protected String s10debcr2dimanche;
  protected String s10fincr2dimanche;
  protected String s10debcr3dimanche;
  protected String s10fincr3dimanche;
  
  
  protected String libelle1;
  protected String libelle2;
  protected String libelle3;
  protected String libelle4;
  protected String libelle5;
  protected String libelle6;
  protected String libelle7;
  protected String libelle8;
  protected String libelle9;
  protected String libelle10;


  protected static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("dd/MM/YYYY");

  protected static SolisManager solisMgr = SolisManager.getInstance();
  
  private HandlerUtil handlerUtil = HandlerUtil.getInstance();


  /**
   * Méthode qui valide le passage à l'étape suivante
   */
  @Override
  protected boolean validateNext() throws IOException {
    // Si on a pas etape suivante on renvoie faux
    if (!this.opNext) {
      return false;
    }
    // Si les champs requis ne sont pas présent on renvoie faux
    if (!validateFields()) {
      return false;
    }
    return super.validateNext();
  }

  /**
   * Méthode qui retourne le nombre détapes
   */
  public int getFormStepCount() {
    return 5;
  }

  /**
   * Méthode qui retourne le nuemro d'étape en cour
   */
  public int getCurrentFormStep() {
    return formStep;
  }

  /**
   * Méthode principal de gestion (point d'entrée)
   */
  @Override
  public boolean processAction() throws IOException {
    
    // Précaution : modifier le format de dates yyyy/MM/dd soumis
    // vers le format dd/MM/yyyy final
    if (Util.notEmpty(dateNaissanceEnfant)) {
      this.setDateNaissanceEnfant(AssmatUtil.convertFormDateToAssmatProfilDate(dateNaissanceEnfant));
    }
    if (Util.notEmpty(dateDebutAccueil)) {
      this.setDateDebutAccueil(AssmatUtil.convertFormDateToAssmatProfilDate(dateDebutAccueil));
    }
    if (Util.notEmpty(dateFinProvisoire)) {
      this.setDateFinProvisoire( AssmatUtil.convertFormDateToAssmatProfilDate(dateFinProvisoire));
    }
   
    // Récuprère le numéro de dossier par rapport au membre connecté
    Member mbr = Channel.getChannel().getCurrentLoggedMember();
    ProfilASSMAT profilAM = ProfilManager.getInstance().getProfilASSMAT(mbr);
    if(profilAM == null) {
      channel.getCurrentJcmsContext().addMsg(new JcmsMessage(Level.ERROR, "Impossible d'enregistrer, profil introuvable"));
      return false;
    }
    
    String idModifDeclarationString = request.getParameter("idModifDeclaration");
    String idModifStatusEnCours = request.getParameter("idModifEnCours");
        

    //si il existe un id de declaration pour modifier une declaration en cours ou terminer une déclaration en brouillon (idModifDeclaration)
    //alors récupérer la declaration a modifier et la seter dans le formhandler 
    //(seulement si num_agrement de la declaration correspond au num_dossier de l'assmat)
    if(Util.notEmpty(idModifDeclarationString)) { 
      Integer idModifDeclaration = Integer.parseInt(request.getParameter("idModifDeclaration"));
      // Récupère la declaration depuis le web-service            
      DeclarationAccueilDTO declarationModif;
      try {
        declarationModif = DemarcheUtil.getDeclarationAccueilById(idModifDeclaration);
        logger.debug("processAction() - declarationModif - getDeclarationAccueilByIdUsingGET1 = " + declarationModif);
        // Si la declaration n'est pas vide et que la declaration appartient bien a l'assmat connectée
        if(declarationModif!= null && declarationModif.getNumDossier() == profilAM.getNum_agrement()){
          this.declaration = declarationModif;
          request.getSession().setAttribute("declaration", declarationModif);          
        }
      } catch (ApiException e) {
        e.printStackTrace();
      }
    }
    
    // Si il s'agit de terminer une déclaration en brouillon
    // Alors reprend à l'étape la plus avancée du formulaire.
    if(Util.notEmpty(request.getParameter("idModifDeclaration")) && Util.isEmpty(idModifStatusEnCours)) {
      if( Util.notEmpty(declaration.getPlanningRegulier()) || Util.notEmpty(declaration.getUniquementVacances()) || Util.notEmpty(declaration.getUniquementVacances()) ) {
        formStep = 2;
      }else if(Util.notEmpty(declaration.getAccueilDomicile())) {
        formStep = 1;
      }
    }

    
    
    // Récupère la déclaration en cours depuis le formhandler.
    // ou création de la déclaration si jamais créée
    DeclarationAccueilDTO declaration;
    if(Util.isEmpty(this.getDeclaration())) {
      declaration = new DeclarationAccueilDTO();
      this.setDeclaration(declaration);
    }else {
      declaration = this.getDeclaration();
    }
    

    // Si on a pas etape suivante, ni précédente, ni finale
    if (!opNext && !opPrevious && !opFinish && !opCreate) {
      return false;
    }
    
    
    
    // Si etape précédente aucun enrigistrement dans la declaration
    Boolean isChangeStep = Util.notEmpty(request.getParameter("opChangeStep"));
    if(opPrevious) {
      try {
        declaration = DemarcheUtil.getDeclarationAccueilById(declaration.getIdDeclaration());
        request.getSession().setAttribute("declaration", declaration);
      } catch (ApiException e) {
        logger.warn("Impossible de revenir a l'état précédent pour la déclaration", e);
      }
      // Si changement d'onglet depuis la barre d'état alors pas d'action à effectuer
      if(isChangeStep) {
        return false;
      }
      return super.processAction();
    }
    
    
    
    // Information sur l'assmat
    declaration.setNomAssmat(mbr.getName());
    declaration.setPrenomAssmat(mbr.getFirstName());
    declaration.setNumDossier(profilAM.getNum_agrement());
    
      
    declaration.setEtatDeclaration("brouillon");
    if(opFinish) {
      declaration.setEtatDeclaration("en cours");
    }
        
    declaration.setType("debut");
    declaration.setCreationWeb(true);
    declaration.setNumDossier(profilAM.getNum_agrement());

    // ENFANT ACCUEILLI
    if(formStep == ENFANT_ACCUEILLI) {
      createEnfantAccueilli();
    }    
    // LIEU ACCUEIL
    if(formStep == LIEU_ACCUEIL) {
      createLieuAccueil();
    }    
    // MODALITES
    if(formStep == MODALITES) {
     createModalites();  
    }    
    // PLANNING
    if(formStep == PLANNING) {       
      // Mettre à jour les champs depuis la requête. Nécessaire faute au nouveau format des données envoyées par le formulaire
//      JSONArray planningDataArray = PlanningUtil.convertRequestFormToHandlerVars(request);
//      if (Util.notEmpty(planningDataArray)) updatePlanningDataFromJson(planningDataArray);
      
      // Planning régulier
      if(declaration.getPlanningRegulier()) { 
        createPlanningRegulier();
      }else if(declaration.getPlanningSemainesTypes() != null && declaration.getPlanningSemainesTypes()) {
        // Planning variable
        createPlanningVariable();
      } else {
        // Planning occasionnel
        createPlanningOccasionnel();
      }
    }    
    
    request.getSession().setAttribute("declaration", declaration);      

    // Passage a l etape suivant ou enregistrement en brouillon
    Boolean isSaveOk = true;
    if (opNext || opCreate || opFinish) {
      // Vérifie la cohérence des données
      // Champ obligatoire et regex des champs de l'étape courant pour etape suivant
      // Seulement regex des champs de l'étape courant pour enregistrer en brouillon
      if (!validateFields()) {
        return false;
      }else {
        
        
        // Si il s'agit d'une modification d'une déclaration d'accueil déjà en cours
        // Une nouvelle est créée par le web service il faut appeler la méthode de création et non de modification
        // L'id est donc passé a null pour créer une nouvelle déclaration
        if(Util.notEmpty(idModifStatusEnCours) && (declaration.getIdDeclaration() == null || idModifStatusEnCours.equals(Integer.toString(declaration.getIdDeclaration())))) {
          declaration.setIdDeclaration(null);
          for(SemaineTypeDTO itSemaine : declaration.getSemaineTypes() ) {
             itSemaine.setIdSemainetype(null);
             for (CreneauDTO itCreneau : itSemaine.getCreneaux()) {
               itCreneau.setIdCreneau(null);
             }
          }
        }
             
        
        // Si ils'agit d'une nouvelle déclaration
        // ou d'une modification d'une déclaration en cours
        // Alors création d'une déclaration d'accueil
        if(Util.isEmpty(declaration.getIdDeclaration())){
          isSaveOk = createSaveDeclaration();
        }else {
          isSaveOk = modifSaveDeclaration();
        }
      }
    }
    
    // Si enregistrement en mode brouillon ne pas passer à l'étape suivante
    // Ou si la sauvegarde de la declaration se passe mal
    if(opCreate || !isSaveOk) {
      return false;
    }    
  
    return super.processAction();
  }

  
  /**
   * ONGLET enfant accueilli
   */
  private void createEnfantAccueilli() {
    declaration.setNomEnfant(nomEnfant != null ? nomEnfant.toUpperCase() : nomEnfant);
    declaration.setPrenomEnfant(Util.recapitalize(prenomEnfant));
    declaration.setSexeEnfant(sexeEnfant);
    if(Util.notEmpty(dateNaissanceEnfant) && isValideDateFormat(dateNaissanceEnfant)) {
      DateTime date = DATE_TIME_FORMAT.parseDateTime(dateNaissanceEnfant);
      declaration.setDateNaissanceEnfant(date);
    }else {
      declaration.setDateNaissanceEnfant(null);
    }
    if(Util.notEmpty(dateDebutAccueil) && isValideDateFormat(dateDebutAccueil)) {
      DateTime date = DATE_TIME_FORMAT.parseDateTime(dateDebutAccueil);
      declaration.setDateDebutAccueil(date);
    }else {
      declaration.setDateDebutAccueil(null);
    }
    declaration.setCiviliteRepresentant1(civiliteRepresentant1);
    declaration.setNomRepresentant1(nomRepresentant1 != null ? nomRepresentant1.toUpperCase() : nomRepresentant1);
    declaration.setPrenomRepresentant1(Util.recapitalize(prenomRepresentant1));
    declaration.setTelephoneRepresentant1(telephoneRepresentant1);    
    declaration.setAdresseRepresentant1(adresseRepresentant1);
    declaration.setCpRepresentant1(codePostalRepresentant1);
    declaration.setCommuneRepresentant1(villeRepresentant1);
    declaration.setComplAdresseRepresentant1(complementRepresentant1);       

       
    declaration.setCiviliteRepresentant2(civiliteRepresentant2);
    declaration.setNomRepresentant2(nomRepresentant2 != null ? nomRepresentant2.toUpperCase() : nomRepresentant2);
    declaration.setPrenomRepresentant2(Util.recapitalize(prenomRepresentant2));
    declaration.setTelephoneRepresentant2(telephoneRepresentant2);
    declaration.setAdresseRepresentant2(adresseRepresentant2);
    declaration.setCpRepresentant2(codePostalRepresentant2);
    declaration.setCommuneRepresentant2(villeRepresentant2);
    declaration.setComplAdresseRepresentant2(complementRepresentant2);
    // 0011670: Déclaration d'accueil - enfant accueilli - règles enregistrement civilite du représentant légal 2 
    // Si le bouton radio de la civilité du représentant légal 2 est coché mais qu’aucune autre donnée du représentant légal 2 est valorisée alors il n’est pas obligatoire de valoriser le reste des champs du représentant légal 2 et la valeur du bouton radio de la civilité du représentant légal 2 ne doit pas être enregistré
    if( Util.isEmpty(nomRepresentant2) && Util.isEmpty(prenomRepresentant2) && Util.isEmpty(telephoneRepresentant2) && Util.isEmpty(adresseRepresentant2) && Util.isEmpty(codePostalRepresentant2) && Util.isEmpty(villeRepresentant2) && Util.isEmpty(complementRepresentant2) ) {
      declaration.setCiviliteRepresentant2(null);
    }
  }

  /**
   *  ONGLET Lieu accueil
   */
  private void createLieuAccueil() {
    if(Util.notEmpty(accueilDomicile)) {
     declaration.setAccueilDomicile(Boolean.valueOf(accueilDomicile));
     
     
     
     // LIEU ACCUEIL DOMICILE   
     declaration.setAdresseDomicile(adresseDomicile);
     declaration.setCommuneDomicile(communeDomicile);
     declaration.setCpDomicile(cpDomicile);    
     // LIEU ACCUEIL MAM
     declaration.setNomMam(nomMam);
     declaration.setAdresseMam(adresseMam);
     declaration.setCommuneMam(communeMam);
     declaration.setCpMam(cpMam); 
    }
  }

  /**
   * ONGLET MODALITE
   */
  private void createModalites() {
    if(planningRegulier != null) {
      declaration.setPlanningRegulier(Boolean.valueOf(planningRegulier));
    }
    
    if(accueilOccasionnel != null) {
      declaration.setAccueilOccasionnel(Boolean.valueOf(accueilOccasionnel));
    }else {
      declaration.setAccueilOccasionnel(null);
    }
    
    if(planningSemainesTypes != null) {
      declaration.setPlanningSemainesTypes(Boolean.valueOf(planningSemainesTypes));
    }else {
      declaration.setPlanningSemainesTypes(null);
    }
    
    if(uniquementVacances != null) {
    declaration.setUniquementVacances(Boolean.valueOf(uniquementVacances));
    }
    if(estProvisoire != null) {
      declaration.setEstProvisoire(Boolean.valueOf(estProvisoire));
    }
    if(Util.notEmpty(dateFinProvisoire) && isValideDateFormat(dateFinProvisoire)) {
      DateTime date = DATE_TIME_FORMAT.parseDateTime(dateFinProvisoire);
      declaration.setDateFinProvisoire(date);
    }else {
      declaration.setDateFinProvisoire(null);
    }
    declaration.setMotifProvisoire(motifProvisoire);
  }

  /**
   * Création du planning régulier (ONGLET PLANNING)
   */
  private void createPlanningRegulier() {
    List<SemaineTypeDTO> semaineList = new ArrayList<SemaineTypeDTO>();     
    // Création de plunning régulier
    semaineList.add(createSemaine(REGULIER, 1));
    if(accueilVacances != null) {
      declaration.setAccueilVacances(Boolean.valueOf(accueilVacances));             
    }
    if(planningDifferentVacances != null) {
      declaration.setPlanningDifferentVacances(Boolean.valueOf(planningDifferentVacances));
      if(declaration.getPlanningDifferentVacances()){
        // sauvegarde la planning vacances en semaine 2 (semaine vacances)
        semaineList.add(createSemaine(VACANCES, 2));
      }else {
        // Si le planning vacances est le même que le régulier
        // sauvegerde le planning régulier comme semaine 2 (semaine vacances)
        semaineList.add(createSemaine(REGULIER, 2));
      }
    }else {
      declaration.setPlanningDifferentVacances(null);
    }
    declaration.setSemaineTypes(semaineList); 
    declaration.setPrecision(precision);
  }
  
  /**
   * Création du planning variable (ONGLET PLANNING)
   */
  private void createPlanningVariable() {
    List<SemaineTypeDTO> semaineList = new ArrayList<SemaineTypeDTO>();      
    for(int nbSemaine = 1 ; nbSemaine <= 10 ; nbSemaine++ ) {
      SemaineTypeDTO semaine = createSemaine(VARIABLE, nbSemaine);
      if(Util.notEmpty(semaine)) {
        semaineList.add(semaine);
      }
    }        
    declaration.setSemaineTypes(semaineList); 
    declaration.setPrecision(precision);     
  }
  
  
  /**
   * Création du planning occasionnel (ONGLET PLANNING)
   */
  private void createPlanningOccasionnel() {
    declaration.setPlanningDifferentVacances(null);
    declaration.setSemaineTypes(null);
    declaration.setPrecision(precision);
  }
  
  

  private SemaineTypeDTO createSemaine(int typeSemaine, int numSemaine) {  
    SemaineTypeDTO semaine = new SemaineTypeDTO();         
    semaine.setNumero(numSemaine);      
    List<CreneauDTO> crenauxList = new ArrayList<CreneauDTO>(); 
    String libelle = null;
    try {      
      // Libelle de la semaine variable
      if(typeSemaine == VARIABLE) {
        libelle = (String) ReflectUtil.invokeMethod(this, "getLibelle"+numSemaine);
        semaine.setLibelle(libelle);
      }        
      for(String itJour : JOURS) {
        Boolean itHasJour =  (Boolean) ReflectUtil.invokeMethod(this, (typeSemaine == VARIABLE ? "getS"+numSemaine : "isRegul") +itJour + (typeSemaine == VACANCES ? "Vac" : ""));       
        if(itHasJour != null && itHasJour) {                             
          // Pour les 3 creneaux possibles par jour
          // Si il y a un jour de coché le creneau 1 est renseigné pour que le jour reste coché (un message invitera l'utilisateur a renseigner un crenau pour chaque jour)
          for(int i=1; i<=3; i++) {  
            String itDebutCreneauJour = (String) ReflectUtil.invokeMethod(this, "get" + (typeSemaine == VARIABLE ? "S"+numSemaine+"d" : "D") + "ebcr"+ i +itJour.toLowerCase() + (typeSemaine == VACANCES ? "Vac" : ""));
            String itFinCreneauJour = (String) ReflectUtil.invokeMethod(this, "get" + (typeSemaine == VARIABLE ? "S"+numSemaine+"f" : "F") + "incr"+ i +itJour.toLowerCase() + (typeSemaine == VACANCES ? "Vac" : ""));
            
            if(itDebutCreneauJour != null && itFinCreneauJour != null) {
              itDebutCreneauJour = itDebutCreneauJour.replace(":", "h");
              itFinCreneauJour = itFinCreneauJour.replace(":", "h");
            }
            
            if(Util.notEmpty(itDebutCreneauJour) || Util.notEmpty(itFinCreneauJour) || i == 1) {
              CreneauDTO creneau = new CreneauDTO();
              creneau.setJourSemaine(itJour);
              creneau.setHeureDebut(CreneauHorraire.formatCreneau(itDebutCreneauJour));
              creneau.setHeureFin(CreneauHorraire.formatCreneau(itFinCreneauJour));
              crenauxList.add(creneau);              
              // Ajout lib spécifique semaine générique si pas remplie
              if(typeSemaine == VARIABLE) {
                String itLibelle = (String) ReflectUtil.invokeMethod(this, "getLibelle"+numSemaine);
                if(Util.isEmpty(itLibelle)) {
                  semaine.setLibelle("Semaine " + numSemaine);
                }
              }              
            }            
          }                        
        }          
      } 
    } catch (IllegalAccessException e) {
      logger.warn("Impossible de créer le formulaire pour planning", e);
    } catch (InvocationTargetException e) {  
      logger.warn("Impossible de créer le formulaire pour planning", e);
    } catch (NoSuchMethodException e) {
      logger.warn("Impossible de créer le formulaire pour planning", e);
    }
    semaine.setCreneaux(crenauxList);
    if(crenauxList.isEmpty() && Util.isEmpty(libelle)) {
      return null;
    }
    return semaine;  
  }

  
  /**
   * Indique si une date est au bon format (jj/mm/aaaa)
   * @param dateNaissanceEnfant
   * @return
   */
  public static boolean isValideDateFormat(String dateString) {   
    if (Util.notEmpty(dateString)) {
      // Format date incorrect
      if(!Pattern.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}", dateString)) {
        return false;
      }      
      // Impossible de parser la date
      try {
        logger.debug(DATE_TIME_FORMAT.parseDateTime(dateString));
      } catch (IllegalFieldValueException e) {
        logger.debug("Date incorrect", e);
        return false;
      }
    }
    return true;
  }
  

  /**
   * Permet d'envoyer une déclaration d'accueil comme modification au webService
   * @return
   */
  private boolean modifSaveDeclaration() {  
    try {
      DemarcheUtil.modifierDeclarationAccueil(declaration.getIdDeclaration(), declaration);
      logger.debug("modifSaveDeclaration() - declaration - modifierDeclarationAccueilUsingPUT1 = " + declaration);
      if(opCreate) {
        channel.getCurrentJcmsContext().addMsg(new JcmsMessage(Level.INFO, "Modifications enregistrées"));
      }
    } catch (ApiException e) {
      logger.warn("Impossible de modifier la déclaration d'accueil", e) ;
      channel.getCurrentJcmsContext().addMsg(new JcmsMessage(Level.ERROR, "Impossible d'enregistrer la modification suite à un problème technique"));
      return false;
    }
    return true; 
  }

  /**
   * Creation d'une declaration a partir de la declaration en session
   */
  private boolean createSaveDeclaration() {
    try {
      DeclarationAccueilDTO declarationRetour = DemarcheUtil.creerDeclarationAccueil(declaration);
      logger.debug("createSaveDeclaration() - declarationRetour - creerDeclarationAccueilUsingPOST = " + declarationRetour);
      // Permet de renvoier l'id et la date de declaration a chaque mise à jour
      declaration.setIdDeclaration(declarationRetour.getIdDeclaration());
      declaration.setIdAccueil(declarationRetour.getIdAccueil());
      declaration.setDateDeclaration(declarationRetour.getDateDeclaration());
      if(opCreate){
        channel.getCurrentJcmsContext().addMsg(new JcmsMessage(Level.INFO, "Enregistré en mode brouillon"));
      }
    } catch (ApiException e) {
      logger.warn("Impossible d'enregistrer la déclaration d'accueil", e) ;
      channel.getCurrentJcmsContext().addMsg(new JcmsMessage(Level.ERROR, "Impossible d'enregistrer suite à un problème technique"));
      return false;
    }
    return true;
  }

  /**
   * Vérifie la nullité des champs de la modale
   *
   * @return <code>true</code> si tous les champs sont remplies
   * @throws IOException
   */
  public boolean validateFields() throws IOException {
    boolean valide = true;
    List<JcmsMessage> listError= new ArrayList<JcmsMessage>();
    
    if(formStep == ENFANT_ACCUEILLI) {      
      checkChampsEnfant(listError);
      checkChampsRL1(listError);
      checkChampsRL2(listError);
    } else if(formStep == LIEU_ACCUEIL) {
      checkLieuAccueil(listError);
    } else if(formStep == MODALITES) {
      checkModalites(listError);
    } else if(formStep == PLANNING) {
      // Vérifie planning variable ou régulier suivant le choix de l'assmat dans les modalités
      checkPlanning(listError);
    }
    
    if(Util.notEmpty(listError)){
      valide = false;
    }
    
    // On affiche l'erreur si il y en à une
    if (!valide) {
      if(!listError.isEmpty()){
        request.setAttribute(JcmsConstants.JCMS_MSG_LIST, listError);
       }
    }   
    return valide;
  }


  private void checkPlanning(List<JcmsMessage> listError) {
    // Planning occasionnel
    if(declaration.getAccueilOccasionnel() != null && declaration.getAccueilOccasionnel()){
      checkPrecision(listError, AssmatUtil.getMessage("ASS-DEC-AOCC-PRE-OBL-HTML"), AssmatUtil.getMessage("ASS-DEC-AOCC-PRE-ERR-HTML"));
    // Planning variable sans semaines types
    }else if(declaration.getPlanningSemainesTypes() != null && !declaration.getPlanningSemainesTypes()){
      checkPrecision(listError, AssmatUtil.getMessage("ASS-DEC-PIRR-PRE-OBL-HTML"), AssmatUtil.getMessage("ASS-DEC-PIRR-PRE-ERR-HTML"));
    // Planning régulier ou planning variable avec semaines types
    }else {
      // Précisions > 250 caract
      if(Util.notEmpty(precision)){
        if(precision.length()> 250){
          listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-PL-Z2-PRE-AUTR-CAR-HTML")));
        }
      }
      checkPlanningRegulier(listError);
      checkPlanningVariable(listError);
    }
  }
  
  /**
   * Depuis un tableau JSON, mettre à jour les données de planning
   * @param planningDataArray
   */
  private void updatePlanningDataFromJson(JSONArray planningDataArray) {
    for (int index = 0; index < planningDataArray.length(); index++) {
      try {
        JSONObject itObject = planningDataArray.getJSONObject(index);
        ReflectUtil.invokeMethod(this, itObject.getString(PlanningUtil.fieldSetMethod), itObject.getString(PlanningUtil.fieldValue));
      } catch (Exception e) {
        logger.error(e);
      }
    }
  }

  private void checkPrecision(List<JcmsMessage> listError, String errorObl, String ErrorLgt){
    if(Util.isEmpty(precision)) {
      listError.add(new JcmsMessage(JcmsMessage.Level.WARN, errorObl));
    // Précisions > 1000 caract
    } else {
      if(precision.length()> 1000){
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, ErrorLgt));
      }
    }
  }

  private void checkPlanningVariable(List<JcmsMessage> listError) {
    if(!declaration.getPlanningRegulier()) {
      
      // Erreur si longueur libelle > 20
      Boolean valideLibelle = true;
      for(int semaineCpt = 1 ; semaineCpt <= 10 ; semaineCpt++){
        String itLibelle;
        try {
          itLibelle = (String) ReflectUtil.invokeMethod(this, "getLibelle" + semaineCpt);
          if(Util.notEmpty(itLibelle)){
            if(itLibelle.length() > 20){
              valideLibelle = false;
            }
          }            
        } catch (IllegalAccessException e) {
          logger.warn("Impossible de créer le formulaire pour planning", e);
        } catch (InvocationTargetException e) {
          logger.warn("Impossible de créer le formulaire pour planning", e);
        } catch (NoSuchMethodException e) {
          logger.warn("Impossible de créer le formulaire pour planning", e);
        }      
      }
      if(!valideLibelle) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-PL-Z2V-SPE-CAR-HTML")));
      }
      
      
      // Au moins un des jours du planning variable doit être coché pour la semaine 1, sinon un message d’erreur s’affiche
      if( ( (s1Lundi == null || !s1Lundi) && (s1Mardi == null || !s1Mardi)  && (s1Mercredi == null || !s1Mercredi) && (s1Jeudi == null || !s1Jeudi) && (s1Vendredi == null || !s1Vendredi) && (s1Samedi == null || !s1Samedi) && (s1Dimanche == null || !s1Dimanche)) ||
          ( (s2Lundi == null || !s2Lundi) && (s2Mardi == null || !s2Mardi)  && (s2Mercredi == null || !s2Mercredi) && (s2Jeudi == null || !s2Jeudi) && (s2Vendredi == null || !s2Vendredi) && (s2Samedi == null || !s2Samedi) && (s2Dimanche == null || !s2Dimanche)) ){
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-PL-Z1-QUE-ERR-HTML")));
      }


      // check creneau horraire (format)
      Boolean valideCreneauFormat = true;
      Boolean valideCoherenceHorraire = true;      
      Boolean valideCreneauPresence = true;
      Boolean valideCoherenceCreneaux = true;
      
      for(int semaineCpt = 1 ; semaineCpt <= 10 ; semaineCpt++){       
        for(String itJour : JOURS) { 
          
          Field sCptJour = ReflectUtil.getField(DeclarerAccueilAssmatHandler.class, "s" + semaineCpt + itJour);
          Boolean isCheckJour = (Boolean) ReflectUtil.getFieldValue(this, sCptJour);
          if(isCheckJour == null) {
            isCheckJour = false;
          }
          
          Boolean unCreneauDeRenseigne = false;
          String finPrecedentCrenauHorraire = "";
          
          for(int creCpt = 1 ; creCpt < 4 ; creCpt++){
            try {
              String itDebutCreneauJour = (String) ReflectUtil.invokeMethod(this, "getS"+ semaineCpt + "debcr"+ creCpt +itJour.toLowerCase());
              String itFinCreneauJour = (String) ReflectUtil.invokeMethod(this, "getS" + semaineCpt +"fincr"+ creCpt +itJour.toLowerCase());

              // Erreur si un des horraire n'est pas dans le format attendu (00h00)
              if(Util.notEmpty(itDebutCreneauJour)){
                valideCreneauFormat = valideCreneauFormat && checkFormatHeure(itDebutCreneauJour);
              }
              if(Util.notEmpty(itFinCreneauJour)){
                valideCreneauFormat = valideCreneauFormat && checkFormatHeure(itFinCreneauJour);
              }

              // Erreur si une des deux date est vide 
              if((Util.isEmpty(itDebutCreneauJour)&& Util.notEmpty(itFinCreneauJour) ) || (Util.notEmpty(itDebutCreneauJour)&& Util.isEmpty(itFinCreneauJour))){
                valideCoherenceHorraire = false;
              }
              
              // Erreur si un jour est coché sans creneaux
              // indique si un creneau est renseigné pour ce jour
              if(Util.notEmpty(itFinCreneauJour) || Util.notEmpty(itDebutCreneauJour)) {
                unCreneauDeRenseigne = true;
              }
              
              // Erreur si la règle suivant n'est pas respectée
              // on doit toujours avoir h1deb < h1fin < h2deb < h2fin < h3deb < h3fin
              if(Util.notEmpty(itFinCreneauJour) && Util.notEmpty(itDebutCreneauJour)) {
                if( 
                    (!crenauDebFinValide(itDebutCreneauJour, itFinCreneauJour)) || 
                    (Util.notEmpty(finPrecedentCrenauHorraire) && !crenauDebFinValide(finPrecedentCrenauHorraire, itDebutCreneauJour) )
                  ) {
                  valideCoherenceCreneaux = false;
                }                                            
              }
              finPrecedentCrenauHorraire = itFinCreneauJour;

            } catch (IllegalAccessException e) {
              logger.warn("Impossible de créer le formulaire pour planning", e);
            } catch (InvocationTargetException e) {
              logger.warn("Impossible de créer le formulaire pour planning", e);
            } catch (NoSuchMethodException e) {
              logger.warn("Impossible de créer le formulaire pour planning", e);
            }
          }
          // Erreur si un jour est coché sans creneaux
          if(isCheckJour && !unCreneauDeRenseigne) {
            valideCreneauPresence = false;
          }
        }      
      }

      if(!valideCreneauFormat) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-PL-Z2-HEU-ERR-HTML")));
      }
      if(!valideCoherenceHorraire) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("DATE-DEBUT-ET-DATE-FIN")));             
      }
      if(!valideCreneauPresence) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-PL-Z2R-ERR-OBL-HTML")));             
      }
      if(!valideCoherenceCreneaux) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-PL-Z2R-COH-HEU-HTML")));             
      }
    }
  }


  private void checkPlanningRegulier(List<JcmsMessage> listError) {
    // check creneau horraire (format)
    if(declaration.getPlanningRegulier()) {
      Boolean valideCreneauFormat = true;
      Boolean valideCoherenceHorraire = true;
      Boolean valideCreneauPresence = true;
      Boolean valideCoherenceCreneaux = true;
      for(int semaineCpt = 1 ; semaineCpt <= 2 ; semaineCpt++){
        for(String itJour : JOURS) {
                  
          Boolean isCheckJour = false;
          try {
            isCheckJour = (Boolean) ReflectUtil.invokeMethod(this, "isRegul" + itJour + (semaineCpt == 2 ? "Vac" : ""));
            if(isCheckJour == null) {
              isCheckJour = false;
            }
          } catch (IllegalAccessException e) {
            logger.warn("Impossible de créer le formulaire pour planning", e);
          } catch (InvocationTargetException e) {
            logger.warn("Impossible de créer le formulaire pour planning", e);
          } catch (NoSuchMethodException e) {
            logger.warn("Impossible de créer le formulaire pour planning", e);
          }
          
          
          
          Boolean unCreneauDeRenseigne = false;
          String finPrecedentCrenauHorraire = "";
          for(int creCpt = 1 ; creCpt < 4 ; creCpt++){
            try {
              String itDebutCreneauJour = (String) ReflectUtil.invokeMethod(this, "getDebcr"+ creCpt +itJour.toLowerCase() + (semaineCpt == 2 ? "Vac" : ""));          
              String itFinCreneauJour = (String) ReflectUtil.invokeMethod(this, "getFincr"+ creCpt +itJour.toLowerCase() + (semaineCpt == 2 ? "Vac" : "")); 

              
              if(itDebutCreneauJour != null && itFinCreneauJour != null) {
                itDebutCreneauJour = itDebutCreneauJour.replace(":", "h");
                itFinCreneauJour = itFinCreneauJour.replace(":", "h");               
              }
              
              // Erreur si un des horraire n'est pas dans le format attendu (00h00)
              if(Util.notEmpty(itDebutCreneauJour)){
                valideCreneauFormat = valideCreneauFormat && checkFormatHeure(itDebutCreneauJour);
              }
              if(Util.notEmpty(itFinCreneauJour)){
                valideCreneauFormat = valideCreneauFormat && checkFormatHeure(itFinCreneauJour);
              }

              // Erreur si une des deux date est vide 
              if((Util.isEmpty(itDebutCreneauJour)&& Util.notEmpty(itFinCreneauJour) ) || (Util.notEmpty(itDebutCreneauJour) && Util.isEmpty(itFinCreneauJour))){
                valideCoherenceHorraire = false;
              }              
              
              // Erreur si un jour est coché sans creneaux
              // indique si un creneau est renseigné pour ce jour
              if(Util.notEmpty(itFinCreneauJour) || Util.notEmpty(itDebutCreneauJour)) {
                unCreneauDeRenseigne = true;
              }
              
              // Erreur si la règle suivant n'est pas respectée
              // on doit toujours avoir h1deb < h1fin < h2deb < h2fin < h3deb < h3fin
              if(Util.notEmpty(itFinCreneauJour) && Util.notEmpty(itDebutCreneauJour)) {
                if( 
                    (!crenauDebFinValide(itDebutCreneauJour, itFinCreneauJour)) || 
                    (Util.notEmpty(finPrecedentCrenauHorraire) && !crenauDebFinValide(finPrecedentCrenauHorraire, itDebutCreneauJour) )
                  ) {               
                  valideCoherenceCreneaux = false;
                }                                            
              }
              finPrecedentCrenauHorraire = itFinCreneauJour;

            } catch (IllegalAccessException e) {
              logger.warn("Impossible de créer le formulaire pour planning", e);
            } catch (InvocationTargetException e) {
              logger.warn("Impossible de créer le formulaire pour planning", e);
            } catch (NoSuchMethodException e) {
              logger.warn("Impossible de créer le formulaire pour planning", e);
            }
          }
          // Erreur si un jour est coché sans creneaux
          if(isCheckJour && !unCreneauDeRenseigne) {
            valideCreneauPresence = false;
          }
        }
      }
      if(!valideCreneauFormat) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-PL-Z2-HEU-ERR-HTML")));
      }
      if(!valideCoherenceHorraire) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("DATE-DEBUT-ET-DATE-FIN")));             
      }
      if(!valideCreneauPresence) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-PL-Z2R-ERR-OBL-HTML")));             
      }
      if(!valideCoherenceCreneaux) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-PL-Z2R-COH-HEU-HTML")));             
      }
      

      if(opFinish) {

        // check semaine réguliere

        // Au moins un des jours du planning régulier doit être coché, sinon un message d’erreur s’affiche
        if(!regulLundi && !regulMardi  && !regulMercredi && !regulJeudi && !regulVendredi && !regulSamedi && !regulDimanche){
          listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-PL-Z1-QUE-ERR-HTML")));
        }
        

        // check question des vacances
        if(!Boolean.valueOf(uniquementVacances)) {
          if (Util.isEmpty(accueilVacances)) {
            listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-PL-Z3-QUE-ERR-HTML")));
          }
          if (Util.notEmpty(accueilVacances) && Boolean.valueOf(accueilVacances) && Util.isEmpty(planningDifferentVacances)) {
            listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-PL-Z3-QUE-SCO-ERR-HTML")));
          }
          
          // check semaine vacances
          if(Util.notEmpty(accueilVacances) && Boolean.valueOf(accueilVacances) && Util.notEmpty(planningDifferentVacances) && Boolean.valueOf(planningDifferentVacances)) {          
            //  Si l’assistante maternelle a cliqué sur « oui » pour le planningDifferentVacances, alors elle doit obligatoirement saisir un jour
            if(!regulLundiVac && !regulMardiVac  && !regulMercrediVac && !regulJeudiVac && !regulVendrediVac && !regulSamediVac && !regulDimancheVac){
              listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-PL-Z1-QUE-ERR-HTML")));
            }         
          }
        }

        

      }
    }

  }
  
  
  
  /**
   * Indique si l'horaire de début est bien inférieux à l'horraire de fin
   * @param itDebutCreneauJour
   * @param itFinCreneauJour
   * @return
   */
  private boolean crenauDebFinValide(String itDebutCreneauJour, String itFinCreneauJour) {
    
    // Seulement si le format de date est correct
    if(checkFormatHeure(itDebutCreneauJour) && checkFormatHeure(itFinCreneauJour)) {
    
      String bebut = CreneauHorraire.formatCreneau(itDebutCreneauJour);
      String horraireDebut[] = bebut.split("h");
      
      String fin = CreneauHorraire.formatCreneau(itFinCreneauJour);
      String horraireFin[] = fin.split("h");
      
      if(horraireDebut.length == 2 && horraireFin.length == 2) {
        int heureDebut = Integer.parseInt(horraireDebut[0]);
        int minuteDebut = Integer.parseInt(horraireDebut[1]);
        
        int heureFin = Integer.parseInt(horraireFin[0]);
        int minutefin = Integer.parseInt(horraireFin[1]);
        
        if(heureDebut > heureFin) {
          return false;
        }else if(heureDebut ==  heureFin && minuteDebut >= minutefin) {
          return false;
        }     
      } 
    }
    return true;
  }

  /**
   * Persiste les données dans des input type hidden pour permettre le retour
   * aux etapes precedentes
   *
   * @return le stringbuilder des inputs
   */
  public String getFormStepHiddenFields() {
    StringBuilder sb = new StringBuilder();
    // Champs caché général
    // sb.append(handlerUtil.getHiddenFieldTag("idDeclaration", declaration.getIdDeclaration() != null ? declaration.getIdDeclaration()+"" : "" ));
    sb.append(handlerUtil.getHiddenFieldTag("formStep", formStep));
    // Onglet lieu d'accueil
    /*
    if (formStep == ENFANT_ACCUEILLI) {
      getHiddenFieldLieuAccueil(sb);
      getHiddenFieldModalites(sb);
      getHiddenFieldPlanning(sb);
    } */
    // Onglet enfant accueilli
    if (formStep >= LIEU_ACCUEIL) {
      getHiddenFieldEnfantAccueilli(sb);
    }
    // Onglet modalités 
    if (formStep >= MODALITES) {
      getHiddenFieldLieuAccueil(sb);
    }
    // Onglet planning
    if (formStep >= PLANNING) {
      getHiddenFieldModalites(sb);
    }
    return sb.toString();
  }





  /**
   * Champs cachés pour l'enfant accueilli
   * @param sb
   */
  private void getHiddenFieldEnfantAccueilli(StringBuilder sb) {
    sb.append(handlerUtil.getHiddenFieldTag("nomEnfant", nomEnfant));
    sb.append(handlerUtil.getHiddenFieldTag("prenomEnfant", prenomEnfant));
    sb.append(handlerUtil.getHiddenFieldTag("sexeEnfant", sexeEnfant));
    sb.append(handlerUtil.getHiddenFieldTag("dateNaissanceEnfant", dateNaissanceEnfant));
    sb.append(handlerUtil.getHiddenFieldTag("dateDebutAccueil", dateDebutAccueil));
    
    sb.append(handlerUtil.getHiddenFieldTag("civiliteRepresentant1", civiliteRepresentant1));
    sb.append(handlerUtil.getHiddenFieldTag("nomRepresentant1", nomRepresentant1));
    sb.append(handlerUtil.getHiddenFieldTag("prenomRepresentant1", prenomRepresentant1));
    sb.append(handlerUtil.getHiddenFieldTag("telephoneRepresentant1", telephoneRepresentant1));
    sb.append(handlerUtil.getHiddenFieldTag("adresseRepresentant1", adresseRepresentant1));
    sb.append(handlerUtil.getHiddenFieldTag("complementRepresentant1", complementRepresentant1));
    
    sb.append(handlerUtil.getHiddenFieldTag("nomRepresentant2", nomRepresentant2));
    sb.append(handlerUtil.getHiddenFieldTag("prenomRepresentant2", prenomRepresentant2));
    sb.append(handlerUtil.getHiddenFieldTag("civiliteRepresentant2", civiliteRepresentant2));
    sb.append(handlerUtil.getHiddenFieldTag("telephoneRepresentant2", telephoneRepresentant2));
    sb.append(handlerUtil.getHiddenFieldTag("adresseRepresentant2", adresseRepresentant2));
    sb.append(handlerUtil.getHiddenFieldTag("complementRepresentant2", complementRepresentant2));
    sb.append(handlerUtil.getHiddenFieldTag("saisieAdresseDif", saisieAdresseDif));    
  }
  

  /**
   * Champs cachés pour le lieu d'accueil
   * @param sb
   */
  private void getHiddenFieldLieuAccueil(StringBuilder sb) {        
    sb.append(handlerUtil.getHiddenFieldTag("accueilDomicile", accueilDomicile));
    sb.append(handlerUtil.getHiddenFieldTag("adresseDomicile", adresseDomicile));
    sb.append(handlerUtil.getHiddenFieldTag("communeDomicile", communeDomicile));
    sb.append(handlerUtil.getHiddenFieldTag("cpDomicile", cpDomicile));
    sb.append(handlerUtil.getHiddenFieldTag("nomMam", nomMam));
    sb.append(handlerUtil.getHiddenFieldTag("adresseMam", adresseMam));
    sb.append(handlerUtil.getHiddenFieldTag("communeMam", communeMam));
    sb.append(handlerUtil.getHiddenFieldTag("cpMam", cpMam));
  }
  
  
  /**
   * Champs cachés pour les modalités
   * @param sb
   */
  private void getHiddenFieldModalites(StringBuilder sb) {
    sb.append(handlerUtil.getHiddenFieldTag("planningRegulier", planningRegulier));
    sb.append(handlerUtil.getHiddenFieldTag("uniquementVacances", uniquementVacances));
    sb.append(handlerUtil.getHiddenFieldTag("estProvisoire", estProvisoire));
    sb.append(handlerUtil.getHiddenFieldTag("dateFinProvisoire", dateFinProvisoire));
    sb.append(handlerUtil.getHiddenFieldTag("motifProvisoire", motifProvisoire));
  }
  

  /**
   * Champs cachés pour le planning
   * @param sb
   */
  private void getHiddenFieldPlanning(StringBuilder sb) {    
    // Planning régulier
//    for(String itJour : JOURS) {     
//      try {
//        Boolean val = (Boolean) ReflectUtil.invokeMethod(this, "isRegul"+itJour);
//        sb.append(handlerUtil.getHiddenFieldTag("regul"+itJour, val));
//      } catch (IllegalAccessException e) {
//        logger.warn("Impossible de créer le formulaire pour planning", e);
//      } catch (InvocationTargetException e) {
//        logger.warn("Impossible de créer le formulaire pour planning", e);
//      } catch (NoSuchMethodException e) {
//        logger.warn("Impossible de créer le formulaire pour planning", e);
//      }                 
//    }
    // Planning régulier vacances scolaires
  }

  /**
   * Persiste toutes les données sans ce soucier de l'étape courantes dans des
   * input type hidden pour permettre le retour aux etapes precedentes
   *
   * @return Le stringBuilder des inputs
   */
  public String getAllFormStepHiddenFields() {
    StringBuilder sb = new StringBuilder();
    // id de la declaration
    sb.append(handlerUtil.getHiddenFieldTag("idModifEnCours", Util.notEmpty(request.getParameter("idModifEnCours")) ? request.getParameter("idModifEnCours") : "" ));
    sb.append(handlerUtil.getHiddenFieldTag("idDeclaration", declaration.getIdDeclaration() != null ? declaration.getIdDeclaration()+"" : "" ));
    sb.append(handlerUtil.getHiddenFieldTag("formStep", formStep));
    // Enfant accueilli    
    getHiddenFieldEnfantAccueilli(sb);
    // Lieu d'accueil
    getHiddenFieldLieuAccueil(sb);
    // Modalités
    getHiddenFieldModalites(sb);
    // Planning
    getHiddenFieldPlanning(sb);
    return sb.toString();
  }

  
  @Override
  public Class<? extends Data> getDataClass() {
    return null;
  }



  /**
   * Verifie si les champs requis de l'enfants sont bien présents
   * 
   * @param listError
   * @return la liste des erreurs
   */

  public  List<JcmsMessage> checkChampsEnfant(List<JcmsMessage> listError){
    // Vérifications seulement quand passage à l'étape suivante
      
   if(opNext) {
      //Sexe enfant
      if (Util.isEmpty(sexeEnfant)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-SEXE-ERR-HTML")));
      } 
      //Date de naissance
      if (Util.isEmpty(dateNaissanceEnfant)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-NAISS-ERR-OBL-HTML")));
      } 
      // Date d'accueil
      if (Util.isEmpty(dateDebutAccueil)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-ACC-ERR-OBL-HTML")));
      } 
    } 
   
    //Nom 
    if (Util.isEmpty(nomEnfant)) {
      listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-NOM-ERR-HTML")));
    } 
    //Prenom
    if (Util.isEmpty(prenomEnfant)) {
      listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-PRENOM-ERR-HTML")));
    } 
   
    //Format de la Date de naissance
    if (Util.notEmpty(dateNaissanceEnfant) && !isValideDateFormat(dateNaissanceEnfant) ) {
      listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-NAISS-ERR-FORMAT-HTML")));
    } else if(Util.notEmpty(dateNaissanceEnfant)) {
      // La date de naissance ne doit pas être postérieure à la date du jour, mais peut être égale à la date du jour
      DateTime naissanceEnfantDate = DATE_TIME_FORMAT.parseDateTime(dateNaissanceEnfant);
      if(naissanceEnfantDate.isAfterNow()) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-NAISS-ERR-POST-NOW-HTML")));
      }
    }
    
    
    
    //Format de la Date d'accueil
    if (Util.notEmpty(dateDebutAccueil) && !isValideDateFormat(dateDebutAccueil) ) {
      listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-ACC-ERR-FORMAT-HTML")));
    } else if(Util.notEmpty(dateDebutAccueil)) {
      //  La date de début d'accueil ne doit pas être postérieure à la date du jour, mais peut être égale à la date du jour
      DateTime debutAccueilDate = DATE_TIME_FORMAT.parseDateTime(dateDebutAccueil);
      if(debutAccueilDate.isAfterNow()) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-ACC-ERR-POST-NOW-HTML")));
      // La date de début d'accueil doit être postérieure à la date de naissance 
      }else if(Util.notEmpty(dateNaissanceEnfant) && isValideDateFormat(dateNaissanceEnfant)) {
        DateTime naissanceEnfantDate = DATE_TIME_FORMAT.parseDateTime(dateNaissanceEnfant);
        if(naissanceEnfantDate.isAfter(debutAccueilDate) || naissanceEnfantDate.isEqual(debutAccueilDate)) {
          listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-ACC-ERR-POST-NAISS-HTML")));
        }
      }
    }
    
    //Nom enfant > 50 caract
    if(Util.notEmpty(nomEnfant)){
      if(nomEnfant.length()> 50){
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-NOM-ERR-CAR-HTML")));
      }
    }
    //Prenom enfant > 50 caract
    if(Util.notEmpty(prenomEnfant)){
      if(prenomEnfant.length()> 60){
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-PRENOM-ERR-CAR-HTML")));
      }
    }
    return listError;
  }


  /**
   * Verifie si les champs requis pour le lieux d'accueil sont bien présents
   * 
   * @param listError
   * @return la liste des erreurs
   */

  public  List<JcmsMessage> checkLieuAccueil(List<JcmsMessage> listError){
    if(opNext) {
      
      // Vérifie qu'un lieu d'accuei est coché
      if (Util.isEmpty(accueilDomicile)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ACC-OBL-HTML")));
      } 
      
      
      
      //Vérifie si une déclaration d'accueil n'est pas déja déclaré pour cet enfant et ce lieu      
      // Un contrôle est effectué : si l’assistante maternelle possède déjà une déclaration d’accueil pour cet enfant 
      // Récupère les accueils en cours et en brouillon avec le meme nom/prénom de l'enfant et date debut accueil que la courrante et d'id accueil différente
            
      
      Member mbr = Channel.getChannel().getCurrentLoggedMember();
      ProfilASSMAT profilAM = ProfilManager.getInstance().getProfilASSMAT(mbr);
      

      try {
        ArrayList<AccueilDTO> listAccueil = new ArrayList<AccueilDTO>();
        
        List<AccueilDTO> declarationBrouillonList = DemarcheUtil.getListAccueils(profilAM.getNum_agrement(), "brouillon");
        logger.debug("checkLieuAccueil - appel declarationApi.getListAccueilsUsingGET brouillon");
        List<AccueilDTO> declarationEnCoursList = DemarcheUtil.getListAccueils(profilAM.getNum_agrement(), "en cours");
        logger.debug("checkLieuAccueil - appel declarationApi.getListAccueilsUsingGET en cours");

        listAccueil.addAll(declarationBrouillonList);
        listAccueil.addAll(declarationEnCoursList);
        
        nomEnfant = declaration.getNomEnfant();
        prenomEnfant = declaration.getPrenomEnfant();
        
        DateTime dateDebutAccueil = declaration.getDateDebutAccueil();
        Integer idAccueil = declaration.getIdAccueil();
        
        DateTime dateNaissanceEnfant = declaration.getDateNaissanceEnfant();
        Boolean isAccueilDomicile = declaration.getAccueilDomicile();
        
        for(AccueilDTO itAccueil : listAccueil) {
          if( nomEnfant.equalsIgnoreCase(itAccueil.getNomEnfant()) &&
              prenomEnfant.equalsIgnoreCase(itAccueil.getPrenomEnfant()) &&
              dateDebutAccueil.equals(itAccueil.getDateDebutAccueil()) &&
              !idAccueil.equals(itAccueil.getIdAccueil()) 
             ) {
            
            DeclarationAccueilDTO itDeclaration = DemarcheUtil.getDeclarationAccueilById(itAccueil.getIdDeclaration());
            logger.debug("checkLieuAccueil() - declaration - getDeclarationAccueilByIdUsingGET1 = " + itDeclaration);
            if( dateNaissanceEnfant.equals(itDeclaration.getDateNaissanceEnfant()) &&
                (isAccueilDomicile != null && isAccueilDomicile.equals(itDeclaration.getAccueilDomicile())) 
                ) {         
              listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-EXIST-HTML")));
              break;
            }
          }
        }

       
      } catch (ApiException e) {
        logger.warn("Impossible de lister les déclaration d'accueil de l'assmat " + profilAM, e);
      }

      
    }
    return listError;
  }

  /**
   * Verifie si les champs requis du représentant 1 sont bien présents
   * 
   * @param listError
   * @return la liste des erreurs
   */

  public  List<JcmsMessage> checkChampsRL1(List<JcmsMessage> listError){
    if(opNext) {
      // Civilité 
      if (Util.isEmpty(civiliteRepresentant1)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP1-CIV-ERR-HTML")));
      } 
      // Nom
      if (Util.isEmpty(nomRepresentant1)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP1-NOM-ERR-HTML")));
      } 
      // Prenom
      if (Util.isEmpty(prenomRepresentant1)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP1-PRENOM-ERR-HTML")));
      } 
      // Téléphone
      if (Util.isEmpty(telephoneRepresentant1)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP1-TEL-ERR-OBL-HTML")));
      } 
      // Adresse
      if ((saisieAdresseR1 == null || !saisieAdresseR1) && Util.isEmpty(adresseRepresentant1)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP1-ADR-ERR-HTML")));
      }           
    }
    if(Util.notEmpty(nomRepresentant1)){
      if(nomRepresentant1.length()> 30){
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP1-NOM-ERR-CAR-HTML")));
      }
    }
    if(Util.notEmpty(prenomRepresentant1)){
      if(prenomRepresentant1.length()> 60){
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP1-PRENOM-ERR-CAR-HTML")));
      }
    }
    if(Util.notEmpty(adresseRepresentant1)){
      if(adresseRepresentant1.length()> 150){
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP1-ADR-CAR-HTML")));
      }            
    }
    if(Util.notEmpty(codePostalRepresentant1) && codePostalRepresentant1.length() > 10){
      listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP1-ADR-CP-ERR-HTML")));
    }
    if(Util.notEmpty(villeRepresentant1) && villeRepresentant1.length() > 40){
      listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP1-ADR-VILLE-ERR-HTML")));
    }
    // Adresse saisie non trouvée
    if(Util.notEmpty(adresseAutoCompleteRepresentant1) && Util.isEmpty(adresseRepresentant1)) {
      listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP1-ADR-ERR-BAN-HTML")));
    }   
    if(Util.notEmpty(complementRepresentant1)){
      if(complementRepresentant1.length()> 40){
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP1-CPLM-CAR-HTML")));
      }
    }
    if(Util.notEmpty(telephoneRepresentant1)){
      if(!AssmatUtil.checkPhone(telephoneRepresentant1)){
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP1-TEL-ERR-FOR-HTML")));
      }
    }
    // Vérification de l'adresse saisie manuellement (si saisie manuelle cochée)
    if(saisieAdresseR1 != null && saisieAdresseR1){
      if(Util.isEmpty(adresseRepresentant1)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP1-ADR-AUTR-LIB-OBL-HTML")));
      }
      if(Util.isEmpty(codePostalRepresentant1)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP1-ADR-AUTR-CP-OBL-HTML")));
      }
      if(Util.isEmpty(villeRepresentant1)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP1-ADR-AUTR-VILL-OBL-HTML")));
      }
    } 
    return listError;
  }

  /**
   * Verifie si les champs requis du représentant 2 sont bien présents
   * 
   * @param listError
   * @return la liste des erreurs
   */

  public  List<JcmsMessage> checkChampsRL2(List<JcmsMessage> listError){
    if(Util.notEmpty(nomRepresentant2)){
      if(nomRepresentant2.length()> 30){
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP2-NOM-ERR-CAR-HTML")));
      }
    }
    if(Util.notEmpty(prenomRepresentant2)){
      if(prenomRepresentant2.length()> 60){
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP2-PRENOM-ERR-CAR-HTML")));
      }
    }
    //adresse > 150 caract
    if(Util.notEmpty(adresseRepresentant2)){
      if(adresseRepresentant2.length()> 150){
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP2-ADR-CAR-HTML")));
      }
    }
    if(Util.notEmpty(codePostalRepresentant2) && codePostalRepresentant2.length() > 10){
      listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP2-ADR-CP-ERR-HTML")));
    }
    if(Util.notEmpty(villeRepresentant2) && villeRepresentant2.length() > 40){
      listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP2-ADR-VILLE-ERR-HTML")));
    }
    // Adresse saisie non trouvée
    if(Util.notEmpty(adresseAutoCompleteRepresentant2) && Util.isEmpty(adresseRepresentant2)) {
      listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP2-ADR-ERR-BAN-HTML")));
    }
    if(Util.notEmpty(complementRepresentant2)){
      if(complementRepresentant2.length()> 40){
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP2-CPLM-CAR-HTML")));
      }
    }
    if(Util.notEmpty(telephoneRepresentant2)){
      if(!AssmatUtil.checkPhone(telephoneRepresentant2)){
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP2-TEL-ERR-FOR-HTML")));
      }
    }
    // Vérification de l'adresse saisie manuellement (si saisie manuelle cochée)
    if(saisieAdresseR2 != null && saisieAdresseR2){
      if(Util.isEmpty(adresseRepresentant2)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP2-ADR-AUTR-LIB-OBL-HTML")));
      }
      if(Util.isEmpty(codePostalRepresentant2)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP2-ADR-AUTR-CP-OBL-HTML")));
      }
      if(Util.isEmpty(villeRepresentant2)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP2-ADR-AUTR-VILL-OBL-HTML")));
      }
    }  
    if(opNext) {
      if((Util.notEmpty(nomRepresentant2) || Util.notEmpty(prenomRepresentant2) ||Util.notEmpty(adresseRepresentant2) ||Util.notEmpty(complementRepresentant2) ||Util.notEmpty(telephoneRepresentant2)) && (Util.isEmpty(civiliteRepresentant2) ||Util.isEmpty(nomRepresentant2) || Util.isEmpty(prenomRepresentant2) ) ){
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP2-OBL-HTML")));
      }
  
      if(saisieAdresseDif){
        if(Util.isEmpty(telephoneRepresentant2) && Util.isEmpty(adresseRepresentant2) ){
          listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-ENF-REP2-ADRTEL-HTML")));
        }
    
      }
    }
    return listError;
  }



  /**
   * Verifie si les champs requis du représentant 1 sont bien présents
   * 
   * @param listError
   * @return la liste des erreurs
   */

  public  List<JcmsMessage> checkModalites(List<JcmsMessage> listError){
    
    // Format de la Date de fin d'acceuil provisoire
    if (Util.notEmpty(dateFinProvisoire) && !isValideDateFormat(dateFinProvisoire) ) {
      listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-MOD-DFI-ERR-FOR-HTML")));
    } 
    
    // Motif d'accueil provisoire
    if(Util.notEmpty(motifProvisoire)){
      if(motifProvisoire.length() > 100){
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-MOD-MOT-ERR-CAR-HTML")));
      }
    }   
    
    if(opNext) {
      // Planning hebdomadaire régulier 
      if (Util.isEmpty(planningRegulier)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-MOD-Q1-ERR-HTML")));
      } 
      // Accueil occasionnel
      if(Util.notEmpty(planningRegulier) && !Boolean.valueOf(planningRegulier) && Util.isEmpty(accueilOccasionnel)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-MOD-QAOCC-ERR-HTML")));
      }
      // Planning semaines types QIRR
      if(Util.notEmpty(accueilOccasionnel) && !Boolean.valueOf(accueilOccasionnel) && Util.isEmpty(planningSemainesTypes)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-MOD-QIRR-ERR-HTML")));
      }
      // Uniquement pendant les vacances scolaires 
      if (Util.isEmpty(uniquementVacances)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-MOD-Q2-ERR-HTML")));
      } 
      // Accueil provisoire 
      if (Util.isEmpty(estProvisoire)) {
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-MOD-Q3-ERR-HTML")));
      }
      
      if (Util.notEmpty(estProvisoire)) {
        
        if ("true".equals(estProvisoire) && Util.isEmpty(motifProvisoire)) {                
            listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("ASS-DEC-MOD-MOT-ERR-OBL-HTML")));                           
        } 
        
      }         
      
    }
    return listError;
  }




  /**
   * Permet de verifier que le format d'une heure est respecté
   * 
   * @param heure
   * @return true ou false
   */
  public static boolean checkFormatHeure(String heure){
    boolean isOk = true;
    Pattern pattern=  Pattern.compile(REGEX_HEURE);
    Matcher matcher = pattern.matcher(heure);
    if(!matcher.matches()){
      isOk= false;
    }    
    return isOk; 
  }



  public String getPlanningRegulier() {
    return planningRegulier;
  }

  public void setPlanningRegulier(String planningRegulier) {
    this.planningRegulier = planningRegulier;
  }
  
  public String getAccueilOccasionnel() {
    return accueilOccasionnel;
  }

  public void setAccueilOccasionnel(String accueilOccasionnel) {
    this.accueilOccasionnel = accueilOccasionnel;
  }

  public String getPlanningSemainesTypes() {
    return planningSemainesTypes;
  }

  public void setPlanningSemainesTypes(String planningSemainesTypes) {
    this.planningSemainesTypes = planningSemainesTypes;
  }

  public String getUniquementVacances() {
    return uniquementVacances;
  }

  public void setUniquementVacances(String uniquementVacances) {
    this.uniquementVacances = uniquementVacances;
  }

  public String getEstProvisoire() {
    return estProvisoire;
  }

  public void setEstProvisoire(String estProvisoire) {
    this.estProvisoire = estProvisoire;
  }

  public String getDateFinProvisoire() {
    return dateFinProvisoire;
  }

  public void setDateFinProvisoire(String dateFinProvisoire) {
    this.dateFinProvisoire = dateFinProvisoire;
  }

  public String getMotifProvisoire() {
    return motifProvisoire;
  }

  public void setMotifProvisoire(String motifProvisoire) {
    this.motifProvisoire = motifProvisoire;
  }

  public String getAccueilDomicile() {
    return accueilDomicile;
  }

  public void setAccueilDomicile(String accueilDomicile) {
    this.accueilDomicile = accueilDomicile;
  }

  public String getAdresseDomicile() {
    return adresseDomicile;
  }

  public void setAdresseDomicile(String adresseDomicile) {
    this.adresseDomicile = adresseDomicile;
  }

  public String getCommuneDomicile() {
    return communeDomicile;
  }

  public void setCommuneDomicile(String communeDomicile) {
    this.communeDomicile = communeDomicile;
  }

  public String getCpDomicile() {
    return cpDomicile;
  }

  public void setCpDomicile(String cpDomicile) {
    this.cpDomicile = cpDomicile;
  }

  public String getNomMam() {
    return nomMam;
  }

  public void setNomMam(String nomMam) {
    this.nomMam = nomMam;
  }

  public String getAdresseMam() {
    return adresseMam;
  }

  public void setAdresseMam(String adresseMam) {
    this.adresseMam = adresseMam;
  }

  public String getCommuneMam() {
    return communeMam;
  }

  public void setCommuneMam(String communeMam) {
    this.communeMam = communeMam;
  }

  public String getCpMam() {
    return cpMam;
  }

  public void setCpMam(String cpMam) {
    this.cpMam = cpMam;
  }

  public String getNomEnfant() {
    return nomEnfant;
  }

  public void setNomEnfant(String nomEnfant) {
    this.nomEnfant = nomEnfant;
  }

  public String getPrenomEnfant() {
    return prenomEnfant;
  }

  public void setPrenomEnfant(String prenomEnfant) {
    this.prenomEnfant = prenomEnfant;
  }

  public String getSexeEnfant() {
    return sexeEnfant;
  }

  public void setSexeEnfant(String sexeEnfant) {
    this.sexeEnfant = sexeEnfant;
  }

  public String getDateNaissanceEnfant() {
    return dateNaissanceEnfant;
  }

  public void setDateNaissanceEnfant(String dateNaissanceEnfant) {
    this.dateNaissanceEnfant = dateNaissanceEnfant;
  }

  public String getNomRepresentant1() {
    return nomRepresentant1;
  }

  public void setNomRepresentant1(String nomRepresentant1) {
    this.nomRepresentant1 = nomRepresentant1;
  }

  public String getPrenomRepresentant1() {
    return prenomRepresentant1;
  }

  public void setPrenomRepresentant1(String prenomRepresentant1) {
    this.prenomRepresentant1 = prenomRepresentant1;
  }

  public String getCiviliteRepresentant1() {
    return civiliteRepresentant1;
  }

  public void setCiviliteRepresentant1(String civiliteRepresentant1) {
    this.civiliteRepresentant1 = civiliteRepresentant1;
  }

  public String getTelephoneRepresentant1() {
    return telephoneRepresentant1;
  }

  public void setTelephoneRepresentant1(String telephoneRepresentant1) {
    if(Util.notEmpty(telephoneRepresentant1)) {
      this.telephoneRepresentant1 = telephoneRepresentant1.replace(" ", "");
    }else {
      this.telephoneRepresentant1 = telephoneRepresentant1;
    }
  }

  public String getAdresseRepresentant1() {
    return adresseRepresentant1;
  }

  public void setAdresseRepresentant1(String adresseRepresentant1) {
    this.adresseRepresentant1 = adresseRepresentant1;
  }
  
  public String getCodePostalRepresentant1() {
    return codePostalRepresentant1;
  }

  public void setCodePostalRepresentant1(String codePostalRepresentant1) {
    this.codePostalRepresentant1 = codePostalRepresentant1;
  }

  public String getVilleRepresentant1() {
    return villeRepresentant1;
  }

  public void setVilleRepresentant1(String villeRepresentant1) {
    this.villeRepresentant1 = villeRepresentant1;
  }

  public String getComplementRepresentant1() {
    return complementRepresentant1;
  }

  public void setComplementRepresentant1(String complementRepresentant1) {
    this.complementRepresentant1 = complementRepresentant1;
  }
  
  public Boolean getSaisieAdresseR1() {
    return saisieAdresseR1;
  }

  public void setSaisieAdresseR1(Boolean saisieAdresseR1) {
    this.saisieAdresseR1 = saisieAdresseR1;
  }

  public Boolean getSaisieAdresseR2() {
    return saisieAdresseR2;
  }

  public void setSaisieAdresseR2(Boolean saisieAdresseR2) {
    this.saisieAdresseR2 = saisieAdresseR2;
  }

  public String getNomRepresentant2() {
    return nomRepresentant2;
  }

  public void setNomRepresentant2(String nomRepresentant2) {
    this.nomRepresentant2 = nomRepresentant2;
  }

  public String getPrenomRepresentant2() {
    return prenomRepresentant2;
  }

  public void setPrenomRepresentant2(String prenomRepresentant2) {
    this.prenomRepresentant2 = prenomRepresentant2;
  }

  public String getCiviliteRepresentant2() {
    return civiliteRepresentant2;
  }

  public void setCiviliteRepresentant2(String civiliteRepresentant2) {
    this.civiliteRepresentant2 = civiliteRepresentant2;
  }

  public String getTelephoneRepresentant2() {
    return telephoneRepresentant2;
  }

  public void setTelephoneRepresentant2(String telephoneRepresentant2) {
    if(Util.notEmpty(telephoneRepresentant2)) {
      this.telephoneRepresentant2 = telephoneRepresentant2.replace(" ", "");
    }else {
      this.telephoneRepresentant2 = telephoneRepresentant2;
    }   
  }

  public String getAdresseRepresentant2() {
    return adresseRepresentant2;
  }

  public void setAdresseRepresentant2(String adresseRepresentant2) {
    this.adresseRepresentant2 = adresseRepresentant2;
  }
  
  public String getCodePostalRepresentant2() {
    return codePostalRepresentant2;
  }

  public void setCodePostalRepresentant2(String codePostalRepresentant2) {
    this.codePostalRepresentant2 = codePostalRepresentant2;
  }

  public String getVilleRepresentant2() {
    return villeRepresentant2;
  }

  public void setVilleRepresentant2(String villeRepresentant2) {
    this.villeRepresentant2 = villeRepresentant2;
  }

  public String getComplementRepresentant2() {
    return complementRepresentant2;
  }

  public void setComplementRepresentant2(String complementRepresentant2) {
    this.complementRepresentant2 = complementRepresentant2;
  }

  public boolean isSaisieAdresseDif() {
    return saisieAdresseDif;
  }

  public void setSaisieAdresseDif(boolean saisieAdresseDif) {
    this.saisieAdresseDif = saisieAdresseDif;
  }
  
  public String getAdresseAutoCompleteRepresentant1() {
    return adresseAutoCompleteRepresentant1;
  }

  public void setAdresseAutoCompleteRepresentant1(String adresseAutoCompleteRepresentant1) {
    this.adresseAutoCompleteRepresentant1 = adresseAutoCompleteRepresentant1;
  }

  public String getAdresseAutoCompleteRepresentant2() {
    return adresseAutoCompleteRepresentant2;
  }

  public void setAdresseAutoCompleteRepresentant2(String adresseAutoCompleteRepresentant2) {
    this.adresseAutoCompleteRepresentant2 = adresseAutoCompleteRepresentant2;
  }

  public String getDateDebutAccueil() {
    return dateDebutAccueil;
  }

  public void setDateDebutAccueil(String dateDebutAccueil) {
    this.dateDebutAccueil = dateDebutAccueil;
  }

  public String getDebcr1lundi() {
    return debcr1lundi;
  }

  public void setDebcr1lundi(String debcr1lundi) {
    this.debcr1lundi = debcr1lundi;
  }

  public String getFincr1lundi() {
    return fincr1lundi;
  }

  public void setFincr1lundi(String fincr1lundi) {
    this.fincr1lundi = fincr1lundi;
  }

  public String getDebcr2lundi() {
    return debcr2lundi;
  }

  public void setDebcr2lundi(String debcr2lundi) {
    this.debcr2lundi = debcr2lundi;
  }

  public String getFincr2lundi() {
    return fincr2lundi;
  }

  public void setFincr2lundi(String fincr2lundi) {
    this.fincr2lundi = fincr2lundi;
  }

  public String getDebcr3lundi() {
    return debcr3lundi;
  }

  public void setDebcr3lundi(String debcr3lundi) {
    this.debcr3lundi = debcr3lundi;
  }

  public String getFincr3lundi() {
    return fincr3lundi;
  }

  public void setFincr3lundi(String fincr3lundi) {
    this.fincr3lundi = fincr3lundi;
  }

  public String getDebcr1mardi() {
    return debcr1mardi;
  }

  public void setDebcr1mardi(String debcr1mardi) {
    this.debcr1mardi = debcr1mardi;
  }

  public String getFincr1mardi() {
    return fincr1mardi;
  }

  public void setFincr1mardi(String fincr1mardi) {
    this.fincr1mardi = fincr1mardi;
  }

  public String getDebcr2mardi() {
    return debcr2mardi;
  }

  public void setDebcr2mardi(String debcr2mardi) {
    this.debcr2mardi = debcr2mardi;
  }

  public String getFincr2mardi() {
    return fincr2mardi;
  }

  public void setFincr2mardi(String fincr2mardi) {
    this.fincr2mardi = fincr2mardi;
  }

  public String getDebcr3mardi() {
    return debcr3mardi;
  }

  public void setDebcr3mardi(String debcr3mardi) {
    this.debcr3mardi = debcr3mardi;
  }

  public String getFincr3mardi() {
    return fincr3mardi;
  }

  public void setFincr3mardi(String fincr3mardi) {
    this.fincr3mardi = fincr3mardi;
  }

  public String getDebcr1mercredi() {
    return debcr1mercredi;
  }

  public void setDebcr1mercredi(String debcr1mercredi) {
    this.debcr1mercredi = debcr1mercredi;
  }

  public String getFincr1mercredi() {
    return fincr1mercredi;
  }

  public void setFincr1mercredi(String fincr1mercredi) {
    this.fincr1mercredi = fincr1mercredi;
  }

  public String getDebcr2mercredi() {
    return debcr2mercredi;
  }

  public void setDebcr2mercredi(String debcr2mercredi) {
    this.debcr2mercredi = debcr2mercredi;
  }

  public String getFincr2mercredi() {
    return fincr2mercredi;
  }

  public void setFincr2mercredi(String fincr2mercredi) {
    this.fincr2mercredi = fincr2mercredi;
  }

  public String getDebcr3mercredi() {
    return debcr3mercredi;
  }

  public void setDebcr3mercredi(String debcr3mercredi) {
    this.debcr3mercredi = debcr3mercredi;
  }

  public String getFincr3mercredi() {
    return fincr3mercredi;
  }

  public void setFincr3mercredi(String fincr3mercredi) {
    this.fincr3mercredi = fincr3mercredi;
  }

  public String getDebcr1jeudi() {
    return debcr1jeudi;
  }

  public void setDebcr1jeudi(String debcr1jeudi) {
    this.debcr1jeudi = debcr1jeudi;
  }

  public String getFincr1jeudi() {
    return fincr1jeudi;
  }

  public void setFincr1jeudi(String fincr1jeudi) {
    this.fincr1jeudi = fincr1jeudi;
  }

  public String getDebcr2jeudi() {
    return debcr2jeudi;
  }

  public void setDebcr2jeudi(String debcr2jeudi) {
    this.debcr2jeudi = debcr2jeudi;
  }

  public String getFincr2jeudi() {
    return fincr2jeudi;
  }

  public void setFincr2jeudi(String fincr2jeudi) {
    this.fincr2jeudi = fincr2jeudi;
  }

  public String getDebcr3jeudi() {
    return debcr3jeudi;
  }

  public void setDebcr3jeudi(String debcr3jeudi) {
    this.debcr3jeudi = debcr3jeudi;
  }

  public String getFincr3jeudi() {
    return fincr3jeudi;
  }

  public void setFincr3jeudi(String fincr3jeudi) {
    this.fincr3jeudi = fincr3jeudi;
  }

  public String getDebcr1vendredi() {
    return debcr1vendredi;
  }

  public void setDebcr1vendredi(String debcr1vendredi) {
    this.debcr1vendredi = debcr1vendredi;
  }

  public String getFincr1vendredi() {
    return fincr1vendredi;
  }

  public void setFincr1vendredi(String fincr1vendredi) {
    this.fincr1vendredi = fincr1vendredi;
  }

  public String getDebcr2vendredi() {
    return debcr2vendredi;
  }

  public void setDebcr2vendredi(String debcr2vendredi) {
    this.debcr2vendredi = debcr2vendredi;
  }

  public String getFincr2vendredi() {
    return fincr2vendredi;
  }

  public void setFincr2vendredi(String fincr2vendredi) {
    this.fincr2vendredi = fincr2vendredi;
  }

  public String getDebcr3vendredi() {
    return debcr3vendredi;
  }

  public void setDebcr3vendredi(String debcr3vendredi) {
    this.debcr3vendredi = debcr3vendredi;
  }

  public String getFincr3vendredi() {
    return fincr3vendredi;
  }

  public void setFincr3vendredi(String fincr3vendredi) {
    this.fincr3vendredi = fincr3vendredi;
  }

  public String getDebcr1samedi() {
    return debcr1samedi;
  }

  public void setDebcr1samedi(String debcr1samedi) {
    this.debcr1samedi = debcr1samedi;
  }

  public String getFincr1samedi() {
    return fincr1samedi;
  }

  public void setFincr1samedi(String fincr1samedi) {
    this.fincr1samedi = fincr1samedi;
  }

  public String getDebcr2samedi() {
    return debcr2samedi;
  }

  public void setDebcr2samedi(String debcr2samedi) {
    this.debcr2samedi = debcr2samedi;
  }

  public String getFincr2samedi() {
    return fincr2samedi;
  }

  public void setFincr2samedi(String fincr2samedi) {
    this.fincr2samedi = fincr2samedi;
  }

  public String getDebcr3samedi() {
    return debcr3samedi;
  }

  public void setDebcr3samedi(String debcr3samedi) {
    this.debcr3samedi = debcr3samedi;
  }

  public String getFincr3samedi() {
    return fincr3samedi;
  }

  public void setFincr3samedi(String fincr3samedi) {
    this.fincr3samedi = fincr3samedi;
  }

  public String getDebcr1dimanche() {
    return debcr1dimanche;
  }

  public void setDebcr1dimanche(String debcr1dimanche) {
    this.debcr1dimanche = debcr1dimanche;
  }

  public String getFincr1dimanche() {
    return fincr1dimanche;
  }

  public void setFincr1dimanche(String fincr1dimanche) {
    this.fincr1dimanche = fincr1dimanche;
  }

  public String getDebcr2dimanche() {
    return debcr2dimanche;
  }

  public void setDebcr2dimanche(String debcr2dimanche) {
    this.debcr2dimanche = debcr2dimanche;
  }

  public String getFincr2dimanche() {
    return fincr2dimanche;
  }

  public void setFincr2dimanche(String fincr2dimanche) {
    this.fincr2dimanche = fincr2dimanche;
  }

  public String getDebcr3dimanche() {
    return debcr3dimanche;
  }

  public void setDebcr3dimanche(String debcr3dimanche) {
    this.debcr3dimanche = debcr3dimanche;
  }

  public String getFincr3dimanche() {
    return fincr3dimanche;
  }

  public void setFincr3dimanche(String fincr3dimanche) {
    this.fincr3dimanche = fincr3dimanche;
  }

  public boolean isRegulLundi() {
    return regulLundi;
  }

  public void setRegulLundi(boolean regulLundi) {
    this.regulLundi = regulLundi;
  }

  public boolean isRegulMardi() {
    return regulMardi;
  }

  public void setRegulMardi(boolean regulMardi) {
    this.regulMardi = regulMardi;
  }

  public boolean isRegulMercredi() {
    return regulMercredi;
  }

  public void setRegulMercredi(boolean regulMercredi) {
    this.regulMercredi = regulMercredi;
  }

  public boolean isRegulJeudi() {
    return regulJeudi;
  }

  public void setRegulJeudi(boolean regulJeudi) {
    this.regulJeudi = regulJeudi;
  }

  public boolean isRegulVendredi() {
    return regulVendredi;
  }

  public void setRegulVendredi(boolean regulVendredi) {
    this.regulVendredi = regulVendredi;
  }

  public boolean isRegulSamedi() {
    return regulSamedi;
  }

  public void setRegulSamedi(boolean regulSamedi) {
    this.regulSamedi = regulSamedi;
  }

  public boolean isRegulDimanche() {
    return regulDimanche;
  }

  public void setRegulDimanche(boolean regulDimanche) {
    this.regulDimanche = regulDimanche;
  }

  public boolean isRegulLundiVac() {
    return regulLundiVac;
  }

  public void setRegulLundiVac(boolean regulLundiVac) {
    this.regulLundiVac = regulLundiVac;
  }

  public boolean isRegulMardiVac() {
    return regulMardiVac;
  }

  public void setRegulMardiVac(boolean regulMardiVac) {
    this.regulMardiVac = regulMardiVac;
  }

  public boolean isRegulMercrediVac() {
    return regulMercrediVac;
  }

  public void setRegulMercrediVac(boolean regulMercrediVac) {
    this.regulMercrediVac = regulMercrediVac;
  }

  public boolean isRegulJeudiVac() {
    return regulJeudiVac;
  }

  public void setRegulJeudiVac(boolean regulJeudiVac) {
    this.regulJeudiVac = regulJeudiVac;
  }

  public boolean isRegulVendrediVac() {
    return regulVendrediVac;
  }

  public void setRegulVendrediVac(boolean regulVendrediVac) {
    this.regulVendrediVac = regulVendrediVac;
  }

  public boolean isRegulSamediVac() {
    return regulSamediVac;
  }

  public void setRegulSamediVac(boolean regulSamediVac) {
    this.regulSamediVac = regulSamediVac;
  }

  public boolean isRegulDimancheVac() {
    return regulDimancheVac;
  }

  public void setRegulDimancheVac(boolean regulDimancheVac) {
    this.regulDimancheVac = regulDimancheVac;
  }

  public String getDebcr1mercrediVac() {
    return debcr1mercrediVac;
  }

  public void setDebcr1mercrediVac(String debcr1mercrediVac) {
    this.debcr1mercrediVac = debcr1mercrediVac;
  }

  public String getFincr1mercrediVac() {
    return fincr1mercrediVac;
  }

  public void setFincr1mercrediVac(String fincr1mercrediVac) {
    this.fincr1mercrediVac = fincr1mercrediVac;
  }

  public String getDebcr2mercrediVac() {
    return debcr2mercrediVac;
  }

  public void setDebcr2mercrediVac(String debcr2mercrediVac) {
    this.debcr2mercrediVac = debcr2mercrediVac;
  }

  public String getFincr2mercrediVac() {
    return fincr2mercrediVac;
  }

  public void setFincr2mercrediVac(String fincr2mercrediVac) {
    this.fincr2mercrediVac = fincr2mercrediVac;
  }

  public String getDebcr3mercrediVac() {
    return debcr3mercrediVac;
  }

  public void setDebcr3mercrediVac(String debcr3mercrediVac) {
    this.debcr3mercrediVac = debcr3mercrediVac;
  }

  public String getFincr3mercrediVac() {
    return fincr3mercrediVac;
  }

  public void setFincr3mercrediVac(String fincr3mercrediVac) {
    this.fincr3mercrediVac = fincr3mercrediVac;
  }

  public String getDebcr1jeudiVac() {
    return debcr1jeudiVac;
  }

  public void setDebcr1jeudiVac(String debcr1jeudiVac) {
    this.debcr1jeudiVac = debcr1jeudiVac;
  }

  public String getFincr1jeudiVac() {
    return fincr1jeudiVac;
  }

  public void setFincr1jeudiVac(String fincr1jeudiVac) {
    this.fincr1jeudiVac = fincr1jeudiVac;
  }

  public String getDebcr2jeudiVac() {
    return debcr2jeudiVac;
  }

  public void setDebcr2jeudiVac(String debcr2jeudiVac) {
    this.debcr2jeudiVac = debcr2jeudiVac;
  }

  public String getFincr2jeudiVac() {
    return fincr2jeudiVac;
  }

  public void setFincr2jeudiVac(String fincr2jeudiVac) {
    this.fincr2jeudiVac = fincr2jeudiVac;
  }

  public String getDebcr3jeudiVac() {
    return debcr3jeudiVac;
  }

  public void setDebcr3jeudiVac(String debcr3jeudiVac) {
    this.debcr3jeudiVac = debcr3jeudiVac;
  }

  public String getFincr3jeudiVac() {
    return fincr3jeudiVac;
  }

  public void setFincr3jeudiVac(String fincr3jeudiVac) {
    this.fincr3jeudiVac = fincr3jeudiVac;
  }

  public String getDebcr1vendrediVac() {
    return debcr1vendrediVac;
  }

  public void setDebcr1vendrediVac(String debcr1vendrediVac) {
    this.debcr1vendrediVac = debcr1vendrediVac;
  }

  public String getFincr1vendrediVac() {
    return fincr1vendrediVac;
  }

  public void setFincr1vendrediVac(String fincr1vendrediVac) {
    this.fincr1vendrediVac = fincr1vendrediVac;
  }

  public String getDebcr2vendrediVac() {
    return debcr2vendrediVac;
  }

  public void setDebcr2vendrediVac(String debcr2vendrediVac) {
    this.debcr2vendrediVac = debcr2vendrediVac;
  }

  public String getFincr2vendrediVac() {
    return fincr2vendrediVac;
  }

  public void setFincr2vendrediVac(String fincr2vendrediVac) {
    this.fincr2vendrediVac = fincr2vendrediVac;
  }

  public String getDebcr3vendrediVac() {
    return debcr3vendrediVac;
  }

  public void setDebcr3vendrediVac(String debcr3vendrediVac) {
    this.debcr3vendrediVac = debcr3vendrediVac;
  }

  public String getFincr3vendrediVac() {
    return fincr3vendrediVac;
  }

  public void setFincr3vendrediVac(String fincr3vendrediVac) {
    this.fincr3vendrediVac = fincr3vendrediVac;
  }

  public String getDebcr1samediVac() {
    return debcr1samediVac;
  }

  public void setDebcr1samediVac(String debcr1samediVac) {
    this.debcr1samediVac = debcr1samediVac;
  }

  public String getFincr1samediVac() {
    return fincr1samediVac;
  }

  public void setFincr1samediVac(String fincr1samediVac) {
    this.fincr1samediVac = fincr1samediVac;
  }

  public String getDebcr2samediVac() {
    return debcr2samediVac;
  }

  public void setDebcr2samediVac(String debcr2samediVac) {
    this.debcr2samediVac = debcr2samediVac;
  }

  public String getFincr2samediVac() {
    return fincr2samediVac;
  }

  public void setFincr2samediVac(String fincr2samediVac) {
    this.fincr2samediVac = fincr2samediVac;
  }

  public String getDebcr3samediVac() {
    return debcr3samediVac;
  }

  public void setDebcr3samediVac(String debcr3samediVac) {
    this.debcr3samediVac = debcr3samediVac;
  }

  public String getFincr3samediVac() {
    return fincr3samediVac;
  }

  public void setFincr3samediVac(String fincr3samediVac) {
    this.fincr3samediVac = fincr3samediVac;
  }

  public String getDebcr1dimancheVac() {
    return debcr1dimancheVac;
  }

  public void setDebcr1dimancheVac(String debcr1dimancheVac) {
    this.debcr1dimancheVac = debcr1dimancheVac;
  }

  public String getFincr1dimancheVac() {
    return fincr1dimancheVac;
  }

  public void setFincr1dimancheVac(String fincr1dimancheVac) {
    this.fincr1dimancheVac = fincr1dimancheVac;
  }

  public String getDebcr2dimancheVac() {
    return debcr2dimancheVac;
  }

  public void setDebcr2dimancheVac(String debcr2dimancheVac) {
    this.debcr2dimancheVac = debcr2dimancheVac;
  }

  public String getFincr2dimancheVac() {
    return fincr2dimancheVac;
  }

  public void setFincr2dimancheVac(String fincr2dimancheVac) {
    this.fincr2dimancheVac = fincr2dimancheVac;
  }

  public String getDebcr3dimancheVac() {
    return debcr3dimancheVac;
  }

  public void setDebcr3dimancheVac(String debcr3dimancheVac) {
    this.debcr3dimancheVac = debcr3dimancheVac;
  }

  public String getFincr3dimancheVac() {
    return fincr3dimancheVac;
  }

  public void setFincr3dimancheVac(String fincr3dimancheVac) {
    this.fincr3dimancheVac = fincr3dimancheVac;
  }

  public String getDebcr1lundiVac() {
    return debcr1lundiVac;
  }

  public void setDebcr1lundiVac(String debcr1lundiVac) {
    this.debcr1lundiVac = debcr1lundiVac;
  }

  public String getFincr1lundiVac() {
    return fincr1lundiVac;
  }

  public void setFincr1lundiVac(String fincr1lundiVac) {
    this.fincr1lundiVac = fincr1lundiVac;
  }

  public String getDebcr2lundiVac() {
    return debcr2lundiVac;
  }

  public void setDebcr2lundiVac(String debcr2lundiVac) {
    this.debcr2lundiVac = debcr2lundiVac;
  }

  public String getFincr2lundiVac() {
    return fincr2lundiVac;
  }

  public void setFincr2lundiVac(String fincr2lundiVac) {
    this.fincr2lundiVac = fincr2lundiVac;
  }

  public String getDebcr3lundiVac() {
    return debcr3lundiVac;
  }

  public void setDebcr3lundiVac(String debcr3lundiVac) {
    this.debcr3lundiVac = debcr3lundiVac;
  }

  public String getFincr3lundiVac() {
    return fincr3lundiVac;
  }

  public void setFincr3lundiVac(String fincr3lundiVac) {
    this.fincr3lundiVac = fincr3lundiVac;
  }

  public String getDebcr1mardiVac() {
    return debcr1mardiVac;
  }

  public void setDebcr1mardiVac(String debcr1mardiVac) {
    this.debcr1mardiVac = debcr1mardiVac;
  }

  public String getFincr1mardiVac() {
    return fincr1mardiVac;
  }

  public void setFincr1mardiVac(String fincr1mardiVac) {
    this.fincr1mardiVac = fincr1mardiVac;
  }

  public String getDebcr2mardiVac() {
    return debcr2mardiVac;
  }

  public void setDebcr2mardiVac(String debcr2mardiVac) {
    this.debcr2mardiVac = debcr2mardiVac;
  }

  public String getFincr2mardiVac() {
    return fincr2mardiVac;
  }

  public void setFincr2mardiVac(String fincr2mardiVac) {
    this.fincr2mardiVac = fincr2mardiVac;
  }

  public String getDebcr3mardiVac() {
    return debcr3mardiVac;
  }

  public void setDebcr3mardiVac(String debcr3mardiVac) {
    this.debcr3mardiVac = debcr3mardiVac;
  }

  public String getFincr3mardiVac() {
    return fincr3mardiVac;
  }

  public void setFincr3mardiVac(String fincr3mardiVac) {
    this.fincr3mardiVac = fincr3mardiVac;
  }

  public String getAccueilVacances() {
    return accueilVacances;
  }

  public void setAccueilVacances(String accueilVacances) {
    this.accueilVacances = accueilVacances;
  }

  public String getPlanningVacance() {
    return planningDifferentVacances;
  }

  public void setPlanningDifferentVacances(String planningDifferentVacances) {
    this.planningDifferentVacances = planningDifferentVacances;
  }

  public DeclarationAccueilDTO getDeclaration() {
    return declaration;
  }

  public void setDeclaration(DeclarationAccueilDTO declaration) {
    this.declaration = declaration;
  }

  public String getPrecision() {
    return precision;
  }

  public void setPrecision(String precision) {
    this.precision = precision;
  }

  public String getS1debcr1lundi() {
    return s1debcr1lundi;
  }

  public void setS1debcr1lundi(String s1debcr1lundi) {
    this.s1debcr1lundi = s1debcr1lundi;
  }

  public String getS1fincr1lundi() {
    return s1fincr1lundi;
  }

  public void setS1fincr1lundi(String s1fincr1lundi) {
    this.s1fincr1lundi = s1fincr1lundi;
  }

  public String getS1debcr2lundi() {
    return s1debcr2lundi;
  }

  public void setS1debcr2lundi(String s1debcr2lundi) {
    this.s1debcr2lundi = s1debcr2lundi;
  }

  public String getS1fincr2lundi() {
    return s1fincr2lundi;
  }

  public void setS1fincr2lundi(String s1fincr2lundi) {
    this.s1fincr2lundi = s1fincr2lundi;
  }

  public String getS1debcr3lundi() {
    return s1debcr3lundi;
  }

  public void setS1debcr3lundi(String s1debcr3lundi) {
    this.s1debcr3lundi = s1debcr3lundi;
  }

  public String getS1fincr3lundi() {
    return s1fincr3lundi;
  }

  public void setS1fincr3lundi(String s1fincr3lundi) {
    this.s1fincr3lundi = s1fincr3lundi;
  }

  public String getS1debcr1mardi() {
    return s1debcr1mardi;
  }

  public void setS1debcr1mardi(String s1debcr1mardi) {
    this.s1debcr1mardi = s1debcr1mardi;
  }

  public String getS1fincr1mardi() {
    return s1fincr1mardi;
  }

  public void setS1fincr1mardi(String s1fincr1mardi) {
    this.s1fincr1mardi = s1fincr1mardi;
  }

  public String getS1debcr2mardi() {
    return s1debcr2mardi;
  }

  public void setS1debcr2mardi(String s1debcr2mardi) {
    this.s1debcr2mardi = s1debcr2mardi;
  }

  public String getS1fincr2mardi() {
    return s1fincr2mardi;
  }

  public void setS1fincr2mardi(String s1fincr2mardi) {
    this.s1fincr2mardi = s1fincr2mardi;
  }

  public String getS1debcr3mardi() {
    return s1debcr3mardi;
  }

  public void setS1debcr3mardi(String s1debcr3mardi) {
    this.s1debcr3mardi = s1debcr3mardi;
  }

  public String getS1fincr3mardi() {
    return s1fincr3mardi;
  }

  public void setS1fincr3mardi(String s1fincr3mardi) {
    this.s1fincr3mardi = s1fincr3mardi;
  }

  public String getS1debcr1mercredi() {
    return s1debcr1mercredi;
  }

  public void setS1debcr1mercredi(String s1debcr1mercredi) {
    this.s1debcr1mercredi = s1debcr1mercredi;
  }

  public String getS1fincr1mercredi() {
    return s1fincr1mercredi;
  }

  public void setS1fincr1mercredi(String s1fincr1mercredi) {
    this.s1fincr1mercredi = s1fincr1mercredi;
  }

  public String getS1debcr2mercredi() {
    return s1debcr2mercredi;
  }

  public void setS1debcr2mercredi(String s1debcr2mercredi) {
    this.s1debcr2mercredi = s1debcr2mercredi;
  }

  public String getS1fincr2mercredi() {
    return s1fincr2mercredi;
  }

  public void setS1fincr2mercredi(String s1fincr2mercredi) {
    this.s1fincr2mercredi = s1fincr2mercredi;
  }

  public String getS1debcr3mercredi() {
    return s1debcr3mercredi;
  }

  public void setS1debcr3mercredi(String s1debcr3mercredi) {
    this.s1debcr3mercredi = s1debcr3mercredi;
  }

  public String getS1fincr3mercredi() {
    return s1fincr3mercredi;
  }

  public void setS1fincr3mercredi(String s1fincr3mercredi) {
    this.s1fincr3mercredi = s1fincr3mercredi;
  }

  public String getS1debcr1jeudi() {
    return s1debcr1jeudi;
  }

  public void setS1debcr1jeudi(String s1debcr1jeudi) {
    this.s1debcr1jeudi = s1debcr1jeudi;
  }

  public String getS1fincr1jeudi() {
    return s1fincr1jeudi;
  }

  public void setS1fincr1jeudi(String s1fincr1jeudi) {
    this.s1fincr1jeudi = s1fincr1jeudi;
  }

  public String getS1debcr2jeudi() {
    return s1debcr2jeudi;
  }

  public void setS1debcr2jeudi(String s1debcr2jeudi) {
    this.s1debcr2jeudi = s1debcr2jeudi;
  }

  public String getS1fincr2jeudi() {
    return s1fincr2jeudi;
  }

  public void setS1fincr2jeudi(String s1fincr2jeudi) {
    this.s1fincr2jeudi = s1fincr2jeudi;
  }

  public String getS1debcr3jeudi() {
    return s1debcr3jeudi;
  }

  public void setS1debcr3jeudi(String s1debcr3jeudi) {
    this.s1debcr3jeudi = s1debcr3jeudi;
  }

  public String getS1fincr3jeudi() {
    return s1fincr3jeudi;
  }

  public void setS1fincr3jeudi(String s1fincr3jeudi) {
    this.s1fincr3jeudi = s1fincr3jeudi;
  }

  public String getS1debcr1vendredi() {
    return s1debcr1vendredi;
  }

  public void setS1debcr1vendredi(String s1debcr1vendredi) {
    this.s1debcr1vendredi = s1debcr1vendredi;
  }

  public String getS1fincr1vendredi() {
    return s1fincr1vendredi;
  }

  public void setS1fincr1vendredi(String s1fincr1vendredi) {
    this.s1fincr1vendredi = s1fincr1vendredi;
  }

  public String getS1debcr2vendredi() {
    return s1debcr2vendredi;
  }

  public void setS1debcr2vendredi(String s1debcr2vendredi) {
    this.s1debcr2vendredi = s1debcr2vendredi;
  }

  public String getS1fincr2vendredi() {
    return s1fincr2vendredi;
  }

  public void setS1fincr2vendredi(String s1fincr2vendredi) {
    this.s1fincr2vendredi = s1fincr2vendredi;
  }

  public String getS1debcr3vendredi() {
    return s1debcr3vendredi;
  }

  public void setS1debcr3vendredi(String s1debcr3vendredi) {
    this.s1debcr3vendredi = s1debcr3vendredi;
  }

  public String getS1fincr3vendredi() {
    return s1fincr3vendredi;
  }

  public void setS1fincr3vendredi(String s1fincr3vendredi) {
    this.s1fincr3vendredi = s1fincr3vendredi;
  }

  public String getS1debcr1samedi() {
    return s1debcr1samedi;
  }

  public void setS1debcr1samedi(String s1debcr1samedi) {
    this.s1debcr1samedi = s1debcr1samedi;
  }

  public String getS1fincr1samedi() {
    return s1fincr1samedi;
  }

  public void setS1fincr1samedi(String s1fincr1samedi) {
    this.s1fincr1samedi = s1fincr1samedi;
  }

  public String getS1debcr2samedi() {
    return s1debcr2samedi;
  }

  public void setS1debcr2samedi(String s1debcr2samedi) {
    this.s1debcr2samedi = s1debcr2samedi;
  }

  public String getS1fincr2samedi() {
    return s1fincr2samedi;
  }

  public void setS1fincr2samedi(String s1fincr2samedi) {
    this.s1fincr2samedi = s1fincr2samedi;
  }

  public String getS1debcr3samedi() {
    return s1debcr3samedi;
  }

  public void setS1debcr3samedi(String s1debcr3samedi) {
    this.s1debcr3samedi = s1debcr3samedi;
  }

  public String getS1fincr3samedi() {
    return s1fincr3samedi;
  }

  public void setS1fincr3samedi(String s1fincr3samedi) {
    this.s1fincr3samedi = s1fincr3samedi;
  }

  public String getS1debcr1dimanche() {
    return s1debcr1dimanche;
  }

  public void setS1debcr1dimanche(String s1debcr1dimanche) {
    this.s1debcr1dimanche = s1debcr1dimanche;
  }

  public String getS1fincr1dimanche() {
    return s1fincr1dimanche;
  }

  public void setS1fincr1dimanche(String s1fincr1dimanche) {
    this.s1fincr1dimanche = s1fincr1dimanche;
  }

  public String getS1debcr2dimanche() {
    return s1debcr2dimanche;
  }

  public void setS1debcr2dimanche(String s1debcr2dimanche) {
    this.s1debcr2dimanche = s1debcr2dimanche;
  }

  public String getS1fincr2dimanche() {
    return s1fincr2dimanche;
  }

  public void setS1fincr2dimanche(String s1fincr2dimanche) {
    this.s1fincr2dimanche = s1fincr2dimanche;
  }

  public String getS1debcr3dimanche() {
    return s1debcr3dimanche;
  }

  public void setS1debcr3dimanche(String s1debcr3dimanche) {
    this.s1debcr3dimanche = s1debcr3dimanche;
  }

  public String getS1fincr3dimanche() {
    return s1fincr3dimanche;
  }

  public void setS1fincr3dimanche(String s1fincr3dimanche) {
    this.s1fincr3dimanche = s1fincr3dimanche;
  }

  public String getS2debcr1lundi() {
    return s2debcr1lundi;
  }

  public void setS2debcr1lundi(String s2debcr1lundi) {
    this.s2debcr1lundi = s2debcr1lundi;
  }

  public String getS2fincr1lundi() {
    return s2fincr1lundi;
  }

  public void setS2fincr1lundi(String s2fincr1lundi) {
    this.s2fincr1lundi = s2fincr1lundi;
  }

  public String getS2debcr2lundi() {
    return s2debcr2lundi;
  }

  public void setS2debcr2lundi(String s2debcr2lundi) {
    this.s2debcr2lundi = s2debcr2lundi;
  }

  public String getS2fincr2lundi() {
    return s2fincr2lundi;
  }

  public void setS2fincr2lundi(String s2fincr2lundi) {
    this.s2fincr2lundi = s2fincr2lundi;
  }

  public String getS2debcr3lundi() {
    return s2debcr3lundi;
  }

  public void setS2debcr3lundi(String s2debcr3lundi) {
    this.s2debcr3lundi = s2debcr3lundi;
  }

  public String getS2fincr3lundi() {
    return s2fincr3lundi;
  }

  public void setS2fincr3lundi(String s2fincr3lundi) {
    this.s2fincr3lundi = s2fincr3lundi;
  }

  public String getS2debcr1mardi() {
    return s2debcr1mardi;
  }

  public void setS2debcr1mardi(String s2debcr1mardi) {
    this.s2debcr1mardi = s2debcr1mardi;
  }

  public String getS2fincr1mardi() {
    return s2fincr1mardi;
  }

  public void setS2fincr1mardi(String s2fincr1mardi) {
    this.s2fincr1mardi = s2fincr1mardi;
  }

  public String getS2debcr2mardi() {
    return s2debcr2mardi;
  }

  public void setS2debcr2mardi(String s2debcr2mardi) {
    this.s2debcr2mardi = s2debcr2mardi;
  }

  public String getS2fincr2mardi() {
    return s2fincr2mardi;
  }

  public void setS2fincr2mardi(String s2fincr2mardi) {
    this.s2fincr2mardi = s2fincr2mardi;
  }

  public String getS2debcr3mardi() {
    return s2debcr3mardi;
  }

  public void setS2debcr3mardi(String s2debcr3mardi) {
    this.s2debcr3mardi = s2debcr3mardi;
  }

  public String getS2fincr3mardi() {
    return s2fincr3mardi;
  }

  public void setS2fincr3mardi(String s2fincr3mardi) {
    this.s2fincr3mardi = s2fincr3mardi;
  }

  public String getS2debcr1mercredi() {
    return s2debcr1mercredi;
  }

  public void setS2debcr1mercredi(String s2debcr1mercredi) {
    this.s2debcr1mercredi = s2debcr1mercredi;
  }

  public String getS2fincr1mercredi() {
    return s2fincr1mercredi;
  }

  public void setS2fincr1mercredi(String s2fincr1mercredi) {
    this.s2fincr1mercredi = s2fincr1mercredi;
  }

  public String getS2debcr2mercredi() {
    return s2debcr2mercredi;
  }

  public void setS2debcr2mercredi(String s2debcr2mercredi) {
    this.s2debcr2mercredi = s2debcr2mercredi;
  }

  public String getS2fincr2mercredi() {
    return s2fincr2mercredi;
  }

  public void setS2fincr2mercredi(String s2fincr2mercredi) {
    this.s2fincr2mercredi = s2fincr2mercredi;
  }

  public String getS2debcr3mercredi() {
    return s2debcr3mercredi;
  }

  public void setS2debcr3mercredi(String s2debcr3mercredi) {
    this.s2debcr3mercredi = s2debcr3mercredi;
  }

  public String getS2fincr3mercredi() {
    return s2fincr3mercredi;
  }

  public void setS2fincr3mercredi(String s2fincr3mercredi) {
    this.s2fincr3mercredi = s2fincr3mercredi;
  }

  public String getS2debcr1jeudi() {
    return s2debcr1jeudi;
  }

  public void setS2debcr1jeudi(String s2debcr1jeudi) {
    this.s2debcr1jeudi = s2debcr1jeudi;
  }

  public String getS2fincr1jeudi() {
    return s2fincr1jeudi;
  }

  public void setS2fincr1jeudi(String s2fincr1jeudi) {
    this.s2fincr1jeudi = s2fincr1jeudi;
  }

  public String getS2debcr2jeudi() {
    return s2debcr2jeudi;
  }

  public void setS2debcr2jeudi(String s2debcr2jeudi) {
    this.s2debcr2jeudi = s2debcr2jeudi;
  }

  public String getS2fincr2jeudi() {
    return s2fincr2jeudi;
  }

  public void setS2fincr2jeudi(String s2fincr2jeudi) {
    this.s2fincr2jeudi = s2fincr2jeudi;
  }

  public String getS2debcr3jeudi() {
    return s2debcr3jeudi;
  }

  public void setS2debcr3jeudi(String s2debcr3jeudi) {
    this.s2debcr3jeudi = s2debcr3jeudi;
  }

  public String getS2fincr3jeudi() {
    return s2fincr3jeudi;
  }

  public void setS2fincr3jeudi(String s2fincr3jeudi) {
    this.s2fincr3jeudi = s2fincr3jeudi;
  }

  public String getS2debcr1vendredi() {
    return s2debcr1vendredi;
  }

  public void setS2debcr1vendredi(String s2debcr1vendredi) {
    this.s2debcr1vendredi = s2debcr1vendredi;
  }

  public String getS2fincr1vendredi() {
    return s2fincr1vendredi;
  }

  public void setS2fincr1vendredi(String s2fincr1vendredi) {
    this.s2fincr1vendredi = s2fincr1vendredi;
  }

  public String getS2debcr2vendredi() {
    return s2debcr2vendredi;
  }

  public void setS2debcr2vendredi(String s2debcr2vendredi) {
    this.s2debcr2vendredi = s2debcr2vendredi;
  }

  public String getS2fincr2vendredi() {
    return s2fincr2vendredi;
  }

  public void setS2fincr2vendredi(String s2fincr2vendredi) {
    this.s2fincr2vendredi = s2fincr2vendredi;
  }

  public String getS2debcr3vendredi() {
    return s2debcr3vendredi;
  }

  public void setS2debcr3vendredi(String s2debcr3vendredi) {
    this.s2debcr3vendredi = s2debcr3vendredi;
  }

  public String getS2fincr3vendredi() {
    return s2fincr3vendredi;
  }

  public void setS2fincr3vendredi(String s2fincr3vendredi) {
    this.s2fincr3vendredi = s2fincr3vendredi;
  }

  public String getS2debcr1samedi() {
    return s2debcr1samedi;
  }

  public void setS2debcr1samedi(String s2debcr1samedi) {
    this.s2debcr1samedi = s2debcr1samedi;
  }

  public String getS2fincr1samedi() {
    return s2fincr1samedi;
  }

  public void setS2fincr1samedi(String s2fincr1samedi) {
    this.s2fincr1samedi = s2fincr1samedi;
  }

  public String getS2debcr2samedi() {
    return s2debcr2samedi;
  }

  public void setS2debcr2samedi(String s2debcr2samedi) {
    this.s2debcr2samedi = s2debcr2samedi;
  }

  public String getS2fincr2samedi() {
    return s2fincr2samedi;
  }

  public void setS2fincr2samedi(String s2fincr2samedi) {
    this.s2fincr2samedi = s2fincr2samedi;
  }

  public String getS2debcr3samedi() {
    return s2debcr3samedi;
  }

  public void setS2debcr3samedi(String s2debcr3samedi) {
    this.s2debcr3samedi = s2debcr3samedi;
  }

  public String getS2fincr3samedi() {
    return s2fincr3samedi;
  }

  public void setS2fincr3samedi(String s2fincr3samedi) {
    this.s2fincr3samedi = s2fincr3samedi;
  }

  public String getS2debcr1dimanche() {
    return s2debcr1dimanche;
  }

  public void setS2debcr1dimanche(String s2debcr1dimanche) {
    this.s2debcr1dimanche = s2debcr1dimanche;
  }

  public String getS2fincr1dimanche() {
    return s2fincr1dimanche;
  }

  public void setS2fincr1dimanche(String s2fincr1dimanche) {
    this.s2fincr1dimanche = s2fincr1dimanche;
  }

  public String getS2debcr2dimanche() {
    return s2debcr2dimanche;
  }

  public void setS2debcr2dimanche(String s2debcr2dimanche) {
    this.s2debcr2dimanche = s2debcr2dimanche;
  }

  public String getS2fincr2dimanche() {
    return s2fincr2dimanche;
  }

  public void setS2fincr2dimanche(String s2fincr2dimanche) {
    this.s2fincr2dimanche = s2fincr2dimanche;
  }

  public String getS2debcr3dimanche() {
    return s2debcr3dimanche;
  }

  public void setS2debcr3dimanche(String s2debcr3dimanche) {
    this.s2debcr3dimanche = s2debcr3dimanche;
  }

  public String getS2fincr3dimanche() {
    return s2fincr3dimanche;
  }

  public void setS2fincr3dimanche(String s2fincr3dimanche) {
    this.s2fincr3dimanche = s2fincr3dimanche;
  }

  public String getS3debcr1lundi() {
    return s3debcr1lundi;
  }

  public void setS3debcr1lundi(String s3debcr1lundi) {
    this.s3debcr1lundi = s3debcr1lundi;
  }

  public String getS3fincr1lundi() {
    return s3fincr1lundi;
  }

  public void setS3fincr1lundi(String s3fincr1lundi) {
    this.s3fincr1lundi = s3fincr1lundi;
  }

  public String getS3debcr2lundi() {
    return s3debcr2lundi;
  }

  public void setS3debcr2lundi(String s3debcr2lundi) {
    this.s3debcr2lundi = s3debcr2lundi;
  }

  public String getS3fincr2lundi() {
    return s3fincr2lundi;
  }

  public void setS3fincr2lundi(String s3fincr2lundi) {
    this.s3fincr2lundi = s3fincr2lundi;
  }

  public String getS3debcr3lundi() {
    return s3debcr3lundi;
  }

  public void setS3debcr3lundi(String s3debcr3lundi) {
    this.s3debcr3lundi = s3debcr3lundi;
  }

  public String getS3fincr3lundi() {
    return s3fincr3lundi;
  }

  public void setS3fincr3lundi(String s3fincr3lundi) {
    this.s3fincr3lundi = s3fincr3lundi;
  }

  public String getS3debcr1mardi() {
    return s3debcr1mardi;
  }

  public void setS3debcr1mardi(String s3debcr1mardi) {
    this.s3debcr1mardi = s3debcr1mardi;
  }

  public String getS3fincr1mardi() {
    return s3fincr1mardi;
  }

  public void setS3fincr1mardi(String s3fincr1mardi) {
    this.s3fincr1mardi = s3fincr1mardi;
  }

  public String getS3debcr2mardi() {
    return s3debcr2mardi;
  }

  public void setS3debcr2mardi(String s3debcr2mardi) {
    this.s3debcr2mardi = s3debcr2mardi;
  }

  public String getS3fincr2mardi() {
    return s3fincr2mardi;
  }

  public void setS3fincr2mardi(String s3fincr2mardi) {
    this.s3fincr2mardi = s3fincr2mardi;
  }

  public String getS3debcr3mardi() {
    return s3debcr3mardi;
  }

  public void setS3debcr3mardi(String s3debcr3mardi) {
    this.s3debcr3mardi = s3debcr3mardi;
  }

  public String getS3fincr3mardi() {
    return s3fincr3mardi;
  }

  public void setS3fincr3mardi(String s3fincr3mardi) {
    this.s3fincr3mardi = s3fincr3mardi;
  }

  public String getS3debcr1mercredi() {
    return s3debcr1mercredi;
  }

  public void setS3debcr1mercredi(String s3debcr1mercredi) {
    this.s3debcr1mercredi = s3debcr1mercredi;
  }

  public String getS3fincr1mercredi() {
    return s3fincr1mercredi;
  }

  public void setS3fincr1mercredi(String s3fincr1mercredi) {
    this.s3fincr1mercredi = s3fincr1mercredi;
  }

  public String getS3debcr2mercredi() {
    return s3debcr2mercredi;
  }

  public void setS3debcr2mercredi(String s3debcr2mercredi) {
    this.s3debcr2mercredi = s3debcr2mercredi;
  }

  public String getS3fincr2mercredi() {
    return s3fincr2mercredi;
  }

  public void setS3fincr2mercredi(String s3fincr2mercredi) {
    this.s3fincr2mercredi = s3fincr2mercredi;
  }

  public String getS3debcr3mercredi() {
    return s3debcr3mercredi;
  }

  public void setS3debcr3mercredi(String s3debcr3mercredi) {
    this.s3debcr3mercredi = s3debcr3mercredi;
  }

  public String getS3fincr3mercredi() {
    return s3fincr3mercredi;
  }

  public void setS3fincr3mercredi(String s3fincr3mercredi) {
    this.s3fincr3mercredi = s3fincr3mercredi;
  }

  public String getS3debcr1jeudi() {
    return s3debcr1jeudi;
  }

  public void setS3debcr1jeudi(String s3debcr1jeudi) {
    this.s3debcr1jeudi = s3debcr1jeudi;
  }

  public String getS3fincr1jeudi() {
    return s3fincr1jeudi;
  }

  public void setS3fincr1jeudi(String s3fincr1jeudi) {
    this.s3fincr1jeudi = s3fincr1jeudi;
  }

  public String getS3debcr2jeudi() {
    return s3debcr2jeudi;
  }

  public void setS3debcr2jeudi(String s3debcr2jeudi) {
    this.s3debcr2jeudi = s3debcr2jeudi;
  }

  public String getS3fincr2jeudi() {
    return s3fincr2jeudi;
  }

  public void setS3fincr2jeudi(String s3fincr2jeudi) {
    this.s3fincr2jeudi = s3fincr2jeudi;
  }

  public String getS3debcr3jeudi() {
    return s3debcr3jeudi;
  }

  public void setS3debcr3jeudi(String s3debcr3jeudi) {
    this.s3debcr3jeudi = s3debcr3jeudi;
  }

  public String getS3fincr3jeudi() {
    return s3fincr3jeudi;
  }

  public void setS3fincr3jeudi(String s3fincr3jeudi) {
    this.s3fincr3jeudi = s3fincr3jeudi;
  }

  public String getS3debcr1vendredi() {
    return s3debcr1vendredi;
  }

  public void setS3debcr1vendredi(String s3debcr1vendredi) {
    this.s3debcr1vendredi = s3debcr1vendredi;
  }

  public String getS3fincr1vendredi() {
    return s3fincr1vendredi;
  }

  public void setS3fincr1vendredi(String s3fincr1vendredi) {
    this.s3fincr1vendredi = s3fincr1vendredi;
  }

  public String getS3debcr2vendredi() {
    return s3debcr2vendredi;
  }

  public void setS3debcr2vendredi(String s3debcr2vendredi) {
    this.s3debcr2vendredi = s3debcr2vendredi;
  }

  public String getS3fincr2vendredi() {
    return s3fincr2vendredi;
  }

  public void setS3fincr2vendredi(String s3fincr2vendredi) {
    this.s3fincr2vendredi = s3fincr2vendredi;
  }

  public String getS3debcr3vendredi() {
    return s3debcr3vendredi;
  }

  public void setS3debcr3vendredi(String s3debcr3vendredi) {
    this.s3debcr3vendredi = s3debcr3vendredi;
  }

  public String getS3fincr3vendredi() {
    return s3fincr3vendredi;
  }

  public void setS3fincr3vendredi(String s3fincr3vendredi) {
    this.s3fincr3vendredi = s3fincr3vendredi;
  }

  public String getS3debcr1samedi() {
    return s3debcr1samedi;
  }

  public void setS3debcr1samedi(String s3debcr1samedi) {
    this.s3debcr1samedi = s3debcr1samedi;
  }

  public String getS3fincr1samedi() {
    return s3fincr1samedi;
  }

  public void setS3fincr1samedi(String s3fincr1samedi) {
    this.s3fincr1samedi = s3fincr1samedi;
  }

  public String getS3debcr2samedi() {
    return s3debcr2samedi;
  }

  public void setS3debcr2samedi(String s3debcr2samedi) {
    this.s3debcr2samedi = s3debcr2samedi;
  }

  public String getS3fincr2samedi() {
    return s3fincr2samedi;
  }

  public void setS3fincr2samedi(String s3fincr2samedi) {
    this.s3fincr2samedi = s3fincr2samedi;
  }

  public String getS3debcr3samedi() {
    return s3debcr3samedi;
  }

  public void setS3debcr3samedi(String s3debcr3samedi) {
    this.s3debcr3samedi = s3debcr3samedi;
  }

  public String getS3fincr3samedi() {
    return s3fincr3samedi;
  }

  public void setS3fincr3samedi(String s3fincr3samedi) {
    this.s3fincr3samedi = s3fincr3samedi;
  }

  public String getS3debcr1dimanche() {
    return s3debcr1dimanche;
  }

  public void setS3debcr1dimanche(String s3debcr1dimanche) {
    this.s3debcr1dimanche = s3debcr1dimanche;
  }

  public String getS3fincr1dimanche() {
    return s3fincr1dimanche;
  }

  public void setS3fincr1dimanche(String s3fincr1dimanche) {
    this.s3fincr1dimanche = s3fincr1dimanche;
  }

  public String getS3debcr2dimanche() {
    return s3debcr2dimanche;
  }

  public void setS3debcr2dimanche(String s3debcr2dimanche) {
    this.s3debcr2dimanche = s3debcr2dimanche;
  }

  public String getS3fincr2dimanche() {
    return s3fincr2dimanche;
  }

  public void setS3fincr2dimanche(String s3fincr2dimanche) {
    this.s3fincr2dimanche = s3fincr2dimanche;
  }

  public String getS3debcr3dimanche() {
    return s3debcr3dimanche;
  }

  public void setS3debcr3dimanche(String s3debcr3dimanche) {
    this.s3debcr3dimanche = s3debcr3dimanche;
  }

  public String getS3fincr3dimanche() {
    return s3fincr3dimanche;
  }

  public void setS3fincr3dimanche(String s3fincr3dimanche) {
    this.s3fincr3dimanche = s3fincr3dimanche;
  }

  public String getS4debcr1lundi() {
    return s4debcr1lundi;
  }

  public void setS4debcr1lundi(String s4debcr1lundi) {
    this.s4debcr1lundi = s4debcr1lundi;
  }

  public String getS4fincr1lundi() {
    return s4fincr1lundi;
  }

  public void setS4fincr1lundi(String s4fincr1lundi) {
    this.s4fincr1lundi = s4fincr1lundi;
  }

  public String getS4debcr2lundi() {
    return s4debcr2lundi;
  }

  public void setS4debcr2lundi(String s4debcr2lundi) {
    this.s4debcr2lundi = s4debcr2lundi;
  }

  public String getS4fincr2lundi() {
    return s4fincr2lundi;
  }

  public void setS4fincr2lundi(String s4fincr2lundi) {
    this.s4fincr2lundi = s4fincr2lundi;
  }

  public String getS4debcr3lundi() {
    return s4debcr3lundi;
  }

  public void setS4debcr3lundi(String s4debcr3lundi) {
    this.s4debcr3lundi = s4debcr3lundi;
  }

  public String getS4fincr3lundi() {
    return s4fincr3lundi;
  }

  public void setS4fincr3lundi(String s4fincr3lundi) {
    this.s4fincr3lundi = s4fincr3lundi;
  }

  public String getS4debcr1mardi() {
    return s4debcr1mardi;
  }

  public void setS4debcr1mardi(String s4debcr1mardi) {
    this.s4debcr1mardi = s4debcr1mardi;
  }

  public String getS4fincr1mardi() {
    return s4fincr1mardi;
  }

  public void setS4fincr1mardi(String s4fincr1mardi) {
    this.s4fincr1mardi = s4fincr1mardi;
  }

  public String getS4debcr2mardi() {
    return s4debcr2mardi;
  }

  public void setS4debcr2mardi(String s4debcr2mardi) {
    this.s4debcr2mardi = s4debcr2mardi;
  }

  public String getS4fincr2mardi() {
    return s4fincr2mardi;
  }

  public void setS4fincr2mardi(String s4fincr2mardi) {
    this.s4fincr2mardi = s4fincr2mardi;
  }

  public String getS4debcr3mardi() {
    return s4debcr3mardi;
  }

  public void setS4debcr3mardi(String s4debcr3mardi) {
    this.s4debcr3mardi = s4debcr3mardi;
  }

  public String getS4fincr3mardi() {
    return s4fincr3mardi;
  }

  public void setS4fincr3mardi(String s4fincr3mardi) {
    this.s4fincr3mardi = s4fincr3mardi;
  }

  public String getS4debcr1mercredi() {
    return s4debcr1mercredi;
  }

  public void setS4debcr1mercredi(String s4debcr1mercredi) {
    this.s4debcr1mercredi = s4debcr1mercredi;
  }

  public String getS4fincr1mercredi() {
    return s4fincr1mercredi;
  }

  public void setS4fincr1mercredi(String s4fincr1mercredi) {
    this.s4fincr1mercredi = s4fincr1mercredi;
  }

  public String getS4debcr2mercredi() {
    return s4debcr2mercredi;
  }

  public void setS4debcr2mercredi(String s4debcr2mercredi) {
    this.s4debcr2mercredi = s4debcr2mercredi;
  }

  public String getS4fincr2mercredi() {
    return s4fincr2mercredi;
  }

  public void setS4fincr2mercredi(String s4fincr2mercredi) {
    this.s4fincr2mercredi = s4fincr2mercredi;
  }

  public String getS4debcr3mercredi() {
    return s4debcr3mercredi;
  }

  public void setS4debcr3mercredi(String s4debcr3mercredi) {
    this.s4debcr3mercredi = s4debcr3mercredi;
  }

  public String getS4fincr3mercredi() {
    return s4fincr3mercredi;
  }

  public void setS4fincr3mercredi(String s4fincr3mercredi) {
    this.s4fincr3mercredi = s4fincr3mercredi;
  }

  public String getS4debcr1jeudi() {
    return s4debcr1jeudi;
  }

  public void setS4debcr1jeudi(String s4debcr1jeudi) {
    this.s4debcr1jeudi = s4debcr1jeudi;
  }

  public String getS4fincr1jeudi() {
    return s4fincr1jeudi;
  }

  public void setS4fincr1jeudi(String s4fincr1jeudi) {
    this.s4fincr1jeudi = s4fincr1jeudi;
  }

  public String getS4debcr2jeudi() {
    return s4debcr2jeudi;
  }

  public void setS4debcr2jeudi(String s4debcr2jeudi) {
    this.s4debcr2jeudi = s4debcr2jeudi;
  }

  public String getS4fincr2jeudi() {
    return s4fincr2jeudi;
  }

  public void setS4fincr2jeudi(String s4fincr2jeudi) {
    this.s4fincr2jeudi = s4fincr2jeudi;
  }

  public String getS4debcr3jeudi() {
    return s4debcr3jeudi;
  }

  public void setS4debcr3jeudi(String s4debcr3jeudi) {
    this.s4debcr3jeudi = s4debcr3jeudi;
  }

  public String getS4fincr3jeudi() {
    return s4fincr3jeudi;
  }

  public void setS4fincr3jeudi(String s4fincr3jeudi) {
    this.s4fincr3jeudi = s4fincr3jeudi;
  }

  public String getS4debcr1vendredi() {
    return s4debcr1vendredi;
  }

  public void setS4debcr1vendredi(String s4debcr1vendredi) {
    this.s4debcr1vendredi = s4debcr1vendredi;
  }

  public String getS4fincr1vendredi() {
    return s4fincr1vendredi;
  }

  public void setS4fincr1vendredi(String s4fincr1vendredi) {
    this.s4fincr1vendredi = s4fincr1vendredi;
  }

  public String getS4debcr2vendredi() {
    return s4debcr2vendredi;
  }

  public void setS4debcr2vendredi(String s4debcr2vendredi) {
    this.s4debcr2vendredi = s4debcr2vendredi;
  }

  public String getS4fincr2vendredi() {
    return s4fincr2vendredi;
  }

  public void setS4fincr2vendredi(String s4fincr2vendredi) {
    this.s4fincr2vendredi = s4fincr2vendredi;
  }

  public String getS4debcr3vendredi() {
    return s4debcr3vendredi;
  }

  public void setS4debcr3vendredi(String s4debcr3vendredi) {
    this.s4debcr3vendredi = s4debcr3vendredi;
  }

  public String getS4fincr3vendredi() {
    return s4fincr3vendredi;
  }

  public void setS4fincr3vendredi(String s4fincr3vendredi) {
    this.s4fincr3vendredi = s4fincr3vendredi;
  }

  public String getS4debcr1samedi() {
    return s4debcr1samedi;
  }

  public void setS4debcr1samedi(String s4debcr1samedi) {
    this.s4debcr1samedi = s4debcr1samedi;
  }

  public String getS4fincr1samedi() {
    return s4fincr1samedi;
  }

  public void setS4fincr1samedi(String s4fincr1samedi) {
    this.s4fincr1samedi = s4fincr1samedi;
  }

  public String getS4debcr2samedi() {
    return s4debcr2samedi;
  }

  public void setS4debcr2samedi(String s4debcr2samedi) {
    this.s4debcr2samedi = s4debcr2samedi;
  }

  public String getS4fincr2samedi() {
    return s4fincr2samedi;
  }

  public void setS4fincr2samedi(String s4fincr2samedi) {
    this.s4fincr2samedi = s4fincr2samedi;
  }

  public String getS4debcr3samedi() {
    return s4debcr3samedi;
  }

  public void setS4debcr3samedi(String s4debcr3samedi) {
    this.s4debcr3samedi = s4debcr3samedi;
  }

  public String getS4fincr3samedi() {
    return s4fincr3samedi;
  }

  public void setS4fincr3samedi(String s4fincr3samedi) {
    this.s4fincr3samedi = s4fincr3samedi;
  }

  public String getS4debcr1dimanche() {
    return s4debcr1dimanche;
  }

  public void setS4debcr1dimanche(String s4debcr1dimanche) {
    this.s4debcr1dimanche = s4debcr1dimanche;
  }

  public String getS4fincr1dimanche() {
    return s4fincr1dimanche;
  }

  public void setS4fincr1dimanche(String s4fincr1dimanche) {
    this.s4fincr1dimanche = s4fincr1dimanche;
  }

  public String getS4debcr2dimanche() {
    return s4debcr2dimanche;
  }

  public void setS4debcr2dimanche(String s4debcr2dimanche) {
    this.s4debcr2dimanche = s4debcr2dimanche;
  }

  public String getS4fincr2dimanche() {
    return s4fincr2dimanche;
  }

  public void setS4fincr2dimanche(String s4fincr2dimanche) {
    this.s4fincr2dimanche = s4fincr2dimanche;
  }

  public String getS4debcr3dimanche() {
    return s4debcr3dimanche;
  }

  public void setS4debcr3dimanche(String s4debcr3dimanche) {
    this.s4debcr3dimanche = s4debcr3dimanche;
  }

  public String getS4fincr3dimanche() {
    return s4fincr3dimanche;
  }

  public void setS4fincr3dimanche(String s4fincr3dimanche) {
    this.s4fincr3dimanche = s4fincr3dimanche;
  }

  public String getS5debcr1lundi() {
    return s5debcr1lundi;
  }

  public void setS5debcr1lundi(String s5debcr1lundi) {
    this.s5debcr1lundi = s5debcr1lundi;
  }

  public String getS5fincr1lundi() {
    return s5fincr1lundi;
  }

  public void setS5fincr1lundi(String s5fincr1lundi) {
    this.s5fincr1lundi = s5fincr1lundi;
  }

  public String getS5debcr2lundi() {
    return s5debcr2lundi;
  }

  public void setS5debcr2lundi(String s5debcr2lundi) {
    this.s5debcr2lundi = s5debcr2lundi;
  }

  public String getS5fincr2lundi() {
    return s5fincr2lundi;
  }

  public void setS5fincr2lundi(String s5fincr2lundi) {
    this.s5fincr2lundi = s5fincr2lundi;
  }

  public String getS5debcr3lundi() {
    return s5debcr3lundi;
  }

  public void setS5debcr3lundi(String s5debcr3lundi) {
    this.s5debcr3lundi = s5debcr3lundi;
  }

  public String getS5fincr3lundi() {
    return s5fincr3lundi;
  }

  public void setS5fincr3lundi(String s5fincr3lundi) {
    this.s5fincr3lundi = s5fincr3lundi;
  }

  public String getS5debcr1mardi() {
    return s5debcr1mardi;
  }

  public void setS5debcr1mardi(String s5debcr1mardi) {
    this.s5debcr1mardi = s5debcr1mardi;
  }

  public String getS5fincr1mardi() {
    return s5fincr1mardi;
  }

  public void setS5fincr1mardi(String s5fincr1mardi) {
    this.s5fincr1mardi = s5fincr1mardi;
  }

  public String getS5debcr2mardi() {
    return s5debcr2mardi;
  }

  public void setS5debcr2mardi(String s5debcr2mardi) {
    this.s5debcr2mardi = s5debcr2mardi;
  }

  public String getS5fincr2mardi() {
    return s5fincr2mardi;
  }

  public void setS5fincr2mardi(String s5fincr2mardi) {
    this.s5fincr2mardi = s5fincr2mardi;
  }

  public String getS5debcr3mardi() {
    return s5debcr3mardi;
  }

  public void setS5debcr3mardi(String s5debcr3mardi) {
    this.s5debcr3mardi = s5debcr3mardi;
  }

  public String getS5fincr3mardi() {
    return s5fincr3mardi;
  }

  public void setS5fincr3mardi(String s5fincr3mardi) {
    this.s5fincr3mardi = s5fincr3mardi;
  }

  public String getS5debcr1mercredi() {
    return s5debcr1mercredi;
  }

  public void setS5debcr1mercredi(String s5debcr1mercredi) {
    this.s5debcr1mercredi = s5debcr1mercredi;
  }

  public String getS5fincr1mercredi() {
    return s5fincr1mercredi;
  }

  public void setS5fincr1mercredi(String s5fincr1mercredi) {
    this.s5fincr1mercredi = s5fincr1mercredi;
  }

  public String getS5debcr2mercredi() {
    return s5debcr2mercredi;
  }

  public void setS5debcr2mercredi(String s5debcr2mercredi) {
    this.s5debcr2mercredi = s5debcr2mercredi;
  }

  public String getS5fincr2mercredi() {
    return s5fincr2mercredi;
  }

  public void setS5fincr2mercredi(String s5fincr2mercredi) {
    this.s5fincr2mercredi = s5fincr2mercredi;
  }

  public String getS5debcr3mercredi() {
    return s5debcr3mercredi;
  }

  public void setS5debcr3mercredi(String s5debcr3mercredi) {
    this.s5debcr3mercredi = s5debcr3mercredi;
  }

  public String getS5fincr3mercredi() {
    return s5fincr3mercredi;
  }

  public void setS5fincr3mercredi(String s5fincr3mercredi) {
    this.s5fincr3mercredi = s5fincr3mercredi;
  }

  public String getS5debcr1jeudi() {
    return s5debcr1jeudi;
  }

  public void setS5debcr1jeudi(String s5debcr1jeudi) {
    this.s5debcr1jeudi = s5debcr1jeudi;
  }

  public String getS5fincr1jeudi() {
    return s5fincr1jeudi;
  }

  public void setS5fincr1jeudi(String s5fincr1jeudi) {
    this.s5fincr1jeudi = s5fincr1jeudi;
  }

  public String getS5debcr2jeudi() {
    return s5debcr2jeudi;
  }

  public void setS5debcr2jeudi(String s5debcr2jeudi) {
    this.s5debcr2jeudi = s5debcr2jeudi;
  }

  public String getS5fincr2jeudi() {
    return s5fincr2jeudi;
  }

  public void setS5fincr2jeudi(String s5fincr2jeudi) {
    this.s5fincr2jeudi = s5fincr2jeudi;
  }

  public String getS5debcr3jeudi() {
    return s5debcr3jeudi;
  }

  public void setS5debcr3jeudi(String s5debcr3jeudi) {
    this.s5debcr3jeudi = s5debcr3jeudi;
  }

  public String getS5fincr3jeudi() {
    return s5fincr3jeudi;
  }

  public void setS5fincr3jeudi(String s5fincr3jeudi) {
    this.s5fincr3jeudi = s5fincr3jeudi;
  }

  public String getS5debcr1vendredi() {
    return s5debcr1vendredi;
  }

  public void setS5debcr1vendredi(String s5debcr1vendredi) {
    this.s5debcr1vendredi = s5debcr1vendredi;
  }

  public String getS5fincr1vendredi() {
    return s5fincr1vendredi;
  }

  public void setS5fincr1vendredi(String s5fincr1vendredi) {
    this.s5fincr1vendredi = s5fincr1vendredi;
  }

  public String getS5debcr2vendredi() {
    return s5debcr2vendredi;
  }

  public void setS5debcr2vendredi(String s5debcr2vendredi) {
    this.s5debcr2vendredi = s5debcr2vendredi;
  }

  public String getS5fincr2vendredi() {
    return s5fincr2vendredi;
  }

  public void setS5fincr2vendredi(String s5fincr2vendredi) {
    this.s5fincr2vendredi = s5fincr2vendredi;
  }

  public String getS5debcr3vendredi() {
    return s5debcr3vendredi;
  }

  public void setS5debcr3vendredi(String s5debcr3vendredi) {
    this.s5debcr3vendredi = s5debcr3vendredi;
  }

  public String getS5fincr3vendredi() {
    return s5fincr3vendredi;
  }

  public void setS5fincr3vendredi(String s5fincr3vendredi) {
    this.s5fincr3vendredi = s5fincr3vendredi;
  }

  public String getS5debcr1samedi() {
    return s5debcr1samedi;
  }

  public void setS5debcr1samedi(String s5debcr1samedi) {
    this.s5debcr1samedi = s5debcr1samedi;
  }

  public String getS5fincr1samedi() {
    return s5fincr1samedi;
  }

  public void setS5fincr1samedi(String s5fincr1samedi) {
    this.s5fincr1samedi = s5fincr1samedi;
  }

  public String getS5debcr2samedi() {
    return s5debcr2samedi;
  }

  public void setS5debcr2samedi(String s5debcr2samedi) {
    this.s5debcr2samedi = s5debcr2samedi;
  }

  public String getS5fincr2samedi() {
    return s5fincr2samedi;
  }

  public void setS5fincr2samedi(String s5fincr2samedi) {
    this.s5fincr2samedi = s5fincr2samedi;
  }

  public String getS5debcr3samedi() {
    return s5debcr3samedi;
  }

  public void setS5debcr3samedi(String s5debcr3samedi) {
    this.s5debcr3samedi = s5debcr3samedi;
  }

  public String getS5fincr3samedi() {
    return s5fincr3samedi;
  }

  public void setS5fincr3samedi(String s5fincr3samedi) {
    this.s5fincr3samedi = s5fincr3samedi;
  }

  public String getS5debcr1dimanche() {
    return s5debcr1dimanche;
  }

  public void setS5debcr1dimanche(String s5debcr1dimanche) {
    this.s5debcr1dimanche = s5debcr1dimanche;
  }

  public String getS5fincr1dimanche() {
    return s5fincr1dimanche;
  }

  public void setS5fincr1dimanche(String s5fincr1dimanche) {
    this.s5fincr1dimanche = s5fincr1dimanche;
  }

  public String getS5debcr2dimanche() {
    return s5debcr2dimanche;
  }

  public void setS5debcr2dimanche(String s5debcr2dimanche) {
    this.s5debcr2dimanche = s5debcr2dimanche;
  }

  public String getS5fincr2dimanche() {
    return s5fincr2dimanche;
  }

  public void setS5fincr2dimanche(String s5fincr2dimanche) {
    this.s5fincr2dimanche = s5fincr2dimanche;
  }

  public String getS5debcr3dimanche() {
    return s5debcr3dimanche;
  }

  public void setS5debcr3dimanche(String s5debcr3dimanche) {
    this.s5debcr3dimanche = s5debcr3dimanche;
  }

  public String getS5fincr3dimanche() {
    return s5fincr3dimanche;
  }

  public void setS5fincr3dimanche(String s5fincr3dimanche) {
    this.s5fincr3dimanche = s5fincr3dimanche;
  }

  public String getS6debcr1lundi() {
    return s6debcr1lundi;
  }

  public void setS6debcr1lundi(String s6debcr1lundi) {
    this.s6debcr1lundi = s6debcr1lundi;
  }

  public String getS6fincr1lundi() {
    return s6fincr1lundi;
  }

  public void setS6fincr1lundi(String s6fincr1lundi) {
    this.s6fincr1lundi = s6fincr1lundi;
  }

  public String getS6debcr2lundi() {
    return s6debcr2lundi;
  }

  public void setS6debcr2lundi(String s6debcr2lundi) {
    this.s6debcr2lundi = s6debcr2lundi;
  }

  public String getS6fincr2lundi() {
    return s6fincr2lundi;
  }

  public void setS6fincr2lundi(String s6fincr2lundi) {
    this.s6fincr2lundi = s6fincr2lundi;
  }

  public String getS6debcr3lundi() {
    return s6debcr3lundi;
  }

  public void setS6debcr3lundi(String s6debcr3lundi) {
    this.s6debcr3lundi = s6debcr3lundi;
  }

  public String getS6fincr3lundi() {
    return s6fincr3lundi;
  }

  public void setS6fincr3lundi(String s6fincr3lundi) {
    this.s6fincr3lundi = s6fincr3lundi;
  }

  public String getS6debcr1mardi() {
    return s6debcr1mardi;
  }

  public void setS6debcr1mardi(String s6debcr1mardi) {
    this.s6debcr1mardi = s6debcr1mardi;
  }

  public String getS6fincr1mardi() {
    return s6fincr1mardi;
  }

  public void setS6fincr1mardi(String s6fincr1mardi) {
    this.s6fincr1mardi = s6fincr1mardi;
  }

  public String getS6debcr2mardi() {
    return s6debcr2mardi;
  }

  public void setS6debcr2mardi(String s6debcr2mardi) {
    this.s6debcr2mardi = s6debcr2mardi;
  }

  public String getS6fincr2mardi() {
    return s6fincr2mardi;
  }

  public void setS6fincr2mardi(String s6fincr2mardi) {
    this.s6fincr2mardi = s6fincr2mardi;
  }

  public String getS6debcr3mardi() {
    return s6debcr3mardi;
  }

  public void setS6debcr3mardi(String s6debcr3mardi) {
    this.s6debcr3mardi = s6debcr3mardi;
  }

  public String getS6fincr3mardi() {
    return s6fincr3mardi;
  }

  public void setS6fincr3mardi(String s6fincr3mardi) {
    this.s6fincr3mardi = s6fincr3mardi;
  }

  public String getS6debcr1mercredi() {
    return s6debcr1mercredi;
  }

  public void setS6debcr1mercredi(String s6debcr1mercredi) {
    this.s6debcr1mercredi = s6debcr1mercredi;
  }

  public String getS6fincr1mercredi() {
    return s6fincr1mercredi;
  }

  public void setS6fincr1mercredi(String s6fincr1mercredi) {
    this.s6fincr1mercredi = s6fincr1mercredi;
  }

  public String getS6debcr2mercredi() {
    return s6debcr2mercredi;
  }

  public void setS6debcr2mercredi(String s6debcr2mercredi) {
    this.s6debcr2mercredi = s6debcr2mercredi;
  }

  public String getS6fincr2mercredi() {
    return s6fincr2mercredi;
  }

  public void setS6fincr2mercredi(String s6fincr2mercredi) {
    this.s6fincr2mercredi = s6fincr2mercredi;
  }

  public String getS6debcr3mercredi() {
    return s6debcr3mercredi;
  }

  public void setS6debcr3mercredi(String s6debcr3mercredi) {
    this.s6debcr3mercredi = s6debcr3mercredi;
  }

  public String getS6fincr3mercredi() {
    return s6fincr3mercredi;
  }

  public void setS6fincr3mercredi(String s6fincr3mercredi) {
    this.s6fincr3mercredi = s6fincr3mercredi;
  }

  public String getS6debcr1jeudi() {
    return s6debcr1jeudi;
  }

  public void setS6debcr1jeudi(String s6debcr1jeudi) {
    this.s6debcr1jeudi = s6debcr1jeudi;
  }

  public String getS6fincr1jeudi() {
    return s6fincr1jeudi;
  }

  public void setS6fincr1jeudi(String s6fincr1jeudi) {
    this.s6fincr1jeudi = s6fincr1jeudi;
  }

  public String getS6debcr2jeudi() {
    return s6debcr2jeudi;
  }

  public void setS6debcr2jeudi(String s6debcr2jeudi) {
    this.s6debcr2jeudi = s6debcr2jeudi;
  }

  public String getS6fincr2jeudi() {
    return s6fincr2jeudi;
  }

  public void setS6fincr2jeudi(String s6fincr2jeudi) {
    this.s6fincr2jeudi = s6fincr2jeudi;
  }

  public String getS6debcr3jeudi() {
    return s6debcr3jeudi;
  }

  public void setS6debcr3jeudi(String s6debcr3jeudi) {
    this.s6debcr3jeudi = s6debcr3jeudi;
  }

  public String getS6fincr3jeudi() {
    return s6fincr3jeudi;
  }

  public void setS6fincr3jeudi(String s6fincr3jeudi) {
    this.s6fincr3jeudi = s6fincr3jeudi;
  }

  public String getS6debcr1vendredi() {
    return s6debcr1vendredi;
  }

  public void setS6debcr1vendredi(String s6debcr1vendredi) {
    this.s6debcr1vendredi = s6debcr1vendredi;
  }

  public String getS6fincr1vendredi() {
    return s6fincr1vendredi;
  }

  public void setS6fincr1vendredi(String s6fincr1vendredi) {
    this.s6fincr1vendredi = s6fincr1vendredi;
  }

  public String getS6debcr2vendredi() {
    return s6debcr2vendredi;
  }

  public void setS6debcr2vendredi(String s6debcr2vendredi) {
    this.s6debcr2vendredi = s6debcr2vendredi;
  }

  public String getS6fincr2vendredi() {
    return s6fincr2vendredi;
  }

  public void setS6fincr2vendredi(String s6fincr2vendredi) {
    this.s6fincr2vendredi = s6fincr2vendredi;
  }

  public String getS6debcr3vendredi() {
    return s6debcr3vendredi;
  }

  public void setS6debcr3vendredi(String s6debcr3vendredi) {
    this.s6debcr3vendredi = s6debcr3vendredi;
  }

  public String getS6fincr3vendredi() {
    return s6fincr3vendredi;
  }

  public void setS6fincr3vendredi(String s6fincr3vendredi) {
    this.s6fincr3vendredi = s6fincr3vendredi;
  }

  public String getS6debcr1samedi() {
    return s6debcr1samedi;
  }

  public void setS6debcr1samedi(String s6debcr1samedi) {
    this.s6debcr1samedi = s6debcr1samedi;
  }

  public String getS6fincr1samedi() {
    return s6fincr1samedi;
  }

  public void setS6fincr1samedi(String s6fincr1samedi) {
    this.s6fincr1samedi = s6fincr1samedi;
  }

  public String getS6debcr2samedi() {
    return s6debcr2samedi;
  }

  public void setS6debcr2samedi(String s6debcr2samedi) {
    this.s6debcr2samedi = s6debcr2samedi;
  }

  public String getS6fincr2samedi() {
    return s6fincr2samedi;
  }

  public void setS6fincr2samedi(String s6fincr2samedi) {
    this.s6fincr2samedi = s6fincr2samedi;
  }

  public String getS6debcr3samedi() {
    return s6debcr3samedi;
  }

  public void setS6debcr3samedi(String s6debcr3samedi) {
    this.s6debcr3samedi = s6debcr3samedi;
  }

  public String getS6fincr3samedi() {
    return s6fincr3samedi;
  }

  public void setS6fincr3samedi(String s6fincr3samedi) {
    this.s6fincr3samedi = s6fincr3samedi;
  }

  public String getS6debcr1dimanche() {
    return s6debcr1dimanche;
  }

  public void setS6debcr1dimanche(String s6debcr1dimanche) {
    this.s6debcr1dimanche = s6debcr1dimanche;
  }

  public String getS6fincr1dimanche() {
    return s6fincr1dimanche;
  }

  public void setS6fincr1dimanche(String s6fincr1dimanche) {
    this.s6fincr1dimanche = s6fincr1dimanche;
  }

  public String getS6debcr2dimanche() {
    return s6debcr2dimanche;
  }

  public void setS6debcr2dimanche(String s6debcr2dimanche) {
    this.s6debcr2dimanche = s6debcr2dimanche;
  }

  public String getS6fincr2dimanche() {
    return s6fincr2dimanche;
  }

  public void setS6fincr2dimanche(String s6fincr2dimanche) {
    this.s6fincr2dimanche = s6fincr2dimanche;
  }

  public String getS6debcr3dimanche() {
    return s6debcr3dimanche;
  }

  public void setS6debcr3dimanche(String s6debcr3dimanche) {
    this.s6debcr3dimanche = s6debcr3dimanche;
  }

  public String getS6fincr3dimanche() {
    return s6fincr3dimanche;
  }

  public void setS6fincr3dimanche(String s6fincr3dimanche) {
    this.s6fincr3dimanche = s6fincr3dimanche;
  }

  public String getS7debcr1lundi() {
    return s7debcr1lundi;
  }

  public void setS7debcr1lundi(String s7debcr1lundi) {
    this.s7debcr1lundi = s7debcr1lundi;
  }

  public String getS7fincr1lundi() {
    return s7fincr1lundi;
  }

  public void setS7fincr1lundi(String s7fincr1lundi) {
    this.s7fincr1lundi = s7fincr1lundi;
  }

  public String getS7debcr2lundi() {
    return s7debcr2lundi;
  }

  public void setS7debcr2lundi(String s7debcr2lundi) {
    this.s7debcr2lundi = s7debcr2lundi;
  }

  public String getS7fincr2lundi() {
    return s7fincr2lundi;
  }

  public void setS7fincr2lundi(String s7fincr2lundi) {
    this.s7fincr2lundi = s7fincr2lundi;
  }

  public String getS7debcr3lundi() {
    return s7debcr3lundi;
  }

  public void setS7debcr3lundi(String s7debcr3lundi) {
    this.s7debcr3lundi = s7debcr3lundi;
  }

  public String getS7fincr3lundi() {
    return s7fincr3lundi;
  }

  public void setS7fincr3lundi(String s7fincr3lundi) {
    this.s7fincr3lundi = s7fincr3lundi;
  }

  public String getS7debcr1mardi() {
    return s7debcr1mardi;
  }

  public void setS7debcr1mardi(String s7debcr1mardi) {
    this.s7debcr1mardi = s7debcr1mardi;
  }

  public String getS7fincr1mardi() {
    return s7fincr1mardi;
  }

  public void setS7fincr1mardi(String s7fincr1mardi) {
    this.s7fincr1mardi = s7fincr1mardi;
  }

  public String getS7debcr2mardi() {
    return s7debcr2mardi;
  }

  public void setS7debcr2mardi(String s7debcr2mardi) {
    this.s7debcr2mardi = s7debcr2mardi;
  }

  public String getS7fincr2mardi() {
    return s7fincr2mardi;
  }

  public void setS7fincr2mardi(String s7fincr2mardi) {
    this.s7fincr2mardi = s7fincr2mardi;
  }

  public String getS7debcr3mardi() {
    return s7debcr3mardi;
  }

  public void setS7debcr3mardi(String s7debcr3mardi) {
    this.s7debcr3mardi = s7debcr3mardi;
  }

  public String getS7fincr3mardi() {
    return s7fincr3mardi;
  }

  public void setS7fincr3mardi(String s7fincr3mardi) {
    this.s7fincr3mardi = s7fincr3mardi;
  }

  public String getS7debcr1mercredi() {
    return s7debcr1mercredi;
  }

  public void setS7debcr1mercredi(String s7debcr1mercredi) {
    this.s7debcr1mercredi = s7debcr1mercredi;
  }

  public String getS7fincr1mercredi() {
    return s7fincr1mercredi;
  }

  public void setS7fincr1mercredi(String s7fincr1mercredi) {
    this.s7fincr1mercredi = s7fincr1mercredi;
  }

  public String getS7debcr2mercredi() {
    return s7debcr2mercredi;
  }

  public void setS7debcr2mercredi(String s7debcr2mercredi) {
    this.s7debcr2mercredi = s7debcr2mercredi;
  }

  public String getS7fincr2mercredi() {
    return s7fincr2mercredi;
  }

  public void setS7fincr2mercredi(String s7fincr2mercredi) {
    this.s7fincr2mercredi = s7fincr2mercredi;
  }

  public String getS7debcr3mercredi() {
    return s7debcr3mercredi;
  }

  public void setS7debcr3mercredi(String s7debcr3mercredi) {
    this.s7debcr3mercredi = s7debcr3mercredi;
  }

  public String getS7fincr3mercredi() {
    return s7fincr3mercredi;
  }

  public void setS7fincr3mercredi(String s7fincr3mercredi) {
    this.s7fincr3mercredi = s7fincr3mercredi;
  }

  public String getS7debcr1jeudi() {
    return s7debcr1jeudi;
  }

  public void setS7debcr1jeudi(String s7debcr1jeudi) {
    this.s7debcr1jeudi = s7debcr1jeudi;
  }

  public String getS7fincr1jeudi() {
    return s7fincr1jeudi;
  }

  public void setS7fincr1jeudi(String s7fincr1jeudi) {
    this.s7fincr1jeudi = s7fincr1jeudi;
  }

  public String getS7debcr2jeudi() {
    return s7debcr2jeudi;
  }

  public void setS7debcr2jeudi(String s7debcr2jeudi) {
    this.s7debcr2jeudi = s7debcr2jeudi;
  }

  public String getS7fincr2jeudi() {
    return s7fincr2jeudi;
  }

  public void setS7fincr2jeudi(String s7fincr2jeudi) {
    this.s7fincr2jeudi = s7fincr2jeudi;
  }

  public String getS7debcr3jeudi() {
    return s7debcr3jeudi;
  }

  public void setS7debcr3jeudi(String s7debcr3jeudi) {
    this.s7debcr3jeudi = s7debcr3jeudi;
  }

  public String getS7fincr3jeudi() {
    return s7fincr3jeudi;
  }

  public void setS7fincr3jeudi(String s7fincr3jeudi) {
    this.s7fincr3jeudi = s7fincr3jeudi;
  }

  public String getS7debcr1vendredi() {
    return s7debcr1vendredi;
  }

  public void setS7debcr1vendredi(String s7debcr1vendredi) {
    this.s7debcr1vendredi = s7debcr1vendredi;
  }

  public String getS7fincr1vendredi() {
    return s7fincr1vendredi;
  }

  public void setS7fincr1vendredi(String s7fincr1vendredi) {
    this.s7fincr1vendredi = s7fincr1vendredi;
  }

  public String getS7debcr2vendredi() {
    return s7debcr2vendredi;
  }

  public void setS7debcr2vendredi(String s7debcr2vendredi) {
    this.s7debcr2vendredi = s7debcr2vendredi;
  }

  public String getS7fincr2vendredi() {
    return s7fincr2vendredi;
  }

  public void setS7fincr2vendredi(String s7fincr2vendredi) {
    this.s7fincr2vendredi = s7fincr2vendredi;
  }

  public String getS7debcr3vendredi() {
    return s7debcr3vendredi;
  }

  public void setS7debcr3vendredi(String s7debcr3vendredi) {
    this.s7debcr3vendredi = s7debcr3vendredi;
  }

  public String getS7fincr3vendredi() {
    return s7fincr3vendredi;
  }

  public void setS7fincr3vendredi(String s7fincr3vendredi) {
    this.s7fincr3vendredi = s7fincr3vendredi;
  }

  public String getS7debcr1samedi() {
    return s7debcr1samedi;
  }

  public void setS7debcr1samedi(String s7debcr1samedi) {
    this.s7debcr1samedi = s7debcr1samedi;
  }

  public String getS7fincr1samedi() {
    return s7fincr1samedi;
  }

  public void setS7fincr1samedi(String s7fincr1samedi) {
    this.s7fincr1samedi = s7fincr1samedi;
  }

  public String getS7debcr2samedi() {
    return s7debcr2samedi;
  }

  public void setS7debcr2samedi(String s7debcr2samedi) {
    this.s7debcr2samedi = s7debcr2samedi;
  }

  public String getS7fincr2samedi() {
    return s7fincr2samedi;
  }

  public void setS7fincr2samedi(String s7fincr2samedi) {
    this.s7fincr2samedi = s7fincr2samedi;
  }

  public String getS7debcr3samedi() {
    return s7debcr3samedi;
  }

  public void setS7debcr3samedi(String s7debcr3samedi) {
    this.s7debcr3samedi = s7debcr3samedi;
  }

  public String getS7fincr3samedi() {
    return s7fincr3samedi;
  }

  public void setS7fincr3samedi(String s7fincr3samedi) {
    this.s7fincr3samedi = s7fincr3samedi;
  }

  public String getS7debcr1dimanche() {
    return s7debcr1dimanche;
  }

  public void setS7debcr1dimanche(String s7debcr1dimanche) {
    this.s7debcr1dimanche = s7debcr1dimanche;
  }

  public String getS7fincr1dimanche() {
    return s7fincr1dimanche;
  }

  public void setS7fincr1dimanche(String s7fincr1dimanche) {
    this.s7fincr1dimanche = s7fincr1dimanche;
  }

  public String getS7debcr2dimanche() {
    return s7debcr2dimanche;
  }

  public void setS7debcr2dimanche(String s7debcr2dimanche) {
    this.s7debcr2dimanche = s7debcr2dimanche;
  }

  public String getS7fincr2dimanche() {
    return s7fincr2dimanche;
  }

  public void setS7fincr2dimanche(String s7fincr2dimanche) {
    this.s7fincr2dimanche = s7fincr2dimanche;
  }

  public String getS7debcr3dimanche() {
    return s7debcr3dimanche;
  }

  public void setS7debcr3dimanche(String s7debcr3dimanche) {
    this.s7debcr3dimanche = s7debcr3dimanche;
  }

  public String getS7fincr3dimanche() {
    return s7fincr3dimanche;
  }

  public void setS7fincr3dimanche(String s7fincr3dimanche) {
    this.s7fincr3dimanche = s7fincr3dimanche;
  }

  public String getS8debcr1lundi() {
    return s8debcr1lundi;
  }

  public void setS8debcr1lundi(String s8debcr1lundi) {
    this.s8debcr1lundi = s8debcr1lundi;
  }

  public String getS8fincr1lundi() {
    return s8fincr1lundi;
  }

  public void setS8fincr1lundi(String s8fincr1lundi) {
    this.s8fincr1lundi = s8fincr1lundi;
  }

  public String getS8debcr2lundi() {
    return s8debcr2lundi;
  }

  public void setS8debcr2lundi(String s8debcr2lundi) {
    this.s8debcr2lundi = s8debcr2lundi;
  }

  public String getS8fincr2lundi() {
    return s8fincr2lundi;
  }

  public void setS8fincr2lundi(String s8fincr2lundi) {
    this.s8fincr2lundi = s8fincr2lundi;
  }

  public String getS8debcr3lundi() {
    return s8debcr3lundi;
  }

  public void setS8debcr3lundi(String s8debcr3lundi) {
    this.s8debcr3lundi = s8debcr3lundi;
  }

  public String getS8fincr3lundi() {
    return s8fincr3lundi;
  }

  public void setS8fincr3lundi(String s8fincr3lundi) {
    this.s8fincr3lundi = s8fincr3lundi;
  }

  public String getS8debcr1mardi() {
    return s8debcr1mardi;
  }

  public void setS8debcr1mardi(String s8debcr1mardi) {
    this.s8debcr1mardi = s8debcr1mardi;
  }

  public String getS8fincr1mardi() {
    return s8fincr1mardi;
  }

  public void setS8fincr1mardi(String s8fincr1mardi) {
    this.s8fincr1mardi = s8fincr1mardi;
  }

  public String getS8debcr2mardi() {
    return s8debcr2mardi;
  }

  public void setS8debcr2mardi(String s8debcr2mardi) {
    this.s8debcr2mardi = s8debcr2mardi;
  }

  public String getS8fincr2mardi() {
    return s8fincr2mardi;
  }

  public void setS8fincr2mardi(String s8fincr2mardi) {
    this.s8fincr2mardi = s8fincr2mardi;
  }

  public String getS8debcr3mardi() {
    return s8debcr3mardi;
  }

  public void setS8debcr3mardi(String s8debcr3mardi) {
    this.s8debcr3mardi = s8debcr3mardi;
  }

  public String getS8fincr3mardi() {
    return s8fincr3mardi;
  }

  public void setS8fincr3mardi(String s8fincr3mardi) {
    this.s8fincr3mardi = s8fincr3mardi;
  }

  public String getS8debcr1mercredi() {
    return s8debcr1mercredi;
  }

  public void setS8debcr1mercredi(String s8debcr1mercredi) {
    this.s8debcr1mercredi = s8debcr1mercredi;
  }

  public String getS8fincr1mercredi() {
    return s8fincr1mercredi;
  }

  public void setS8fincr1mercredi(String s8fincr1mercredi) {
    this.s8fincr1mercredi = s8fincr1mercredi;
  }

  public String getS8debcr2mercredi() {
    return s8debcr2mercredi;
  }

  public void setS8debcr2mercredi(String s8debcr2mercredi) {
    this.s8debcr2mercredi = s8debcr2mercredi;
  }

  public String getS8fincr2mercredi() {
    return s8fincr2mercredi;
  }

  public void setS8fincr2mercredi(String s8fincr2mercredi) {
    this.s8fincr2mercredi = s8fincr2mercredi;
  }

  public String getS8debcr3mercredi() {
    return s8debcr3mercredi;
  }

  public void setS8debcr3mercredi(String s8debcr3mercredi) {
    this.s8debcr3mercredi = s8debcr3mercredi;
  }

  public String getS8fincr3mercredi() {
    return s8fincr3mercredi;
  }

  public void setS8fincr3mercredi(String s8fincr3mercredi) {
    this.s8fincr3mercredi = s8fincr3mercredi;
  }

  public String getS8debcr1jeudi() {
    return s8debcr1jeudi;
  }

  public void setS8debcr1jeudi(String s8debcr1jeudi) {
    this.s8debcr1jeudi = s8debcr1jeudi;
  }

  public String getS8fincr1jeudi() {
    return s8fincr1jeudi;
  }

  public void setS8fincr1jeudi(String s8fincr1jeudi) {
    this.s8fincr1jeudi = s8fincr1jeudi;
  }

  public String getS8debcr2jeudi() {
    return s8debcr2jeudi;
  }

  public void setS8debcr2jeudi(String s8debcr2jeudi) {
    this.s8debcr2jeudi = s8debcr2jeudi;
  }

  public String getS8fincr2jeudi() {
    return s8fincr2jeudi;
  }

  public void setS8fincr2jeudi(String s8fincr2jeudi) {
    this.s8fincr2jeudi = s8fincr2jeudi;
  }

  public String getS8debcr3jeudi() {
    return s8debcr3jeudi;
  }

  public void setS8debcr3jeudi(String s8debcr3jeudi) {
    this.s8debcr3jeudi = s8debcr3jeudi;
  }

  public String getS8fincr3jeudi() {
    return s8fincr3jeudi;
  }

  public void setS8fincr3jeudi(String s8fincr3jeudi) {
    this.s8fincr3jeudi = s8fincr3jeudi;
  }

  public String getS8debcr1vendredi() {
    return s8debcr1vendredi;
  }

  public void setS8debcr1vendredi(String s8debcr1vendredi) {
    this.s8debcr1vendredi = s8debcr1vendredi;
  }

  public String getS8fincr1vendredi() {
    return s8fincr1vendredi;
  }

  public void setS8fincr1vendredi(String s8fincr1vendredi) {
    this.s8fincr1vendredi = s8fincr1vendredi;
  }

  public String getS8debcr2vendredi() {
    return s8debcr2vendredi;
  }

  public void setS8debcr2vendredi(String s8debcr2vendredi) {
    this.s8debcr2vendredi = s8debcr2vendredi;
  }

  public String getS8fincr2vendredi() {
    return s8fincr2vendredi;
  }

  public void setS8fincr2vendredi(String s8fincr2vendredi) {
    this.s8fincr2vendredi = s8fincr2vendredi;
  }

  public String getS8debcr3vendredi() {
    return s8debcr3vendredi;
  }

  public void setS8debcr3vendredi(String s8debcr3vendredi) {
    this.s8debcr3vendredi = s8debcr3vendredi;
  }

  public String getS8fincr3vendredi() {
    return s8fincr3vendredi;
  }

  public void setS8fincr3vendredi(String s8fincr3vendredi) {
    this.s8fincr3vendredi = s8fincr3vendredi;
  }

  public String getS8debcr1samedi() {
    return s8debcr1samedi;
  }

  public void setS8debcr1samedi(String s8debcr1samedi) {
    this.s8debcr1samedi = s8debcr1samedi;
  }

  public String getS8fincr1samedi() {
    return s8fincr1samedi;
  }

  public void setS8fincr1samedi(String s8fincr1samedi) {
    this.s8fincr1samedi = s8fincr1samedi;
  }

  public String getS8debcr2samedi() {
    return s8debcr2samedi;
  }

  public void setS8debcr2samedi(String s8debcr2samedi) {
    this.s8debcr2samedi = s8debcr2samedi;
  }

  public String getS8fincr2samedi() {
    return s8fincr2samedi;
  }

  public void setS8fincr2samedi(String s8fincr2samedi) {
    this.s8fincr2samedi = s8fincr2samedi;
  }

  public String getS8debcr3samedi() {
    return s8debcr3samedi;
  }

  public void setS8debcr3samedi(String s8debcr3samedi) {
    this.s8debcr3samedi = s8debcr3samedi;
  }

  public String getS8fincr3samedi() {
    return s8fincr3samedi;
  }

  public void setS8fincr3samedi(String s8fincr3samedi) {
    this.s8fincr3samedi = s8fincr3samedi;
  }

  public String getS8debcr1dimanche() {
    return s8debcr1dimanche;
  }

  public void setS8debcr1dimanche(String s8debcr1dimanche) {
    this.s8debcr1dimanche = s8debcr1dimanche;
  }

  public String getS8fincr1dimanche() {
    return s8fincr1dimanche;
  }

  public void setS8fincr1dimanche(String s8fincr1dimanche) {
    this.s8fincr1dimanche = s8fincr1dimanche;
  }

  public String getS8debcr2dimanche() {
    return s8debcr2dimanche;
  }

  public void setS8debcr2dimanche(String s8debcr2dimanche) {
    this.s8debcr2dimanche = s8debcr2dimanche;
  }

  public String getS8fincr2dimanche() {
    return s8fincr2dimanche;
  }

  public void setS8fincr2dimanche(String s8fincr2dimanche) {
    this.s8fincr2dimanche = s8fincr2dimanche;
  }

  public String getS8debcr3dimanche() {
    return s8debcr3dimanche;
  }

  public void setS8debcr3dimanche(String s8debcr3dimanche) {
    this.s8debcr3dimanche = s8debcr3dimanche;
  }

  public String getS8fincr3dimanche() {
    return s8fincr3dimanche;
  }

  public void setS8fincr3dimanche(String s8fincr3dimanche) {
    this.s8fincr3dimanche = s8fincr3dimanche;
  }
    
  public String getS9debcr1lundi() {
    return s9debcr1lundi;
  }

  public void setS9debcr1lundi(String s9debcr1lundi) {
    this.s9debcr1lundi = s9debcr1lundi;
  }

  public String getS9fincr1lundi() {
    return s9fincr1lundi;
  }

  public void setS9fincr1lundi(String s9fincr1lundi) {
    this.s9fincr1lundi = s9fincr1lundi;
  }

  public String getS9debcr2lundi() {
    return s9debcr2lundi;
  }

  public void setS9debcr2lundi(String s9debcr2lundi) {
    this.s9debcr2lundi = s9debcr2lundi;
  }

  public String getS9fincr2lundi() {
    return s9fincr2lundi;
  }

  public void setS9fincr2lundi(String s9fincr2lundi) {
    this.s9fincr2lundi = s9fincr2lundi;
  }

  public String getS9debcr3lundi() {
    return s9debcr3lundi;
  }

  public void setS9debcr3lundi(String s9debcr3lundi) {
    this.s9debcr3lundi = s9debcr3lundi;
  }

  public String getS9fincr3lundi() {
    return s9fincr3lundi;
  }

  public void setS9fincr3lundi(String s9fincr3lundi) {
    this.s9fincr3lundi = s9fincr3lundi;
  }

  public String getS9debcr1mardi() {
    return s9debcr1mardi;
  }

  public void setS9debcr1mardi(String s9debcr1mardi) {
    this.s9debcr1mardi = s9debcr1mardi;
  }

  public String getS9fincr1mardi() {
    return s9fincr1mardi;
  }

  public void setS9fincr1mardi(String s9fincr1mardi) {
    this.s9fincr1mardi = s9fincr1mardi;
  }

  public String getS9debcr2mardi() {
    return s9debcr2mardi;
  }

  public void setS9debcr2mardi(String s9debcr2mardi) {
    this.s9debcr2mardi = s9debcr2mardi;
  }

  public String getS9fincr2mardi() {
    return s9fincr2mardi;
  }

  public void setS9fincr2mardi(String s9fincr2mardi) {
    this.s9fincr2mardi = s9fincr2mardi;
  }

  public String getS9debcr3mardi() {
    return s9debcr3mardi;
  }

  public void setS9debcr3mardi(String s9debcr3mardi) {
    this.s9debcr3mardi = s9debcr3mardi;
  }

  public String getS9fincr3mardi() {
    return s9fincr3mardi;
  }

  public void setS9fincr3mardi(String s9fincr3mardi) {
    this.s9fincr3mardi = s9fincr3mardi;
  }

  public String getS9debcr1mercredi() {
    return s9debcr1mercredi;
  }

  public void setS9debcr1mercredi(String s9debcr1mercredi) {
    this.s9debcr1mercredi = s9debcr1mercredi;
  }

  public String getS9fincr1mercredi() {
    return s9fincr1mercredi;
  }

  public void setS9fincr1mercredi(String s9fincr1mercredi) {
    this.s9fincr1mercredi = s9fincr1mercredi;
  }

  public String getS9debcr2mercredi() {
    return s9debcr2mercredi;
  }

  public void setS9debcr2mercredi(String s9debcr2mercredi) {
    this.s9debcr2mercredi = s9debcr2mercredi;
  }

  public String getS9fincr2mercredi() {
    return s9fincr2mercredi;
  }

  public void setS9fincr2mercredi(String s9fincr2mercredi) {
    this.s9fincr2mercredi = s9fincr2mercredi;
  }

  public String getS9debcr3mercredi() {
    return s9debcr3mercredi;
  }

  public void setS9debcr3mercredi(String s9debcr3mercredi) {
    this.s9debcr3mercredi = s9debcr3mercredi;
  }

  public String getS9fincr3mercredi() {
    return s9fincr3mercredi;
  }

  public void setS9fincr3mercredi(String s9fincr3mercredi) {
    this.s9fincr3mercredi = s9fincr3mercredi;
  }

  public String getS9debcr1jeudi() {
    return s9debcr1jeudi;
  }

  public void setS9debcr1jeudi(String s9debcr1jeudi) {
    this.s9debcr1jeudi = s9debcr1jeudi;
  }

  public String getS9fincr1jeudi() {
    return s9fincr1jeudi;
  }

  public void setS9fincr1jeudi(String s9fincr1jeudi) {
    this.s9fincr1jeudi = s9fincr1jeudi;
  }

  public String getS9debcr2jeudi() {
    return s9debcr2jeudi;
  }

  public void setS9debcr2jeudi(String s9debcr2jeudi) {
    this.s9debcr2jeudi = s9debcr2jeudi;
  }

  public String getS9fincr2jeudi() {
    return s9fincr2jeudi;
  }

  public void setS9fincr2jeudi(String s9fincr2jeudi) {
    this.s9fincr2jeudi = s9fincr2jeudi;
  }

  public String getS9debcr3jeudi() {
    return s9debcr3jeudi;
  }

  public void setS9debcr3jeudi(String s9debcr3jeudi) {
    this.s9debcr3jeudi = s9debcr3jeudi;
  }

  public String getS9fincr3jeudi() {
    return s9fincr3jeudi;
  }

  public void setS9fincr3jeudi(String s9fincr3jeudi) {
    this.s9fincr3jeudi = s9fincr3jeudi;
  }

  public String getS9debcr1vendredi() {
    return s9debcr1vendredi;
  }

  public void setS9debcr1vendredi(String s9debcr1vendredi) {
    this.s9debcr1vendredi = s9debcr1vendredi;
  }

  public String getS9fincr1vendredi() {
    return s9fincr1vendredi;
  }

  public void setS9fincr1vendredi(String s9fincr1vendredi) {
    this.s9fincr1vendredi = s9fincr1vendredi;
  }

  public String getS9debcr2vendredi() {
    return s9debcr2vendredi;
  }

  public void setS9debcr2vendredi(String s9debcr2vendredi) {
    this.s9debcr2vendredi = s9debcr2vendredi;
  }

  public String getS9fincr2vendredi() {
    return s9fincr2vendredi;
  }

  public void setS9fincr2vendredi(String s9fincr2vendredi) {
    this.s9fincr2vendredi = s9fincr2vendredi;
  }

  public String getS9debcr3vendredi() {
    return s9debcr3vendredi;
  }

  public void setS9debcr3vendredi(String s9debcr3vendredi) {
    this.s9debcr3vendredi = s9debcr3vendredi;
  }

  public String getS9fincr3vendredi() {
    return s9fincr3vendredi;
  }

  public void setS9fincr3vendredi(String s9fincr3vendredi) {
    this.s9fincr3vendredi = s9fincr3vendredi;
  }

  public String getS9debcr1samedi() {
    return s9debcr1samedi;
  }

  public void setS9debcr1samedi(String s9debcr1samedi) {
    this.s9debcr1samedi = s9debcr1samedi;
  }

  public String getS9fincr1samedi() {
    return s9fincr1samedi;
  }

  public void setS9fincr1samedi(String s9fincr1samedi) {
    this.s9fincr1samedi = s9fincr1samedi;
  }

  public String getS9debcr2samedi() {
    return s9debcr2samedi;
  }

  public void setS9debcr2samedi(String s9debcr2samedi) {
    this.s9debcr2samedi = s9debcr2samedi;
  }

  public String getS9fincr2samedi() {
    return s9fincr2samedi;
  }

  public void setS9fincr2samedi(String s9fincr2samedi) {
    this.s9fincr2samedi = s9fincr2samedi;
  }

  public String getS9debcr3samedi() {
    return s9debcr3samedi;
  }

  public void setS9debcr3samedi(String s9debcr3samedi) {
    this.s9debcr3samedi = s9debcr3samedi;
  }

  public String getS9fincr3samedi() {
    return s9fincr3samedi;
  }

  public void setS9fincr3samedi(String s9fincr3samedi) {
    this.s9fincr3samedi = s9fincr3samedi;
  }

  public String getS9debcr1dimanche() {
    return s9debcr1dimanche;
  }

  public void setS9debcr1dimanche(String s9debcr1dimanche) {
    this.s9debcr1dimanche = s9debcr1dimanche;
  }

  public String getS9fincr1dimanche() {
    return s9fincr1dimanche;
  }

  public void setS9fincr1dimanche(String s9fincr1dimanche) {
    this.s9fincr1dimanche = s9fincr1dimanche;
  }

  public String getS9debcr2dimanche() {
    return s9debcr2dimanche;
  }

  public void setS9debcr2dimanche(String s9debcr2dimanche) {
    this.s9debcr2dimanche = s9debcr2dimanche;
  }

  public String getS9fincr2dimanche() {
    return s9fincr2dimanche;
  }

  public void setS9fincr2dimanche(String s9fincr2dimanche) {
    this.s9fincr2dimanche = s9fincr2dimanche;
  }

  public String getS9debcr3dimanche() {
    return s9debcr3dimanche;
  }

  public void setS9debcr3dimanche(String s9debcr3dimanche) {
    this.s9debcr3dimanche = s9debcr3dimanche;
  }

  public String getS9fincr3dimanche() {
    return s9fincr3dimanche;
  }

  public void setS9fincr3dimanche(String s9fincr3dimanche) {
    this.s9fincr3dimanche = s9fincr3dimanche;
  }

  public String getS10debcr1lundi() {
    return s10debcr1lundi;
  }

  public void setS10debcr1lundi(String s10debcr1lundi) {
    this.s10debcr1lundi = s10debcr1lundi;
  }

  public String getS10fincr1lundi() {
    return s10fincr1lundi;
  }

  public void setS10fincr1lundi(String s10fincr1lundi) {
    this.s10fincr1lundi = s10fincr1lundi;
  }

  public String getS10debcr2lundi() {
    return s10debcr2lundi;
  }

  public void setS10debcr2lundi(String s10debcr2lundi) {
    this.s10debcr2lundi = s10debcr2lundi;
  }

  public String getS10fincr2lundi() {
    return s10fincr2lundi;
  }

  public void setS10fincr2lundi(String s10fincr2lundi) {
    this.s10fincr2lundi = s10fincr2lundi;
  }

  public String getS10debcr3lundi() {
    return s10debcr3lundi;
  }

  public void setS10debcr3lundi(String s10debcr3lundi) {
    this.s10debcr3lundi = s10debcr3lundi;
  }

  public String getS10fincr3lundi() {
    return s10fincr3lundi;
  }

  public void setS10fincr3lundi(String s10fincr3lundi) {
    this.s10fincr3lundi = s10fincr3lundi;
  }

  public String getS10debcr1mardi() {
    return s10debcr1mardi;
  }

  public void setS10debcr1mardi(String s10debcr1mardi) {
    this.s10debcr1mardi = s10debcr1mardi;
  }

  public String getS10fincr1mardi() {
    return s10fincr1mardi;
  }

  public void setS10fincr1mardi(String s10fincr1mardi) {
    this.s10fincr1mardi = s10fincr1mardi;
  }

  public String getS10debcr2mardi() {
    return s10debcr2mardi;
  }

  public void setS10debcr2mardi(String s10debcr2mardi) {
    this.s10debcr2mardi = s10debcr2mardi;
  }

  public String getS10fincr2mardi() {
    return s10fincr2mardi;
  }

  public void setS10fincr2mardi(String s10fincr2mardi) {
    this.s10fincr2mardi = s10fincr2mardi;
  }

  public String getS10debcr3mardi() {
    return s10debcr3mardi;
  }

  public void setS10debcr3mardi(String s10debcr3mardi) {
    this.s10debcr3mardi = s10debcr3mardi;
  }

  public String getS10fincr3mardi() {
    return s10fincr3mardi;
  }

  public void setS10fincr3mardi(String s10fincr3mardi) {
    this.s10fincr3mardi = s10fincr3mardi;
  }

  public String getS10debcr1mercredi() {
    return s10debcr1mercredi;
  }

  public void setS10debcr1mercredi(String s10debcr1mercredi) {
    this.s10debcr1mercredi = s10debcr1mercredi;
  }

  public String getS10fincr1mercredi() {
    return s10fincr1mercredi;
  }

  public void setS10fincr1mercredi(String s10fincr1mercredi) {
    this.s10fincr1mercredi = s10fincr1mercredi;
  }

  public String getS10debcr2mercredi() {
    return s10debcr2mercredi;
  }

  public void setS10debcr2mercredi(String s10debcr2mercredi) {
    this.s10debcr2mercredi = s10debcr2mercredi;
  }

  public String getS10fincr2mercredi() {
    return s10fincr2mercredi;
  }

  public void setS10fincr2mercredi(String s10fincr2mercredi) {
    this.s10fincr2mercredi = s10fincr2mercredi;
  }

  public String getS10debcr3mercredi() {
    return s10debcr3mercredi;
  }

  public void setS10debcr3mercredi(String s10debcr3mercredi) {
    this.s10debcr3mercredi = s10debcr3mercredi;
  }

  public String getS10fincr3mercredi() {
    return s10fincr3mercredi;
  }

  public void setS10fincr3mercredi(String s10fincr3mercredi) {
    this.s10fincr3mercredi = s10fincr3mercredi;
  }

  public String getS10debcr1jeudi() {
    return s10debcr1jeudi;
  }

  public void setS10debcr1jeudi(String s10debcr1jeudi) {
    this.s10debcr1jeudi = s10debcr1jeudi;
  }

  public String getS10fincr1jeudi() {
    return s10fincr1jeudi;
  }

  public void setS10fincr1jeudi(String s10fincr1jeudi) {
    this.s10fincr1jeudi = s10fincr1jeudi;
  }

  public String getS10debcr2jeudi() {
    return s10debcr2jeudi;
  }

  public void setS10debcr2jeudi(String s10debcr2jeudi) {
    this.s10debcr2jeudi = s10debcr2jeudi;
  }

  public String getS10fincr2jeudi() {
    return s10fincr2jeudi;
  }

  public void setS10fincr2jeudi(String s10fincr2jeudi) {
    this.s10fincr2jeudi = s10fincr2jeudi;
  }

  public String getS10debcr3jeudi() {
    return s10debcr3jeudi;
  }

  public void setS10debcr3jeudi(String s10debcr3jeudi) {
    this.s10debcr3jeudi = s10debcr3jeudi;
  }

  public String getS10fincr3jeudi() {
    return s10fincr3jeudi;
  }

  public void setS10fincr3jeudi(String s10fincr3jeudi) {
    this.s10fincr3jeudi = s10fincr3jeudi;
  }

  public String getS10debcr1vendredi() {
    return s10debcr1vendredi;
  }

  public void setS10debcr1vendredi(String s10debcr1vendredi) {
    this.s10debcr1vendredi = s10debcr1vendredi;
  }

  public String getS10fincr1vendredi() {
    return s10fincr1vendredi;
  }

  public void setS10fincr1vendredi(String s10fincr1vendredi) {
    this.s10fincr1vendredi = s10fincr1vendredi;
  }

  public String getS10debcr2vendredi() {
    return s10debcr2vendredi;
  }

  public void setS10debcr2vendredi(String s10debcr2vendredi) {
    this.s10debcr2vendredi = s10debcr2vendredi;
  }

  public String getS10fincr2vendredi() {
    return s10fincr2vendredi;
  }

  public void setS10fincr2vendredi(String s10fincr2vendredi) {
    this.s10fincr2vendredi = s10fincr2vendredi;
  }

  public String getS10debcr3vendredi() {
    return s10debcr3vendredi;
  }

  public void setS10debcr3vendredi(String s10debcr3vendredi) {
    this.s10debcr3vendredi = s10debcr3vendredi;
  }

  public String getS10fincr3vendredi() {
    return s10fincr3vendredi;
  }

  public void setS10fincr3vendredi(String s10fincr3vendredi) {
    this.s10fincr3vendredi = s10fincr3vendredi;
  }

  public String getS10debcr1samedi() {
    return s10debcr1samedi;
  }

  public void setS10debcr1samedi(String s10debcr1samedi) {
    this.s10debcr1samedi = s10debcr1samedi;
  }

  public String getS10fincr1samedi() {
    return s10fincr1samedi;
  }

  public void setS10fincr1samedi(String s10fincr1samedi) {
    this.s10fincr1samedi = s10fincr1samedi;
  }

  public String getS10debcr2samedi() {
    return s10debcr2samedi;
  }

  public void setS10debcr2samedi(String s10debcr2samedi) {
    this.s10debcr2samedi = s10debcr2samedi;
  }

  public String getS10fincr2samedi() {
    return s10fincr2samedi;
  }

  public void setS10fincr2samedi(String s10fincr2samedi) {
    this.s10fincr2samedi = s10fincr2samedi;
  }

  public String getS10debcr3samedi() {
    return s10debcr3samedi;
  }

  public void setS10debcr3samedi(String s10debcr3samedi) {
    this.s10debcr3samedi = s10debcr3samedi;
  }

  public String getS10fincr3samedi() {
    return s10fincr3samedi;
  }

  public void setS10fincr3samedi(String s10fincr3samedi) {
    this.s10fincr3samedi = s10fincr3samedi;
  }

  public String getS10debcr1dimanche() {
    return s10debcr1dimanche;
  }

  public void setS10debcr1dimanche(String s10debcr1dimanche) {
    this.s10debcr1dimanche = s10debcr1dimanche;
  }

  public String getS10fincr1dimanche() {
    return s10fincr1dimanche;
  }

  public void setS10fincr1dimanche(String s10fincr1dimanche) {
    this.s10fincr1dimanche = s10fincr1dimanche;
  }

  public String getS10debcr2dimanche() {
    return s10debcr2dimanche;
  }

  public void setS10debcr2dimanche(String s10debcr2dimanche) {
    this.s10debcr2dimanche = s10debcr2dimanche;
  }

  public String getS10fincr2dimanche() {
    return s10fincr2dimanche;
  }

  public void setS10fincr2dimanche(String s10fincr2dimanche) {
    this.s10fincr2dimanche = s10fincr2dimanche;
  }

  public String getS10debcr3dimanche() {
    return s10debcr3dimanche;
  }

  public void setS10debcr3dimanche(String s10debcr3dimanche) {
    this.s10debcr3dimanche = s10debcr3dimanche;
  }

  public String getS10fincr3dimanche() {
    return s10fincr3dimanche;
  }

  public void setS10fincr3dimanche(String s10fincr3dimanche) {
    this.s10fincr3dimanche = s10fincr3dimanche;
  }

  public Boolean getS1Lundi() {
    return s1Lundi;
  }

  public void setS1Lundi(Boolean s1Lundi) {
    this.s1Lundi = s1Lundi;
  }

  public Boolean getS1Mardi() {
    return s1Mardi;
  }

  public void setS1Mardi(Boolean s1Mardi) {
    this.s1Mardi = s1Mardi;
  }

  public Boolean getS1Mercredi() {
    return s1Mercredi;
  }

  public void setS1Mercredi(Boolean s1Mercredi) {
    this.s1Mercredi = s1Mercredi;
  }

  public Boolean getS1Jeudi() {
    return s1Jeudi;
  }

  public void setS1Jeudi(Boolean s1Jeudi) {
    this.s1Jeudi = s1Jeudi;
  }

  public Boolean getS1Vendredi() {
    return s1Vendredi;
  }

  public void setS1Vendredi(Boolean s1Vendredi) {
    this.s1Vendredi = s1Vendredi;
  }

  public Boolean getS1Samedi() {
    return s1Samedi;
  }

  public void setS1Samedi(Boolean s1Samedi) {
    this.s1Samedi = s1Samedi;
  }

  public Boolean getS1Dimanche() {
    return s1Dimanche;
  }

  public void setS1Dimanche(Boolean s1Dimanche) {
    this.s1Dimanche = s1Dimanche;
  }

  public Boolean getS2Lundi() {
    return s2Lundi;
  }

  public void setS2Lundi(Boolean s2Lundi) {
    this.s2Lundi = s2Lundi;
  }

  public Boolean getS2Mardi() {
    return s2Mardi;
  }

  public void setS2Mardi(Boolean s2Mardi) {
    this.s2Mardi = s2Mardi;
  }

  public Boolean getS2Mercredi() {
    return s2Mercredi;
  }

  public void setS2Mercredi(Boolean s2Mercredi) {
    this.s2Mercredi = s2Mercredi;
  }

  public Boolean getS2Jeudi() {
    return s2Jeudi;
  }

  public void setS2Jeudi(Boolean s2Jeudi) {
    this.s2Jeudi = s2Jeudi;
  }

  public Boolean getS2Vendredi() {
    return s2Vendredi;
  }

  public void setS2Vendredi(Boolean s2Vendredi) {
    this.s2Vendredi = s2Vendredi;
  }

  public Boolean getS2Samedi() {
    return s2Samedi;
  }

  public void setS2Samedi(Boolean s2Samedi) {
    this.s2Samedi = s2Samedi;
  }

  public Boolean getS2Dimanche() {
    return s2Dimanche;
  }

  public void setS2Dimanche(Boolean s2Dimanche) {
    this.s2Dimanche = s2Dimanche;
  }

  public Boolean getS3Lundi() {
    return s3Lundi;
  }

  public void setS3Lundi(Boolean s3Lundi) {
    this.s3Lundi = s3Lundi;
  }

  public Boolean getS3Mardi() {
    return s3Mardi;
  }

  public void setS3Mardi(Boolean s3Mardi) {
    this.s3Mardi = s3Mardi;
  }

  public Boolean getS3Mercredi() {
    return s3Mercredi;
  }

  public void setS3Mercredi(Boolean s3Mercredi) {
    this.s3Mercredi = s3Mercredi;
  }

  public Boolean getS3Jeudi() {
    return s3Jeudi;
  }

  public void setS3Jeudi(Boolean s3Jeudi) {
    this.s3Jeudi = s3Jeudi;
  }

  public Boolean getS3Vendredi() {
    return s3Vendredi;
  }

  public void setS3Vendredi(Boolean s3Vendredi) {
    this.s3Vendredi = s3Vendredi;
  }

  public Boolean getS3Samedi() {
    return s3Samedi;
  }

  public void setS3Samedi(Boolean s3Samedi) {
    this.s3Samedi = s3Samedi;
  }

  public Boolean getS3Dimanche() {
    return s3Dimanche;
  }

  public void setS3Dimanche(Boolean s3Dimanche) {
    this.s3Dimanche = s3Dimanche;
  }

  public Boolean getS4Lundi() {
    return s4Lundi;
  }

  public void setS4Lundi(Boolean s4Lundi) {
    this.s4Lundi = s4Lundi;
  }

  public Boolean getS4Mardi() {
    return s4Mardi;
  }

  public void setS4Mardi(Boolean s4Mardi) {
    this.s4Mardi = s4Mardi;
  }

  public Boolean getS4Mercredi() {
    return s4Mercredi;
  }

  public void setS4Mercredi(Boolean s4Mercredi) {
    this.s4Mercredi = s4Mercredi;
  }

  public Boolean getS4Jeudi() {
    return s4Jeudi;
  }

  public void setS4Jeudi(Boolean s4Jeudi) {
    this.s4Jeudi = s4Jeudi;
  }

  public Boolean getS4Vendredi() {
    return s4Vendredi;
  }

  public void setS4Vendredi(Boolean s4Vendredi) {
    this.s4Vendredi = s4Vendredi;
  }

  public Boolean getS4Samedi() {
    return s4Samedi;
  }

  public void setS4Samedi(Boolean s4Samedi) {
    this.s4Samedi = s4Samedi;
  }

  public Boolean getS4Dimanche() {
    return s4Dimanche;
  }

  public void setS4Dimanche(Boolean s4Dimanche) {
    this.s4Dimanche = s4Dimanche;
  }

  public Boolean getS5Lundi() {
    return s5Lundi;
  }

  public void setS5Lundi(Boolean s5Lundi) {
    this.s5Lundi = s5Lundi;
  }

  public Boolean getS5Mardi() {
    return s5Mardi;
  }

  public void setS5Mardi(Boolean s5Mardi) {
    this.s5Mardi = s5Mardi;
  }

  public Boolean getS5Mercredi() {
    return s5Mercredi;
  }

  public void setS5Mercredi(Boolean s5Mercredi) {
    this.s5Mercredi = s5Mercredi;
  }

  public Boolean getS5Jeudi() {
    return s5Jeudi;
  }

  public void setS5Jeudi(Boolean s5Jeudi) {
    this.s5Jeudi = s5Jeudi;
  }

  public Boolean getS5Vendredi() {
    return s5Vendredi;
  }

  public void setS5Vendredi(Boolean s5Vendredi) {
    this.s5Vendredi = s5Vendredi;
  }

  public Boolean getS5Samedi() {
    return s5Samedi;
  }

  public void setS5Samedi(Boolean s5Samedi) {
    this.s5Samedi = s5Samedi;
  }

  public Boolean getS5Dimanche() {
    return s5Dimanche;
  }

  public void setS5Dimanche(Boolean s5Dimanche) {
    this.s5Dimanche = s5Dimanche;
  }

  public Boolean getS6Lundi() {
    return s6Lundi;
  }

  public void setS6Lundi(Boolean s6Lundi) {
    this.s6Lundi = s6Lundi;
  }

  public Boolean getS6Mardi() {
    return s6Mardi;
  }

  public void setS6Mardi(Boolean s6Mardi) {
    this.s6Mardi = s6Mardi;
  }

  public Boolean getS6Mercredi() {
    return s6Mercredi;
  }

  public void setS6Mercredi(Boolean s6Mercredi) {
    this.s6Mercredi = s6Mercredi;
  }

  public Boolean getS6Jeudi() {
    return s6Jeudi;
  }

  public void setS6Jeudi(Boolean s6Jeudi) {
    this.s6Jeudi = s6Jeudi;
  }

  public Boolean getS6Vendredi() {
    return s6Vendredi;
  }

  public void setS6Vendredi(Boolean s6Vendredi) {
    this.s6Vendredi = s6Vendredi;
  }

  public Boolean getS6Samedi() {
    return s6Samedi;
  }

  public void setS6Samedi(Boolean s6Samedi) {
    this.s6Samedi = s6Samedi;
  }

  public Boolean getS6Dimanche() {
    return s6Dimanche;
  }

  public void setS6Dimanche(Boolean s6Dimanche) {
    this.s6Dimanche = s6Dimanche;
  }

  public Boolean getS7Lundi() {
    return s7Lundi;
  }

  public void setS7Lundi(Boolean s7Lundi) {
    this.s7Lundi = s7Lundi;
  }

  public Boolean getS7Mardi() {
    return s7Mardi;
  }

  public void setS7Mardi(Boolean s7Mardi) {
    this.s7Mardi = s7Mardi;
  }

  public Boolean getS7Mercredi() {
    return s7Mercredi;
  }

  public void setS7Mercredi(Boolean s7Mercredi) {
    this.s7Mercredi = s7Mercredi;
  }

  public Boolean getS7Jeudi() {
    return s7Jeudi;
  }

  public void setS7Jeudi(Boolean s7Jeudi) {
    this.s7Jeudi = s7Jeudi;
  }

  public Boolean getS7Vendredi() {
    return s7Vendredi;
  }

  public void setS7Vendredi(Boolean s7Vendredi) {
    this.s7Vendredi = s7Vendredi;
  }

  public Boolean getS7Samedi() {
    return s7Samedi;
  }

  public void setS7Samedi(Boolean s7Samedi) {
    this.s7Samedi = s7Samedi;
  }

  public Boolean getS7Dimanche() {
    return s7Dimanche;
  }

  public void setS7Dimanche(Boolean s7Dimanche) {
    this.s7Dimanche = s7Dimanche;
  }

  public Boolean getS8Lundi() {
    return s8Lundi;
  }

  public void setS8Lundi(Boolean s8Lundi) {
    this.s8Lundi = s8Lundi;
  }

  public Boolean getS8Mardi() {
    return s8Mardi;
  }

  public void setS8Mardi(Boolean s8Mardi) {
    this.s8Mardi = s8Mardi;
  }

  public Boolean getS8Mercredi() {
    return s8Mercredi;
  }

  public void setS8Mercredi(Boolean s8Mercredi) {
    this.s8Mercredi = s8Mercredi;
  }

  public Boolean getS8Jeudi() {
    return s8Jeudi;
  }

  public void setS8Jeudi(Boolean s8Jeudi) {
    this.s8Jeudi = s8Jeudi;
  }

  public Boolean getS8Vendredi() {
    return s8Vendredi;
  }

  public void setS8Vendredi(Boolean s8Vendredi) {
    this.s8Vendredi = s8Vendredi;
  }

  public Boolean getS8Samedi() {
    return s8Samedi;
  }

  public void setS8Samedi(Boolean s8Samedi) {
    this.s8Samedi = s8Samedi;
  }

  public Boolean getS8Dimanche() {
    return s8Dimanche;
  }

  public void setS8Dimanche(Boolean s8Dimanche) {
    this.s8Dimanche = s8Dimanche;
  }

  public Boolean getS9Lundi() {
    return s9Lundi;
  }

  public void setS9Lundi(Boolean s9Lundi) {
    this.s9Lundi = s9Lundi;
  }

  public Boolean getS9Mardi() {
    return s9Mardi;
  }

  public void setS9Mardi(Boolean s9Mardi) {
    this.s9Mardi = s9Mardi;
  }

  public Boolean getS9Mercredi() {
    return s9Mercredi;
  }

  public void setS9Mercredi(Boolean s9Mercredi) {
    this.s9Mercredi = s9Mercredi;
  }

  public Boolean getS9Jeudi() {
    return s9Jeudi;
  }

  public void setS9Jeudi(Boolean s9Jeudi) {
    this.s9Jeudi = s9Jeudi;
  }

  public Boolean getS9Vendredi() {
    return s9Vendredi;
  }

  public void setS9Vendredi(Boolean s9Vendredi) {
    this.s9Vendredi = s9Vendredi;
  }

  public Boolean getS9Samedi() {
    return s9Samedi;
  }

  public void setS9Samedi(Boolean s9Samedi) {
    this.s9Samedi = s9Samedi;
  }

  public Boolean getS9Dimanche() {
    return s9Dimanche;
  }

  public void setS9Dimanche(Boolean s9Dimanche) {
    this.s9Dimanche = s9Dimanche;
  }

  public Boolean getS10Lundi() {
    return s10Lundi;
  }

  public void setS10Lundi(Boolean s10Lundi) {
    this.s10Lundi = s10Lundi;
  }

  public Boolean getS10Mardi() {
    return s10Mardi;
  }

  public void setS10Mardi(Boolean s10Mardi) {
    this.s10Mardi = s10Mardi;
  }

  public Boolean getS10Mercredi() {
    return s10Mercredi;
  }

  public void setS10Mercredi(Boolean s10Mercredi) {
    this.s10Mercredi = s10Mercredi;
  }

  public Boolean getS10Jeudi() {
    return s10Jeudi;
  }

  public void setS10Jeudi(Boolean s10Jeudi) {
    this.s10Jeudi = s10Jeudi;
  }

  public Boolean getS10Vendredi() {
    return s10Vendredi;
  }

  public void setS10Vendredi(Boolean s10Vendredi) {
    this.s10Vendredi = s10Vendredi;
  }

  public Boolean getS10Samedi() {
    return s10Samedi;
  }

  public void setS10Samedi(Boolean s10Samedi) {
    this.s10Samedi = s10Samedi;
  }

  public Boolean getS10Dimanche() {
    return s10Dimanche;
  }

  public void setS10Dimanche(Boolean s10Dimanche) {
    this.s10Dimanche = s10Dimanche;
  }
  
  public String getLibelle1() {
    return libelle1;
  }

  public void setLibelle1(String libelle1) {
    this.libelle1 = libelle1;
  }

  public String getLibelle2() {
    return libelle2;
  }

  public void setLibelle2(String libelle2) {
    this.libelle2 = libelle2;
  }

  public String getLibelle3() {
    return libelle3;
  }

  public void setLibelle3(String libelle3) {
    this.libelle3 = libelle3;
  }

  public String getLibelle4() {
    return libelle4;
  }

  public void setLibelle4(String libelle4) {
    this.libelle4 = libelle4;
  }

  public String getLibelle5() {
    return libelle5;
  }

  public void setLibelle5(String libelle5) {
    this.libelle5 = libelle5;
  }

  public String getLibelle6() {
    return libelle6;
  }

  public void setLibelle6(String libelle6) {
    this.libelle6 = libelle6;
  }

  public String getLibelle7() {
    return libelle7;
  }

  public void setLibelle7(String libelle7) {
    this.libelle7 = libelle7;
  }

  public String getLibelle8() {
    return libelle8;
  }

  public void setLibelle8(String libelle8) {
    this.libelle8 = libelle8;
  }

  public String getLibelle9() {
    return libelle9;
  }

  public void setLibelle9(String libelle9) {
    this.libelle9 = libelle9;
  }

  public String getLibelle10() {
    return libelle10;
  }

  public void setLibelle10(String libelle10) {
    this.libelle10 = libelle10;
  }

  /**
   * Indique si la déclaration à un accueil pour le jour pour une semaine (avec le numéro de la semaine)
   * @param declaration
   * @param jour
   * @return
   */
  public static boolean hasJourSemaine(DeclarationAccueilDTO declaration, String jour, int numSemaine) {       
    if(declaration == null || declaration.getSemaineTypes() == null || declaration.getSemaineTypes().size() < numSemaine) {
      return false;
    }   
    SemaineTypeDTO semaine = declaration.getSemaineTypes().get(numSemaine-1);
    if(Util.notEmpty(semaine)) {
      List<CreneauDTO> creneauxList = semaine.getCreneaux();
      if(Util.notEmpty(creneauxList)) {
        for(CreneauDTO itCreneau : creneauxList) {
          if(jour.equalsIgnoreCase(itCreneau.getJourSemaine())) {
            return true;
          }
        }
      }
    }
    return false;    
  }
  
  /**
   * Retourne une map de jour avec pour chaque jour une liste de creneau horraire (date debut et date fin)
   * @param declaration
   * @return
   */
  public static Map<String, ArrayList<CreneauHorraire>> getCreneauHorraireJour(DeclarationAccueilDTO declaration, int numSemaine) {
    Map<String, ArrayList<CreneauHorraire>>  horraireMap = new HashMap<String, ArrayList<CreneauHorraire>>();   
    
    if(declaration == null || declaration.getSemaineTypes() == null || declaration.getSemaineTypes().size() < numSemaine) {
      return horraireMap;
    }
    
    SemaineTypeDTO semaine = declaration.getSemaineTypes().get(numSemaine-1);   
    if(Util.notEmpty(semaine)) {
      for(CreneauDTO itCreneau : semaine.getCreneaux()) {      
        ArrayList<CreneauHorraire> creaneauList =  horraireMap.get(itCreneau.getJourSemaine());      
        if(creaneauList == null) {
          creaneauList = new ArrayList<CreneauHorraire>();
        }      
        CreneauHorraire newCreneau = new CreneauHorraire(itCreneau.getHeureDebut(), itCreneau.getHeureFin());
        creaneauList.add(newCreneau);      
        horraireMap.put(itCreneau.getJourSemaine(), creaneauList);
      } 
    }
    return horraireMap;
  }
  
  /**
   * Indique le nombre maximum de creneau pour cette map peut importe le jour
   * @param creneauMap
   * @return
   */
  public static int getNbCreneau(Map<String, ArrayList<CreneauHorraire>> creneauMap) {
    int nbCreneau = 0;   
    for(String itJour : JOURS) {
      ArrayList<CreneauHorraire> itCreneauHorraire =  creneauMap.get(itJour);      
      if(Util.notEmpty(itCreneauHorraire)) {
        if(itCreneauHorraire.size() > nbCreneau) {
          nbCreneau = itCreneauHorraire.size();
        }
      }
    }
    return nbCreneau;
  }


}
