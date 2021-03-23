package br.unb.cic.laico.analysis.utilitybased.model;

import java.io.Serializable;

public class FutureEstimate implements Serializable {

	private static final long serialVersionUID = 1L;

	private long futureTimeWindowSize;
	private long firstChunkAvailableTimeInWindow;
	private long acumulatedAvailableTimeInWindow;
	
	private double acumulatedBidVariationsInWindow;
	private int bidVariationsInWindow;
	
	private double averageBid;
	private double futureAvailability;
	
	private double sse;
	
	public FutureEstimate(long futureTimeWindowSize) {
		this(futureTimeWindowSize, 0L, 0L, 0D, 0, 0D, 0D, 0D);
	}
	
	public FutureEstimate(long futureTimeWindowSize, long firstChunkAvailableTimeInWindow,
			long acumulatedAvailableTimeInWindow, double acumulatedBidVariationsInWindow, int bidVariationsInWindow,
			double averageBid, double futureAvailability, double sse) {
		this.futureTimeWindowSize = futureTimeWindowSize;
		this.firstChunkAvailableTimeInWindow = firstChunkAvailableTimeInWindow;
		this.acumulatedAvailableTimeInWindow = acumulatedAvailableTimeInWindow;
		this.acumulatedBidVariationsInWindow = acumulatedBidVariationsInWindow;
		this.bidVariationsInWindow = bidVariationsInWindow;
		this.averageBid = averageBid;
		this.futureAvailability = futureAvailability;
		this.sse = sse;
	}

	public long getFutureTimeWindowSize() {
		return futureTimeWindowSize;
	}

	public void setFutureTimeWindowSize(long futureTimeWindowSize) {
		this.futureTimeWindowSize = futureTimeWindowSize;
	}
	
	public long getFirstChunkAvailableTimeInWindow() {
		return firstChunkAvailableTimeInWindow;
	}
	
	public double getFirstChunkAvailabilityInWindow() {
		return (double) firstChunkAvailableTimeInWindow
				/ (double) futureTimeWindowSize;
	}

	public void setFirstChunkAvailableTimeInWindow(long firstChunkAvailableTimeInWindow) {
		this.firstChunkAvailableTimeInWindow = firstChunkAvailableTimeInWindow;
	}

	public long getAcumulatedAvailableTimeInWindow() {
		return acumulatedAvailableTimeInWindow;
	}

	public void setAcumulatedAvailableTimeInWindow(long acumulatedAvailableTimeInWindow) {
		this.acumulatedAvailableTimeInWindow = acumulatedAvailableTimeInWindow;
	}

	public double getAcumulatedBidVariationsInWindow() {
		return acumulatedBidVariationsInWindow;
	}

	public void setAcumulatedBidVariationsInWindow(double acumulatedBidVariationsInWindow) {
		this.acumulatedBidVariationsInWindow = acumulatedBidVariationsInWindow;
	}

	public int getBidVariationsInWindow() {
		return bidVariationsInWindow;
	}

	public void setBidVariationsInWindow(int bidVariationsInWindow) {
		this.bidVariationsInWindow = bidVariationsInWindow;
	}

	public double getAverageBid() {
		return averageBid;
	}

	public void setAverageBid(double averageBid) {
		this.averageBid = averageBid;
	}

	public double getFutureAvailability() {
		return futureAvailability;
	}

	public void setFutureAvailability(double futureAvailability) {
		this.futureAvailability = futureAvailability;
	}

	public void addAcumulatedAvailableTimeInWindow(long availableTime) {
		this.acumulatedAvailableTimeInWindow += availableTime;
	}

	public void addAcumulatedBidVariation(double bidVariation) {
		this.acumulatedBidVariationsInWindow += bidVariation;
		this.bidVariationsInWindow++;
	}
	
	public void updateEstimatedValues() {
		
		if (futureTimeWindowSize != 0L) {
			this.futureAvailability = (((double) this.acumulatedAvailableTimeInWindow)
					/ ((double) this.futureTimeWindowSize));
		}
		if (bidVariationsInWindow != 0) {
			this.averageBid = (this.acumulatedBidVariationsInWindow
					/ ((double) this.bidVariationsInWindow));
		}
		if (bidVariationsInWindow > 0 && firstChunkAvailableTimeInWindow == 0L) {
			this.firstChunkAvailableTimeInWindow
					= this.acumulatedAvailableTimeInWindow;
		}
	}
	
	public void addError(double error) {
		this.sse += Math.pow(error, 2); 
	}
	
	public double getMSE() {
		return this.sse / (double) this.bidVariationsInWindow;
	}
}
