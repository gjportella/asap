package br.unb.cic.boot;

import java.util.Locale;

import br.unb.cic.regression.ondemand.ODAnalysisBuilder;

/**
 * On demand statistical analysis for WSCAD 2016.
 * 
 * @author gjportella
 *
 */
public class BootOnDemandAnalysis {

	public static void main(String[] args) throws Exception {

		Locale.setDefault(Locale.US);
		ODAnalysisBuilder builder;
		
		builder = new ODAnalysisBuilder("Amazon", "mlr",
				"src/main/resources/on_demand/01-mlr-amazon-leite2015.txt");
		builder.doAnalysis();
		
		builder = new ODAnalysisBuilder("Google", "mlr",
				"src/main/resources/on_demand/02-mlr-google-leite2015.txt");
		builder.doAnalysis();
		
		builder = new ODAnalysisBuilder("Amazon", "mlr-spt",
				"src/main/resources/on_demand/03-mlr-spt-amazon-leite2015.txt");
		builder.doAnalysis();
		
		builder = new ODAnalysisBuilder("Amazon", "lr-linpack",
				"src/main/resources/on_demand/04-lr-linpack-amazon-ostermann2009.txt");
		builder.doAnalysis();
		
		builder = new ODAnalysisBuilder("Amazon", "mlr-ecu-restricted",
				"src/main/resources/on_demand/05-mlr-ecu-restricted-amazon2016.txt");
		builder.doAnalysis();
		
		builder = new ODAnalysisBuilder("Amazon", "mlr-strict-no-core",
				"src/main/resources/on_demand/06-mlr-strict-no-core-amazon2016.txt");
		builder.doAnalysis();
	}
}
