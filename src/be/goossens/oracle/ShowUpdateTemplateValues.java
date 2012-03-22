package be.goossens.oracle;

/*
 * When a user loads a template it will first see this screen to update the amound of food and the food unit.
 * Then it will see the ShowSelectedFood page again
 * */

import java.util.ArrayList;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ShowUpdateTemplateValues extends ListActivity {
	private DbAdapter dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_update_template_values);
		dbHelper = new DbAdapter(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		fillData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	private void fillData() {
		CustomArrayAdapterSelectedFoodUpdateValues adapter = new CustomArrayAdapterSelectedFoodUpdateValues(
				this, getSelectedFoodToArray());
		setListAdapter(adapter);
	}

	private ArrayList<DBSelectedFood> getSelectedFoodToArray() {
		ArrayList<DBSelectedFood> returnValue = new ArrayList<DBSelectedFood>();
		Cursor cSelectedFood = dbHelper.fetchAllSelectedFood();
		cSelectedFood.moveToFirst();
		do {
			Cursor cUnit = dbHelper
					.fetchFoodUnit(cSelectedFood.getLong(cSelectedFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)));
			cUnit.moveToFirst();
			Cursor cFood = dbHelper
					.fetchFood(cUnit.getLong(cUnit
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)));
			cFood.moveToFirst();

			returnValue
					.add(new DBSelectedFood(
							cSelectedFood
									.getLong(cSelectedFood
											.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_ID)),
							cSelectedFood.getLong(cSelectedFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT)),
							cSelectedFood.getLong(cSelectedFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)),
							cFood.getString(cFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)),
							cUnit.getString(cUnit
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME))));

			cFood.close();
			cUnit.close();
		} while (cSelectedFood.moveToNext());
		cSelectedFood.close();
		return returnValue;
	}
	
	public void onClickSave(View view){
		//Update all the selectedFoods
		/*CustomArrayAdapterSelectedFoodUpdateValues adapter = (CustomArrayAdapterSelectedFoodUpdateValues)getListAdapter();
		for(int i = 0; i < adapter.getCount(); i++){
			//Toast.makeText(this, "" + adapter.getItem(i), Toast.LENGTH_SHORT).show();
		}*/
		finish();
	}

}
