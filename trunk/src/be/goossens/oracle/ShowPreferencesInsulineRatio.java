package be.goossens.oracle;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ShowPreferencesInsulineRatio extends Activity {
	private DbAdapter dbHelper;
	private EditText insulineRatioBreakfast, insulineRatioLunch,
			insulineRatioSnack, insulineRatioDinner;
	private int id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_preferences_insuline_ratio);
		dbHelper = new DbAdapter(this);
		dbHelper.open();
		insulineRatioBreakfast = (EditText) findViewById(R.id.editTextShowPreferencesBreakfastRatio);
		insulineRatioLunch = (EditText) findViewById(R.id.editTextShowPreferencesLunchRatio);
		insulineRatioSnack = (EditText) findViewById(R.id.editTextShowPreferencesSnackRatio);
		insulineRatioDinner = (EditText) findViewById(R.id.editTextShowPreferencesDinnerRatio);
		fillData();
	}

	public void fillData() {
		Cursor cSettings = dbHelper.fetchAllSettings();
		startManagingCursor(cSettings);
		cSettings.moveToFirst();
		id = cSettings.getInt(cSettings
				.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_ID));
		insulineRatioBreakfast
				.setText(cSettings.getString(cSettings
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_INSULINE_RATIO_BREAKFAST)));
		insulineRatioLunch
				.setText(cSettings.getString(cSettings
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_INSULINE_RATIO_LUNCH)));
		insulineRatioSnack
				.setText(cSettings.getString(cSettings
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_INSULINE_RATIO_SNACK)));
		insulineRatioDinner
				.setText(cSettings.getString(cSettings
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_INSULINE_RATIO_DINNER)));
		cSettings.close();
	}

	// If we click on go to page Show meal times
	public void onClickGoToMealTimes(View view) {
		// call finish becaus this activity is finished ( so when we press the
		// back button on our phone we go to the list of food and not to the
		// other preference )
		finish();
		Intent i = new Intent(this, ShowPreferencesMealTimes.class);
		startActivity(i);
	}

	// on click back button
	public void onClickBack(View view) {
		finish();
	}

	// on click update
	public void onClickUpdate(View view) {
		// insert values in to the database

		float breakfastRatio = 0;
		float lunchRatio = 0;
		float snackRatio = 0;
		float dinnerRatio = 0;

		try {
			breakfastRatio = Float.parseFloat(insulineRatioBreakfast.getText()
					.toString());
		} catch (Exception e) {
			breakfastRatio = 0;
		}
		try {
			lunchRatio = Float.parseFloat(insulineRatioLunch.getText()
					.toString());
		} catch (Exception e) {
			lunchRatio = 0;
		}
		try {
			snackRatio = Float.parseFloat(insulineRatioSnack.getText()
					.toString());
		} catch (Exception e) {
			snackRatio = 0;
		}
		try {
			dinnerRatio = Float.parseFloat(insulineRatioDinner.getText()
					.toString());
		} catch (Exception e) {
			dinnerRatio = 0;
		}

		dbHelper.updateSettingsInsulineRatio(id, breakfastRatio, lunchRatio,
				snackRatio, dinnerRatio);
		Toast.makeText(this, getResources().getString(R.string.saved),
				Toast.LENGTH_LONG).show();
		fillData();
	}

	@Override
	protected void onPause() {
		dbHelper.close();
		super.onPause();
	}
}
