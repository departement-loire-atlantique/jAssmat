package fr.cg44.plugin.assmat.comparator;

import io.swagger.client.model.AccueilDTO;

import java.util.Comparator;

/**
 * Compare les déclarations d'accueil par rapport à la date de modification
 * 
 */
public class DeclarationAccueilDateFinComparator implements Comparator<AccueilDTO> {


  public DeclarationAccueilDateFinComparator(){
  }

  @Override
  public int compare(AccueilDTO declaration1, AccueilDTO declaration2) {    
    if (declaration1.getDateFinAccueil().getMillis() > declaration2.getDateFinAccueil().getMillis()) {
      return -1;
    }else if(declaration1.getDateFinAccueil().getMillis() < declaration2.getDateFinAccueil().getMillis()){
      return 1;
    }else {
      return declaration1.getIdDeclaration() - declaration2.getIdDeclaration();
    }
  }


  
  

}
