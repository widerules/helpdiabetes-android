package be.goossens.oracle.Show.Settings;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import be.goossens.oracle.R;

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
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.settings_array,
				android.R.layout.simple_list_item_1);
		setListAdapter(adapter);
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

	// if we click on the back button
	public void onClickBack(View view) {
		finish();
	}
}
