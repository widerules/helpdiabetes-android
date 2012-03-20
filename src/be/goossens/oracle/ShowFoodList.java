package be.goossens.oracle;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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
import android.widget.Toast;

public class ShowFoodList extends ListActivity {
	// dbHelper to get the food list out the database
	private DbAdapter dbHelper;

	// editTextSearch is the search box above the listview
	private EditText editTextSearch;

	// private Cursor cFoodCursor;
	// private SimpleCursorAdapter scaFood;

	private CustomArrayAdapterFoodList fooditemlist;

	//This is used to know if we need to show the pop up to delete selected food
	//without this boolean the pop up would spawn every time we come back to this activity
	private boolean startUp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_food_list);
		startUp = true;
		fooditemlist = null;

		editTextSearch = (EditText) findViewById(R.id.editTextSearch);
		dbHelper = new DbAdapter(this);
		dbHelper.createDatabase();

		// This is used to update the listview when the text in the search boxs
		// changes
		editTextSearch.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				setSelection(fooditemlist.getFirstMatchingItem(s));
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});

		if (fooditemlist == null) {
			fooditemlist = new CustomArrayAdapterFoodList(this,
					R.layout.row_food,20);
			fooditemlist.initializeFoodItemList(null);
			setListAdapter(fooditemlist);
		} else {
			setListAdapter(fooditemlist);
			setSelection(fooditemlist.getFirstMatchingItem(editTextSearch
					.getText()));
		}
	}

	private void updateTitle() {
		Cursor selectedFood = dbHelper.fetchAllSelectedFood();
		startManagingCursor(selectedFood);
		setTitle(getResources().getString(R.string.app_name) + " ("
				+ selectedFood.getCount() + " "
				+ getResources().getString(R.string.items_selected) + ")");
		selectedFood.close();
	}

	// when we click on a item in the listview
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, ShowAddFoodToSelection.class);

		// Toast.makeText(this, "SelectedFoodName = " +
		// fooditemlist.getFoodItem(position).getName() + " and id = " +
		// fooditemlist.getFoodItem(position).getId(),
		// Toast.LENGTH_LONG).show();
		i.putExtra(DbAdapter.DATABASE_FOOD_ID,
				Long.parseLong("" + fooditemlist.getFoodItem(position).getId()));
		startActivity(i);
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		// every time we resume make the search box empty so the user dont have
		// to press delete search box every time he adds a selection
		editTextSearch.setText("");
		updateTitle();
		if(startUp)
			checkToShowPopUpToDeleteSelectedFood();
	}

	private void checkToShowPopUpToDeleteSelectedFood() {
		// check if there are still selections in the selectedFood table
		if (dbHelper.fetchAllSelectedFood().getCount() > 0) {
			// Show dialog box to delete the selections
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
 						startUp = false;
						Cursor cSelectedFood = dbHelper.fetchAllSelectedFood();
						cSelectedFood.moveToFirst();
						do {
							dbHelper.deleteSelectedFood(cSelectedFood.getLong(cSelectedFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_ID)));
						} while (cSelectedFood.moveToNext());
						updateTitle();
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						startUp = false;
						break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					getResources().getString(
							R.string.do_you_want_to_delete_the_selections))
					.setPositiveButton(getResources().getString(R.string.yes),
							dialogClickListener)
					.setNegativeButton(getResources().getString(R.string.no),
							dialogClickListener).show();
		}
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
		int count = selectedFood.getCount();
		selectedFood.close();
		if (count > 0) {
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

	/*
	 * private void refresh() { updateTitle(); fillListView(); }
	 */

	@Override
	protected void onPause() {
		dbHelper.close();
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void triggerSearching() {
		setSelection(fooditemlist
				.getFirstMatchingItem(editTextSearch.getText()));
	}
}