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
		dbHelper.open();
		foodId = getIntent().getExtras().getLong(DbAdapter.DATABASE_FOOD_ID);
		editTextFoodName = (EditText) findViewById(R.id.editTextShowUpdateOwnFoodFoodName);

		//fillData();
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
		fillData();
		super.onResume();
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case DELETE_ID:
			Cursor cFoodUnit = dbHelper.fetchFoodUnit(info.id);
			// check if food is in use
			if (dbHelper.fetchSelectedFoodByFoodUnitId(info.id).getCount() <= 0) {
				// if food is not in use, check if the food has more then 1 unit
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
		menu.add(0, EDIT_ID, 0, R.string.menu_edit);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

}
