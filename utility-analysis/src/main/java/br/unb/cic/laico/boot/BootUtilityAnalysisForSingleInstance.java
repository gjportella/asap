package br.unb.cic.laico.boot;

import java.util.Locale;

import br.unb.cic.laico.analysis.utilitybased.UtilityBasedAnalysisBuilder;

public class BootUtilityAnalysisForSingleInstance {

	public static final String AVZONE_US_EAST = "us-east-1b";
	
	private void runAnalysis(String instanceType) throws Exception {
		
		double eta = UtilityBasedAnalysisBuilder.DEFAULT_OD_PRICE_FACTOR_LOWER_LIMIT;
		double theta = UtilityBasedAnalysisBuilder.DEFAULT_OD_PRICE_FACTOR_UPPER_LIMIT;
		double sigma = UtilityBasedAnalysisBuilder.DEFAULT_ESTIMATION_ACCURACY;
		byte utilitySetting = UtilityBasedAnalysisBuilder.UTILITY_SETTING_AVAILABILITY;
		boolean runDayOfWeekAnalysis = false;
		boolean runFutureAvailability = true;

		String analysisLabel = "AnalysisFor_" + instanceType;
		String inputCsvPath = "src/main/resources/single-instance/";
		String[] inputCsvFileNames = new String[] { instanceType + ".txt" };
		UtilityBasedAnalysisBuilder builder;
		
		try {
			builder = new UtilityBasedAnalysisBuilder(
					instanceType, AVZONE_US_EAST, analysisLabel,
					eta, theta, sigma, utilitySetting,
					inputCsvPath, inputCsvFileNames,
					runDayOfWeekAnalysis, runFutureAvailability);
			builder.doAnalysis();

		} catch (Exception ex) {
			System.err.println("Error processing " + instanceType
					+ " / " + analysisLabel	+ ". Cause: " + ex.toString());
		}
	}
	
	public static void main(String[] args) throws Exception {

		Locale.setDefault(Locale.US);

		BootUtilityAnalysisForSingleInstance app
				= new BootUtilityAnalysisForSingleInstance();		
		String instanceType = "m5.2xlarge";
		app.runAnalysis(instanceType);
	}
}
