package br.unb.cic.laico.analysis.utilitybased.model;

import java.io.Serializable;

public class RegressionTrend implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int EXPONENTIAL_TREND = 0;
	public static final int LOGARITHM_TREND = 1;
	public static final int POLYNOMIAL_TREND = 2;
	public static final int POWER_TREND = 3;
	
	private int numberOfPoints;
	private double[] coefficients;
	private double rSquared;
	private double stdErrOfEstimate;

	private long currentTime;
	private double currentPrice;
	private long nextTime;
	private double predictedPriceAtNextTime;

	public RegressionTrend() {
	}

	public int getNumberOfPoints() {
		return numberOfPoints;
	}

	public void setNumberOfPoints(int numberOfPoints) {
		this.numberOfPoints = numberOfPoints;
	}

	public double[] getCoefficients() {
		return coefficients;
	}

	public void setCoefficients(double[] coefficients) {
		this.coefficients = coefficients;
	}

	public double getRSquared() {
		return rSquared;
	}

	public void setRSquared(double rSquared) {
		this.rSquared = rSquared;
	}

	public double getStdErrOfEstimate() {
		return stdErrOfEstimate;
	}

	public void setStdErrOfEstimate(double stdErrOfEstimate) {
		this.stdErrOfEstimate = stdErrOfEstimate;
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public long getNextTime() {
		return nextTime;
	}

	public void setNextTime(long nextTime) {
		this.nextTime = nextTime;
	}

	public double getPredictedPriceAtNextTime() {
		return predictedPriceAtNextTime;
	}

	public void setPredictedPriceAtNextTime(double predictedPriceAtNextTime) {
		this.predictedPriceAtNextTime = predictedPriceAtNextTime;
	}
}
