package fr.cg44.plugin.assmat.selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import generated.FicheLieu;

import com.jalios.jcms.Data;
import com.jalios.jcms.DataSelector;
import com.jalios.util.Util;

/**
 * Ce sélecteur permet de ne choisir que les contenus de type FicheLieu qui correspondent a l'ID solis passé en parametre
 */
public class RelaisMamSelectorIDSolis implements DataSelector
{
  private final String  idSolis;

  /**
   * Initialisation des données de recherche.
   * 
   * @param idSolis id a rechercher
   */
  public RelaisMamSelectorIDSolis(String idSolis )
  {
    this.idSolis= idSolis;
  }

  /**
   * {@inheritDoc}
   */



  public boolean isSelected(Data data) {
    boolean selected= false;

    if(Util.notEmpty(idSolis)){
      if (data instanceof FicheLieu) {
        FicheLieu place = (FicheLieu) data;
        if(Util.notEmpty(place)){
          
          List<String> listIdSolis = new ArrayList<String>();
          String idSolisId = place.getIdSolis();
          if(Util.notEmpty(idSolisId)) {
            String[] idSolisTab = idSolisId.split(" ");
            listIdSolis = Arrays.asList(idSolisTab);
            if(Util.notEmpty(listIdSolis) && listIdSolis.contains(idSolis)) {
              selected = true;
            }
          }          
        }
      }
    } 
    return selected;
  }
}
