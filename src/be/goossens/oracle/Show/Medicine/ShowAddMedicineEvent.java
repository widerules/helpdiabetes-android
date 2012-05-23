// Please read info.txt for license and legal information

package be.goossens.oracle.Show.Medicine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupExercise;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.ActivityGroup.ActivityGroupMedicine;
import be.goossens.oracle.ActivityGroup.ActivityGroupTracking;
import be.goossens.oracle.Custom.CustomSimpleArrayAdapterForASpinner;
import be.goossens.oracle.Objects.DBNameAndID;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.Functions;
import be.goossens.oracle.Rest.TrackingValues;
import be.goossens.oracle.Show.ShowHomeTab;
import be.goossens.oracle.slider.DateSlider;
import be.goossens.oracle.slider.DateTimeSlider;

public class ShowAddMedicineEvent extends Activity {

	private Button btUpdateDateAndHour, btAdd;
	private Spinner spMedicine;
	private EditText etAmount;
	private TextView tvUnit;

	private Calendar mCalendar;
	private Functions functions;

	private List<DBNameAndID> objects;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_add_medicine_event, null);
		setContentView(contentView);

		// track we come here
		ActivityGroupMedicine.group.parent
				.trackPageView(TrackingValues.pageShowMedicineTab);

		functions = new Functions();

		btUpdateDateAndHour = (Button) findViewById(R.id.buttonUpdateDateAndHour);
		btAdd = (Button) findViewById(R.id.buttonAdd);
		spMedicine = (Spinner) findViewById(R.id.SpinnerMedicine);
		etAmount = (EditText) findViewById(R.id.editTextMedicineAmount);
		tvUnit = (TextView) findViewById(R.id.textViewMedicineUnit);

		updateDateAndTimeTextView(Calendar.getInstance());

		// on click button show the popup with the time and date slider
		btUpdateDateAndHour.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(0);
			}
		});

		btAdd.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				onClickBtAdd();
			}
		});

		// update the tvUnit when we select a other medicine type
		spMedicine.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				DBNameAndID obj = (DBNameAndID) spMedicine.getSelectedItem();
				tvUnit.setText("" + obj.getNameTwo());
				// we cant close this cursor obj becaus when we do we wont see
				// our text in our spinner anymore!
			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		etAmount.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// filter so we only get the onkey up actions
				if (event.getAction() != KeyEvent.ACTION_DOWN) {
					// if the pressed key = enter we do the add code
					if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
						onClickBtAdd();
					}
				}
				// if we dont return false our text wont get in the
				// edittext
				return false;
			}
		});

	}

	// when we click on the button add
	private void onClickBtAdd() {
		DbAdapter db = new DbAdapter(this);
		db.open();

		float amount = 0;

		try {
			amount = Float.parseFloat(etAmount.getText().toString());
		} catch (Exception e) {
			amount = 0;
		}

		// create a medicine event
		db.createMedicineEvent(amount,
				new Functions().getDateAsStringFromCalendar(mCalendar),
				spMedicine.getSelectedItemId());

		db.close();

		ActivityGroupTracking.group.restartThisActivity();

		// clear the edittext
		etAmount.setText("");

		// Go to tracking tab when clicked on add
		ShowHomeTab parentActivity;
		parentActivity = (ShowHomeTab) this.getParent().getParent();
		parentActivity.goToTab(DataParser.activityIDTracking);
	}

	@Override
	protected void onResume() {
		fillSpinnerMedicineTypes();
		setDefaultSpinner();
		super.onResume();
	}

	private void setDefaultSpinner() {
		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i).getId() == ActivityGroupMeal.group.getFoodData().defaultMedicineTypeID) {
				spMedicine.setSelection(i);
			}
		}
	}

	private void fillSpinnerMedicineTypes() {
		DbAdapter db = new DbAdapter(this);
		db.open();
		objects = new ArrayList<DBNameAndID>();
		Cursor cMedicineTypes = db.fetchAllMedicineTypes();
		if (cMedicineTypes.getCount() > 0) {
			cMedicineTypes.moveToFirst();
			do {
				objects.add(new DBNameAndID(
						cMedicineTypes
								.getLong(cMedicineTypes
										.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_ID)),
						cMedicineTypes.getString(cMedicineTypes
								.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINENAME)),
						cMedicineTypes.getString(cMedicineTypes
								.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINEUNIT))));
			} while (cMedicineTypes.moveToNext());
		}
		cMedicineTypes.close();
		db.close();

		CustomSimpleArrayAdapterForASpinner adapter = new CustomSimpleArrayAdapterForASpinner(
				this, android.R.layout.simple_spinner_item, objects, 25);

		spMedicine.setAdapter(adapter);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	// create this dialog when the user press on the button date & time
	@Override
	protected Dialog onCreateDialog(int id) {
		// this method is called after invoking 'showDialog' for the first time
		// here we initiate the corresponding DateSlideSelector and return the
		// dialog to its caller
		final Calendar c = Calendar.getInstance();
		return new DateTimeSlider(ActivityGroupMedicine.group,
				mDateTimeSetListener, c);
	}

	// when the slider changed we update time and date
	private DateSlider.OnDateSetListener mDateTimeSetListener = new DateSlider.OnDateSetListener() {
		public void onDateSet(DateSlider view, Calendar selectedDate) {
			updateDateAndTimeTextView(selectedDate);
		}
	};

	// This method is called when the activity starts and when the slider
	// changes
	// This method will update the button and set right Calendar object in
	// mCalendar
	private void updateDateAndTimeTextView(Calendar instance) {
		mCalendar = instance;

		// update the dateText view with the corresponding date
		btUpdateDateAndHour.setText(android.text.format.DateFormat
				.getDateFormat(this).format(mCalendar.getTime())
				+ " "
				+ functions.getTimeFromDate(mCalendar.getTime()));
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
					ActivityGroupMedicine.group.killApplication();
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupMedicine.group);
		builder.setMessage(getResources().getString(R.string.sureToExit))
				.setPositiveButton(getResources().getString(R.string.yes),
						dialogClickListener)
				.setNegativeButton(getResources().getString(R.string.no),
						dialogClickListener).show();
	}
}
