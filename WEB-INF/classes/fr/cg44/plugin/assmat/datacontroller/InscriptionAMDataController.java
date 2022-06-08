package fr.cg44.plugin.assmat.datacontroller;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.AssmatUtil.SelectionLogin;
import fr.cg44.plugin.assmat.AssmatUtil.SelectionPreferenceReception;
import fr.cg44.plugin.assmat.managers.ProfilManager;
import fr.cg44.plugin.assmat.selector.InscriptionAMSelectorByNumAgrement;
import fr.trsb.cd44.solis.beans.AssmatSolis;
import fr.trsb.cd44.solis.manager.SolisManager;
import generated.InscriptionAM;
import generated.ProfilASSMAT;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.jalios.jcms.BasicDataController;
import com.jalios.jcms.Data;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.Member;
import com.jalios.jcms.WorkflowConstants;
import com.jalios.util.ReflectUtil;
import com.jalios.util.Util;

/**
 * Créer le ProfilAM, le compte JCMS et les disponibilité de l'inscriptionAM
 *  et le log à la fin du traitement
 *
 * @author trsb
 *
 */
public class InscriptionAMDataController extends BasicDataController {

	private static final Logger logger = Logger.getLogger(InscriptionAMDataController.class);
	
	@Override
	/**
	 * On verifie si le type InscriptionAM passe de l'état brouillon à l'état publié
	 *
	 *
	 */
	public void afterWrite(Data data, int op, Member mbr, Map map) {

		// Mise a jours sur les inscription AM
		if (op == OP_UPDATE && data instanceof InscriptionAM) {
			InscriptionAM inscription = (InscriptionAM) data;

			if (inscription.getPstatus() == WorkflowConstants.PUBLISHED_PSTATUS) {	// Seulement si le pstatus est publié

				// On verifie le pstatus avant modification
				InscriptionAM oldInscription = (InscriptionAM) map.get(CTXT_PREVIOUS_DATA);
				// Si le pstatus etait bien a brouillon on commence le traitement
				if (oldInscription.getPstatus() == -100) {
					logger.debug("Mise à jour du contenu <"+oldInscription+"> / <"+oldInscription.getId()+">");
					
					// On creer le membre jcms
					Member member = new Member();
					SelectionLogin login = SelectionLogin.getTypeLoginByValue(inscription.getPreferenceDeLogin());
					// Login
					if (SelectionLogin.TELEPHONE.equals(login)) {
						member.setLogin(inscription.getTelephonePortable());
					}
					if (SelectionLogin.MAIL.equals(login)) {
						member.setLogin(inscription.getAdresseMail());
					}
					if (SelectionLogin.NUMERO_DOSSIER.equals(login)) {
						member.setLogin(Integer.toString(inscription.getNumeroDagrement()));
					}

					String name = "";
					String firstname = "";
					AssmatSolis solisData = null;
					List<AssmatSolis> listeSolis = SolisManager.getInstance().getAssmatSolisByNumAgrement(inscription.getNumeroDagrement());
					if(Util.notEmpty(listeSolis)){
						solisData = listeSolis.get(0);
					}
					
					if(Util.notEmpty(solisData)){
						name = SolisManager.clean(solisData.getNomAssmat());
						firstname = SolisManager.clean(solisData.getPrenomAssmat());
					}

					member.setName(name);
					member.setFirstName(firstname);
					member.setPassword(channel.crypt(inscription.getMotDePasse()));
					// adresse mail
					if (Util.notEmpty(inscription.getAdresseMail())) {
						member.setEmail(inscription.getAdresseMail());
					}
					// ajout du groupe "Assistante maternelle" au membre
					if(Util.notEmpty(AssmatUtil.getGroupAssmat())){
						member.addGroup(AssmatUtil.getGroupAssmat());	
					}
					//Telephone
					if(Util.notEmpty(inscription.getTelephonePortable())){
					  member.setMobile(inscription.getTelephonePortable());
					}

					
					
					
					
					ProfilManager profilManager = ProfilManager.getInstance();
					member.setSalutation(profilManager.getCivilite(inscription.getCivilite()));
					
					StringBuilder stbd = AssmatUtil.getOrganization(inscription,name,firstname);						

					member.setOrganization(stbd.toString());

					// perform
					member.performCreate(channel.getDefaultAdmin());
					logger.debug("Le membre <"+member.getFullName()+"> / <"+member.getId()+"> a été crée");
					
					// On creer un profilAM
					ProfilASSMAT profil = new ProfilASSMAT();
					profil.setAuthor(member);
					profil.setTitle(AssmatUtil.getTitleProfilAssmat(profil.getTitle(), member, inscription.getCommune(), inscription.getCommuneMam(), inscription.getNumeroDagrement()+""));
					profil.setCommune(inscription.getCommune());
					profil.setCommuneMam(inscription.getCommuneMam());
					if (SelectionLogin.TELEPHONE == login) {
						profil.setTypeLogin(AssmatUtil.SelectionLogin.TELEPHONE.getValue());
					}
					if (SelectionLogin.MAIL == login) {
						profil.setTypeLogin(AssmatUtil.SelectionLogin.MAIL.getValue());
					}
					if (SelectionLogin.NUMERO_DOSSIER == login) {
						profil.setTypeLogin(AssmatUtil.SelectionLogin.NUMERO_DOSSIER.getValue());
					}
					//Valeur par defaut AUCUN
					profil.setVisbiliteTelephoneFixe(AssmatUtil.Selection.SELECTION_NONE.getValue());
					profil.setVisibiliteTelephonePortable(AssmatUtil.Selection.SELECTION_NONE.getValue());
					profil.setVisibiliteAdresseEmail(AssmatUtil.Selection.SELECTION_NONE.getValue());
						
					// Comtpe non activé
					profil.setCompteActive(true);
					profil.setProfilRenseigne(false);
					
					//Telephone fixe
					if(Util.notEmpty(inscription.getTelephoneFixe())){
						  profil.setTelephoneFixe(inscription.getTelephoneFixe());
					}
					
					
					//Latitude / Longitude  AM
     if(Util.notEmpty(inscription.getLatitudeAssmat()) && Util.notEmpty(inscription.getLongitudeAssmat())){
       profil.setLatitudeAssmat(inscription.getLatitudeAssmat());
       profil.setLongitudeAssmat(inscription.getLongitudeAssmat());
     }
     
   //Latitude / Longitude MAM
     if(Util.notEmpty(inscription.getLatitudeMAM()) && Util.notEmpty(inscription.getLongitudeMAM())){
       profil.setLatitudeMAM(inscription.getLatitudeMAM());
       profil.setLongitudeMAM(inscription.getLongitudeMAM());
     }
					
					SelectionPreferenceReception preferenceRecep = SelectionPreferenceReception.getTypeLoginByValue(inscription.getPreferenceReceptionMessageDepart());
					if(Util.notEmpty(preferenceRecep))
						profil.setCanalDeCommunicationSite(preferenceRecep.getValue());

					// Numero d'agrement
					int numeroAgrement = inscription.getNumeroDagrement();
					profil.setNum_agrement(numeroAgrement);
					
					// Enregistre le profilAM dans JCMS
					profil.performCreate(channel.getDefaultAdmin());
					logger.debug("Le ProfilASSMAT <"+profil.getTitle()+"> / <"+profil.getId()+"> a été crée");
					
					// On creer les contenus dissponibilite
					SolisManager solisMgr = SolisManager.getInstance();
					List<AssmatSolis> listAssmat = solisMgr.getAssmatSolisByNumAgrement(numeroAgrement);
					if (Util.notEmpty(listAssmat)) {
					  AssmatSolis assmat = Util.getFirst(listAssmat);


					  // Pour chacune des places de solis création des disponibilités
					  for(int itPlace=1; itPlace<=8; itPlace++) {

					    // agrementTrancheAgeKey
					    Field agrementTracheAgeKeyField = ReflectUtil.getField(assmat.getClass(), "place"+itPlace+"AgrementTrancheAgeKey");
					    String agrement = (String) ReflectUtil.getFieldValue(assmat, agrementTracheAgeKeyField);

					    // placeTracheAgeKey
					    Field placeTracheAgeKeyField = ReflectUtil.getField(assmat.getClass(), "place"+itPlace+"TrancheAgeKey");
					    Integer keyPlace = (Integer) ReflectUtil.getFieldValue(assmat, placeTracheAgeKeyField);  

					    // PlaceNbPlaces
					    Field placeNbPlacesField = ReflectUtil.getField(assmat.getClass(), "place"+itPlace+"NbPlaces");
					    Integer nbPlace = (Integer) ReflectUtil.getFieldValue(assmat, placeNbPlacesField);

					    // placeTrancheAge
					    Field placeTrancheAgeField = ReflectUtil.getField(assmat.getClass(), "place"+itPlace+"TrancheAge");
					    String placeTrancheAge = (String) ReflectUtil.getFieldValue(assmat, placeTrancheAgeField);

					    // PlaceLibCompl
					    Field placeLibComplField = ReflectUtil.getField(assmat.getClass(), "place"+itPlace+"LibCompl");
					    String placeLibCompl = (String) ReflectUtil.getFieldValue(assmat, placeLibComplField);

					    // Saisie disponible
					    Field placeSaisieField = ReflectUtil.getField(assmat.getClass(), "place"+itPlace+"SaisieDisponibilite");
					    Boolean placeSaisie = (Boolean) ReflectUtil.getFieldValue(assmat, placeSaisieField);

					    // Dispo
					    if (Util.notEmpty(agrement) && Util.notEmpty(keyPlace) && Util.notEmpty(nbPlace) && nbPlace != 0) {
					      AssmatUtil.createManyDispo(member, agrement, keyPlace, placeTrancheAge, placeLibCompl, nbPlace, placeSaisie, profil);
					    }
					  }


					}
					

					

					expiredFormInscriptionFromMember(inscription.getNumeroDagrement());
					// AuthenticationContext ctx = new AuthenticationContext(null,null,
					// channel.getLocale(),channel.getCurrentServletRequest(),
					// channel.getCurrentServletResponse() );
	
					// Autolog
					if(Util.isEmpty(channel.getCurrentLoggedMember())){
						channel.getCurrentServletRequest().getSession().setAttribute("loggedMemberId", member.getId());
					}
				}

			}
		}
	}

	/**
	 * Méthode servant à expirer les formulaires qui ont été crée alors que l'inscription a été abandonnée
	 * @param numeroAgrement Le numéro d'agrément de l'AM
	 */
	  private void expiredFormInscriptionFromMember(int numeroAgrement) {
		   Set<InscriptionAM> listeInscription = JcmsUtil.select(channel.getPublicationSet(InscriptionAM.class, channel.getDefaultAdmin(),false), new InscriptionAMSelectorByNumAgrement(numeroAgrement), null);

		  if(Util.notEmpty(listeInscription)){
			  if(Util.notEmpty(listeInscription)){
				  for (InscriptionAM inscriptionAM : listeInscription) {
					  inscriptionAM.updatePstatus(channel.getDefaultAdmin(), WorkflowConstants.EXPIRED_PSTATUS, "");
				  }
			  }
		  }
	  }

}
