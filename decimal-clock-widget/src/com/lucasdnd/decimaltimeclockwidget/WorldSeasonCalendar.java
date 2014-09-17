package com.lucasdnd.decimaltimeclockwidget;

import android.text.format.Time;

/**
 * As proposed by Isaac Asimov (except for the starting year)
 * @author lucas.tulio
 *
 */
public class WorldSeasonCalendar {
	
	public static final int STARTING_YEAR = 1543;	// Year Copernicus' "On the Revolutions of the Heavenly Spheres" was first published
	private static final int DAY_OFFSET = 11;		// The World Season year starts in Dec 21
	
	/**
	 * Converts the stupid Gregorian date into Asimov's World Season date
	 * @param today
	 * @return
	 */
	public static String getWorldSeasonDate(Time today) {
		
		// Results
		int month, dayOfMonth, year = 0;
		int leapDay = 0;
		int dayOfYear = today.yearDay;
		
		// Calc!
		
		year = today.year - STARTING_YEAR;
		
		// LEAP DAY!
		if(today.getActualMaximum(Time.YEAR_DAY) > 364) {
			
			if(today.monthDay == 20 && today.month == 5) {
				return "B-92, " + year;
			} else if((today.monthDay >= 21 && today.month >= 5) || (today.month >= 6)) {
				leapDay = 1;
			}
		}
		
		// Common year, after Dec. 21
		if(today.month == 11 && today.monthDay >= 21) {
			
			leapDay = 0;
			year++;
			dayOfYear = DAY_OFFSET - (today.getActualMaximum(Time.YEAR_DAY) - (today.yearDay - 1));
			dayOfYear -= DAY_OFFSET; // No offset here
		}
		
		// YEAR DAY!
		if(dayOfYear + DAY_OFFSET == today.getActualMaximum(Time.YEAR_DAY)) {
			return "D-92, " + year;
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
		
		return monthString + "-" + dayOfMonthString + ", " + year;
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
}
