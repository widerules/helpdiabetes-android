package be.goossens.oracle.Show.Exercise;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowAddExerciseEvent extends Activity {
	private TimePicker startTime, endTime;
	private Spinner spinnerExerciseTypes;
	private EditText etDescription;

	private DbAdapter dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_add_exercise_event);

		startTime = (TimePicker) findViewById(R.id.timePicker1);
		endTime = (TimePicker) findViewById(R.id.timePicker2);
		spinnerExerciseTypes = (Spinner) findViewById(R.id.spinner1);
		etDescription = (EditText) findViewById(R.id.editText1);
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
	}

	private void fillSpinner() {
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item,
				dbHelper.fetchAllExerciseTypes(),
				new String[] { DbAdapter.DATABASE_EXERCISETYPE_DESCRIPTION },
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
		Date dStartTime = new Date();
		Date dStopTime = new Date();
		Date dTimeStamp = new Date();
		dStartTime.setHours(startTime.getCurrentHour());
		dStartTime.setMinutes(startTime.getCurrentMinute());
		dStopTime.setHours(endTime.getCurrentHour());
		dStopTime.setMinutes(endTime.getCurrentMinute());

		dbHelper.createExerciseEvent(etDescription.getText().toString(), dStartTime, dStopTime,
				dTimeStamp, spinnerExerciseTypes.getSelectedItemId());
 
		setResult(RESULT_OK);
		finish();
	}

	public void onClickBack(View view) {
		finish();
	}
}
