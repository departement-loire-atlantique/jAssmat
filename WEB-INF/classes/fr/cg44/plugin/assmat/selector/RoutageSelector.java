package fr.cg44.plugin.assmat.selector;

import generated.Routage;

import com.jalios.jcms.Category;
import com.jalios.jcms.Data;
import com.jalios.jcms.DataSelector;
import com.jalios.util.Util;

/**
 * Ce sélecteur permet de ne choisir que les contenus de type Routage qui sont catégorisersur la catégorie passée en param
 */
public class RoutageSelector implements DataSelector
{
	private final Category categ;

	/**
	 * Initialisation des données de recherche.
	 * 
	 * @param categ  Catégorie à rechercher
	 */
	public RoutageSelector(Category categ)
	{
		this.categ = categ;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSelected(Data data)
	{
		boolean selected= false;
		if (data instanceof Routage) {

			Routage rtg = (Routage) data;
			if(Util.notEmpty(rtg.getCategories())){

				//on itere sur les categorie du contenu
				for(Category itCateg :rtg.getCategories() ){
					if(itCateg.getName().equals(categ.getName())){
						selected = true;
					}
				}
			}
		}
		return selected;
	}
}
