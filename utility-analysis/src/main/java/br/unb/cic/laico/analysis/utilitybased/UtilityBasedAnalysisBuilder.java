package br.unb.cic.laico.analysis.utilitybased;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import br.unb.cic.laico.analysis.utilitybased.model.AnalysisOutput;
import br.unb.cic.laico.analysis.utilitybased.model.FutureEstimate;
import br.unb.cic.laico.analysis.utilitybased.model.OnDemandPriceList;
import br.unb.cic.laico.analysis.utilitybased.model.RegressionTrend;
import br.unb.cic.laico.analysis.utilitybased.model.SpotHistoryPoint;
import br.unb.cic.laico.analysis.utilitybased.model.UtilityPoint;
import br.unb.cic.laico.conversion.csv.CsvWrapperReader;
import br.unb.cic.laico.conversion.csv.CsvWrapperWriter;
import br.unb.cic.laico.regression.trend.ExponentialTrendLine;
import br.unb.cic.laico.regression.trend.LogarithmTrendLine;
import br.unb.cic.laico.regression.trend.PolynomialTrendLine;
import br.unb.cic.laico.regression.trend.PowerTrendLine;
import br.unb.cic.laico.regression.trend.base.TrendLine;
import br.unb.cic.laico.statistics.Accumulator;
import br.unb.cic.laico.statistics.StatisticsHelper;

public class UtilityBasedAnalysisBuilder implements UtilityBasedAnalysis, Serializable {

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(UtilityBasedAnalysisBuilder.class);
	
	// Delimitadores dos arquivos CSV de entrada e saida
	private static final String INPUT_CSV_DELIMITER = "\t";
	private static final String OUTPUT_CSV_DELIMITER = ";";

	// Formatacao de numeros e datas
	private static final NumberFormat inNF = NumberFormat.getInstance(Locale.US);
	private static final DateFormat inDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static final DateFormat outDF = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
	private static final DateFormat outDFDateOnly = new SimpleDateFormat("dd/MM/yyyy");
	private static final DateFormat outDFPrefixFileName = new SimpleDateFormat("yyyyMMdd-HHmmss");

	// Mascara de formatacao de Fload/Double para String
	private static final String FLOAT_MASK = "%.8f";
	
	// Tamanho da janela em milissegundos (IEEECloud = 12h, IEEETCC = 12h)
	private static final long WINDOW_SIZE = 12 * 60 * 60 * 1000;
	
	// Tamanho da janela de uma semana em milissegundos
	private static final long WEEK_WINDOW_SIZE = 7 * 24 * 60 * 60 * 1000;
	
	// Tamanho da janela de uma hora em milissegundos
	private static final long HOUR_WINDOW_SIZE = 60 * 60 * 1000;
	
	// Tamanho da janela de tempo para analises futuras em milissegundos (IEEECloud = 12h, IEEETCC = 12h)
	private static final long FUTURE_WINDOW_SIZE = 12 * 60 * 60 * 1000;
	
	// Valor default da precisao da estimativa de lance do usuario (sigma)
	public static final double DEFAULT_ESTIMATION_ACCURACY = 0.001;

	// Valor default do fator multiplicador do limite inferior do preco on demand (eta)
	public static final double DEFAULT_OD_PRICE_FACTOR_LOWER_LIMIT = 0;
	
	// Valor default do fator multiplicador do limite superior do preco on demand (theta)
	public static final double DEFAULT_OD_PRICE_FACTOR_UPPER_LIMIT = 1;
	
	// Numero minimo de pontos na janela
	private static final int MINIMAL_NUMBER_OF_POINTS_IN_WINDOW = 3;
	
	// Configuracoes da funcao de utilidade
	public static final byte UTILITY_SETTING_BID = -1;
	public static final byte UTILITY_SETTING_EQUIVALENT = 0;
	public static final byte UTILITY_SETTING_AVAILABILITY = 1;
	
	// Tempo (em milissegundos) do inicio do processamento
	private long startTimeMilis;

	// Tempo (em milissegundos) do termino do processamento
	private long endTimeMilis;
	
	// Tipo da instancia a ser analisada (parametro do algoritmo)
	private String instanceType;
	
	// Nome da zona de disponibilidade para filtragem dos dados de entrada
	private String availabilityZoneFilter;
	
	// Precisao da estimativa de lance do usuario (parametro sigma do algoritmo)
	private double estimationAccuracy;

	// Preco on demand da instancia
	private double odPrice;

	// Fator multiplicador do limite inferior do preco on demand (parametro eta do algoritmo)
	private double odPriceFactorLowerLimit;

	// Fator multiplicador do limite superior do preco on demand (parametro theta do algoritmo)
	private double odPriceFactorUpperLimit;

	// Configuracao do calculo da funcao de utilidade, privilegiando mais o lance ou
	// a utilidade (parametro do algoritmo)
	private byte utilitySetting;
	
	// Caminho do arquivo de entrada (parametro do algoritmo)
	private String inputCsvPath;

	// Nomes dos arquivos de entrada (parametro do algoritmo)
	private String[] inputCsvFileNames;
	
	// Flag para indicar a execucao da analise por dia da semana (true/false)
	private boolean runDayOfWeekAnalysis;
	
	// Flag para indicar a execucao da analise futura (true/false)
	private boolean runFutureAvailability;
	
	// Nome do label que identifique alguma caracteristica especifica da analise
	private String analysisLabel;
	
	// Lista de datas do historico de variacoes spot (lida do arquivo de entrada)
	private List<Date> dateHistory;
	
	// Lista de precos do historico de variacoes spot (lida do arquivo de entrada)
	private List<Double> priceHistory;
	
	// Lista do historico de variacoes de precos spot (apos remocao de outliers)
	private List<SpotHistoryPoint> spotHistoryPointList;

	// Objeto com metodos estatisticos utilizados para remocao de outliers
	private StatisticsHelper statisticsHelper;
	
	// Quantidade de outliers removidos do historico de precos spot
	private int outliers;
	
	// Quantidade de execucoes do algoritmo que avalia a funcao de utilidade
	private long utilityFunctionRuns;

	// Tempo de execucao total do algoritmo que avalia a funcao de utilidade
	private long utilityFunctionTotalSpeedup;
	
	// Objeto de saida da analise para facilitar a producao de relatorios e comparacao dos resultados
	private AnalysisOutput analysisOutput;
	
	/**
	 * 
	 * @param instanceType
	 * @param inputCsvPath
	 * @param inputCsvFileNames
	 */
	public UtilityBasedAnalysisBuilder(String instanceType,
			String inputCsvPath, String[] inputCsvFileNames) {
		this(instanceType,
				null,
				null,
				inputCsvPath,
				inputCsvFileNames);
	}

	/**
	 * 
	 * @param instanceType
	 * @param availabilityZoneFilter
	 * @param analysisLabel
	 * @param inputCsvPath
	 * @param inputCsvFileNames
	 */
	public UtilityBasedAnalysisBuilder(String instanceType, String availabilityZoneFilter,
			String analysisLabel, String inputCsvPath, String[] inputCsvFileNames) {
		this(instanceType,
				availabilityZoneFilter,
				analysisLabel,
				DEFAULT_OD_PRICE_FACTOR_LOWER_LIMIT,
				DEFAULT_OD_PRICE_FACTOR_UPPER_LIMIT,
				DEFAULT_ESTIMATION_ACCURACY,
				UTILITY_SETTING_AVAILABILITY,
				inputCsvPath,
				inputCsvFileNames,
				false,
				false);
	}

	/**
	 * 
	 * @param instanceType
	 * @param availabilityZoneFilter
	 * @param analysisLabel
	 * @param odPriceFactorLowerLimit
	 * @param odPriceFactorUpperLimit
	 * @param estimationAccuracy
	 * @param utilitySetting
	 * @param inputCsvPath
	 * @param inputCsvFileNames
	 * @param runDayOfWeekAnalysis
	 * @param runFutureAvailability
	 */
	public UtilityBasedAnalysisBuilder(String instanceType,
			String availabilityZoneFilter, String analysisLabel,
			double odPriceFactorLowerLimit, double odPriceFactorUpperLimit,
			double estimationAccuracy, byte utilitySetting,
			String inputCsvPath, String[] inputCsvFileNames,
			boolean runDayOfWeekAnalysis, boolean runFutureAvailability) {

		this.instanceType = instanceType;
		this.availabilityZoneFilter = availabilityZoneFilter;
		this.analysisLabel = analysisLabel;
		this.odPriceFactorLowerLimit = odPriceFactorLowerLimit;
		this.odPriceFactorUpperLimit = odPriceFactorUpperLimit;
		this.estimationAccuracy = estimationAccuracy;
		this.utilitySetting = utilitySetting;
		this.inputCsvPath = inputCsvPath;
		this.inputCsvFileNames = inputCsvFileNames;
		this.runDayOfWeekAnalysis = runDayOfWeekAnalysis;
		this.runFutureAvailability = runFutureAvailability;
		this.utilityFunctionRuns = 0L;
		this.utilityFunctionTotalSpeedup = 0L;
		this.statisticsHelper = new StatisticsHelper();
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void doAnalysis() throws Exception {
		
		logger.debug("Instance type: " + instanceType);
		logger.debug("Estimation accuracy (sigma): " + Double.toString(estimationAccuracy));
		logger.debug("On demand Lower Limit Price Factor (eta): " + Double.toString(odPriceFactorLowerLimit));
		logger.debug("On demand Upper Limit Price Factor (theta): " + Double.toString(odPriceFactorUpperLimit));
		logger.debug("Availability zone filter" + (availabilityZoneFilter!=null ? availabilityZoneFilter : ""));
		logger.debug("Analysis label" + analysisLabel!=null?analysisLabel:"");
		
		this.startTimeMilis = System.currentTimeMillis();
		step01_ReadODPrice();
		step02_ReadData();
		step03_RemoveOutliers();
		step04_CalculateOptimalUtilities();
		if (runDayOfWeekAnalysis) {
			step05_CalculateOptimalUtilitiesByDayOfWeek();
		}
		step06_CalculateNormalizedUtilities();
		if (runFutureAvailability) {
			step07_CalculateFutureAvailability();
		}
		step08_CalculateRegressionTrends();
		step09_WriteGnuPlotDataToCsv();
		step09_WriteRDataToCsv();
		step10_WriteBidAvailabilityGnuPlotScript(false);
		step11_WriteUtilityGnuPlotScript(false);
		if (runFutureAvailability) {
			step12_WriteFutureWindowGnuPlotScript();
		}
		step13_WriteDataForSuggestionTabble();
		this.endTimeMilis = System.currentTimeMillis();
		
		step14_WriteFinalReport();
	}
	
	public void step01_ReadODPrice() throws Exception {
		
		// Obtencao do preco on-demand para o tipo de instancia informado
		odPrice = 0D;
		try {
			OnDemandPriceList odplInstance = OnDemandPriceList.getInstance();
			odPrice = odplInstance.getPrice(instanceType);

		} catch (Exception ex) {
			throw new Exception("Invalid instance type.", ex);
		}
		
		logger.debug("(Step 01) On-demand price for " + instanceType
				+ ": " + Double.toString(odPrice));
	}
	
	public void step02_ReadData() throws Exception {
		
		// Listas encadeadas para auxiliar na leitura dos dados historicos
		List<Date> dateListAux = new LinkedList<Date>();
		List<Double> priceListAux = new LinkedList<Double>();
		
		// Leitura dos arquivos CSV
		for (String inputCsvFileName: inputCsvFileNames) {
			
			CsvWrapperReader csvReader = null;
			try {
				
				csvReader = new CsvWrapperReader(inputCsvPath + inputCsvFileName, INPUT_CSV_DELIMITER);
				csvReader.openFile();
				
				String[] auxObj;
				while ((auxObj = csvReader.readLine()) != null) {
					
					if (availabilityZoneFilter != null && !availabilityZoneFilter.equals(auxObj[1])) {
						continue;
					}
	
					dateListAux.add(inDF.parse(auxObj[5]));
					priceListAux.add(Double.valueOf(inNF.parse(auxObj[4]).doubleValue()));
				}
				
			} catch (IOException ioEx) {
				logger.error("Error reading file " + inputCsvPath + inputCsvFileName, ioEx);
				
			} finally {
				if (csvReader != null) {
					csvReader.closeFile();
				}
			}
		}

		// Criacao das listas de historico de datas e precos spot
		dateHistory = new ArrayList<Date>(dateListAux);
		priceHistory = new ArrayList<Double>(priceListAux);
		
		logger.debug("(Step 02) Occurrences read: " + Integer.toString(priceHistory.size()));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void step03_RemoveOutliers() throws Exception {

		List<Date> dateList = new ArrayList<Date>(dateHistory.size());
		List<Double> priceList = new ArrayList<Double>(priceHistory.size());
		
		statisticsHelper.removeOutliers(dateHistory, priceHistory,
				dateList, priceList, 10D);
		outliers = priceHistory.size() - priceList.size();
		
		// Criacao da lista definitiva de historico de variacoes de preco spot a serem analisadas
		spotHistoryPointList = new ArrayList<SpotHistoryPoint>(priceList.size());
		for (int i=0; i<priceList.size(); i++) {
			spotHistoryPointList.add(new SpotHistoryPoint(dateList.get(i), priceList.get(i)));
		}
		Collections.sort(spotHistoryPointList);
		
		logger.debug("(Step 03) Outliers removed: " + Integer.toString(outliers));	
	}
	
	public void step04_CalculateOptimalUtilities() throws Exception {

		// Realiza calculo desconsiderando dia da semana (parametro igual a -1)
		calculateOptimalUtilitiesByDayOfWeek(-1);

		logger.debug("(Step 04) Estimated bids and availabilities for optimal utilities calculated.");
	}
	
	public void step05_CalculateOptimalUtilitiesByDayOfWeek() throws Exception {

		// Realiza calculo das utilidades por dia da semana
		for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; dayOfWeek++) {
			calculateOptimalUtilitiesByDayOfWeek(dayOfWeek);

			logger.debug("(Step 05) Estimated bids and availabilities based on day of week calculated ("
					+ Integer.toString(dayOfWeek) + "/7).");
		}
	}

	public void step06_CalculateNormalizedUtilities() throws Exception {
		
		double lowestUtility = Double.MAX_VALUE;
		double highestUtility = 0D;
		
		// Percorre todos os valores (pontos) da serie historica para
		// determinar a menor e a maior utilidade calculada
		for (SpotHistoryPoint point: spotHistoryPointList) {
		
			// Verifica se foi calculada a utilidade para o ponto
			UtilityPoint utilityPoint = point.getUtilityPoint();
			if (utilityPoint != null) {
				
				// Verifica se eh a menor utilidade
				if (utilityPoint.getUtility() < lowestUtility) {
					lowestUtility = utilityPoint.getUtility();
				}
				
				// Verifica se eh a maior utilidade
				if (utilityPoint.getUtility() > highestUtility) {
					highestUtility = utilityPoint.getUtility();
				}
			}
		}
		
		// Calcula e atualiza a utilidade normalizada em cada ponto
		for (SpotHistoryPoint point: spotHistoryPointList) {
			
			// Verifica se foi calculada a utilidade para o ponto
			UtilityPoint utilityPoint = point.getUtilityPoint();
			if (utilityPoint != null) {
				
				// Calcula a utilidade normalizada
				double normalizedUtility = (utilityPoint.getUtility() - lowestUtility) /
						(highestUtility - lowestUtility);
				
				// Atualiza o valor da utilidade normalizada no ponto
				utilityPoint.setNormalizedUtility(normalizedUtility);
			}
		}
		
		logger.debug("(Step 06) Normalized utilities calculated.");
	}
	
	public void step07_CalculateFutureAvailability() throws Exception {

		// Obtencao do ultimo ponto da serie historica
		SpotHistoryPoint lastPoint = spotHistoryPointList.get(spotHistoryPointList.size() - 1);
		
		// Percorre todos os valores da serie historica
		for (int i=0; i<spotHistoryPointList.size(); i++) {

			// Ponto na serie historica a ser analisado no momento Ti
			SpotHistoryPoint pointTi = spotHistoryPointList.get(i);

			// Verifica se nao foi calculada a utilidade para o ponto Ti (inicio da serie) ou se
			// nao ha janela futura suficiente para fazer a projecao de disponibilidade (final da serie)
			// e continua para o processamento do proximo ponto da serie
			UtilityPoint utilityPoint = pointTi.getUtilityPoint();
			if (utilityPoint == null ||
					lastPoint.getTime() - pointTi.getTime() < FUTURE_WINDOW_SIZE) {
				continue;
			}

			// Tempo total da janela de tempo futura (em ms)
			long totalMilisInWindow = 0L;

			// Estimativas futuras de disponibilidade, lance e preco da instancia, considerando lance fixo,
			// lance totalmente lance variavel e lance variavel apenas de hora em hora
			FutureEstimate futureEstimateForFixedBid = new FutureEstimate(FUTURE_WINDOW_SIZE);
			FutureEstimate futureEstimateForVariableBid = new FutureEstimate(FUTURE_WINDOW_SIZE);
			FutureEstimate futureEstimateForHourlyVariableBid = new FutureEstimate(FUTURE_WINDOW_SIZE);

			// Define o lance na estimativa futura com lance fixo
			futureEstimateForFixedBid.addAcumulatedBidVariation(utilityPoint.getBid());

			// Variaveis de controle de lances variaveis em janelas fixas de uma hora
			int previousHour = -1;
			double hourlyVariableBid = 0D;

			// Indice corrente e proximo indice em relacao a i
			int currIndex = i;
			int nextIndex = i+1;

			// Percorre a janela de tempo no futuro
			boolean stillInsideTheWindow = FUTURE_WINDOW_SIZE < lastPoint.getTime() - pointTi.getTime();
			while (stillInsideTheWindow && nextIndex < spotHistoryPointList.size()) {

				// Ponto corrente e proximo ponto em relacao a i
				SpotHistoryPoint currPoint = spotHistoryPointList.get(currIndex);
				SpotHistoryPoint nextPoint = spotHistoryPointList.get(nextIndex);

				// Tempo decorrido do ponto corrente ate o proximo ponto
				long difTimeMilis = nextPoint.getTime() - currPoint.getTime();

				// Verifica se o tempo decorrido esta dentro da janela
				if (totalMilisInWindow + difTimeMilis < FUTURE_WINDOW_SIZE) {

					// Acumula no tempo total
					totalMilisInWindow += difTimeMilis;

				} else {

					// Ajusta tempo decorrido para ficar dentro da janela
					difTimeMilis = FUTURE_WINDOW_SIZE - totalMilisInWindow;
					totalMilisInWindow = FUTURE_WINDOW_SIZE;
					stillInsideTheWindow = false;
				}

				// Verifica se o preco spot esta abaixo do lance fixo e adiciona tempo de disponibilidade da instancia
				// na estimativa futura de lance fixo
				if (currPoint.getPrice() <= utilityPoint.getBid()) {
					futureEstimateForFixedBid.addAcumulatedAvailableTimeInWindow(difTimeMilis);
				} else {
					
					// Verifica e define tempo ate a primeira indisponibilidade da instancia
					if (futureEstimateForFixedBid.getFirstChunkAvailableTimeInWindow() == 0L) {
						futureEstimateForFixedBid.setFirstChunkAvailableTimeInWindow(
								futureEstimateForFixedBid.getAcumulatedAvailableTimeInWindow());
					}
				}

				// Verifica se o preco spot esta abaixo do lance variavel e adiciona tempo de disponibilidade da instancia
				// na estimativa futura de lance variavel
				if (currPoint.getPrice() <= currPoint.getUtilityPoint().getBid()) {
					futureEstimateForVariableBid.addAcumulatedAvailableTimeInWindow(difTimeMilis);

					// Adiciona valor de lance a estimativa futura de lance variavel
					futureEstimateForVariableBid.addAcumulatedBidVariation(currPoint.getUtilityPoint().getBid());

				} else {
					
					// Verifica e define tempo ate a primeira indisponibilidade da instancia
					if (futureEstimateForVariableBid.getFirstChunkAvailableTimeInWindow() == 0L) {
						futureEstimateForVariableBid.setFirstChunkAvailableTimeInWindow(
								futureEstimateForVariableBid.getAcumulatedAvailableTimeInWindow());
					}
				}

				// Calcula quatidade de horas desde a ultima alteracao de lance e verifica se houve alteracao de hora
				int hoursElapsed = ((int) (difTimeMilis / HOUR_WINDOW_SIZE));
				if (hoursElapsed > previousHour) {

					// Atualiza alteracao de hora
					previousHour = hoursElapsed;

					// Atualiza variacao de lance de hora em hora 
					hourlyVariableBid = currPoint.getUtilityPoint().getBid();

					// Adiciona valor de lance a estimativa futura de lance variavel de hora em hora
					futureEstimateForHourlyVariableBid.addAcumulatedBidVariation(hourlyVariableBid);
				}

				// Verifica se o preco spot esta abaixo do lance variavel de hora em hora e adiciona tempo de disponibilidade
				// da instancia na estimativa futura de lance variavel de hora em hora
				if (currPoint.getPrice() <= hourlyVariableBid) {
					futureEstimateForHourlyVariableBid.addAcumulatedAvailableTimeInWindow(difTimeMilis);

				} else {
					
					// Verifica e define tempo ate a primeira indisponibilidade da instancia
					if (futureEstimateForHourlyVariableBid.getFirstChunkAvailableTimeInWindow() == 0L) {
						futureEstimateForHourlyVariableBid.setFirstChunkAvailableTimeInWindow(
								futureEstimateForHourlyVariableBid.getAcumulatedAvailableTimeInWindow());
					}
				}

				// Atualiza indices para proxima iteração
				currIndex = nextIndex;
				nextIndex++;
			}

			// Atualiza estimativas futuras no objeto utilidade
			futureEstimateForFixedBid.updateEstimatedValues();
			utilityPoint.setFutureEstimateForFixedBid(futureEstimateForFixedBid);

			futureEstimateForVariableBid.updateEstimatedValues();
			utilityPoint.setFutureEstimateForVariableBid(futureEstimateForVariableBid);

			futureEstimateForHourlyVariableBid.updateEstimatedValues();
			utilityPoint.setFutureEstimateForHourlyVariableBid(futureEstimateForHourlyVariableBid);
		}

		logger.debug("(Step 07) Future availabilities calculated.");
	}

	public void step08_CalculateRegressionTrends() throws Exception {

		// Percorre todos os valores da serie historica
		for (int i=0; i<spotHistoryPointList.size()-1; i++) {

			// Ponto atual e proximo ponto na serie historica
			SpotHistoryPoint currentPoint = spotHistoryPointList.get(i);
			SpotHistoryPoint nextPoint = spotHistoryPointList.get(i+1);

			// Verifica se nao foi calculada a utilidade para o ponto Ti e
			// continua para o processamento do proximo ponto da serie
			UtilityPoint utilityPoint = currentPoint.getUtilityPoint();
			if (utilityPoint == null) {
				continue;
			}

			// Obtem janela extendida considerando dia da semana especifico
			List<SpotHistoryPoint> extendedWindow = getExtendedWindowByDayOfWeek(-1, i);
			int numberOfPointsInWindow = extendedWindow.size();
			if (numberOfPointsInWindow > MINIMAL_NUMBER_OF_POINTS_IN_WINDOW) {
			
				// Obtem os dados dos eixos x (timestamps) e y (precos)
				double[] xSamples = new double[numberOfPointsInWindow];
				double[] ySamples = new double[numberOfPointsInWindow];
				for (int j=0; j<numberOfPointsInWindow; j++) {
	
					SpotHistoryPoint windowPoint = extendedWindow.get(j);
					xSamples[j] = (double) windowPoint.getTime();
					ySamples[j] = (double) windowPoint.getPrice();
				}
	
				// Cria regressoes para cada tipo usando valores de timestamps e precos
				utilityPoint.setExponentialTrend(buildRegressionTrend(RegressionTrend.EXPONENTIAL_TREND, xSamples, ySamples,
						currentPoint.getTime(), currentPoint.getPrice(), nextPoint.getTime()));
				utilityPoint.setLogarithmTrend(buildRegressionTrend(RegressionTrend.LOGARITHM_TREND, xSamples, ySamples,
						currentPoint.getTime(), currentPoint.getPrice(), nextPoint.getTime()));
				utilityPoint.setPolynomialTrend(buildRegressionTrend(RegressionTrend.POLYNOMIAL_TREND, xSamples, ySamples,
						currentPoint.getTime(), currentPoint.getPrice(), nextPoint.getTime()));
				utilityPoint.setPowerTrend(buildRegressionTrend(RegressionTrend.POWER_TREND, xSamples, ySamples,
						currentPoint.getTime(), currentPoint.getPrice(), nextPoint.getTime()));
			}
		}

		logger.debug("(Step 08) Regression trends calculated.");
	}	
	
	public void step09_WriteGnuPlotDataToCsv() throws Exception {

		// Nomes dos dias da semana
		String[] namesOfWeekdays = DateFormatSymbols.getInstance().getWeekdays();
		
		String outputFileName = getOutputFileName(true, "gnuplot-estimated-data.txt");
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(outputFileName, OUTPUT_CSV_DELIMITER);

		try {
			csvWriter.openFile();
			for (SpotHistoryPoint point: spotHistoryPointList) {
				if (point.getUtilityPoint() != null) {

					// Objeto para auxiliar na escrita dos dados
					StringBuilder data = new StringBuilder();
					
					// Posicao 1: timestamp
					data.append(outDF.format(point.getDateObject()));
					data.append(OUTPUT_CSV_DELIMITER);

					// Posicao 2: nome do dia da semana referente ao timestamp
					data.append(point.getDayOfWeekName());
					data.append(OUTPUT_CSV_DELIMITER);
					
					// Posicao 3: valor spot observado
					data.append(Double.toString(point.getPrice()));
					data.append(OUTPUT_CSV_DELIMITER);
					
					// Posicao 4: lance estimado
					data.append(Double.toString(point.getUtilityPoint().getBid()));
					data.append(OUTPUT_CSV_DELIMITER);
	
					// Posicao 5: disponibilidade estimada
					data.append(Double.toString(point.getUtilityPoint().getAvailability()));
					data.append(OUTPUT_CSV_DELIMITER);

					// Posicao 6: utilidade calculada
					data.append(Double.toString(point.getUtilityPoint().getUtility()));
					data.append(OUTPUT_CSV_DELIMITER);
					
					// Posicao 7: utilidade calculada
					data.append(Double.toString(point.getUtilityPoint().getNormalizedUtility()));
					data.append(OUTPUT_CSV_DELIMITER);
					
					if (point.getUtilityPoint().getFutureEstimateForFixedBid() != null) {
					
						// Posicao 8: disponibilidade por lance fixo na janela futura
						data.append(Double.toString(point.getUtilityPoint().getFutureEstimateForFixedBid().getFutureAvailability()));
						data.append(OUTPUT_CSV_DELIMITER);
						
						// Posicao 9: lance medio por lance fixo na janela futura
						data.append(Double.toString(point.getUtilityPoint().getFutureEstimateForFixedBid().getAverageBid()));
						data.append(OUTPUT_CSV_DELIMITER);
						
						// Posicao 10: primeiro periodo de tempo (em h) por lance fixo na janela futura
						data.append(Double.toString(point.getUtilityPoint().getFutureEstimateForFixedBid().getFirstChunkAvailableTimeInWindow()
								/ (double) HOUR_WINDOW_SIZE));
						data.append(OUTPUT_CSV_DELIMITER);
						
						// Posicao 11: disponibilidade do primeiro periodo de tempo por lance fixo na janela futura
						data.append(Double.toString(point.getUtilityPoint().getFutureEstimateForFixedBid().getFirstChunkAvailabilityInWindow()));
						data.append(OUTPUT_CSV_DELIMITER);

					} else {
						data.append(OUTPUT_CSV_DELIMITER)
							.append(OUTPUT_CSV_DELIMITER)
							.append(OUTPUT_CSV_DELIMITER)
							.append(OUTPUT_CSV_DELIMITER);
					}
					
					if (point.getUtilityPoint().getFutureEstimateForVariableBid() != null) {
						
						// Posicao 12: disponibilidade por lance variavel na janela futura
						data.append(Double.toString(point.getUtilityPoint().getFutureEstimateForVariableBid().getFutureAvailability()));
						data.append(OUTPUT_CSV_DELIMITER);
						
						// Posicao 13: lance medio por lance variavel na janela futura
						data.append(Double.toString(point.getUtilityPoint().getFutureEstimateForVariableBid().getAverageBid()));
						data.append(OUTPUT_CSV_DELIMITER);
						
						// Posicao 14: primeiro periodo de tempo (em h) por lance variavel na janela futura
						data.append(Double.toString(point.getUtilityPoint().getFutureEstimateForVariableBid().getFirstChunkAvailableTimeInWindow()
								/ (double) HOUR_WINDOW_SIZE));
						data.append(OUTPUT_CSV_DELIMITER);
						
						// Posicao 15: disponibilidade do primeiro periodo de tempo por lance variavel na janela futura
						data.append(Double.toString(point.getUtilityPoint().getFutureEstimateForVariableBid().getFirstChunkAvailabilityInWindow()));
						data.append(OUTPUT_CSV_DELIMITER);
					
					} else {
						data.append(OUTPUT_CSV_DELIMITER)
							.append(OUTPUT_CSV_DELIMITER)
							.append(OUTPUT_CSV_DELIMITER)
							.append(OUTPUT_CSV_DELIMITER);
					}

					if (point.getUtilityPoint().getFutureEstimateForHourlyVariableBid() != null) {
						
						// Posicao 16: disponibilidade por lance variavel de hora em hora na janela futura
						data.append(Double.toString(point.getUtilityPoint().getFutureEstimateForHourlyVariableBid().getFutureAvailability()));
						data.append(OUTPUT_CSV_DELIMITER);
						
						// Posicao 17: lance medio por lance variavel de hora em hora na janela futura
						data.append(Double.toString(point.getUtilityPoint().getFutureEstimateForHourlyVariableBid().getAverageBid()));
						data.append(OUTPUT_CSV_DELIMITER);
						
						// Posicao 18: primeiro periodo de tempo (em h) por lance variavel de hora em hora na janela futura
						data.append(Double.toString(point.getUtilityPoint().getFutureEstimateForHourlyVariableBid().getFirstChunkAvailableTimeInWindow()
								/ (double) HOUR_WINDOW_SIZE));
						data.append(OUTPUT_CSV_DELIMITER);
						
						// Posicao 19: disponibilidade do primeiro periodo de tempo por lance variavel de hora em hora na janela futura
						data.append(Double.toString(point.getUtilityPoint().getFutureEstimateForHourlyVariableBid().getFirstChunkAvailabilityInWindow()));
						data.append(OUTPUT_CSV_DELIMITER);
						
					} else {
						data.append(OUTPUT_CSV_DELIMITER)
							.append(OUTPUT_CSV_DELIMITER)
							.append(OUTPUT_CSV_DELIMITER)
							.append(OUTPUT_CSV_DELIMITER);
					}
					
					// Posicoes relativas aos dias da semana (19+(dayOfWeek-1)*4)
					for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; dayOfWeek++) {
						
						UtilityPoint utilityPoint = point.getUtilityByDayOfWeek(dayOfWeek);
						if (utilityPoint != null) {
	
							// Posicao referente ao nome do dia da semana
							data.append(namesOfWeekdays[dayOfWeek]);
							data.append(OUTPUT_CSV_DELIMITER);
							
							// Posicao referente ao lance estimado considerando dia da semana
							data.append(Double.toString(utilityPoint.getBid()));
							data.append(OUTPUT_CSV_DELIMITER);
							
							// Posicao referente a disponibilidade estimada considerando dia da semana
							data.append(Double.toString(utilityPoint.getAvailability()));
							data.append(OUTPUT_CSV_DELIMITER);
							
							// Posicao referente a utilidade calculada considerando dia da semana
							data.append(Double.toString(utilityPoint.getUtility()));
							data.append(OUTPUT_CSV_DELIMITER);
						}
					}
					
					// Posicao referente a sugestao (-1 = now ou Calendar.MONDAY, etc.)
					data.append(point.getUtilityBasedSuggestion());
					
					// Escrita dos dados
					csvWriter.writeLine(data.toString());
				}
			}
			
		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
		
		logger.debug("(Step 09) Estimated GnuPlot output data written.");
	}
	
	public void step09_WriteRDataToCsv() throws Exception {

		String outputFileName = getOutputFileName(true, "r-data.txt");
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(outputFileName, OUTPUT_CSV_DELIMITER);

		try {
			csvWriter.openFile();
			for (SpotHistoryPoint point: spotHistoryPointList) {
				
				// Objeto para auxiliar na escrita dos dados
				StringBuilder data = new StringBuilder();

				// Posicao 0: timestamp
				data.append(outDF.format(point.getDateObject()));
				data.append(OUTPUT_CSV_DELIMITER);

				// Posicao 1: valor spot observado
				data.append(Double.toString(point.getPrice()));
				data.append(OUTPUT_CSV_DELIMITER);
				
				if (point.getUtilityPoint() != null) {

					// Posicao 2: lance estimado
					data.append(Double.toString(point.getUtilityPoint().getBid()));
					data.append(OUTPUT_CSV_DELIMITER);

					// Posicao 3: disponibilidade estimada
					data.append(Double.toString(point.getUtilityPoint().getAvailability()));
					data.append(OUTPUT_CSV_DELIMITER);

					// Posicao 4: utilidade calculada
					data.append(Double.toString(point.getUtilityPoint().getUtility()));
					data.append(OUTPUT_CSV_DELIMITER);

					if (point.getUtilityPoint().getFutureEstimateForFixedBid() != null) {

						// Posicao 5: lance medio por lance fixo na janela futura
						data.append(Double.toString(point.getUtilityPoint().getFutureEstimateForFixedBid().getAverageBid()));
						data.append(OUTPUT_CSV_DELIMITER);

						// Posicao 6: primeiro periodo de tempo (em h) por lance fixo na janela futura
						data.append(Double.toString(point.getUtilityPoint().getFutureEstimateForFixedBid().getFirstChunkAvailableTimeInWindow()
								/ (double) HOUR_WINDOW_SIZE));
						data.append(OUTPUT_CSV_DELIMITER);

					} else {
						data.append(OUTPUT_CSV_DELIMITER)
							.append(OUTPUT_CSV_DELIMITER);
					}

					if (point.getUtilityPoint().getFutureEstimateForHourlyVariableBid() != null) {

						// Posicao 7: lance medio por lance variavel de hora em hora na janela futura
						data.append(Double.toString(point.getUtilityPoint().getFutureEstimateForHourlyVariableBid().getAverageBid()));
						data.append(OUTPUT_CSV_DELIMITER);

						// Posicao 8: primeiro periodo de tempo (em ms) por lance variavel de hora em hora na janela futura
						data.append(Double.toString(point.getUtilityPoint().getFutureEstimateForHourlyVariableBid().getFirstChunkAvailableTimeInWindow()
								/ (double) HOUR_WINDOW_SIZE));

					} else {
						data.append(OUTPUT_CSV_DELIMITER);
					}

				} else {
					data.append(OUTPUT_CSV_DELIMITER)
						.append(OUTPUT_CSV_DELIMITER)
						.append(OUTPUT_CSV_DELIMITER)
						.append(OUTPUT_CSV_DELIMITER)
						.append(OUTPUT_CSV_DELIMITER)
						.append(OUTPUT_CSV_DELIMITER);
				}

				csvWriter.writeLine(data.toString());
			}
			
		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
		
		logger.debug("(Step 09) Estimated R output data written.");
	}
	
	public void step10_WriteBidAvailabilityGnuPlotScript(boolean showNormalizedUtility) throws Exception {

		// Obtencao do primeiro e ultimo pontos da serie historica
		SpotHistoryPoint firstPoint = spotHistoryPointList.get(0);
		SpotHistoryPoint lastPoint = spotHistoryPointList.get(spotHistoryPointList.size() - 1);
		
		// Filename do arquivo PNG do grafico de saida
		String pngFileName;
		if (showNormalizedUtility) {
			pngFileName = getOutputFileName(false, "gnuplot-bid-availability-utility-plot.png");
		} else {
			pngFileName = getOutputFileName(false, "gnuplot-bid-availability-plot.png");
		}
		
		// Filename do arquivo CSV com os dados
		String csvDataFileName = getOutputFileName(false, "gnuplot-estimated-data.txt");
		
		// Filename do arquivo de script de saida
		String outputFileName;
		if (showNormalizedUtility) {
			outputFileName = getOutputFileName(true, "gnuplot-bid-availability-utility-script.txt");
		} else {
			outputFileName = getOutputFileName(true, "gnuplot-bid-availability-script.txt");
		}
		
		// Abertura do arquivo
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(outputFileName, OUTPUT_CSV_DELIMITER);

		try {
			File f = new File(inputCsvPath);
			csvWriter.openFile();
			
			csvWriter.writeLine("reset");
			csvWriter.writeLine("set title \"" + instanceType + "\"");
			csvWriter.writeLine("set xlabel \"Date (dd/mm)\"");
			csvWriter.writeLine("set xdata time");
			csvWriter.writeLine("set timefmt \"%H:%M:%S %d/%m/%Y\"");
			csvWriter.writeLine("set xrange [\"00:00:00 " + outDFDateOnly.format(firstPoint.getDateObject())
					+ "\":\"23:59:59 " + outDFDateOnly.format(lastPoint.getDateObject()) + "\"]");
			csvWriter.writeLine("set format x \"%d/%m\"");
			csvWriter.writeLine("set ylabel \"Price (USD/hour)\"");
			csvWriter.writeLine("set autoscale y");
			csvWriter.writeLine("set ytics");

			if (showNormalizedUtility) {
				csvWriter.writeLine("set y2label \"Availability and Utility (%)\"");
			} else {
				csvWriter.writeLine("set y2label \"Availability (%)\"");
			}
			
			csvWriter.writeLine("set y2range [0.0:1.0]");
			csvWriter.writeLine("set y2tics");
			csvWriter.writeLine("set key left top box");
			csvWriter.writeLine("set grid");
			csvWriter.writeLine("set datafile separator \";\"");
			csvWriter.writeLine("set terminal png size 1920,1080");
			csvWriter.writeLine("cd '" + f.getAbsolutePath() + "'");
			csvWriter.writeLine("set output \"" + pngFileName + "\"");
			csvWriter.writeLine("plot \"" + csvDataFileName + "\" using 1:3 axes x1y1 lt 1 lc rgb \"#C0C0C0\" title 'Observed', \\");
			csvWriter.writeLine("     " + Double.toString(odPrice) + " axes x1y1 lt 1 lc rgb \"#000000\" title 'On demand', \\");
			csvWriter.writeLine("     \"" + csvDataFileName + "\" using 1:4 axes x1y1 with lines lw 1 lt 1 lc rgb \"#0000FF\" title 'Bid', \\");
			
			if (showNormalizedUtility) {
				csvWriter.writeLine("     \"" + csvDataFileName + "\" using 1:5 axes x1y2 with lines lw 1 lt 1 lc rgb \"#FF0000\" title 'Availability', \\");
				csvWriter.writeLine("     \"" + csvDataFileName + "\" using 1:7 axes x1y2 with lines lw 1 lt 1 lc rgb \"#00FF00\" title 'Normalized Utility'");
			} else {
				csvWriter.writeLine("     \"" + csvDataFileName + "\" using 1:5 axes x1y2 with lines lw 1 lt 1 lc rgb \"#FF0000\" title 'Availability'");	
			}

		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}

		if (showNormalizedUtility) {
			logger.debug("(Step 10) Bid, availability and utility GnuPlot script written.");
		} else {
			logger.debug("(Step 10) Bid and availability GnuPlot script written.");
		}
	}
	
	public void step11_WriteUtilityGnuPlotScript(boolean showNormalizedUtility) throws Exception {

		// Obtencao do primeiro e ultimo pontos da serie historica
		SpotHistoryPoint firstPoint = spotHistoryPointList.get(0);
		SpotHistoryPoint lastPoint = spotHistoryPointList.get(spotHistoryPointList.size() - 1);
		
		// Filename do arquivo PNG do grafico de saida
		String pngFileName;
		if (showNormalizedUtility) {
			pngFileName = getOutputFileName(false, "gnuplot-utility-normalized-plot.png");
		} else {
			pngFileName = getOutputFileName(false, "gnuplot-utility-plot.png");
		}
		
		// Filename do arquivo CSV com os dados
		String csvDataFileName = getOutputFileName(false, "gnuplot-estimated-data.txt");
		
		// Filename do arquivo de script de saida
		String outputFileName;
		if (showNormalizedUtility) {
			outputFileName = getOutputFileName(true, "gnuplot-utility-normalized-script.txt");
		} else {
			outputFileName = getOutputFileName(true, "gnuplot-utility-script.txt");
		}
		
		// Abertura do arquivo
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(outputFileName, OUTPUT_CSV_DELIMITER);

		try {
			File f = new File(inputCsvPath);
			csvWriter.openFile();
			
			csvWriter.writeLine("reset");
			csvWriter.writeLine("set title \"" + instanceType + "\"");
			csvWriter.writeLine("set xlabel \"Date (dd/mm)\"");
			csvWriter.writeLine("set xdata time");
			csvWriter.writeLine("set timefmt \"%H:%M:%S %d/%m/%Y\"");
			csvWriter.writeLine("set xrange [\"00:00:00 " + outDFDateOnly.format(firstPoint.getDateObject())
					+ "\":\"23:59:59 " + outDFDateOnly.format(lastPoint.getDateObject()) + "\"]");
			csvWriter.writeLine("set format x \"%d/%m\"");
			csvWriter.writeLine("set ylabel \"Utility\"");
			csvWriter.writeLine("set autoscale y");
			csvWriter.writeLine("set ytics");

			if (showNormalizedUtility) {
				csvWriter.writeLine("set y2label \"Normalized Utility\"");
				csvWriter.writeLine("set y2range [0.0:1.0]");
				csvWriter.writeLine("set y2tics");
			}
			
			csvWriter.writeLine("set key left bottom box");
			csvWriter.writeLine("set grid");
			csvWriter.writeLine("set datafile separator \";\"");
			csvWriter.writeLine("set terminal png size 1920,1080");
			csvWriter.writeLine("cd '" + f.getAbsolutePath() + "'");
			csvWriter.writeLine("set output \"" + pngFileName + "\"");
			
			if (showNormalizedUtility) {
				csvWriter.writeLine("plot \"" + csvDataFileName + "\" using 1:6 axes x1y1 with lines lw 1 lt 1 lc rgb \"#008800\" title 'Utility', \\");
				csvWriter.writeLine("     \"" + csvDataFileName + "\" using 1:7 axes x1y2 with lines lw 1 lt 1 lc rgb \"#00FF00\" title 'Normalized Utility'");
			} else {
				csvWriter.writeLine("plot \"" + csvDataFileName + "\" using 1:6 axes x1y1 with lines lw 1 lt 1 lc rgb \"#00FF00\" title 'Utility'");
			}

		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}

		if (showNormalizedUtility) {
			logger.debug("(Step 11) Utility and normalized utility GnuPlot script written.");
		} else {
			logger.debug("(Step 11) Utility GnuPlot script written.");
		}
	}
	
	public void step12_WriteFutureWindowGnuPlotScript() throws Exception {

		// Obtencao do primeiro e ultimo pontos da serie historica
		SpotHistoryPoint firstPoint = spotHistoryPointList.get(0);
		SpotHistoryPoint lastPoint = spotHistoryPointList.get(spotHistoryPointList.size() - 1);

		// Filename do arquivo CSV com os dados
		String csvDataFileName = getOutputFileName(false, "gnuplot-estimated-data.txt");
		
		// Filename do arquivo PNG do grafico de saida
		String pngFileName = getOutputFileName(false, "gnuplot-future-window-plot.png");
		
		String outputFileName = getOutputFileName(true, "gnuplot-future-window-script.txt");
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(outputFileName, OUTPUT_CSV_DELIMITER);

		try {
			File f = new File(inputCsvPath);
			csvWriter.openFile();

			csvWriter.writeLine("reset");
			csvWriter.writeLine("set title \"" + instanceType
					+ " from " + outDFDateOnly.format(firstPoint.getDateObject())
					+ " to " + outDFDateOnly.format(lastPoint.getDateObject()) + "\"");
			csvWriter.writeLine("set xlabel \"X: Date (dd/mm)\\n\"");
			csvWriter.writeLine("set xdata time");
			csvWriter.writeLine("set timefmt \"%H:%M:%S %d/%m/%Y\"");
			csvWriter.writeLine("set xrange [\"00:00:00 " + outDFDateOnly.format(firstPoint.getDateObject())
					+ "\":\"23:59:59 " + outDFDateOnly.format(lastPoint.getDateObject()) + "\"]");
			csvWriter.writeLine("set xtics \"00:00:00 " + outDFDateOnly.format(firstPoint.getDateObject())
					+ "\",864000,\"23:59:59 " + outDFDateOnly.format(lastPoint.getDateObject()) + "\"");
			csvWriter.writeLine("set format x \"%d/%m\"");
			
			csvWriter.writeLine("set ylabel \"Y1: Price (USD/hour)\"");
			csvWriter.writeLine("set autoscale y");
			csvWriter.writeLine("set ytics");

			csvWriter.writeLine("set y2label \"Y2: Time Window (hour)\"");
			csvWriter.writeLine("set y2range [0:12]");
			csvWriter.writeLine("set y2tics");

			csvWriter.writeLine("set key out horiz bottom center box width -5");
			csvWriter.writeLine("set grid");
			csvWriter.writeLine("set datafile separator \";\"");
			
			//csvWriter.writeLine("set terminal png size 1000,500 font \"Default,14\"");
			csvWriter.writeLine("set terminal png size 1920,1080");
			
			csvWriter.writeLine("cd '" + f.getAbsolutePath() + "'");
			csvWriter.writeLine("set output \"" + pngFileName + "\"");
			
			csvWriter.writeLine("plot \"" + csvDataFileName + "\" using 1:3 axes x1y1 lw 2 lt 1 lc rgb \"#909090\" title 'Spot (Y1)', \\");
			csvWriter.writeLine("     " + Double.toString(odPrice) + " axes x1y1 lw 2 lt 1 lc rgb \"#000000\" title 'On demand (Y1)', \\");
			csvWriter.writeLine("     \"" + csvDataFileName + "\" using 1:4 axes x1y1 with lines lw 2 lt 1 lc rgb \"#0000FF\" title 'Bid (Y1)', \\");
			csvWriter.writeLine("     \"" + csvDataFileName + "\" using 1:10 axes x1y2 lw 2 lt 1 lc rgb \"#009900\" title 'Durability (Y2)', \\");
			csvWriter.writeLine("     8.5 axes x1y2 lw 2 lt 1 lc rgb \"#FF7777\" title '5% Percentile (Y2)', \\");
			csvWriter.writeLine("     9.5 axes x1y2 lw 2 lt 1 lc rgb \"#990000\" title '10% Percentile (Y2)'");

		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}

		logger.debug("(Step 12) Future window GnuPlot script written.");
	}
	
	public void step13_WriteDataForSuggestionTabble() throws Exception {
		
		Map<String, Integer> table = new HashMap<String, Integer>();
		for (SpotHistoryPoint point: spotHistoryPointList) {
			if (point.getUtilityPoint() != null) {
				
				String key = point.getDayOfWeekName()
						+ OUTPUT_CSV_DELIMITER
						+ point.getUtilityBasedSuggestion();
				
				Integer value = table.get(key);
				if (value != null) {
					table.put(key, Integer.valueOf(value.intValue() + 1));
				} else {
					table.put(key, Integer.valueOf(1));
				}
			}
		}
		
		String outputFileName = getOutputFileName(true, "suggestion-table.txt");
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(outputFileName, OUTPUT_CSV_DELIMITER);

		try {
			csvWriter.openFile();
			
			Set<String> keySet = table.keySet();
			for (String key: keySet) {
				csvWriter.writeLine(key, Integer.toString(table.get(key).intValue()));
			}

		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}
		
		logger.debug("(Step 13) Data for suggestion table written.");
	}
	
	public void step14_WriteFinalReport() throws Exception {

		// Acumuladores para o calculo do preco e lance medios
		Accumulator priceAcum = new Accumulator();
		Accumulator bidAcum = new Accumulator();
		Accumulator availabilityAcum = new Accumulator();
		Accumulator utilityAcum = new Accumulator();
		Accumulator normalizedUtilityAcum = new Accumulator();
		
		// Acumuladores para a disponibilidade futura com base no lance fixo
		Accumulator futureAvailFixedBidAcum = new Accumulator();
		Accumulator futureFixedBidAcum = new Accumulator();
		Accumulator futureFirstChunkFixedBidAcum = new Accumulator();
		Accumulator futureFirstChunkAvailabilityFixedBidAcum = new Accumulator();
		int fixedBidDurabilityEqWindow = 0;
		
		// Acumuladores para a disponibilidade futura com base no lance variavel
		Accumulator futureAvailVariableBidAcum = new Accumulator();
		Accumulator futureVariableBidAcum = new Accumulator();
		Accumulator futureFirstChunkVariableBidAcum = new Accumulator();
		Accumulator futureFirstChunkAvailabilityVariableBidAcum = new Accumulator();
		int variableBidDurabilityEqWindow = 0;
		
		// Acumuladores para a disponibilidade futura com base no lance variavel de hora em hora
		Accumulator futureAvailHourlyVariableBidAcum = new Accumulator();
		Accumulator futureHourlyVariableBidAcum = new Accumulator();
		Accumulator futureFirstChunkHourlyVariableBidAcum = new Accumulator();
		Accumulator futureFirstChunkAvailabilityHourlyVariableBidAcum = new Accumulator();
		int hourlyBidDurabilityEqWindow = 0;
		
		// Acumuladores para numero de pontos e quantidades de melhores fits por tipo de regressao
		Accumulator numberOfPointsInRegressionsAcum = new Accumulator();
		int[] trendBestFits = new int[] {0, 0, 0, 0};
		
		// Obtencao do primeiro e ultimo pontos da serie historica
		SpotHistoryPoint firstPoint = spotHistoryPointList.get(0);
		SpotHistoryPoint lastPoint = spotHistoryPointList.get(spotHistoryPointList.size() - 1);
		
		// Calculo dos parametros de disponibilidade
		for (SpotHistoryPoint point: spotHistoryPointList) {

			UtilityPoint utilityPoint = point.getUtilityPoint();
			if (utilityPoint != null) {

				priceAcum.addValue(point.getPrice());
				bidAcum.addValue(utilityPoint.getBid());
				availabilityAcum.addValue(utilityPoint.getAvailability());
				utilityAcum.addValue(utilityPoint.getUtility());
				normalizedUtilityAcum.addValue(utilityPoint.getNormalizedUtility());
				
				if (utilityPoint.getFutureEstimateForFixedBid() != null) {
					futureAvailFixedBidAcum.addValue(utilityPoint.getFutureEstimateForFixedBid().getFutureAvailability());
					futureFixedBidAcum.addValue(utilityPoint.getFutureEstimateForFixedBid().getAverageBid());
					futureFirstChunkFixedBidAcum.addValue(utilityPoint.getFutureEstimateForFixedBid().getFirstChunkAvailableTimeInWindow());
					futureFirstChunkAvailabilityFixedBidAcum.addValue(
							utilityPoint.getFutureEstimateForFixedBid().getFirstChunkAvailabilityInWindow());
					if (FUTURE_WINDOW_SIZE == utilityPoint.getFutureEstimateForFixedBid().getFirstChunkAvailableTimeInWindow()) {
						fixedBidDurabilityEqWindow++;
					}
				}

				if (utilityPoint.getFutureEstimateForVariableBid() != null) {
					futureAvailVariableBidAcum.addValue(utilityPoint.getFutureEstimateForVariableBid().getFutureAvailability());
					futureVariableBidAcum.addValue(utilityPoint.getFutureEstimateForVariableBid().getAverageBid());
					futureFirstChunkVariableBidAcum.addValue(utilityPoint.getFutureEstimateForVariableBid().getFirstChunkAvailableTimeInWindow());
					futureFirstChunkAvailabilityVariableBidAcum.addValue(
							utilityPoint.getFutureEstimateForVariableBid().getFirstChunkAvailabilityInWindow());
					if (FUTURE_WINDOW_SIZE == utilityPoint.getFutureEstimateForVariableBid().getFirstChunkAvailableTimeInWindow()) {
						variableBidDurabilityEqWindow++;
					}
				}
				
				if (utilityPoint.getFutureEstimateForHourlyVariableBid() != null) {
					futureAvailHourlyVariableBidAcum.addValue(utilityPoint.getFutureEstimateForHourlyVariableBid().getFutureAvailability());
					futureHourlyVariableBidAcum.addValue(utilityPoint.getFutureEstimateForHourlyVariableBid().getAverageBid());
					futureFirstChunkHourlyVariableBidAcum.addValue(utilityPoint.getFutureEstimateForHourlyVariableBid().getFirstChunkAvailableTimeInWindow());
					futureFirstChunkAvailabilityHourlyVariableBidAcum.addValue(
							utilityPoint.getFutureEstimateForHourlyVariableBid().getFirstChunkAvailabilityInWindow());
					if (FUTURE_WINDOW_SIZE == utilityPoint.getFutureEstimateForHourlyVariableBid().getFirstChunkAvailableTimeInWindow()) {
						hourlyBidDurabilityEqWindow++;
					}
				}
				
				int trendTypeWithBestFit = utilityPoint.getTrendTypeWithBestFit();
				if (trendTypeWithBestFit >= 0 && trendTypeWithBestFit  <= 3) {
					numberOfPointsInRegressionsAcum.addValue(utilityPoint
							.getRegressionTrend(trendTypeWithBestFit)
							.getNumberOfPoints());
					trendBestFits[trendTypeWithBestFit]++;
				}
			}
		}
		
		// Calculo do speedup medio
		double averageSppedup = (((double) endTimeMilis) - ((double) startTimeMilis))
				/ ((double) utilityFunctionRuns);
	
		// Criacao do objeto de output
		analysisOutput = new AnalysisOutput();

		// Abertura do arquivo de relatorio para escrita
		String outputFileName = getOutputFileName(true, "final-report.txt");
		CsvWrapperWriter csvWriter = new CsvWrapperWriter(outputFileName, OUTPUT_CSV_DELIMITER);

		try {
			csvWriter.openFile();
			csvWriter.writeLine("Start time", outDF.format(new Date(startTimeMilis)));
			analysisOutput.setStartTime(startTimeMilis);
			csvWriter.writeLine("Instance type", instanceType);
			analysisOutput.setInstanceType(instanceType);
			csvWriter.writeLine("Availability zone filter", availabilityZoneFilter!=null?availabilityZoneFilter:"");
			analysisOutput.setAvailabilityZoneFilter(availabilityZoneFilter);
			csvWriter.writeLine("Analysis label", analysisLabel!=null?analysisLabel:"");
			analysisOutput.setAnalysisLabel(analysisLabel);
			csvWriter.writeLine("First point", outDF.format(firstPoint.getDateObject()));
			csvWriter.writeLine("Last point", outDF.format(lastPoint.getDateObject()));
			csvWriter.writeLine("Occurrences read", Integer.toString(spotHistoryPointList.size()));
			analysisOutput.setOccurrencesRead(spotHistoryPointList.size());
			csvWriter.writeLine("Outliers removed", Integer.toString(outliers));
			analysisOutput.setOccurrencesRemoved(outliers);
			csvWriter.writeLine("Estimation accuracy (sigma)", Double.toString(estimationAccuracy));
			analysisOutput.setSigma(estimationAccuracy);
			csvWriter.writeLine("Window size (ms)", Long.toString(WINDOW_SIZE));
			csvWriter.writeLine("Future window size (ms)", Long.toString(FUTURE_WINDOW_SIZE));

			csvWriter.writeLine("On demand Lower Limit Price Factor (eta)", Double.toString(odPriceFactorLowerLimit));
			analysisOutput.setTheta(odPriceFactorLowerLimit);
			csvWriter.writeLine("On demand Upper Limit Price Factor (theta)", Double.toString(odPriceFactorUpperLimit));
			csvWriter.writeLine("On demand Price", Double.toString(odPrice));
			analysisOutput.setOnDemandPrice(odPrice);

			csvWriter.writeLine("Average price", String.format(FLOAT_MASK, priceAcum.getMean()));
			analysisOutput.setAveragePrice(priceAcum.getMean());
			csvWriter.writeLine("Price variance", String.format(FLOAT_MASK, priceAcum.getVariance()));
			analysisOutput.setPriceVariance(priceAcum.getVariance());
			csvWriter.writeLine("Price standard deviation", String.format(FLOAT_MASK, priceAcum.getStddev()));
			
			csvWriter.writeLine("Average bid", String.format(FLOAT_MASK, bidAcum.getMean()));
			analysisOutput.setAverageBid(bidAcum.getMean());
			csvWriter.writeLine("Highest bid", String.format(FLOAT_MASK, bidAcum.getMax()));
			csvWriter.writeLine("Last bid", String.format(FLOAT_MASK, lastPoint.getUtilityPoint().getBid()));
			
			csvWriter.writeLine("Average availability", String.format(FLOAT_MASK, availabilityAcum.getMean()));
			analysisOutput.setAverageAvailability(availabilityAcum.getMean());
			csvWriter.writeLine("Average utility", String.format(FLOAT_MASK, utilityAcum.getMean()));
			csvWriter.writeLine("Average normalized utility", String.format(FLOAT_MASK, normalizedUtilityAcum.getMean()));

			csvWriter.writeLine("(Fixed bid in future window) Average availability", String.format(FLOAT_MASK,
					futureAvailFixedBidAcum.getMean()));
			analysisOutput.setFixedBidAverageAvailability(futureAvailFixedBidAcum.getMean());
			csvWriter.writeLine("(Fixed bid in future window) Average bid", String.format(FLOAT_MASK,
					futureFixedBidAcum.getMean()));
			analysisOutput.setFixedBidAverageBid(futureFixedBidAcum.getMean());
			csvWriter.writeLine("(Fixed bid in future window) Average first chunk size (in ms)", String.format(FLOAT_MASK,
					futureFirstChunkFixedBidAcum.getMean()));
			csvWriter.writeLine("(Fixed bid in future window) Average first chunk size (in %)", String.format(FLOAT_MASK,
					futureFirstChunkAvailabilityFixedBidAcum.getMean()));
			analysisOutput.setFixedBidAverageFirstChunk(futureFirstChunkAvailabilityFixedBidAcum.getMean());
			csvWriter.writeLine("(Fixed bid in future window) Ocorrences with durability equal to future window",
					Integer.toString(fixedBidDurabilityEqWindow));
			analysisOutput.setFixedBidDurabilityEqWindow(fixedBidDurabilityEqWindow);

			csvWriter.writeLine("(Hourly variable bid in future window) Average availability", String.format(FLOAT_MASK,
					futureAvailHourlyVariableBidAcum.getMean()));
			analysisOutput.setHourlyBidAverageAvailability(futureAvailHourlyVariableBidAcum.getMean());
			csvWriter.writeLine("(Hourly variable bid in future window) Average bid", String.format(FLOAT_MASK,
					futureHourlyVariableBidAcum.getMean()));
			analysisOutput.setHourlyBidAverageBid(futureHourlyVariableBidAcum.getMean());
			csvWriter.writeLine("(Hourly variable bid in future window) Average first chunk size (in ms)", String.format(FLOAT_MASK,
					futureFirstChunkHourlyVariableBidAcum.getMean()));
			csvWriter.writeLine("(Hourly variable bid in future window) Average first chunk size (in %)", String.format(FLOAT_MASK,
					futureFirstChunkAvailabilityHourlyVariableBidAcum.getMean()));
			analysisOutput.setHourlyBidAverageFirstChunk(futureFirstChunkAvailabilityHourlyVariableBidAcum.getMean());
			csvWriter.writeLine("(Hourly variable bid in future window) Ocorrences with durability equal to future window",
					Integer.toString(hourlyBidDurabilityEqWindow));
			analysisOutput.setHourlyBidDurabilityEqWindow(hourlyBidDurabilityEqWindow);

			csvWriter.writeLine("(Variable bid in future window) Average availability", String.format(FLOAT_MASK,
					futureAvailVariableBidAcum.getMean()));
			analysisOutput.setVariableBidAverageAvailability(futureAvailVariableBidAcum.getMean());
			csvWriter.writeLine("(Variable bid in future window) Average bid", String.format(FLOAT_MASK,
					futureVariableBidAcum.getMean()));
			analysisOutput.setVariableBidAverageBid(futureVariableBidAcum.getMean());
			csvWriter.writeLine("(Variable bid in future window) Average first chunk size (in ms)", String.format(FLOAT_MASK,
					futureFirstChunkVariableBidAcum.getMean()));
			csvWriter.writeLine("(Variable bid in future window) Average first chunk size (in %)", String.format(FLOAT_MASK,
					futureFirstChunkAvailabilityVariableBidAcum.getMean()));
			analysisOutput.setVariableBidAverageFirstChunk(futureFirstChunkAvailabilityVariableBidAcum.getMean());
			csvWriter.writeLine("(Variable bid in future window) Ocorrences with durability equal to future window",
					Integer.toString(variableBidDurabilityEqWindow));
			analysisOutput.setVariableBidDurabilityEqWindow(variableBidDurabilityEqWindow);

			csvWriter.writeLine("Average number of points in regressions", Double.toString(numberOfPointsInRegressionsAcum.getMean()));
			csvWriter.writeLine("Exponential regression best fits", Integer.toString(trendBestFits[RegressionTrend.EXPONENTIAL_TREND]));
			csvWriter.writeLine("Logarithm regression best fits", Integer.toString(trendBestFits[RegressionTrend.LOGARITHM_TREND]));
			csvWriter.writeLine("Polynomial regression best fits", Integer.toString(trendBestFits[RegressionTrend.POLYNOMIAL_TREND]));
			csvWriter.writeLine("Power regression best fits", Integer.toString(trendBestFits[RegressionTrend.POWER_TREND]));

			csvWriter.writeLine("Utility function runs", Long.toString(utilityFunctionRuns));
			csvWriter.writeLine("Utility function average speedup (ms)", Double.toString(averageSppedup));
			analysisOutput.setUtilityFunctionAverageSpeedup(averageSppedup);
			csvWriter.writeLine("Total speedup (ms)", Long.toString(endTimeMilis - startTimeMilis));
			analysisOutput.setTotalSpeedup(endTimeMilis - startTimeMilis);
			csvWriter.writeLine("End time", outDF.format(new Date(endTimeMilis)));

		} finally {
			if (csvWriter != null) {
				csvWriter.closeFile();
			}
		}

		logger.debug("(Step 14) Final report written.");		
	}

	public AnalysisOutput getAnalysisOutput() {
		return analysisOutput;
	}

	/*************************************************************************/
	
	private void calculateOptimalUtilitiesByDayOfWeek(int dayOfWeek) throws Exception {

		// Obtencao do momento inicial na serie historica
		SpotHistoryPoint pointT0 = null;
		try {
			pointT0 = spotHistoryPointList.get(0);
		} catch (Exception ex) {
			throw new Exception("Invalid spot history time serie.", ex);
		}

		// Aplicacao do fator multiplicador no limite superior do preco on demand
		double odPriceLowerLimit = (odPriceFactorLowerLimit * odPrice);
		
		// Aplicacao do fator multiplicador no limite superior do preco on demand
		double odPriceUpperLimit = (odPriceFactorUpperLimit * odPrice);
		
		// Criacao do vetor de lances estimados do usuario com valor inicial igual a zero
		// ate o limite on-demand, com incremento igual a precisao de lance estimada
		List<Double> aux1 = new LinkedList<Double>();
		double estimatedBid = odPriceLowerLimit;
		while (estimatedBid <= odPriceUpperLimit) {
			
			estimatedBid += estimationAccuracy;
			aux1.add(Double.valueOf(estimatedBid));
		}
		List<Double> estimatedBids = new ArrayList<Double>(aux1);

		// Localiza indice na serie historica para inicio da analise (apos 1 semana ou 1 janela)
		int indexToStart = 0;
		for (int i=0; i<spotHistoryPointList.size(); i++) {
			
			indexToStart = i;
			SpotHistoryPoint pointTi = spotHistoryPointList.get(i);
			if (runDayOfWeekAnalysis &&
					pointTi.getTime() - pointT0.getTime() > WEEK_WINDOW_SIZE) {
				break;
			} else if (pointTi.getTime() - pointT0.getTime() > WINDOW_SIZE) {
				break;
			}
		}
		
		// Percorre todos os valores da serie historica a partir da segunda semana
		for (int i=indexToStart; i<spotHistoryPointList.size(); i++) {

			// Ponto na serie historica a ser analisado
			SpotHistoryPoint pointTi = spotHistoryPointList.get(i);
			
			// Obtem janela extendida considerando dia da semana especifico
			List<SpotHistoryPoint> extendedWindow = getExtendedWindowByDayOfWeek(dayOfWeek, i);
			
			// Calcula utilidade para cada lance estimado considerando a janela
			UtilityPoint optimalUtilityPoint = getOptimalUtility(extendedWindow,
					pointTi, odPriceUpperLimit, estimatedBids);

			// Verifica se foi informado um dia da semana valido
			if (dayOfWeek >= Calendar.SUNDAY && dayOfWeek <= Calendar.SATURDAY) {

				// Atribui a utilidade otima ao objeto da serie historica (referente ao dia da semana)
				pointTi.setUtilityByDayOfWeek(dayOfWeek, optimalUtilityPoint);
				
			} else {
				
				// Atribui a utilidade otima ao objeto da serie historica
				pointTi.setUtilityPoint(optimalUtilityPoint);
			}
		}
	}
	
	private List<SpotHistoryPoint> getExtendedWindowByDayOfWeek(int dayOfWeek, int currentIndex) {
		
		// Lista de pontos resultante (implementa lista encadeada para insercao mais rapida)
		List<SpotHistoryPoint> extendedWindow = new LinkedList<SpotHistoryPoint>();

		// Ponto que representa a posiçao atual
		SpotHistoryPoint currentPoint = spotHistoryPointList.get(currentIndex);
		
		// Percorre a lista de forma decrescente partindo da posição atual
		int index = currentIndex;
		while (index > 0) {
			
			SpotHistoryPoint point = spotHistoryPointList.get(index);
			if (currentPoint.getTime() - point.getTime() > WINDOW_SIZE) {
				break;
			}
			extendedWindow.add(point);
			index--;
		}
		
		// Verifica se foi informado um dia da semana valido
		if (dayOfWeek >= Calendar.SUNDAY && dayOfWeek <= Calendar.SATURDAY) {
		
			// Continua percorrimento da lista para buscar pontos no dia da semana informado
			while (index > 0) {
				
				SpotHistoryPoint point = spotHistoryPointList.get(index);
				if (currentPoint.getTime() - point.getTime() > WEEK_WINDOW_SIZE) {
					break;
				}
				
				// Verifica o dia da semana
				if (dayOfWeek == point.getDayOfWeek()) {
					extendedWindow.add(point);
				}
				index--;
			}
		}
		
		// Retorna a lista resultante (implementa array para percorrimento mais rapido)
		extendedWindow = new ArrayList<SpotHistoryPoint>(extendedWindow);
		Collections.sort(extendedWindow);
		return extendedWindow;
	}
	
	private UtilityPoint getOptimalUtility(List<SpotHistoryPoint> timeWindow, SpotHistoryPoint point,
			double odPriceUpperLimit, List<Double> estimatedBids) throws Exception {

		// Timestamp do inicio da execucao
		long startTimeMilisUtilityFunction = System.currentTimeMillis();
		
		// Calcula utilidade para cada lance estimado considerando a janela
		UtilityPoint greatestUtilityPoint = new UtilityPoint();
		for (int k=0; k<estimatedBids.size(); k++) {

			// Contagem de quantas vezes o preco spot esta abaixo do estimado (nBelow)
			int nBelow = 0;
			double estimatedBid = estimatedBids.get(k).doubleValue();
			for (SpotHistoryPoint pointAux: timeWindow) {
				if (pointAux.getPrice() < estimatedBid) {
					nBelow++;
				}
			}
			
			// Calculo da disponibilidade em funcao do lance estimado
			double availability = ((double) nBelow) / ((double) timeWindow.size());
			
			// Calculo da utilidade em funcao do lance estimado e da disponibilidade
			double utility;
			if (utilitySetting == UTILITY_SETTING_BID) {
				utility = Math.pow((odPriceUpperLimit - estimatedBid), 2) * availability;
			} else if (utilitySetting == UTILITY_SETTING_AVAILABILITY) {
				utility = Math.pow((odPriceUpperLimit - estimatedBid), 0.5) * availability;
			} else if (utilitySetting == UTILITY_SETTING_EQUIVALENT) {
				utility = (odPriceUpperLimit - estimatedBid) * availability;
			} else {
				throw new Exception("Invalid Utility Setting.");
			}
			
			// Verifica se foi calculada a maior utilidade
			if (utility > greatestUtilityPoint.getUtility()) {

				// Atualiza objeto com dados da utilidade otima
				greatestUtilityPoint.setBid(estimatedBid);
				greatestUtilityPoint.setAvailability(availability);
				greatestUtilityPoint.setUtility(utility);
			}
		}

		// Incremente o numero de execucoes da funcao de utilidade
		utilityFunctionRuns++;
		
		// Timestamp do termino da execucao
		long endTimeMilisUtilityFunction = System.currentTimeMillis();

		// Adiciona tempo de execucao no tempo total do algoritmo que avalia a funcao de utilidade
		utilityFunctionTotalSpeedup = utilityFunctionTotalSpeedup
				+ (endTimeMilisUtilityFunction - startTimeMilisUtilityFunction);
		
		// Retorna ponto com a utilidade otima
		return greatestUtilityPoint;
	}
	
	private RegressionTrend buildRegressionTrend(int trendType, double[] xSamples, double[] ySamples,
			long currentTime, double currentPrice, long nextTime) {

		try {
			TrendLine trendLine;
			switch (trendType) {
			case RegressionTrend.EXPONENTIAL_TREND:
				trendLine = new ExponentialTrendLine();
				break;
			case RegressionTrend.LOGARITHM_TREND:
				trendLine = new LogarithmTrendLine();
				break;
			case RegressionTrend.POLYNOMIAL_TREND:
				trendLine = new PolynomialTrendLine(2);
				break;
			case RegressionTrend.POWER_TREND:
				trendLine = new PowerTrendLine();
				break;
			default:
				throw new IllegalArgumentException("Invalid regression type.");
			}
			trendLine.setValues(ySamples, xSamples);

			RegressionTrend regressionTrend = new RegressionTrend();
			regressionTrend.setNumberOfPoints(xSamples.length);
			regressionTrend.setCoefficients(trendLine.getCoefs());
			regressionTrend.setRSquared(trendLine.getRSquared());
			regressionTrend.setStdErrOfEstimate(trendLine.getStandardErrorOfEstimate());
			regressionTrend.setCurrentTime(currentTime);
			regressionTrend.setCurrentPrice(currentPrice);
			regressionTrend.setNextTime(nextTime);
			regressionTrend.setPredictedPriceAtNextTime(trendLine.predict((double) nextTime));
			return regressionTrend;

		} catch (Exception ex) {
			return null;
		}
	}
		
	private String getOutputFileName(boolean withInputCsvPath, String suffix) {
		
		StringBuilder builder = new StringBuilder();
		if (withInputCsvPath) {
			builder.append(inputCsvPath);
		}
		builder.append(outDFPrefixFileName.format(new Date(startTimeMilis)));
		
		builder.append("_");
		builder.append(instanceType);
		
		if (availabilityZoneFilter != null) {
			builder.append("_");
			builder.append(availabilityZoneFilter);
		}
		
		if (analysisLabel != null) {
			builder.append("_");
			builder.append(analysisLabel);
		}
		
		builder.append("_");
		builder.append(suffix);
		return builder.toString();
	}
}
