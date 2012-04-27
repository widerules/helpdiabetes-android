package be.goossens.oracle.Show.Medicine;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.ActivityGroup.ActivityGroupMedicine;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.Functions;

public class ShowAddMedicineEvent extends Activity {

	private DbAdapter dbHelper;

	private Button btUpdateDateAndHour, btAdd;
	private Spinner spMedicine;
	private EditText etAmount;
	private TextView tvUnit;
 
	private Calendar mCalendar;
	private Functions functions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_add_medicine_event, null);
		setContentView(contentView);

		dbHelper = new DbAdapter(this);
		functions = new Functions();

		btUpdateDateAndHour = (Button) findViewById(R.id.buttonUpdateDateAndHour);
		btAdd = (Button) findViewById(R.id.buttonAdd);
		spMedicine = (Spinner) findViewById(R.id.SpinnerMedicine);
		etAmount = (EditText) findViewById(R.id.editTextMedicineAmount);
		tvUnit = (TextView) findViewById(R.id.textViewMedicineUnit);

		updateTimeAndTimeTextView(Calendar.getInstance());

	}

	private void updateTimeAndTimeTextView(Calendar instance) {
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
			builder.setMessage(
					getResources().getString(R.string.sureToExit))
					.setPositiveButton(
							getResources().getString(R.string.yes),
							dialogClickListener)
					.setNegativeButton(
							getResources().getString(R.string.no),
							dialogClickListener).show();
		}
}
