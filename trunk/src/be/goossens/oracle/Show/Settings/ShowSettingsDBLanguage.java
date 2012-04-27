package be.goossens.oracle.Show.Settings;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.ActivityGroup.ActivityGroupSettings;
import be.goossens.oracle.Custom.CustomArrayAdapterDBFoodLanguage;
import be.goossens.oracle.Objects.DBFoodLanguage;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowSettingsDBLanguage extends ListActivity {

	private List<DBFoodLanguage> objects;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_db_language);
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillListView();
	}

	private void fillListView() {
		DbAdapter db = new DbAdapter(this);
		db.open();

		objects = new ArrayList<DBFoodLanguage>();

		Cursor cFoodLanguage = db.fetchAllFoodLanguages();
		if (cFoodLanguage.getCount() > 0) {
			cFoodLanguage.moveToFirst();
			do {
				objects.add(new DBFoodLanguage(
						cFoodLanguage
								.getLong(cFoodLanguage
										.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODLANGUAGE_ID)),
						cFoodLanguage.getString(cFoodLanguage
								.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODLANGUAGE_LANGUAGE))));
				
			} while (cFoodLanguage.moveToNext()); 
		}
		cFoodLanguage.close();
		db.close();
		
		CustomArrayAdapterDBFoodLanguage adapter = new CustomArrayAdapterDBFoodLanguage(
				this, android.R.layout.simple_list_item_1, objects);
 
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//save the new languageID to the settings
		DbAdapter db = new DbAdapter(this);
		db.open();
		db.updateSettingsByName(getResources().getString(R.string.setting_language), "" + objects.get(position).getId());
		db.close();
		
		//restart the activitygroupmeal
		ActivityGroupMeal.group.restartThisActivity();
		
		//go back to settings page
		ActivityGroupSettings.group.back();
		
		super.onListItemClick(l, v, position, id);
	}
	
	// override the onkeydown so we can go back to the main setting page
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
