package be.goossens.oracle.Show.Exercise;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TimePicker;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupExercise;
import be.goossens.oracle.ActivityGroup.ActivityGroupTracking;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.Functions;
import be.goossens.oracle.Show.ShowHomeTab;

public class ShowAddExerciseEvent extends Activity {
	private TimePicker startTime, endTime;
	private Spinner spinnerExerciseTypes;
	private EditText etDescription;
	private Button btAdd, btDelete;

	private DbAdapter dbHelper;

	private static final int requestCodeAddExerciseType = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_add_exercise_event);

		startTime = (TimePicker) findViewById(R.id.timePicker1);
		endTime = (TimePicker) findViewById(R.id.timePicker2);
		spinnerExerciseTypes = (Spinner) findViewById(R.id.spinner1);
		etDescription = (EditText) findViewById(R.id.editText1);
		btAdd = (Button) findViewById(R.id.buttonAdd);
		btDelete = (Button) findViewById(R.id.buttonDelete);

		btDelete.setVisibility(View.GONE);
		startTime.setIs24HourView(true);
		endTime.setIs24HourView(true);
		endTime.setCurrentHour(endTime.getCurrentHour() + 2);
		dbHelper = new DbAdapter(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		fillSpinner();
		if (getIntent().getExtras().getString(DataParser.whatToDo)
				.equals(DataParser.doUpdateExerciseEvent))
			setExistingValues();
	}

	private void setExistingValues() {
		Functions functions = new Functions();

		Cursor cExerciseEvent = dbHelper.fetchExerciseEventByID(getIntent()
				.getExtras().getLong(DataParser.idExerciseEvent));
		cExerciseEvent.moveToFirst();

		Date dStartTime = functions
				.parseStringToDate(cExerciseEvent.getString(cExerciseEvent
						.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_STARTTIME)));

		startTime.setCurrentHour(dStartTime.getHours());
		startTime.setCurrentMinute(dStartTime.getMinutes());

		Date dStopTime = functions
				.parseStringToDate(cExerciseEvent.getString(cExerciseEvent
						.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_STOPTIME)));
		endTime.setCurrentHour(dStopTime.getHours());
		endTime.setCurrentMinute(dStopTime.getMinutes());

		etDescription
				.setText(cExerciseEvent.getString(cExerciseEvent
						.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_DESCRIPTION)));

		setRightItemInSpinner(cExerciseEvent
				.getLong(cExerciseEvent
						.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EXERCISETYPEID)));

		cExerciseEvent.close();

		btAdd.setText(getResources().getString(R.string.update));
		btDelete.setVisibility(View.VISIBLE);
	}

	private void setRightItemInSpinner(long exerciseTypeID) {
		int position = 0;
		Cursor cTemp = dbHelper.fetchAllExerciseTypes();
		cTemp.moveToFirst();
		do {
			if (cTemp.getLong(cTemp
					.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_ID)) == exerciseTypeID) {
				position = cTemp.getPosition();
			}
		} while (cTemp.moveToNext());
		cTemp.close();

		spinnerExerciseTypes.setSelection(position);
	}

	private void fillSpinner() {
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item,
				dbHelper.fetchAllExerciseTypes(),
				new String[] { DbAdapter.DATABASE_EXERCISETYPE_NAME },
				new int[] { android.R.id.text1 });
		spinnerExerciseTypes.setAdapter(adapter);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	public void onClickAdd(View view) {
		if (etDescription.length() <= 0) {
			Toast.makeText(
					this,
					""
							+ getResources().getString(
									R.string.description_cant_be_empty),
					Toast.LENGTH_LONG).show();
		} else {
			if (getIntent().getExtras().getString(DataParser.whatToDo)
					.equals(DataParser.doUpdateExerciseEvent)) {
				// update exercise event
				dbHelper.updateExerciseEventByID(
						getIntent().getExtras().getLong(
								DataParser.idExerciseEvent),
						etDescription.getText().toString(),
						startTime.getCurrentHour() + ":"
								+ startTime.getCurrentMinute(),
						endTime.getCurrentHour() + ":"
								+ endTime.getCurrentMinute(),
						spinnerExerciseTypes.getSelectedItemId());
			} else {
				// create new exercise event
				Date dTimeStamp = new Date();
				dbHelper.createExerciseEvent(
						etDescription.getText().toString(),
						startTime.getCurrentHour() + ":"
								+ startTime.getCurrentMinute(),
						endTime.getCurrentHour() + ":"
								+ endTime.getCurrentMinute(), dTimeStamp,
						spinnerExerciseTypes.getSelectedItemId());
			}
			etDescription.setText("");
			Date time = new Date();
			startTime.setCurrentHour(time.getHours());
			startTime.setCurrentMinute(time.getMinutes());
			endTime.setCurrentHour(time.getHours() + 2);
			endTime.setCurrentMinute(time.getMinutes());
			
			ShowHomeTab parentActivity;
			parentActivity = (ShowHomeTab) this.getParent().getParent();
			parentActivity.goToTab(0);
			
		
			
			//setResult(RESULT_OK);
			//finish();        
		}
	}

	public void onClickBack(View view) {
		finish();
	}

	public void onClickDelete(View view) {
		dbHelper.deleteExerciseEventByID(getIntent().getExtras().getLong(
				DataParser.idExerciseEvent));
		setResult(RESULT_OK);
		finish();
	}

	// create menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.exercise_event_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	// on menu item selected
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		switch(item.getItemId()){
		case R.id.menuManageSportTypes:
			i  = new Intent(this, ShowExerciseTypes.class);
			startActivityForResult(i, requestCodeAddExerciseType);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// if we come back from addExerciseType we have to update the spinner
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case requestCodeAddExerciseType:
			if(resultCode == RESULT_OK)
				fillSpinner();
			break;
		}
	}
}
