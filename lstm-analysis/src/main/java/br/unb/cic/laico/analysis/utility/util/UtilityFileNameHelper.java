package br.unb.cic.laico.analysis.utility.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import br.unb.cic.laico.analysis.utility.config.UtilityConfiguration;

public class UtilityFileNameHelper {

	private static final DateFormat DF_PREFIX_FILENAME = new SimpleDateFormat("yyyyMMdd-HHmmss");
	
	public static String getOutputFileName(UtilityConfiguration configuration, String suffix) throws Exception {
		return getOutputFileName(configuration, suffix, true, ".txt");
	}

	public static String getOutputFileName(UtilityConfiguration configuration, String suffix, boolean appendPath,
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
