package com.lucasdnd.decimaltimeclockwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Spinner;

public class SettingsActivity extends Activity {
	
	private String[] dayMonthOptions = {"Gregorian Calendar", "World Season Calendar"};
	private String[] colorOptions = {"White", "Gray"};
	private Context context;
	private int appWidgetId;
	
	private Spinner dayMonthSpinner;
	private EditText yearEditText;
	private Spinner colorSpinner;
	
	private int selectedColor;
	private int selectedDayAndMonth;
	private int startingYear;
	
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
		
		// Load preferences
		SharedPreferences prefs = getSharedPreferences("preferences", MODE_PRIVATE); 
		selectedColor = prefs.getInt("color", 0);
		selectedDayAndMonth = prefs.getInt("dayAndMonth", 0);
		startingYear = prefs.getInt("year", 0);
		
		// Day and month spinner
		dayMonthSpinner = (Spinner) findViewById(R.id.dayMonthSpinner);
		ArrayAdapter<String> dayMonthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, dayMonthOptions);
		dayMonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dayMonthSpinner.setAdapter(dayMonthAdapter);
		dayMonthSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedDayAndMonth = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
			
		});
		dayMonthSpinner.setSelection(selectedDayAndMonth);
		
		// Year Input
		yearEditText = (EditText) findViewById(R.id.yearEditText);
		yearEditText.setText("" + startingYear);
		
		// Color spinner
		colorSpinner = (Spinner) findViewById(R.id.colorSpinner);
		ArrayAdapter<String> colorSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, colorOptions);
		colorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		colorSpinner.setAdapter(colorSpinnerAdapter);
		colorSpinner.setSelection(selectedColor);
		colorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedColor = position;
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		// Save Button
		Button saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				try {
					startingYear = Integer.parseInt(yearEditText.getText().toString());
				} catch (Exception e) {
					startingYear = 0;
					e.printStackTrace();
				}
				
				saveSettings();
			}
		});
	}
	
	/**
	 * Save the user preferences
	 */
	private void saveSettings() {
		
		// Save preferences
    	SharedPreferences.Editor editor = getSharedPreferences("preferences", MODE_PRIVATE).edit();
    	editor.putInt("color", selectedColor);
    	editor.putInt("dayAndMonth", selectedDayAndMonth);
    	editor.putInt("year", startingYear);
    	editor.commit();
    	
    	// Update the widget
    	AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    	AppWidgetProviderInfo appInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
    	if (context == null || appInfo == null) {
    		finish();
    		return;
    	}
    	RemoteViews views = new RemoteViews(context.getPackageName(), appInfo.initialLayout);
    	
    	Bundle b = new Bundle();
    	b.putInt("color", selectedColor);
    	b.putInt("dayAndMonth", selectedDayAndMonth);
    	b.putInt("year", startingYear);
    	
    	appWidgetManager.updateAppWidgetOptions(appWidgetId, b);    	
    	appWidgetManager.updateAppWidget(appWidgetId, views);
		
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		resultValue.setAction("prefs");
		setResult(RESULT_OK, resultValue);
		
		finish();
	}
}
