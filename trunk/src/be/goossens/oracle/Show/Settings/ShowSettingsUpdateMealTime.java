package be.goossens.oracle.Show.Settings;

import be.goossens.oracle.R;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

public class ShowSettingsUpdateMealTime extends Activity {
	private TextView tv;
	private TimePicker tp;
	private DbAdapter dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_update_meal_time);
		dbHelper = new DbAdapter(this);
		tv = (TextView) findViewById(R.id.textViewShowSettingsUpdateMealTime);
		tp = (TimePicker) findViewById(R.id.timePickerShowSettingsUpdateMealTime);
		// set the time picker to 24h mode view
		tp.setIs24HourView(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		setTextView();
		setTimePicker();
	}

	private void setTimePicker() {
		// Get the right time from the settings out the database
		Cursor cSetting = dbHelper.fetchSettingByName(getIntent().getExtras()
				.getString(DataParser.fromWhereWeCome));
		cSetting.moveToFirst();
		// get the hours and minutes out the settings
		int hours = Integer
				.parseInt(cSetting
						.getString(
								cSetting.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE))
						.substring(
								0,
								cSetting.getString(
										cSetting.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE))
										.indexOf(":")));
		int minutes = Integer
				.parseInt(cSetting
						.getString(
								cSetting.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE))
						.substring(
								cSetting.getString(
										cSetting.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE))
										.indexOf(":") + 1));
		// close the cursor
		cSetting.close();
		// Set the hours and minutes out the settings in the timepicker
		tp.setCurrentHour(hours);
		tp.setCurrentMinute(minutes);
	}

	private void setTextView() {
		// set the right text in the textview ( breakfast, lunch, snack or
		// dinner )
		if (getIntent().getExtras().getString(DataParser.fromWhereWeCome)
				.equals(getResources().getString(R.string.meal_time_lunch))) {
			// set lunch in tv
			tv.setText(getResources().getString(R.string.pref_lunchratio_title)
					+ " " + getResources().getString(R.string.starts_at));
		} else if (getIntent().getExtras()
				.getString(DataParser.fromWhereWeCome)
				.equals(getResources().getString(R.string.meal_time_snack))) {
			// set snack in tv
			tv.setText(getResources().getString(R.string.pref_snackratio_title)
					+ " " + getResources().getString(R.string.starts_at));
		} else if (getIntent().getExtras()
				.getString(DataParser.fromWhereWeCome)
				.equals(getResources().getString(R.string.meal_time_dinner))) {
			// set dinner in tv
			tv.setText(getResources()
					.getString(R.string.pref_dinnerratio_title)
					+ " "
					+ getResources().getString(R.string.starts_at));
		} else {
			// set breakfast in tv
			tv.setText(getResources().getString(
					R.string.pref_breakfastratio_title)
					+ " " + getResources().getString(R.string.starts_at));
		}
	}

	// on click on the save button
	public void onClickSave(View view) {
		// if the minutes are less then 10 ( 0 - 9 ) then we have to add another
		// zero in front of the minutes
		if (tp.getCurrentMinute() < 10) {
			//we add a zero in front of the minutes when minutes are less then 10
			updateMealTime(":0");
		} else {
			updateMealTime(":");
		}
		setResult(RESULT_OK);
		finish();
	}

	private void updateMealTime(String middle) {
		dbHelper.updateSettingsByName(
				getIntent().getExtras().getString(
						DataParser.fromWhereWeCome), tp.getCurrentHour()
						+ middle + tp.getCurrentMinute());
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}
}
