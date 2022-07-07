package fr.cg44.plugin.assmat.handler;

import fr.cg44.plugin.assmat.AssmatUtil;
import generated.ProfilASSMAT;

import java.io.IOException;

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
public class ProfilPrefsContactHandler extends JcmsFormHandler  {

  private static final Logger logger = Logger.getLogger(ProfilPrefsContactHandler.class);

  // Opération
  private boolean opUpdate = false;
  
  // Variables du formhandler
  private String choixCanalComm;
  private String email;
  private String telMobile;
  
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
    Member cloneMbr = (Member) member.getUpdateInstance();
    
    cloneMbr.setEmail(email);
    cloneMbr.setMobile(telMobile);

    profil.setCanalDeCommunicationSite(choixCanalComm);
    // Si le profil contien une erreur, retourne faux et ajoute l'erreur en warning    
    if(cloneMbr.checkIntegrity().hasFailed()) {
      addMsg(new JcmsMessage(Level.WARN, cloneMbr.checkIntegrity().toString()));
      HibernateUtil.evict(profil); 
      return false;
    }   
    if(profil.checkIntegrity().hasFailed()) {
      HibernateUtil.evict(profil);   
      addMsg(new JcmsMessage(Level.WARN, profil.checkIntegrity().toString()));
      return false;
    }
    
    // perform le membre
    cloneMbr.performUpdate(channel.getDefaultAdmin());
    // Le perform Update ne persiste pas la donnée mais lance le data controller sur le profil AM
    profil.performUpdate(channel.getDefaultAdmin());
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


  public String getChoixCanalComm() {
    return choixCanalComm;
  }


  public void setChoixCanalComm(String choixCanalComm) {
    this.choixCanalComm = choixCanalComm;
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
