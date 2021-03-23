package br.unb.cic.laico.boot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import br.unb.cic.laico.analysis.utilitybased.UtilityBasedAnalysisBuilder;
import br.unb.cic.laico.analysis.utilitybased.model.AnalysisOutput;
import br.unb.cic.laico.conversion.csv.CsvWrapperWriter;

public class BootUtilityExtensiveAnalysis {

	private static Logger logger = Logger.getLogger(BootUtilityExtensiveAnalysis.class);

	private static final String OUTPUT_CSV_DELIMITER = ";";

	private final String avZoneFilter = "us-east-1c";
	private String basePath = "src/main/resources/extensive-analysis/";

	private String[] instanceTypeArray = new String[] {
			"c5n.2xlarge",
			"c5n.9xlarge", 
			"g3s.xlarge", 
			"g4dn.8xlarge", 
			"g4dn.xlarge", 
			"i3.2xlarge", 
			"i3.8xlarge", 
			"inf1.6xlarge", 
			"m5.2xlarge", 
			"m5.4xlarge", 
			"p2.xlarge", 
			"p3.2xlarge", 
			"r4.2xlarge", 
			"r5.2xlarge"
		};

	private final String[] firstQuarter2020 = new String[] {
			"2020-01", "2020-02", "2020-03"
		};

	private final String[] secondQuarter2020 = new String[] {
			"2020-04", "2020-05", "2020-06"
		};
	
	private final String[] thirdQuarter2020 = new String[] {
			"2020-07", "2020-08", "2020-09"
		};

	private List<AnalysisOutput> runRegionUSEast1(String analysisLabel, String[] instanceTypes,
			String[] yearMonthLabels, String availabilityZoneFilter) {

		double eta = UtilityBasedAnalysisBuilder.DEFAULT_OD_PRICE_FACTOR_LOWER_LIMIT;
		double theta = UtilityBasedAnalysisBuilder.DEFAULT_OD_PRICE_FACTOR_UPPER_LIMIT;
		double sigma = UtilityBasedAnalysisBuilder.DEFAULT_ESTIMATION_ACCURACY;
		byte utilitySetting = UtilityBasedAnalysisBuilder.UTILITY_SETTING_AVAILABILITY;
		boolean runDayOfWeekAnalysis = false;
		boolean runFutureAvailability = true;

		String inputCsvPath;
		UtilityBasedAnalysisBuilder builder;
		List<AnalysisOutput> outputList = new ArrayList<AnalysisOutput>(instanceTypes.length);

		for (String instanceType: instanceTypes) {

			String[] inputCsvFileNames = new String[yearMonthLabels.length];
			for (int i=0; i<yearMonthLabels.length; i++) {
				inputCsvFileNames[i] = instanceType + "." + yearMonthLabels[i] + ".txt";
			}
			
			try {
				inputCsvPath = basePath + instanceType + "/us-east-1/";
				builder = new UtilityBasedAnalysisBuilder(
						instanceType, availabilityZoneFilter, analysisLabel,
						eta, theta, sigma, utilitySetting,
						inputCsvPath, inputCsvFileNames,
						runDayOfWeekAnalysis, runFutureAvailability);
				builder.doAnalysis();
				
				AnalysisOutput output = builder.getAnalysisOutput();
				outputList.add(output);

			} catch (Exception ex) {
				logger.error("Error processing " + instanceType
						+ " / " + analysisLabel	+ ". Cause: " + ex.toString());
			}
		}
		return outputList;
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
	
	public void runApp() {
		
		String outputFileName = basePath + "UtilityExtensiveAnalysis.txt";

		CsvWrapperWriter csvWriter = null;
		try {
			csvWriter = new CsvWrapperWriter(outputFileName, OUTPUT_CSV_DELIMITER);
			csvWriter.openFile();

			// Write file reader
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
			csvWriter.writeLine(header.toString());

			// First Quarter 2020 analysis
			String analysisLabel = "FirstQuarter2020";
			List<AnalysisOutput> outputList = runRegionUSEast1(analysisLabel,
					instanceTypeArray, firstQuarter2020, avZoneFilter);
			for (AnalysisOutput output: outputList) {
				String data = parseAnalysisOutputToString(output);
				csvWriter.writeLine(data);
			}

			// Second Quarter 2020 analysis
			analysisLabel = "SecondQuarter2020";
			outputList = runRegionUSEast1(analysisLabel,
					instanceTypeArray, secondQuarter2020, avZoneFilter);
			for (AnalysisOutput output: outputList) {
				String data = parseAnalysisOutputToString(output);
				csvWriter.writeLine(data);
			}
			
			// Third Quarter 2020 analysis
			analysisLabel = "ThirdQuarter2020";
			outputList = runRegionUSEast1(analysisLabel,
					instanceTypeArray, thirdQuarter2020, avZoneFilter);
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
		BootUtilityExtensiveAnalysis app = new BootUtilityExtensiveAnalysis();
		app.runApp();
	}
}
