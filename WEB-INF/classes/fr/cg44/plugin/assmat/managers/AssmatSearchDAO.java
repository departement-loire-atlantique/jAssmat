package fr.cg44.plugin.assmat.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;

import com.jalios.jcms.db.HibernateUtil;
import com.jalios.util.Util;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.beans.AssmatSearch;
import fr.cg44.plugin.assmat.beans.DispoAssmat;
import fr.cg44.plugin.assmat.hibernate.HibernateCD44Util;
import fr.trsb.cd44.solis.beans.AssmatSolis;
import fr.trsb.cd44.solis.beans.CorresRamUa;
import fr.trsb.cd44.solis.beans.GroupementRam;
import fr.trsb.cd44.solis.manager.SolisManager;
import generated.OuvertureSite;


public class AssmatSearchDAO {


	private static final Logger logger = Logger.getLogger(AssmatSearchDAO.class);
	
	private static final int DISPO_RECHERCHE = 1;
	private static final int DISPO_FUTURE = 2;
	private static final int DISPO_NON = 3;
	private static final int DISPO_NON_RENSEIGNE = 4;
	
	/**
	 * Retourne tous les comptes de la table Solis
	 * 
	 * @return Une {@link List} de {@link AssmatSearch}
	 */
	@SuppressWarnings("unchecked")
	public static List<AssmatSearch> getAllAssmatSearch() {
		List<AssmatSearch> list = HibernateCD44Util.getSession().createCriteria(AssmatSearch.class).list();
		return list;
	}

	public static void removeAllDispos(String id){
		Set<DispoAssmat> setDispos = getDispoASSMatFromMbr(id);
		if(Util.notEmpty(setDispos)){
			for (DispoAssmat dispoAssmat : setDispos) {
				HibernateCD44Util.delete(dispoAssmat);
			}
		}
	}

	
	/**
  * Retourne tous les comptes de la table Solis
  * 
  * @param id
  *            L'Id recherché
  * @return Une {@link List} de {@link AssmatSearch}
  */
 public static List<OuvertureSite> getOuvertureSiteByTel(String tel) {  
  tel = tel.replaceAll(" ", ""); 
  List<OuvertureSite> ouvertureSiteList = HibernateUtil.query(OuvertureSite.class, "title", tel);
  return ouvertureSiteList;
 }
	
	
	
	/**
	 * Retourne tous les comptes de la table Solis
	 * 
	 * @param id
	 *            L'Id recherché
	 * @return Une {@link List} de {@link AssmatSearch}
	 */
	public static List<AssmatSearch> getAssmatSearchById(long id) {
		List<AssmatSearch> list = HibernateCD44Util.getSession().createCriteria(AssmatSearch.class).add(Restrictions.eq("JRowId", id)).list();
		return list;
	}


	/**
	 * Retourne tous les comptes de la table Solis
	 * 
	 * @param id
	 *            L'Id recherché
	 * @return Une {@link List} de {@link AssmatSearch}
	 */
	public static List<AssmatSearch> getAssmatSearchById(Set<String> ids) {
		List<AssmatSearch> list = HibernateCD44Util.getSession().createCriteria(AssmatSearch.class).add(Restrictions.in("dbprofilId", ids)).list();
		return list;
	}



	/**
	 * Retourne tous les comptes de la table Solis
	 * 
	 * @param idMember
	 *            L'Id du membre recherché
	 * @return Une {@link List} de {@link AssmatSearch}
	 */
	public static List<AssmatSearch> getAssmatSearchByIdMembre(String idMember) {
		List<AssmatSearch> list = HibernateCD44Util.getSession().createCriteria(AssmatSearch.class).add(Restrictions.eq("membreJcmsId", idMember)).list();
		return list;
	}

	/**
	 * Retourne tous les comptes de la table Solis
	 * 
	 * @param idMember - L'Id du membre recherché
	 * @param exerciceDomicile - Si le mode de garde est à domicile. Si <code>false</code>, c'est en MAM
	 * 			
	 * @return Une {@link List} de {@link AssmatSearch}
	 */
	public static List<AssmatSearch> getAssmatSearchByIdMembreTypeGarde(String idMember, boolean exerciceDomicile) {
		List<AssmatSearch> list = HibernateCD44Util.getSession().createCriteria(AssmatSearch.class).add(Restrictions.eq("membreJcmsId", idMember)).add(Restrictions.eq("isDomicile", exerciceDomicile)).list();
		return list;
	}

	public static List<AssmatSearch> getAssmatSearchByIdDBProfil(String dbProfilId) {
		List<AssmatSearch> list = HibernateCD44Util.getSession().createCriteria(AssmatSearch.class).add(Restrictions.eq("dbprofilId", dbProfilId)).list();
		return list;
	}

	public static List<AssmatSearch> getAssmatSearchByIdSolis(String jRowSolisId) {
		List<AssmatSearch> list = HibernateCD44Util.getSession().createCriteria(AssmatSearch.class).add(Restrictions.eq("JRowSolisId", jRowSolisId)).list();
		return list;
	}

	public static List<DispoAssmat> getDispoAssMat(long id) {
		List<DispoAssmat> list = HibernateCD44Util.getSession().createCriteria(DispoAssmat.class).add(Restrictions.eq("j_item_id", id)).list();
		return list;
	}

	public static List<DispoAssmat> getDispoAssMatByJcmsId(String id) {
		List<DispoAssmat> list = HibernateCD44Util.getSession().createCriteria(DispoAssmat.class).add(Restrictions.eq("jcmsId", id)).list();
		return list;
	}

	public static List<DispoAssmat> getDispoAssMatByAMsearch(AssmatSearch amSearch) {
		List<DispoAssmat> list = HibernateCD44Util.getSession().createCriteria(DispoAssmat.class).add(Restrictions.eq("JRowId", amSearch.getJRowId())).list();
		return list;
	}

	public static Set<DispoAssmat> getDispoASSMatFromMbr(String idMbr) {
		List<AssmatSearch> list = getAssmatSearchByIdMembre(idMbr);
		if(Util.notEmpty(list)){
			return list.get(0).getDispoAssmats();
		}
		return null;
	}




	public static List<AssmatSearch> getDispoASSMatFromMbrAndAgeKey(String idMbr, Integer trancheAgeKey) {
		Criteria crit = HibernateCD44Util.getSession().createCriteria(AssmatSearch.class)
				.createAlias("dispoAssmats", "d")
				.add(Restrictions.eq("membreJcmsId", idMbr))
				.add(Restrictions.eq("d.trancheAgeKey", trancheAgeKey));

		List list = crit.list();
		return list;
	}



	private static boolean setSpecificite(Criteria crit, boolean noCritere, Boolean accueilPeriscolaire, Boolean accueilMercredi, Boolean accueilVacances, Boolean accueilApres20h, Boolean accueilAvant7h, Boolean accueilSamedi, Boolean accueilDimanche, Boolean accueilNuit, Boolean accueilAtypique, Boolean specHandicape, Boolean specPartiel, Boolean specDepannages){
		if (accueilPeriscolaire != null && accueilPeriscolaire == true) {
			crit.add(Restrictions.eq("accueilPeriscolaire", accueilPeriscolaire));
			noCritere = false;
		}
		if (accueilMercredi != null && accueilMercredi == true) {
			crit.add(Restrictions.eq("accueilMercredi", accueilMercredi));
			noCritere = false;
		}
		if (accueilVacances != null && accueilVacances == true) {
			crit.add(Restrictions.eq("accueilVacances", accueilVacances));
			noCritere = false;
		}
		if (accueilAvant7h != null && accueilAvant7h == true) {
			crit.add(Restrictions.eq("accueilAvant7h", accueilAvant7h));
			noCritere = false;
		}
		if (accueilApres20h != null && accueilApres20h == true) {
			crit.add(Restrictions.eq("accueilApres20h", accueilApres20h));
			noCritere = false;
		}
		if (accueilSamedi != null && accueilSamedi == true) {
			crit.add(Restrictions.eq("accueilSamedi", accueilSamedi));
			noCritere = false;
		}
		if (accueilDimanche != null && accueilDimanche == true) {
			crit.add(Restrictions.eq("accueilDimanche", accueilDimanche));
			noCritere = false;
		}
		if (accueilNuit != null && accueilNuit == true) {
			crit.add(Restrictions.eq("accueilNuit", accueilNuit));
			noCritere = false;
		}
		if (accueilAtypique != null && accueilAtypique == true) {
			crit.add(Restrictions.eq("accueilAtypique", accueilAtypique));
			noCritere = false;
		}
		if (specHandicape != null && specHandicape == true) {
			crit.add(Restrictions.eq("specHandicape", specHandicape));
			noCritere = false;
		}
		if (specPartiel != null && specPartiel == true) {
			crit.add(Restrictions.eq("specPartiel", specPartiel));
			noCritere = false;
		}
		if (specDepannages != null && specDepannages == true) {
			crit.add(Restrictions.eq("specDepannages", specDepannages));
			noCritere = false;
		}
		return noCritere;
	}

	private static boolean setNomAssmat(Criteria crit, boolean noCritere, String nomAssmat){
		if (Util.notEmpty(nomAssmat)) {
			crit.add(Restrictions.eq("nomAssmat", SolisManager.clean(nomAssmat)));
			noCritere = false;
		}
		return noCritere;
	}

	private static boolean setLieu(Criteria crit, boolean noCritere, String commune,int codeInsee, String[] codesQuartiers, String[] codesMicroQuartiers, Boolean lieuDomicile, Boolean lieuMam,String adresse, double latitude, double longitude, double distance){
	// Cherche sur la commune seulement si seule une commune est renseigné sans adresse
	// Ou si une adresse sans distance (Toute la commune / Par quartier / Par micro quartier) est renseignée
	  if( (Util.notEmpty(codeInsee) && Util.isEmpty(adresse) && ( Util.isEmpty(latitude) ||  Util.isEmpty(longitude))  ) ||
	      ( Util.notEmpty(codeInsee) && ( Util.isEmpty(distance) || distance<=0 || Util.isEmpty(latitude) || Util.isEmpty(longitude)))){
				crit.add(Restrictions.eq("commune", codeInsee));
				noCritere = false;
		}
		// Si une restriction lieu (mam ou domicile est coché)
		if(lieuDomicile && !lieuMam || lieuMam && !lieuDomicile){
			crit.add(Restrictions.eq("isDomicile",lieuDomicile));

			noCritere = false;
		}
		

		// Si quartier renseigné, recherche si l'assmat exerce à domicile ou en MAM dans ce quartier
		//crit.add(Restrictions.like("idMicroQuartierDom","4410902%"));		
	     if(Util.notEmpty(codesQuartiers) && codesQuartiers.length > 0){
	         Disjunction disjunctionQuartier = Restrictions.disjunction();
	         for (int i = 0; i < codesQuartiers.length; i++) {
	          String codeQuartier = codesQuartiers[i];
	          disjunctionQuartier.add(Restrictions.like("idMicroQuartier",codeQuartier+"%"));
	         }

	         crit.add(disjunctionQuartier);
				
	         noCritere = false;
	        } 
		
	     	// Si micro quartiers renseigné(s), recherche si l'assmat exerce à domicile ou en MAM dans ce(s) micro quartier(s)
			//crit.add(Restrictions.like("idMicroQuartierDom","4410902%"));		
		     if(Util.notEmpty(codesMicroQuartiers) && codesMicroQuartiers.length > 0){
		         Disjunction disjunctionMicroQuartier = Restrictions.disjunction();
		         for (int i = 0; i < codesMicroQuartiers.length; i++) {
		          String codeMicroQuartier = codesMicroQuartiers[i];
		          disjunctionMicroQuartier.add(Restrictions.eq("idMicroQuartier",codeMicroQuartier));
		         }

		         crit.add(disjunctionMicroQuartier);
					
		         noCritere = false;
		        }		
		
		/*if(Util.notEmpty(adresse)){
			String street = adresse + " " + commune;
			List<Double> latLong =  AssmatUtil.getLatitudeLongitudeFromAdresse(street);
			if(Util.notEmpty(latLong)){
			 longitude = latLong.get(0);
				latitude = latLong.get(1);				
			}
		}
		*/
		/* On ne prend en compte ce critère que si on a précisé une distance pour le périmètre de la recherche
		 * On exclue donc la recherche sur toute la commune et par quartiers/micro quartiers.
		*/
		if(Util.notEmpty(distance) && distance>0 && Util.notEmpty(latitude) && Util.notEmpty(longitude)){
			crit.add(Restrictions.sqlRestriction("ST_Distance_sphere(ST_Point("+longitude+","+latitude+"), geometry)   < "+distance));
			noCritere = false;
		}	 
		return noCritere;
	}

	/**
	 * La date de dispo future la plus proche de la date de recherche
	 * @return
	 */
 public static Map<Long, Date> getResultDispoFutur(String commune,int codeInsee, String adresse, Boolean lieuDomicile, Boolean lieuMam, Boolean accueilPeriscolaire, Boolean accueilMercredi, Boolean accueilVacances, Boolean accueilAvant7h, Boolean accueilApres20h, Boolean accueilSamedi, Boolean accueilDimanche, Boolean accueilNuit, Boolean specHandicape, Boolean specPartiel, Boolean specDepannages, Boolean accueilAtypique, String nomAssmat, String[] trancheAgeKey, Date dateTime, double distance, double longitude, double lattitude) {
  
   Criteria rechercheDispo = HibernateCD44Util.getSession().createCriteria(DispoAssmat.class);

   // 0011202: recherche par nom d'AM inopérante / La recherche par nom vide par défaut les autres critères pour ne permettre qu'une recherche sur ce critère  
   if(Util.isEmpty(nomAssmat)){

     if(Util.notEmpty(trancheAgeKey) && trancheAgeKey.length > 0){
      Disjunction disjunctionAge = Restrictions.disjunction();
      for (int i = 0; i < trancheAgeKey.length; i++) {
       String age = trancheAgeKey[i];
       disjunctionAge.add(Restrictions.eq("trancheAgeKey", Integer.parseInt(age)));
      }
      rechercheDispo.add(disjunctionAge);  
     }   
   }

   setFuturDispo(rechercheDispo, dateTime);
   
   Map<Long, Date> resultMap = new HashMap<Long, Date>();
   
   HashSet<DispoAssmat> listeDispo = new HashSet<DispoAssmat>(rechercheDispo.list());
   for(DispoAssmat itDispo : listeDispo) {     
     if(itDispo.getAssmatSearch() != null) {    
      if( (resultMap.get(itDispo.getAssmatSearch().getJRowId()) == null) || (resultMap.get(itDispo.getAssmatSearch().getJRowId()).getTime() > itDispo.getDateDebut().getTime()) ) {
        resultMap.put(itDispo.getAssmatSearch().getJRowId(), itDispo.getDateDebut());
      }
     }
   }      
   return resultMap;
 }
 
 
 /**
  * Retoune les dispo par assmat pour une recherche donnée
  * @return
  */
 public static Map<Long, Set<DispoAssmat>> getResultDispo(String commune,int codeInsee, String adresse, String[] codesQuartier, String[] codeMicroQuartier, Boolean lieuDomicile, Boolean lieuMam, Boolean accueilPeriscolaire, Boolean accueilMercredi, Boolean accueilVacances, Boolean accueilAvant7h, Boolean accueilApres20h, Boolean accueilSamedi, Boolean accueilDimanche, Boolean accueilNuit, Boolean specHandicape, Boolean specPartiel, Boolean specDepannages, Boolean accueilAtypique, String nomAssmat, String[] trancheAgeKey, Date dateTime, double distance, double longitude, double lattitude, int typeDispo) {
  
   Criteria rechercheDispo = HibernateCD44Util.getSession().createCriteria(DispoAssmat.class);

   // 0011202: recherche par nom d'AM inopérante / La recherche par nom vide par défaut les autres critères pour ne permettre qu'une recherche sur ce critère  
   if(Util.isEmpty(nomAssmat)){

     if(Util.notEmpty(trancheAgeKey) && trancheAgeKey.length > 0){
      Disjunction disjunctionAge = Restrictions.disjunction();
      for (int i = 0; i < trancheAgeKey.length; i++) {
       String age = trancheAgeKey[i];
       disjunctionAge.add(Restrictions.eq("trancheAgeKey", Integer.parseInt(age)));
      }
      rechercheDispo.add(disjunctionAge);  
     }   
   }
   
   if(typeDispo == DISPO_RECHERCHE) {
     setDispo(rechercheDispo, dateTime);
   }else if(typeDispo == DISPO_FUTURE) { 
     setFuturDispo(rechercheDispo, dateTime);
   }
   
   Map<Long, Set<DispoAssmat>> resultMap = new HashMap<Long, Set<DispoAssmat>>();   
   HashSet<DispoAssmat> listeDispo = new HashSet<DispoAssmat>(rechercheDispo.list());
   
   for(DispoAssmat itDispo : listeDispo) {
     if(itDispo.getAssmatSearch() != null) {
       Set<DispoAssmat> dispoAssmatList = new HashSet<DispoAssmat>();
       if( (resultMap.get(itDispo.getAssmatSearch().getJRowId()) != null)  ) {
         dispoAssmatList = resultMap.get(itDispo.getAssmatSearch().getJRowId());
       }
       dispoAssmatList.add(itDispo);     
       resultMap.put(itDispo.getAssmatSearch().getJRowId(), dispoAssmatList);
     }
   }    
   return resultMap;
 }
 
 
 /**
  * Lance la recherche sur les assmat non inscrites sur le site
  */
 public static List<AssmatSolis>  getNonInscritResultSearch(Map<String, String[]> paramsMap, String idRamSolis){ 
   if(Util.isEmpty(idRamSolis) || !idRamSolis.contains("RAM_")) {
     return Collections.emptyList();
   }
   String codeInsee = paramsMap.get("codeInsee")[0];
   String ageParam = paramsMap.get("age")[0];
   String[] trancheAgeString = ageParam.split("\\|");
   Integer[] trancheAge = new Integer[trancheAgeString.length];
   String[] idQuartier = paramsMap.get("quartier");
   String[] idMicroQuartier = paramsMap.get("microQuartier");
   int cpt = 0;
   for (String itAge : trancheAgeString) {
     trancheAge[cpt++] = Integer.parseInt(itAge);
   }
   int idRam = Integer.parseInt(idRamSolis.replaceAll("RAM_", ""));
   String lieu = "%";
   if(Util.notEmpty(paramsMap.get("lieuexercice"))){
     String lieuParam = paramsMap.get("lieuexercice")[0];
     // Case DOM cochée
     if("cra_67016".equals(lieuParam)){
       lieu = "dom%";
       // Case MAM cochée
     }else if("cra_67017".equals(lieuParam)) {
       lieu = "mam%";
     }
   }
   boolean isAdressDistance = false;
   String distanceParam = paramsMap.get("distance")[0];
   String adresseParam = paramsMap.get("adresse")[0];
   if(Util.notEmpty(adresseParam) && Util.notEmpty(distanceParam) && !"0".equals(distanceParam)) {
     isAdressDistance = true;
   }
   return getNonInscritResultSearch(trancheAge, codeInsee, idRam, lieu, isAdressDistance, idQuartier, idMicroQuartier);
 }
 
 
 /**
  * Lance la recherche sur les assmat non inscrites sur le site (commune, trache d'age, statut et id RAM) avec les critère 
  */
 public static List<AssmatSolis>  getNonInscritResultSearch(Integer[] tranchesAge, String codeInsee, int idRam, String lieu, boolean isAdressDistance, String[] idQuartiers, String[] idMicroQuartiers){       
   Criteria crit = HibernateCD44Util.getSession().createCriteria(AssmatSolis.class);  
   // Récupère la liste des RAM associé a cette RAM
   List<Integer> idRamAssocie = new ArrayList<Integer>();      
   // 0012184 : Dans le cas d'une commune autre que nantes, il ne faudrait pas restreindre l'affichage des AM non inscrites ou non visibles au cas où une recherche avec adresse et distance est renseignée
   if(!isAdressDistance && (idRam == 16 || idRam == 17 || idRam == 19 || idRam == 20 || idRam == 77)) {
     idRamAssocie.add(idRam);
   }else {
     idRamAssocie = getRamAssocie(idRam);
   }     
   // Commune : Filtre sur la commune si toute la commune
   if(!isAdressDistance) {     
     crit.add(Restrictions.eq("codeInsee", codeInsee)); 
   }
   // Quartier : Filtre sur les quartiers si renseigné
   if(Util.notEmpty(idQuartiers)) {
    crit.add(Restrictions.disjunction()
        .add(Restrictions.in("idQuartierDom", idQuartiers))
        .add(Restrictions.in("idQuartierMam", idQuartiers))
        );
   }
   // Micro-Quartier : Filtre sur les micro-quartiers si renseigné
   if(Util.notEmpty(idMicroQuartiers)) {
    crit.add(Restrictions.disjunction()
        .add(Restrictions.in("idMicroQuartierDom", idMicroQuartiers))
        .add(Restrictions.in("idMicroQuartierMam", idMicroQuartiers))
        );
   }
   // Statut
   crit.add(Restrictions.eq("statut", "OK"));
   
   // RAM associée
   crit.add(Restrictions.disjunction()
       .add(Restrictions.in("idRam", idRamAssocie))
       .add(Restrictions.in("idRamMam", idRamAssocie))
       );
   // Tranche d'age
   Disjunction disjunctionTrancheAge = Restrictions.disjunction();   
   for(int i = 1; i <= 8; i++) {
   disjunctionTrancheAge.add(Restrictions.conjunction()
       .add(Restrictions.in("place"+ i +"TrancheAgeKey", tranchesAge))
       .add(Restrictions.like("place"+ i +"AgrementTrancheAgeKey", lieu))
       );
   }   
   crit.add(disjunctionTrancheAge);   
   // Resultat
   List<AssmatSolis> list = crit.list();
   return list;
 }
 
 /**
  * Retourne la liste des RAM assosié de cette RAM (et elle même)
  * @param idRam
  * @return la liste des RAM assosié de cette RAM (et elle même)
  */
 private static List<Integer> getRamAssocie(int idRam) {
   List<Integer> listRamAssocie = new ArrayList<Integer>(); 
   List<GroupementRam> listGroupement = getGroupementRam(idRam); 
   if(Util.notEmpty(listGroupement)) {
     for(GroupementRam itGroupement : listGroupement) {
       listRamAssocie.add(itGroupement.getIdRamCible());
     }
   } else {
     listRamAssocie.add(idRam);
   }
   return listRamAssocie;
 }

  /**
	 * Lance la recherche des disponibilité suivant etat : à la date de la recherche, furture ou non disponible
	 */
	public static Set<AssmatSearch> getResultSearchCore(int typeDispo, String commune,int codeInsee, String adresse, String[] codesQuartier, String[] codeMicroQuartier, Boolean lieuDomicile, Boolean lieuMam, Boolean accueilPeriscolaire, Boolean accueilMercredi, Boolean accueilVacances, Boolean accueilAvant7h, Boolean accueilApres20h, Boolean accueilSamedi, Boolean accueilDimanche, Boolean accueilNuit, Boolean specHandicape, Boolean specPartiel, Boolean specDepannages, Boolean accueilAtypique, String nomAssmat, String[] trancheAgeKey, Date dateTime, double distance, double longitude, double lattitude) {
	  long start = System.currentTimeMillis();
	  	  
	  if(Logger.getLogger(AssmatSearchDAO.class).isDebugEnabled()){
		   logger.debug("getResultSearchCore / Debut : "+(start)+" ms");
		  }   
	  
	  HashSet<AssmatSearch> setResultSearch = new HashSet<AssmatSearch>();
	  //Boolean pour empecher de rerourner tout les resultats lorsqu'aucun critere n'a ete renseigné 
	  boolean noCritere=true;
	  Criteria crit = HibernateCD44Util.getSession().createCriteria(AssmatSearch.class);

	  Criteria rechercheDispo = HibernateCD44Util.getSession().createCriteria(DispoAssmat.class);

	  String groupBy = "assmat_search_id";
	  // 0011202: recherche par nom d'AM inopérante / La recherche par nom vide par défaut les autres critères pour ne permettre qu'une recherche sur ce critère  
	  if(Util.isEmpty(nomAssmat)){
	  
	    if(Util.notEmpty(trancheAgeKey) && trancheAgeKey.length > 0){
	     Disjunction disjunctionAge = Restrictions.disjunction();
	     for (int i = 0; i < trancheAgeKey.length; i++) {
	      String age = trancheAgeKey[i];
	      disjunctionAge.add(Restrictions.eq("trancheAgeKey", Integer.parseInt(age)));
	     }
	     rechercheDispo.add(disjunctionAge);  
	    }
	    groupBy = "assmat_search_id" + " having " + "count(*) >= 1"; // il faut au moins 1 place dans les dispos
	   }

	  if(DISPO_RECHERCHE == typeDispo){
	    setDispo(rechercheDispo,dateTime);
	  }else if(DISPO_FUTURE == typeDispo){
	    setFuturDispo(rechercheDispo, dateTime);	    
	  }else if(DISPO_NON == typeDispo){
	    setNonDispo(rechercheDispo);
	  }else if(DISPO_NON_RENSEIGNE == typeDispo) {
	    setDispoNonRenseignee(rechercheDispo);
	  }

	  String[] alias = new String[1]; 
	  alias[0] = "assmat_search_id"; 
	  Type[] types = new Type[1]; 
	  types[0] = Hibernate.LONG;
	  ProjectionList projectionList = Projections.projectionList();
	  projectionList.add( Projections.sqlGroupProjection("assmat_search_id", groupBy , alias, types));

	  rechercheDispo.setProjection(projectionList);

	  HashSet<Long> listeAssmatSearchId = new HashSet<Long>(rechercheDispo.list());

	  
	  
	  // On lance une requête pour récupérer les AssmatSearch liés aux Ids 
	  if(Util.notEmpty(listeAssmatSearchId)){
	   Criteria critSearchFromAssmatSearchId = HibernateCD44Util.getSession().createCriteria(AssmatSearch.class);
	   critSearchFromAssmatSearchId.add(Restrictions.in("JRowId", listeAssmatSearchId));
	   List<AssmatSearch> listeSearch = critSearchFromAssmatSearchId.list();
	   if(Util.notEmpty(listeSearch)){
	    if(Util.isEmpty(setResultSearch)){
	     setResultSearch.addAll(listeSearch);
	    } else {  
	     setResultSearch = (HashSet<AssmatSearch>) Util.interSet(setResultSearch,new HashSet(listeSearch));
	    }
	   } else {
	    return Util.emptyTreeSet();
	   }
	  } else {
	   return Util.emptyTreeSet();
	  }

	  // TODO bloque sur la commune recherchée, a voir 
	  if(codeInsee != 0){
	   noCritere = setLieu(crit, noCritere, commune, codeInsee, codesQuartier, codeMicroQuartier, lieuDomicile, lieuMam, adresse,lattitude,longitude,distance);
	  }
	   noCritere = setSpecificite(crit, noCritere, accueilPeriscolaire, accueilMercredi, accueilVacances, accueilApres20h, accueilAvant7h, accueilSamedi, accueilDimanche, accueilNuit, accueilAtypique, specHandicape, specPartiel, specDepannages);
	   noCritere = setNomAssmat(crit, noCritere, nomAssmat);
	  //Si un critere a été reseigné
	  if(!noCritere){
	   List<AssmatSearch> list = crit.list();
	   if(Util.notEmpty(list)){
	    if(Util.isEmpty(setResultSearch)){
	     setResultSearch.addAll(list);
	    } else {  
	     setResultSearch = (HashSet<AssmatSearch>) Util.interSet(setResultSearch,new HashSet(list));
	    }
	   }else{
	    return Util.emptyTreeSet();
	   }
	  }

	  
	  long end = System.currentTimeMillis();
	  if(Logger.getLogger(AssmatSearchDAO.class).isDebugEnabled()){
	   logger.debug("getResultSearchCore / Temps d'éxécution : "+(end-start)+" ms");
	  }    
	  return setResultSearch;
	}
	
	
	

	private static void setDispo(Criteria crit, Date dateTime){
	  // Disponible immédiatement
	  Disjunction disjunction = Restrictions.disjunction();
	  disjunction.add(Restrictions.eq("disponible", AssmatUtil.SelectionEtatDispo.IMMEDIATE.getValue()));
	  // Disponibilité futur mais à une date dans le passé par rapport à la date recherchée
	  Conjunction conjunction = Restrictions.conjunction();
	  conjunction.add(Restrictions.eq("disponible", AssmatUtil.SelectionEtatDispo.FUTURE.getValue()));
	  conjunction.add(Restrictions.le("dateDebut", new java.sql.Date(AssmatUtil.getDateFinMonth(dateTime).getTime())));
	  disjunction.add(conjunction);
	  crit.add(disjunction);  
	}
	
	
	public static Set<AssmatSearch> getResultSearchDispo(String commune,int codeInsee, String adresse, String[]quartier, String[] microQuartier, Boolean lieuDomicile, Boolean lieuMam, Boolean accueilPeriscolaire, Boolean accueilMercredi, Boolean accueilVacances, Boolean accueilAvant7h, Boolean accueilApres20h, Boolean accueilSamedi, Boolean accueilDimanche, Boolean accueilNuit, Boolean specHandicape, Boolean specPartiel, Boolean specDepannages, Boolean accueilAtypique, String nomAssmat, String[] trancheAgeKey, Date dateTime, double distance, double longitude, double lattitude) { 	 
		  return getResultSearchCore(DISPO_RECHERCHE, commune, codeInsee, adresse, quartier, microQuartier, lieuDomicile, lieuMam, accueilPeriscolaire, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, trancheAgeKey, dateTime, distance, longitude, lattitude);
	}

	
 private static void setFuturDispo(Criteria crit, Date dateTime){   
   Conjunction conjunction = Restrictions.conjunction();
   conjunction.add(Restrictions.eq("disponible", AssmatUtil.SelectionEtatDispo.FUTURE.getValue()));
   conjunction.add(Restrictions.gt("dateDebut", new java.sql.Date(AssmatUtil.getDateFinMonth(dateTime).getTime())));
   crit.add(conjunction);   
 }
 
 
 public static Set<AssmatSearch> getResultSearchFutur(String commune,int codeInsee, String adresse, String[] codesQuartier, String[] codeMicroQuartier, Boolean lieuDomicile, Boolean lieuMam, Boolean accueilPeriscolaire, Boolean accueilMercredi, Boolean accueilVacances, Boolean accueilAvant7h, Boolean accueilApres20h, Boolean accueilSamedi, Boolean accueilDimanche, Boolean accueilNuit, Boolean specHandicape, Boolean specPartiel, Boolean specDepannages, Boolean accueilAtypique, String nomAssmat, String[] trancheAgeKey,Date dateTime, double distance, double longitude, double lattitude) {
   return getResultSearchCore(DISPO_FUTURE, commune, codeInsee, adresse, codesQuartier, codeMicroQuartier, lieuDomicile, lieuMam, accueilPeriscolaire, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, trancheAgeKey, dateTime, distance, longitude, lattitude);
 }
 
 
 private static void setNonDispo(Criteria crit){
   Disjunction disjunction = Restrictions.disjunction();
   disjunction.add(Restrictions.eq("disponible", AssmatUtil.SelectionEtatDispo.NON_DISPO.getValue()));
   crit.add(disjunction);   
 }

 
 public static Set<AssmatSearch> getResultSearchNonDispos(String commune,int codeInsee, String adresse, String[] codesQuartier, String[] codeMicroQuartier, Boolean lieuDomicile, Boolean lieuMam, Boolean accueilPeriscolaire, Boolean accueilMercredi, Boolean accueilVacances, Boolean accueilAvant7h, Boolean accueilApres20h, Boolean accueilSamedi, Boolean accueilDimanche, Boolean accueilNuit, Boolean specHandicape, Boolean specPartiel, Boolean specDepannages, Boolean accueilAtypique, String nomAssmat, String[] trancheAgeKey,Date dateTime, double distance, double longitude, double lattitude) {
   return getResultSearchCore(DISPO_NON, commune, codeInsee, adresse, codesQuartier, codeMicroQuartier, lieuDomicile, lieuMam, accueilPeriscolaire, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, trancheAgeKey, dateTime, distance, longitude, lattitude);
 } 
 
 
 private static void setDispoNonRenseignee(Criteria crit){
   crit.add(Restrictions.eq("disponible", AssmatUtil.SelectionEtatDispo.INCONNU.getValue()));
  }
	
 public static Set<AssmatSearch> getResultSearchDispoNonRenseigne(String commune,int codeInsee, String adresse, String[] codesQuartier, String[] codeMicroQuartier, Boolean lieuDomicile, Boolean lieuMam, Boolean accueilPeriscolaire, Boolean accueilMercredi, Boolean accueilVacances, Boolean accueilAvant7h, Boolean accueilApres20h, Boolean accueilSamedi, Boolean accueilDimanche, Boolean accueilNuit, Boolean specHandicape, Boolean specPartiel, Boolean specDepannages, Boolean accueilAtypique, String nomAssmat, String[] trancheAgeKey,Date dateTime, double distance, double longitude, double lattitude) {
   return getResultSearchCore(DISPO_NON_RENSEIGNE, commune, codeInsee, adresse, codesQuartier, codeMicroQuartier, lieuDomicile, lieuMam, accueilPeriscolaire, accueilMercredi, accueilVacances, accueilAvant7h, accueilApres20h, accueilSamedi, accueilDimanche, accueilNuit, specHandicape, specPartiel, specDepannages, accueilAtypique, nomAssmat, trancheAgeKey, dateTime, distance, longitude, lattitude);
 } 
 
 
 /**
  * Retourne les relais associés
  */
 public static List<GroupementRam> getGroupementRam(Integer id) {
  List<GroupementRam> list = HibernateCD44Util.getSession().createCriteria(GroupementRam.class)
      .add(Restrictions.eq("idRamSource", id)).list();
  return list;
 }
 
 
 /**
  * Retourne l'ua correspondante a une RAM
  */
 public static Integer getCorresRamUa(Integer ram) {
   Integer ua = null;
   List<CorresRamUa> list = HibernateCD44Util.getSession().createCriteria(CorresRamUa.class)
       .add(Restrictions.eq("idRam", ram)).list();
   if(Util.notEmpty(list)){
     ua = Util.getFirst(list).getIdUa();
   }
   return ua;
 }
	
}