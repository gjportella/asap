package br.unb.cic.laico.data.utility;

import java.io.Serializable;

public class UtilityInformation implements Serializable, Comparable<UtilityInformation> {

	private static final long serialVersionUID = 1L;

	private double bid;
	private double utility;
	private double normalizedUtility;
	private double availability;
	
	private FutureEstimate futureEstimateForFixedBid;
	private FutureEstimate futureEstimateForVariableBid;

	public UtilityInformation() {
		super();
	}

	public int compareTo(UtilityInformation other) {
		if (this.utility < other.getUtility()) {
			return -1;
		}
		if (this.utility > other.getUtility()) {
			return 1;
		}
		return 0;
	}

	public double getBid() {
		return bid;
	}

	public void setBid(double bid) {
		this.bid = bid;
	}

	public double getUtility() {
		return utility;
	}

	public void setUtility(double utility) {
		this.utility = utility;
	}

	public double getNormalizedUtility() {
		return normalizedUtility;
	}

	public void setNormalizedUtility(double normalizedUtility) {
		this.normalizedUtility = normalizedUtility;
	}

	public double getAvailability() {
		return availability;
	}

	public void setAvailability(double availability) {
		this.availability = availability;
	}

	public FutureEstimate getFutureEstimateForFixedBid() {
		return futureEstimateForFixedBid;
	}

	public void setFutureEstimateForFixedBid(FutureEstimate futureEstimateForFixedBid) {
		this.futureEstimateForFixedBid = futureEstimateForFixedBid;
	}

	public FutureEstimate getFutureEstimateForVariableBid() {
		return futureEstimateForVariableBid;
	}

	public void setFutureEstimateForVariableBid(FutureEstimate futureEstimateForVariableBid) {
		this.futureEstimateForVariableBid = futureEstimateForVariableBid;
	}
}
