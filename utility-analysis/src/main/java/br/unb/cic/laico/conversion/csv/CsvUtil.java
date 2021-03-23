package br.unb.cic.laico.conversion.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public final class CsvUtil {

	public static final String CHARSET_NAME_ISO_8859_1 = "ISO-8859-1";
	public static final String CHARSET_NAME_UTF_8 = "UTF-8";

	public static final BufferedReader createInput(String inputFilePath) throws Exception {
		return createInput(inputFilePath, CHARSET_NAME_UTF_8);
	}

	public static final BufferedReader createInput(String inputFilePath, String charsetName) throws Exception {

		BufferedReader input = new BufferedReader(
				new InputStreamReader(new FileInputStream(inputFilePath), charsetName));
		return input;
	}

	public static final BufferedWriter createOutput(String outputFilePath) throws Exception {
		return createOutput(outputFilePath, CHARSET_NAME_UTF_8);
	}

	public static final BufferedWriter createOutput(String outputFilePath, String charsetName) throws Exception {

		BufferedWriter output = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(outputFilePath), charsetName));
		return output;
	}

}
