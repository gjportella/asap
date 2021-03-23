package br.unb.cic.laico.regression.trend.base;

import java.util.Arrays;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 * Original code from:
 *
 *   https://stackoverflow.com/questions/17592139/trend-lines-regression-curve-fitting-java-library
 *
 */
public abstract class OLSTrendLine implements TrendLine {

	private double[] x;
	private double[] y;

	private RealMatrix coef = null; // will hold prediction coefs once we get values

	public OLSTrendLine() {
	}

	protected abstract double[] xVector(double x); // create vector of values from x

	protected abstract boolean logY(); // set true to predict log of y (note: y must be positive)

	public void setValues(double[] y, double[] x) {

		if (x.length != y.length) {
			throw new IllegalArgumentException(
					String.format("The numbers of y and x values must be equal (%d != %d)", y.length, x.length));
		}

		this.x = x;
		this.y = y;

		double[][] xData = new double[x.length][];
		for (int i = 0; i < x.length; i++) {
			// the implementation determines how to produce a vector of predictors from a
			// single x
			xData[i] = xVector(x[i]);
		}

		if (logY()) { // in some models we are predicting ln y, so we replace each y with ln y
			y = Arrays.copyOf(y, y.length); // user might not be finished with the array we were given
			for (int i = 0; i < x.length; i++) {
				y[i] = Math.log(y[i]);
			}
		}

		OLSMultipleLinearRegression ols = new OLSMultipleLinearRegression();
		ols.setNoIntercept(true); // let the implementation include a constant in xVector if desired
		ols.newSampleData(y, xData); // provide the data to the model
		coef = MatrixUtils.createColumnRealMatrix(ols.estimateRegressionParameters()); // get our coefs
	}

	public double predict(double x) {

		double yhat = coef.preMultiply(xVector(x))[0]; // apply coefs to xVector
		if (logY()) {
			yhat = (Math.exp(yhat)); // if we predicted ln y, we still need to get y
		}
		return yhat;
	}

	public double getRSquared() {

		double[] predictedValues = new double[x.length];
		double residualSumOfSquares = 0d;
		DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();

		for (int i = 0; i < x.length; i++) {
			predictedValues[i] = predict(x[i]);
			residualSumOfSquares += Math.pow((predictedValues[i] - y[i]), 2);
			descriptiveStatistics.addValue(y[i]);
		}

		final double avgActualValues = descriptiveStatistics.getMean();
		double totalSumOfSquares = 0d;
		for (int i = 0; i < x.length; i++) {
			totalSumOfSquares += Math.pow((predictedValues[i] - avgActualValues), 2);
		}
		return 1.0 - (residualSumOfSquares / totalSumOfSquares);
	}

	public double getStandardErrorOfEstimate() {

		double[] predictedValues = new double[x.length];
		double residualSumOfSquares = 0d;

		for (int i = 0; i < x.length; i++) {
			predictedValues[i] = predict(x[i]);
			residualSumOfSquares += Math.pow((predictedValues[i] - y[i]), 2);
		}

		return Math.pow((residualSumOfSquares / x.length), 0.5);
	}
	
	public double[] getCoefs() {
		
		return coef.getColumn(0);
	}
}
