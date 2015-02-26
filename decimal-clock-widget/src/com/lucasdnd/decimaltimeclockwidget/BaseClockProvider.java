package com.lucasdnd.decimaltimeclockwidget;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.RemoteViews;

public abstract class BaseClockProvider extends AppWidgetProvider {
	
	public static String CLOCK_UPDATE = "com.lucasdnd.decimaltimeclockwidget.CLOCK_UPDATE";
	public static String SWITCH_COLORS_ACTION = "com.lucasdnd.decimaltimeclockwidget.SWITCH_COLORS";
	public static String UPDATE_PREFERENCES = "android.appwidget.action.APPWIDGET_UPDATE_OPTIONS";
	private static int canvasSize = 400;
	private static int canvasPadding = 12;
	
	// Preferences
	private static final int GREGORIAN_DATE = 0;
	private static int dayAndMonth = 0;
	private static int startingYear = 0;
	private static boolean isWhiteColor = true;
	
	@Override
	public void onReceive(Context context, Intent intent) {

		super.onReceive(context, intent);
		
		// Get the widget manager and ids for this widget provider, then call the shared clock update method.
		ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
	    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
	    
	    if (UPDATE_PREFERENCES.equals(intent.getAction())) {
	    	int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
		    for (int appWidgetID: ids) {
		    	
		    	Bundle bundle = appWidgetManager.getAppWidgetOptions(appWidgetID);
		    	isWhiteColor = (bundle.getInt("color", 0) == 0);
		    	dayAndMonth = bundle.getInt("dayAndMonth", 0);
		    	startingYear = bundle.getInt("year", 0);
		    	
				updateClock(context, appWidgetManager, appWidgetID);
		    }
	    }
	    
	    // Clock Update Event
		if (CLOCK_UPDATE.equals(intent.getAction())) {
		    int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
		    for (int appWidgetID: ids) {
				updateClock(context, appWidgetManager, appWidgetID);
		    }
		}
		
		// Touch Event
		if(SWITCH_COLORS_ACTION.equals(intent.getAction())) {
			int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
		    for (int appWidgetID: ids) {
		    	updateClock(context, appWidgetManager, appWidgetID);
		    }
		}
	}
	
	private PendingIntent createClockTickIntent(Context context) {
		Intent intent = new Intent(CLOCK_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
	}
	
	@Override
	public void onDisabled(Context context) {
		
		super.onDisabled(context);
		
		// Stop the Timer
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));	
	}
	
	@Override 
	public void onEnabled(Context context) {
		
		super.onEnabled(context);
		
		// Create the Timer
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 1);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 8640, createClockTickIntent(context));
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		// Perform this loop procedure for each App Widget that belongs to this provider
		for (int i = 0; i < appWidgetIds.length; i++) {
			
			int appWidgetId = appWidgetIds[i];
			
			// Get the layout for the App Widget and attach an on-click listener to the button
			AppWidgetProviderInfo appInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
			if (context == null || appInfo == null) {
				return;
			}
			RemoteViews views = new RemoteViews(context.getPackageName(), appInfo.initialLayout);
			
			// Update The clock label using a shared method
			updateClock(context, appWidgetManager, appWidgetId);
	        
	        // Tell the AppWidgetManager to perform an update on the current app widget	        
	        appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
	
	public static void updateClock(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		
		// Load preferences
		SharedPreferences prefs = context.getSharedPreferences("preferences", Context.MODE_PRIVATE); 
		isWhiteColor = (prefs.getInt("color", 0) == 0);
		dayAndMonth = prefs.getInt("dayAndMonth", 0);
		startingYear = prefs.getInt("year", 0);
		
		// Get a reference to our Remote View
		AppWidgetProviderInfo appInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
		RemoteViews views = new RemoteViews(context.getPackageName(), appInfo.initialLayout);
		
		// Update the time text
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		int hour = today.hour;
		int minute = today.minute;
		int second = today.second;
		double time = second + (minute * 60) + (hour * 60 * 60);
		double secondsInDay = 86400; // Seconds in a day
		double decimalHour = time * 100 / secondsInDay;
		int decimalMinuteInt = (int)(Math.floor((decimalHour - Math.floor(decimalHour)) * 100));
		
		// Strings that will be shown on the widget
		String decimalHourString = "" + ((int)decimalHour);
		if(decimalHourString.length() == 1) {
			decimalHourString = "0" + decimalHourString;
		}
		String decimalMinuteString = "" + decimalMinuteInt;
		if(decimalMinuteString.length() == 1) {
			decimalMinuteString =  "0" + decimalMinuteString;
		}
		
		// Image
		float timeSlice = (float)(time * 360 / secondsInDay);
		
		Bitmap bitmap = Bitmap.createBitmap(canvasSize, canvasSize, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint p = new Paint();
		p.setAntiAlias(true);
		
		// Draw the white background
		p.setColor(Color.argb(128, 255, 255, 255));
		p.setStyle(Paint.Style.FILL);
		canvas.drawCircle(canvasSize / 2, canvasSize / 2, canvasSize / 2 - canvasPadding, p);
		
		// Draw the gray circle
		if(isWhiteColor) {
			p.setColor(Color.argb(255, 200, 200, 200));
		} else {
			p.setColor(Color.argb(255, 159, 159, 159));
		}
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3);
		canvas.drawCircle(canvasSize / 2, canvasSize / 2, canvasSize / 2 - canvasPadding, p);
		
		// Draw the outter arc
		if(isWhiteColor) {
			p.setColor(Color.argb(255, 255, 255, 255));
			views.setInt(R.id.clockTextView, "setTextColor", Color.argb(255, 242, 242, 242));
			views.setInt(R.id.dateTextView, "setTextColor", Color.argb(255, 242, 242, 242));
		} else {
			p.setColor(Color.argb(255, 117, 117, 117));
			views.setInt(R.id.clockTextView, "setTextColor", Color.argb(255, 117, 117, 117));
			views.setInt(R.id.dateTextView, "setTextColor", Color.argb(255, 117, 117, 117));
		}
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(canvasPadding);
		p.setStrokeCap(Paint.Cap.ROUND);
		canvas.drawArc(new RectF(canvasPadding, canvasPadding, canvasSize - canvasPadding, canvasSize - canvasPadding), -90f, timeSlice, false, p);
		
		views.setImageViewBitmap(R.id.background, bitmap);
		
		// Apply the time to the views
		views.setTextViewText(R.id.clockTextView, "" + decimalHourString + ":" + decimalMinuteString);
		
		if (dayAndMonth == GREGORIAN_DATE) {
			int year = today.year - startingYear;
			views.setTextViewText(R.id.dateTextView, today.monthDay + "/" + (today.month + 1) + ", " + year);
		} else {
			int year = WorldSeasonCalendar.getYear(today, startingYear);
			views.setTextViewText(R.id.dateTextView, WorldSeasonCalendar.getWorldSeasonDate(today) + ", " + year);			
		}
		
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}
}
