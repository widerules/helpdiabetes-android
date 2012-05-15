// Please read info.txt for license and legal information

package be.goossens.oracle.Show.Tracking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.ActivityGroup.ActivityGroupTracking;
import be.goossens.oracle.Custom.CustomArrayAdapterDBTracking;
import be.goossens.oracle.Objects.DBBloodGlucoseEvent;
import be.goossens.oracle.Objects.DBExerciseEvent;
import be.goossens.oracle.Objects.DBFoodUnit;
import be.goossens.oracle.Objects.DBMealEvent;
import be.goossens.oracle.Objects.DBMealFood;
import be.goossens.oracle.Objects.DBMedicineEvent;
import be.goossens.oracle.Objects.DBTracking;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.Functions;
import be.goossens.oracle.Rest.TimeComparator;
import be.goossens.oracle.Show.ShowHomeTab;

public class ShowTracking extends ListActivity {
	private List<DBTracking> listTracking;
	private CustomArrayAdapterDBTracking adapter;
	private TextView tvFetchingData;
	private boolean thereAreMoreItems;
	private Calendar calendarTime;
	private Button btMore;
	private boolean setSelectedItemToLastOne;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_tracking);

		calendarTime = Calendar.getInstance();
		
		calendarMinusOneMonth();

		btMore = (Button) findViewById(R.id.buttonMore);
		//standard hide the button
		btMore.setVisibility(View.GONE);
		
		listTracking = new ArrayList<DBTracking>();
		adapter = null;
		tvFetchingData = (TextView) findViewById(R.id.textViewFetchingData);

		btMore.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setSelectedItemToLastOne = true;
				calendarMinusOneMonth();
				refresh();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		tvFetchingData.setVisibility(View.VISIBLE);
		new DoInBackground().execute();
	}

	private void refresh() {
		// hide button see more
		btMore.setVisibility(View.GONE);

		setListAdapter(null);
		tvFetchingData.setVisibility(View.VISIBLE);
		new DoInBackground().execute();
	}

	private class DoInBackground extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			setListTracking();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			fillListView();
			tvFetchingData.setVisibility(View.GONE);
 
			// when there is more to show then show the button
			if (thereAreMoreItems)
				btMore.setVisibility(View.VISIBLE);

			if(setSelectedItemToLastOne){
				setSelection(listTracking.size());
			}
			
			super.onPostExecute(result);
		}
	}

	private void fillListView() {
		if (listTracking.size() <= 0) {
			// create the list
			listTracking = new ArrayList<DBTracking>();
			// Fill the list with 1 item "no records found"
			listTracking
					.add(new DBTracking(null, null, null, null, null, false,
							getResources().getString(R.string.noTrackingValues)));
		}

		adapter = new CustomArrayAdapterDBTracking(this, 0, listTracking,
				ActivityGroupMeal.group.getFoodData().dbFontSize,
				ActivityGroupMeal.group.getFoodData().defaultValue);

		setListAdapter(adapter);

	}

	private void calendarMinusOneMonth() {
		// do - 1 month standard
		calendarTime.add(Calendar.MONTH, -1);
	}

	// This method will fill the listTracking with all the data from the db
	private void setListTracking() {
		DbAdapter dbHelper = new DbAdapter(this);
		dbHelper.open();

		// clear the list and create the object
		listTracking = new ArrayList<DBTracking>();
		// needed to temp store the dates in
		List<Date> listDates = new ArrayList<Date>();
		// get all needed objects
		Cursor cDates = dbHelper.fetchAllTimestamps();

		// fill listDates with all the dates
		if (cDates.getCount() > 0) {
			cDates.moveToFirst();
			do {
				Date tempDate = new Functions()
						.getYearMonthDayAsDateFromString(cDates.getString(cDates
								.getColumnIndexOrThrow(new DataParser().timestamp)));
				 
				if (tempDate.after(calendarTime.getTime()) || tempDate.equals(calendarTime.getTime())) { 
					thereAreMoreItems = false;
					listDates.add(tempDate);
				} else {
					// if we get here there are more items so we flag the
					// boolean to show button " see more "
					thereAreMoreItems = true;
				}

			} while (cDates.moveToNext());
		}
		cDates.close();

		for (Date date : listDates) {
			// For each date object add it to the listview
			listTracking.add(new DBTracking(null, null, null, null, date, true,
					""));

			// create a temporary list with all the events from the date so we
			// can sort it by time
			List<DBTracking> listTempDBTracking = new ArrayList<DBTracking>();

			// For each date get the exercise events
			Cursor cExerciseEvents = dbHelper
					.fetchExerciseEventByDate(new Functions()
							.getYearMonthDayAsStringFromDate(date));
			if (cExerciseEvents.getCount() > 0) {
				cExerciseEvents.moveToFirst();
				do {
					Cursor cExerciseType = dbHelper
							.fetchExerciseTypeByID(cExerciseEvents.getLong(cExerciseEvents
									.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EXERCISETYPEID)));
					cExerciseType.moveToFirst();
					// new DBTracking(exerciseEvent, mealEvent,
					// bloodGlucoseEvent, medicineEvent, timestamp, noRecors)
					listTempDBTracking
							.add(new DBTracking(
									new DBExerciseEvent(
											cExerciseEvents
													.getLong(cExerciseEvents
															.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_ID)),
											cExerciseEvents.getString(cExerciseEvents
													.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_DESCRIPTION)),
											cExerciseEvents.getInt(cExerciseEvents
													.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_STARTTIME)),
											cExerciseEvents.getInt(cExerciseEvents
													.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_STOPTIME)),
											cExerciseEvents.getString(cExerciseEvents
													.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EVENTDATETIME)),
											cExerciseEvents.getLong(cExerciseEvents
													.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EXERCISETYPEID)),
											cExerciseEvents.getLong(cExerciseEvents
													.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_USERID)),
											cExerciseType.getString(cExerciseType
													.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_NAME))),
									null,
									null,
									null,
									new Functions()
											.getYearMonthDayHourMinutesAsDateFromString(cExerciseEvents.getString(cExerciseEvents
													.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EVENTDATETIME))),
									false, ""));

					cExerciseType.close();
				} while (cExerciseEvents.moveToNext());
			}
			cExerciseEvents.close();

			// For each date get the meal event
			Cursor cMealEvents = dbHelper.fetchMealEventsByDate(new Functions()
					.getYearMonthDayAsStringFromDate(date));

			if (cMealEvents.getCount() > 0) {
				cMealEvents.moveToFirst();
				do {
					List<DBMealFood> listDBMealFood = new ArrayList<DBMealFood>();
					Cursor cMealFood = dbHelper
							.fetchMealFoodByMealEventID(cMealEvents.getLong(cMealEvents
									.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_ID)));
					if (cMealFood.getCount() > 0) {
						cMealFood.moveToFirst();
						do {
							// fill the list with meal foods

							// to do so we first get the unit that belongs to
							// this mealfood
							Cursor cUnit = dbHelper
									.fetchFoodUnit(cMealFood.getLong(cMealFood
											.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALFOOD_FOODUNITID)));
							cUnit.moveToFirst();
							Cursor cFood = dbHelper
									.fetchFood(cUnit.getLong(cUnit
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)));
							cFood.moveToFirst();

							// fill cmealfood
							listDBMealFood
									.add(new DBMealFood(
											cMealFood
													.getLong(cMealFood
															.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALFOOD_ID)),
											cFood.getString(cFood
													.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)),
											cMealFood.getFloat(cMealFood
													.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALFOOD_AMOUNT)),
											new DBFoodUnit(
													cUnit.getLong(cUnit
															.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_ID)),
													cUnit.getString(cUnit
															.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_NAME)),
													cUnit.getString(cUnit
															.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_DESCRIPTION)),
													cUnit.getFloat(cUnit
															.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT)),
													cUnit.getFloat(cUnit
															.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_KCAL)),
													cUnit.getFloat(cUnit
															.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_PROTEIN)),
													cUnit.getFloat(cUnit
															.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_CARBS)),
													cUnit.getFloat(cUnit
															.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FAT)),
													cUnit.getFloat(cUnit
															.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_VISIBLE)),
													cUnit.getInt(cUnit
															.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)))));

							cFood.close();
							cUnit.close();

						} while (cMealFood.moveToNext());
					}
					cMealFood.close();

					// new DBTracking(exerciseEvent, mealEvent,
					// bloodGlucoseEvent, medicineEvent, timestamp, noRecors)
					listTempDBTracking
							.add(new DBTracking(
									null,
									new DBMealEvent(
											cMealEvents
													.getLong(cMealEvents
															.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_ID)),
											cMealEvents.getFloat(cMealEvents
													.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_INSULINERATIO)),
											cMealEvents.getFloat(cMealEvents
													.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_CORRECTIONFACTOR)),
											cMealEvents.getFloat(cMealEvents
													.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_CALCULATEDINSULINEAMOUNT)),
											cMealEvents.getString(cMealEvents
													.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_EVENTDATETIME)),
											cMealEvents.getLong(cMealEvents
													.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_USERID)),
											listDBMealFood),
									null,
									null,
									new Functions()
											.getYearMonthDayHourMinutesAsDateFromString(cMealEvents.getString(cMealEvents
													.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_EVENTDATETIME))),
									false, ""));
				} while (cMealEvents.moveToNext());
			}
			cMealEvents.close();

			// For each date get the glucose event
			Cursor cGlucoseEvent = dbHelper
					.fetchBloodGlucoseEventByDate(new Functions()
							.getYearMonthDayAsStringFromDate(date));
			if (cGlucoseEvent.getCount() > 0) {
				cGlucoseEvent.moveToFirst();
				do {
					// get the unit
					Cursor cBGUnit = dbHelper
							.fetchBloodGlucoseUnitsByID(cGlucoseEvent.getLong(cGlucoseEvent
									.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_BGUNITID)));
					cBGUnit.moveToFirst();
					// new DBTracking(exerciseEvent, mealEvent,
					// bloodGlucoseEvent, medicineEvent, timestamp, noRecors)
					listTempDBTracking
							.add(new DBTracking(
									null,
									null,
									new DBBloodGlucoseEvent(
											cGlucoseEvent
													.getLong(cGlucoseEvent
															.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_ID)),
											cGlucoseEvent.getFloat(cGlucoseEvent
													.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_AMOUNT)),
											cGlucoseEvent.getString(cGlucoseEvent
													.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_EVENTDATETIME)),
											cGlucoseEvent.getLong(cGlucoseEvent
													.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_BGUNITID)),
											cGlucoseEvent.getLong(cGlucoseEvent
													.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_USERID)),
											cBGUnit.getString(cBGUnit
													.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEUNIT_UNIT))),
									null,
									new Functions()
											.getYearMonthDayHourMinutesAsDateFromString(cGlucoseEvent.getString(cGlucoseEvent
													.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_EVENTDATETIME))),
									false, ""));

					cBGUnit.close();
				} while (cGlucoseEvent.moveToNext());
			}
			cGlucoseEvent.close();

			// For each date get the medicine event
			Cursor cMedicineEvent = dbHelper
					.fetchMedicineEventByDate(new Functions()
							.getYearMonthDayAsStringFromDate(date));
			if (cMedicineEvent.getCount() > 0) {
				cMedicineEvent.moveToFirst();

				// loop true all medicine events
				do {
					// get the medicine type
					Cursor cMedicineType = dbHelper
							.fetchMedicineTypesByID(cMedicineEvent.getLong(cMedicineEvent
									.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINEEVENT_MEDICINETYPEID)));
					cMedicineType.moveToFirst();
					// new DBTracking(exerciseEvent, mealEvent,
					// bloodGlucoseEvent,
					// medicineEvent, timestamp, noRecors)
					listTempDBTracking
							.add(new DBTracking(
									null,
									null,
									null,
									new DBMedicineEvent(
											cMedicineEvent
													.getLong(cMedicineEvent
															.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINEEVENT_ID)),
											cMedicineEvent.getFloat(cMedicineEvent
													.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINEEVENT_AMOUNT)),
											cMedicineEvent.getString(cMedicineEvent
													.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINEEVENT_TIMESTAMP)),
											cMedicineEvent.getLong(cMedicineEvent
													.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINEEVENT_MEDICINETYPEID)),
											cMedicineType.getString(cMedicineType
													.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINENAME)),
											cMedicineType.getString(cMedicineType
													.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINEUNIT))),
									new Functions()
											.getYearMonthDayHourMinutesAsDateFromString(cMedicineEvent.getString(cMedicineEvent
													.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINEEVENT_TIMESTAMP))),
									false, ""));
					cMedicineType.close();
				} while (cMedicineEvent.moveToNext());
			}
			cMedicineEvent.close();

			// order the temp list on timestamp
			Collections.sort(listTempDBTracking, new TimeComparator());

			// add the temp list to the real list
			listTracking.addAll(listTracking.size(), listTempDBTracking);
		}
		dbHelper.close();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// click on medine event
		if (listTracking.get(position).getMedicineEvent() != null) {
			showPopUpDeleteMedicineEvent(listTracking.get(position)
					.getMedicineEvent());
		} else if (listTracking.get(position).getExerciseEvent() != null) {
			showPopUpDeleteExerciseEvent(listTracking.get(position)
					.getExerciseEvent());
		} else if (listTracking.get(position).getBloodGlucoseEvent() != null) {
			showPopUpDeleteGlucoseEvent(listTracking.get(position)
					.getBloodGlucoseEvent());
		} else if (listTracking.get(position).getMealEvent() != null) {

			float totalValue = 0;
			String text = " \n ";
			String defaultValueText = "";

			for (DBMealFood mealFood : listTracking.get(position)
					.getMealEvent().getMealFood()) {
				float calculatedValue = 0f;

				switch (ActivityGroupMeal.group.getFoodData().defaultValue) {
				case 1:
					calculatedValue = ((mealFood.getUnit().getCarbs() / mealFood
							.getUnit().getStandardamound()) * mealFood
							.getAmount());
					defaultValueText = getResources().getString(
							R.string.short_carbs);
					break;
				case 2:
					calculatedValue = ((mealFood.getUnit().getProtein() / mealFood
							.getUnit().getStandardamound()) * mealFood
							.getAmount());
					defaultValueText = getResources().getString(
							R.string.amound_of_protein);
					break;
				case 3:
					calculatedValue = ((mealFood.getUnit().getFat() / mealFood
							.getUnit().getStandardamound()) * mealFood
							.getAmount());
					defaultValueText = getResources().getString(
							R.string.amound_of_fat);
					break;
				case 4:
					calculatedValue = ((mealFood.getUnit().getKcal() / mealFood
							.getUnit().getStandardamound()) * mealFood
							.getAmount());
					defaultValueText = getResources().getString(
							R.string.short_kcal);
					break;
				}

				totalValue += calculatedValue;

				// round calculatedValue
				calculatedValue = new Functions().roundFloats(calculatedValue,
						1);

				text += "" + mealFood.getAmount() + " "
						+ mealFood.getUnit().getName() + " "
						+ mealFood.getFoodName() + " (" + calculatedValue + " "
						+ defaultValueText + ") \n ";
			}

			// Round total value
			totalValue = new Functions().roundFloats(totalValue, 1);

			showPopUpWhatToDoWithMealEvent(listTracking.get(position)
					.getMealEvent().getId(), totalValue + " "
					+ defaultValueText + " \n" + text);
		}
	}

	private void showPopUpDeleteGlucoseEvent(final DBBloodGlucoseEvent event) {
		// Show a dialog
		new AlertDialog.Builder(ActivityGroupTracking.group)
				.setTitle(getResources().getString(R.string.delete))
				.setPositiveButton(getResources().getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// on click positive button
								// Delete from database
								deleteGlucoseEventFromDatabase(event.getId());
								// refresh list
								refresh();
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// on click negative button do nothing
							}
						})
				.setMessage(
						""
								+ new Functions().getTimeFromString(event
										.getTimeStamp()) + " \n"
								+ event.getAmount() + " " + event.getUnit())
				.show();
	}

	private void showPopUpDeleteExerciseEvent(final DBExerciseEvent event) {
		// Show a dialog
		new AlertDialog.Builder(ActivityGroupTracking.group)
				.setTitle(getResources().getString(R.string.delete))
				.setPositiveButton(getResources().getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// on click positive button
								// Delete from database
								deleteExerciseEventFromDatabase(event.getId());
								// refresh list
								refresh();
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// on click negative button do nothing
							}
						})
				.setMessage(
						""
								+ new Functions().getTimeFromString(event
										.getTimeStamp())
								+ " - "
								+ new Functions().getTimeFromSeconds(event
										.getEndTime()) + " \n"
								+ event.getDescription()).show();
	}

	private void showPopUpDeleteMedicineEvent(
			final DBMedicineEvent medicineEvent) {
		// Show a dialog
		new AlertDialog.Builder(ActivityGroupTracking.group)
				.setTitle(getResources().getString(R.string.delete))
				.setPositiveButton(getResources().getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// on click positive button
								// Delete from database
								deleteMedicineEventFromDatabase(medicineEvent
										.getId());
								// refresh list
								refresh();
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// on click negative button do nothing
							}
						})
				.setMessage(
						""
								+ new Functions()
										.getTimeFromString(medicineEvent
												.getTimeStamp()) + " \n"
								+ medicineEvent.getAmount() + " "
								+ medicineEvent.getMedicineTypeUnit() + " "
								+ medicineEvent.getMedicineTypeName()).show();
	}

	private void deleteGlucoseEventFromDatabase(long id) {
		DbAdapter db = new DbAdapter(this);
		db.open();
		db.deleteBloodGlucoseEventByID(id);
		db.close();
	}

	private void deleteExerciseEventFromDatabase(long id) {
		DbAdapter db = new DbAdapter(this);
		db.open();
		db.deleteExerciseEventByID(id);
		db.close();
	}

	private void deleteMedicineEventFromDatabase(long id) {
		DbAdapter db = new DbAdapter(this);
		db.open();
		db.deleteMedicineEventByID(id);
		db.close();
	}

	private void showPopUpWhatToDoWithMealEvent(final long mealID, String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupTracking.group);
		builder.setMessage(text)
				.setPositiveButton(
						getResources().getString(R.string.addToSelection),
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								addMealEventToSelections(mealID);
								// go to selected food tab ?
								goToFoodTab();
							}
						})
				.setNeutralButton(getResources().getString(R.string.cancel),
						null)
				.setNegativeButton(getResources().getString(R.string.delete),
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// delete from database
								deleteMealEventFromDatabase(mealID);

								// refresh the list
								refresh();
							}
						}).show();
	}

	private void goToFoodTab() {
		try {
			// flag to start show selected food
			// ActivityGroupMeal.group.goToSeletedFood();

			// Go to tracking tab when clicked on add
			ShowHomeTab parentActivity;
			parentActivity = (ShowHomeTab) this.getParent().getParent();
			parentActivity.goToTab(DataParser.activityIDMeal);

			// show sucess message
			Toast.makeText(this,
					getResources().getString(R.string.sucesfull_added),
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			showPopUp(e.toString());
		}
	}

	private void showPopUp(String string) {
		// Show a dialog with a inputbox to insert the template name
		new AlertDialog.Builder(ActivityGroupTracking.group).setTitle("Error")
				.setMessage(string)
				.setNeutralButton(getResources().getString(R.string.oke), null)
				.show();
	}

	private void deleteMealEventFromDatabase(long mealID) {
		DbAdapter db = new DbAdapter(this);
		db.open();
		Cursor cMealFoods = db.fetchMealFoodByMealEventID(mealID);
		if (cMealFoods.getCount() > 0) {
			cMealFoods.moveToFirst();
			do {
				db.deleteMealFoodByID(cMealFoods.getLong(cMealFoods
						.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALFOOD_ID)));
			} while (cMealFoods.moveToNext());
		}
		cMealFoods.close();
		db.deleteMealEventByID(mealID);
		db.close();
	}

	private void addMealEventToSelections(long mealID) {
		DbAdapter db = new DbAdapter(this);
		db.open();
		Cursor cMealFoods = db.fetchMealFoodByMealEventID(mealID);
		cMealFoods.moveToFirst();

		do {
			db.createSelectedFood(
					cMealFoods
							.getFloat(cMealFoods
									.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALFOOD_AMOUNT)),
					cMealFoods.getLong(cMealFoods
							.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALFOOD_FOODUNITID)),
					new Functions().getDateAsStringFromCalendar(Calendar
							.getInstance()));

			ActivityGroupMeal.group.getFoodData().countSelectedFood++;
		} while (cMealFoods.moveToNext());

		cMealFoods.close();
		db.close();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	// if we press the back button on this activity we have to show a popup to
	// exit
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			showPopUpToExitApplication();
			// when we return true here we wont call the onkeydown from
			// activitygroup
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private void showPopUpToExitApplication() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					// exit application on click button positive
					ActivityGroupTracking.group.killApplication();
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupTracking.group);
		builder.setMessage(getResources().getString(R.string.sureToExit))
				.setPositiveButton(getResources().getString(R.string.yes),
						dialogClickListener)
				.setNegativeButton(getResources().getString(R.string.no),
						dialogClickListener).show();
	}
}
