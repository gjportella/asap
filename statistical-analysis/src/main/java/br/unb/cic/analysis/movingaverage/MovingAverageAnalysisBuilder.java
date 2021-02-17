package br.unb.cic.analysis.movingaverage;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import br.unb.cic.analysis.movingaverage.model.AveragePoint;
import br.unb.cic.conversion.csv.CsvWrapperReader;
import br.unb.cic.conversion.csv.CsvWrapperWriter;

public class MovingAverageAnalysisBuilder implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String INPUT_CSV_DELIMITER = "\t";
	private static final String OUTPUT_CSV_DELIMITER = ";";
	
	private static final double QUATILE = 1.96d;
	
	private static final NumberFormat inNF = NumberFormat.getInstance(Locale.US);
	private static final DateFormat outDF = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
	private static final DateFormat outDFDateOnly = new SimpleDateFormat("dd/MM/yyyy");
	
	private static Logger logger = Logger.getLogger(MovingAverageAnalysisBuilder.class);
	
	private String instanceType;
	private String inputCsvPath;
	private long timeWindow;

	private List<AveragePoint> averagePointList;
	
	public MovingAverageAnalysisBuilder(String instanceType, String inputCsvPath, long timeWindow) {
		this.instanceType = instanceType;
		this.inputCsvPath = inputCsvPath;
		this.timeWindow = timeWindow;
	}

	public void doAnalysis() throws Exception {
		
		logger.debug("(begin) " + outDF.format(new Date()));
		step01_ReadData();
		step02_CalculateMovingAverage();
		step04_WriteObservedToCsv();
		step05_WriteEstimatedToCsv();
		step06_WriteUpperConfidenceToCsv();
		step07_WriteLowerConfidenceToCsv();
		step09_WriteGnuPlotScript();
		logger.debug("(end) " + outDF.format(new Date()));
	}
	
	public void step01_ReadData() throws Exception {
		
		List<AveragePoint> auxList = new LinkedList<AveragePoint>();
		CsvWrapperReader csvReader = null;
		try {
			
			csvReader = new CsvWrapperReader(inputCsvPath, INPUT_CSV_DELIMITER);
			csvReader.openFile();
			
			String[] auxObj;
			while ((auxObj = csvReader.readLine()) != null) {

				AveragePoint point = new AveragePoint(
						inNF.parse(auxObj[1]).longValue(),
						inNF.parse(auxObj[2]).doubleValue());
				auxList.add(point);
			}
			
		} catch (IOException ioEx) {
			logger.error("Error reading file " + inputCsvPath, ioEx);
			
		} finally {
			if (csvReader != null) {
				csvReader.closeFile();
			}
		}
		
		averagePointList = new ArrayList<AveragePoint>(auxList);
		Collections.sort(averagePointList);
		
		logger.debug("(Step 01) Learning occurrences read: " + Integer.toString(averagePointList.size()));
	}

	public void step02_CalculateMovingAverage() throws Exception {
		
		for (int i=1; i<averagePointList.size(); i++) {
			
			AveragePoint point_i = averagePointList.get(i);
			double avg = 0d;
			double sum = 0d;
			double stdev = 0d;
			int n = 0;
			
			// calculate the moving average
			for (int j=i; j>=0; j--) {

				AveragePoint point_j = averagePointList.get(j);
				if (point_i.getTime() - point_j.getTime() > timeWindow) {
					break;
				}
				sum += point_j.getPrice();
				n++;
			}
			avg = sum / (double) n;
			point_i.setAvg(avg);
			point_i.setN(n);
			
			// calculate the standard deviation
			sum = 0d;
			n = 0;
			for (int j=i; j>=0; j--) {

				AveragePoint point_j = averagePointList.get(j);
				if (point_i.getTime() - point_j.getTime() > timeWindow) {
					break;
				}
				sum += Math.pow((point_j.getPrice() - avg), 2d);
				n++;
			}
			stdev = Math.pow(sum / ((double) n-1), 0.5d);
			point_i.setStdev(stdev);

			// calculate the 95% confidence interval
			point_i.setLower(avg - (QUATILE * stdev));
			point_i.setUpper(avg + (QUATILE * stdev));
			point_i.setProcessed(true);
		}
		
		logger.debug("(Step 02) Time-smoothed moving averages calculated.");
	}
	
	public void step04_WriteObservedToCsv() throws Exception {

		CsvWrapperWriter csvWriter = new CsvWrapperWriter(
				inputCsvPath + "-gnuplot-observed.txt", OUTPUT_CSV_DELIMITER);
		
		try { 
			csvWriter.openFile();
			for (AveragePoint point: averagePointList) {
				csvWriter.writeLine(outDF.format(point.getDateObject()),
						Double.toString(point.getPrice()));
			}
			
		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
		
		logger.debug("(Step 04) Observed prices written.");
	}
	
	public void step05_WriteEstimatedToCsv() throws Exception {

		CsvWrapperWriter csvWriter = new CsvWrapperWriter(
				inputCsvPath + "-gnuplot-estimated.txt", OUTPUT_CSV_DELIMITER);
		
		try { 
			csvWriter.openFile();
			for (AveragePoint point: averagePointList) {
				if (point.isProcessed()) {
					csvWriter.writeLine(outDF.format(point.getDateObject()),
							Double.toString(point.getAvg()));
				}
			}
			
		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
		
		logger.debug("(Step 05) Estimated (averages) prices written.");
	}

	public void step06_WriteUpperConfidenceToCsv() throws Exception {

		CsvWrapperWriter csvWriter = new CsvWrapperWriter(
				inputCsvPath + "-gnuplot-upper-ci.txt", OUTPUT_CSV_DELIMITER);
		
		try { 
			csvWriter.openFile();
			for (AveragePoint point: averagePointList) {
				if (point.isProcessed()) {
					csvWriter.writeLine(outDF.format(point.getDateObject()),
							Double.toString(point.getUpper()));
				}
			}
			
		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
		
		logger.debug("(Step 06) Upper confidence interval written.");
	}

	public void step07_WriteLowerConfidenceToCsv() throws Exception {

		CsvWrapperWriter csvWriter = new CsvWrapperWriter(
				inputCsvPath + "-gnuplot-lower-ci.txt", OUTPUT_CSV_DELIMITER);
		
		try { 
			csvWriter.openFile();
			for (AveragePoint point: averagePointList) {
				if (point.isProcessed()) {
					csvWriter.writeLine(outDF.format(point.getDateObject()),
							Double.toString(point.getLower()));
				}
			}
			
		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
		
		logger.debug("(Step 06) Lower confidence interval written.");
	}
	
	public void step09_WriteGnuPlotScript() throws Exception {
		
		AveragePoint firstPoint = averagePointList.get(0);
		AveragePoint lastPoint = averagePointList.get(averagePointList.size() - 1);
		
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(
				inputCsvPath + "-gnuplot-script.txt", OUTPUT_CSV_DELIMITER);
		
		try {
			File f = new File(inputCsvPath);
			
			csvWriter.openFile();
			csvWriter.writeLine("reset");
			csvWriter.writeLine("set title \"" + instanceType + "\"");
			csvWriter.writeLine("set xlabel \"Date (dd/mm)\"");
			csvWriter.writeLine("set xdata time");
			csvWriter.writeLine("set timefmt \"%H:%M:%S %d/%m/%Y\"");
			csvWriter.writeLine("set xrange [\"00:00:00 " + outDFDateOnly.format(firstPoint.getDateObject())
					+ "\":\"23:59:59 " + outDFDateOnly.format(lastPoint.getDateObject()) + "\"]");
			csvWriter.writeLine("set format x \"%d/%m\"");
			csvWriter.writeLine("set ylabel \"Price (USD/hour)\"");
			csvWriter.writeLine("set autoscale y");
			csvWriter.writeLine("set ytics");
			csvWriter.writeLine("set key left top box");
			csvWriter.writeLine("set grid");			
			csvWriter.writeLine("set datafile separator \";\"");
			csvWriter.writeLine("set terminal png size 1300,650");
			csvWriter.writeLine("cd '" + f.getAbsolutePath().replace(f.getName(),"") + "'");
			csvWriter.writeLine("set output \"" + f.getName() + "-gnuplot-scatter-plot.png\"");
			csvWriter.writeLine("plot \"" + f.getName() + "-gnuplot-observed.txt\" using 1:2 lt 1 lc rgb \"#C0C0C0\" title 'observed', \\");
			csvWriter.writeLine("     \"" + f.getName() + "-gnuplot-estimated.txt\" using 1:2 with lines lw 2 lt 1 lc rgb \"#000000\" title 'estimated', \\");
			csvWriter.writeLine("     \"" + f.getName() + "-gnuplot-upper-ci.txt\" using 1:2 with lines lw 2 lt 1 lc rgb \"#777777\" title '95% CI', \\");
			csvWriter.writeLine("     \"" + f.getName() + "-gnuplot-lower-ci.txt\" using 1:2 with lines lw 2 lt 1 lc rgb \"#777777\" notitle");
			
		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
		
		logger.debug("(Step 09) GnuPlot script written.");
	}
}
