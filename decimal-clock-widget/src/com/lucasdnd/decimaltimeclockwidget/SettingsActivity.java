package com.lucasdnd.decimaltimeclockwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Spinner;

public class SettingsActivity extends Activity implements OnItemSelectedListener {
	
	private String[] dayMonthOptions = {"Gregorian Calendar", "World Season Calendar"};
	private Context context;
	private int appWidgetId;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		this.context = this.getApplicationContext();
		
		this.setContentView(R.layout.settings_activity);
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		
		// Load saved data
		
		
		// Day and month spinner
		Spinner dayMonthSpinner = (Spinner) findViewById(R.id.dayMonthSpinner);
		ArrayAdapter<String> dayMonthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, dayMonthOptions);
		dayMonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dayMonthSpinner.setAdapter(dayMonthAdapter);
		dayMonthSpinner.setOnItemSelectedListener(this);
		
		// Year Input
		
		
		// Save Button
		Button saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveSettings();
			}
		});
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Log.d("decimal", "selected: " + dayMonthOptions[position]);
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		Log.d("decimal", "nothing selected");
	}
	
	/**
	 * Save the user preferences
	 */
	private void saveSettings() {
		
		// Save preferences
    	Log.d("decimal", "saving settings");
    	
    	AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    	AppWidgetProviderInfo appInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
    	if (context == null || appInfo == null) {
    		finish();
    		return;
    	}
    	RemoteViews views = new RemoteViews(context.getPackageName(), appInfo.initialLayout);
    	appWidgetManager.updateAppWidget(appWidgetId, views);
		
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		setResult(RESULT_OK, resultValue);
		
		finish();
	}
}
