package fr.cg44.plugin.assmat.util;

import java.util.List;

import fr.cg44.plugin.assmat.AssmatUtil;
import io.swagger.client.ApiException;
import io.swagger.client.api.DeclarationAccueilServiceApi;
import io.swagger.client.model.AccueilDTO;
import io.swagger.client.model.DeclarationAccueilDTO;

/**
 * Classe intermédiaire pour intérogger le service des démarches
 * car les signatures des classes changent à chaque regénération de la lib cliente
 * @author 022569F
 *
 */
public class DemarcheUtil {
  
  private static final DeclarationAccueilServiceApi DECLARATION_API = AssmatUtil.getDeclarationAccueilServiceApi();
  
  public static DeclarationAccueilDTO getDeclarationAccueilById(Integer idDeclaration) throws ApiException {
    return DECLARATION_API.getDeclarationAccueilById(idDeclaration);
  }
  
  public static List<AccueilDTO> getListAccueils(Integer numeroDossier, String etat) throws ApiException {
    return DECLARATION_API.getListAccueils1(numeroDossier, etat);
  }
  
  public static DeclarationAccueilDTO creerDeclarationAccueil(DeclarationAccueilDTO declarationAccueil) throws ApiException {
    return DECLARATION_API.creerDeclarationAccueil(declarationAccueil);
  }
  
  public static DeclarationAccueilDTO modifierDeclarationAccueil(Integer idDeclaration, DeclarationAccueilDTO declarationAccueil) throws ApiException  {
    return DECLARATION_API.modifierDeclarationAccueil(idDeclaration, declarationAccueil);
  }
  
  
  

}
