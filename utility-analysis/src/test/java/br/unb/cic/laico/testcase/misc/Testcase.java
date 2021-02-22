package br.unb.cic.laico.testcase.misc;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public abstract class Testcase {

	protected String formatElapsedInterval(long l) {

		long hr = TimeUnit.MILLISECONDS.toHours(l);
		long min = TimeUnit.MILLISECONDS.toMinutes(
				l - TimeUnit.HOURS.toMillis(hr));
		long sec = TimeUnit.MILLISECONDS.toSeconds(
				l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
		long ms = TimeUnit.MILLISECONDS.toMillis(
				l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
		return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
	}

	protected void computeSpot(String label, String startTime, String endTime, int sumSeq, double maxPrice,
			double spotCost) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		long sumTime = sdf.parse(endTime).getTime() - sdf.parse(startTime).getTime();

		System.out.println("---");
		System.out.println("Spot: " + label);
		System.out.println("Start time: " + startTime);
		System.out.println("End time: " + endTime);
		System.out.println("Elapsed time: " + formatElapsedInterval(sumTime) + " (" + Long.toString(sumTime) + " ms)");
		System.out.println("Max price: $" + (maxPrice > 0D ? Double.toString(maxPrice) : " -"));
		System.out.println("Spot total price: $" + Double.toString(spotCost));
		System.out.println("Number of comparisons: " + Integer.toString(sumSeq));
		System.out.println("Average comparison cost: $" + String.format("%.8f", spotCost / (double) sumSeq));
		System.out.println("Average comparison time: "
					+ String.format("%.8f", (double) sumTime / (double) sumSeq) + " ms");
		System.out.println("---\n");
	}
}
