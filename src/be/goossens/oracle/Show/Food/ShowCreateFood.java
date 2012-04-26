package be.goossens.oracle.Show.Food;

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
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.Custom.CustomArrayAdapterCharSequenceShowCreateFood;
import be.goossens.oracle.Objects.DBValueOrder;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.ValueOrderComparator;

public class ShowCreateFood extends Activity {
	private EditText editTextfoodName;
	private EditText editTextUnitStandardAmound;
	private EditText editTextUnitName;
	private Button btAdd;

	private List<TextView> tvList;
	private List<EditText> etList;

	private Spinner spinnerUnit;
	private TableRow tableRowSpecialFoodUnit;

	private DbAdapter dbHelper;

	private List<DBValueOrder> listValueOrders;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_create_food, null);
		setContentView(contentView);

		dbHelper = new DbAdapter(this);

		tvList = new ArrayList<TextView>();
		etList = new ArrayList<EditText>();

		spinnerUnit = (Spinner) findViewById(R.id.spinnerShowCreateFoodUnit);
		tableRowSpecialFoodUnit = (TableRow) findViewById(R.id.tableRowSpecialFoodUnit);
		editTextfoodName = (EditText) findViewById(R.id.editTextFoodName);
		editTextUnitStandardAmound = (EditText) findViewById(R.id.editTextFoodUnitStandardAmound);
		editTextUnitName = (EditText) findViewById(R.id.editTextFoodUnitName);
		btAdd = (Button) findViewById(R.id.buttonAdd);

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

		btAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickAdd(v);
			}
		});

		for (int i = 0; i < etList.size(); i++) {
			etList.get(i).setOnKeyListener(new OnKeyListener() {

				public boolean onKey(View v, int keyCode, KeyEvent event) {
					// filter so we only get the onkey up actions
					if (event.getAction() != KeyEvent.ACTION_DOWN) {
						// if the pressed key = enter we go to the next
						if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
							goToNextEditText();
						} 
					}
					//if we dont return false our numbers wont get in the edittext
					return false;
				}
			});
		}
	}

	public void goToNextEditText() {
		// change focus to next
		// but do size-1 so we dont try to change from edittext so one outside
		// the list that doesnt exists
		for (int i = 0; i < etList.size() - 1; i++) {
			if (this.getCurrentFocus() == etList.get(i)) {
				// set focus
				etList.get(i + 1).requestFocus();
				// stop this method
				return;
			}
		}
		// if we get here we clicked enter on the last edit text
		// if we do that we create the food and go back to the list
		onClickAdd(null);
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
		fillTextViewFoodName();
		fillListValueOrders();
		fillSpinner();
		fillTextViews();
	}

	private void fillTextViewFoodName() {
		editTextfoodName.setText(getIntent().getExtras().getString(
				DataParser.foodSearchValue));
	}

	private void fillTextViews() {
		for (int i = 0; i < listValueOrders.size(); i++) {
			tvList.get(i).setText(listValueOrders.get(i).getValueName());
		}
	}

	private void fillSpinner() {
		CustomArrayAdapterCharSequenceShowCreateFood adapter = new CustomArrayAdapterCharSequenceShowCreateFood(
				this,
				R.layout.custom_spinner_array_adapter_charsequence_show_create_food,
				getArrayList());
		spinnerUnit.setAdapter(adapter);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	private List<CharSequence> getArrayList() {
		List<CharSequence> value = new ArrayList<CharSequence>();
		String[] arr = getResources().getStringArray(
				R.array.standard_food_units);
		for (int i = 0; i < arr.length; i++) {
			value.add(arr[i]);
		}
		return value;
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	public void onClickAdd(View view) {
		dbHelper.open();
		if (checkAllFieldsGotSomeValue()) {
			float standardAmound = 0f;
			String unitName = "";
			float carbs = 0f;
			float kcal = 0f;
			float prot = 0f;
			float fat = 0f;

			long foodId = dbHelper.createFood(editTextfoodName.getText()
					.toString(),
					ActivityGroupMeal.group.getFoodData().foodLanguageID);

			// see what option we selected
			if (spinnerUnit.getSelectedItemPosition() == 0) {
				// if we selected '100 gram'
				standardAmound = 100f;
				unitName = getResources().getString(R.string.gram);
			} else if (spinnerUnit.getSelectedItemPosition() == 1) {
				// if we selected '100 ml'
				standardAmound = 100f;
				unitName = getResources().getString(R.string.ml);
			} else {
				// else get from the editText boxes
				standardAmound = Float.parseFloat(editTextUnitStandardAmound
						.getText().toString());
				unitName = editTextUnitName.getText().toString();
			}

			// fill the right unit values
			for (int i = 0; i < listValueOrders.size(); i++) {
				if (listValueOrders
						.get(i)
						.getSettingName()
						.equals(getResources().getString(
								R.string.setting_value_order_carb))) {
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
								R.string.setting_value_order_prot))) {
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
								R.string.setting_value_order_fat))) {
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
								R.string.setting_value_order_kcal))) {
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

			// set the foodID in the activitygroup so the showFoodList know we
			// have to update the list
			ActivityGroupMeal.group.newFoodID = foodId;

			// Go back to the previous screen
			ActivityGroupMeal.group.back();
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
			if (editTextUnitStandardAmound.getText().length() <= 0
					|| editTextUnitName.getText().length() <= 0) {
				Toast.makeText(
						this,
						getResources()
								.getString(R.string.unit_name_is_required),
						Toast.LENGTH_LONG).show();
				return false;
			}

			// and check if standardamount != 0
			float checkStandardAmount = 0;
			try {
				checkStandardAmount = Float
						.parseFloat(editTextUnitStandardAmound.getText()
								.toString());
			} catch (Exception e) {
				checkStandardAmount = 0;
			}
			if (checkStandardAmount == 0) {
				Toast.makeText(
						this,
						getResources().getString(
								R.string.unit_amount_cant_be_zero),
						Toast.LENGTH_LONG).show();
				return false;
			}
		}
		// if everything went OK we return true
		return true;
	}

	// This method will fill the list of DBValueOrders with the right values
	private void fillListValueOrders() {
		dbHelper.open();
		// make the list empty
		listValueOrders = new ArrayList<DBValueOrder>();

		// get all the value orders
		Cursor cSettingValueOrderProt = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.setting_value_order_prot));
		Cursor cSettingValueOrderCarb = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.setting_value_order_carb));
		Cursor cSettingValueOrderFat = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.setting_value_order_fat));
		Cursor cSettingValueOrderKcal = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.setting_value_order_kcal));

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
		// if we press the back key
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
			// return false so the keydown event from activitygroupmeal will get
			// called
			return false;
		else
			return super.onKeyDown(keyCode, event);
	}

}
