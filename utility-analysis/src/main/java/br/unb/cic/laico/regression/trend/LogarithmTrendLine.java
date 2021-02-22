package br.unb.cic.laico.regression.trend;

import br.unb.cic.laico.regression.trend.base.OLSTrendLine;

public class LogarithmTrendLine extends OLSTrendLine {

	@Override
	protected double[] xVector(double x) {
		return new double[] { 1, Math.log(x) };
	}

	@Override
	protected boolean logY() {
		return false;
	}
}
