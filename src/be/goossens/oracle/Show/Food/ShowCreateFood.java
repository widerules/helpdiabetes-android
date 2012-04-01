package be.goossens.oracle.Show.Food;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.Objects.DBValueOrder;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.ValueOrderComparator;

public class ShowCreateFood extends Activity {
	private EditText editTextfoodName;
	private EditText editTextUnitStandardAmound;
	private EditText editTextUnitName;

	private List<TextView> tvList;
	private List<EditText> etList;

	private Spinner spinnerUnit;
	private TableRow tableRowSpecialFoodUnit;

	private DbAdapter dbHelper;

	private List<DBValueOrder> listValueOrders;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_create_food);

		dbHelper = new DbAdapter(this);

		tvList = new ArrayList<TextView>();
		etList = new ArrayList<EditText>();

		spinnerUnit = (Spinner) findViewById(R.id.spinnerShowCreateFoodUnit);
		tableRowSpecialFoodUnit = (TableRow) findViewById(R.id.tableRowSpecialFoodUnit);
		editTextfoodName = (EditText) findViewById(R.id.editTextFoodName);
		editTextUnitStandardAmound = (EditText) findViewById(R.id.editTextFoodUnitStandardAmound);
		editTextUnitName = (EditText) findViewById(R.id.editTextFoodUnitName);

		tvList.add((TextView) findViewById(R.id.textViewShowCreateFood1));
		tvList.add((TextView) findViewById(R.id.textViewShowCreateFood2));
		tvList.add((TextView) findViewById(R.id.textViewShowCreateFood3));
		tvList.add((TextView) findViewById(R.id.textViewShowCreateFood4));

		etList.add((EditText) findViewById(R.id.editTextShowCreateFood1));
		etList.add((EditText) findViewById(R.id.editTextShowCreateFood2));
		etList.add((EditText) findViewById(R.id.editTextShowCreateFood3));
		etList.add((EditText) findViewById(R.id.editTextShowCreateFood4));

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
		fillListValueOrders();
		fillSpinner();
		fillTextViews();
	}

	private void fillTextViews() {
		for (int i = 0; i < listValueOrders.size(); i++) {
			tvList.get(i).setText(listValueOrders.get(i).getValueName());
		}
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
			float standardAmound = 0f;
			String unitName = "";
			float carbs = 0f;
			float kcal = 0f;
			float prot = 0f;
			float fat = 0f;

			long foodId = dbHelper.createFood(editTextfoodName.getText()
					.toString());
			// see what option we selected
			if (spinnerUnit.getSelectedItemPosition() == 0) {
				// if we selected '100 gram'
				standardAmound = 100f;
				unitName = "gram";
			} else if (spinnerUnit.getSelectedItemPosition() == 1) {
				// if we selected '100 ml'
				standardAmound = 100f;
				unitName = "ml";
			} else {
				// else get from the editText boxes
				standardAmound = Float.parseFloat(editTextUnitStandardAmound
						.getText().toString());
				unitName = editTextUnitName.getText().toString();
			}

			//fill the right unit values
			for (int i = 0; i < listValueOrders.size(); i++) {
				if (listValueOrders
						.get(i)
						.getSettingName()
						.equals(getResources().getString(
								R.string.value_order_carb))) {
					try {
						carbs = Float.parseFloat(etList.get(i).getText()
								.toString());
					} catch (Exception e) {
						carbs = 0f;
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

			dbHelper.createFoodUnit(foodId, unitName, standardAmound, carbs,
					prot, fat, kcal);

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
