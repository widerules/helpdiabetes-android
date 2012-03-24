package be.goossens.oracle;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ShowUpdateOwnFood extends ListActivity {
	private DbAdapter dbHelper;
	private EditText editTextFoodName;
	private long foodId;

	private static final int EDIT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_update_own_food);

		dbHelper = new DbAdapter(this);
		foodId = getIntent().getExtras().getLong(DbAdapter.DATABASE_FOOD_ID);
		editTextFoodName = (EditText) findViewById(R.id.editTextShowUpdateOwnFoodFoodName);

		// fillData();
		registerForContextMenu(getListView());
	}

	// converts the cursor with food units to a arrayList<DBFoodunit>
	// and retturns that arrayList
	private ArrayList<DBFoodUnit> getFoodUnitsFromSelectedFood() {
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
		// set the text on the editTextFoodName
		Cursor cFood = dbHelper.fetchFood(foodId);
		startManagingCursor(cFood);
		editTextFoodName.setText(cFood.getString(cFood
				.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)));
		cFood.close();
		// fill the listview with all the units
		CustomBaseAdapterUnit adapter = new CustomBaseAdapterUnit(this,
				getFoodUnitsFromSelectedFood());
		setListAdapter(adapter);
	}

	// On click save food name
	public void onClickSaveFoodName(View view) {
		// update food name
		if (editTextFoodName.getText().length() > 0) {
			dbHelper.updateFoodName(foodId, editTextFoodName.getText()
					.toString());
			Toast.makeText(
					this,
					getResources().getString(
							R.string.food_name_succesfull_updated),
					Toast.LENGTH_LONG).show();
		} else {
			// return message food name cant be empty
			Toast.makeText(this,
					getResources().getString(R.string.food_name_is_required),
					Toast.LENGTH_LONG).show();
		}
	}

	// on click add unit
	public void onClickAddUnit(View view) {
		// Go to the unit add page
		Intent i = new Intent(this, ShowCreateUnit.class);
		i.putExtra(DbAdapter.DATABASE_FOOD_ID, foodId);
		startActivity(i);
	}

	@Override
	protected void onResume() {
		dbHelper.open();
		fillData();
		super.onResume();
	}

	@Override
	protected void onPause() {
		dbHelper.close();
		super.onPause();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, ShowCreateUnit.class);
		i.putExtra("unitId", id);
		startActivity(i);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
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
			// and go back to foodlist
			finish();
		}
	}

	// Check if the food is in use
	private boolean checkIfTheFoodIsInUse(long foodId) {
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
				if (cFoodUnit
						.getLong(cFoodUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)) == foodId) {
					count++;
				}
				cFoodUnit.close();
			} while (cSelectedFood.moveToNext() && count == 0);

			cSelectedFood.close();
		}
		if (count == 0) {
			// Then see if the food is in use in the template_food table
			Cursor cTemplateFood = dbHelper.fetchAllTemplateFoods();
			if (cTemplateFood.getCount() > 0) {
				cTemplateFood.moveToFirst();
				// see if the foodId is the same
				do {
					// check if foodID is the same
					if (foodId == cTemplateFood
							.getLong(cTemplateFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_TEMPLATEFOOD_FOODID)))
						count++;
				} while (cTemplateFood.moveToNext() && count == 0);
				cTemplateFood.close();
			}
		}
		return count > 0;
	}

	// Delete the foodUnits and the food
	private void deleteFoodAndFoodUnits(long id) {
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

}
