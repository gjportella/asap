package br.unb.cic.laico.analysis.utilitybased.model;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SpotHistoryPoint implements Serializable, Comparable<SpotHistoryPoint> {

	private static final long serialVersionUID = 1L;

	// Quantidade de dias da semana
	private static final int MAP_INITIAL_CAPACITY = 7;

	// Nomes dos dias da semana
	private static final String[] WEEKDAYS_NAMES = DateFormatSymbols.getInstance().getWeekdays();

	private long time;
	private double price;
	private Date dateObject;
	private int dayOfWeek;
	private UtilityPoint utilityPoint;

	private Map<Integer, UtilityPoint> utilityByDayOfWeek;

	public SpotHistoryPoint(long time, double price) {

		this(new Date(time), price);
	}

	public SpotHistoryPoint(Date dateObject, double price) {

		this.price = price;
		this.dateObject = dateObject;
		this.time = dateObject.getTime();

		Calendar cal = Calendar.getInstance();
		cal.setTime(this.dateObject);
		this.dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		this.utilityByDayOfWeek = new HashMap<Integer, UtilityPoint>(MAP_INITIAL_CAPACITY);
	}
	
	public int compareTo(SpotHistoryPoint other) {
		if (this.time < other.getTime()) {
			return -1;
		}
		if (this.time > other.getTime()) {
			return 1;
		}
		return 0;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Date getDateObject() {
		return dateObject;
	}

	public void setDateObject(Date dateObject) {
		this.dateObject = dateObject;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public String getDayOfWeekName() {
		return WEEKDAYS_NAMES[dayOfWeek];
	}

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public UtilityPoint getUtilityPoint() {
		return utilityPoint;
	}

	public void setUtilityPoint(UtilityPoint utilityPoint) {
		this.utilityPoint = utilityPoint;
	}

	public UtilityPoint getUtilityByDayOfWeek(int dayOfWeek) {
		return utilityByDayOfWeek.get(Integer.valueOf(dayOfWeek));
	}

	public void setUtilityByDayOfWeek(int dayOfWeek, UtilityPoint utilityPoint) {
		this.utilityByDayOfWeek.put(Integer.valueOf(dayOfWeek), utilityPoint);
	}

	/**
	 * Get now or day of week suggestion based on utility.
	 *
	 * @see java.util.Calendar
	 */
	public String getUtilityBasedSuggestion() {

		double greatestUtility = utilityPoint.getUtility();
		String suggestion = "Now";

		Set<Integer> keySet = utilityByDayOfWeek.keySet();
		for (Integer key:keySet) {

			UtilityPoint point = utilityByDayOfWeek.get(key);
			if (point.getUtility() > greatestUtility) {

				greatestUtility = point.getUtility();
				suggestion = WEEKDAYS_NAMES[key.intValue()];
			}
		}
		return suggestion;
	}
}
