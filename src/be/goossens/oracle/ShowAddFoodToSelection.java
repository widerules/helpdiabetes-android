package be.goossens.oracle;

/*
 * This class is uses to add food to the selected food list or update a selected food.
 * This class gets a parameter from its intent with the foodID
 */

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
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
	private SimpleCursorAdapter adapter;

	// The button to delete the food from selection when we come from
	// ShowSelectedFood page
	private Button buttonDeleteSelectedFood;

	// this boolean is needed to not set "" or standardamount in the
	// editTextFoodAmound on start if we come from showSelectedFood
	private boolean setStandardAmount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_add_food);

		dbHelper = new DbAdapter(this);

		setStandardAmount = true;

		textViewSelectedFood = (TextView) findViewById(R.id.textViewSelectedFood);
		textViewSelectedFoodValues = (TextView) findViewById(R.id.textViewSelectedFoodValues);
		textViewCalculated = (TextView) findViewById(R.id.textViewCalculated);
		editTextFoodAmound = (EditText) findViewById(R.id.editTextFoodAmount);
		spinnerFoodUnits = (Spinner) findViewById(R.id.spinnerFoodUnit);
		buttonAddOrUpdate = (Button) findViewById(R.id.buttonAddOrUpdate);
		buttonDeleteSelectedFood = (Button) findViewById(R.id.buttonShowAddFoodDelete);

		// Hide the button to delete the food from selectedFood
		buttonDeleteSelectedFood.setVisibility(View.GONE);

		// update the textViews when the spinner selected item changes
		spinnerFoodUnits
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (setStandardAmount) {
							checkStandardAmound();
							fillTextViewSelectedFood();
							fillTextViewCalculated();
						}

						// switch standardamount if its false
						if (!setStandardAmount)
							setStandardAmount = true;
					}

					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		/*
		 * Set a listener on the editTextFoodAmound so values can be updated
		 * when the amound changes
		 */
		editTextFoodAmound.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				onKeyPress();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void afterTextChanged(Editable s) {

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		dbHelper.open();

		// Get the selected food
		foodCursor = dbHelper.fetchFood(getIntent().getExtras().getLong(
				DataParser.idFood));
		startManagingCursor(foodCursor);

		fillData();

		checkStandardAmound();

		/*
		 * If we come from showSelectedFood we have to: 1. fill in the spinner
		 * with the right unit 2. set the right amount 3. Show the button to
		 * delete the food 4. set the text on the button from add to update
		 */
		if (getIntent().getExtras().getString(DataParser.fromWhereWeCome)
				.equals(DataParser.weComeFRomShowSelectedFood)) {
			Cursor cSelectedFood = dbHelper.fetchSelectedFood(getIntent()
					.getExtras().getLong(DataParser.idSelectedFood));
			startManagingCursor(cSelectedFood);
			// 1. fill in the spinner with the right unit
			setSelectedFoodUnitItemInSpinnerSelected(cSelectedFood
					.getInt(cSelectedFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)));
			// 2. set the right amount
			editTextFoodAmound
					.setText(""
							+ cSelectedFood.getFloat(cSelectedFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT)));
			// 3. show the button to delete the food
			buttonDeleteSelectedFood.setVisibility(View.VISIBLE);
			// 4. set the text "update" on the button
			buttonAddOrUpdate
					.setText(getResources().getString(R.string.update));
			// 5. setStandardAmount = false so that we see our food amount and
			// not "" or standardamount
			setStandardAmount = false;
			cSelectedFood.close();
		} else if (getIntent().getExtras()
				.getString(DataParser.fromWhereWeCome)
				.equals(DataParser.weComeFromShowFoodTemplates)) {
			// if we come from the page to load a template
			// 1. fill the spinner with the right unit
			setSelectedFoodUnitItemInSpinnerSelected(getIntent().getExtras()
					.getInt(DataParser.idUnit));
			// 2. set the righ amount in the editText
			if (getIntent().getExtras().getFloat(DataParser.foodAmount) > 0) {
				editTextFoodAmound
						.setText(""
								+ getIntent().getExtras().getFloat(
										DataParser.foodAmount));
			}
			// setStandardAmound = false so that we see our food amound and not
			// "" or standardAmount
			setStandardAmount = false;
		}

		fillTextViewSelectedFood();
		fillTextViewCalculated();
	}

	// When we click on the button delete
	public void onClickDeleteFoodFromSelection(View view) {
		dbHelper.deleteSelectedFood(getIntent().getExtras().getLong(
				DataParser.idSelectedFood));
		finish();
	}

	/*
	 * This function will always be executed when a user changes the value of
	 * the amound
	 */
	private void onKeyPress() {
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
	}

	/*
	 * This function wil set the amount = standardamount when standardamount !=
	 * 100 Else it wil set "" as amount
	 */
	private void checkStandardAmound() {
		Cursor cUnit = dbHelper.fetchFoodUnit(spinnerFoodUnits
				.getSelectedItemId());
		cUnit.moveToFirst();
		if (cUnit
				.getInt(cUnit
						.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)) != 100) {
			editTextFoodAmound
					.setText(cUnit.getString(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)));
		} else {
			editTextFoodAmound.setText("");
		}
		cUnit.close();
	}

	private void setSelectedFoodUnitItemInSpinnerSelected(int unitId) {
		int position = 0;
		Cursor cursorTemp = dbHelper.fetchFoodUnitByFoodId(getIntent()
				.getExtras().getLong(DataParser.idFood));
		startManagingCursor(cursorTemp);
		cursorTemp.moveToFirst();
		startManagingCursor(cursorTemp);

		// check the first one ( if the id is the same of the units )
		if (cursorTemp.getInt(cursorTemp
				.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)) == unitId) {
			position = cursorTemp.getPosition();
		}

		// move next and keep checking if the id is the same
		while (cursorTemp.moveToNext()) {
			if (cursorTemp.getInt(cursorTemp
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)) == unitId) {
				position = cursorTemp.getPosition();
			}
		}

		// This will set the selected item = position that is given
		spinnerFoodUnits.setSelection(position);
		cursorTemp.close();
	}

	// This method will fill the calculated textView ( its under the spinner )
	// This method is called when the spinner selected item changes or when the
	// amount in editText changes
	private void fillTextViewCalculated() {
		float amound = 0;

		// if we press in 0. then the app crashes so amound = 0 then
		try {
			amound = Float.parseFloat(editTextFoodAmound.getText().toString());
		} catch (Exception e) {
			amound = 0;
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

		// Update: when the unit = 100 gram we only display gram and do all the
		// calculations / 100
		if (selectedFoodUnitCursor
				.getInt(selectedFoodUnitCursor
						.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)) == 100) {
			hoeveelheidCal = hoeveelheidCal / 100;
			hoeveelheidCarb = hoeveelheidCarb / 100;
			hoeveelheidProt = hoeveelheidProt / 100;
			hoeveelheidFat = hoeveelheidFat / 100;
		}

		textViewCalculated
				.setText(amound
						+ " "
						+ selectedFoodUnitCursor.getString(selectedFoodUnitCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME))
						+ " "
						+ foodCursor.getString(foodCursor
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME))
						+ "\n" + hoeveelheidCal + " "
						+ getResources().getString(R.string.amound_of_kcal)
						+ "\n" + hoeveelheidCarb + " "
						+ getResources().getString(R.string.amound_of_carbs)
						+ "\n" + hoeveelheidProt + " "
						+ getResources().getString(R.string.amound_of_protein)
						+ "\n" + hoeveelheidFat + " "
						+ getResources().getString(R.string.amound_of_fat));
		selectedFoodUnitCursor.close();
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
		selectedFoodUnitCursor.close();
	}

	private void fillData() {

		adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item,
				dbHelper.fetchFoodUnitByFoodId(getIntent().getExtras().getLong(
						DataParser.idFood)),
				new String[] { DbAdapter.DATABASE_FOODUNIT_NAME },
				new int[] { android.R.id.text1 });

		spinnerFoodUnits.setAdapter(adapter);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

	}

	// when pressed on the button Back
	public void onClickBack(View view) {
		setResult(RESULT_OK);
		finish();
	}

	// when pressed on button Add
	public void onClickButtonAddFood(View view) {
		Cursor cSelectedFoodUnit = dbHelper.fetchFoodUnit(spinnerFoodUnits
				.getSelectedItemId());

		startManagingCursor(cSelectedFoodUnit);

		// check if we need to udpate or add new selectedFood
		// if we come from showSelectedFood we have to update the selectedFood
		if (getIntent().getExtras().getString(DataParser.fromWhereWeCome)
				.equals(DataParser.weComeFRomShowSelectedFood)) {
			// update a selectedFood
			dbHelper.updateSelectedFood(
					getIntent().getExtras().getLong(DataParser.idSelectedFood),
					Float.parseFloat(editTextFoodAmound.getText().toString()),
					cSelectedFoodUnit.getLong(cSelectedFoodUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)));
			cSelectedFoodUnit.close();
		} else {
			float amount = 0f;
			try{
				//when the editTextFoodAmound = ""; this wil go to the catch part
				amount = Float.parseFloat(editTextFoodAmound.getText().toString());
			}catch(Exception e){
				amount = 0f;
			}
			// create a new selectedFood
			dbHelper.createSelectedFood(
					amount,
					cSelectedFoodUnit.getLong(cSelectedFoodUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)));

			cSelectedFoodUnit.close();
		}

		setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void onStop() {
		foodCursor.close();
		adapter = null;
		dbHelper.close();
		super.onStop();
	}
}
