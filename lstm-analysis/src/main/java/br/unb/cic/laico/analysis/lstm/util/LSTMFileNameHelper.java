package br.unb.cic.laico.analysis.lstm.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import br.unb.cic.laico.analysis.lstm.config.LSTMConfiguration;

public final class LSTMFileNameHelper {

	private static final DateFormat DF_PREFIX_FILENAME = new SimpleDateFormat("yyyyMMdd-HHmmss");

	private LSTMFileNameHelper() {
	}

	public static String getOutputFileName(LSTMConfiguration configuration, String suffix) throws Exception {
		return getOutputFileName(configuration, suffix, true);
	}
	
	public static String getOutputFileName(LSTMConfiguration configuration, String suffix, boolean appendPath)
			throws Exception {
		return getOutputFileName(configuration, suffix, appendPath, ".txt");
	}

	public static String getOutputFileName(LSTMConfiguration configuration, String suffix, boolean appendPath,
			String extension) throws Exception {

		// File name object
		StringBuilder fileName = new StringBuilder();

		// Append predefined CSV path
		if (appendPath) {
			fileName.append(configuration.getInputCsvPath());
		}

		// Append configuration creation date
		fileName.append(DF_PREFIX_FILENAME.format(configuration.getCreationDate()));

		// Append configuration label
		fileName.append("-").append(configuration.getLabel());
		
		// Check instance type
		if (configuration.getInstanceType() != null) {
			fileName.append("-").append(configuration.getInstanceType());
		}

		// Check availability zone filter
		if (configuration.getAvailabilityZoneFilter() != null) {
			fileName.append("-").append(configuration.getAvailabilityZoneFilter());
		}

		// Append suffix
		fileName.append("-").append(suffix);

		// Append suffix and extension
		fileName.append(extension);

		// Return file name as String
		return fileName.toString();
	}
}
