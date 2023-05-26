package fr.cg44.plugin.assmat.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jalios.util.ReflectUtil;
import com.jalios.util.Util;

/**
 * Classe permettant principalement de convertir les données de formulaires de planning
 * (ex. planning[1][monday][end] => fincr1lundi, planning_s3[1][wednesday][start] => s3debcr1mercredi)
 * pour fonctionner au sein du formhandler des déclarations d'accueil
 */
public class PlanningUtil {

  private static final Logger LOGGER = Logger.getLogger(PlanningUtil.class);
  
  public static String fieldValue = "fieldValue";
  public static String fieldSetMethod = "fieldSetMethod";
  
  /**
   * Format typePlanning[colonne_planning][jour][startOrEnd]
   * 
   * typePlanning
   *      planning => régulier simple
   *      planningVac => régulier vacances
   *      planning_sX => planning variable semaine type X
   * colonne_planning
   *      1, 2 ou 3
   * jour
   *      monday, tuesday, wednesday...
   * startOrEnd
   *      start
   *      end
   *      
   * 1 -> transformer chaque donnée reçue en json
   * Ex
   * {
   *    fieldValue:"",
   *    fieldSetMethod:""    <= à générer
   * }
   * 
   * 2 -> Utiliser la réflection de Java pour récupérer le champ souhaité et le mettre à jour
   * ATTENTION -> A faire dans la classe du formhandler !
   * 
   */
  
  private static String planningLbl = "planning";
  private static String openArrayChar = "[";
  private static String underscore = "_";
  private static String separator = "\\]\\[";
  private static String twoDots = ":";
  private static String letterH = "h";
  private static String start = "start";
  
  /**
   * Récupère les paramètres d'une requête pour retourner un objet JSON
   * correspondant aux couples field / value des planning d'une déclaration d'accueil
   * @param request
   * @return
   */
  public static JSONArray convertRequestFormToHandlerVars(HttpServletRequest request) {
    JSONArray fieldArray = new JSONArray();
    // explorer la Map des paramètres
    Map<String, String[]> parameterMap = request.getParameterMap();
    for (String paramName : parameterMap.keySet()) {
      try {
        if (Util.notEmpty(paramName) && paramName.startsWith(planningLbl) && paramName.contains(openArrayChar)) {
          // la clé correspond à un planning : elle nous intéresse
          // on assume qu'il n'y a qu'une valeur correspondant à cette clé
          fieldArray.put(generateStringToPlanningJson(paramName, parameterMap.get(paramName)[0]));
        }
      } catch (Exception e) {
        LOGGER.error(e);
      }      
    }
    return fieldArray;
  }
  
  /**
   * Convertit un string paramètre d'un format précis en un objet JSON
   * @param param
   * @return
   */
  private static JSONObject generateStringToPlanningJson(String param, String value) {
    JSONObject fieldNameValue = new JSONObject();
    // format de param => typePlanning[colonne_planning][jour][startOrEnd]
    String updatedParam = param;
    updatedParam = updatedParam.replace(planningLbl, "");
    // s'il reste quelque chose avant le premier [ alors il y a un préfixe ou un suffixe
    String prefix = "";
    String suffix = "";
    if (updatedParam.indexOf(openArrayChar) != 0) {
      String firstChars = updatedParam.substring(0, updatedParam.indexOf(openArrayChar)); // récupérer les caractères du début (soit "_sX", soit "Vac")
      updatedParam = updatedParam.substring(updatedParam.indexOf(openArrayChar)); // mettre à jour le param sans ces caractères
      switch (firstChars) {
      case "Vac":
        // suffixe
        suffix = firstChars;
        break;
      default:
        // préfixe semaine variable
        prefix = firstChars.replace(underscore, "");
      }
    }
    
    // reste [colonne_planning][jour][startOrEnd]
    // On doit retirer le premier caractère et le dernier, et faire un split pour avoir un tableau
    String col = "";
    String day = "";
    String startOrEnd = "";
    try {
      updatedParam = updatedParam.substring(1, updatedParam.length()-1);
      String[] paramArray = updatedParam.split(separator);
      // [0] est la colonne
      // [1] est le jour, qu'il faudra traduire
      // [2] est le début ou la fin
      col = paramArray[0];
      day = convertDayLblToFrench(paramArray[1]);
      startOrEnd = paramArray[2];
    } catch (Exception e) {
      LOGGER.error(e);
      // si ça n'est pas le format attendu, on ne peut pas continuer
      return new JSONObject();
    }
    
    // on génère le fieldName
    StringBuilder builder = new StringBuilder();
    builder.append(prefix);
    builder.append(startOrEnd);
    builder.append(col);
    builder.append(day);
    builder.append(suffix);
    
    // génération de la méthode 'set'
    String numSemaine = prefix.replace("s", "");
    String setMethod = "set" + (Util.notEmpty(prefix) ? "S"+numSemaine+(startOrEnd.equals(start) ? "d" : "f") : startOrEnd.equals(start) ? "D" : "F") 
        + (startOrEnd.equals(start) ? "ebcr" : "incr" )+ col +day.toLowerCase() + (Util.notEmpty(suffix) ? "Vac" : "");
    try {
      fieldNameValue.put(fieldValue, value.replace(twoDots, letterH));
      fieldNameValue.put(fieldSetMethod, setMethod);
    } catch (Exception e) {
      LOGGER.error(e);
      return new JSONObject();
    }
    
    return fieldNameValue;
  }
  
  /**
   * Méthode qui renvoie un nom de jour en français selon une valeur en anglais
   * nécessaire pour la récupération d'une méthode via réflexivité
   * @param day
   * @return
   */
  private static String convertDayLblToFrench(String day) {
    // à améliorer éventuellement
    switch (day) {
      case "monday":
        return "lundi";
      case "tuesday":
        return "mardi";
      case "wednesday":
        return "mercredi";
      case "thursday":
        return "jeudi";
      case "friday":
        return "vendredi";
      case "saturday":
        return "samedi";
      case "sunday":
        return "dimanche";
    }
    
    return "";
  }
  
  /**
   * Sur un horaire 08h35, renvoie 08
   * @param horaire
   * @return
   */
  public static String getHeuresFromHoraire(String horaire) {
    return Util.notEmpty(horaire) && horaire.indexOf(letterH) > 0 ? horaire.substring(0, horaire.indexOf(letterH)) : "";
  }
  
  /**
   * Sur un horaire 08h35, renvoie 35
   * @param horaire
   * @return
   */
  public static String getMinutesFromHoraire(String horaire) {
    return Util.notEmpty(horaire) && horaire.indexOf(letterH) > 0 ? horaire.substring(horaire.indexOf(letterH) + 1) : "";
  }
  
}