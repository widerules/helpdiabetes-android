package be.goossens.oracle.Show.Settings;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import be.goossens.oracle.R;
import be.goossens.oracle.Custom.CustomArrayAdapterCharSequenceSettings;

public class ShowSettings extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings);
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillListView();
	}

	private void fillListView() {
		CustomArrayAdapterCharSequenceSettings adapter = new CustomArrayAdapterCharSequenceSettings(
				this, R.layout.row_custom_array_adapter_charsequence_settings, getCharSequenceList());

		/*
		 * ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		 * this, R.array.settings_array, android.R.layout.simple_list_item_1);
		 */
		setListAdapter(adapter);
	}

	private List<CharSequence> getCharSequenceList() {
		List<CharSequence> value = new ArrayList<CharSequence>();
		String[] arr = getResources().getStringArray(R.array.settings_array);
		for(int i = 0; i < arr.length ; i++){
			value.add(arr[i]);
		}
		return value;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = null;
		switch (position) {
		case 0:
			i = new Intent(this, ShowSettingsMealTimes.class);
			break;
		case 1:
			i = new Intent(this, ShowSettingsInsulineRatio.class);
			break;
		case 2:
			i = new Intent(this, ShowSettingsValueOrder.class);
			break;
		case 3:
			i = new Intent(this, ShowSettingsFontSizeLists.class);
			break;
		}
		startActivity(i);
	}

}
