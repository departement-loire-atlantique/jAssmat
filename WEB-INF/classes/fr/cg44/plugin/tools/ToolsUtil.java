package fr.cg44.plugin.tools;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.ibm.icu.text.MessageFormat;
import com.jalios.jcms.Category;
import com.jalios.jcms.Channel;
import com.jalios.jcms.Member;
import com.jalios.jcms.Publication;
import com.jalios.util.Util;

import fr.cg44.plugin.tools.facetedsearch.exception.UnknowBeginDateException;
import fr.cg44.plugin.tools.facetedsearch.exception.UnknowCantonCodeException;
import fr.cg44.plugin.tools.facetedsearch.exception.UnknowCantonException;
import fr.cg44.plugin.tools.facetedsearch.exception.UnknowCitiesException;
import fr.cg44.plugin.tools.facetedsearch.exception.UnknowCityCodeException;
import fr.cg44.plugin.tools.facetedsearch.exception.UnknowCityException;
import fr.cg44.plugin.tools.facetedsearch.exception.UnknowDelegationCodeException;
import fr.cg44.plugin.tools.facetedsearch.exception.UnknowDelegationException;
import fr.cg44.plugin.tools.facetedsearch.exception.UnknowDelegationsException;
import fr.cg44.plugin.tools.facetedsearch.exception.UnknowEndDateException;
import fr.cg44.plugin.tools.facetedsearch.exception.UnknowSportException;
import fr.cg44.plugin.tools.facetedsearch.policyfilter.PublicationFacetedSearchCantonEnginePolicyFilter;
import fr.cg44.plugin.tools.facetedsearch.policyfilter.PublicationFacetedSearchCityEnginePolicyFilter;
import fr.cg44.plugin.tools.facetedsearch.policyfilter.PublicationFacetedSearchDateEnginePolicyFilter;
import fr.cg44.plugin.tools.facetedsearch.policyfilter.PublicationFacetedSearchDelegationEnginePolicyFilter;
import fr.cg44.plugin.tools.facetedsearch.policyfilter.PublicationFacetedSearchSportCommitteeEnginePolicyFilter;
import fr.cg44.plugin.tools.googlemaps.exception.KmlColorFormatException;

/**
 * Class utilitaire.
 */
public final class ToolsUtil {

  private static final String EXTRA_PREFIX = "extra.";

  private static final String EXTRA_SUFFIX = ".plugin.tools.geolocation.kml";

  private static final Logger LOGGER = Logger.getLogger(ToolsUtil.class);

  public static final String LOG_INDEXATION_FIELD = "Exception raised during the indexation of publication ({0} [id: {1}, field: {2}]).";

  public static final String LOG_HAS_NOT_FIELD = "Publication ({0} [id: {1}]) has not {2}.";

  private static final String LOG_FIELD_CITY_CODE = "cityCode";

  private static final String LOG_FIELD_CANTON_CODE = "cantonCode";

  private static final String LOG_FIELD_DELEGATION_CODE = "delegationCode";

  private ToolsUtil() {
  }

  /**
   * Retourne la valeur stricte d'une propriété.
   * 
   * @param property
   *          Propriété à récupérer.
   * @return La valeur d'une propriété, null si elle n'est pas renseignée.
   */
  public static String getProperty(String property) {
    String channelProperty = Channel.getChannel().getProperty(property);
    if (property.equals(channelProperty)) {
      return null;
    } else {
      return channelProperty;
    }
  }

  /**
   * Retourne le gabarit de la carte autonome en positionnant le type de carte
   * en attribut de requête.
   * 
   * @param request
   *          Requête courante.
   * @param type
   *          Type de la carte autonome.
   * @return Gabarit de la carte autonome.
   * @throws FileNotFoundException
   *           Une exception de type FileNotFoundException est levée si le
   *           gabarit n'existe pas.
   */
  public static String getIntegratedMapTemplate(HttpServletRequest request, String type) throws FileNotFoundException {

    String property = getProperty("plugin.tools.googlemaps.v3.typemap.template");
    if (property != null) {
      request.setAttribute("gMapsType", type);
      return property;
    } else {
      throw new FileNotFoundException();
    }
  }

  /**
   * Retourne le gabarit de la carte autonome en positionnant le type de carte
   * et la stratégie de coloration en attributs de requête.
   * 
   * @param request
   *          Requête courante.
   * @param type
   *          Type de la carte autonome.
   * @param strategy
   *          Stratégie de coloration.
   * @return Gabarit de la carte autonome.
   * @throws FileNotFoundException
   *           Une exception de type FileNotFoundException est levée si le
   *           gabarit n'existe pas.
   */
  public static String getIntegratedMapTemplate(HttpServletRequest request, String type, String strategy) throws FileNotFoundException {

    String property = getProperty("plugin.tools.googlemaps.v3.typemap.template");
    if (property != null) {
      request.setAttribute("gMapsType", type);
      request.setAttribute("gMapsStrategy", strategy);
      return property;
    } else {
      throw new FileNotFoundException();
    }
  }

  /**
   * Retourne le gabarit de la carte autonome avec kml en positionnant le type
   * de carte en attribut de requête.
   * 
   * @param request
   *          Requête courante.
   * @param type
   *          Type de la carte autonome avec kml.
   * @return Gabarit de la carte autonome avec kml.
   * @throws FileNotFoundException
   *           Une exception de type FileNotFoundException est levée si le
   *           gabarit n'existe pas.
   */
  public static String getIntegratedKmlMapTemplate(HttpServletRequest request, String type) throws FileNotFoundException {
    String property = getProperty("plugin.tools.googlemaps.v3.typemap.kml.template");
    if (property != null) {
      request.setAttribute("gMapsType", type);
      return property;
    } else {
      throw new FileNotFoundException();
    }
  }

  /**
   * Convertisseur de couleur au format kml vers le format hexadecimal.
   * 
   * @param color
   *          Couleur à convertir.
   * @return Couleur passée en paramètre convertie au format hexadécimal.
   * @throws KmlColorFormatException
   *           Une exception de type KmlClorFormatException est levée si la
   *           couleur passée en paramètre ne respecte pas le standard kml.
   */
  public static String convertKmlColorToHexColor(String color) throws KmlColorFormatException {
    if (color != null && color.length() == 8) {
      // Suppression de l'information d'opacité.
      char[] colorChars = color.substring(2).toCharArray();
      // Réorganisation des valeurs (couples inversés).
      return "" + colorChars[4] + colorChars[5] + colorChars[2] + colorChars[3] + colorChars[0] + colorChars[1];
    } else {
      throw new KmlColorFormatException();
    }
  }

  /**
   * Détermine si une publication est liée à un kml.
   * 
   * @param publication
   *          Publication à vérifier.
   * @return True si la publication est liée à un kml, false sinon.
   */
  public static boolean hasKml(Publication publication) {
    return publication.getExtraData(EXTRA_PREFIX + publication.getClass().getSimpleName() + EXTRA_SUFFIX) != null;
  }

  /**
   * Retourne l'url du kml associé à la publication.
   * 
   * @param publication
   *          Publication dont le kml doit-être retourné.
   * @return L'url du kml si elle existe, null sinon.
   */
  public static String getKmlUrl(Publication publication) {
    return publication.getExtraData(EXTRA_PREFIX + publication.getClass().getSimpleName() + EXTRA_SUFFIX);
  }

  /**
   * Récupère la commune associée à un contenu.
   * 
   * @param publication
   *          Publication associée à une commune.
   * @return Commune associée à la publication si elle existe sinon une
   *         exception de type UnknowCityException est levée.
   */
  public static Object getCity(Publication publication) throws UnknowCityException {
    if (publication == null) {
      throw new UnknowCityException();
    }
    Class clazz = publication.getClass();
    Method getCityMethod;
    Object city = null;
    try {
      getCityMethod = clazz.getMethod("getCity");
      try {
        city = getCityMethod.invoke(publication);
      } catch (IllegalAccessException iae) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchCityEnginePolicyFilter.FIELD_CITY };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iae);
        }
      } catch (IllegalArgumentException iare) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchCityEnginePolicyFilter.FIELD_CITY };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iare);
        }
      } catch (InvocationTargetException ite) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchCityEnginePolicyFilter.FIELD_CITY };
          LOGGER.debug(MessageFormat.format(LOG_HAS_NOT_FIELD, params));
        }
      }
    } catch (NoSuchMethodException nsme) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchCityEnginePolicyFilter.FIELD_CITY };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), nsme);
      }
    } catch (SecurityException se) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchCityEnginePolicyFilter.FIELD_CITY };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), se);
      }
    }
    if (city == null) {
      throw new UnknowCityException();
    } else {
      return city;
    }
  }
  
  	/**
  	 * Méthode récupérant les communes limitrophes et les communes des communautés de la commune de la publication passée en paramètre
  	 * @param publication La publication
  	 * @return Un Object
  	 */
	public static Object getClosenessCitiesAndCommunities(Publication publication) {
		Channel channel = Channel.getChannel();

		TreeSet<Publication> setCity = new TreeSet<Publication>();

		if (Util.notEmpty(publication)) {
			// Communes limitrophes
			Class clazzPublication = publication.getClass();
			try {
				// Récupération de la méthode limitrophes
				Method getClosenessCities = clazzPublication.getMethod("getClosenessCities");
				try {
					// Invocation de la méthode et ajout à la liste
					Publication[] citiesCloseness = (Publication[]) getClosenessCities.invoke(publication);
					if(Util.notEmpty(citiesCloseness) && citiesCloseness.length > 0){
						setCity.addAll(Arrays.asList(citiesCloseness));
					}
				} catch (IllegalAccessException e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(e.getMessage());
					}
				} catch (IllegalArgumentException e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(e.getMessage());
					}
				} catch (InvocationTargetException e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(e.getMessage());
					}
				}

				// Communes de la communauté de communes
				Class[] parameters = new Class[] { Member.class };
				Method getECPI = clazzPublication.getMethod("getEpci", parameters);

				try {
					// Invocation de la méthode
					Set<Category> ECPI = (Set<Category>) getECPI.invoke(publication, channel.getDefaultAdmin());
					for (Category category : ECPI) {
						try {
							// Récupération des communes à partir de la
							// catégorie de la communauté de commune
							Class clazzCity = Class.forName("generated.City");
							setCity.addAll(category	.getPublicationSet(clazzCity));
						} catch (ClassNotFoundException e) {
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(e.getMessage());
							}
						}
					}
				} catch (IllegalAccessException e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(e.getMessage());
					}
				} catch (IllegalArgumentException e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(e.getMessage());
					}
				} catch (InvocationTargetException e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(e.getMessage());
					}
				}
			} catch (NoSuchMethodException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(e.getMessage());
				}
			} catch (SecurityException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(e.getMessage());
				}
			}
		}
		// setCity.remove(publication);
		return setCity;
	}

  /**
   * Récupère les commune associées à un contenu.
   * 
   * @param publication
   *          Publication associée à une ou plusieurs communes.
   * @return Tableau des communes associées à la publication si elles existent
   *         sinon une exception de type UnknowCitiesException est levée.
   */
  public static Object[] getCities(Publication publication) throws UnknowCitiesException {
    if (publication == null) {
      throw new UnknowCitiesException();
    }
    Class clazz = publication.getClass();
    Method getCitiesMethod;
    Object[] cities = null;
    try {
      getCitiesMethod = clazz.getMethod("getCities");
      try {
        cities = (Object[]) getCitiesMethod.invoke(publication);
      } catch (IllegalAccessException iae) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchCityEnginePolicyFilter.FIELD_CITY };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iae);
        }
      } catch (IllegalArgumentException iare) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchCityEnginePolicyFilter.FIELD_CITY };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iare);
        }
      } catch (InvocationTargetException ite) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchCityEnginePolicyFilter.FIELD_CITY };
          LOGGER.debug(MessageFormat.format(LOG_HAS_NOT_FIELD, params));
        }
      }
    } catch (NoSuchMethodException nsme) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchCityEnginePolicyFilter.FIELD_CITY };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), nsme);
      }
    } catch (SecurityException se) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchCityEnginePolicyFilter.FIELD_CITY };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), se);
      }
    }
    if (cities == null) {
      throw new UnknowCitiesException();
    } else {
      return cities;
    }
  }

  /**
   * Récupère le code commune d'une publication.
   * 
   * @param publication
   *          Publication disposant d'un code commune.
   * @return Code commune de la publication s'il existe sinon une exception de
   *         type UnknowCityCodeException est levée.
   */
  public static String getCityCode(Publication publication) throws UnknowCityCodeException {
    String cityCode = "";
    if(publication == null){
    	return "";
    }
    Class clazz = publication.getClass();
    Method getCityCodeMethod;
    try {
      getCityCodeMethod = clazz.getMethod("getCityCode");
      try {
        cityCode = String.valueOf(getCityCodeMethod.invoke(publication));
      } catch (IllegalAccessException iae) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), LOG_FIELD_CITY_CODE };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iae);
        }
      } catch (IllegalArgumentException iare) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), LOG_FIELD_CITY_CODE };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iare);
        }
      } catch (InvocationTargetException ite) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), LOG_FIELD_CITY_CODE };
          LOGGER.debug(MessageFormat.format(LOG_HAS_NOT_FIELD, params));
        }
      }
    } catch (NoSuchMethodException nsme) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), LOG_FIELD_CITY_CODE };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), nsme);
      }
    } catch (SecurityException se) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), LOG_FIELD_CITY_CODE };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), se);
      }
    }
    if (cityCode.equals("")) {
      throw new UnknowCityCodeException();
    } else {
      return cityCode;
    }
  }

  /**
   * Récupère le canton associé à un contenu.
   * 
   * @param publication
   *          Publication associée à un canton.
   * @return Canton associé à la publication si elle existe sinon une exception
   *         de type UnknowCantonException est levée.
   */
  public static Object getCanton(Publication publication) throws UnknowCantonException {
    if (publication == null) {
      throw new UnknowCantonException();
    }
    Class clazz = publication.getClass();
    Method getCantonMethod;
    Object canton = null;
    try {
      getCantonMethod = clazz.getMethod("getCanton");
      try {
        canton = getCantonMethod.invoke(publication);
      } catch (IllegalAccessException iae) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchCantonEnginePolicyFilter.FIELD_CANTON };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iae);
        }
      } catch (IllegalArgumentException iare) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchCantonEnginePolicyFilter.FIELD_CANTON };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iare);
        }
      } catch (InvocationTargetException ite) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchCantonEnginePolicyFilter.FIELD_CANTON };
          LOGGER.debug(MessageFormat.format(LOG_HAS_NOT_FIELD, params));
        }
      }
    } catch (NoSuchMethodException nsme) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchCantonEnginePolicyFilter.FIELD_CANTON };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), nsme);
      }
    } catch (SecurityException se) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchCantonEnginePolicyFilter.FIELD_CANTON };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), se);
      }
    }
    if (canton == null || !(canton instanceof Publication)) {
      throw new UnknowCantonException();
    } else {
      return canton;
    }
  }

  /**
   * Récupère le code canton d'une publication.
   * 
   * @param publication
   *          Publication disposant d'un code canton.
   * @return Code canton de la publication s'il existe sinon une exception de
   *         type UnknowCantonCodeException est levée.
   */
  public static String getCantonCode(Publication publication) throws UnknowCantonCodeException {
    String cantonCode = "";
    Class clazz = publication.getClass();
    Method getCantonCodeMethod;
    try {
      getCantonCodeMethod = clazz.getMethod("getCantonCode");
      try {
        cantonCode = String.valueOf(getCantonCodeMethod.invoke(publication));
      } catch (IllegalAccessException iae) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), LOG_FIELD_CANTON_CODE };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iae);
        }
      } catch (IllegalArgumentException iare) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), LOG_FIELD_CANTON_CODE };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iare);
        }
      } catch (InvocationTargetException ite) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), LOG_FIELD_CANTON_CODE };
          LOGGER.debug(MessageFormat.format(LOG_HAS_NOT_FIELD, params));
        }
      }
    } catch (NoSuchMethodException nsme) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), LOG_FIELD_CANTON_CODE };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), nsme);
      }
    } catch (SecurityException se) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), LOG_FIELD_CANTON_CODE };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), se);
      }
    }
    if (cantonCode.equals("")) {
      throw new UnknowCantonCodeException();
    } else {
      return cantonCode;
    }
  }

  /**
   * Récupère la délégation associée à un contenu.
   * 
   * @param publication
   *          Publication associée à une délégation.
   * @return Délégation associée à la publication si elle existe sinon une
   *         exception de type UnknowDelegationException est levée.
   */
  public static Object getDelegation(Publication publication) throws UnknowDelegationException {
    if (publication == null) {
      throw new UnknowDelegationException();
    }
    Class clazz = publication.getClass();
    Method getDelegationMethod;
    Object delegation = null;
    try {
      getDelegationMethod = clazz.getMethod("getDelegation");
      try {
        delegation = getDelegationMethod.invoke(publication);
      } catch (IllegalAccessException iae) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDelegationEnginePolicyFilter.FIELD_DELEGATIONS };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iae);
        }
      } catch (IllegalArgumentException iare) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDelegationEnginePolicyFilter.FIELD_DELEGATIONS };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iare);
        }
      } catch (InvocationTargetException ite) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDelegationEnginePolicyFilter.FIELD_DELEGATIONS };
          LOGGER.debug(MessageFormat.format(LOG_HAS_NOT_FIELD, params));
        }
      }
    } catch (NoSuchMethodException nsme) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDelegationEnginePolicyFilter.FIELD_DELEGATIONS };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), nsme);
      }
    } catch (SecurityException se) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchCityEnginePolicyFilter.FIELD_CITY };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), se);
      }
    }
    if (delegation == null) {
      throw new UnknowDelegationException();
    } else {
      return delegation;
    }
  }

  /**
   * Récupère les délégations associées à un contenu.
   * 
   * @param publication
   *          Publication associée à une ou plusieurs délégations.
   * @return Tableau des délégations associées à la publication si elles
   *         existent sinon une exception de type UnknowDelegationsException est
   *         levée.
   */
  public static Object[] getDelegations(Publication publication) throws UnknowDelegationsException {
    if (publication == null) {
      throw new UnknowDelegationsException();
    }
    Class clazz = publication.getClass();
    Method getDelegationMethod;
    Object[] delegations = null;
    try {
      getDelegationMethod = clazz.getMethod("getDelegations");
      try {
        delegations = (Object[]) getDelegationMethod.invoke(publication);
      } catch (IllegalAccessException iae) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDelegationEnginePolicyFilter.FIELD_DELEGATIONS };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iae);
        }
      } catch (IllegalArgumentException iare) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDelegationEnginePolicyFilter.FIELD_DELEGATIONS };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iare);
        }
      } catch (InvocationTargetException ite) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDelegationEnginePolicyFilter.FIELD_DELEGATIONS };
          LOGGER.debug(MessageFormat.format(LOG_HAS_NOT_FIELD, params));
        }
      }
    } catch (NoSuchMethodException nsme) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDelegationEnginePolicyFilter.FIELD_DELEGATIONS };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), nsme);
      }
    } catch (SecurityException se) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDelegationEnginePolicyFilter.FIELD_DELEGATIONS };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), se);
      }
    }
    if (delegations == null) {
      throw new UnknowDelegationsException();
    } else {
      return delegations;
    }
  }

  /**
   * Récupère le code délégation (ID Solis) d'une publication.
   * 
   * @param publication
   *          Publication disposant d'un code delegation.
   * @return Code délégation de la publication s'il existe sinon une exception
   *         de type UnknowDelegationCodeException est levée.
   */
  public static String getDelegationCode(Publication publication) throws UnknowDelegationCodeException {
    String delegationCode = "";
    Class clazz = publication.getClass();
    Method getDelegationCodeMethod;
    try {
      getDelegationCodeMethod = clazz.getMethod("getZipCode");
      try {
        delegationCode = String.valueOf(getDelegationCodeMethod.invoke(publication));
      } catch (IllegalAccessException iae) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), LOG_FIELD_DELEGATION_CODE };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iae);
        }
      } catch (IllegalArgumentException iare) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), LOG_FIELD_DELEGATION_CODE };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iare);
        }
      } catch (InvocationTargetException ite) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), LOG_FIELD_DELEGATION_CODE };
          LOGGER.debug(MessageFormat.format(LOG_HAS_NOT_FIELD, params));
        }
      }
    } catch (NoSuchMethodException nsme) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), LOG_FIELD_DELEGATION_CODE };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), nsme);
      }
    } catch (SecurityException se) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), LOG_FIELD_DELEGATION_CODE };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), se);
      }
    }
    if (delegationCode.equals("")) {
      throw new UnknowDelegationCodeException();
    } else {
      return delegationCode;
    }
  }

  /**
   * Récupère la date de début d'un contenu.
   * 
   * @param publication
   *          Publication associée à une date de début.
   * @return Date de début associée à la publication si elle existe sinon une
   *         exception de type UnknowBeginDateException est levée.
   */
  public static Object getBeginDate(Publication publication) throws UnknowBeginDateException {
    if (publication == null) {
      throw new UnknowBeginDateException();
    }
    Class clazz = publication.getClass();
    Method getBeginDateMethod;
    Object beginDate = null;
    try {
      getBeginDateMethod = clazz.getMethod("getBeginDate");
      try {
        beginDate = getBeginDateMethod.invoke(publication);
      } catch (IllegalAccessException iae) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDateEnginePolicyFilter.FIELD_BEGIN_DATE };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iae);
        }
      } catch (IllegalArgumentException iare) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDateEnginePolicyFilter.FIELD_BEGIN_DATE };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iare);
        }
      } catch (InvocationTargetException ite) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDateEnginePolicyFilter.FIELD_BEGIN_DATE };
          LOGGER.debug(MessageFormat.format(LOG_HAS_NOT_FIELD, params));
        }
      }
    } catch (NoSuchMethodException nsme) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDateEnginePolicyFilter.FIELD_BEGIN_DATE };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), nsme);
      }
    } catch (SecurityException se) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDateEnginePolicyFilter.FIELD_BEGIN_DATE };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), se);
      }
    }
    if (beginDate == null) {
      throw new UnknowBeginDateException();
    } else {
      return beginDate;
    }
  }

  /**
   * Récupère la date de début d'un contenu.
   * 
   * @param publication
   *          Publication associée à une date de début.
   * @return Date de début associée à la publication si elle existe sinon une
   *         exception de type UnknowBeginDateException est levée.
   */
  public static Object getEndDate(Publication publication) throws UnknowEndDateException {
    if (publication == null) {
      throw new UnknowEndDateException();
    }
    Class clazz = publication.getClass();
    Method getEndDateMethod;
    Object endDate = null;
    try {
      getEndDateMethod = clazz.getMethod("getEndDate");
      try {
        endDate = getEndDateMethod.invoke(publication);
      } catch (IllegalAccessException iae) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDateEnginePolicyFilter.FIELD_END_DATE };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iae);
        }
      } catch (IllegalArgumentException iare) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDateEnginePolicyFilter.FIELD_END_DATE };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iare);
        }
      } catch (InvocationTargetException ite) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDateEnginePolicyFilter.FIELD_END_DATE };
          LOGGER.debug(MessageFormat.format(LOG_HAS_NOT_FIELD, params));
        }
      }
    } catch (NoSuchMethodException nsme) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDateEnginePolicyFilter.FIELD_END_DATE };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), nsme);
      }
    } catch (SecurityException se) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchDateEnginePolicyFilter.FIELD_END_DATE };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), se);
      }
    }
    if (endDate == null) {
      throw new UnknowEndDateException();
    } else {
      return endDate;
    }
  }

  /**
   * Récupère la discipline d'un contenu.
   * 
   * @param publication
   *          Publication associée à une discipline.
   * @return Discipline associée à la publication si elle existe sinon une
   *         exception de type UnknowDisciplineException est levée.
   */
  public static Object getSport(Publication publication) throws UnknowSportException {
    if (publication == null) {
      throw new UnknowSportException();
    }
    Class clazz = publication.getClass();
    Method getSportMethod;
    Object sport = null;
    try {
      getSportMethod = clazz.getMethod("getSport");
      try {
        sport = getSportMethod.invoke(publication);
      } catch (IllegalAccessException iae) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchSportCommitteeEnginePolicyFilter.FIELD_SPORT };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iae);
        }
      } catch (IllegalArgumentException iare) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchSportCommitteeEnginePolicyFilter.FIELD_SPORT };
          LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), iare);
        }
      } catch (InvocationTargetException ite) {
        if (LOGGER.isDebugEnabled()) {
          Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchSportCommitteeEnginePolicyFilter.FIELD_SPORT };
          LOGGER.debug(MessageFormat.format(LOG_HAS_NOT_FIELD, params));
        }
      }
    } catch (NoSuchMethodException nsme) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchSportCommitteeEnginePolicyFilter.FIELD_SPORT };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), nsme);
      }
    } catch (SecurityException se) {
      if (LOGGER.isDebugEnabled()) {
        Object[] params = { publication.getTitle(), publication.getId(), PublicationFacetedSearchSportCommitteeEnginePolicyFilter.FIELD_SPORT };
        LOGGER.debug(MessageFormat.format(LOG_INDEXATION_FIELD, params), se);
      }
    }
    if (sport == null) {
      throw new UnknowSportException();
    } else {
      return sport;
    }
  }

  /**
   * Détermine si une valeur est présente dans un tableau.
   * 
   * @param array
   *          Tableau à parcourir.
   * @param value
   *          Valeur à vérifier.
   * @return True si la valeur est présente dans le tableau, false sinon.
   */
  public static boolean inArray(String[] array, String value) {
    if (Util.notEmpty(array)) {
      for (String string : array) {
        if (string.equals(value)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Détermine si un caractère est présent dans un tableau.
   * 
   * @param array
   *          Tableau à parcourir.
   * @param value
   *          Valeur à vérifier.
   * @return True si la valeur est présente dans le tableau, false sinon.
   */
  public static boolean inArray(char[] array, char value) {
    if (Util.notEmpty(array)) {
      for (char character : array) {
        if (character == value) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Fusionne 2 tableau en 1 seul.
   * 
   * @param array1
   *          Tableau à fusionner.
   * @param array2
   *          Tableau à fusionner.
   * @return Null si les 2 tableaux sont null. Tableau plein si un des tableau
   *         est null. Tableau contenant toutes les entrées de arra1 et array2
   *         selon la logique suivante :
   *         {array1[0],array1[1],...,array1[array1.length-1],
   *         array2[0],...,array2[array2.length-1]}.
   */
  public static String[] arrayUnion(String[] array1, String[] array2) {

    if (array1 == null && array2 == null) {
      return null;
    }
    if (array1 != null && array2 == null) {
      return array1;
    }
    if (array1 == null && array2 != null) {
      return array2;
    }

    // On fait la fusion
    @SuppressWarnings("null")
    String[] union = new String[array1.length + array2.length];
    int index = 0;
    for (String s : array1) {
      union[index] = s;
      index++;
    }
    for (String s : array2) {
      union[index] = s;
      index++;
    }
    return union;

  }

  /**
   * Supprimer les accents d'une chaîne de caractère.
   * 
   * @param s
   *          La chaîne à formater.
   * @return Chaîne passée en paramètre sans les accents.
   */
  public static String accentsRemover(final String s) {
    return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
  }

  /**
   * Fonction remplaçant les apostrophes, les tirets et les points par des
   * espaces
   * 
   * @param word
   *          Le mot à formatter
   * @return Le mot sans apostrophes, les tirets et les points.
   */
  public static String specialsCharsRemover(final String word) {
    return word.replaceAll("\\W", " ");
  }

  /**
   * Vérifie qu'un mot ne contient que des majuscules et aucun caractères
   * accentuées comme les apostrophes, les tirets et les points.
   * 
   * @param word
   *          Le mot à vérifier
   */
  public static void stringCkeckeur(final String word) {
    // Seulement des chiffres, des caractères en miniscules et des espaces
    String regex = "[0-9A-Z\\s]+";

    if (!word.matches(regex)) {
      LOGGER.warn("Result of the function ToolsUtil.accentsRemover() doesn't match the pattern " + regex+". Word : " + word);
    }
  }

  /**
   * Supprime les accents et les caractères spéciaux, remplace le texte en
   * majuscule et retire les espaces blancs.
   * 
   * @param word
   *          Le mot à formater.
   * @return Le mot formaté.
   */
  public static String cleanData(String word) {
    String res = accentsRemover(word);
    res = specialsCharsRemover(res);
    res = res.toUpperCase();
    res = res.trim();

    // Vérification
    stringCkeckeur(res);

    return res;
  }

  /**
   * Construit la liste des inputs propres aux informations contenues dans la
   * requête d'une PQF. Cette liste est insérable dans un formulaire html.
   * 
   * @param query
   *          Requête d'un portlet PQF sous forme de chaîne de caractère.
   * @return Chaîne de caractères contenant la liste des inputs au format html.
   * @throws PatternSyntaxException
   */
  public static String buildExtendQuery(String query) throws PatternSyntaxException {
    StringBuffer input = new StringBuffer();
    // Filtre sur les types
    Pattern pattern = Pattern.compile("generated\\.[a-zA-Z0-9]+");
    Matcher matcher = pattern.matcher(query);
    while (matcher.find()) {
      input.append("<input type=\"hidden\" value=\"").append(query.substring(matcher.start(), matcher.end())).append("\" name=\"types\" />");
    }
    // Mode de filtre des catégories
    Pattern catPattern = Pattern.compile("catMode=or");
    Matcher catMatcher = catPattern.matcher(query);
    if (catMatcher.find()) {
      input.append("<input type=\"hidden\" value=\"or\" name=\"catMode\" />");
    } else {
      input.append("<input type=\"hidden\" value=\"and\" name=\"catMode\" />");
    }

    // Filtre des catégories de base
    Pattern catIdPattern = Pattern.compile("cids=([a-zA-Z0-9_]+)");
    Matcher catIdMatcher = catIdPattern.matcher(query);
    while (catIdMatcher.find()) {
      input.append("<input type=\"hidden\" name=\"hiddenCid\" value=\"" + catIdMatcher.group(1) + "\" />");
    }

    // Filtre sur les types exactes
    Pattern exactTypePattern = Pattern.compile("exactType=true");
    Matcher exactTypeMatcher = exactTypePattern.matcher(query);
    if (exactTypeMatcher.find()) {
      input.append("<input type=\"hidden\" value=\"true\" name=\"exactType\" />");
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Faceted search - extend query: " + input.toString() + ".");
    }
    return input.toString();
  }

  /**
   * Construit une liste des types propres aux informations contenues dans la
   * requête d'une PQF au format url.
   * 
   * @param query
   *          Requête d'un portlet PQF sous forme de chaîne de caractère.
   * @return Chaîne de caractères contenant la liste des inputs au format url.
   * @throws PatternSyntaxException
   */
  public static String autocompleteTypes(String query) throws PatternSyntaxException {
    StringBuffer parameters = new StringBuffer();
    Pattern pattern = Pattern.compile("generated\\.[a-zA-Z]+");
    Matcher matcher = pattern.matcher(query);
    while (matcher.find()) {
      if ("".equals(parameters.toString())) {
        parameters.append("types=");
      } else {
        parameters.append("&types=");
      }
      // Suppression du package.
      parameters.append(query.substring(matcher.start() + 10, matcher.end()));
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Faceted search - autocomplete parameters: " + parameters.toString() + ".");
    }
    return parameters.toString();
  }

  /**
   * Échappe les caractères spéciaux pour une chaîne javascript (',\,\r,\n et
   * ").
   * 
   * @param code
   *          Chaîne de caractères à échapper.
   * @return Chaîne de caractères dont les caractères spéciaux ont été échappés.
   */
  public static String escapeJavaStringChar(String code) {
    if (code == null) {
      return null;
    }
    StringBuffer localStringBuffer = new StringBuffer();
    int i = code.length();
    for (int j = 0; j < i; j++) {
      char c = code.charAt(j);
      switch (c) {
      case '\'':
        localStringBuffer.append("\\\'");
        break;
      case '\\':
        localStringBuffer.append("\\\\");
        break;
      case '\r':
        localStringBuffer.append("\\r");
        break;
      case '\n':
        localStringBuffer.append("\\n");
        break;
      case '"':
        localStringBuffer.append("\\\"");
        break;
      default:
        localStringBuffer.append(c);
      }
    }
    return localStringBuffer.toString();
  }

  /**
   * Remplacement des caractères accentués par des caractères simples et des
   * majuscules par des minuscules.
   * 
   * @param s
   *          Chaîne de caractère à traiter.
   * @return Chaîne de caractères sans les accents et les majuscules.
   */
  public static String normalize(String s) {
    return accentsRemover(s).toLowerCase();
  }
  
  /**
   * Retourne le code insee d'une commune nouvelle à partir du code d'une de ses commune délégué
   * Si pas de commune nouvelle à partir du code retourne null
   * @param code
   * @return Nouveau code insee de la commune
   */
  public static String getNewCity(String code) {
	if(Util.isEmpty(code)) {
		return null;
	}
	String newCode = Channel.getChannel().getProperty("plugin.tools.commune.delegation.code." + code);	
	if(newCode.equals("plugin.tools.commune.delegation.code." + code)) {
		return null;
	}	
	return newCode;
  }
  
  /**
   * Ajoute un nombre de mois donné à une date donnée
   * @param dateInitiale La date à modifier
   * @param nbMois Le nombre de mois à ajouter
   * @return Nouveau code insee de la commune
   */  
  public static Date ajouterMois(Date dateInitiale, int nbMois){
	  GregorianCalendar gc = new GregorianCalendar();
	  gc.setTime(dateInitiale);
	  gc.add(GregorianCalendar.MONTH, nbMois);
	  return gc.getTime();
  }  
  
  /**
   * Regarde si une publication a pour ancêtre telle catégorie
   * @param pub La publication à tester
   * @param cat La catégorie à tester
   * @return True si la publication est rangée dans cette catégorie 
   */
  public static boolean hasParentCategory(Publication pub, Category cat){
	  if(Util.notEmpty(pub)&&Util.notEmpty(cat)){
		  TreeSet categorySet = pub.getCategorySet();
		  for (Iterator iterator=categorySet.iterator(); iterator.hasNext();) {
				Category itCat = (Category)iterator.next();
				if(itCat.hasAncestor(cat)){
					return true;
				}
		  }
		  
	  }
	  return false;
  }  
}