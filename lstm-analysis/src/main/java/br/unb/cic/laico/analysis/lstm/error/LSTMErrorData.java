package br.unb.cic.laico.analysis.lstm.error;

import java.io.Serializable;

public class LSTMErrorData implements Serializable {

	private static final long serialVersionUID = 1L;

	private int epoch;
	private double averageMAE;
	private double averageMSE;
	private double averageRMSE;
	private double averageR2;

	public LSTMErrorData(int epoch, double averageMAE, double averageMSE, double averageRMSE, double averageR2) {
		this.epoch = epoch;
		this.averageMAE = averageMAE;
		this.averageMSE = averageMSE;
		this.averageRMSE = averageRMSE;
		this.averageR2 = averageR2;
	}

	public int getEpoch() {
		return epoch;
	}

	public void setEpoch(int epoch) {
		this.epoch = epoch;
	}

	public double getAverageMAE() {
		return averageMAE;
	}

	public void setAverageMAE(double averageMAE) {
		this.averageMAE = averageMAE;
	}

	public double getAverageMSE() {
		return averageMSE;
	}

	public void setAverageMSE(double averageMSE) {
		this.averageMSE = averageMSE;
	}

	public double getAverageRMSE() {
		return averageRMSE;
	}

	public void setAverageRMSE(double averageRMSE) {
		this.averageRMSE = averageRMSE;
	}

	public double getAverageR2() {
		return averageR2;
	}

	public void setAverageR2(double averageR2) {
		this.averageR2 = averageR2;
	}
}
