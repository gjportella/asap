package br.unb.cic.boot;

import java.util.Locale;

import br.unb.cic.regression.spot.SPAnalysisBuilder;

public class BootSpotAnalysis {

	public static void main(String[] args) throws Exception {

		Locale.setDefault(Locale.US);
		
		String regionFilter = "us-east-1c";
		SPAnalysisBuilder builder;

		//c3.8xlarge
		builder = new SPAnalysisBuilder("src/main/resources/spot/c3.8xlarge/c3.8xlarge.2016-09.txt", regionFilter);
		builder.doAnalysis();

		builder = new SPAnalysisBuilder("src/main/resources/spot/c3.8xlarge/c3.8xlarge.2016-10.txt", regionFilter);
		builder.doAnalysis();

		builder = new SPAnalysisBuilder("src/main/resources/spot/c3.8xlarge/c3.8xlarge.2016-11.txt", regionFilter);
		builder.doAnalysis();

		builder = new SPAnalysisBuilder("src/main/resources/spot/c3.8xlarge/c3.8xlarge.2016-12.txt", regionFilter);
		builder.doAnalysis();
		
		builder = new SPAnalysisBuilder("src/main/resources/spot/c3.8xlarge/c3.8xlarge.2016-append-09_10_11.txt", regionFilter);
		builder.doAnalysis();

		//g2.2xlarge
		builder = new SPAnalysisBuilder("src/main/resources/spot/g2.2xlarge/g2.2xlarge.2016-09.txt", regionFilter);
		builder.doAnalysis();
		
		builder = new SPAnalysisBuilder("src/main/resources/spot/g2.2xlarge/g2.2xlarge.2016-10.txt", regionFilter);
		builder.doAnalysis();

		builder = new SPAnalysisBuilder("src/main/resources/spot/g2.2xlarge/g2.2xlarge.2016-11.txt", regionFilter);
		builder.doAnalysis();

		builder = new SPAnalysisBuilder("src/main/resources/spot/g2.2xlarge/g2.2xlarge.2016-12.txt", regionFilter);
		builder.doAnalysis();
		
		builder = new SPAnalysisBuilder("src/main/resources/spot/g2.2xlarge/g2.2xlarge.2016-append-09_10_11.txt", regionFilter);
		builder.doAnalysis();

		//m4.10xlarge
		builder = new SPAnalysisBuilder("src/main/resources/spot/m4.10xlarge/m4.10xlarge.2016-09.txt", regionFilter);
		builder.doAnalysis();
		
		builder = new SPAnalysisBuilder("src/main/resources/spot/m4.10xlarge/m4.10xlarge.2016-10.txt", regionFilter);
		builder.doAnalysis();

		builder = new SPAnalysisBuilder("src/main/resources/spot/m4.10xlarge/m4.10xlarge.2016-11.txt", regionFilter);
		builder.doAnalysis();

		builder = new SPAnalysisBuilder("src/main/resources/spot/m4.10xlarge/m4.10xlarge.2016-12.txt", regionFilter);
		builder.doAnalysis();
		
		builder = new SPAnalysisBuilder("src/main/resources/spot/m4.10xlarge/m4.10xlarge.2016-append-09_10_11.txt", regionFilter);
		builder.doAnalysis();

		//r3.2xlarge
		builder = new SPAnalysisBuilder("src/main/resources/spot/r3.2xlarge/r3.2xlarge.2016-09.txt", regionFilter);
		builder.doAnalysis();

		builder = new SPAnalysisBuilder("src/main/resources/spot/r3.2xlarge/r3.2xlarge.2016-10.txt", regionFilter);
		builder.doAnalysis();

		builder = new SPAnalysisBuilder("src/main/resources/spot/r3.2xlarge/r3.2xlarge.2016-11.txt", regionFilter);
		builder.doAnalysis();

		builder = new SPAnalysisBuilder("src/main/resources/spot/r3.2xlarge/r3.2xlarge.2016-12.txt", regionFilter);
		builder.doAnalysis();

		builder = new SPAnalysisBuilder("src/main/resources/spot/r3.2xlarge/r3.2xlarge.2016-append-09_10_11.txt", regionFilter);
		builder.doAnalysis();

		//t1.micro
		builder = new SPAnalysisBuilder("src/main/resources/spot/t1.micro/t1.micro.2016-09.txt", regionFilter);
		builder.doAnalysis();

		builder = new SPAnalysisBuilder("src/main/resources/spot/t1.micro/t1.micro.2016-10.txt", regionFilter);
		builder.doAnalysis();

		builder = new SPAnalysisBuilder("src/main/resources/spot/t1.micro/t1.micro.2016-11.txt", regionFilter);
		builder.doAnalysis();

		builder = new SPAnalysisBuilder("src/main/resources/spot/t1.micro/t1.micro.2016-12.txt", regionFilter);
		builder.doAnalysis();
		
		builder = new SPAnalysisBuilder("src/main/resources/spot/t1.micro/t1.micro.2016-append-09_10_11.txt", regionFilter);
		builder.doAnalysis();
	}
}
