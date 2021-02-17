package br.unb.cic.regression.spot.scatterplot;

import java.awt.Color;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import br.unb.cic.conversion.csv.CsvWrapperWriter;

public class ScatterPlotHelper {

	private final static DateFormat outDF = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

	private List<ScatterPlotPoint> pointList;
	
	public ScatterPlotHelper(List<Date> dateHistory, List<Double> priceHistory) {
		this.pointList = doNormalization(dateHistory, priceHistory);
	}

	private List<ScatterPlotPoint> doNormalization(List<Date> dateHistory, List<Double> priceHistory) {
		
		double milisOneDay = 24d * 60d * 60d * 1000d;
		Calendar dateOne = Calendar.getInstance();
		dateOne.setTime(dateHistory.get(0));
		dateOne.set(Calendar.DAY_OF_MONTH, 1);
		dateOne.set(Calendar.HOUR, -24);
		dateOne.set(Calendar.AM_PM, Calendar.AM);
		dateOne.set(Calendar.MINUTE, 0);
		dateOne.set(Calendar.SECOND, 0);
		dateOne.set(Calendar.MILLISECOND, 0);
		
		List<ScatterPlotPoint> pointList = new ArrayList<ScatterPlotPoint>(dateHistory.size());
		for (int i=0; i<dateHistory.size(); i++) {
			
			double dateDouble = ((double) (dateHistory.get(i).getTime()
					- dateOne.getTimeInMillis())) / milisOneDay;
			
			ScatterPlotPoint point = new ScatterPlotPoint();
			point.setDateObject(dateHistory.get(i));
			point.setDateLong(dateHistory.get(i).getTime());
			point.setDateDouble(dateDouble);
			point.setPrice(priceHistory.get(i).doubleValue());
			pointList.add(point);
		}
		
		Collections.sort(pointList, new BeanComparator<ScatterPlotPoint>("dateLong"));
		return pointList;
	}
	
	public void writeCsvData(String inputCsvPath, String csvDelimitter) throws Exception {

		CsvWrapperWriter csvWriter = new CsvWrapperWriter(
				inputCsvPath + "-scatterplot.txt", csvDelimitter);
		
		try { 
			csvWriter.openFile();
			for (ScatterPlotPoint point: pointList) {
				csvWriter.writeLine(new String[] {
						outDF.format(point.getDateObject()),
						Long.toString(point.getDateLong()),
						Double.toString(point.getPrice())
						});
			}
			
		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
	}
	
	public void writeGraphic(String inputCsvPath, String instanceType, String region,
			SimpleRegression regression) throws Exception {

		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("SPOT History Data");
		for (ScatterPlotPoint point: pointList) {
			series1.add(point.getDateLong(), point.getPrice());
		} 
		dataset.addSeries(series1);
		
		if (regression != null) {
			XYSeries series2 = new XYSeries("Linear Regression");
			for (ScatterPlotPoint point: pointList) {
				point.setPredictedPrice(regression.predict(point.getDateObject().getTime()));
				series2.add(point.getDateLong(), point.getPredictedPrice());
			}
		}
		
		ScatterPlotPoint first = pointList.get(0);
		ScatterPlotPoint last = pointList.get(pointList.size()-1);
		String title = "Instance " + instanceType
				+ " from " + outDF.format(first.getDateObject())
				+ " to " + outDF.format(last.getDateObject())
				+ " on " + region;
		
		JFreeChart chart = ChartFactory.createScatterPlot(
		        title, "Date", "Price", dataset);
		
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(new Color(228,228,228));
		
		String pngPath = inputCsvPath + "-scatterplot.png";
		ChartUtils.saveChartAsPNG(new File(pngPath), chart, 800, 600);
	}
	
	public void writeGraphicHomoscedasticity(String inputCsvPath, String instanceType,
			SimpleRegression regression) throws Exception {

		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Error");
		for (ScatterPlotPoint point: pointList) {
			
			double error = point.getPrice() - point.getPredictedPrice();
			series1.add(point.getPredictedPrice(), error);
		} 
		dataset.addSeries(series1);
		
		String title = "Homoscedasticity (" + instanceType + ")";
		JFreeChart chart = ChartFactory.createScatterPlot(
		        title, "Prediction", "Error", dataset);
		
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(new Color(228,228,228));
		
		String pngPath = inputCsvPath + "-homoscedasticity.png";
		ChartUtils.saveChartAsPNG(new File(pngPath), chart, 800, 600);
	}
	
	public void writeFrequencyDistributionGraphic(String inputCsvPath, String instanceType,
			String region) throws Exception {
		
		Map<Double, Integer> map = new HashMap<Double, Integer>();
		for (ScatterPlotPoint point: pointList) {
			
			Double price = Double.valueOf(point.getPrice());
			Integer freq = map.get(price);
			if (freq == null) {
				freq = Integer.valueOf(0);
			}
			map.put(price, Integer.valueOf(freq.intValue() + 1));
		}
		List<Double> list = new ArrayList<Double>(map.keySet());
		Collections.sort(list);

		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Frequency distribution");
		for (Double price: list) {
			Integer freq = map.get(price);
			series1.add(price.doubleValue(), freq.intValue());
		}
		dataset.addSeries(series1);
		
		ScatterPlotPoint first = pointList.get(0);
		ScatterPlotPoint last = pointList.get(pointList.size()-1);
		String title = "Instance " + instanceType
				+ " from " + outDF.format(first.getDateObject())
				+ " to " + outDF.format(last.getDateObject())
				+ " on " + region;
		
		JFreeChart chart = ChartFactory.createScatterPlot(
		        title, "Price", "Frequency", dataset);
		
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(new Color(228,228,228));
		
		String pngPath = inputCsvPath + "-frequency-distribution.png";
		ChartUtils.saveChartAsPNG(new File(pngPath), chart, 800, 600);
	}
}
