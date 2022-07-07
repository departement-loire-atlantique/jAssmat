package fr.cg44.plugin.assmat.selector;

import generated.InscriptionAM;

import com.jalios.jcms.Data;
import com.jalios.jcms.DataSelector;
import com.jalios.util.Util;

/**
 * Ce sélecteur permet de ne choisir que les contenus de type InscriptionAM qui
 * correspondent au numéro d'agrement passé en parametres
 */
public class InscriptionAMSelectorByNumAgrement implements DataSelector {
	private int numeroAgrement;

	/**
	 * Initialisation des données de recherche.
	 * 
	 * @param numeroAgrement
	 *            numero de dossier à rechercher
	 */
	public InscriptionAMSelectorByNumAgrement(int numeroAgrement) {
		this.numeroAgrement = numeroAgrement;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSelected(Data data) {

		if (!(data instanceof InscriptionAM))
			return false;

		if (Util.isEmpty(numeroAgrement))
			return false;

		// Selection en fonction du numero d'agrement
		InscriptionAM inscription = (InscriptionAM) data;
		if (numeroAgrement == inscription.getNumeroDagrement()) {
			return true;
		}

		return false;

	}
}