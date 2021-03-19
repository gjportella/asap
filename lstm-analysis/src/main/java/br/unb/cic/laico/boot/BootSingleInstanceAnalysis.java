package br.unb.cic.laico.boot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.unb.cic.laico.analysis.lstm.LSTMBasedAnalysis;
import br.unb.cic.laico.analysis.lstm.LSTMBasedAnalysisBuilder;
import br.unb.cic.laico.analysis.lstm.config.LSTMConfiguration;

public class BootSingleInstanceAnalysis implements BootAnalysis {

	public void runAnalysis() throws Exception {

		// Availability zone and instance type
		String avZone = "us-east-1c";
		String instanceType = "c5n.2xlarge";

		// Training and testing periods
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
		Date trainingStartDate = sdf.parse("2020-04-01T00:00:00");
		Date trainingEndDate = sdf.parse("2020-04-21T23:59:59");
		Date predictionEndDate = sdf.parse("2020-04-30T23:59:59");

		// Training hyperparameters
		int features = 2;
		int predictions = 1;
		byte algorithm = LSTMConfiguration.UPDATER_ADAM;
		int hiddenNodes = 16;
		int epochs = 50;
		double learningRate = 0.005;
		byte type = LSTMConfiguration.TYPE_2LAYERS_LSTM;

		// LSTM Configuration object
		LSTMConfiguration lstmConfig = new LSTMConfiguration();
		lstmConfig.setInstanceType(instanceType);
		lstmConfig.setAvailabilityZoneFilter(avZone);
		lstmConfig.setInputCsvPath("src/main/resources/single-analysis/");
		lstmConfig.setInputCsvFileNames(
				new String[] {
						instanceType + ".2020-04.txt"
						});
		lstmConfig.setTrainingStartDate(trainingStartDate);
		lstmConfig.setTrainingEndDate(trainingEndDate);
		lstmConfig.setPredictionEndDate(predictionEndDate);
		lstmConfig.setGradientDescentUpdater(algorithm);
		lstmConfig.setHiddenLayer1Nodes(hiddenNodes);
		lstmConfig.setHiddenLayer2Nodes(hiddenNodes);
		lstmConfig.setDenseLayerNodes(hiddenNodes);
		lstmConfig.setNumberOfEpochs(epochs);
		lstmConfig.setLearningRate(learningRate);
		lstmConfig.setType(type);
		lstmConfig.setNumberOfFeatures(features);
		lstmConfig.setInputNodes(features - predictions);
		lstmConfig.setOutputNodes(predictions);
		lstmConfig.setGnuplotShowTestingData(true);
		lstmConfig.setRegularizationData(true);
		lstmConfig.setRegularizationTimestepInHours(1);

		// Instantiate and run LSTM analysis
		LSTMBasedAnalysis lstmAnalysis = new LSTMBasedAnalysisBuilder();
		lstmAnalysis.doAnalysis(lstmConfig);
	}
	
	public static void main(String[] args) throws Exception {

		Locale.setDefault(Locale.ENGLISH);
		BootAnalysis analysis = new BootSingleInstanceAnalysis();
		analysis.runAnalysis();
	}
}
