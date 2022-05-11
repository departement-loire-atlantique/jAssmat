package fr.cg44.plugin.assmat.policyfilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.jalios.io.IOUtil;
import com.jalios.jcms.Channel;
import com.jalios.jcms.mashup.MashupUtil;
import com.jalios.util.DateUtil;
import com.jalios.util.Util;

public class ImportExpireStatus {
  
  private static final Logger LOGGER = Logger.getLogger(ImportExpireStatus.class);
  
  private Date importDate;
  private String expire;
  private String publie;
  
  
  public ImportExpireStatus() {    
  }
  
  public ImportExpireStatus(String line) {
    String separateur = "\\[([^\\]]+)\\]";
    String[] donneesArray = Util.splitWithPattern(line, separateur);
    
    importDate = DateUtil.parseW3cDate(donneesArray[0]);
    expire = getItemValue(donneesArray[1]);
    publie = getItemValue(donneesArray[2]);
  }
  
  private String getItemValue(String paramString) {
    int i = paramString.indexOf(':');
    return paramString.substring(i + 1, paramString.length()).trim();
  }
  
  
  /**
   * Retourne la liste des logs pour les changements de status (expire/publie) après un import 
   * @return la liste des logs pour les changements de status
   */
  public static List<ImportExpireStatus> getImportExpireStatus() {
    List<ImportExpireStatus> importStatus = new ArrayList<ImportExpireStatus>();    
    File logFile = new File(Channel.getChannel().getDataPath("mashup/import/expire.log"));
    if (!logFile.exists()) {
      return importStatus;
    }   
    try {
      String[] ligneTab = IOUtil.tail(logFile, 20, true, "UTF-8");
      for (int i = 0; i < ligneTab.length; i++) {
        importStatus.add(new ImportExpireStatus(ligneTab[i]));
      }           
    } catch (IOException e) {
      LOGGER.warn("Impossible de charger le journal des import (expire/publie)", e);
    }    
    return importStatus; 
  }

  
  /**
   * Ajoute une ligne dans le journal des log après un import (expire/publie)
   * @param importExpire
   */
  public static void addImportExpireStatus(ImportExpireStatus importExpire) {
    if(Util.notEmpty(importExpire.getExpire()) || Util.notEmpty(importExpire.getPublie())){
      String logFilePath = Channel.getChannel().getDataPath("mashup/import/expire.log");
      MashupUtil.appendLogEntry(logFilePath, importExpire.toString());  
    }
  }
  
  /**
   * Retourne un ImportExpireStatus en chaine de caractère (permet de le sauvegarder dans un fichier de log)
   * @return un ImportExpireStatus en chaine de caractère
   */
  public String toString() {
    StringBuilder logStringBuffer = new StringBuilder(1024);
    logStringBuffer.append("[" + DateUtil.formatW3cDate(importDate) + "] ");
    logStringBuffer.append("[expires: " + expire + "]");
    logStringBuffer.append("[publies: " + publie + "]");
    return logStringBuffer.toString();
  }
  
  public Date getImportDate() {
    return importDate;
  }

  public void setImportDate(Date importDate) {
    this.importDate = importDate;
  }

  public String getExpire() {
    return expire;
  }

  public void setExpire(String expire) {
    this.expire = expire;
  }

  public String getPublie() {
    return publie;
  }

  public void setPublie(String publie) {
    this.publie = publie;
  }
  

}
