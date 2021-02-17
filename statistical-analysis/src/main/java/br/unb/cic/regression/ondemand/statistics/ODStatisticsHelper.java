package br.unb.cic.regression.ondemand.statistics;

import java.util.List;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import br.unb.cic.conversion.csv.CsvWrapperWriter;
import br.unb.cic.regression.spot.statistics.StatisticsDistributionTables;

public class ODStatisticsHelper {

	private StatisticsDistributionTables tables;
	
	private String provider;
	private String experiment;
	private List<String> headerLabels;
	private List<Double> prices;

	public ODStatisticsHelper(String provider, String experiment, List<String> headerLabels, List<Double> prices) {
		this.tables = StatisticsDistributionTables.getInstance();
		this.provider = provider;
		this.experiment = experiment;
		this.headerLabels = headerLabels;
		this.prices = prices;
	}

	public void writeCsvData(OLSMultipleLinearRegression regression,
			String inputCsvPath, String csvDelimitter) throws Exception {
		
		double[] betas = regression.estimateRegressionParameters();
		double[] stderrs = regression.estimateRegressionParametersStandardErrors();
		
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(
				inputCsvPath + "-statistics.txt", csvDelimitter);
		
		try { 
			csvWriter.openFile();
			
			csvWriter.writeLine("Experiment: " + experiment);
			csvWriter.writeLine("Provider: " + provider);
			csvWriter.writeLine("Regression equation:");
			String equation = writeEquation(headerLabels, betas);
			csvWriter.writeLine(equation);

			int n = prices.size();
			int k = betas.length - 1;
			
			csvWriter.writeLine("Occurrences (n): " + Integer.toString(n));
			csvWriter.writeLine("Parameters (k): " + Integer.toString(k));
			csvWriter.writeLine("RSquare (R2): " + Double.toString(regression.calculateRSquared()));
			csvWriter.writeLine("Confidence interval of coefficients \u03B8 (\u03B1 = 0.1):");
			
			for (int i=0; i<betas.length; i++) {
				
				double beta = betas[i];
				double stderr = stderrs[i];
				
				double[] interval = calculateConfidenceInterval(
						beta, stderr, 0.1d, prices.size());
				csvWriter.writeLine("\u03B8" + Integer.toString(i) + " = [" + Double.toString(interval[0])
						+ ";" + Double.toString(interval[1]) + "]");
			}
			
			double dfR = (double) k;
			double dfE = ((double) (n - (k + 1d)));
			csvWriter.writeLine("F-Test (table reference value, \u03B1 = 0.1): " +
					Double.toString(tables.getFDistributionValue(0.1d, (int) dfR, (int) dfE)));
			
			double SST = regression.calculateTotalSumOfSquares();
			double SSE = regression.calculateResidualSumOfSquares();
			double SSR = SST - SSE;
			
			double MSR = SSR / dfR;
			double MSE = SSE / dfE;
			double fCalculated = MSR / MSE;
			
			csvWriter.writeLine("F-Test (calculated value): " + Double.toString(fCalculated));

		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
	}
	
	private String writeEquation(List<String> labels, double[] betas) {
		
		String equation = labels.get(1) + " = (" + Double.toString(betas[0]) + ")";
		for (int i=1; i<betas.length; i++) {
			equation = equation + " + (" + Double.toString(betas[i]) + ") * " + labels.get(i+1);
		}
		return equation;
	}
	
	private double[] calculateConfidenceInterval(double value, double stdDev,
			double alpha, int df) throws Exception {
		
		double tValue = tables.getTDistributionValue(alpha, df);
		double minorValue = value - (tValue * stdDev);
		double greaterValue = value + (tValue * stdDev);
		return new double[] {minorValue, greaterValue};
	}
}
