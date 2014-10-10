package com.lucasdnd.decimaltimeclockwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

public class LockScreenClockProvider extends BaseClockProvider {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}
	
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	}
	
	@Override 
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
