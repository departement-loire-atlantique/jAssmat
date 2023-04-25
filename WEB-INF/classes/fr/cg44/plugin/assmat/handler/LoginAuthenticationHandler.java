package fr.cg44.plugin.assmat.handler;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jalios.jcms.DataSelector;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.Member;
import com.jalios.jcms.Publication;
import com.jalios.jcms.authentication.AuthenticationContext;
import com.jalios.jcms.authentication.AuthenticationHandler;
import com.jalios.util.Util;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.managers.ProfilManager;
import fr.cg44.plugin.assmat.selector.PlaceSelector;
import fr.trsb.cd44.solis.beans.AssmatSolis;
import fr.trsb.cd44.solis.manager.SolisManager;
import generated.FicheLieu;
import generated.ProfilASSMAT;

public class LoginAuthenticationHandler extends AuthenticationHandler {
  
  private static final Logger logger = Logger.getLogger(LoginAuthenticationHandler.class);

  private static String REGEX_NUM_DOSSIER =  "([0-9]+)";
  private static Pattern PATTERN_DOSSIER = Pattern.compile(REGEX_NUM_DOSSIER);
  
  
  public LoginAuthenticationHandler(){
    setOrder(-108);
  }


  @Override
  public void login(AuthenticationContext ctxt) throws IOException {
    
    Member memberFromLogin = channel.getMemberFromLogin(ctxt.getLogin());
    
    ProfilASSMAT profilAM = null;
    ProfilManager profilManager = ProfilManager.getInstance();
    // Récupère le membre par rapport à son numéro de dossier si le login ne marche pas.
    // Permet de tester le status du memebre aussi par rapport a son numéro de dossier.
    if(memberFromLogin == null && ctxt.getLogin() != null) { 
      String loginNum = ctxt.getLogin();
      // Ignorer tout type de caractère qui ne soit pas un chiffre (exemple un point). 
      loginNum = loginNum.replaceAll("[^0-9]", "");
      Matcher matcher = PATTERN_DOSSIER.matcher(loginNum);
      if(matcher.matches()) {
        try {
          Integer numDossier = Integer.parseInt(loginNum);
          
          profilAM = profilManager.getProfilASSMAT(numDossier);
          if(profilAM != null) {
            memberFromLogin = profilManager.getMember(profilAM); 
          }
        } catch (NumberFormatException e) {
          logger.warn("Impossible de parser le numéro de dossier", e);
        }           
      }
    }

    
    if (memberFromLogin != null) {	    
      ProfilASSMAT profil = profilManager.getProfilASSMAT(memberFromLogin);
      if(Util.notEmpty(profil)){
        // On récupere ses infos Solis
        List<AssmatSolis> listeSolis = SolisManager.getInstance().getAssmatSolisByNumAgrement(profil.getNum_agrement());
        AssmatSolis solis = Util.getFirst(listeSolis);
        if(Util.notEmpty(solis)){
          // Récupère son ua et le numéro de tel de l'ua
          String uaName = "";
          String uaTel = "";
          if(Util.notEmpty(solis.getIdUa())) {
            Set<FicheLieu> uaSet = channel.getDataSet(FicheLieu.class);
            DataSelector selector = new PlaceSelector(solis.getIdUa());
            Set<FicheLieu> resultUaSet = JcmsUtil.select(uaSet, selector, Publication.getMdateComparator());
            FicheLieu ua = Util.getFirst(resultUaSet) ;
            if(Util.notEmpty(ua)) {
              uaName = ua.getTitle();
              uaTel = Util.getFirst(ua.getTelephone());
            }
          }
          
          // On vérifie son statut
          String statut = solis.getStatut();          
          // 0010454: Changement de statut entre la création du compte et une nouvelle connexion à l'espace personnel 
          if("A FORMER".equalsIgnoreCase(statut) || "NON VALIDE".equalsIgnoreCase(statut) || "DOSSIER CLOS".equalsIgnoreCase(statut) || "RETRAIT".equalsIgnoreCase(statut) || "SUSPENDUE".equalsIgnoreCase(statut) ){
            statut = SolisManager.cleanStatut(statut);
            String errorMsg = AssmatUtil.getMessagePropertiesParametersValues("CONNEXION-ECHEC-" + statut + "-HTML", new String[]{uaName, uaTel});            
            // Si pas de message alors message par defaut
            if(("CONNEXION-ECHEC-" + statut + "-HTML").equals(errorMsg)) {
              errorMsg = AssmatUtil.getMessage("CONNEXION-ECHEC-STATUT-INTROUVABLE-HTML");
              logger.warn("Pas de message d'erreur spécifique de connexion pour le statut: " + statut);
            }
            ctxt.setWarningMsg(errorMsg);
            return;
          }
        }   
      }     
    }
    ctxt.doChain();
  }


}
