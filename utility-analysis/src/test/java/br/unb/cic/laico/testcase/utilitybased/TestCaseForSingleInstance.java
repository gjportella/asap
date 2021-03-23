package br.unb.cic.laico.testcase.utilitybased;

import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import br.unb.cic.laico.analysis.utilitybased.UtilityBasedAnalysisBuilder;

public class TestCaseForSingleInstance {

	public TestCaseForSingleInstance() {
	}

	@Test
	public void testApp() {

		Locale.setDefault(Locale.ENGLISH);
		String instanceType = "m5.2xlarge";
		String avZone = "us-east-1b";
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
		
		Exception exception = null;
		try {
			builder = new UtilityBasedAnalysisBuilder(
					instanceType, avZone, analysisLabel,
					eta, theta, sigma, utilitySetting,
					inputCsvPath, inputCsvFileNames,
					runDayOfWeekAnalysis, runFutureAvailability);
			builder.doAnalysis();
		} catch (Exception ex) {
			exception = ex;
		}
		Assertions.assertNull(exception);
	}
}
