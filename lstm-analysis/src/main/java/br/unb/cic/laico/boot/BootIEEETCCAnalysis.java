package br.unb.cic.laico.boot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.log4j.Logger;

import br.unb.cic.laico.analysis.lstm.LSTMBasedAnalysisBuilder;
import br.unb.cic.laico.analysis.lstm.config.LSTMConfiguration;
import br.unb.cic.laico.analysis.utility.UtilityBasedAnalysisBuilder;
import br.unb.cic.laico.analysis.utility.config.UtilityConfiguration;
import br.unb.cic.laico.conversion.csv.CsvWrapperWriter;

public class BootIEEETCCAnalysis {

	private static Logger logger = Logger.getLogger(BootIEEETCCAnalysis.class);

	private static final String OUTPUT_CSV_DELIMITER = ";";
	private static final DateFormat OUT_DF = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

	private String[] yearMonthArray = new String[] {
			"2020-01",
			"2020-02",
			"2020-03",
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

	private String[] configParamsArray = new String[] {
			//A;H;E;L;T
			"1;16;50;0.005;2",
			"1;16;50;0.005;3",
			"2;16;100;0.005;2",
			"2;16;100;0.005;3",
			"2;32;200;0.005;2",
			"2;32;200;0.005;3",
			"3;16;100;0.005;2",
			"3;16;100;0.005;3",
			"3;32;200;0.005;2",
			"3;32;200;0.005;3"
	};

	private LSTMConfiguration getLSTMConfiguration(String yearMonth, String instanceType, int numberOfFeatures,
			int hiddenLayerNodes, byte type, byte gradientDescentUpdater, double learningRate, int numberOfEpochs) {

		// Configuration object
		LSTMConfiguration config = new LSTMConfiguration();

		// Processing parameters
		config.setInstanceType(instanceType);
		config.setAvailabilityZoneFilter("us-east-1c");
		config.setTrainingDataProportion(0.7);
		config.setNumberOfFeatures(numberOfFeatures);
		config.setStandardizeData(true);
		config.setInputCsvPath("src/main/resources/ieee-tcc/" + instanceType + "/us-east-1/");
		config.setInputCsvFileNames(new String[] { instanceType + "." + yearMonth + ".txt" });

		// Network parameters
		config.setType(type);
		config.setGradientDescentUpdater(gradientDescentUpdater);
		config.setSeed(123);
		config.setLearningRate(learningRate);
		config.setHiddenLayer1Nodes(hiddenLayerNodes);
		config.setHiddenLayer2Nodes(hiddenLayerNodes);
		config.setDenseLayerNodes(hiddenLayerNodes);
		config.setGradientNormalizationThreshold(10);
		config.setInputNodes(numberOfFeatures - 1);
		config.setOutputNodes(1);
		config.setNumberOfEpochs(numberOfEpochs);
		config.setBatchSize(1024);
		return config;
	}

	private UtilityConfiguration getUtilityConfiguration(String instanceType) {

		// Configuration object
		UtilityConfiguration config = new UtilityConfiguration();

		// Processing parameters
		config.setInstanceType(instanceType);
		config.setAvailabilityZoneFilter("us-east-1c");
		config.setInputCsvPath("src/main/resources/ieee-tcc/" + instanceType + "/us-east-1/");

		// Model parameters
		config.setEstimationAccuracy(0.001);
		config.setOdPriceFactorLowerLimit(0);
		config.setOdPriceFactorUpperLimit(1);
		return config;
	}
	
	public void bootAnalysis() throws Exception {

		Locale.setDefault(Locale.US); 
		
		int gaugeSize = yearMonthArray.length
				* instanceTypeArray.length
				* configParamsArray.length;
		int gaugeCount = 0;

		LSTMConfiguration lstmConfig;
		LSTMBasedAnalysisBuilder lstmApp;
		UtilityConfiguration utilityConfig;
		UtilityBasedAnalysisBuilder utilityApp;

		CsvWrapperWriter csv = new CsvWrapperWriter("src/main/resources/ieee-tcc/IEEETCCAnalysis.txt");
		try {
			csv.openFile();
			csv.write("YearMonth;" + "ConfigDate;" + "ExecutionTime;" + "TrainingTime;" + "InstanceType;"
					+ "LSTMConfig;"	+ "TrainingAverageMSE;" + "TrainingAverageRMSE;" + "PredictionAverageMSE;"
					+ "PredictionAverageRMSE;" + "UtilityConfig;" + "UtilityMSE;" + "UtilityRMSE;"
					+ "FutureMSEForFixedBid;" + "FutureMSEForVariableBid" + "\n");

			for (String yearMonth : yearMonthArray) {
				for (String instanceType : instanceTypeArray) {
					for (String configParams : configParamsArray) {

						String[] splittedParams = configParams.split(";");
						byte gradientDescentUpdater = Byte.parseByte(splittedParams[0]);
						int hiddenLayerNodes = Integer.parseInt(splittedParams[1]);
						int numberOfEpochs = Integer.parseInt(splittedParams[2]);
						double learningRate = Double.parseDouble(splittedParams[3]);
						byte networkType = Byte.parseByte(splittedParams[4]);
						int numberOfFeatures = 2;

						lstmConfig = getLSTMConfiguration(yearMonth, instanceType, numberOfFeatures,
								hiddenLayerNodes, networkType, gradientDescentUpdater, learningRate, numberOfEpochs);
						lstmApp = new LSTMBasedAnalysisBuilder();
						lstmApp.doAnalysis(lstmConfig);

						utilityConfig = getUtilityConfiguration(instanceType);
						utilityConfig.setComplementaryLabel(lstmConfig.getLabel());
						utilityConfig.setCreationDate(lstmConfig.getCreationDate());
						utilityApp = new UtilityBasedAnalysisBuilder();
						utilityApp.doAnalysis(utilityConfig, lstmApp.getSpotHistoryPointList());

						StringBuilder buffer = new StringBuilder();
						buffer.append(yearMonth)
								.append(OUTPUT_CSV_DELIMITER)
								.append(OUT_DF.format(lstmConfig.getCreationDate()))
								.append(OUTPUT_CSV_DELIMITER)
								.append(Long.toString(lstmApp.getEndTimeMilis() - lstmApp.getStartTimeMilis()))
								.append(OUTPUT_CSV_DELIMITER)
								.append(Long.toString(lstmConfig.getTrainingTimeMilis()))
								.append(OUTPUT_CSV_DELIMITER)
								.append(instanceType)
								.append(OUTPUT_CSV_DELIMITER)
								.append(lstmConfig.getLabel())
								.append(OUTPUT_CSV_DELIMITER)
								.append(Double.toString(lstmApp.getTrainingAverageMSE()))
								.append(OUTPUT_CSV_DELIMITER)
								.append(Double.toString(lstmApp.getTrainingAverageRMSE()))
								.append(OUTPUT_CSV_DELIMITER)
								.append(Double.toString(lstmApp.getPredictionAverageMSE()))
								.append(OUTPUT_CSV_DELIMITER)
								.append(Double.toString(lstmApp.getPredictionAverageRMSE()))
								.append(OUTPUT_CSV_DELIMITER)
								.append(utilityConfig.getLabel())
								.append(OUTPUT_CSV_DELIMITER)
								.append(Double.toString(utilityApp.getMSE()))
								.append(OUTPUT_CSV_DELIMITER)
								.append(Double.toString(utilityApp.getRMSE()))
								.append(OUTPUT_CSV_DELIMITER)
								.append(Double.toString(utilityApp.getFutureMSEForFixedBid()))
								.append(OUTPUT_CSV_DELIMITER)
								.append(Double.toString(utilityApp.getFutureMSEForVariableBid()))
								.append("\n");
						csv.write(buffer.toString());

						gaugeCount++;
						logger.debug("Analysis status (%): " + String.format("%.2f",
								100 * (double) gaugeCount / (double) gaugeSize));
					}
				}
			}

		} finally {
			csv.closeFile();
		}
	}

	public static void main(String[] args) throws Exception {
		BootIEEETCCAnalysis app = new BootIEEETCCAnalysis();
		app.bootAnalysis();
	}
}