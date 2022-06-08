package fr.cg44.plugin.administrabletexteplugin.selector;

import generated.TexteParametrable;

import com.jalios.jcms.Data;
import com.jalios.jcms.DataSelector;

/**
 * Ce sélecteur permet de ne choisir que les contenus de type TexteParametrable
 * correspondant aux critères choisis
 */
public class TexteParametrableSelector implements DataSelector {
	private final String key;

	/**
	 * Initialisation des données de recherche.
	 * 
	 * @param key
	 *            Key à rechercher
	 */
	public TexteParametrableSelector(String key) {
		this.key = key;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSelected(Data data) {
		if (data instanceof TexteParametrable) {
			return ((TexteParametrable) data).getIdentifiantTechnique().equals(key);
		}
		return false;
	}
}
