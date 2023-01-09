package fr.cg44.plugin.assmat.handler;

import fr.cg44.plugin.assmat.AssmatUtil;
import generated.ProfilASSMAT;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.jalios.jcms.Member;
import com.jalios.jcms.context.JcmsMessage;
import com.jalios.jcms.context.JcmsMessage.Level;
import com.jalios.jcms.handler.JcmsFormHandler;
import com.jalios.util.Util;


/**
 * FormHandler pour la mise à jour du password (membre JCMS) et du type de login (Profil AM et membre JCMS - login)
 * 
 * @author m.formont
 *
 */
public class ProfilLoginMdpHandler extends JcmsFormHandler  {

  private static final Logger logger = Logger.getLogger(ProfilLoginMdpHandler.class);

  // Opération
  private boolean opUpdate = false;
  
  // Variables du formhandler
  private String choixLogin;
  private String password;
  private String passwordConfirm;
  
  // Champs fix du formhandler
  private ProfilASSMAT profil;
  private Member member;


  @Override
  public boolean processAction() throws IOException {
    if(opUpdate && valideChangeLoginMdp()) {
      performChangeLoginMdp();
    }    
    return false;
  }

  
  /**
   * Vérifie que les valeurs saisies par l'assmat sont valides
   * @return true si le formulaire est valide
   */
  private boolean valideChangeLoginMdp() {
    boolean valide = true;
    // Vérifie le mot de passe si celui-ci est renseigné
    if(Util.notEmpty(password)) {
      // Vérifie que le mot de passe est bien égale à la confirmation du mot de passe
      if(!password.equalsIgnoreCase(passwordConfirm)){
        addMsg(new JcmsMessage(Level.WARN , AssmatUtil.getMessage("PASSWORD-EQUALS")));
        valide = false;
      }     
      // Vérifie que le mot de passe remplie bien les prérequis de sécurité
      if(!AssmatUtil.checkPassword(password)){
        addMsg(new JcmsMessage(Level.WARN , AssmatUtil.getMessage("PASSWORD-ERROR")));
        valide = false;
      }
    }
    return valide;
  }


  /**
   * Lance la modification du profil AM et du membre
   * Champs modifiés : Type de login (Profil AM) et login/password (Membre)
   */
  private void performChangeLoginMdp() {
    // Si un mot de passe est renseigné celui-ci est changé
    if(Util.notEmpty(password)) {
      Member cloneMbr = (Member) member.getUpdateInstance();
      cloneMbr.setPassword(channel.crypt(password));
      cloneMbr.performUpdate(channel.getDefaultAdmin());
    }
    // Si le type de login change celui-ci est mit à jour dans le profil AM
    // Le datacontroller sur le profil AM ce charge de mettre le login du membre à jour
    if(Util.isEmpty(profil.getTypeLogin()) || !profil.getTypeLogin().equals(choixLogin)){
      ProfilASSMAT cloneProfil = (ProfilASSMAT) profil;
      cloneProfil.setTypeLogin(choixLogin);
      cloneProfil.performUpdate(channel.getDefaultAdmin());
    }
    addMsg(new JcmsMessage(Level.INFO, AssmatUtil.getMessage("PROFIL-CHANGE-SAVE")));
  }

  
  //---------------------------------------------------------------
  // Setters
  // ---------------------------------------------------------------
 
  
  public String getChoixLogin() {
    return choixLogin;
  }


  public void setChoixLogin(String choixLogin) {
    this.choixLogin = choixLogin;
  }


  public String getPassword() {
    return password;
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
  
  
  public ProfilASSMAT getProfil() {
    return profil;
  }
  
  
  public Member getMember() {
    return member;
  }


  public void setMember(Member member) {
    this.member = member;
  }


  public void setProfil(ProfilASSMAT profil) {
    this.profil = profil;
  }


  public boolean isOpUpdate() {
    return opUpdate;
  }


  public void setOpUpdate(boolean opUpdate) {
    this.opUpdate = opUpdate;
  }
  
   
}
