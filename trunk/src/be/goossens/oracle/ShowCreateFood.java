package be.goossens.oracle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

public class ShowCreateFood extends Activity {
	private EditText editTextfoodName;
	private EditText editTextUnitStandardAmound;
	private EditText editTextUnitName;
	private EditText editTextUnitKcal;
	private EditText editTextUnitCarbs;
	private EditText editTextUnitProt;
	private EditText editTextUnitFat;
	private Spinner spinnerUnit;
	private TableRow tableRowSpecialFoodUnit;

	DbAdapter dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_create_food);

		dbHelper = new DbAdapter(this);
		spinnerUnit = (Spinner) findViewById(R.id.spinnerShowCreateFoodUnit);
		tableRowSpecialFoodUnit = (TableRow) findViewById(R.id.tableRowSpecialFoodUnit);
		editTextfoodName = (EditText) findViewById(R.id.editTextFoodName);
		editTextUnitStandardAmound = (EditText) findViewById(R.id.editTextFoodUnitStandardAmound);
		editTextUnitName = (EditText) findViewById(R.id.editTextFoodUnitName);
		editTextUnitKcal = (EditText) findViewById(R.id.editTextFoodUnitKcal);
		editTextUnitCarbs = (EditText) findViewById(R.id.editTextFoodUnitCarbs);
		editTextUnitProt = (EditText) findViewById(R.id.editTextFoodUnitProt);
		editTextUnitFat = (EditText) findViewById(R.id.editTextFoodUnitFat);

		spinnerUnit.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// hide or show the tableRowSpecialFoodUnit
				hideOrShowSpecialFoodUnitTableRow();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	// This method will show the special food unit table row when the last
	// spinner item is selected.
	// This method will also hide the special food unit table row when any other
	// spinner item is selected.
	private void hideOrShowSpecialFoodUnitTableRow() {
		if (spinnerUnit.getCount() == spinnerUnit.getSelectedItemPosition() + 1) {
			// Show the row
			tableRowSpecialFoodUnit.setVisibility(View.VISIBLE);
		} else {
			// Hide the row
			tableRowSpecialFoodUnit.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		fillSpinner();
	}

	private void fillSpinner() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.standard_food_units,
				android.R.layout.simple_spinner_item);
		spinnerUnit.setAdapter(adapter);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	public void onClickAdd(View view) {
		if (checkAllFieldsGotSomeValue()) {
			long foodId = dbHelper.createFood(editTextfoodName.getText()
					.toString());
			float standardAmound = 0f;
			String unitName = "";
			//see what option we selected
			if(spinnerUnit.getSelectedItemPosition() == 0){
				//if we selected '100 gram'
				standardAmound = 100f;
				unitName = "gram";
			} else if(spinnerUnit.getSelectedItemPosition() == 1){
				//if we selected '100 ml'
				standardAmound = 100f;
				unitName = "ml";
			} else {
				//else get from the editText boxes
				standardAmound = Float.parseFloat(editTextUnitStandardAmound.getText().toString());
				unitName = editTextUnitName.getText().toString();
			}
			 
			dbHelper.createFoodUnit(foodId, unitName, standardAmound,
					Float.parseFloat(editTextUnitKcal.getText().toString()),
					Float.parseFloat(editTextUnitProt.getText().toString()),
					Float.parseFloat(editTextUnitCarbs.getText().toString()),
					Float.parseFloat(editTextUnitFat.getText().toString()));

			setResult(RESULT_OK);
			finish();
		}
	}

	private boolean checkAllFieldsGotSomeValue() {
		if (editTextfoodName.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.food_name_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (editTextUnitKcal.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.kcal_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (editTextUnitProt.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.prot_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (editTextUnitCarbs.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.carbs_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (editTextUnitFat.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.fat_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (spinnerUnit.getCount() == spinnerUnit
				.getSelectedItemPosition() + 1) {
			// if we selected the last spinner option we have to check if
			// standardamount and unitName are filled in
			if (editTextUnitStandardAmound.getText().length() <= 0) {
				Toast.makeText(
						this,
						getResources()
								.getString(R.string.food_name_is_required),
						Toast.LENGTH_LONG).show();
				return false;
			} else if (editTextUnitName.getText().length() <= 0) {
				Toast.makeText(
						this,
						getResources()
								.getString(R.string.unit_name_is_required),
						Toast.LENGTH_LONG).show();
				return false;
			}
		}
		// if everything went OK we return true
		return true;
	}

	public void onClickBack(View view) {
		finish();
	}
}
