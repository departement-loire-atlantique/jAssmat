package fr.cg44.plugin.assmat.handler;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.AssmatUtil.SelectionPreferenceReception;
import fr.cg44.plugin.assmat.managers.SmsDAO;
import generated.ProfilASSMAT;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.jalios.jcms.Member;
import com.jalios.jcms.context.JcmsMessage;
import com.jalios.jcms.context.JcmsMessage.Level;
import com.jalios.jcms.db.HibernateUtil;


/**
 * FormHandler pour la mise à jour du canal de communication, e-mail et mobile du profil AM
 * 
 * @author m.formont
 *
 */
public class ProfilActualiserDispoHandler extends ParametrageAssmatHandler  {

  // Opération
  private boolean opUpdate = false;
     
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
    performDispos();
    // Si le profil contien une erreur, retourne faux et ajoute l'erreur en warning
    if(profil.checkIntegrity().hasFailed()) {
      HibernateUtil.evict(profil);   
      addMsg(new JcmsMessage(Level.WARN, profil.checkIntegrity().toString()));
      return false;
    }
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
