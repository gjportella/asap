package br.unb.cic.laico.regression.trend;

import br.unb.cic.laico.regression.trend.base.OLSTrendLine;

public class PowerTrendLine extends OLSTrendLine {

	@Override
	protected double[] xVector(double x) {
		return new double[] { 1, Math.log(x) };
	}

	@Override
	protected boolean logY() {
		return true;
	}
	
	@Override
	public double[] getCoefs() {
		double[] c = super.getCoefs();
		return new double[] { Math.exp(c[0]), c[1] };
	}
}
