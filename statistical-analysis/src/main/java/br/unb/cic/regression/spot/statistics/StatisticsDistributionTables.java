package br.unb.cic.regression.spot.statistics;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import br.unb.cic.conversion.csv.CsvWrapperReader;

public final class StatisticsDistributionTables implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(StatisticsDistributionTables.class);
	
	private final static NumberFormat NF = NumberFormat.getInstance(Locale.US);
	
	private Map<Double, Double> tDistributionQuantiles30;
	private Map<Double, Double> tDistributionQuantiles60;
	private Map<Double, Double> tDistributionQuantiles120;
	
	private String[][] fDistribution0_001;
	private String[][] fDistribution0_010;
	private String[][] fDistribution0_025;
	private String[][] fDistribution0_050;
	private String[][] fDistribution0_100;
	
	private static StatisticsDistributionTables instance;
	
	public static synchronized StatisticsDistributionTables getInstance() {
		if (instance == null) {
			instance = new StatisticsDistributionTables();
		}
		return instance;
	}
	
	private StatisticsDistributionTables() {
		
		this.tDistributionQuantiles30 = initTDistributionQuantiles30();
		this.tDistributionQuantiles60 = initTDistributionQuantiles60();
		this.tDistributionQuantiles120 = initTDistributionQuantiles120();
		
		this.fDistribution0_001 = initFDistribution("src/main/resources/f_tables/f_table_alpha_0_001.txt", "\t");
		this.fDistribution0_010 = initFDistribution("src/main/resources/f_tables/f_table_alpha_0_010.txt", "\t");
		this.fDistribution0_025 = initFDistribution("src/main/resources/f_tables/f_table_alpha_0_025.txt", "\t");
		this.fDistribution0_050 = initFDistribution("src/main/resources/f_tables/f_table_alpha_0_050.txt", "\t");
		this.fDistribution0_100 = initFDistribution("src/main/resources/f_tables/f_table_alpha_0_100.txt", "\t");
	}
	
	private Map<Double, Double> initTDistributionQuantiles30() {
		Map<Double, Double> distribution = new HashMap<Double, Double>(8);
		distribution.put(Double.valueOf(0.600d), Double.valueOf(0.256d));
		distribution.put(Double.valueOf(0.700d), Double.valueOf(0.530d));
		distribution.put(Double.valueOf(0.800d), Double.valueOf(0.854d));
		distribution.put(Double.valueOf(0.900d), Double.valueOf(1.310d));
		distribution.put(Double.valueOf(0.950d), Double.valueOf(1.697d));
		distribution.put(Double.valueOf(0.975d), Double.valueOf(2.042d));
		distribution.put(Double.valueOf(0.995d), Double.valueOf(2.750d));
		distribution.put(Double.valueOf(0.999d), Double.valueOf(3.646d));
		return distribution;
	}
	
	private Map<Double, Double> initTDistributionQuantiles60() {
		Map<Double, Double> distribution = new HashMap<Double, Double>(8);
		distribution.put(Double.valueOf(0.600d), Double.valueOf(0.254d));
		distribution.put(Double.valueOf(0.700d), Double.valueOf(0.527d));
		distribution.put(Double.valueOf(0.800d), Double.valueOf(0.848d));
		distribution.put(Double.valueOf(0.900d), Double.valueOf(1.296d));
		distribution.put(Double.valueOf(0.950d), Double.valueOf(1.671d));
		distribution.put(Double.valueOf(0.975d), Double.valueOf(2.000d));
		distribution.put(Double.valueOf(0.995d), Double.valueOf(2.660d));
		distribution.put(Double.valueOf(0.999d), Double.valueOf(3.460d));
		return distribution;
	}
	
	private Map<Double, Double> initTDistributionQuantiles120() {
		Map<Double, Double> distribution = new HashMap<Double, Double>(8);
		distribution.put(Double.valueOf(0.600d), Double.valueOf(0.254d));
		distribution.put(Double.valueOf(0.700d), Double.valueOf(0.526d));
		distribution.put(Double.valueOf(0.800d), Double.valueOf(0.845d));
		distribution.put(Double.valueOf(0.900d), Double.valueOf(1.289d));
		distribution.put(Double.valueOf(0.950d), Double.valueOf(1.658d));
		distribution.put(Double.valueOf(0.975d), Double.valueOf(1.980d));
		distribution.put(Double.valueOf(0.995d), Double.valueOf(2.617d));
		distribution.put(Double.valueOf(0.999d), Double.valueOf(3.373d));
		return distribution;
	}
	
	private String[][] initFDistribution(String csvPath, String delimiter) {

		String[][] distribution = null;
		CsvWrapperReader csvReader = new CsvWrapperReader(csvPath, delimiter);
		try {
			csvReader.openFile();
			List<String[]> list = new ArrayList<String[]>();
			String[] line;
			while ((line = csvReader.readLine()) != null) {
				list.add(line);
			}
			distribution = list.toArray(new String[0][0]);

		} catch (Exception ex) {
			logger.error("Error reading file " + csvPath, ex);

		} finally {
			if (csvReader != null) {
				try {
					csvReader.closeFile();
				} catch (Exception ex) {
					logger.error("Error closing file " + csvPath, ex);
				}
			}
		}
		return distribution;
	}
	
	public double getTDistributionValue(double alpha, int df) throws Exception {
		
		Map<Double, Double> distribution;
		/*if (df < 30) {
			throw new Exception("Value of df must be greater than 30.");
		} else*/ if (df < 60) {
			distribution = tDistributionQuantiles30;
		} else if (df <120) {
			distribution = tDistributionQuantiles60;
		} else {
			distribution = tDistributionQuantiles120;
		}
		
		double quantil = 1d - (alpha / 2d);
		Double aux = distribution.get(Double.valueOf(quantil));
		if (aux == null) {
			throw new Exception("Invalid parameter alpha.");
		}
		return aux.doubleValue();
	}

	public double getFDistributionValue(double alpha, int numeratorDF, int denominatorDF)
			throws Exception {
		
		String[][] distribution;
		if (alpha == 0.001d) {
			distribution = fDistribution0_001;
		} else if (alpha == 0.010d) {
			distribution = fDistribution0_010;
		} else if (alpha == 0.025d) {
			distribution = fDistribution0_025;
		} else if (alpha == 0.050d) {
			distribution = fDistribution0_050;
		} else if (alpha == 0.100d) {
			distribution = fDistribution0_100;
		} else {
			throw new Exception("Invalid alpha.");
		}
		
		int indexDF1 = 0;
		if (numeratorDF < 1) {
			throw new Exception("Invalid numerator degree of freedom (df).");
		} else if (numeratorDF >= 1 && numeratorDF < 10) {
			indexDF1 = numeratorDF;
		} else if (numeratorDF >= 10 && numeratorDF < 12) {
			indexDF1 = 10;
		} else if (numeratorDF >= 12 && numeratorDF < 15) {
			indexDF1 = 11;
		} else if (numeratorDF >= 15 && numeratorDF < 20) {
			indexDF1 = 12;
		} else if (numeratorDF >= 20 && numeratorDF < 24) {
			indexDF1 = 13;
		} else if (numeratorDF >= 24 && numeratorDF < 30) {
			indexDF1 = 14;
		} else if (numeratorDF >= 30 && numeratorDF < 40) {
			indexDF1 = 15;
		} else if (numeratorDF >= 40 && numeratorDF < 60) {
			indexDF1 = 16;
		} else if (numeratorDF >= 60 && numeratorDF < 120) {
			indexDF1 = 17;
		} else if (numeratorDF >= 120 && numeratorDF < 240) {
			indexDF1 = 18;
		} else if (numeratorDF >= 240) {
			indexDF1 = 19;
		}
		
		int indexDF2 = 0;
		if (denominatorDF < 1) {
			throw new Exception("Invalid denominator degree of freedom (df).");
		} else if (denominatorDF >= 1 && denominatorDF < 30) {
			indexDF2 = denominatorDF;
		} else if (denominatorDF >= 30 && denominatorDF < 40) {
			indexDF2 = 30;
		} else if (denominatorDF >= 40 && denominatorDF < 60) {
			indexDF2 = 31;
		} else if (denominatorDF >= 60 && denominatorDF < 120) {
			indexDF2 = 32;
		} else if (denominatorDF >= 120 && denominatorDF < 240) {
			indexDF2 = 33;
		} else if (denominatorDF >= 240) {
			indexDF2 = 34;
		}
		
		Number number = NF.parse(distribution[indexDF2][indexDF1]);
		return number.doubleValue();
	}
}
