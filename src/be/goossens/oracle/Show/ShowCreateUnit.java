package be.goossens.oracle.Show;

/*
 * This class is used to create a new unit for a food.
 * This class is also used to update a unit.
 */

import be.goossens.oracle.R;
import be.goossens.oracle.Rest.DbAdapter;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
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

	private Spinner spinnerUnit;
	private TableRow tableRowSpecialUnit;

	// Button to delete unit
	private Button buttonDelete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_create_unit);
		dbHelper = new DbAdapter(this);
		foodId = getIntent().getExtras().getLong(DbAdapter.DATABASE_FOOD_ID);

		editTextStandardAmound = (EditText) findViewById(R.id.editTextShowCreateUnitFoodUnitStandardAmound);
		editTextName = (EditText) findViewById(R.id.editTextShowCreateUnitFoodUnitName);
		editTextKcal = (EditText) findViewById(R.id.editTextShowCreateUnitFoodUnitKcal);
		editTextProt = (EditText) findViewById(R.id.editTextShowCreateUnitFoodUnitProt);
		editTextCarbs = (EditText) findViewById(R.id.editTextShowCreateUnitFoodUnitCarbs);
		editTextFat = (EditText) findViewById(R.id.editTextShowCreateUnitFoodUnitFat);
		buttonAddOrUpdate = (Button) findViewById(R.id.buttonShowCreateUnitAddOrUpdate);
		buttonDelete = (Button) findViewById(R.id.buttonShowCreateUnitDelete);

		spinnerUnit = (Spinner) findViewById(R.id.spinnerShowCreateUnit);
		tableRowSpecialUnit = (TableRow) findViewById(R.id.tableRowShowCreateUnitSpecialFoodUnit);

		spinnerUnit.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				hideOrShowSpecialFoodUnitTableRow();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		buttonDelete.setVisibility(View.GONE);

	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		fillSpinner();
		checkUpdateUnit();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	private void checkUpdateUnit() {
		// if we are trying to update a unit
		if (getIntent().getExtras().getLong("unitId") != 0) {
			// set the text on the button
			buttonAddOrUpdate
					.setText(getResources().getString(R.string.update));

			Cursor cUnit = dbHelper.fetchFoodUnit(getIntent().getExtras()
					.getLong("unitId"));
			startManagingCursor(cUnit);

			// set the right spinner item
			if (cUnit
					.getFloat(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)) == 100) {
				if (cUnit
						.getString(
								cUnit.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME))
						.equals(getResources().getString(R.string.gram)))
					spinnerUnit.setSelection(0);
				else if (cUnit
						.getString(
								cUnit.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME))
						.equals(getResources().getString(R.string.ml)))
					spinnerUnit.setSelection(1);
				else
					spinnerUnit.setSelection(spinnerUnit.getCount() - 1);
			} else {
				spinnerUnit.setSelection(spinnerUnit.getCount() - 1);
			}

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
			checkForButtonDelete();
		}
	}

	private void fillSpinner() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.standard_food_units,
				android.R.layout.simple_spinner_item);
		spinnerUnit.setAdapter(adapter);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	// This method will show the special food unit table row when the last
	// spinenr item is selected.
	// This method will also hide the special food unit table row when any other
	// spinner item is selected
	private void hideOrShowSpecialFoodUnitTableRow() {
		if (spinnerUnit.getCount() == spinnerUnit.getSelectedItemPosition() + 1) {
			tableRowSpecialUnit.setVisibility(View.VISIBLE);
		} else {
			tableRowSpecialUnit.setVisibility(View.GONE);
		}
	}

	// This method will check if we may delete the unit ( its its not in use )
	// Then we display the button delete
	private void checkForButtonDelete() {
		Cursor cFoodUnit = dbHelper.fetchFoodUnit(getIntent().getExtras()
				.getLong("unitId"));
		// check if foodUnit is in use in selectedFood
		if (dbHelper.fetchSelectedFoodByFoodUnitId(
				getIntent().getExtras().getLong("unitId")).getCount() <= 0) {
			// if foodUnit is not in use, check if the food has more then 1
			// foodUnit
			if (dbHelper
					.fetchFoodUnitByFoodId(
							cFoodUnit.getLong(cFoodUnit
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)))
					.getCount() > 1) {
				buttonDelete.setVisibility(View.VISIBLE);
			}
		}

	}

	public void onClickAdd(View view) {
		// always check the values so they are not empty or 0
		if (checkValues()) {
			float standardAmound = 0f;
			String unitName = "";

			if (spinnerUnit.getSelectedItemPosition() == 0) {
				standardAmound = 100f;
				unitName = getResources().getString(R.string.gram);
			} else if (spinnerUnit.getSelectedItemPosition() == 1) {
				standardAmound = 100f;
				unitName = getResources().getString(R.string.ml);
			} else {
				standardAmound = Float.parseFloat(editTextStandardAmound
						.getText().toString());
				unitName = editTextName.getText().toString();
			}

			// if the intent unitId != 0 then we have to update a unit!
			if (getIntent().getExtras().getLong("unitId") != 0) {
				dbHelper.updateFoodUnit(
						getIntent().getExtras().getLong("unitId"), unitName,
						standardAmound,
						Float.parseFloat(editTextCarbs.getText().toString()),
						Float.parseFloat(editTextProt.getText().toString()),
						Float.parseFloat(editTextFat.getText().toString()),
						Float.parseFloat(editTextKcal.getText().toString()));
			} else {

				// else we create a new unit
				dbHelper.createFoodUnit(foodId, unitName, standardAmound,
						Float.parseFloat(editTextCarbs.getText().toString()),
						Float.parseFloat(editTextProt.getText().toString()),
						Float.parseFloat(editTextFat.getText().toString()),
						Float.parseFloat(editTextKcal.getText().toString()));
			}
			setResult(RESULT_OK);
			finish();
		}
	}

	private boolean checkValues() {
		if (editTextKcal.getText().length() <= 0) {
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
		} else if (spinnerUnit.getCount() == spinnerUnit
				.getSelectedItemPosition() + 1) {
			// if we selected the last spinner option we have to check if
			// standardamount and unitname are filled in
			if (editTextName.getText().length() <= 0) {
				Toast.makeText(
						this,
						getResources()
								.getString(R.string.unit_name_is_required),
						Toast.LENGTH_LONG).show();
				return false;
			} else if (editTextStandardAmound.getText().length() <= 0) {
				Toast.makeText(
						this,
						getResources()
								.getString(R.string.unit_name_is_required),
						Toast.LENGTH_LONG).show();
				return false;
			}
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
		}
		// if everything went OK we return true
		return true;
	}

	public void onClickBack(View view) {
		setResult(RESULT_OK);
		finish();
	}

	// If we press the button delete
	public void onClickDelete(View view) {
		dbHelper.deleteFoodUnit(getIntent().getExtras().getLong("unitId"));
		finish();
	}

}
