package br.unb.cic.laico.analysis.utility.config;

import java.io.Serializable;
import java.util.Date;

public class UtilityConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date creationDate;
	private String instanceType;
	private String availabilityZoneFilter;
	private String inputCsvPath;
	private String complementaryLabel;
	
	// Precisao da estimativa de lance do usuario (parametro sigma do algoritmo)
	private double estimationAccuracy;

	// Fator multiplicador do limite inferior do preco on demand (parametro eta do algoritmo)
	private double odPriceFactorLowerLimit;

	// Fator multiplicador do limite superior do preco on demand (parametro theta do algoritmo)
	private double odPriceFactorUpperLimit;

	public UtilityConfiguration() {
		this.creationDate = new Date(System.currentTimeMillis());
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

	public String getAvailabilityZoneFilter() {
		return availabilityZoneFilter;
	}

	public void setAvailabilityZoneFilter(String availabilityZoneFilter) {
		this.availabilityZoneFilter = availabilityZoneFilter;
	}

	public String getInputCsvPath() {
		return inputCsvPath;
	}

	public void setInputCsvPath(String inputCsvPath) {
		this.inputCsvPath = inputCsvPath;
	}

	public String getComplementaryLabel() {
		return complementaryLabel;
	}

	public void setComplementaryLabel(String complementaryLabel) {
		this.complementaryLabel = complementaryLabel;
	}

	public double getEstimationAccuracy() {
		return estimationAccuracy;
	}

	public void setEstimationAccuracy(double estimationAccuracy) {
		this.estimationAccuracy = estimationAccuracy;
	}

	public double getOdPriceFactorLowerLimit() {
		return odPriceFactorLowerLimit;
	}

	public void setOdPriceFactorLowerLimit(double odPriceFactorLowerLimit) {
		this.odPriceFactorLowerLimit = odPriceFactorLowerLimit;
	}

	public double getOdPriceFactorUpperLimit() {
		return odPriceFactorUpperLimit;
	}

	public void setOdPriceFactorUpperLimit(double odPriceFactorUpperLimit) {
		this.odPriceFactorUpperLimit = odPriceFactorUpperLimit;
	}

	public String getLabel() {
		StringBuilder label = new StringBuilder();
		label.append("Utility-");
		label.append("S").append(Double.toString(this.getEstimationAccuracy()));
		label.append("E").append(Double.toString(this.getOdPriceFactorLowerLimit()));
		label.append("T").append(Double.toString(this.getOdPriceFactorUpperLimit()));
		return label.toString();
	}
}
