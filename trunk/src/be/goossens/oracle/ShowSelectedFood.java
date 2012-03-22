package be.goossens.oracle;

/*
 * This class shows the selected food
 * */

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShowSelectedFood extends ListActivity {
	private DbAdapter dbHelper;

	private List<DBSelectedFood> listOfSelectedFood;

	private static final int EDIT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	// Need this id to update all the values afther we updated a selectedFood
	private static final int update_selectedFood_id = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_selected_food);
		dbHelper = new DbAdapter(this);
		listOfSelectedFood = new ArrayList<DBSelectedFood>();
		registerForContextMenu(getListView());
	}

	// converts the cursor with all selected food to a arrayList<DBSelectedFood>
	// and returns the arraylist
	private ArrayList<DBSelectedFood> getSelectedFood() {
		ArrayList<DBSelectedFood> list = new ArrayList<DBSelectedFood>();

		Cursor cSelectedFood = dbHelper.fetchAllSelectedFood();
		if (cSelectedFood.getCount() > 0) {
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

				list.add(new DBSelectedFood(
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
		}
		return list;
	}

	// Show on top Total: amound calories
	private void calculateValues() {
		TextView tvTotal = (TextView) findViewById(R.id.textViewShowTotal);
		// calculate amound of kilocalories
		float totalKcal = 0;
		float totalCarbs = 0;
		float totalProtein = 0;
		float totalFat = 0;

		Cursor cSelectedFood = dbHelper.fetchAllSelectedFood();
		if (cSelectedFood.getCount() > 0) {
			startManagingCursor(cSelectedFood);
			cSelectedFood.moveToFirst();

			do {
				float subKcal, subCarbs, subProtein, subFat;
				Cursor cUnit = dbHelper
						.fetchFoodUnit(cSelectedFood.getLong(cSelectedFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)));
				cUnit.moveToFirst();

				// add the calculated kcal to the total
				subKcal = cUnit
						.getFloat(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL))
						* cSelectedFood
								.getFloat(cSelectedFood
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT));
				// add the calculated carbs to the total
				subCarbs = cUnit
						.getFloat(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_CARBS))
						* cSelectedFood
								.getFloat(cSelectedFood
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT));
				// add the calculated protein to the total
				subProtein = cUnit
						.getFloat(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_PROTEIN))
						* cSelectedFood
								.getFloat(cSelectedFood
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT));
				// add the calculated fat to the total
				subFat = cUnit
						.getFloat(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FAT))
						* cSelectedFood
								.getFloat(cSelectedFood
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT));

				// Update when the unit standardamound == 100
				if (cUnit
						.getInt(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)) == 100) {
					subKcal = subKcal / 100;
					subCarbs = subCarbs / 100;
					subProtein = subProtein / 100;
					subFat = subFat / 100;
				}
				totalKcal += subKcal;
				totalCarbs += subCarbs;
				totalProtein += subProtein;
				totalFat += subFat;

				cUnit.close();
			} while (cSelectedFood.moveToNext());
		}
		cSelectedFood.close();

		tvTotal.setText(getResources().getString(R.string.total) + ": \n" + " "
				+ totalKcal + " "
				+ getResources().getString(R.string.amound_of_kcal) + " \n"
				+ " " + totalCarbs + " "
				+ getResources().getString(R.string.amound_of_carbs) + " \n"
				+ " " + totalProtein + " "
				+ getResources().getString(R.string.amound_of_protein) + " \n"
				+ " " + totalFat + " "
				+ getResources().getString(R.string.amound_of_fat));

	}

	// create the context menu ( display if we long press on a item in the
	// listview )
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, EDIT_ID, 0, R.string.menu_edit);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	// if we select a item in the context menu check if pressed delete or edit.
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		// if pressed delete
		case DELETE_ID:
			dbHelper.deleteSelectedFood(info.id);
			refreshData();
			break;
		// if pressed edit
		// go back to the select page
		case EDIT_ID:
			Cursor cSelectedFood = dbHelper.fetchSelectedFood(info.id);
			cSelectedFood.moveToFirst();
			Intent i = new Intent(this, ShowAddFoodToSelection.class);

			// get the food
			Cursor cUnit = dbHelper
					.fetchFoodUnit(cSelectedFood.getLong(cSelectedFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)));
			cUnit.moveToFirst();
			i.putExtra(DbAdapter.DATABASE_FOOD_ID, cUnit.getLong(cUnit
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)));
			// cant put paramer dbadapter.database_selectedfood_id instead of
			// selectedfoodid becaus in the parameter the value is _id and
			// food_id its paramter is _id to!
			i.putExtra("selectedfoodid", info.id);
			cUnit.close();
			cSelectedFood.close();
			startActivityForResult(i, update_selectedFood_id);
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// without this dbHelper.open the app wil crash when it comes back from
		// ShowAddFoodToSelection
		dbHelper.open();
		switch (requestCode) {
		// if we come back from our update screen refresh all the values
		case update_selectedFood_id:
			refreshData();
			break;
		default:
			refreshData();
			break;
		}
	}

	// to fill the listview with data
	private void fillData() {
		listOfSelectedFood = getSelectedFood();
		if (listOfSelectedFood.size() > 0) {
			CustomBaseAdapterSelectedFood adapter = new CustomBaseAdapterSelectedFood(
					this, listOfSelectedFood);
			setListAdapter(adapter);
		} else {
			// if we delete all items
			// we need to clear the listview
			setListAdapter(null);
		}
	}

	// when we click on the button return
	public void onClickBack(View view) {
		setResult(RESULT_OK);
		finish();
	}

	// when we click on the button delete all
	public void onClickDeleteAll(View view) {
		Cursor allSelectedFood = dbHelper.fetchAllSelectedFood();
		startManagingCursor(allSelectedFood);
		while (allSelectedFood.moveToNext()) {
			dbHelper.deleteSelectedFood(allSelectedFood.getInt(allSelectedFood
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_ID)));
		}
		refreshData();
	}

	// this method will refresh all the data on the screen
	public void refreshData() {
		calculateValues();
		fillData();
		calculateTemplates();
	}

	private void calculateTemplates() {
		Button buttonLoadTemplate = (Button) findViewById(R.id.buttonShowSelectedFoodButtonLoadTemplate);

		Cursor cFoodTemplates = dbHelper.fetchAllFoodTemplates();

		buttonLoadTemplate.setText(""
				+ getResources().getString(R.string.Load_template) + " ("
				+ cFoodTemplates.getCount() + ")");

		cFoodTemplates.close();
	}

	// this method is called when the user press on save as template
	public void onClickSaveAsTemplate(View view) {
		/*
		 * First we check if we have more then 1 selectedFood in the table It
		 * would be stupid to add only 1 selected food to a template
		 */
		if (dbHelper.fetchAllSelectedFood().getCount() > 1) {
			final EditText input = new EditText(this);
			// Show a dialog with a inputbox to insert the template name
			new AlertDialog.Builder(this)
					.setTitle(getResources().getString(R.string.template_name))
					.setView(input)
					.setPositiveButton(getResources().getString(R.string.save),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// on click positive button
									// if inputbox text is longer then ""
									if (input.getText().length() > 0) {
										createNewTemplate(input.getText()
												.toString());
									} else {
										showToast(getResources()
												.getString(
														R.string.template_name_cant_be_empty));
									}
								}
							})
					.setNegativeButton(
							getResources().getString(R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// on click negative button do nothing
								}
							}).show();
		} else {
			Toast.makeText(
					this,
					getResources().getString(
							R.string.add_food_to_template_error),
					Toast.LENGTH_LONG).show();
		}
	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	private void createNewTemplate(String templateName) {
		// Add the selected food to a template
		// We create a new mealType
		Long mealTypeID = dbHelper.createMealType("testOneMealType");
		// Then we add a FoodTemplate with the MealTypeID
		Long foodTemplateID = dbHelper.createFoodTemplate(mealTypeID,
				templateName);
		// then for every selected food row we create a template_food
		Cursor cSelectedFood = dbHelper.fetchAllSelectedFood();
		cSelectedFood.moveToFirst();

		do {
			Cursor cUnit = dbHelper
					.fetchFoodUnit(cSelectedFood.getLong(cSelectedFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)));
			cUnit.moveToFirst();
			dbHelper.createTemplateFood(foodTemplateID, cUnit.getLong(cUnit
					.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)));
			dbHelper.deleteSelectedFood(cSelectedFood.getLong(cSelectedFood
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_ID)));
			cUnit.close();
		} while (cSelectedFood.moveToNext());

		cSelectedFood.close();

		refreshData();
	}

	public void onClickLoadTemplate(View view) {
		if (dbHelper.fetchAllFoodTemplates().getCount() > 0) {
			Intent i = new Intent(this, ShowFoodTemplates.class);
			startActivity(i);
		} else {
			showToast(getResources().getString(R.string.template_load_empty));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		refreshData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}
}
