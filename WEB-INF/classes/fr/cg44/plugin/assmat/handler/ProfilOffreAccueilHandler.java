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


/**
 * FormHandler pour la mise à jour du canal de communication, e-mail et mobile du profil AM
 * 
 * @author m.formont
 *
 */
public class ProfilOffreAccueilHandler extends JcmsFormHandler  {

  private static final Logger logger = Logger.getLogger(ProfilOffreAccueilHandler.class);

  // Opération
  private boolean opUpdate = false;
  
  // Variables du formhandler
  private String accueilTempsPartiel;
  private String accueilPeriscolaire;
  private String accueilMercredi;
  private String accueilPendantLesVacancesSco;
  private String horairesAtypiques;
  private String avant7h;
  private String apres20h;
  private String leSamedi;
  private String leDimanche;
  private String laNuit;
  private String accepteDepannage;
  private String precisionsDepannage;
  private String accueilEnfantHandicap;
  private String precisionsEnfantHandicap;
  private String logementAccessible;
  private String precisionsLogementAccessible;
  
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
    profil.setAccueilTempsPartiel(accueilTempsPartiel);
    profil.setAccueilPeriscolaire(accueilPeriscolaire);
    profil.setAccueilMercredi(accueilMercredi);
    profil.setAccueilPendantLesVacancesSco(accueilPendantLesVacancesSco);
    profil.setHorairesAtypiques(horairesAtypiques);
    profil.setAvant7h(avant7h);
    profil.setApres20h(apres20h);
    profil.setLeSamedi(leSamedi);
    profil.setLeDimanche(leDimanche);
    profil.setLaNuit(laNuit);
    profil.setAccepteDepannage(accepteDepannage);
    profil.setPrecisionsDepannage(precisionsDepannage);
    profil.setAccueilEnfantHandicap(accueilEnfantHandicap);
    profil.setPrecisionsEnfantHandicap(precisionsEnfantHandicap);
    profil.setLogementAccessible(logementAccessible);
    profil.setPrecisionsLogementAccessible(precisionsLogementAccessible);
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


  public boolean isOpUpdate() {
    return opUpdate;
  }


  public void setOpUpdate(boolean opUpdate) {
    this.opUpdate = opUpdate;
  }


  //---------------------------------------------------------------
  // Setters
  // ---------------------------------------------------------------
  
  
  public String getAccueilTempsPartiel() {
    return accueilTempsPartiel;
  }


  public void setAccueilTempsPartiel(String accueilTempsPartiel) {
    this.accueilTempsPartiel = accueilTempsPartiel;
  }


  public String getAccueilPeriscolaire() {
    return accueilPeriscolaire;
  }


  public void setAccueilPeriscolaire(String accueilPeriscolaire) {
    this.accueilPeriscolaire = accueilPeriscolaire;
  }


  public String getAccueilMercredi() {
    return accueilMercredi;
  }


  public void setAccueilMercredi(String accueilMercredi) {
    this.accueilMercredi = accueilMercredi;
  }


  public String getAccueilPendantLesVacancesSco() {
    return accueilPendantLesVacancesSco;
  }


  public void setAccueilPendantLesVacancesSco(String accueilPendantLesVacancesSco) {
    this.accueilPendantLesVacancesSco = accueilPendantLesVacancesSco;
  }


  public String getHorairesAtypiques() {
    return horairesAtypiques;
  }


  public void setHorairesAtypiques(String horairesAtypiques) {
    this.horairesAtypiques = horairesAtypiques;
  }


  public String getAvant7h() {
    return avant7h;
  }


  public void setAvant7h(String avant7h) {
    this.avant7h = avant7h;
  }


  public String getApres20h() {
    return apres20h;
  }


  public void setApres20h(String apres20h) {
    this.apres20h = apres20h;
  }


  public String getLeSamedi() {
    return leSamedi;
  }


  public void setLeSamedi(String leSamedi) {
    this.leSamedi = leSamedi;
  }


  public String getLeDimanche() {
    return leDimanche;
  }


  public void setLeDimanche(String leDimanche) {
    this.leDimanche = leDimanche;
  }


  public String getLaNuit() {
    return laNuit;
  }


  public void setLaNuit(String laNuit) {
    this.laNuit = laNuit;
  }


  public String getAccepteDepannage() {
    return accepteDepannage;
  }


  public void setAccepteDepannage(String accepteDepannage) {
    this.accepteDepannage = accepteDepannage;
  }


  public String getPrecisionsDepannage() {
    return precisionsDepannage;
  }


  public void setPrecisionsDepannage(String precisionsDepannage) {
    this.precisionsDepannage = precisionsDepannage;
  }


  public String getAccueilEnfantHandicap() {
    return accueilEnfantHandicap;
  }


  public void setAccueilEnfantHandicap(String accueilEnfantHandicap) {
    this.accueilEnfantHandicap = accueilEnfantHandicap;
  }


  public String getPrecisionsEnfantHandicap() {
    return precisionsEnfantHandicap;
  }


  public void setPrecisionsEnfantHandicap(String precisionsEnfantHandicap) {
    this.precisionsEnfantHandicap = precisionsEnfantHandicap;
  }


  public String getLogementAccessible() {
    return logementAccessible;
  }


  public void setLogementAccessible(String logementAccessible) {
    this.logementAccessible = logementAccessible;
  }


  public String getPrecisionsLogementAccessible() {
    return precisionsLogementAccessible;
  }


  public void setPrecisionsLogementAccessible(String precisionsLogementAccessible) {
    this.precisionsLogementAccessible = precisionsLogementAccessible;
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
