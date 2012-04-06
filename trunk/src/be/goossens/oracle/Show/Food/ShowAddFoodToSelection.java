package be.goossens.oracle.Show.Food;

/*
 * This class is uses to add food to the selected food list or update a selected food.
 * This class gets a parameter from its intent with the foodID
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.Objects.DBValueOrder;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.ValueOrderComparator;

public class ShowAddFoodToSelection extends Activity {
	private DbAdapter dbHelper;

	private TextView textViewSelectedFood;
	private EditText editTextFoodAmound;
	private Spinner spinnerFoodUnits;
	private Button buttonAddOrUpdate,buttonDeleteSelectedFood;

	// The table textview
	private TextView textViewRowOneFieldOne, textViewRowOneFieldTwo,
			textViewRowOneFieldThree;

	// To store the selected food in
	private Cursor foodCursor;
	private SimpleCursorAdapter adapter;

	// this boolean is needed to not set "" or standardamount in the
	// editTextFoodAmound on start if we come from showSelectedFood
	private boolean setStandardAmount;

	// this boolean is needed to check it the user presses the first time on a
	// key in the amount field.
	// if so we first delete whats in the amount feel ( this way the user dont
	// have to delete it iself )
	private boolean firstKeyPress;

	private List<DBValueOrder> listValueOrders;
	private List<TextView> listTextViewColumnOne;
	private List<TextView> listTextViewColumnTwo;
	private List<TextView> listTextViewColumnThree;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_add_food, null);
		setContentView(contentView);

		dbHelper = new DbAdapter(this);

		setStandardAmount = true;
		firstKeyPress = true;

		listTextViewColumnOne = new ArrayList<TextView>();
		listTextViewColumnTwo = new ArrayList<TextView>();
		listTextViewColumnThree = new ArrayList<TextView>();

		textViewSelectedFood = (TextView) findViewById(R.id.textViewSelectedFood);
		editTextFoodAmound = (EditText) findViewById(R.id.editTextFoodAmount);
		spinnerFoodUnits = (Spinner) findViewById(R.id.spinnerFoodUnit);
		buttonAddOrUpdate = (Button) findViewById(R.id.buttonAddOrUpdate);
		buttonDeleteSelectedFood = (Button) findViewById(R.id.buttonShowAddFoodDelete);
		
		// fill the listTextViewColumn ONE
		listTextViewColumnOne
				.add((TextView) findViewById(R.id.textViewShowAddFoodRowTwoFieldOne));
		listTextViewColumnOne
				.add((TextView) findViewById(R.id.textViewShowAddFoodRowThreeFieldOne));
		listTextViewColumnOne
				.add((TextView) findViewById(R.id.textViewShowAddFoodRowFourFieldOne));
		listTextViewColumnOne
				.add((TextView) findViewById(R.id.textViewShowAddFoodRowFiveFieldOne));

		// fill the listTextViewColumn TWO
		listTextViewColumnTwo
				.add((TextView) findViewById(R.id.textViewShowAddFoodRowTwoFieldTwo));
		listTextViewColumnTwo
				.add((TextView) findViewById(R.id.textViewShowAddFoodRowThreeFieldTwo));
		listTextViewColumnTwo
				.add((TextView) findViewById(R.id.textViewShowAddFoodRowFourFieldTwo));
		listTextViewColumnTwo
				.add((TextView) findViewById(R.id.textViewShowAddFoodRowFiveFieldTwo));

		// fill the listTextViewColumn THREE
		listTextViewColumnThree
				.add((TextView) findViewById(R.id.textViewShowAddFoodRowTwoFieldThree));
		listTextViewColumnThree
				.add((TextView) findViewById(R.id.textViewShowAddFoodRowThreeFieldThree));
		listTextViewColumnThree
				.add((TextView) findViewById(R.id.textViewShowAddFoodRowFourFieldThree));
		listTextViewColumnThree
				.add((TextView) findViewById(R.id.textViewShowAddFoodRowFiveFieldThree));

		// gething the table textViews
		textViewRowOneFieldOne = (TextView) findViewById(R.id.textViewShowAddFoodRowOneFieldOne);
		textViewRowOneFieldTwo = (TextView) findViewById(R.id.textViewShowAddFoodRowOneFieldTwo);
		textViewRowOneFieldThree = (TextView) findViewById(R.id.textViewShowAddFoodRowOneFieldThree);

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
							firstKeyPress = true;
						}

						// switch standardamount if its false
						if (!setStandardAmount)
							setStandardAmount = true;
					}

					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		// set on click listener for buttonAddOrUpdate
		buttonAddOrUpdate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickButtonAddFood(v);
			}
		});
		
		buttonDeleteSelectedFood.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickDeleteFoodFromSelection(v);
			}
		});
	}

	// This will handle the editTextFoodAmunt
	// we cant handle this text with a editText.setOnTextListener becaus then we
	// would create a infinity loop with
	// editTextFoodAmound.setText("");
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (firstKeyPress) {
			firstKeyPress = false;
			editTextFoodAmound.setText("");
		}
		onKeyPress();
		return super.dispatchKeyEvent(event);
	}

	@Override
	protected void onResume() {
		// clear the editTextSearch from showFoodList
		ActivityGroupMeal.group.refreshShowFoodListEditTextSearch();

		super.onResume();

		dbHelper.open();

		fillListValueOrders();

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
				editTextFoodAmound.setText(""
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

		ActivityGroupMeal.group.back();
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
		// First get the amount inserted
		float amount = 0f;
		try {
			amount = Float.parseFloat(editTextFoodAmound.getText().toString());
		} catch (Exception e) {
			amount = 0f;
		}
		// calculate al the standard fields
		float calcCarbs = 0f;
		float calcKcal = 0f;
		float calcFat = 0f;
		float calcProtein = 0f;

		// without this try the app will crash when the device is turned.
		// reason: ( not 100% sure ) when the device turns this method is called
		// without data in the spinner so it wil crash at gething the unit.
		try {
			Cursor cUnit = dbHelper.fetchFoodUnit(spinnerFoodUnits
					.getSelectedItemId());
			cUnit.moveToFirst();

			calcCarbs = amount
					* cUnit.getFloat(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_CARBS));
			calcKcal = amount
					* cUnit.getFloat(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL));
			calcFat = amount
					* cUnit.getFloat(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FAT));
			calcProtein = amount
					* cUnit.getFloat(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_PROTEIN));

			// if standardamount == 100 we have to / 100 every calculated stuff
			if (cUnit
					.getFloat(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)) == 100) {
				calcCarbs = calcCarbs / 100;
				calcKcal = calcKcal / 100;
				calcFat = calcFat / 100;
				calcProtein = calcProtein / 100;
			}

			// Round the calculated floats
			float p = (float) Math.pow(10, 2);
			calcCarbs = Math.round(calcCarbs * p) / p;
			calcKcal = Math.round(calcKcal * p) / p;
			calcFat = Math.round(calcFat * p) / p;
			calcProtein = Math.round(calcProtein * p) / p;

			// Fill the table with the data
			// 1. Fill column 2 with the standard stuff
			String unitName = cUnit.getString(cUnit
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME));

			// if the text is to long we have to substring the text and add
			// "..." to it.
			// otherwise our last column wont be shown on the screen!
			if (unitName.length() > 10) {
				unitName = unitName.substring(0, 8) + "...";
			}

			// Fill the first row first field with unit name
			textViewRowOneFieldOne.setText(unitName);
			textViewRowOneFieldTwo
					.setText(cUnit.getString(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)));
			textViewRowOneFieldThree.setText("" + amount);

			// fill the rest of the table
			for (int i = 0; i < listValueOrders.size(); i++) {
				listTextViewColumnOne.get(i).setText(
						listValueOrders.get(i).getValueName());

				// display the carb
				if (listValueOrders
						.get(i)
						.getSettingName()
						.equals(getResources().getString(
								R.string.value_order_carb))) {
					listTextViewColumnTwo
							.get(i)
							.setText(
									cUnit.getString(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_CARBS)));
					listTextViewColumnThree.get(i).setText("" + calcCarbs);
				}

				// display the prot
				if (listValueOrders
						.get(i)
						.getSettingName()
						.equals(getResources().getString(
								R.string.value_order_prot))) {
					listTextViewColumnTwo
							.get(i)
							.setText(
									cUnit.getString(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_PROTEIN)));
					listTextViewColumnThree.get(i).setText("" + calcProtein);
				}

				// display the fat
				if (listValueOrders
						.get(i)
						.getSettingName()
						.equals(getResources().getString(
								R.string.value_order_fat))) {
					listTextViewColumnTwo
							.get(i)
							.setText(
									cUnit.getString(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FAT)));
					listTextViewColumnThree.get(i).setText("" + calcFat);
				}

				// display the kcal
				if (listValueOrders
						.get(i)
						.getSettingName()
						.equals(getResources().getString(
								R.string.value_order_kcal))) {
					listTextViewColumnTwo
							.get(i)
							.setText(
									cUnit.getString(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL)));
					listTextViewColumnThree.get(i).setText("" + calcKcal);
				}

			}

			cUnit.close();
		} catch (Exception e) {
		}
	}

	// This method will fill the textView selectedFood
	// ( its on top of the page )
	private void fillTextViewSelectedFood() {
		textViewSelectedFood.setText(foodCursor.getString(foodCursor
				.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)));
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
			try {
				// when the editTextFoodAmound = ""; this wil go to the catch
				// part
				amount = Float.parseFloat(editTextFoodAmound.getText()
						.toString());
			} catch (Exception e) {
				amount = 0f;
			}
			// create a new selectedFood
			dbHelper.createSelectedFood(
					amount,
					cSelectedFoodUnit.getLong(cSelectedFoodUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)));

			cSelectedFoodUnit.close();
		}

		ActivityGroupMeal.group.back();
		//refresh the page show selected food
		ActivityGroupMeal.group.refreshShowSelectedFood(1);
		//refresh button from show food list
		ActivityGroupMeal.group.refreshShowFoodListButtonSelections();
	}

	@Override
	protected void onStop() {
		foodCursor.close();
		adapter = null;
		dbHelper.close();
		super.onStop();
	}

	// This method will fill the list of DBValueOrders with the right values
	private void fillListValueOrders() {
		// make the list empty
		listValueOrders = new ArrayList<DBValueOrder>();

		// get all the value orders
		Cursor cSettingValueOrderProt = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.value_order_prot));
		Cursor cSettingValueOrderCarb = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.value_order_carb));
		Cursor cSettingValueOrderFat = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.value_order_fat));
		Cursor cSettingValueOrderKcal = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.value_order_kcal));

		// Move cursors to first object
		cSettingValueOrderProt.moveToFirst();
		cSettingValueOrderCarb.moveToFirst();
		cSettingValueOrderFat.moveToFirst();
		cSettingValueOrderKcal.moveToFirst();

		// Fill list
		listValueOrders
				.add(new DBValueOrder(
						cSettingValueOrderProt
								.getInt(cSettingValueOrderProt
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)),
						cSettingValueOrderProt.getString(cSettingValueOrderProt
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_NAME)),
						getResources().getString(R.string.amound_of_protein)));

		listValueOrders
				.add(new DBValueOrder(
						cSettingValueOrderCarb
								.getInt(cSettingValueOrderCarb
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)),
						cSettingValueOrderCarb.getString(cSettingValueOrderCarb
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_NAME)),
						getResources().getString(R.string.amound_of_carbs)));

		listValueOrders
				.add(new DBValueOrder(
						cSettingValueOrderFat
								.getInt(cSettingValueOrderFat
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)),
						cSettingValueOrderFat.getString(cSettingValueOrderFat
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_NAME)),
						getResources().getString(R.string.amound_of_fat)));

		listValueOrders
				.add(new DBValueOrder(
						cSettingValueOrderKcal
								.getInt(cSettingValueOrderKcal
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)),
						cSettingValueOrderKcal.getString(cSettingValueOrderKcal
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_NAME)),
						getResources().getString(R.string.amound_of_kcal)));

		// Close all the cursor
		cSettingValueOrderProt.close();
		cSettingValueOrderCarb.close();
		cSettingValueOrderFat.close();
		cSettingValueOrderKcal.close();

		// Sort the list on order
		ValueOrderComparator comparator = new ValueOrderComparator();
		Collections.sort(listValueOrders, comparator);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
}
