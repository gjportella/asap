package br.unb.cic.regression.ondemand.scatterplot;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ODScatterPlotHelper {

	private String provider;
	private String experiment;
	private String inputCsvPath;
	private List<Double> prices;
	private OLSMultipleLinearRegression regression;
	
	public ODScatterPlotHelper(String provider, String experiment, String inputCsvPath, List<Double> prices,
			OLSMultipleLinearRegression regression) {
		this.provider = provider;
		this.experiment = experiment;
		this.inputCsvPath = inputCsvPath;
		this.prices = prices;
		this.regression = regression;
	}

	public void writeQuantilGraphic() throws Exception {
		
		double[] residuals = regression.estimateResiduals();
		int n = residuals.length;
		
		List<Double> residualList = new ArrayList<Double>(n);
		for (double residual: residuals) {
			residualList.add(Double.valueOf(residual));
		}
		Collections.sort(residualList);
		
		List<Double> normalList = new ArrayList<Double>(n);
		for (int i=0; i<n; i++) {
			double q = ((i + 1d) - 0.5d) / ((double) n);
			double normal = 4.9d * (Math.pow(q, 0.14d) - Math.pow((1d - q), 0.14d));
			normalList.add(Double.valueOf(normal));
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Residual");
		for (int i=0; i<n; i++) {
			series1.add(normalList.get(i).doubleValue(),
					residualList.get(i).doubleValue());
		}
		dataset.addSeries(series1);
		
		String title = "Normal x Residual Quantile (" + experiment + " at " + provider + ")";
		JFreeChart chart = ChartFactory.createScatterPlot(
		        title, "Normal Quantile", "Residual Quantile", dataset);
		
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(new Color(228,228,228));
		
		String pngPath = inputCsvPath + "-quantile.png";
		ChartUtils.saveChartAsPNG(new File(pngPath), chart, 800, 600);
	}
	
	public void writeHomoscedasticityGraphic() throws Exception {

		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Error");
		double[] residuals = regression.estimateResiduals();
		for (int i=0; i<residuals.length; i++) {
			
			double residual = residuals[i];
			double predicted = prices.get(i).doubleValue() - residual;
			series1.add(predicted, residual);
		}
		dataset.addSeries(series1);
		
		String title = "Homoscedasticity (" + experiment + " at " + provider + ")";
		JFreeChart chart = ChartFactory.createScatterPlot(
		        title, "Prediction", "Error", dataset);
		
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(new Color(228,228,228));
		
		String pngPath = inputCsvPath + "-homoscedasticity.png";
		ChartUtils.saveChartAsPNG(new File(pngPath), chart, 800, 600);
	}
}
