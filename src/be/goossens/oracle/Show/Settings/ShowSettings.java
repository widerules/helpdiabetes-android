package be.goossens.oracle.Show.Settings;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.ActivityGroup.ActivityGroupSettings;
import be.goossens.oracle.Custom.CustomArrayAdapterCharSequenceSettings;

public class ShowSettings extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View contentView = LayoutInflater.from(getParent()).inflate(
				R.layout.show_settings, null);
		setContentView(contentView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillListView();
	}

	private void fillListView() {
		CustomArrayAdapterCharSequenceSettings adapter = new CustomArrayAdapterCharSequenceSettings(
				this, R.layout.row_custom_array_adapter_charsequence_settings,
				getCharSequenceList());

		setListAdapter(adapter);
	}

	private List<CharSequence> getCharSequenceList() {
		List<CharSequence> value = new ArrayList<CharSequence>();
		String[] arr = getResources().getStringArray(R.array.settings_array);
		for (int i = 0; i < arr.length; i++) {
			value.add(arr[i]);
		}
		return value;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = null;
		switch (position) {
		case 0:
			i = new Intent(getApplicationContext(), ShowSettingsMealTimes.class);
			break;
		case 1:
			i = new Intent(getApplicationContext(),
					ShowSettingsInsulineRatio.class);
			break;
		case 2:
			i = new Intent(getApplicationContext(),
					ShowSettingsGlucoseUnit.class);
			break;
		case 3:
			i = new Intent(getApplicationContext(),
					ShowSettingsValueOrder.class);
			break;
		case 4:
			i = new Intent(getApplicationContext(),
					ShowSettingsFontSizeLists.class);
			break;
		}
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		View view = ActivityGroupSettings.group.getLocalActivityManager()
				.startActivity("ShowSetting", i).getDecorView();

		ActivityGroupSettings.group.setContentView(view);

	}

	// if we press the back button on this activity we have to show a popup to
	// exit
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			showPopUpToExitApplication();
			// when we return true here we wont call the onkeydown from
			// activitygroupmeal
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
					ActivityGroupSettings.group.killApplication();
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityGroupSettings.group);
		builder.setMessage(getResources().getString(R.string.sureToExit))
				.setPositiveButton(getResources().getString(R.string.yes),
						dialogClickListener)
				.setNegativeButton(getResources().getString(R.string.no),
						dialogClickListener).show();
	}

}
