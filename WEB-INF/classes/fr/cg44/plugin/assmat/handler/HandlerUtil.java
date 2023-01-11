package fr.cg44.plugin.assmat.handler;

import com.jalios.jcms.Data;
import com.jalios.jcms.handler.EditDataHandler;

public class HandlerUtil extends EditDataHandler {
  
  private String endTag = "/>";
  private String endTagTechnicalField = "data-technical-field />";
  
  public static HandlerUtil getInstance() {
    return new HandlerUtil();
  }
  
  /**
   * Permet d'ajouter "data-technical-field" aux input type="hidden"
   * @param fieldName
   * @param value
   * @return
   */
  public String getHiddenFieldTag(String fieldName, String value) {
    return getHiddenField(fieldName, value).replace(endTag, endTagTechnicalField);
  }
  
  public String getHiddenFieldTag(String fieldName, String[] value) {
    return getHiddenField(fieldName, value).replace(endTag, endTagTechnicalField);
  }
  
  public String getHiddenFieldTag(String fieldName, int value) {
    return getHiddenField(fieldName, value).replace(endTag, endTagTechnicalField);
  }
  
  public String getHiddenFieldTag(String fieldName, Double value) {
    return getHiddenField(fieldName, value).replace(endTag, endTagTechnicalField);
  }
  
  public String getHiddenFieldTag(String fieldName, long value) {
    return getHiddenField(fieldName, value).replace(endTag, endTagTechnicalField);
  }
  
  public String getHiddenFieldTag(String fieldName, boolean value) {
    return getHiddenField(fieldName, value).replace(endTag, endTagTechnicalField);
  }

  @Override
  public Class<? extends Data> getDataClass() {
    return null;
  }
  
}