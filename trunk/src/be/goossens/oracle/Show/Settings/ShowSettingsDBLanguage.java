// Please read info.txt for license and legal information

package be.goossens.oracle.Show.Settings;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.ActivityGroup.ActivityGroupSettings;
import be.goossens.oracle.ActivityGroup.ActivityGroupTracking;
import be.goossens.oracle.Custom.CustomArrayAdapterDBFoodLanguage;
import be.goossens.oracle.Objects.DBFoodLanguage;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.DbSettings;
import be.goossens.oracle.Rest.TrackingValues;

public class ShowSettingsDBLanguage extends ListActivity {

	private List<DBFoodLanguage> objects;
	private Button btBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_db_language);

		try {
			// track we come here
			ActivityGroupSettings.group.parent
					.trackPageView(TrackingValues.pageShowSettingFoodDatabaseLanguage);
		} catch (RuntimeException e) {
		}

		btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupSettings.group.back();
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		fillListView();

		// check if we need to show the button next
		// only need to show first time application starts
		try {
			if (getIntent().getExtras().getString(DataParser.whatToDo)
					.equals(DataParser.doFirstTime)) {
				// hide the button back
				btBack.setVisibility(View.GONE);
			}
		} catch (Exception e) {
		}
	}

	private void fillListView() {
		DbAdapter db = new DbAdapter(this);
		db.open();

		objects = new ArrayList<DBFoodLanguage>();
		DBFoodLanguage objectCustomFoodLanguage = null;

		Cursor cFoodLanguage = db.fetchAllFoodLanguages();
		if (cFoodLanguage.getCount() > 0) {
			cFoodLanguage.moveToFirst();
			do {
				if (!cFoodLanguage
						.getString(
								cFoodLanguage
										.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODLANGUAGE_LANGUAGE))
						.equals("Custom")) {
					objects.add(new DBFoodLanguage(
							cFoodLanguage
									.getLong(cFoodLanguage
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODLANGUAGE_ID)),
							cFoodLanguage.getString(cFoodLanguage
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODLANGUAGE_LANGUAGE)),
							cFoodLanguage.getString(cFoodLanguage
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODLANGUAGE_NAME))));
				} else {
					objectCustomFoodLanguage = new DBFoodLanguage(
							cFoodLanguage
									.getLong(cFoodLanguage
											.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODLANGUAGE_ID)),
							cFoodLanguage.getString(cFoodLanguage
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODLANGUAGE_LANGUAGE)),
							cFoodLanguage.getString(cFoodLanguage
									.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODLANGUAGE_NAME)));
				}
			} while (cFoodLanguage.moveToNext());
		}
		cFoodLanguage.close();
		db.close();

		if (objectCustomFoodLanguage != null) {
			// add the custom food item as first item in list
			objects.add(0, objectCustomFoodLanguage);
		}

		CustomArrayAdapterDBFoodLanguage adapter = new CustomArrayAdapterDBFoodLanguage(
				this, R.layout.row_custom_array_adapter_with_arrow, objects);

		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// save the new languageID to the settings
		DbAdapter databaseHelper = new DbAdapter(this);
		databaseHelper.open();
		databaseHelper.updateSettingsByName(DbSettings.setting_language, ""
				+ objects.get(position).getId());
		databaseHelper.close();

		// create string to show message
		String message = "";

		if (position == 0) {
			message = getResources().getString(R.string.customResource);
		} else {
			message = getResources().getString(R.string.resource) + ": "
					+ objects.get(position).getResource();
		}

		// try to go back to
		// when its the first time we run this application
		// we cant go back but we have to finish()
		try {
			if (getIntent().getExtras().getString(DataParser.whatToDo)
					.equals(DataParser.doFirstTime)) {
				// show popup to with resource and call finish
				new AlertDialog.Builder(this)
						.setMessage(message)
						.setPositiveButton(
								getResources().getString(R.string.oke),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										finish();
									}
								}).show();

			} else {
				new AlertDialog.Builder(ActivityGroupSettings.group)
						.setMessage(message)
						.setPositiveButton(
								getResources().getString(R.string.oke),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// restart the activity tracking
										ActivityGroupTracking.group
												.restartThisActivity();

										// go back to settings page
										ActivityGroupSettings.group.back();
									}
								}).show();
			}
		} catch (Exception e) {
			new AlertDialog.Builder(ActivityGroupSettings.group)
					.setMessage(message)
					.setPositiveButton(getResources().getString(R.string.oke),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// restart the activity tracking
									ActivityGroupTracking.group
											.restartThisActivity();

									// restart the activity meal
									ActivityGroupMeal.group
											.restartThisActivity();

									// go back to settings page
									ActivityGroupSettings.group.back();
								}
							}).show();
		}
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
