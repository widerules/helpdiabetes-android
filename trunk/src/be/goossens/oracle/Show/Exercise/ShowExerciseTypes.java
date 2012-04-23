package be.goossens.oracle.Show.Exercise;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import be.goossens.oracle.R;
import be.goossens.oracle.Custom.CustomArrayAdapterDBExerciseType;
import be.goossens.oracle.Objects.DBExerciseType;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowExerciseTypes extends ListActivity {
	private DbAdapter dbHelper;
	private List<DBExerciseType> listExerciseTypes;
	private final static int requestCodeAddExercise = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_exercise_types);
		dbHelper = new DbAdapter(this);
		registerForContextMenu(getListView());
	}

	// update exercise type when we select one from the list
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(this, ShowAddExerciseType.class);
		i.putExtra(DataParser.whatToDo, DataParser.doUpdateExerciseType);
		i.putExtra(DataParser.idExerciseType, listExerciseTypes.get((int)id).getId());
		startActivityForResult(i, requestCodeAddExercise);
		super.onListItemClick(l, v, position, id);
	}

	// on click button add exercise type
	public void onClickAddExerciseType(View view) {
		Intent i = new Intent(this, ShowAddExerciseType.class); 
		i.putExtra(DataParser.whatToDo, DataParser.doCreateExerciseType);
		startActivityForResult(i, requestCodeAddExercise);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// without this dbHelper.open() the app will crash if we come back from
		// another page
		dbHelper.open();
		switch (requestCode) {
		case requestCodeAddExercise:
			if (resultCode == RESULT_OK)
				refresh();
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		refresh();
	}

	private void refresh() {
		fillListExerciseTypes();
		fillListView();
	}

	private void fillListView() {
		Cursor cSettings = dbHelper.fetchSettingByName(getResources()
				.getString(R.string.setting_font_size));
		cSettings.moveToFirst();

		CustomArrayAdapterDBExerciseType adapter = new CustomArrayAdapterDBExerciseType(
				this,
				R.layout.row_custom_array_adapter,
				listExerciseTypes,
				cSettings.getInt(cSettings
						.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));

		setListAdapter(adapter);

		cSettings.close();
	}

	// This method will fill the list object with the right items
	private void fillListExerciseTypes() {
		listExerciseTypes = new ArrayList<DBExerciseType>();
		Cursor cExerciseType = dbHelper.fetchAllExerciseTypes();

		if (cExerciseType.getCount() > 0) {
			cExerciseType.moveToFirst();
			do {
				listExerciseTypes
						.add(new DBExerciseType(
								cExerciseType
										.getLong(cExerciseType
												.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_ID)),
								cExerciseType.getString(cExerciseType
										.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_NAME)),
								cExerciseType.getString(cExerciseType
										.getColumnIndexOrThrow(DbAdapter.DATABASE_EXERCISETYPE_DESCRIPTION))));
			} while (cExerciseType.moveToNext());
		}

		cExerciseType.close();
	}

	
	
	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

}
