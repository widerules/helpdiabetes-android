package com.hippoandfriends.helpdiabetes.Show.Tracking;

import java.util.Calendar;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupMeal;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupTracking;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.Functions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class ShowTrackingDefaultInsulineChart extends Activity {
	private Button btBack, btForward;
	private GraphViewData[] listData;
	private TextView tv;
	private String titel;
	private int dataLength = 8;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graphs);

		tv = (TextView) findViewById(R.id.textView1);
		tv.setText(getResources().getString(R.string.loading));

		btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupTracking.group.back();
			}
		});

		btForward = (Button) findViewById(R.id.ButtonForward);
		btForward.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new Async().execute();
	}

	private class Async extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			getData();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			tv.setText(titel);
			createChart();
			super.onPostExecute(result);
		}

	}

	private void getData() {
		DbAdapter db = new DbAdapter(this);
		db.open();

		listData = new GraphViewData[dataLength];

		Calendar time = Calendar.getInstance();

		// set the right title
		Cursor cMedicineType = db.fetchMedicineTypesByID(ActivityGroupMeal.group
				.getFoodData().defaultMedicineTypeID);
		if (cMedicineType.getCount() > 0) {
			cMedicineType.moveToFirst();
			titel = ""
					+ cMedicineType.getString(cMedicineType
							.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINETYPE_MEDICINENAME));
		} else {
			titel = "";
		}
		cMedicineType.close();

		// loop
		for (int i = dataLength-1; i >= 0; i--) {
			Cursor c = db
					.fetchMedicineEventByDateAndTypeID(
							new Functions()
									.getYearMonthDayAsStringFromDate(time
											.getTime()),
							ActivityGroupMeal.group.getFoodData().defaultMedicineTypeID);

			if (c.getCount() > 0) {
				int middle = 0;
				c.moveToFirst();
				do {
					middle += c
							.getInt(c
									.getColumnIndexOrThrow(DbAdapter.DATABASE_MEDICINEEVENT_AMOUNT));
				} while (c.moveToNext());

				// do / getCount so we have the middle
				middle = middle / c.getCount();
 
				// add it to the graph
				listData[i] = new GraphViewData(i, middle);

			} else {
				listData[i] = new GraphViewData(i, 0);
			}
			
			//do time - 1 day
			time.add(Calendar.DAY_OF_MONTH, -1);
		}
		db.close();
	}

	private void createChart() {
		GraphViewSeries data = new GraphViewSeries(listData);

		GraphView graphView = new LineGraphView(ActivityGroupTracking.group, "") {

			@Override
			protected String formatLabel(double value, boolean isValueX) {
				if (isValueX)
					return new Functions().getDayAndMonthFromDouble(value - dataLength - 1);
				else
					return super.formatLabel(value, isValueX);
			}
		};

		graphView.addSeries(data);
		
		//draw background
		((LineGraphView) graphView).setDrawBackground(true);
				
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.addView(graphView);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if we press the back key
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
			// return false so the keydown event from activitygroupmeal will get
			// called
			return false;
		else
			return super.onKeyDown(keyCode, event);
	}
}
