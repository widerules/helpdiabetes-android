package be.goossens.oracle;

/*
 * This class is used to create a new unit for a food.
 * This class is also used to update a unit.
 */

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ShowCreateUnit extends Activity {
	private DbAdapter dbHelper;
	private long foodId;

	private EditText editTextStandardAmound;
	private EditText editTextName;
	private EditText editTextKcal;
	private EditText editTextProt;
	private EditText editTextCarbs;
	private EditText editTextFat;
	private Button buttonAddOrUpdate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_create_unit);
		dbHelper = new DbAdapter(this);
		dbHelper.open();
		foodId = getIntent().getExtras().getLong(DbAdapter.DATABASE_FOOD_ID);

		editTextStandardAmound = (EditText) findViewById(R.id.editTextShowCreateUnitFoodUnitStandardAmound);
		editTextName = (EditText) findViewById(R.id.editTextShowCreateUnitFoodUnitName);
		editTextKcal = (EditText) findViewById(R.id.editTextShowCreateUnitFoodUnitKcal);
		editTextProt = (EditText) findViewById(R.id.editTextShowCreateUnitFoodUnitProt);
		editTextCarbs = (EditText) findViewById(R.id.editTextShowCreateUnitFoodUnitCarbs);
		editTextFat = (EditText) findViewById(R.id.editTextShowCreateUnitFoodUnitFat);
		buttonAddOrUpdate = (Button) findViewById(R.id.buttonShowCreateUnitAddOrUpdate);

		// if we are trying to update a unit
		if (getIntent().getExtras().getLong("unitId") != 0) {
			// set the text on the button
			buttonAddOrUpdate
					.setText(getResources().getString(R.string.update));

			Cursor cUnit = dbHelper.fetchFoodUnit(getIntent().getExtras()
					.getLong("unitId"));
			startManagingCursor(cUnit);

			// set the values in the boxes
			editTextStandardAmound
					.setText(cUnit.getString(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)));
			editTextName.setText(cUnit.getString(cUnit
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME)));
			editTextKcal.setText(cUnit.getString(cUnit
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL)));
			editTextProt
					.setText(cUnit.getString(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_PROTEIN)));
			editTextCarbs.setText(cUnit.getString(cUnit
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_CARBS)));
			editTextFat.setText(cUnit.getString(cUnit
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FAT)));

		}
	}

	public void onClickAdd(View view) {
		// always check the values so they are not empty or 0
		if (checkValues()) {
			// if the intent unitId != 0 then we have to update a unit!
			if (getIntent().getExtras().getLong("unitId") != 0) {
				dbHelper.updateFoodUnit(
						getIntent().getExtras().getLong("unitId"), editTextName
								.getText().toString(), Float
								.parseFloat(editTextStandardAmound.getText()
										.toString()), Float
								.parseFloat(editTextKcal.getText().toString()),
						Float.parseFloat(editTextProt.getText().toString()),
						Float.parseFloat(editTextCarbs.getText().toString()),
						Float.parseFloat(editTextFat.getText().toString()));
			} else {
				// else we create a new unit
				dbHelper.createFoodUnit(foodId, editTextName.getText()
						.toString(), Float.parseFloat(editTextStandardAmound
						.getText().toString()), Float.parseFloat(editTextKcal
						.getText().toString()), Float.parseFloat(editTextProt
						.getText().toString()), Float.parseFloat(editTextCarbs
						.getText().toString()), Float.parseFloat(editTextFat
						.getText().toString()));
			}
			setResult(RESULT_OK);
			finish();
		}
	}

	private boolean checkValues() {
		if (editTextStandardAmound.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.unit_name_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (editTextName.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.unit_name_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (editTextKcal.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.kcal_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (editTextProt.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.prot_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (editTextCarbs.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.carbs_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (editTextFat.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.fat_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else {
			// if there is a value in everything ( check if the value aint '0'
			// or '0.'
			try {
				float unitStandardAmound = Float
						.parseFloat(editTextStandardAmound.getText().toString());
				if (unitStandardAmound <= 0) {
					Toast.makeText(
							this,
							getResources().getString(
									R.string.unit_name_is_required),
							Toast.LENGTH_LONG).show();
					return false;
				}
			} catch (Exception e) {
				Toast.makeText(
						this,
						getResources()
								.getString(R.string.unit_name_is_required),
						Toast.LENGTH_LONG).show();
				return false;
			}

			try {
				float unitKcal = Float.parseFloat(editTextKcal.getText()
						.toString());
				if (unitKcal <= 0) {
					Toast.makeText(
							this,
							getResources().getString(R.string.kcal_is_required),
							Toast.LENGTH_LONG).show();
					return false;
				}
			} catch (Exception e) {
				Toast.makeText(this,
						getResources().getString(R.string.kcal_is_required),
						Toast.LENGTH_LONG).show();
				return false;
			}

			try {
				float unitProt = Float.parseFloat(editTextProt.getText()
						.toString());
				if (unitProt <= 0) {
					Toast.makeText(
							this,
							getResources().getString(R.string.prot_is_required),
							Toast.LENGTH_LONG).show();
					return false;
				}
			} catch (Exception e) {
				Toast.makeText(this,
						getResources().getString(R.string.prot_is_required),
						Toast.LENGTH_LONG).show();
				return false;
			}

			try {
				float unitCarb = Float.parseFloat(editTextCarbs.getText()
						.toString());
				if (unitCarb <= 0) {
					Toast.makeText(
							this,
							getResources()
									.getString(R.string.carbs_is_required),
							Toast.LENGTH_LONG).show();
					return false;
				}
			} catch (Exception e) {
				Toast.makeText(this,
						getResources().getString(R.string.carbs_is_required),
						Toast.LENGTH_LONG).show();
				return false;
			}

			try {
				float unitFat = Float.parseFloat(editTextFat.getText()
						.toString());
				if (unitFat <= 0) {
					Toast.makeText(this,
							getResources().getString(R.string.fat_is_required),
							Toast.LENGTH_LONG).show();
					return false;
				}
			} catch (Exception e) {
				Toast.makeText(this,
						getResources().getString(R.string.fat_is_required),
						Toast.LENGTH_LONG).show();
				return false;
			}
			// if everything went OK we return true
			return true;
		}
	}

	public void onClickBack(View view) {
		setResult(RESULT_OK);
		finish();
	}

}
