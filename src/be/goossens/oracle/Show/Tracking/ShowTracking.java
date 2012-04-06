package be.goossens.oracle.Show.Tracking;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.Custom.CustomArrayAdapterDBTracking;
import be.goossens.oracle.Objects.DBExerciseEvent;
import be.goossens.oracle.Objects.DBTracking;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.Functions;

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
			listTracking = new ArrayList<DBTracking>();

			listTracking.add(new DBTracking(null, new Date(), getResources()
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
		listTracking = new ArrayList<DBTracking>();

		/*
		 * select distinct timestamp from (select distinct
		 * to_char(timestamp,'dd-mon-yyyy') from table UNION select distinct
		 * to_char(timestamp,'dd-mon-yyyy') from table2 UNION select distinct
		 * to_char(timestamp,'dd-mon-yyyy') from table3 )
		 */  

		Functions functions = new Functions();
		// fill the list with all the
		// exercise events
		Cursor cExerciseEvents = dbHelper.fetchAllExerciseEvents();
		if (cExerciseEvents.getCount() > 0) {
			cExerciseEvents.moveToFirst();
			do {
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
										functions
												.parseStringTimeStampToDate(cExerciseEvents
														.getString(cExerciseEvents
																.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_TIMESTAMP))),
										cExerciseEvents.getLong(cExerciseEvents
												.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EXERCISETYPEID)),
										cExerciseEvents.getLong(cExerciseEvents
												.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_USERID))),
								functions
										.parseStringTimeStampToDate(cExerciseEvents.getString(cExerciseEvents
												.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_TIMESTAMP))),
								""));
			} while (cExerciseEvents.moveToNext());
		}
		cExerciseEvents.close();

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (listTracking.get(position).getExerciseEvent() != null) {
			Toast.makeText(
					this,
					""
							+ listTracking.get(position).getExerciseEvent()
									.getTimeStamp(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}
}
