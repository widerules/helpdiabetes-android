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

public class ShowFoodTemplates extends ListActivity {
	private DbAdapter dbHelper;
	private Cursor cFoodTemplate;

	private static final int DELETE_ID = Menu.FIRST;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_food_templates);
		dbHelper = new DbAdapter(this);
		registerForContextMenu(getListView());
	}

	@Override
	protected void onPause() {
		super.onPause();
		cFoodTemplate.close();
		dbHelper.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		fillData();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case DELETE_ID:
			// delete all the template foods
			dbHelper.deleteTemplateFoodByFoodTemplateID(info.id);
			// delete all the food teplates
			dbHelper.deleteFoodTemplate(info.id);
			// refresh the data
			fillData();
			break;
		}
		return super.onContextItemSelected(item);
	}

	private void fillData() {
		cFoodTemplate = dbHelper.fetchAllFoodTemplates();
		String[] from = new String[] { DbAdapter.DATABASE_FOODTEMPLATE_FOODTEMPLATENAME };
		int[] to = new int[] { android.R.id.text1 };
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, cFoodTemplate, from, to);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// First put all the food in the selectedFood table
		Cursor cTemplateFood = dbHelper.fetchTemplateFoodsByFoodTemplateID(id);
		cTemplateFood.moveToFirst();

		do {
			Cursor cUnit = dbHelper
					.fetchFoodUnitByFoodId(cTemplateFood.getLong(cTemplateFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_TEMPLATEFOOD_FOODID)));
			cUnit.moveToFirst();
			dbHelper.createSelectedFood(0f, cUnit.getLong(cUnit
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)));
			cUnit.close();
		} while (cTemplateFood.moveToNext());

		cTemplateFood.close();
		// Then go back to the selected food page
		finish();
		Intent i = new Intent(this, ShowUpdateTemplateValues.class);
		startActivity(i);
	}

}
