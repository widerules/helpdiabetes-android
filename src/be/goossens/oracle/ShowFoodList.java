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

	// activity ids
	private static final int ACTIVITY_CREATE_DATA = 0;
	private static final int ACTIVITY_ADD_FOOD = 1;
	private static final int ACTIVITY_SELECTED_FOOD = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_food_list);
		editTextSearch = (EditText) findViewById(R.id.editTextSearch);
		dbHelper = new DbAdapter(this);
		dbHelper.open();
		// check the data to see if its the first time this app is running
		checkData();
		registerForContextMenu(getListView());
	}

	private void checkData() {
		Cursor foodCursor = dbHelper.fetchAllFood();
		switch (foodCursor.getCount()) {
		// When it is the first time this app is running
		// go to the page to create the data
		case 0:
			Intent i = new Intent(this, CreateData.class);
			startActivityForResult(i, ACTIVITY_CREATE_DATA);
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
		setTitle(getResources().getString(R.string.app_name) + " (" + selectedFood.getCount() + " " + getResources().getString(R.string.items_selected) + ")");
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
		Intent i = new Intent(this, AddFood.class);
		i.putExtra(DbAdapter.DATABASE_FOOD_ID, id);
		startActivityForResult(i, ACTIVITY_ADD_FOOD);
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
		Intent i = new Intent(this, SelectedFood.class);
		startActivityForResult(i, ACTIVITY_SELECTED_FOOD);
		return true;
	}

	public void onClickShowSelectedFood(View view) {
		Intent i = new Intent(this, SelectedFood.class);
		startActivityForResult(i, ACTIVITY_SELECTED_FOOD);
	}

	public void goToPageSelectedFood() {
		Intent i = new Intent(this, SelectedFood.class);
		startActivityForResult(i, ACTIVITY_SELECTED_FOOD);
	}
}