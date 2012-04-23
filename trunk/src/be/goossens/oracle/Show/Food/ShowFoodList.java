package be.goossens.oracle.Show.Food;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.Objects.DBFoodComparable;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.ExcelCharacter;
import be.goossens.oracle.Rest.FoodComparator;
import be.goossens.oracle.Show.ShowHomeTab;

public class ShowFoodList extends ListActivity {
	// dbHelper to get the food list out the database
	private DbAdapter dbHelper;

	// editTextSearch is the search box above the listview
	private EditText editTextSearch;

	private CustomArrayAdapterFoodList customArrayAdapterFoodList;

	private List<DBFoodComparable> listDBFoodComparable;
	private List<DBFoodComparable> listDBFoodComparableWithFilter;
	private boolean listWithFilter;

	private Button btCreateFood, btSelections, btSearch;
	private int countSelectedFood;

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
	private String getState = "getState";

	private boolean threadStarted;

	// The textview with text = loading...
	private TextView tvLoading;

	// Animation for the button selections
	// this animation is used to let the button flash when we have selections
	private Animation animation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_food_list, null);
		setContentView(contentView);

		startUp = true;

		customArrayAdapterFoodList = null;

		editTextSearch = (EditText) findViewById(R.id.editTextSearch);
		btCreateFood = (Button) findViewById(R.id.buttonShowFoodListShowCreateFood);
		btSelections = (Button) findViewById(R.id.buttonShowFoodListShowSelectedFood);
		btSearch = (Button) findViewById(R.id.buttonShowFoodListSearch);
		tvLoading = (TextView) findViewById(R.id.textViewLoading);

		dbHelper = new DbAdapter(this);
		dbHelperFoodList = new DbAdapter(this);

		// set animation
		animation = new AlphaAnimation(1, 0); // Change alpha from fully visible
												// to invisible
		animation.setDuration(800); // duration ( 500 = half a second )
		animation.setInterpolator(new LinearInterpolator()); // do not alter
																// animation
																// rate
		animation.setRepeatCount(Animation.INFINITE); // Repeat animation
														// infinitely
		animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
													// end so the button will
													// fade back in

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

		btSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickSearch();
			}
		});

		editTextSearch.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				keyboardShow();
			}
		});
	}

	private void goToItem(long foodID) {
		for (int i = 0; i < listDBFoodComparable.size(); i++) {
			if (listDBFoodComparable.get(i).getId() == foodID) {
				setSelection(i);
			}
		}
	}

	private void onClickSearch() {

		if (listWithFilter) {
			listWithFilter = false;
			btSearch.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.ic_search_no));
			tvLoading.setVisibility(View.VISIBLE);
			setListAdapter(null);
			updateListAdapter(); 
		} else if (!listWithFilter) {
			if (editTextSearch.getText().toString().length() > 0) {
				listWithFilter = true;
				btSearch.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.ic_search_yes));
				// when we click on the button search
				tvLoading.setVisibility(View.VISIBLE);
				setListAdapter(null);
				// start thread
				new AsyncGetSearch().execute();
			}
		}
	}

	private class AsyncGetSearch extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			dbHelper.open();
			fillObjectsWithFilter();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			clearEditTextSearch();
			updateListAdapterWithFilter();
			super.onPostExecute(result);
		}

	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		// retrieve the startUpBoolean
		startUp = state.getBoolean(getState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// save the startUpBoolean
		outState.putBoolean(getState, startUp);
		super.onSaveInstanceState(outState);
	}

	protected void onResume() {
		dbHelper.open();

		if (startUp)
			checkToShowPopUpToDeleteSelectedFood();

		// run when we first come to this activity ( app starts up )
		if (!threadStarted) {
			threadStarted = true;
			// only do this once
			countSelectedFood = dbHelper.fetchAllSelectedFood().getCount();
			new AsyncUpdateListAdapter().execute();
		}

		updateButton();

		super.onResume();
	};

	public void clearEditTextSearch() {
		editTextSearch.setText("");
	}

	// This method will first get all the food objects and put them in a array
	// list
	// Then it will sort that array list
	// and then it wil call onpostexecute where it wil update the list adapter
	private class AsyncUpdateListAdapter extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			// open connection
			dbHelperFoodList.open();
			// get objects out of the database
			fillObjects();
			return null;
		}

		@Override
		protected void onCancelled() {
			updateListAdapter();
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {

			// threadStarted = true;
			updateListAdapter();
			super.onPostExecute(result);
		}
	}

	public void onClickCreateNewFood(View view) {
		// Go to new page to create new food
		Intent i = new Intent(this, ShowCreateFood.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		View v = ActivityGroupMeal.group.getLocalActivityManager()
				.startActivity(DataParser.activityIDShowFoodList, i)
				.getDecorView();
		ActivityGroupMeal.group.setContentView(v);
	}

	public void updateListAdapterWithFilter() {
		dbHelper.open();
		Cursor cSettings = dbHelperFoodList.fetchSettingByName(getResources()
				.getString(R.string.setting_font_size));

		cSettings.moveToFirst();

		customArrayAdapterFoodList = new CustomArrayAdapterFoodList(
				this,
				R.layout.row_food,
				20,
				cSettings.getInt(cSettings
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)),
				listDBFoodComparableWithFilter);

		cSettings.close();
		setListAdapter(customArrayAdapterFoodList);
		tvLoading.setVisibility(View.GONE);
	}

	public void updateListAdapter() {
		dbHelper.open();
		Cursor cSettings = dbHelperFoodList.fetchSettingByName(getResources()
				.getString(R.string.setting_font_size));

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
		tvLoading.setVisibility(View.GONE);
	}

	private void fillObjectsWithFilter() {
		listDBFoodComparableWithFilter = new ArrayList<DBFoodComparable>();
		dbHelperFoodList.open();
		Cursor cSettings = dbHelperFoodList.fetchSettingByName(getResources()
				.getString(R.string.setting_language));
		cSettings.moveToFirst();

		// get all the food items
		Cursor cFood = dbHelperFoodList
				.fetchFoodWithFilterByName(
						editTextSearch.getText().toString(),
						cSettings.getLong(cSettings
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));

		if (cFood.getCount() > 0) {
			cFood.moveToFirst();
			do {
				// new DBFoodComparable(id, platform, languageid, visible,
				// categoryid, userid, isfavorite, name)
				DBFoodComparable newFood = new DBFoodComparable(
						cFood.getInt(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
						cFood.getString(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_PLATFORM)),
						0,
						0,
						0,
						0,
						cFood.getInt(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ISFAVORITE)),
						cFood.getString(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)));
				listDBFoodComparableWithFilter.add(newFood);
			} while (cFood.moveToNext());
		}

		cSettings.close();
		cFood.close();

		// sort the list
		sortObjectsWithFilter();
	}

	private void fillObjects() {
		listDBFoodComparable = new ArrayList<DBFoodComparable>();
		dbHelperFoodList.open();
		Cursor cSettings = dbHelperFoodList.fetchSettingByName(getResources()
				.getString(R.string.setting_language));
		cSettings.moveToFirst();

		// get all the food items
		Cursor cFood = dbHelperFoodList
				.fetchFoodByLanguageID(cSettings.getLong(cSettings
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));

		if (cFood.getCount() > 0) {
			cFood.moveToFirst();
			do {
				// new DBFoodComparable(id, platform, languageid, visible,
				// categoryid, userid, isfavorite, name)
				DBFoodComparable newFood = new DBFoodComparable(
						cFood.getInt(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
						cFood.getString(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_PLATFORM)),
						0,
						0,
						0,
						0,
						cFood.getInt(cFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ISFAVORITE)),
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

	private void sortObjectsWithFilter() {
		// sort the list
		FoodComparator comparator = new FoodComparator();
		Collections.sort(listDBFoodComparableWithFilter, comparator);
	}

	private void sortObjects() {
		// sort the list
		FoodComparator comparator = new FoodComparator();
		Collections.sort(listDBFoodComparable, comparator);
	}

	public void updateButton() {
		Button buttonSelections = (Button) findViewById(R.id.buttonShowFoodListShowSelectedFood);
		if (countSelectedFood == 0) {
			buttonSelections.clearAnimation();
			buttonSelections.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.ic_selection_no));
		} else {
			buttonSelections.setAnimation(animation);
			buttonSelections.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.ic_selection_yes));
		}
	}

	public void goToPageAddFoodToSelection(int positionOfFood) {
		// hide the virtual keyboard
		keyboardDissapear();

		Intent i = new Intent(this, ShowAddFoodToSelection.class)
				.putExtra(DataParser.fromWhereWeCome,
						DataParser.weComeFromShowFoodList)
				.putExtra(
						DataParser.idFood,
						Long.parseLong(""
								+ listDBFoodComparable.get(positionOfFood)
										.getId()))
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		View view = ActivityGroupMeal.group.getLocalActivityManager()
				.startActivity(DataParser.activityIDShowAddFoodToSelection, i)
				.getDecorView();
		ActivityGroupMeal.group.setContentView(view);
	}

	// let the keyboard dissapear
	private void keyboardDissapear() {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void keyboardShow() {
		InputMethodManager inputManager = (InputMethodManager) ActivityGroupMeal.group
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// show keyboard , when it fails first switch tab and then try again
		if (!inputManager.showSoftInput(null, InputMethodManager.SHOW_FORCED)) {
			// switch from tab and back
			// the keyboard wont show if we dont do this
			ShowHomeTab parentActivity;
			parentActivity = (ShowHomeTab) this.getParent().getParent();
			parentActivity.goToTab(DataParser.activityIDTracking);
			parentActivity.goToTab(DataParser.activityIDShowFoodList);

			inputManager.showSoftInput(null, InputMethodManager.SHOW_FORCED);
		}
	}

	private void checkToShowPopUpToDeleteSelectedFood() {
		startUp = false;

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
						cSelectedFood.close();
						countSelectedFood = 0;
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
					.startActivity(DataParser.activityIDShowFoodList, i)
					.getDecorView();
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
				.startActivity(DataParser.activityIDShowFoodList, i)
				.getDecorView();
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
			editTextSearch.setText("");
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	public int getCountSelectedFood() {
		return countSelectedFood;
	}

	public void setCountSelectedFood(int count) {
		countSelectedFood = count;
		updateButton();
	}

	public void refreshListView() {
		tvLoading.setVisibility(View.VISIBLE);
		setListAdapter(null);
		new AsyncUpdateListAdapter().execute();
	}

	public void deleteFoodItemFromList(long foodId) {
		for (int i = 0; i < listDBFoodComparable.size(); i++) {
			if (listDBFoodComparable.get(i).getId() == foodId) {
				listDBFoodComparable.remove(i);
			}
		}
		customArrayAdapterFoodList.notifyDataSetChanged();
	}

	private class ThreadAddFoodItemToList extends AsyncTask<Long, Long, Long> {
		@Override
		protected Long doInBackground(Long... params) {
			addOneFoodItemToList(params[0]);
			return params[0];
		}

		@Override
		protected void onPostExecute(Long result) {
			setListAdapter(customArrayAdapterFoodList);
			customArrayAdapterFoodList.notifyDataSetChanged();
			goToItem(result);
			super.onPostExecute(result);
		}

	}

	private void addOneFoodItemToList(long foodId) {
		dbHelper.open();
		Cursor cFood = dbHelper.fetchFood(foodId);
		if (cFood.getCount() > 0) {
			cFood.moveToFirst();
			DBFoodComparable newFood = new DBFoodComparable(
					cFood.getInt(cFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
					cFood.getString(cFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_PLATFORM)),
					0,
					0,
					0,
					0,
					cFood.getInt(cFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ISFAVORITE)),
					cFood.getString(cFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)));
			listDBFoodComparable.add(newFood);
			sortObjects();
		}

		cFood.close();
	}

	public void addFoodItemToList(long foodId) {
		tvLoading.setVisibility(View.VISIBLE);
		setListAdapter(null);
		new ThreadAddFoodItemToList().execute(foodId);
	}

	public void changeFavorite(int position) {
		dbHelper.open();
		// when the favorite atm == 0
		if (listDBFoodComparable.get(position).getIsfavorite() == 0) {
			dbHelper.updateFoodIsFavorite(listDBFoodComparable.get(position)
					.getId(), 1);
			listDBFoodComparable.get(position).setIsfavorite(1);

		} else {
			dbHelper.updateFoodIsFavorite(listDBFoodComparable.get(position)
					.getId(), 0);
			listDBFoodComparable.get(position).setIsfavorite(0);
		}
		// sort the objects again
		sortObjects();
		// notify the change to the listview
		customArrayAdapterFoodList.notifyDataSetChanged();

		// Goto the top of the list
		setSelection(0);
	}

	private void showDialogLongClickListItem(final int position) {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupMeal.group);
		builder.setTitle(getResources().getString(R.string.options));
		builder.setItems(
				getResources().getStringArray(
						R.array.showFoodListResourceOptions),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							dbHelper.open();
							// set visible = 0 for food item in database
							dbHelper.updateFoodSetInVisible(listDBFoodComparable
									.get(position).getId());

							// delete selected fooditem from list
							listDBFoodComparable.remove(position);

							// update list
							customArrayAdapterFoodList.notifyDataSetChanged();
							break;
						}
					}
				});

		builder.create().show();
	}

	public class CustomArrayAdapterFoodList extends
			ArrayAdapter<DBFoodComparable> {
		private Context ctx;
		private int fontSize;
		private List<DBFoodComparable> foodItemList;
		private String previousSearchString;
		private int[] firstIndex;
		private int[] lastIndex;

		public CustomArrayAdapterFoodList(Context context,
				int textViewResourceId, int maximuSearchStringLength,
				int fontSize, List<DBFoodComparable> foodItemList) {
			super(context, textViewResourceId, foodItemList);
			this.ctx = context;
			this.foodItemList = foodItemList;
			this.fontSize = fontSize;

			previousSearchString = null;

			firstIndex = new int[maximuSearchStringLength + 1];
			lastIndex = new int[maximuSearchStringLength + 1];

			firstIndex[0] = 0;
			lastIndex[0] = foodItemList.size() - 1;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) ctx
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row_food, null);
			}

			TextView tt = (TextView) v.findViewById(R.id.row_food_text);
			TextView ttTwo = (TextView) v.findViewById(R.id.text2);
			ImageView iv = (ImageView) v.findViewById(R.id.imageViewFavorite);

			tt.setText(foodItemList.get(position).getName());

			tt.setTextSize(fontSize);
			ttTwo.setTextSize(fontSize);

			// first see if the food is favorite
			if (foodItemList.get(position).getIsfavorite() != 0) {
				iv.setImageDrawable(ctx.getResources().getDrawable(
						R.drawable.ic_star_yellow));
				// else see if the food is no standard
			} else if (!foodItemList.get(position).getPlatform().equals("s")) {
				iv.setImageDrawable(ctx.getResources().getDrawable(
						R.drawable.ic_star_green));
			} else {
				// else mark food as normal
				iv.setImageDrawable(ctx.getResources().getDrawable(
						R.drawable.ic_star_bw));
			}

			// when we click on a star
			iv.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					changeFavorite(position);
				}
			});

			// when we click on a textview!
			tt.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					goToPageAddFoodToSelection(position);
				}
			});

			ttTwo.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					goToPageAddFoodToSelection(position);
				}
			});

			// when we long click on a textview!
			tt.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View v) {
					showDialogLongClickListItem(position);
					return false;
				}
			});

			ttTwo.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View v) {
					showDialogLongClickListItem(position);
					return false;
				}
			});

			if (position % 2 == 0) {
				tt.setBackgroundColor(ctx.getResources().getColor(
						R.color.ColorListViewOne));
				ttTwo.setBackgroundColor(ctx.getResources().getColor(
						R.color.ColorListViewOne));
			} else {
				tt.setBackgroundColor(ctx.getResources().getColor(
						R.color.ColorListViewTwo));
				ttTwo.setBackgroundColor(ctx.getResources().getColor(
						R.color.ColorListViewTwo));
			}

			return v;
		}

		// For searching in the list
		public int getFirstMatchingItem(CharSequence s) {
			int index = 0;
			int[] result = new int[2];

			if (previousSearchString != null) {
				while ((index < s.length())
						&& (index < previousSearchString.length())
						&& (ExcelCharacter.compareToAsInExcel(s.charAt(index),
								previousSearchString.charAt(index)) == 0)) {
					index++;
				}
			}

			if (index != s.length()) {
				while ((index < s.length())
						&& (index < (firstIndex.length - 1))) {
					result = searchFirst(firstIndex[index], lastIndex[index],
							s.charAt(index), index);
					if (result[0] > -1) {
						firstIndex[index + 1] = result[0];
						lastIndex[index + 1] = searchLast(result[0], result[1],
								s.charAt(index), index);
					} else {
						if (index < (firstIndex.length - 1)) {
							firstIndex[index + 1] = firstIndex[index];
							lastIndex[index + 1] = lastIndex[index];
						}
					}
					index++;
				}
			}

			previousSearchString = s.toString();
			return firstIndex[index];
		}

		private int[] searchFirst(int low, int high, char value, int index) {
			int temp = 0;
			int temp2 = 0;
			int mid = 0;
			int belenth;
			low++;
			high++;
			int[] returnValue = { -1, high };
			char[] be;
			temp = high + 1;
			while (low < temp) {
				mid = (low + temp) / 2;
				be = this.foodItemList.get(mid - 1).getName().toCharArray();
				belenth = be.length;
				if (!(belenth > index)) {
					low = mid + 1;
				} else {
					if (ExcelCharacter.compareToAsInExcel(be[index], value) < 0) {
						low = mid + 1;
					} else {
						if (temp2 > value) {
							returnValue[1] = mid;
						}
						temp = mid;
					}
				}
			}
			if (low > high) {
				;
			} else {
				be = this.foodItemList.get(low - 1).getName().toCharArray();
				belenth = be.length;
				if (belenth > index) {
					if ((low < (high + 1))
							&& (ExcelCharacter.compareToAsInExcel(be[index],
									value) == 0))
						returnValue[0] = low;
				} else {
					;
				}
			}
			returnValue[0] = returnValue[0] - 1;
			returnValue[1] = returnValue[1] - 1;
			return returnValue;
		}

		private int searchLast(int low, int high, char value, int index) {
			int temp = 0;
			int mid = 0;
			int returnvalue = -1;
			char[] be;
			int belength;
			low++;
			high++;
			temp = low - 1;
			while (high > temp) {
				if ((high + temp) % 2 > 0) {
					mid = (high + temp) / 2 + 1;
				} else {
					mid = (high + temp) / 2;
				}
				be = this.foodItemList.get(mid - 1).getName().toCharArray();
				belength = be.length;
				if (!(belength > index)) {
					temp = mid;
				} else {
					if (ExcelCharacter.compareToAsInExcel(be[index], value) > 0)
						high = mid - 1;
					else
						temp = mid;
				}
			}
			if (high < low) {
				;
			} else {
				be = this.foodItemList.get(high - 1).getName().toCharArray();
				belength = be.length;
				if (belength > index) {
					if (((low - 1) < high)
							& (ExcelCharacter.compareToAsInExcel(be[index],
									value) == 0))
						returnvalue = high;
				} else {
					;
				}
			}
			returnvalue = returnvalue - 1;
			return returnvalue;
		}
	}
}