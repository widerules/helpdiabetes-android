package be.goossens.oracle;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ShowPreferencesMealTimes extends Activity {
	private DbAdapter dbHelper;

	private EditText editTextMealTimeLunch, editTextMealTimeBreakfast,
			editTextMealTimeSnack, editTextMealTimeDinner,
			editTextMealTimeLunchTwo, editTextMealTimeBreakfastTwo,
			editTextMealTimeSnackTwo, editTextMealTimeDinnerTwo;
	private int id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_preferences_meal_times);

		dbHelper = new DbAdapter(this);
		dbHelper.open();

		editTextMealTimeBreakfast = (EditText) findViewById(R.id.editTextShowPreferencesMealTimesBreakfastTime);
		editTextMealTimeLunch = (EditText) findViewById(R.id.editTextShowPreferencesMealTimesLunchTime);
		editTextMealTimeSnack = (EditText) findViewById(R.id.editTextShowPreferencesMealTimesSnackTime);
		editTextMealTimeDinner = (EditText) findViewById(R.id.editTextShowPreferencesMealTimesDinnerTime);

		editTextMealTimeBreakfastTwo = (EditText) findViewById(R.id.editTextShowPreferencesMealTimesBreakfastTimeTwo);
		editTextMealTimeLunchTwo = (EditText) findViewById(R.id.editTextShowPreferencesMealTimesLunchTimeTwo);
		editTextMealTimeSnackTwo = (EditText) findViewById(R.id.editTextShowPreferencesMealTimesSnackTimeTwo);
		editTextMealTimeDinnerTwo = (EditText) findViewById(R.id.editTextShowPreferencesMealTimesDinnerTimeTwo);
		fillData();
	}

	private void fillData() {
		Cursor cSettings = dbHelper.fetchAllSettings();
		startManagingCursor(cSettings);
		cSettings.moveToFirst();
		id = cSettings.getInt(cSettings
				.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_ID));
		editTextMealTimeBreakfast
				.setText(cSettings
						.getString(
								cSettings
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_MEAL_TIME_BREAKFAST))
						.subSequence(
								0,
								cSettings
										.getString(
												cSettings
														.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_MEAL_TIME_BREAKFAST))
										.indexOf(":")));
		editTextMealTimeBreakfastTwo
				.setText(cSettings
						.getString(
								cSettings
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_MEAL_TIME_BREAKFAST))
						.substring(
								cSettings
										.getString(
												cSettings
														.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_MEAL_TIME_BREAKFAST))
										.indexOf(":") + 1));

		editTextMealTimeLunch
				.setText(cSettings
						.getString(
								cSettings
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_MEAL_TIME_LUNCH))
						.subSequence(
								0,
								cSettings
										.getString(
												cSettings
														.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_MEAL_TIME_LUNCH))
										.indexOf(":")));
		editTextMealTimeLunchTwo
				.setText(cSettings
						.getString(
								cSettings
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_MEAL_TIME_LUNCH))
						.substring(
								cSettings
										.getString(
												cSettings
														.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_MEAL_TIME_LUNCH))
										.indexOf(":") + 1));

		editTextMealTimeSnack
				.setText(cSettings
						.getString(
								cSettings
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_MEAL_TIME_SNACK))
						.subSequence(
								0,
								cSettings
										.getString(
												cSettings
														.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_MEAL_TIME_SNACK))
										.indexOf(":")));
		editTextMealTimeSnackTwo
				.setText(cSettings
						.getString(
								cSettings
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_MEAL_TIME_SNACK))
						.substring(
								cSettings
										.getString(
												cSettings
														.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_MEAL_TIME_SNACK))
										.indexOf(":") + 1));

		editTextMealTimeDinner
				.setText(cSettings
						.getString(
								cSettings
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_MEAL_TIME_DINNER))
						.subSequence(
								0,
								cSettings
										.getString(
												cSettings
														.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_MEAL_TIME_DINNER))
										.indexOf(":")));
		editTextMealTimeDinnerTwo
				.setText(cSettings
						.getString(
								cSettings
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_MEAL_TIME_DINNER))
						.substring(
								cSettings
										.getString(
												cSettings
														.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_MEAL_TIME_DINNER))
										.indexOf(":") + 1));

		cSettings.close();
	}

	// If we click on go to page Show insuline ratios
	public void onClickGoToInsulineRatios(View view) {
		// call finish becaus this activity is finished ( so when we press the
		// back button on our phone we go to the list of food and not to the
		// other preference )
		finish();
		Intent i = new Intent(this, ShowPreferencesInsulineRatio.class);
		startActivity(i);
	}

	// on click back button
	public void onClickBack(View view) {
		finish();
	}

	// on click update
	public void onClickUpdate(View view) {
		// if values are checked we can insert them in to the database
		if (checkValues()) {
			String breakfast, lunch, snack, dinner;
			breakfast = editTextMealTimeBreakfast.getText().toString() + ":"
					+ editTextMealTimeBreakfastTwo.getText().toString();
			
			lunch = editTextMealTimeLunch.getText().toString() + ":"
					+ editTextMealTimeLunchTwo.getText().toString();
			
			snack = editTextMealTimeSnack.getText().toString() + ":"
					+ editTextMealTimeSnackTwo.getText().toString();
			
			dinner = editTextMealTimeDinner.getText().toString() + ":"
					+ editTextMealTimeDinnerTwo.getText().toString();
			
			dbHelper.updateSettingsMealTimes(id, breakfast, lunch, snack, dinner);
			Toast.makeText(this, getResources().getString(R.string.saved), Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, getResources().getString(R.string.pref_error_hour_and_minutes), Toast.LENGTH_LONG).show();
		}
	}

	private boolean checkValues() {
		boolean pass = false;
		int breakfastOne, breakfastTwo, lunchOne, lunchTwo, snackOne, snackTwo, dinnerOne, dinnerTwo;

		try {
			breakfastOne = Integer.parseInt(editTextMealTimeBreakfast.getText()
					.toString());
			breakfastTwo = Integer.parseInt(editTextMealTimeBreakfastTwo
					.getText().toString());

			lunchOne = Integer.parseInt(editTextMealTimeLunch.getText()
					.toString());
			lunchTwo = Integer.parseInt(editTextMealTimeLunchTwo.getText()
					.toString());

			snackOne = Integer.parseInt(editTextMealTimeSnack.getText()
					.toString());
			snackTwo = Integer.parseInt(editTextMealTimeSnackTwo.getText()
					.toString());

			dinnerOne = Integer.parseInt(editTextMealTimeDinner.getText()
					.toString());
			dinnerTwo = Integer.parseInt(editTextMealTimeDinnerTwo.getText()
					.toString());

			// the integers with 'One' cant be more then 24 ( caus thats the max
			// a hour can be )
			// the integers with 'two' cant be more then 59 ( caus thats the max
			// a minute can be )
			if (breakfastOne <= 24 && lunchOne <= 24 && snackOne <= 24
					&& dinnerOne <= 24 && breakfastTwo <= 59 && lunchTwo <= 59
					&& snackTwo <= 59 && dinnerTwo <= 59) {
				pass = true;
			} else {
				pass = false;
			}

		} catch (Exception e) {
			pass = false;
		}

		return pass;
	}

	@Override
	protected void onPause() {
		dbHelper.close();
		super.onPause();
	}

}
