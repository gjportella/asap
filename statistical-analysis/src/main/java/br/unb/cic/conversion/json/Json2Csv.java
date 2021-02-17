package br.unb.cic.conversion.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import br.unb.cic.conversion.csv.CsvUtil;

public class Json2Csv {

	public void convert(String jsonPath, String csvPath) throws Exception {
		
		BufferedReader input = null;
		BufferedWriter output = null;
		try {
			input = CsvUtil.createInput(jsonPath);
			output = CsvUtil.createOutput(csvPath);
			
			String ts = null;
			String pd = null;
			String it = null;
			String sp = null;
			String az = null;
			
			String line = null;
			while ((line = input.readLine()) != null) {
				line = line.trim();

				if (line.startsWith("\"Timestamp\"")) {
					ts = parseValue(line);

				} else if (line.startsWith("\"ProductDescription\"")) {
					pd = parseValue(line);

				} else if (line.startsWith("\"InstanceType\"")) {
					it = parseValue(line);

				} else if (line.startsWith("\"SpotPrice\"")) {
					sp = parseValue(line);

				} else if (line.startsWith("\"AvailabilityZone\"")) {
					az = parseValue(line);

				} else if (ts != null && pd != null && it != null
						&& sp != null && az != null) {
					
					output.write("SPOTPRICEHISTORY"
							+ "\t" + az
							+ "\t" + it
							+ "\t" + pd
							+ "\t" + sp
							+ "\t" + ts
							+ "\n");
					ts = null;
					pd = null;
					it = null;
					sp = null;
					az = null;
				}
			}
			
		} finally {
			if (input != null) {
				input.close();
			}
			
			if (output != null) {
				output.close();
			}
		}
	}
	
	private String parseValue(String str) {
		String value = str.substring(str.indexOf(":")+2, str.length());
		if (value.startsWith("\"")) {
			value = value.substring(1);
		}
		if (value.endsWith("\"")) {
			value = value.substring(0, value.length()-1);
		} else if (value.endsWith("\",")) {
			value = value.substring(0, value.length()-2);
		}
		return value;
	}
}
