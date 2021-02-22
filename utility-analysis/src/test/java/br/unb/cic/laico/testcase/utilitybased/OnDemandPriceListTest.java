package br.unb.cic.laico.testcase.utilitybased;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import br.unb.cic.laico.analysis.utilitybased.model.OnDemandPriceList;

public class OnDemandPriceListTest {

	public OnDemandPriceListTest() {
	}

	@Test
	public void testGetInstance() {

		OnDemandPriceList instance1 = OnDemandPriceList.getInstance();
		OnDemandPriceList instance2 = OnDemandPriceList.getInstance();

		Assertions.assertEquals(instance1, instance2);
	}

	@Test
	public void testGetPrice() {

		OnDemandPriceList instance = OnDemandPriceList.getInstance();
		Double price = instance.getPrice("t2.micro");

		Assertions.assertEquals(price.doubleValue(), 0.0116D);
	}
}
