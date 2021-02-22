package br.unb.cic.laico.testcase.misc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Testcase_20210115_c5n9xlarge extends Testcase {

	private void printHeader() {

		/*
		Date: 2021-01-15
		SPOT Instance type: c5n.9xlarge (36 vCPUs, 96 GiB (Mem), 8 GiB (Storage))
		Current price (us-east-1c): $0.7522 (15/01/2021 11:05)
		On demand price: $1.944
		*/

		System.out.println("Test date: 2021-01-15");
		System.out.println("Instance type: c5n.9xlarge (36 vCPUs, 96 GiB (Mem), 8 GiB (Storage))");
		System.out.println("Current price (us-east-1c): $0.7522 (15/01/2021 11:05)");
		System.out.println("On demand price: $1.944");
		System.out.println("");
	}
	
	private void computeSpotUtility() throws Exception {

		/*
		Spot utility: ec2-34-200-232-240.compute-1.amazonaws.com
		IP: 172.31.12.81
		Max price: $0.753
		Launch time: 01/15/2021 12:00:01 PM
		State transition: 01/16/2021 12:04:09 AM
		Spot total: 9.03

		{active:true,key:c5n.9xlarge-util-001,ipAddress:172.31.12.81,startTime:2021-01-15T15:20:11,endTime:2021-01-16T03:01:12,completedSequencesSize:138804}
		lastSequence: ZEPI_ISL_754274_EPI_ISL_719071
		*/

		String label = "Utility";
		String startTime = "2021-01-15T15:20:11";
		String endTime = "2021-01-16T03:01:12";
		int sumSeq = 138804;
		double maxPrice = 0.753D;
		double spotCost = 9.03D;

		computeSpot(label, startTime, endTime, sumSeq, maxPrice, spotCost);
	}

	private void computeSpotCurrent() throws Exception {

		/*
		Spot current: ec2-44-192-10-144.compute-1.amazonaws.com
		IP: 172.31.6.119
		Max price: $0.7522
		Launch time: 01/15/2021 12:00:01 PM
		State transition: 01/15/2021 05:21:48 PM
		Spot total: $3.76

		{active:true,key:c5n.9xlarge-curr-001,ipAddress:172.31.6.119,startTime:2021-01-15T15:20:10,endTime:2021-01-15T20:21:48,completedSequencesSize:59945}
		lastSequence: ZEPI_ISL_754256_EPI_ISL_668170
		*/

		String label = "Current";
		String startTime = "2021-01-15T15:20:10";
		String endTime = "2021-01-15T20:21:48";
		int sumSeq = 59945;
		double maxPrice = 0.752D;
		double spotCost = 3.76D;

		computeSpot(label, startTime, endTime, sumSeq, maxPrice, spotCost);
	}

	private void computeSpotDefaultExtrapolated() throws Exception {

		/*
		Spot default: ec2-3-237-77-120.compute-1.amazonaws.com
		IP: 172.31.13.143
		Max price: -
		Launch time: 01/15/2021 12:00:01 PM
		State transition: 01/15/2021 05:16:21 PM
		Spot total: $3.76
		
		{active:true,key:c5n.9xlarge-def-001,ipAddress:172.31.13.143,startTime:2021-01-15T15:20:14,endTime:2021-01-15T15:21:57,completedSequencesSize:342}
		{active:true,key:c5n.9xlarge-def-001,ipAddress:172.31.13.143,startTime:2021-01-15T15:20:14,endTime:2021-01-15T20:16:20,completedSequencesSize:58993}
		lastSequence: ZEPI_ISL_754255_EPI_ISL_736326
		*/
		
		String label = "Default";
		String startTime = "2021-01-15T15:20:14";
		String endTime = "2021-01-15T15:21:57";
		int sumSeq = 342;
		double maxPrice = 0D;
		double spotCost = 0.75D;
		
		computeSpot(label, startTime, endTime, sumSeq, maxPrice, spotCost);
		
		System.out.println("----------------------- EXTRAPOLATION ----------------------");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		long sumTime = sdf.parse("2021-01-15T15:21:57").getTime() - sdf.parse("2021-01-15T15:20:14").getTime();
		System.out.println("Elapsed time from 2021-01-15T15:20:14 to 2021-01-15T15:21:57 : "
				+ formatElapsedInterval(sumTime) + " (" + Long.toString(sumTime) + " ms)");
		
		double compTime = (double) sumTime / (long) sumSeq;
		System.out.println("Average comparison time: " + Double.toString(compTime));
		
		sumTime = sdf.parse("2021-01-15T20:16:21").getTime() - sdf.parse("2021-01-15T15:20:14").getTime();
		System.out.println("Elapsed time from 2021-01-15T15:20:14 to 2021-01-15T20:16:21 : "
				+ formatElapsedInterval(sumTime) + " (" + Long.toString(sumTime) + " ms)");

		sumSeq = (int) ((double) sumTime / compTime);
		System.out.println("Number of comparisons (sumSeq): "
				+ Long.toString(sumTime) + " / " + Double.toString(compTime) + " = "
				+ Integer.toString(sumSeq));

		sumTime = (long) (compTime * (double) sumSeq);
		System.out.println("New elapsed time from 2021-01-15T15:20:14: "
				+ formatElapsedInterval(sumTime) + " (" + Long.toString(sumTime) + " ms)");
		
		long newEndTime = sdf.parse("2021-01-15T15:20:14").getTime() + sumTime;
		endTime = sdf.format(new Date(newEndTime));
		System.out.println("New endTime: " + endTime);
		
		spotCost = 3.76D;
		System.out.println("------------------------------------------------------------\n");
		
		label = "Default (extrapolated)";
		computeSpot(label, startTime, endTime, sumSeq, maxPrice, spotCost);
	}

	private void computeMetrics() throws Exception {

		printHeader();
		computeSpotUtility();
		computeSpotCurrent();
		computeSpotDefaultExtrapolated();
	}
	
	public static void main(String[] args) throws Exception {

		Locale.setDefault(Locale.US);

		Testcase_20210115_c5n9xlarge app = new Testcase_20210115_c5n9xlarge();
		app.computeMetrics();
	}
}
