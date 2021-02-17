package br.unb.cic.regression.spot.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import br.unb.cic.conversion.csv.CsvWrapperWriter;

public class StatisticsHelper {
	
	private StatisticsDistributionTables tables;
	
	private int outliersRemoved;
	private double priceMeanWithOutliers;
	private double priceMeanWithoutOutliers;
	
	public StatisticsHelper() {
		this.tables = StatisticsDistributionTables.getInstance();
		this.outliersRemoved = 0;
		this.priceMeanWithOutliers = 0d;
		this.priceMeanWithoutOutliers = 0d;
	}

	public void removeOutliers(List<Date> dateList, List<Double> priceList,
			List<Date> dateListOut, List<Double> priceListOut,
			double weight) {
		
		List<Double> orderedPriceList = new ArrayList<Double>(priceList);
		Collections.sort(orderedPriceList);
		
		int q1Index = (int) Math.ceil(orderedPriceList.size() / 4);
		int q2Index = (int) Math.ceil(orderedPriceList.size() / 2);
		int q3Index = q1Index + q2Index;
		Double q1 = orderedPriceList.get(q1Index);
		Double q3 = orderedPriceList.get(q3Index);
		double range = (1d / weight) * (q3.doubleValue() - q1.doubleValue());
		
		double sumOfPricesWithOutliers = 0d;
		double sumOfPricesWithoutOutliers = 0d;
		for (int i=0; i<priceList.size(); i++) {
			
			Date date = dateList.get(i);
			Double price = priceList.get(i);
			if (price.doubleValue() >= (q1.doubleValue() - range) &&
					price.doubleValue() <= (q3.doubleValue() + range)) {

				dateListOut.add(date);
				priceListOut.add(price);
				sumOfPricesWithoutOutliers = sumOfPricesWithoutOutliers + price.doubleValue();
			}
			sumOfPricesWithOutliers = sumOfPricesWithOutliers + price.doubleValue();
		}
		
		outliersRemoved = priceList.size() - priceListOut.size();
		priceMeanWithOutliers = sumOfPricesWithOutliers / ((double) priceList.size());
		priceMeanWithoutOutliers = sumOfPricesWithoutOutliers / ((double) (priceList.size() - outliersRemoved));
	}

	public double[] calculateConfidenceInterval(double value, double stdDev,
			double alpha, int df) throws Exception {
		
		double tValue = tables.getTDistributionValue(alpha, df);
		double minorValue = value - (tValue * stdDev);
		double greaterValue = value + (tValue * stdDev);
				
		return new double[] {minorValue, greaterValue};
	}
	
	public void writeCsvData(SimpleRegression regression,
			String inputCsvPath, String csvDelimitter) throws Exception {
		
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(
				inputCsvPath + "-statistics.txt", csvDelimitter);
		
		try { 
			csvWriter.openFile();
			
			csvWriter.writeLine("Outlier analysis");
			csvWriter.writeLine("Occurences", Long.toString(regression.getN()));
			csvWriter.writeLine("Outliers removed", Integer.toString(outliersRemoved));
			csvWriter.writeLine("Price mean with outliers", Double.toString(priceMeanWithOutliers));
			csvWriter.writeLine("Price mean without outliers", Double.toString(priceMeanWithoutOutliers));
			
			csvWriter.writeLine("Linear Regression");
			csvWriter.writeLine("price = (" + Double.toString(regression.getIntercept())
					+ ") + (" + Double.toString(regression.getSlope())
					+ ") * date");
			
			csvWriter.writeLine(""); // empty line
			csvWriter.writeLine("Statistical data");
			csvWriter.writeLine("TotalSumSquares (SST)", Double.toString(regression.getTotalSumSquares()));
			csvWriter.writeLine("SumSquaredErrors (SSE)", Double.toString(regression.getSumSquaredErrors()));
			csvWriter.writeLine("RSquare (R2)", Double.toString(regression.getRSquare()));
			csvWriter.writeLine("MeanSquareError (MSE)", Double.toString(regression.getMeanSquareError()));
			csvWriter.writeLine("Significance", Double.toString(regression.getSignificance()));
			
			csvWriter.writeLine(""); // empty line
			csvWriter.writeLine("Confidence interval of coefficients");
			csvWriter.writeLine("Intercept", Double.toString(regression.getIntercept()));
			csvWriter.writeLine("InterceptStdErr", Double.toString(regression.getInterceptStdErr()));
			csvWriter.writeLine("Slope", Double.toString(regression.getSlope()));
			csvWriter.writeLine("SlopeStdErr", Double.toString(regression.getSlopeStdErr()));

			double[] interceptInterval = calculateConfidenceInterval(
					regression.getIntercept(), regression.getInterceptStdErr(),
					0.05d, (int) regression.getN());
			csvWriter.writeLine("Intercept confidence interval",
					Double.toString(interceptInterval[0]),
					Double.toString(interceptInterval[1]));
			
			double[] slopeInterval = calculateConfidenceInterval(
					regression.getSlope(), regression.getSlopeStdErr(),
					0.05d, (int) regression.getN());
			csvWriter.writeLine("Slope confidence interval",
					Double.toString(slopeInterval[0]),
					Double.toString(slopeInterval[1]));
			
			csvWriter.writeLine(""); // empty line
			csvWriter.writeLine("F-test");

			double SST = regression.getTotalSumSquares();
			double SSE = regression.getSumSquaredErrors();
			double SSR = SST - SSE;
			
			double k = 1d;
			double dfR = k;
			double dfE = ((double) regression.getN()) - (k + 1d);
			
			double MSR = SSR / dfR;
			double MSE = SSE / dfE;
			double fCalculated = MSR / MSE;
			
			csvWriter.writeLine("Calculated value", Double.toString(fCalculated));
			csvWriter.writeLine("Table value (alpha=0.001)",  Double.toString(tables.getFDistributionValue(0.001, (int) dfR, (int) dfE)));
			csvWriter.writeLine("Table value (alpha=0.010)",  Double.toString(tables.getFDistributionValue(0.01, (int) dfR, (int) dfE)));
			csvWriter.writeLine("Table value (alpha=0.025)",  Double.toString(tables.getFDistributionValue(0.025, (int) dfR, (int) dfE)));
			csvWriter.writeLine("Table value (alpha=0.050)",  Double.toString(tables.getFDistributionValue(0.05, (int) dfR, (int) dfE)));
			csvWriter.writeLine("Table value (alpha=0.100)",  Double.toString(tables.getFDistributionValue(0.1, (int) dfR, (int) dfE)));

			csvWriter.writeLine(""); // empty line
			csvWriter.writeLine("----------------------------------------------------------------------------------------------------");
			csvWriter.writeLine("");
			csvWriter.writeLine("Regression for instance [instance_type] on [month], [year]:");
			csvWriter.writeLine("Price = (" + Double.toString(regression.getIntercept())
					+ ") + (" + Double.toString(regression.getSlope())
					+ ") * Date");
			csvWriter.writeLine("R2 = " + Double.toString(regression.getRSquare())
					+ ", n = " + Long.toString(regression.getN())
					+ ", outliers = " + Integer.toString(outliersRemoved));
			csvWriter.writeLine("Confidence interval of coefficients \u03B8 (\u03B1 = 0.05):");
			csvWriter.writeLine("\u03B80 = [" + Double.toString(interceptInterval[0])
					+ ";" + Double.toString(interceptInterval[1]) + "]");
			csvWriter.writeLine("\u03B81 = [" + Double.toString(slopeInterval[0])
					+ ";" + Double.toString(slopeInterval[1]) + "]");
			csvWriter.writeLine("F-Test (table reference value, \u03B1 = 0.001) = "
					+ Double.toString(tables.getFDistributionValue(0.001, (int) dfR, (int) dfE)));
			csvWriter.writeLine("F-Test (calculated value) = "
					+ Double.toString(fCalculated));
			csvWriter.writeLine("");
			csvWriter.writeLine("----------------------------------------------------------------------------------------------------");

		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
	}
}
