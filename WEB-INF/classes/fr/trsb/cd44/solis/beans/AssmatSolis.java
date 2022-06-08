package fr.trsb.cd44.solis.beans;

import java.math.BigDecimal;
import java.util.Date;

public class AssmatSolis implements java.io.Serializable {

	private long JRowId;
	private String civiliteAssmat;
	private String nomAssmat;
	private String prenomAssmat;
	private Date dateNaissAssmat;
	private Integer numDossierAssmat;
	private String telPrincipal;
	private String telPortable;
	private String emailAssmat;
	private Date datePremierAgrement;
	private Boolean formationInitiale;
	private Date dateDernierRenouvellement;
	private Date dateProchainRenouvellement;
	private Date dateDernierRenouvellementMam;
	private Date dateProchainRenouvellementMam;
	private String adresseDomicile;
	private String cpDomicile;
	private String communeDomicile;
	private Boolean exerceMam;
	private String nomMam;
	private String adresseMam;
	private String cpMam;
	private String communeMam;
	private Boolean enActivite;
	private String place1AgrementTrancheAgeKey;
	private Integer place1TrancheAgeKey;
	private String place1TrancheAge;
	private String place1LibCompl;
	private Integer place1NbPlaces;
	private Boolean place1SaisieDisponibilite;
	private String place2AgrementTrancheAgeKey;
	private Integer place2TrancheAgeKey;
	private String place2TrancheAge;
	private String place2LibCompl;
	private Integer place2NbPlaces;
	private Boolean place2SaisieDisponibilite;
	private String place3AgrementTrancheAgeKey;
	private Integer place3TrancheAgeKey;
	private String place3TrancheAge;
	private String place3LibCompl;
	private Integer place3NbPlaces;
	private Boolean place3SaisieDisponibilite;
	private String place4AgrementTrancheAgeKey;
	private Integer place4TrancheAgeKey;
	private String place4TrancheAge;
	private String place4LibCompl;
	private Integer place4NbPlaces;
	private Boolean place4SaisieDisponibilite;
	private String place5AgrementTrancheAgeKey;
	private Integer place5TrancheAgeKey;
	private String place5TrancheAge;
	private String place5LibCompl;
	private Integer place5NbPlaces;
	private Boolean place5SaisieDisponibilite;
	private String place6AgrementTrancheAgeKey;
	private Integer place6TrancheAgeKey;
	private String place6TrancheAge;
	private String place6LibCompl;
	private Integer place6NbPlaces;
	private Boolean place6SaisieDisponibilite;
	private String place7AgrementTrancheAgeKey;
	private Integer place7TrancheAgeKey;
	private String place7TrancheAge;
	private String place7LibCompl;
	private Integer place7NbPlaces;
	private Boolean place7SaisieDisponibilite;
	private String place8AgrementTrancheAgeKey;
	private Integer place8TrancheAgeKey;
	private String place8TrancheAge;
	private String place8LibCompl;
	private Integer place8NbPlaces;
	private Boolean place8SaisieDisponibilite;
	private String statut;
	private Boolean autorisationActivation;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private BigDecimal latitudeMam;
	private BigDecimal longitudeMam;
	private String idUa;
	private Boolean exerceDomicile;
	private Boolean aideCaf;
	private String codeInsee;
	private Integer idRam;
	private Integer idRamMam;
	private String complementAdresse;
	private String codeInseeMam;
	private String idQuartierDom;
 private String idQuartierMam;
	private String idMicroQuartierDom;
	private String idMicroQuartierMam;


	public AssmatSolis() {
	}

	public AssmatSolis(long JRowId) {
		this.JRowId = JRowId;
	}

	

	public AssmatSolis(long jRowId, String civiliteAssmat, String nomAssmat, String prenomAssmat, Date dateNaissAssmat, Integer numDossierAssmat,
      String telPrincipal, String telPortable, String emailAssmat, Date datePremierAgrement, Boolean formationInitiale, Date dateDernierRenouvellement,
      Date dateProchainRenouvellement, Date dateDernierRenouvellementMam, Date dateProchainRenouvellementMam, String adresseDomicile, String cpDomicile,
      String communeDomicile, Boolean exerceMam, String nomMam, String adresseMam, String cpMam, String communeMam, Boolean enActivite,
      String place1AgrementTrancheAgeKey, Integer place1TrancheAgeKey, String place1TrancheAge, String place1LibCompl, Integer place1NbPlaces,
      Boolean place1SaisieDisponibilite, String place2AgrementTrancheAgeKey, Integer place2TrancheAgeKey, String place2TrancheAge, String place2LibCompl,
      Integer place2NbPlaces, Boolean place2SaisieDisponibilite, String place3AgrementTrancheAgeKey, Integer place3TrancheAgeKey, String place3TrancheAge,
      String place3LibCompl, Integer place3NbPlaces, Boolean place3SaisieDisponibilite, String place4AgrementTrancheAgeKey, Integer place4TrancheAgeKey,
      String place4TrancheAge, String place4LibCompl, Integer place4NbPlaces, Boolean place4SaisieDisponibilite, String place5AgrementTrancheAgeKey,
      Integer place5TrancheAgeKey, String place5TrancheAge, String place5LibCompl, Integer place5NbPlaces, Boolean place5SaisieDisponibilite,
      String place6AgrementTrancheAgeKey, Integer place6TrancheAgeKey, String place6TrancheAge, String place6LibCompl, Integer place6NbPlaces,
      Boolean place6SaisieDisponibilite, String place7AgrementTrancheAgeKey, Integer place7TrancheAgeKey, String place7TrancheAge, String place7LibCompl,
      Integer place7NbPlaces, Boolean place7SaisieDisponibilite, String place8AgrementTrancheAgeKey, Integer place8TrancheAgeKey, String place8TrancheAge,
      String place8LibCompl, Integer place8NbPlaces, Boolean place8SaisieDisponibilite, String statut, Boolean autorisationActivation, BigDecimal latitude,
      BigDecimal longitude, BigDecimal latitudeMam, BigDecimal longitudeMam, String idUa, Boolean exerceDomicile, Boolean aideCaf, String codeInsee,
      Integer idRam, Integer idRamMam, String complementAdresse, String codeInseeMam, String idQuartierDom, String idQuartierMam, String idMicroQuartierDom,
      String idMicroQuartierMam) {
    super();
    JRowId = jRowId;
    this.civiliteAssmat = civiliteAssmat;
    this.nomAssmat = nomAssmat;
    this.prenomAssmat = prenomAssmat;
    this.dateNaissAssmat = dateNaissAssmat;
    this.numDossierAssmat = numDossierAssmat;
    this.telPrincipal = telPrincipal;
    this.telPortable = telPortable;
    this.emailAssmat = emailAssmat;
    this.datePremierAgrement = datePremierAgrement;
    this.formationInitiale = formationInitiale;
    this.dateDernierRenouvellement = dateDernierRenouvellement;
    this.dateProchainRenouvellement = dateProchainRenouvellement;
    this.dateDernierRenouvellementMam = dateDernierRenouvellementMam;
    this.dateProchainRenouvellementMam = dateProchainRenouvellementMam;
    this.adresseDomicile = adresseDomicile;
    this.cpDomicile = cpDomicile;
    this.communeDomicile = communeDomicile;
    this.exerceMam = exerceMam;
    this.nomMam = nomMam;
    this.adresseMam = adresseMam;
    this.cpMam = cpMam;
    this.communeMam = communeMam;
    this.enActivite = enActivite;
    this.place1AgrementTrancheAgeKey = place1AgrementTrancheAgeKey;
    this.place1TrancheAgeKey = place1TrancheAgeKey;
    this.place1TrancheAge = place1TrancheAge;
    this.place1LibCompl = place1LibCompl;
    this.place1NbPlaces = place1NbPlaces;
    this.place1SaisieDisponibilite = place1SaisieDisponibilite;
    this.place2AgrementTrancheAgeKey = place2AgrementTrancheAgeKey;
    this.place2TrancheAgeKey = place2TrancheAgeKey;
    this.place2TrancheAge = place2TrancheAge;
    this.place2LibCompl = place2LibCompl;
    this.place2NbPlaces = place2NbPlaces;
    this.place2SaisieDisponibilite = place2SaisieDisponibilite;
    this.place3AgrementTrancheAgeKey = place3AgrementTrancheAgeKey;
    this.place3TrancheAgeKey = place3TrancheAgeKey;
    this.place3TrancheAge = place3TrancheAge;
    this.place3LibCompl = place3LibCompl;
    this.place3NbPlaces = place3NbPlaces;
    this.place3SaisieDisponibilite = place3SaisieDisponibilite;
    this.place4AgrementTrancheAgeKey = place4AgrementTrancheAgeKey;
    this.place4TrancheAgeKey = place4TrancheAgeKey;
    this.place4TrancheAge = place4TrancheAge;
    this.place4LibCompl = place4LibCompl;
    this.place4NbPlaces = place4NbPlaces;
    this.place4SaisieDisponibilite = place4SaisieDisponibilite;
    this.place5AgrementTrancheAgeKey = place5AgrementTrancheAgeKey;
    this.place5TrancheAgeKey = place5TrancheAgeKey;
    this.place5TrancheAge = place5TrancheAge;
    this.place5LibCompl = place5LibCompl;
    this.place5NbPlaces = place5NbPlaces;
    this.place5SaisieDisponibilite = place5SaisieDisponibilite;
    this.place6AgrementTrancheAgeKey = place6AgrementTrancheAgeKey;
    this.place6TrancheAgeKey = place6TrancheAgeKey;
    this.place6TrancheAge = place6TrancheAge;
    this.place6LibCompl = place6LibCompl;
    this.place6NbPlaces = place6NbPlaces;
    this.place6SaisieDisponibilite = place6SaisieDisponibilite;
    this.place7AgrementTrancheAgeKey = place7AgrementTrancheAgeKey;
    this.place7TrancheAgeKey = place7TrancheAgeKey;
    this.place7TrancheAge = place7TrancheAge;
    this.place7LibCompl = place7LibCompl;
    this.place7NbPlaces = place7NbPlaces;
    this.place7SaisieDisponibilite = place7SaisieDisponibilite;
    this.place8AgrementTrancheAgeKey = place8AgrementTrancheAgeKey;
    this.place8TrancheAgeKey = place8TrancheAgeKey;
    this.place8TrancheAge = place8TrancheAge;
    this.place8LibCompl = place8LibCompl;
    this.place8NbPlaces = place8NbPlaces;
    this.place8SaisieDisponibilite = place8SaisieDisponibilite;
    this.statut = statut;
    this.autorisationActivation = autorisationActivation;
    this.latitude = latitude;
    this.longitude = longitude;
    this.latitudeMam = latitudeMam;
    this.longitudeMam = longitudeMam;
    this.idUa = idUa;
    this.exerceDomicile = exerceDomicile;
    this.aideCaf = aideCaf;
    this.codeInsee = codeInsee;
    this.idRam = idRam;
    this.idRamMam = idRamMam;
    this.complementAdresse = complementAdresse;
    this.codeInseeMam = codeInseeMam;
    this.idQuartierDom = idQuartierDom;
    this.idQuartierMam = idQuartierMam;
    this.idMicroQuartierDom = idMicroQuartierDom;
    this.idMicroQuartierMam = idMicroQuartierMam;
  }

  public long getJRowId() {
		return this.JRowId;
	}

	public void setJRowId(long JRowId) {
		this.JRowId = JRowId;
	}

	public String getCiviliteAssmat() {
		return this.civiliteAssmat;
	}

	public void setCiviliteAssmat(String civiliteAssmat) {
		this.civiliteAssmat = civiliteAssmat;
	}

	public String getNomAssmat() {
		return this.nomAssmat;
	}

	public void setNomAssmat(String nomAssmat) {
		this.nomAssmat = nomAssmat;
	}

	public String getPrenomAssmat() {
		return this.prenomAssmat;
	}

	public void setPrenomAssmat(String prenomAssmat) {
		this.prenomAssmat = prenomAssmat;
	}

	public Date getDateNaissAssmat() {
		return this.dateNaissAssmat;
	}

	public void setDateNaissAssmat(Date dateNaissAssmat) {
		this.dateNaissAssmat = dateNaissAssmat;
	}

	public Integer getNumDossierAssmat() {
		return this.numDossierAssmat;
	}

	public void setNumDossierAssmat(Integer numDossierAssmat) {
		this.numDossierAssmat = numDossierAssmat;
	}

	public String getTelPrincipal() {
		return this.telPrincipal;
	}

	public void setTelPrincipal(String telPrincipal) {
		this.telPrincipal = telPrincipal;
	}

	public String getTelPortable() {
		return this.telPortable;
	}

	public void setTelPortable(String telPortable) {
		this.telPortable = telPortable;
	}

	public String getEmailAssmat() {
		return this.emailAssmat;
	}

	public void setEmailAssmat(String emailAssmat) {
		this.emailAssmat = emailAssmat;
	}

	public Date getDatePremierAgrement() {
		return this.datePremierAgrement;
	}

	public void setDatePremierAgrement(Date datePremierAgrement) {
		this.datePremierAgrement = datePremierAgrement;
	}

	public Boolean getFormationInitiale() {
		return this.formationInitiale;
	}

	public void setFormationInitiale(Boolean formationInitiale) {
		this.formationInitiale = formationInitiale;
	}

	public Date getDateDernierRenouvellement() {
		return this.dateDernierRenouvellement;
	}

	public void setDateDernierRenouvellement(Date dateDernierRenouvellement) {
		this.dateDernierRenouvellement = dateDernierRenouvellement;
	}

	public Date getDateProchainRenouvellement() {
		return this.dateProchainRenouvellement;
	}

	public void setDateProchainRenouvellement(Date dateProchainRenouvellement) {
		this.dateProchainRenouvellement = dateProchainRenouvellement;
	}

	public Date getDateDernierRenouvellementMam() {
		return this.dateDernierRenouvellementMam;
	}

	public void setDateDernierRenouvellementMam(Date dateDernierRenouvellementMam) {
		this.dateDernierRenouvellementMam = dateDernierRenouvellementMam;
	}

	public Date getDateProchainRenouvellementMam() {
		return this.dateProchainRenouvellementMam;
	}

	public void setDateProchainRenouvellementMam(Date dateProchainRenouvellementMam) {
		this.dateProchainRenouvellementMam = dateProchainRenouvellementMam;
	}

	public String getAdresseDomicile() {
		return this.adresseDomicile;
	}

	public void setAdresseDomicile(String adresseDomicile) {
		this.adresseDomicile = adresseDomicile;
	}

	public String getCpDomicile() {
		return this.cpDomicile;
	}

	public void setCpDomicile(String cpDomicile) {
		this.cpDomicile = cpDomicile;
	}

	public String getCommuneDomicile() {
		return this.communeDomicile;
	}

	public void setCommuneDomicile(String communeDomicile) {
		this.communeDomicile = communeDomicile;
	}

	public Boolean getExerceMam() {
		return this.exerceMam;
	}

	public void setExerceMam(Boolean exerceMam) {
		this.exerceMam = exerceMam;
	}

	public String getNomMam() {
		return this.nomMam;
	}

	public void setNomMam(String nomMam) {
		this.nomMam = nomMam;
	}

	public String getAdresseMam() {
		return this.adresseMam;
	}

	public void setAdresseMam(String adresseMam) {
		this.adresseMam = adresseMam;
	}

	public String getCpMam() {
		return this.cpMam;
	}

	public void setCpMam(String cpMam) {
		this.cpMam = cpMam;
	}

	public String getCommuneMam() {
		return this.communeMam;
	}

	public void setCommuneMam(String communeMam) {
		this.communeMam = communeMam;
	}

	public Boolean getEnActivite() {
		return this.enActivite;
	}

	public void setEnActivite(Boolean enActivite) {
		this.enActivite = enActivite;
	}

	public String getPlace1AgrementTrancheAgeKey() {
		return this.place1AgrementTrancheAgeKey;
	}

	public void setPlace1AgrementTrancheAgeKey(String place1AgrementTrancheAgeKey) {
		this.place1AgrementTrancheAgeKey = place1AgrementTrancheAgeKey;
	}

	public Integer getPlace1TrancheAgeKey() {
		return this.place1TrancheAgeKey;
	}

	public void setPlace1TrancheAgeKey(Integer place1TrancheAgeKey) {
		this.place1TrancheAgeKey = place1TrancheAgeKey;
	}

	public String getPlace1TrancheAge() {
		return this.place1TrancheAge;
	}

	public void setPlace1TrancheAge(String place1TrancheAge) {
		this.place1TrancheAge = place1TrancheAge;
	}

	public String getPlace1LibCompl() {
		return this.place1LibCompl;
	}

	public void setPlace1LibCompl(String place1LibCompl) {
		this.place1LibCompl = place1LibCompl;
	}

	public Integer getPlace1NbPlaces() {
		return this.place1NbPlaces;
	}

	public void setPlace1NbPlaces(Integer place1NbPlaces) {
		this.place1NbPlaces = place1NbPlaces;
	}

	public Boolean getPlace1SaisieDisponibilite() {
		return this.place1SaisieDisponibilite;
	}

	public void setPlace1SaisieDisponibilite(Boolean place1SaisieDisponibilite) {
		this.place1SaisieDisponibilite = place1SaisieDisponibilite;
	}

	public String getPlace2AgrementTrancheAgeKey() {
		return this.place2AgrementTrancheAgeKey;
	}

	public void setPlace2AgrementTrancheAgeKey(String place2AgrementTrancheAgeKey) {
		this.place2AgrementTrancheAgeKey = place2AgrementTrancheAgeKey;
	}

	public Integer getPlace2TrancheAgeKey() {
		return this.place2TrancheAgeKey;
	}

	public void setPlace2TrancheAgeKey(Integer place2TrancheAgeKey) {
		this.place2TrancheAgeKey = place2TrancheAgeKey;
	}

	public String getPlace2TrancheAge() {
		return this.place2TrancheAge;
	}

	public void setPlace2TrancheAge(String place2TrancheAge) {
		this.place2TrancheAge = place2TrancheAge;
	}

	public String getPlace2LibCompl() {
		return this.place2LibCompl;
	}

	public void setPlace2LibCompl(String place2LibCompl) {
		this.place2LibCompl = place2LibCompl;
	}

	public Integer getPlace2NbPlaces() {
		return this.place2NbPlaces;
	}

	public void setPlace2NbPlaces(Integer place2NbPlaces) {
		this.place2NbPlaces = place2NbPlaces;
	}

	public Boolean getPlace2SaisieDisponibilite() {
		return this.place2SaisieDisponibilite;
	}

	public void setPlace2SaisieDisponibilite(Boolean place2SaisieDisponibilite) {
		this.place2SaisieDisponibilite = place2SaisieDisponibilite;
	}

	public String getPlace3AgrementTrancheAgeKey() {
		return this.place3AgrementTrancheAgeKey;
	}

	public void setPlace3AgrementTrancheAgeKey(String place3AgrementTrancheAgeKey) {
		this.place3AgrementTrancheAgeKey = place3AgrementTrancheAgeKey;
	}

	public Integer getPlace3TrancheAgeKey() {
		return this.place3TrancheAgeKey;
	}

	public void setPlace3TrancheAgeKey(Integer place3TrancheAgeKey) {
		this.place3TrancheAgeKey = place3TrancheAgeKey;
	}

	public String getPlace3TrancheAge() {
		return this.place3TrancheAge;
	}

	public void setPlace3TrancheAge(String place3TrancheAge) {
		this.place3TrancheAge = place3TrancheAge;
	}

	public String getPlace3LibCompl() {
		return this.place3LibCompl;
	}

	public void setPlace3LibCompl(String place3LibCompl) {
		this.place3LibCompl = place3LibCompl;
	}

	public Integer getPlace3NbPlaces() {
		return this.place3NbPlaces;
	}

	public void setPlace3NbPlaces(Integer place3NbPlaces) {
		this.place3NbPlaces = place3NbPlaces;
	}

	public Boolean getPlace3SaisieDisponibilite() {
		return this.place3SaisieDisponibilite;
	}

	public void setPlace3SaisieDisponibilite(Boolean place3SaisieDisponibilite) {
		this.place3SaisieDisponibilite = place3SaisieDisponibilite;
	}

	public String getPlace4AgrementTrancheAgeKey() {
		return this.place4AgrementTrancheAgeKey;
	}

	public void setPlace4AgrementTrancheAgeKey(String place4AgrementTrancheAgeKey) {
		this.place4AgrementTrancheAgeKey = place4AgrementTrancheAgeKey;
	}

	public Integer getPlace4TrancheAgeKey() {
		return this.place4TrancheAgeKey;
	}

	public void setPlace4TrancheAgeKey(Integer place4TrancheAgeKey) {
		this.place4TrancheAgeKey = place4TrancheAgeKey;
	}

	public String getPlace4TrancheAge() {
		return this.place4TrancheAge;
	}

	public void setPlace4TrancheAge(String place4TrancheAge) {
		this.place4TrancheAge = place4TrancheAge;
	}

	public String getPlace4LibCompl() {
		return this.place4LibCompl;
	}

	public void setPlace4LibCompl(String place4LibCompl) {
		this.place4LibCompl = place4LibCompl;
	}

	public Integer getPlace4NbPlaces() {
		return this.place4NbPlaces;
	}

	public void setPlace4NbPlaces(Integer place4NbPlaces) {
		this.place4NbPlaces = place4NbPlaces;
	}

	public Boolean getPlace4SaisieDisponibilite() {
		return this.place4SaisieDisponibilite;
	}

	public void setPlace4SaisieDisponibilite(Boolean place4SaisieDisponibilite) {
		this.place4SaisieDisponibilite = place4SaisieDisponibilite;
	}

	public String getPlace5AgrementTrancheAgeKey() {
		return this.place5AgrementTrancheAgeKey;
	}

	public void setPlace5AgrementTrancheAgeKey(String place5AgrementTrancheAgeKey) {
		this.place5AgrementTrancheAgeKey = place5AgrementTrancheAgeKey;
	}

	public Integer getPlace5TrancheAgeKey() {
		return this.place5TrancheAgeKey;
	}

	public void setPlace5TrancheAgeKey(Integer place5TrancheAgeKey) {
		this.place5TrancheAgeKey = place5TrancheAgeKey;
	}

	public String getPlace5TrancheAge() {
		return this.place5TrancheAge;
	}

	public void setPlace5TrancheAge(String place5TrancheAge) {
		this.place5TrancheAge = place5TrancheAge;
	}

	public String getPlace5LibCompl() {
		return this.place5LibCompl;
	}

	public void setPlace5LibCompl(String place5LibCompl) {
		this.place5LibCompl = place5LibCompl;
	}

	public Integer getPlace5NbPlaces() {
		return this.place5NbPlaces;
	}

	public void setPlace5NbPlaces(Integer place5NbPlaces) {
		this.place5NbPlaces = place5NbPlaces;
	}

	public Boolean getPlace5SaisieDisponibilite() {
		return this.place5SaisieDisponibilite;
	}

	public void setPlace5SaisieDisponibilite(Boolean place5SaisieDisponibilite) {
		this.place5SaisieDisponibilite = place5SaisieDisponibilite;
	}

	public String getPlace6AgrementTrancheAgeKey() {
		return this.place6AgrementTrancheAgeKey;
	}

	public void setPlace6AgrementTrancheAgeKey(String place6AgrementTrancheAgeKey) {
		this.place6AgrementTrancheAgeKey = place6AgrementTrancheAgeKey;
	}

	public Integer getPlace6TrancheAgeKey() {
		return this.place6TrancheAgeKey;
	}

	public void setPlace6TrancheAgeKey(Integer place6TrancheAgeKey) {
		this.place6TrancheAgeKey = place6TrancheAgeKey;
	}

	public String getPlace6TrancheAge() {
		return this.place6TrancheAge;
	}

	public void setPlace6TrancheAge(String place6TrancheAge) {
		this.place6TrancheAge = place6TrancheAge;
	}

	public String getPlace6LibCompl() {
		return this.place6LibCompl;
	}

	public void setPlace6LibCompl(String place6LibCompl) {
		this.place6LibCompl = place6LibCompl;
	}

	public Integer getPlace6NbPlaces() {
		return this.place6NbPlaces;
	}

	public void setPlace6NbPlaces(Integer place6NbPlaces) {
		this.place6NbPlaces = place6NbPlaces;
	}

	public Boolean getPlace6SaisieDisponibilite() {
		return this.place6SaisieDisponibilite;
	}

	public void setPlace6SaisieDisponibilite(Boolean place6SaisieDisponibilite) {
		this.place6SaisieDisponibilite = place6SaisieDisponibilite;
	}

	public String getPlace7AgrementTrancheAgeKey() {
		return this.place7AgrementTrancheAgeKey;
	}

	public void setPlace7AgrementTrancheAgeKey(String place7AgrementTrancheAgeKey) {
		this.place7AgrementTrancheAgeKey = place7AgrementTrancheAgeKey;
	}

	public Integer getPlace7TrancheAgeKey() {
		return this.place7TrancheAgeKey;
	}

	public void setPlace7TrancheAgeKey(Integer place7TrancheAgeKey) {
		this.place7TrancheAgeKey = place7TrancheAgeKey;
	}

	public String getPlace7TrancheAge() {
		return this.place7TrancheAge;
	}

	public void setPlace7TrancheAge(String place7TrancheAge) {
		this.place7TrancheAge = place7TrancheAge;
	}

	public String getPlace7LibCompl() {
		return this.place7LibCompl;
	}

	public void setPlace7LibCompl(String place7LibCompl) {
		this.place7LibCompl = place7LibCompl;
	}

	public Integer getPlace7NbPlaces() {
		return this.place7NbPlaces;
	}

	public void setPlace7NbPlaces(Integer place7NbPlaces) {
		this.place7NbPlaces = place7NbPlaces;
	}

	public Boolean getPlace7SaisieDisponibilite() {
		return this.place7SaisieDisponibilite;
	}

	public void setPlace7SaisieDisponibilite(Boolean place7SaisieDisponibilite) {
		this.place7SaisieDisponibilite = place7SaisieDisponibilite;
	}

	public String getPlace8AgrementTrancheAgeKey() {
		return this.place8AgrementTrancheAgeKey;
	}

	public void setPlace8AgrementTrancheAgeKey(String place8AgrementTrancheAgeKey) {
		this.place8AgrementTrancheAgeKey = place8AgrementTrancheAgeKey;
	}

	public Integer getPlace8TrancheAgeKey() {
		return this.place8TrancheAgeKey;
	}

	public void setPlace8TrancheAgeKey(Integer place8TrancheAgeKey) {
		this.place8TrancheAgeKey = place8TrancheAgeKey;
	}

	public String getPlace8TrancheAge() {
		return this.place8TrancheAge;
	}

	public void setPlace8TrancheAge(String place8TrancheAge) {
		this.place8TrancheAge = place8TrancheAge;
	}

	public String getPlace8LibCompl() {
		return this.place8LibCompl;
	}

	public void setPlace8LibCompl(String place8LibCompl) {
		this.place8LibCompl = place8LibCompl;
	}

	public Integer getPlace8NbPlaces() {
		return this.place8NbPlaces;
	}

	public void setPlace8NbPlaces(Integer place8NbPlaces) {
		this.place8NbPlaces = place8NbPlaces;
	}

	public Boolean getPlace8SaisieDisponibilite() {
		return this.place8SaisieDisponibilite;
	}

	public void setPlace8SaisieDisponibilite(Boolean place8SaisieDisponibilite) {
		this.place8SaisieDisponibilite = place8SaisieDisponibilite;
	}

	public String getStatut() {
		return this.statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public Boolean getAutorisationActivation() {
		return this.autorisationActivation;
	}

	public void setAutorisationActivation(Boolean autorisationActivation) {
		this.autorisationActivation = autorisationActivation;
	}

	public BigDecimal getLatitude() {
		return this.latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return this.longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getLatitudeMam() {
		return this.latitudeMam;
	}

	public void setLatitudeMam(BigDecimal latitudeMam) {
		this.latitudeMam = latitudeMam;
	}

	public BigDecimal getLongitudeMam() {
		return this.longitudeMam;
	}

	public void setLongitudeMam(BigDecimal longitudeMam) {
		this.longitudeMam = longitudeMam;
	}

	public String getIdUa() {
		return this.idUa;
	}

	public void setIdUa(String idUa) {
		this.idUa = idUa;
	}

	public Boolean getExerceDomicile() {
		return this.exerceDomicile;
	}

	public void setExerceDomicile(Boolean exerceDomicile) {
		this.exerceDomicile = exerceDomicile;
	}

	public Boolean getAideCaf() {
		return this.aideCaf;
	}

	public void setAideCaf(Boolean aideCaf) {
		this.aideCaf = aideCaf;
	}

	public String getCodeInsee() {
		return this.codeInsee;
	}

	public void setCodeInsee(String codeInsee) {
		this.codeInsee = codeInsee;
	}

	public Integer getIdRam() {
		return this.idRam;
	}

	public void setIdRam(Integer idRam) {
		this.idRam = idRam;
	}

	public Integer getIdRamMam() {
		return this.idRamMam;
	}

	public void setIdRamMam(Integer idRamMam) {
		this.idRamMam = idRamMam;
	}

	public String getComplementAdresse() {
		return this.complementAdresse;
	}

	public void setComplementAdresse(String complementAdresse) {
		this.complementAdresse = complementAdresse;
	}
	
	public String getCodeInseeMam() {
		return this.codeInseeMam;
	}

	public void setCodeInseeMam(String codeInseeMam) {
		this.codeInseeMam = codeInseeMam;
	}	
	
	public String getIdQuartierDom() {
    return idQuartierDom;
  }

  public void setIdQuartierDom(String idQuartierDom) {
    this.idQuartierDom = idQuartierDom;
  }

  public String getIdQuartierMam() {
    return idQuartierMam;
  }

  public void setIdQuartierMam(String idQuartierMam) {
    this.idQuartierMam = idQuartierMam;
  }

  public String getIdMicroQuartierDom() {
		return idMicroQuartierDom;
	}

	public void setIdMicroQuartierDom(String idMicroQuartierDom) {
		this.idMicroQuartierDom = idMicroQuartierDom;
	}

	public String getIdMicroQuartierMam() {
		return idMicroQuartierMam;
	}

	public void setIdMicroQuartierMam(String idMicroQuartierMam) {
		this.idMicroQuartierMam = idMicroQuartierMam;
	}	
	
 @Override
 public String toString() {
  StringBuilder builder = new StringBuilder();
  builder.append("<ul><li>JRowId : ").append(JRowId).append("</li><li>civiliteAssmat : ").append(civiliteAssmat).append("</li><li>nomAssmat : ").append(nomAssmat).append("</li><li>prenomAssmat : ").append(prenomAssmat).append("</li><li>dateNaissAssmat : ").append(dateNaissAssmat).append("</li><li>numDossierAssmat : ").append(numDossierAssmat).append("</li><li>telPrincipal : ").append(telPrincipal).append("</li><li>telPortable : ").append(telPortable).append("</li><li>emailAssmat : ")
    .append(emailAssmat).append("</li><li>datePremierAgrement : ").append(datePremierAgrement).append("</li><li>formationInitiale : ").append(formationInitiale).append("</li><li>dateDernierRenouvellement : ").append(dateDernierRenouvellement).append("</li><li>dateProchainRenouvellement : ").append(dateProchainRenouvellement).append("</li><li>dateDernierRenouvellementMam : ").append(dateDernierRenouvellementMam).append("</li><li>dateProchainRenouvellementMam : ")
    .append(dateProchainRenouvellementMam).append("</li><li>adresseDomicile : ").append(adresseDomicile).append("</li><li>cpDomicile : ").append(cpDomicile).append("</li><li>communeDomicile : ").append(communeDomicile).append("</li><li>exerceMam : ").append(exerceMam).append("</li><li>nomMam : ").append(nomMam).append("</li><li>adresseMam : ").append(adresseMam).append("</li><li>cpMam : ").append(cpMam).append("</li><li>communeMam : ").append(communeMam)
    .append("</li><li>enActivite : ").append(enActivite).append("</li><li>place1AgrementTrancheAgeKey : ").append(place1AgrementTrancheAgeKey).append("</li><li>place1TrancheAgeKey : ").append(place1TrancheAgeKey).append("</li><li>place1TrancheAge : ").append(place1TrancheAge).append("</li><li>place1LibCompl : ").append(place1LibCompl).append("</li><li>place1NbPlaces : ").append(place1NbPlaces).append("</li><li>place1SaisieDisponibilite : ").append(place1SaisieDisponibilite)
    .append("</li><li>place2AgrementTrancheAgeKey : ").append(place2AgrementTrancheAgeKey).append("</li><li>place2TrancheAgeKey : ").append(place2TrancheAgeKey).append("</li><li>place2TrancheAge : ").append(place2TrancheAge).append("</li><li>place2LibCompl : ").append(place2LibCompl).append("</li><li>place2NbPlaces : ").append(place2NbPlaces).append("</li><li>place2SaisieDisponibilite : ").append(place2SaisieDisponibilite).append("</li><li>place3AgrementTrancheAgeKey : ")
    .append(place3AgrementTrancheAgeKey).append("</li><li>place3TrancheAgeKey : ").append(place3TrancheAgeKey).append("</li><li>place3TrancheAge : ").append(place3TrancheAge).append("</li><li>place3LibCompl : ").append(place3LibCompl).append("</li><li>place3NbPlaces : ").append(place3NbPlaces).append("</li><li>place3SaisieDisponibilite : ").append(place3SaisieDisponibilite).append("</li><li>place4AgrementTrancheAgeKey : ").append(place4AgrementTrancheAgeKey)
    .append("</li><li>place4TrancheAgeKey : ").append(place4TrancheAgeKey).append("</li><li>place4TrancheAge : ").append(place4TrancheAge).append("</li><li>place4LibCompl : ").append(place4LibCompl).append("</li><li>place4NbPlaces : ").append(place4NbPlaces).append("</li><li>place4SaisieDisponibilite : ").append(place4SaisieDisponibilite).append("</li><li>place5AgrementTrancheAgeKey : ").append(place5AgrementTrancheAgeKey).append("</li><li>place5TrancheAgeKey : ")
    .append(place5TrancheAgeKey).append("</li><li>place5TrancheAge : ").append(place5TrancheAge).append("</li><li>place5LibCompl : ").append(place5LibCompl).append("</li><li>place5NbPlaces : ").append(place5NbPlaces).append("</li><li>place5SaisieDisponibilite : ").append(place5SaisieDisponibilite).append("</li><li>place6AgrementTrancheAgeKey : ").append(place6AgrementTrancheAgeKey).append("</li><li>place6TrancheAgeKey : ").append(place6TrancheAgeKey)
    .append("</li><li>place6TrancheAge : ").append(place6TrancheAge).append("</li><li>place6LibCompl : ").append(place6LibCompl).append("</li><li>place6NbPlaces : ").append(place6NbPlaces).append("</li><li>place6SaisieDisponibilite : ").append(place6SaisieDisponibilite).append("</li><li>place7AgrementTrancheAgeKey : ").append(place7AgrementTrancheAgeKey).append("</li><li>place7TrancheAgeKey : ").append(place7TrancheAgeKey).append("</li><li>place7TrancheAge : ")
    .append(place7TrancheAge).append("</li><li>place7LibCompl : ").append(place7LibCompl).append("</li><li>place7NbPlaces : ").append(place7NbPlaces).append("</li><li>place7SaisieDisponibilite : ").append(place7SaisieDisponibilite).append("</li><li>place8AgrementTrancheAgeKey : ").append(place8AgrementTrancheAgeKey).append("</li><li>place8TrancheAgeKey : ").append(place8TrancheAgeKey).append("</li><li>place8TrancheAge : ").append(place8TrancheAge)
    .append("</li><li>place8LibCompl : ").append(place8LibCompl).append("</li><li>place8NbPlaces : ").append(place8NbPlaces).append("</li><li>place8SaisieDisponibilite : ").append(place8SaisieDisponibilite).append("</li><li>statut : ").append(statut).append("</li><li>autorisationActivation : ").append(autorisationActivation).append("</li><li>latitude : ").append(latitude).append("</li><li>longitude : ").append(longitude).append("</li><li>latitudeMam : ").append(latitudeMam)
    .append("</li><li>longitudeMam : ").append(longitudeMam).append("</li><li>idUa : ").append(idUa).append("</li><li>exerceDomicile : ").append(exerceDomicile).append("</li><li>aideCaf : ").append(aideCaf).append("</li></ul>");
  return builder.toString();
 }

}
