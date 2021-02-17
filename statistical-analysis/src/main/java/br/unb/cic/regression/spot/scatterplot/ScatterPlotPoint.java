package br.unb.cic.regression.spot.scatterplot;

import java.io.Serializable;
import java.util.Date;

public class ScatterPlotPoint implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date dateObject;
	private long dateLong;
	private double dateDouble;
	private double price;
	private double predictedPrice;
	
	public ScatterPlotPoint() {
	}

	public Date getDateObject() {
		return dateObject;
	}

	public void setDateObject(Date dateObject) {
		this.dateObject = dateObject;
	}

	public long getDateLong() {
		return dateLong;
	}

	public void setDateLong(long dateLong) {
		this.dateLong = dateLong;
	}

	public double getDateDouble() {
		return dateDouble;
	}

	public void setDateDouble(double dateDouble) {
		this.dateDouble = dateDouble;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getPredictedPrice() {
		return predictedPrice;
	}

	public void setPredictedPrice(double predictedPrice) {
		this.predictedPrice = predictedPrice;
	}
}
