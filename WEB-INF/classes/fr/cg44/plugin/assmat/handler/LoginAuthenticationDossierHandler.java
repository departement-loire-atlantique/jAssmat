package fr.cg44.plugin.assmat.handler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jalios.jcms.Member;
import com.jalios.jcms.authentication.AuthenticationContext;
import com.jalios.jcms.authentication.AuthenticationHandler;
import com.jalios.jcms.authentication.AuthenticationManager;

import fr.cg44.plugin.assmat.managers.ProfilManager;
import generated.ProfilASSMAT;

public class LoginAuthenticationDossierHandler extends AuthenticationHandler {
  
  private static final Logger logger = Logger.getLogger(LoginAuthenticationDossierHandler.class);

  private static String REGEX_NUM_DOSSIER =  "([0-9]+)";
  private static Pattern PATTERN_DOSSIER = Pattern.compile(REGEX_NUM_DOSSIER);
  
  
  public LoginAuthenticationDossierHandler(){
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
      
      // Si il n'est pas connecté et numéro de dossier renseigné
      // Identifie le membre si numéro de dossier / mdp correct
      if(profilAM != null && memberFromLogin != null) {
        // Identifie la personne a partir de son login (permet de vérifier le mot de passe avec le couple login / mdp).
        Member mbrDossier = AuthenticationManager.getInstance().login(memberFromLogin.getLogin() , ctxt.getPassword());
        ctxt.setLoggedMember(mbrDossier);   
      }         
    }        
    ctxt.doChain();
  }

}
