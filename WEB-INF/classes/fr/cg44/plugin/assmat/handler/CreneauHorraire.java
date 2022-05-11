package fr.cg44.plugin.assmat.handler;

import com.jalios.util.Util;

public class CreneauHorraire {
  
  private String debut;
  private String fin;
  
 
  public CreneauHorraire(String debut, String fin) {
    this.debut = formatCreneau(debut);
    this.fin = formatCreneau(fin);

  }
  
  public String getDebut() {
    return debut;
  }
  public void setDebut(String debut) {
    this.debut = formatCreneau(debut);
  }
  public String getFin() {
    return fin;
  }
  public void setFin(String fin) {
    this.fin = formatCreneau(fin);
  }
  
  // Met au format 00h00 sauf si le creneau n'est pas au format correct
  public static String formatCreneau(String creneau) {
    String var = "";    
    if(Util.isEmpty(creneau)) {
      return var;
    }   
    if(!DeclarerAccueilAssmatHandler.checkFormatHeure(creneau)) {
      return creneau;
    }
    var = creneau.replaceAll(" ", ""); 
    var = var.toLowerCase();
    
    String horraire[] = var.split("h");
    if(horraire.length >= 1) {
      String heure = horraire[0];
      String minute = "00";
      if(horraire.length == 2) {
        minute = horraire[1];
        if(minute.length() == 1) {
          minute = "0" + minute;
        }
      }
      if(heure.length() == 1) {
        heure = "0" + heure;
      }
      var = heure + "h" + minute;
    }       
    return var;
  }
  
}
