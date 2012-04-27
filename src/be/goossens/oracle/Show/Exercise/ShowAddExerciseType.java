package be.goossens.oracle.Show.Exercise;

/*
 * This class is used to add a new exercise type
 * This class is also used to update a existing exercise type
 * This class is also used to delete a existing exercise type when it is not in use and it is not the last existing exercise type
 */

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupSettings;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowAddExerciseType extends Activity {
	private EditText etName;
	private Button btAdd, btDelete;
	private long excistingExerciseTypeID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_add_exercise_type, null);
		setContentView(contentView);

		setContentView(R.layout.show_add_exercise_type);
		etName = (EditText) findViewById(R.id.editTextShowAddExerciseTypeName);
		btAdd = (Button) findViewById(R.id.buttonAdd);
		btDelete = (Button) findViewById(R.id.buttonDelete);

		// standard hide the button delete
		btDelete.setVisibility(View.GONE);

		btAdd.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				onClickAdd();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		// we put a try around this code becaus when we come here to create a
		// NEW exercise type this will return a nullpointerexception
		try {
			excistingExerciseTypeID = getIntent().getExtras().getLong(
					DataParser.idExerciseType);
			fillEditText();
		} catch (NullPointerException e) {
			excistingExerciseTypeID = -1;
		}

	}

	private void fillEditText() {
		DbAdapter db = new DbAdapter(this);
		db.open();
		Cursor cExerciseType = db
				.fetchExerciseTypeByID(excistingExerciseTypeID);
		cExerciseType.moveToFirst();
		etName.setText(cExerciseType.getString(cExerciseType
				.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_NAME)));
		cExerciseType.close();
		db.close();
	}

	public void test(String txt) {
		Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
	}

	// on click button add
	private void onClickAdd() {
		if (etName.length() > 0) {
			DbAdapter db = new DbAdapter(this);
			db.open();
			
			//create new one
			if (excistingExerciseTypeID == -1) {
				

				// create the exercise type
				long exerciseType = db.createExerciseType(etName.getText()
						.toString(), "");

				db.close();

				// add the created item to the list of food on the exercise
				// TYPES
				// activity
				ActivityGroupSettings.group.getExerciseTypes()
						.addExerciseTypeToList(exerciseType);

				// go back to the list
				ActivityGroupSettings.group.back();
			} else {
				//update one
				db.updateExerciseTypeByID(excistingExerciseTypeID, etName.getText().toString(), "");
				
				//refresh the list
				ActivityGroupSettings.group.getExerciseTypes().refreshObject(excistingExerciseTypeID, etName.getText().toString());
				
				//go back 
				ActivityGroupSettings.group.back();
			}
		} else {
			Toast.makeText(
					this,
					getResources().getString(
							R.string.exercise_type_cant_be_empty),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
