package br.unb.cic.laico.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StatisticsHelper {

	private static final double DEFAULT_WEIGHT = 1d;

	public StatisticsHelper() {
	}

	public int removeOutliers(List<Date> dateList, List<Double> priceList, List<Date> dateListOut,
			List<Double> priceListOut) {

		return removeOutliers(dateList, priceList, dateListOut, priceListOut, DEFAULT_WEIGHT);
	}

	public int removeOutliers(List<Date> dateList, List<Double> priceList, List<Date> dateListOut,
			List<Double> priceListOut, double weight) {

		List<Double> orderedPriceList = new ArrayList<Double>(priceList);
		Collections.sort(orderedPriceList);

		int q1Index = (int) Math.ceil(orderedPriceList.size() / 4);
		int q2Index = (int) Math.ceil(orderedPriceList.size() / 2);
		int q3Index = q1Index + q2Index;
		Double q1 = orderedPriceList.get(q1Index);
		Double q3 = orderedPriceList.get(q3Index);
		double range = (weight) * (q3.doubleValue() - q1.doubleValue());

		int addedValues = 0;
		for (int i = 0; i < priceList.size(); i++) {

			Date date = dateList.get(i);
			Double price = priceList.get(i);
			if (price.doubleValue() >= (q1.doubleValue() - range)
					&& price.doubleValue() <= (q3.doubleValue() + range)) {

				dateListOut.add(date);
				priceListOut.add(price);
				addedValues++;
			}
		}

		return priceList.size() - addedValues;
	}
}
