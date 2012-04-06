package be.goossens.oracle.Show.Food;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.Custom.CustomArrayAdapterFoodList;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowFoodList extends ListActivity {
	// dbHelper to get the food list out the database
	private DbAdapter dbHelper;

	// editTextSearch is the search box above the listview
	private EditText editTextSearch;

	private CustomArrayAdapterFoodList fooditemlist;

	private Button btCreateFood, btSelections;

	/*
	 * This is used to know if we need to show the pop up to delete selected
	 * food without this boolean the pop up would spawn every time we come back
	 * to this activity
	 */
	private boolean startUp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_food_list, null);
		setContentView(contentView);

		startUp = true;
		fooditemlist = null;

		editTextSearch = (EditText) findViewById(R.id.editTextSearch);
		btCreateFood = (Button) findViewById(R.id.buttonShowFoodListShowCreateFood);
		btSelections = (Button) findViewById(R.id.buttonShowFoodListShowSelectedFood);

		dbHelper = new DbAdapter(this);

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

		if (fooditemlist == null) {
			updateListAdapter();
		} else {
			setListAdapter(fooditemlist);
			setSelection(fooditemlist.getFirstMatchingItem(editTextSearch
					.getText()));
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

	private void updateListAdapter() {
		dbHelper.open();
		Cursor cSettings = dbHelper.fetchSettingByName(getResources()
				.getString(R.string.font_size));
		cSettings.moveToFirst();
		fooditemlist = new CustomArrayAdapterFoodList(
				this,
				R.layout.row_food,
				20,
				cSettings.getInt(cSettings
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		cSettings.close();
		fooditemlist.initializeFoodItemList(null);
		setListAdapter(fooditemlist);
	}

	public void updateButton() {
		dbHelper.open();
		Cursor selectedFood = dbHelper.fetchAllSelectedFood();
		startManagingCursor(selectedFood);

		Button buttonSelections = (Button) findViewById(R.id.buttonShowFoodListShowSelectedFood);
		buttonSelections.setText(getResources().getString(
				R.string.showFoodListButtonSelections)
				+ " (" + selectedFood.getCount() + ")");
		selectedFood.close();
	}

	// when we click on a item in the listview
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// hide the virtual keyboard
		keyboardDissapear();

		Intent i = new Intent(this, ShowAddFoodToSelection.class)
				.putExtra(DataParser.fromWhereWeCome,
						DataParser.weComeFromShowFoodList)
				.putExtra(
						DataParser.idFood,
						Long.parseLong(""
								+ fooditemlist.getFoodItem(position).getId()))
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

	@Override
	public void onResume() {
		super.onResume();
		dbHelper.open();
		updateListAdapter();
		clearEditTextSearch();
		updateButton();
		if (startUp)
			checkToShowPopUpToDeleteSelectedFood();
	}

	public void clearEditTextSearch() {
		// every time we resume make the search box empty so the user dont have
		// to press delete search box every time he adds a selection
		editTextSearch.setText("");
	}

	private void checkToShowPopUpToDeleteSelectedFood() {
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
		startUp = false;
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

	public void triggerSearching() {
		setSelection(fooditemlist
				.getFirstMatchingItem(editTextSearch.getText()));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}