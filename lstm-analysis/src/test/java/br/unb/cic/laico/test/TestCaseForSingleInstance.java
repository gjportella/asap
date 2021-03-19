package br.unb.cic.laico.test;

import java.util.Locale;

import org.apache.log4j.Logger;

import br.unb.cic.laico.boot.BootAnalysis;
import br.unb.cic.laico.boot.BootSingleInstanceAnalysis;
import br.unb.cic.laico.test.properties.JUnitProperties;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test case for a single instance.
 */
public class TestCaseForSingleInstance extends TestCase {

	/**
	 * Log4j error object
	 */
	private static Logger logger = Logger.getLogger(TestCaseForSingleInstance.class);
	
	/**
	 * Create the test case.
	 *
	 * @param testName name of the test case
	 */
	public TestCaseForSingleInstance(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(TestCaseForSingleInstance.class);
	}

	/**
	 * Run the test
	 */
	public void testApp() {

		Locale.setDefault(Locale.ENGLISH);
		JUnitProperties prop = JUnitProperties.getInstance();
		boolean runTheTest = Boolean.parseBoolean(prop.getPropertyValue("run.SingleInstance"));
		if (runTheTest) {
			Exception exception = null;
			try {
				BootAnalysis analysis = new BootSingleInstanceAnalysis();
				analysis.runAnalysis();
			} catch (Exception ex) {
				logger.error(ex.toString());
				exception = ex;
			}
			assertNull(exception);
		}
	}
}
