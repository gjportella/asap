package br.unb.cic.laico.analysis.lstm;

import java.util.List;

import org.apache.log4j.Logger;

import br.unb.cic.laico.analysis.lstm.config.LSTMConfiguration;
import br.unb.cic.laico.analysis.lstm.model.LSTMHistoryDataPreprocessor;
import br.unb.cic.laico.analysis.lstm.model.LSTMReportBuilder;
import br.unb.cic.laico.analysis.lstm.model.LSTMTimestepRegression;
import br.unb.cic.laico.analysis.lstm.state.LSTMAnalysisState;
import br.unb.cic.laico.data.ec2.SpotHistoryPoint;

public class LSTMBasedAnalysisBuilder implements LSTMBasedAnalysis {

	private static Logger logger = Logger.getLogger(LSTMBasedAnalysisBuilder.class);

	private LSTMAnalysisState analysisState;
	
	public LSTMBasedAnalysisBuilder() {
	}

	public void doAnalysis(LSTMConfiguration configuration) throws Exception {

		logger.debug("LSTM Based Analysis of Amazon EC2 SPOT Pricing History");

		// Step 1: process input SPOT history files (ETL)
		LSTMHistoryDataPreprocessor processor = new LSTMHistoryDataPreprocessor(configuration);
		LSTMAnalysisState analysisState = processor.doETL();

		// Step 3: run LSTM based analysis
		long startTimeMilis = System.currentTimeMillis();
		LSTMTimestepRegression regression = new LSTMTimestepRegression(configuration);
		regression.doAnalysis(analysisState);
		long endTimeMilis = System.currentTimeMillis();

		// Step 4: update start and end time in analysis state
		analysisState.setStartTimeMilis(startTimeMilis);
		analysisState.setEndTimeMilis(endTimeMilis);
		
		// Step 5: build reports
		LSTMReportBuilder report = new LSTMReportBuilder(configuration);
		report.buildReports(analysisState);
		
		// Step 6: updates analysis state object
		this.analysisState = analysisState;
	}

	public double getOndemandPrice() {
		return analysisState.getOndemandPrice();
	}

	public List<SpotHistoryPoint> getSpotHistoryPointList() {
		return analysisState.getSpotHistoryPointList();
	}

	public long getStartTimeMilis() {
		return analysisState.getStartTimeMilis();
	}

	public long getEndTimeMilis() {
		return analysisState.getEndTimeMilis();
	}

	public double getTrainingAverageMSE() {
		return analysisState.getTrainingAverageMSE();
	}

	public double getTrainingAverageRMSE() {
		return analysisState.getTrainingAverageRMSE();
	}
	
	public double getPredictionAverageMSE() {
		return analysisState.getPredictionAverageMSE();
	}

	public double getPredictionAverageRMSE() {
		return analysisState.getPredictionAverageRMSE();
	}
}
