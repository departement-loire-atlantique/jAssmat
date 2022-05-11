package fr.cg44.plugin.assmat.comparator;

import java.util.Comparator;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.jalios.util.Util;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.PointAssmat;
import fr.cg44.plugin.assmat.beans.AssmatSearch;
import fr.trsb.cd44.solis.beans.AssmatSolis;

/**
 * Compare le réuslat des assmats non inscrites sur le site (assmat solis)
 * 
 */
public class AssmatSolisDistanceComparator implements Comparator<AssmatSolis> {
  
  Map<AssmatSearch,PointAssmat> map;
  PointAssmat pointUser;
  String hashKey;
  Checksum checksum;
  
  
  public AssmatSolisDistanceComparator(PointAssmat pointUser, String hashKey) {
      checksum = new CRC32();
      this.pointUser = pointUser;
      this.hashKey = hashKey;
  }

  
  public int compare(AssmatSolis am1, AssmatSolis am2) {    
        
    // Trie par distance si une adresse est renseignée
    Double lat1 = 0.0;
    Double longi1 = 0.0;
    if(Util.notEmpty(am1.getLatitude()) && Util.notEmpty(am1.getLongitude())) {
      lat1 = am1.getLatitude().doubleValue();
      longi1 = am1.getLongitude().doubleValue();
    }
    
    Double lat2 = 0.0;
    Double longi2 = 0.0;
    if(Util.notEmpty(am2.getLatitude()) && Util.notEmpty(am2.getLongitude())) {
      lat2 = am2.getLatitude().doubleValue();
      longi2 = am2.getLongitude().doubleValue();
    }
       
    double distance1=0;
    double distance2=0;
    
    if(Util.notEmpty(pointUser) && Util.notEmpty(pointUser.getLatitude()) && Util.notEmpty(pointUser.getLongitude())){
      distance1= AssmatUtil.getDistance(lat1,longi1,(double) pointUser.getLatitude(), (double)pointUser.getLongitude()); 
      distance2= AssmatUtil.getDistance(lat2,longi2,(double) pointUser.getLatitude(), (double)pointUser.getLongitude());
      
      if(distance1>distance2){
        return 1;
      }
      if(distance1<distance2){
        return -1;
      }if(distance1==distance2){
        return -1;
      }
    }
    
    // Si l'utilisateur n'a pas activé sa géolocalisation
    // Alors trie aléatoirement
    if (Util.isEmpty(pointUser)) { 
      String assmat1 = am1.getNomAssmat() + am1.getPrenomAssmat() + am1.getJRowId() + hashKey;
      checksum.reset();
      checksum.update(assmat1.getBytes(), 0, assmat1.length());
      Long assmat1hash = checksum.getValue();

      
      String assmat2 = am2.getNomAssmat() + am2.getPrenomAssmat() + am2.getJRowId() + hashKey;
      checksum.reset();
      checksum.update(assmat2.getBytes(), 0, assmat2.length());
      Long assmat2hash = checksum.getValue();
      
      int cmpHash = assmat1hash.compareTo(assmat2hash);
      return cmpHash; 
    }
    
    return -1;
  }


  
  
}
