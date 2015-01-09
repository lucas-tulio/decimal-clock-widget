package com.lucasdnd.decimaltimeclockwidget;

import android.text.format.Time;

/**
 * As proposed by Isaac Asimov
 * @author lucas.tulio
 *
 */
public class WorldSeasonCalendar {
	
	private static final int DAY_OFFSET = 11;		// The World Season year starts in Dec 21
	
	/**
	 * Converts the stupid Gregorian date into Asimov's World Season date
	 * 
	 * @param today
	 * @return
	 */
	public static String getWorldSeasonDate(Time today) {
		
		// Results
		int month, dayOfMonth;
		int leapDay = 0;
		int dayOfYear = today.yearDay;
		
		// LEAP DAY!
		if(today.getActualMaximum(Time.YEAR_DAY) > 364) {
			
			if(today.monthDay == 20 && today.month == 5) {
				return "B-92";
			} else if((today.monthDay >= 21 && today.month >= 5) || (today.month >= 6)) {
				leapDay = 1;
			}
		}
		
		// Common year, after Dec. 21
		if(today.month == 11 && today.monthDay >= 21) {
			
			leapDay = 0;
			dayOfYear = DAY_OFFSET - (today.getActualMaximum(Time.YEAR_DAY) - (today.yearDay - 1));
			dayOfYear -= DAY_OFFSET; // No offset here
		}
		
		// YEAR DAY!
		if(dayOfYear + DAY_OFFSET == today.getActualMaximum(Time.YEAR_DAY)) {
			return "D-92";
		}
		
		// Get the Date
		dayOfYear -= leapDay;
		dayOfYear += DAY_OFFSET;
		month = dayOfYear / 91;
		dayOfMonth = dayOfYear % 91; dayOfMonth++; // Prevent day zero
		
		// Convert to string
		String monthString = monthToString(month);
		String dayOfMonthString = "" + dayOfMonth;
		if(dayOfMonthString.length() == 1) {
			dayOfMonthString = "0" + dayOfMonthString;
		}
		
		return monthString + "-" + dayOfMonthString;
	}
	
	private static String monthToString(int month) {
		switch(month) {
		case 0:
			return "A";
		case 1:
			return "B";
		case 2:
			return "C";
		case 3:
			return "D";
		default:
			return null;
		}
	}
	
	/**
	 * Get the year
	 * 
	 * @param today
	 * @param startingYear
	 * @return
	 */
	public static int getYear(Time today, int startingYear) {
		
		int year = today.year - startingYear;
		
		if (today.month == 11 && today.monthDay >= 21) {
			year++;
		}
		
		return year;
	}
}
