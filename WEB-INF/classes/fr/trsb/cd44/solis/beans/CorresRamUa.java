package fr.trsb.cd44.solis.beans;

public class CorresRamUa implements java.io.Serializable {

  private Integer idRam;
  private Integer idUa;


  public CorresRamUa(){	  
  }

  
  public CorresRamUa(Integer idRam, Integer idUa) {
    super();
    this.idRam = idRam;
    this.idUa = idUa;
  }


  public Integer getIdRam() {
    return idRam;
  }


  public void setIdRam(Integer idRam) {
    this.idRam = idRam;
  }


  public Integer getIdUa() {
    return idUa;
  }


  public void setIdUa(Integer idUa) {
    this.idUa = idUa;
  }
 

}
