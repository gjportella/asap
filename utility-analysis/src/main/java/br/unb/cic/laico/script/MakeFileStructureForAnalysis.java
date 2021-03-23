package br.unb.cic.laico.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

public class MakeFileStructureForAnalysis {

	private static Logger logger = Logger.getLogger(MakeFileStructureForAnalysis.class);

	private static final String SOURCE_PATH = "c:/Temp/spot history";
	private static final String DESTINATION_PATH = "c:/Temp/structured";
	private static final String AV_ZONE = "us-east-1";

	private static final String[] INSTANCE_TYPES = new String[] {
			"a1.2xlarge", "a1.4xlarge", "a1.large", "a1.medium", "a1.xlarge",
			"c4.2xlarge", "c4.4xlarge", "c4.8xlarge", "c4.large", "c4.xlarge",
			"c5.18xlarge", "c5.2xlarge", "c5.4xlarge", "c5.9xlarge", "c5.large", "c5.xlarge",
			"c5d.18xlarge", "c5d.2xlarge", "c5d.4xlarge", "c5d.9xlarge", "c5d.xlarge",
			"c5n.18xlarge", "c5n.2xlarge", "c5n.4xlarge", "c5n.9xlarge", "c5n.large", "c5n.xlarge",
			"d2.2xlarge", "d2.4xlarge", "d2.8xlarge", "d2.xlarge",
			"f1.16xlarge", "f1.2xlarge", "f1.4xlarge",
			"g3.16xlarge", "g3.4xlarge", "g3.8xlarge", "g3s.xlarge",
			"h1.16xlarge", "h1.2xlarge", "h1.4xlarge", "h1.8xlarge",
			"i3.16xlarge", "i3.2xlarge", "i3.4xlarge", "i3.8xlarge", "i3.large", "i3.metal", "i3.xlarge",
			"m4.10xlarge", "m4.16xlarge", "m4.2xlarge", "m4.4xlarge", "m4.large", "m4.xlarge",
			"m5.12xlarge", "m5.24xlarge", "m5.2xlarge", "m5.4xlarge", "m5.large", "m5.xlarge",
			"m5a.12xlarge", "m5a.24xlarge", "m5a.2xlarge", "m5a.4xlarge", "m5a.large", "m5a.xlarge",
			"m5d.12xlarge", "m5d.24xlarge", "m5d.2xlarge", "m5d.4xlarge", "m5d.large", "m5d.xlarge",
			"p2.16xlarge", "p2.8xlarge", "p2.xlarge",
			"p3.16xlarge", "p3.2xlarge", "p3.8xlarge",
			"r4.16xlarge", "r4.2xlarge", "r4.4xlarge", "r4.8xlarge", "r4.large", "r4.xlarge",
			"r5.12xlarge", "r5.24xlarge", "r5.2xlarge", "r5.4xlarge", "r5.large", "r5.xlarge",
			"r5a.12xlarge", "r5a.24xlarge", "r5a.2xlarge", "r5a.4xlarge", "r5a.large", "r5a.xlarge",
			"r5d.12xlarge", "r5d.24xlarge", "r5d.2xlarge", "r5d.4xlarge", "r5d.large", "r5d.xlarge",
			"t2.2xlarge", "t2.large", "t2.medium", "t2.micro", "t2.nano", "t2.small", "t2.xlarge",
			"t3.2xlarge", "t3.large", "t3.medium", "t3.micro", "t3.nano", "t3.small", "t3.xlarge",
			"u-12tb1.metal", "u-6tb1.metal", "u-9tb1.metal",
			"x1.16xlarge", "x1.32xlarge",
			"x1e.16xlarge", "x1e.2xlarge", "x1e.32xlarge", "x1e.4xlarge", "x1e.8xlarge", "x1e.xlarge",
			"z1d.12xlarge", "z1d.2xlarge", "z1d.3xlarge", "z1d.6xlarge", "z1d.large", "z1d.xlarge"
		};
	
	private static final String[] YEAR_MONTH_PERIODS = new String[] {
			"2020-04", "2020-05", "2020-06"
		};
	
	public void makeStructure(String instanceType, String year, String month) throws Exception {

		// Make root directory, if it does not exist
		String destinationPath = DESTINATION_PATH;
		File directory = new File(destinationPath);
		if (!directory.exists()) {
			directory.mkdir();
		}

		// Make directory for instance type, if it does not exist
		destinationPath += "/" + instanceType;
		directory = new File(destinationPath);
		if (!directory.exists()) {
			directory.mkdir();
		}

		// Make directory for availability zone, if it does not exist
		destinationPath += "/" + AV_ZONE;
		directory = new File(destinationPath);
		if (!directory.exists()) {
			directory.mkdir();
		}

		// List files from source
		String sourcePath = SOURCE_PATH + "/" + year + month + "_currgen";
		File sourceDir = new File(sourcePath);
		if (!sourceDir.isDirectory()) {
			throw new IllegalStateException("Source path \"" + sourcePath + "\" must be a directory.");
		}
		String[] sourceFiles = sourceDir.list();

		// Copy instance type file to destination path
		String destinationFileName = destinationPath + "/" + instanceType + "." + year + "-" + month + ".txt";
		for (String sourceFileName : sourceFiles) {
			if (sourceFileName.startsWith(instanceType)) {
				copyFile(sourcePath + "/" + sourceFileName, destinationFileName);
				logger.debug(sourceFileName + " -> " + destinationFileName);
				break;
			}
		}
	}

	private void copyFile(String sourceLocation, String targetLocation) throws Exception {

		InputStream in = new FileInputStream(sourceLocation);
		OutputStream out = new FileOutputStream(targetLocation);

		// Copy the bits from input stream to output stream
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static void main(String[] args) {

		logger.debug("Making file structure for analysis...");
		MakeFileStructureForAnalysis app = new MakeFileStructureForAnalysis();
		for (String yearMonthPeriod: YEAR_MONTH_PERIODS) {
			
			String[] aux = yearMonthPeriod.split("-");
			String year = aux[0];
			String month = aux[1];
			for (String instanceType: INSTANCE_TYPES) {
				
				try {
					app.makeStructure(instanceType, year, month);
				} catch (Exception ex) {
					logger.error(ex);
				}
			}
		}

		logger.debug("Done!");
	}
}
