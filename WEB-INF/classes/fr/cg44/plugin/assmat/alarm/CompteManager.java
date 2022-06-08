package fr.cg44.plugin.assmat.alarm;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.jalios.jcms.Channel;
import com.jalios.jcms.Group;
import com.jalios.jcms.Member;
import com.jalios.util.Util;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.managers.ProfilManager;
import generated.Disponibilite;
import generated.ProfilASSMAT;

public class CompteManager {

	private static final Logger logger = Logger.getLogger(CompteManager.class);

	private static CompteManager manager = null;

	private static Channel channel = Channel.getChannel();

	public static CompteManager getInstance() {
		if (Util.isEmpty(manager)) {
			manager = new CompteManager();
		}
		return manager;
	}

	// Envoie une relance aux membres qui ne se sont pas connecté depuis plus de 6 mois
	public void runRelance(){
		// récupération de tout les membres
		TreeSet<Member> members = channel.getAllDataSet(Member.class);
		// récupération du groupe des ass.mat.
		Group assmat = AssmatUtil.getGroupAssmat();
		// récupération du profilManager pour récupérer le profil AM
		ProfilManager profilManager = ProfilManager.getInstance();

		for(Member itMember: members){
			// traitement que si le membre appartient au groupe des assmat
			if(itMember.belongsToGroup(assmat)){
				if(Util.notEmpty(itMember.getLastLoginDate())){
					// récupétation de la date de dernière connection
					Calendar lastConnect = new GregorianCalendar();
					lastConnect.setTime(itMember.getLastLoginDate());

					Calendar today = new GregorianCalendar();

					// calcul de la différence de temps entre deux dates
					int diffYear = today.get(Calendar.YEAR) - lastConnect.get(Calendar.YEAR);
					int diffMonth = diffYear * 12 + today.get(Calendar.MONTH) - lastConnect.get(Calendar.MONTH);
					int diffDay = diffMonth * 30 + today.get(Calendar.DAY_OF_MONTH) - lastConnect.get(Calendar.DAY_OF_MONTH);

					// si le membre ne s'est pas connecté depuis 6 mois (180 jours)
					if(diffDay >= 180){
						ProfilASSMAT profil = profilManager.getProfilASSMAT(itMember);

						// récupération du sujet du mail
						String subject = AssmatUtil.getMessage("MAIL-RELANCE-SUBJECT");

						// ajout du contenu du mail
						String[] parameters = new String[]{};
						parameters = (String[]) Util.insertArray(parameters, 0, itMember.getFirstName());
						parameters = (String[]) Util.insertArray(parameters, 1, itMember.getName());
						parameters = (String[]) Util.insertArray(parameters, 2, channel.getUrl());
						String content = AssmatUtil.getMessagePropertiesParametersValues("MAIL-RELANCE-CONTENT", parameters);

						// envoie du mail
						String from = AssmatUtil.getDefaultEmail();
						AssmatUtil.sendMessage(profil, subject, content, from);
					}
				}
			}

		}
	}

	// Envoie d'un mail 1 mois avant la future dispo
	public void runAvailable(){
		// récupération de tout les membres
		TreeSet<Member> members = channel.getAllDataSet(Member.class);
		// récupération du groupe des ass.mat.
		Group assmat = AssmatUtil.getGroupAssmat();
		// récupération du profilManager pour récupérer le profil AM
		ProfilManager profilManager = ProfilManager.getInstance();

		for(Member itMember: members){
			// traitement que si le membre appartient au groupe des assmat
			if(itMember.belongsToGroup(assmat)){
				// récupération du profilAssmat lié au membre
				ProfilASSMAT profil = profilManager.getProfilASSMAT(itMember);
				// récupérations de toutes les dispos
				TreeSet<Disponibilite> dispos =  channel.getAllPublicationSet(Disponibilite.class, itMember);

				for(Disponibilite itDispo: dispos){
					if(itDispo.getAuthor().equals(itMember)){
						String etatDispo = itDispo.getEtatDispo();
						if(etatDispo.equals(AssmatUtil.SelectionEtatDispo.FUTURE.getValue()) && Util.notEmpty(itDispo.getDateDispoPlaceFuture())){
							// récupétation de la date de futur dispo
							Calendar availability = new GregorianCalendar();
							availability.setTime(itDispo.getDateDispoPlaceFuture());
	
							Calendar today = new GregorianCalendar();
	
							// calcul de la différence de temps entre deux dates
							int diffYear = availability.get(Calendar.YEAR) - today.get(Calendar.YEAR);
							int diffMonth = diffYear * 12 + availability.get(Calendar.MONTH) - today.get(Calendar.MONTH);
							int diffDay = diffMonth * 30 + availability.get(Calendar.DAY_OF_MONTH) - today.get(Calendar.DAY_OF_MONTH);

							// si la dispo est dans un mois (30 jours)
							if(diffDay == 30){
								// récupération du sujet du mail
								String subject = AssmatUtil.getMessage("MAIL-AVAILABLE-SUBJECT");
	
								// ajout du contenu du mail
								String[] parameters = new String[]{};
								parameters = (String[]) Util.insertArray(parameters, 0, itMember.getFirstName());
								parameters = (String[]) Util.insertArray(parameters, 1, itMember.getName());
								String content = AssmatUtil.getMessagePropertiesParametersValues("MAIL-AVAILABLE-CONTENT", parameters);
	
								// envoie du mail
								String from = AssmatUtil.getDefaultEmail();
								AssmatUtil.sendMessage(profil, subject, content, from);
							}
						}
					}
				}
			}
		}
	}
}
