package br.unb.cic.laico.analysis.utilitybased.model;

import java.io.Serializable;

public class UtilityPoint implements Serializable, Comparable<UtilityPoint> {

	private static final long serialVersionUID = 1L;

	private double bid;
	private double utility;
	private double normalizedUtility;
	private double availability;
	
	private FutureEstimate futureEstimateForFixedBid;
	private FutureEstimate futureEstimateForVariableBid;
	private FutureEstimate futureEstimateForHourlyVariableBid;
	
	private RegressionTrend exponentialTrend;
	private RegressionTrend logarithmTrend;
	private RegressionTrend polynomialTrend;
	private RegressionTrend powerTrend;

	public UtilityPoint() {
	}

	public int compareTo(UtilityPoint other) {
		if (this.utility < other.getUtility()) {
			return -1;
		}
		if (this.utility > other.getUtility()) {
			return 1;
		}
		return 0;
	}

	public double getBid() {
		return bid;
	}

	public void setBid(double bid) {
		this.bid = bid;
	}

	public double getUtility() {
		return utility;
	}

	public void setUtility(double utility) {
		this.utility = utility;
	}

	public double getNormalizedUtility() {
		return normalizedUtility;
	}

	public void setNormalizedUtility(double normalizedUtility) {
		this.normalizedUtility = normalizedUtility;
	}

	public double getAvailability() {
		return availability;
	}

	public void setAvailability(double availability) {
		this.availability = availability;
	}

	public FutureEstimate getFutureEstimateForFixedBid() {
		return futureEstimateForFixedBid;
	}

	public void setFutureEstimateForFixedBid(FutureEstimate futureEstimateForFixedBid) {
		this.futureEstimateForFixedBid = futureEstimateForFixedBid;
	}

	public FutureEstimate getFutureEstimateForVariableBid() {
		return futureEstimateForVariableBid;
	}

	public void setFutureEstimateForVariableBid(FutureEstimate futureEstimateForVariableBid) {
		this.futureEstimateForVariableBid = futureEstimateForVariableBid;
	}

	public FutureEstimate getFutureEstimateForHourlyVariableBid() {
		return futureEstimateForHourlyVariableBid;
	}

	public void setFutureEstimateForHourlyVariableBid(FutureEstimate futureEstimateForHourlyVariableBid) {
		this.futureEstimateForHourlyVariableBid = futureEstimateForHourlyVariableBid;
	}

	public RegressionTrend getExponentialTrend() {
		return exponentialTrend;
	}

	public void setExponentialTrend(RegressionTrend exponentialTrend) {
		this.exponentialTrend = exponentialTrend;
	}

	public RegressionTrend getLogarithmTrend() {
		return logarithmTrend;
	}

	public void setLogarithmTrend(RegressionTrend logarithmTrend) {
		this.logarithmTrend = logarithmTrend;
	}

	public RegressionTrend getPolynomialTrend() {
		return polynomialTrend;
	}

	public void setPolynomialTrend(RegressionTrend polynomialTrend) {
		this.polynomialTrend = polynomialTrend;
	}

	public RegressionTrend getPowerTrend() {
		return powerTrend;
	}

	public void setPowerTrend(RegressionTrend powerTrend) {
		this.powerTrend = powerTrend;
	}
	
	public RegressionTrend getRegressionTrend(int trendType) {
		switch (trendType) {
		case RegressionTrend.EXPONENTIAL_TREND:
			return exponentialTrend;
		case RegressionTrend.LOGARITHM_TREND:
			return logarithmTrend;
		case RegressionTrend.POLYNOMIAL_TREND:
			return polynomialTrend;
		case RegressionTrend.POWER_TREND:
			return powerTrend;
		default:
			throw new IllegalArgumentException("Invalid regression type.");
		}
	}

	public int getTrendTypeWithBestFit() {
		
		if (exponentialTrend == null || logarithmTrend == null
				|| polynomialTrend == null || powerTrend == null) {
			return -1;
		}
		
		double[] fitArray = new double[4];
		fitArray[RegressionTrend.EXPONENTIAL_TREND] = exponentialTrend.getStdErrOfEstimate();
		fitArray[RegressionTrend.LOGARITHM_TREND] = logarithmTrend.getStdErrOfEstimate();
		fitArray[RegressionTrend.POLYNOMIAL_TREND] = polynomialTrend.getStdErrOfEstimate();
		fitArray[RegressionTrend.POWER_TREND] = powerTrend.getStdErrOfEstimate();
		
		double bestFit = Double.MAX_VALUE;
		int indexBestFit = -1;
		for (int i=0; i<fitArray.length; i++) {
			if (bestFit > fitArray[i]) {
				bestFit = fitArray[i];
				indexBestFit = i;
			}
		}
		return indexBestFit;
	}
}
