package be.goossens.oracle.Show.Settings;

import java.util.ArrayList;

import be.goossens.oracle.R;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ShowSettingsMealTimes extends ListActivity {
	private DbAdapter dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_meal_times);

		dbHelper = new DbAdapter(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		fillData();
	}

	// This function will return a arraylist with the times
	// This arraylist is used in our listview
	private ArrayList<String> createArrayList() {
		ArrayList<String> list = new ArrayList<String>();

		// get the times out the database
		// breakfast
		Cursor cBreakfastTime = dbHelper.fetchSettingByName(getResources()
				.getString(R.string.meal_time_breakfast));
		cBreakfastTime.moveToFirst();
		// lunch
		Cursor cLunchTime = dbHelper.fetchSettingByName(getResources()
				.getString(R.string.meal_time_lunch));
		cLunchTime.moveToFirst();
		// snack
		Cursor cSnackTime = dbHelper.fetchSettingByName(getResources()
				.getString(R.string.meal_time_snack));
		cSnackTime.moveToFirst();
		// dinner
		Cursor cDinnerTime = dbHelper.fetchSettingByName(getResources()
				.getString(R.string.meal_time_dinner));
		cDinnerTime.moveToFirst();

		// Fill the list with the times
		list.add(getResources().getString(R.string.pref_breakfastratio_title)
				+ " "
				+ getResources().getString(R.string.starts_at)
				+ " "
				+ cBreakfastTime.getString(cBreakfastTime
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		list.add(getResources().getString(R.string.pref_lunchratio_title)
				+ " "
				+ getResources().getString(R.string.starts_at)
				+ " "
				+ cLunchTime.getString(cLunchTime
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		list.add(getResources().getString(R.string.pref_snackratio_title)
				+ " "
				+ getResources().getString(R.string.starts_at)
				+ " "
				+ cSnackTime.getString(cSnackTime
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		list.add(getResources().getString(R.string.pref_dinnerratio_title)
				+ " "
				+ getResources().getString(R.string.starts_at)
				+ " "
				+ cDinnerTime.getString(cDinnerTime
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));

		// Close all the cursors
		cDinnerTime.close();
		cSnackTime.close();
		cLunchTime.close();
		cBreakfastTime.close();

		return list;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// This list has 4 items in it ( 0 = breakfast, 1 = lunch, 2 = snack and
		// 3 = dinner ).
		// See which item is clicked and go to the page to change the time
		Intent i = new Intent(this, ShowSettingsUpdateMealTime.class);
		switch ((int) id) {
		case 0:
			// put the breakfast key in the intent
			i.putExtra(DataParser.fromWhereWeCome,
					getResources().getString(R.string.meal_time_breakfast));
			break;
		case 1:
			// put the lunch key in the intent
			i.putExtra(DataParser.fromWhereWeCome,
					getResources().getString(R.string.meal_time_lunch));
			break;
		case 2:
			// put the snack key in the intent
			i.putExtra(DataParser.fromWhereWeCome,
					getResources().getString(R.string.meal_time_snack));
			break;
		case 3:
			// put the dinner key in the intent
			i.putExtra(DataParser.fromWhereWeCome,
					getResources().getString(R.string.meal_time_dinner));
			break;
		default:
			// default put the breakfast key in the intent
			i.putExtra(DataParser.fromWhereWeCome,
					getResources().getString(R.string.meal_time_breakfast));
			break;
		}
		// launch the activity
		startActivityForResult(i, 0);
	}

	// when we come back from our update setting meal time we have to refresh
	// the data
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//only refresh data is resultCode = result_ok ( this is when we pressed saved in the update meal time )
		// when we click on the back button in update mealtime the data wont refresh
		if (resultCode == RESULT_OK) {
			dbHelper.open();
			fillData();
		}
	}

	private void fillData() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, createArrayList());
		setListAdapter(adapter);
	} 

	// on click back button
	public void onClickBack(View view) {
		finish();
	}

	@Override
	protected void onPause() {
		dbHelper.close();
		super.onPause();
	}

}
