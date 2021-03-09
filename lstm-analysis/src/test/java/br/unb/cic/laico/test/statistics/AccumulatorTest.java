package br.unb.cic.laico.test.statistics;

import br.unb.cic.laico.statistics.Accumulator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AccumulatorTest extends TestCase {
	
	public AccumulatorTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(AccumulatorTest.class);
	}
	
	private double round(double value) {
		return (double) Math.round(100 * value) / 100;
	}
	
	public void testAccum1() {
		
		Accumulator accum = new Accumulator();
		accum.addValue(10D);
		accum.addValue(9D);
		accum.addValue(11D);
		accum.addValue(12D);
		accum.addValue(8D);
		
		assertEquals(10D, round(accum.getMean()));
		assertEquals(2D, round(accum.getVariance()));
	}
	
	public void testAccum2() {
		
		Accumulator accum = new Accumulator();
		accum.addValue(15D);
		accum.addValue(12D);
		accum.addValue(16D);
		accum.addValue(10D);
		accum.addValue(11D);
		
		assertEquals(12.8D, round(accum.getMean()));
		assertEquals(5.36D, round(accum.getVariance()));
	}

}
