package br.unb.cic.laico.analysis.utility;

import java.util.List;

import org.apache.log4j.Logger;

import br.unb.cic.laico.analysis.utility.config.UtilityConfiguration;
import br.unb.cic.laico.analysis.utility.model.UtilityRegression;
import br.unb.cic.laico.analysis.utility.model.UtilityReportBuilder;
import br.unb.cic.laico.data.ec2.SpotHistoryPoint;

public class UtilityBasedAnalysisBuilder implements UtilityBasedAnalysis {

	private static Logger logger = Logger.getLogger(UtilityBasedAnalysisBuilder.class);

	private UtilityRegression regression;
	private UtilityReportBuilder report;

	public UtilityBasedAnalysisBuilder() {
	}
	
	public void doAnalysis(UtilityConfiguration configuration,
			List<SpotHistoryPoint> spotHistoryPointList) throws Exception {
		
		logger.debug("Utility Based Analysis of Amazon EC2 SPOT Pricing History");

		// Step 1: run utility-based analysis
		long startTimeMilis = System.currentTimeMillis();
		regression = new UtilityRegression(configuration, spotHistoryPointList);
		regression.calculateOptimalUtilities();
		regression.calculateFutureAvailability();
		long endTimeMilis = System.currentTimeMillis();
		
		// Step 2: build reports
		report = new UtilityReportBuilder(configuration, spotHistoryPointList, startTimeMilis, endTimeMilis);
		report.buildReports();
	}
	
	public double getMSE() {
		return report.getMSE();
	}

	public double getRMSE() {
		return report.getRMSE();
	}
	
	public double getFutureMSEForFixedBid() {
		return report.getFutureMSEForFixedBid();
	}

	public double getFutureMSEForVariableBid() {
		return report.getFutureMSEForVariableBid();
	}
}