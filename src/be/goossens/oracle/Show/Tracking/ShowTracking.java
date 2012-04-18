package be.goossens.oracle.Show.Tracking;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.Custom.CustomArrayAdapterDBTracking;
import be.goossens.oracle.Objects.DBBloodGlucoseEvent;
import be.goossens.oracle.Objects.DBExerciseEvent;
import be.goossens.oracle.Objects.DBMealEvent;
import be.goossens.oracle.Objects.DBTracking;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.Functions;

public class ShowTracking extends ListActivity {
	private DbAdapter dbHelper;
	private List<DBTracking> listTracking;
	private CustomArrayAdapterDBTracking adapter;
	private TextView tvFetchingData;
	private boolean threadFinished;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_tracking);
		dbHelper = new DbAdapter(this);
		listTracking = new ArrayList<DBTracking>();
		adapter = null;
		tvFetchingData = (TextView) findViewById(R.id.textViewFetchingData);
	}
 
	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		
		if(!threadFinished){
			threadFinished = true;
			tvFetchingData.setVisibility(View.VISIBLE);
			new DoInBackground().execute();
		}
	}

	public void refreshData(){
		new DoInBackground().execute();
	}
		
	private class DoInBackground extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			dbHelper.open();
			fillListView();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			tvFetchingData.setVisibility(View.GONE);
			setListAdapter(adapter);
			super.onPostExecute(result);
		}
	}

	private void fillListView() {
		setListTracking();

		if (listTracking.size() <= 0) {
			// create the list
			listTracking = new ArrayList<DBTracking>();
			// Fill the list with 1 item "no records found"
			listTracking.add(new DBTracking(null, null, null, null,
					getResources().getString(R.string.noTrackingValues)));

		}

		Cursor cSetting = dbHelper.fetchSettingByName(getResources().getString(
				R.string.font_size));
		cSetting.moveToFirst();

		adapter = new CustomArrayAdapterDBTracking(
				this,
				0,
				listTracking,
				cSetting.getInt(cSetting
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		// setListAdapter(adapter);
		cSetting.close();

	}

	// This method will fill the listTracking with all the data from the db
	private void setListTracking() {
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
			listTracking.add(new DBTracking(null, null, null, date, ""));

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

					listTracking
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
									null, null, null, ""));

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
					listTracking
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
											null), null, null, ""));
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

					listTracking
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
									null, ""));

					cBGUnit.close();
				} while (cGlucoseEvent.moveToNext());
			}
			cGlucoseEvent.close();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		/*if (listTracking.get(position).getTimestamp() != null) {
			Toast.makeText(this,
					"" + listTracking.get(position).getTimestamp().getTime(),
					Toast.LENGTH_LONG).show();
		}*/
	}

	@Override
	protected void onPause() {
		super.onPause(); 
	}
}
