package be.goossens.oracle;

/*
 * This class is uses to add food to the selected food list.
 * This class is also used to update a selected food.
 */

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ShowAddFoodToSelection extends Activity {
	private DbAdapter dbHelper;

	private TextView textViewSelectedFood;
	private TextView textViewSelectedFoodValues;
	private TextView textViewCalculated;
	private EditText editTextFoodAmound;
	private Spinner spinnerFoodUnits;
	private Button buttonAddOrUpdate;

	// To store the selected food in
	private Cursor foodCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_add_food);

		dbHelper = new DbAdapter(this);
		dbHelper.open();

		// Get the selected food
		foodCursor = dbHelper.fetchFood(getIntent().getExtras().getLong(
				DbAdapter.DATABASE_FOOD_ID));
		startManagingCursor(foodCursor);
		textViewSelectedFood = (TextView) findViewById(R.id.textViewSelectedFood);
		textViewSelectedFoodValues = (TextView) findViewById(R.id.textViewSelectedFoodValues);
		textViewCalculated = (TextView) findViewById(R.id.textViewCalculated);
		editTextFoodAmound = (EditText) findViewById(R.id.editTextFoodAmount);
		spinnerFoodUnits = (Spinner) findViewById(R.id.spinnerFoodUnit);
		buttonAddOrUpdate = (Button) findViewById(R.id.buttonAddOrUpdate);

		// update the textViews when the spinner selected item changes
		spinnerFoodUnits
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						fillTextViewSelectedFood();
						fillTextViewCalculated();
					}

					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		fillData();

		// after we filled the data in the spinner we have to check if we need
		// to update or create a selectedFood
		if (getIntent().getExtras().getLong("selectedfoodid") != 0) {
			Cursor cSelectedFood = dbHelper.fetchSelectedFood(getIntent()
					.getExtras().getLong("selectedfoodid"));
			startManagingCursor(cSelectedFood);
			// set text on button
			buttonAddOrUpdate
					.setText(getResources().getString(R.string.update));
			// set the amount we have in selectedFood
			editTextFoodAmound
					.setText(""
							+ cSelectedFood.getFloat(cSelectedFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT)));
			// set the selected item in the spinner = the unit from selectedFood
			setSelectedFoodUnitItemInSpinnerSelected(cSelectedFood
					.getInt(cSelectedFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)));
		}

		fillTextViewSelectedFood();
	}

	private void setSelectedFoodUnitItemInSpinnerSelected(int selectedFoodUnitId) {
		int position = 0;

		// First we get all the foodUnits from the spinner
		CustomSimpleCursorAdapterUnit adapter = (CustomSimpleCursorAdapterUnit) spinnerFoodUnits
				.getAdapter();
		Cursor cursorTest = adapter.getCursor();
		startManagingCursor(cursorTest);
		cursorTest.moveToFirst();
		startManagingCursor(cursorTest);

		// check the first one ( if the id is the same of the units )
		if (cursorTest.getInt(cursorTest
				.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)) == selectedFoodUnitId) {
			position = cursorTest.getPosition();
		}

		// move next and keep checking if the id is the same
		while (cursorTest.moveToNext()) {
			if (cursorTest.getInt(cursorTest
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)) == selectedFoodUnitId) {
				position = cursorTest.getPosition();
			}
		}

		// This will set the selected item = position that is given
		spinnerFoodUnits.setSelection(position);
	}

	// This method will fill the calculated textView ( its under the spinner )
	// This method is called when the spinner selected item changes or when the
	// amount in editText changes
	private void fillTextViewCalculated() {
		float amound = 0;

		if (editTextFoodAmound.getText().length() > 0) {
			// if we press in 0. then the app crashes so amound = 0 then
			try {
				amound = Float.parseFloat(editTextFoodAmound.getText()
						.toString());
			} catch (Exception e) {
				amound = 0;
			}
		}

		Cursor selectedFoodUnitCursor = dbHelper.fetchFoodUnit(spinnerFoodUnits
				.getSelectedItemId());
		startManagingCursor(selectedFoodUnitCursor);
		float hoeveelheidCal = amound
				* selectedFoodUnitCursor
						.getFloat(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL));

		float hoeveelheidCarb = amound
				* selectedFoodUnitCursor
						.getFloat(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_CARBS));

		float hoeveelheidProt = amound
				* selectedFoodUnitCursor
						.getFloat(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_PROTEIN));

		float hoeveelheidFat = amound
				* selectedFoodUnitCursor
						.getFloat(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FAT));

		textViewCalculated
				.setText(Math.round(amound
						* selectedFoodUnitCursor.getFloat(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)))
						+ " "
						+ selectedFoodUnitCursor.getString(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME))
						+ " "
						+ foodCursor.getString(foodCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME))
						+ "\n"
						+ hoeveelheidCal
						+ " "
						+ getResources().getString(R.string.amound_of_kcal)
						+ "\n"
						+ hoeveelheidCarb
						+ " "
						+ getResources().getString(R.string.amound_of_carbs)
						+ "\n"
						+ hoeveelheidProt
						+ " "
						+ getResources().getString(R.string.amound_of_protein)
						+ "\n"
						+ hoeveelheidFat
						+ " "
						+ getResources().getString(R.string.amound_of_fat));
	}

	// This method will fill the textView selectedFood and selectedFoodValues
	// ( its on top of the page )
	private void fillTextViewSelectedFood() {
		Cursor selectedFoodUnitCursor = dbHelper.fetchFoodUnit(spinnerFoodUnits
				.getSelectedItemId());
		startManagingCursor(selectedFoodUnitCursor);
		textViewSelectedFood
				.setText(selectedFoodUnitCursor.getString(selectedFoodUnitCursor
						.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT))
						+ " "
						+ selectedFoodUnitCursor.getString(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME))
						+ "\n "
						+ foodCursor.getString(foodCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME))
						+ " =");

		textViewSelectedFoodValues
				.setText("\n "
						+ selectedFoodUnitCursor.getString(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL))
						+ " "
						+ getResources().getString(R.string.amound_of_kcal)

						+ "\n "
						+ selectedFoodUnitCursor.getString(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_CARBS))
						+ " "
						+ getResources().getString(R.string.amound_of_carbs)

						+ "\n "
						+ selectedFoodUnitCursor.getString(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_PROTEIN))
						+ " "
						+ getResources().getString(R.string.amound_of_protein)

						+ "\n "
						+ selectedFoodUnitCursor.getString(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FAT))
						+ " "
						+ getResources().getString(R.string.amound_of_fat)

				);
	}

	private void fillData() {
		String[] from = new String[] {
				DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT,
				DbAdapter.DATABASE_FOODUNIT_NAME };
		int[] to = new int[] { R.id.spinnerPopupPartOne,
				R.id.spinnerPopupPartTwo };
		CustomSimpleCursorAdapterUnit adapter = new CustomSimpleCursorAdapterUnit(this,
				android.R.layout.simple_spinner_item,
				dbHelper.fetchFoodUnitByFoodId(getIntent().getExtras().getLong(
						DbAdapter.DATABASE_FOOD_ID)), from, to);
		spinnerFoodUnits.setAdapter(adapter);
		adapter.setDropDownViewResource(R.layout.row_add_food_spinner_popup);
	}

	// this is called every time a user presses a key
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// check if the user try's to add more then 999 as value
		// this is becaus if we try to add 999999 the application crash!
		int insertedAmound;

		try {
			insertedAmound = Integer.parseInt(editTextFoodAmound.getText()
					.toString());
		} catch (Exception e) {
			insertedAmound = 0;
		}

		if (insertedAmound > 999) {
			Toast.makeText(
					this,
					getResources().getString(
							R.string.value_cant_be_more_then_ninety_nine),
					Toast.LENGTH_SHORT).show();
			editTextFoodAmound.setText(editTextFoodAmound.getText()
					.subSequence(1, editTextFoodAmound.getText().length()));
		} else if (editTextFoodAmound.getText().toString().length() > 5) {
			Toast.makeText(
					this,
					getResources().getString(
							R.string.cant_add_more_then_five_digits),
					Toast.LENGTH_SHORT).show();
			editTextFoodAmound.setText(editTextFoodAmound.getText()
					.subSequence(1, editTextFoodAmound.getText().length()));
		} else {
			fillTextViewCalculated();
		}
		return super.dispatchKeyEvent(event);
	}

	// when pressed on the button Back
	public void onClickBack(View view) {
		setResult(RESULT_OK);
		finish();
	}

	// when pressed on button Add
	public void onClickButtonAddFood(View view) {
		// first try to get the amound as a float
		try {
			float amound = Float.parseFloat(editTextFoodAmound.getText()
					.toString());

			Cursor selectedFoodUnitCursor = dbHelper
					.fetchFoodUnit(spinnerFoodUnits.getSelectedItemId());

			startManagingCursor(selectedFoodUnitCursor);

			// check if we need to udpate or add new selectedFood
			if (getIntent().getExtras().getLong("selectedfoodid") != 0) {
				// update a selectedFood
				dbHelper.updateSelectedFood(
						getIntent().getExtras().getLong("selectedfoodid"),
						foodCursor.getString(foodCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)),
						selectedFoodUnitCursor.getString(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME)),
						selectedFoodUnitCursor.getFloat(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL)),
						Float.parseFloat(editTextFoodAmound.getText()
								.toString()),
						selectedFoodUnitCursor.getFloat(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)),
						selectedFoodUnitCursor.getFloat(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_PROTEIN)),
						selectedFoodUnitCursor.getFloat(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_CARBS)),
						selectedFoodUnitCursor.getFloat(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FAT)),
						foodCursor.getLong(foodCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
						selectedFoodUnitCursor.getFloat(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)));
			} else {
				// create a new selectedFood
				if (amound > 0) {
					dbHelper.createSelectedFood(
							foodCursor
									.getString(foodCursor
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)),
							selectedFoodUnitCursor.getString(selectedFoodUnitCursor
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME)),
							selectedFoodUnitCursor.getFloat(selectedFoodUnitCursor
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL)),
							Float.parseFloat(editTextFoodAmound.getText()
									.toString()),
							selectedFoodUnitCursor.getFloat(selectedFoodUnitCursor
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)),
							selectedFoodUnitCursor.getFloat(selectedFoodUnitCursor
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_PROTEIN)),
							selectedFoodUnitCursor.getFloat(selectedFoodUnitCursor
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_CARBS)),
							selectedFoodUnitCursor.getFloat(selectedFoodUnitCursor
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FAT)),
							foodCursor.getLong(foodCursor
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
							selectedFoodUnitCursor.getFloat(selectedFoodUnitCursor
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)));
				} else {
					returnMessageFoodAintAddedValueCantBeZero();
				}
			}
		} catch (Exception e) {
			// if we cant get the amound as a float return message
			returnMessageFoodAintAddedValueCantBeZero();
		}
		setResult(RESULT_OK);
		finish();
	}

	// when the amount is 0 and the user press on Add ( this popup will be show
	// )
	public void returnMessageFoodAintAddedValueCantBeZero() {
		Toast.makeText(
				this,
				getResources().getString(R.string.selected_food_aint_added)
						+ "\n "
						+ getResources().getString(
								R.string.value_amount_cant_be_zero),
				Toast.LENGTH_LONG).show();
	}
}
