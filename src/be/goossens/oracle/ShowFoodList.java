package be.goossens.oracle;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ShowFoodList extends ListActivity {
	// dbHelper to get the food list out the database
	private DbAdapter dbHelper;

	// editTextSearch is the search box above the listview
	private EditText editTextSearch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_food_list);

		editTextSearch = (EditText) findViewById(R.id.editTextSearch);
		dbHelper = new DbAdapter(this);
		dbHelper.open();
		// check the data to see if its the first time this app is running
		checkData();
	}

	private void checkData() {
		Cursor foodCursor = dbHelper.fetchAllFood();
		startManagingCursor(foodCursor);
		switch (foodCursor.getCount()) {
		// When it is the first time this app is running
		// go to the page to create the data
		case 0:
			Intent i = new Intent(this, ShowCreateData.class);
			startActivity(i);
			break;
		// if data already exists, fill the listview
		default:
			updateTitle();
			fillListView();
			break;
		}
	}

	// Fill the listview with all the food data
	private void fillListView() {
		Cursor foodCursor = null;
		if (editTextSearch.getText().length() == 0) {
			foodCursor = dbHelper.fetchAllFood();
		} else {
			foodCursor = dbHelper.fetchFoodWithFilterByName(editTextSearch
					.getText().toString());
		}
		startManagingCursor(foodCursor);
		String[] name = new String[] { DbAdapter.DATABASE_FOOD_NAME };
		int[] id = new int[] { R.id.text1 };
		SimpleCursorAdapter food = new SimpleCursorAdapter(this,
				R.layout.food_row, foodCursor, name, id);
		setListAdapter(food);
	}

	private void updateTitle() {
		Cursor selectedFood = dbHelper.fetchAllSelectedFood();
		startManagingCursor(selectedFood);
		setTitle(getResources().getString(R.string.app_name) + " ("
				+ selectedFood.getCount() + " "
				+ getResources().getString(R.string.items_selected) + ")");
	}

	// when we press a key ( update the listview )
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		updateTitle();
		fillListView();
		return super.dispatchKeyEvent(event);
	}

	// when we click on a item in the listview
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, ShowAddFood.class);
		i.putExtra(DbAdapter.DATABASE_FOOD_ID, id);
		startActivity(i);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateTitle();
		fillListView();
	}

	// Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		//if we press in the menu on selections
		case R.id.selected_food:
			goToPageSelectedFood();
			break;
		//if we press in the menu on update own food
		case R.id.update_own_food:
			Intent i =new Intent(this,ShowUpdateOwnFood.class);
			startActivity(i);
			break;
		}
		return true;
	}

	public void onClickShowSelectedFood(View view) {
		goToPageSelectedFood();
	}

	public void goToPageSelectedFood() {
		Intent i = new Intent(this, ShowSelectedFood.class);
		startActivity(i);
	}
}