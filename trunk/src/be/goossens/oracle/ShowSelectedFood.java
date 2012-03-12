package be.goossens.oracle;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.TextView;

public class ShowSelectedFood extends ListActivity {
	private DbAdapter dbHelper;

	private List<DBSelectedFood> listOfSelectedFood;

	private static final int DELETE_ID = Menu.FIRST;
	private static final int EDIT_ID = Menu.FIRST + 1;

	// Need this id to update all the values afther we updated a selectedFood
	private static final int update_selectedFood_id = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selected_food);

		dbHelper = new DbAdapter(this);
		dbHelper.open();

		listOfSelectedFood = new ArrayList<DBSelectedFood>();
		// refreshData got 2 methods to show al the data on the screen
		refreshData();
		registerForContextMenu(getListView());
	}

	// converts the cursor with all selected food to a arrayList<DBSelectedFood>
	// and returns the arraylist
	private ArrayList<DBSelectedFood> getSelectedFood() {
		Cursor selectedFoodCursor = dbHelper.fetchAllSelectedFood();
		startManagingCursor(selectedFoodCursor);
		ArrayList<DBSelectedFood> list = new ArrayList<DBSelectedFood>();
		while (selectedFoodCursor.moveToNext()) {
			list.add(
			// new DBSelectedFood(id, amound, foodName, kcal, unitName, carbs,
			// prot, fat, standardAmound)
			new DBSelectedFood(
					selectedFoodCursor
							.getString(selectedFoodCursor
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_ID)),
					selectedFoodCursor.getFloat(selectedFoodCursor
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT)),
					selectedFoodCursor.getString(selectedFoodCursor
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_FOODNAME)),
					selectedFoodCursor.getFloat(selectedFoodCursor
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_KCAL)),
					selectedFoodCursor.getString(selectedFoodCursor
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITNAME)),
					selectedFoodCursor.getFloat(selectedFoodCursor
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_CARBS)),
					selectedFoodCursor.getFloat(selectedFoodCursor
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_PROTEIN)),
					selectedFoodCursor.getFloat(selectedFoodCursor
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_FAT)),
					selectedFoodCursor.getFloat(selectedFoodCursor
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_STANDARDAMOUNT))));
		}
		return list;
	}

	// Show on top Total: amound calories
	private void calculateValues() {
		TextView tvTotal = (TextView) findViewById(R.id.textViewShowTotal);
		// calculate amound of kilocalories
		float totalKcal = 0;
		float totalCarbs = 0;
		float totalProtein = 0;
		float totalFat = 0;

		Cursor allSelectedFood = dbHelper.fetchAllSelectedFood();
		startManagingCursor(allSelectedFood);
		while (allSelectedFood.moveToNext()) {
			// add the calculated kcal to the total
			totalKcal += allSelectedFood
					.getFloat(allSelectedFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT))
					* allSelectedFood
							.getFloat(allSelectedFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_KCAL));

			// add the calculated carbs to the total
			totalCarbs += allSelectedFood
					.getFloat(allSelectedFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT))
					* allSelectedFood
							.getFloat(allSelectedFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_CARBS));

			// add the calculated protein to the total
			totalProtein += allSelectedFood
					.getFloat(allSelectedFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT))
					* allSelectedFood
							.getFloat(allSelectedFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_PROTEIN));

			totalFat += allSelectedFood
					.getFloat(allSelectedFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT))
					* allSelectedFood
							.getFloat(allSelectedFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_FAT));
		}
		tvTotal.setText(getResources().getString(R.string.total) + ": \n" + " "
				+ totalKcal + " "
				+ getResources().getString(R.string.amound_of_kcal) + " \n"
				+ " " + totalCarbs + " "
				+ getResources().getString(R.string.amound_of_carbs) + " \n"
				+ " " + totalProtein + " "
				+ getResources().getString(R.string.amound_of_protein) + " \n"
				+ " " + totalFat + " "
				+ getResources().getString(R.string.amound_of_fat));

	}

	// create the context menu ( display if we long press on a item in the
	// listview )
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		menu.add(0, EDIT_ID, 0, R.string.menu_edit);
	}

	// if we select a item in the context menu check if pressed delete or edit.
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		// if pressed delete
		case DELETE_ID:
			dbHelper.deleteSelectedFood(info.id);
			refreshData();
			break;
		// if pressed edit
		// go back to the select page
		case EDIT_ID:
			Cursor cSelectedFood = dbHelper.fetchSelectedFood(info.id);
			Intent i = new Intent(this, ShowAddFood.class);
			i.putExtra(
					DbAdapter.DATABASE_FOOD_ID,
					cSelectedFood.getLong(cSelectedFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_FOODID)));
			// cant put paramer dbadapter.database_selectedfood_id instead of
			// selectedfoodid becaus in the parameter the value is _id and
			// food_id its paramter is _id to!
			i.putExtra("selectedfoodid", info.id);
			startActivityForResult(i, update_selectedFood_id);
			// startActivity(i);
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		// if we come back from our update screen refresh all the values
		case update_selectedFood_id:
			refreshData();
			break;
		}
	}

	// to fill the listview with data
	private void fillData() {
		listOfSelectedFood = getSelectedFood();
		CustomAdapterSelectedFood adapter = new CustomAdapterSelectedFood(this,
				listOfSelectedFood);
		setListAdapter(adapter);
	}

	// when we click on the button return
	public void onClickBack(View view) {
		dbHelper.close();
		setResult(RESULT_OK);
		finish();
	}

	// when we click on the button delete all
	public void onClickDeleteAll(View view) {
		Cursor allSelectedFood = dbHelper.fetchAllSelectedFood();
		startManagingCursor(allSelectedFood);
		while (allSelectedFood.moveToNext()) {
			dbHelper.deleteSelectedFood(allSelectedFood.getInt(allSelectedFood
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_ID)));
		}
		refreshData();
	}

	// this method wil refresh al the data on the screen
	public void refreshData() {
		calculateValues();
		fillData();
	}
}
