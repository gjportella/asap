package br.unb.cic.laico.script;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import br.unb.cic.laico.conversion.csv.CsvWrapperReader;
import br.unb.cic.laico.conversion.csv.CsvWrapperWriter;

public class WriteSpotPriceAmplenessFile {
	
	private static Logger logger = Logger.getLogger(WriteSpotPriceAmplenessFile.class);
	
	private static final NumberFormat inNF = NumberFormat.getInstance(Locale.US);
	
	private static final String INPUT_CSV_DELIMITER = "\t";
	private static final String OUTPUT_CSV_DELIMITER = ";";
	
	private static final String DESTINATION_PATH = "c:/Temp/spot history/";
	private static final String CSV_FILE_NAME = "spot_ampleness.csv";
	
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
			"201904", "201905", "201906", "201907", "201908", "201909",
			"201910", "201911",	"201912", "202001", "202002", "202003"
		};
	
	public class InstanceTypeAvZoneData {
		
		double minPrice;
		double maxPrice;
		int priceOcurrences;
		
		public InstanceTypeAvZoneData() {
		}
		
		public double getMinPrice() {
			return minPrice;
		}
		
		public void setMinPrice(double minPrice) {
			this.minPrice = minPrice;
		}
		
		public double getMaxPrice() {
			return maxPrice;
		}
		
		public void setMaxPrice(double maxPrice) {
			this.maxPrice = maxPrice;
		}
		
		public int getPriceOcurrences() {
			return priceOcurrences;
		}
		
		public void setPriceOcurrences(int priceOcurrences) {
			this.priceOcurrences = priceOcurrences;
		}
	}
	
	public WriteSpotPriceAmplenessFile() {
	}
	
	private Map<String, InstanceTypeAvZoneData> readInstanceTypeYearMonthCsvFile(String yearMonthPeriod,
			String instanceType) throws Exception {
		
		Map<String, InstanceTypeAvZoneData> avZoneMinMaxMap = new HashMap<String, InstanceTypeAvZoneData>();
		CsvWrapperReader csvReader = null;
		try {
			
			csvReader = new CsvWrapperReader(DESTINATION_PATH + yearMonthPeriod
					+ "_currgen/" + instanceType + ".txt", INPUT_CSV_DELIMITER);
			csvReader.openFile();
			
			String[] auxObj;
			while ((auxObj = csvReader.readLine()) != null) {
				
				String avZone = auxObj[1];
				double price = inNF.parse(auxObj[4]).doubleValue();
				
				InstanceTypeAvZoneData instanceTypeAvZoneData = avZoneMinMaxMap.get(avZone);
				if (instanceTypeAvZoneData == null) {

					instanceTypeAvZoneData = new InstanceTypeAvZoneData();
					instanceTypeAvZoneData.setMinPrice(price);
					instanceTypeAvZoneData.setMaxPrice(price);
					instanceTypeAvZoneData.setPriceOcurrences(1);
					avZoneMinMaxMap.put(avZone, instanceTypeAvZoneData);
					
				} else {
					
					double minPrice = instanceTypeAvZoneData.getMinPrice();
					double maxPrice = instanceTypeAvZoneData.getMaxPrice();
					if (price < minPrice) {
						instanceTypeAvZoneData.setMinPrice(price);
					} else if (price > maxPrice) {
						instanceTypeAvZoneData.setMaxPrice(price);
					}
					instanceTypeAvZoneData.setPriceOcurrences(
							instanceTypeAvZoneData.getPriceOcurrences()+1);
				}
			}
			
		} finally {
			if (csvReader != null) {
				csvReader.closeFile();
			}
		}
		return avZoneMinMaxMap;
	}
	
	public void writeCsvFile() throws Exception {
		
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(
				DESTINATION_PATH + CSV_FILE_NAME);
		
		try {
			csvWriter.openFile();
			for (String yearMonthPeriod: YEAR_MONTH_PERIODS) {
				for (String instanceType: INSTANCE_TYPES) {
					
					Map<String, InstanceTypeAvZoneData> avZoneMinMaxMap
							= readInstanceTypeYearMonthCsvFile(yearMonthPeriod, instanceType);
					Set<String> keySet = avZoneMinMaxMap.keySet();
					for (String avZone: keySet) {
						
						InstanceTypeAvZoneData obj = avZoneMinMaxMap.get(avZone);
						StringBuilder data = new StringBuilder();
						data.append(instanceType)
							.append(OUTPUT_CSV_DELIMITER);
						data.append(yearMonthPeriod)
							.append(OUTPUT_CSV_DELIMITER);
						data.append(avZone)
							.append(OUTPUT_CSV_DELIMITER);
						data.append(obj.getPriceOcurrences())
							.append(OUTPUT_CSV_DELIMITER);
						data.append(Double.toString(obj.getMinPrice()))
							.append(OUTPUT_CSV_DELIMITER);
						data.append(Double.toString(obj.getMaxPrice()))
							.append(OUTPUT_CSV_DELIMITER);
						data.append(Double.toString(obj.getMaxPrice() - obj.getMinPrice()));
						csvWriter.writeLine(data.toString());
					}
				}
			}
			
		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
	}
	
	public static void main(String[] args) {

		logger.debug("Writing Spot Price ampleness file...");
		WriteSpotPriceAmplenessFile app = new WriteSpotPriceAmplenessFile();
		try {
			app.writeCsvFile();
			logger.debug("Done!");
			
		} catch (Exception ex) {
			logger.error(ex);
		}
	}
}
