package fr.cg44.plugin.assmat.datacontroller;

import org.apache.log4j.Logger;

import com.jalios.jcms.BasicDataController;
import com.jalios.jcms.ControllerStatus;
import com.jalios.jcms.Data;

import fr.cg44.plugin.assmat.AssmatUtil;
import generated.DeclarerAccueil;

/**
 * Controle le champ certifie l'exactitude des informations et mentions particulieres des démarches
 * 
 * @author mformont
 *
 */
public class DemarcheDataController extends BasicDataController {

  private static Logger logger = Logger.getLogger(ProfilASSMATDataController.class);
  
  @Override
  public ControllerStatus checkIntegrity(Data data) {
    try {
      // Champ certifie exactitude des informations
      if(!data.getBooleanFieldValue("certifieExactitude")){
        return new ControllerStatus(AssmatUtil.getMessage("DEMARCHE-ERROR-CERTIFIE-EXACT"));
      }      
      // Champ accepte mentions particulieres
      if(data instanceof DeclarerAccueil && !data.getBooleanFieldValue("accepteMentions")) {
        return new ControllerStatus(AssmatUtil.getMessage("DEMARCHE-ERROR-ACCEPTE-MENTIONS"));
      }      
    } catch (NoSuchFieldException e) {
      logger.warn("Impossible de vérifier si le ou les checkbox obligatoires de la démarche sont cochées", e);
    }   
    return ControllerStatus.OK;
  }

}
