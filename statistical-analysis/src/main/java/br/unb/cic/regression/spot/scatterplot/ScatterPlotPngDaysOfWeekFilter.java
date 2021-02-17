package br.unb.cic.regression.spot.scatterplot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

public class ScatterPlotPngDaysOfWeekFilter {
	
	public ScatterPlotPngDaysOfWeekFilter() {
	}

	public void applyDaysOfWeekFilter(String pngPathPrefix,
			List<ScatterPlotPoint> pointList) throws Exception {
		
		List<Date> uniqueDays = getUniqueDays(pointList);
		String[] weekDays =  getWeekDays(uniqueDays);
		writeWeekDays(weekDays, pngPathPrefix);
	}
	
	private List<Date> getUniqueDays(List<ScatterPlotPoint> pointList) {
		
		List<Date> uniqueDays = new ArrayList<Date>();
		for (ScatterPlotPoint point: pointList) {
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(point.getDateObject());
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.AM_PM, Calendar.AM);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date date = calendar.getTime();
			if (!uniqueDays.contains(date)) {
				uniqueDays.add(date);
			}
		}
		return uniqueDays;
	}
	
	private String[] getWeekDays(List<Date> uniqueDays) {
		
		String[] weekDays = new String[uniqueDays.size()];
		Calendar calendar = Calendar.getInstance();
		for (int i=0; i<uniqueDays.size(); i++) {
			
			calendar.setTime(uniqueDays.get(i));
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			String dayOfWeekStr = "";
			switch (dayOfWeek) {
			case Calendar.SUNDAY:
				dayOfWeekStr = "s";
				break;
			case Calendar.MONDAY:
				dayOfWeekStr = "m";
				break;
			case Calendar.TUESDAY:
				dayOfWeekStr = "t";
				break;
			case Calendar.WEDNESDAY:
				dayOfWeekStr = "w";
				break;
			case Calendar.THURSDAY:
				dayOfWeekStr = "t";
				break;
			case Calendar.FRIDAY:
				dayOfWeekStr = "f";
				break;
			case Calendar.SATURDAY:
				dayOfWeekStr = "s";
				break;
			}
			weekDays[i] = dayOfWeekStr;
		}
		return weekDays;
	}
	
	private void writeWeekDays(String[] weekDays,
			String pngPathPrefix) throws Exception {
		
		BufferedImage img = ImageIO.read(new File(pngPathPrefix + ".png"));
		Graphics2D g = img.createGraphics();
		g.setColor(Color.BLACK);
		
		float xPos = 66f;
		int yPos = 553;
		float xDif = 700f / ((float) weekDays.length);
		
		for (int i=0; i<weekDays.length; i++) {
			
			float xDifWeight = 0f;
			if ("f".equals(weekDays[i]) || "t".equals(weekDays[i])) {
				xDifWeight = 0.7f;
			} else if ("s".equals(weekDays[i])) {
				xDifWeight = 1.0f;
			} else if ("w".equals(weekDays[i])) {
				xDifWeight = 1.4f;
			} else if ("m".equals(weekDays[i])) {
				xDifWeight = 1.5f;
			}
			
			g.drawString(weekDays[i], xPos, yPos);
			if (weekDays.length > 60) {
				xPos = xPos + (xDifWeight * xDif);
			} else {
				xPos = xPos + xDif;
			}
		}
		
		g.dispose();
		ImageIO.write(img, "PNG", new File(pngPathPrefix + "-withWeekDays.png"));		
	}
}
