package fr.cg44.plugin.assmat.selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import generated.FicheLieu;

import com.jalios.jcms.Data;
import com.jalios.jcms.DataSelector;
import com.jalios.util.Util;

/**
 * Ce sélecteur permet de ne choisir que les contenus de type Place qui correspondent à l'idSolis passé en parametres
 */
public class PlaceSelector implements DataSelector
{
	private final String  idUa;

	/**
	 * Initialisation des données de recherche.
	 * 
	 * @param idSolis  idSolis à rechercher
	 */
	public PlaceSelector(String idUa)
	{
		this.idUa = idUa;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSelected(Data data) {
	  boolean selected= false;

   if(Util.notEmpty(idUa)){
     if (data instanceof FicheLieu) {
       FicheLieu place = (FicheLieu) data;
       if(Util.notEmpty(place)){
         
         List<String> listIdSolis = new ArrayList<String>();
         String idSolisId = place.getIdSolis();
         if(Util.notEmpty(idSolisId)) {
           String[] idSolisTab = idSolisId.split(" ");
           listIdSolis = Arrays.asList(idSolisTab);
           if(Util.notEmpty(listIdSolis) && listIdSolis.contains(idUa)) {
             selected = true;
           }
         }          
       }
     }
   } 
   return selected;
 }
}
