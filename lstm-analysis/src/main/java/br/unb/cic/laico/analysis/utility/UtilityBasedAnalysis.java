package br.unb.cic.laico.analysis.utility;

import java.util.List;

import br.unb.cic.laico.analysis.utility.config.UtilityConfiguration;
import br.unb.cic.laico.data.ec2.SpotHistoryPoint;

public interface UtilityBasedAnalysis {

	void doAnalysis(UtilityConfiguration configuration,
			List<SpotHistoryPoint> spotHistoryPointList) throws Exception;

	double getMSE();

	double getRMSE();

	double getFutureMSEForFixedBid();

	double getFutureMSEForVariableBid();
}
