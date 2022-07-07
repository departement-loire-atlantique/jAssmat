package fr.cg44.plugin.assmat;

import java.util.Date;

import fr.cg44.plugin.socle.Point;

/**
 * Point localisé dans un espace à 2 dimensions avec une notion d'ordre et un type de point.
 */
public class PointAssmat extends Point {

  private String couleurPoint;
  private String infoPoint;
  private boolean isDomicile;
  private int etatDispo;
  private Date firstFuturDate;
 
  public PointAssmat(String latitude, String longitude, String couleurPoint) {   
    super(latitude, longitude);    
    this.setCouleurPoint(couleurPoint);    
  }
  
 public PointAssmat(String latitude, String longitude, String couleurPoint, String infoPoint) {    
    super(latitude, longitude);
    this.setInfoPoint(infoPoint);
    this.setCouleurPoint(couleurPoint);
    isDomicile = true;
  }
 
 public PointAssmat(String latitude, String longitude, String couleurPoint, String infoPoint, boolean isDomicile) { 
   this(latitude, longitude, couleurPoint, infoPoint, 0, isDomicile);
 }
  
  
  public PointAssmat(String latitude, String longitude, String couleurPoint, String infoPoint, int etatDispo, boolean isDomicile) {   
    super(latitude, longitude);
    this.setInfoPoint(infoPoint);
    this.setEtatDispo(etatDispo);
    this.setCouleurPoint(couleurPoint);
    this.setDomicile(isDomicile);
  }

  public String getInfoPoint() {
    return infoPoint;
  }

  public void setInfoPoint(String infoPoint) {
    this.infoPoint = infoPoint;
  }

  public String getCouleurPoint() {
    return couleurPoint;
  }

  public void setCouleurPoint(String couleurPoint) {
    this.couleurPoint = couleurPoint;
  }
 
  public int getEtatDispo() {
    return etatDispo;
  }

  public void setEtatDispo(int etatDispo) {
    this.etatDispo = etatDispo;
  }

  public boolean isDomicile() {
    return isDomicile;
  }

  public void setDomicile(boolean isDomicile) {
    this.isDomicile = isDomicile;
  }
   
  public Date getFirstFuturDate() {
    return firstFuturDate;
  }

  public void setFirstFuturDate(Date firstFuturDate) {
    this.firstFuturDate = firstFuturDate;
  }

@Override
public String toString() {
	return "PointAssmat [" + (couleurPoint != null ? "couleurPoint=" + couleurPoint + ", " : "") + (infoPoint != null ? "infoPoint=" + infoPoint + ", " : "") + "isDomicile=" + isDomicile + ", getLatitude()=" + getLatitude() + ", getLongitude()=" + getLongitude() + "]";
}

  
  

}