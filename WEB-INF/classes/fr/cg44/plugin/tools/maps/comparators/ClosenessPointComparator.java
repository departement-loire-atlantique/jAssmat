package fr.cg44.plugin.tools.maps.comparators;

import java.util.Comparator;

import fr.cg44.plugin.socle.Point;

import static com.jalios.jcms.Channel.getChannel;



/**
 * Comparateur de proximité basé sur un écart minimum longitude/latitude à respecter.
 */
public class ClosenessPointComparator implements Comparator {
  private static final double DEFAULT_VALUE = 0.00041;
  private static final double MAX_POSITIVE_SPREAD = getChannel().getDoubleProperty("plugin.tools.googlemaps.comparator.deltacoordinates",DEFAULT_VALUE);
  private static final double MAX_NEGATIVE_SPREAD = - getChannel().getDoubleProperty("plugin.tools.googlemaps.comparator.deltacoordinates",DEFAULT_VALUE);
  
  @Override
  public int compare(Object o1, Object o2) {
    Point p1 = (Point) o1;
    Point p2 = (Point) o2;

    try{
      Float latitude1 = p1.getLatitude();
      Float longitude1 = p1.getLongitude();
      Float latitude2 = p2.getLatitude();
      Float longitude2 = p2.getLongitude();
  
      int result = 1;
      if (latitude1 - latitude2 < MAX_POSITIVE_SPREAD && latitude1 - latitude2 > MAX_NEGATIVE_SPREAD) {
        //Les 2 éléments ne respectent pas l'écart minimum des latitudes.
        if (longitude1 - longitude2 < MAX_POSITIVE_SPREAD && longitude1 - longitude2 > MAX_NEGATIVE_SPREAD) {
          //Les 2 éléments ne respectent pas l'écart minimum des longitudes.
          
          result = 0;
        } 
      }
      return result;
    } catch(NumberFormatException nfe){
      //La position de la publication est mal renseignée, son ordre importe peu.
      return 0;
    } catch(NullPointerException npe){
      //La publication n'est pas géolocalisée, son ordre importe peu.
      return 0;
    }
  }
}
