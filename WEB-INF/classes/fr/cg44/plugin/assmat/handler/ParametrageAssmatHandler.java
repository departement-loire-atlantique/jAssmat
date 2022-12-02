package fr.cg44.plugin.assmat.handler;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.AssmatUtil.SelectionPreferenceReception;
import fr.cg44.plugin.assmat.Semainier;
import fr.cg44.plugin.assmat.SemainierUtil;
import fr.cg44.plugin.assmat.managers.SmsDAO;
import generated.Disponibilite;
import generated.EditProfilASSMATHandler;
import generated.ProfilASSMAT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.jalios.jcms.Data;
import com.jalios.jcms.JcmsConstants;
import com.jalios.jcms.Member;
import com.jalios.jcms.Publication;
import com.jalios.jcms.context.JcmsMessage;
import com.jalios.jcms.context.JcmsMessage.Level;
import com.jalios.util.Util;

public class ParametrageAssmatHandler extends EditProfilASSMATHandler {

	protected static Logger logger = Logger.getLogger(ParametrageAssmatHandler.class);

	public static final int VISIBILITE_STEP = 0;
	public static final int CONTACTS_STEP = 1;
	public static final int OFFRE_STEP = 2;
	public static final int DISPONIBILITE_STEP = 3;
	public static final int AUTORISATION_STEP = 4;

  // Permet d'afficher un message si la personne ne souhaite pas afficher ces
	// informations sur le site
	protected boolean afficherMessageConfirmationVisibiliteSiteNone = true;

	protected boolean afficherMessageConfirmation = false;
	
	private String endTag = "/>";
  private String endTagTechnicalField = "data-technical-field />";

	/**
	 * Méthode qui valide le passage à l'étape suivante
	 */
	@Override
	protected boolean validateNext() throws IOException {
		// Si on a pas etape suivante on renvoie faux
		if (!this.opNext) {
			return false;
		}
		// Si les champs requis ne sont pas présent on renvoie faux
		if (!validateFields()) {
			return false;
		}
		return super.validateNext();
	}

	/**
	 * Méthode qui retourne le nombre détapes
	 */
	public int getFormStepCount() {
		return 5;
	}

	/**
	 * Méthode qui retourne le nuemro d'étape en cour
	 */
	public int getCurrentFormStep() {
		return formStep;
	}

	/**
	 * Méthode principal de gestion (point d'entrée)
	 */
	@Override
	public boolean processAction() throws IOException {
		// Si on est à la premiere etape on verifie les champs
		if (formStep == VISIBILITE_STEP) {
			// Si validation du formulaire
			if (opNext) {
				if (Util.notEmpty(getPublication())) {
					getPublication().setVisibiliteSite(getAvailableVisibiliteSite());
				}

				if (!validateFields()) {
					if (!getAvailableVisibiliteSite()) {
						afficherMessageConfirmationVisibiliteSiteNone = false;
						if (getPublication().checkUpdate(channel.getDefaultAdmin()).isOK()) {
							getPublication().setProfilRenseigne(true);
							getPublication().performUpdate(channel.getDefaultAdmin());
						}
					}
					return false;
				} else {
					getPublication().setEtapeDeValidation(formStep+1);

					// Affichage du message optionnel
					if (getAvailableVisibiliteSite()) {
						afficherMessageConfirmationVisibiliteSiteNone = true;
						if (getPublication().checkUpdate(channel.getDefaultAdmin()).isOK()) {
							getPublication().performUpdate(channel.getDefaultAdmin());
						}
					}
				}
			}
		}

		// Si on est à la 2eme etape on verifie les champs
		if (formStep == CONTACTS_STEP) {
			if (opNext) {
				if (!validateFields()) {
					return false;
				} else {
					getPublication().setTelephoneFixe(getAvailableTelephoneFixe());
					if (Util.notEmpty(getAvailableVisbiliteTelephoneFixe())) {
						getPublication().setVisbiliteTelephoneFixe(getAvailableVisbiliteTelephoneFixe());
					} else {
						getPublication().setVisbiliteTelephoneFixe(AssmatUtil.Selection.SELECTION_NONE.getValue());
					}

					if (Util.notEmpty(getAvailableVisibiliteTelephonePortable())) {
						getPublication().setVisibiliteTelephonePortable(getAvailableVisibiliteTelephonePortable());
					} else {
						getPublication().setVisibiliteTelephonePortable(AssmatUtil.Selection.SELECTION_NONE.getValue());
					}

					getPublication().setCreneauHorairesDappel(getAvailableCreneauHorairesDappel());

					if (Util.notEmpty(getAvailableVisibiliteAdresseEmail())) {
						getPublication().setVisibiliteAdresseEmail(getAvailableVisibiliteAdresseEmail());
					} else {
						getPublication().setVisibiliteAdresseEmail(AssmatUtil.Selection.SELECTION_NONE.getValue());
					}

					if (Util.notEmpty(getAvailableAfficherContactUniquementSiD())) {
						getPublication().setAfficherContactUniquementSiD(getAvailableAfficherContactUniquementSiD());
					} 

					getPublication().setEtapeDeValidation(formStep+1);
					if (getPublication().checkUpdate(channel.getDefaultAdmin()).isOK()) {
						getPublication().performUpdate(channel.getDefaultAdmin());
					}

					Member memberClone = (Member) getLoggedMember().getUpdateInstance();
					memberClone.setEmail(getAdresseMail());
					memberClone.setMobile(getTelephonePortable());
					if (memberClone.checkIntegrity().hasFailed()) {
						addMsg(new JcmsMessage(Level.WARN, memberClone.checkIntegrity().toString()));
						return false;
					}
					memberClone.performUpdate(channel.getDefaultAdmin());
				}
			}
		}

		// Si on est à la 3eme etape on verifie les champs
		if (formStep == OFFRE_STEP) {
			if (opNext) {
				if (!validateFields()) {
					return false;
				} else {
					setFields(getPublication());
					getPublication().setEtapeDeValidation(formStep+1);
					// enregistrement des données avant de passer à l'étapes
					// suivantes
					if (getPublication().checkUpdate(channel.getDefaultAdmin()).isOK()) {
						getPublication().performUpdate(channel.getDefaultAdmin());
					}
				}
			}
		}

		// Si on est à la 4eme etape on verifie les champs
		if (formStep == DISPONIBILITE_STEP) {
			if (opNext) {
				if (!validateFields()) {
					return false;
				} else {
					setFields(getPublication());
					performDispos();
					getPublication().setEtapeDeValidation(formStep+1);
					if (getPublication().checkUpdate(channel.getDefaultAdmin()).isOK()) {
						getPublication().performUpdate(channel.getDefaultAdmin());
					}
				}
			}
		}

		// Si on est à la 5eme etape on verifie les champs
		if (formStep == AUTORISATION_STEP) {
			if (opNext) {
				if (!validateFields()) {
					return false;
				} else {
					if (getPublication().getCanalDeCommunicationSite().equals(SelectionPreferenceReception.MAIL.getValue())) {

						String to = channel.getCurrentLoggedMember().getEmail();
						
							//String from = AssmatUtil.getMessage("MAIL-PARAMETRAGE-FROM-CONTENT");
						 String from = AssmatUtil.getDefaultEmail(); 
							String subject = AssmatUtil.getMessage("MAIL-PARAMETRAGE-SUJET-CONTENT");

							String[] parameters = new String[] {};
							parameters = (String[]) Util.insertArray(parameters, 0, "<a href=" + channel.getUrl() + ">connexion</a>");
							parameters = (String[]) Util.insertArray(parameters, 1, channel.getUrl());

							String content = AssmatUtil.getMessagePropertiesParametersValues("MAIL-PARAMETRAGE-CONTENTMAIL-CONTENT", parameters);

							AssmatUtil.sendMail(to, subject, content, from);
						
					} else if (getPublication().getCanalDeCommunicationSite().equals(SelectionPreferenceReception.TELEPHONE.getValue())) {
						SmsDAO dao = new SmsDAO();
						String idMessage = "PARAMETRAGE-COMPTE-SMS-VALIDATION-MESSAGE-CONTENT";
						String subject = idMessage;
	
						String[] parameters = new String[] {};				
						parameters = (String[]) Util.insertArray(parameters, 0, channel.getUrl());
					
						String content = AssmatUtil.getMessagePropertiesParametersValues("PARAMETRAGE-COMPTE-SMS-VALIDATION-MESSAGE-CONTENT",parameters);
						String resumeContent = AssmatUtil.getMessagePropertiesParametersValues("PARAMETRAGE-COMPTE-SMS-VALIDATION-MESSAGE-CONTENT",parameters);

						dao.sendSMS(getPublication(), subject, content, resumeContent);

					}
					setFields(getPublication());
					getPublication().setEtapeDeValidation(formStep+1);
					getPublication().setProfilRenseigne(true);
					if (getPublication().checkUpdate(channel.getDefaultAdmin()).isOK()) {
						getPublication().performUpdate(channel.getDefaultAdmin());
						afficherMessageConfirmation = true;
						return false;
					}

				}
			}
		}

		// Si on a pas etape suivante, ni précédente, ni finale
		if (!opNext && !opPrevious && !opFinish) {
			return false;
		}
		// Si on est à la dernière étape
		if (opFinish) {
		 Publication portalPerso = channel.getPublication(channel.getProperty("jcmsplugin.assmatplugin.socle.portail.param.id"));
			sendRedirect(portalPerso);
			return true;
		}
		return super.processAction();
	}

	public boolean getAfficherMessageConfirmationVisibiliteSiteNone() {
		return afficherMessageConfirmationVisibiliteSiteNone;
	}

	public boolean getAfficherMessageConfirmation() {
		return afficherMessageConfirmation;
	}

	/**
	 * Vérifie la nullité des champs de la modale
	 * 
	 * @return <code>true</code> si tous les champs sont remplies
	 * @throws IOException
	 */
	public boolean validateFields() throws IOException {
		boolean valide = true;
		List<JcmsMessage> listError = new ArrayList<JcmsMessage>();

		if (formStep == VISIBILITE_STEP) {
			if (!getAvailableVisibiliteSite()) {
				valide = false;
				afficherMessageConfirmationVisibiliteSiteNone = false;
			}
		}

		// On vérifie les champs de l'étape 2
		if (formStep == CONTACTS_STEP) {
		 
		 // Au moins un contact doit être affiché sur le site lors de l'inscription
		 if(!"true".equals(getAvailableVisbiliteTelephoneFixe()) && !"true".equals(getAvailableVisibiliteTelephonePortable()) && !"true".equals(getAvailableVisibiliteAdresseEmail())) {
	    listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("CONTACT-VALIDITE") ));
		 }
		  
			if (Util.isEmpty(getAvailableTelephoneFixe()) && "true".equals(getAvailableVisbiliteTelephoneFixe())) {
				listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY", new String[] { "CONTACT-TEL-HTML" })));
			}

			if (Util.notEmpty(getAvailableTelephoneFixe()) && !AssmatUtil.checkPhone(getAvailableTelephoneFixe())) {
				listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("CONTACT-TEL-VALIDITE", new String[] { "CONTACT-TEL-HTML" })));
			}

			if (Util.isEmpty(getTelephonePortable()) && "true".equals(getAvailableVisibiliteTelephonePortable())) {
				listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY", new String[] { "CONTACT-MOB-HTML" })));
			}

			if (Util.notEmpty(getTelephonePortable()) && !AssmatUtil.checkPhone(getTelephonePortable())) {
				listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("CONTACT-TEL-VALIDITE", new String[] { "CONTACT-MOB-HTML" })));
			}

			if (Util.isEmpty(getAdresseMail()) && "true".equals(getAvailableVisibiliteAdresseEmail())) {
				listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY", new String[] { "CONTACT-MAIL-HTML" })));
			}

			if (Util.notEmpty(getAdresseMail()) && !AssmatUtil.checkEmail(getAdresseMail())) {
				listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("CONTACT-MAIL-VALIDITE", new String[] { "CONTACT-MAIL-HTML" })));
			}
		}

		// On affiche l'erreur si il y en à une
		if (!listError.isEmpty()) {
			request.setAttribute(JcmsConstants.JCMS_MSG_LIST, listError);
			valide = false;
		}

		return valide;
	}

	/**
	 * Persiste les données dans des input type hidden pour permettre le retour
	 * aux etapes precedentes
	 * 
	 * @return
	 */
	public String getFormStepHiddenFields() {
		StringBuilder stringBuilder = new StringBuilder();

		// stringBuilder.append(getHiddenField("formStep", formStep).replace(endTag, endTagTechnicalField));

		return stringBuilder.toString();
	}

	/**
	 * Persiste toutes les données sans ce soucier de l'étape courantes dans des
	 * input type hidden pour permettre le retour aux etapes precedentes
	 * 
	 * @return
	 */
	public String getAllFormStepHiddenFields() {
		StringBuilder stringBuilder = new StringBuilder();

		return stringBuilder.toString();
	}

	@Override
	public Class<? extends Data> getDataClass() {
		return null;
	}

	// GETTERS AND SETTERS

	public ProfilASSMAT getPublication() {
		return (ProfilASSMAT) super.getPublication();
	}

	protected String adresseMail;

	public String getAdresseMail() {
		return adresseMail;
	}

	public void setAdresseMail(String adresseMail) {
		this.adresseMail = adresseMail;
	}

	// dispo 1
	protected String idDispo1;
	protected String etatDispo1;
	protected String precisionPlaceFuture1;
	protected String precisionsPlaceDisponible1;
	protected String moisDispo1;
	protected String anneeDispo1;
	protected String lundi_avant_ecole1;
	protected String mardi_avant_ecole1;
	protected String mercredi_avant_ecole1;
	protected String jeudi_avant_ecole1;
	protected String vendredi_avant_ecole1;
	protected String samedi_avant_ecole1;
	protected String dimanche_avant_ecole1;
	protected String lundi_matin1;
	protected String mardi_matin1;
	protected String mercredi_matin1;
	protected String jeudi_matin1;
	protected String vendredi_matin1;
	protected String samedi_matin1;
	protected String dimanche_matin1;
	protected String lundi_midi1;
	protected String mardi_midi1;
	protected String mercredi_midi1;
	protected String jeudi_midi1;
	protected String vendredi_midi1;
	protected String samedi_midi1;
	protected String dimanche_midi1;
	protected String lundi_pm1;
	protected String mardi_pm1;
	protected String mercredi_pm1;
	protected String jeudi_pm1;
	protected String vendredi_pm1;
	protected String samedi_pm1;
	protected String dimanche_pm1;
	protected String lundi_apres_ecole1;
	protected String mardi_apres_ecole1;
	protected String mercredi_apres_ecole1;
	protected String jeudi_apres_ecole1;
	protected String vendredi_apres_ecole1;
	protected String samedi_apres_ecole1;
	protected String dimanche_apres_ecole1;
	protected String lundi_nuit1;
	protected String mardi_nuit1;
	protected String mercredi_nuit1;
	protected String jeudi_nuit1;
	protected String vendredi_nuit1;
	protected String samedi_nuit1;
	protected String dimanche_nuit1;
	protected String dispoDepannagePlaceDisponible1;
	protected String precisionsDepannagePlaceDisponible1;
	// dispo 2
	protected String idDispo2;
	protected String etatDispo2;
	protected String precisionPlaceFuture2;
	protected String precisionsPlaceDisponible2;
	protected String moisDispo2;
	protected String anneeDispo2;
	protected String lundi_avant_ecole2;
	protected String mardi_avant_ecole2;
	protected String mercredi_avant_ecole2;
	protected String jeudi_avant_ecole2;
	protected String vendredi_avant_ecole2;
	protected String samedi_avant_ecole2;
	protected String dimanche_avant_ecole2;
	protected String lundi_matin2;
	protected String mardi_matin2;
	protected String mercredi_matin2;
	protected String jeudi_matin2;
	protected String vendredi_matin2;
	protected String samedi_matin2;
	protected String dimanche_matin2;
	protected String lundi_midi2;
	protected String mardi_midi2;
	protected String mercredi_midi2;
	protected String jeudi_midi2;
	protected String vendredi_midi2;
	protected String samedi_midi2;
	protected String dimanche_midi2;
	protected String lundi_pm2;
	protected String mardi_pm2;
	protected String mercredi_pm2;
	protected String jeudi_pm2;
	protected String vendredi_pm2;
	protected String samedi_pm2;
	protected String dimanche_pm2;
	protected String lundi_apres_ecole2;
	protected String mardi_apres_ecole2;
	protected String mercredi_apres_ecole2;
	protected String jeudi_apres_ecole2;
	protected String vendredi_apres_ecole2;
	protected String samedi_apres_ecole2;
	protected String dimanche_apres_ecole2;
	protected String lundi_nuit2;
	protected String mardi_nuit2;
	protected String mercredi_nuit2;
	protected String jeudi_nuit2;
	protected String vendredi_nuit2;
	protected String samedi_nuit2;
	protected String dimanche_nuit2;
	protected String dispoDepannagePlaceDisponible2;
	protected String precisionsDepannagePlaceDisponible2;
	// dispo 3
	protected String idDispo3;
	protected String etatDispo3;
	protected String precisionPlaceFuture3;
	protected String precisionsPlaceDisponible3;
	protected String moisDispo3;
	protected String anneeDispo3;
	protected String lundi_avant_ecole3;
	protected String mardi_avant_ecole3;
	protected String mercredi_avant_ecole3;
	protected String jeudi_avant_ecole3;
	protected String vendredi_avant_ecole3;
	protected String samedi_avant_ecole3;
	protected String dimanche_avant_ecole3;
	protected String lundi_matin3;
	protected String mardi_matin3;
	protected String mercredi_matin3;
	protected String jeudi_matin3;
	protected String vendredi_matin3;
	protected String samedi_matin3;
	protected String dimanche_matin3;
	protected String lundi_midi3;
	protected String mardi_midi3;
	protected String mercredi_midi3;
	protected String jeudi_midi3;
	protected String vendredi_midi3;
	protected String samedi_midi3;
	protected String dimanche_midi3;
	protected String lundi_pm3;
	protected String mardi_pm3;
	protected String mercredi_pm3;
	protected String jeudi_pm3;
	protected String vendredi_pm3;
	protected String samedi_pm3;
	protected String dimanche_pm3;
	protected String lundi_apres_ecole3;
	protected String mardi_apres_ecole3;
	protected String mercredi_apres_ecole3;
	protected String jeudi_apres_ecole3;
	protected String vendredi_apres_ecole3;
	protected String samedi_apres_ecole3;
	protected String dimanche_apres_ecole3;
	protected String lundi_nuit3;
	protected String mardi_nuit3;
	protected String mercredi_nuit3;
	protected String jeudi_nuit3;
	protected String vendredi_nuit3;
	protected String samedi_nuit3;
	protected String dimanche_nuit3;
	protected String dispoDepannagePlaceDisponible3;
	protected String precisionsDepannagePlaceDisponible3;
	// dispo 4
	protected String idDispo4;
	protected String etatDispo4;
	protected String precisionPlaceFuture4;
	protected String precisionsPlaceDisponible4;
	protected String moisDispo4;
	protected String anneeDispo4;
	protected String lundi_avant_ecole4;
	protected String mardi_avant_ecole4;
	protected String mercredi_avant_ecole4;
	protected String jeudi_avant_ecole4;
	protected String vendredi_avant_ecole4;
	protected String samedi_avant_ecole4;
	protected String dimanche_avant_ecole4;
	protected String lundi_matin4;
	protected String mardi_matin4;
	protected String mercredi_matin4;
	protected String jeudi_matin4;
	protected String vendredi_matin4;
	protected String samedi_matin4;
	protected String dimanche_matin4;
	protected String lundi_midi4;
	protected String mardi_midi4;
	protected String mercredi_midi4;
	protected String jeudi_midi4;
	protected String vendredi_midi4;
	protected String samedi_midi4;
	protected String dimanche_midi4;
	protected String lundi_pm4;
	protected String mardi_pm4;
	protected String mercredi_pm4;
	protected String jeudi_pm4;
	protected String vendredi_pm4;
	protected String samedi_pm4;
	protected String dimanche_pm4;
	protected String lundi_apres_ecole4;
	protected String mardi_apres_ecole4;
	protected String mercredi_apres_ecole4;
	protected String jeudi_apres_ecole4;
	protected String vendredi_apres_ecole4;
	protected String samedi_apres_ecole4;
	protected String dimanche_apres_ecole4;
	protected String lundi_nuit4;
	protected String mardi_nuit4;
	protected String mercredi_nuit4;
	protected String jeudi_nuit4;
	protected String vendredi_nuit4;
	protected String samedi_nuit4;
	protected String dimanche_nuit4;
	protected String dispoDepannagePlaceDisponible4;
	protected String precisionsDepannagePlaceDisponible4;
	// dispo 5
	protected String idDispo5;
	protected String etatDispo5;
	protected String precisionPlaceFuture5;
	protected String precisionsPlaceDisponible5;
	protected String moisDispo5;
	protected String anneeDispo5;
	protected String lundi_avant_ecole5;
	protected String mardi_avant_ecole5;
	protected String mercredi_avant_ecole5;
	protected String jeudi_avant_ecole5;
	protected String vendredi_avant_ecole5;
	protected String samedi_avant_ecole5;
	protected String dimanche_avant_ecole5;
	protected String lundi_matin5;
	protected String mardi_matin5;
	protected String mercredi_matin5;
	protected String jeudi_matin5;
	protected String vendredi_matin5;
	protected String samedi_matin5;
	protected String dimanche_matin5;
	protected String lundi_midi5;
	protected String mardi_midi5;
	protected String mercredi_midi5;
	protected String jeudi_midi5;
	protected String vendredi_midi5;
	protected String samedi_midi5;
	protected String dimanche_midi5;
	protected String lundi_pm5;
	protected String mardi_pm5;
	protected String mercredi_pm5;
	protected String jeudi_pm5;
	protected String vendredi_pm5;
	protected String samedi_pm5;
	protected String dimanche_pm5;
	protected String lundi_apres_ecole5;
	protected String mardi_apres_ecole5;
	protected String mercredi_apres_ecole5;
	protected String jeudi_apres_ecole5;
	protected String vendredi_apres_ecole5;
	protected String samedi_apres_ecole5;
	protected String dimanche_apres_ecole5;
	protected String lundi_nuit5;
	protected String mardi_nuit5;
	protected String mercredi_nuit5;
	protected String jeudi_nuit5;
	protected String vendredi_nuit5;
	protected String samedi_nuit5;
	protected String dimanche_nuit5;
	protected String dispoDepannagePlaceDisponible5;
	protected String precisionsDepannagePlaceDisponible5;
	// dispo 6
	protected String idDispo6;
	protected String etatDispo6;
	protected String precisionPlaceFuture6;
	protected String precisionsPlaceDisponible6;
	protected String moisDispo6;
	protected String anneeDispo6;
	protected String lundi_avant_ecole6;
	protected String mardi_avant_ecole6;
	protected String mercredi_avant_ecole6;
	protected String jeudi_avant_ecole6;
	protected String vendredi_avant_ecole6;
	protected String samedi_avant_ecole6;
	protected String dimanche_avant_ecole6;
	protected String lundi_matin6;
	protected String mardi_matin6;
	protected String mercredi_matin6;
	protected String jeudi_matin6;
	protected String vendredi_matin6;
	protected String samedi_matin6;
	protected String dimanche_matin6;
	protected String lundi_midi6;
	protected String mardi_midi6;
	protected String mercredi_midi6;
	protected String jeudi_midi6;
	protected String vendredi_midi6;
	protected String samedi_midi6;
	protected String dimanche_midi6;
	protected String lundi_pm6;
	protected String mardi_pm6;
	protected String mercredi_pm6;
	protected String jeudi_pm6;
	protected String vendredi_pm6;
	protected String samedi_pm6;
	protected String dimanche_pm6;
	protected String lundi_apres_ecole6;
	protected String mardi_apres_ecole6;
	protected String mercredi_apres_ecole6;
	protected String jeudi_apres_ecole6;
	protected String vendredi_apres_ecole6;
	protected String samedi_apres_ecole6;
	protected String dimanche_apres_ecole6;
	protected String lundi_nuit6;
	protected String mardi_nuit6;
	protected String mercredi_nuit6;
	protected String jeudi_nuit6;
	protected String vendredi_nuit6;
	protected String samedi_nuit6;
	protected String dimanche_nuit6;
	protected String dispoDepannagePlaceDisponible6;
	protected String precisionsDepannagePlaceDisponible6;
	// dispo 7
	protected String idDispo7;
	protected String etatDispo7;
	protected String precisionPlaceFuture7;
	protected String precisionsPlaceDisponible7;
	protected String moisDispo7;
	protected String anneeDispo7;
	protected String lundi_avant_ecole7;
	protected String mardi_avant_ecole7;
	protected String mercredi_avant_ecole7;
	protected String jeudi_avant_ecole7;
	protected String vendredi_avant_ecole7;
	protected String samedi_avant_ecole7;
	protected String dimanche_avant_ecole7;
	protected String lundi_matin7;
	protected String mardi_matin7;
	protected String mercredi_matin7;
	protected String jeudi_matin7;
	protected String vendredi_matin7;
	protected String samedi_matin7;
	protected String dimanche_matin7;
	protected String lundi_midi7;
	protected String mardi_midi7;
	protected String mercredi_midi7;
	protected String jeudi_midi7;
	protected String vendredi_midi7;
	protected String samedi_midi7;
	protected String dimanche_midi7;
	protected String lundi_pm7;
	protected String mardi_pm7;
	protected String mercredi_pm7;
	protected String jeudi_pm7;
	protected String vendredi_pm7;
	protected String samedi_pm7;
	protected String dimanche_pm7;
	protected String lundi_apres_ecole7;
	protected String mardi_apres_ecole7;
	protected String mercredi_apres_ecole7;
	protected String jeudi_apres_ecole7;
	protected String vendredi_apres_ecole7;
	protected String samedi_apres_ecole7;
	protected String dimanche_apres_ecole7;
	protected String lundi_nuit7;
	protected String mardi_nuit7;
	protected String mercredi_nuit7;
	protected String jeudi_nuit7;
	protected String vendredi_nuit7;
	protected String samedi_nuit7;
	protected String dimanche_nuit7;
	protected String dispoDepannagePlaceDisponible7;
	protected String precisionsDepannagePlaceDisponible7;
	// dispo 8
	protected String idDispo8;
	protected String etatDispo8;
	protected String precisionPlaceFuture8;
	protected String precisionsPlaceDisponible8;
	protected String moisDispo8;
	protected String anneeDispo8;
	protected String lundi_avant_ecole8;
	protected String mardi_avant_ecole8;
	protected String mercredi_avant_ecole8;
	protected String jeudi_avant_ecole8;
	protected String vendredi_avant_ecole8;
	protected String samedi_avant_ecole8;
	protected String dimanche_avant_ecole8;
	protected String lundi_matin8;
	protected String mardi_matin8;
	protected String mercredi_matin8;
	protected String jeudi_matin8;
	protected String vendredi_matin8;
	protected String samedi_matin8;
	protected String dimanche_matin8;
	protected String lundi_midi8;
	protected String mardi_midi8;
	protected String mercredi_midi8;
	protected String jeudi_midi8;
	protected String vendredi_midi8;
	protected String samedi_midi8;
	protected String dimanche_midi8;
	protected String lundi_pm8;
	protected String mardi_pm8;
	protected String mercredi_pm8;
	protected String jeudi_pm8;
	protected String vendredi_pm8;
	protected String samedi_pm8;
	protected String dimanche_pm8;
	protected String lundi_apres_ecole8;
	protected String mardi_apres_ecole8;
	protected String mercredi_apres_ecole8;
	protected String jeudi_apres_ecole8;
	protected String vendredi_apres_ecole8;
	protected String samedi_apres_ecole8;
	protected String dimanche_apres_ecole8;
	protected String lundi_nuit8;
	protected String mardi_nuit8;
	protected String mercredi_nuit8;
	protected String jeudi_nuit8;
	protected String vendredi_nuit8;
	protected String samedi_nuit8;
	protected String dimanche_nuit8;
	protected String dispoDepannagePlaceDisponible8;
	protected String precisionsDepannagePlaceDisponible8;
	// dispo 9
	protected String idDispo9;
	protected String etatDispo9;
	protected String precisionPlaceFuture9;
	protected String precisionsPlaceDisponible9;
	protected String moisDispo9;
	protected String anneeDispo9;
	protected String lundi_avant_ecole9;
	protected String mardi_avant_ecole9;
	protected String mercredi_avant_ecole9;
	protected String jeudi_avant_ecole9;
	protected String vendredi_avant_ecole9;
	protected String samedi_avant_ecole9;
	protected String dimanche_avant_ecole9;
	protected String lundi_matin9;
	protected String mardi_matin9;
	protected String mercredi_matin9;
	protected String jeudi_matin9;
	protected String vendredi_matin9;
	protected String samedi_matin9;
	protected String dimanche_matin9;
	protected String lundi_midi9;
	protected String mardi_midi9;
	protected String mercredi_midi9;
	protected String jeudi_midi9;
	protected String vendredi_midi9;
	protected String samedi_midi9;
	protected String dimanche_midi9;
	protected String lundi_pm9;
	protected String mardi_pm9;
	protected String mercredi_pm9;
	protected String jeudi_pm9;
	protected String vendredi_pm9;
	protected String samedi_pm9;
	protected String dimanche_pm9;
	protected String lundi_apres_ecole9;
	protected String mardi_apres_ecole9;
	protected String mercredi_apres_ecole9;
	protected String jeudi_apres_ecole9;
	protected String vendredi_apres_ecole9;
	protected String samedi_apres_ecole9;
	protected String dimanche_apres_ecole9;
	protected String lundi_nuit9;
	protected String mardi_nuit9;
	protected String mercredi_nuit9;
	protected String jeudi_nuit9;
	protected String vendredi_nuit9;
	protected String samedi_nuit9;
	protected String dimanche_nuit9;
	protected String dispoDepannagePlaceDisponible9;
	protected String precisionsDepannagePlaceDisponible9;
	// dispo 10
	protected String idDispo10;
	protected String etatDispo10;
	protected String precisionPlaceFuture10;
	protected String precisionsPlaceDisponible10;
	protected String moisDispo10;
	protected String anneeDispo10;
	protected String lundi_avant_ecole10;
	protected String mardi_avant_ecole10;
	protected String mercredi_avant_ecole10;
	protected String jeudi_avant_ecole10;
	protected String vendredi_avant_ecole10;
	protected String samedi_avant_ecole10;
	protected String dimanche_avant_ecole10;
	protected String lundi_matin10;
	protected String mardi_matin10;
	protected String mercredi_matin10;
	protected String jeudi_matin10;
	protected String vendredi_matin10;
	protected String samedi_matin10;
	protected String dimanche_matin10;
	protected String lundi_midi10;
	protected String mardi_midi10;
	protected String mercredi_midi10;
	protected String jeudi_midi10;
	protected String vendredi_midi10;
	protected String samedi_midi10;
	protected String dimanche_midi10;
	protected String lundi_pm10;
	protected String mardi_pm10;
	protected String mercredi_pm10;
	protected String jeudi_pm10;
	protected String vendredi_pm10;
	protected String samedi_pm10;
	protected String dimanche_pm10;
	protected String lundi_apres_ecole10;
	protected String mardi_apres_ecole10;
	protected String mercredi_apres_ecole10;
	protected String jeudi_apres_ecole10;
	protected String vendredi_apres_ecole10;
	protected String samedi_apres_ecole10;
	protected String dimanche_apres_ecole10;
	protected String lundi_nuit10;
	protected String mardi_nuit10;
	protected String mercredi_nuit10;
	protected String jeudi_nuit10;
	protected String vendredi_nuit10;
	protected String samedi_nuit10;
	protected String dimanche_nuit10;
	protected String dispoDepannagePlaceDisponible10;
	protected String precisionsDepannagePlaceDisponible10;
 protected String telephonePortable;

	public String getEtatDispo1() {
		return etatDispo1;
	}

	public void setEtatDispo1(String etatDispo1) {
		this.etatDispo1 = etatDispo1;
	}

	public String getPrecisionPlaceFuture1() {
		return precisionPlaceFuture1;
	}

	public void setPrecisionPlaceFuture1(String precisionPlaceFuture1) {
		this.precisionPlaceFuture1 = precisionPlaceFuture1;
	}

	public String getPrecisionsPlaceDisponible1() {
		return precisionsPlaceDisponible1;
	}

	public void setPrecisionsPlaceDisponible1(String precisionsPlaceDisponible1) {
		this.precisionsPlaceDisponible1 = precisionsPlaceDisponible1;
	}

	public String getMoisDispo1() {
		return moisDispo1;
	}

	public void setMoisDispo1(String moisDispo1) {
		this.moisDispo1 = moisDispo1;
	}

	public String getAnneeDispo1() {
		return anneeDispo1;
	}

	public void setAnneeDispo1(String anneeDispo1) {
		this.anneeDispo1 = anneeDispo1;
	}

	public String getLundi_avant_ecole1() {
		return lundi_avant_ecole1;
	}

	public void setLundi_avant_ecole1(String lundi_avant_ecole1) {
		this.lundi_avant_ecole1 = lundi_avant_ecole1;
	}

	public String getMardi_avant_ecole1() {
		return mardi_avant_ecole1;
	}

	public void setMardi_avant_ecole1(String mardi_avant_ecole1) {
		this.mardi_avant_ecole1 = mardi_avant_ecole1;
	}

	public String getMercredi_avant_ecole1() {
		return mercredi_avant_ecole1;
	}

	public void setMercredi_avant_ecole1(String mercredi_avant_ecole1) {
		this.mercredi_avant_ecole1 = mercredi_avant_ecole1;
	}

	public String getJeudi_avant_ecole1() {
		return jeudi_avant_ecole1;
	}

	public void setJeudi_avant_ecole1(String jeudi_avant_ecole1) {
		this.jeudi_avant_ecole1 = jeudi_avant_ecole1;
	}

	public String getVendredi_avant_ecole1() {
		return vendredi_avant_ecole1;
	}

	public void setVendredi_avant_ecole1(String vendredi_avant_ecole1) {
		this.vendredi_avant_ecole1 = vendredi_avant_ecole1;
	}

	public String getSamedi_avant_ecole1() {
		return samedi_avant_ecole1;
	}

	public void setSamedi_avant_ecole1(String samedi_avant_ecole1) {
		this.samedi_avant_ecole1 = samedi_avant_ecole1;
	}

	public String getDimanche_avant_ecole1() {
		return dimanche_avant_ecole1;
	}

	public void setDimanche_avant_ecole1(String dimanche_avant_ecole1) {
		this.dimanche_avant_ecole1 = dimanche_avant_ecole1;
	}

	public String getLundi_matin1() {
		return lundi_matin1;
	}

	public void setLundi_matin1(String lundi_matin1) {
		this.lundi_matin1 = lundi_matin1;
	}

	public String getMardi_matin1() {
		return mardi_matin1;
	}

	public void setMardi_matin1(String mardi_matin1) {
		this.mardi_matin1 = mardi_matin1;
	}

	public String getMercredi_matin1() {
		return mercredi_matin1;
	}

	public void setMercredi_matin1(String mercredi_matin1) {
		this.mercredi_matin1 = mercredi_matin1;
	}

	public String getJeudi_matin1() {
		return jeudi_matin1;
	}

	public void setJeudi_matin1(String jeudi_matin1) {
		this.jeudi_matin1 = jeudi_matin1;
	}

	public String getVendredi_matin1() {
		return vendredi_matin1;
	}

	public void setVendredi_matin1(String vendredi_matin1) {
		this.vendredi_matin1 = vendredi_matin1;
	}

	public String getSamedi_matin1() {
		return samedi_matin1;
	}

	public void setSamedi_matin1(String samedi_matin1) {
		this.samedi_matin1 = samedi_matin1;
	}

	public String getDimanche_matin1() {
		return dimanche_matin1;
	}

	public void setDimanche_matin1(String dimanche_matin1) {
		this.dimanche_matin1 = dimanche_matin1;
	}

	public String getLundi_midi1() {
		return lundi_midi1;
	}

	public void setLundi_midi1(String lundi_midi1) {
		this.lundi_midi1 = lundi_midi1;
	}

	public String getMardi_midi1() {
		return mardi_midi1;
	}

	public void setMardi_midi1(String mardi_midi1) {
		this.mardi_midi1 = mardi_midi1;
	}

	public String getMercredi_midi1() {
		return mercredi_midi1;
	}

	public void setMercredi_midi1(String mercredi_midi1) {
		this.mercredi_midi1 = mercredi_midi1;
	}

	public String getJeudi_midi1() {
		return jeudi_midi1;
	}

	public void setJeudi_midi1(String jeudi_midi1) {
		this.jeudi_midi1 = jeudi_midi1;
	}

	public String getVendredi_midi1() {
		return vendredi_midi1;
	}

	public void setVendredi_midi1(String vendredi_midi1) {
		this.vendredi_midi1 = vendredi_midi1;
	}

	public String getSamedi_midi1() {
		return samedi_midi1;
	}

	public void setSamedi_midi1(String samedi_midi1) {
		this.samedi_midi1 = samedi_midi1;
	}

	public String getDimanche_midi1() {
		return dimanche_midi1;
	}

	public void setDimanche_midi1(String dimanche_midi1) {
		this.dimanche_midi1 = dimanche_midi1;
	}

	public String getLundi_pm1() {
		return lundi_pm1;
	}

	public void setLundi_pm1(String lundi_pm1) {
		this.lundi_pm1 = lundi_pm1;
	}

	public String getMardi_pm1() {
		return mardi_pm1;
	}

	public void setMardi_pm1(String mardi_pm1) {
		this.mardi_pm1 = mardi_pm1;
	}

	public String getMercredi_pm1() {
		return mercredi_pm1;
	}

	public void setMercredi_pm1(String mercredi_pm1) {
		this.mercredi_pm1 = mercredi_pm1;
	}

	public String getJeudi_pm1() {
		return jeudi_pm1;
	}

	public void setJeudi_pm1(String jeudi_pm1) {
		this.jeudi_pm1 = jeudi_pm1;
	}

	public String getVendredi_pm1() {
		return vendredi_pm1;
	}

	public void setVendredi_pm1(String vendredi_pm1) {
		this.vendredi_pm1 = vendredi_pm1;
	}

	public String getSamedi_pm1() {
		return samedi_pm1;
	}

	public void setSamedi_pm1(String samedi_pm1) {
		this.samedi_pm1 = samedi_pm1;
	}

	public String getDimanche_pm1() {
		return dimanche_pm1;
	}

	public void setDimanche_pm1(String dimanche_pm1) {
		this.dimanche_pm1 = dimanche_pm1;
	}

	public String getLundi_apres_ecole1() {
		return lundi_apres_ecole1;
	}

	public void setLundi_apres_ecole1(String lundi_apres_ecole1) {
		this.lundi_apres_ecole1 = lundi_apres_ecole1;
	}

	public String getMardi_apres_ecole1() {
		return mardi_apres_ecole1;
	}

	public void setMardi_apres_ecole1(String mardi_apres_ecole1) {
		this.mardi_apres_ecole1 = mardi_apres_ecole1;
	}

	public String getMercredi_apres_ecole1() {
		return mercredi_apres_ecole1;
	}

	public void setMercredi_apres_ecole1(String mercredi_apres_ecole1) {
		this.mercredi_apres_ecole1 = mercredi_apres_ecole1;
	}

	public String getJeudi_apres_ecole1() {
		return jeudi_apres_ecole1;
	}

	public void setJeudi_apres_ecole1(String jeudi_apres_ecole1) {
		this.jeudi_apres_ecole1 = jeudi_apres_ecole1;
	}

	public String getVendredi_apres_ecole1() {
		return vendredi_apres_ecole1;
	}

	public void setVendredi_apres_ecole1(String vendredi_apres_ecole1) {
		this.vendredi_apres_ecole1 = vendredi_apres_ecole1;
	}

	public String getSamedi_apres_ecole1() {
		return samedi_apres_ecole1;
	}

	public void setSamedi_apres_ecole1(String samedi_apres_ecole1) {
		this.samedi_apres_ecole1 = samedi_apres_ecole1;
	}

	public String getDimanche_apres_ecole1() {
		return dimanche_apres_ecole1;
	}

	public void setDimanche_apres_ecole1(String dimanche_apres_ecole1) {
		this.dimanche_apres_ecole1 = dimanche_apres_ecole1;
	}

	public String getLundi_nuit1() {
		return lundi_nuit1;
	}

	public void setLundi_nuit1(String lundi_nuit1) {
		this.lundi_nuit1 = lundi_nuit1;
	}

	public String getMardi_nuit1() {
		return mardi_nuit1;
	}

	public void setMardi_nuit1(String mardi_nuit1) {
		this.mardi_nuit1 = mardi_nuit1;
	}

	public String getMercredi_nuit1() {
		return mercredi_nuit1;
	}

	public void setMercredi_nuit1(String mercredi_nuit1) {
		this.mercredi_nuit1 = mercredi_nuit1;
	}

	public String getJeudi_nuit1() {
		return jeudi_nuit1;
	}

	public void setJeudi_nuit1(String jeudi_nuit1) {
		this.jeudi_nuit1 = jeudi_nuit1;
	}

	public String getVendredi_nuit1() {
		return vendredi_nuit1;
	}

	public void setVendredi_nuit1(String vendredi_nuit1) {
		this.vendredi_nuit1 = vendredi_nuit1;
	}

	public String getSamedi_nuit1() {
		return samedi_nuit1;
	}

	public void setSamedi_nuit1(String samedi_nuit1) {
		this.samedi_nuit1 = samedi_nuit1;
	}

	public String getDimanche_nuit1() {
		return dimanche_nuit1;
	}

	public void setDimanche_nuit1(String dimanche_nuit1) {
		this.dimanche_nuit1 = dimanche_nuit1;
	}

	public String getDispoDepannagePlaceDisponible1() {
		return dispoDepannagePlaceDisponible1;
	}

	public void setDispoDepannagePlaceDisponible1(String dispoDepannagePlaceDisponible1) {
		this.dispoDepannagePlaceDisponible1 = dispoDepannagePlaceDisponible1;
	}

	public String getPrecisionsDepannagePlaceDisponible1() {
		return precisionsDepannagePlaceDisponible1;
	}

	public void setPrecisionsDepannagePlaceDisponible1(String precisionsDepannagePlaceDisponible1) {
		this.precisionsDepannagePlaceDisponible1 = precisionsDepannagePlaceDisponible1;
	}

	public String getEtatDispo2() {
		return etatDispo2;
	}

	public void setEtatDispo2(String etatDispo2) {
		this.etatDispo2 = etatDispo2;
	}

	public String getPrecisionPlaceFuture2() {
		return precisionPlaceFuture2;
	}

	public void setPrecisionPlaceFuture2(String precisionPlaceFuture2) {
		this.precisionPlaceFuture2 = precisionPlaceFuture2;
	}

	public String getPrecisionsPlaceDisponible2() {
		return precisionsPlaceDisponible2;
	}

	public void setPrecisionsPlaceDisponible2(String precisionsPlaceDisponible2) {
		this.precisionsPlaceDisponible2 = precisionsPlaceDisponible2;
	}

	public String getMoisDispo2() {
		return moisDispo2;
	}

	public void setMoisDispo2(String moisDispo2) {
		this.moisDispo2 = moisDispo2;
	}

	public String getAnneeDispo2() {
		return anneeDispo2;
	}

	public void setAnneeDispo2(String anneeDispo2) {
		this.anneeDispo2 = anneeDispo2;
	}

	public String getLundi_avant_ecole2() {
		return lundi_avant_ecole2;
	}

	public void setLundi_avant_ecole2(String lundi_avant_ecole2) {
		this.lundi_avant_ecole2 = lundi_avant_ecole2;
	}

	public String getMardi_avant_ecole2() {
		return mardi_avant_ecole2;
	}

	public void setMardi_avant_ecole2(String mardi_avant_ecole2) {
		this.mardi_avant_ecole2 = mardi_avant_ecole2;
	}

	public String getMercredi_avant_ecole2() {
		return mercredi_avant_ecole2;
	}

	public void setMercredi_avant_ecole2(String mercredi_avant_ecole2) {
		this.mercredi_avant_ecole2 = mercredi_avant_ecole2;
	}

	public String getJeudi_avant_ecole2() {
		return jeudi_avant_ecole2;
	}

	public void setJeudi_avant_ecole2(String jeudi_avant_ecole2) {
		this.jeudi_avant_ecole2 = jeudi_avant_ecole2;
	}

	public String getVendredi_avant_ecole2() {
		return vendredi_avant_ecole2;
	}

	public void setVendredi_avant_ecole2(String vendredi_avant_ecole2) {
		this.vendredi_avant_ecole2 = vendredi_avant_ecole2;
	}

	public String getSamedi_avant_ecole2() {
		return samedi_avant_ecole2;
	}

	public void setSamedi_avant_ecole2(String samedi_avant_ecole2) {
		this.samedi_avant_ecole2 = samedi_avant_ecole2;
	}

	public String getDimanche_avant_ecole2() {
		return dimanche_avant_ecole2;
	}

	public void setDimanche_avant_ecole2(String dimanche_avant_ecole2) {
		this.dimanche_avant_ecole2 = dimanche_avant_ecole2;
	}

	public String getLundi_matin2() {
		return lundi_matin2;
	}

	public void setLundi_matin2(String lundi_matin2) {
		this.lundi_matin2 = lundi_matin2;
	}

	public String getMardi_matin2() {
		return mardi_matin2;
	}

	public void setMardi_matin2(String mardi_matin2) {
		this.mardi_matin2 = mardi_matin2;
	}

	public String getMercredi_matin2() {
		return mercredi_matin2;
	}

	public void setMercredi_matin2(String mercredi_matin2) {
		this.mercredi_matin2 = mercredi_matin2;
	}

	public String getJeudi_matin2() {
		return jeudi_matin2;
	}

	public void setJeudi_matin2(String jeudi_matin2) {
		this.jeudi_matin2 = jeudi_matin2;
	}

	public String getVendredi_matin2() {
		return vendredi_matin2;
	}

	public void setVendredi_matin2(String vendredi_matin2) {
		this.vendredi_matin2 = vendredi_matin2;
	}

	public String getSamedi_matin2() {
		return samedi_matin2;
	}

	public void setSamedi_matin2(String samedi_matin2) {
		this.samedi_matin2 = samedi_matin2;
	}

	public String getDimanche_matin2() {
		return dimanche_matin2;
	}

	public void setDimanche_matin2(String dimanche_matin2) {
		this.dimanche_matin2 = dimanche_matin2;
	}

	public String getLundi_midi2() {
		return lundi_midi2;
	}

	public void setLundi_midi2(String lundi_midi2) {
		this.lundi_midi2 = lundi_midi2;
	}

	public String getMardi_midi2() {
		return mardi_midi2;
	}

	public void setMardi_midi2(String mardi_midi2) {
		this.mardi_midi2 = mardi_midi2;
	}

	public String getMercredi_midi2() {
		return mercredi_midi2;
	}

	public void setMercredi_midi2(String mercredi_midi2) {
		this.mercredi_midi2 = mercredi_midi2;
	}

	public String getJeudi_midi2() {
		return jeudi_midi2;
	}

	public void setJeudi_midi2(String jeudi_midi2) {
		this.jeudi_midi2 = jeudi_midi2;
	}

	public String getVendredi_midi2() {
		return vendredi_midi2;
	}

	public void setVendredi_midi2(String vendredi_midi2) {
		this.vendredi_midi2 = vendredi_midi2;
	}

	public String getSamedi_midi2() {
		return samedi_midi2;
	}

	public void setSamedi_midi2(String samedi_midi2) {
		this.samedi_midi2 = samedi_midi2;
	}

	public String getDimanche_midi2() {
		return dimanche_midi2;
	}

	public void setDimanche_midi2(String dimanche_midi2) {
		this.dimanche_midi2 = dimanche_midi2;
	}

	public String getLundi_pm2() {
		return lundi_pm2;
	}

	public void setLundi_pm2(String lundi_pm2) {
		this.lundi_pm2 = lundi_pm2;
	}

	public String getMardi_pm2() {
		return mardi_pm2;
	}

	public void setMardi_pm2(String mardi_pm2) {
		this.mardi_pm2 = mardi_pm2;
	}

	public String getMercredi_pm2() {
		return mercredi_pm2;
	}

	public void setMercredi_pm2(String mercredi_pm2) {
		this.mercredi_pm2 = mercredi_pm2;
	}

	public String getJeudi_pm2() {
		return jeudi_pm2;
	}

	public void setJeudi_pm2(String jeudi_pm2) {
		this.jeudi_pm2 = jeudi_pm2;
	}

	public String getVendredi_pm2() {
		return vendredi_pm2;
	}

	public void setVendredi_pm2(String vendredi_pm2) {
		this.vendredi_pm2 = vendredi_pm2;
	}

	public String getSamedi_pm2() {
		return samedi_pm2;
	}

	public void setSamedi_pm2(String samedi_pm2) {
		this.samedi_pm2 = samedi_pm2;
	}

	public String getDimanche_pm2() {
		return dimanche_pm2;
	}

	public void setDimanche_pm2(String dimanche_pm2) {
		this.dimanche_pm2 = dimanche_pm2;
	}

	public String getLundi_apres_ecole2() {
		return lundi_apres_ecole2;
	}

	public void setLundi_apres_ecole2(String lundi_apres_ecole2) {
		this.lundi_apres_ecole2 = lundi_apres_ecole2;
	}

	public String getMardi_apres_ecole2() {
		return mardi_apres_ecole2;
	}

	public void setMardi_apres_ecole2(String mardi_apres_ecole2) {
		this.mardi_apres_ecole2 = mardi_apres_ecole2;
	}

	public String getMercredi_apres_ecole2() {
		return mercredi_apres_ecole2;
	}

	public void setMercredi_apres_ecole2(String mercredi_apres_ecole2) {
		this.mercredi_apres_ecole2 = mercredi_apres_ecole2;
	}

	public String getJeudi_apres_ecole2() {
		return jeudi_apres_ecole2;
	}

	public void setJeudi_apres_ecole2(String jeudi_apres_ecole2) {
		this.jeudi_apres_ecole2 = jeudi_apres_ecole2;
	}

	public String getVendredi_apres_ecole2() {
		return vendredi_apres_ecole2;
	}

	public void setVendredi_apres_ecole2(String vendredi_apres_ecole2) {
		this.vendredi_apres_ecole2 = vendredi_apres_ecole2;
	}

	public String getSamedi_apres_ecole2() {
		return samedi_apres_ecole2;
	}

	public void setSamedi_apres_ecole2(String samedi_apres_ecole2) {
		this.samedi_apres_ecole2 = samedi_apres_ecole2;
	}

	public String getDimanche_apres_ecole2() {
		return dimanche_apres_ecole2;
	}

	public void setDimanche_apres_ecole2(String dimanche_apres_ecole2) {
		this.dimanche_apres_ecole2 = dimanche_apres_ecole2;
	}

	public String getLundi_nuit2() {
		return lundi_nuit2;
	}

	public void setLundi_nuit2(String lundi_nuit2) {
		this.lundi_nuit2 = lundi_nuit2;
	}

	public String getMardi_nuit2() {
		return mardi_nuit2;
	}

	public void setMardi_nuit2(String mardi_nuit2) {
		this.mardi_nuit2 = mardi_nuit2;
	}

	public String getMercredi_nuit2() {
		return mercredi_nuit2;
	}

	public void setMercredi_nuit2(String mercredi_nuit2) {
		this.mercredi_nuit2 = mercredi_nuit2;
	}

	public String getJeudi_nuit2() {
		return jeudi_nuit2;
	}

	public void setJeudi_nuit2(String jeudi_nuit2) {
		this.jeudi_nuit2 = jeudi_nuit2;
	}

	public String getVendredi_nuit2() {
		return vendredi_nuit2;
	}

	public void setVendredi_nuit2(String vendredi_nuit2) {
		this.vendredi_nuit2 = vendredi_nuit2;
	}

	public String getSamedi_nuit2() {
		return samedi_nuit2;
	}

	public void setSamedi_nuit2(String samedi_nuit2) {
		this.samedi_nuit2 = samedi_nuit2;
	}

	public String getDimanche_nuit2() {
		return dimanche_nuit2;
	}

	public void setDimanche_nuit2(String dimanche_nuit2) {
		this.dimanche_nuit2 = dimanche_nuit2;
	}

	public String getDispoDepannagePlaceDisponible2() {
		return dispoDepannagePlaceDisponible2;
	}

	public void setDispoDepannagePlaceDisponible2(String dispoDepannagePlaceDisponible2) {
		this.dispoDepannagePlaceDisponible2 = dispoDepannagePlaceDisponible2;
	}

	public String getPrecisionsDepannagePlaceDisponible2() {
		return precisionsDepannagePlaceDisponible2;
	}

	public void setPrecisionsDepannagePlaceDisponible2(String precisionsDepannagePlaceDisponible2) {
		this.precisionsDepannagePlaceDisponible2 = precisionsDepannagePlaceDisponible2;
	}

	public String getEtatDispo3() {
		return etatDispo3;
	}

	public void setEtatDispo3(String etatDispo3) {
		this.etatDispo3 = etatDispo3;
	}

	public String getPrecisionPlaceFuture3() {
		return precisionPlaceFuture3;
	}

	public void setPrecisionPlaceFuture3(String precisionPlaceFuture3) {
		this.precisionPlaceFuture3 = precisionPlaceFuture3;
	}

	public String getPrecisionsPlaceDisponible3() {
		return precisionsPlaceDisponible3;
	}

	public void setPrecisionsPlaceDisponible3(String precisionsPlaceDisponible3) {
		this.precisionsPlaceDisponible3 = precisionsPlaceDisponible3;
	}

	public String getMoisDispo3() {
		return moisDispo3;
	}

	public void setMoisDispo3(String moisDispo3) {
		this.moisDispo3 = moisDispo3;
	}

	public String getAnneeDispo3() {
		return anneeDispo3;
	}

	public void setAnneeDispo3(String anneeDispo3) {
		this.anneeDispo3 = anneeDispo3;
	}

	public String getLundi_avant_ecole3() {
		return lundi_avant_ecole3;
	}

	public void setLundi_avant_ecole3(String lundi_avant_ecole3) {
		this.lundi_avant_ecole3 = lundi_avant_ecole3;
	}

	public String getMardi_avant_ecole3() {
		return mardi_avant_ecole3;
	}

	public void setMardi_avant_ecole3(String mardi_avant_ecole3) {
		this.mardi_avant_ecole3 = mardi_avant_ecole3;
	}

	public String getMercredi_avant_ecole3() {
		return mercredi_avant_ecole3;
	}

	public void setMercredi_avant_ecole3(String mercredi_avant_ecole3) {
		this.mercredi_avant_ecole3 = mercredi_avant_ecole3;
	}

	public String getJeudi_avant_ecole3() {
		return jeudi_avant_ecole3;
	}

	public void setJeudi_avant_ecole3(String jeudi_avant_ecole3) {
		this.jeudi_avant_ecole3 = jeudi_avant_ecole3;
	}

	public String getVendredi_avant_ecole3() {
		return vendredi_avant_ecole3;
	}

	public void setVendredi_avant_ecole3(String vendredi_avant_ecole3) {
		this.vendredi_avant_ecole3 = vendredi_avant_ecole3;
	}

	public String getSamedi_avant_ecole3() {
		return samedi_avant_ecole3;
	}

	public void setSamedi_avant_ecole3(String samedi_avant_ecole3) {
		this.samedi_avant_ecole3 = samedi_avant_ecole3;
	}

	public String getDimanche_avant_ecole3() {
		return dimanche_avant_ecole3;
	}

	public void setDimanche_avant_ecole3(String dimanche_avant_ecole3) {
		this.dimanche_avant_ecole3 = dimanche_avant_ecole3;
	}

	public String getLundi_matin3() {
		return lundi_matin3;
	}

	public void setLundi_matin3(String lundi_matin3) {
		this.lundi_matin3 = lundi_matin3;
	}

	public String getMardi_matin3() {
		return mardi_matin3;
	}

	public void setMardi_matin3(String mardi_matin3) {
		this.mardi_matin3 = mardi_matin3;
	}

	public String getMercredi_matin3() {
		return mercredi_matin3;
	}

	public void setMercredi_matin3(String mercredi_matin3) {
		this.mercredi_matin3 = mercredi_matin3;
	}

	public String getJeudi_matin3() {
		return jeudi_matin3;
	}

	public void setJeudi_matin3(String jeudi_matin3) {
		this.jeudi_matin3 = jeudi_matin3;
	}

	public String getVendredi_matin3() {
		return vendredi_matin3;
	}

	public void setVendredi_matin3(String vendredi_matin3) {
		this.vendredi_matin3 = vendredi_matin3;
	}

	public String getSamedi_matin3() {
		return samedi_matin3;
	}

	public void setSamedi_matin3(String samedi_matin3) {
		this.samedi_matin3 = samedi_matin3;
	}

	public String getDimanche_matin3() {
		return dimanche_matin3;
	}

	public void setDimanche_matin3(String dimanche_matin3) {
		this.dimanche_matin3 = dimanche_matin3;
	}

	public String getLundi_midi3() {
		return lundi_midi3;
	}

	public void setLundi_midi3(String lundi_midi3) {
		this.lundi_midi3 = lundi_midi3;
	}

	public String getMardi_midi3() {
		return mardi_midi3;
	}

	public void setMardi_midi3(String mardi_midi3) {
		this.mardi_midi3 = mardi_midi3;
	}

	public String getMercredi_midi3() {
		return mercredi_midi3;
	}

	public void setMercredi_midi3(String mercredi_midi3) {
		this.mercredi_midi3 = mercredi_midi3;
	}

	public String getJeudi_midi3() {
		return jeudi_midi3;
	}

	public void setJeudi_midi3(String jeudi_midi3) {
		this.jeudi_midi3 = jeudi_midi3;
	}

	public String getVendredi_midi3() {
		return vendredi_midi3;
	}

	public void setVendredi_midi3(String vendredi_midi3) {
		this.vendredi_midi3 = vendredi_midi3;
	}

	public String getSamedi_midi3() {
		return samedi_midi3;
	}

	public void setSamedi_midi3(String samedi_midi3) {
		this.samedi_midi3 = samedi_midi3;
	}

	public String getDimanche_midi3() {
		return dimanche_midi3;
	}

	public void setDimanche_midi3(String dimanche_midi3) {
		this.dimanche_midi3 = dimanche_midi3;
	}

	public String getLundi_pm3() {
		return lundi_pm3;
	}

	public void setLundi_pm3(String lundi_pm3) {
		this.lundi_pm3 = lundi_pm3;
	}

	public String getMardi_pm3() {
		return mardi_pm3;
	}

	public void setMardi_pm3(String mardi_pm3) {
		this.mardi_pm3 = mardi_pm3;
	}

	public String getMercredi_pm3() {
		return mercredi_pm3;
	}

	public void setMercredi_pm3(String mercredi_pm3) {
		this.mercredi_pm3 = mercredi_pm3;
	}

	public String getJeudi_pm3() {
		return jeudi_pm3;
	}

	public void setJeudi_pm3(String jeudi_pm3) {
		this.jeudi_pm3 = jeudi_pm3;
	}

	public String getVendredi_pm3() {
		return vendredi_pm3;
	}

	public void setVendredi_pm3(String vendredi_pm3) {
		this.vendredi_pm3 = vendredi_pm3;
	}

	public String getSamedi_pm3() {
		return samedi_pm3;
	}

	public void setSamedi_pm3(String samedi_pm3) {
		this.samedi_pm3 = samedi_pm3;
	}

	public String getDimanche_pm3() {
		return dimanche_pm3;
	}

	public void setDimanche_pm3(String dimanche_pm3) {
		this.dimanche_pm3 = dimanche_pm3;
	}

	public String getLundi_apres_ecole3() {
		return lundi_apres_ecole3;
	}

	public void setLundi_apres_ecole3(String lundi_apres_ecole3) {
		this.lundi_apres_ecole3 = lundi_apres_ecole3;
	}

	public String getMardi_apres_ecole3() {
		return mardi_apres_ecole3;
	}

	public void setMardi_apres_ecole3(String mardi_apres_ecole3) {
		this.mardi_apres_ecole3 = mardi_apres_ecole3;
	}

	public String getMercredi_apres_ecole3() {
		return mercredi_apres_ecole3;
	}

	public void setMercredi_apres_ecole3(String mercredi_apres_ecole3) {
		this.mercredi_apres_ecole3 = mercredi_apres_ecole3;
	}

	public String getJeudi_apres_ecole3() {
		return jeudi_apres_ecole3;
	}

	public void setJeudi_apres_ecole3(String jeudi_apres_ecole3) {
		this.jeudi_apres_ecole3 = jeudi_apres_ecole3;
	}

	public String getVendredi_apres_ecole3() {
		return vendredi_apres_ecole3;
	}

	public void setVendredi_apres_ecole3(String vendredi_apres_ecole3) {
		this.vendredi_apres_ecole3 = vendredi_apres_ecole3;
	}

	public String getSamedi_apres_ecole3() {
		return samedi_apres_ecole3;
	}

	public void setSamedi_apres_ecole3(String samedi_apres_ecole3) {
		this.samedi_apres_ecole3 = samedi_apres_ecole3;
	}

	public String getDimanche_apres_ecole3() {
		return dimanche_apres_ecole3;
	}

	public void setDimanche_apres_ecole3(String dimanche_apres_ecole3) {
		this.dimanche_apres_ecole3 = dimanche_apres_ecole3;
	}

	public String getLundi_nuit3() {
		return lundi_nuit3;
	}

	public void setLundi_nuit3(String lundi_nuit3) {
		this.lundi_nuit3 = lundi_nuit3;
	}

	public String getMardi_nuit3() {
		return mardi_nuit3;
	}

	public void setMardi_nuit3(String mardi_nuit3) {
		this.mardi_nuit3 = mardi_nuit3;
	}

	public String getMercredi_nuit3() {
		return mercredi_nuit3;
	}

	public void setMercredi_nuit3(String mercredi_nuit3) {
		this.mercredi_nuit3 = mercredi_nuit3;
	}

	public String getJeudi_nuit3() {
		return jeudi_nuit3;
	}

	public void setJeudi_nuit3(String jeudi_nuit3) {
		this.jeudi_nuit3 = jeudi_nuit3;
	}

	public String getVendredi_nuit3() {
		return vendredi_nuit3;
	}

	public void setVendredi_nuit3(String vendredi_nuit3) {
		this.vendredi_nuit3 = vendredi_nuit3;
	}

	public String getSamedi_nuit3() {
		return samedi_nuit3;
	}

	public void setSamedi_nuit3(String samedi_nuit3) {
		this.samedi_nuit3 = samedi_nuit3;
	}

	public String getDimanche_nuit3() {
		return dimanche_nuit3;
	}

	public void setDimanche_nuit3(String dimanche_nuit3) {
		this.dimanche_nuit3 = dimanche_nuit3;
	}

	public String getDispoDepannagePlaceDisponible3() {
		return dispoDepannagePlaceDisponible3;
	}

	public void setDispoDepannagePlaceDisponible3(String dispoDepannagePlaceDisponible3) {
		this.dispoDepannagePlaceDisponible3 = dispoDepannagePlaceDisponible3;
	}

	public String getPrecisionsDepannagePlaceDisponible3() {
		return precisionsDepannagePlaceDisponible3;
	}

	public void setPrecisionsDepannagePlaceDisponible3(String precisionsDepannagePlaceDisponible3) {
		this.precisionsDepannagePlaceDisponible3 = precisionsDepannagePlaceDisponible3;
	}

	public String getEtatDispo4() {
		return etatDispo4;
	}

	public void setEtatDispo4(String etatDispo4) {
		this.etatDispo4 = etatDispo4;
	}

	public String getPrecisionPlaceFuture4() {
		return precisionPlaceFuture4;
	}

	public void setPrecisionPlaceFuture4(String precisionPlaceFuture4) {
		this.precisionPlaceFuture4 = precisionPlaceFuture4;
	}

	public String getPrecisionsPlaceDisponible4() {
		return precisionsPlaceDisponible4;
	}

	public void setPrecisionsPlaceDisponible4(String precisionsPlaceDisponible4) {
		this.precisionsPlaceDisponible4 = precisionsPlaceDisponible4;
	}

	public String getMoisDispo4() {
		return moisDispo4;
	}

	public void setMoisDispo4(String moisDispo4) {
		this.moisDispo4 = moisDispo4;
	}

	public String getAnneeDispo4() {
		return anneeDispo4;
	}

	public void setAnneeDispo4(String anneeDispo4) {
		this.anneeDispo4 = anneeDispo4;
	}

	public String getLundi_avant_ecole4() {
		return lundi_avant_ecole4;
	}

	public void setLundi_avant_ecole4(String lundi_avant_ecole4) {
		this.lundi_avant_ecole4 = lundi_avant_ecole4;
	}

	public String getMardi_avant_ecole4() {
		return mardi_avant_ecole4;
	}

	public void setMardi_avant_ecole4(String mardi_avant_ecole4) {
		this.mardi_avant_ecole4 = mardi_avant_ecole4;
	}

	public String getMercredi_avant_ecole4() {
		return mercredi_avant_ecole4;
	}

	public void setMercredi_avant_ecole4(String mercredi_avant_ecole4) {
		this.mercredi_avant_ecole4 = mercredi_avant_ecole4;
	}

	public String getJeudi_avant_ecole4() {
		return jeudi_avant_ecole4;
	}

	public void setJeudi_avant_ecole4(String jeudi_avant_ecole4) {
		this.jeudi_avant_ecole4 = jeudi_avant_ecole4;
	}

	public String getVendredi_avant_ecole4() {
		return vendredi_avant_ecole4;
	}

	public void setVendredi_avant_ecole4(String vendredi_avant_ecole4) {
		this.vendredi_avant_ecole4 = vendredi_avant_ecole4;
	}

	public String getSamedi_avant_ecole4() {
		return samedi_avant_ecole4;
	}

	public void setSamedi_avant_ecole4(String samedi_avant_ecole4) {
		this.samedi_avant_ecole4 = samedi_avant_ecole4;
	}

	public String getDimanche_avant_ecole4() {
		return dimanche_avant_ecole4;
	}

	public void setDimanche_avant_ecole4(String dimanche_avant_ecole4) {
		this.dimanche_avant_ecole4 = dimanche_avant_ecole4;
	}

	public String getLundi_matin4() {
		return lundi_matin4;
	}

	public void setLundi_matin4(String lundi_matin4) {
		this.lundi_matin4 = lundi_matin4;
	}

	public String getMardi_matin4() {
		return mardi_matin4;
	}

	public void setMardi_matin4(String mardi_matin4) {
		this.mardi_matin4 = mardi_matin4;
	}

	public String getMercredi_matin4() {
		return mercredi_matin4;
	}

	public void setMercredi_matin4(String mercredi_matin4) {
		this.mercredi_matin4 = mercredi_matin4;
	}

	public String getJeudi_matin4() {
		return jeudi_matin4;
	}

	public void setJeudi_matin4(String jeudi_matin4) {
		this.jeudi_matin4 = jeudi_matin4;
	}

	public String getVendredi_matin4() {
		return vendredi_matin4;
	}

	public void setVendredi_matin4(String vendredi_matin4) {
		this.vendredi_matin4 = vendredi_matin4;
	}

	public String getSamedi_matin4() {
		return samedi_matin4;
	}

	public void setSamedi_matin4(String samedi_matin4) {
		this.samedi_matin4 = samedi_matin4;
	}

	public String getDimanche_matin4() {
		return dimanche_matin4;
	}

	public void setDimanche_matin4(String dimanche_matin4) {
		this.dimanche_matin4 = dimanche_matin4;
	}

	public String getLundi_midi4() {
		return lundi_midi4;
	}

	public void setLundi_midi4(String lundi_midi4) {
		this.lundi_midi4 = lundi_midi4;
	}

	public String getMardi_midi4() {
		return mardi_midi4;
	}

	public void setMardi_midi4(String mardi_midi4) {
		this.mardi_midi4 = mardi_midi4;
	}

	public String getMercredi_midi4() {
		return mercredi_midi4;
	}

	public void setMercredi_midi4(String mercredi_midi4) {
		this.mercredi_midi4 = mercredi_midi4;
	}

	public String getJeudi_midi4() {
		return jeudi_midi4;
	}

	public void setJeudi_midi4(String jeudi_midi4) {
		this.jeudi_midi4 = jeudi_midi4;
	}

	public String getVendredi_midi4() {
		return vendredi_midi4;
	}

	public void setVendredi_midi4(String vendredi_midi4) {
		this.vendredi_midi4 = vendredi_midi4;
	}

	public String getSamedi_midi4() {
		return samedi_midi4;
	}

	public void setSamedi_midi4(String samedi_midi4) {
		this.samedi_midi4 = samedi_midi4;
	}

	public String getDimanche_midi4() {
		return dimanche_midi4;
	}

	public void setDimanche_midi4(String dimanche_midi4) {
		this.dimanche_midi4 = dimanche_midi4;
	}

	public String getLundi_pm4() {
		return lundi_pm4;
	}

	public void setLundi_pm4(String lundi_pm4) {
		this.lundi_pm4 = lundi_pm4;
	}

	public String getMardi_pm4() {
		return mardi_pm4;
	}

	public void setMardi_pm4(String mardi_pm4) {
		this.mardi_pm4 = mardi_pm4;
	}

	public String getMercredi_pm4() {
		return mercredi_pm4;
	}

	public void setMercredi_pm4(String mercredi_pm4) {
		this.mercredi_pm4 = mercredi_pm4;
	}

	public String getJeudi_pm4() {
		return jeudi_pm4;
	}

	public void setJeudi_pm4(String jeudi_pm4) {
		this.jeudi_pm4 = jeudi_pm4;
	}

	public String getVendredi_pm4() {
		return vendredi_pm4;
	}

	public void setVendredi_pm4(String vendredi_pm4) {
		this.vendredi_pm4 = vendredi_pm4;
	}

	public String getSamedi_pm4() {
		return samedi_pm4;
	}

	public void setSamedi_pm4(String samedi_pm4) {
		this.samedi_pm4 = samedi_pm4;
	}

	public String getDimanche_pm4() {
		return dimanche_pm4;
	}

	public void setDimanche_pm4(String dimanche_pm4) {
		this.dimanche_pm4 = dimanche_pm4;
	}

	public String getLundi_apres_ecole4() {
		return lundi_apres_ecole4;
	}

	public void setLundi_apres_ecole4(String lundi_apres_ecole4) {
		this.lundi_apres_ecole4 = lundi_apres_ecole4;
	}

	public String getMardi_apres_ecole4() {
		return mardi_apres_ecole4;
	}

	public void setMardi_apres_ecole4(String mardi_apres_ecole4) {
		this.mardi_apres_ecole4 = mardi_apres_ecole4;
	}

	public String getMercredi_apres_ecole4() {
		return mercredi_apres_ecole4;
	}

	public void setMercredi_apres_ecole4(String mercredi_apres_ecole4) {
		this.mercredi_apres_ecole4 = mercredi_apres_ecole4;
	}

	public String getJeudi_apres_ecole4() {
		return jeudi_apres_ecole4;
	}

	public void setJeudi_apres_ecole4(String jeudi_apres_ecole4) {
		this.jeudi_apres_ecole4 = jeudi_apres_ecole4;
	}

	public String getVendredi_apres_ecole4() {
		return vendredi_apres_ecole4;
	}

	public void setVendredi_apres_ecole4(String vendredi_apres_ecole4) {
		this.vendredi_apres_ecole4 = vendredi_apres_ecole4;
	}

	public String getSamedi_apres_ecole4() {
		return samedi_apres_ecole4;
	}

	public void setSamedi_apres_ecole4(String samedi_apres_ecole4) {
		this.samedi_apres_ecole4 = samedi_apres_ecole4;
	}

	public String getDimanche_apres_ecole4() {
		return dimanche_apres_ecole4;
	}

	public void setDimanche_apres_ecole4(String dimanche_apres_ecole4) {
		this.dimanche_apres_ecole4 = dimanche_apres_ecole4;
	}

	public String getLundi_nuit4() {
		return lundi_nuit4;
	}

	public void setLundi_nuit4(String lundi_nuit4) {
		this.lundi_nuit4 = lundi_nuit4;
	}

	public String getMardi_nuit4() {
		return mardi_nuit4;
	}

	public void setMardi_nuit4(String mardi_nuit4) {
		this.mardi_nuit4 = mardi_nuit4;
	}

	public String getMercredi_nuit4() {
		return mercredi_nuit4;
	}

	public void setMercredi_nuit4(String mercredi_nuit4) {
		this.mercredi_nuit4 = mercredi_nuit4;
	}

	public String getJeudi_nuit4() {
		return jeudi_nuit4;
	}

	public void setJeudi_nuit4(String jeudi_nuit4) {
		this.jeudi_nuit4 = jeudi_nuit4;
	}

	public String getVendredi_nuit4() {
		return vendredi_nuit4;
	}

	public void setVendredi_nuit4(String vendredi_nuit4) {
		this.vendredi_nuit4 = vendredi_nuit4;
	}

	public String getSamedi_nuit4() {
		return samedi_nuit4;
	}

	public void setSamedi_nuit4(String samedi_nuit4) {
		this.samedi_nuit4 = samedi_nuit4;
	}

	public String getDimanche_nuit4() {
		return dimanche_nuit4;
	}

	public void setDimanche_nuit4(String dimanche_nuit4) {
		this.dimanche_nuit4 = dimanche_nuit4;
	}

	public String getDispoDepannagePlaceDisponible4() {
		return dispoDepannagePlaceDisponible4;
	}

	public void setDispoDepannagePlaceDisponible4(String dispoDepannagePlaceDisponible4) {
		this.dispoDepannagePlaceDisponible4 = dispoDepannagePlaceDisponible4;
	}

	public String getPrecisionsDepannagePlaceDisponible4() {
		return precisionsDepannagePlaceDisponible4;
	}

	public void setPrecisionsDepannagePlaceDisponible4(String precisionsDepannagePlaceDisponible4) {
		this.precisionsDepannagePlaceDisponible4 = precisionsDepannagePlaceDisponible4;
	}

	public String getEtatDispo5() {
		return etatDispo5;
	}

	public void setEtatDispo5(String etatDispo5) {
		this.etatDispo5 = etatDispo5;
	}

	public String getPrecisionPlaceFuture5() {
		return precisionPlaceFuture5;
	}

	public void setPrecisionPlaceFuture5(String precisionPlaceFuture5) {
		this.precisionPlaceFuture5 = precisionPlaceFuture5;
	}

	public String getPrecisionsPlaceDisponible5() {
		return precisionsPlaceDisponible5;
	}

	public void setPrecisionsPlaceDisponible5(String precisionsPlaceDisponible5) {
		this.precisionsPlaceDisponible5 = precisionsPlaceDisponible5;
	}

	public String getMoisDispo5() {
		return moisDispo5;
	}

	public void setMoisDispo5(String moisDispo5) {
		this.moisDispo5 = moisDispo5;
	}

	public String getAnneeDispo5() {
		return anneeDispo5;
	}

	public void setAnneeDispo5(String anneeDispo5) {
		this.anneeDispo5 = anneeDispo5;
	}

	public String getLundi_avant_ecole5() {
		return lundi_avant_ecole5;
	}

	public void setLundi_avant_ecole5(String lundi_avant_ecole5) {
		this.lundi_avant_ecole5 = lundi_avant_ecole5;
	}

	public String getMardi_avant_ecole5() {
		return mardi_avant_ecole5;
	}

	public void setMardi_avant_ecole5(String mardi_avant_ecole5) {
		this.mardi_avant_ecole5 = mardi_avant_ecole5;
	}

	public String getMercredi_avant_ecole5() {
		return mercredi_avant_ecole5;
	}

	public void setMercredi_avant_ecole5(String mercredi_avant_ecole5) {
		this.mercredi_avant_ecole5 = mercredi_avant_ecole5;
	}

	public String getJeudi_avant_ecole5() {
		return jeudi_avant_ecole5;
	}

	public void setJeudi_avant_ecole5(String jeudi_avant_ecole5) {
		this.jeudi_avant_ecole5 = jeudi_avant_ecole5;
	}

	public String getVendredi_avant_ecole5() {
		return vendredi_avant_ecole5;
	}

	public void setVendredi_avant_ecole5(String vendredi_avant_ecole5) {
		this.vendredi_avant_ecole5 = vendredi_avant_ecole5;
	}

	public String getSamedi_avant_ecole5() {
		return samedi_avant_ecole5;
	}

	public void setSamedi_avant_ecole5(String samedi_avant_ecole5) {
		this.samedi_avant_ecole5 = samedi_avant_ecole5;
	}

	public String getDimanche_avant_ecole5() {
		return dimanche_avant_ecole5;
	}

	public void setDimanche_avant_ecole5(String dimanche_avant_ecole5) {
		this.dimanche_avant_ecole5 = dimanche_avant_ecole5;
	}

	public String getLundi_matin5() {
		return lundi_matin5;
	}

	public void setLundi_matin5(String lundi_matin5) {
		this.lundi_matin5 = lundi_matin5;
	}

	public String getMardi_matin5() {
		return mardi_matin5;
	}

	public void setMardi_matin5(String mardi_matin5) {
		this.mardi_matin5 = mardi_matin5;
	}

	public String getMercredi_matin5() {
		return mercredi_matin5;
	}

	public void setMercredi_matin5(String mercredi_matin5) {
		this.mercredi_matin5 = mercredi_matin5;
	}

	public String getJeudi_matin5() {
		return jeudi_matin5;
	}

	public void setJeudi_matin5(String jeudi_matin5) {
		this.jeudi_matin5 = jeudi_matin5;
	}

	public String getVendredi_matin5() {
		return vendredi_matin5;
	}

	public void setVendredi_matin5(String vendredi_matin5) {
		this.vendredi_matin5 = vendredi_matin5;
	}

	public String getSamedi_matin5() {
		return samedi_matin5;
	}

	public void setSamedi_matin5(String samedi_matin5) {
		this.samedi_matin5 = samedi_matin5;
	}

	public String getDimanche_matin5() {
		return dimanche_matin5;
	}

	public void setDimanche_matin5(String dimanche_matin5) {
		this.dimanche_matin5 = dimanche_matin5;
	}

	public String getLundi_midi5() {
		return lundi_midi5;
	}

	public void setLundi_midi5(String lundi_midi5) {
		this.lundi_midi5 = lundi_midi5;
	}

	public String getMardi_midi5() {
		return mardi_midi5;
	}

	public void setMardi_midi5(String mardi_midi5) {
		this.mardi_midi5 = mardi_midi5;
	}

	public String getMercredi_midi5() {
		return mercredi_midi5;
	}

	public void setMercredi_midi5(String mercredi_midi5) {
		this.mercredi_midi5 = mercredi_midi5;
	}

	public String getJeudi_midi5() {
		return jeudi_midi5;
	}

	public void setJeudi_midi5(String jeudi_midi5) {
		this.jeudi_midi5 = jeudi_midi5;
	}

	public String getVendredi_midi5() {
		return vendredi_midi5;
	}

	public void setVendredi_midi5(String vendredi_midi5) {
		this.vendredi_midi5 = vendredi_midi5;
	}

	public String getSamedi_midi5() {
		return samedi_midi5;
	}

	public void setSamedi_midi5(String samedi_midi5) {
		this.samedi_midi5 = samedi_midi5;
	}

	public String getDimanche_midi5() {
		return dimanche_midi5;
	}

	public void setDimanche_midi5(String dimanche_midi5) {
		this.dimanche_midi5 = dimanche_midi5;
	}

	public String getLundi_pm5() {
		return lundi_pm5;
	}

	public void setLundi_pm5(String lundi_pm5) {
		this.lundi_pm5 = lundi_pm5;
	}

	public String getMardi_pm5() {
		return mardi_pm5;
	}

	public void setMardi_pm5(String mardi_pm5) {
		this.mardi_pm5 = mardi_pm5;
	}

	public String getMercredi_pm5() {
		return mercredi_pm5;
	}

	public void setMercredi_pm5(String mercredi_pm5) {
		this.mercredi_pm5 = mercredi_pm5;
	}

	public String getJeudi_pm5() {
		return jeudi_pm5;
	}

	public void setJeudi_pm5(String jeudi_pm5) {
		this.jeudi_pm5 = jeudi_pm5;
	}

	public String getVendredi_pm5() {
		return vendredi_pm5;
	}

	public void setVendredi_pm5(String vendredi_pm5) {
		this.vendredi_pm5 = vendredi_pm5;
	}

	public String getSamedi_pm5() {
		return samedi_pm5;
	}

	public void setSamedi_pm5(String samedi_pm5) {
		this.samedi_pm5 = samedi_pm5;
	}

	public String getDimanche_pm5() {
		return dimanche_pm5;
	}

	public void setDimanche_pm5(String dimanche_pm5) {
		this.dimanche_pm5 = dimanche_pm5;
	}

	public String getLundi_apres_ecole5() {
		return lundi_apres_ecole5;
	}

	public void setLundi_apres_ecole5(String lundi_apres_ecole5) {
		this.lundi_apres_ecole5 = lundi_apres_ecole5;
	}

	public String getMardi_apres_ecole5() {
		return mardi_apres_ecole5;
	}

	public void setMardi_apres_ecole5(String mardi_apres_ecole5) {
		this.mardi_apres_ecole5 = mardi_apres_ecole5;
	}

	public String getMercredi_apres_ecole5() {
		return mercredi_apres_ecole5;
	}

	public void setMercredi_apres_ecole5(String mercredi_apres_ecole5) {
		this.mercredi_apres_ecole5 = mercredi_apres_ecole5;
	}

	public String getJeudi_apres_ecole5() {
		return jeudi_apres_ecole5;
	}

	public void setJeudi_apres_ecole5(String jeudi_apres_ecole5) {
		this.jeudi_apres_ecole5 = jeudi_apres_ecole5;
	}

	public String getVendredi_apres_ecole5() {
		return vendredi_apres_ecole5;
	}

	public void setVendredi_apres_ecole5(String vendredi_apres_ecole5) {
		this.vendredi_apres_ecole5 = vendredi_apres_ecole5;
	}

	public String getSamedi_apres_ecole5() {
		return samedi_apres_ecole5;
	}

	public void setSamedi_apres_ecole5(String samedi_apres_ecole5) {
		this.samedi_apres_ecole5 = samedi_apres_ecole5;
	}

	public String getDimanche_apres_ecole5() {
		return dimanche_apres_ecole5;
	}

	public void setDimanche_apres_ecole5(String dimanche_apres_ecole5) {
		this.dimanche_apres_ecole5 = dimanche_apres_ecole5;
	}

	public String getLundi_nuit5() {
		return lundi_nuit5;
	}

	public void setLundi_nuit5(String lundi_nuit5) {
		this.lundi_nuit5 = lundi_nuit5;
	}

	public String getMardi_nuit5() {
		return mardi_nuit5;
	}

	public void setMardi_nuit5(String mardi_nuit5) {
		this.mardi_nuit5 = mardi_nuit5;
	}

	public String getMercredi_nuit5() {
		return mercredi_nuit5;
	}

	public void setMercredi_nuit5(String mercredi_nuit5) {
		this.mercredi_nuit5 = mercredi_nuit5;
	}

	public String getJeudi_nuit5() {
		return jeudi_nuit5;
	}

	public void setJeudi_nuit5(String jeudi_nuit5) {
		this.jeudi_nuit5 = jeudi_nuit5;
	}

	public String getVendredi_nuit5() {
		return vendredi_nuit5;
	}

	public void setVendredi_nuit5(String vendredi_nuit5) {
		this.vendredi_nuit5 = vendredi_nuit5;
	}

	public String getSamedi_nuit5() {
		return samedi_nuit5;
	}

	public void setSamedi_nuit5(String samedi_nuit5) {
		this.samedi_nuit5 = samedi_nuit5;
	}

	public String getDimanche_nuit5() {
		return dimanche_nuit5;
	}

	public void setDimanche_nuit5(String dimanche_nuit5) {
		this.dimanche_nuit5 = dimanche_nuit5;
	}

	public String getDispoDepannagePlaceDisponible5() {
		return dispoDepannagePlaceDisponible5;
	}

	public void setDispoDepannagePlaceDisponible5(String dispoDepannagePlaceDisponible5) {
		this.dispoDepannagePlaceDisponible5 = dispoDepannagePlaceDisponible5;
	}

	public String getPrecisionsDepannagePlaceDisponible5() {
		return precisionsDepannagePlaceDisponible5;
	}

	public void setPrecisionsDepannagePlaceDisponible5(String precisionsDepannagePlaceDisponible5) {
		this.precisionsDepannagePlaceDisponible5 = precisionsDepannagePlaceDisponible5;
	}

	public String getEtatDispo6() {
		return etatDispo6;
	}

	public void setEtatDispo6(String etatDispo6) {
		this.etatDispo6 = etatDispo6;
	}

	public String getPrecisionPlaceFuture6() {
		return precisionPlaceFuture6;
	}

	public void setPrecisionPlaceFuture6(String precisionPlaceFuture6) {
		this.precisionPlaceFuture6 = precisionPlaceFuture6;
	}

	public String getPrecisionsPlaceDisponible6() {
		return precisionsPlaceDisponible6;
	}

	public void setPrecisionsPlaceDisponible6(String precisionsPlaceDisponible6) {
		this.precisionsPlaceDisponible6 = precisionsPlaceDisponible6;
	}

	public String getMoisDispo6() {
		return moisDispo6;
	}

	public void setMoisDispo6(String moisDispo6) {
		this.moisDispo6 = moisDispo6;
	}

	public String getAnneeDispo6() {
		return anneeDispo6;
	}

	public void setAnneeDispo6(String anneeDispo6) {
		this.anneeDispo6 = anneeDispo6;
	}

	public String getLundi_avant_ecole6() {
		return lundi_avant_ecole6;
	}

	public void setLundi_avant_ecole6(String lundi_avant_ecole6) {
		this.lundi_avant_ecole6 = lundi_avant_ecole6;
	}

	public String getMardi_avant_ecole6() {
		return mardi_avant_ecole6;
	}

	public void setMardi_avant_ecole6(String mardi_avant_ecole6) {
		this.mardi_avant_ecole6 = mardi_avant_ecole6;
	}

	public String getMercredi_avant_ecole6() {
		return mercredi_avant_ecole6;
	}

	public void setMercredi_avant_ecole6(String mercredi_avant_ecole6) {
		this.mercredi_avant_ecole6 = mercredi_avant_ecole6;
	}

	public String getJeudi_avant_ecole6() {
		return jeudi_avant_ecole6;
	}

	public void setJeudi_avant_ecole6(String jeudi_avant_ecole6) {
		this.jeudi_avant_ecole6 = jeudi_avant_ecole6;
	}

	public String getVendredi_avant_ecole6() {
		return vendredi_avant_ecole6;
	}

	public void setVendredi_avant_ecole6(String vendredi_avant_ecole6) {
		this.vendredi_avant_ecole6 = vendredi_avant_ecole6;
	}

	public String getSamedi_avant_ecole6() {
		return samedi_avant_ecole6;
	}

	public void setSamedi_avant_ecole6(String samedi_avant_ecole6) {
		this.samedi_avant_ecole6 = samedi_avant_ecole6;
	}

	public String getDimanche_avant_ecole6() {
		return dimanche_avant_ecole6;
	}

	public void setDimanche_avant_ecole6(String dimanche_avant_ecole6) {
		this.dimanche_avant_ecole6 = dimanche_avant_ecole6;
	}

	public String getLundi_matin6() {
		return lundi_matin6;
	}

	public void setLundi_matin6(String lundi_matin6) {
		this.lundi_matin6 = lundi_matin6;
	}

	public String getMardi_matin6() {
		return mardi_matin6;
	}

	public void setMardi_matin6(String mardi_matin6) {
		this.mardi_matin6 = mardi_matin6;
	}

	public String getMercredi_matin6() {
		return mercredi_matin6;
	}

	public void setMercredi_matin6(String mercredi_matin6) {
		this.mercredi_matin6 = mercredi_matin6;
	}

	public String getJeudi_matin6() {
		return jeudi_matin6;
	}

	public void setJeudi_matin6(String jeudi_matin6) {
		this.jeudi_matin6 = jeudi_matin6;
	}

	public String getVendredi_matin6() {
		return vendredi_matin6;
	}

	public void setVendredi_matin6(String vendredi_matin6) {
		this.vendredi_matin6 = vendredi_matin6;
	}

	public String getSamedi_matin6() {
		return samedi_matin6;
	}

	public void setSamedi_matin6(String samedi_matin6) {
		this.samedi_matin6 = samedi_matin6;
	}

	public String getDimanche_matin6() {
		return dimanche_matin6;
	}

	public void setDimanche_matin6(String dimanche_matin6) {
		this.dimanche_matin6 = dimanche_matin6;
	}

	public String getLundi_midi6() {
		return lundi_midi6;
	}

	public void setLundi_midi6(String lundi_midi6) {
		this.lundi_midi6 = lundi_midi6;
	}

	public String getMardi_midi6() {
		return mardi_midi6;
	}

	public void setMardi_midi6(String mardi_midi6) {
		this.mardi_midi6 = mardi_midi6;
	}

	public String getMercredi_midi6() {
		return mercredi_midi6;
	}

	public void setMercredi_midi6(String mercredi_midi6) {
		this.mercredi_midi6 = mercredi_midi6;
	}

	public String getJeudi_midi6() {
		return jeudi_midi6;
	}

	public void setJeudi_midi6(String jeudi_midi6) {
		this.jeudi_midi6 = jeudi_midi6;
	}

	public String getVendredi_midi6() {
		return vendredi_midi6;
	}

	public void setVendredi_midi6(String vendredi_midi6) {
		this.vendredi_midi6 = vendredi_midi6;
	}

	public String getSamedi_midi6() {
		return samedi_midi6;
	}

	public void setSamedi_midi6(String samedi_midi6) {
		this.samedi_midi6 = samedi_midi6;
	}

	public String getDimanche_midi6() {
		return dimanche_midi6;
	}

	public void setDimanche_midi6(String dimanche_midi6) {
		this.dimanche_midi6 = dimanche_midi6;
	}

	public String getLundi_pm6() {
		return lundi_pm6;
	}

	public void setLundi_pm6(String lundi_pm6) {
		this.lundi_pm6 = lundi_pm6;
	}

	public String getMardi_pm6() {
		return mardi_pm6;
	}

	public void setMardi_pm6(String mardi_pm6) {
		this.mardi_pm6 = mardi_pm6;
	}

	public String getMercredi_pm6() {
		return mercredi_pm6;
	}

	public void setMercredi_pm6(String mercredi_pm6) {
		this.mercredi_pm6 = mercredi_pm6;
	}

	public String getJeudi_pm6() {
		return jeudi_pm6;
	}

	public void setJeudi_pm6(String jeudi_pm6) {
		this.jeudi_pm6 = jeudi_pm6;
	}

	public String getVendredi_pm6() {
		return vendredi_pm6;
	}

	public void setVendredi_pm6(String vendredi_pm6) {
		this.vendredi_pm6 = vendredi_pm6;
	}

	public String getSamedi_pm6() {
		return samedi_pm6;
	}

	public void setSamedi_pm6(String samedi_pm6) {
		this.samedi_pm6 = samedi_pm6;
	}

	public String getDimanche_pm6() {
		return dimanche_pm6;
	}

	public void setDimanche_pm6(String dimanche_pm6) {
		this.dimanche_pm6 = dimanche_pm6;
	}

	public String getLundi_apres_ecole6() {
		return lundi_apres_ecole6;
	}

	public void setLundi_apres_ecole6(String lundi_apres_ecole6) {
		this.lundi_apres_ecole6 = lundi_apres_ecole6;
	}

	public String getMardi_apres_ecole6() {
		return mardi_apres_ecole6;
	}

	public void setMardi_apres_ecole6(String mardi_apres_ecole6) {
		this.mardi_apres_ecole6 = mardi_apres_ecole6;
	}

	public String getMercredi_apres_ecole6() {
		return mercredi_apres_ecole6;
	}

	public void setMercredi_apres_ecole6(String mercredi_apres_ecole6) {
		this.mercredi_apres_ecole6 = mercredi_apres_ecole6;
	}

	public String getJeudi_apres_ecole6() {
		return jeudi_apres_ecole6;
	}

	public void setJeudi_apres_ecole6(String jeudi_apres_ecole6) {
		this.jeudi_apres_ecole6 = jeudi_apres_ecole6;
	}

	public String getVendredi_apres_ecole6() {
		return vendredi_apres_ecole6;
	}

	public void setVendredi_apres_ecole6(String vendredi_apres_ecole6) {
		this.vendredi_apres_ecole6 = vendredi_apres_ecole6;
	}

	public String getSamedi_apres_ecole6() {
		return samedi_apres_ecole6;
	}

	public void setSamedi_apres_ecole6(String samedi_apres_ecole6) {
		this.samedi_apres_ecole6 = samedi_apres_ecole6;
	}

	public String getDimanche_apres_ecole6() {
		return dimanche_apres_ecole6;
	}

	public void setDimanche_apres_ecole6(String dimanche_apres_ecole6) {
		this.dimanche_apres_ecole6 = dimanche_apres_ecole6;
	}

	public String getLundi_nuit6() {
		return lundi_nuit6;
	}

	public void setLundi_nuit6(String lundi_nuit6) {
		this.lundi_nuit6 = lundi_nuit6;
	}

	public String getMardi_nuit6() {
		return mardi_nuit6;
	}

	public void setMardi_nuit6(String mardi_nuit6) {
		this.mardi_nuit6 = mardi_nuit6;
	}

	public String getMercredi_nuit6() {
		return mercredi_nuit6;
	}

	public void setMercredi_nuit6(String mercredi_nuit6) {
		this.mercredi_nuit6 = mercredi_nuit6;
	}

	public String getJeudi_nuit6() {
		return jeudi_nuit6;
	}

	public void setJeudi_nuit6(String jeudi_nuit6) {
		this.jeudi_nuit6 = jeudi_nuit6;
	}

	public String getVendredi_nuit6() {
		return vendredi_nuit6;
	}

	public void setVendredi_nuit6(String vendredi_nuit6) {
		this.vendredi_nuit6 = vendredi_nuit6;
	}

	public String getSamedi_nuit6() {
		return samedi_nuit6;
	}

	public void setSamedi_nuit6(String samedi_nuit6) {
		this.samedi_nuit6 = samedi_nuit6;
	}

	public String getDimanche_nuit6() {
		return dimanche_nuit6;
	}

	public void setDimanche_nuit6(String dimanche_nuit6) {
		this.dimanche_nuit6 = dimanche_nuit6;
	}

	public String getDispoDepannagePlaceDisponible6() {
		return dispoDepannagePlaceDisponible6;
	}

	public void setDispoDepannagePlaceDisponible6(String dispoDepannagePlaceDisponible6) {
		this.dispoDepannagePlaceDisponible6 = dispoDepannagePlaceDisponible6;
	}

	public String getPrecisionsDepannagePlaceDisponible6() {
		return precisionsDepannagePlaceDisponible6;
	}

	public void setPrecisionsDepannagePlaceDisponible6(String precisionsDepannagePlaceDisponible6) {
		this.precisionsDepannagePlaceDisponible6 = precisionsDepannagePlaceDisponible6;
	}

	public String getEtatDispo7() {
		return etatDispo7;
	}

	public void setEtatDispo7(String etatDispo7) {
		this.etatDispo7 = etatDispo7;
	}

	public String getPrecisionPlaceFuture7() {
		return precisionPlaceFuture7;
	}

	public void setPrecisionPlaceFuture7(String precisionPlaceFuture7) {
		this.precisionPlaceFuture7 = precisionPlaceFuture7;
	}

	public String getPrecisionsPlaceDisponible7() {
		return precisionsPlaceDisponible7;
	}

	public void setPrecisionsPlaceDisponible7(String precisionsPlaceDisponible7) {
		this.precisionsPlaceDisponible7 = precisionsPlaceDisponible7;
	}

	public String getMoisDispo7() {
		return moisDispo7;
	}

	public void setMoisDispo7(String moisDispo7) {
		this.moisDispo7 = moisDispo7;
	}

	public String getAnneeDispo7() {
		return anneeDispo7;
	}

	public void setAnneeDispo7(String anneeDispo7) {
		this.anneeDispo7 = anneeDispo7;
	}

	public String getLundi_avant_ecole7() {
		return lundi_avant_ecole7;
	}

	public void setLundi_avant_ecole7(String lundi_avant_ecole7) {
		this.lundi_avant_ecole7 = lundi_avant_ecole7;
	}

	public String getMardi_avant_ecole7() {
		return mardi_avant_ecole7;
	}

	public void setMardi_avant_ecole7(String mardi_avant_ecole7) {
		this.mardi_avant_ecole7 = mardi_avant_ecole7;
	}

	public String getMercredi_avant_ecole7() {
		return mercredi_avant_ecole7;
	}

	public void setMercredi_avant_ecole7(String mercredi_avant_ecole7) {
		this.mercredi_avant_ecole7 = mercredi_avant_ecole7;
	}

	public String getJeudi_avant_ecole7() {
		return jeudi_avant_ecole7;
	}

	public void setJeudi_avant_ecole7(String jeudi_avant_ecole7) {
		this.jeudi_avant_ecole7 = jeudi_avant_ecole7;
	}

	public String getVendredi_avant_ecole7() {
		return vendredi_avant_ecole7;
	}

	public void setVendredi_avant_ecole7(String vendredi_avant_ecole7) {
		this.vendredi_avant_ecole7 = vendredi_avant_ecole7;
	}

	public String getSamedi_avant_ecole7() {
		return samedi_avant_ecole7;
	}

	public void setSamedi_avant_ecole7(String samedi_avant_ecole7) {
		this.samedi_avant_ecole7 = samedi_avant_ecole7;
	}

	public String getDimanche_avant_ecole7() {
		return dimanche_avant_ecole7;
	}

	public void setDimanche_avant_ecole7(String dimanche_avant_ecole7) {
		this.dimanche_avant_ecole7 = dimanche_avant_ecole7;
	}

	public String getLundi_matin7() {
		return lundi_matin7;
	}

	public void setLundi_matin7(String lundi_matin7) {
		this.lundi_matin7 = lundi_matin7;
	}

	public String getMardi_matin7() {
		return mardi_matin7;
	}

	public void setMardi_matin7(String mardi_matin7) {
		this.mardi_matin7 = mardi_matin7;
	}

	public String getMercredi_matin7() {
		return mercredi_matin7;
	}

	public void setMercredi_matin7(String mercredi_matin7) {
		this.mercredi_matin7 = mercredi_matin7;
	}

	public String getJeudi_matin7() {
		return jeudi_matin7;
	}

	public void setJeudi_matin7(String jeudi_matin7) {
		this.jeudi_matin7 = jeudi_matin7;
	}

	public String getVendredi_matin7() {
		return vendredi_matin7;
	}

	public void setVendredi_matin7(String vendredi_matin7) {
		this.vendredi_matin7 = vendredi_matin7;
	}

	public String getSamedi_matin7() {
		return samedi_matin7;
	}

	public void setSamedi_matin7(String samedi_matin7) {
		this.samedi_matin7 = samedi_matin7;
	}

	public String getDimanche_matin7() {
		return dimanche_matin7;
	}

	public void setDimanche_matin7(String dimanche_matin7) {
		this.dimanche_matin7 = dimanche_matin7;
	}

	public String getLundi_midi7() {
		return lundi_midi7;
	}

	public void setLundi_midi7(String lundi_midi7) {
		this.lundi_midi7 = lundi_midi7;
	}

	public String getMardi_midi7() {
		return mardi_midi7;
	}

	public void setMardi_midi7(String mardi_midi7) {
		this.mardi_midi7 = mardi_midi7;
	}

	public String getMercredi_midi7() {
		return mercredi_midi7;
	}

	public void setMercredi_midi7(String mercredi_midi7) {
		this.mercredi_midi7 = mercredi_midi7;
	}

	public String getJeudi_midi7() {
		return jeudi_midi7;
	}

	public void setJeudi_midi7(String jeudi_midi7) {
		this.jeudi_midi7 = jeudi_midi7;
	}

	public String getVendredi_midi7() {
		return vendredi_midi7;
	}

	public void setVendredi_midi7(String vendredi_midi7) {
		this.vendredi_midi7 = vendredi_midi7;
	}

	public String getSamedi_midi7() {
		return samedi_midi7;
	}

	public void setSamedi_midi7(String samedi_midi7) {
		this.samedi_midi7 = samedi_midi7;
	}

	public String getDimanche_midi7() {
		return dimanche_midi7;
	}

	public void setDimanche_midi7(String dimanche_midi7) {
		this.dimanche_midi7 = dimanche_midi7;
	}

	public String getLundi_pm7() {
		return lundi_pm7;
	}

	public void setLundi_pm7(String lundi_pm7) {
		this.lundi_pm7 = lundi_pm7;
	}

	public String getMardi_pm7() {
		return mardi_pm7;
	}

	public void setMardi_pm7(String mardi_pm7) {
		this.mardi_pm7 = mardi_pm7;
	}

	public String getMercredi_pm7() {
		return mercredi_pm7;
	}

	public void setMercredi_pm7(String mercredi_pm7) {
		this.mercredi_pm7 = mercredi_pm7;
	}

	public String getJeudi_pm7() {
		return jeudi_pm7;
	}

	public void setJeudi_pm7(String jeudi_pm7) {
		this.jeudi_pm7 = jeudi_pm7;
	}

	public String getVendredi_pm7() {
		return vendredi_pm7;
	}

	public void setVendredi_pm7(String vendredi_pm7) {
		this.vendredi_pm7 = vendredi_pm7;
	}

	public String getSamedi_pm7() {
		return samedi_pm7;
	}

	public void setSamedi_pm7(String samedi_pm7) {
		this.samedi_pm7 = samedi_pm7;
	}

	public String getDimanche_pm7() {
		return dimanche_pm7;
	}

	public void setDimanche_pm7(String dimanche_pm7) {
		this.dimanche_pm7 = dimanche_pm7;
	}

	public String getLundi_apres_ecole7() {
		return lundi_apres_ecole7;
	}

	public void setLundi_apres_ecole7(String lundi_apres_ecole7) {
		this.lundi_apres_ecole7 = lundi_apres_ecole7;
	}

	public String getMardi_apres_ecole7() {
		return mardi_apres_ecole7;
	}

	public void setMardi_apres_ecole7(String mardi_apres_ecole7) {
		this.mardi_apres_ecole7 = mardi_apres_ecole7;
	}

	public String getMercredi_apres_ecole7() {
		return mercredi_apres_ecole7;
	}

	public void setMercredi_apres_ecole7(String mercredi_apres_ecole7) {
		this.mercredi_apres_ecole7 = mercredi_apres_ecole7;
	}

	public String getJeudi_apres_ecole7() {
		return jeudi_apres_ecole7;
	}

	public void setJeudi_apres_ecole7(String jeudi_apres_ecole7) {
		this.jeudi_apres_ecole7 = jeudi_apres_ecole7;
	}

	public String getVendredi_apres_ecole7() {
		return vendredi_apres_ecole7;
	}

	public void setVendredi_apres_ecole7(String vendredi_apres_ecole7) {
		this.vendredi_apres_ecole7 = vendredi_apres_ecole7;
	}

	public String getSamedi_apres_ecole7() {
		return samedi_apres_ecole7;
	}

	public void setSamedi_apres_ecole7(String samedi_apres_ecole7) {
		this.samedi_apres_ecole7 = samedi_apres_ecole7;
	}

	public String getDimanche_apres_ecole7() {
		return dimanche_apres_ecole7;
	}

	public void setDimanche_apres_ecole7(String dimanche_apres_ecole7) {
		this.dimanche_apres_ecole7 = dimanche_apres_ecole7;
	}

	public String getLundi_nuit7() {
		return lundi_nuit7;
	}

	public void setLundi_nuit7(String lundi_nuit7) {
		this.lundi_nuit7 = lundi_nuit7;
	}

	public String getMardi_nuit7() {
		return mardi_nuit7;
	}

	public void setMardi_nuit7(String mardi_nuit7) {
		this.mardi_nuit7 = mardi_nuit7;
	}

	public String getMercredi_nuit7() {
		return mercredi_nuit7;
	}

	public void setMercredi_nuit7(String mercredi_nuit7) {
		this.mercredi_nuit7 = mercredi_nuit7;
	}

	public String getJeudi_nuit7() {
		return jeudi_nuit7;
	}

	public void setJeudi_nuit7(String jeudi_nuit7) {
		this.jeudi_nuit7 = jeudi_nuit7;
	}

	public String getVendredi_nuit7() {
		return vendredi_nuit7;
	}

	public void setVendredi_nuit7(String vendredi_nuit7) {
		this.vendredi_nuit7 = vendredi_nuit7;
	}

	public String getSamedi_nuit7() {
		return samedi_nuit7;
	}

	public void setSamedi_nuit7(String samedi_nuit7) {
		this.samedi_nuit7 = samedi_nuit7;
	}

	public String getDimanche_nuit7() {
		return dimanche_nuit7;
	}

	public void setDimanche_nuit7(String dimanche_nuit7) {
		this.dimanche_nuit7 = dimanche_nuit7;
	}

	public String getDispoDepannagePlaceDisponible7() {
		return dispoDepannagePlaceDisponible7;
	}

	public void setDispoDepannagePlaceDisponible7(String dispoDepannagePlaceDisponible7) {
		this.dispoDepannagePlaceDisponible7 = dispoDepannagePlaceDisponible7;
	}

	public String getPrecisionsDepannagePlaceDisponible7() {
		return precisionsDepannagePlaceDisponible7;
	}

	public void setPrecisionsDepannagePlaceDisponible7(String precisionsDepannagePlaceDisponible7) {
		this.precisionsDepannagePlaceDisponible7 = precisionsDepannagePlaceDisponible7;
	}

	public String getEtatDispo8() {
		return etatDispo8;
	}

	public void setEtatDispo8(String etatDispo8) {
		this.etatDispo8 = etatDispo8;
	}

	public String getPrecisionPlaceFuture8() {
		return precisionPlaceFuture8;
	}

	public void setPrecisionPlaceFuture8(String precisionPlaceFuture8) {
		this.precisionPlaceFuture8 = precisionPlaceFuture8;
	}

	public String getPrecisionsPlaceDisponible8() {
		return precisionsPlaceDisponible8;
	}

	public void setPrecisionsPlaceDisponible8(String precisionsPlaceDisponible8) {
		this.precisionsPlaceDisponible8 = precisionsPlaceDisponible8;
	}

	public String getMoisDispo8() {
		return moisDispo8;
	}

	public void setMoisDispo8(String moisDispo8) {
		this.moisDispo8 = moisDispo8;
	}

	public String getAnneeDispo8() {
		return anneeDispo8;
	}

	public void setAnneeDispo8(String anneeDispo8) {
		this.anneeDispo8 = anneeDispo8;
	}

	public String getLundi_avant_ecole8() {
		return lundi_avant_ecole8;
	}

	public void setLundi_avant_ecole8(String lundi_avant_ecole8) {
		this.lundi_avant_ecole8 = lundi_avant_ecole8;
	}

	public String getMardi_avant_ecole8() {
		return mardi_avant_ecole8;
	}

	public void setMardi_avant_ecole8(String mardi_avant_ecole8) {
		this.mardi_avant_ecole8 = mardi_avant_ecole8;
	}

	public String getMercredi_avant_ecole8() {
		return mercredi_avant_ecole8;
	}

	public void setMercredi_avant_ecole8(String mercredi_avant_ecole8) {
		this.mercredi_avant_ecole8 = mercredi_avant_ecole8;
	}

	public String getJeudi_avant_ecole8() {
		return jeudi_avant_ecole8;
	}

	public void setJeudi_avant_ecole8(String jeudi_avant_ecole8) {
		this.jeudi_avant_ecole8 = jeudi_avant_ecole8;
	}

	public String getVendredi_avant_ecole8() {
		return vendredi_avant_ecole8;
	}

	public void setVendredi_avant_ecole8(String vendredi_avant_ecole8) {
		this.vendredi_avant_ecole8 = vendredi_avant_ecole8;
	}

	public String getSamedi_avant_ecole8() {
		return samedi_avant_ecole8;
	}

	public void setSamedi_avant_ecole8(String samedi_avant_ecole8) {
		this.samedi_avant_ecole8 = samedi_avant_ecole8;
	}

	public String getDimanche_avant_ecole8() {
		return dimanche_avant_ecole8;
	}

	public void setDimanche_avant_ecole8(String dimanche_avant_ecole8) {
		this.dimanche_avant_ecole8 = dimanche_avant_ecole8;
	}

	public String getLundi_matin8() {
		return lundi_matin8;
	}

	public void setLundi_matin8(String lundi_matin8) {
		this.lundi_matin8 = lundi_matin8;
	}

	public String getMardi_matin8() {
		return mardi_matin8;
	}

	public void setMardi_matin8(String mardi_matin8) {
		this.mardi_matin8 = mardi_matin8;
	}

	public String getMercredi_matin8() {
		return mercredi_matin8;
	}

	public void setMercredi_matin8(String mercredi_matin8) {
		this.mercredi_matin8 = mercredi_matin8;
	}

	public String getJeudi_matin8() {
		return jeudi_matin8;
	}

	public void setJeudi_matin8(String jeudi_matin8) {
		this.jeudi_matin8 = jeudi_matin8;
	}

	public String getVendredi_matin8() {
		return vendredi_matin8;
	}

	public void setVendredi_matin8(String vendredi_matin8) {
		this.vendredi_matin8 = vendredi_matin8;
	}

	public String getSamedi_matin8() {
		return samedi_matin8;
	}

	public void setSamedi_matin8(String samedi_matin8) {
		this.samedi_matin8 = samedi_matin8;
	}

	public String getDimanche_matin8() {
		return dimanche_matin8;
	}

	public void setDimanche_matin8(String dimanche_matin8) {
		this.dimanche_matin8 = dimanche_matin8;
	}

	public String getLundi_midi8() {
		return lundi_midi8;
	}

	public void setLundi_midi8(String lundi_midi8) {
		this.lundi_midi8 = lundi_midi8;
	}

	public String getMardi_midi8() {
		return mardi_midi8;
	}

	public void setMardi_midi8(String mardi_midi8) {
		this.mardi_midi8 = mardi_midi8;
	}

	public String getMercredi_midi8() {
		return mercredi_midi8;
	}

	public void setMercredi_midi8(String mercredi_midi8) {
		this.mercredi_midi8 = mercredi_midi8;
	}

	public String getJeudi_midi8() {
		return jeudi_midi8;
	}

	public void setJeudi_midi8(String jeudi_midi8) {
		this.jeudi_midi8 = jeudi_midi8;
	}

	public String getVendredi_midi8() {
		return vendredi_midi8;
	}

	public void setVendredi_midi8(String vendredi_midi8) {
		this.vendredi_midi8 = vendredi_midi8;
	}

	public String getSamedi_midi8() {
		return samedi_midi8;
	}

	public void setSamedi_midi8(String samedi_midi8) {
		this.samedi_midi8 = samedi_midi8;
	}

	public String getDimanche_midi8() {
		return dimanche_midi8;
	}

	public void setDimanche_midi8(String dimanche_midi8) {
		this.dimanche_midi8 = dimanche_midi8;
	}

	public String getLundi_pm8() {
		return lundi_pm8;
	}

	public void setLundi_pm8(String lundi_pm8) {
		this.lundi_pm8 = lundi_pm8;
	}

	public String getMardi_pm8() {
		return mardi_pm8;
	}

	public void setMardi_pm8(String mardi_pm8) {
		this.mardi_pm8 = mardi_pm8;
	}

	public String getMercredi_pm8() {
		return mercredi_pm8;
	}

	public void setMercredi_pm8(String mercredi_pm8) {
		this.mercredi_pm8 = mercredi_pm8;
	}

	public String getJeudi_pm8() {
		return jeudi_pm8;
	}

	public void setJeudi_pm8(String jeudi_pm8) {
		this.jeudi_pm8 = jeudi_pm8;
	}

	public String getVendredi_pm8() {
		return vendredi_pm8;
	}

	public void setVendredi_pm8(String vendredi_pm8) {
		this.vendredi_pm8 = vendredi_pm8;
	}

	public String getSamedi_pm8() {
		return samedi_pm8;
	}

	public void setSamedi_pm8(String samedi_pm8) {
		this.samedi_pm8 = samedi_pm8;
	}

	public String getDimanche_pm8() {
		return dimanche_pm8;
	}

	public void setDimanche_pm8(String dimanche_pm8) {
		this.dimanche_pm8 = dimanche_pm8;
	}

	public String getLundi_apres_ecole8() {
		return lundi_apres_ecole8;
	}

	public void setLundi_apres_ecole8(String lundi_apres_ecole8) {
		this.lundi_apres_ecole8 = lundi_apres_ecole8;
	}

	public String getMardi_apres_ecole8() {
		return mardi_apres_ecole8;
	}

	public void setMardi_apres_ecole8(String mardi_apres_ecole8) {
		this.mardi_apres_ecole8 = mardi_apres_ecole8;
	}

	public String getMercredi_apres_ecole8() {
		return mercredi_apres_ecole8;
	}

	public void setMercredi_apres_ecole8(String mercredi_apres_ecole8) {
		this.mercredi_apres_ecole8 = mercredi_apres_ecole8;
	}

	public String getJeudi_apres_ecole8() {
		return jeudi_apres_ecole8;
	}

	public void setJeudi_apres_ecole8(String jeudi_apres_ecole8) {
		this.jeudi_apres_ecole8 = jeudi_apres_ecole8;
	}

	public String getVendredi_apres_ecole8() {
		return vendredi_apres_ecole8;
	}

	public void setVendredi_apres_ecole8(String vendredi_apres_ecole8) {
		this.vendredi_apres_ecole8 = vendredi_apres_ecole8;
	}

	public String getSamedi_apres_ecole8() {
		return samedi_apres_ecole8;
	}

	public void setSamedi_apres_ecole8(String samedi_apres_ecole8) {
		this.samedi_apres_ecole8 = samedi_apres_ecole8;
	}

	public String getDimanche_apres_ecole8() {
		return dimanche_apres_ecole8;
	}

	public void setDimanche_apres_ecole8(String dimanche_apres_ecole8) {
		this.dimanche_apres_ecole8 = dimanche_apres_ecole8;
	}

	public String getLundi_nuit8() {
		return lundi_nuit8;
	}

	public void setLundi_nuit8(String lundi_nuit8) {
		this.lundi_nuit8 = lundi_nuit8;
	}

	public String getMardi_nuit8() {
		return mardi_nuit8;
	}

	public void setMardi_nuit8(String mardi_nuit8) {
		this.mardi_nuit8 = mardi_nuit8;
	}

	public String getMercredi_nuit8() {
		return mercredi_nuit8;
	}

	public void setMercredi_nuit8(String mercredi_nuit8) {
		this.mercredi_nuit8 = mercredi_nuit8;
	}

	public String getJeudi_nuit8() {
		return jeudi_nuit8;
	}

	public void setJeudi_nuit8(String jeudi_nuit8) {
		this.jeudi_nuit8 = jeudi_nuit8;
	}

	public String getVendredi_nuit8() {
		return vendredi_nuit8;
	}

	public void setVendredi_nuit8(String vendredi_nuit8) {
		this.vendredi_nuit8 = vendredi_nuit8;
	}

	public String getSamedi_nuit8() {
		return samedi_nuit8;
	}

	public void setSamedi_nuit8(String samedi_nuit8) {
		this.samedi_nuit8 = samedi_nuit8;
	}

	public String getDimanche_nuit8() {
		return dimanche_nuit8;
	}

	public void setDimanche_nuit8(String dimanche_nuit8) {
		this.dimanche_nuit8 = dimanche_nuit8;
	}

	public String getDispoDepannagePlaceDisponible8() {
		return dispoDepannagePlaceDisponible8;
	}

	public void setDispoDepannagePlaceDisponible8(String dispoDepannagePlaceDisponible8) {
		this.dispoDepannagePlaceDisponible8 = dispoDepannagePlaceDisponible8;
	}

	public String getPrecisionsDepannagePlaceDisponible8() {
		return precisionsDepannagePlaceDisponible8;
	}

	public void setPrecisionsDepannagePlaceDisponible8(String precisionsDepannagePlaceDisponible8) {
		this.precisionsDepannagePlaceDisponible8 = precisionsDepannagePlaceDisponible8;
	}

	public String getEtatDispo9() {
		return etatDispo9;
	}

	public void setEtatDispo9(String etatDispo9) {
		this.etatDispo9 = etatDispo9;
	}

	public String getPrecisionPlaceFuture9() {
		return precisionPlaceFuture9;
	}

	public void setPrecisionPlaceFuture9(String precisionPlaceFuture9) {
		this.precisionPlaceFuture9 = precisionPlaceFuture9;
	}

	public String getPrecisionsPlaceDisponible9() {
		return precisionsPlaceDisponible9;
	}

	public void setPrecisionsPlaceDisponible9(String precisionsPlaceDisponible9) {
		this.precisionsPlaceDisponible9 = precisionsPlaceDisponible9;
	}

	public String getMoisDispo9() {
		return moisDispo9;
	}

	public void setMoisDispo9(String moisDispo9) {
		this.moisDispo9 = moisDispo9;
	}

	public String getAnneeDispo9() {
		return anneeDispo9;
	}

	public void setAnneeDispo9(String anneeDispo9) {
		this.anneeDispo9 = anneeDispo9;
	}

	public String getLundi_avant_ecole9() {
		return lundi_avant_ecole9;
	}

	public void setLundi_avant_ecole9(String lundi_avant_ecole9) {
		this.lundi_avant_ecole9 = lundi_avant_ecole9;
	}

	public String getMardi_avant_ecole9() {
		return mardi_avant_ecole9;
	}

	public void setMardi_avant_ecole9(String mardi_avant_ecole9) {
		this.mardi_avant_ecole9 = mardi_avant_ecole9;
	}

	public String getMercredi_avant_ecole9() {
		return mercredi_avant_ecole9;
	}

	public void setMercredi_avant_ecole9(String mercredi_avant_ecole9) {
		this.mercredi_avant_ecole9 = mercredi_avant_ecole9;
	}

	public String getJeudi_avant_ecole9() {
		return jeudi_avant_ecole9;
	}

	public void setJeudi_avant_ecole9(String jeudi_avant_ecole9) {
		this.jeudi_avant_ecole9 = jeudi_avant_ecole9;
	}

	public String getVendredi_avant_ecole9() {
		return vendredi_avant_ecole9;
	}

	public void setVendredi_avant_ecole9(String vendredi_avant_ecole9) {
		this.vendredi_avant_ecole9 = vendredi_avant_ecole9;
	}

	public String getSamedi_avant_ecole9() {
		return samedi_avant_ecole9;
	}

	public void setSamedi_avant_ecole9(String samedi_avant_ecole9) {
		this.samedi_avant_ecole9 = samedi_avant_ecole9;
	}

	public String getDimanche_avant_ecole9() {
		return dimanche_avant_ecole9;
	}

	public void setDimanche_avant_ecole9(String dimanche_avant_ecole9) {
		this.dimanche_avant_ecole9 = dimanche_avant_ecole9;
	}

	public String getLundi_matin9() {
		return lundi_matin9;
	}

	public void setLundi_matin9(String lundi_matin9) {
		this.lundi_matin9 = lundi_matin9;
	}

	public String getMardi_matin9() {
		return mardi_matin9;
	}

	public void setMardi_matin9(String mardi_matin9) {
		this.mardi_matin9 = mardi_matin9;
	}

	public String getMercredi_matin9() {
		return mercredi_matin9;
	}

	public void setMercredi_matin9(String mercredi_matin9) {
		this.mercredi_matin9 = mercredi_matin9;
	}

	public String getJeudi_matin9() {
		return jeudi_matin9;
	}

	public void setJeudi_matin9(String jeudi_matin9) {
		this.jeudi_matin9 = jeudi_matin9;
	}

	public String getVendredi_matin9() {
		return vendredi_matin9;
	}

	public void setVendredi_matin9(String vendredi_matin9) {
		this.vendredi_matin9 = vendredi_matin9;
	}

	public String getSamedi_matin9() {
		return samedi_matin9;
	}

	public void setSamedi_matin9(String samedi_matin9) {
		this.samedi_matin9 = samedi_matin9;
	}

	public String getDimanche_matin9() {
		return dimanche_matin9;
	}

	public void setDimanche_matin9(String dimanche_matin9) {
		this.dimanche_matin9 = dimanche_matin9;
	}

	public String getLundi_midi9() {
		return lundi_midi9;
	}

	public void setLundi_midi9(String lundi_midi9) {
		this.lundi_midi9 = lundi_midi9;
	}

	public String getMardi_midi9() {
		return mardi_midi9;
	}

	public void setMardi_midi9(String mardi_midi9) {
		this.mardi_midi9 = mardi_midi9;
	}

	public String getMercredi_midi9() {
		return mercredi_midi9;
	}

	public void setMercredi_midi9(String mercredi_midi9) {
		this.mercredi_midi9 = mercredi_midi9;
	}

	public String getJeudi_midi9() {
		return jeudi_midi9;
	}

	public void setJeudi_midi9(String jeudi_midi9) {
		this.jeudi_midi9 = jeudi_midi9;
	}

	public String getVendredi_midi9() {
		return vendredi_midi9;
	}

	public void setVendredi_midi9(String vendredi_midi9) {
		this.vendredi_midi9 = vendredi_midi9;
	}

	public String getSamedi_midi9() {
		return samedi_midi9;
	}

	public void setSamedi_midi9(String samedi_midi9) {
		this.samedi_midi9 = samedi_midi9;
	}

	public String getDimanche_midi9() {
		return dimanche_midi9;
	}

	public void setDimanche_midi9(String dimanche_midi9) {
		this.dimanche_midi9 = dimanche_midi9;
	}

	public String getLundi_pm9() {
		return lundi_pm9;
	}

	public void setLundi_pm9(String lundi_pm9) {
		this.lundi_pm9 = lundi_pm9;
	}

	public String getMardi_pm9() {
		return mardi_pm9;
	}

	public void setMardi_pm9(String mardi_pm9) {
		this.mardi_pm9 = mardi_pm9;
	}

	public String getMercredi_pm9() {
		return mercredi_pm9;
	}

	public void setMercredi_pm9(String mercredi_pm9) {
		this.mercredi_pm9 = mercredi_pm9;
	}

	public String getJeudi_pm9() {
		return jeudi_pm9;
	}

	public void setJeudi_pm9(String jeudi_pm9) {
		this.jeudi_pm9 = jeudi_pm9;
	}

	public String getVendredi_pm9() {
		return vendredi_pm9;
	}

	public void setVendredi_pm9(String vendredi_pm9) {
		this.vendredi_pm9 = vendredi_pm9;
	}

	public String getSamedi_pm9() {
		return samedi_pm9;
	}

	public void setSamedi_pm9(String samedi_pm9) {
		this.samedi_pm9 = samedi_pm9;
	}

	public String getDimanche_pm9() {
		return dimanche_pm9;
	}

	public void setDimanche_pm9(String dimanche_pm9) {
		this.dimanche_pm9 = dimanche_pm9;
	}

	public String getLundi_apres_ecole9() {
		return lundi_apres_ecole9;
	}

	public void setLundi_apres_ecole9(String lundi_apres_ecole9) {
		this.lundi_apres_ecole9 = lundi_apres_ecole9;
	}

	public String getMardi_apres_ecole9() {
		return mardi_apres_ecole9;
	}

	public void setMardi_apres_ecole9(String mardi_apres_ecole9) {
		this.mardi_apres_ecole9 = mardi_apres_ecole9;
	}

	public String getMercredi_apres_ecole9() {
		return mercredi_apres_ecole9;
	}

	public void setMercredi_apres_ecole9(String mercredi_apres_ecole9) {
		this.mercredi_apres_ecole9 = mercredi_apres_ecole9;
	}

	public String getJeudi_apres_ecole9() {
		return jeudi_apres_ecole9;
	}

	public void setJeudi_apres_ecole9(String jeudi_apres_ecole9) {
		this.jeudi_apres_ecole9 = jeudi_apres_ecole9;
	}

	public String getVendredi_apres_ecole9() {
		return vendredi_apres_ecole9;
	}

	public void setVendredi_apres_ecole9(String vendredi_apres_ecole9) {
		this.vendredi_apres_ecole9 = vendredi_apres_ecole9;
	}

	public String getSamedi_apres_ecole9() {
		return samedi_apres_ecole9;
	}

	public void setSamedi_apres_ecole9(String samedi_apres_ecole9) {
		this.samedi_apres_ecole9 = samedi_apres_ecole9;
	}

	public String getDimanche_apres_ecole9() {
		return dimanche_apres_ecole9;
	}

	public void setDimanche_apres_ecole9(String dimanche_apres_ecole9) {
		this.dimanche_apres_ecole9 = dimanche_apres_ecole9;
	}

	public String getLundi_nuit9() {
		return lundi_nuit9;
	}

	public void setLundi_nuit9(String lundi_nuit9) {
		this.lundi_nuit9 = lundi_nuit9;
	}

	public String getMardi_nuit9() {
		return mardi_nuit9;
	}

	public void setMardi_nuit9(String mardi_nuit9) {
		this.mardi_nuit9 = mardi_nuit9;
	}

	public String getMercredi_nuit9() {
		return mercredi_nuit9;
	}

	public void setMercredi_nuit9(String mercredi_nuit9) {
		this.mercredi_nuit9 = mercredi_nuit9;
	}

	public String getJeudi_nuit9() {
		return jeudi_nuit9;
	}

	public void setJeudi_nuit9(String jeudi_nuit9) {
		this.jeudi_nuit9 = jeudi_nuit9;
	}

	public String getVendredi_nuit9() {
		return vendredi_nuit9;
	}

	public void setVendredi_nuit9(String vendredi_nuit9) {
		this.vendredi_nuit9 = vendredi_nuit9;
	}

	public String getSamedi_nuit9() {
		return samedi_nuit9;
	}

	public void setSamedi_nuit9(String samedi_nuit9) {
		this.samedi_nuit9 = samedi_nuit9;
	}

	public String getDimanche_nuit9() {
		return dimanche_nuit9;
	}

	public void setDimanche_nuit9(String dimanche_nuit9) {
		this.dimanche_nuit9 = dimanche_nuit9;
	}

	public String getDispoDepannagePlaceDisponible9() {
		return dispoDepannagePlaceDisponible9;
	}

	public void setDispoDepannagePlaceDisponible9(String dispoDepannagePlaceDisponible9) {
		this.dispoDepannagePlaceDisponible9 = dispoDepannagePlaceDisponible9;
	}

	public String getPrecisionsDepannagePlaceDisponible9() {
		return precisionsDepannagePlaceDisponible9;
	}

	public void setPrecisionsDepannagePlaceDisponible9(String precisionsDepannagePlaceDisponible9) {
		this.precisionsDepannagePlaceDisponible9 = precisionsDepannagePlaceDisponible9;
	}

	public String getEtatDispo10() {
		return etatDispo10;
	}

	public void setEtatDispo10(String etatDispo10) {
		this.etatDispo10 = etatDispo10;
	}

	public String getPrecisionPlaceFuture10() {
		return precisionPlaceFuture10;
	}

	public void setPrecisionPlaceFuture10(String precisionPlaceFuture10) {
		this.precisionPlaceFuture10 = precisionPlaceFuture10;
	}

	public String getPrecisionsPlaceDisponible10() {
		return precisionsPlaceDisponible10;
	}

	public void setPrecisionsPlaceDisponible10(String precisionsPlaceDisponible10) {
		this.precisionsPlaceDisponible10 = precisionsPlaceDisponible10;
	}

	public String getMoisDispo10() {
		return moisDispo10;
	}

	public void setMoisDispo10(String moisDispo10) {
		this.moisDispo10 = moisDispo10;
	}

	public String getAnneeDispo10() {
		return anneeDispo10;
	}

	public void setAnneeDispo10(String anneeDispo10) {
		this.anneeDispo10 = anneeDispo10;
	}

	public String getLundi_avant_ecole10() {
		return lundi_avant_ecole10;
	}

	public void setLundi_avant_ecole10(String lundi_avant_ecole10) {
		this.lundi_avant_ecole10 = lundi_avant_ecole10;
	}

	public String getMardi_avant_ecole10() {
		return mardi_avant_ecole10;
	}

	public void setMardi_avant_ecole10(String mardi_avant_ecole10) {
		this.mardi_avant_ecole10 = mardi_avant_ecole10;
	}

	public String getMercredi_avant_ecole10() {
		return mercredi_avant_ecole10;
	}

	public void setMercredi_avant_ecole10(String mercredi_avant_ecole10) {
		this.mercredi_avant_ecole10 = mercredi_avant_ecole10;
	}

	public String getJeudi_avant_ecole10() {
		return jeudi_avant_ecole10;
	}

	public void setJeudi_avant_ecole10(String jeudi_avant_ecole10) {
		this.jeudi_avant_ecole10 = jeudi_avant_ecole10;
	}

	public String getVendredi_avant_ecole10() {
		return vendredi_avant_ecole10;
	}

	public void setVendredi_avant_ecole10(String vendredi_avant_ecole10) {
		this.vendredi_avant_ecole10 = vendredi_avant_ecole10;
	}

	public String getSamedi_avant_ecole10() {
		return samedi_avant_ecole10;
	}

	public void setSamedi_avant_ecole10(String samedi_avant_ecole10) {
		this.samedi_avant_ecole10 = samedi_avant_ecole10;
	}

	public String getDimanche_avant_ecole10() {
		return dimanche_avant_ecole10;
	}

	public void setDimanche_avant_ecole10(String dimanche_avant_ecole10) {
		this.dimanche_avant_ecole10 = dimanche_avant_ecole10;
	}

	public String getLundi_matin10() {
		return lundi_matin10;
	}

	public void setLundi_matin10(String lundi_matin10) {
		this.lundi_matin10 = lundi_matin10;
	}

	public String getMardi_matin10() {
		return mardi_matin10;
	}

	public void setMardi_matin10(String mardi_matin10) {
		this.mardi_matin10 = mardi_matin10;
	}

	public String getMercredi_matin10() {
		return mercredi_matin10;
	}

	public void setMercredi_matin10(String mercredi_matin10) {
		this.mercredi_matin10 = mercredi_matin10;
	}

	public String getJeudi_matin10() {
		return jeudi_matin10;
	}

	public void setJeudi_matin10(String jeudi_matin10) {
		this.jeudi_matin10 = jeudi_matin10;
	}

	public String getVendredi_matin10() {
		return vendredi_matin10;
	}

	public void setVendredi_matin10(String vendredi_matin10) {
		this.vendredi_matin10 = vendredi_matin10;
	}

	public String getSamedi_matin10() {
		return samedi_matin10;
	}

	public void setSamedi_matin10(String samedi_matin10) {
		this.samedi_matin10 = samedi_matin10;
	}

	public String getDimanche_matin10() {
		return dimanche_matin10;
	}

	public void setDimanche_matin10(String dimanche_matin10) {
		this.dimanche_matin10 = dimanche_matin10;
	}

	public String getLundi_midi10() {
		return lundi_midi10;
	}

	public void setLundi_midi10(String lundi_midi10) {
		this.lundi_midi10 = lundi_midi10;
	}

	public String getMardi_midi10() {
		return mardi_midi10;
	}

	public void setMardi_midi10(String mardi_midi10) {
		this.mardi_midi10 = mardi_midi10;
	}

	public String getMercredi_midi10() {
		return mercredi_midi10;
	}

	public void setMercredi_midi10(String mercredi_midi10) {
		this.mercredi_midi10 = mercredi_midi10;
	}

	public String getJeudi_midi10() {
		return jeudi_midi10;
	}

	public void setJeudi_midi10(String jeudi_midi10) {
		this.jeudi_midi10 = jeudi_midi10;
	}

	public String getVendredi_midi10() {
		return vendredi_midi10;
	}

	public void setVendredi_midi10(String vendredi_midi10) {
		this.vendredi_midi10 = vendredi_midi10;
	}

	public String getSamedi_midi10() {
		return samedi_midi10;
	}

	public void setSamedi_midi10(String samedi_midi10) {
		this.samedi_midi10 = samedi_midi10;
	}

	public String getDimanche_midi10() {
		return dimanche_midi10;
	}

	public void setDimanche_midi10(String dimanche_midi10) {
		this.dimanche_midi10 = dimanche_midi10;
	}

	public String getLundi_pm10() {
		return lundi_pm10;
	}

	public void setLundi_pm10(String lundi_pm10) {
		this.lundi_pm10 = lundi_pm10;
	}

	public String getMardi_pm10() {
		return mardi_pm10;
	}

	public void setMardi_pm10(String mardi_pm10) {
		this.mardi_pm10 = mardi_pm10;
	}

	public String getMercredi_pm10() {
		return mercredi_pm10;
	}

	public void setMercredi_pm10(String mercredi_pm10) {
		this.mercredi_pm10 = mercredi_pm10;
	}

	public String getJeudi_pm10() {
		return jeudi_pm10;
	}

	public void setJeudi_pm10(String jeudi_pm10) {
		this.jeudi_pm10 = jeudi_pm10;
	}

	public String getVendredi_pm10() {
		return vendredi_pm10;
	}

	public void setVendredi_pm10(String vendredi_pm10) {
		this.vendredi_pm10 = vendredi_pm10;
	}

	public String getSamedi_pm10() {
		return samedi_pm10;
	}

	public void setSamedi_pm10(String samedi_pm10) {
		this.samedi_pm10 = samedi_pm10;
	}

	public String getDimanche_pm10() {
		return dimanche_pm10;
	}

	public void setDimanche_pm10(String dimanche_pm10) {
		this.dimanche_pm10 = dimanche_pm10;
	}

	public String getLundi_apres_ecole10() {
		return lundi_apres_ecole10;
	}

	public void setLundi_apres_ecole10(String lundi_apres_ecole10) {
		this.lundi_apres_ecole10 = lundi_apres_ecole10;
	}

	public String getMardi_apres_ecole10() {
		return mardi_apres_ecole10;
	}

	public void setMardi_apres_ecole10(String mardi_apres_ecole10) {
		this.mardi_apres_ecole10 = mardi_apres_ecole10;
	}

	public String getMercredi_apres_ecole10() {
		return mercredi_apres_ecole10;
	}

	public void setMercredi_apres_ecole10(String mercredi_apres_ecole10) {
		this.mercredi_apres_ecole10 = mercredi_apres_ecole10;
	}

	public String getJeudi_apres_ecole10() {
		return jeudi_apres_ecole10;
	}

	public void setJeudi_apres_ecole10(String jeudi_apres_ecole10) {
		this.jeudi_apres_ecole10 = jeudi_apres_ecole10;
	}

	public String getVendredi_apres_ecole10() {
		return vendredi_apres_ecole10;
	}

	public void setVendredi_apres_ecole10(String vendredi_apres_ecole10) {
		this.vendredi_apres_ecole10 = vendredi_apres_ecole10;
	}

	public String getSamedi_apres_ecole10() {
		return samedi_apres_ecole10;
	}

	public void setSamedi_apres_ecole10(String samedi_apres_ecole10) {
		this.samedi_apres_ecole10 = samedi_apres_ecole10;
	}

	public String getDimanche_apres_ecole10() {
		return dimanche_apres_ecole10;
	}

	public void setDimanche_apres_ecole10(String dimanche_apres_ecole10) {
		this.dimanche_apres_ecole10 = dimanche_apres_ecole10;
	}

	public String getLundi_nuit10() {
		return lundi_nuit10;
	}

	public void setLundi_nuit10(String lundi_nuit10) {
		this.lundi_nuit10 = lundi_nuit10;
	}

	public String getMardi_nuit10() {
		return mardi_nuit10;
	}

	public void setMardi_nuit10(String mardi_nuit10) {
		this.mardi_nuit10 = mardi_nuit10;
	}

	public String getMercredi_nuit10() {
		return mercredi_nuit10;
	}

	public void setMercredi_nuit10(String mercredi_nuit10) {
		this.mercredi_nuit10 = mercredi_nuit10;
	}

	public String getJeudi_nuit10() {
		return jeudi_nuit10;
	}

	public void setJeudi_nuit10(String jeudi_nuit10) {
		this.jeudi_nuit10 = jeudi_nuit10;
	}

	public String getVendredi_nuit10() {
		return vendredi_nuit10;
	}

	public void setVendredi_nuit10(String vendredi_nuit10) {
		this.vendredi_nuit10 = vendredi_nuit10;
	}

	public String getSamedi_nuit10() {
		return samedi_nuit10;
	}

	public void setSamedi_nuit10(String samedi_nuit10) {
		this.samedi_nuit10 = samedi_nuit10;
	}

	public String getDimanche_nuit10() {
		return dimanche_nuit10;
	}

	public void setDimanche_nuit10(String dimanche_nuit10) {
		this.dimanche_nuit10 = dimanche_nuit10;
	}

	public String getDispoDepannagePlaceDisponible10() {
		return dispoDepannagePlaceDisponible10;
	}

	public void setDispoDepannagePlaceDisponible10(String dispoDepannagePlaceDisponible10) {
		this.dispoDepannagePlaceDisponible10 = dispoDepannagePlaceDisponible10;
	}

	public String getPrecisionsDepannagePlaceDisponible10() {
		return precisionsDepannagePlaceDisponible10;
	}

	public void setPrecisionsDepannagePlaceDisponible10(String precisionsDepannagePlaceDisponible10) {
		this.precisionsDepannagePlaceDisponible10 = precisionsDepannagePlaceDisponible10;
	}

	public void setIdDispo1(String idDispo1) {
		this.idDispo1 = idDispo1;
	}

	public void setIdDispo2(String idDispo2) {
		this.idDispo2 = idDispo2;
	}

	public void setIdDispo3(String idDispo3) {
		this.idDispo3 = idDispo3;
	}

	public void setIdDispo4(String idDispo4) {
		this.idDispo4 = idDispo4;
	}

	public void setIdDispo5(String idDispo5) {
		this.idDispo5 = idDispo5;
	}

	public void setIdDispo6(String idDispo6) {
		this.idDispo6 = idDispo6;
	}

	public void setIdDispo7(String idDispo7) {
		this.idDispo7 = idDispo7;
	}

	public void setIdDispo8(String idDispo8) {
		this.idDispo8 = idDispo8;
	}

	public void setIdDispo9(String idDispo9) {
		this.idDispo9 = idDispo9;
	}

	public void setIdDispo10(String idDispo10) {
		this.idDispo10 = idDispo10;
	}
 public void setTelephonePortable(String telephonePortable) {
   this.telephonePortable =  telephonePortable;
  }
 public void setFormStep(int formStep) {
   this.formStep = formStep;
 }

 public String getTelephonePortable() {
		return telephonePortable;
}
	private void performDisponibilite(String idDispo, String etatDispo, String precisionPlaceFuture, String precisionsPlaceDisponible, String moisDispo, String anneeDispo, String lundi_avant_ecole, String mardi_avant_ecole, String mercredi_avant_ecole, String jeudi_avant_ecole, String vendredi_avant_ecole, String samedi_avant_ecole, String dimanche_avant_ecole, String lundi_matin, String mardi_matin, String mercredi_matin, String jeudi_matin, String vendredi_matin, String samedi_matin,
			String dimanche_matin, String lundi_midi, String mardi_midi, String mercredi_midi, String jeudi_midi, String vendredi_midi, String samedi_midi, String dimanche_midi, String lundi_pm, String mardi_pm, String mercredi_pm, String jeudi_pm, String vendredi_pm, String samedi_pm, String dimanche_pm, String lundi_apres_ecole, String mardi_apres_ecole, String mercredi_apres_ecole, String jeudi_apres_ecole, String vendredi_apres_ecole, String samedi_apres_ecole, String dimanche_apres_ecole,
			String lundi_nuit, String mardi_nuit, String mercredi_nuit, String jeudi_nuit, String vendredi_nuit, String samedi_nuit, String dimanche_nuit, String dispoDepannagePlaceDisponible, String precisionsDepannagePlaceDisponible) {

		if (Util.notEmpty(idDispo)) {
			Disponibilite dispo = (Disponibilite) channel.getPublication(idDispo);
			if (Util.notEmpty(dispo)) {

				logger.debug("Mise à jour de la disponibilité < " + dispo.getId() + "> : " + dispo.getTitle());

				Disponibilite dispoClone = (Disponibilite) dispo.getUpdateInstance();

				// 0011760: Modification de date de dispo future non prise ne compte dans la recherche 
				// Permet de contourner le problème de dataController qui ne s’exécute pas lorsque seule la date de dispo futur est modifiée.
				dispoClone.setDateModification(new Date().getTime()+ "");
				
				dispoClone.setEtatDispo(etatDispo);
				dispoClone.setPrecisionPlaceFuture(precisionPlaceFuture);
				dispoClone.setPrecisionsPlaceDisponible(precisionsPlaceDisponible);

				if (Util.notEmpty(moisDispo) && Util.notEmpty(anneeDispo)) {
					Calendar calendar = Calendar.getInstance();
					calendar.clear();
					calendar.set(Calendar.MONTH, Integer.parseInt(moisDispo));
					calendar.set(Calendar.YEAR, Integer.parseInt(anneeDispo));
					java.util.Date dateFuture = calendar.getTime();
					dispoClone.setDateDispoPlaceFuture(dateFuture);
				}

				Semainier s = new Semainier();
				s.setLundi_avant_ecole(Boolean.parseBoolean(lundi_avant_ecole));
				s.setMardi_avant_ecole(Boolean.parseBoolean(mardi_avant_ecole));
				s.setMercredi_avant_ecole(Boolean.parseBoolean(mercredi_avant_ecole));
				s.setJeudi_avant_ecole(Boolean.parseBoolean(jeudi_avant_ecole));
				s.setVendredi_avant_ecole(Boolean.parseBoolean(vendredi_avant_ecole));
				s.setSamedi_avant_ecole(Boolean.parseBoolean(samedi_avant_ecole));
				s.setDimanche_avant_ecole(Boolean.parseBoolean(dimanche_avant_ecole));
				s.setLundi_matin(Boolean.parseBoolean(lundi_matin));
				s.setMardi_matin(Boolean.parseBoolean(mardi_matin));
				s.setMercredi_matin(Boolean.parseBoolean(mercredi_matin));
				s.setJeudi_matin(Boolean.parseBoolean(jeudi_matin));
				s.setVendredi_matin(Boolean.parseBoolean(vendredi_matin));
				s.setSamedi_matin(Boolean.parseBoolean(samedi_matin));
				s.setDimanche_matin(Boolean.parseBoolean(dimanche_matin));
				s.setLundi_midi(Boolean.parseBoolean(lundi_midi));
				s.setMardi_midi(Boolean.parseBoolean(mardi_midi));
				s.setMercredi_midi(Boolean.parseBoolean(mercredi_midi));
				s.setJeudi_midi(Boolean.parseBoolean(jeudi_midi));
				s.setVendredi_midi(Boolean.parseBoolean(vendredi_midi));
				s.setSamedi_midi(Boolean.parseBoolean(samedi_midi));
				s.setDimanche_midi(Boolean.parseBoolean(dimanche_midi));
				s.setLundi_pm(Boolean.parseBoolean(lundi_pm));
				s.setMardi_pm(Boolean.parseBoolean(mardi_pm));
				s.setMercredi_pm(Boolean.parseBoolean(mercredi_pm));
				s.setJeudi_pm(Boolean.parseBoolean(jeudi_pm));
				s.setVendredi_pm(Boolean.parseBoolean(vendredi_pm));
				s.setSamedi_pm(Boolean.parseBoolean(samedi_pm));
				s.setDimanche_pm(Boolean.parseBoolean(dimanche_pm));
				s.setLundi_apres_ecole(Boolean.parseBoolean(lundi_apres_ecole));
				s.setMardi_apres_ecole(Boolean.parseBoolean(mardi_apres_ecole));
				s.setMercredi_apres_ecole(Boolean.parseBoolean(mercredi_apres_ecole));
				s.setJeudi_apres_ecole(Boolean.parseBoolean(jeudi_apres_ecole));
				s.setVendredi_apres_ecole(Boolean.parseBoolean(vendredi_apres_ecole));
				s.setSamedi_apres_ecole(Boolean.parseBoolean(samedi_apres_ecole));
				s.setDimanche_apres_ecole(Boolean.parseBoolean(dimanche_apres_ecole));
				s.setLundi_nuit(Boolean.parseBoolean(lundi_nuit));
				s.setMardi_nuit(Boolean.parseBoolean(mardi_nuit));
				s.setMercredi_nuit(Boolean.parseBoolean(mercredi_nuit));
				s.setJeudi_nuit(Boolean.parseBoolean(jeudi_nuit));
				s.setVendredi_nuit(Boolean.parseBoolean(vendredi_nuit));
				s.setSamedi_nuit(Boolean.parseBoolean(samedi_nuit));
				s.setDimanche_nuit(Boolean.parseBoolean(dimanche_nuit));

				dispoClone.setSemainierPlaceDisponible(SemainierUtil.encoded(s));
				dispoClone.setDispoDepannagePlaceDisponible(Boolean.parseBoolean(dispoDepannagePlaceDisponible));
				dispoClone.setPrecisionsDepannagePlaceDisponible(precisionsDepannagePlaceDisponible);

				dispoClone.performUpdate(channel.getCurrentLoggedMember());

			}
		}
	}

	public void performDispos() {
		performDisponibilite(idDispo1, etatDispo1, precisionPlaceFuture1, precisionsPlaceDisponible1, moisDispo1, anneeDispo1, lundi_avant_ecole1, mardi_avant_ecole1, mercredi_avant_ecole1, jeudi_avant_ecole1, vendredi_avant_ecole1, samedi_avant_ecole1, dimanche_avant_ecole1, lundi_matin1, mardi_matin1, mercredi_matin1, jeudi_matin1, vendredi_matin1, samedi_matin1, dimanche_matin1, lundi_midi1, mardi_midi1, mercredi_midi1, jeudi_midi1, vendredi_midi1, samedi_midi1, dimanche_midi1, lundi_pm1,
				mardi_pm1, mercredi_pm1, jeudi_pm1, vendredi_pm1, samedi_pm1, dimanche_pm1, lundi_apres_ecole1, mardi_apres_ecole1, mercredi_apres_ecole1, jeudi_apres_ecole1, vendredi_apres_ecole1, samedi_apres_ecole1, dimanche_apres_ecole1, lundi_nuit1, mardi_nuit1, mercredi_nuit1, jeudi_nuit1, vendredi_nuit1, samedi_nuit1, dimanche_nuit1, dispoDepannagePlaceDisponible1, precisionsDepannagePlaceDisponible1);
		performDisponibilite(idDispo2, etatDispo2, precisionPlaceFuture2, precisionsPlaceDisponible2, moisDispo2, anneeDispo2, lundi_avant_ecole2, mardi_avant_ecole2, mercredi_avant_ecole2, jeudi_avant_ecole2, vendredi_avant_ecole2, samedi_avant_ecole2, dimanche_avant_ecole2, lundi_matin2, mardi_matin2, mercredi_matin2, jeudi_matin2, vendredi_matin2, samedi_matin2, dimanche_matin2, lundi_midi2, mardi_midi2, mercredi_midi2, jeudi_midi2, vendredi_midi2, samedi_midi2, dimanche_midi2, lundi_pm2,
				mardi_pm2, mercredi_pm2, jeudi_pm2, vendredi_pm2, samedi_pm2, dimanche_pm2, lundi_apres_ecole2, mardi_apres_ecole2, mercredi_apres_ecole2, jeudi_apres_ecole2, vendredi_apres_ecole2, samedi_apres_ecole2, dimanche_apres_ecole2, lundi_nuit2, mardi_nuit2, mercredi_nuit2, jeudi_nuit2, vendredi_nuit2, samedi_nuit2, dimanche_nuit2, dispoDepannagePlaceDisponible2, precisionsDepannagePlaceDisponible2);
		performDisponibilite(idDispo3, etatDispo3, precisionPlaceFuture3, precisionsPlaceDisponible3, moisDispo3, anneeDispo3, lundi_avant_ecole3, mardi_avant_ecole3, mercredi_avant_ecole3, jeudi_avant_ecole3, vendredi_avant_ecole3, samedi_avant_ecole3, dimanche_avant_ecole3, lundi_matin3, mardi_matin3, mercredi_matin3, jeudi_matin3, vendredi_matin3, samedi_matin3, dimanche_matin3, lundi_midi3, mardi_midi3, mercredi_midi3, jeudi_midi3, vendredi_midi3, samedi_midi3, dimanche_midi3, lundi_pm3,
				mardi_pm3, mercredi_pm3, jeudi_pm3, vendredi_pm3, samedi_pm3, dimanche_pm3, lundi_apres_ecole3, mardi_apres_ecole3, mercredi_apres_ecole3, jeudi_apres_ecole3, vendredi_apres_ecole3, samedi_apres_ecole3, dimanche_apres_ecole3, lundi_nuit3, mardi_nuit3, mercredi_nuit3, jeudi_nuit3, vendredi_nuit3, samedi_nuit3, dimanche_nuit3, dispoDepannagePlaceDisponible3, precisionsDepannagePlaceDisponible3);
		performDisponibilite(idDispo4, etatDispo4, precisionPlaceFuture4, precisionsPlaceDisponible4, moisDispo4, anneeDispo4, lundi_avant_ecole4, mardi_avant_ecole4, mercredi_avant_ecole4, jeudi_avant_ecole4, vendredi_avant_ecole4, samedi_avant_ecole4, dimanche_avant_ecole4, lundi_matin4, mardi_matin4, mercredi_matin4, jeudi_matin4, vendredi_matin4, samedi_matin4, dimanche_matin4, lundi_midi4, mardi_midi4, mercredi_midi4, jeudi_midi4, vendredi_midi4, samedi_midi4, dimanche_midi4, lundi_pm4,
				mardi_pm4, mercredi_pm4, jeudi_pm4, vendredi_pm4, samedi_pm4, dimanche_pm4, lundi_apres_ecole4, mardi_apres_ecole4, mercredi_apres_ecole4, jeudi_apres_ecole4, vendredi_apres_ecole4, samedi_apres_ecole4, dimanche_apres_ecole4, lundi_nuit4, mardi_nuit4, mercredi_nuit4, jeudi_nuit4, vendredi_nuit4, samedi_nuit4, dimanche_nuit4, dispoDepannagePlaceDisponible4, precisionsDepannagePlaceDisponible4);
		performDisponibilite(idDispo5, etatDispo5, precisionPlaceFuture5, precisionsPlaceDisponible5, moisDispo5, anneeDispo5, lundi_avant_ecole5, mardi_avant_ecole5, mercredi_avant_ecole5, jeudi_avant_ecole5, vendredi_avant_ecole5, samedi_avant_ecole5, dimanche_avant_ecole5, lundi_matin5, mardi_matin5, mercredi_matin5, jeudi_matin5, vendredi_matin5, samedi_matin5, dimanche_matin5, lundi_midi5, mardi_midi5, mercredi_midi5, jeudi_midi5, vendredi_midi5, samedi_midi5, dimanche_midi5, lundi_pm5,
				mardi_pm5, mercredi_pm5, jeudi_pm5, vendredi_pm5, samedi_pm5, dimanche_pm5, lundi_apres_ecole5, mardi_apres_ecole5, mercredi_apres_ecole5, jeudi_apres_ecole5, vendredi_apres_ecole5, samedi_apres_ecole5, dimanche_apres_ecole5, lundi_nuit5, mardi_nuit5, mercredi_nuit5, jeudi_nuit5, vendredi_nuit5, samedi_nuit5, dimanche_nuit5, dispoDepannagePlaceDisponible5, precisionsDepannagePlaceDisponible5);
		performDisponibilite(idDispo6, etatDispo6, precisionPlaceFuture6, precisionsPlaceDisponible6, moisDispo6, anneeDispo6, lundi_avant_ecole6, mardi_avant_ecole6, mercredi_avant_ecole6, jeudi_avant_ecole6, vendredi_avant_ecole6, samedi_avant_ecole6, dimanche_avant_ecole6, lundi_matin6, mardi_matin6, mercredi_matin6, jeudi_matin6, vendredi_matin6, samedi_matin6, dimanche_matin6, lundi_midi6, mardi_midi6, mercredi_midi6, jeudi_midi6, vendredi_midi6, samedi_midi6, dimanche_midi6, lundi_pm6,
				mardi_pm6, mercredi_pm6, jeudi_pm6, vendredi_pm6, samedi_pm6, dimanche_pm6, lundi_apres_ecole6, mardi_apres_ecole6, mercredi_apres_ecole6, jeudi_apres_ecole6, vendredi_apres_ecole6, samedi_apres_ecole6, dimanche_apres_ecole6, lundi_nuit6, mardi_nuit6, mercredi_nuit6, jeudi_nuit6, vendredi_nuit6, samedi_nuit6, dimanche_nuit6, dispoDepannagePlaceDisponible6, precisionsDepannagePlaceDisponible6);
		performDisponibilite(idDispo7, etatDispo7, precisionPlaceFuture7, precisionsPlaceDisponible7, moisDispo7, anneeDispo7, lundi_avant_ecole7, mardi_avant_ecole7, mercredi_avant_ecole7, jeudi_avant_ecole7, vendredi_avant_ecole7, samedi_avant_ecole7, dimanche_avant_ecole7, lundi_matin7, mardi_matin7, mercredi_matin7, jeudi_matin7, vendredi_matin7, samedi_matin7, dimanche_matin7, lundi_midi7, mardi_midi7, mercredi_midi7, jeudi_midi7, vendredi_midi7, samedi_midi7, dimanche_midi7, lundi_pm7,
				mardi_pm7, mercredi_pm7, jeudi_pm7, vendredi_pm7, samedi_pm7, dimanche_pm7, lundi_apres_ecole7, mardi_apres_ecole7, mercredi_apres_ecole7, jeudi_apres_ecole7, vendredi_apres_ecole7, samedi_apres_ecole7, dimanche_apres_ecole7, lundi_nuit7, mardi_nuit7, mercredi_nuit7, jeudi_nuit7, vendredi_nuit7, samedi_nuit7, dimanche_nuit7, dispoDepannagePlaceDisponible7, precisionsDepannagePlaceDisponible7);
		performDisponibilite(idDispo8, etatDispo8, precisionPlaceFuture8, precisionsPlaceDisponible8, moisDispo8, anneeDispo8, lundi_avant_ecole8, mardi_avant_ecole8, mercredi_avant_ecole8, jeudi_avant_ecole8, vendredi_avant_ecole8, samedi_avant_ecole8, dimanche_avant_ecole8, lundi_matin8, mardi_matin8, mercredi_matin8, jeudi_matin8, vendredi_matin8, samedi_matin8, dimanche_matin8, lundi_midi8, mardi_midi8, mercredi_midi8, jeudi_midi8, vendredi_midi8, samedi_midi8, dimanche_midi8, lundi_pm8,
				mardi_pm8, mercredi_pm8, jeudi_pm8, vendredi_pm8, samedi_pm8, dimanche_pm8, lundi_apres_ecole8, mardi_apres_ecole8, mercredi_apres_ecole8, jeudi_apres_ecole8, vendredi_apres_ecole8, samedi_apres_ecole8, dimanche_apres_ecole8, lundi_nuit8, mardi_nuit8, mercredi_nuit8, jeudi_nuit8, vendredi_nuit8, samedi_nuit8, dimanche_nuit8, dispoDepannagePlaceDisponible8, precisionsDepannagePlaceDisponible8);
		performDisponibilite(idDispo9, etatDispo9, precisionPlaceFuture9, precisionsPlaceDisponible9, moisDispo9, anneeDispo9, lundi_avant_ecole9, mardi_avant_ecole9, mercredi_avant_ecole9, jeudi_avant_ecole9, vendredi_avant_ecole9, samedi_avant_ecole9, dimanche_avant_ecole9, lundi_matin9, mardi_matin9, mercredi_matin9, jeudi_matin9, vendredi_matin9, samedi_matin9, dimanche_matin9, lundi_midi9, mardi_midi9, mercredi_midi9, jeudi_midi9, vendredi_midi9, samedi_midi9, dimanche_midi9, lundi_pm9,
				mardi_pm9, mercredi_pm9, jeudi_pm9, vendredi_pm9, samedi_pm9, dimanche_pm9, lundi_apres_ecole9, mardi_apres_ecole9, mercredi_apres_ecole9, jeudi_apres_ecole9, vendredi_apres_ecole9, samedi_apres_ecole9, dimanche_apres_ecole9, lundi_nuit9, mardi_nuit9, mercredi_nuit9, jeudi_nuit9, vendredi_nuit9, samedi_nuit9, dimanche_nuit9, dispoDepannagePlaceDisponible9, precisionsDepannagePlaceDisponible9);
		performDisponibilite(idDispo10, etatDispo10, precisionPlaceFuture10, precisionsPlaceDisponible10, moisDispo10, anneeDispo10, lundi_avant_ecole10, mardi_avant_ecole10, mercredi_avant_ecole10, jeudi_avant_ecole10, vendredi_avant_ecole10, samedi_avant_ecole10, dimanche_avant_ecole10, lundi_matin10, mardi_matin10, mercredi_matin10, jeudi_matin10, vendredi_matin10, samedi_matin10, dimanche_matin10, lundi_midi10, mardi_midi10, mercredi_midi10, jeudi_midi10, vendredi_midi10, samedi_midi10,
				dimanche_midi10, lundi_pm10, mardi_pm10, mercredi_pm10, jeudi_pm10, vendredi_pm10, samedi_pm10, dimanche_pm10, lundi_apres_ecole10, mardi_apres_ecole10, mercredi_apres_ecole10, jeudi_apres_ecole10, vendredi_apres_ecole10, samedi_apres_ecole10, dimanche_apres_ecole10, lundi_nuit10, mardi_nuit10, mercredi_nuit10, jeudi_nuit10, vendredi_nuit10, samedi_nuit10, dimanche_nuit10, dispoDepannagePlaceDisponible10, precisionsDepannagePlaceDisponible10);
	}
	
	// Surcharges car les getters générés empêchent de récupérer les données du formulaire
	@Override
  public String getAvailableVisbiliteTelephoneFixe() {
    if (Util.notEmpty(this.visbiliteTelephoneFixe) && !"none".equals(this.visbiliteTelephoneFixe)) {
      return this.visbiliteTelephoneFixe;
    } else {
      return super.getAvailableVisbiliteTelephoneFixe();
    }
  }
	
	@Override
  public String getAvailableVisibiliteTelephonePortable() {
    if (Util.notEmpty(this.visibiliteTelephonePortable) && !"none".equals(this.visibiliteTelephonePortable)) {
      return this.visibiliteTelephonePortable;
    } else {
      return super.getAvailableVisibiliteTelephonePortable();
    }
  }
	
	@Override
  public String getAvailableVisibiliteAdresseEmail() {
    if (Util.notEmpty(this.visibiliteAdresseEmail) && !"none".equals(this.visibiliteAdresseEmail)) {
      return this.visibiliteAdresseEmail;
    } else {
      return super.getAvailableVisibiliteAdresseEmail();
    }
  }
	
	@Override
  public String getAvailableAccueilTempsPartiel() {
    if (Util.notEmpty(this.accueilTempsPartiel) && !"none".equals(this.accueilTempsPartiel)) {
      return this.accueilTempsPartiel;
    } else {
      return super.getAvailableAccueilTempsPartiel();
    }
  }
	
	@Override
  public String getAvailableAccueilPeriscolaire() {
    if (Util.notEmpty(this.accueilPeriscolaire) && !"none".equals(this.accueilPeriscolaire)) {
      return this.accueilPeriscolaire;
    } else {
      return super.getAvailableAccueilPeriscolaire();
    }
  }
	
	@Override
  public String getAvailableAccueilMercredi() {
    if (Util.notEmpty(this.accueilMercredi) && !"none".equals(this.accueilMercredi)) {
      return this.accueilMercredi;
    } else {
      return super.getAvailableAccueilMercredi();
    }
  }
	
	@Override
  public String getAvailableAccueilPendantLesVacancesSco() {
    if (Util.notEmpty(this.accueilPendantLesVacancesSco) && !"none".equals(this.accueilPendantLesVacancesSco)) {
      return this.accueilPendantLesVacancesSco;
    } else {
      return super.getAvailableAccueilPendantLesVacancesSco();
    }
  }
	
	@Override
  public String getAvailableHorairesAtypiques() {
    if (Util.notEmpty(this.horairesAtypiques) && !"none".equals(this.horairesAtypiques)) {
      return this.horairesAtypiques;
    } else {
      return super.getAvailableHorairesAtypiques();
    }
  }
	
	@Override
  public String getAvailableAvant7h() {
    if (Util.notEmpty(this.avant7h) && !"none".equals(this.avant7h)) {
      return this.avant7h;
    } else {
      return super.getAvailableAvant7h();
    }
  }
	
	@Override
  public String getAvailableApres20h() {
    if (Util.notEmpty(this.apres20h) && !"none".equals(this.apres20h)) {
      return this.apres20h;
    } else {
      return super.getAvailableApres20h();
    }
  }
	
	@Override
  public String getAvailableLeSamedi() {
    if (Util.notEmpty(this.leSamedi) && !"none".equals(this.leSamedi)) {
      return this.leSamedi;
    } else {
      return super.getAvailableLeSamedi();
    }
  }
	
	@Override
  public String getAvailableLeDimanche() {
    if (Util.notEmpty(this.leDimanche) && !"none".equals(this.leDimanche)) {
      return this.leDimanche;
    } else {
      return super.getAvailableLeDimanche();
    }
  }
	
	@Override
  public String getAvailableLaNuit() {
    if (Util.notEmpty(this.laNuit) && !"none".equals(this.laNuit)) {
      return this.laNuit;
    } else {
      return super.getAvailableLaNuit();
    }
  }
	
	@Override
  public String getAvailableAccepteDepannage() {
    if (Util.notEmpty(this.accepteDepannage) && !"none".equals(this.accepteDepannage)) {
      return this.accepteDepannage;
    } else {
      return super.getAvailableAccepteDepannage();
    }
  }
	
	@Override
  public String getAvailableAccueilEnfantHandicap() {
    if (Util.notEmpty(this.accueilEnfantHandicap) && !"none".equals(this.accueilEnfantHandicap)) {
      return this.accueilEnfantHandicap;
    } else {
      return super.getAvailableAccueilEnfantHandicap();
    }
  }
	
	@Override
  public String getAvailableLogementAccessible() {
    if (Util.notEmpty(this.logementAccessible) && !"none".equals(this.logementAccessible)) {
      return this.logementAccessible;
    } else {
      return super.getAvailableLogementAccessible();
    }
  }
}