package fr.cg44.plugin.assmat.managers;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.AssmatUtil.SelectionEtatDispo;
import fr.cg44.plugin.assmat.AssmatUtil.SelectionLogin;
import fr.cg44.plugin.assmat.beans.AssmatSearch;
import fr.cg44.plugin.assmat.hibernate.HibernateCD44Util;
import fr.cg44.plugin.tools.facetedsearch.exception.UnknowCityException;
import fr.cg44.plugin.tools.facetedsearch.manager.CityManager;
import fr.trsb.cd44.solis.beans.AssmatSolis;
import fr.trsb.cd44.solis.manager.SolisManager;
import generated.City;
import generated.Disponibilite;
import generated.ProfilASSMAT;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.jalios.jcms.Member;
import com.jalios.jcms.db.HibernateUtil;
import com.jalios.util.Util;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Manager utilisé dans l'édition d'une fiche profil en front office
 * 
 * @author tlegat
 *
 */
public class ProfilManager {

	private static final ProfilManager SINGLETON = new ProfilManager();


	private static Logger logger = Logger.getLogger(ProfilManager.class);

	/**
	 * Singleton de la classe
	 * 
	 * @return l'instance
	 */
	public static ProfilManager getInstance() {
		return SINGLETON;
	}

	/**
	 * Méthode permettant de récupérer le profil du membre
	 * 
	 * @param paramMember
	 *            le membre
	 * @return {@link ESNCCIProfil}
	 */
	public ProfilASSMAT getProfilASSMAT(Member paramMember) {
		return getProfilASSMAT(paramMember, false);
	}


	/**
	 * Récupère le login renseigné dans le profil AM suivant le choix Type login
	 * 
	 * @return
	 */
	public String getLogin(ProfilASSMAT profil) {
		String typeLogin = profil.getTypeLogin();
		String loginProfil;
		if (SelectionLogin.TELEPHONE.getValue().equals(typeLogin)) {
			loginProfil = profil.getAuthor().getMobile();
		} else if (SelectionLogin.MAIL.getValue().equals(typeLogin)) {
			loginProfil = profil.getAuthor().getEmail();
		} else {
			loginProfil = Integer.toString(profil.getNum_agrement());
		}
		return loginProfil;
	}

	/**
	 * Récupère le membre JCMS relié au profil assmat
	 * 
	 * @param profil
	 */
	public Member getMember(ProfilASSMAT profil) {
		if (Util.isEmpty(profil)) {
			return null;
		}
		return profil.getAuthor();
	}

	/**
	 * Traduit la civilité entre les assmat (table solis) et la civilité d'un
	 * membre JCMS
	 * 
	 * @param civilite
	 *            La civilité de l'assmat à partir de la table solis
	 * @return La civilité pour formatée pour un membre JCMS
	 */
	public String getCivilite(String civilite) {
		if ("Mme".equalsIgnoreCase(civilite)) {
			return "mrs";
		}
		if ("M".equalsIgnoreCase(civilite)) {
			return "mr";
		}
		return civilite;
	}

	/**
	 * Traduit la civilité entre les assmat (table solis) et la civilité d'un
	 * membre JCMS
	* @param civilite
	* @return
	*/
	public String transformCiviliteToSolis(String civilite){
		if("mr".equalsIgnoreCase(civilite)){
			civilite = "M";
		} else if("mrs".equalsIgnoreCase(civilite)){
			civilite = "Mme";
		} else if("mme".equalsIgnoreCase(civilite)){
			civilite = "Mme";
		} else if("m".equalsIgnoreCase(civilite)){
			civilite = "M";
		}
		return civilite;
	}

	/**
	 * Récupère le profile assmat correspondant au numéro de dossier
	 * 
	 * @param dossier
	 * @return
	 */
	public ProfilASSMAT getProfilASSMAT(Integer numDossier) {
		ProfilASSMAT profilAssmat = HibernateUtil.queryUnique(ProfilASSMAT.class, "num_agrement", numDossier);
		return profilAssmat;
	}

	
 /**
  * Récupère le profile assmat correspondant a une assamtSearch
  * 
  * @param dossier
  * @return
  */
 public ProfilASSMAT getProfilASSMATbyAssmatSearch(AssmatSearch assmatSearch) {
  ProfilASSMAT profilAssmat = HibernateUtil.queryUnique(ProfilASSMAT.class, "rowId", assmatSearch.getJRowProfilam());
  return profilAssmat;
 }
	
	
	/**
	 * Méthode permettant de récupérer le profil du membre. S'il n'existe pas il
	 * est crée
	 * 
	 * @param paramMember
	 *            le membre
	 * @param paramBoolean
	 *            boolean permettant de créer le contenu de profil
	 * @return {@link ASSMATProfil}
	 */
	public ProfilASSMAT getProfilASSMAT(Member paramMember, boolean paramBoolean) {
		if (paramMember == null) {
			return null;
		}

		ProfilASSMAT localMemberProfile = (ProfilASSMAT) Util.getFirst(getProfilASSMATList(ProfilASSMAT.class, paramMember, null));
		if (localMemberProfile != null) {
			return localMemberProfile;
		}

		if (!paramBoolean) {
			return null;
		}

		localMemberProfile = new ProfilASSMAT();
		localMemberProfile.setAuthor(paramMember);
		localMemberProfile.setTitle(SolisManager.clean(paramMember.getFullName()));
		localMemberProfile.performCreate(paramMember);

		return localMemberProfile;
	}
	
	public List<Disponibilite> getDisponibilitesList(Member paramMember, String paramString) {
		if (paramMember == null) {
			return null;
		}
		List<Disponibilite> localList = HibernateUtil.query(Disponibilite.class, "authorId", paramMember.getId(), paramString);
		return localList;
	}

	
	/**
	 * Indique su le memebre a au moins une disponibilité disponible actuellement ou dans le futur
	 * (Set pour afficher les coordonnées seulement si l'assmat a des disponibilités)
	 * @param mbr
	 * @return
	 */
	public Boolean hasDispo(Member mbr) {
    List<Disponibilite> dispoList = getDisponibilitesList(mbr, null);
    if(Util.notEmpty(dispoList)) {
      for(Disponibilite itDispo : dispoList) {        
       if(SelectionEtatDispo.IMMEDIATE.getValue().equals(itDispo.getEtatDispo()) || 
          SelectionEtatDispo.FUTURE.getValue().equals(itDispo.getEtatDispo())) {
         return true;
       }
      }
    }
    return false;	  
	}
	
	
 /**
  * Retourne la date la plus récente de la modification des disponibilités de l'assmat
  * @param mbr
  * @return
  */
 public Date getDateModifDispo(Member mbr) {
    List<Disponibilite> dispoList = getDisponibilitesList(mbr, null);
    Date dateModif = null;
    if(Util.notEmpty(dispoList)) {
      for(Disponibilite itDispo : dispoList) {        
       if(dateModif == null || dateModif.before(itDispo.getMdate())) {
         dateModif = itDispo.getMdate();
       }
      }
    }
    return dateModif;   
 }

	/**
	 * Méthode permettant de récupérer la liste des {@link ASSMATProfil}
	 * 
	 * @param paramClass
	 *            Classe permettant de requêter en base
	 * @param <T>
	 *            le type de classe
	 * @param paramMember
	 *            Le membre souhaitant récupéré ses profils
	 * @param paramString
	 *            le tri
	 * @return Une liste d'objet de la base
	 */
	private <T> List<T> getProfilASSMATList(Class<T> paramClass, Member paramMember, String paramString) {
		if (paramMember == null || paramMember.getId() == null) {
			return null;
		}
		List localList = HibernateUtil.query(paramClass, "authorId", paramMember.getId(), paramString);
		return localList;
	}
	
 /**
  * Met à jour le champ longitute et latitude des assmat si celui-ci est à 0
  * @param profil
  */
	public void UpdateLontLatProfilAssmat(ProfilASSMAT profil) {
	  SolisManager solisMgr = SolisManager.getInstance();
   AssmatSolis assmatSolis = Util.getFirst(solisMgr.getAssmatSolisByNumAgrement(profil.getNum_agrement()));
   
   if(assmatSolis == null) {
     logger.warn("Le profil " + profil.getId() + " n'éxiste plus dans la table solis et ne peut pas être mis à jour");
   }else {
     if(profil.getLongitudeAssmat() == 0 && profil.getLatitudeAssmat() == 0 && assmatSolis.getLongitude() != null && assmatSolis.getLatitude() != null) {
       profil.setLongitudeAssmat(assmatSolis.getLongitude().doubleValue());
       profil.setLatitudeAssmat(assmatSolis.getLatitude().doubleValue());
     }
     if(profil.getLongitudeMAM() == 0 && profil.getLatitudeMAM() == 0 && assmatSolis.getLongitudeMam() != null && assmatSolis.getLatitudeMam() != null) {             
       profil.setLongitudeMAM(assmatSolis.getLongitudeMam().doubleValue());
       profil.setLatitudeMAM(assmatSolis.getLatitudeMam().doubleValue());
     }
   }
	}
	
	/**
	 * Permets de créer les contenus de la recherche en fonction du profil
	 * @param profil
	 */
	public void createSearch(ProfilASSMAT profil, AssmatSolis assmatSolis){
		boolean exerceDomicile = SolisManager.getInstance().exerceDomicile(profil.getNum_agrement());
		boolean exerceMAM = SolisManager.getInstance().exerceMAM(profil.getNum_agrement());

		// Si l'AM exerce à domicile
		if(exerceDomicile){
			AssmatSearch assmatSearch = new AssmatSearch();
			setAssmatSearchValue(profil, assmatSearch, assmatSolis, true);
		}

		// Si l'AM exerce en MAM 
		if(exerceMAM){
			AssmatSearch assmatSearch = new AssmatSearch();
			setAssmatSearchValue(profil, assmatSearch, assmatSolis, false);
		}
	}
 
	/**
	  * 
	  * Mise a jour des champs assmatSearch en fonction du profil
	  * 
	  * @param profil
	 * @param assmatSolis 
	  */
	 private void setAssmatSearchValue(ProfilASSMAT profil, AssmatSearch assmatSearch, AssmatSolis assmatSolis, boolean exerciceDomicile) {

	   if (Util.notEmpty(assmatSearch)) {
	     // Accueil periscolaire
	//     if (Util.notEmpty(profil.getAccueilPeriscolaire())) {
	       assmatSearch.setAccueilPeriscolaire(AssmatUtil.getBooleanFromString(profil.getAccueilPeriscolaire()));
	 //    }

	     // Accueil le mercredi
	  //   if (Util.notEmpty(profil.getAccueilMercredi())) {
	       assmatSearch.setAccueilMercredi(AssmatUtil.getBooleanFromString(profil.getAccueilMercredi()));
	   //  }

	     // Accueil les vacances
	    //    if (Util.notEmpty(profil.getAccueilPendantLesVacancesSco())) {
	       assmatSearch.setAccueilVacances(AssmatUtil.getBooleanFromString(profil.getAccueilPendantLesVacancesSco()));
	    //    }

	     // Accueil avant 7h
	    //    if (Util.notEmpty(profil.getAvant7h())) {
	       assmatSearch.setAccueilAvant7h(AssmatUtil.getBooleanFromString(profil.getAvant7h()));
	    //    }

	     // Accueil apres 20h
	    //   if (Util.notEmpty(profil.getApres20h())) {
	       assmatSearch.setAccueilApres20h(AssmatUtil.getBooleanFromString(profil.getApres20h()));
	    //    }

	     // Accueil samedi
	    //   if (Util.notEmpty(profil.getLeSamedi())) {
	       assmatSearch.setAccueilSamedi(AssmatUtil.getBooleanFromString(profil.getLeSamedi()));
	    //    }

	     // Accueil dimanche
	    //    if (Util.notEmpty(profil.getLeDimanche())) {
	       assmatSearch.setAccueilDimanche(AssmatUtil.getBooleanFromString(profil.getLeDimanche()));
	    //     }

	     // Accueil nuit
	    //     if (Util.notEmpty(profil.getLaNuit())) {
	       assmatSearch.setAccueilNuit(AssmatUtil.getBooleanFromString(profil.getLaNuit()));
	    //      }

	     // Accueil atypique
	    //   if (Util.notEmpty(profil.getHorairesAtypiques())) {
	       assmatSearch.setAccueilAtypique(AssmatUtil.getBooleanFromString(profil.getHorairesAtypiques()));
	    //   }

	     // spec handicap
	    //     if (Util.notEmpty(profil.getAccueilEnfantHandicap())) {
	       assmatSearch.setSpecHandicape(AssmatUtil.getBooleanFromString(profil.getAccueilEnfantHandicap()));
	    //     }

	     // Spec partiel
	    //     if (Util.notEmpty(profil.getAccueilTempsPartiel())) {
	       assmatSearch.setSpecPartiel(AssmatUtil.getBooleanFromString(profil.getAccueilTempsPartiel()));
	    //    }

	     // depannage
	    //     if (Util.notEmpty(profil.getAccepteDepannage())) {
	       assmatSearch.setSpecDepannages(AssmatUtil.getBooleanFromString(profil.getAccepteDepannage()));
	    //     }

	     // nom
	     if (Util.notEmpty(profil.getAuthor())) {
	       assmatSearch.setNomAssmat(profil.getAuthor().getName());
	     }

	     // id member jcms
	     if (Util.notEmpty(profil.getAuthor())) {
	       assmatSearch.setMembreJcmsId(profil.getAuthor().getId());
	     }

	     // accepte contact
	     if (Util.notEmpty(profil.getAfficherContactUniquementSiD())) {
	       assmatSearch.setAccepteContact(profil.getAfficherContactUniquementSiD());
	     }

	     // profil id
	     if (Util.notEmpty(profil.getId())) {
	       assmatSearch.setDbprofilId(profil.getId());
	     }
	     
	     //jrowid 
	     if(Util.notEmpty(profil.getRowId())){
	    	 assmatSearch.setJRowProfilam(profil.getRowId());
	     }
	     
	     if(Util.notEmpty(assmatSolis.getJRowId())){
	    	 assmatSearch.setJRowSolisId(assmatSolis.getJRowId());
	     }
	     
	     if(exerciceDomicile){
	    	 if(Util.notEmpty(assmatSolis.getIdMicroQuartierDom())){
	    		 assmatSearch.setIdMicroQuartier(assmatSolis.getIdMicroQuartierDom());
	    	 }
	     } else if(Util.notEmpty(assmatSolis.getIdMicroQuartierMam())){
	    	 assmatSearch.setIdMicroQuartier(assmatSolis.getIdMicroQuartierMam());
	     }
	    

	     // Performe la commune par rapport a son code insee
	     if(Util.notEmpty(profil.getLatitudeAssmat()) && Util.notEmpty(profil.getLongitudeAssmat()) && exerciceDomicile && Util.notEmpty(assmatSolis.getCodeInsee())){
	    	 assmatSearch.setCommune(Integer.parseInt(assmatSolis.getCodeInsee()));
	    	 assmatSearch.setLatitude(profil.getLatitudeAssmat());
	    	 assmatSearch.setLongitude(profil.getLongitudeAssmat());
	    	 assmatSearch.setGeometry(AssmatUtil.getGeometry(profil.getLatitudeAssmat(), profil.getLongitudeAssmat()));
	     } else if(Util.notEmpty(profil.getLatitudeMAM()) && Util.notEmpty(profil.getLongitudeMAM()) && !exerciceDomicile ){
	    	 assmatSearch.setCommune(Integer.parseInt(assmatSolis.getCodeInseeMam()));
	    	 assmatSearch.setLatitude(profil.getLatitudeMAM());
	    	 assmatSearch.setLongitude(profil.getLongitudeMAM());
	    	 assmatSearch.setGeometry(AssmatUtil.getGeometry(profil.getLatitudeMAM(), profil.getLongitudeMAM()));
	     }

	     assmatSearch.setIsDomicile(exerciceDomicile);

	     HibernateCD44Util.add(assmatSearch);
	   }
	 }
	 
 /**
  * Met à jour la table assmat search par rapport à un contenu assmat
  * Champs mis à jour : Latitude, Longitude, Lieu domicile, Lieu MAM, Nom assmat
  * @param mbr
  * @param assmatSolis
  */
	 public void updateSearch(ProfilASSMAT profilAM, AssmatSolis assmatSolis,boolean exerceDomicile) {
		 AssmatSearch search = Util.getFirst(AssmatSearchDAO.getAssmatSearchByIdMembreTypeGarde(profilAM.getAuthor().getId(),exerceDomicile));

		 if(Util.notEmpty(search)){
			 setAssmatSearchValue(profilAM, search, assmatSolis, exerceDomicile);
			 
			 HibernateCD44Util.update(search);
		 } else {
			 createSearch(profilAM,assmatSolis);
		 }
	 }
  
}
