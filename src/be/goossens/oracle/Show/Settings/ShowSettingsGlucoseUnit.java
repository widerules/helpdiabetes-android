package be.goossens.oracle.Show.Settings;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupSettings;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowSettingsGlucoseUnit extends Activity {
	private DbAdapter dbHelper;
	private RadioGroup rgGlucoseUnit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_settings_glucose_unit, null);
		setContentView(contentView);

		dbHelper = new DbAdapter(this);
		rgGlucoseUnit = (RadioGroup) findViewById(R.id.radioGroupGlucoseUnit);
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		fillRadioGroup();

		//set on checked change listener after we filled the radio group!
		rgGlucoseUnit.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				onRadioGroupSelectedChange();
			}
		});
	}

	private void fillRadioGroup() {
		Cursor cGlucoseUnits = dbHelper.fetchAllBloodGlucoseUnits();
		Cursor cSettingGlucoseUnit = dbHelper.fetchSettingByName(getResources()
				.getString(R.string.setting_glucose_unit));
		cSettingGlucoseUnit.moveToFirst();

		if (cGlucoseUnits.getCount() > 0) {
			cGlucoseUnits.moveToFirst();
			do {
				RadioButton rb = new RadioButton(this);
				rb.setText(cGlucoseUnits.getString(cGlucoseUnits
						.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEUNIT_UNIT)));

				rb.setId(cGlucoseUnits.getInt(cGlucoseUnits
						.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEUNIT_ID)));

				rb.setTextColor(getResources().getColor(R.color.ColorText));
				
				if (cSettingGlucoseUnit
						.getLong(cSettingGlucoseUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)) == cGlucoseUnits
						.getLong(cGlucoseUnits
								.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEUNIT_ID))) {
					rb.setChecked(true);
				}

				rgGlucoseUnit.addView(rb);
			} while (cGlucoseUnits.moveToNext());
		}
		cSettingGlucoseUnit.close();
		cGlucoseUnits.close();
	}


	private void onRadioGroupSelectedChange() {
		// update the setting glucose unit
		dbHelper.updateSettingsByName(
				getResources().getString(R.string.setting_glucose_unit), ""
						+ rgGlucoseUnit.getCheckedRadioButtonId());

		// go back
		ActivityGroupSettings.group.back();
	}
	
	@Override
	protected void onPause() {
		dbHelper.close();
		super.onPause();
	}
}
