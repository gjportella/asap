package br.unb.cic.laico.regression.trend.base;

/**
 * Original code from:
 *
 *   https://stackoverflow.com/questions/17592139/trend-lines-regression-curve-fitting-java-library
 *
 */
public interface TrendLine {

	void setValues(double[] y, double[] x); // y ~ f(x)

	double predict(double x); // get a predicted y for a given x
	
	double getRSquared(); // R2 statistical relevance
	
	double getStandardErrorOfEstimate(); // for function fitting
	
	double[] getCoefs(); // get coefficients
}
