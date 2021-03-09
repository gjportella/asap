package br.unb.cic.laico.boot;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.unb.cic.laico.analysis.lstm.LSTMBasedAnalysis;
import br.unb.cic.laico.analysis.lstm.LSTMBasedAnalysisBuilder;
import br.unb.cic.laico.analysis.lstm.config.LSTMConfiguration;

public class BootLSTMAnalysisForSingleInstance {

	public static final String AVZONE_US_EAST = "us-east-1c";

	public void runAnalysis() throws Exception {

		String instanceType = "c5n.2xlarge";
		String trainingStartDate = "2020-04-09T00:00:00";
		String trainingEndDate = "2020-04-30T23:59:59";
		String predictionEndDate = "2020-05-08T23:59:59";

		int features = 2;
		byte algorithm = LSTMConfiguration.UPDATER_ADAM;
		int hiddenNodes = 16;
		int epochs = 100;
		double learningRate = 0.005;
		byte type = LSTMConfiguration.TYPE_2LAYERS_LSTM;

		// LSTM Configuration object
		LSTMConfiguration lstmConfig = new LSTMConfiguration();
		lstmConfig.setInstanceType(instanceType);
		lstmConfig.setAvailabilityZoneFilter(AVZONE_US_EAST);
		lstmConfig.setInputCsvPath("src/main/resources/single-instance/");
		lstmConfig.setInputCsvFileNames(new String[] { instanceType + ".2020-04.txt", instanceType + ".2020-05.txt" });
		lstmConfig.setGradientNormalizationThreshold(10);
		lstmConfig.setSeed(123);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
		lstmConfig.setTrainingStartDate(sdf.parse(trainingStartDate));
		lstmConfig.setTrainingEndDate(sdf.parse(trainingEndDate));
		lstmConfig.setPredictionEndDate(sdf.parse(predictionEndDate));

		lstmConfig.setType(type);
		lstmConfig.setGradientDescentUpdater(algorithm);
		lstmConfig.setHiddenLayer1Nodes(hiddenNodes);
		lstmConfig.setHiddenLayer2Nodes(hiddenNodes);
		lstmConfig.setDenseLayerNodes(hiddenNodes);
		lstmConfig.setLearningRate(learningRate);
		lstmConfig.setNumberOfEpochs(epochs);
		lstmConfig.setBatchSize(32);

		lstmConfig.setNumberOfFeatures(features);
		lstmConfig.setInputNodes(features - 1);
		lstmConfig.setOutputNodes(1);
		lstmConfig.setStandardizeData(true);
		lstmConfig.setGnuplotShowTestingData(true);
		lstmConfig.setRegularizationTimestepInHours(1);

		// Run LSTM analysis
		LSTMBasedAnalysis lstmAnalysis = new LSTMBasedAnalysisBuilder();
		lstmAnalysis.doAnalysis(lstmConfig);
	}

	public static void main(String[] args) throws Exception {

		Locale.setDefault(Locale.US);
		BootLSTMAnalysisForSingleInstance app = new BootLSTMAnalysisForSingleInstance();
		app.runAnalysis();
	}
}
