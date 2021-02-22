package br.unb.cic.laico.script;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.unb.cic.laico.conversion.csv.CsvWrapperWriter;

public class WriteEc2dsphAllRegionsBatScript {

	private static final int MONTH = 8;
	private static final int YEAR = 2019;

	private static final String PRODUCT_DESCRIPTION = "Linux/UNIX";
	
	private static final String[] INSTANCE_TYPES = new String[] {
		"c4.8xlarge", "c5d.9xlarge", "g3s.xlarge", "h1.4xlarge", "i3.4xlarge",
		"m4.16xlarge", "p2.xlarge", "r4.16xlarge", "t2.micro", "x1.16xlarge"
	};
	
	private static final String[] ALL_REGIONS = new String[] {
		"us-east-1", "us-east-2", "us-west-1", "us-west-2",
		"ca-central-1", "eu-central-1", "eu-west-1", "eu-west-2",
		"eu-west-3", "eu-north-1", "ap-east-1", "ap-northeast-1",
		"ap-northeast-2", "ap-northeast-3", "ap-southeast-1", "ap-southeast-2",
		"ap-south-1", "me-south-1", "sa-east-1"
	};
	
	private static final String DESTINATION_PATH = "c:/Temp/";
	private static final String SCRIPT_FILE_NAME = "ec2dsph_generated.bat";
	
	private static void writeScript() throws Exception {
		
		DateFormat inDF = new SimpleDateFormat("yyyy-MM-dd");
		Date date = inDF.parse(Integer.toString(YEAR) + "-"
				+ Integer.toString(MONTH) + "-01");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		String startTime = Integer.toString(YEAR) + "-"
				+ Integer.toString(MONTH) + "-01T00:00:00";
		String endTime = Integer.toString(YEAR) + "-"
				+ Integer.toString(MONTH) + "-"
				+ Integer.toString(lastDayOfMonth) + "T23:59:59";
		
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(
				DESTINATION_PATH + SCRIPT_FILE_NAME);
		
		try {
			csvWriter.openFile();
			csvWriter.writeLine("@echo off");
			csvWriter.writeLine("echo running ec2dsph...");
			
			for (String instanceType: INSTANCE_TYPES) {
				
				for (String region: ALL_REGIONS) {
					
					csvWriter.writeLine("echo " + instanceType + " " + region);
					
					String fileName = DESTINATION_PATH.replaceAll("/", "\\\\")
							+ Integer.toString(YEAR) + "-" + Integer.toString(MONTH) + "_"
							+ instanceType + "_" + region + ".txt";
					
					StringBuilder data = new StringBuilder();
					data.append("aws ec2 describe-spot-price-history");
					data.append(" --instance-types " + instanceType);
					data.append(" --product-description " + PRODUCT_DESCRIPTION);
					data.append(" --start-time " + startTime);
					data.append(" --end-time " + endTime);
					data.append(" --region " + region);
					data.append(" >> " + fileName);
					
					csvWriter.writeLine(data.toString());
				}
			}
			csvWriter.writeLine("echo done.");
			
		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
	}
	
	public static void main(String[] args) {
		
		try {
			writeScript();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
