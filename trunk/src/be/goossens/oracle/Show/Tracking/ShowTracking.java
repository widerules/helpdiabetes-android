package be.goossens.oracle.Show.Tracking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.ActivityGroup.ActivityGroupTracking;
import be.goossens.oracle.Custom.CustomArrayAdapterDBTracking;
import be.goossens.oracle.Objects.DBMedicineEvent;
import be.goossens.oracle.Objects.DBloodGlucoseEvent;
import be.goossens.oracle.Objects.DBExerciseEvent;
import be.goossens.oracle.Objects.DBMealEvent;
import be.goossens.oracle.Objects.DBTracking;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.Functions;
import be.goossens.oracle.Rest.TimeComparator;

public class ShowTracking extends ListActivity {
	private List<DBTracking> listTracking;
	private CustomArrayAdapterDBTracking adapter;
	private TextView tvFetchingData;
	private boolean threadFinished;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_tracking);
		listTracking = new ArrayList<DBTracking>();
		adapter = null;
		tvFetchingData = (TextView) findViewById(R.id.textViewFetchingData);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!threadFinished) {
			threadFinished = true;
			tvFetchingData.setVisibility(View.VISIBLE);
			new DoInBackground().execute();
		}
	}

	public void refreshData() {
		setListAdapter(null);
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
				ActivityGroupMeal.group.getFoodData().dbFontSize);
		setListAdapter(adapter);

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
				listDates
						.add(new Functions().getYearMonthDayAsDateFromString(cDates.getString(cDates
								.getColumnIndexOrThrow(new DataParser().timestamp))));
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
											null),
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
									new DBloodGlucoseEvent(
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
		/*
		 * if (listTracking.get(position).getTimestamp() != null) {
		 * Toast.makeText(this, "" +
		 * listTracking.get(position).getTimestamp().getTime(),
		 * Toast.LENGTH_LONG).show(); }
		 */
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
