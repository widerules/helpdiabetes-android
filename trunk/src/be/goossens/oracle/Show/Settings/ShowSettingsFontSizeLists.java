package be.goossens.oracle.Show.Settings;

import java.util.ArrayList;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.Custom.CustomArrayAdapter;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowSettingsFontSizeLists extends ListActivity {

	private DbAdapter dbHelper;
	private TextView tvFontSize;
	private ArrayList<String> list;
	private int fontSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_font_size_list);
		dbHelper = new DbAdapter(this);
		list = getList();
		tvFontSize = (TextView) findViewById(R.id.textViewShowSettingsFontSizeListTextSize);
		fontSize = 0;
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		fillTvFontSize();
		updateList();
	}

	private void saveSize() {
		dbHelper.updateSettingsByName(
				getResources().getString(R.string.font_size), "" + fontSize);
	}

	private void updateList() {
		CustomArrayAdapter adapter = new CustomArrayAdapter(this,
				R.layout.row_custom_array_adapter, list, fontSize);
		setListAdapter(adapter);
	}

	private ArrayList<String> getList() {
		ArrayList<String> returnList = new ArrayList<String>();
		for (int i = 0; i < 21; i++) {
			returnList
					.add(getResources().getString(R.string.example) + " " + i);
		}
		return returnList;
	}

	private void fillTvFontSize() {
		Cursor cSetting = dbHelper.fetchSettingByName(getResources().getString(
				R.string.font_size));
		cSetting.moveToFirst();
		fontSize = cSetting.getInt(cSetting
				.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE));
		cSetting.close();
		tvFontSize.setText(getResources().getString(R.string.fontSize)
				+ " " + fontSize);
	}

	private void changeFontSize(int number) {
		fontSize += number;
		
		if (fontSize < 3)
			fontSize = 3; 
		else if (fontSize > 50)
			fontSize = 50;

		tvFontSize.setText(getResources().getString(R.string.fontSize)
				+ " " + fontSize);
	}

	public void onClickFontUp(View view) {
		changeFontSize(1);
		saveSize();
		updateList();
	}

	public void onClickFontDown(View view) {
		changeFontSize(-1);
		saveSize();
		updateList();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			//on click back we refresh the lists
			//the show food list
			ActivityGroupMeal.group.refreshShowFoodList();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
