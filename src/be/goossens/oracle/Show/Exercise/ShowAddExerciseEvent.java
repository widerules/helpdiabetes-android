package be.goossens.oracle.Show.Exercise;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupExercise;
import be.goossens.oracle.ActivityGroup.ActivityGroupTracking;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.Functions;
import be.goossens.oracle.Show.ShowHomeTab;
import be.goossens.oracle.slider.DateSlider;
import be.goossens.oracle.slider.DateTimeSlider;
import be.goossens.oracle.slider.TimeLabeler;
 
public class ShowAddExerciseEvent extends Activity {
	// private TimePicker startTime, endTime;
	private Spinner spinnerExerciseTypes, spinnerDuration;
	private EditText etDescription;
	private Button btAdd, btDelete, btUpdateDateAndHour;
	private DbAdapter dbHelper;
	private static final int requestCodeAddExerciseType = 1;
	private Calendar mCalendar;
	private Functions functions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_add_exercise_event, null);
		setContentView(contentView);

		dbHelper = new DbAdapter(this);
		functions = new Functions();
		spinnerExerciseTypes = (Spinner) findViewById(R.id.spinnerSportType);
		spinnerDuration = (Spinner) findViewById(R.id.spinnerDuration);
		etDescription = (EditText) findViewById(R.id.editText1);

		btAdd = (Button) findViewById(R.id.buttonAdd);
		btDelete = (Button) findViewById(R.id.buttonDelete);
		btUpdateDateAndHour = (Button) findViewById(R.id.buttonUpdateDateAndHour);

		btDelete.setVisibility(View.GONE);

		// create a new date with current date and hour
		mCalendar = Calendar.getInstance();

		updateTimeAndTimeTextView(Calendar.getInstance());

		btAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickAdd(v);
			}
		});

		btUpdateDateAndHour.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(0);
			}
		});
	}
 
	private void updateTimeAndTimeTextView(Calendar selectedDate) {
		// set the local variable = the given variable
		mCalendar = selectedDate;

		// update the dateText view with the corresponding date
		int minute = selectedDate.get(Calendar.MINUTE)
				/ TimeLabeler.MINUTEINTERVAL * TimeLabeler.MINUTEINTERVAL;
		btUpdateDateAndHour.setText(String.format("%te. %tB %tY%n%tH:%02d", selectedDate,
				selectedDate, selectedDate, selectedDate, minute));
	}

	private DateSlider.OnDateSetListener mDateTimeSetListener = new DateSlider.OnDateSetListener() {
		public void onDateSet(DateSlider view, Calendar selectedDate) {
			updateTimeAndTimeTextView(selectedDate);
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		// this method is called after invoking 'showDialog' for the first time
		// here we initiate the corresponding DateSlideSelector and return the
		// dialog to its caller

		final Calendar c = Calendar.getInstance();
		return new DateTimeSlider(ActivityGroupExercise.group,
				mDateTimeSetListener, c);
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		fillSpinnerSportType();
		fillSpinnerDuration();

		if (getIntent().getExtras().getString(DataParser.whatToDo)
				.equals(DataParser.doUpdateExerciseEvent))
			setExistingValues();
	}

	private void setExistingValues() {
		Cursor cExerciseEvent = dbHelper.fetchExerciseEventByID(getIntent()
				.getExtras().getLong(DataParser.idExerciseEvent));
		cExerciseEvent.moveToFirst();

		/*
		 * Date dStartTime = functions
		 * .parseStringToDate(cExerciseEvent.getString(cExerciseEvent
		 * .getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_STARTTIME)));
		 * 
		 * Date dStopTime = functions
		 * .parseStringToDate(cExerciseEvent.getString(cExerciseEvent
		 * .getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_STOPTIME)));
		 */

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

	private void fillSpinnerSportType() {
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item,
				dbHelper.fetchAllExerciseTypes(),
				new String[] { DbAdapter.DATABASE_EXERCISETYPE_NAME },
				new int[] { android.R.id.text1 });
		spinnerExerciseTypes.setAdapter(adapter);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	private void fillSpinnerDuration() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.standard_duration,
				android.R.layout.simple_spinner_item);
		spinnerDuration.setAdapter(adapter);
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
				/*
				 * dbHelper.updateExerciseEventByID(
				 * getIntent().getExtras().getLong( DataParser.idExerciseEvent),
				 * etDescription.getText().toString(),
				 * startTime.getCurrentHour() + ":" +
				 * startTime.getCurrentMinute(), endTime.getCurrentHour() + ":"
				 * + endTime.getCurrentMinute(),
				 * spinnerExerciseTypes.getSelectedItemId());
				 */
			} else {
				// create new exercise event
				int startTime = 0;
				int endTime = 0;

				// Fill startTime with seconds of time
				startTime = (functions.getHour(mCalendar) * 3600) + (functions.getMinutes(mCalendar) * 60);

				// Fill endTime
				// The * 1800 comes from 30 minutes * position + 1
				endTime = ((spinnerDuration.getSelectedItemPosition() + 1) * 1800);

				// do endtime += starttime becaus the endTime is the startTime +
				// the just calculated seconds
				endTime += startTime;

				// create exerciseEvent
				dbHelper.createExerciseEvent(
						etDescription.getText().toString(), startTime, endTime, 
						spinnerExerciseTypes.getSelectedItemId(), functions.getDateAsStringFromCalendar(mCalendar));

			}
 
			etDescription.setText("");

			//refresh tracking list
			ActivityGroupTracking.group.showTrackingRefreshList(); 
			
			// Go to tracking tab when clicked on add
			ShowHomeTab parentActivity; 
			parentActivity = (ShowHomeTab) this.getParent().getParent();
			parentActivity.goToTab(DataParser.activityIDTracking);
		}
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
		switch (item.getItemId()) {
		case R.id.menuManageSportTypes:
			i = new Intent(this, ShowExerciseTypes.class);
			startActivityForResult(i, requestCodeAddExerciseType);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// if we come back from addExerciseType we have to update the spinner
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case requestCodeAddExerciseType:
			if (resultCode == RESULT_OK)
				fillSpinnerSportType();
			break;
		}
	}
}
