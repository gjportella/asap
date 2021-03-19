package br.unb.cic.laico.analysis.lstm.config;

import java.io.Serializable;
import java.util.Date;

public class LSTMConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final byte TYPE_BASIC_LSTM = 1;
	public static final byte TYPE_2LAYERS_LSTM = 2;
	public static final byte TYPE_3LAYERS_DENSE_LSTM = 3;

	public static final byte UPDATER_SGD = 1;
	public static final byte UPDATER_ADAM = 2;
	public static final byte UPDATER_NADAM = 3;

	public static final int DEFAULT_SEED = 123;
	public static final int DEFAULT_BATCH_SIZE = 1024;
	public static final int DEFAULT_GRADIENT_NORMALIZATION_THRESHOLD = 10;
	public static final int DEFAULT_TIMESTEP_IN_HOURS = 4;

	private Date creationDate;

	private String instanceType;
	private String availabilityZoneFilter;
	private double trainingDataProportion;
	private Date trainingStartDate;
	private Date trainingEndDate;
	private Date predictionEndDate;
	private int numberOfFeatures;
	private String inputCsvPath;
	private String[] inputCsvFileNames;

	private byte type;
	private long seed;
	private byte gradientDescentUpdater;
	private double learningRate;
	private int inputNodes;
	private int outputNodes;
	private int hiddenLayer1Nodes;
	private int hiddenLayer2Nodes;
	private int denseLayerNodes;
	private int gradientNormalizationThreshold;
	private int numberOfEpochs;
	private int batchSize;

	private boolean gnuplotShowTestingData;
	private boolean regularizationData;
	private int regularizationTimestepInHours;

	private long trainingTimeMilis;

	public LSTMConfiguration() {

		// Basic configuration parameters
		this.creationDate = new Date(System.currentTimeMillis());
		this.type = TYPE_BASIC_LSTM;
		this.gradientDescentUpdater = UPDATER_SGD;
		this.gradientNormalizationThreshold = DEFAULT_GRADIENT_NORMALIZATION_THRESHOLD;
		this.seed = DEFAULT_SEED;
		this.batchSize = DEFAULT_BATCH_SIZE;
		this.gnuplotShowTestingData = true;
		this.regularizationData = false;
		this.regularizationTimestepInHours = DEFAULT_TIMESTEP_IN_HOURS;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

	public String getAvailabilityZoneFilter() {
		return availabilityZoneFilter;
	}

	public void setAvailabilityZoneFilter(String availabilityZoneFilter) {
		this.availabilityZoneFilter = availabilityZoneFilter;
	}

	public double getTrainingDataProportion() {
		return trainingDataProportion;
	}

	public Date getTrainingStartDate() {
		return trainingStartDate;
	}

	public void setTrainingStartDate(Date trainingStartDate) {
		this.trainingStartDate = trainingStartDate;
	}

	public void setTrainingDataProportion(double trainingDataProportion) {
		this.trainingDataProportion = trainingDataProportion;
	}

	public Date getTrainingEndDate() {
		return trainingEndDate;
	}

	public void setTrainingEndDate(Date trainingEndDate) {
		this.trainingEndDate = trainingEndDate;
	}

	public Date getPredictionEndDate() {
		return predictionEndDate;
	}

	public void setPredictionEndDate(Date predictionEndDate) {
		this.predictionEndDate = predictionEndDate;
	}

	public int getNumberOfFeatures() {
		return numberOfFeatures;
	}

	public void setNumberOfFeatures(int numberOfFeatures) {
		this.numberOfFeatures = numberOfFeatures;
	}

	public String getInputCsvPath() {
		return inputCsvPath;
	}

	public void setInputCsvPath(String inputCsvPath) {
		this.inputCsvPath = inputCsvPath;
	}

	public String[] getInputCsvFileNames() {
		return inputCsvFileNames;
	}

	public void setInputCsvFileNames(String[] inputCsvFileNames) {
		this.inputCsvFileNames = inputCsvFileNames;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public byte getGradientDescentUpdater() {
		return gradientDescentUpdater;
	}

	public void setGradientDescentUpdater(byte gradientDescentUpdater) {
		this.gradientDescentUpdater = gradientDescentUpdater;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public int getInputNodes() {
		return inputNodes;
	}

	public void setInputNodes(int inputNodes) {
		this.inputNodes = inputNodes;
	}

	public int getOutputNodes() {
		return outputNodes;
	}

	public void setOutputNodes(int outputNodes) {
		this.outputNodes = outputNodes;
	}

	public int getHiddenLayer1Nodes() {
		return hiddenLayer1Nodes;
	}

	public void setHiddenLayer1Nodes(int hiddenLayer1Nodes) {
		this.hiddenLayer1Nodes = hiddenLayer1Nodes;
	}

	public int getHiddenLayer2Nodes() {
		return hiddenLayer2Nodes;
	}

	public void setHiddenLayer2Nodes(int hiddenLayer2Nodes) {
		this.hiddenLayer2Nodes = hiddenLayer2Nodes;
	}

	public int getDenseLayerNodes() {
		return denseLayerNodes;
	}

	public void setDenseLayerNodes(int denseLayerNodes) {
		this.denseLayerNodes = denseLayerNodes;
	}

	public int getGradientNormalizationThreshold() {
		return gradientNormalizationThreshold;
	}

	public void setGradientNormalizationThreshold(int gradientNormalizationThreshold) {
		this.gradientNormalizationThreshold = gradientNormalizationThreshold;
	}

	public int getNumberOfEpochs() {
		return numberOfEpochs;
	}

	public void setNumberOfEpochs(int numberOfEpochs) {
		this.numberOfEpochs = numberOfEpochs;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public boolean isGnuplotShowTestingData() {
		return gnuplotShowTestingData;
	}

	public void setGnuplotShowTestingData(boolean gnuplotShowTestingData) {
		this.gnuplotShowTestingData = gnuplotShowTestingData;
	}

	public boolean isRegularizationData() {
		return regularizationData;
	}

	public void setRegularizationData(boolean regularizationData) {
		this.regularizationData = regularizationData;
	}

	public int getRegularizationTimestepInHours() {
		return regularizationTimestepInHours;
	}

	public void setRegularizationTimestepInHours(int regularizationTimestepInHours) {
		this.regularizationTimestepInHours = regularizationTimestepInHours;
	}

	public long getTrainingTimeMilis() {
		return trainingTimeMilis;
	}

	public void setTrainingTimeMilis(long trainingTimeMilis) {
		this.trainingTimeMilis = trainingTimeMilis;
	}

	public String getLabel() {
		StringBuilder label = new StringBuilder();
		label.append("LSTM-");
		// label.append("F").append(Integer.toString(this.getNumberOfFeatures()));
		label.append("A").append(Byte.toString(this.getGradientDescentUpdater()));
		label.append("H").append(Integer.toString(this.getHiddenLayer1Nodes()));
		label.append("E").append(Integer.toString(this.getNumberOfEpochs()));
		label.append("L").append(Double.toString(this.getLearningRate()));
		label.append("T").append(Byte.toString(this.getType()));
		return label.toString();
	}
}
