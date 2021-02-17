package br.unb.cic.analysis.linearbid;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import br.unb.cic.conversion.csv.CsvWrapperReader;
import br.unb.cic.conversion.csv.CsvWrapperWriter;

public class LinearBidAnalysisBuilder implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String CSV_DELIMITER = "\t";
	private static final NumberFormat inNF = NumberFormat.getInstance(Locale.US);
	private static final DateFormat outDF = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
	
	private static Logger logger = Logger.getLogger(LinearBidAnalysisBuilder.class);
	
	private String instanceType;
	private String regionFilter;
	private String outputPath;
	
	private List<String> csvLearningPathList;
	private List<String> csvPredictionPathList;

	private Long[] learningDates;
	private Double[] learningPrices;
	private Date[] learningTimeInterval;
	
	private Long[] predictionDates;
	private Double[] predictionPrices;
	private Date[] predictionTimeInterval;
	
	private List<Float> keyList4LinearBids;
	private Map<Float, Double> linearBids;
	
	public LinearBidAnalysisBuilder(String instanceType, String regionFilter, String outputPath) {
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
		step03_FindTimeIntervals();
		step04_CalculateLinearBids();
		step05_RunAvailabilityAnalysis();
		logger.debug("(end) " + outDF.format(new Date()));
	}

	public void step01_ReadLearningData() throws Exception {
		
		List<Long> dateList = new LinkedList<Long>();
		List<Double> priceList = new LinkedList<Double>();
		
		for (String csvLearningPath: csvLearningPathList) {
			addDataToList(csvLearningPath, dateList, priceList);
		}
		
		learningDates = dateList.toArray(new Long[0]);
		learningPrices = priceList.toArray(new Double[0]);
		
		logger.debug("(Step 01) Learning occurrences read: " + Integer.toString(learningDates.length));
	}
	
	public void step02_ReadPredictionData() throws Exception {
		
		List<Long> dateList = new LinkedList<Long>();
		List<Double> priceList = new LinkedList<Double>();
		
		for (String csvPredictionPath: csvPredictionPathList) {
			addDataToList(csvPredictionPath, dateList, priceList);
		}
		
		predictionDates = dateList.toArray(new Long[0]);
		predictionPrices = priceList.toArray(new Double[0]);
		
		logger.debug("(Step 02) Prediction occurrences read: " + Integer.toString(predictionDates.length));
	}
	
	private void addDataToList(String csvPath, List<Long> dateList,
			List<Double> priceList) throws Exception {
		
		CsvWrapperReader csvReader = null;
		try {
			csvReader = new CsvWrapperReader(csvPath, CSV_DELIMITER);
			csvReader.openFile();
			
			String[] auxObj;
			while ((auxObj = csvReader.readLine()) != null) {

				dateList.add(Long.valueOf(inNF.parse(auxObj[1]).longValue()));
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
	
	public void step03_FindTimeIntervals() throws Exception {
		
		long minLearningDate = Long.MAX_VALUE;
		long maxLearningDate = 0L;

		long minPredictionDate = Long.MAX_VALUE;
		long maxPredictionDate = 0L;

		for (Long time: learningDates) {
			if (minLearningDate > time.longValue()) {
				minLearningDate = time.longValue();
			}
			if (maxLearningDate < time.longValue()) {
				maxLearningDate = time.longValue();
			}
		}
		learningTimeInterval = new Date[] {new Date(minLearningDate), new Date(maxLearningDate)};
		
		for (Long time: predictionDates) {
			if (minPredictionDate > time.longValue()) {
				minPredictionDate = time.longValue();
			}
			if (maxPredictionDate < time.longValue()) {
				maxPredictionDate = time.longValue();
			}
		}
		predictionTimeInterval = new Date[] {new Date(minPredictionDate), new Date(maxPredictionDate)};
		
		logger.debug("(Step 03) Learning and prediction time intervals found.");
	}
	
	public void step04_CalculateLinearBids() throws Exception {
		
		int length = learningPrices.length;
		Double[] sortedLearningPrices = new Double[length];
		System.arraycopy(learningPrices, 0, sortedLearningPrices, 0, length);
		Arrays.sort(sortedLearningPrices);
		
		keyList4LinearBids = new ArrayList<Float>();
		keyList4LinearBids.add(Float.valueOf(0.50f));
		keyList4LinearBids.add(Float.valueOf(0.80f));
		keyList4LinearBids.add(Float.valueOf(0.90f));
		keyList4LinearBids.add(Float.valueOf(0.95f));
		keyList4LinearBids.add(Float.valueOf(0.99f));
		
		linearBids = new HashMap<Float, Double>(keyList4LinearBids.size());
		for (Float key: keyList4LinearBids) {
			int pos = (int) ((length * key.floatValue()) - 1);
			linearBids.put(key, sortedLearningPrices[pos]);
		}
		
		logger.debug("(Step 04) Linear bids calculated.");
	}
	
	private void step05_RunAvailabilityAnalysis() throws Exception {
		
		double[] array1BidPrices = new double[keyList4LinearBids.size()];
		for (int i=0; i<keyList4LinearBids.size(); i++) {
			Float key = keyList4LinearBids.get(i);
			Double bidPrice = linearBids.get(key);
			array1BidPrices[i] = bidPrice.doubleValue();
		}
		
		long totalTime = 0L;
		double totalCost = 0d; 
		long[] array2UpTime = new long[keyList4LinearBids.size()];
		double[] array4Cost = new double[keyList4LinearBids.size()];
		for (int i=0; i<predictionDates.length-1; i++) {
			
			long period = (predictionDates[i+1].longValue()
					- predictionDates[i].longValue()) / 1000L;
			totalTime += period;
			
			double cost = ((double) period / 3600d) * predictionPrices[i];
			totalCost += cost;
			
			for (int j=0; j<array1BidPrices.length; j++) {
				
				double bidPrice = array1BidPrices[j];
				if (bidPrice >= predictionPrices[i].doubleValue()) {
					array2UpTime[j] += period;
					array4Cost[j] += cost;
				}
			}
		}

		double[] array3Availability = new double[keyList4LinearBids.size()];
		for (int i=0; i<array3Availability.length; i++) {
			array3Availability[i] = ((double) array2UpTime[i]) / ((double) totalTime);
		}

		CsvWrapperWriter csvWriter = new CsvWrapperWriter(
				outputPath + instanceType + "-linearbids.txt", CSV_DELIMITER);
		try { 
			csvWriter.openFile();
			csvWriter.writeLine("Linear Bids Availability Analysis");
			csvWriter.writeLine("Instance type", instanceType);
			csvWriter.writeLine("Region", regionFilter);
			csvWriter.writeLine("Learning occurrences read", Integer.toString(learningDates.length));
			csvWriter.writeLine("Learning time interval", outDF.format(learningTimeInterval[0]),
					outDF.format(learningTimeInterval[1]));
			csvWriter.writeLine("Prediction occurrences read", Integer.toString(predictionDates.length));
			csvWriter.writeLine("Prediction time interval", outDF.format(predictionTimeInterval[0]),
					outDF.format(predictionTimeInterval[1]));

			csvWriter.writeLine("");
			csvWriter.writeLine(
					"% Over learning prices",
					"Bid price ($/hour)",
					"Up time(s)",
					"Availability (%)",
					"Cost ($)");
			csvWriter.writeLine(
					"",
					"",
					Long.toString(totalTime),
					"100%",
					Double.toString(totalCost));
			
			for (int i=0; i<array3Availability.length; i++) {
				csvWriter.writeLine(
						keyList4LinearBids.get(i).toString(),
						Double.toString(array1BidPrices[i]),
						Long.toString(array2UpTime[i]),
						Double.toString(array3Availability[i]),
						Double.toString(array4Cost[i]));
			}
			
		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
		
		logger.debug("(Step 05) Availability analysis successfully done.");
	}
}
