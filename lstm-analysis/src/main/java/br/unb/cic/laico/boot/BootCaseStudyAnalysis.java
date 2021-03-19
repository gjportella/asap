package br.unb.cic.laico.boot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.unb.cic.laico.analysis.lstm.LSTMBasedAnalysis;
import br.unb.cic.laico.analysis.lstm.LSTMBasedAnalysisBuilder;
import br.unb.cic.laico.analysis.lstm.config.LSTMConfiguration;

public class BootCaseStudyAnalysis implements BootAnalysis {

	// us-east-1b (API) = us-east-1c (web console)
	private static final String AVZONE = "us-east-1b";
	
	private static final String INPUT_PATH = "src/main/resources/case-study/";
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

	private void runAnalysis(Date trainingEndDate, Date predictionEndDate, String instanceType, int features,
			byte algorithm, int hiddenNodes, int epochs, double learningRate, byte type) throws Exception {

		// LSTM Configuration object
		LSTMConfiguration lstmConfig = new LSTMConfiguration();

		// Default parameters
		lstmConfig.setAvailabilityZoneFilter(AVZONE);
		lstmConfig.setInputCsvPath(INPUT_PATH);
		lstmConfig.setInputCsvFileNames(new String[] { instanceType + ".txt" });
		lstmConfig.setGradientNormalizationThreshold(10);
		lstmConfig.setSeed(123);
		lstmConfig.setBatchSize(32);
		lstmConfig.setOutputNodes(1);
		lstmConfig.setRegularizationData(false);
		lstmConfig.setGnuplotShowTestingData(false);

		// Case study parameters
		lstmConfig.setTrainingEndDate(trainingEndDate);
		lstmConfig.setPredictionEndDate(predictionEndDate);
		lstmConfig.setInstanceType(instanceType);
		lstmConfig.setNumberOfFeatures(features);
		lstmConfig.setInputNodes(features - 1);
		lstmConfig.setGradientDescentUpdater(algorithm);
		lstmConfig.setHiddenLayer1Nodes(hiddenNodes);
		lstmConfig.setHiddenLayer2Nodes(hiddenNodes);
		lstmConfig.setDenseLayerNodes(hiddenNodes);
		lstmConfig.setNumberOfEpochs(epochs);
		lstmConfig.setLearningRate(learningRate);
		lstmConfig.setType(type);

		// Run LSTM analysis
		LSTMBasedAnalysis lstmAnalysis = new LSTMBasedAnalysisBuilder();
		lstmAnalysis.doAnalysis(lstmConfig);
	}
	
	private void caseStudy_m52xlarge() throws Exception {

		Date trainingEndDate = SDF.parse("2021-01-13T00:00:00"); //SDF.parse("2021-01-13T11:59:59");
		Date predictionEndDate = SDF.parse("2021-01-21T23:59:59");
		String instanceType = "m5.2xlarge";
		int features = 2;
		byte algorithm = LSTMConfiguration.UPDATER_ADAM;
		int hiddenNodes = 32;
		int epochs = 200;
		double learningRate = 0.005;
		byte type = LSTMConfiguration.TYPE_2LAYERS_LSTM;
		runAnalysis(trainingEndDate, predictionEndDate, instanceType, features,
				algorithm, hiddenNodes, epochs,	learningRate, type);
	}
	
	private void caseStudy_i32xlarge() throws Exception {

		Date trainingEndDate = SDF.parse("2021-01-16T00:00:00"); //SDF.parse("2021-01-16T11:59:59");
		Date predictionEndDate = SDF.parse("2021-01-23T23:59:59");
		String instanceType = "i3.2xlarge";
		int features = 2;
		byte algorithm = LSTMConfiguration.UPDATER_ADAM;
		int hiddenNodes = 16;
		int epochs = 100;
		double learningRate = 0.005;
		byte type = LSTMConfiguration.TYPE_2LAYERS_LSTM;
		runAnalysis(trainingEndDate, predictionEndDate, instanceType, features,
				algorithm, hiddenNodes, epochs,	learningRate, type);
	}

	@Override
	public void runAnalysis() throws Exception {

		caseStudy_m52xlarge();
		caseStudy_i32xlarge();
	}
	
	public static void main(String[] args) throws Exception {

		Locale.setDefault(Locale.ENGLISH);
		BootAnalysis analysis = new BootCaseStudyAnalysis();
		analysis.runAnalysis();
	}
}
