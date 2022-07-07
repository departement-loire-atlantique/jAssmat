package fr.cg44.plugin.assmat.comparator;

import fr.cg44.plugin.assmat.AssmatUtil.SelectionEtatDispo;
import generated.Disponibilite;

import java.util.Comparator;

/**
 * Compare les diposnibilités par apport à leur agrément domicile en 1er puis le libéllé (ordre chrono d'âge)
 * 
 */
public class DisponibiliteComparator implements Comparator<Disponibilite> {


  public DisponibiliteComparator(){
  }

  @Override
  public int compare(Disponibilite dispo1, Disponibilite dispo2) {
    // Priorité au place dispo immédiatement
    if( SelectionEtatDispo.IMMEDIATE.getValue().equalsIgnoreCase(dispo1.getEtatDispo()) &&  !SelectionEtatDispo.IMMEDIATE.getValue().equalsIgnoreCase(dispo2.getEtatDispo()) ){
      return -1;
    }
    if( !SelectionEtatDispo.IMMEDIATE.getValue().equalsIgnoreCase(dispo1.getEtatDispo())  &&  SelectionEtatDispo.IMMEDIATE.getValue().equalsIgnoreCase(dispo2.getEtatDispo()) ){
      return 1;
    }
    // Puis au place dispo dans le futur
    if( SelectionEtatDispo.FUTURE.getValue().equalsIgnoreCase(dispo1.getEtatDispo()) &&  !SelectionEtatDispo.FUTURE.getValue().equalsIgnoreCase(dispo2.getEtatDispo()) ){
      return -1;
    }
    if( !SelectionEtatDispo.FUTURE.getValue().equalsIgnoreCase(dispo1.getEtatDispo())  &&  SelectionEtatDispo.FUTURE.getValue().equalsIgnoreCase(dispo2.getEtatDispo()) ){
      return 1;
    }
    

    String dispo1lbl = formatDispo(dispo1.getLibelle());
    String dispo2lbl = formatDispo(dispo2.getLibelle());

    if(!dispo1lbl.equals(dispo2lbl)) {
      return dispo1lbl.compareTo(dispo2lbl);		
    }else {
      return dispo1.getCdate().compareTo(dispo2.getCdate());
    }
  }

  /**
   * Retourne les chiffres 0 à 9 de la chaine sur deux chiffres (ex : 2 -> 02)
   * @param lib
   * @return
   */
  public String formatDispo(String lib){
    if(lib == null) {
      return "";
    }
    String libFormat = lib.toString();
    libFormat = libFormat.replaceAll(" 0 ", " 00 " );
    libFormat = libFormat.replaceAll(" 1 ", " 01 " );
    libFormat = libFormat.replaceAll(" 2 ", " 02 " );
    libFormat = libFormat.replaceAll(" 3 ", " 03 " );
    libFormat = libFormat.replaceAll(" 4 ", " 04 " );
    libFormat = libFormat.replaceAll(" 5 ", " 05 " );
    libFormat = libFormat.replaceAll(" 6 ", " 06 " );
    libFormat = libFormat.replaceAll(" 7 ", " 07 " );
    libFormat = libFormat.replaceAll(" 8 ", " 08 " );
    libFormat = libFormat.replaceAll(" 9 ", " 09 " );
    return libFormat;
  }

}
