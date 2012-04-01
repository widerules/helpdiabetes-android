package be.goossens.oracle.Show.Exercise;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import be.goossens.oracle.R;
import be.goossens.oracle.Custom.CustomArrayAdapterDBExerciseEvent;
import be.goossens.oracle.Objects.DBExerciseEvent;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowExerciseEvents extends ListActivity {

	private DbAdapter dbHelper;
	private List<DBExerciseEvent> listExerciseEvents;
	private static final int request_code_add_exercise_event = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_exercise_events);
		dbHelper = new DbAdapter(this);
	}

	// on click button exersive event
	public void onClickAddExerciseEvent(View view) {
		Intent i = new Intent(this, ShowAddExerciseEvent.class);
		startActivityForResult(i, request_code_add_exercise_event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		dbHelper.open();
		switch (requestCode) {
		case request_code_add_exercise_event:
			if (resultCode == RESULT_OK)
				refresh();
			break; 
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void refresh() {
		fillListExerciseEvents();
		fillListView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		refresh();
	}

	// This method will fill the list object with the right items
	private void fillListExerciseEvents() {
		listExerciseEvents = new ArrayList<DBExerciseEvent>();
		Cursor cExerciseEvent = dbHelper.fetchAllExerciseEvents();
		if (cExerciseEvent.getCount() > 0) {
			cExerciseEvent.moveToFirst();
			do {
				Date startTime = new Date();
				Date endTime = new Date();
				Date timeStamp = new Date();

				listExerciseEvents
						.add(new DBExerciseEvent(
								cExerciseEvent
										.getLong(cExerciseEvent
												.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_ID)),
								cExerciseEvent.getString(cExerciseEvent
										.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_DESCRIPTION)),
								startTime,
								endTime,
								timeStamp,
								cExerciseEvent.getLong(cExerciseEvent
										.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EXERCISETYPEID)),
								cExerciseEvent.getLong(cExerciseEvent
										.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_USERID))));
			} while (cExerciseEvent.moveToNext());
		}
		cExerciseEvent.close();
	}

	// This method will fill the listview with all the exercise events
	private void fillListView() {
		if (listExerciseEvents.size() > 0) {
			Cursor cSettings = dbHelper.fetchSettingByName(getResources()
					.getString(R.string.font_size));
			cSettings.moveToFirst();
			CustomArrayAdapterDBExerciseEvent adapter = new CustomArrayAdapterDBExerciseEvent(
					this,
					R.layout.row_custom_array_adapter,
					listExerciseEvents,
					cSettings.getInt(cSettings
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
			setListAdapter(adapter);
			cSettings.close();
		} else {
			setListAdapter(null);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

}
