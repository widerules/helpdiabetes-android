package be.goossens.oracle;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ShowUpdateOwnFood extends ListActivity {
	private DbAdapter dbHelper;

	private static final int UPDATE_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_update_own_food);
		dbHelper = new DbAdapter(this);
		dbHelper.open();
		// fill the listview with own created food
		fillData();
		registerForContextMenu(getListView());
	}

	private void fillData() {
		Cursor foodCursor = dbHelper.fetchAllOwnCreatedFood();
		startManagingCursor(foodCursor);
		String[] name = new String[] { DbAdapter.DATABASE_FOOD_NAME };
		int[] id = new int[] { android.R.id.text1 };
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, foodCursor, name, id);
		setListAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, UPDATE_ID, 0, R.string.menu_edit);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case UPDATE_ID:
			break;
		case DELETE_ID:
			// if the food is in use we cant delete it!
			if (checkIfTheFoodIsInUse(info.id)) {
				Toast.makeText(
						this,
						getResources().getString(
								R.string.cant_delete_food_caus_food_is_in_use),
						Toast.LENGTH_LONG).show();
			} else {
				// else we can delete it
				deleteFoodAndFoodUnits(info.id);
				//and refresh the list
				fillData();
			}
			break;
		}
		return super.onContextItemSelected(item);
	}

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
		// Then we delete the food self
		dbHelper.deleteFood(id);
	}

	private boolean checkIfTheFoodIsInUse(long foodId) {
		return dbHelper.fetchSelectedFoodByFoodId(foodId).getCount() > 0;
	}

	// if we press on create new food
	public void onClickCreateNewFood(View view) {
		//Go to new page to create new food
	}
}
