package fr.cg44.plugin.assmat.datacontroller;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.AssmatUtil.SelectionEtatDispo;
import fr.cg44.plugin.assmat.beans.AssmatSearch;
import fr.cg44.plugin.assmat.beans.DispoAssmat;
import fr.cg44.plugin.assmat.hibernate.HibernateCD44Util;
import fr.cg44.plugin.assmat.managers.AssmatSearchDAO;
import generated.Disponibilite;

import java.util.List;
import java.util.Map;

import com.jalios.jcms.BasicDataController;
import com.jalios.jcms.Data;
import com.jalios.jcms.Member;
import com.jalios.util.Util;

/**
 *
 * 
 */
public class DispoASSMATDataController extends BasicDataController {

 

  @Override
  public void afterWrite(Data data, int op, Member mbr, Map map) {

    Disponibilite dispo = (Disponibilite) data;

    //  creation de la dispo
    if (op == OP_CREATE) {
      createDispoSearch(dispo);
    }
    
    // Mise a jour de la dispo
    if (op == OP_UPDATE) {
      updateDispoSearch(dispo);
    }

    // suppression de la dispo
    if (op == OP_DELETE) {
      deleteDispoSearch(dispo);
    }
  }

  /**
   * Supprime la ligne dans assmatSearch correspondant au profilAm
   * 
   * @param disponibilite
   */
  public void deleteDispoSearch(Disponibilite disponibilite) {

    // on recupere la ligne dans la table
    DispoAssmat dispo = Util.getFirst(AssmatSearchDAO.getDispoAssMatByJcmsId(disponibilite.getId()));
    
    if(Util.notEmpty(dispo)){
      HibernateCD44Util.delete(dispo);
    }
  }

  /**
   * Supprime les dispos correspondant a l'assmat search
   * 
   * @param disponibilite
   */
  public void deleteDispoSearchByAmSearch(AssmatSearch assmat) {

    // on recupere la ligne dans la table
    List<DispoAssmat> listDispo =   AssmatSearchDAO.getDispoAssMatByAMsearch(assmat);
    if(Util.notEmpty(listDispo)){
      HibernateCD44Util.delete(listDispo);
    }
  }
  
 
  
  
  /**
   * Lors d'une mise a jour d'un profil assmat
   * 
   * @param disponibilite
   */

  public void updateDispoSearch(Disponibilite disponibilite) {

    // on recupere la ligne dans la table
    DispoAssmat dispo = Util.getFirst(AssmatSearchDAO.getDispoAssMatByJcmsId(disponibilite.getId()));
    if(Util.notEmpty(dispo)){
      setDispoValue(disponibilite, dispo);
    }
 }

  /**
   * Méthode permettant de creer l'entre dans DispoAssmat correspondant a une disponibilite jcms
   * 
   * @param disponibilite
   */
  public void createDispoSearch(Disponibilite disponibilite) {
	  // Une disponibilité "exceptionnelle" n'a pas à être recherchable dans les résultats de recherche
	  if(disponibilite.getSaisieDisponible()){
	      // Si vide on creer une ligne dans la table AssmatSearch
	      DispoAssmat dispo = new DispoAssmat();
	      setDispoValue(disponibilite, dispo);
	  }
  }

  /**
   * 
   * Mise a jour des champs assmatSearch en fonction du profil
   * 
   * @param disponibilite
   * @param dispo
   */
  public void setDispoValue(Disponibilite disponibilite, DispoAssmat dispo) {

    if (Util.notEmpty(disponibilite) && Util.notEmpty(disponibilite.getAuthor())) {
      // Accueil periscolaire
     
      // tranche d'age
      if (Util.notEmpty(disponibilite.getTrancheDage())) {
        dispo.setTrancheAgeKey(disponibilite.getTrancheDage());
      }
      // Date de debut
      if (Util.notEmpty(disponibilite.getDateDispoPlaceFuture())) {
        dispo.setDateDebut(disponibilite.getDateDispoPlaceFuture()); 
      }

      // ID jcms
      if (Util.notEmpty(disponibilite.getId())) {
        dispo.setJcmsId(disponibilite.getId()); 
      }
      
      dispo.setDisponible(disponibilite.getEtatDispo());
      
      SelectionEtatDispo etatDispoEnum = SelectionEtatDispo.getEtatDispo(disponibilite.getEtatDispo());
      if(Util.notEmpty(etatDispoEnum) && etatDispoEnum.equals(SelectionEtatDispo.FUTURE)){
    	  dispo.setDateDebut(disponibilite.getDateDispoPlaceFuture());
      }      
 
      boolean domicile = AssmatUtil.isDisponibiliteDomicile(disponibilite);
      if(Util.notEmpty(AssmatSearchDAO.getAssmatSearchByIdMembreTypeGarde(disponibilite.getAuthor().getId(),domicile))){
    	AssmatSearch assmatsearch=Util.getFirst(AssmatSearchDAO.getAssmatSearchByIdMembreTypeGarde(disponibilite.getAuthor().getId(),domicile));
        dispo.setAssmatSearch(assmatsearch);
      }
     
      
     dispo.setAssmatId(disponibilite.getAuthor().getId()); 
    
  
      HibernateCD44Util.add(dispo);
    }

  }


}
