package be.goossens.oracle.Show;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import be.goossens.oracle.R;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowSelectLanguage extends Activity {

	private DbAdapter dbHelper;
	private ListView lv;

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
				clickOnItem(arg3);
			}
		});

		fillListView();
	}

	private void fillListView() {
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1,
				dbHelper.fetchAllFoodLanguages(),
				new String[] { DbAdapter.DATABASE_FOODLANGUAGE_LANGUAGE },
				new int[] { android.R.id.text1 });
		lv.setAdapter(adapter);
	}

	private void clickOnItem(long id) {
		// update the setting languageID
		dbHelper.updateSettingsByName(getResources().getString(R.string.language), "" + id);
		//close the db
		dbHelper.close();
		//go back to foodlist
		finish();
	}

}
