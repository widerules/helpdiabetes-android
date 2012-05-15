// Please read info.txt for license and legal information

package be.goossens.oracle.Show;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import be.goossens.oracle.R;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.DbSettings;
import be.goossens.oracle.Show.Settings.ShowSettingsDBLanguage;
import be.goossens.oracle.Show.Settings.ShowSettingsDefaultMedicineType;
import be.goossens.oracle.Show.Settings.ShowSettingsInsulineRatio;
import be.goossens.oracle.Show.Settings.ShowSettingsMealTimes;

public class ShowStart extends Activity {

	private DbAdapter dbHelper;
	private TextView tv2;

	private final int requestCodeSelectedDBFood = 1;
	private final int requestCodeMealTimes = 2;
	private final int requestCodeKHRatio = 3;
	private final int requestCodeDefaultMedicineType = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_start);

		tv2 = (TextView) findViewById(R.id.textView2);
		tv2.setText(getResources().getString(R.string.checkingDatabase));

		dbHelper = new DbAdapter(this);
		new checkDatabase().execute();
	}

	private class checkDatabase extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				dbHelper.createDataBase();
			} catch (IOException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			goToRightPage();
		}
	}
 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case requestCodeSelectedDBFood:
			showPopUpDiabetesPatient();
			break;
		case requestCodeMealTimes:
			startIntentInsulineRatios();
			break;
		case requestCodeKHRatio:
			showActivityDefaultMedicineType();
			break; 
		case requestCodeDefaultMedicineType:
			startHomeActivityAndStopThisActivity();
			break; 
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showActivityDefaultMedicineType() {
		Intent k = new Intent(this, ShowSettingsDefaultMedicineType.class);
		// mark the intent as first time
		k.putExtra(DataParser.whatToDo, DataParser.doFirstTime);
		startActivityForResult(k, requestCodeDefaultMedicineType);
	}

	private void goToRightPage() {
		// check to see if it is the first time application starts
		dbHelper.open();

		Cursor cSetting = dbHelper.fetchSettingByName(DbSettings.setting_language);
		if (cSetting.getCount() > 0) {
			cSetting.moveToFirst();

			if (cSetting.getLong(cSetting
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)) == 0) {

				// first time application starts!
				startActivityDBLanguage();

			} else {
				// mark app as startup ( to show dialog when we have already
				// food selected )
				dbHelper.updateSettingsByName(DbSettings.setting_startUp, "1");
				startHomeActivityAndStopThisActivity();
			}
		} else {
			// mark app as startup ( to show dialog when we have already food
			// selected )
			dbHelper.updateSettingsByName(DbSettings.setting_startUp, "1");
			startHomeActivityAndStopThisActivity();
		}
		cSetting.close();
		dbHelper.close();
	}

	private void startActivityDBLanguage() {
		// start the set database language page
		Intent m = new Intent(this, ShowSettingsDBLanguage.class).putExtra(
				DataParser.whatToDo, DataParser.doFirstTime);
		startActivityForResult(m, requestCodeSelectedDBFood);
	}

	private void showPopUpDiabetesPatient() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					startIntentMealTimes();
					break; 
				default:
					startHomeActivityAndStopThisActivity();
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				getResources().getString(R.string.are_you_diabetes_patient))
				.setPositiveButton(getResources().getString(R.string.yes),
						dialogClickListener)
				.setNegativeButton(getResources().getString(R.string.no),
						dialogClickListener).show();
	}
 
	private void startIntentMealTimes(){
		Intent k = new Intent(this, ShowSettingsMealTimes.class);
		// mark the intent as first time
		k.putExtra(DataParser.whatToDo, DataParser.doFirstTime);
		startActivityForResult(k, requestCodeMealTimes);
	}
	
	private void startIntentInsulineRatios() {
		Intent k = new Intent(this, ShowSettingsInsulineRatio.class);
		// mark the intent as first time
		k.putExtra(DataParser.whatToDo, DataParser.doFirstTime);
		startActivityForResult(k, requestCodeKHRatio);
	}

	private void startHomeActivityAndStopThisActivity() {
		// start home activity
		Intent i = new Intent(this, ShowHomeTab.class);
		startActivity(i);

		// stop this activity
		finish();
	}
}
