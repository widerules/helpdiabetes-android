package be.goossens.oracle;

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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ShowManageOwnFood extends ListActivity {
	private DbAdapter dbHelper;

	private static final int UPDATE_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_manage_own_food);
		dbHelper = new DbAdapter(this);
		registerForContextMenu(getListView());
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		// fill the listview with own created food
		fillData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	private void fillData() {
		Cursor cFood = dbHelper.fetchAllOwnCreatedFood();
		startManagingCursor(cFood);
		String[] name = new String[] { DbAdapter.DATABASE_FOOD_NAME };
		int[] id = new int[] { android.R.id.text1 };
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, cFood, name, id);
		setListAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, UPDATE_ID, 0, R.string.update);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, ShowUpdateOwnFood.class);
		i.putExtra(DbAdapter.DATABASE_FOOD_ID, id);
		startActivity(i);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case UPDATE_ID:
			Intent i = new Intent(this, ShowUpdateOwnFood.class);
			i.putExtra(DbAdapter.DATABASE_FOOD_ID, info.id);
			startActivity(i);
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
				// and refresh the list
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
		cFoodUnit.close();
		// Then we delete the food self
		dbHelper.deleteFood(id);
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

	// if we press on create new food
	public void onClickCreateNewFood(View view) {
		// Go to new page to create new food
		Intent i = new Intent(this, ShowCreateFood.class);
		startActivity(i);
	}
}
