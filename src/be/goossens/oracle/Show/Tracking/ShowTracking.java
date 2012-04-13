package be.goossens.oracle.Show.Tracking;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.Custom.CustomArrayAdapterDBTracking;
import be.goossens.oracle.Objects.DBExerciseEvent;
import be.goossens.oracle.Objects.DBMealEvent;
import be.goossens.oracle.Objects.DBTracking;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowTracking extends ListActivity {
	private DbAdapter dbHelper;
	private List<DBTracking> listTracking;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_tracking);
		dbHelper = new DbAdapter(this);
		listTracking = new ArrayList<DBTracking>();
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();

		fillListView();
	}

	private void fillListView() {
		setListTracking();

		if (listTracking.size() <= 0) {
			// create the list
			listTracking = new ArrayList<DBTracking>();
			// Fill the list with 1 item "no records found"
			listTracking.add(new DBTracking(null, null, null, getResources()
					.getString(R.string.noTrackingValues)));

		}

		Cursor cSetting = dbHelper.fetchSettingByName(getResources().getString(
				R.string.font_size));
		cSetting.moveToFirst();

		CustomArrayAdapterDBTracking adapter = new CustomArrayAdapterDBTracking(
				this,
				R.layout.row_custom_array_adapter,
				listTracking,
				cSetting.getInt(cSetting
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		setListAdapter(adapter);
		cSetting.close();

	}

	// This method will fill the listTracking with all the data from the db
	private void setListTracking() {
		// clear the list and create the object
		listTracking = new ArrayList<DBTracking>();
		// needed to temp store the dates in
		List<Date> listDates = new ArrayList<Date>();

		// get all needed objects
		Cursor cExerciseEvents = dbHelper.fetchAllExerciseEvents();
		Cursor cMealEvents = dbHelper.fetchAllMealEvents();
		// if exercise event != 0 then we can move to first
		if (cExerciseEvents.getCount() > 0) {
			cExerciseEvents.moveToFirst();
			do {
				// check if the date already exists in the list
				boolean exists = false;
				for (Date time : listDates) {
					Date dbDate = new Date(
							cExerciseEvents.getLong(cExerciseEvents
									.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EVENTDATETIME)));

					if (time.getTime() == dbDate.getTime())
						exists = true;
				}

				// if the date doesnt exists in the list, we can add it!
				if (!exists) {
					// add the date to the list
					listDates
							.add(new Date(
									cExerciseEvents.getLong(cExerciseEvents
											.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EVENTDATETIME))));
				}
			} while (cExerciseEvents.moveToNext());
		}

		// if meal event != 0 then we can move to first
		if (cMealEvents.getCount() != 0) {
			cMealEvents.moveToFirst();
			do {
				// check if the date already exists in the list
				boolean exists = false;
				for (Date time : listDates) {
					Date dbDate = new Date(
							cMealEvents.getLong(cMealEvents
									.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_EVENTDATETIME)));
					if (time.getTime() == dbDate.getTime())
						exists = true;
				}

				// if the date doesnt exists in the list, we can add it!
				if (!exists)
					listDates
							.add(new Date(
									cMealEvents.getLong(cMealEvents
											.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_EVENTDATETIME))));
			} while (cMealEvents.moveToNext());
		}

		// /////////////////// For every date object /////////////////////
		for (Date timestamp : listDates) {
			// first we add the timestamp in a object so we can display it as a
			// single row
			listTracking.add(new DBTracking(null, null, timestamp, ""));

			// get all exercise events from timestamp
			Cursor cExerciseEventsFromTimestamp = dbHelper
					.fetchExerciseEventsByTimestamp(timestamp.getTime());

			// get all the meal events from timestamp
			Cursor cMealEventsFromTimestamp = dbHelper
					.fetchAllMealEventsByTimestamp(timestamp.getTime());

			// if exercise event != 0 we can move to first
			if (cExerciseEventsFromTimestamp.getCount() > 0) {
				cExerciseEventsFromTimestamp.moveToFirst();
				do {
					listTracking
							.add(new DBTracking(
									new DBExerciseEvent(
											cExerciseEventsFromTimestamp
													.getLong(cExerciseEventsFromTimestamp
															.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_ID)),
											cExerciseEventsFromTimestamp
													.getString(cExerciseEventsFromTimestamp
															.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_DESCRIPTION)),
											cExerciseEventsFromTimestamp
													.getInt(cExerciseEventsFromTimestamp
															.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_STARTTIME)),
											cExerciseEventsFromTimestamp
													.getInt(cExerciseEventsFromTimestamp
															.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_STOPTIME)),
											cExerciseEventsFromTimestamp
													.getString(cExerciseEventsFromTimestamp
															.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EVENTDATETIME)),
											cExerciseEventsFromTimestamp
													.getInt(cExerciseEventsFromTimestamp
															.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EXERCISETYPEID)),
											cExerciseEventsFromTimestamp
													.getInt(cExerciseEventsFromTimestamp
															.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_USERID))),
									null, null, ""));
				} while (cExerciseEventsFromTimestamp.moveToNext());
			}

			// if meal event != 0 we can move to first
			if (cMealEventsFromTimestamp.getCount() > 0) {
				cMealEventsFromTimestamp.moveToFirst();
				do {
					listTracking
							.add(new DBTracking(
									null,
									new DBMealEvent(
											cMealEventsFromTimestamp
													.getLong(cMealEventsFromTimestamp
															.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_ID)),
											cMealEventsFromTimestamp
													.getFloat(cMealEventsFromTimestamp
															.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_INSULINERATIO)),
											cMealEventsFromTimestamp
													.getFloat(cMealEventsFromTimestamp
															.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_CORRECTIONFACTOR)),
											cMealEventsFromTimestamp
													.getFloat(cMealEventsFromTimestamp
															.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_CALCULATEDINSULINEAMOUNT)),
											cMealEventsFromTimestamp
													.getString(cMealEventsFromTimestamp
															.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_EVENTDATETIME)),
											cMealEventsFromTimestamp
													.getLong(cMealEventsFromTimestamp
															.getColumnIndexOrThrow(DbAdapter.DATABASE_MEALEVENT_USERID)),
											null), null, ""));
				} while (cMealEventsFromTimestamp.moveToNext());
			}

			// close the cusor we dont need anymore
			cMealEventsFromTimestamp.close();
			cExerciseEventsFromTimestamp.close();
		}

		// close the cursors we dont need anymore
		cMealEvents.close();
		cExerciseEvents.close();

		/*// test gething all time stamps
		Cursor cTimeStamps = dbHelper.fetchAllTimestamps();
		if (cTimeStamps.getCount() > 0) {
			cTimeStamps.moveToFirst();
			do{
			Toast.makeText(this, "" + cTimeStamps.getString(cTimeStamps.getColumnIndexOrThrow("time")), Toast.LENGTH_LONG)
			.show();}while(cTimeStamps.moveToNext());
		}
		cTimeStamps.close();*/
		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (listTracking.get(position).getTimestamp() != null) {
			Toast.makeText(this,
					"" + listTracking.get(position).getTimestamp().getTime(),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}
}
