package be.goossens.oracle.Show;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import be.goossens.oracle.R;
import be.goossens.oracle.Rest.DbAdapter;

public class ShowStart extends Activity {

	private DbAdapter dbHelper;
	private boolean firstTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_start);

		dbHelper = new DbAdapter(this);
		firstTime = false;
		new checkDatabase().execute();
	}

	private class checkDatabase extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				firstTime = dbHelper.createDataBase();
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

		// if firsttime == true then we have to show the select language page
		if (firstTime) {
			Intent m = new Intent(this, ShowSelectLanguage.class);
			startActivity(m);
		}
	}
}
