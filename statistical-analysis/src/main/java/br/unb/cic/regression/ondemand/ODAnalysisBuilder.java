package br.unb.cic.regression.ondemand;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.log4j.Logger;

import br.unb.cic.conversion.csv.CsvWrapperReader;
import br.unb.cic.regression.ondemand.scatterplot.ODScatterPlotHelper;
import br.unb.cic.regression.ondemand.statistics.ODStatisticsHelper;

public class ODAnalysisBuilder implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final String CSV_DELIMITER = "\t";

	private final static DateFormat outDF = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
	
	private static Logger logger = Logger.getLogger(ODAnalysisBuilder.class);
	
	private String provider;
	private String experiment;
	private String inputCsvPath;

	private List<String> headerLabels;
	private List<String> instanceTypes;
	private List<Double[]> infrastructureParams;
	private List<Double> prices;
	
	private OLSMultipleLinearRegression regression;
	
	public ODAnalysisBuilder(String provider, String experiment, String inputCsvPath) {
		this.provider = provider;
		this.experiment = experiment;
		this.inputCsvPath = inputCsvPath;
	}
	
	public void doAnalysis() throws Exception {
		
		logger.debug("(begin) " + outDF.format(new Date()));
		step01_ReadOnDemandPricingData();
		step02_BuildMultiLinearRegression();
		step03_CreateScatterPlot();
		step04_RunStatistics();
		logger.debug("(end) " + outDF.format(new Date()));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void step01_ReadOnDemandPricingData() throws Exception {

		headerLabels = new LinkedList<String>();
		instanceTypes = new LinkedList<String>();
		infrastructureParams = new LinkedList<Double[]>();
		prices = new LinkedList<Double>();
		
		CsvWrapperReader csvReader = null;
		
		try {
			csvReader = new CsvWrapperReader(inputCsvPath, CSV_DELIMITER);
			csvReader.openFile();

			String[] auxObj;
			while ((auxObj = csvReader.readLine()) != null) {

				if (headerLabels.isEmpty()) {
					for (String label: auxObj) {
						headerLabels.add(label);
					}
					continue;
				}
				
				instanceTypes.add(auxObj[0]);
				prices.add(Double.parseDouble(auxObj[1]));
				Double[] params = new Double[auxObj.length-2];
				for (int i=0; i<auxObj.length-2; i++) {
					params[i] = Double.parseDouble(auxObj[i+2]);
				}
				infrastructureParams.add(params);
			}
		
		} catch (IOException ioEx) {
			logger.error("(Step 01) Input csv reading error.", ioEx);
			
		} finally {
			if (csvReader != null) {
				csvReader.closeFile();
			}
		}

		logger.debug("(Step 01) instance types read: " + Integer.toString(instanceTypes.size()));	
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void step02_BuildMultiLinearRegression() throws Exception {
		
		regression = new OLSMultipleLinearRegression();
		
		double[] y = new double[instanceTypes.size()];
		double[][] x = new double[instanceTypes.size()][];
		
		for (int i=0; i < instanceTypes.size(); i++) {
			
			Double[] params = infrastructureParams.get(i);
			x[i] = new double[params.length];
			for (int j=0; j<params.length; j++) {
				x[i][j] = params[j].doubleValue();
			}
			y[i] = prices.get(i).doubleValue();
			
		}
		regression.newSampleData(y, x);
		
		logger.debug("(Step 02) multilinear regression built");
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void step03_CreateScatterPlot() throws Exception {
		
		ODScatterPlotHelper helper = new ODScatterPlotHelper(
				provider, experiment, inputCsvPath, prices, regression);
		helper.writeQuantilGraphic();
		helper.writeHomoscedasticityGraphic();
		
		logger.debug("(Step 03) quantil scatter plot and homoscedasticity graphs writen");
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void step04_RunStatistics() throws Exception {
		
		ODStatisticsHelper statisticsHelper = new ODStatisticsHelper(provider, experiment, headerLabels, prices);
		statisticsHelper.writeCsvData(regression, inputCsvPath, CSV_DELIMITER);
		
		logger.debug("(Step 04) statistics writen");
	}
}
