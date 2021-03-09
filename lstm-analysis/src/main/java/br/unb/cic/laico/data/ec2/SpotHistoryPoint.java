package br.unb.cic.laico.data.ec2;

import java.io.Serializable;
import java.util.Date;

import br.unb.cic.laico.data.lstm.LSTMInformation;
import br.unb.cic.laico.data.utility.UtilityInformation;

public class SpotHistoryPoint implements Serializable, Cloneable, Comparable<SpotHistoryPoint> {

	private static final long serialVersionUID = 1L;

	public static final byte STATUS_INITIALIZED = 0;
	public static final byte STATUS_TRAINING = 1;
	public static final byte STATUS_TESTING = 2;

	private byte status;
	private double price;
	private Date dateObject;
	
	private UtilityInformation utilityInformation;
	private LSTMInformation lstmInformation;

	public SpotHistoryPoint(Date dateObject, double price) {
		this.status = STATUS_INITIALIZED;
		this.price = price;
		this.dateObject = dateObject;
	}

	public Object clone() throws CloneNotSupportedException {
		SpotHistoryPoint cloned = (SpotHistoryPoint) super.clone();
		cloned.setStatus(this.getStatus());
		cloned.setDateObject((Date) this.getDateObject().clone());
		cloned.setPrice(this.getPrice());
		return cloned;
	}

	public int compareTo(SpotHistoryPoint other) {
		if (this.getTime() < other.getTime()) {
			return -1;
		}
		if (this.getTime() > other.getTime()) {
			return 1;
		}
		return 0;
	}

	public long getTime() {
		return dateObject.getTime();
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}


	public Date getDateObject() {
		return dateObject;
	}

	public void setDateObject(Date dateObject) {
		this.dateObject = dateObject;
	}

	public UtilityInformation getUtilityInformation() {
		return utilityInformation;
	}

	public void setUtilityInformation(UtilityInformation utilityInformation) {
		this.utilityInformation = utilityInformation;
	}

	public LSTMInformation getLSTMInformation() {
		return lstmInformation;
	}

	public void setLSTMInformation(LSTMInformation lstmInformation) {
		this.lstmInformation = lstmInformation;
	}
}