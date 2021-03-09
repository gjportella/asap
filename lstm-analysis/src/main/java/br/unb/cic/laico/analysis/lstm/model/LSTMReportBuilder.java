package br.unb.cic.laico.analysis.lstm.model;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import br.unb.cic.laico.analysis.lstm.config.LSTMConfiguration;
import br.unb.cic.laico.analysis.lstm.error.LSTMErrorData;
import br.unb.cic.laico.analysis.lstm.state.LSTMAnalysisState;
import br.unb.cic.laico.analysis.lstm.util.LSTMFileNameHelper;
import br.unb.cic.laico.conversion.csv.CsvWrapperWriter;
import br.unb.cic.laico.data.ec2.SpotHistoryPoint;
import br.unb.cic.laico.statistics.Accumulator;

public class LSTMReportBuilder implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(LSTMReportBuilder.class);

	private static final String OUTPUT_CSV_DELIMITER = ";";
	private static final DateFormat OUT_DF = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
	private static final DateFormat OUT_DF_DATE_ONLY = new SimpleDateFormat("dd/MM/yyyy");
	private static final String FLOAT_MASK = "%.8f";

	private LSTMConfiguration configuration;

	public LSTMReportBuilder(LSTMConfiguration configuration) {
		this.configuration = configuration;
	}

	public LSTMAnalysisState buildReports(LSTMAnalysisState analysisState) throws Exception {

		writeTrainingDataToCSV(analysisState);
		writeTestingDataToCSV(analysisState);
		writePredictedDataToCSV(analysisState);
		
		writeGNUPlotScript(analysisState);
		calculatePredictionErrorData(analysisState);
		writeErrorDataToCSV(analysisState);
		writeFinalReport(analysisState);
		
		return analysisState;
	}

	private void writeTrainingDataToCSV(LSTMAnalysisState analysisState) throws Exception {

		List<SpotHistoryPoint> originalHistoryPointList = analysisState.getOriginalHistoryPointList();
		Date trainingEndDate = configuration.getTrainingEndDate();
		
		String trainingFileName = LSTMFileNameHelper.getOutputFileName(configuration, "gnuplot-training-data");
		CsvWrapperWriter trainingFileWriter = new CsvWrapperWriter(trainingFileName, OUTPUT_CSV_DELIMITER);
		try {

			trainingFileWriter.openFile();
			for (int i=0; i<originalHistoryPointList.size(); i++) {
				SpotHistoryPoint point = originalHistoryPointList.get(i);
				if (point.getDateObject().getTime() <= trainingEndDate.getTime()) {
					
					StringBuilder trainingDataBuffer = new StringBuilder();
					trainingDataBuffer.append(OUT_DF.format(point.getDateObject()))
						.append(OUTPUT_CSV_DELIMITER)
						.append(Double.toString(point.getPrice()))
						.append("\n");
					trainingFileWriter.write(trainingDataBuffer.toString());
				
				} else {
					break;
				}
			}

		} catch (Exception ex) {
			logger.error("Error writing data to file " + trainingFileName);
			throw ex;
		} finally {
			if (trainingFileWriter != null) {
				trainingFileWriter.closeFile();
			}
		}
	}

	private void writeTestingDataToCSV(LSTMAnalysisState analysisState) throws Exception {

		List<SpotHistoryPoint> originalHistoryPointList = analysisState.getOriginalHistoryPointList();
		Date trainingEndDate = configuration.getTrainingEndDate();
		
		String testingFileName = LSTMFileNameHelper.getOutputFileName(configuration, "gnuplot-testing-data");
		CsvWrapperWriter testingFileWriter = new CsvWrapperWriter(testingFileName, OUTPUT_CSV_DELIMITER);
		try {

			testingFileWriter.openFile();
			SpotHistoryPoint lastTrainingPoint = null;
			for (int i=0; i<originalHistoryPointList.size(); i++) {

				SpotHistoryPoint point = originalHistoryPointList.get(i);
				if (point.getDateObject().getTime() > trainingEndDate.getTime()) {
					
					StringBuilder testingDataBuffer = new StringBuilder();
					if (lastTrainingPoint != null) {
						testingDataBuffer.append(OUT_DF.format(lastTrainingPoint.getDateObject()))
							.append(OUTPUT_CSV_DELIMITER)
							.append(Double.toString(lastTrainingPoint.getPrice()))
							.append("\n");
						lastTrainingPoint = null;
					}

					testingDataBuffer.append(OUT_DF.format(point.getDateObject()))
						.append(OUTPUT_CSV_DELIMITER)
						.append(Double.toString(point.getPrice()))
						.append("\n");
					testingFileWriter.write(testingDataBuffer.toString());

				} else {
					lastTrainingPoint = point;
				}
			}

		} catch (Exception ex) {
			logger.error("Error writing data to file " + testingFileName);
			throw ex;
		} finally {
			if (testingFileWriter != null) {
				testingFileWriter.closeFile();
			}
		}
	}

	private void writePredictedDataToCSV(LSTMAnalysisState analysisState) throws Exception {

		List<SpotHistoryPoint> spotHistoryPointList = analysisState.getSpotHistoryPointList();
		double[] predictedData = analysisState.getPredictedData();
		
		String predictedFileName = LSTMFileNameHelper.getOutputFileName(configuration, "gnuplot-predicted-data");
		CsvWrapperWriter predictedFileWriter = new CsvWrapperWriter(predictedFileName, OUTPUT_CSV_DELIMITER);
		try {

			predictedFileWriter.openFile();
			int initialIndex = spotHistoryPointList.size() - predictedData.length;
			for (int i=initialIndex; i<spotHistoryPointList.size(); i++) {
				
				SpotHistoryPoint point = spotHistoryPointList.get(i);
				StringBuilder predictedDataBuffer = new StringBuilder();
				predictedDataBuffer.append(OUT_DF.format(point.getDateObject()))
					.append(OUTPUT_CSV_DELIMITER)
					.append(Double.toString(predictedData[i - initialIndex]))
					.append("\n");
				predictedFileWriter.write(predictedDataBuffer.toString());
			}

		} catch (Exception ex) {
			logger.error("Error writing data to file " + predictedFileName);
			throw ex;
		} finally {
			if (predictedFileWriter != null) {
				predictedFileWriter.closeFile();
			}
		}
	}

	private void writeGNUPlotScript(LSTMAnalysisState analysisState) throws Exception {

		logger.debug("Writing GNUPlot script");

		List<SpotHistoryPoint> spotHistoryPointList = analysisState.getSpotHistoryPointList();
		SpotHistoryPoint firstPoint = spotHistoryPointList.get(0);
		SpotHistoryPoint lastPoint = spotHistoryPointList.get(spotHistoryPointList.size() - 1);
		
		String trainingFileName = LSTMFileNameHelper.getOutputFileName(configuration, "gnuplot-training-data", false);
		String testingFileName = LSTMFileNameHelper.getOutputFileName(configuration, "gnuplot-testing-data", false);
		String predictedFileName = LSTMFileNameHelper.getOutputFileName(configuration, "gnuplot-predicted-data", false);
		
		String pngFileName = LSTMFileNameHelper.getOutputFileName(configuration, "gnuplot-script", false, ".png");
		String scriptFileName = LSTMFileNameHelper.getOutputFileName(configuration, "gnuplot-script");
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(scriptFileName);

		try {
			csvWriter.openFile();
			String title = configuration.getInstanceType()
					+ " from " + OUT_DF_DATE_ONLY.format(firstPoint.getDateObject())
					+ " to " + OUT_DF_DATE_ONLY.format(lastPoint.getDateObject())
					+ " " + configuration.getLabel();

			csvWriter.writeLine("reset");
			csvWriter.writeLine("set title \"" + title + "\"");
			csvWriter.writeLine("set xlabel \"Date (dd/mm)\"");
			csvWriter.writeLine("set xdata time");
			csvWriter.writeLine("set timefmt \"%H:%M:%S %d/%m/%Y\"");
			csvWriter.writeLine("set xrange [\"00:00:00 " + OUT_DF_DATE_ONLY.format(firstPoint.getDateObject())
					+ "\":\"23:59:59 " + OUT_DF_DATE_ONLY.format(lastPoint.getDateObject()) + "\"]");
			csvWriter.writeLine("set format x \"%d/%m\"");
			csvWriter.writeLine("set ylabel \"Price (USD/hour)\"");
			csvWriter.writeLine("set autoscale y");
			csvWriter.writeLine("set ytics");
			csvWriter.writeLine("set key left top box");
			csvWriter.writeLine("set grid");
			csvWriter.writeLine("set datafile separator \";\"");
			csvWriter.writeLine("set terminal png size 1300,650");
			
			File f = new File(configuration.getInputCsvPath());
			csvWriter.writeLine("cd '" + f.getAbsolutePath() + "'");
			csvWriter.writeLine("set output \"" + pngFileName + "\"");
			
			csvWriter.writeLine("plot \"" + trainingFileName
					+ "\" using 1:2 axes x1y1 with lines lw 2 lt 1 lc rgb \"#0000FF\" title 'Training', \\");
			if (configuration.isGnuplotShowTestingData()) {
				csvWriter.writeLine("     \"" + testingFileName
					+ "\" using 1:2 axes x1y1 with lines lw 2 lt 1 lc rgb \"#FF0000\" title 'Testing', \\");
			}
			csvWriter.writeLine("     \"" + predictedFileName
					+ "\" using 1:2 axes x1y1 with lines lw 2 lt 1 lc rgb \"#00FF00\" title 'Predicted'");

		} catch (Exception ex) {
			logger.error("Error writing data to file " + scriptFileName);
			throw ex;

		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
	}
	
	private void calculatePredictionErrorData(LSTMAnalysisState analysisState) throws Exception {

		logger.debug("Calculating Prediction Error Data (MSE and RMSE)");
		
		double[] testingData = analysisState.getTestingData();
		double[] predictedData = analysisState.getPredictedData();
		
		double sum = 0D;
		for (int i=0; i<predictedData.length; i++) {
			
			int j = i * (configuration.getNumberOfFeatures() - 1);
			double error = testingData[j] - predictedData[i];
			sum = sum + Math.pow(error, 2);
		}

		analysisState.setPredictionAverageMSE(
				sum / (double) predictedData.length);
		analysisState.setPredictionAverageRMSE(
				Math.pow(analysisState.getPredictionAverageMSE(), 0.5));
	}
	
	private void writeErrorDataToCSV(LSTMAnalysisState analysisState) throws Exception {

		logger.debug("Writing Error Data");
		
		List<LSTMErrorData> trainingErrorDataList = analysisState.getTrainingErrorDataList();
		String erroFileName = LSTMFileNameHelper.getOutputFileName(configuration, "error-data");
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(erroFileName, OUTPUT_CSV_DELIMITER);
		try {
			csvWriter.openFile();
			for (LSTMErrorData errorData: trainingErrorDataList) {
				StringBuilder dataBuffer = new StringBuilder();
				dataBuffer.append(Integer.toString(errorData.getEpoch()))
						.append(OUTPUT_CSV_DELIMITER)
						.append(String.format(FLOAT_MASK, errorData.getAverageMSE()))
						.append(OUTPUT_CSV_DELIMITER)
						.append(String.format(FLOAT_MASK, errorData.getAverageRMSE()));
				csvWriter.writeLine(dataBuffer.toString());
			}
			
		} catch (Exception ex) {
			logger.error("Error writing data to file " + erroFileName);
			throw ex;

		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
	}
	
	private void writeFinalReport(LSTMAnalysisState analysisState) throws Exception {
		
		logger.debug("Writing Final Report");
		
		// Spot point list and training data
		List<SpotHistoryPoint> spotHistoryPointList = analysisState.getSpotHistoryPointList();
		double[] trainingData = analysisState.getTrainingData();
		
		// First and last points from price history
		SpotHistoryPoint firstPoint = spotHistoryPointList.get(0);
		SpotHistoryPoint lastPoint = spotHistoryPointList.get(spotHistoryPointList.size() - 1);
		
		// Data accumulators
		Accumulator mseAcum = new Accumulator();
		Accumulator rmseAcum = new Accumulator();
		
		List<LSTMErrorData> trainingErrorDataList = analysisState.getTrainingErrorDataList();
		for (LSTMErrorData errorData: trainingErrorDataList) {
			mseAcum.addValue(errorData.getAverageMSE());
			rmseAcum.addValue(errorData.getAverageRMSE());
		}
		analysisState.setTrainingAverageMSE(mseAcum.getMean());
		analysisState.setTrainingAverageRMSE(rmseAcum.getMean());
		
		String reportFileName = LSTMFileNameHelper.getOutputFileName(configuration, "final-report");
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(reportFileName, OUTPUT_CSV_DELIMITER);
		try {
			csvWriter.openFile();
			csvWriter.writeLine("Start time", OUT_DF.format(new Date(analysisState.getStartTimeMilis())));
			csvWriter.writeLine("Label", configuration.getLabel());
			csvWriter.writeLine("Instance type", configuration.getInstanceType());
			csvWriter.writeLine("Availability zone filter", configuration.getAvailabilityZoneFilter());
			csvWriter.writeLine("First point", OUT_DF.format(firstPoint.getDateObject()));
			csvWriter.writeLine("Last point", OUT_DF.format(lastPoint.getDateObject()));
			csvWriter.writeLine("Occurrences read", Integer.toString(spotHistoryPointList.size()));
			csvWriter.writeLine("Occurrences analysed", Integer.toString(trainingData.length));

			csvWriter.writeLine("Training Data Proportion",
					Double.toString(configuration.getTrainingDataProportion()));
			if (configuration.getTrainingStartDate() != null) {
				csvWriter.writeLine("Training Start Date", OUT_DF.format(configuration.getTrainingStartDate()));
			} else {
				csvWriter.writeLine("Training Start Date", "");
			}
			if (configuration.getTrainingEndDate() != null) {
				csvWriter.writeLine("Training End Date", OUT_DF.format(configuration.getTrainingEndDate()));
			} else {
				csvWriter.writeLine("Training End Date", "");
			}

			if (configuration.getPredictionEndDate() != null) {
				csvWriter.writeLine("Prediction End Date", OUT_DF.format(configuration.getPredictionEndDate()));
			} else {
				csvWriter.writeLine("Prediction End Date", "");
			}

			csvWriter.writeLine("Number Of Features", Integer.toString(configuration.getNumberOfFeatures()));
			csvWriter.writeLine("Standardize Data", Boolean.toString(configuration.isStandardizeData()));
			csvWriter.writeLine("Regularization Timestep in Hours", Integer.toString(
					configuration.getRegularizationTimestepInHours()));

			csvWriter.writeLine("Network Type", Byte.toString(configuration.getType()));
			csvWriter.writeLine("Seed", Long.toString(configuration.getSeed()));
			csvWriter.writeLine("Gradient Descent Updater", Byte.toString(configuration.getGradientDescentUpdater()));
			csvWriter.writeLine("Learning Rate", Double.toString(configuration.getLearningRate()));
			csvWriter.writeLine("Input Nodes", Integer.toString(configuration.getInputNodes()));
			csvWriter.writeLine("Output Nodes", Integer.toString(configuration.getOutputNodes()));
			csvWriter.writeLine("Hidden Layer 1 Nodes", Integer.toString(configuration.getHiddenLayer1Nodes()));
			csvWriter.writeLine("Hidden Layer 2 Nodes", Integer.toString(configuration.getHiddenLayer2Nodes()));
			csvWriter.writeLine("Dense Layer Nodes", Integer.toString(configuration.getDenseLayerNodes()));
			csvWriter.writeLine("Number Of Epochs", Integer.toString(configuration.getNumberOfEpochs()));
			csvWriter.writeLine("Batch Size", Integer.toString(configuration.getBatchSize()));

			csvWriter.writeLine("Training Average MSE", String.format(FLOAT_MASK, analysisState.getTrainingAverageMSE()));
			csvWriter.writeLine("Training Average RMSE", String.format(FLOAT_MASK, analysisState.getTrainingAverageRMSE()));
			csvWriter.writeLine("Prediction Average MSE", String.format(FLOAT_MASK, analysisState.getPredictionAverageMSE()));
			csvWriter.writeLine("Prediction Average RMSE", String.format(FLOAT_MASK, analysisState.getPredictionAverageRMSE()));
			csvWriter.writeLine("Training time (ms)", Long.toString(configuration.getTrainingTimeMilis()));
			csvWriter.writeLine("Total speedup (ms)", Long.toString(analysisState.getEndTimeMilis() - analysisState.getStartTimeMilis()));
			csvWriter.writeLine("End time", OUT_DF.format(new Date(analysisState.getEndTimeMilis())));

		} catch (Exception ex) {
			logger.error("Error writing data to file " + reportFileName);
			throw ex;

		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
	}
}
