package fr.cg44.plugin.assmat.datacontroller;

import static com.jalios.jcms.Channel.getChannel;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jalios.jcms.BasicDataController;
import com.jalios.jcms.ControllerStatus;
import com.jalios.jcms.Data;
import com.jalios.jcms.DataController;
import com.jalios.jcms.Member;
import com.jalios.jcms.context.JcmsMessage;
import com.jalios.jcms.context.JcmsMessage.Level;
import com.jalios.util.Util;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.AssmatUtil.SelectionLogin;
import fr.cg44.plugin.assmat.managers.ProfilManager;
import fr.trsb.cd44.solis.manager.SolisManager;
import generated.Disponibilite;
import generated.ProfilASSMAT;

/**
 * Synchronise le profil AM lors de la modification d'un membre
 * 
 * - Si le nom ou prénom du membre JCMS change alors le titre du profil AM est mis à jour en conséquence
 * - Si un membre est supprimé, son profil AM est aussi supprimé ainsi que toutes ses disponibilités
 * 
 * @author m.formont
 *
 */
public class MemberDataController extends BasicDataController {

	private static Logger logger = Logger.getLogger(MemberDataController.class);



	/**
	 * Les logins des membres Assmat ne peuvent pas etre modifié
	 * 
	 * 
	 */
	@Override
	public void beforeWrite(Data data, int op, Member mbr, Map map) {
	  Member membre = (Member) data; 

	  // mise a jour de l'organisation du membre à chaque modification du contenu
	  if(op == OP_UPDATE){
	    Member previousMember = (Member) map.get(DataController.CTXT_PREVIOUS_DATA);
	    
	    ProfilManager profilManager = ProfilManager.getInstance();  
	    ProfilASSMAT profil = profilManager.getProfilASSMAT(membre);
	    
	    // si le nom le prenom ou le login de la personne change on modifie son organization
	    if(Util.notEmpty(previousMember) && Util.notEmpty(profil) 
	    	&& (Util.notEmpty(membre.getName()) && !membre.getName().equals(previousMember.getName())  
	    			|| (Util.notEmpty(membre.getFirstName()) && !membre.getFirstName().equals(previousMember.getFirstName())) 
	    			|| (Util.notEmpty(membre.getMobile()) && membre.getMobile() != previousMember.getMobile()) 
	    			|| (Util.notEmpty(membre.getEmail()) && membre.getEmail()!= previousMember.getEmail()) )){
 
	      StringBuilder stbd = AssmatUtil.getOrganization(membre);

	      // mise à jour de l'organization du membre
	      membre.setOrganization(stbd.toString());
	    }
	  }
	}	 
	
	public void  afterWrite(Data data, int op, Member mbr, Map map) {    
		Member membre = (Member) data;              
		if (op == OP_UPDATE && Util.isEmpty(map.get("isForbiden"))) {               
			Member oldMember = (Member) map.get(DataController.CTXT_PREVIOUS_DATA);   
			UpdateMemberLogin(membre);
			
			// Si le nom ou le prénom du membre a changé
			// Met à jour le titre du contenu profil assmat par rapport au nom et prénom du membre
			UpdateProfilAM(membre, oldMember);

			updateDispo(membre, oldMember);  
		}       
		if(op == OP_DELETE){
			// Supprime le profil AM attaché au membre en cas de suppréssion de celui-ci
			DeleteProfilAM(membre);
			// Supprime les disponibilités attachées au membre en cas de suppréssion de celui-ci
			DeleteDispo(membre);
		}
	}

	/**
	 * On vérifie que le login voulu correspond bien au login renseigné sur le membre
	 * @param member
	 */
	public void UpdateMemberLogin(Member member) {   
		AssmatUtil.synchroLoginMember(member);
	}
	
	/**
	 * Supprime le profil AM attaché au membre
	 * @param membre
	 */
	public void DeleteProfilAM(Member membre) {
		ProfilManager profilManager = ProfilManager.getInstance();  
		ProfilASSMAT profilAssmat = profilManager.getProfilASSMAT(membre);
		if(Util.notEmpty(profilAssmat)) {
			profilAssmat.performDelete(getChannel().getDefaultAdmin());
		}
	}


	/**
	 * Supprime les disponibilités attachées au membre
	 * @param membre
	 */
	public void DeleteDispo(Member membre) {
		ProfilManager profilManager = ProfilManager.getInstance();
		List<Disponibilite> dispoJcms = profilManager.getDisponibilitesList(membre, null);
		if(Util.notEmpty(dispoJcms)) {
			for(Disponibilite itDispo : dispoJcms) {
				itDispo.performDelete(getChannel().getDefaultAdmin());
			}
		}
	}


	/**
	 * Si le nom ou le prénom du membre a changé
	 * Met à jour le titre du contenu profil assmat par rapport au nom et prénom du membre
	 * @param membre
	 * @param oldMember
	 */
	public void UpdateProfilAM(Member member, Member oldMember) {
		if(!isSameName(member, oldMember)) {
			ProfilManager profilManager = ProfilManager.getInstance();  
			ProfilASSMAT profilAssmat = profilManager.getProfilASSMAT(member);
			if(Util.notEmpty(profilAssmat)) {
				ProfilASSMAT profilAssmatClone = (ProfilASSMAT) profilAssmat.getUpdateInstance();				
				profilAssmatClone.setTitle(AssmatUtil.getTitleProfilAssmat(profilAssmatClone.getTitle(),member, profilAssmatClone.getCommune(), profilAssmatClone.getCommuneMam(), profilAssmatClone.getNum_agrement()+""));			
				profilAssmatClone.performUpdate(getChannel().getDefaultAdmin());
			}
		}
	}


	private void updateDispo(Member membre, Member oldMember) {
		if(!isSameName(membre, oldMember)) {
			List<Disponibilite> list = ProfilManager.getInstance().getDisponibilitesList(membre, null);
			if(Util.notEmpty(list)){
				ProfilManager profilManager = ProfilManager.getInstance();
				ProfilASSMAT profilAssmat = profilManager.getProfilASSMAT(membre);
				if(Util.notEmpty(profilAssmat)) {
					
				for (Disponibilite disponibilite : list) {
					Disponibilite dispoClone = (Disponibilite) disponibilite.getUpdateInstance();
					dispoClone.setTitle(AssmatUtil.getTitleDispo(disponibilite.getTitle(), membre, profilAssmat.getCommune(), profilAssmat.getCommuneMam(), profilAssmat.getNum_agrement()+"", disponibilite.getAgrement(), disponibilite.getTrancheDage()+""));
					dispoClone.performUpdate(getChannel().getDefaultAdmin());
				}
				}
			}

		}

	}

	
	
	
	
	/**
	 * Compare les noms et prénoms des deux membres
	 */
	public static Boolean isSameName(Member mbr1, Member mbr2) {
		return mbr1.getName().equals(mbr2.getName()) && mbr1.getFirstName().equals(mbr2.getFirstName());
	}

	@Override
	public ControllerStatus checkIntegrity(Data data) {
		if(data instanceof Member){
			Member membre = (Member) data;
			ProfilManager profilManager = ProfilManager.getInstance();
			ProfilASSMAT profil = profilManager.getProfilASSMAT(membre);
			if(Util.notEmpty(profil)){
				String typeLogin = profil.getTypeLogin();
				
				if (SelectionLogin.MAIL.getValue().equals(typeLogin) && Util.isEmpty(membre.getEmail())) {
					return new ControllerStatus(AssmatUtil.getMessage("PROFIL-ERROR-LOGIN-MAIL"));
				}
				
				// Si le canal de communication choisi est email et que celui-ci n'est pas renseigné
				if(Util.notEmpty(profil.getCanalDeCommunicationSite()) && profil.getCanalDeCommunicationSite().equals(AssmatUtil.SelectionPreferenceReception.MAIL.getValue()) && Util.isEmpty(membre.getEmail())){
					return new ControllerStatus(AssmatUtil.getMessage("PROFIL-ERROR-CANAL-MAIL"));
				}
				
				// Si le numéro de téléphone mobile est renseigné et n'est pas au bon format
	   if (Util.notEmpty(membre.getMobile()) && !AssmatUtil.checkPhone(membre.getMobile())) {
	     return new ControllerStatus(AssmatUtil.getMessagePropertiesParameters("CONTACT-TEL-VALIDITE", new String[] { "CONTACTS-TEL-PORTABLE-HTML" }));
	   }
	   
	   // Si le canal de communication choisi est téléphone et que celui-ci n'est
	   // pas renseigné
	   if (Util.notEmpty(profil.getCanalDeCommunicationSite())
	       && profil.getCanalDeCommunicationSite().equals(AssmatUtil.SelectionPreferenceReception.TELEPHONE.getValue())
	       && Util.isEmpty(membre.getMobile())) {
	     return new ControllerStatus(AssmatUtil.getMessage("PROFIL-ERROR-CANAL-TEL"));
	   }
				
				
			}
		}
		return super.checkIntegrity(data);
	}
}
