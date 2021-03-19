package br.unb.cic.laico.analysis.utility.model;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import br.unb.cic.laico.analysis.utility.config.UtilityConfiguration;
import br.unb.cic.laico.analysis.utility.util.UtilityFileNameHelper;
import br.unb.cic.laico.conversion.csv.CsvWrapperWriter;
import br.unb.cic.laico.data.ec2.SpotHistoryPoint;
import br.unb.cic.laico.data.utility.UtilityInformation;
import br.unb.cic.laico.statistics.Accumulator;

public class UtilityReportBuilder implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(UtilityReportBuilder.class);

	private static final String OUTPUT_CSV_DELIMITER = ";";
	private static final DateFormat OUT_DF = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
	private static final DateFormat OUT_DF_DATE_ONLY = new SimpleDateFormat("dd/MM/yyyy");
	private static final String FLOAT_MASK = "%.8f";

	private UtilityConfiguration configuration;
	private List<SpotHistoryPoint> spotHistoryPointList;
	
	private long startTimeMilis;
	private long endTimeMilis;

	private double mse;
	private double rmse;

	private double futureMSEForFixedBid;
	private double futureMSEForVariableBid;

	public UtilityReportBuilder(UtilityConfiguration configuration, List<SpotHistoryPoint> spotHistoryPointList,
			long startTimeMilis, long endTimeMilis) {

		this.configuration = configuration;
		this.spotHistoryPointList = spotHistoryPointList;
		this.startTimeMilis = startTimeMilis;
		this.endTimeMilis = endTimeMilis;
	}

	public void buildReports() throws Exception {
		
		writeGNUPlotDataToCSV();
		writeGNUPlotScript();
		writeFinalReport();
	}
	
	private void writeGNUPlotDataToCSV() throws Exception {
		
		logger.debug("Writing Utility GNUPlot data");

		String fileName = UtilityFileNameHelper.getOutputFileName(configuration, "utility-gnuplot-data");
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(fileName, OUTPUT_CSV_DELIMITER);
		try {
			csvWriter.openFile();
			for (SpotHistoryPoint point: spotHistoryPointList) {

				StringBuilder data = new StringBuilder();
				data.append(OUT_DF.format(point.getDateObject()));
				data.append(OUTPUT_CSV_DELIMITER);
				data.append(Double.toString(point.getPrice()));
				
				if (point.getUtilityInformation() != null) {
					data.append(OUTPUT_CSV_DELIMITER);
					data.append(Double.toString(point.getUtilityInformation().getBid()));
					data.append(OUTPUT_CSV_DELIMITER);
					data.append(Double.toString(point.getUtilityInformation().getAvailability()));
					data.append(OUTPUT_CSV_DELIMITER);
					data.append(Double.toString(point.getUtilityInformation().getUtility()));
				} else {
					data.append(OUTPUT_CSV_DELIMITER);
					data.append("NaN");
					data.append(OUTPUT_CSV_DELIMITER);
					data.append("NaN");
					data.append(OUTPUT_CSV_DELIMITER);
					data.append("NaN");
				}
				
				if (point.getLSTMInformation() != null) {
					data.append(OUTPUT_CSV_DELIMITER);
					data.append(Double.toString(point.getLSTMInformation().getPredictedPrice()));
				} else {
					data.append(OUTPUT_CSV_DELIMITER);
					data.append("NaN");
				}

				csvWriter.writeLine(data.toString());
			}

		} catch (Exception ex) {
			logger.error("Error writing data to file " + fileName);
			throw ex;

		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
	}
	
	private void writeGNUPlotScript() throws Exception {

		logger.debug("Writing Utility GNUPlot script");
	
		SpotHistoryPoint firstPoint = spotHistoryPointList.get(0);
		SpotHistoryPoint lastPoint = spotHistoryPointList.get(spotHistoryPointList.size() - 1);
		
		String dataFileName = UtilityFileNameHelper.getOutputFileName(configuration, "utility-gnuplot-data", false, ".txt");
		String pngFileName = UtilityFileNameHelper.getOutputFileName(configuration, "utility-gnuplot-script", false, ".png");
		String scriptFileName = UtilityFileNameHelper.getOutputFileName(configuration, "utility-gnuplot-script");
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(scriptFileName);

		try {
			csvWriter.openFile();
			String title = configuration.getInstanceType()
					+ " from " + OUT_DF_DATE_ONLY.format(firstPoint.getDateObject())
					+ " to " + OUT_DF_DATE_ONLY.format(lastPoint.getDateObject())
					+ " " + configuration.getLabel()
					+ " " + configuration.getComplementaryLabel();

			csvWriter.writeLine("reset");
			csvWriter.writeLine("set title \"" + title + "\"");
			csvWriter.writeLine("set xlabel \"Date (dd/mm)\"");
			csvWriter.writeLine("set xdata time");
			csvWriter.writeLine("set timefmt \"%H:%M:%S %d/%m/%Y\"");
			csvWriter.writeLine("set xrange [\"00:00:00 " + OUT_DF_DATE_ONLY.format(firstPoint.getDateObject())
					+ "\":\"23:59:59 " + OUT_DF_DATE_ONLY.format(lastPoint.getDateObject()) + "\"]");
			csvWriter.writeLine("set format x \"%d/%m\"");
			csvWriter.writeLine("set ylabel \"Price (USD/hour)\"");
			csvWriter.writeLine("set autoscale y");
			csvWriter.writeLine("set ytics");
			csvWriter.writeLine("set key left top box");
			csvWriter.writeLine("set grid");
			csvWriter.writeLine("set datafile separator \";\"");
			csvWriter.writeLine("set datafile missing \"NaN\"");
			csvWriter.writeLine("set terminal png size 1300,650");
			
			File f = new File(configuration.getInputCsvPath());
			csvWriter.writeLine("cd '" + f.getAbsolutePath() + "'");
			csvWriter.writeLine("set output \"" + pngFileName + "\"");
			
			csvWriter.writeLine("plot \"" + dataFileName + "\" using 1:2 axes x1y1 with lines lw 2 lt 1 lc rgb \"#0000FF\" title 'Spot', \\");
			csvWriter.writeLine("     \"" + dataFileName + "\" using 1:3 axes x1y1 with lines lw 2 lt 1 lc rgb \"#FF0000\" title 'Bid (Utility)', \\");
			csvWriter.writeLine("     \"" + dataFileName + "\" using 1:6 axes x1y1 with lines lw 2 lt 1 lc rgb \"#00FF00\" title 'Predicted (LSTM)'");

		} catch (Exception ex) {
			logger.error("Error writing data to file " + scriptFileName);
			throw ex;

		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
	}
	
	private void writeFinalReport() throws Exception {
		
		logger.debug("Writing Final Report");
		
		// First and last points from price history
		SpotHistoryPoint firstPoint = spotHistoryPointList.get(0);
		SpotHistoryPoint lastPoint = spotHistoryPointList.get(spotHistoryPointList.size() - 1);

		// Data accumulators
		Accumulator priceAcum = new Accumulator();
		Accumulator bidAcum = new Accumulator();
		Accumulator availabilityAcum = new Accumulator();
		Accumulator utilityAcum = new Accumulator();
		
		Accumulator futureMSEForFixedBidAcum = new Accumulator();
		Accumulator futureMSEForVariableBidAcum = new Accumulator();

		int occurrences = 0;
		double sse = 0;
		for (SpotHistoryPoint point: spotHistoryPointList) {
			
			UtilityInformation utilityInfo = point.getUtilityInformation();
			if (point.getUtilityInformation() != null) {
				
				priceAcum.addValue(point.getPrice());
				bidAcum.addValue(utilityInfo.getBid());
				availabilityAcum.addValue(utilityInfo.getAvailability());
				utilityAcum.addValue(utilityInfo.getUtility());
				
				// MSE and RMSE calculation
				double error = point.getPrice() - utilityInfo.getBid();
				sse += Math.pow(error, 2);
				occurrences++;
				
				// Future evaluate errors
				if (utilityInfo.getFutureEstimateForFixedBid() != null) {
					futureMSEForFixedBidAcum.addValue(utilityInfo.getFutureEstimateForFixedBid().getMSE());
				}
				if (utilityInfo.getFutureEstimateForVariableBid() != null) {
					futureMSEForVariableBidAcum.addValue(utilityInfo.getFutureEstimateForVariableBid().getMSE());
				}
			}
		}
		
		// MSE and RMSE results
		mse = sse / occurrences;
		rmse = Math.pow(mse, 0.5);
		
		// Future evaluate errors
		futureMSEForFixedBid = futureMSEForFixedBidAcum.getMean();
		futureMSEForVariableBid = futureMSEForVariableBidAcum.getMean();
		
		String reportFileName = UtilityFileNameHelper.getOutputFileName(configuration, "final-report");
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(reportFileName, OUTPUT_CSV_DELIMITER);
		try {
			csvWriter.openFile();
			csvWriter.writeLine("Start time", OUT_DF.format(new Date(startTimeMilis)));
			csvWriter.writeLine("Label", configuration.getLabel());
			csvWriter.writeLine("Instance type", configuration.getInstanceType());
			csvWriter.writeLine("Availability zone filter", configuration.getAvailabilityZoneFilter());
			csvWriter.writeLine("First point", OUT_DF.format(firstPoint.getDateObject()));
			csvWriter.writeLine("Last point", OUT_DF.format(lastPoint.getDateObject()));
			csvWriter.writeLine("Occurrences read", Integer.toString(spotHistoryPointList.size()));
			csvWriter.writeLine("Occurrences analysed", Integer.toString(occurrences));

			csvWriter.writeLine("Estimation accuracy (sigma)",
					Double.toString(configuration.getEstimationAccuracy()));
			csvWriter.writeLine("On demand Lower Limit Price Factor (eta)",
					Double.toString(configuration.getOdPriceFactorLowerLimit()));
			csvWriter.writeLine("On demand Upper Limit Price Factor (theta)",
					Double.toString(configuration.getOdPriceFactorUpperLimit()));

			csvWriter.writeLine("Average price", String.format(FLOAT_MASK, priceAcum.getMean()));
			csvWriter.writeLine("Average bid", String.format(FLOAT_MASK, bidAcum.getMean()));
			csvWriter.writeLine("Average availability", String.format(FLOAT_MASK, availabilityAcum.getMean()));
			csvWriter.writeLine("Average utility", String.format(FLOAT_MASK, utilityAcum.getMean()));

			csvWriter.writeLine("MSE", String.format(FLOAT_MASK, mse));
			csvWriter.writeLine("RMSE", String.format(FLOAT_MASK, rmse));

			csvWriter.writeLine("Future Estimate For Fixed Bid Average MSE",
					String.format(FLOAT_MASK, futureMSEForFixedBid));
			csvWriter.writeLine("Future Estimate For Variable Bid Average MSE",
					String.format(FLOAT_MASK, futureMSEForVariableBid));

			csvWriter.writeLine("Total speedup (ms)", Long.toString(endTimeMilis - startTimeMilis));
			csvWriter.writeLine("End time", OUT_DF.format(new Date(endTimeMilis)));

		} catch (Exception ex) {
			logger.error("Error writing data to file " + reportFileName);
			throw ex;

		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
	}

	public long getStartTimeMilis() {
		return startTimeMilis;
	}

	public long getEndTimeMilis() {
		return endTimeMilis;
	}

	public double getMSE() {
		return mse;
	}

	public double getRMSE() {
		return rmse;
	}

	public double getFutureMSEForFixedBid() {
		return futureMSEForFixedBid;
	}

	public double getFutureMSEForVariableBid() {
		return futureMSEForVariableBid;
	}
}
