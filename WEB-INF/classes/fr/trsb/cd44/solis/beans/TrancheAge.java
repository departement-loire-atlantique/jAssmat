package fr.trsb.cd44.solis.beans;

/**
 * TrancheAge generated by hbm2java
 */
public class TrancheAge implements java.io.Serializable {

	private int trancheAgeKey;
	private Integer ageMin;
	private Integer ageMax;

	public TrancheAge() {
	}

	public TrancheAge(int trancheAgeKey) {
		this.trancheAgeKey = trancheAgeKey;
	}

	public TrancheAge(int trancheAgeKey, Integer ageMin, Integer ageMax) {
		this.trancheAgeKey = trancheAgeKey;
		this.ageMin = ageMin;
		this.ageMax = ageMax;
	}

	public int getTrancheAgeKey() {
		return this.trancheAgeKey;
	}

	public void setTrancheAgeKey(int trancheAgeKey) {
		this.trancheAgeKey = trancheAgeKey;
	}

	public Integer getAgeMin() {
		return this.ageMin;
	}

	public void setAgeMin(Integer ageMin) {
		this.ageMin = ageMin;
	}

	public Integer getAgeMax() {
		return this.ageMax;
	}

	public void setAgeMax(Integer ageMax) {
		this.ageMax = ageMax;
	}

}
