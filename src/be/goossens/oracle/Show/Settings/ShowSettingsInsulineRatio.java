package be.goossens.oracle.Show.Settings;

import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupSettings;
import be.goossens.oracle.Rest.DbAdapter;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class ShowSettingsInsulineRatio extends Activity {
	private DbAdapter dbHelper;
	private EditText insulineRatioBreakfast, insulineRatioLunch,
			insulineRatioSnack, insulineRatioDinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_insuline_ratio);
		dbHelper = new DbAdapter(this);
		dbHelper.open();
		insulineRatioBreakfast = (EditText) findViewById(R.id.editTextShowPreferencesBreakfastRatio);
		insulineRatioLunch = (EditText) findViewById(R.id.editTextShowPreferencesLunchRatio);
		insulineRatioSnack = (EditText) findViewById(R.id.editTextShowPreferencesSnackRatio);
		insulineRatioDinner = (EditText) findViewById(R.id.editTextShowPreferencesDinnerRatio);
		fillData();
	}

	public void fillData() {

		float breakfastRatio = 0;
		float lunchRatio = 0;
		float snackRatio = 0;
		float dinnerRatio = 0;

		Cursor cSettingInsulineRatioBreakfast = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.insuline_ratio_breakfast));
		cSettingInsulineRatioBreakfast.moveToFirst();
		breakfastRatio = cSettingInsulineRatioBreakfast
				.getFloat(cSettingInsulineRatioBreakfast
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));
		
		Cursor cSettinginsulineratioLunch = dbHelper.fetchSettingByName(getResources().getString(R.string.insuline_ratio_lunch));
		cSettinginsulineratioLunch.moveToFirst();
		lunchRatio = cSettinginsulineratioLunch.getFloat(cSettinginsulineratioLunch.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));

		Cursor cSettingInsulineRatioSnack = dbHelper.fetchSettingByName(getResources().getString(R.string.insuline_ratio_snack));
		cSettingInsulineRatioSnack.moveToFirst();
		snackRatio = cSettingInsulineRatioSnack.getFloat(cSettingInsulineRatioSnack.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));
		
		Cursor cSettingInsulineRatioDinner = dbHelper.fetchSettingByName(getResources().getString(R.string.insuline_ratio_dinner));
		cSettingInsulineRatioDinner.moveToFirst();
		dinnerRatio = cSettingInsulineRatioDinner.getFloat(cSettingInsulineRatioDinner.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));
		
		insulineRatioBreakfast.setText("" + breakfastRatio);
		insulineRatioLunch.setText("" + lunchRatio);
		insulineRatioSnack.setText("" + snackRatio);
		insulineRatioDinner.setText("" + dinnerRatio);

		cSettingInsulineRatioDinner.close();
		cSettingInsulineRatioSnack.close();
		cSettinginsulineratioLunch.close();
		cSettingInsulineRatioBreakfast.close();
	}

	

	// on click update
	public void onClickUpdate(View view) {
			float breakfastRatio = 0;
			float lunchRatio = 0;
			float snackRatio = 0;
			float dinnerRatio = 0;

			try {
				breakfastRatio = Float.parseFloat(insulineRatioBreakfast
						.getText().toString());
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

			// Round the calculated floats
			float p = (float) Math.pow(10, 2);
			breakfastRatio = Math.round(breakfastRatio * p) / p;
			lunchRatio = Math.round(lunchRatio * p) / p;
			snackRatio = Math.round(snackRatio * p) / p;
			dinnerRatio = Math.round(dinnerRatio * p) / p;
			
			dbHelper.updateSettingsByName(getResources().getString(R.string.insuline_ratio_breakfast), "" + breakfastRatio);
			dbHelper.updateSettingsByName(getResources().getString(R.string.insuline_ratio_lunch), "" + lunchRatio);
			dbHelper.updateSettingsByName(getResources().getString(R.string.insuline_ratio_snack), "" + snackRatio);
			dbHelper.updateSettingsByName(getResources().getString(R.string.insuline_ratio_dinner), "" + dinnerRatio);
			 
			// go back
			ActivityGroupSettings.group.back();
	}
	
	@Override
	protected void onPause() {
		dbHelper.close();
		super.onPause();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
