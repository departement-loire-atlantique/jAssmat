package fr.cg44.plugin.assmat.handler;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jalios.jcms.Category;
import com.jalios.jcms.Data;
import com.jalios.jcms.JcmsConstants;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.WorkflowConstants;
import com.jalios.jcms.context.JcmsContext;
import com.jalios.jcms.context.JcmsMessage;
import com.jalios.jcms.handler.EditDataHandler;
import com.jalios.util.Util;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.AssmatUtil.SelectionPreferenceReception;
import fr.cg44.plugin.assmat.TokenUtil;
import fr.cg44.plugin.assmat.beans.TrsbActivation;
import fr.cg44.plugin.assmat.hibernate.HibernateCD44Util;
import fr.cg44.plugin.assmat.managers.ActivationDAO;
import fr.cg44.plugin.assmat.managers.ProfilManager;
import fr.cg44.plugin.assmat.managers.SmsDAO;
import fr.cg44.plugin.assmat.selector.PlaceSelector;
import fr.trsb.cd44.solis.beans.AssmatSolis;
import fr.trsb.cd44.solis.manager.SolisManager;
import generated.InscriptionAM;
import generated.Place;
import generated.ProfilASSMAT;
import generated.Routage;

public class InscriptionAssmatHandler extends EditDataHandler {

  private static final Logger logger = Logger.getLogger(InscriptionAssmatHandler.class);

  private static ActivationDAO activationMgr = new ActivationDAO();

  public static final int IDENTIFICATION_STEP = 0;

  public static final int VERIFICATION_STEP = 1;

  public static final int CONTACT_STEP = 2;

  public static final int LOGIN_STEP = 3;

  public static final int CONFIRMATION_STEP = 4;

  public static final int NB_TENTATIVE_MAX = 2;

  public static final int NB_TENTATIVE_MAX_SMS = 3;

  private int nbTentativeErrone = 0;

  private int nbTentativeErroneSMS = 0;

  private static int ACTIVATION_DURATION= 172800000;

  protected String codePostal;
  protected String telephone2Error;
  protected String mail2Error;
  
  protected String texteSupport;

  protected String texteSignalement;

  protected String civilite;

  protected String nom;

  protected String prenom;

  protected String dateNaissance;

  protected String email;

  protected String telephone;
  
  protected String telephoneFixe;

  protected boolean isNumeroDagrementValidated = true;
  
  public static final int NUMERO_AGREMENT_DEFAULT = -1;
  protected Integer numeroAgrementInteger = NUMERO_AGREMENT_DEFAULT;
  //protected String numeroAgrement = numeroAgrementInteger.toString();
  
  protected String commune;
  
  protected String communeMam;

  protected String typeenvoi;

  protected String idInscriptionAM;

  protected String choixLogin;

  protected String password;

  protected String passwordConfirm;

  protected long idSolis;

  protected String idUa;

  protected Double latMAM;
  
  protected Double longMAM;
  
  protected Double latAM;
  
  protected Double longAM;
  
  protected String datePremierAgrement;

  protected String token;

  protected int codeConfirmation;

 protected String[] numeroTelUA;



protected String nameUA;

  boolean inscriptionOK = false;

  protected AssmatSolis assmat;

  protected boolean opContact;


  protected static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

  protected static SolisManager solisMgr = SolisManager.getInstance();

  protected String dateDernierRenouvellement;
  protected String dateProchainRenouvellement;
  protected String dateDernierRenouvellementMAM;
  protected String dateProchainRenouvellementMAM;


  
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


    // Validation d'une inscription
    //On verifie la presence d'un token
    if(Util.notEmpty(token)){
     InscriptionAM inscription= TokenUtil.getInscriptionFromToken(token);

     //Token invalide ou expire
     if(Util.isEmpty(inscription)){
       String errorMsg = JcmsUtil.glp(channel.getCurrentUserLang(), "jcmsplugin.assmatplugin.inscription.error.token.invalide");
       JcmsContext.setWarningMsg(errorMsg, request);       
       return false;
     }else{
       // Si compte déjà créé renvoi sur la page d'accueil du site
       //On passe l'inscription en publiée, ce qui declenche un dataController pour la création des comptes.
       //inscription.setPstatus(WorkflowConstants.PUBLISHED_PSTATUS);       
       if(inscription.getPstatus() == WorkflowConstants.EXPIRED_PSTATUS) {
         sendRedirect(channel.getData(channel.getProperty("jcmsplugin.assmatplugin.general.portal.accueil")));
         return false;
       }else {
         inscription.updatePstatus(channel.getDefaultAdmin(), WorkflowConstants.PUBLISHED_PSTATUS, "");
       }
       setInscriptionOK(true);

       return true;
     }

    }

 
    
    // Si on est à la premiere etape on verifie les champs
    if (formStep == IDENTIFICATION_STEP) {
      // Si validation du formulaire
      if (opNext) {
        if (!validateFields()) {
          return false;
        } else {
          // On recupere l'assmat correspondante
          assmat = searchAssmat();

          // Si on ne retrouve pas d'assmat
          if (Util.isEmpty(assmat)) {
            // Premiere tentative erroné
            if (nbTentativeErrone == 1) {
              request.setAttribute("notFoundCompte", "true");
              //String errorMsg = AssmatUtil.getMessage("COMPTE-NOT-FOUND-1");
              //JcmsContext.setWarningMsg(errorMsg, request);
            }
            return false;
          } else {

            ProfilASSMAT profilDejaExistant = ProfilManager.getInstance().getProfilASSMAT(numeroAgrementInteger);

            //Si un compte n'as pas deja ete active
            if (Util.isEmpty(profilDejaExistant)) {
              if (Util.notEmpty(assmat.getEnActivite()) && assmat.getEnActivite() && assmat.getAutorisationActivation()) {

                InscriptionAM inscriptionAM = null;
                // On verifie si une inscription n'est
                // pas deja en cours
                if (Util.notEmpty(idInscriptionAM)) {
                  inscriptionAM = (InscriptionAM) channel.getPublication(idInscriptionAM);
                  setIdInscriptionAM(inscriptionAM.getId());    
                } else {
                  // On creer le type de formulaire
                  // inscription AM
                  inscriptionAM = new InscriptionAM();

                  Object[] parameters = new String[] {};
                  parameters = (String[]) Util.insertArray(parameters, 0, dateFormatter.format(new Date()));
                  parameters = (String[]) Util.insertArray(parameters, 1, SolisManager.clean(nom) + " " + SolisManager.clean(prenom));
                  parameters = (String[]) Util.insertArray(parameters, 2, numeroAgrementInteger.toString());

                  inscriptionAM.setTitle(glp("jcmsplugin.assmatplugin.title.inscriptionAM", parameters));

                  inscriptionAM.setTelephoneFixe(telephoneFixe);
                  inscriptionAM.setNumeroDagrement(numeroAgrementInteger);
                  inscriptionAM.setCommune(commune);
                  inscriptionAM.setCommuneMam(communeMam);
                  inscriptionAM.setAuthor(channel.getDefaultAdmin());
//                  inscriptionAM.setNom(SolisManager.clean(nom));
//                  inscriptionAM.setPrenom(SolisManager.clean(prenom));
                  inscriptionAM.setCivilite(civilite);
                  inscriptionAM.setPstatus(-100);
                  if(inscriptionAM.checkCreate(channel.getDefaultAdmin()).isOK()){
	                  inscriptionAM.performCreate(channel.getDefaultAdmin());
	                  setIdInscriptionAM(inscriptionAM.getId());
                  } else {
                	  JcmsContext.setWarningMsg("Erreur lors de la tentative de création de votre inscription", request);
                	  logger.warn(inscriptionAM.checkCreate(channel.getDefaultAdmin()).getMessage(userLang));
                  }
                }
                              
              } else {
                String statut = assmat.getStatut();
                statut = SolisManager.cleanStatut(statut);

                String errorMsg = AssmatUtil.getMessage("IDENT-ECHEC-" + statut + "-HTML");											
                // Si pas de message alors message par defaut
                if(("IDENT-ECHEC-" + statut + "-HTML").equals(errorMsg)) {
                  errorMsg = AssmatUtil.getMessage("IDENT-ECHEC-STATUT-INTROUVABLE-HTML");
                  logger.warn("Pas de message d'erreur spécifique pour le status: " + statut);
                }
                nbTentativeErrone = 2;
                request.setAttribute("ticket10451", "true");
                JcmsContext.setWarningMsg(errorMsg, request);
                return false;
              }
            } else {
              String errorMsg = AssmatUtil.getMessage("CONTROLE-COMPTE");							
              JcmsContext.setWarningMsg(errorMsg, request);
              return false;
            }
          }
        }
      }
      // Demande de contact suite a la 2 eme tentative
      if (opContact) {
          if (!validateFields()) {
              return false;
            } else {
	    	  boolean bool = contact2eTentativeIdentification();
	    	  if(!bool){
	    		  return bool;
	    	  }
            }
      }
    }
    // Si on est à la 2eme etape on verifie les champs
    if (formStep == VERIFICATION_STEP) {

      if (opNext) {
        if (!validateFields()) {
          return false;
        }else{
          idInscriptionAM = getIdInscriptionAM();
          if (Util.notEmpty(idInscriptionAM)) {
           InscriptionAM inscriptionUpdated = (InscriptionAM) (channel.getPublication(idInscriptionAM)).clone();

           if(Util.notEmpty(latAM) && Util.notEmpty(longAM)){
             inscriptionUpdated.setLatitudeAssmat(latAM.doubleValue());
             inscriptionUpdated.setLongitudeAssmat(longAM.doubleValue());
           }
           if(Util.notEmpty(latMAM) && Util.notEmpty(longMAM)){
            inscriptionUpdated.setLatitudeMAM(latMAM.doubleValue());
            inscriptionUpdated.setLongitudeMAM(longMAM.doubleValue());
           }
           
           inscriptionUpdated.performUpdate(channel.getDefaultAdmin());
           
          }
         
        }
      }
      // Signalement d'une erreur
      if (opContact) {

    	  signalementErreurInformationSolis();

      }

    }

    // Si on est à la 3eme etape on verifie les champs
		if (formStep == CONTACT_STEP) {
			if (opNext) {
				if (!validateFields()) {
					return false;
				} else {

					// Récuperation de l'inscriptionAM
					idInscriptionAM = getIdInscriptionAM();
					if (Util.notEmpty(idInscriptionAM)) {
						InscriptionAM isncriptionUpdated = (InscriptionAM) (channel.getPublication(idInscriptionAM)).clone();

						if (Util.notEmpty(isncriptionUpdated)) {
							// On set la preference d'envoi des données
							if ("mail".equalsIgnoreCase(typeenvoi)) {
								isncriptionUpdated.setPreferenceReceptionMessageDepart(SelectionPreferenceReception.MAIL.getValue());
							} else if ("telephone".equalsIgnoreCase(typeenvoi)) {
								isncriptionUpdated.setPreferenceReceptionMessageDepart(SelectionPreferenceReception.TELEPHONE.getValue());
							}

							// On set le mail
							if (Util.notEmpty(email)) {
								isncriptionUpdated.setAdresseMail(email);
							}

							// On set le tel
							if (Util.notEmpty(telephone)) {
								isncriptionUpdated.setTelephonePortable(telephone);
							}

							isncriptionUpdated.performUpdate(channel.getDefaultAdmin());
						}
					}
				}
			}
		}

    // Si on est à la 3eme etape on verifie les champs
		if (formStep == LOGIN_STEP) {
			if (opNext) {
				if (!validateFields()) {
					return false;
				} else {

					// Récuperation de l'inscriptionAM
					idInscriptionAM = getIdInscriptionAM();
					if (Util.notEmpty(idInscriptionAM)) {
						InscriptionAM inscriptionUpdated = (InscriptionAM) (channel.getPublication(idInscriptionAM)).clone();

						// Mise a jour de l'inscription ASSMAT
						if (Util.notEmpty(inscriptionUpdated)) {
							if ("tel".equals(choixLogin)) {
								inscriptionUpdated.setPreferenceDeLogin(AssmatUtil.SelectionLogin.TELEPHONE.getValue());
								inscriptionUpdated.setIdentifiant(telephone);
							}
							if ("email".equals(choixLogin)) {
								inscriptionUpdated.setPreferenceDeLogin(AssmatUtil.SelectionLogin.MAIL.getValue());
								inscriptionUpdated.setIdentifiant(email);
							}
							if ("dossier".equals(choixLogin)) {
								inscriptionUpdated.setPreferenceDeLogin(AssmatUtil.SelectionLogin.NUMERO_DOSSIER.getValue());
								inscriptionUpdated.setIdentifiant(numeroAgrementInteger.toString());
							}
							inscriptionUpdated.setMotDePasse(password);
							inscriptionUpdated.performUpdate(channel.getDefaultAdmin());
						}

						// Envoi du mail de confirmation
						if ("mail".equalsIgnoreCase(typeenvoi)) {

							// Création du token (2 Jour de validité)
							String tokenGenerate = TokenUtil.generatePasswordResetToken(idInscriptionAM, ACTIVATION_DURATION);

							// Preparation du mail
							String from = AssmatUtil.getDefaultEmail();
							String subject = AssmatUtil.getMessage("INSCRIPTION-MAIL-VALIDATION-SUBJECT");
							String lien = "<a href=\"" + channel.getUrl() + "espaceperso?token=" + tokenGenerate + "\">" + channel.getUrl() + "espaceperso?token=" + tokenGenerate + "</a>";

							String[] parameters = new String[] {};
							parameters = (String[]) Util.insertArray(parameters, 0, prenom);
							parameters = (String[]) Util.insertArray(parameters, 1, nom);
							parameters = (String[]) Util.insertArray(parameters, 2, lien);

							String htmlcontent = AssmatUtil.getMessagePropertiesParametersValues("INSCRIPTION-MAIL-VALIDATION-MESSAGE", parameters);
							/*
							 * String htmlcontent = glp(
							 * "jcmsplugin.assmatplugin.activation.mail.lbl.bonjour"
							 * )+" "+prenom+" "+nom+"<br><br>"+glp(
							 * "jcmsplugin.assmatplugin.activation.mail.lbl.finaliser"
							 * ) +"<a href='"+lien+
							 * "' >http://assmatv2.loire-atlantique.fr/espaceperso</a><br> "
							 * +glp(
							 * "jcmsplugin.assmatplugin.activation.mail.lbl.dispo"
							 * )+"<br><br>" +glp(
							 * "jcmsplugin.assmatplugin.activation.mail.lbl.procedure"
							 * )+"<br><br> "+glp(
							 * "jcmsplugin.assmatplugin.activation.mail.lbl.nota"
							 * )+" "+lien+" "+glp(
							 * "jcmsplugin.assmatplugin.activation.mail.lbl.navigateur"
							 * )+"<br>"+ glp(
							 * "jcmsplugin.assmatplugin.activation.mail.lbl.bientot"
							 * );
							 */

							AssmatUtil.sendMail(email, subject, htmlcontent, from);
						}

						// Envoi du sms de confirmation
						if ("telephone".equalsIgnoreCase(typeenvoi)) {

							// Création du code de confirmation
							int lower = 10000000;
							int higher = 99999999;
							// Generation du code
							int codeConfirmationGenere = (int) (Math.random() * (higher - lower)) + lower;

							logger.info(codeConfirmationGenere);
							List<TrsbActivation> listActivation = activationMgr.getActivationByIdForm(idInscriptionAM);

							// Si il y a deja des activation en cours on les
							// supprimes
							if (Util.notEmpty(listActivation)) {
								for (TrsbActivation activation : listActivation) {
									HibernateCD44Util.delete(activation);
								}
							}

							// On créer la nouvelle
							TrsbActivation activation = new TrsbActivation();
							activation.setCodeConfirmation(codeConfirmationGenere);
							activation.setInscriptionId(idInscriptionAM);

							// Date d'expiration de l'activation
							long dateExpirationDuration = System.currentTimeMillis() + (ACTIVATION_DURATION > 0L ? ACTIVATION_DURATION : ACTIVATION_DURATION);
							Date date = new Date(dateExpirationDuration);
							activation.setDateExpiration(date);
							HibernateCD44Util.create(activation);

							// Envoi du sms contenant le code de confirmation
							SmsDAO sms = new SmsDAO();
							String login = AssmatUtil.getLoginFromInscriptionAM(inscriptionUpdated);
//							String name = isncriptionUpdated.getNom();
//							String firstname = isncriptionUpdated.getPrenom();
							String name = getNom();
							String firstname = getPrenom();
							String portable = inscriptionUpdated.getTelephonePortable();

							String idMessage = "INSCRIPTION-SMS-VALIDATION-CONTENT";

							String[] parameters = new String[] {};
							parameters = (String[]) Util.insertArray(parameters, 0, codeConfirmationGenere + "");
							parameters = (String[]) Util.insertArray(parameters, 1, channel.getUrl());
							parameters = (String[]) Util.insertArray(parameters, 2, channel.getProperty("jcmsplugin.assmatplugin.telephone-support"));

							String subject = idMessage;
							String content = AssmatUtil.getMessage(AssmatUtil.getMessagePropertiesParametersValues(idMessage, parameters));
							String resumeContent = AssmatUtil.getMessage(AssmatUtil.getMessagePropertiesParametersValues(idMessage, parameters));
							sms.sendSMS(name, firstname, portable, subject, content, resumeContent, login, null);
						}

					}
				}
			}
		}

    // Si on est à la 4eme etape on verifie les champs
    if (formStep == CONFIRMATION_STEP) {
      if (!validateFields()) {
        return false;
      }else{
        String infoMsg="";
        //On recuepre le code de confirmation
        codeConfirmation = getCodeConfirmation();
        if(Util.notEmpty(codeConfirmation) && Util.notEmpty(idInscriptionAM)){

          //On recupere l'activation correspondante a l'inscription
          List<TrsbActivation> listActivation= activationMgr.getActivationByIdForm(idInscriptionAM);
          if(Util.notEmpty(listActivation)){
            TrsbActivation activation = Util.getFirst(listActivation);
            //Si l'activaation n'est pas expiré
            if(System.currentTimeMillis()<activation.getDateExpiration().getTime()){

              //Si le code de confirmation est bon
              if(codeConfirmation == activation.getCodeConfirmation()){

                //On recupere l'inscriptionAm
                String idInscriptionAMFromInscription = activation.getInscriptionId();
                if(Util.notEmpty(idInscriptionAMFromInscription)){

                  InscriptionAM inscriptionAm= (InscriptionAM) channel.getPublication(idInscriptionAMFromInscription);

                  //On passe l'inscription en publiée, ce qui declenche un dataController pour la création des comptes.
                  inscriptionAm.updatePstatus(channel.getDefaultAdmin(), WorkflowConstants.PUBLISHED_PSTATUS, "");
                  setInscriptionOK(true);
                  return true;
                }
              }
              //Code de confiramtion non valide
              else{
                infoMsg = AssmatUtil.getMessage("CODE-VALIDATION-SMS-NON-VALIDE");
                JcmsContext.setWarningMsg(infoMsg, request);
                nbTentativeErroneSMS++;
                return false;
              }

            }
            //Code de confiramtion expiré
            else{
            	infoMsg = AssmatUtil.getMessage("CODE-VALIDATION-SMS-EXPIRE");
              JcmsContext.setWarningMsg(infoMsg, request);
              nbTentativeErroneSMS++;
              return false;

            }
          }else{
            infoMsg = AssmatUtil.getMessage("CODE-VALIDATION-SMS-NOT-FORM");
            JcmsContext.setWarningMsg(infoMsg, request);
            nbTentativeErroneSMS++;
            return false;
          }
        }else{
        	infoMsg = AssmatUtil.getMessage("CODE-VALIDATION-SMS-NOT-FORM");
          JcmsContext.setWarningMsg(infoMsg, request);
          nbTentativeErroneSMS++;
          return false;
        }

      }
    }

    // Si on a pas etape suivante, ni précédente, ni finale
    if (!opNext && !opPrevious && !opFinish) {
      return false;
    }
    // Si on est à la dernière étape
    if (opFinish) {
      // performCreate();
      return true;
    }
    return super.processAction();

  }


public AssmatSolis getAssmat() {
  if(Util.isEmpty(assmat)){
    assmat = searchAssmat();
  }
    return assmat;
  }

  public void setAssmat(AssmatSolis assmat) {
    this.assmat = assmat;
  }

  /**
   * Vérifie la nullité des champs de la modale
   *
   * @return <code>true</code> si tous les champs sont remplies
   * @throws IOException
   */
  public boolean validateFields() throws IOException {
    boolean valide = true;

    List<JcmsMessage> listError= new ArrayList<JcmsMessage>();

    // On vérifie que un des champs de l'étape 1 n'est pas vide
    if (formStep == IDENTIFICATION_STEP) {
    	if(opNext){
	      civilite = getCivilite();
	      nom = getNom();
	      prenom = getPrenom();
	      dateNaissance = getDateDeNaissance();	      
	    
	      
	      if (Util.isEmpty(civilite)) {
	        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY",new String[]{"IDENT-CIV-HTML"})));
	        valide = false;
	      } else if (Util.isEmpty(nom)) {
	        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY",new String[]{"IDENT-NOM-HTML"})));
	        valide = false;
	      } else if (Util.isEmpty(prenom)) {
	        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY",new String[]{"IDENT-PRENOM-HTML"})));
	        valide = false;
	      }else if (!isNumeroDagrementValidated) {
	          String errorMsg = AssmatUtil.getMessage("CONTROLES-FORMAT-AGREMENT-NON-VALIDE");
	          JcmsContext.setWarningMsg(errorMsg, request);
	          return false;
	      }  else if (getNumeroAgrementInteger() == NUMERO_AGREMENT_DEFAULT) {
	        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY",new String[]{"IDENT-NUM-DOSSIER-HTML"})));
         valide = false;
	      }else if (Util.isEmpty(dateNaissance)) {
	        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY",new String[]{"IDENT-DATE-HTML"})));
	        valide = false;
	      } else if(Util.notEmpty(dateNaissance)){
	    	  if(!Pattern.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}", dateNaissance)){
	    	        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("INSCRIPTION-FORMATTAGE-DATE-ERROR")));
	    	        valide = false; 
	    	  }
	      }
    	} else if(opContact){   	      
    		if (Util.isEmpty(telephone2Error)) {
    			String errorMsg = AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY", new String[] { "IDENT-ECHEC2-TEL-HTML" });
    			JcmsContext.setWarningMsg(errorMsg, request);
    			return false;
    		}
    		if(Util.notEmpty(telephone2Error)){
    			if (!AssmatUtil.checkPhone(telephone2Error)) {
    				String errorMsg = AssmatUtil.getMessagePropertiesParameters("CONTACT-TEL-VALIDITE",new String[]{"CONTACTS-TEL-PORTABLE-HTML"});
        			JcmsContext.setWarningMsg(errorMsg, request);    				
    				return false;
    			}
    		}    		
    		    		
    		if(Util.notEmpty(mail2Error)){
    			if (!AssmatUtil.checkEmail(mail2Error)) {
        			String errorMsg = AssmatUtil.getMessagePropertiesParameters("CONTACT-MAIL-VALIDITE",new String[]{"CONTACTS-EMAIL-HTML"});
        			JcmsContext.setWarningMsg(errorMsg, request);    				
    				return false;
    			}
    		}
    		
    		if (Util.isEmpty(codePostal)) {
    			String errorMsg = AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY", new String[] { "IDENT-ECHEC2-CP-HTML" });
    			JcmsContext.setWarningMsg(errorMsg, request);
    			return false;
    		}
    		if (Util.notEmpty(codePostal)) {
    			String cp = codePostal;
    			cp = Util.replaceAll(cp, " ", "");
    			if(cp.length() != 5){
    				String errorMsg = AssmatUtil.getMessagePropertiesParameters("LENGTH-5", new String[] { "IDENT-ECHEC2-CP-HTML" });
    				JcmsContext.setWarningMsg(errorMsg, request);
    				return false;
    			}
    		}
    		if(Util.isEmpty(texteSupport)){
    			String errorMsg = AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY", new String[] { "IDENT-ECHEC2-TXT-HTML" });
    			JcmsContext.setWarningMsg(errorMsg, request);
    			return false;
    		}
    		if(Util.notEmpty(texteSupport) && texteSupport.length() < 10){
    			String errorMsg = AssmatUtil.getMessagePropertiesParameters("LENGTH-10", new String[] { "IDENT-ECHEC2-TXT-HTML" });
    			JcmsContext.setWarningMsg(errorMsg, request);
    			return false;
    		}
    	}
    }

    // On vérifie les champs de l'étape 3
    if (formStep == CONTACT_STEP) {

      telephone = getTelephone();
      email = getEmail();
      typeenvoi = getTypeenvoi();

      // Vérification de la concordance des champs du formualire
      if (Util.isEmpty(telephone) && Util.isEmpty(email)) {
        valide = false;
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY-DOUBLE",new String[]{"CONTACTS-EMAIL-HTML","CONTACTS-TEL-PORTABLE-HTML"})));

      } else if (Util.isEmpty(typeenvoi)) {
        valide = false;
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY",new String[]{"CONTACTS-PREF-HTML"})));

      } else if ("mail".equals(typeenvoi) && Util.isEmpty(email)) {
        valide = false;
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("CONTACTS-PREF-EMAIL-ALERTE",new String[]{"CONTACTS-EMAIL-HTML"})));

      } else if ("telephone".equals(typeenvoi) && Util.isEmpty(telephone)) {
        valide = false;
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("CONTACTS-PREF-SMS-ALERTE",new String[]{"CONTACTS-TEL-PORTABLE-HTML"})));

      }
      boolean correspond = false;
      // Verification du pattern de l'email
      if (valide && Util.notEmpty(email)) {
    	  correspond = AssmatUtil.checkEmail(email);
        if (!correspond) {
          valide = false;
          listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("CONTACT-MAIL-VALIDITE",new String[]{"CONTACTS-EMAIL-HTML"})));

        }
      }
      if (valide && Util.notEmpty(telephone)) {
        // Verification du pattern du telephone
        correspond = AssmatUtil.checkPhone(telephone);

        if (!correspond) {
          valide = false;
          listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("CONTACT-TEL-VALIDITE",new String[]{"CONTACTS-TEL-PORTABLE-HTML"})));
        }
      }

    }


    // On vérifie les champs de l'étape 4
    if (formStep == LOGIN_STEP) {

      choixLogin = getChoixLogin();
      password = getPassword();
      passwordConfirm = getPasswordConfirm();

      // Vérification de la concordance des champs du formualire
      if (Util.isEmpty(choixLogin)) {
        valide = false;
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("CHOICE-LOGIN-NOT-EMPTY")));
      } else if (Util.isEmpty(password)) {
        valide = false;
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY",new String[]{"LOGIN-MDP-HTML"})));
      } else if (Util.isEmpty(passwordConfirm)) {
        valide = false;
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY",new String[]{"LOGIN-MDP-CONF-HTML"})));
      } else if (!password.equalsIgnoreCase(passwordConfirm)) {
        valide = false;
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("PASSWORD-EQUALS")));
      } else if(!AssmatUtil.checkPassword(password)){
        valide = false;
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("PASSWORD-ERROR")));
      }
      
    
        if ("tel".equals(choixLogin)) {
         if(Util.notEmpty(channel.getMemberFromLogin(telephone))){
           valide = false;
           listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("MEMBER-SAME-LOGIN-ALREADY-EXIST")));
         }
        } 
        if ("email".equals(choixLogin)) {
          if(Util.notEmpty(channel.getMemberFromLogin(email))){
            valide = false;
            listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("MEMBER-SAME-LOGIN-ALREADY-EXIST")));
          }
        }
        if ("dossier".equals(choixLogin)) {
          if(Util.notEmpty(channel.getMemberFromLogin(numeroAgrementInteger.toString()))){
            valide = false;
            listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessage("MEMBER-SAME-LOGIN-ALREADY-EXIST")));
          }
        }
  
    }


 // On vérifie les champs de l'étape Confirmation
    if (formStep == CONFIRMATION_STEP) {

      codeConfirmation = getCodeConfirmation();

      // Vérification de la concordance des champs du formualire
      if (Util.isEmpty(codeConfirmation)) {
        valide = false;
        listError.add(new JcmsMessage(JcmsMessage.Level.WARN, AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY",new String[]{"CONFIRM-CODE-HTML"})));
      }
    }

    // On affiche l'erreur si il y en à une
    if (!valide) {
      if(!listError.isEmpty()){
        request.setAttribute(JcmsConstants.JCMS_MSG_LIST, listError);
       }
    }

    return valide;
  }

  /**
   * Méthode qui verife si une assmat existe en fonction du nom, prenom, date de
   * naissance et numero d'agrement
   *
   * @return L'assmat si trouvé, sinon null
   */
	public AssmatSolis searchAssmat() {

		List<AssmatSolis> solisList = null;

		if (Util.notEmpty(dateNaissance)) {
			Date dateAnniv = null;
			try {
				dateAnniv = dateFormatter.parse(dateNaissance);
			} catch (ParseException e) {
				logger.error("Date de naissance non valide", e);
			}
			// On verifie avec le nom et prenom et date de naissance
			if (Util.notEmpty(nom) && Util.notEmpty(prenom) && Util.notEmpty(dateNaissance) && numeroAgrementInteger != NUMERO_AGREMENT_DEFAULT) { // On verifie avec le nom et prenom et date de naissance et le numero d'agrement
				solisList = solisMgr.getAssmatSolisByNameFirstnameDateNaissanceNumAgrement(civilite, nom, prenom, dateAnniv, numeroAgrementInteger);
			}
			if (Util.notEmpty(solisList)) {
				assmat = Util.getFirst(solisList);
			} else {
				nbTentativeErrone++;
			}
		}

		if (Util.notEmpty(assmat)) {
			civilite = assmat.getCiviliteAssmat();
			nom = assmat.getNomAssmat();
			prenom = assmat.getPrenomAssmat();
			if (Util.notEmpty(assmat.getDateNaissAssmat())) {
				dateNaissance = dateFormatter.format(assmat.getDateNaissAssmat());
			}
			numeroAgrementInteger = assmat.getNumDossierAssmat();
//			numeroAgrement = numeroAgrementInteger.toString();
			if (Util.notEmpty(assmat.getDatePremierAgrement())) {
				datePremierAgrement = dateFormatter.format(assmat.getDatePremierAgrement());
			}

			if (Util.notEmpty(assmat.getDateDernierRenouvellement())) {
				dateDernierRenouvellement = dateFormatter.format(assmat.getDateDernierRenouvellement());
			}
			if (Util.notEmpty(assmat.getDateProchainRenouvellement())) {
				dateProchainRenouvellement = dateFormatter.format(assmat.getDateProchainRenouvellement());
			}	
			
			if (Util.notEmpty(assmat.getDateDernierRenouvellementMam())) {
				dateDernierRenouvellementMAM = dateFormatter.format(assmat.getDateDernierRenouvellementMam());
			}
			if (Util.notEmpty(assmat.getDateProchainRenouvellementMam())) {
				dateProchainRenouvellementMAM = dateFormatter.format(assmat.getDateProchainRenouvellementMam());
			}
			
			commune = assmat.getCommuneDomicile();
			communeMam = assmat.getCommuneMam();
			idSolis = assmat.getJRowId();
			idUa = assmat.getIdUa();
			
			// 0010191: Activation du compte - écran contacts - perte des données saisies
			if(Util.isEmpty(email)){
			  email = assmat.getEmailAssmat();
			}
			if(Util.isEmpty(telephone)){
			  telephone = assmat.getTelPortable();
			}
			if(Util.isEmpty(telephoneFixe)){
			  telephoneFixe = assmat.getTelPrincipal();
			}
			if (Util.notEmpty(idUa)) {
				Set<Place> setPlace = (Set<Place>) JcmsUtil.applyDataSelector(channel.getPublicationSet(Place.class, channel.getDefaultAdmin()), new PlaceSelector(idUa));
				if (Util.notEmpty(setPlace)) {
					// On recuepre le premier lieu
					Place place = Util.getFirst(setPlace);
					numeroTelUA = place.getPhones();
					nameUA = place.getTitle();
				}
			}
		}

		return assmat;
	}

  /**
   * Persiste les données dans des input type hidden pour permettre le retour
   * aux etapes precedentes
   *
   * @return le stringbuilder des inputs
   */
  public String getFormStepHiddenFields() {
    StringBuilder sb = new StringBuilder();

    sb.append(getHiddenField("formStep", formStep));
    sb.append(getHiddenField("inscriptionOK", inscriptionOK));
    if (formStep == IDENTIFICATION_STEP) {
      sb.append(getHiddenField("nbTentativeErrone", nbTentativeErrone));
      sb.append(getHiddenField("idInscriptionAM", idInscriptionAM));
      sb.append(getHiddenField("numeroAgrement", numeroAgrementInteger));
      sb.append(getHiddenField("commune", commune));
      sb.append(getHiddenField("communeMam", communeMam));

      if(nbTentativeErrone>=NB_TENTATIVE_MAX){	// Formulaire de 2e erreur d'identification
	      sb.append(getHiddenField("nom", nom));
	      sb.append(getHiddenField("prenom", prenom));
      }
    }
    if (formStep == VERIFICATION_STEP) {
      sb.append(getHiddenField("nom", nom));
      sb.append(getHiddenField("civilite", civilite));
      sb.append(getHiddenField("prenom", prenom));
      sb.append(getHiddenField("email", email));
      sb.append(getHiddenField("telephone", telephone));

    
      
      sb.append(getHiddenField("telephoneFixe", telephoneFixe));
      sb.append(getHiddenField("numeroAgrement", numeroAgrementInteger));
      sb.append(getHiddenField("commune", commune));
      sb.append(getHiddenField("communeMam", communeMam));
      sb.append(getHiddenField("nbTentativeErrone", nbTentativeErrone));
      sb.append(getHiddenField("dateDeNaissance", dateNaissance));
      sb.append(getHiddenField("datePremierAgrement", datePremierAgrement));
      
      if(Util.notEmpty(latAM) && Util.notEmpty(longAM) ){
        sb.append(getHiddenField("latAM", latAM));
        sb.append(getHiddenField("longAM", longAM));
       }
       if(Util.notEmpty(latMAM) && Util.notEmpty(longMAM) ){
        sb.append(getHiddenField("latMAM", latMAM));
        sb.append(getHiddenField("longMAM", longMAM));
       }
      
      sb.append(getHiddenField("dateProchainRenouvellement", dateProchainRenouvellement));
      sb.append(getHiddenField("dateProchainRenouvellementMAM", dateProchainRenouvellementMAM));
      sb.append(getHiddenField("dateDernierRenouvellement", dateDernierRenouvellement));
      sb.append(getHiddenField("dateDernierRenouvellementMAM", dateDernierRenouvellementMAM));      
      
      sb.append(getHiddenField("idSolis", idSolis));
      sb.append(getHiddenField("idUa", idUa));
      sb.append(getHiddenField("idInscriptionAM", idInscriptionAM));
      sb.append(getHiddenField("numeroTelUA", numeroTelUA));
      sb.append(getHiddenField("nameUA", nameUA));

    }
    if (formStep == CONTACT_STEP) {
      sb.append(getHiddenField("nom", nom));
      sb.append(getHiddenField("prenom", prenom));
      sb.append(getHiddenField("email", email));
      sb.append(getHiddenField("telephone", telephone));
      sb.append(getHiddenField("telephoneFixe", telephoneFixe));
      sb.append(getHiddenField("numeroAgrement", numeroAgrementInteger));
      sb.append(getHiddenField("commune", commune));
      sb.append(getHiddenField("communeMam", communeMam));
      sb.append(getHiddenField("nbTentativeErrone", nbTentativeErrone));
      sb.append(getHiddenField("dateDeNaissance", dateNaissance));
      sb.append(getHiddenField("civilite", civilite));
      sb.append(getHiddenField("typeenvoi", typeenvoi));
      sb.append(getHiddenField("datePremierAgrement", datePremierAgrement));
      
      if(Util.notEmpty(latAM) && Util.notEmpty(longAM) ){
       sb.append(getHiddenField("latAM", latAM));
       sb.append(getHiddenField("longAM", longAM));
      }
      if(Util.notEmpty(latMAM) && Util.notEmpty(longMAM) ){
       sb.append(getHiddenField("latMAM", latMAM));
       sb.append(getHiddenField("longMAM", longMAM));
      }
      sb.append(getHiddenField("dateProchainRenouvellement", dateProchainRenouvellement));
      sb.append(getHiddenField("dateProchainRenouvellementMAM", dateProchainRenouvellementMAM));
      sb.append(getHiddenField("dateDernierRenouvellement", dateDernierRenouvellement));
      sb.append(getHiddenField("dateDernierRenouvellementMAM", dateDernierRenouvellementMAM));      
      
      sb.append(getHiddenField("idSolis", idSolis));
      sb.append(getHiddenField("idUa", idUa));
      sb.append(getHiddenField("idInscriptionAM", idInscriptionAM));

    }
    if (formStep == LOGIN_STEP) {
      sb.append(getHiddenField("nom", nom));
      sb.append(getHiddenField("prenom", prenom));
      sb.append(getHiddenField("email", email));
      sb.append(getHiddenField("numeroAgrement", numeroAgrementInteger));
      sb.append(getHiddenField("commune", commune));
      sb.append(getHiddenField("communeMam", communeMam));
      sb.append(getHiddenField("telephone", telephone));
      sb.append(getHiddenField("telephoneFixe", telephoneFixe));
      sb.append(getHiddenField("nbTentativeErrone", nbTentativeErrone));
      sb.append(getHiddenField("dateDeNaissance", dateNaissance));
      sb.append(getHiddenField("civilite", civilite));
      sb.append(getHiddenField("typeenvoi", typeenvoi));
      sb.append(getHiddenField("idInscriptionAM", idInscriptionAM));
      sb.append(getHiddenField("datePremierAgrement", datePremierAgrement));
      

      if(Util.notEmpty(latAM) && Util.notEmpty(longAM) ){
        sb.append(getHiddenField("latAM", latAM));
        sb.append(getHiddenField("longAM", longAM));
       }
       if(Util.notEmpty(latMAM) && Util.notEmpty(longMAM) ){
        sb.append(getHiddenField("latMAM", latMAM));
        sb.append(getHiddenField("longMAM", longMAM));
       }
      sb.append(getHiddenField("dateProchainRenouvellement", dateProchainRenouvellement));
      sb.append(getHiddenField("dateProchainRenouvellementMAM", dateProchainRenouvellementMAM));
      sb.append(getHiddenField("dateDernierRenouvellement", dateDernierRenouvellement));
      sb.append(getHiddenField("dateDernierRenouvellementMAM", dateDernierRenouvellementMAM));      
      
      sb.append(getHiddenField("idSolis", idSolis));
      sb.append(getHiddenField("idUa", idUa));

    }
    if(formStep == CONFIRMATION_STEP){
      sb.append(getHiddenField("nom", nom));
      sb.append(getHiddenField("prenom", prenom));
      sb.append(getHiddenField("email", email));
      sb.append(getHiddenField("numeroAgrement", numeroAgrementInteger));
      sb.append(getHiddenField("commune", commune));
      sb.append(getHiddenField("communeMam", communeMam));
      sb.append(getHiddenField("telephone", telephone));
      sb.append(getHiddenField("telephoneFixe", telephoneFixe));
      sb.append(getHiddenField("nbTentativeErrone", nbTentativeErrone));
      sb.append(getHiddenField("dateDeNaissance", dateNaissance));
      sb.append(getHiddenField("civilite", civilite));
      sb.append(getHiddenField("typeenvoi", typeenvoi));
      sb.append(getHiddenField("idInscriptionAM", idInscriptionAM));
      sb.append(getHiddenField("datePremierAgrement", datePremierAgrement));
      if(Util.notEmpty(latAM) && Util.notEmpty(longAM) ){
        sb.append(getHiddenField("latAM", latAM));
        sb.append(getHiddenField("longAM", longAM));
       }
       if(Util.notEmpty(latMAM) && Util.notEmpty(longMAM) ){
        sb.append(getHiddenField("latMAM", latMAM));
        sb.append(getHiddenField("longMAM", longMAM));
       }

      
      sb.append(getHiddenField("dateProchainRenouvellement", dateProchainRenouvellement));
      sb.append(getHiddenField("dateProchainRenouvellementMAM", dateProchainRenouvellementMAM));
      sb.append(getHiddenField("dateDernierRenouvellement", dateDernierRenouvellement));
      sb.append(getHiddenField("dateDernierRenouvellementMAM", dateDernierRenouvellementMAM));      
      
      sb.append(getHiddenField("idSolis", idSolis));
      sb.append(getHiddenField("idUa", idUa));
      sb.append(getHiddenField("nbTentativeErroneSMS", nbTentativeErroneSMS));

    }

    return sb.toString();
  }

  /**
   * Persiste toutes les données sans ce soucier de l'étape courantes dans des
   * input type hidden pour permettre le retour aux etapes precedentes
   *
   * @return Le stringBuilder des inputs
   */
  public String getAllFormStepHiddenFields() {
    StringBuilder sb = new StringBuilder();

    sb.append(getHiddenField("formStep", formStep));
    if((formStep ==InscriptionAssmatHandler.IDENTIFICATION_STEP && nbTentativeErrone>=InscriptionAssmatHandler.NB_TENTATIVE_MAX)){
    	sb.append(getHiddenField("nbTentativeErrone", 0));	
    } else {
    sb.append(getHiddenField("nbTentativeErrone", nbTentativeErrone));
    }
    sb.append(getHiddenField("numeroAgrement", numeroAgrementInteger));
    sb.append(getHiddenField("commune", commune));
    sb.append(getHiddenField("communeMam", communeMam));
    sb.append(getHiddenField("nom", nom));
    sb.append(getHiddenField("prenom", prenom));
    sb.append(getHiddenField("civilite", civilite));
    sb.append(getHiddenField("idInscriptionAM", idInscriptionAM));
    sb.append(getHiddenField("dateDeNaissance", dateNaissance));
    sb.append(getHiddenField("email", email));
    sb.append(getHiddenField("telephone", telephone));
    sb.append(getHiddenField("telephoneFixe", telephoneFixe));
    sb.append(getHiddenField("telephone", telephone));
    sb.append(getHiddenField("typeenvoi", typeenvoi));
    sb.append(getHiddenField("choixLogin", choixLogin));
    sb.append(getHiddenField("datePremierAgrement", datePremierAgrement));
    
    if(Util.notEmpty(latAM) && Util.notEmpty(longAM) ){
      sb.append(getHiddenField("latAM", latAM));
      sb.append(getHiddenField("longAM", longAM));
     }
     if(Util.notEmpty(latMAM) && Util.notEmpty(longMAM) ){
      sb.append(getHiddenField("latMAM", latMAM));
      sb.append(getHiddenField("longMAM", longMAM));
     }
   
   
    sb.append(getHiddenField("dateProchainRenouvellement", dateProchainRenouvellement));
    sb.append(getHiddenField("dateProchainRenouvellementMAM", dateProchainRenouvellementMAM));
    sb.append(getHiddenField("dateDernierRenouvellement", dateDernierRenouvellement));
    sb.append(getHiddenField("dateDernierRenouvellementMAM", dateDernierRenouvellementMAM));
    
    sb.append(getHiddenField("idSolis", idSolis));
    sb.append(getHiddenField("idUa", idUa));
    sb.append(getHiddenField("inscriptionOK", inscriptionOK));


    return sb.toString();
  }

  @Override
  public Class<? extends Data> getDataClass() {

    return null;
  }

  // GETTERS AND SETTERS
  public int getNbTentativeErrone() {
    return nbTentativeErrone;
  }

  public void setNbTentativeErrone(int nbTentativeErrone) {
    this.nbTentativeErrone = nbTentativeErrone;
  }

  public String getChoixLogin() {
    return choixLogin;
  }

  public void setChoixLogin(String choixLogin) {
    this.choixLogin = choixLogin;
  }

  public boolean isOpContact() {
    return opContact;
  }

  public void setOpContact(boolean opContact) {
    this.opContact = opContact;
  }

  public String getCivilite() {
    return civilite;
  }

  public void setCivilite(String civilite) {
    this.civilite = civilite;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public String getPrenom() {
    return prenom;
  }

  public void setPrenom(String prenom) {
    this.prenom = prenom;
  }

  public String getDateDeNaissance() {
    return dateNaissance;
  }

  public void setDateDeNaissance(String dateDeNaissance) {
    this.dateNaissance = dateDeNaissance;
  }

  public int getNumeroAgrementInteger() {
    return numeroAgrementInteger;
  }
  
  public String getNumeroAgrement() {
	    return numeroAgrementInteger.toString();
	  }
  
  public void setNumeroAgrement(String numeroAgrement) {
    try {
      this.numeroAgrementInteger = Integer.parseInt(numeroAgrement);
    } catch(NumberFormatException ex) {
      isNumeroDagrementValidated = false;
    }
    if(numeroAgrementInteger != NUMERO_AGREMENT_DEFAULT && !Pattern.matches("[0-9]{0,7}", numeroAgrementInteger.toString())) {
      isNumeroDagrementValidated = false;
    }
  }
  
	public String getCommune() {
		return commune;
	}

	public void setCommune(String commune) {
		this.commune = commune;
	}
	
 public String getCommuneMam() {
   return communeMam;
  }

  public void setCommuneMam(String communeMam) {
   this.communeMam = communeMam;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getIdInscriptionAM() {
    return idInscriptionAM;
  }

  public void setIdInscriptionAM(String idInscriptionAM) {
    this.idInscriptionAM = idInscriptionAM;
  }

  public String getPassword() {
    return password;
  }
  public boolean isInscriptionOK() {
    return inscriptionOK;
  }

  public void setInscriptionOK(boolean inscriptionOK) {
    this.inscriptionOK = inscriptionOK;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPasswordConfirm() {
    return passwordConfirm;
  }

  public void setPasswordConfirm(String passwordConfirm) {
    this.passwordConfirm = passwordConfirm;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }
  
  public String getTelephoneFixe() {
	    return telephoneFixe;
	  }

  public void setTelephoneFixe(String telephoneFixe) {
    this.telephoneFixe = telephoneFixe;
  }
	  
  public String[] getNumeroTelUA() {
    return numeroTelUA;
  }

  public void setNumeroTelUA(String[] numeroTelUA) {
    this.numeroTelUA = numeroTelUA;
  }

  public String getNameUA() {
    return nameUA;
  }

  public void setNameUA(String nameUA) {
    this.nameUA = nameUA;
  }
  public String getTypeenvoi() {
    return typeenvoi;
  }

  public void setTypeenvoi(String typeenvoi) {
    this.typeenvoi = typeenvoi;
  }

  public String getDatePremierAgrement() {
    return datePremierAgrement;
  }

  public void setDatePremierAgrement(String datePremierAgrement) {
    this.datePremierAgrement = datePremierAgrement;
  }
  
  public String getDateDernierRenouvellement() {
	return dateDernierRenouvellement;
}

public void setDateDernierRenouvellement(String dateDernierRenouvellement) {
	this.dateDernierRenouvellement = dateDernierRenouvellement;
}

public String getDateProchainRenouvellement() {
	return dateProchainRenouvellement;
}

public void setDateProchainRenouvellement(String dateProchainRenouvellement) {
	this.dateProchainRenouvellement = dateProchainRenouvellement;
}

public String getDateDernierRenouvellementMAM() {
	return dateDernierRenouvellementMAM;
}

public void setDateDernierRenouvellementMAM(String dateDernierRenouvellementMAM) {
	this.dateDernierRenouvellementMAM = dateDernierRenouvellementMAM;
}

public String getDateProchainRenouvellementMAM() {
	return dateProchainRenouvellementMAM;
}

public void setDateProchainRenouvellementMAM(String dateProchainRenouvellementMAM) {
	this.dateProchainRenouvellementMAM = dateProchainRenouvellementMAM;
}

  public int getCodeConfirmation() {
    return codeConfirmation;
  }

  public void setCodeConfirmation(int codeConfirmation) {
    this.codeConfirmation = codeConfirmation;
  }

  public String getTelephone2Error() {
    return telephone2Error;
  }

  public void setTelephone2Error(String telephone2Error) {
    this.telephone2Error = telephone2Error;
  }
  
  public String getMail2Error() {
	  return mail2Error;
  }

  public void setMail2Error(String mail2Error) {
	  this.mail2Error = mail2Error;
  }

  public String getCodePostal() {
	  return codePostal;
  }

  public void setCodePostal(String codePostal) {
	  this.codePostal = codePostal;
  }

  public String getTexteSupport() {
    return texteSupport;
  }

  public void setTexteSupport(String texteSupport) {
    this.texteSupport = texteSupport;
  }

  public String getTexteSignalement() {
    return texteSignalement;
  }

  public void setTexteSignalement(String texteSignalement) {
    this.texteSignalement = texteSignalement;
  }

  public long getIdSolis() {
    return idSolis;
  }

  public void setIdSolis(String idSolis) {
    this.idSolis = Long.parseLong(idSolis);
  }

	public String getIdUa() {
		return idUa;
	}

	public void setIdUa(String idUa) {
		this.idUa = idUa;
	}

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
  public int getNbTentativeErroneSMS() {
    return nbTentativeErroneSMS;
  }

  public void setNbTentativeErroneSMS(int nbTentativeErroneSMS) {
    this.nbTentativeErroneSMS = nbTentativeErroneSMS;
  }

public Double getLatMAM() {
    return latMAM;
  }

  public void setLatMAM(Double latMAM) {
    this.latMAM = latMAM;
  }

  public Double getLongMAM() {
    return longMAM;
  }

  public void setLongMAM(Double longMAM) {
    this.longMAM = longMAM;
  }

  public Double getLatAM() {
    return latAM;
  }

  public void setLatAM(Double latAM) {
    this.latAM = latAM;
  }

  public Double getLongAM() {
    return longAM;
  }

  public void setLongAM(Double longAM) {
    this.longAM = longAM;
  }

  /**
   * Méthode permettant de contacter le support si le compte n'est pas trouvé
   * @return
   */
	public boolean contact2eTentativeIdentification() {
		// Si le code postale est vide
		if (Util.isEmpty(codePostal)) {
			String errorMsg = AssmatUtil.getMessagePropertiesParameters("NOT-EMPTY", new String[] { "IDENT-ECHEC2-CP-HTML" });
			JcmsContext.setWarningMsg(errorMsg, request);
			return false;
		} else {

		  
		  Routage routageMail = (Routage)channel.getPublication("$jcmsplugin.assmatplugin.categ.routage");
		  
		  if(Util.notEmpty(routageMail)){
		    if (Util.notEmpty(routageMail.getEmail())) {
		      String[] email = routageMail.getEmail();
		      Set<String> adresseSet = new HashSet<String>(Arrays.asList(email));
		      String subject = AssmatUtil.getMessage("INSCRIPTION-PROFIL-NOT-FOUND-SUBJECT-MAIL");

		       StringBuilder stbd = new StringBuilder();
		       if(Util.notEmpty(telephone2Error))
		    	   stbd.append("Téléphone: "+telephone2Error+"\n");
		       
		       if(Util.notEmpty(mail2Error))
		    	   stbd.append("Email: "+mail2Error+"\n");
		       
		       stbd.append("Nom: "+nom+"\n");
		       stbd.append("Prénom: "+prenom+"\n");
		       stbd.append("Numéro de dossier: "+numeroAgrementInteger.toString()+"\n");
		       stbd.append("Code postal: "+codePostal+"\n");
		       stbd.append("Message: "+getTexteSupport());
		      
		      // Envoie du mail
		       String from = AssmatUtil.getDefaultEmail();
		      AssmatUtil.sendMail(adresseSet, subject, stbd.toString(), from);

		      String infoMsg = AssmatUtil.getMessage("INSCRIPTION-PROFIL-NOT-FOUND-ENVOYE");
		      JcmsContext.setInfoMsg(infoMsg, request);
		     }
		    } else {
		     String infoMsg = AssmatUtil.getMessage("INSCRIPTION-PROFIL-NOT-FOUND-ROUTAGE-NOT-FOUND");
		     JcmsContext.setWarningMsg(infoMsg, request);
		    }
		   } 

		nbTentativeErrone = 0;
		return true;
	}

	/**
	 * Méthode permettant de remonter des erreurs dans les données Solis
	 */
	public void signalementErreurInformationSolis(){
		texteSignalement = getTexteSignalement();
		//On enregistre le message d'erreur
		// Récuperation de l'inscriptionAM
		idInscriptionAM = getIdInscriptionAM();
		if(Util.notEmpty(idInscriptionAM)){

			InscriptionAM isncriptionUpdated = (InscriptionAM) (channel.getPublication(idInscriptionAM)).clone();

			//On ecrase pas les autres messages si il y en a
			if(Util.notEmpty(isncriptionUpdated.getMessagesDerreur())){
				isncriptionUpdated.setMessagesDerreur(isncriptionUpdated.getMessagesDerreur() + texteSignalement);
			}else{
				isncriptionUpdated.setMessagesDerreur(texteSignalement);
			}
			isncriptionUpdated.performUpdate(channel.getDefaultAdmin());
		}

		if (Util.isEmpty(idSolis)) {
			logger.error("L'ASSMAT " + nom + " " + prenom + "n'est lié(e) à aucun idSolis en base de données");
		} else {
			// On recupere les fiches lieux
			Set<Place> setPlace = (Set<Place>) JcmsUtil.applyDataSelector(channel.getPublicationSet(Place.class, channel.getDefaultAdmin()), new PlaceSelector(idUa));
			if (Util.isEmpty(setPlace)) {
				logger.error("Impossible d’afficher les données UA pour l’AM" + nom + " " + prenom + ", ID fiche" + idSolis);
				sendDefaultMailRoutage();
			} else {
				// On recuepre le premier lieu
				Place place = Util.getFirst(setPlace);
				String[] emailContact = place.getMails();
				if (Util.notEmpty(emailContact)) {
					sendMailErrorInformation(emailContact);
				} else {
					logger.error("Aucun mail renseigné pour le lien <"+place.getId()+"> <"+place.getTitle()+">, envoi du mail aux adresses par défaut.");
					sendDefaultMailRoutage();
				}
			}
		}

	}
	
	private void sendMailErrorInformation(String[] email){
		Set<String> adresseSet = new HashSet<String>(Arrays.asList(email));

		//Sujet
		String[] parameters = new String[] {};
		parameters = (String[]) Util.insertArray(parameters, 0, prenom);
		parameters = (String[]) Util.insertArray(parameters, 1, nom);
		parameters = (String[]) Util.insertArray(parameters, 2, numeroAgrementInteger.toString());
		parameters = (String[]) Util.insertArray(parameters, 3, commune);
		String subject = AssmatUtil.getMessagePropertiesParametersValues("INSCRIPTION-PROFIL-ERROR-INFO-SUBJECT", parameters);


		String[] parametersMail = new String[] {};
		parametersMail = (String[]) Util.insertArray(parameters, 0, getTexteSignalement());     
		texteSignalement = AssmatUtil.getMessagePropertiesParametersValues("INSCRIPTION-PROFIL-ERROR-INFO-MAIL-CONTENT", parametersMail);

		String from = AssmatUtil.getDefaultEmail();
		AssmatUtil.sendMail(adresseSet, subject, texteSignalement, from);
		String infoMsg = AssmatUtil.getMessage("INSCRIPTION-PROFIL-ERROR-INFO-CONFIRMATION");
		JcmsContext.setInfoMsg(infoMsg, request);
	}
	

	private void sendDefaultMailRoutage(){
		Category categRoutage = channel.getCategory("$jcmsplugin.assmatplugin.categ.routage.signalement");
		Routage routageMail = Util.getFirst(categRoutage.getPublicationSet(Routage.class));
		if(Util.notEmpty(routageMail)){
			if (Util.notEmpty(routageMail.getEmail())) {
				String[] email = routageMail.getEmail();
				sendMailErrorInformation(email);
			}     
		}else{
			logger.error("Aucun contenu Routage trouvé pour la catégorie : " +channel.getCategory("$jcmsplugin.assmatplugin.categ.routage.signalement") + " envoi de mail impossible.");
			String infoMsg = AssmatUtil.getMessage("INSCRIPTION-PROFIL-ERROR-INFO-ENVOI-ERROR"); 
			JcmsContext.setInfoMsg(infoMsg, request);
		}
	}
	
	

}
