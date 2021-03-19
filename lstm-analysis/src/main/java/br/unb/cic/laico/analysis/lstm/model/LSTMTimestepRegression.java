package br.unb.cic.laico.analysis.lstm.model;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.CollectionInputSplit;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.conf.preprocessor.FeedForwardToRnnPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.RnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.evaluation.regression.RegressionEvaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.IUpdater;
import org.nd4j.linalg.learning.config.Nadam;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import br.unb.cic.laico.analysis.lstm.config.LSTMConfiguration;
import br.unb.cic.laico.analysis.lstm.error.LSTMErrorData;
import br.unb.cic.laico.analysis.lstm.state.LSTMAnalysisState;

public class LSTMTimestepRegression implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(LSTMTimestepRegression.class);

	private LSTMConfiguration configuration;

	public LSTMTimestepRegression(LSTMConfiguration configuration) {
		this.configuration = configuration;
	}

	public LSTMAnalysisState doAnalysis(LSTMAnalysisState analysisState) throws Exception {

		logger.debug("Neural network training started");
		
		// Initialize training and testing file names from analysis state
		String trainingDataFileName = analysisState.getOutputTrainingDataFileName();
		String testingDataFileName = analysisState.getOutputTestingDataFileName();
		
		// Initialize list of training error data
		List<LSTMErrorData> trainingErrorDataList = new ArrayList<LSTMErrorData>(configuration.getNumberOfEpochs());

		// Create iterators and data sets from files
		DataSetIterator trainingDataSetIterator = readCSVDataset(trainingDataFileName);
		DataSet trainingDataSet = trainingDataSetIterator.next();
		DataSetIterator testingDataSetIterator = readCSVDataset(testingDataFileName);
		DataSet testingDataSet = testingDataSetIterator.next();

		// Normalize data, including labels (fitLabel=true)
		NormalizerMinMaxScaler normalizer = new NormalizerMinMaxScaler(0, 1);
		normalizer.fitLabel(true);
		normalizer.fit(trainingDataSet);
		normalizer.transform(trainingDataSet);
		normalizer.transform(testingDataSet);

		// LSTM network initialization
		MultiLayerNetwork net;
		if (configuration.getType() == LSTMConfiguration.TYPE_BASIC_LSTM) {
			net = getBasicLSTMNetworkModel(); 
		} else if (configuration.getType() == LSTMConfiguration.TYPE_2LAYERS_LSTM) {
			net = get2LayersLSTMNetworkModel();
		} else if (configuration.getType() == LSTMConfiguration.TYPE_3LAYERS_DENSE_LSTM) {
			net = get3LayersDenseLSTMNetworkModel();
		} else {
			throw new IllegalArgumentException("Invalid LSTM configuration type.");
		}

		// Start training time
		long startTimeMilis = System.currentTimeMillis();

		// Train the network, evaluating the test set performance at each epoch
		for (int i = 0; i < configuration.getNumberOfEpochs(); i++) {

			// Train the network
			net.fit(trainingDataSet);
			logger.debug("Epoch " + i + " complete. Error evaluation:");

			// Run regression evaluation on our single column input
			RegressionEvaluation evaluation = net.evaluateRegression(testingDataSetIterator);
			logger.debug("\n" + evaluation.stats());
			
			// Collect training error data
			LSTMErrorData errorData = new LSTMErrorData(i,
					evaluation.averageMeanAbsoluteError(),
					evaluation.averageMeanSquaredError(),
					evaluation.averagerootMeanSquaredError(),
					evaluation.averageRSquared());
			trainingErrorDataList.add(errorData);
		}
		
		// Put training error data list in analysis state object
		analysisState.setTrainingErrorDataList(trainingErrorDataList);
		
		// Collect training time and set it in configuration
		long endTimeMilis = System.currentTimeMillis();
		configuration.setTrainingTimeMilis(endTimeMilis - startTimeMilis);

		// Initialize rrnTimeStemp with train data and predict test data
		net.rnnTimeStep(trainingDataSet.getFeatures());
		INDArray predicted = net.rnnTimeStep(testingDataSet.getFeatures());

		// Revert data back to original values for plotting
		normalizer.revert(trainingDataSet);
		normalizer.revert(testingDataSet);
		normalizer.revertLabels(predicted);
		
		// Put training data in resulting array of analysis state object
		double[] trainingData = new double[(int) trainingDataSet.getFeatures().length()];
		for (int i=0; i<trainingData.length; i++) {
			trainingData[i] = trainingDataSet.getFeatures().getDouble(i);
		}
		analysisState.setTrainingData(trainingData);
		
		// Put testing data in resulting array of analysis state object
		double[] testingData = new double[(int) testingDataSet.getFeatures().length()];
		for (int i=0; i<testingData.length; i++) {
			testingData[i] = testingDataSet.getFeatures().getDouble(i);
		}
		analysisState.setTestingData(testingData);

		// Put predicted data in resulting array of analysis state object
		double[] predictedData = new double[(int) predicted.length()];
		for (int i=0; i<predictedData.length; i++) {
			predictedData[i] = predicted.getDouble(i);
		}
		analysisState.setPredictedData(predictedData);
		
		logger.debug("Neural network training finished");
		
		// Return analysis state object
		return analysisState;
	}

	private DataSetIterator readCSVDataset(String filename) throws Exception {

		List<URI> trainURIList = new ArrayList<URI>(1);
		File file = new File(filename);
		trainURIList.add(file.toURI());
		SequenceRecordReader trainReader = new CSVSequenceRecordReader(0, ";");
		trainReader.initialize(new CollectionInputSplit(trainURIList));
		return new SequenceRecordReaderDataSetIterator(trainReader, configuration.getBatchSize(), -1, 1, true);
	}
	
	private IUpdater getGradientDescentUpdater() {
		
		if (configuration.getGradientDescentUpdater() == LSTMConfiguration.UPDATER_ADAM) {
			return new Adam(configuration.getLearningRate());
		} else if (configuration.getGradientDescentUpdater() == LSTMConfiguration.UPDATER_NADAM) {
			return new Nadam(configuration.getLearningRate());
		}
		return new Sgd(configuration.getLearningRate());
	}

	private MultiLayerNetwork getBasicLSTMNetworkModel() {

		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(configuration.getSeed())
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.weightInit(WeightInit.XAVIER)
				.updater(getGradientDescentUpdater())
				.list()
				.layer(new LSTM.Builder()
						.name("lstm1")
						.activation(Activation.TANH)
						.nIn(configuration.getInputNodes())
						.nOut(configuration.getHiddenLayer1Nodes())
						.build())
				.layer(new RnnOutputLayer.Builder()
						.name("output")
						.activation(Activation.IDENTITY)
						.nOut(configuration.getOutputNodes())
						.lossFunction(LossFunctions.LossFunction.MSE)
						.build())
				.build();
		MultiLayerNetwork net = new MultiLayerNetwork(conf);
		net.init();

		logger.debug(net.summary());
		return net;
	}

	private MultiLayerNetwork get2LayersLSTMNetworkModel() {

		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.trainingWorkspaceMode(WorkspaceMode.ENABLED)
				.inferenceWorkspaceMode(WorkspaceMode.ENABLED)
				.seed(configuration.getSeed())
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.weightInit(WeightInit.XAVIER)
				.updater(getGradientDescentUpdater())
				.list()
				.layer(new LSTM.Builder()
						.name("lstm1")
						.activation(Activation.TANH)
						.nIn(configuration.getInputNodes())
						.nOut(configuration.getHiddenLayer1Nodes())
						.build())
				.layer(new LSTM.Builder()
						.name("lstm2")
						.activation(Activation.TANH)
						.nOut(configuration.getHiddenLayer2Nodes())
						.build())
				.layer(new RnnOutputLayer.Builder()
						.name("output")
						.activation(Activation.IDENTITY)
						.nOut(configuration.getOutputNodes())
						.lossFunction(LossFunctions.LossFunction.MSE)
						.build())
				.build();
		MultiLayerNetwork net = new MultiLayerNetwork(conf);
		net.init();
		
		logger.debug(net.summary());
		return net;
	}
	
	private MultiLayerNetwork get3LayersDenseLSTMNetworkModel() {

		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(configuration.getSeed())
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.weightInit(WeightInit.XAVIER)
				.updater(getGradientDescentUpdater())
				.list()
				.layer(new LSTM.Builder()
						.name("lstm1")
						.activation(Activation.TANH)
						.nIn(configuration.getInputNodes())
						.nOut(configuration.getHiddenLayer1Nodes())
						.build())
				.layer(new LSTM.Builder()
						.name("lstm2")
						.activation(Activation.TANH)
						.nIn(configuration.getHiddenLayer1Nodes())
						.nOut(configuration.getHiddenLayer2Nodes())
						.build())
                .layer(new DenseLayer.Builder()
                		.name("dense")
						.nIn(configuration.getHiddenLayer2Nodes())
                		.nOut(configuration.getDenseLayerNodes())
                        .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                        .gradientNormalizationThreshold(
                        		configuration.getGradientNormalizationThreshold())
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.RELU)
                        .build())
				.layer(new RnnOutputLayer.Builder()
						.name("output")
						.activation(Activation.IDENTITY)
						.nIn(configuration.getHiddenLayer1Nodes())
						.nOut(configuration.getOutputNodes())
						.lossFunction(LossFunctions.LossFunction.MSE)
						.build())
                .inputPreProcessor(2, new RnnToFeedForwardPreProcessor())
                .inputPreProcessor(3, new FeedForwardToRnnPreProcessor())
                .backpropType(BackpropType.TruncatedBPTT)
				.build();
		MultiLayerNetwork net = new MultiLayerNetwork(conf);
		net.init();
		
		logger.debug(net.summary());
		return net;
	}
}
