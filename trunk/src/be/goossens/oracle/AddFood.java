package be.goossens.oracle;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddFood extends Activity {
	private DbAdapter dbHelper;

	private TextView textViewSelectedFood;
	private TextView textViewCalculated;
	private EditText editTextFoodAmound;
	private Spinner spinnerFoodUnits;
	private Cursor foodCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_food);

		dbHelper = new DbAdapter(this);
		dbHelper.open();

		Bundle extras = getIntent().getExtras();
		foodCursor = dbHelper.fetchFood(extras
				.getLong(DbAdapter.DATABASE_FOOD_ID));

		textViewSelectedFood = (TextView) findViewById(R.id.textViewSelectedFood);
		textViewCalculated = (TextView) findViewById(R.id.textViewCalculated);
		editTextFoodAmound = (EditText) findViewById(R.id.editTextFoodAmount);
		spinnerFoodUnits = (Spinner) findViewById(R.id.spinnerFoodUnit);

		getFoodUnits();
		setTextOnTextView();
		setTextOnCalculatedTextView();

		// update the textviewCalculated when the spinner selected item changes
		spinnerFoodUnits
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						setTextOnTextView();
						setTextOnCalculatedTextView();
					}

					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});
	}

	private void setTextOnTextView() {
		Cursor selectedFoodUnitCursor = dbHelper.fetchFoodUnit(spinnerFoodUnits
				.getSelectedItemId());
		textViewSelectedFood
				.setText(selectedFoodUnitCursor.getString(selectedFoodUnitCursor
						.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT))
						+ " "
						+ selectedFoodUnitCursor.getString(selectedFoodUnitCursor
						.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME))
						+ " "
						+ foodCursor.getString(foodCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME))
						+ " = "
						+ selectedFoodUnitCursor.getString(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL))
						+ " kcal.");
	}

	private void getFoodUnits() {
		Cursor foodUnitCursor = dbHelper.fetchFoodUnitByFoodId(getIntent()
				.getExtras().getLong(DbAdapter.DATABASE_FOOD_ID));
		startManagingCursor(foodUnitCursor);

		String[] name = new String[] { DbAdapter.DATABASE_FOODUNIT_NAME };
		int[] id = new int[] { android.R.id.text1 };

		SimpleCursorAdapter foodUnits = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, foodUnitCursor, name, id);
		spinnerFoodUnits.setAdapter(foodUnits);
		foodUnits
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	private void setTextOnCalculatedTextView() {
		// only update the textview calculated when the amount entered is bigger
		// then 0
		if (editTextFoodAmound.getText().length() > 0) {

			Cursor selectedFoodUnitCursor = dbHelper
					.fetchFoodUnit(spinnerFoodUnits.getSelectedItemId());

			Integer hoeveelheidCal = (Integer.parseInt(editTextFoodAmound
					.getText().toString()) * selectedFoodUnitCursor
					.getInt(selectedFoodUnitCursor
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL)));

			textViewCalculated
					.setText(editTextFoodAmound.getText()
							+ " x "
							+ selectedFoodUnitCursor.getString(selectedFoodUnitCursor
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT))
							+ " "
							+ selectedFoodUnitCursor.getString(selectedFoodUnitCursor
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME))
							+ " "
							+ foodCursor.getString(foodCursor
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME))
							+ "."
							+ "\n"
							+ editTextFoodAmound.getText()
							+ " x "
							+ selectedFoodUnitCursor.getString(selectedFoodUnitCursor
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL))
							+ " kcal = " + hoeveelheidCal + " kcal.");
		}
	}

	// On click button "Add"
	public void onClickButtonAddFood(View view) {
		try {
			Integer amound = Integer.parseInt(editTextFoodAmound.getText()
					.toString());
			if (amound > 0) {
				Cursor selectedFoodUnitCursor = dbHelper
						.fetchFoodUnit(spinnerFoodUnits.getSelectedItemId());

				dbHelper.createSelectedFood(
						foodCursor
								.getString(foodCursor
										.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)),
						selectedFoodUnitCursor.getString(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME)),
						selectedFoodUnitCursor.getInt(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL)),
						Integer.parseInt(editTextFoodAmound.getText()
								.toString()));
			} else {
				Toast.makeText(this,
						"Food aint added. \n Value amount cant be zero!",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(this,
					"Food aint added. \n Value amount cant be zero!",
					Toast.LENGTH_LONG).show();
		}
		setResult(RESULT_OK);
		finish();
	}

	// update the textViewCalculated when the amount changes
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// update the calculated field
		setTextOnCalculatedTextView();
		return super.dispatchKeyEvent(event);
	}

	public void onClickBack(View view) {
		setResult(RESULT_OK);
		finish();
	}

}
