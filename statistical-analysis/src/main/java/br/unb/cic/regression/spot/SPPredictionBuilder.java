package br.unb.cic.regression.spot;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.log4j.Logger;

import br.unb.cic.conversion.csv.CsvWrapperReader;
import br.unb.cic.conversion.csv.CsvWrapperWriter;
import br.unb.cic.conversion.math.MathUtil;
import br.unb.cic.regression.spot.scatterplot.ScatterPlotHelper;
import br.unb.cic.regression.spot.statistics.StatisticsHelper;

public class SPPredictionBuilder implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final String CSV_DELIMITER = "\t";
	private static final NumberFormat inNF = NumberFormat.getInstance(Locale.US);
	private static final DateFormat outDF = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

	private static Logger logger = Logger.getLogger(SPPredictionBuilder.class);
	
	private String instanceType;
	private String regionFilter;
	private String outputPath;
	
	private List<String> csvLearningPathList;
	private List<String> csvPredictionPathList;
	
	private Double[] learningDates;
	private Double[] learningPrices;
	
	private Double[] predictionDates;
	private Double[] predictionPrices;
	private Double[] predictionPricesFromRegression;
	private Double MSE;
	private Double maxPE;
	private Double avgPE;
	
	private SimpleRegression regression;
	
	public SPPredictionBuilder(String instanceType, String regionFilter, String outputPath) {
		this.instanceType = instanceType;
		this.regionFilter = regionFilter;
		this.outputPath = outputPath;
		this.csvLearningPathList = new ArrayList<String>();
		this.csvPredictionPathList = new ArrayList<String>();
	}
	
	public void addCsvLearningPath(String csvLearningPath) {
		csvLearningPathList.add(csvLearningPath);
	}

	public void addCsvPredictionPath(String csvPredictionPath) {
		csvPredictionPathList.add(csvPredictionPath);
	}
	
	public void doPrediction() throws Exception {
		logger.debug("(begin) " + outDF.format(new Date()));
		step01_ReadLearningData();
		step02_ReadPredictionData();
		step03_BuildLinearRegression();
		step04_RunStatistics();
		step05_RunPrediction();
		step06_RunAvailabilityAnalysis();
		step07_CreateScatterPlot();
		logger.debug("(end) " + outDF.format(new Date()));
	}
	
	public void step01_ReadLearningData() throws Exception {
		
		List<Double> dateList = new LinkedList<Double>();
		List<Double> priceList = new LinkedList<Double>();
		
		for (String csvLearningPath: csvLearningPathList) {
			addDataToList(csvLearningPath, dateList, priceList);
		}
		
		learningDates = dateList.toArray(new Double[0]);
		learningPrices = priceList.toArray(new Double[0]);
		
		logger.debug("(Step 01) Learning occurrences read: " + Integer.toString(learningDates.length));
	}
	
	public void step02_ReadPredictionData() throws Exception {
		
		List<Double> dateList = new LinkedList<Double>();
		List<Double> priceList = new LinkedList<Double>();
		
		for (String csvPredictionPath: csvPredictionPathList) {
			addDataToList(csvPredictionPath, dateList, priceList);
		}
		
		predictionDates = dateList.toArray(new Double[0]);
		predictionPrices = priceList.toArray(new Double[0]);
		
		logger.debug("(Step 02) Prediction occurrences read: " + Integer.toString(predictionDates.length));
	}
	
	private void addDataToList(String csvPath, List<Double> dateList,
			List<Double> priceList) throws Exception {
		
		CsvWrapperReader csvReader = null;
		try {
			csvReader = new CsvWrapperReader(csvPath, CSV_DELIMITER);
			csvReader.openFile();
			
			String[] auxObj;
			while ((auxObj = csvReader.readLine()) != null) {
				
				dateList.add(Double.valueOf(inNF.parse(auxObj[1]).doubleValue()));
				priceList.add(Double.valueOf(inNF.parse(auxObj[2]).doubleValue()));
			}
			
		} catch (IOException ioEx) {
			logger.error("Csv reading error on file " + csvPath, ioEx);
			
		} finally {
			if (csvReader != null) {
				csvReader.closeFile();
			}
		}
	}
	
	public void step03_BuildLinearRegression() throws Exception {
		
		regression = new SimpleRegression();
		for (int i=0; i < learningDates.length; i++) {
			regression.addData(learningDates[i].doubleValue(), learningPrices[i].doubleValue());
		}
		double intercept = regression.getIntercept();
		double slope = regression.getSlope();
		
		logger.debug("(Step 03) Linear regression: price = ("
				+ Double.toString(intercept) + ") + ("
				+ Double.toString(slope) + ") * date");
	}
	
	public void step04_RunStatistics() throws Exception {
		
		StatisticsHelper helper = new StatisticsHelper();
		helper.writeCsvData(regression, outputPath + instanceType + "-predictions", CSV_DELIMITER);

		logger.debug("(Step 04) RSquare: " + regression.getRSquare());
	}
	
	public void step05_RunPrediction() throws Exception {
		
		predictionPricesFromRegression = new Double[predictionDates.length];
		
		double sumSquaredErrors = 0d;
		double sumPositiveErrors = 0d;
		double maxError = 0d;

		for (int i=0; i<predictionDates.length; i++) {
			
			predictionPricesFromRegression[i] = Double.valueOf(
					regression.predict(predictionDates[i].doubleValue()));
			double error = predictionPrices[i].doubleValue()
					- predictionPricesFromRegression[i].doubleValue();
			
			sumSquaredErrors = sumSquaredErrors + Math.pow(error, 2d);
			if (error > 0d) {
				sumPositiveErrors = sumPositiveErrors + error;
			}
			if (maxError < error) {
				maxError = error;
			}
		}
		
		double n = (double) predictionDates.length;
		MSE = Double.valueOf(sumSquaredErrors / n);
		maxPE = Double.valueOf(maxError);
		avgPE = Double.valueOf(sumPositiveErrors / n);
		
		logger.debug("(Step 05) MSE: " + Double.toString(MSE.doubleValue())
				+ " maxPE: " + Double.toString(maxPE.doubleValue())
				+ " avgPE: " + Double.toString(avgPE.doubleValue()));
	}
	
	public void step06_RunAvailabilityAnalysis() throws Exception {
		
		double totalTime  = 0d;
		double timeNoOver = 0d;
		double timeMSE    = 0d;
		double timeAvgPE  = 0d;
		
		double totalCost  = 0d;
		double costNoOver = 0d;
		double costMSE    = 0d;
		double costAvgPE  = 0d;
		
		double maxPrice       = 0d;
		double minPrice       = Double.MAX_VALUE;
		double maxPriceNoOver = 0d;
		double minPriceNoOver = Double.MAX_VALUE;
		
		for (int i=0; i<predictionDates.length-1; i++) {
			
			double period = (predictionDates[i+1].doubleValue()
					- predictionDates[i].doubleValue()) / 1000d;
			
			double error = predictionPrices[i].doubleValue()
					- predictionPricesFromRegression[i].doubleValue();
			double errorMSE = predictionPrices[i].doubleValue()
					- (predictionPricesFromRegression[i].doubleValue()
							+ MSE.doubleValue());
			double errorAvgPE = predictionPrices[i].doubleValue()
					- (predictionPricesFromRegression[i].doubleValue()
							+ avgPE.doubleValue());

			// time and cost calculation
			totalTime = totalTime + period;
			totalCost = totalCost + ((period / 3600d)
					* predictionPrices[i].doubleValue());
			if (error <= 0d) {
				timeNoOver = timeNoOver + period;
				costNoOver = costNoOver + ((period / 3600d)
						* predictionPrices[i].doubleValue());
			}
			if (errorMSE <= 0d) {
				timeMSE = timeMSE + period;
				costMSE = costMSE + ((period / 3600d)
						* predictionPrices[i].doubleValue());
			}
			if (errorAvgPE <= 0d) {
				timeAvgPE = timeAvgPE + period;
				costAvgPE = costAvgPE + ((period / 3600d)
						* predictionPrices[i].doubleValue());
			}

			// min and max price calculation
			if (minPrice > predictionPrices[i].doubleValue()) {
				minPrice = predictionPrices[i].doubleValue();
			}
			if (maxPrice < predictionPrices[i].doubleValue()) {
				maxPrice = predictionPrices[i].doubleValue();
			}

			if (minPriceNoOver > predictionPricesFromRegression[i].doubleValue()) {
				minPriceNoOver = predictionPricesFromRegression[i].doubleValue();
			}
			if (maxPriceNoOver < predictionPricesFromRegression[i].doubleValue()) {
				maxPriceNoOver = predictionPricesFromRegression[i].doubleValue();
			}
		}
		
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(
				outputPath + instanceType + "-predictions.txt", CSV_DELIMITER);
		try { 
			csvWriter.openFile();
			csvWriter.writeLine("Availability Analysis");
			csvWriter.writeLine("Description",
					"Overestimating Metric",
					"Runtime (s)",
					"Availability (%)",
					"Cost ($)",
					"Price Range ($/hour)");
			csvWriter.writeLine("Total runtime",
					"-",
					MathUtil.round2String(totalTime),
					"100.0%",
					MathUtil.round2String(totalCost),
					"[" + MathUtil.round2String(minPrice) + "," + MathUtil.round2String(maxPrice) + "]");
			csvWriter.writeLine("No overstimating",
					"-",
					MathUtil.round2String(timeNoOver),
					MathUtil.round2String(100d * (timeNoOver / totalTime)) + "%",
					MathUtil.round2String(costNoOver),
					"[" + MathUtil.round2String(minPriceNoOver) + "," + MathUtil.round2String(maxPriceNoOver) + "]");
			csvWriter.writeLine("MSE overstimating",
					MathUtil.round2String(MSE.doubleValue()),
					MathUtil.round2String(timeMSE),
					MathUtil.round2String(100d * (timeMSE / totalTime)) + "%",
					MathUtil.round2String(costMSE),
					"[" + MathUtil.round2String(minPriceNoOver + MSE.doubleValue()) + "," + MathUtil.round2String(maxPriceNoOver + MSE.doubleValue()) + "]");
			csvWriter.writeLine("MeanPE overstimating",
					MathUtil.round2String(avgPE.doubleValue()),
					MathUtil.round2String(timeAvgPE),
					MathUtil.round2String(100d * (timeAvgPE / totalTime)) + "%",
					MathUtil.round2String(costAvgPE),
					"[" + MathUtil.round2String(minPriceNoOver + avgPE.doubleValue()) + "," + MathUtil.round2String(maxPriceNoOver + avgPE.doubleValue()) + "]");
			
		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
		
		logger.debug("(Step 06) Availability analysis successfully done.");
	}
	
	public void step07_CreateScatterPlot() throws Exception {
		
		List<Date> dateHistory = new ArrayList<Date>(predictionDates.length);
		for (int i=0; i<predictionDates.length; i++) {
			dateHistory.add(new Date(predictionDates[i].longValue()));
		}
		List<Double> priceHistory = new ArrayList<Double>(predictionPrices.length);
		for (int i=0; i<predictionPrices.length; i++) {
			priceHistory.add(predictionPrices[i]);
		}
		
		ScatterPlotHelper helper = new ScatterPlotHelper(dateHistory, priceHistory);
		helper.writeGraphic(outputPath + instanceType + "-predictions.txt", instanceType, regionFilter, regression);
		
		logger.debug("(Step 07) scatter plot graphs writen.");
	}
}
