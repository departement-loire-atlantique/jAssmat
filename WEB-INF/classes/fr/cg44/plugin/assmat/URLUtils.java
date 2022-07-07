package fr.cg44.plugin.assmat;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Classe utilitaire pour gérer les URL
 * 
 * @author m.formont
 *
 */
public final class URLUtils {

  private static final Logger LOGGER = Logger.getLogger(URLUtils.class);

  /**
   * Renvoie une map key/valeur des paramètres à partir d'une query
   * @param queryString
   * @return
   */
  public static Map<String, List<String>> splitQuery(String queryString) {
    final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
    final String[] pairs = queryString.split("&");
    for (String pair : pairs) {
      final int idx = pair.indexOf("=");
      try {
        String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
        if (!query_pairs.containsKey(key)) {
          query_pairs.put(key, new LinkedList<String>());
        }
        final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
        query_pairs.get(key).add(value);
      } catch (UnsupportedEncodingException e) {
        LOGGER.warn("impossible de décoder l'URL", e);
      }
    }
    return query_pairs;
  }

}