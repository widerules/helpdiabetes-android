package be.goossens.oracle.Show.Settings;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.ActivityGroup.ActivityGroupSettings;
import be.goossens.oracle.Custom.CustomArrayAdapterFontSize;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowSettingsFontSizeLists extends ListActivity {

	private DbAdapter dbHelper;
	private TextView tvFontSize;
	private ArrayList<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_font_size_list);
		dbHelper = new DbAdapter(this);
		list = getList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		fillList();
	}

	private void saveSize(int fontSize) {
		dbHelper.updateSettingsByName(
				getResources().getString(R.string.setting_font_size), "" + fontSize);
	}

	private void fillList() {
		CustomArrayAdapterFontSize adapter = new CustomArrayAdapterFontSize(this,
				R.layout.row_custom_array_adapter, list);
		
		setListAdapter(adapter);
	}
 
	private ArrayList<String> getList() {
		ArrayList<String> returnList = new ArrayList<String>();
		for (int i = 0; i < 53; i++) {
			returnList.add(getResources().getString(R.string.fontSize));
		} 
		return returnList;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//save font size
		saveSize((position+8));
		//refresh the list of food 
		ActivityGroupMeal.group.refreshFoodListFontSize();
		//go back
		ActivityGroupSettings.group.back();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
