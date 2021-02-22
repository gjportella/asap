package br.unb.cic.laico.conversion.csv;

public abstract class CsvWrapper {

	abstract void openFile() throws Exception;

	abstract void closeFile() throws Exception;
}
