package br.unb.cic.conversion.csv;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CsvWrapperReader extends CsvWrapper implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String filePath;
	private String delimiter;
	private BufferedReader input;

	public CsvWrapperReader(String filePath, String delimiter) {
		this.filePath = filePath;
		this.delimiter = delimiter;
	}

	public void openFile() throws Exception {
		input = CsvUtil.createInput(filePath);
	}

	public String[] readLine() throws Exception {
		
		String line = input.readLine();
		if (line != null) {
			
			List<String> aux = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(line, delimiter);
			while (st.hasMoreTokens()) {
				aux.add(st.nextToken());
			}
			return aux.toArray(new String[0]);
		}
		return null;
	}

	public void closeFile() throws Exception {
		if (input != null) {
			input.close();
		}
	}
}
