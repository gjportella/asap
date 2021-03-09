package br.unb.cic.laico.script;

import java.text.NumberFormat;
import java.util.Locale;

import org.apache.log4j.Logger;

import br.unb.cic.laico.conversion.csv.CsvWrapperReader;
import br.unb.cic.laico.conversion.csv.CsvWrapperWriter;
import br.unb.cic.laico.statistics.Accumulator;

public class ScriptWritePriceAmpleness {

	private static Logger logger = Logger.getLogger(ScriptWritePriceAmpleness.class);

	private static final NumberFormat inNF = NumberFormat.getInstance(Locale.US);

	private static final String INPUT_CSV_DELIMITER = "\t";
	private static final String OUTPUT_CSV_DELIMITER = ";";

	private String basePath = "src/main/resources/ieee-tcc/";
	private String availabilityZoneFilter = "us-east-1c";

	private String[] firstQuarter2020 = new String[] {
			"2020-01",
			"2020-02",
			"2020-03"
		};

	private String[] secondQuarter2020 = new String[] {
			"2020-04",
			"2020-05",
			"2020-06"
		};

	private String[] instanceTypeArray = new String[] {
			"c5n.2xlarge",
			"c5n.9xlarge",
			"i3.2xlarge",
			"i3.8xlarge",
			"m5.2xlarge",
			"m5.4xlarge",
			"r4.2xlarge",
			"r5.2xlarge"
		};

	private void readDataFromFileToAccumulator(String absolutePath, Accumulator accumulator) {

		CsvWrapperReader csvReader = null;
		try {
			csvReader = new CsvWrapperReader(absolutePath, INPUT_CSV_DELIMITER);
			csvReader.openFile();

			String[] auxObj;
			while ((auxObj = csvReader.readLine()) != null) {

				// Check if input data is from the specified availability zone
				if (availabilityZoneFilter != null && !availabilityZoneFilter.equals(auxObj[1])) {
					continue;
				}

				// Parse spot price
				double spotPrice = Double.valueOf(inNF.parse(auxObj[4]).doubleValue());
				accumulator.addValue(spotPrice);
			}

		} catch (Exception ex) {
			logger.error("Error reading data from file " + absolutePath);

		} finally {
			if (csvReader != null) {
				try {
					csvReader.closeFile();
				} catch (Exception ex) {
					logger.error("Error closing file " + absolutePath);
				}
			}
		}
	}
	
	private Accumulator readDataFromFile(String instanceType, String[] yearMonthArray) {

		Accumulator accumulator = new Accumulator();
		for (String yearMonth: yearMonthArray) {
				String absolutePath = basePath + instanceType + "/us-east-1/"
						+ instanceType + "." + yearMonth + ".txt";
				readDataFromFileToAccumulator(absolutePath, accumulator);
		}		
		return accumulator;
	}
	
	private String readData(String periodLabel, String instanceType,
			String[] yearMonthArray, String availabilityZoneFilter) {
		
		StringBuilder data = new StringBuilder();

		Accumulator accum = readDataFromFile(instanceType, yearMonthArray);
		data.append(periodLabel)
				.append(OUTPUT_CSV_DELIMITER).append(instanceType)
				.append(OUTPUT_CSV_DELIMITER).append(yearMonthArray[0])
				.append(OUTPUT_CSV_DELIMITER).append(yearMonthArray[yearMonthArray.length-1])
				.append(OUTPUT_CSV_DELIMITER).append(Integer.toString(accum.getN()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(accum.getMin()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(accum.getMax()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(accum.getMean()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(accum.getVariance()))
				.append(OUTPUT_CSV_DELIMITER).append(Double.toString(accum.getStddev()))
				.append("\n");

		for (String yearMonth: yearMonthArray) {
			accum = readDataFromFile(instanceType, new String[] { yearMonth });
			data.append("Only" + yearMonth)
					.append(OUTPUT_CSV_DELIMITER).append(instanceType)
					.append(OUTPUT_CSV_DELIMITER).append(yearMonth)
					.append(OUTPUT_CSV_DELIMITER).append(yearMonth)
					.append(OUTPUT_CSV_DELIMITER).append(Integer.toString(accum.getN()))
					.append(OUTPUT_CSV_DELIMITER).append(Double.toString(accum.getMin()))
					.append(OUTPUT_CSV_DELIMITER).append(Double.toString(accum.getMax()))
					.append(OUTPUT_CSV_DELIMITER).append(Double.toString(accum.getMean()))
					.append(OUTPUT_CSV_DELIMITER).append(Double.toString(accum.getVariance()))
					.append(OUTPUT_CSV_DELIMITER).append(Double.toString(accum.getStddev()))
					.append("\n");
		}

		return data.toString();
	}

	public void runScript() {

		String outputFileName = basePath + "SpotPriceAmpleness.txt";

		CsvWrapperWriter csvWriter = null;
		try {
			csvWriter = new CsvWrapperWriter(outputFileName, OUTPUT_CSV_DELIMITER);
			csvWriter.openFile();

			// Write file reader
			StringBuilder header = new StringBuilder();
			header.append("Label")
				.append(OUTPUT_CSV_DELIMITER).append("InstanceType")
				.append(OUTPUT_CSV_DELIMITER).append("From")
				.append(OUTPUT_CSV_DELIMITER).append("To")
				.append(OUTPUT_CSV_DELIMITER).append("Ocurrences")
				.append(OUTPUT_CSV_DELIMITER).append("Min")
				.append(OUTPUT_CSV_DELIMITER).append("Max")
				.append(OUTPUT_CSV_DELIMITER).append("Mean")
				.append(OUTPUT_CSV_DELIMITER).append("Variance")
				.append(OUTPUT_CSV_DELIMITER).append("StdDev")
				.append("\n");
			csvWriter.write(header.toString());
			
			// Write data to file
			for (String instanceType: instanceTypeArray) {

				String data = readData("FirstQuarter2020", instanceType,
						firstQuarter2020, availabilityZoneFilter);
				csvWriter.write(data);

				data = readData("SecondQuarter2020", instanceType,
						secondQuarter2020, availabilityZoneFilter);
				csvWriter.write(data);
			}
			
		} catch (Exception ex) {
			logger.error("Error writing data to file " + outputFileName);

		} finally {
			try {
				csvWriter.closeFile();
			} catch (Exception ex) {
				logger.error("Error closing file " + outputFileName);
			}
		}
	}

	public static void main(String[] args) {

		ScriptWritePriceAmpleness app = new ScriptWritePriceAmpleness();
		app.runScript();
	}
}
