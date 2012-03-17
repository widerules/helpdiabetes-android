package be.goossens.oracle;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

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
		dbHelper.createDatabase();

		// This is used to update the listview when the text in the search boxs
		// changes
		editTextSearch.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				refresh();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
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
				R.layout.row_food, foodCursor, name, id);
		setListAdapter(food);
	}

	private void updateTitle() {
		Cursor selectedFood = dbHelper.fetchAllSelectedFood();
		startManagingCursor(selectedFood);
		setTitle(getResources().getString(R.string.app_name) + " ("
				+ selectedFood.getCount() + " "
				+ getResources().getString(R.string.items_selected) + ")");
	}

	// when we click on a item in the listview
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, ShowAddFoodToSelection.class);
		i.putExtra(DbAdapter.DATABASE_FOOD_ID, id);
		startActivity(i);
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		// every time we resume make the search box empty so the user dont have
		// to press delete search box every time he adds a selection
		editTextSearch.setText("");
		refresh();
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
		// if we press in the menu on selections
		case R.id.menu_selected_food:
			goToPageSelectedFood();
			break;
		// if we press in the menu on update own food
		case R.id.menu_update_own_food:
			Intent i = new Intent(this, ShowManageOwnFood.class);
			startActivity(i);
			break;
		// if we press in the menu on preferences
		case R.id.menu_preferences:
			Intent o = new Intent(this, ShowPreferencesInsulineRatio.class);
			startActivity(o);
			break;
		}
		return true;
	}

	public void onClickShowSelectedFood(View view) {
		Cursor selectedFood = dbHelper.fetchAllSelectedFood();
		startManagingCursor(selectedFood);
		if (selectedFood.getCount() > 0) {
			goToPageSelectedFood();
		} else {
			Toast.makeText(this,
					getResources().getString(R.string.selections_are_empty),
					Toast.LENGTH_SHORT).show();
		}
	}

	public void goToPageSelectedFood() {
		Intent i = new Intent(this, ShowSelectedFood.class);
		startActivity(i);
	}

	private void refresh() {
		updateTitle();
		fillListView();
	}

	@Override
	protected void onPause() {
		dbHelper.close();
		super.onPause();
	}

	@Override
	protected void onStop() {
		dbHelper.close();
		super.onStop();
	}
}