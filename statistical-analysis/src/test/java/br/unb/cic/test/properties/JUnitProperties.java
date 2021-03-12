package br.unb.cic.test.properties;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Implements singleton.
 * 
 * @author gustavop
 *
 */
public final class JUnitProperties {

	private static Logger logger = Logger.getLogger(JUnitProperties.class);

	private static final String PROPERTIES_FILE_PATH = "src/test/resources/junit.properties";

	private static JUnitProperties instance;

	private JUnitProperties() {
	}

	public static synchronized JUnitProperties getInstance() {
		if (instance == null) {
			instance = new JUnitProperties();
		}
		return instance;
	}

	public String getPropertyValue(String name) {

		String value = null;
		try {
			InputStream input = new FileInputStream(PROPERTIES_FILE_PATH);
			Properties prop = new Properties();
			prop.load(input);
			value = prop.getProperty(name);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return value;
	}
}
