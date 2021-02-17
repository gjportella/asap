package br.unb.cic.boot;

import java.util.Locale;

import br.unb.cic.regression.spot.SPPredictionBuilder;

public class BootSpotPrediction {

	public static void main(String[] args) throws Exception {
		
		Locale.setDefault(Locale.US);
		
		String regionFilter = "us-east-1c";
		SPPredictionBuilder builder;
		
		builder = new SPPredictionBuilder("c3.8xlarge", regionFilter, "src/main/resources/spot/c3.8xlarge/");
		builder.addCsvLearningPath("src/main/resources/spot/c3.8xlarge/c3.8xlarge.2016-append-09_10_11.txt-scatterplot.txt");
		builder.addCsvPredictionPath("src/main/resources/spot/c3.8xlarge/c3.8xlarge.2016-12.txt-scatterplot.txt");
		builder.doPrediction();

		builder = new SPPredictionBuilder("g2.2xlarge", regionFilter, "src/main/resources/spot/g2.2xlarge/");
		builder.addCsvLearningPath("src/main/resources/spot/g2.2xlarge/g2.2xlarge.2016-append-09_10_11.txt-scatterplot.txt");
		builder.addCsvPredictionPath("src/main/resources/spot/g2.2xlarge/g2.2xlarge.2016-12.txt-scatterplot.txt");
		builder.doPrediction();

		builder = new SPPredictionBuilder("m4.10xlarge", regionFilter, "src/main/resources/spot/m4.10xlarge/");
		builder.addCsvLearningPath("src/main/resources/spot/m4.10xlarge/m4.10xlarge.2016-append-09_10_11.txt-scatterplot.txt");
		builder.addCsvPredictionPath("src/main/resources/spot/m4.10xlarge/m4.10xlarge.2016-12.txt-scatterplot.txt");
		builder.doPrediction();

		builder = new SPPredictionBuilder("r3.2xlarge", regionFilter, "src/main/resources/spot/r3.2xlarge/");
		builder.addCsvLearningPath("src/main/resources/spot/r3.2xlarge/r3.2xlarge.2016-append-09_10_11.txt-scatterplot.txt");
		builder.addCsvPredictionPath("src/main/resources/spot/r3.2xlarge/r3.2xlarge.2016-12.txt-scatterplot.txt");
		builder.doPrediction();

		builder = new SPPredictionBuilder("t1.micro", regionFilter, "src/main/resources/spot/t1.micro/");
		builder.addCsvLearningPath("src/main/resources/spot/t1.micro/t1.micro.2016-append-09_10_11.txt-scatterplot.txt");
		builder.addCsvPredictionPath("src/main/resources/spot/t1.micro/t1.micro.2016-12.txt-scatterplot.txt");
		builder.doPrediction();
	}
}
