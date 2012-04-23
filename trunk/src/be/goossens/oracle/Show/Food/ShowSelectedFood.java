package be.goossens.oracle.Show.Food;

/*
 * This class shows the selected food
 * */

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.ActivityGroup.ActivityGroupTracking;
import be.goossens.oracle.Custom.CustomBaseAdapterSelectedFood;
import be.goossens.oracle.Objects.DBSelectedFood;
import be.goossens.oracle.Objects.DBValueOrder;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.Functions;
import be.goossens.oracle.Rest.ValueOrderComparator;
import be.goossens.oracle.Show.ShowHomeTab;

public class ShowSelectedFood extends ListActivity {
	private DbAdapter dbHelper;

	private List<DBSelectedFood> listOfSelectedFood;

	private static final int EDIT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	// Need this id to update all the values afther we updated a selectedFood
	private static final int update_selectedFood_id = 0;

	private boolean saveFoodAmount;
	private String templateName;

	private float fInsulineRatio;
	private float fCorrectionFactor;
	private float fCalculatedInsulineAmount;

	private List<DBValueOrder> listValueOrders;

	private Button btDelete, btSaveTemplate, btLoadTemplate,
			btAddSelectedFoodToTracking;

	private Functions functions;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_selected_food, null);
		setContentView(contentView);

		functions = new Functions();

		btDelete = (Button) findViewById(R.id.buttonDelete);
		btSaveTemplate = (Button) findViewById(R.id.buttonSaveAsTemplate);
		btLoadTemplate = (Button) findViewById(R.id.buttonShowSelectedFoodButtonLoadTemplate);
		btAddSelectedFoodToTracking = (Button) findViewById(R.id.buttonAddSelectedFoodToTracking);

		saveFoodAmount = false;

		fInsulineRatio = 0f;
		fCorrectionFactor = 0f;
		fCalculatedInsulineAmount = 0f;

		dbHelper = new DbAdapter(this);

		listOfSelectedFood = new ArrayList<DBSelectedFood>();
		registerForContextMenu(getListView());

		btAddSelectedFoodToTracking.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickAddSelectedFoodToTracking();
			}
		});

		btDelete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickDeleteAll(v);
			}
		});

		btSaveTemplate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickSaveAsTemplate(v);
			}
		});

		btLoadTemplate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickLoadTemplate(v);
			}
		});
	}

	// Click on button add selected food to tracking
	private void onClickAddSelectedFoodToTracking() {
		dbHelper.open();
		Cursor cSelectedFood = dbHelper.fetchAllSelectedFood();

		// only create objects if we have something in the selectedFood list
		if (cSelectedFood.getCount() > 0) {
			// create a meal event
			long lMealEventID = dbHelper.createMealEvent(fInsulineRatio,
					fCorrectionFactor, fCalculatedInsulineAmount,
					new Functions().getDateAsStringFromCalendar(Calendar
							.getInstance()));

			// create for every food in the selectedFood a mealFood object
			cSelectedFood.moveToFirst();
			do {
				// get the foodUnit ( we need it for the foodID )
				Cursor cUnit = dbHelper
						.fetchFoodUnit(cSelectedFood.getLong(cSelectedFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)));
				// move the foodUnit to the first object in the cursor
				cUnit.moveToFirst();

				// create the mealFood
				dbHelper.createMealFood(
						lMealEventID,
						cUnit.getInt(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)),
						cSelectedFood.getFloat(cSelectedFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT)));

				// close the foodUnit cursor
				cUnit.close();
			} while (cSelectedFood.moveToNext());
		}

		cSelectedFood.close();

		// delete selected food from selectedFood table in database
		deleteSelectedFood();

		// kill this activity
		ActivityGroupMeal.group.back();
		// set button with selectedFoodCount = 0
		ActivityGroupMeal.group.showFoodListsetCountSelectedFood(0);

		// refresh tracking list
		ActivityGroupTracking.group.showTrackingRefreshList();

		// Go to tracking activity
		ShowHomeTab parentActivity;
		parentActivity = (ShowHomeTab) this.getParent().getParent();
		parentActivity.goToTab(DataParser.activityIDTracking);
	}

	// converts the cursor with all selected food to a arrayList<DBSelectedFood>
	// and returns the arraylist
	private ArrayList<DBSelectedFood> getSelectedFood() {
		ArrayList<DBSelectedFood> list = new ArrayList<DBSelectedFood>();
		dbHelper.open();
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

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		startActivityUpdateSelectedFood(id);
	}

	// This will set the total calculated values on top of the page
	private void calculateValues() {
		TextView tvTotalNames = (TextView) findViewById(R.id.textViewShowTotalNames);
		TextView tvTotalValues = (TextView) findViewById(R.id.textViewShowTotalValues);
		TextView tvInsuline = (TextView) findViewById(R.id.textViewInsuline);

		// calculate amound of kilocalories
		float totalKcal = 0;
		float totalCarbs = 0;
		float totalProtein = 0;
		float totalFat = 0;
		fCalculatedInsulineAmount = 0;
		dbHelper.open();
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

		// insuline ratio
		// Get all the needed settings
		Cursor cSettingsBreakfastTime = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.setting_meal_time_breakfast));
		cSettingsBreakfastTime.moveToFirst();
		Cursor cSettingsLunchTime = dbHelper.fetchSettingByName(getResources()
				.getString(R.string.setting_meal_time_lunch));
		cSettingsLunchTime.moveToFirst();
		Cursor cSettingsSnackTime = dbHelper.fetchSettingByName(getResources()
				.getString(R.string.setting_meal_time_snack));
		cSettingsSnackTime.moveToFirst();
		Cursor cSettingsDinnerTime = dbHelper.fetchSettingByName(getResources()
				.getString(R.string.setting_meal_time_dinner));
		cSettingsDinnerTime.moveToFirst();

		// Make all the needed variables
		Date currentTime = new Date();
		Date dateBreakfastTime = new Date();
		Date dateLunchTime = new Date();
		Date dateSnackTime = new Date();
		Date dateDinnerTime = new Date();

		// Set all the needed variables
		dateBreakfastTime = transformStringToDate(cSettingsBreakfastTime
				.getString(cSettingsBreakfastTime
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		dateLunchTime = transformStringToDate(cSettingsLunchTime
				.getString(cSettingsLunchTime
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		dateSnackTime = transformStringToDate(cSettingsSnackTime
				.getString(cSettingsSnackTime
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		dateDinnerTime = transformStringToDate(cSettingsDinnerTime
				.getString(cSettingsDinnerTime
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));

		Cursor cInsulineRatio = null;

		// see if it is breakfast time
		// If current time is after breakfast time but before lunch time,
		// then its breakfast time!
		if (currentTime.after(dateBreakfastTime)
				&& currentTime.before(dateLunchTime)) {
			cInsulineRatio = dbHelper.fetchSettingByName(getResources()
					.getString(R.string.setting_insuline_ratio_breakfast));
		} else if (currentTime.after(dateLunchTime)
				&& currentTime.before(dateSnackTime)) {
			cInsulineRatio = dbHelper.fetchSettingByName(getResources()
					.getString(R.string.setting_insuline_ratio_lunch));
		} else if (currentTime.after(dateSnackTime)
				&& currentTime.before(dateDinnerTime)) {
			cInsulineRatio = dbHelper.fetchSettingByName(getResources()
					.getString(R.string.setting_insuline_ratio_snack));
		} else {
			cInsulineRatio = dbHelper.fetchSettingByName(getResources()
					.getString(R.string.setting_insuline_ratio_dinner));
		}

		cInsulineRatio.moveToFirst();
		fInsulineRatio = cInsulineRatio.getFloat(cInsulineRatio
				.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));

		// close all cursors
		cSettingsDinnerTime.close();
		cSettingsSnackTime.close();
		cSettingsLunchTime.close();
		cSettingsBreakfastTime.close();
		cInsulineRatio.close();

		// set the right insuline if fInsulineRatio != 0
		if (fInsulineRatio != 0)
			fCalculatedInsulineAmount = totalCarbs / fInsulineRatio;

		// Round the calculated floats
		totalCarbs = functions.roundFloats(totalCarbs, 1);
		totalProtein = functions.roundFloats(totalProtein, 1);
		totalFat = functions.roundFloats(totalFat, 1);
		totalKcal = functions.roundFloats(totalKcal, 1);
		fCalculatedInsulineAmount = functions.roundFloats(
				fCalculatedInsulineAmount, 1);

		// set the text on the textview in the right order
		String tvNameText = " ";
		String tvValueText = " ";

		for (DBValueOrder obj : listValueOrders) {
			if (obj.getSettingName()
					.equals(getResources().getString(
							R.string.setting_value_order_carb))) {
				// add the carbs to the total
				tvNameText += getResources()
						.getString(R.string.amound_of_carbs) + " \n ";
				tvValueText += totalCarbs + " \n ";
			} else if (obj.getSettingName()
					.equals(getResources().getString(
							R.string.setting_value_order_prot))) {
				// add the prot to the total
				tvNameText += getResources().getString(
						R.string.amound_of_protein)
						+ " \n ";
				tvValueText += totalProtein + " \n ";
			} else if (obj.getSettingName().equals(
					getResources().getString(R.string.setting_value_order_fat))) {
				// add the fat to the total
				tvNameText += getResources().getString(R.string.amound_of_fat)
						+ " \n ";
				tvValueText += totalFat + " \n ";

			} else if (obj.getSettingName()
					.equals(getResources().getString(
							R.string.setting_value_order_kcal))) {
				// add the kcal to the total
				tvNameText += getResources().getString(R.string.amound_of_kcal)
						+ " \n ";
				tvValueText += totalKcal + " \n ";
			}
		}

		// add insuline ratio to the string when the radio != 0
		if (fInsulineRatio != 0) {
			tvInsuline.setText(fCalculatedInsulineAmount + " "
					+ getResources().getString(R.string.insulineUnit) + " "
					+ getResources().getString(R.string.insuline) + " " + "(à "
					+ functions.roundFloats(fInsulineRatio, 1) + " "
					+ getResources().getString(R.string.insulineRatio) + ")");
		} else {
			tvInsuline.setText("");
		}

		//remove the last \n from the strings 
		tvNameText = tvNameText.substring(0,tvNameText.length() - 2);  
		tvValueText = tvValueText.substring(0,tvValueText.length() - 2);
		
		// tvTotal.setText(tvText);
		tvTotalNames.setText(tvNameText);
		tvTotalValues.setText(tvValueText);
	}

	// This function will transform a string to a date object
	// Example "06:00" to a date object with time 6hours 0minutes
	private Date transformStringToDate(String time) {
		Date value = new Date();
		value.setHours(Integer.parseInt(time.substring(0, time.indexOf(":"))));
		value.setMinutes(Integer.parseInt(time.substring(time.indexOf(":") + 1)));
		return value;
	}

	// create the context menu ( display if we long press on a item in the
	// listview )
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, EDIT_ID, 0, R.string.update);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	// if we select a item in the context menu check if pressed delete or edit.
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		/*
		 * switch (item.getItemId()) { // if pressed delete case DELETE_ID:
		 * dbHelper.deleteSelectedFood(info.id); refreshData(); break; // if
		 * pressed edit // go back to the select page case EDIT_ID:
		 * startActivityUpdateSelectedFood(info.id); break; }
		 */
		return super.onContextItemSelected(item);
	}

	private void startActivityUpdateSelectedFood(long selectedFoodId) {
		dbHelper.open();
		Cursor cSelectedFood = dbHelper.fetchSelectedFood(selectedFoodId);
		cSelectedFood.moveToFirst();

		// get the food
		Cursor cUnit = dbHelper
				.fetchFoodUnit(cSelectedFood.getLong(cSelectedFood
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)));
		cUnit.moveToFirst();
		Intent i = new Intent(this, ShowAddFoodToSelection.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
				.putExtra(DataParser.fromWhereWeCome,
						DataParser.weComeFRomShowSelectedFood)
				.putExtra(
						DataParser.idFood,
						cUnit.getLong(cUnit
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)))
				.putExtra(DataParser.idSelectedFood, selectedFoodId);

		View v = ActivityGroupMeal.group.getLocalActivityManager()
				.startActivity(DataParser.activityIDShowFoodList, i).getDecorView();
		ActivityGroupMeal.group.setContentView(v);

		cUnit.close();
		cSelectedFood.close();
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
		dbHelper.open();
		if (listOfSelectedFood.size() > 0) {
			Cursor cSettings = dbHelper.fetchSettingByName(getResources()
					.getString(R.string.setting_font_size));
			cSettings.moveToFirst();
			CustomBaseAdapterSelectedFood adapter = new CustomBaseAdapterSelectedFood(
					this,
					listOfSelectedFood, 
					cSettings.getInt(cSettings
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
			setListAdapter(adapter);
			cSettings.close();
		} else {
			// if we delete all items
			// we need to clear the listview
			setListAdapter(null);
		}
	}

	// when we click on the button delete all
	public void onClickDeleteAll(View view) {
		deleteSelectedFood();
		// update the button with count = 0;
		ActivityGroupMeal.group.showFoodListsetCountSelectedFood(0);
		// refresh the data
		refreshData();
	}

	private void deleteSelectedFood() {
		dbHelper.open();
		Cursor cSelectedFood = dbHelper.fetchAllSelectedFood();
		if (cSelectedFood.getCount() > 0) {
			cSelectedFood.moveToFirst();
			do {
				dbHelper.deleteSelectedFood(cSelectedFood.getInt(cSelectedFood
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_ID)));
			} while (cSelectedFood.moveToNext());
		}
		cSelectedFood.close();
	}

	// this method will refresh all the data on the screen
	public void refreshData() {
		calculateValues();
		fillData();
		calculateTemplates();
	}

	private void calculateTemplates() {
		Button buttonLoadTemplate = (Button) findViewById(R.id.buttonShowSelectedFoodButtonLoadTemplate);
		dbHelper.open();
		Cursor cFoodTemplates = dbHelper.fetchAllFoodTemplates();
 
		buttonLoadTemplate.setText(" ("
				+ cFoodTemplates.getCount() + ")");

		cFoodTemplates.close();
	}

	// this method is called when the user press on save as template
	public void onClickSaveAsTemplate(View view) {
		dbHelper.open();
		/*
		 * First we check if we have more then 1 selectedFood in the table It
		 * would be stupid to add only 1 selected food to a template
		 */
		if (dbHelper.fetchAllSelectedFood().getCount() > 1) {
			final EditText input = new EditText(this);
			// Show a dialog with a inputbox to insert the template name
			new AlertDialog.Builder(ActivityGroupMeal.group)
					.setTitle(getResources().getString(R.string.template_name))
					.setView(input)
					.setPositiveButton(getResources().getString(R.string.save),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// on click positive button
									// if inputbox text is longer then ""
									if (input.getText().length() > 0) {
										// Show dialog to ask if we need to save
										// food amount
										templateName = input.getText()
												.toString();
										showDialogSaveFoodAmount();
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

	private void showDialogSaveFoodAmount() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					saveFoodAmount = true;
					break;
				default:
					saveFoodAmount = false;
					break;
				}
				createNewTemplate();
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupMeal.group);
		builder.setMessage(
				getResources().getString(
						R.string.showSelectedFoodPopupAddAmountToTemplate))
				.setPositiveButton(getResources().getString(R.string.yes),
						dialogClickListener)
				.setNegativeButton(getResources().getString(R.string.no),
						dialogClickListener).show();
	}

	private void createNewTemplate() {
		dbHelper.open();
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
			float amount = 0f;
			if (saveFoodAmount)
				amount = cSelectedFood
						.getFloat(cSelectedFood
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT));

			dbHelper.createTemplateFood(
					foodTemplateID,
					cSelectedFood.getLong(cSelectedFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITID)),
					amount);

		} while (cSelectedFood.moveToNext());

		cSelectedFood.close();

		refreshData();
	}

	public void onClickLoadTemplate(View view) {
		dbHelper.open();
		if (dbHelper.fetchAllFoodTemplates().getCount() > 0) {
			Intent i = new Intent(this, ShowFoodTemplates.class)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			View v = ActivityGroupMeal.group.getLocalActivityManager()
					.startActivity(DataParser.activityIDShowFoodList, i).getDecorView();
			ActivityGroupMeal.group.setContentView(v);
		} else {
			showToast(getResources().getString(R.string.template_load_empty));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		dbHelper.open();
		fillListValueOrders();
		refreshData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	// This method will fill the list of DBValueOrders with the right values
	private void fillListValueOrders() {
		dbHelper.open();
		// make the list empty
		listValueOrders = new ArrayList<DBValueOrder>();

		// get all the value orders
		Cursor cSettingValueOrderProt = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.setting_value_order_prot));
		Cursor cSettingValueOrderCarb = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.setting_value_order_carb));
		Cursor cSettingValueOrderFat = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.setting_value_order_fat));
		Cursor cSettingValueOrderKcal = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.setting_value_order_kcal));

		// Move cursors to first object
		cSettingValueOrderProt.moveToFirst();
		cSettingValueOrderCarb.moveToFirst();
		cSettingValueOrderFat.moveToFirst();
		cSettingValueOrderKcal.moveToFirst();

		// Fill list
		listValueOrders
				.add(new DBValueOrder(
						cSettingValueOrderProt
								.getInt(cSettingValueOrderProt
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)),
						cSettingValueOrderProt.getString(cSettingValueOrderProt
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_NAME)),
						getResources().getString(R.string.amound_of_protein)));

		listValueOrders
				.add(new DBValueOrder(
						cSettingValueOrderCarb
								.getInt(cSettingValueOrderCarb
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)),
						cSettingValueOrderCarb.getString(cSettingValueOrderCarb
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_NAME)),
						getResources().getString(R.string.amound_of_carbs)));

		listValueOrders
				.add(new DBValueOrder(
						cSettingValueOrderFat
								.getInt(cSettingValueOrderFat
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)),
						cSettingValueOrderFat.getString(cSettingValueOrderFat
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_NAME)),
						getResources().getString(R.string.amound_of_fat)));

		listValueOrders
				.add(new DBValueOrder(
						cSettingValueOrderKcal
								.getInt(cSettingValueOrderKcal
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)),
						cSettingValueOrderKcal.getString(cSettingValueOrderKcal
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_NAME)),
						getResources().getString(R.string.amound_of_kcal)));

		// Close all the cursor
		cSettingValueOrderProt.close();
		cSettingValueOrderCarb.close();
		cSettingValueOrderFat.close();
		cSettingValueOrderKcal.close();

		// Sort the list on order
		ValueOrderComparator comparator = new ValueOrderComparator();
		Collections.sort(listValueOrders, comparator);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
