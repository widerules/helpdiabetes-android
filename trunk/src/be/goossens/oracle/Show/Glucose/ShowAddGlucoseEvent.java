package be.goossens.oracle.Show.Glucose;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupGlucose;
import be.goossens.oracle.ActivityGroup.ActivityGroupTracking;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.Functions;
import be.goossens.oracle.Show.ShowHomeTab;
import be.goossens.oracle.numberPicker.NumberPicker;
import be.goossens.oracle.slider.DateSlider;
import be.goossens.oracle.slider.DateTimeSlider;
import be.goossens.oracle.slider.TimeLabeler;

public class ShowAddGlucoseEvent extends Activity {
	private Button btUpdateDateAndHour, btAdd;

	private Calendar mCalendar;
	private DbAdapter dbHelper;
	private Functions functions;
	private long glucoseUnitID;
	private NumberPicker np;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_add_glucose_event, null);
		setContentView(contentView);

		dbHelper = new DbAdapter(this);
		functions = new Functions();

		mCalendar = Calendar.getInstance();

		np = (NumberPicker) findViewById(R.id.NumberPicker);

		glucoseUnitID = -1;

		btUpdateDateAndHour = (Button) contentView
				.findViewById(R.id.buttonUpdateDateAndHour);

		btAdd = (Button) findViewById(R.id.buttonAdd);

		btUpdateDateAndHour.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showDialog(0);
			}
		});

		btAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickAdd();
			}
		});

		updateTimeAndTimeTextView(mCalendar);
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		fillTextViewGlucoseUnit();
	}

	private void fillTextViewGlucoseUnit() {
		dbHelper.open();
		Cursor cSetting = dbHelper.fetchSettingByName(getResources().getString(
				R.string.setting_glucose_unit));
		cSetting.moveToFirst();

		glucoseUnitID = cSetting.getLong(cSetting
				.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));

		cSetting.close();

		Cursor cGlucoseUnit = dbHelper
				.fetchBloodGlucoseUnitsByID(glucoseUnitID);

		cGlucoseUnit.moveToFirst();

		np.setTextViewValue(cGlucoseUnit.getString(cGlucoseUnit
				.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEUNIT_UNIT)));
		
		cGlucoseUnit.close();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	private void updateTimeAndTimeTextView(Calendar selectedDate) {
		// set the local variable = the given variable
		mCalendar = selectedDate;

		// update the dateText view with the corresponding date
		int minute = selectedDate.get(Calendar.MINUTE)
				/ TimeLabeler.MINUTEINTERVAL * TimeLabeler.MINUTEINTERVAL;
		btUpdateDateAndHour
				.setText(String.format("%te. %tB %tY%n%tH:%02d", selectedDate,
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
		return new DateTimeSlider(ActivityGroupGlucose.group,
				mDateTimeSetListener, c);
	}

	private void onClickAdd() {
		if (np.getValue() > 0) {
			dbHelper.open();

			dbHelper.createBloodGlucoseEvent(np.getValue(),
					functions.getDateAsStringFromCalendar(mCalendar),
					glucoseUnitID);

			// refresh tracking list
			ActivityGroupTracking.group.showTrackingRefreshList();

			// / Go to tracking tab when clicked on add
			ShowHomeTab parentActivity;
			parentActivity = (ShowHomeTab) this.getParent().getParent();
			parentActivity.goToTab(DataParser.activityIDTracking);

			np.setValue(0);
		} else {
			Toast.makeText(this,
					getResources().getString(R.string.amountCantBeZero),
					Toast.LENGTH_LONG).show();
			np.setValue(0);
		}
	}
}
