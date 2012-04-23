package be.goossens.oracle.Show.Food;

import java.util.ArrayList;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.Custom.CustomBaseAdapterFoodTemplates;
import be.goossens.oracle.Objects.DBFood;
import be.goossens.oracle.Objects.DBFoodTemplate;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowFoodTemplates extends ListActivity {
	private DbAdapter dbHelper;

	private static final int DELETE_ID = Menu.FIRST;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_food_templates, null);
		setContentView(contentView);

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
		dbHelper.open();
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
				ActivityGroupMeal.group.back();
				// refresh the page selectedFood
				ActivityGroupMeal.group.refreshShowSelectedFood(1);
			}
			break;
		}
		return super.onContextItemSelected(item);
	}

	private void fillData() {
		dbHelper.open();
		if (dbHelper.fetchAllFoodTemplates().getCount() > 0) {
			Cursor cSettings = dbHelper.fetchSettingByName(getResources()
					.getString(R.string.setting_font_size));
			cSettings.moveToFirst();
			CustomBaseAdapterFoodTemplates adapter = new CustomBaseAdapterFoodTemplates(
					this,
					getFoodTemplates(),
					cSettings.getInt(cSettings
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
			setListAdapter(adapter);
			cSettings.close();
		} else {
			setListAdapter(null);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		dbHelper.open();
		// count how much foodItems we add
		int count = 0;

		// First put all the food in the selectedFood table
		Cursor cTemplateFood = dbHelper.fetchTemplateFoodsByFoodTemplateID(id);
		cTemplateFood.moveToFirst();
		Cursor cUnit = null;
		do {
			count++; 
			cUnit = dbHelper
					.fetchFoodUnit(cTemplateFood.getLong(cTemplateFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_TEMPLATEFOOD_UNITID)));
			cUnit.moveToFirst();
			dbHelper.createSelectedFood(
					cTemplateFood
							.getFloat(cTemplateFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_TEMPLATEFOOD_AMOUNT)),
					cUnit.getLong(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)));
		} while (cTemplateFood.moveToNext());

		cUnit.close();
		cTemplateFood.close();
		ActivityGroupMeal.group.back();

		// update the list in show selected food
		ActivityGroupMeal.group.refreshShowSelectedFood(1);

		// update the button on showfoodlist page
		ActivityGroupMeal.group
				.showFoodListsetCountSelectedFood((ActivityGroupMeal.group
						.showFoodListGetCountSelectedFood() + count));
	}

	// converts the cursor with all food templates to a arrayList
	// and returns that array list
	private ArrayList<DBFoodTemplate> getFoodTemplates() {
		dbHelper.open();
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
				Cursor cUnit = dbHelper
						.fetchFoodUnit(cTemplateFood.getLong(cTemplateFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_TEMPLATEFOOD_UNITID)));

				cUnit.moveToFirst();
				Cursor cFood = dbHelper
						.fetchFood(cUnit.getLong(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)));
				cFood.moveToFirst();
				foods.add(new DBFood(
						cFood.getInt(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
						cFood.getString(cFood 
				 				.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)),cTemplateFood.getFloat(cTemplateFood.getColumnIndexOrThrow(DbAdapter.DATABASE_TEMPLATEFOOD_AMOUNT)) + " " + cUnit.getString(cUnit.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME)))); 
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
