package be.goossens.oracle.Show.Exercise;

/*
 * This class is used to add a new exercise type
 * This class is also used to update a existing exercise type
 * This class is also used to delete a existing exercise type when it is not in use and it is not the last existing exercise type
 */

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowAddExerciseType extends Activity {
	private DbAdapter dbHelper;
	private EditText etName, etDescription;
	private Button btAdd, btDelete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_add_exercise_type);
		etName = (EditText) findViewById(R.id.editTextShowAddExerciseTypeName);
		etDescription = (EditText) findViewById(R.id.editTextShowAddExerciseTypeDescription);
		btAdd = (Button) findViewById(R.id.buttonAdd);
		btDelete = (Button) findViewById(R.id.buttonDelete);
		btDelete.setVisibility(View.GONE);
		dbHelper = new DbAdapter(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();

		if (getIntent().getExtras().getString(DataParser.whatToDo)
				.equals(DataParser.doUpdateExerciseType))
			fillExistingData();

	}

	// This method is called when we update a existing exercise type
	private void fillExistingData() {
		Cursor cExerciseType = dbHelper.fetchExerciseTypeByID(getIntent()
				.getExtras().getLong(DataParser.idExerciseType));

		cExerciseType.moveToFirst();

		etName.setText(cExerciseType.getString(cExerciseType
				.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_NAME)));
		etDescription
				.setText(cExerciseType.getString(cExerciseType
						.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_DESCRIPTION)));

		cExerciseType.close();

		btAdd.setText(getResources().getString(R.string.update));
		checkButtonDelete();
	}

	// This method will check if the exercise type is in use
	// if the exercise type is not in use and its not the last one we can show
	// the button delete
	private void checkButtonDelete() {
		boolean showBtDelete = true;

		// check if the exercise type is in use
		Cursor cExerciseEvents = dbHelper.fetchAllExerciseEvents();
		if (cExerciseEvents.getCount() > 0) {
			cExerciseEvents.moveToFirst();
			do {
				if (cExerciseEvents
						.getLong(cExerciseEvents
								.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISEEVENT_EXERCISETYPEID)) == getIntent()
						.getExtras().getLong(DataParser.idExerciseType))
					showBtDelete = false;
			} while (cExerciseEvents.moveToNext());
		}
		cExerciseEvents.close();

		// if the exercise type is not in use check it it is the last one
		if (showBtDelete) {
			if (dbHelper.fetchAllExerciseTypes().getCount() < 2) {
				showBtDelete = false;
			}
		}

		// if still showBtDelete = true then we set the button to visible
		if (showBtDelete)
			btDelete.setVisibility(View.VISIBLE);
	}

	// on click add button
	public void onClickAdd(View view) {
		// name cant be ""
		if (etName.length() <= 0)
			Toast.makeText(
					this,
					getResources().getString(
							R.string.exercise_type_cant_be_empty),
					Toast.LENGTH_LONG).show();
		else {
			// update exercise type
			if (getIntent().getExtras().getString(DataParser.whatToDo)
					.equals(DataParser.doUpdateExerciseType)) {
				dbHelper.updateExerciseTypeByID(getIntent().getExtras()
						.getLong(DataParser.idExerciseType), etName.getText()
						.toString(), etDescription.getText().toString());
			} else {
				// create new exercise type
				dbHelper.createExerciseType(etName.getText().toString(),
						etDescription.getText().toString());
			}
			setResult(RESULT_OK);
			finish();
		}
	}

	// on click delete button
	public void onClickDelete(View view) {
		dbHelper.deleteExerciseTypeByID(getIntent().getExtras().getLong(
				DataParser.idExerciseType));
		setResult(RESULT_OK);
		finish();
	}

	// on click cancel button
	public void onClickCancel(View view) {
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}
}
