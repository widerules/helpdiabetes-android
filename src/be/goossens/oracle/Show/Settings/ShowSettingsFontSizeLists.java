package be.goossens.oracle.Show.Settings;

import java.util.ArrayList;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import be.goossens.oracle.R;
import be.goossens.oracle.Custom.CustomArrayAdapter;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowSettingsFontSizeLists extends ListActivity {

	private DbAdapter dbHelper;
	private EditText etFontSize;
	private ArrayList<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_font_size_list);
		dbHelper = new DbAdapter(this);

		list = getList();

		etFontSize = (EditText) findViewById(R.id.editTextShowSettingsFontSizeList);

		etFontSize.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				saveSize();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		fillTvFontSize();
		updateList();
	}

	private void saveSize() {
		int fontSize = 0;
		try {
			fontSize = Integer.parseInt(etFontSize.getText().toString());
			dbHelper.updateSettingsByName(
					getResources().getString(R.string.font_size), "" + fontSize);
			updateList();
		} catch (Exception e) {
		}
	}

	private void updateList() {
		int fontSize = 0;
		try {
			fontSize = Integer.parseInt(etFontSize.getText().toString());
		} catch (Exception e) {
			fontSize = 0;
		}
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
		etFontSize.setText(cSetting.getString(cSetting
				.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)));
		cSetting.close();
	}

	private void changeFontSize(int number){
		int fontSize = 0;
		try {
			fontSize = Integer.parseInt(etFontSize.getText().toString());
		} catch (Exception e){
			fontSize = 0;
		} 
		fontSize += number;
		if(fontSize < 0)
			fontSize = 0;
		etFontSize.setText("" + fontSize);
	}
	
	public void onClickFontUp(View view) {
		changeFontSize(1);
	}

	public void onClickFontDown(View view) {
		changeFontSize(-1);
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}
	
	public void onClickBack(View view){
		finish();
	}

}
