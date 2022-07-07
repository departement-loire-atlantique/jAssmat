package fr.cg44.plugin.assmat.listener;

import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PreUpdateEvent;

import com.jalios.jcms.Channel;
import com.jalios.jcms.Member;
import com.jalios.jcms.db.BasicDBListener;
import com.jalios.jcms.db.DBData;
import com.jalios.jcms.plugin.PluginComponent;
import com.jalios.util.Util;

import generated.Disponibilite;
import generated.ProfilASSMAT;

/**
 * {@link DBListener} permettant d'éviter que l'auteur d'un contenu passe à
 * vide. En effet dans un environnement Jsync, nous avons identifié un problème
 * lié au Jsync et aux traitements des {@link DBEventLog} A l'activation d'un
 * compte, nous créeons un {@link ProfilASSMAT} et des {@link Disponibilite}. Ce
 * qui a pour effet de créer des {@link DBEventLog} pour qu'ils soient traités
 * par les autres instances Malheureusement le Jsync prenant un peu plus de
 * temps que prévu, il se peut que le {@link DBEventLog} soit taité avant la
 * réplication du membre. Dans ce cas l'auteur récupéré est null car pas encore
 * dans le store des réplicas. Il est donc performé null comme auteur.
 * 
 * @author tlegat
 *
 */
public class DBListenerAuthor extends BasicDBListener implements PluginComponent {
	@Override
	public void onPreUpdate(DBData data, PreUpdateEvent event) {
		if (data instanceof ProfilASSMAT) {
			ProfilASSMAT profil = (ProfilASSMAT) data;
			if (Util.isEmpty(profil.getAuthor())) {
				Object[] tab = event.getOldState();
				if (Util.notEmpty(tab) && tab.length > 3) {
					String idAuthor = (String) tab[2];
					Member author = Channel.getChannel().getMember(idAuthor);
					if (Util.notEmpty(author)) {
						profil.setAuthor(author);
					}
				}
			}
			data = profil;
		}

		if (data instanceof Disponibilite) {
			Disponibilite dispo = (Disponibilite) data;
			if (Util.isEmpty(dispo.getAuthor())) {
				Object[] tab = event.getOldState();
				if (Util.notEmpty(tab) && tab.length > 3) {
					String idAuthor = (String) tab[2];
					Member author = Channel.getChannel().getMember(idAuthor);
					if (Util.notEmpty(author)) {
						dispo.setAuthor(author);
					}
				}
			}
			data = dispo;
		}

		super.onPreUpdate(data, event);
	}
}