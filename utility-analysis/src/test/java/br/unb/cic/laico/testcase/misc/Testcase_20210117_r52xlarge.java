package br.unb.cic.laico.testcase.misc;

import java.util.Locale;

public class Testcase_20210117_r52xlarge extends Testcase {

	private void printHeader() {

		/*
		Date: 2021-01-16
		SPOT Instance type: r5.2xlarge (8 vCPUs, 64 GiB (Mem), 8 GiB (Storage))
		Current price (us-east-1c): $0.2001 (17/01/2021 11:58)
		On demand price: $0.504
		*/

		System.out.println("Test date: 2021-01-16");
		System.out.println("Instance type: r5.2xlarge (8 vCPUs, 64 GiB (Mem), 8 GiB (Storage))");
		System.out.println("Current price (us-east-1c): $0.2001 (17/01/2021 11:58)");
		System.out.println("On demand price: $0.504");
		System.out.println("");
	}

	private void computeSpotUtility() throws Exception {

		/*
		Spot utility: ec2-34-234-223-64.compute-1.amazonaws.com
		IP: 172.31.0.123
		Max price: $0.201
		Launch time: 01/17/2021 12:38:21 PM
		State transition: 01/18/2021 12:39:56 PM
		Spot total: $2.40
		
		{active:true,key:r5.2xlarge-util-001,ipAddress:172.31.0.123,startTime:2021-01-17T15:50:57,endTime:2021-01-18T03:38:14,completedSequencesSize:59098}
		lastSequence: ZEPI_ISL_754256_EPI_ISL_735895
		*/

		String label = "Utility";
		String startTime = "2021-01-17T15:50:57";
		String endTime = "2021-01-18T03:38:14";
		int sumSeq = 59098;
		double maxPrice = 0.201D;
		double spotCost = 2.40D;

		computeSpot(label, startTime, endTime, sumSeq, maxPrice, spotCost);
	}

	private void computeSpotCurrent() throws Exception {

		/*
		Spot current: ec2-44-192-11-29.compute-1.amazonaws.com
		IP: 172.31.6.199
		Max price: $0.200
		Launch time: 01/17/2021 12:30:01 PM
		State transition: 01/18/2021 12:33:04 AM
		Spot total: $2.40
		
		{active:true,key:r5.2xlarge-curr-001,ipAddress:172.31.6.199,startTime:2021-01-17T15:50:49,endTime:2021-01-18T03:31:13,completedSequencesSize:58424}
		lastSequence: ZEPI_ISL_754256_EPI_ISL_731048
		*/

		String label = "Current";
		String startTime = "2021-01-17T15:50:49";
		String endTime = "2021-01-18T03:31:13";
		int sumSeq = 58424;
		double maxPrice = 0.200D;
		double spotCost = 2.40D;

		computeSpot(label, startTime, endTime, sumSeq, maxPrice, spotCost);
	}

	private void computeSpotDefault() throws Exception {

		/*
		Spot default: ec2-18-210-6-101.compute-1.amazonaws.com
		IP: 172.31.3.41
		Max price: -
		Launch time: 01/17/2021 12:30:01 PM
		State transition: 01/18/2021 12:34:00 AM
		Spot total: $2.40
		
		{active:true,key:r5.2xlarge-def-001,ipAddress:172.31.3.41,startTime:2021-01-17T15:50:46,endTime:2021-01-18T03:31:12,completedSequencesSize:58458}
		lastSequence: ZEPI_ISL_754256_EPI_ISL_731150
		*/

		String label = "Default";
		String startTime = "2021-01-17T15:50:46";
		String endTime = "2021-01-18T03:31:12";
		int sumSeq = 58458;
		double maxPrice = 0D;
		double spotCost = 2.40D;

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

		Testcase_20210117_r52xlarge app = new Testcase_20210117_r52xlarge();
		app.computeMetrics();
	}
}
