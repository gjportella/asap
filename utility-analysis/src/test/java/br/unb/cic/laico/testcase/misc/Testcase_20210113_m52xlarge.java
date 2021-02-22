package br.unb.cic.laico.testcase.misc;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Testcase_20210113_m52xlarge extends Testcase {

	private void printHeader() {

		/*
		Test date: 2021-01-13
		Instance type: m5.2xlarge (8 vCPUs, 32 GiB (Mem), 8 GiB (Storage))
		Current price (us-east-1c): $0.1429 (13/01/2021 11:38)
		On demand price: $0.384
		*/

		System.out.println("Test date: 2021-01-13");
		System.out.println("Instance type: m5.2xlarge (8 vCPUs, 32 GiB (Mem), 8 GiB (Storage))");
		System.out.println("Current price (us-east-1c): $0.1429 (13/01/2021 11:38)");
		System.out.println("On demand price: $0.384");
		System.out.println("");
	}

	private void computeSpotUtility() throws Exception {

		/*
		Spot utility: ec2-3-239-224-228.compute-1.amazonaws.com
		IP: 172.31.9.13
		Max price: $0.159
		Launch time: 2021-01-13 12:00:01 GMT-0300
		State transition: 2021-01-13 21:48:13 GMT-0300
		Spot total: $1.43

		startTime:2021-01-13T15:28:17,endTime:2021-01-13T16:20:14,Sequences:4279
		startTime:2021-01-13T16:22:08,endTime:2021-01-13T17:13:43,Sequences:4279
		startTime:2021-01-13T17:14:48,endTime:2021-01-13T18:06:51,Sequences:4279
		startTime:2021-01-13T18:12:23,endTime:2021-01-13T19:04:24,Sequences:4279
		startTime:2021-01-13T19:05:50,endTime:2021-01-13T19:57:52,Sequences:4279
		startTime:2021-01-13T19:59:34,endTime:2021-01-13T20:51:47,Sequences:4279
		startTime:2021-01-13T20:58:08,endTime:2021-01-13T21:50:19,Sequences:4279
		startTime:2021-01-13T21:51:55,endTime:2021-01-13T22:44:11,Sequences:4279
		startTime:2021-01-13T22:47:00,endTime:2021-01-13T23:39:13,Sequences:4279
		startTime:2021-01-13T23:43:35,endTime:2021-01-14T00:35:41,Sequences:4279
		startTime:2021-01-14T00:38:51,endTime:2021-01-14T00:48:13,Sequences:770
		lastSequence:EPI_ISL_703229.fasta
		*/

		long sumTime = 0L;
		int sumSeq = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sumTime += sdf.parse("2021-01-13T16:20:14").getTime() - sdf.parse("2021-01-13T15:28:17").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-13T17:13:43").getTime() - sdf.parse("2021-01-13T16:22:08").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-13T18:06:51").getTime() - sdf.parse("2021-01-13T17:14:48").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-13T19:04:24").getTime() - sdf.parse("2021-01-13T18:12:23").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-13T19:57:52").getTime() - sdf.parse("2021-01-13T19:05:50").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-13T20:51:47").getTime() - sdf.parse("2021-01-13T19:59:34").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-13T21:50:19").getTime() - sdf.parse("2021-01-13T20:58:08").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-13T22:44:11").getTime() - sdf.parse("2021-01-13T21:51:55").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-13T23:39:13").getTime() - sdf.parse("2021-01-13T22:47:00").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-14T00:35:41").getTime() - sdf.parse("2021-01-13T23:43:35").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-14T00:48:13").getTime() - sdf.parse("2021-01-14T00:38:51").getTime(); sumSeq += 770;

		System.out.println("---");
		System.out.println("Spot utility: ec2-3-239-224-228.compute-1.amazonaws.com");
		System.out.println("Max price: $0.159");
		System.out.println("Spot total price: $1.43");
		System.out.println("Number of comparisons: " + Integer.toString(sumSeq));
		System.out.println("Elapsed time: " + formatElapsedInterval(sumTime));
		System.out.println("Average comparison cost: $" + String.format("%.8f", 1.43D / (double) sumSeq));
		System.out.println("Average comparison time: " + String.format("%.8f", (double) sumTime / (double) sumSeq) + "ms");
		System.out.println("---\n");
	}

	private void computeSpotCurrent() throws Exception {

		/*
		Spot current: ec2-3-219-217-113.compute-1.amazonaws.com
		IP: 172.31.8.69
		Max price: $0.143
		Launch time: 2021-01-13 12:00:01 GMT-0300
		State transition: 2021-01-13 13:49:27 GMT-0300
		Spot total: $0.29

		startTime:2021-01-13T15:41:46,endTime:2021-01-13T16:34:25,Sequences:4279
		startTime:2021-01-13T16:35:24,endTime:2021-01-13T16:49:27,Sequences:1141
		lastSequence:EPI_ISL_709068.fasta
		 */

		long sumTime = 0L;
		int sumSeq = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sumTime += sdf.parse("2021-01-13T16:34:25").getTime() - sdf.parse("2021-01-13T15:41:46").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-13T16:49:27").getTime() - sdf.parse("2021-01-13T16:35:24").getTime(); sumSeq += 1141;

		System.out.println("---");
		System.out.println("Spot current: ec2-3-219-217-113.compute-1.amazonaws.com");
		System.out.println("Max price: $0.143");
		System.out.println("Spot total price: $0.29");
		System.out.println("Number of comparisons: " + Integer.toString(sumSeq));
		System.out.println("Elapsed time: " + formatElapsedInterval(sumTime));
		System.out.println("Average comparison cost: $" + String.format("%.8f", 0.29D / (double) sumSeq));
		System.out.println("Average comparison time: " + String.format("%.8f", (double) sumTime / (double) sumSeq) + "ms");
		System.out.println("---\n");
	}

	private void computeSpotDefault() throws Exception {

		/*
		Spot default: ec2-3-236-38-250.compute-1.amazonaws.com
		IP: 172.31.8.244
		Max price: -
		Launch time: 2021-01-13 12:00:01 GMT-0300
		State transition: 2021-01-13 21:11:22 GMT-0300
		Spot total: $1.29

		startTime:2021-01-13T15:52:49,endTime:2021-01-13T16:45:37,Sequences:4279
		startTime:2021-01-13T16:46:56,endTime:2021-01-13T17:39:48,Sequences:4279
		startTime:2021-01-13T17:40:51,endTime:2021-01-13T18:33:38,Sequences:4279
		startTime:2021-01-13T18:36:57,endTime:2021-01-13T19:29:43,Sequences:4279
		startTime:2021-01-13T19:30:49,endTime:2021-01-13T20:23:40,Sequences:4279
		startTime:2021-01-13T20:26:54,endTime:2021-01-13T21:19:44,Sequences:4279
		startTime:2021-01-13T21:38:49,endTime:2021-01-13T22:31:32,Sequences:4279
		startTime:2021-01-13T22:32:34,endTime:2021-01-13T23:25:03,Sequences:4279
		startTime:2021-01-13T23:28:32,endTime:2021-01-14T00:11:22,Sequences:3469
		lastSequence:EPI_ISL_735892.fasta
		*/

		long sumTime = 0L;
		int sumSeq = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sumTime += sdf.parse("2021-01-13T16:45:37").getTime() - sdf.parse("2021-01-13T15:52:49").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-13T17:39:48").getTime() - sdf.parse("2021-01-13T16:46:56").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-13T18:33:38").getTime() - sdf.parse("2021-01-13T17:40:51").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-13T19:29:43").getTime() - sdf.parse("2021-01-13T18:36:57").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-13T20:23:40").getTime() - sdf.parse("2021-01-13T19:30:49").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-13T21:19:44").getTime() - sdf.parse("2021-01-13T20:26:54").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-13T22:31:32").getTime() - sdf.parse("2021-01-13T21:38:49").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-13T23:25:03").getTime() - sdf.parse("2021-01-13T22:32:34").getTime(); sumSeq += 4279;
		sumTime += sdf.parse("2021-01-14T00:11:22").getTime() - sdf.parse("2021-01-13T23:28:32").getTime(); sumSeq += 3469;

		System.out.println("---");
		System.out.println("Spot default: ec2-3-236-38-250.compute-1.amazonaws.com");
		System.out.println("Max price: -");
		System.out.println("Spot total price: $1.29");
		System.out.println("Number of comparisons: " + Integer.toString(sumSeq));
		System.out.println("Elapsed time: " + formatElapsedInterval(sumTime));
		System.out.println("Average comparison cost: $" + String.format("%.8f", 1.29D / (double) sumSeq));
		System.out.println("Average comparison time: " + String.format("%.8f", (double) sumTime / (double) sumSeq) + "ms");
		System.out.println("---\n");
	}

	private void computeMetrics() throws Exception {

		printHeader();
		computeSpotUtility();
		computeSpotCurrent();
		computeSpotDefault();
	}

	public static void main(String[] args) throws Exception {

		Locale.setDefault(Locale.US);

		Testcase_20210113_m52xlarge app = new Testcase_20210113_m52xlarge();
		app.computeMetrics();
	}
}
