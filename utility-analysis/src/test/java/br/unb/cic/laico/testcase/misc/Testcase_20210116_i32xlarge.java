package br.unb.cic.laico.testcase.misc;

import java.util.Locale;

public class Testcase_20210116_i32xlarge extends Testcase {

	private void printHeader() {

		/*
		Date: 2021-01-16
		SPOT Instance type: i3.2xlarge (8 vCPUs, 61 GiB (Mem), 8 GiB (Storage))
		Current price (us-east-1c): $0.1998 (16/01/2021 11:07)
		On demand price: $0.624
		*/

		System.out.println("Test date: 2021-01-16");
		System.out.println("Instance type:i3.2xlarge (8 vCPUs, 61 GiB (Mem), 8 GiB (Storage))");
		System.out.println("Current price (us-east-1c): $0.1998 (16/01/2021 11:07)");
		System.out.println("On demand price: $0.624");
		System.out.println("");
	}
	
	private void computeSpotUtility() throws Exception {

		/*
		Spot utility: ec2-3-236-93-96.compute-1.amazonaws.com
		IP: 172.31.10.205
		Max price: $0.201
		Launch time: 01/16/2021 12:00:02 PM
		State transition: 01/17/2021 12:02:53 AM
		Spot total: $2.39
		
		{active:true,key:i3.2xlarge-util-001,ipAddress:172.31.10.205,startTime:2021-01-16T15:24:44,endTime:2021-01-17T03:01:13,completedSequencesSize:45678}
		lastSequence: ZEPI_ISL_754253_EPI_ISL_731332
		*/

		String label = "Utility";
		String startTime = "2021-01-16T15:24:44";
		String endTime = "2021-01-17T03:01:13";
		int sumSeq = 45678;
		double maxPrice = 0.201D;
		double spotCost = 2.39D;

		computeSpot(label, startTime, endTime, sumSeq, maxPrice, spotCost);
	}

	private void computeSpotCurrent() throws Exception {

		/*
		Spot current: ec2-34-232-95-227.compute-1.amazonaws.com
		IP: 172.31.8.117
		Max price: $0.200
		Launch time: 01/16/2021 12:10:48 PM
		State transition: 01/17/2021 12:13:04 AM
		Spot total: $2.39
		
		{active:true,key:i3.2xlarge-curr-001,ipAddress:172.31.8.117,startTime:2021-01-16T15:24:48,endTime:2021-01-17T03:11:31,completedSequencesSize:46427}
		lastSequence: ZEPI_ISL_754253_EPI_ISL_736268
		*/

		String label = "Current";
		String startTime = "2021-01-16T15:24:48";
		String endTime = "2021-01-17T03:11:31";
		int sumSeq = 46427;
		double maxPrice = 0.200D;
		double spotCost = 2.39D;

		computeSpot(label, startTime, endTime, sumSeq, maxPrice, spotCost);
	}

	private void computeSpotDefault() throws Exception {

		/*
		Spot default: ec2-34-234-100-251.compute-1.amazonaws.com
		IP: 172.31.6.58
		Max price: $ -
		Launch time: 01/16/2021 12:00:02 PM
		State transition: 01/17/2021 12:02:53 AM
		Spot total: $2.39
		
		{active:true,key:i3.2xlarge-def-001,ipAddress:172.31.6.58,startTime:2021-01-16T15:24:51,endTime:2021-01-17T03:01:12,completedSequencesSize:45461}
		lastSequence: ZEPI_ISL_754253_EPI_ISL_727937
		*/

		String label = "Default";
		String startTime = "2021-01-16T15:24:51";
		String endTime = "2021-01-17T03:01:12";
		int sumSeq = 45461;
		double maxPrice = 0D;
		double spotCost = 2.39D;

		computeSpot(label, startTime, endTime, sumSeq, maxPrice, spotCost);
	}

	private void computeMetrics() throws Exception {

		printHeader();
		computeSpotUtility();
		computeSpotCurrent();
		computeSpotDefault();
	}
	
	public static void main(String[] args) throws Exception {

		Locale.setDefault(Locale.US);

		Testcase_20210116_i32xlarge app = new Testcase_20210116_i32xlarge();
		app.computeMetrics();
	}
}
