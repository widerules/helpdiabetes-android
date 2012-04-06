package be.goossens.oracle.Show.Exercise;

import java.util.Date;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.Functions;
import be.goossens.oracle.Show.ShowHomeTab;

public class ShowAddExerciseEvent extends Activity {
	// private TimePicker startTime, endTime;
	private Spinner spinnerExerciseTypes, spinnerDuration;
	private EditText etDescription, etHour, etMinute;
	private Button btAdd, btDelete, btHourUp, btHourDown, btMinuteUp,
			btMinuteDown;
	private TextView tvDate;
	private DbAdapter dbHelper;

	private static final int requestCodeAddExerciseType = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_add_exercise_event, null);
		setContentView(contentView);

		dbHelper = new DbAdapter(this);
		spinnerExerciseTypes = (Spinner) findViewById(R.id.spinnerSportType);
		spinnerDuration = (Spinner) findViewById(R.id.spinnerDuration);
		etDescription = (EditText) findViewById(R.id.editText1);
		etHour = (EditText) findViewById(R.id.editTextHour);
		etMinute = (EditText) findViewById(R.id.editTextMinute);
		btHourUp = (Button) findViewById(R.id.buttonHourUp);
		btHourDown = (Button) findViewById(R.id.buttonHourDown);
		btMinuteUp = (Button) findViewById(R.id.buttonMinuteUp);
		btMinuteDown = (Button) findViewById(R.id.buttonMinuteDown);
		btAdd = (Button) findViewById(R.id.buttonAdd);
		btDelete = (Button) findViewById(R.id.buttonDelete);
		tvDate = (TextView) findViewById(R.id.textViewDate);

		btDelete.setVisibility(View.GONE);

		Date date = new Date();

		// The year + 1900 is becaus date.getYear returns current year -
		// 1900....

		// The day and month + 1 becaus it starts counting from 0 ....
		tvDate.setText((new Date().getDay() + 1) + "-"
				+ (new Date().getMonth() + 1) + "-"
				+ (new Date().getYear() + 1900));
		btHourUp.setText(" " + getResources().getString(R.string.time_up) + " ");
		btMinuteUp.setText(" " + getResources().getString(R.string.time_up)
				+ " ");
		btHourDown.setText(" " + getResources().getString(R.string.time_down)
				+ " ");
		btMinuteDown.setText(" " + getResources().getString(R.string.time_down)
				+ " ");

		if (date.getHours() < 10)
			etHour.setText("0" + date.getHours());
		else
			etHour.setText("" + date.getHours());

		if (date.getMinutes() < 10)
			etMinute.setText("0" + date.getMinutes());
		else
			etMinute.setText("" + date.getMinutes());

		btAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickAdd(v);
			}
		});

		btHourUp.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				onClickHourUp();
			}
		});

		btHourDown.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				onClickHourDown();
			}
		});

		btMinuteUp.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				onClickMinuteUp();
			}
		});

		btMinuteDown.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				onClickMinuteDown();
			}
		});
	}

	private void onClickHourUp() {
		// The max hour that can be set = 23h
		int hour = 0;
		try {
			// get hour
			hour = Integer.parseInt(etHour.getText().toString());

			// Do hour ++ if its less then 23 else set 0
			if (hour < 23)
				hour++;
			else
				hour = 0;

			// set text back on hour
			if (hour < 10)
				etHour.setText("0" + hour);
			else
				etHour.setText("" + hour);

		} catch (Exception e) {
			// this wil be thrown when the etHour = "";
			// when the user then press the + button we want so set 1 as hour
			etHour.setText("01");
		}
	}

	private void onClickHourDown() {
		// The min hour that can be set = 0h
		int hour = 0;
		try {
			// get hour
			hour = Integer.parseInt(etHour.getText().toString());

			// Do hour -- if its more then 0 else set 23
			if (hour > 0)
				hour--;
			else
				hour = 23;

			// set text back on hour
			if (hour < 10)
				etHour.setText("0" + hour);
			else
				etHour.setText("" + hour);
		} catch (Exception e) {
			// this wil be thrown when the etHour = "";
			// when the user then press the + button we want so set 1 as hour
			etHour.setText("23");
		}
	}

	private void onClickMinuteUp() {
		// The max minutes that can be set = 59
		int minutes = 0;
		try {
			minutes = Integer.parseInt(etMinute.getText().toString());

			// Do minutes ++ if its less then 59 else set 0
			if (minutes < 59)
				minutes++;
			else
				minutes = 0;

			// set text back on minutes
			if (minutes < 10)
				etMinute.setText("0" + minutes);
			else
				etMinute.setText("" + minutes);
		} catch (Exception e) {
		}
	}

	private void onClickMinuteDown() {
		// The min minutes that can be set = 0
		int minutes = 0;
		try {
			// get minutes
			minutes = Integer.parseInt(etMinute.getText().toString());

			// Do minutes -- if its more then 0 else set 59
			if (minutes > 0)
				minutes--;
			else
				minutes = 59;

			// set text back on hour
			if (minutes < 10)
				etMinute.setText("0" + minutes);
			else
				etMinute.setText("" + minutes);
		} catch (Exception e) {
			etMinute.setText("59");
		}
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
		Functions functions = new Functions();

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
				Date date = new Date();
				String timeStamp = "";
				int startTime = 0;
				int endTime = 0;

				// Fill day on timestamp
				if ((date.getDay()+1) < 10)
					timeStamp += "0" + (date.getDay()+1);
				else
					timeStamp += (date.getDay()+1);

				// Fill month on timestamp
				if ((date.getMonth()+1) < 10)
					timeStamp += "0" + (date.getMonth()+1);
				else
					timeStamp += (date.getMonth()+1);

				// Fill year on timestamp
				timeStamp += (date.getYear() + 1900);

				// Fill startTime with seconds of time
				startTime = (Integer.parseInt(etHour.getText().toString()) * 3600)
						+ (Integer.parseInt(etMinute.getText().toString()) * 60);

				// Fill endTime
				// The * 1800 comes from 30 minutes * position + 1
				endTime = ((spinnerDuration.getSelectedItemPosition() + 1) * 1800);

				dbHelper.createExerciseEvent(
						etDescription.getText().toString(), startTime, endTime,
						timeStamp, spinnerExerciseTypes.getSelectedItemId());

			}

			etDescription.setText("");

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
