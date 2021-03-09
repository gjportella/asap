package br.unb.cic.laico.test;

import org.apache.log4j.Logger;

import br.unb.cic.laico.boot.BootIEEETCCAnalysis;
import br.unb.cic.laico.test.properties.JUnitProperties;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit for IEE TCC Utility and LSTM mechanisms tests.
 */
public class IEEETCCTest extends TestCase {

	/**
	 * Log4j error object.
	 */
	private static Logger logger = Logger.getLogger(IEEETCCTest.class);
	
	/**
	 * Create the test case.
	 *
	 * @param testName name of the test case
	 */
	public IEEETCCTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(IEEETCCTest.class);
	}

	/**
	 * Run the test.
	 */
	public void testApp() {

		JUnitProperties prop = JUnitProperties.getInstance();
		boolean runTheTest = Boolean.parseBoolean(prop.getPropertyValue("run.IEEETCCTest"));
		if (runTheTest) {
			
			Exception exception = null;
			try {
				BootIEEETCCAnalysis app = new BootIEEETCCAnalysis();
				app.bootAnalysis();
			} catch (Exception ex) {
				logger.error(ex.toString());
				exception = ex;
			}
			assertNull(exception);
		}
	}
}
