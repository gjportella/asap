package br.unb.cic.laico.analysis.lstm.state;

import java.io.Serializable;
import java.util.List;

import br.unb.cic.laico.analysis.lstm.error.LSTMErrorData;
import br.unb.cic.laico.data.ec2.SpotHistoryPoint;

public class LSTMAnalysisState implements Serializable {

	private static final long serialVersionUID = 1L;

	// LSTMHistoryDataPreprocessor state attributes
	private double ondemandPrice;
	private List<SpotHistoryPoint> originalHistoryPointList;
	private List<SpotHistoryPoint> spotHistoryPointList;
	private String outputTrainingDataFileName;
	private String outputTestingDataFileName;
	private List<double[]> featuresBuffer;

	// LSTMTimestepRegression state attributes
	private double[] trainingData;
	private double[] testingData;
	private double[] predictedData;
	private List<LSTMErrorData> trainingErrorDataList;
	private long startTimeMilis;
	private long endTimeMilis;
	
	// LSTMReportBuilder state attributes
	private double trainingAverageMSE;
	private double trainingAverageRMSE;
	private double predictionAverageMSE;
	private double predictionAverageRMSE;

	public LSTMAnalysisState() {
	}

	public double getOndemandPrice() {
		return ondemandPrice;
	}

	public void setOndemandPrice(double ondemandPrice) {
		this.ondemandPrice = ondemandPrice;
	}

	public List<SpotHistoryPoint> getOriginalHistoryPointList() {
		return originalHistoryPointList;
	}

	public void setOriginalHistoryPointList(List<SpotHistoryPoint> originalHistoryPointList) {
		this.originalHistoryPointList = originalHistoryPointList;
	}

	public List<SpotHistoryPoint> getSpotHistoryPointList() {
		return spotHistoryPointList;
	}

	public void setSpotHistoryPointList(List<SpotHistoryPoint> spotHistoryPointList) {
		this.spotHistoryPointList = spotHistoryPointList;
	}

	public String getOutputTrainingDataFileName() {
		return outputTrainingDataFileName;
	}

	public void setOutputTrainingDataFileName(String outputTrainingDataFileName) {
		this.outputTrainingDataFileName = outputTrainingDataFileName;
	}

	public String getOutputTestingDataFileName() {
		return outputTestingDataFileName;
	}

	public void setOutputTestingDataFileName(String outputTestingDataFileName) {
		this.outputTestingDataFileName = outputTestingDataFileName;
	}

	public List<double[]> getFeaturesBuffer() {
		return featuresBuffer;
	}

	public void setFeaturesBuffer(List<double[]> featuresBuffer) {
		this.featuresBuffer = featuresBuffer;
	}

	public double[] getTrainingData() {
		return trainingData;
	}

	public void setTrainingData(double[] trainingData) {
		this.trainingData = trainingData;
	}

	public double[] getTestingData() {
		return testingData;
	}

	public void setTestingData(double[] testingData) {
		this.testingData = testingData;
	}

	public double[] getPredictedData() {
		return predictedData;
	}

	public void setPredictedData(double[] predictedData) {
		this.predictedData = predictedData;
	}

	public List<LSTMErrorData> getTrainingErrorDataList() {
		return trainingErrorDataList;
	}

	public void setTrainingErrorDataList(List<LSTMErrorData> trainingErrorDataList) {
		this.trainingErrorDataList = trainingErrorDataList;
	}

	public long getStartTimeMilis() {
		return startTimeMilis;
	}

	public void setStartTimeMilis(long startTimeMilis) {
		this.startTimeMilis = startTimeMilis;
	}

	public long getEndTimeMilis() {
		return endTimeMilis;
	}

	public void setEndTimeMilis(long endTimeMilis) {
		this.endTimeMilis = endTimeMilis;
	}

	public double getTrainingAverageMSE() {
		return trainingAverageMSE;
	}

	public void setTrainingAverageMSE(double trainingAverageMSE) {
		this.trainingAverageMSE = trainingAverageMSE;
	}

	public double getTrainingAverageRMSE() {
		return trainingAverageRMSE;
	}

	public void setTrainingAverageRMSE(double trainingAverageRMSE) {
		this.trainingAverageRMSE = trainingAverageRMSE;
	}

	public double getPredictionAverageMSE() {
		return predictionAverageMSE;
	}

	public void setPredictionAverageMSE(double predictionAverageMSE) {
		this.predictionAverageMSE = predictionAverageMSE;
	}

	public double getPredictionAverageRMSE() {
		return predictionAverageRMSE;
	}

	public void setPredictionAverageRMSE(double predictionAverageRMSE) {
		this.predictionAverageRMSE = predictionAverageRMSE;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("startTimeMilis:")
			.append(Long.toString(startTimeMilis))
			.append(";");
		sb.append("endTimeMilis:")
			.append(Long.toString(endTimeMilis))
			.append(";");
		return sb.toString();
	}
}