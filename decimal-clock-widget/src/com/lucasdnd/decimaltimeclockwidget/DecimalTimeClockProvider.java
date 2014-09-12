package com.lucasdnd.decimaltimeclockwidget;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.format.Time;
import android.widget.RemoteViews;

public class DecimalTimeClockProvider extends AppWidgetProvider {
	
	public static String CLOCK_UPDATE = "com.lucasdnd.decimaltimeclockwidget.CLOCK_UPDATE";
	private static int canvasSize = 400;
	private static int canvasPadding = 12;
	
	@Override
	public void onReceive(Context context, Intent intent) {

		super.onReceive(context, intent);

		// Get the widget manager and ids for this widget provider, then call the shared clock update method.
		ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
	    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				    
		if (CLOCK_UPDATE.equals(intent.getAction())) {
			
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
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000, createClockTickIntent(context));
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		// Perform this loop procedure for each App Widget that belongs to this provider
		for (int i = 0; i < appWidgetIds.length; i++) {
			
			int appWidgetId = appWidgetIds[i];

			// Get the layout for the App Widget and attach an on-click listener to the button
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgetlayout);
			
			// Tell the AppWidgetManager to perform an update on the current app widget
			appWidgetManager.updateAppWidget(appWidgetId, views);

			// Update The clock label using a shared method
			updateClock(context, appWidgetManager, appWidgetId);
		}
	}

	public static void updateClock(Context context,	AppWidgetManager appWidgetManager, int appWidgetId) {
		
		// Update the time text
		RemoteViews views = new RemoteViews(context.getPackageName(),	R.layout.widgetlayout);
		
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		int hour = today.hour;
		int minute = today.minute;
		int second = today.second;
		double time = second + (minute * 60) + (hour * 60 * 60);
		double secondsInDay = 86400; // Seconds in a day
		double decimalHour = time * 100 / secondsInDay;
		int decimalMinuteInt = (int)(Math.floor((decimalHour - Math.floor(decimalHour)) * 100));
		String decimalMinute = "" + decimalMinuteInt;
		if(decimalMinute.length() == 1) {
			decimalMinute =  "0" + decimalMinute;
		}
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
		p.setColor(Color.argb(255, 200, 200, 200));
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3);
		canvas.drawCircle(canvasSize / 2, canvasSize / 2, canvasSize / 2 - canvasPadding, p);
		
		// Draw the white arc
		p.setColor(Color.argb(255, 255, 255, 255));
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(canvasPadding);
		p.setStrokeCap(Paint.Cap.ROUND);
		canvas.drawArc(new RectF(canvasPadding, canvasPadding, canvasSize - canvasPadding, canvasSize - canvasPadding), -90f, timeSlice, false, p);
		
		views.setImageViewBitmap(R.id.background, bitmap);
		views.setTextViewText(R.id.clockTextView, "" + ((int)decimalHour + ":" + decimalMinute));
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}
}