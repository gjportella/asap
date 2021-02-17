package br.unb.cic.analysis.movingaverage.model;

import java.io.Serializable;
import java.util.Date;

public class AveragePoint implements Serializable, Comparable<AveragePoint> {

	private static final long serialVersionUID = 1L;

	private long time;
	private double price;
	private Date dateObject;
	
	private double avg;
	private double stdev;
	private double upper;
	private double lower;
	private int n;
	private boolean processed;

	public AveragePoint(long time, double price) {
		
		this.time = time;
		this.price = price;
		this.dateObject = new Date(time);
		
		this.avg = 0d;
		this.stdev = 0d;
		this.upper = 0d;
		this.lower = 0d;
		this.n = 0;
		this.processed = false;
	}

	public int compareTo(AveragePoint other) {
		if (this.time < other.getTime()) {
			return -1;
		}
		if (this.time > other.getTime()) {
			return 1;
		}
		return 0;
	}
	
	public long getTime() {
		return time;
	}

	public double getPrice() {
		return price;
	}

	public Date getDateObject() {
		return dateObject;
	}
	
	public double getAvg() {
		return avg;
	}

	public void setAvg(double avg) {
		this.avg = avg;
	}

	public double getStdev() {
		return stdev;
	}

	public void setStdev(double stdev) {
		this.stdev = stdev;
	}

	public double getUpper() {
		return upper;
	}

	public void setUpper(double upper) {
		this.upper = upper;
	}

	public double getLower() {
		return lower;
	}

	public void setLower(double lower) {
		this.lower = lower;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
}
