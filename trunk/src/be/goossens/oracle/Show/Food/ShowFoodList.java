package be.goossens.oracle.Show.Food;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.Custom.CustomArrayAdapterFoodList;
import be.goossens.oracle.Objects.DBFoodComparable;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.FoodComparator;

public class ShowFoodList extends ListActivity {
	// dbHelper to get the food list out the database
	private DbAdapter dbHelper;

	// editTextSearch is the search box above the listview
	private EditText editTextSearch;

	private CustomArrayAdapterFoodList customArrayAdapterFoodList;
	private List<DBFoodComparable> listDBFoodComparable;

	private Button btCreateFood, btSelections;

	// create a seperated dbAdapter for the foodlist
	// this becaus the food list is in a thread and the other dbAdapter gets
	// closed when we update the button
	private DbAdapter dbHelperFoodList;

	/*
	 * This is used to know if we need to show the pop up to delete selected
	 * food without this boolean the pop up would spawn every time we come back
	 * to this activity
	 */
	private boolean startUp;
 
	// This boolean is used to see if asynctask is running
	private boolean runAsyncTask;

	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_food_list, null);
		setContentView(contentView);

		runAsyncTask = false;
		startUp = true;

		customArrayAdapterFoodList = null;

		editTextSearch = (EditText) findViewById(R.id.editTextSearch);
		btCreateFood = (Button) findViewById(R.id.buttonShowFoodListShowCreateFood);
		btSelections = (Button) findViewById(R.id.buttonShowFoodListShowSelectedFood);

		dbHelper = new DbAdapter(this);
		dbHelperFoodList = new DbAdapter(this);

		editTextSearch.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (customArrayAdapterFoodList != null
						&& listDBFoodComparable.size() > 0)
					setSelection(customArrayAdapterFoodList
							.getFirstMatchingItem(s));
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});

		btCreateFood.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickCreateNewFood(v);
			}
		});

		btSelections.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickShowSelectedFood(v);
			}
		});

		updateButton();

		if (startUp)
			checkToShowPopUpToDeleteSelectedFood();
	}

	protected void onResume() {
		refreshFoodList();
		super.onResume();
	};

	public void refreshFoodList() {
		if (!runAsyncTask) {
			runAsyncTask = true;
			new threadUpdateListAdapter().execute();
		}
	}

	// asynctask is used to thread in android
	// This method will first get al the food objects and put them in a array
	// list
	// Then it will sort that array list
	// and then it wil call onpostexecute where it wil update the list adapter
	private class threadUpdateListAdapter extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			//make list empty so we cant click on a item that doesnt exists anymore!
			setListAdapter(null);
			// open connection
			dbHelperFoodList.open();
			// get objects out of the database
			fillObjects();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			updateListAdapter();
			dbHelperFoodList.close();
			runAsyncTask = false;
			super.onPostExecute(result);
		}
	}

	public void onClickCreateNewFood(View view) {
		// Go to new page to create new food
		Intent i = new Intent(this, ShowCreateFood.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		View v = ActivityGroupMeal.group.getLocalActivityManager()
				.startActivity(DataParser.activityIDMeal, i).getDecorView();
		ActivityGroupMeal.group.setContentView(v);
	}

	public void updateListAdapter() {
		Cursor cSettings = dbHelperFoodList.fetchSettingByName(getResources()
				.getString(R.string.font_size));

		cSettings.moveToFirst();

		customArrayAdapterFoodList = new CustomArrayAdapterFoodList(
				this,
				R.layout.row_food,
				20,
				cSettings.getInt(cSettings
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)),
				listDBFoodComparable);

		cSettings.close();
		setListAdapter(customArrayAdapterFoodList);
	}

	private void fillObjects() {
		listDBFoodComparable = new ArrayList<DBFoodComparable>();

		Cursor cSettings = dbHelperFoodList.fetchSettingByName(getResources()
				.getString(R.string.language));
		cSettings.moveToFirst();

		// get all the food items
		Cursor cFood = dbHelperFoodList
				.fetchFoodByLanguageID(cSettings.getLong(cSettings
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		// Cursor cFood = dbHelperFoodList.fetchAllFood();

		if (cFood.getCount() > 0) {
			cFood.moveToFirst();
			do {
				DBFoodComparable newFood = new DBFoodComparable(
						cFood.getInt(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
						null,
						0,
						0,
						0,
						0,
						0,
						cFood.getString(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)));
				listDBFoodComparable.add(newFood);
			} while (cFood.moveToNext());
		}

		cSettings.close();
		cFood.close();

		// sort the list
		sortObjects();
	}

	private void sortObjects() {
		// sort the list
		FoodComparator comparator = new FoodComparator();
		Collections.sort(listDBFoodComparable, comparator);
	}

	public void updateButton() {
		dbHelper.open();
		Button buttonSelections = (Button) findViewById(R.id.buttonShowFoodListShowSelectedFood);
		buttonSelections.setText(getResources().getString(
				R.string.showFoodListButtonSelections)
				+ " (" + dbHelper.fetchAllSelectedFood().getCount() + ")");
		dbHelper.close();
	}

	// when we click on a item in the listview
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// hide the virtual keyboard
		keyboardDissapear();

		Intent i = new Intent(this, ShowAddFoodToSelection.class)
				.putExtra(DataParser.fromWhereWeCome,
						DataParser.weComeFromShowFoodList)
				.putExtra(
						DataParser.idFood,
						Long.parseLong(""
								+ listDBFoodComparable.get(position).getId()))
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		View view = ActivityGroupMeal.group.getLocalActivityManager()
				.startActivity(DataParser.activityIDMeal, i).getDecorView();
		ActivityGroupMeal.group.setContentView(view);
	}

	// let the keyboard dissapear
	private void keyboardDissapear() {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void clearEditTextSearch() {
		// every time we resume make the search box empty so the user dont have
		// to press delete search box every time he adds a selection
		editTextSearch.setText("");
	}

	private void checkToShowPopUpToDeleteSelectedFood() {
		startUp = false;
		dbHelper.open();
		// check if there are still selections in the selectedFood table
		if (dbHelper.fetchAllSelectedFood().getCount() > 0) {
			// Show dialog box to delete the selections
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						Cursor cSelectedFood = dbHelper.fetchAllSelectedFood();
						cSelectedFood.moveToFirst();
						do {
							dbHelper.deleteSelectedFood(cSelectedFood.getLong(cSelectedFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_ID)));
						} while (cSelectedFood.moveToNext());
						updateButton();
						cSelectedFood.close();
						break;
					}
				}
			};
  
			AlertDialog.Builder builder = new AlertDialog.Builder(
					ActivityGroupMeal.group);
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
		inflater.inflate(R.menu.food_event_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		switch (item.getItemId()) {
		// if we press in the menu on update own food
		case R.id.menuManageOwnFood:
			i = new Intent(this, ShowManageOwnFood.class)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			View v = ActivityGroupMeal.group.getLocalActivityManager()
					.startActivity(DataParser.activityIDMeal, i).getDecorView();
			ActivityGroupMeal.group.setContentView(v);
			break;
		}
		return true;
	}

	public void onClickShowSelectedFood(View view) {
		goToPageSelectedFood();
	}

	public void goToPageSelectedFood() {
		Intent i = new Intent(this, ShowSelectedFood.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		View v = ActivityGroupMeal.group.getLocalActivityManager()
				.startActivity(DataParser.activityIDMeal, i).getDecorView();
		ActivityGroupMeal.group.setContentView(v);
	}
 
	@Override
	protected void onPause() {
		dbHelper.close();
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}