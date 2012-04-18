package be.goossens.oracle.Show;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import be.goossens.oracle.R;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowStart extends Activity {

	private DbAdapter dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_start);

		dbHelper = new DbAdapter(this);
		new checkDatabase().execute();
	}

	private class checkDatabase extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				dbHelper.createDataBase();
			} catch (IOException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			goToRightPage();
		}
	}

	private void goToRightPage() {
		Intent i = new Intent(this, ShowHomeTab.class);
		startActivity(i);

		// check to see if we have to show the selectLanguage page
		dbHelper.open();
		
		Cursor cSetting = dbHelper.fetchSettingByName(getResources().getString(
				R.string.language));
		cSetting.moveToFirst();
		 
		if (cSetting.getLong(cSetting
				.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)) == 0) {
			Intent m = new Intent(this, ShowSelectLanguage.class);
			startActivity(m);
		}
		
		cSetting.close();
		dbHelper.close();
	}
}
