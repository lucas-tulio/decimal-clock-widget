package com.lucasdnd.decimaltimeclockwidget;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingsActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		this.setContentView(R.layout.settings_activity);
		
		// Day and month spinner
		Spinner dayMonthSpinner = (Spinner) findViewById(R.id.dayMonthSpinner);
		
		String[] dayMonthOptions = {"Gregorian Calendar", "World Season Calendar"};
		ArrayAdapter<String> dayMonthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, dayMonthOptions);
		dayMonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dayMonthSpinner.setAdapter(dayMonthAdapter);
		
		// Year Input
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		
		// 1543
		
	}
}
