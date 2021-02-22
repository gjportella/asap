package br.unb.cic.laico.testcase.statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.unb.cic.laico.statistics.StatisticsHelper;

public class StatisticsHelperTest {

	private static final double[] PRICE_VALUES
			= { 8.8D, 6.7D, 7.4D, 10.2D, 3.0D, 5.9D, 0.2D, 5.7D, 9.6D, 4.8D, 9.3D, 17.3D, 9.3D };

	private StatisticsHelper statisticsHelper;
	private List<Date> dateList;
	private List<Double> priceList;

	public StatisticsHelperTest() {
	}

	@BeforeEach
	public void beforeEach() {

		statisticsHelper = new StatisticsHelper();
		dateList = new ArrayList<Date>(PRICE_VALUES.length);
		priceList = new ArrayList<Double>(PRICE_VALUES.length);
		for (int i = 0; i < PRICE_VALUES.length; i++) {

			dateList.add(new Date((long) i));
			priceList.add(Double.valueOf(PRICE_VALUES[i]));
		}
	}

	@Test
	public void testRemoveOutliers() {

		List<Date> dateListOut = new ArrayList<Date>(PRICE_VALUES.length);
		List<Double> priceListOut = new ArrayList<Double>(PRICE_VALUES.length);

		int removedValues = statisticsHelper.removeOutliers(dateList, priceList, dateListOut, priceListOut);
		Assertions.assertEquals(2, removedValues);
	}

	@AfterEach
	public void afterEach() {

		statisticsHelper = null;
		dateList = null;
		priceList = null;
	}
}
