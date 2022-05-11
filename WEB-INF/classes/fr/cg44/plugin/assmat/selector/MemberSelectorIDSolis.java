package fr.cg44.plugin.assmat.selector;

import com.jalios.jcms.Data;
import com.jalios.jcms.DataSelector;
import com.jalios.jcms.Member;
import com.jalios.util.Util;

/**
 * Ce sélecteur permet de ne choisir que les Membres qui correspondent a l'ID solis passé en parametre
 */
public class MemberSelectorIDSolis implements DataSelector
{
	private final String  idSolis;
	
	/**
	 * Initialisation des données de recherche.
	 * 
	 * @param idSolis id a rechercher
	 */
	public MemberSelectorIDSolis(String idSolis )
	{
		this.idSolis= idSolis;
	}

	/**
	 * {@inheritDoc}
	 */
	
	
	
	public boolean isSelected(Data data) {
		boolean selected= false;

		if(Util.notEmpty(idSolis)){
			if (data instanceof Member) {
			  Member mbr = (Member) data;
				if(idSolis.equals(mbr.getExtraData("extra.Member.jcmsplugin.assmatplugin.idsolis.lieu"))){
					selected = true;
				}
			}
		} 
		return selected;
	}
}
