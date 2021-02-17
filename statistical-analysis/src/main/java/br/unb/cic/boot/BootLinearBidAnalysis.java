package br.unb.cic.boot;

import java.util.Locale;

import br.unb.cic.analysis.linearbid.LinearBidAnalysisBuilder;

public class BootLinearBidAnalysis {

	public static void main(String[] args) throws Exception {

		Locale.setDefault(Locale.US);
		String regionFilter = "us-east-1c";
		
		LinearBidAnalysisBuilder builder;
		builder = new LinearBidAnalysisBuilder("c3.8xlarge", regionFilter, "src/main/resources/spot/c3.8xlarge/");
		builder.addCsvLearningPath("src/main/resources/spot/c3.8xlarge/c3.8xlarge.2016-09.txt-scatterplot.txt");
		builder.addCsvPredictionPath("src/main/resources/spot/c3.8xlarge/c3.8xlarge.2016-10.txt-scatterplot.txt");
		builder.doPrediction();
	}

}
