package br.unb.cic.laico.data.ec2;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import br.unb.cic.laico.conversion.csv.CsvWrapperReader;

public final class OnDemandPrice implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(OnDemandPrice.class);

	private static final NumberFormat inNF = NumberFormat.getInstance(Locale.US);

	private static final String INPUT_CSV_FILE_NAME = "src/main/resources/ondemand/202004-ondemand.txt";
	private static final String INPUT_CSV_DELIMITER = ";";

	private static OnDemandPrice instance;
	private Map<String, Double> onDemandPrices = new HashMap<String, Double>();

	private OnDemandPrice() {
		this.onDemandPrices = new HashMap<String, Double>();
		this.loadData();
	}

	private void loadData() {

		CsvWrapperReader csvReader = null;
		try {
			csvReader = new CsvWrapperReader(INPUT_CSV_FILE_NAME, INPUT_CSV_DELIMITER);
			csvReader.openFile();

			String[] auxObj;
			while ((auxObj = csvReader.readLine()) != null) {
				onDemandPrices.put(auxObj[0], Double.valueOf(inNF.parse(auxObj[1]).doubleValue()));
			}
			logger.debug("Ondemand prices successfully read from file " + INPUT_CSV_FILE_NAME);

		} catch (Exception ex) {
			logger.error("Error reading file " + INPUT_CSV_FILE_NAME, ex);

		} finally {
			if (csvReader != null) {
				try {
					csvReader.closeFile();
				} catch (Exception ex) {
					logger.error("Error closing file " + INPUT_CSV_FILE_NAME, ex);
				}
			}
		}
	}

	public static synchronized OnDemandPrice getInstance() {
		if (instance == null) {
			instance = new OnDemandPrice();
		}
		return instance;
	}

	public Double getPrice(String instanceType) {
		return onDemandPrices.get(instanceType);
	}
}
