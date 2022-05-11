package fr.cg44.plugin.assmat.handler;

import fr.cg44.plugin.assmat.AssmatUtil;
import generated.ProfilASSMAT;

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.jalios.jcms.Member;
import com.jalios.jcms.context.JcmsMessage;
import com.jalios.jcms.context.JcmsMessage.Level;
import com.jalios.jcms.db.HibernateUtil;
import com.jalios.jcms.handler.JcmsFormHandler;
import com.jalios.util.Util;


/**
 * FormHandler pour la mise à jour du canal de communication, e-mail et mobile du profil AM
 * 
 * @author m.formont
 *
 */
public class ProfilContactsHandler extends JcmsFormHandler  {

  private static final Logger logger = Logger.getLogger(ProfilContactsHandler.class);

  // Opération
  private boolean opUpdate = false;
  
  // Variables du formhandler
  private String email;
  private String telMobile;
  private String telFix;
  
  private String creneauAppels;
  
  private String visibleTelMobile;
  private String visibleFixMobile;
  private String visibleEmail;
  private boolean afficherContact;
  
  // Champs fix du formhandler
  private ProfilASSMAT profil;
  private Member member;


  @Override
  public boolean processAction() throws IOException {
    if(opUpdate) {
      return performChangePerfsContact();
    }    
    return false;
  }

  
  /**
   * Lance la modification du profil AM (contenu bdd donc pas de clone mais evict en cas d'erreur)
   * Champs modifiés : Canal de communication, e-mail et mobile
   */
  private boolean performChangePerfsContact() { 

    //On verifie la concordance des champs
    if(Util.isEmpty(telMobile) && AssmatUtil.getBooleanFromString(visibleTelMobile)){
      addMsg(new JcmsMessage(Level.WARN, AssmatUtil.getMessage("ERROR-VISIBILITE-TEL-MOBILE")));
      return false;
    }

    if(Util.isEmpty(telFix) && AssmatUtil.getBooleanFromString(visibleFixMobile)){
      addMsg(new JcmsMessage(Level.WARN, AssmatUtil.getMessage("ERROR-VISIBILITE-TEL-FIXE")));
      return false;
    }

    Member memberClone = (Member) member.getUpdateInstance();
    memberClone.setEmail(email);
    memberClone.setMobile(telMobile);
    if(memberClone.checkIntegrity().hasFailed()) {
      addMsg(new JcmsMessage(Level.WARN, memberClone.checkIntegrity().toString()));
      return false;
    }
    memberClone.performUpdate(channel.getDefaultAdmin());

    profil.setTelephoneFixe(telFix);
    
    if(Util.notEmpty(visibleFixMobile)) {
      profil.setVisbiliteTelephoneFixe(visibleFixMobile);
    } else {
      profil.setVisbiliteTelephoneFixe(AssmatUtil.Selection.SELECTION_NONE.getValue());
    }
    
    if(Util.notEmpty(visibleTelMobile)) {
      profil.setVisibiliteTelephonePortable(visibleTelMobile);
    } else {
      profil.setVisibiliteTelephonePortable(AssmatUtil.Selection.SELECTION_NONE.getValue());
    }
    
    profil.setCreneauHorairesDappel(creneauAppels); 
    
    if(Util.notEmpty(visibleEmail)) {
      profil.setVisibiliteAdresseEmail(visibleEmail);
    } else {
      profil.setVisibiliteAdresseEmail(AssmatUtil.Selection.SELECTION_NONE.getValue());
    }
    
    profil.setAfficherContactUniquementSiD(afficherContact);
    // Si le profil contien une erreur, retourne faux et ajoute l'erreur en warning
    if(profil.checkIntegrity().hasFailed()) {
      HibernateUtil.evict(profil);   
      addMsg(new JcmsMessage(Level.WARN, profil.checkIntegrity().toString()));
      return false;
    }
    // ajout d'une map pour différentier les formulaires
    HashMap<String, String> map = new HashMap();
    map.put("isUpdateContact", "true");
    // Le perform Update ne persiste pas la donnée mais lance le data controller sur le profil AM
    profil.performUpdate(channel.getDefaultAdmin(), map);
    addMsg(new JcmsMessage(Level.INFO, AssmatUtil.getMessage("PROFIL-CHANGE-SAVE")));
    return true;
  }

  
  //---------------------------------------------------------------
  // Setters
  // ---------------------------------------------------------------


  public boolean isOpUpdate() {
    return opUpdate;
  }


  public void setOpUpdate(boolean opUpdate) {
    this.opUpdate = opUpdate;
  }


  public String getEmail() {
    return email;
  }


  public void setEmail(String email) {
    this.email = email;
  }


  public String getTelMobile() {
    return telMobile;
  }


  public void setTelMobile(String telMobile) {
    this.telMobile = telMobile;
  }


  public String getTelFix() {
    return telFix;
  }


  public void setTelFix(String telFix) {
    this.telFix = telFix;
  }


  public String getCreneauAppels() {
    return creneauAppels;
  }


  public void setCreneauAppels(String creneauAppels) {
    this.creneauAppels = creneauAppels;
  }


  public String getVisibleTelMobile() {
    return visibleTelMobile;
  }


  public void setVisibleTelMobile(String visibleTelMobile) {
    this.visibleTelMobile = visibleTelMobile;
  }


  public String getVisibleFixMobile() {
    return visibleFixMobile;
  }


  public void setVisibleFixMobile(String visibleFixMobile) {
    this.visibleFixMobile = visibleFixMobile;
  }


  public String getVisibleEmail() {
    return visibleEmail;
  }


  public void setVisibleEmail(String visibleEmail) {
    this.visibleEmail = visibleEmail;
  }


  public boolean getAfficherContact() {
    return afficherContact;
  }


  public void setAfficherContact(boolean afficherContact) {
    this.afficherContact = afficherContact;
  }


  public ProfilASSMAT getProfil() {
    return profil;
  }


  public void setProfil(ProfilASSMAT profil) {
    this.profil = profil;
  }


  public Member getMember() {
    return member;
  }


  public void setMember(Member member) {
    this.member = member;
  }

  
}
