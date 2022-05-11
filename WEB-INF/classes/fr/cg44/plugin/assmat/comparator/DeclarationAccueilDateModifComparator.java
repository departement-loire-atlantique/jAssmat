package fr.cg44.plugin.assmat.comparator;

import io.swagger.client.model.AccueilDTO;

import java.util.Comparator;

/**
 * Compare les déclarations d'accueil par rapport à la date de modification
 * 
 */
public class DeclarationAccueilDateModifComparator implements Comparator<AccueilDTO> {


  public DeclarationAccueilDateModifComparator(){
  }

  @Override
  public int compare(AccueilDTO declaration1, AccueilDTO declaration2) {    
    if (declaration1.getDateMaj().getMillis() > declaration2.getDateMaj().getMillis()) {
      return -1;
    }else if(declaration1.getDateMaj().getMillis() < declaration2.getDateMaj().getMillis()){
      return 1;
    }else {
      return declaration1.getIdDeclaration() - declaration2.getIdDeclaration();
    }
  }


  
  

}
