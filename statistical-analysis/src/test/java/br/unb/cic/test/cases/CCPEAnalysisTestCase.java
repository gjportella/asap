package br.unb.cic.test.cases;

import java.util.Locale;

import org.apache.log4j.Logger;

import br.unb.cic.boot.BootCCPEAnalysis;
import br.unb.cic.test.properties.JUnitProperties;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Statistical analysis test case
 *
 */
public class CCPEAnalysisTestCase extends TestCase {

	/**
	 * Log4j error object
	 */
	private static Logger logger = Logger.getLogger(CCPEAnalysisTestCase.class);
	
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public CCPEAnalysisTestCase(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(CCPEAnalysisTestCase.class);
	}

	/**
	 * Run the test
	 */
	public void testApp() {

		Locale.setDefault(Locale.ENGLISH);
		JUnitProperties prop = JUnitProperties.getInstance();
		boolean runTheTest = Boolean.parseBoolean(prop.getPropertyValue("run.CCPE"));
		
		Exception exception = null;
		if (runTheTest) {
			try {
				BootCCPEAnalysis app = new BootCCPEAnalysis();
				app.bootAnalysis();
			} catch (Exception ex) {
				logger.error(ex.toString());
				exception = ex;
			}
		}
		assertNull(exception);
	}
}
