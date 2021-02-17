package br.unb.cic.boot;

import java.util.Locale;

import br.unb.cic.analysis.movingaverage.MovingAverageAnalysisBuilder;

/**
 * Time-smoothed moving averages analysis for CCPE 2017.
 * 
 * @author gjportella
 *
 */
public class BootCCPEAnalysis {

	public static void main(String[] args) throws Exception {

		Locale.setDefault(Locale.US);
		long timeWindow = 12L * 60L * 60L * 1000L;

		MovingAverageAnalysisBuilder builder = new MovingAverageAnalysisBuilder(
				"c3.8xlarge", "src/main/resources/spot/c3.8xlarge/c3.8xlarge.2016-append-09_10_11.txt-scatterplot.txt", timeWindow);
		builder.doAnalysis();
		
		builder = new MovingAverageAnalysisBuilder(
				"g2.2xlarge", "src/main/resources/spot/g2.2xlarge/g2.2xlarge.2016-append-09_10_11.txt-scatterplot.txt", timeWindow);
		builder.doAnalysis();
		
		builder = new MovingAverageAnalysisBuilder(
				"m4.10xlarge", "src/main/resources/spot/m4.10xlarge/m4.10xlarge.2016-append-09_10_11.txt-scatterplot.txt", timeWindow);
		builder.doAnalysis();
		
		builder = new MovingAverageAnalysisBuilder(
				"r3.2xlarge", "src/main/resources/spot/r3.2xlarge/r3.2xlarge.2016-append-09_10_11.txt-scatterplot.txt", timeWindow);
		builder.doAnalysis();
		
		builder = new MovingAverageAnalysisBuilder(
				"t1.micro", "src/main/resources/spot/t1.micro/t1.micro.2016-append-09_10_11.txt-scatterplot.txt", timeWindow);
		builder.doAnalysis();
	}
}
