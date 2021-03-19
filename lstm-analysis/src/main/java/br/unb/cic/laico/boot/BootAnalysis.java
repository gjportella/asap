package br.unb.cic.laico.boot;

/**
 * End-point interface to boot an analysis.
 * @author gjportella
 *
 */
public interface BootAnalysis {

	/**
	 * Initial method for the analysis.
	 * @throws Exception
	 */
	void runAnalysis() throws Exception;
}
