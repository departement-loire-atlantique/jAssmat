package fr.trsb.cd44.solis.beans;

public class GroupementRam implements java.io.Serializable {

  private Integer idRamSource;
  private Integer idRamCible;


  public GroupementRam(){	  
  }

  public GroupementRam(Integer idRamSource, Integer idRamCible) {
    super();
    this.idRamSource = idRamSource;
    this.idRamCible = idRamCible;
  }

  public Integer getIdRamSource() {
    return idRamSource;
  }
  public void setIdRamSource(Integer idRamSource) {
    this.idRamSource = idRamSource;
  }
  public Integer getIdRamCible() {
    return idRamCible;
  }
  public void setIdRamCible(Integer idRamCible) {
    this.idRamCible = idRamCible;
  }

}
