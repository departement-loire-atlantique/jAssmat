package fr.trsb.cd44.solis.beans;


/**
 * DispoAssmat generated by hbm2java
 */
public class Quartier implements java.io.Serializable {

	private Integer idRam;
	private String idQuartier;
	private String idMicroQuartier;
	private String libQuartier;
	private String libMicroQuartier;
	
	private int type;
	
	public static final int RAM = 0;
	public static final int QUARTIER = 1;
	public static final int MICRO_QUARTIER = 2;

	public Quartier() {
	}

	public Quartier(Integer idRam, String idQuartier, String idMicroQuartier, String libQuartier, String libMicroQuartier) {
		this.idRam = idRam;
		this.idQuartier = idQuartier;
		this.idMicroQuartier = idMicroQuartier;
		this.libQuartier = libQuartier;
		this.libMicroQuartier = libMicroQuartier;
		this.type = RAM;		
	}
	
	public Quartier(String idQuartier, String libQuartier) {
		this(null, idQuartier, "", libQuartier, "");
		this.type = QUARTIER;
	}
	
	public Quartier(String idMicroQuartier, String libMicroQuartier, boolean isMicroQuartier) {				
		this(null, "", idMicroQuartier, "", libMicroQuartier);	
		this.type = MICRO_QUARTIER;
	}


	public Integer getIdRam() {
		return idRam;
	}


	public void setIdRam(Integer idRam) {
		this.idRam = idRam;
	}


	public String getIdQuartier() {
		return idQuartier;
	}


	public void setIdQuartier(String idQuartier) {
		this.idQuartier = idQuartier;
	}


	public String getIdMicroQuartier() {
		return idMicroQuartier;
	}


	public void setIdMicroQuartier(String idMicroQuartier) {
		this.idMicroQuartier = idMicroQuartier;
	}


	public String getLibQuartier() {
		return libQuartier;
	}


	public void setLibQuartier(String libQuartier) {
		this.libQuartier = libQuartier;
	}


	public String getLibMicroQuartier() {
		return libMicroQuartier;
	}


	public void setLibMicroQuartier(String libMicroQuartier) {
		this.libMicroQuartier = libMicroQuartier;
	}


}
