package be.goossens.oracle.Show.Food;

/*
 * This class is used to create a new unit for a food.
 * This class is also used to update a unit.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import android.widget.TextView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.Objects.DBValueOrder;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.ValueOrderComparator;

public class ShowCreateUnit extends Activity {
	private DbAdapter dbHelper;
	private long foodId;

	private EditText editTextStandardAmound;
	private EditText editTextName;
	private Button buttonAddOrUpdate;

	private Spinner spinnerUnit;
	private TableRow tableRowSpecialUnit;

	private List<TextView> tvList;
	private List<EditText> etList;
	private List<DBValueOrder> listValueOrders;

	// Button to delete unit
	private Button buttonDelete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_create_unit);
		dbHelper = new DbAdapter(this);
		foodId = getIntent().getExtras().getLong(DbAdapter.DATABASE_FOOD_ID);

		tvList = new ArrayList<TextView>();
		etList = new ArrayList<EditText>();

		editTextStandardAmound = (EditText) findViewById(R.id.editTextShowCreateUnitFoodUnitStandardAmound);
		editTextName = (EditText) findViewById(R.id.editTextShowCreateUnitFoodUnitName);
		buttonAddOrUpdate = (Button) findViewById(R.id.buttonShowCreateUnitAddOrUpdate);
		buttonDelete = (Button) findViewById(R.id.buttonShowCreateUnitDelete);
		spinnerUnit = (Spinner) findViewById(R.id.spinnerShowCreateUnit);
		tableRowSpecialUnit = (TableRow) findViewById(R.id.tableRowShowCreateUnitSpecialFoodUnit);

		tvList.add((TextView) findViewById(R.id.textViewShowCreateUnit1));
		tvList.add((TextView) findViewById(R.id.textViewShowCreateUnit2));
		tvList.add((TextView) findViewById(R.id.textViewShowCreateUnit3));
		tvList.add((TextView) findViewById(R.id.textViewShowCreateUnit4));

		etList.add((EditText) findViewById(R.id.editTextShowCreateUnit1));
		etList.add((EditText) findViewById(R.id.editTextShowCreateUnit2));
		etList.add((EditText) findViewById(R.id.editTextShowCreateUnit3));
		etList.add((EditText) findViewById(R.id.editTextShowCreateUnit4));

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
		fillListValueOrders();
		fillTextViews();
		fillSpinner();
		checkUpdateUnit();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	private void fillTextViews() {
		for (int i = 0; i < listValueOrders.size(); i++) {
			tvList.get(i).setText(listValueOrders.get(i).getValueName());
		}
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

			editTextStandardAmound
					.setText(cUnit.getString(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)));
			editTextName.setText(cUnit.getString(cUnit
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME)));

			// set the values in the editTextBoxes
			for (int i = 0; i < listValueOrders.size(); i++) {
				if (listValueOrders
						.get(i)
						.getSettingName()
						.equals(getResources().getString(
								R.string.value_order_carb))) {
					etList.get(i)
							.setText(
									cUnit.getString(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_CARBS)));
				} else if (listValueOrders
						.get(i)
						.getSettingName()
						.equals(getResources().getString(
								R.string.value_order_prot))) {
					etList.get(i)
							.setText(
									cUnit.getString(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_PROTEIN)));
				} else if (listValueOrders
						.get(i)
						.getSettingName()
						.equals(getResources().getString(
								R.string.value_order_fat))) {
					etList.get(i)
							.setText(
									cUnit.getString(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FAT)));
				} else if (listValueOrders
						.get(i)
						.getSettingName()
						.equals(getResources().getString(
								R.string.value_order_kcal))) {
					etList.get(i)
							.setText(
									cUnit.getString(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL)));
				}
			}

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

			float carb = 0f;
			float prot = 0f;
			float fat = 0f;
			float kcal = 0f;

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

			// fill the unit values
			for (int i = 0; i < listValueOrders.size(); i++) {
				if (listValueOrders
						.get(i)
						.getSettingName()
						.equals(getResources().getString(
								R.string.value_order_carb))) {
					try {
						carb = Float.parseFloat(etList.get(i).getText()
								.toString());
					} catch (Exception e) {
						carb = 0f;
					}
				} else if (listValueOrders
						.get(i)
						.getSettingName()
						.equals(getResources().getString(
								R.string.value_order_prot))) {
					try {
						prot = Float.parseFloat(etList.get(i).getText()
								.toString());
					} catch (Exception e) {
						prot = 0f;
					}
				} else if (listValueOrders
						.get(i)
						.getSettingName()
						.equals(getResources().getString(
								R.string.value_order_fat))) {
					try {
						fat = Float.parseFloat(etList.get(i).getText()
								.toString());
					} catch (Exception e) {
						fat = 0f;
					}
				} else if (listValueOrders
						.get(i)
						.getSettingName()
						.equals(getResources().getString(
								R.string.value_order_kcal))) {
					try {
						kcal = Float.parseFloat(etList.get(i).getText()
								.toString());
					} catch (Exception e) {
						kcal = 0f;
					}
				}
			}

			// if the intent unitId != 0 then we have to update a unit!
			if (getIntent().getExtras().getLong("unitId") != 0) {
				dbHelper.updateFoodUnit(
						getIntent().getExtras().getLong("unitId"), unitName,
						standardAmound, carb, prot, fat, kcal);
			} else {

				// else we create a new unit
				dbHelper.createFoodUnit(foodId, unitName, standardAmound, carb,
						prot, fat, kcal);
			}
			setResult(RESULT_OK);
			finish();
		}
	}

	private boolean checkValues() {
		if (spinnerUnit.getCount() == spinnerUnit.getSelectedItemPosition() + 1) {
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
		}
		// if everything went OK we return true
		return true;
	}

	// If we press the button delete
	public void onClickDelete(View view) {
		dbHelper.deleteFoodUnit(getIntent().getExtras().getLong("unitId"));
		finish();
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

}
