package be.goossens.oracle.Show.Food;

/*
 * This class will be the first class that starts when we open the activitygroupmeal.
 * This class will hold our foodList and other values we cant lose when we restart the ShowFoodList activity
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.Objects.DBFoodComparable;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.FoodComparator;

public class ShowLoadingFoodData extends Activity {

	// used to show fetching data from database
	private TextView tv2;

	// this is used to see if we need to get stuff from the database
	// when its the first time we come here this boolean is false
	// in the onresume we handle this object
	public boolean notFirstTime;

	// This boolean is used to see if we are finish with gething our data from
	// the database
	// The first time the application starts showFoodList will check this
	// boolean
	// sees that we are still bussy and displayes the loading screen
	// when we finish with gething our data the method will trigger showFoodList
	// to update its listview
	// the next time we display showFoodList ( when we come back from a child
	// activity )
	// ShowFoodList will show the foodlist becaus this boolean says we are donne
	// gething our data
	public boolean finishedGethingData;

	// This context is used in the async task to create a dbadapter
	private Context context;

	// all the food with visible = 1 and foodlanguageID = setting ID
	public List<DBFoodComparable> listAllFood;
	// all the food with visible = 1 and foodlanguageID = setting ID and
	// favorite = 1
	public List<DBFoodComparable> listFavoriteFood;

	// stuff we need to hold for the showFoodList
	public List<DBFoodComparable> listFood;
	public long foodLanguageID;
	public int countSelectedFood;
	public int dbFontSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// show the loading screen
		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_start, null);
		setContentView(contentView);

		tv2 = (TextView) findViewById(R.id.textView2);
		tv2.setText(getResources().getString(R.string.retrievingFood));

		context = this;
	}

	@Override
	protected void onResume() {

		// if not notFirst time ( so its the first time )
		// we only do this 1x when we start activity
		if (!notFirstTime) {
			// we set notFirstTime to true
			notFirstTime = true;

			// set the values fontsize, languageid and countSelectedFood
			setValues();

			// check to see if we have already food items in selectedFood
			// if yes we have to show popup to delete them
			// In the end of this method we will start the asynctask to get our
			// food
			checkToShowPopUpDeleteSelectedFoodItems();
		}

		super.onResume();
	}

	private void checkToShowPopUpDeleteSelectedFoodItems() {
		DbAdapter db = new DbAdapter(context);
		db.open();

		// if selectedFood > 0
		if (db.fetchAllSelectedFood().getCount() > 0) {
			// create popup to show
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						DbAdapter database = new DbAdapter(context);
						database.open();

						Cursor cSelectedFood = database.fetchAllSelectedFood();
						cSelectedFood.moveToFirst();

						do {
							// delete all selected food
							database.deleteSelectedFood(cSelectedFood.getLong(cSelectedFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_ID)));
						} while (cSelectedFood.moveToNext());

						cSelectedFood.close();
						database.close();
						countSelectedFood = 0;
						// start asynctask after we deleted all
						new AsyncGetFoodListOutDatabase().execute();
						break;
					default:
						// start asynctask when we press the no button
						new AsyncGetFoodListOutDatabase().execute();
						break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(
					ActivityGroupMeal.group);
			builder.setMessage(
					context.getResources().getString(
							R.string.do_you_want_to_delete_the_selections))
					.setPositiveButton(
							context.getResources().getString(R.string.yes),
							dialogClickListener)
					.setNegativeButton(
							context.getResources().getString(R.string.no),
							dialogClickListener)
					.setOnKeyListener(new OnKeyListener() {

						public boolean onKey(DialogInterface dialog,
								int keyCode, KeyEvent event) {
							// when we press the back button to close the dialog
							// we have to start the async task to!
							if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
								new AsyncGetFoodListOutDatabase().execute();
							return false;
						}
					}).show();
		} else {
			// run a async task when we dont have selectedFood
			new AsyncGetFoodListOutDatabase().execute();
		}

		db.close();
	}

	private void setValues() {
		// this method will set the count selected food
		// the foodlanguageid and the fontsize

		countSelectedFood = 0;

		// we put these 3 methods in 1 super method so we can use 1 database
		// connection
		DbAdapter db = new DbAdapter(context);
		db.open();
		setCountSelectedFood(db);
		setFoodLanguageID(db);
		setFontSize(db);
		db.close();
	}

	private void setCountSelectedFood(DbAdapter db) {
		countSelectedFood = db.fetchAllSelectedFood().getCount();
	}

	private void setFontSize(DbAdapter db) {
		Cursor cSetting = db.fetchSettingByName(getResources().getString(
				R.string.setting_font_size));
		if (cSetting.getCount() > 0) {
			cSetting.moveToFirst();
			dbFontSize = cSetting.getInt(cSetting
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));
		} else {
			dbFontSize = 20;
		}
		cSetting.close();
	}

	// This method is called from show settings font size
	// When the user presses on a list item to set new font size
	// This method will set the new font size on this page
	// and reload the showFoodList
	public void setNewFontSize() {
		DbAdapter db = new DbAdapter(context);
		db.open();
		// get the new font size
		setFontSize(db);
		db.close();
		// refresh the foodlist
		ActivityGroupMeal.group.showFoodListUpdateListAdapter();
	}

	private void setFoodLanguageID(DbAdapter db) {

		// First get the food language id
		Cursor cSetting = db.fetchSettingByName(getResources().getString(
				R.string.setting_language));
		// there will always be 1 record
		if (cSetting.getCount() > 0) {
			// move the cursor to the first record
			cSetting.moveToFirst();
			// put the foodLanguageID in the variable
			foodLanguageID = cSetting.getLong(cSetting
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));
		} else {
			// if we dont have the record ( something went wrong )
			// and we set foodLanguage to 1 so the app wont crash
			foodLanguageID = 1;
		}

		// close the cursor
		cSetting.close();

	}

	private class AsyncGetFoodListOutDatabase extends
			AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// we are not finished gething our data
			finishedGethingData = false;

			// create new dbHelper object
			DbAdapter dbHelper = new DbAdapter(context);

			// initialise the lists
			listAllFood = new ArrayList<DBFoodComparable>();
			listFavoriteFood = new ArrayList<DBFoodComparable>();

			// open database helper object
			dbHelper.open();

			// get all the food items with the right languageID and visible = 1
			Cursor cAllFood = dbHelper.fetchFoodByLanguageID(foodLanguageID);
			// if we have records
			if (cAllFood.getCount() > 0) {
				// move to first record
				cAllFood.moveToFirst();
				// do this loop for every record
				do {
					// create a DBFoodComparable object
					DBFoodComparable newFood = new DBFoodComparable(
							cAllFood.getLong(cAllFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
							cAllFood.getString(cAllFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_PLATFORM)),
							cAllFood.getLong(cAllFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_FOODLANGUAGEID)),
							cAllFood.getInt(cAllFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_VISIBLE)),
							cAllFood.getLong(cAllFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_CATEGORYID)),
							cAllFood.getLong(cAllFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_USERID)),
							cAllFood.getInt(cAllFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ISFAVORITE)),
							cAllFood.getString(cAllFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)));
					// add the new food to the list
					listAllFood.add(newFood);
				} while (cAllFood.moveToNext());
			}
			// close the cursor
			cAllFood.close();

			// get all the food items with the right languageID and visible = 1
			// and favorite = 1
			Cursor cFavoriteFood = dbHelper.fetchFoodByLanguageIDAndFavorite(foodLanguageID);
			// if we have records
			if (cFavoriteFood.getCount() > 0) {
				// move to first record
				cFavoriteFood.moveToFirst();
				// do this loop for every record
				do {
					// create a DBFoodComparable object
					DBFoodComparable newFood = new DBFoodComparable(
							cFavoriteFood
									.getLong(cFavoriteFood
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
							cFavoriteFood.getString(cFavoriteFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_PLATFORM)),
							cFavoriteFood.getLong(cFavoriteFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_FOODLANGUAGEID)),
							cFavoriteFood.getInt(cFavoriteFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_VISIBLE)),
							cFavoriteFood.getLong(cFavoriteFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_CATEGORYID)),
							cFavoriteFood.getLong(cFavoriteFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_USERID)),
							cFavoriteFood.getInt(cFavoriteFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ISFAVORITE)),
							cFavoriteFood.getString(cFavoriteFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)));
					// add the new food to the list
					listFavoriteFood.add(newFood);
				} while (cFavoriteFood.moveToNext());
			}
			// close the cursor
			cFavoriteFood.close();

			// close dbHelper object
			dbHelper.close();

			// sort the 2 lists and create a total list
			recreateTotalList();

			return null;
		}

		// when we finished getting the food list out the database and sorting
		// it
		@Override
		protected void onPostExecute(Void result) {
			// only do this the first time
			if (!finishedGethingData) {
				// we are finished gething our data
				finishedGethingData = true;

				// create the showfoodlist page
				Intent i = new Intent(context, ShowFoodList.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				View v = ActivityGroupMeal.group.getLocalActivityManager()
						.startActivity(DataParser.activityIDShowFoodList, i)
						.getDecorView();
				ActivityGroupMeal.group.setContentView(v);
			}
			super.onPostExecute(result);
		}
	}

	// this method will sort the 2 lists and recreate the total list
	public void recreateTotalList() {
		FoodComparator comparator = new FoodComparator();
		
		// sort all the food items
		Collections.sort(listAllFood, comparator);
		
		// sort all the favorite food items
		Collections.sort(listFavoriteFood, comparator);
 
		// set list to empty
		listFood = null;
		// Initialise the total list of food 
		listFood = new ArrayList<DBFoodComparable>();
		// put the favorites and the list with all food in this list
		listFood.addAll(listFood.size(), listFavoriteFood);
		listFood.addAll(listFood.size(), listAllFood);
	}
 
	// This method is called from showfoodlist when we change favorite
	public void setIsFavoriteFromFoodListAll(long foodID, int isFavorite) {
		for(DBFoodComparable obj : listAllFood){
			if(obj.getId() == foodID){
				listAllFood.get(listAllFood.indexOf(obj)).setIsfavorite(isFavorite);
				return;
			}
		}
	}
 
	// This method is called from showfoodlist when we unmark a fooditem as
	// favorite
	public void deleteFoodFromFavoriteList(long foodID) {
		for(DBFoodComparable obj : listFavoriteFood){
			if(obj.getId() == foodID){
				listFavoriteFood.remove(obj);
				return;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// return true so the application wont exit
		return true;
	}
}
