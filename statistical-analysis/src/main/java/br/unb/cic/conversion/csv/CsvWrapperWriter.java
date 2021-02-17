package br.unb.cic.conversion.csv;

import java.io.BufferedWriter;
import java.io.Serializable;

public class CsvWrapperWriter extends CsvWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	private String filePath;
	private String delimiter;
	private BufferedWriter output;
	
	public CsvWrapperWriter(String filePath, String delimiter) {
		this.filePath = filePath;
		this.delimiter = delimiter;
	}
	
	public void openFile() throws Exception {
		output = CsvUtil.createOutput(filePath);
	}

	public void writeLine(String token1) throws Exception {
		output.write(token1 + "\n");
	}
	
	public void writeLine(String token1, String token2) throws Exception {
		output.write(token1 + delimiter + token2 + "\n");
	}
	
	public void writeLine(String token1, String token2, String token3) throws Exception {
		output.write(token1 + delimiter + token2 + delimiter + token3 + "\n");
	}
	
	public void writeLine(String token1, String token2, String token3, String token4) throws Exception {
		output.write(token1 + delimiter + token2 + delimiter + token3 + delimiter + token4 + "\n");
	}
	
	public void writeLine(String token1, String token2, String token3, String token4, String token5) throws Exception {
		output.write(token1 + delimiter + token2 + delimiter + token3 + delimiter + token4 + delimiter + token5 + "\n");
	}
	
	public void writeLine(String token1, String token2, String token3, String token4, String token5, String token6) throws Exception {
		output.write(token1 + delimiter + token2 + delimiter + token3 + delimiter + token4 + delimiter + token5 + delimiter + token6 + "\n");
	}

	public void writeLine(String[] tokens) throws Exception {
		
		for (int i=0; i<tokens.length; i++) {
			output.write(tokens[i]);
			if (i<tokens.length-1) {
				output.write(delimiter);
			} else {
				output.write("\n");
			}
		}
	}
	
	public void closeFile() throws Exception {
		if (output != null) {
			output.close();
		}
	}
}
