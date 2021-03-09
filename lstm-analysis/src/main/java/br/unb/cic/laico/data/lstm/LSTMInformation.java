package br.unb.cic.laico.data.lstm;

import java.io.Serializable;

public class LSTMInformation implements Serializable {

	private static final long serialVersionUID = 1L;

	private double predictedPrice;

	public LSTMInformation() {
	}

	public double getPredictedPrice() {
		return predictedPrice;
	}

	public void setPredictedPrice(double predictedPrice) {
		this.predictedPrice = predictedPrice;
	}
}
