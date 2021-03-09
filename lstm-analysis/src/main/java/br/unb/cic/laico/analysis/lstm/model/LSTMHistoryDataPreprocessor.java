package br.unb.cic.laico.analysis.lstm.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import br.unb.cic.laico.analysis.lstm.config.LSTMConfiguration;
import br.unb.cic.laico.analysis.lstm.state.LSTMAnalysisState;
import br.unb.cic.laico.analysis.lstm.util.LSTMFileNameHelper;
import br.unb.cic.laico.conversion.csv.CsvWrapperReader;
import br.unb.cic.laico.conversion.csv.CsvWrapperWriter;
import br.unb.cic.laico.data.ec2.OnDemandPrice;
import br.unb.cic.laico.data.ec2.SpotHistoryPoint;
import br.unb.cic.laico.statistics.Accumulator;

public class LSTMHistoryDataPreprocessor implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(LSTMHistoryDataPreprocessor.class);

	private static final NumberFormat inNF = NumberFormat.getInstance(Locale.US);
	private static final DateFormat inDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	private static final String INPUT_CSV_DELIMITER = "\t";
	private static final String OUTPUT_CSV_DELIMITER = ";";

	public static final int DEFAULT_NUMBER_OF_FEATURES = 2;

	private static final String FLOAT_MASK = "%.8f";

	private LSTMConfiguration configuration;
	
	public LSTMHistoryDataPreprocessor(LSTMConfiguration configuration) {
		this.configuration = configuration;
	}

	public LSTMAnalysisState doETL() throws Exception {

		logger.debug("History files preprocessing started");

		// Initialize analysis state object
		LSTMAnalysisState analysisState = new LSTMAnalysisState();
		
		// Set output file name (train file)
		String outputTrainingDataFileName = LSTMFileNameHelper.getOutputFileName(configuration, "dl4j-training");
		analysisState.setOutputTrainingDataFileName(outputTrainingDataFileName);

		// Set output file name (test file)
		String outputTestingDataFileName = LSTMFileNameHelper.getOutputFileName(configuration, "dl4j-testing");
		analysisState.setOutputTestingDataFileName(outputTestingDataFileName);;

		// Read on-demand price for the instance type
		double ondemandPrice = readOndemandPrice(configuration.getInstanceType());
		analysisState.setOndemandPrice(ondemandPrice);

		// History point list to be filled with data from CSV input files
		List<SpotHistoryPoint> spotHistoryPointList = new LinkedList<SpotHistoryPoint>();
		
		// For each input CSV file
		for (int i = 0; i < configuration.getInputCsvFileNames().length; i++) {

			// Get current input CSV file name
			String inputCsvFileName = configuration.getInputCsvFileNames()[i];

			// Read history data from input CSV file and add to the list of points
			List<SpotHistoryPoint> auxList = readHistoryDataFromFile(configuration.getInputCsvPath(),
					inputCsvFileName, configuration.getAvailabilityZoneFilter()); 
			spotHistoryPointList.addAll(auxList);
		}

		// Order spot history point list
		Collections.sort(spotHistoryPointList);

		// Complete with dates from the most recent history date until the prediction end date
		if (configuration.getPredictionEndDate() != null) {
			spotHistoryPointList = doCompletePointsUntilPredictionEndDate(
					spotHistoryPointList);
		}

		// A copy of the original list is kept in case of standardization
		List<SpotHistoryPoint> clonedList = new ArrayList<SpotHistoryPoint>(spotHistoryPointList.size());
		for (SpotHistoryPoint point: spotHistoryPointList) {
			clonedList.add((SpotHistoryPoint) point.clone());
		}
		analysisState.setOriginalHistoryPointList(clonedList);

		// Standardize history points in predefined time steps
		if (configuration.isStandardizeData()) {
			spotHistoryPointList = doTimestepStandardization(
					spotHistoryPointList);
		}

		// Keep spot list in analysis state
		analysisState.setSpotHistoryPointList(spotHistoryPointList);

		// Adjust configuration training start date, end date and data proportion
		adjustTrainingParameters(spotHistoryPointList);
		
		// Load the features to the buffer
		List<double[]> featuresBuffer = loadFeaturesBuffer(spotHistoryPointList);
		analysisState.setFeaturesBuffer(featuresBuffer);

		// Write train and test output files
		writeOutputFile(true, analysisState);
		writeOutputFile(false, analysisState);

		logger.debug("History files preprocessing completed");

		// Return analysis state object
		return analysisState;
	}

	private double readOndemandPrice(String instanceType) throws Exception {

		OnDemandPrice odPrice = OnDemandPrice.getInstance();
		return odPrice.getPrice(instanceType);
	}

	private List<SpotHistoryPoint> readHistoryDataFromFile(String inputCsvPath,
			String inputCsvFileName, String availabilityZoneFilter) throws Exception {

		// History point list to be filled with data from CSV file
		List<SpotHistoryPoint> spotHistoryPointList = null;
		
		// CSV reader object do parse data from file
		CsvWrapperReader csvReader = null;
		try {

			// Open input CSV file
			csvReader = new CsvWrapperReader(inputCsvPath + inputCsvFileName, INPUT_CSV_DELIMITER);
			csvReader.openFile();
			
			// Create list with linked list implementation
			spotHistoryPointList = new LinkedList<SpotHistoryPoint>();

			// Read all lines from input CSV file
			String[] auxObj;
			while ((auxObj = csvReader.readLine()) != null) {

				// Check if input data is from the specified availability zone
				if (availabilityZoneFilter != null
						&& !availabilityZoneFilter.equals(auxObj[1])) {
					continue;
				}

				// Parse date object
				Date dateObject = inDF.parse(auxObj[5]);

				// Check if input date is greater than training start date
				if (configuration.getTrainingStartDate() != null
						&& dateObject.getTime() < configuration.getTrainingStartDate().getTime()) {
					continue;
				}
				
				// Check if input date is lower than prediction end date
				if (configuration.getPredictionEndDate() != null
						&& dateObject.getTime() > configuration.getPredictionEndDate().getTime()) {
					continue;
				}
				
				// Parse spot price
				double spotPrice = Double.valueOf(inNF.parse(auxObj[4]).doubleValue());

				// Create spot history point and add to list
				SpotHistoryPoint point = new SpotHistoryPoint(dateObject, spotPrice);
				spotHistoryPointList.add(point);
			}

		} catch (Exception ex) {

			// Log error message and throw the exception anyway
			logger.error("Error reading data from file " + configuration.getInputCsvPath() + inputCsvFileName);
			throw ex;

		} finally {

			// Close the input CSV file
			if (csvReader != null) {
				csvReader.closeFile();
			}
		}
		
		// Return list with points read
		return spotHistoryPointList;
	}
	
	private List<SpotHistoryPoint> doCompletePointsUntilPredictionEndDate(
			List<SpotHistoryPoint> spotHistoryPointList) throws Exception {
		
		logger.debug("Number of points before complete until prediction end date: "
				+ Integer.toString(spotHistoryPointList.size()));

		// List with points with additional values until prediction end date
		List<SpotHistoryPoint> resultList = new LinkedList<SpotHistoryPoint>();

		// Accumulator for average time interval between points
		Accumulator acum = new Accumulator();
		
		// Go through the list from the first object until the last but one
		for (int i = 0; i < spotHistoryPointList.size() - 1; i++) {
			
			// Get current and next object to calculate time interval
			SpotHistoryPoint current = spotHistoryPointList.get(i);
			SpotHistoryPoint next = spotHistoryPointList.get(i + 1);
			
			// Calculate time interval (in milliseconds) between points
			acum.addValue(next.getTime() - current.getTime());
			
			// Add current object to the result list
			resultList.add(current);
		}
		
		// Get last point and add to result list
		SpotHistoryPoint lastPoint = spotHistoryPointList.get(spotHistoryPointList.size() - 1);
		resultList.add(lastPoint);
		
		// Go from last point to end prediction date
		while (lastPoint.getTime() < configuration.getPredictionEndDate().getTime()) {
			
			// Clone last point object to update the date adding average time
			lastPoint = (SpotHistoryPoint) lastPoint.clone();
			lastPoint.setDateObject(new Date(lastPoint.getTime() + (long) acum.getMean()));
			
			// Add cloned last point to result list
			resultList.add(lastPoint);
		}
		
		// Return the result list
		logger.debug("Number of points after complete until prediction end date: "
				+ Integer.toString(resultList.size()));
		return resultList;
	}

	private List<SpotHistoryPoint> doTimestepStandardization(
			List<SpotHistoryPoint> spotHistoryPointList) throws Exception {

		logger.debug("Number of points before standardization: " + Integer.toString(spotHistoryPointList.size()));

		// Calendar object to adjust the time of spot history objects
		Calendar calendar = Calendar.getInstance();

		// Standardized list with points only in predefined time steps
		List<SpotHistoryPoint> resultList = new LinkedList<SpotHistoryPoint>();

		// Get first point to adjust minutes, seconds and milliseconds to zero
		SpotHistoryPoint firstPoint = spotHistoryPointList.get(0);
		calendar.setTime(firstPoint.getDateObject());
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		firstPoint.setDateObject(calendar.getTime());

		// Go through the list from the first object until the last but one
		for (int i = 0; i < spotHistoryPointList.size() - 1; i++) {

			// Get current and next object
			SpotHistoryPoint current = spotHistoryPointList.get(i);
			SpotHistoryPoint next = spotHistoryPointList.get(i + 1);

			// Adjust next object to the nearest next time step
			calendar.setTime(next.getDateObject());
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			next.setDateObject(calendar.getTime());

			// While current object is before next
			while (current.getTime() < next.getTime()) {

				// Add current object to the result list
				resultList.add(current);

				// Clone current point and add 1 times step
				current = (SpotHistoryPoint) current.clone();
				calendar.setTime(current.getDateObject());
				calendar.add(Calendar.HOUR_OF_DAY,
						configuration.getRegularizationTimestepInHours());
				current.setDateObject(calendar.getTime());
			}
		}

		// Get last point and add to result list
		SpotHistoryPoint lastPoint = spotHistoryPointList.get(spotHistoryPointList.size() - 1);
		resultList.add(lastPoint);

		// Return the result list
		logger.debug("Number of points after standardization: " + Integer.toString(resultList.size()));
		return resultList;
	}
	
	private void adjustTrainingParameters(List<SpotHistoryPoint> spotHistoryPointList) {

		// Adjust training start date
		if (configuration.getTrainingStartDate() == null) {
			SpotHistoryPoint firstPoint = spotHistoryPointList.get(0);
			configuration.setTrainingStartDate(firstPoint.getDateObject());
		}

		// Adjust training data proportion or training end date
		if (configuration.getTrainingDataProportion() > 0D) {

			// Adjust training data proportion
			int index = (int) Math.ceil((double) spotHistoryPointList.size()
					* configuration.getTrainingDataProportion()) - 1;
			SpotHistoryPoint point = spotHistoryPointList.get(index);
			configuration.setTrainingEndDate(point.getDateObject());

		} else if (configuration.getTrainingEndDate() != null) {

			// Adjust training end date
			int index = 0;
			for (SpotHistoryPoint point: spotHistoryPointList) {
				index++;
				if (point.getDateObject().getTime() > configuration.getTrainingEndDate().getTime()) {
					break;
				}
			}
			double trainingDataProportion = (double) index / (double) spotHistoryPointList.size();
			configuration.setTrainingDataProportion(trainingDataProportion);

		} else {

			// Illegal argument: training proportion or end date must be provided
			throw new IllegalArgumentException("Invalid training data proportion or end date.");
		}
	}

	private List<double[]> loadFeaturesBuffer(List<SpotHistoryPoint> spotHistoryPointList) throws Exception {
		
		List<double[]> featuresBuffer = new LinkedList<double[]>();
		for (int i = 0; i < spotHistoryPointList.size() - configuration.getNumberOfFeatures() + 1; i++) {

			double[] features = new double[configuration.getNumberOfFeatures()];
			for (int j = 0; j < features.length; j++) {

				features[j] = spotHistoryPointList.get(i + j).getPrice();
			}
			featuresBuffer.add(features);
		}

		return featuresBuffer;
	}

	private void writeOutputFile(boolean trainingFile, LSTMAnalysisState analysisState) throws Exception {
		
		// Spot point list and feature buffer
		List<SpotHistoryPoint> spotHistoryPointList = analysisState.getSpotHistoryPointList();
		List<double[]> featuresBuffer = analysisState.getFeaturesBuffer();
		
		// Lower and upper bounds
		int lowerBound = 0;
		int upperBound = featuresBuffer.size();
		
		// Output file name
		String outputFileName;

		// Check if training or testing data to be processed
		if (trainingFile) {
			
			// Get training file name from analysis state
			outputFileName = analysisState.getOutputTrainingDataFileName();
			
			// Check which parameter should be used: proportion or end date
			if (configuration.getTrainingDataProportion() > 0) {
				
				// Define upper training bound by training data proportion
				upperBound = (int) Math.ceil(featuresBuffer.size() * configuration.getTrainingDataProportion()
						- (configuration.getNumberOfFeatures() - 1));
				
			} else if (configuration.getTrainingEndDate() != null) {
				
				// Define upper training bound by training end date
				for (int i=0; i< spotHistoryPointList.size(); i++) {
					SpotHistoryPoint point = spotHistoryPointList.get(i);
					if (point.getTime() > configuration.getTrainingEndDate().getTime()) {
						
						upperBound = i - (configuration.getNumberOfFeatures() - 1);
						break;
					}
				}
			}

		} else {
			
			// Get testing file name from analysis state
			outputFileName = analysisState.getOutputTestingDataFileName();
			
			// Check which parameter should be used: proportion or end date
			if (configuration.getTrainingDataProportion() > 0) {
				
				// Define lower testing bound by training data proportion
				lowerBound = (int) Math.floor(featuresBuffer.size() * configuration.getTrainingDataProportion()
						- (configuration.getNumberOfFeatures() - 1));

			} else if (configuration.getTrainingEndDate() != null) {
				
				// Define lower testing bound by training end date
				for (int i=0; i< spotHistoryPointList.size(); i++) {
					SpotHistoryPoint point = spotHistoryPointList.get(i);
					if (point.getTime() > configuration.getTrainingEndDate().getTime()) {
						
						lowerBound = i - (configuration.getNumberOfFeatures() - 1);
						break;
					}
				}
			}
		}

		// Output file object
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(outputFileName, OUTPUT_CSV_DELIMITER);
		try {

			// Open output file
			csvWriter.openFile();

			// For each row in buffer
			for (int i = lowerBound; i < upperBound; i++) {

				// Get features from buffer
				double[] features = featuresBuffer.get(i);

				// Add price in position 0
				StringBuilder data = new StringBuilder();
				data.append(String.format(FLOAT_MASK, features[0]));

				// Add delimiter and other prices
				for (int j = 1; j < features.length; j++) {
					data.append(OUTPUT_CSV_DELIMITER);
					data.append(String.format(FLOAT_MASK, features[j]));
				}

				// Writing the output data
				csvWriter.writeLine(data.toString());
			}

		} catch (Exception ex) {

			// Log error message and throw the exception anyway
			logger.error("Error writing data to file " + outputFileName);
			throw ex;

		} finally {

			// Close the output CSV file
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
	}
}
