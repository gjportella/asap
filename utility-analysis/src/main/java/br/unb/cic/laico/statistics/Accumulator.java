package br.unb.cic.laico.statistics;

import java.io.Serializable;

public class Accumulator implements Serializable {

	private static final long serialVersionUID = 1L;

	private int n; 			// number of data values
	private double mean;	// sample mean
	private double var;		// sample variance * (n-1)
	private double min;		// minimum value
	private double max;		// maximum value

	public Accumulator() {
		this.n = 0;
		this.mean = 0D;
		this.var = 0D;
		this.min = Double.MAX_VALUE;
		this.max = Double.MIN_VALUE;
	}

	public int getN() {
		return n;
	}

	public double getMean() {
		if (n == 0) {
			return Double.NaN;
		}
		return mean;
	}

	public double getMin() {
		if (n == 0) {
			return Double.NaN;
		}
		return min;
	}

	public double getMax() {
		if (n == 0) {
			return Double.NaN;
		}
		return max;
	}

	public void addValue(double value) {
		if (!Double.isNaN(value)) {
			n++;
			double delta = value - mean;
			mean += delta / (double) n;
			var += ((double) (n - 1) / (double) n) * delta * delta;
			if (value < min) {
				min = value;
			}
			if (value > max) {
				max = value;
			}
		}
	}

	public double getVariance() {
		if (n == 0) {
			return Double.NaN;
		}
		return var / (double) n;
	}

	public double getStddev() {
		if (n == 0) {
			return Double.NaN;
		}
		double variance = this.getVariance();
		return Math.sqrt(variance);
	}
}
