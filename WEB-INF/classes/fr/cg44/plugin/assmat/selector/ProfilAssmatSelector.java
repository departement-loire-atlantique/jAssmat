package fr.cg44.plugin.assmat.selector;

import generated.ProfilASSMAT;
import com.jalios.jcms.Data;
import com.jalios.jcms.DataSelector;
import com.jalios.jcms.Member;
import com.jalios.util.Util;

/**
 * Ce sélecteur permet de ne choisir que les contenus de type ProfilAssmat qui correspondent au membre ou numero d'agrement passé en parametres
 */
public class ProfilAssmatSelector implements DataSelector
{
	private  int  numeroAgrement;
	private Member member;
	/**
	 * Initialisation des données de recherche.
	 * 
	 * @param numeroAgrement  numero de dossier à rechercher
	 */
	public ProfilAssmatSelector(int numeroAgrement)
	{
		this.numeroAgrement = numeroAgrement;
	}

	public ProfilAssmatSelector(Member member)
	{
		this.member = member;
	}


	/**
	 * {@inheritDoc}
	 */
	public boolean isSelected(Data data) {
		boolean selected= false;

		//Selection en fonction du numero d'agrement
		if(Util.notEmpty(numeroAgrement)){
			if (data instanceof ProfilASSMAT) {
				ProfilASSMAT profil = (ProfilASSMAT) data;
				if(numeroAgrement==profil.getNum_agrement()){
					selected = true;
				}
			}
		} 

		//Selection en fonction du membre
		if(Util.notEmpty(member)){
			if (data instanceof ProfilASSMAT) {
				ProfilASSMAT profil = (ProfilASSMAT) data;
				if(member==profil.getAuthor()){
					selected = true;
				}
			}
		}

		return selected;
	}
}
