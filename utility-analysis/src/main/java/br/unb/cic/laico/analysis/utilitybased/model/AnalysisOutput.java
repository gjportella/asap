package br.unb.cic.laico.analysis.utilitybased.model;

import java.io.Serializable;

public class AnalysisOutput implements Serializable {

	private static final long serialVersionUID = 1L;

	private long startTime;
	private String instanceType;
	private String availabilityZoneFilter;
	private String analysisLabel;
	private int occurrencesRead;
	private int occurrencesRemoved;

	private double sigma;
	private double theta;
	private double onDemandPrice;
	private double averagePrice;
	private double priceVariance;
	private double averageBid;
	private double averageAvailability;
	
	private double fixedBidAverageAvailability;
	private double fixedBidAverageBid;
	private double fixedBidAverageFirstChunk;
	private int fixedBidDurabilityEqWindow;	
	
	private double hourlyBidAverageAvailability;
	private double hourlyBidAverageBid;
	private double hourlyBidAverageFirstChunk;
	private int hourlyBidDurabilityEqWindow;

	private double variableBidAverageAvailability;
	private double variableBidAverageBid;
	private double variableBidAverageFirstChunk;
	private int variableBidDurabilityEqWindow;

	private double utilityFunctionAverageSpeedup;
	private long totalSpeedup;

	public AnalysisOutput() {

	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
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

	public String getAnalysisLabel() {
		return analysisLabel;
	}

	public void setAnalysisLabel(String analysisLabel) {
		this.analysisLabel = analysisLabel;
	}

	public int getOccurrencesRead() {
		return occurrencesRead;
	}

	public void setOccurrencesRead(int occurrencesRead) {
		this.occurrencesRead = occurrencesRead;
	}

	public int getOccurrencesRemoved() {
		return occurrencesRemoved;
	}

	public void setOccurrencesRemoved(int occurrencesRemoved) {
		this.occurrencesRemoved = occurrencesRemoved;
	}

	public double getSigma() {
		return sigma;
	}

	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	public double getTheta() {
		return theta;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}

	public double getOnDemandPrice() {
		return onDemandPrice;
	}

	public void setOnDemandPrice(double onDemandPrice) {
		this.onDemandPrice = onDemandPrice;
	}

	public double getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(double averagePrice) {
		this.averagePrice = averagePrice;
	}

	public double getPriceVariance() {
		return priceVariance;
	}

	public void setPriceVariance(double priceVariance) {
		this.priceVariance = priceVariance;
	}

	public double getAverageBid() {
		return averageBid;
	}

	public void setAverageBid(double averageBid) {
		this.averageBid = averageBid;
	}

	public double getAverageAvailability() {
		return averageAvailability;
	}

	public void setAverageAvailability(double averageAvailability) {
		this.averageAvailability = averageAvailability;
	}

	public double getFixedBidAverageAvailability() {
		return fixedBidAverageAvailability;
	}

	public void setFixedBidAverageAvailability(double fixedBidAverageAvailability) {
		this.fixedBidAverageAvailability = fixedBidAverageAvailability;
	}

	public double getFixedBidAverageBid() {
		return fixedBidAverageBid;
	}

	public void setFixedBidAverageBid(double fixedBidAverageBid) {
		this.fixedBidAverageBid = fixedBidAverageBid;
	}

	public double getFixedBidAverageFirstChunk() {
		return fixedBidAverageFirstChunk;
	}

	public void setFixedBidAverageFirstChunk(double fixedBidAverageFirstChunk) {
		this.fixedBidAverageFirstChunk = fixedBidAverageFirstChunk;
	}

	public int getFixedBidDurabilityEqWindow() {
		return fixedBidDurabilityEqWindow;
	}

	public void setFixedBidDurabilityEqWindow(int fixedBidDurabilityEqWindow) {
		this.fixedBidDurabilityEqWindow = fixedBidDurabilityEqWindow;
	}

	public double getHourlyBidAverageAvailability() {
		return hourlyBidAverageAvailability;
	}

	public void setHourlyBidAverageAvailability(double hourlyBidAverageAvailability) {
		this.hourlyBidAverageAvailability = hourlyBidAverageAvailability;
	}

	public double getHourlyBidAverageBid() {
		return hourlyBidAverageBid;
	}

	public void setHourlyBidAverageBid(double hourlyBidAverageBid) {
		this.hourlyBidAverageBid = hourlyBidAverageBid;
	}

	public double getHourlyBidAverageFirstChunk() {
		return hourlyBidAverageFirstChunk;
	}

	public void setHourlyBidAverageFirstChunk(double hourlyBidAverageFirstChunk) {
		this.hourlyBidAverageFirstChunk = hourlyBidAverageFirstChunk;
	}

	public int getHourlyBidDurabilityEqWindow() {
		return hourlyBidDurabilityEqWindow;
	}

	public void setHourlyBidDurabilityEqWindow(int hourlyBidDurabilityEqWindow) {
		this.hourlyBidDurabilityEqWindow = hourlyBidDurabilityEqWindow;
	}

	public double getVariableBidAverageAvailability() {
		return variableBidAverageAvailability;
	}

	public void setVariableBidAverageAvailability(double variableBidAverageAvailability) {
		this.variableBidAverageAvailability = variableBidAverageAvailability;
	}

	public double getVariableBidAverageBid() {
		return variableBidAverageBid;
	}

	public void setVariableBidAverageBid(double variableBidAverageBid) {
		this.variableBidAverageBid = variableBidAverageBid;
	}

	public double getVariableBidAverageFirstChunk() {
		return variableBidAverageFirstChunk;
	}

	public void setVariableBidAverageFirstChunk(double variableBidAverageFirstChunk) {
		this.variableBidAverageFirstChunk = variableBidAverageFirstChunk;
	}

	public int getVariableBidDurabilityEqWindow() {
		return variableBidDurabilityEqWindow;
	}

	public void setVariableBidDurabilityEqWindow(int variableBidDurabilityEqWindow) {
		this.variableBidDurabilityEqWindow = variableBidDurabilityEqWindow;
	}

	public double getUtilityFunctionAverageSpeedup() {
		return utilityFunctionAverageSpeedup;
	}

	public void setUtilityFunctionAverageSpeedup(double utilityFunctionAverageSpeedup) {
		this.utilityFunctionAverageSpeedup = utilityFunctionAverageSpeedup;
	}

	public long getTotalSpeedup() {
		return totalSpeedup;
	}

	public void setTotalSpeedup(long totalSpeedup) {
		this.totalSpeedup = totalSpeedup;
	}
}