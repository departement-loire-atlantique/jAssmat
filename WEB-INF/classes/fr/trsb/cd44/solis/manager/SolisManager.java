package fr.trsb.cd44.solis.manager;



import java.text.Normalizer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.jalios.util.Util;

import fr.cg44.plugin.assmat.hibernate.AssmatHibernateManager;
import fr.cg44.plugin.assmat.hibernate.HibernateCD44Util;
import fr.cg44.plugin.assmat.managers.ProfilManager;
import fr.trsb.cd44.solis.beans.AssmatSolis;
import fr.trsb.cd44.solis.beans.TrancheAge;

public class SolisManager {
  private static SessionFactory sessionFactory;
  private static volatile SolisManager instance;
  //private static Session session;

  private static Logger logger = Logger.getLogger(SolisManager.class);


  /**
   * Constructeur
   */
  private SolisManager() {

  }

  /**
   * Singeleton pour RepriseManager
   *
   * @return RepriseManager
   */
  public static SolisManager getInstance() {
    if (instance == null) {
      synchronized (SolisManager.class) {
        if (instance == null) {
          instance = new SolisManager();
        }
      }
    }
    return instance;
  }

  /**
   * Permet d'initialiser la connexion à la base de données
   * Cette méthode doit être appelée avant toute interrogation à la base
   */
  public static void init() { 
    // Récupère la session factory depuis assmat
    sessionFactory = AssmatHibernateManager.getInstance().getSessionFactory();              
  }


  /**
   * Retourne tous les comptes de la table Solis
   * @return Une {@link List} de {@link AssmatSolis}
   */
  @SuppressWarnings("unchecked")
  public List<AssmatSolis> getAllAssmatSolis() {
    List<AssmatSolis> list = HibernateCD44Util.getSession().createCriteria(AssmatSolis.class).list();
    return list;
  }

  /**
   * Retourne tous les comptes de la table Solis à partir d'un numéro d'agrément
   * @param numAgrement Le numéro d'agrément recherché recherché
   * @return Une {@link List} de {@link AssmatSolis}
   */
  public List<AssmatSolis> getAssmatSolisByNumAgrement(int numAgrement){
    List<AssmatSolis> list = HibernateCD44Util.getSession().createCriteria(AssmatSolis.class)
        .add(Restrictions.eq("numDossierAssmat", numAgrement))
        .list();
    return list;
  }

  /**
   * Retourne tous les comptes de la table Solis à partir de plusieurs numéros d'agrément
   * @param numAgrement Les numéros d'agrément recherchés
   * @return Une {@link List} de {@link AssmatSolis}
   */
  public List<AssmatSolis> getAssmatSolisByNumAgrement(List<String> numAgrementList){
    Disjunction or = Restrictions.disjunction();
    Criteria critere = HibernateCD44Util.getSession().createCriteria(AssmatSolis.class);                      
    for (String num : numAgrementList){
      or.add(Restrictions.eq("numDossierAssmat", Integer.parseInt(num)));
    }           
    critere.add(or);           
    List<AssmatSolis> list = critere.list(); 
    return list;
  }

  /**
   * Retourne tous les comptes de la table Solis à partir du nom et prénom
   * @param name Le nom
   * @param firstName Le prénom
   * @param caseSensitive Sensible à la casse
   * @return Une {@link List} de {@link AssmatSolis}
   */
  public List<AssmatSolis> getAssmatSolisByNameFirstname(String name, String firstName){
    Criteria crit = HibernateCD44Util.getSession().createCriteria(AssmatSolis.class);

    crit.add(Restrictions.eq("nomAssmat", clean(name)))
    	.add(Restrictions.like("prenomAssmat", clean(firstName).replaceAll(" ", "_")));
    
    List<AssmatSolis> list = crit.list();
    return list;
  }

  /**
   * Retourne tous les comptes de la table Solis à partir du nom et prénom et de la date de naissance
   * @param name Le nom
   * @param firstName Le prénom
   * @param dateDeNaissance La date de naissance
   * @param caseSensitive Sensible à la casse
   * @return Une {@link List} de {@link AssmatSolis}
   */
  public List<AssmatSolis> getAssmatSolisByNameFirstnameDateNaissance(String name, String firstName, Date dateDeNaissance){
    Criteria crit = HibernateCD44Util.getSession().createCriteria(AssmatSolis.class);

    crit.add(Restrictions.ge("dateNaissAssmat",new java.sql.Date(getDateDebut(dateDeNaissance).getTime())))
    	.add(Restrictions.le("dateNaissAssmat",new java.sql.Date(getDateFin(dateDeNaissance).getTime())))
    	.add(Restrictions.eq("nomAssmat", clean(name)))
      	.add(Restrictions.eq("prenomAssmat", clean(firstName)));
   

    List<AssmatSolis> list = crit.list();
    return list;
  }
  
  /**
   * Retourne tous les comptes de la table Solis à partir du nom et prénom et de la date de naissance et son numéro d'agrément
   * @param name
   * @param firstName
   * @param dateDeNaissance
   * @param numeroAgrement
   * @return
   */
  public List<AssmatSolis> getAssmatSolisByNameFirstnameDateNaissanceNumAgrement(String civilite, String name, String firstName, Date dateDeNaissance, int numeroAgrement){
	    Criteria crit = HibernateCD44Util.getSession().createCriteria(AssmatSolis.class);

	    crit.add(Restrictions.eq("civiliteAssmat", ProfilManager.getInstance().transformCiviliteToSolis(civilite)))
			.add(Restrictions.ge("dateNaissAssmat",new java.sql.Date(getDateDebut(dateDeNaissance).getTime())))
	    	.add(Restrictions.le("dateNaissAssmat",new java.sql.Date(getDateFin(dateDeNaissance).getTime())))
	    	.add(Restrictions.eq("nomAssmat", clean(name)))
	      	.add(Restrictions.eq("prenomAssmat", clean(firstName)))
	    	.add(Restrictions.eq("numDossierAssmat", numeroAgrement));

	    List<AssmatSolis> list = crit.list();
	    return list;
	  }

  /**
   * Méthode nous indiquant si l'ASSMAT exerce à domicile
   * @param numAgrement Le numéro d'agrément
   * @return <true> si l'ASSMAT exerce à domicile, sinon <false>
   */
  public boolean exerceDomicile(int numAgrement){
    Boolean exerceDomicile = (Boolean) HibernateCD44Util.getSession().createCriteria(AssmatSolis.class)
        .add(Restrictions.eq("numDossierAssmat", numAgrement))
        .setProjection(Projections.projectionList()
            .add(Projections.property("exerceDomicile")))
            .uniqueResult();
    return exerceDomicile == null ? false: exerceDomicile;
  }

  /**
   * Méthode nous retournant le statut de l'ASSMAT
   * @param numAgrement Le numéro d'agrément
   * @return Le statut
   */
  public String getStatut(int numAgrement){
    String statut = (String) HibernateCD44Util.getSession().createCriteria(AssmatSolis.class)
        .add(Restrictions.eq("numDossierAssmat", numAgrement))
        .setProjection(Projections.projectionList()
            .add(Projections.property("statut")))
            .uniqueResult();
    return statut == null ? "": statut;
  }

  /**
   * Méthode nous indiquant si l'ASSMAT exerce en MAM
   * @param numAgrement Le numéro d'agrément
   * @return <true> si l'ASSMAT exerce en MAM, sinon <false>
   */
  public boolean exerceMAM(int numAgrement){
    Boolean exerceMAM = (Boolean) HibernateCD44Util.getSession().createCriteria(AssmatSolis.class)
        .add(Restrictions.eq("numDossierAssmat", numAgrement))
        .setProjection(Projections.projectionList()
            .add(Projections.property("exerceMam")))
            .uniqueResult();
    return exerceMAM == null ? false: exerceMAM;
  }

  /**
   * Méthode nous indiquant si l'ASSMAT est en activité
   * @param numAgrement Le numéro d'agrément
   * @return <true> si l'ASSMAT est en activité, sinon <false>
   */
  public boolean enActivite(int numAgrement){
    Boolean enActivite = (Boolean) HibernateCD44Util.getSession().createCriteria(AssmatSolis.class)
        .add(Restrictions.eq("numDossierAssmat", numAgrement))
        .setProjection(Projections.projectionList()
            .add(Projections.property("enActivite")))
            .uniqueResult();
    return enActivite == null ? false: enActivite;
  }

  public boolean isAuthorized(int numAgrement){
    List<AssmatSolis> listeSolis = getAssmatSolisByNumAgrement(numAgrement);
    if(listeSolis != null && !listeSolis.isEmpty()){
      AssmatSolis compte = listeSolis.get(0);
      if(compte != null){
        return compte.getAutorisationActivation();
      }
    }
    return false;
  }

  public boolean isAuthorized(String name, String firstName, Date dateDeNaissance){
    List<AssmatSolis> listeSolis = getAssmatSolisByNameFirstnameDateNaissance(name, firstName, dateDeNaissance);
    if(listeSolis != null && !listeSolis.isEmpty()){
      AssmatSolis compte = listeSolis.get(0);
      if(compte != null){
        return compte.getAutorisationActivation();
      }
    }

    return false;
  }

  public TrancheAge getTrancheAge(String key){
    TrancheAge tranche = (TrancheAge) HibernateCD44Util.getSession().createCriteria(TrancheAge.class)
        .add(Restrictions.eq("trancheAgeKey", key))
        .uniqueResult();
    return tranche;
  }
  
  public List<TrancheAge> getAllTrancheAge(){
	    List<TrancheAge> listTranche = HibernateCD44Util.getSession().createCriteria(TrancheAge.class).list();
	    return listTranche;
  }

  private static Date getDateDebut(Date date){
    Date dateDebut = new Date(date.getTime());    
    dateDebut.setHours(0);
    dateDebut.setMinutes(0);
    dateDebut.setSeconds(0);
    return dateDebut;
  }

  private static Date getDateFin(Date date){
    Date dateFin = new Date(date.getTime());
    dateFin.setHours(23);
    dateFin.setMinutes(59);
    dateFin.setSeconds(59);
    return dateFin;
  }
  
  public static String clean(String s){
	  String cleanString = s;
	  cleanString = removeAccent(cleanString);
	  cleanString = cleanString.replace("-", " ");
	  cleanString = cleanString.replace("_", " ");
	  cleanString = StringUtils.clean(cleanString);
	  cleanString = cleanString.replace("STE ", "sainte ");
	  cleanString = cleanString.replace("ST ", "saint ");
	  cleanString = cleanString.toUpperCase();  
	  return cleanString;
  }
  
  private static String removeAccent(String source){
	  return Normalizer.normalize(source, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
  }
  
  public static String cleanStatut(String statut){
	  return Util.replaceAll(statut, " ", "_");
  }
   
  private static HashMap<String, AssmatSolis> mapSolis = new HashMap<String, AssmatSolis>();
  public HashMap<String, AssmatSolis> getMapAssmatSolis(){
	  return mapSolis;
  }
  
  public void setMapAssmatSolis(HashMap<String, AssmatSolis> map){
	  this.mapSolis = map;
  }
  
  public AssmatSolis getAssmatSolisFromMap(String idProfil){
	  return mapSolis.get(idProfil);
  }
}