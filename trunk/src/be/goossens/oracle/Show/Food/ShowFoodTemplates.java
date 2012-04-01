package be.goossens.oracle.Show.Food;

import java.util.ArrayList;

import be.goossens.oracle.R;
import be.goossens.oracle.Custom.CustomBaseAdapter;
import be.goossens.oracle.Objects.DBFood;
import be.goossens.oracle.Objects.DBFoodTemplate;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;

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

public class ShowFoodTemplates extends ListActivity {
	private DbAdapter dbHelper;

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
			// if it was the last template we go back to the
			// showSelectedFoodPage
			if (dbHelper.fetchAllFoodTemplates().getCount() > 0) {
				// refresh the data
				fillData();
			} else {
				finish();
			}
			break;
		}
		return super.onContextItemSelected(item);
	}

	private void fillData() {  
		if (dbHelper.fetchAllFoodTemplates().getCount() > 0) {
			Cursor cSettings = dbHelper.fetchSettingByName(getResources().getString(R.string.font_size));
			cSettings.moveToFirst();
			CustomBaseAdapter adapter = new CustomBaseAdapter(this,
					getFoodTemplates(),cSettings.getInt(cSettings.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
			setListAdapter(adapter);
			cSettings.close();
		} else {
			setListAdapter(null);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// First put all the food in the selectedFood table
		Cursor cTemplateFood = dbHelper.fetchTemplateFoodsByFoodTemplateID(id);
		cTemplateFood.moveToFirst();
		Cursor cUnit = null;
		do {
			// start a new activity for every food row in the template
			Intent i = new Intent(this, ShowAddFoodToSelection.class);
			i.putExtra(DataParser.fromWhereWeCome, DataParser.weComeFromShowFoodTemplates);
			
			cUnit = dbHelper.fetchFoodUnit(cTemplateFood.getLong(cTemplateFood.getColumnIndexOrThrow(DbAdapter.DATABASE_TEMPLATEFOOD_UNITID)));
			cUnit.moveToFirst();
			i.putExtra(DataParser.idFood,cUnit.getLong(cUnit.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)));
			i.putExtra(DataParser.idUnit, cUnit.getInt(cUnit.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)));
			i.putExtra(DataParser.foodAmount, cTemplateFood.getFloat(cTemplateFood.getColumnIndexOrThrow(DbAdapter.DATABASE_TEMPLATEFOOD_AMOUNT)));
			startActivity(i);
		} while (cTemplateFood.moveToNext());
		cUnit.close();
		cTemplateFood.close();
		// Then go back to the selected food page
		finish();
	}

	// converts the cursor with all food templates to a arrayList
	// and returns that array list
	private ArrayList<DBFoodTemplate> getFoodTemplates() {
		ArrayList<DBFoodTemplate> list = new ArrayList<DBFoodTemplate>();
		Cursor cFoodTemplates = dbHelper.fetchAllFoodTemplates();
		cFoodTemplates.moveToFirst();
		
		do {

			// first create a arrayList with the food
			ArrayList<DBFood> foods = new ArrayList<DBFood>();

			Cursor cTemplateFood = dbHelper
					.fetchTemplateFoodsByFoodTemplateID(cFoodTemplates.getLong(cFoodTemplates
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODTEMPLATE_ID)));
			cTemplateFood.moveToFirst();
			do {
				Cursor cUnit = dbHelper.fetchFoodUnit(cTemplateFood.getLong(cTemplateFood.getColumnIndexOrThrow(DbAdapter.DATABASE_TEMPLATEFOOD_UNITID)));
				
				cUnit.moveToFirst();
				Cursor cFood = dbHelper
						.fetchFood(cUnit.getLong(cUnit.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)));
				cFood.moveToFirst();
				foods.add(new DBFood(
						cFood.getInt(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
						cFood.getString(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME))));
				cUnit.close();
				cFood.close();
			} while (cTemplateFood.moveToNext());
			cTemplateFood.close();

			list.add(new DBFoodTemplate(
					cFoodTemplates
							.getInt(cFoodTemplates
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODTEMPLATE_ID)),
					cFoodTemplates.getInt(cFoodTemplates
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODTEMPLATE_MEALTYPEID)),
					cFoodTemplates.getInt(cFoodTemplates
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODTEMPLATE_USERID)),
					cFoodTemplates.getInt(cFoodTemplates
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODTEMPLATE_VISIBLE)),
					cFoodTemplates.getString(cFoodTemplates
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODTEMPLATE_FOODTEMPLATENAME)),
					foods));
		} while (cFoodTemplates.moveToNext());
		cFoodTemplates.close();
		return list;
	}

}
