package fr.cg44.plugin.assmat.selector;



import generated.City;

import com.jalios.jcms.Data;
import com.jalios.jcms.DataSelector;
import com.jalios.util.Util;

/**
 * Ce sélecteur permet de ne choisir que les contenus de type City qui correspondent à l'idImport passé en parametres
 */
public class CommuneImportIdSelector implements DataSelector
{
	private final String  idImport;

/**
 * SetterCommune
 * @param idImport
 */
	public CommuneImportIdSelector(String idImport)
	{
		this.idImport = idImport;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSelected(Data data) {
		boolean selected= false;
		if(Util.notEmpty(idImport)){
			if (data instanceof City) {
			  City city = (City) data;
				if(idImport.equalsIgnoreCase(city.getImportId())){
					selected = true;
				}
			}
		} 
		return selected;
	}
}
