package be.goossens.oracle.Show.Food;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.Custom.CustomBaseAdapterUnit;
import be.goossens.oracle.Objects.DBFoodUnit;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowUpdateOwnFood extends ListActivity {
	private DbAdapter dbHelper;
	private EditText editTextFoodName;
	private long foodId;
	private Button btDeleteFood, btAdd;

	private static final int EDIT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_update_own_food, null);
		setContentView(contentView);

		dbHelper = new DbAdapter(this);

		editTextFoodName = (EditText) findViewById(R.id.editTextShowUpdateOwnFoodFoodName);

		btDeleteFood = (Button) findViewById(R.id.buttonDelete);
		btAdd = (Button) findViewById(R.id.buttonAdd);

		editTextFoodName.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				saveFoodName();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});

		btDeleteFood.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickDelete(v);
			}
		});

		btAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickAddUnit(v);
			}
		});

		// fillData();
		registerForContextMenu(getListView());
	}

	// converts the cursor with food units to a arrayList<DBFoodunit>
	// and retturns that arrayList
	private ArrayList<DBFoodUnit> getFoodUnitsFromSelectedFood() {
		dbHelper.open();
		Cursor cFoodUnits = dbHelper.fetchFoodUnitByFoodId(foodId);
		startManagingCursor(cFoodUnits);
		ArrayList<DBFoodUnit> list = new ArrayList<DBFoodUnit>();
		do {
			DBFoodUnit unit = new DBFoodUnit();
			unit.setId(cFoodUnits.getInt(cFoodUnits
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)));
			unit.setStandardamound(cFoodUnits.getFloat(cFoodUnits
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)));
			unit.setName(cFoodUnits.getString(cFoodUnits
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME)));
			list.add(unit);
		} while (cFoodUnits.moveToNext());
		return list;
	}

	private void fillData() {
		dbHelper.open();
		// set the text on the editTextFoodName
		Cursor cFood = dbHelper.fetchFood(foodId);
		cFood.moveToFirst();
		editTextFoodName.setText(cFood.getString(cFood
				.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)));
		cFood.close();
		// fill the listview with all the units
		Cursor cSetting = dbHelper.fetchSettingByName(getResources().getString(
				R.string.setting_font_size));
		cSetting.moveToFirst();
		CustomBaseAdapterUnit adapter = new CustomBaseAdapterUnit(
				this,
				getFoodUnitsFromSelectedFood(),
				cSetting.getInt(cSetting
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		setListAdapter(adapter);
		cSetting.close();
	}

	// write the foodname to the database
	public void saveFoodName() {
		dbHelper.open();
		if (editTextFoodName.getText().length() > 0) {
			dbHelper.updateFoodName(foodId, editTextFoodName.getText()
					.toString());
		}
	}

	// on click add unit
	public void onClickAddUnit(View view) {
		Intent i = new Intent(this, ShowCreateUnit.class).addFlags(
				Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(
				DbAdapter.DATABASE_FOOD_ID, foodId);
		View v = ActivityGroupMeal.group.getLocalActivityManager()
				.startActivity(DataParser.activityIDShowFoodList, i).getDecorView();
		ActivityGroupMeal.group.setContentView(v);
	}

	@Override
	public void onResume() {  
		try {
			dbHelper.open();

			foodId = getIntent().getExtras()
					.getLong(DbAdapter.DATABASE_FOOD_ID);

			if (checkIfTheFoodIsInUse(foodId))
				btDeleteFood.setVisibility(View.GONE);

			fillData();
			super.onResume();
		} catch (Exception e) {
			ActivityGroupMeal.group.back();
		}
	}

	@Override
	protected void onPause() {
		dbHelper.close();
		super.onPause();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(this, ShowCreateUnit.class).addFlags(
				Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("unitId", id);
		View view = ActivityGroupMeal.group.getLocalActivityManager()
				.startActivity(DataParser.activityIDShowFoodList, i).getDecorView();
		ActivityGroupMeal.group.setContentView(view);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		dbHelper.open();
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		// delete the selected unit ID = info.id
		case DELETE_ID:
			Cursor cFoodUnit = dbHelper.fetchFoodUnit(info.id);
			// check if foodUnit is in use in selectedFood
			if (dbHelper.fetchSelectedFoodByFoodUnitId(info.id).getCount() <= 0) {
				// if foodUnit is not in use, check if the food has more then 1
				// foodUnit
				if (dbHelper
						.fetchFoodUnitByFoodId(
								cFoodUnit.getLong(cFoodUnit
										.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)))
						.getCount() > 1) {
					dbHelper.deleteFoodUnit(info.id);
					fillData();
				} else {
					Toast.makeText(
							this,
							getResources()
									.getString(
											R.string.cant_delete_food_unit_caus_food_unit_is_last_one),
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(
						this,
						getResources()
								.getString(
										R.string.cant_delete_food_unit_caus_food_unit_is_in_use),
						Toast.LENGTH_LONG).show();
			}
			break;

		case EDIT_ID:
			Intent i = new Intent(this, ShowCreateUnit.class);
			i.putExtra("unitId", info.id);
			startActivity(i);
			break;
		}
		return super.onContextItemSelected(item);
	}

	// Create context menu ( long pressed on item in listview )
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, EDIT_ID, 0, R.string.update);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	// If we click on the Delete button
	public void onClickDelete(View view) {
		// if the food is in use we cant delete it!
		if (checkIfTheFoodIsInUse(foodId)) {
			Toast.makeText(
					this,
					getResources().getString(
							R.string.cant_delete_food_caus_food_is_in_use),
					Toast.LENGTH_LONG).show();
		} else { 
			// else we can delete it
			deleteFoodAndFoodUnits(foodId);
			
			//delete the fooditem from the showfoodlist page
			ActivityGroupMeal.group.deleteFoodIDFromList = foodId;
			
			// and go back to foodlist
			ActivityGroupMeal.group.back();
		}
	}

	// Check if the food is in use
	private boolean checkIfTheFoodIsInUse(long foodId) {
		dbHelper.open();
		int count = 0;
		// first get all selectedFood to see if the food is in use in the
		// selectedFood table
		Cursor cSelectedFood = dbHelper.fetchAllSelectedFood();
		if (cSelectedFood.getCount() > 0) {
			cSelectedFood.moveToFirst();

			do {
				// get the foodUnit from the selectedFood
				Cursor cFoodUnit = dbHelper
						.fetchFoodUnit(cSelectedFood.getLong(cSelectedFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)));
				cFoodUnit.moveToFirst();
				if (cFoodUnit.getCount() > 0) {
					if (cFoodUnit
							.getLong(cFoodUnit
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)) == foodId)
						count++;
				}
				cFoodUnit.close();
			} while (cSelectedFood.moveToNext() && count == 0);

			cSelectedFood.close();
		}
		if (count == 0) {
			// if the food still isnt in the selectedFood table see if its in a
			// template
			// get all templates
			Cursor cTemplateFood = dbHelper.fetchAllTemplateFoods();
			if (cTemplateFood.getCount() > 0) {
				cTemplateFood.moveToFirst();
				// see if the foodId is the same
				do {
					// get the foodUnit from the foodTemplate
					Cursor cFoodUnit = dbHelper
							.fetchFoodUnit(cTemplateFood.getLong(cTemplateFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_TEMPLATEFOOD_UNITID)));
					cFoodUnit.moveToFirst();
					// check if foodID is the same
					if (cFoodUnit.getCount() > 0)
						if (cFoodUnit
								.getLong(cFoodUnit
										.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)) == foodId)
							count++;
					cFoodUnit.close();
				} while (cTemplateFood.moveToNext() && count == 0);
				cTemplateFood.close();
			}
		}
		return count > 0;
	}

	// Delete the foodUnits and the food
	private void deleteFoodAndFoodUnits(long id) {
		dbHelper.open();
		// First we delete all foodUnits from the food we want to delete
		Cursor cFoodUnit = dbHelper.fetchFoodUnitByFoodId(id);
		startManagingCursor(cFoodUnit);
		if (cFoodUnit != null) {
			// delete first foodUnit
			dbHelper.deleteFoodUnit(cFoodUnit.getInt(cFoodUnit
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)));
			// delete the other foodUnits
			while (cFoodUnit.moveToNext()) {
				dbHelper.deleteFoodUnit(cFoodUnit.getInt(cFoodUnit
						.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)));
			}
		}
		cFoodUnit.close();
		// Then we delete the food self
		dbHelper.deleteFood(id);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//if we press the back key
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
			//return false so the keydown event from activitygroupmeal will get called
			return false;
		else
			return super.onKeyDown(keyCode, event);
	}
}
