package fr.cg44.plugin.assmat.selector;

import generated.FicheLieu;

import com.jalios.jcms.Category;
import com.jalios.jcms.Channel;
import com.jalios.jcms.Data;
import com.jalios.jcms.DataSelector;
import com.jalios.util.Util;

/**
 * Ce sélecteur permet de ne choisir que les contenus de type Place qui sont catégorisés sur la categorie passé en parametre
 */
public class PlaceCategSelector implements DataSelector
{
	private final String  idCateg;

	/**
	 * Initialisation des données de recherche.
	 * 
	 * @param idCateg  idCateg 
	 */
	public PlaceCategSelector(String idCateg)
	{
		this.idCateg = idCateg;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSelected(Data data) {
		boolean selected= false;
		if(Util.notEmpty(idCateg)){
		  Category categRAM = Channel.getChannel().getCategory(idCateg);
			if (data instanceof FicheLieu) {
			  FicheLieu place = (FicheLieu) data;
				if(Util.notEmpty( place.getCategorySet())){
				  if( place.getCategorySet().contains(categRAM)){
				    selected = true;
				  }
				}
			}
		} 
		return selected;
	}
}
