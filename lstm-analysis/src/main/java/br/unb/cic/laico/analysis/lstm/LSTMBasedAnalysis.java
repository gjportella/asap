package br.unb.cic.laico.analysis.lstm;

import java.util.List;

import br.unb.cic.laico.analysis.lstm.config.LSTMConfiguration;
import br.unb.cic.laico.data.ec2.SpotHistoryPoint;

public interface LSTMBasedAnalysis {

	void doAnalysis(LSTMConfiguration configuration) throws Exception;

	double getOndemandPrice();

	List<SpotHistoryPoint> getSpotHistoryPointList();

	long getStartTimeMilis();

	long getEndTimeMilis();

	double getTrainingAverageMSE();

	double getTrainingAverageRMSE();

	double getPredictionAverageMSE();

	double getPredictionAverageRMSE();
}
