package fr.cg44.plugin.assmat.comparator;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.jalios.util.Util;

import fr.cg44.plugin.assmat.AssmatUtil;
import fr.cg44.plugin.assmat.PointAssmat;
import fr.cg44.plugin.assmat.beans.AssmatSearch;
import fr.cg44.plugin.assmat.beans.DispoAssmat;

/**
 * Compare le réuslat des assmats
 * 
 */
public class AssmatSearchDistanceComparator implements Comparator<AssmatSearch> {
  
  Map<AssmatSearch,PointAssmat> map;
  Map<Long, Set<DispoAssmat>> resultDispoRechercheMap;
  PointAssmat pointUser;
  String hashKey;
  Checksum checksum;
  boolean isFutureSearch;
  
  
  public AssmatSearchDistanceComparator(PointAssmat pointUser, Map<AssmatSearch,PointAssmat> map, String hashKey, boolean isFutureSearch, Map<Long, Set<DispoAssmat>> resultDispoRechercheMap) {
      checksum = new CRC32();
      this.pointUser = pointUser;
      this.map = map;
      this.resultDispoRechercheMap = resultDispoRechercheMap;
      this.hashKey = hashKey;
      this.isFutureSearch = isFutureSearch;
  }

  
  public int compare(AssmatSearch am1, AssmatSearch am2) {    
    
    PointAssmat pointAssmat1 = map.get(am1);
    PointAssmat pointAssmat2 = map.get(am2);
    
    // Tie par disponibilité
    Integer etatDispoAm1 = pointAssmat1.getEtatDispo();
    Integer etatDispoAm2 = pointAssmat2.getEtatDispo();    
    Integer cmp = etatDispoAm1.compareTo(etatDispoAm2);    
    if(cmp != 0) {
      return cmp;
    }
    
    
    // 0012201 : Si recherche a une date dans le future alors trie sur la date de dispo furture pour les dispo actuellement
    if(etatDispoAm1 == 0 && etatDispoAm2 == 0){          
      Set<DispoAssmat> dispoListAm1 = resultDispoRechercheMap.get(am1.getJRowId());
      Set<DispoAssmat> dispoListAm2 = resultDispoRechercheMap.get(am2.getJRowId());          
      Date dispoDerniereDateAm1 = getDispoDerniereDate(dispoListAm1);
      Date dispoDerniereDateAm2 = getDispoDerniereDate(dispoListAm2);       
      int cmpDateRechercheFuture = dispoDerniereDateAm1.compareTo(dispoDerniereDateAm2) * - 1;     
      if(cmpDateRechercheFuture != 0){
        return cmpDateRechercheFuture;
      }
    }
    
    
    // Si disponibilité future alors trier sur la date de dispo future la plus proche de la recherche (si date différente)
    if(etatDispoAm1 == 1 && etatDispoAm2 == 1){
      int cmpDate = pointAssmat1.getFirstFuturDate().compareTo(pointAssmat2.getFirstFuturDate());
      if(cmpDate != 0) {
        return cmpDate;
      }
    }
        
    // Trie par distance si la géolocalisation est activée ou si une adresse est renseignée
    Double lat1 = (double) pointAssmat1.getLatitude();
    Double longi1 = (double) pointAssmat1.getLongitude();
    
    Double longi2 = (double) pointAssmat2.getLongitude();
    Double lat2 = (double) pointAssmat2.getLatitude();
       
    double distance1=0;
    double distance2=0;
    
    if(Util.notEmpty(lat1) && Util.notEmpty(longi1) && Util.notEmpty(longi2) && Util.notEmpty(lat2) && Util.notEmpty(pointUser)){
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
      String assmat1 = am1.getNomAssmat() + am1.getMembreJcmsId() + am1.getJRowId() + hashKey;
      checksum.reset();
      checksum.update(assmat1.getBytes(), 0, assmat1.length());
      Long assmat1hash = checksum.getValue();
      
      String assmat2 = am2.getNomAssmat() + am2.getMembreJcmsId() + am2.getJRowId() + hashKey;
      checksum.reset();
      checksum.update(assmat2.getBytes(), 0, assmat2.length());
      Long assmat2hash = checksum.getValue();
      
      int cmpHash = assmat1hash.compareTo(assmat2hash);
      return cmpHash; 
    }
    
    return -1;
  }


  private Date getDispoDerniereDate(Set<DispoAssmat> dispoListAm1) {
    Date dispoDerniereDate = null;
    for(DispoAssmat itDispo : dispoListAm1) {
      if( "2".equals(itDispo.getDisponible()) && itDispo.getDateDebut() != null ){
        Date itDate = itDispo.getDateDebut();
        if( itDate.after(new Date()) && ( dispoDerniereDate == null || itDate.getTime() > dispoDerniereDate.getTime()) ){
          dispoDerniereDate = itDate;
        }
      }
    }
    if(dispoDerniereDate == null) {
      dispoDerniereDate = new Date(0);
    }
    return dispoDerniereDate;
  }
  
  
}
