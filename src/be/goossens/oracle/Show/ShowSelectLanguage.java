package be.goossens.oracle.Show;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import be.goossens.oracle.R;
import be.goossens.oracle.Custom.CustomArrayAdapterFoodLanguage;
import be.goossens.oracle.Objects.DBFoodLanguage;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowSelectLanguage extends Activity {

	private DbAdapter dbHelper;
	private ListView lv;
	private List<DBFoodLanguage> objects;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_select_language);
		dbHelper = new DbAdapter(this);
		dbHelper.open();

		lv = (ListView) findViewById(R.id.listView1);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				clickOnItem(arg2);
			}
		});

		fillListView();
	}

	private void fillObjects() {
		objects = new ArrayList<DBFoodLanguage>();
		Cursor cFoodLanguages = dbHelper.fetchAllFoodLanguages();
		if (cFoodLanguages.getCount() > 0) {
			cFoodLanguages.moveToFirst();

			do {
				objects.add(new DBFoodLanguage(
						cFoodLanguages
								.getLong(cFoodLanguages
										.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODLANGUAGE_ID)),
						cFoodLanguages.getString(cFoodLanguages
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODLANGUAGE_LANGUAGE))));
			} while (cFoodLanguages.moveToNext());
		}
		cFoodLanguages.close();
	} 

	private void fillListView() {
		fillObjects();

		CustomArrayAdapterFoodLanguage adapter = new CustomArrayAdapterFoodLanguage(
				this, R.layout.row_custom_array_adapter_food_language, objects);
		
		lv.setAdapter(adapter);
	}

	private void clickOnItem(int position) {
		// update the setting languageID
		dbHelper.updateSettingsByName(
				getResources().getString(R.string.setting_language), "" + objects.get(position).getId());
 
		// close the db
		dbHelper.close();
		// go back to foodlist
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
