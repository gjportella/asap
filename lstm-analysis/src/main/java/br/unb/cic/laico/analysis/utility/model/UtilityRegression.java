package br.unb.cic.laico.analysis.utility.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import br.unb.cic.laico.analysis.utility.config.UtilityConfiguration;
import br.unb.cic.laico.data.ec2.OnDemandPrice;
import br.unb.cic.laico.data.ec2.SpotHistoryPoint;
import br.unb.cic.laico.data.utility.FutureEstimate;
import br.unb.cic.laico.data.utility.UtilityInformation;

public class UtilityRegression {

	private static Logger logger = Logger.getLogger(UtilityRegression.class);

	// Default window size in millisecond (12h)
	private static final long WINDOW_SIZE = 12 * 60 * 60 * 1000;

	// Future window size for availability evaluation
	private static final long FUTURE_WINDOW_SIZE = WINDOW_SIZE;

	private UtilityConfiguration configuration;
	private List<SpotHistoryPoint> spotHistoryPointList;

	public UtilityRegression(UtilityConfiguration configuration, List<SpotHistoryPoint> spotHistoryPointList) {
		this.configuration = configuration;
		this.spotHistoryPointList = spotHistoryPointList;
	}

	public void calculateOptimalUtilities() throws Exception {

		logger.debug("Calculating Optimal Utilities");

		// Obtencao do momento inicial na serie historica (nesse caso, o primeiro ponto
		// no inicio do periodo de teste LSTM)
		int indexT0 = 0;
		for (int i=0; i<spotHistoryPointList.size(); i++) {
			SpotHistoryPoint point = spotHistoryPointList.get(i);
			if (point.getStatus() == SpotHistoryPoint.STATUS_TESTING) {
				indexT0 = i;
				break;
			}
		}

		// Obtem o preco ondemand da instancia
		double ondemandPrice = readOndemandPrice();

		// Aplicacao do fator multiplicador no limite superior do preco on demand
		double odPriceLowerLimit = (configuration.getOdPriceFactorLowerLimit()
				* ondemandPrice);

		// Aplicacao do fator multiplicador no limite superior do preco on demand
		double odPriceUpperLimit = (configuration.getOdPriceFactorUpperLimit()
				* ondemandPrice);

		// Criacao do vetor de lances estimados do usuario com valor inicial igual a zero
		// ate o limite on-demand, com incremento igual a precisao de lance estimada
		List<Double> aux1 = new LinkedList<Double>();
		double estimatedBid = odPriceLowerLimit;
		while (estimatedBid <= odPriceUpperLimit) {
			estimatedBid += configuration.getEstimationAccuracy();
			aux1.add(Double.valueOf(estimatedBid));
		}
		List<Double> estimatedBids = new ArrayList<Double>(aux1);

		// Percorre todos os valores da serie historica a partir da segunda semana
		for (int i=indexT0; i<spotHistoryPointList.size(); i++) {

			// Ponto na serie historica a ser analisado
			SpotHistoryPoint pointTi = spotHistoryPointList.get(i);

			// Obtem janela de tempo anterior ao ponto
			List<SpotHistoryPoint> extendedWindow = getWindow(i);

			// Calcula utilidade para cada lance estimado considerando a janela
			UtilityInformation optimalUtility = getOptimalUtility(extendedWindow,
					pointTi, odPriceUpperLimit, estimatedBids);

			// Atribui a utilidade otima ao objeto da serie historica
			pointTi.setUtilityInformation(optimalUtility);
		}
	}
	
	private double readOndemandPrice() throws Exception {
		OnDemandPrice instance = OnDemandPrice.getInstance();
		return instance.getPrice(configuration.getInstanceType());
	}
	
	private List<SpotHistoryPoint> getWindow(int currentIndex) {
		
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
		
		// Retorna a lista resultante (implementa array para percorrimento mais rapido)
		extendedWindow = new ArrayList<SpotHistoryPoint>(extendedWindow);
		return extendedWindow;
	}

	private UtilityInformation getOptimalUtility(List<SpotHistoryPoint> timeWindow, SpotHistoryPoint point,
			double odPriceUpperLimit, List<Double> estimatedBids) throws Exception {
		
		// Calcula utilidade para cada lance estimado considerando a janela
		UtilityInformation greatestUtility = new UtilityInformation();
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
			double utility = Math.pow((odPriceUpperLimit - estimatedBid), 0.5) * availability;
			
			// Verifica se foi calculada a maior utilidade
			if (utility > greatestUtility.getUtility()) {

				// Atualiza objeto com dados da utilidade otima
				greatestUtility.setBid(estimatedBid);
				greatestUtility.setAvailability(availability);
				greatestUtility.setUtility(utility);
			}
		}
		
		// Retorna ponto com a utilidade otima
		return greatestUtility;
	}
	
	public void calculateFutureAvailability() throws Exception {
		
		logger.debug("Calculating Optimal Utilities");

		// Obtencao do ultimo ponto da serie historica
		SpotHistoryPoint lastPoint = spotHistoryPointList.get(spotHistoryPointList.size() - 1);
		
		// Percorre todos os valores da serie historica
		for (int i=0; i<spotHistoryPointList.size(); i++) {

			// Ponto na serie historica a ser analisado no momento Ti
			SpotHistoryPoint pointTi = spotHistoryPointList.get(i);

			// Verifica se nao foi calculada a utilidade para o ponto Ti e
			// continua para o processamento do proximo ponto da serie
			UtilityInformation utilityInfo = pointTi.getUtilityInformation();
			if (utilityInfo == null) {
				continue;
			}

			// Tempo total da janela de tempo futura (em ms)
			long totalMilisInWindow = 0L;

			// Estimativas futuras de disponibilidade, lance e preco da instancia, considerando lance fixo
			// e lance totalmente lance variavel
			FutureEstimate futureEstimateForFixedBid = new FutureEstimate(FUTURE_WINDOW_SIZE);
			FutureEstimate futureEstimateForVariableBid = new FutureEstimate(FUTURE_WINDOW_SIZE);

			// Define o lance na estimativa futura com lance fixo
			futureEstimateForFixedBid.addAcumulatedBidVariation(utilityInfo.getBid());
			futureEstimateForFixedBid.addError(pointTi.getPrice() - utilityInfo.getBid());

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
				if (currPoint.getPrice() <= utilityInfo.getBid()) {
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
				if (currPoint.getPrice() <= currPoint.getUtilityInformation().getBid()) {
					futureEstimateForVariableBid.addAcumulatedAvailableTimeInWindow(difTimeMilis);

					// Adiciona valor de lance a estimativa futura de lance variavel
					futureEstimateForVariableBid.addAcumulatedBidVariation(currPoint.getUtilityInformation().getBid());
					futureEstimateForVariableBid.addError(currPoint.getPrice() - currPoint.getUtilityInformation().getBid());

				} else {
					
					// Verifica e define tempo ate a primeira indisponibilidade da instancia
					if (futureEstimateForVariableBid.getFirstChunkAvailableTimeInWindow() == 0L) {
						futureEstimateForVariableBid.setFirstChunkAvailableTimeInWindow(
								futureEstimateForVariableBid.getAcumulatedAvailableTimeInWindow());
					}
				}

				// Atualiza indices para proxima iteração
				currIndex = nextIndex;
				nextIndex++;
			}

			// Atualiza estimativas futuras no objeto utilidade
			futureEstimateForFixedBid.updateEstimatedValues();
			utilityInfo.setFutureEstimateForFixedBid(futureEstimateForFixedBid);

			futureEstimateForVariableBid.updateEstimatedValues();
			utilityInfo.setFutureEstimateForVariableBid(futureEstimateForVariableBid);
		}
	}
}
