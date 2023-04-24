package fr.cg44.plugin.assmat.selector;

import generated.City;
import generated.FicheLieu;

import com.jalios.jcms.Category;
import com.jalios.jcms.Data;
import com.jalios.jcms.DataSelector;
import com.jalios.util.Util;

/**
 * Ce sélecteur permet de ne choisir que les contenus de type Place qui correspondent a la commune passé en parametre
 */
public class RelaisMamSelectorCommune implements DataSelector
{
	private final City  city;
	private final Category  categ;
	/**
	 * Initialisation des données de recherche.
	 * 
	 * @param city  city à rechercher
	 */
	public RelaisMamSelectorCommune(City city, Category categ)
	{
		this.city = city;
		this.categ = categ;
	}

	/**
	 * {@inheritDoc}
	 */
	
	
	
	public boolean isSelected(Data data) {
		boolean selected= false;

		
		if(Util.notEmpty(city)){
			if (data instanceof FicheLieu) {
			  FicheLieu place = (FicheLieu) data;
				if(place.getCommune() == city || Util.arrayContains(place.getCommunes(), city)){
				  if(Util.arrayContains(place.getCategories(), categ))
					selected = true;
				}
			}
		} 
		return selected;
	}
}
