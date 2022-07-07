package fr.cg44.plugin.assmat.datacontroller;

import static com.jalios.jcms.Channel.getChannel;
import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.AssmatUtil.SelectionLogin;
import fr.cg44.plugin.assmat.AssmatUtil.SelectionPreferenceReception;
import fr.cg44.plugin.assmat.beans.AssmatSearch;
import fr.cg44.plugin.assmat.hibernate.HibernateCD44Util;
import fr.cg44.plugin.assmat.managers.AssmatSearchDAO;
import fr.cg44.plugin.assmat.managers.ProfilManager;
import fr.trsb.cd44.solis.beans.AssmatSolis;
import fr.trsb.cd44.solis.manager.SolisManager;
import generated.ProfilASSMAT;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jalios.jcms.BasicDataController;
import com.jalios.jcms.ControllerStatus;
import com.jalios.jcms.Data;
import com.jalios.jcms.Member;
import com.jalios.util.Util;

/**
* Synchronise le membre JCMS et la table de recherche solis lors de la
* modification d'un profil AM
* 
* - Si le mail ou téléphone change dans profil AM et que celui-ci est utilisé
* en login alors met à jour le logion du membre JCMS
* 
* @author m.formont
* 
*/
public class ProfilASSMATDataController extends BasicDataController {

 private static final Logger logger = Logger.getLogger(ProfilASSMATDataController.class);
  
 @Override
 public void afterWrite(Data data, int op, Member mbr, Map map) {

   ProfilASSMAT profil = (ProfilASSMAT) data;

   // création du profil
   if (op == OP_CREATE) {
      createAssmatSearch(profil);
   }

   // Mise a joru du profil
   if (op == OP_UPDATE) {
     updateMemberLogin(profil);
     sendMail(profil, map);

     updateAssmatSearch(profil);
   }
   
   // suppression du profil
   if (op == OP_DELETE) {
     deleteAssmatSearch(profil);
   }
 }

 /**
  * Méthode permettant de creer l'entre dans AssmatSearch correspondant a un
  * profilAM
  * 
  * @param profil
  */
 public void createAssmatSearch(ProfilASSMAT profil) {
   Member member = profil.getAuthor();
   SolisManager solisMgr = SolisManager.getInstance();
   //AssmatSolis assmatSolis = Util.getFirst(solisMgr.getAssmatSolisByNameFirstname(profil.getAuthor().getLastName(), profil.getAuthor().getFirstName()));      
   AssmatSolis assmatSolis = Util.getFirst(solisMgr.getAssmatSolisByNumAgrement(profil.getNum_agrement()));
   
   if(assmatSolis == null || member == null) {
     logger.warn("Le profil " + profil.getId() + " n'éxiste plus dans la table solis et ne peut pas être recherchable");
   }else if (Util.isEmpty(AssmatSearchDAO.getAssmatSearchByIdMembre(member.getId()))) {
	   ProfilManager.getInstance().createSearch(profil,assmatSolis);
   }
 }
 
 /**
  * Lors d'une mise a jour d'un profil assmat
  * 
  * @param profil
  */
 public void updateAssmatSearch(ProfilASSMAT profil) {

	 SolisManager solisMgr = SolisManager.getInstance();
	 //AssmatSolis assmatSolis = Util.getFirst(solisMgr.getAssmatSolisByNameFirstname(profil.getAuthor().getLastName(), profil.getAuthor().getFirstName()));
	 AssmatSolis assmatSolis = Util.getFirst(solisMgr.getAssmatSolisByNumAgrement(profil.getNum_agrement()));
	 if(Util.notEmpty(assmatSolis)){
		 if(assmatSolis.getExerceDomicile() != null && assmatSolis.getExerceDomicile()){
			 ProfilManager.getInstance().updateSearch(profil, assmatSolis,true);
		 } else {
			 List<AssmatSearch> listeSearch = AssmatSearchDAO.getAssmatSearchByIdMembreTypeGarde(profil.getId(),true);
			 deleteListSearch(listeSearch);
		 }
		 if(assmatSolis.getExerceMam() != null && assmatSolis.getExerceMam()){
			 ProfilManager.getInstance().updateSearch(profil, assmatSolis,false);
		 } else {
			 List<AssmatSearch> listeSearch = AssmatSearchDAO.getAssmatSearchByIdMembreTypeGarde(profil.getId(),false);
			 deleteListSearch(listeSearch);
		 }
	 }
 }
 
 /**
  * Supprime la ligne dans assmatSearch correspondant au profilAm
  * 
  * @param profil
  */
 public void deleteAssmatSearch(ProfilASSMAT profil) {

	 // on recupere la ligne dans la table
	 if(Util.notEmpty(profil) && Util.notEmpty(profil.getAuthor())){
		 List<AssmatSearch> listeAssmat = AssmatSearchDAO.getAssmatSearchByIdMembre(profil.getAuthor().getId());
		 if(Util.notEmpty(listeAssmat)){
			 for(AssmatSearch assmat : listeAssmat){
				 if(Util.notEmpty(assmat)){
					 HibernateCD44Util.delete(assmat);
				 }
			 }
		 }
	 }
 }

 private void deleteListSearch(List<AssmatSearch> list){
	 if(Util.notEmpty(list)){
		 for(AssmatSearch search:list){
			 HibernateCD44Util.delete(search);
		 }
	 }
 }
 


 
 
 /**
  * Si le champ correspondant au type de login du profil AM de correspond pas
  * au login du membre JCMS Met le login du membre JCMS à jour
  * 
  * @param profil
  */
 public void updateMemberLogin(ProfilASSMAT profil) {
   ProfilManager profilManager = ProfilManager.getInstance();
   Member member = profilManager.getMember(profil);
   String loginMember = member.getLogin();
   // Récupère le login renseigné dans le profil AM suivant le choix Type login
   String loginProfil = profilManager.getLogin(profil);
   if (!loginMember.equals(loginProfil) && Util.notEmpty(loginProfil)) {
     Member clone = (Member) member.clone();
     clone.setLogin(loginProfil);
     clone.performUpdate(getChannel().getDefaultAdmin());
     // Envoi d'un message de changement de login
     String subject = AssmatUtil.getMessagePropertiesParametersValues("INSCRIPTION-PROFIL-CHANGE-LOGIN-SUBJECT-MAIL", new String[] { member.getSalutation(),
         member.getName(), member.getFirstName(), member.getLogin() });
     String content = AssmatUtil.getMessagePropertiesParametersValues("INSCRIPTION-PROFIL-CHANGE-LOGIN-CONTENT-MAIL", new String[] { member.getSalutation(),
         member.getName(), member.getFirstName(), member.getLogin() });
     if (SelectionPreferenceReception.TELEPHONE.getValue().equals(profil.getCanalDeCommunicationSite())) {
       content = AssmatUtil.getMessagePropertiesParametersValues("INSCRIPTION-PROFIL-CHANGE-LOGIN-CONTENT-SMS", new String[] { member.getSalutation(),
           member.getName(), member.getFirstName(), member.getLogin() });
     }
     
     String from = AssmatUtil.getDefaultEmail();
     AssmatUtil.sendMessage(profil, subject, content, from);
   }
 }

 /**
  * Contrôle l'intégrité du profil AM
  */
 @Override
 public ControllerStatus checkIntegrity(Data data) {
   ProfilASSMAT profil = (ProfilASSMAT) data;

   // Vérifie si suivant le type de login choisi celui-ci est bien renseigné
   String typeLogin = profil.getTypeLogin();
   if (SelectionLogin.TELEPHONE.getValue().equals(typeLogin) && Util.notEmpty(profil.getAuthor()) && Util.isEmpty(profil.getAuthor().getMobile())) {
     return new ControllerStatus(AssmatUtil.getMessage("PROFIL-ERROR-LOGIN-TEL"));
   }
   
   // Si le numéro de téléphone fix est renseigné et n'est pas au bon format
   if (Util.notEmpty(profil.getTelephoneFixe()) && !AssmatUtil.checkPhone(profil.getTelephoneFixe())) {
     return new ControllerStatus(AssmatUtil.getMessagePropertiesParameters("CONTACT-TEL-VALIDITE", new String[] { "CONTACTS-TEL-FIX-HTML" }));
   }

   // Si le canal de communication choisi est téléphone et que celui-ci n'est
   // pas renseigné
   if (Util.notEmpty(profil.getCanalDeCommunicationSite())
       && profil.getCanalDeCommunicationSite().equals(AssmatUtil.SelectionPreferenceReception.TELEPHONE.getValue())
       && Util.notEmpty(profil.getAuthor()) && Util.isEmpty(profil.getAuthor().getMobile())) {
     return new ControllerStatus(AssmatUtil.getMessage("PROFIL-ERROR-CANAL-TEL"));
   }
    
   // Si le canal de communication choisi est email et que celui-ci n'est pas renseigné
   if(Util.notEmpty(profil) && Util.notEmpty(profil.getCanalDeCommunicationSite()) && profil.getCanalDeCommunicationSite().equals(AssmatUtil.SelectionPreferenceReception.MAIL.getValue()) && Util.notEmpty(profil.getAuthor()) && Util.isEmpty(profil.getAuthor().getEmail())){
    return new ControllerStatus(AssmatUtil.getMessage("PROFIL-ERROR-CANAL-MAIL"));
   }

   return super.checkIntegrity(data);
 }

 /**
  * Envoie des emails
  */
 private void sendMail(ProfilASSMAT profil, Map map){
	// envoie de mail ou de sms après changements
	// récupération du profil avant modification
	ProfilASSMAT previousProfil = (ProfilASSMAT) map.get(CTXT_PREVIOUS_DATA);

	// Récupération de l'email du membre
	String mail = profil.getAuthor().getEmail();
	if(Util.notEmpty(previousProfil)){
		// si les contacts (tel ou email) et leur visibilité ont changé
		// si la map contient la valeur "true" pour la clé "isUpdateContact" cela veut dire que la modification se fait après le parametrage, donc envoie du mail
		if(map.containsKey("isUpdateContact") && map.get("isUpdateContact").equals("true")){
			// on envoie un mail au membre, donc besoin de l'adresse mail
			if(Util.notEmpty(mail)){
				sendUpdate(profil, mail);
			}
		} else {
			sendUpdateCanalComm(profil, previousProfil, mail);
		}
	}
 }
 
 /**
  * Envoie un message lors de la modification des contacts et leur visibilite
  */
 private void sendUpdate(ProfilASSMAT profil, String mail){
	String content="";
	String[] parameters = new String[]{};
	boolean asInfoModif =false;
	// création du paramètre pour afficher les informations visibles sur le site
	StringBuilder sb = new StringBuilder();
	// telephone fixe
	if(Util.notEmpty(profil.getVisbiliteTelephoneFixe()) && profil.getVisbiliteTelephoneFixe().equals("true") && Util.notEmpty(profil.getTelephoneFixe())){
		sb.append(AssmatUtil.getMessage("CONTACT-TEL-HTML") + profil.getTelephoneFixe() + "<br />");
		asInfoModif = true;
	}
	// telephone portable
	if(Util.notEmpty(profil.getVisibiliteTelephonePortable()) && profil.getVisibiliteTelephonePortable().equals("true") && Util.notEmpty(profil.getAuthor().getMobile())){
		sb.append(AssmatUtil.getMessage("CONTACT-MOB-HTML") + profil.getAuthor().getMobile() + "<br />");
		asInfoModif = true;
	}
	// creneau horaire
	if(Util.notEmpty(profil.getCreneauHorairesDappel())){
		sb.append(AssmatUtil.getMessage("CONTACT-TEL-HOR-HTML") + profil.getCreneauHorairesDappel() + "<br />");
		asInfoModif = true;
	}
	// mail
	if(Util.notEmpty(profil.getVisibiliteAdresseEmail()) && profil.getVisibiliteAdresseEmail().equals("true") && Util.notEmpty(mail)){
		sb.append(AssmatUtil.getMessage("CONTACT-MAIL-HTML") + mail + "<br />");
		asInfoModif = true;
	}
	sb.append("<br />");
	// visibilite si dispo
	if(profil.getAfficherContactUniquementSiD()){
		sb.append(AssmatUtil.getMessage("MAIL-MODIFIER-CONTACTS-AFFICHER-DISPO-OUI") + "<br />");
	} else {
		sb.append(AssmatUtil.getMessage("MAIL-MODIFIER-CONTACTS-AFFICHER-DISPO-NON") + "<br />");
	}

	parameters = (String[]) Util.insertArray(parameters, 0, sb.toString());
	String MesContactLien = channel.getCategory("cra_60274").getDisplayUrl(Locale.FRANCE);
	parameters = (String[]) Util.insertArray(parameters, 1, "<a href=\"" + MesContactLien + "\">" + AssmatUtil.getMessage("MODIFIER-CHOIX") + "</a>");
	if(asInfoModif){
	  content = AssmatUtil.getMessagePropertiesParametersValues("MAIL-MODIFIER-CONTACTS-CONTENT",parameters);
	}else{
	  content = AssmatUtil.getMessage("MAIL-MODIFIER-CONTACTS-MASQUE-CONTENT");
	}
	String from = AssmatUtil.getDefaultEmail();
	String subject = AssmatUtil.getMessage("MAIL-MODIFIER-CONTACTS-SUBJECT");

	// Envoie du mail
	AssmatUtil.sendMail(mail, subject, content, from);
 }

 /**
  * Envoie un message lors du changement de canal de communication
  */
 private void sendUpdateCanalComm(ProfilASSMAT profil, ProfilASSMAT previousProfil, String mail){
	// si le canal à changer lors de la modification du membre
	if(Util.notEmpty(previousProfil.getCanalDeCommunicationSite()) && !previousProfil.getCanalDeCommunicationSite().equals(profil.getCanalDeCommunicationSite())){
	    String subject = "";
	    String content = "";
	    String adresseFrom = "";
	    if (SelectionPreferenceReception.MAIL.getValue().equals(profil.getCanalDeCommunicationSite())) {
	    	subject = AssmatUtil.getMessage("PARAMETRAGE-CANAL-COM-SUBJECT-MAIL");

	    	String[] parameters = new String[]{};
			parameters = (String[]) Util.insertArray(parameters, 0, mail);
			parameters = (String[]) Util.insertArray(parameters, 1, "<a href=\"" + channel.getUrl() + "\">" + channel.getUrl() + "</a>");
			parameters = (String[]) Util.insertArray(parameters, 2, channel.getUrl());
	    	content = AssmatUtil.getMessagePropertiesParametersValues("PARAMETRAGE-CANAL-COM-CONTENT-MAIL", parameters);

	    	adresseFrom = AssmatUtil.getMessage("MAIL-PARAMETRAGE-FROM-CONTENT");
	    } else if (SelectionPreferenceReception.TELEPHONE.getValue().equals(profil.getCanalDeCommunicationSite())) {
	    	String idMessage = "PARAMETRAGE-CANAL-COM-CONTENT-SMS";
	    	subject = idMessage;

	    	String[] parameters = new String[]{};
			parameters = (String[]) Util.insertArray(parameters, 0, profil.getAuthor().getMobile());
			parameters = (String[]) Util.insertArray(parameters, 1, channel.getUrl());
	    	content = AssmatUtil.getMessagePropertiesParametersValues(idMessage, parameters);

	    	adresseFrom = null;
		}
    	AssmatUtil.sendMessage(profil, subject, content, adresseFrom);
	}
 }
}