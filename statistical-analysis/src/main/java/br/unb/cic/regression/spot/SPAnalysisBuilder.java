package br.unb.cic.regression.spot;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.log4j.Logger;

import br.unb.cic.conversion.csv.CsvWrapperReader;
import br.unb.cic.regression.spot.scatterplot.ScatterPlotHelper;
import br.unb.cic.regression.spot.statistics.StatisticsHelper;

public class SPAnalysisBuilder implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String CSV_DELIMITER = "\t";
	
	private final static DateFormat inDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private final static DateFormat outDF = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
	private final static NumberFormat inNF = NumberFormat.getInstance(Locale.US);
	
	private static Logger logger = Logger.getLogger(SPAnalysisBuilder.class);
	
	private String regionFilter;
	private String inputCsvPath;
	
	private String instanceType;
	private List<Date> dateHistory;
	private List<Double> priceHistory;
	
	private StatisticsHelper statisticsHelper;
	private SimpleRegression regression;
	
	public SPAnalysisBuilder(String inputCsvPath) {
		this(inputCsvPath, null);
	}
	
	public SPAnalysisBuilder(String inputCsvPath, String regionFilter) {
		this.inputCsvPath = inputCsvPath;
		this.regionFilter = regionFilter;
		this.statisticsHelper = new StatisticsHelper();
	}
	
	public void doAnalysis() throws Exception {
		
		logger.debug("(begin) " + outDF.format(new Date()));
		step01_ReadSpotPricingHistoryData();
		step02_RemoveOutliers();
		step03_BuildLinearRegression();
		step04_CreateScatterPlot();
		step05_RunStatistics();
		logger.debug("(end) " + outDF.format(new Date()));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void step01_ReadSpotPricingHistoryData() throws Exception {

		List<Date> dateList = new LinkedList<Date>();
		List<Double> priceList = new LinkedList<Double>();
		CsvWrapperReader csvReader = null;
		instanceType = null;
		
		try {
			csvReader = new CsvWrapperReader(inputCsvPath, CSV_DELIMITER);
			csvReader.openFile();

			String[] auxObj;
			while ((auxObj = csvReader.readLine()) != null) {
	
				if (regionFilter != null && !regionFilter.equals(auxObj[1])) {
					continue;
				}
				
				if (instanceType == null) {
					instanceType = auxObj[2];
				}
				
				try {
					Date date = inDF.parse(auxObj[5]);
					dateList.add(date);
				} catch (ParseException nex) {
					logger.error("Key parsing error", nex);
				}
				
				try {
					Number number = inNF.parse(auxObj[4]);
					priceList.add(Double.valueOf(number.doubleValue()));
				} catch (ParseException nex) {
					logger.error("Price parsing error.", nex);
					throw nex;
				}
			}
		
		} catch (IOException ioEx) {
			logger.error("(Step 01) Input csv reading error.", ioEx);
			
		} finally {
			if (csvReader != null) {
				csvReader.closeFile();
			}
		}

		dateHistory = new ArrayList<Date>(dateList);
		priceHistory = new ArrayList<Double>(priceList);

		logger.debug("(Step 01) instance type: " + instanceType
				+ " start: " + outDF.format(dateList.get(0))
				+ " end: " + outDF.format(dateList.get(dateList.size()-1)));	
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void step02_RemoveOutliers() throws Exception {

		List<Date> dateList = new ArrayList<Date>(dateHistory.size());
		List<Double> priceList = new ArrayList<Double>(priceHistory.size());
		
		statisticsHelper.removeOutliers(dateHistory, priceHistory,
				dateList, priceList, 0.1d);
		int pricesRead = priceHistory.size();
		int pricesRemained = priceList.size();
		
		dateHistory = dateList;
		priceHistory = priceList;
		
		logger.debug("(Step 02) prices read: " + Integer.toString(pricesRead)
				+ " prices remained: " + Integer.toString(pricesRemained)
				+ " outliers: " + Integer.toString(pricesRead - pricesRemained));	
	}
	
	/**
	 * 
	 * @see http://commons.apache.org/proper/commons-math/userguide/stat.html
	 * @throws Exception
	 */
	public void step03_BuildLinearRegression() throws Exception {
		
		regression = new SimpleRegression();
		for (int i=0; i < dateHistory.size(); i++) {
			regression.addData(dateHistory.get(i).getTime(),
					priceHistory.get(i).doubleValue());
		}
		double intercept = regression.getIntercept();
		double slope = regression.getSlope();
		
		logger.debug("(Step 03) price = ("
				+ Double.toString(intercept) + ") + ("
				+ Double.toString(slope) + ") * date");
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void step04_CreateScatterPlot() throws Exception {
		
		ScatterPlotHelper helper = new ScatterPlotHelper(dateHistory, priceHistory);
		helper.writeCsvData(inputCsvPath, CSV_DELIMITER);
		helper.writeGraphic(inputCsvPath, instanceType, regionFilter, regression);
		helper.writeGraphicHomoscedasticity(inputCsvPath, instanceType, regression);
		helper.writeFrequencyDistributionGraphic(inputCsvPath, instanceType, regionFilter);

		logger.debug("(Step 04) scatter plot and homoscedasticity graphs writen.");
	}	
	
	/**
	 * 
	 * @throws Exception
	 */
	public void step05_RunStatistics() throws Exception {
		
		statisticsHelper.writeCsvData(regression, inputCsvPath, CSV_DELIMITER);
		logger.debug("(Step 05) RSquare: " + regression.getRSquare());
	}
}
