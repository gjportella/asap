package br.unb.cic.conversion.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MathUtil {

	private static final int DEFAULT_SCALE = 6;

	public static String round2String(Double d) {
		return Double.toString(round(d));
	}
	
	public static String round2String(double d) {
		return Double.toString(round(d));
	}
	
	public static double round(Double d) {
		return round(d.doubleValue());
	}
	
	public static double round(double d) {
		return round(DEFAULT_SCALE, d);
	}
	
	public static double round(int scale, double d) {
		BigDecimal value = new BigDecimal(d).setScale(6, RoundingMode.HALF_EVEN);
        return value.doubleValue();
	}
}
