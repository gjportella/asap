package br.unb.cic.laico.conversion.csv;

import java.io.BufferedWriter;
import java.io.Serializable;

public class CsvWrapperWriter extends CsvWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_DELIMITER = ",";

	private String filePath;
	private String delimiter;
	private BufferedWriter output;

	public CsvWrapperWriter(String filePath) {
		this(filePath, DEFAULT_DELIMITER);
	}

	public CsvWrapperWriter(String filePath, String delimiter) {
		this.filePath = filePath;
		this.delimiter = delimiter;
	}

	public void openFile() throws Exception {
		output = CsvUtil.createOutput(filePath);
	}

	public void writeLine(String token1) throws Exception {
		output.write(token1 + "\n");
		output.flush();
	}

	public void writeLine(String token1, String token2) throws Exception {
		output.write(token1 + delimiter + token2 + "\n");
		output.flush();
	}

	public void write(String content) throws Exception {
		output.write(content);
		output.flush();
	}

	public void writeLine(String[] tokens) throws Exception {

		for (int i = 0; i < tokens.length; i++) {
			output.write(tokens[i]);
			if (i < tokens.length - 1) {
				output.write(delimiter);
			} else {
				output.write("\n");
			}
		}
		output.flush();
	}

	public void closeFile() throws Exception {
		if (output != null) {
			output.close();
		}
	}
}
