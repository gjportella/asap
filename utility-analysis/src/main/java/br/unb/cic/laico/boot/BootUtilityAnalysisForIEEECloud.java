package br.unb.cic.laico.boot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import br.unb.cic.laico.analysis.utilitybased.UtilityBasedAnalysisBuilder;
import br.unb.cic.laico.analysis.utilitybased.model.AnalysisOutput;
import br.unb.cic.laico.conversion.csv.CsvWrapperWriter;

public class BootUtilityAnalysisForIEEECloud {

	private static Logger logger = Logger.getLogger(BootUtilityAnalysisForIEEECloud.class);

	private static final String OUTPUT_CSV_DELIMITER = ";";
	
	private String parseHeader() {
		
		StringBuilder header = new StringBuilder();
		header.append("startTime")
				.append(OUTPUT_CSV_DELIMITER).append("instanceType")
				.append(OUTPUT_CSV_DELIMITER).append("availabilityZoneFilter")
				.append(OUTPUT_CSV_DELIMITER).append("analysisLabel")
				.append(OUTPUT_CSV_DELIMITER).append("occurrencesRead")
				.append(OUTPUT_CSV_DELIMITER).append("occurrencesRemoved")
				.append(OUTPUT_CSV_DELIMITER).append("sigma")
				.append(OUTPUT_CSV_DELIMITER).append("theta")
				.append(OUTPUT_CSV_DELIMITER).append("onDemandPrice")
				.append(OUTPUT_CSV_DELIMITER).append("averagePrice")
				.append(OUTPUT_CSV_DELIMITER).append("priceVariance")
				.append(OUTPUT_CSV_DELIMITER).append("averageBid")
				.append(OUTPUT_CSV_DELIMITER).append("averageAvailability")
				.append(OUTPUT_CSV_DELIMITER).append("fixedBidAverageAvailability")
				.append(OUTPUT_CSV_DELIMITER).append("fixedBidAverageBid")
				.append(OUTPUT_CSV_DELIMITER).append("fixedBidAverageFirstChunk")
				.append(OUTPUT_CSV_DELIMITER).append("fixedBidDurabilityEqWindow")
				.append(OUTPUT_CSV_DELIMITER).append("hourlyBidAverageAvailability")
				.append(OUTPUT_CSV_DELIMITER).append("hourlyBidAverageBid")
				.append(OUTPUT_CSV_DELIMITER).append("hourlyBidAverageFirstChunk")
				.append(OUTPUT_CSV_DELIMITER).append("hourlyBidDurabilityEqWindow")
				.append(OUTPUT_CSV_DELIMITER).append("variableBidAverageAvailability")
				.append(OUTPUT_CSV_DELIMITER).append("variableBidAverageBid")
				.append(OUTPUT_CSV_DELIMITER).append("variableBidAverageFirstChunk")
				.append(OUTPUT_CSV_DELIMITER).append("variableBidDurabilityEqWindow")
				.append(OUTPUT_CSV_DELIMITER).append("utilityFunctionAverageSpeedup")
				.append(OUTPUT_CSV_DELIMITER).append("totalSpeedup");
		return header.toString();
	}
	
	private String parseAnalysisOutputToString(AnalysisOutput output) {
		
		StringBuilder data = new StringBuilder();
		data.append(Long.toString(output.getStartTime()))
				.append(OUTPUT_CSV_DELIMITER).append(output.getInstanceType())
				.append(OUTPUT_CSV_DELIMITER).append(output.getAvailabilityZoneFilter())
				.append(OUTPUT_CSV_DELIMITER).append(output.getAnalysisLabel())
				.append(OUTPUT_CSV_DELIMITER).append(Integer.toString(output.getOccurrencesRead()))
				.append(OUTPUT_CSV_DELIMITER).append(Integer.toString(output.getOccurrencesRemoved()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getSigma()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getTheta()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getOnDemandPrice()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getAveragePrice()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getPriceVariance()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getAverageBid()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getAverageAvailability()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getFixedBidAverageAvailability()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getFixedBidAverageBid()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getFixedBidAverageFirstChunk()))
				.append(OUTPUT_CSV_DELIMITER).append(Integer.toString(output.getFixedBidDurabilityEqWindow()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getHourlyBidAverageAvailability()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getHourlyBidAverageBid()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getHourlyBidAverageFirstChunk()))
				.append(OUTPUT_CSV_DELIMITER).append(Integer.toString(output.getHourlyBidDurabilityEqWindow()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getVariableBidAverageAvailability()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getVariableBidAverageBid()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getVariableBidAverageFirstChunk()))
				.append(OUTPUT_CSV_DELIMITER).append(Integer.toString(output.getVariableBidDurabilityEqWindow()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(output.getUtilityFunctionAverageSpeedup()))
				.append(OUTPUT_CSV_DELIMITER).append(Long.toString(output.getTotalSpeedup()));
		return data.toString();
	}

	private AnalysisOutput runAnalysis(String basePath, String instanceType, double theta, double sigma) {

		String avZoneFilter = "us-east-1c";
		String analysisLabel = "SeptemberToNovember2016";
		double eta = UtilityBasedAnalysisBuilder.DEFAULT_OD_PRICE_FACTOR_LOWER_LIMIT;
		byte utilitySetting = UtilityBasedAnalysisBuilder.UTILITY_SETTING_AVAILABILITY;
		String inputCsvPath = basePath + instanceType + "/";
		String[] inputCsvFileNames = new String[] {
				instanceType + ".2016-09.txt",
				instanceType + ".2016-10.txt",
				instanceType + ".2016-11.txt"
			};
		boolean runDayOfWeekAnalysis = true;
		boolean runFutureAvailability = false;

		AnalysisOutput output = null;
		try {
			UtilityBasedAnalysisBuilder builder = new UtilityBasedAnalysisBuilder(
					instanceType, avZoneFilter, analysisLabel,
					eta, theta, sigma, utilitySetting,
					inputCsvPath, inputCsvFileNames,
					runDayOfWeekAnalysis, runFutureAvailability);
			builder.doAnalysis();
			output = builder.getAnalysisOutput();

		} catch (Exception ex) {
			logger.error("Error processing " + instanceType
					+ " (theta= " + analysisLabel + "). Cause: " + ex.toString());
		}
		return output;
	}
	
	public void runApp() {

		// Output file parameters
		String basePath = "src/main/resources/ieee-cloud/";
		String outputFileName = basePath + "UtilityAnalysisForIEEECloud.txt";
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(outputFileName, OUTPUT_CSV_DELIMITER);

		// Analysis parameters
		String instanceType;
		double theta;
		double sigma;
		AnalysisOutput analysisOutput;
		List<AnalysisOutput> outputList = new ArrayList<AnalysisOutput>();
		
		// Run analysis for t1.micro
		instanceType = "t1.micro";
		theta = 1D;
		sigma = 0.001;
		analysisOutput = runAnalysis(basePath, instanceType, theta, sigma);
		outputList.add(analysisOutput);
		
		// Run analysis for c3.8xlarge
		instanceType = "c3.8xlarge";
		theta = 2D;
		sigma = 0.01;
		analysisOutput = runAnalysis(basePath, instanceType, theta, sigma);
		outputList.add(analysisOutput);

		// Run analysis for r3.2xlarge
		instanceType = "r3.2xlarge";
		theta = 3D;
		sigma = 0.01;
		analysisOutput = runAnalysis(basePath, instanceType, theta, sigma);
		outputList.add(analysisOutput);

		// Run analysis for g2.2xlarge
		instanceType = "g2.2xlarge";
		theta = 2D;
		sigma = 0.01;
		analysisOutput = runAnalysis(basePath, instanceType, theta, sigma);
		outputList.add(analysisOutput);

		// Run analysis for m4.10xlarge
		instanceType = "m4.10xlarge";
		theta = 2D;
		sigma = 0.01;
		analysisOutput = runAnalysis(basePath, instanceType, theta, sigma);
		outputList.add(analysisOutput);

		try {
			// Open output file
			csvWriter.openFile();

			// Write header to file
			String header = parseHeader();
			csvWriter.writeLine(header);
			
			// Write output to file 
			for (AnalysisOutput output: outputList) {
				String data = parseAnalysisOutputToString(output);
				csvWriter.writeLine(data);
			}

		} catch (Exception ex) {
			logger.error("Error writing data to file " + outputFileName);

		} finally {
			try {
				csvWriter.closeFile();
			} catch (Exception ex) {
				logger.error("Error closing file " + outputFileName);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {

		Locale.setDefault(Locale.ENGLISH);
		BootUtilityAnalysisForIEEECloud app = new BootUtilityAnalysisForIEEECloud();
		app.runApp();
	}
}
