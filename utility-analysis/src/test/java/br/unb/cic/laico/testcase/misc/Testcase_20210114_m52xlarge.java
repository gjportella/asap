package br.unb.cic.laico.testcase.misc;

import java.util.Locale;

public class Testcase_20210114_m52xlarge extends Testcase {

	private void printHeader() {

		/*
		Test date: 2021-01-14
		SPOT Instance type: m5.2xlarge (8 vCPUs, 32 GiB (Mem), 8 GiB (Storage))
		Current price (us-east-1c): $0.1437 (14/01/2021 14:46)
		On demand price: $0.384
		*/

		System.out.println("Test date: 2021-01-14");
		System.out.println("Instance type: m5.2xlarge (8 vCPUs, 32 GiB (Mem), 8 GiB (Storage))");
		System.out.println("Current price (us-east-1c): $0.1437 (14/01/2021 14:46)");
		System.out.println("On demand price: $0.384");
		System.out.println("");
	}
	
	private void computeSpotUtility() throws Exception {

		/*
		Spot utility: ec2-3-237-238-244.compute-1.amazonaws.com
		IP: 172.31.10.248
		Max price: $0.154
		Launch time: 01/14/2021 02:59:59 PM
		State transition: 01/15/2021 03:03:18 AM
		Spot total: $1.73
		
		spt-util-001,ipAddress:172.31.10.248,startTime:2021-01-14T18:33:35,endTime:2021-01-15T06:01:10,Sequences:56840
		lastSequence: ZEPI_ISL_754255_EPI_ISL_709223
		*/

		String label = "Utility";
		String startTime = "2021-01-14T18:33:35";
		String endTime = "2021-01-15T06:01:10";
		int sumSeq = 56840;
		double maxPrice = 0.154D;
		double spotCost = 1.73D;
		
		computeSpot(label, startTime, endTime, sumSeq, maxPrice, spotCost);
	}

	private void computeSpotCurrent() throws Exception {

		/*
		Spot current: ec2-3-239-250-69.compute-1.amazonaws.com
		IP: 172.31.15.82
		Max price: $0.144
		Launch time: 01/14/2021 02:59:58 PM
		State transition: 01/14/2021 02:59:58 AM
		Spot total: $1.73
		
		spt-curr-001,ipAddress:172.31.15.82,startTime:2021-01-14T18:33:38,endTime:2021-01-15T06:01:10,Sequences:58032
		lastSequence: ZEPI_ISL_754255_EPI_ISL_720875
		*/
		
		String label = "Current";
		String startTime = "2021-01-14T18:33:38";
		String endTime = "2021-01-15T06:01:10";
		int sumSeq = 58032;
		double maxPrice = 0.144D;
		double spotCost = 1.73D;
		
		computeSpot(label, startTime, endTime, sumSeq, maxPrice, spotCost);
	}

	private void computeSpotDefault() throws Exception {

		/*
		Spot default: ec2-35-172-236-34.compute-1.amazonaws.com
		IP: 172.31.8.119
		Max price: -
		Launch time: 01/14/2021 03:00:01 PM
		State transition: 01/15/2021 03:04:24 AM
		Spot total: $1.73
		
		spt-default-001,ipAddress:172.31.8.119,startTime:2021-01-14T18:33:37,endTime:2021-01-15T06:01:12,Sequences:56100
		lastSequence: ZEPI_ISL_754255_EPI_ISL_679990
		*/
		
		String label = "Default";
		String startTime = "2021-01-14T18:33:37";
		String endTime = "2021-01-15T06:01:12";
		int sumSeq = 56100;
		double maxPrice = 0D;
		double spotCost = 1.73D;
		
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

		Testcase_20210114_m52xlarge app = new Testcase_20210114_m52xlarge();
		app.computeMetrics();
	}
}
