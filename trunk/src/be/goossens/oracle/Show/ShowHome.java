package be.goossens.oracle.Show;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.Custom.CustomArrayAdapterHome;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Show.Exercise.ShowAddExerciseEvent;
import be.goossens.oracle.Show.Food.ShowFoodList;
import be.goossens.oracle.Show.Settings.ShowSettings;
import be.goossens.oracle.Show.Tracking.ShowTracking;

public class ShowHome extends ListActivity {
	private DbAdapter dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_home);
		dbHelper = new DbAdapter(this);
		dbHelper.createDatabase();
		filListView();
	} 

	// This method will fill the listview from the source file
	private void filListView() {
		/*
		 * ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		 * this, R.array.home_array, android.R.layout.simple_list_item_1);
		 */
		CustomArrayAdapterHome adapter = new CustomArrayAdapterHome(this,
				R.layout.row_custom_array_adapter_home, getArrayList(),
				getColors());
		setListAdapter(adapter);
	}

	private int[] getColors() {
		return new int[] { getResources().getColor(R.color.colorTracking), getResources().getColor(R.color.colorFood),
				getResources().getColor(R.color.colorSport),
				getResources().getColor(R.color.colorInsuline),
				getResources().getColor(R.color.colorGlucose) };
	}

	private List<String> getArrayList() {
		List<String> value = new ArrayList<String>();
		String[] arr = getResources().getStringArray(R.array.home_array);
		for (int i = 0; i < arr.length - 1; i++) {
			value.add(arr[i]);
		}
		return value;
	}

	// when we click on a list item!
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = null;
		switch (position) {
		case 0:
			// rapport activity
			i = new Intent(this, ShowTracking.class);
			break;
		case 1:
			// meal activity
			i = new Intent(this, ShowFoodList.class);
			break;
		case 2:
			// sport activity
			i = new Intent(this, ShowAddExerciseEvent.class);
			i.putExtra(DataParser.whatToDo, DataParser.doCreateExerciseEvent);
			break;
		case 3:
			// medicine acitivty
			Toast.makeText(this, "stil under construction", Toast.LENGTH_LONG)
					.show();
			break;
		case 4:
			// glucose activity
			Toast.makeText(this, "stil under construction", Toast.LENGTH_LONG)
					.show();
			break;
		case 5:
			// setting
			i = new Intent(this, ShowSettings.class);
			break;
		}
		if (i != null)
			startActivity(i);
		// super.onListItemClick(l, v, position, id);
	}
}
