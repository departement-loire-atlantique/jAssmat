package fr.cg44.plugin.tools.maps;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import fr.cg44.plugin.socle.Point;
import fr.cg44.plugin.tools.maps.comparators.ClosenessPointComparator;


/**
 * Class de récupération des informations liées à la génération des carte
 * (marqueur et proximité des publications).
 */
public class Geolocation {


  private static final Logger LOGGER = Logger.getLogger(Geolocation.class);
  
  /**
   * Calcule la liste des points à proximité d'un point de
   * référence à partir d'un itérateur de liste triée par comparateur de
   * proximité.
   * 
   * @param point
   *          Point de référence.
   * @param itPoints
   *          Iterateur de liste triée par comparateur de proximité.
   * @return Liste des points à proximité du point de référence.
   */
  public static List<Point> getClosenessPoints(Point point, ListIterator<Point> itPoints) {
    List closenessPoints = new ArrayList<Point>();
    if (point != null) {
      ClosenessPointComparator closenessComparator = new ClosenessPointComparator();
        while (itPoints.hasNext()) {
          Point currentPoint = itPoints.next();
          if (closenessComparator.compare(point, currentPoint) == 0){
                closenessPoints.add(currentPoint);
          }
        }
    }
    return closenessPoints;
  }
  
}
  

