package com.hippoandfriends.helpdiabetes.Show.Tracking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
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
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupTracking;
import com.hippoandfriends.helpdiabetes.Objects.DBDoubleAndDouble;
import com.hippoandfriends.helpdiabetes.Rest.DataParser;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.Functions;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;

public class ShowTrackingBloodGlucoseChart extends Activity {

	private Button btBack, btForward;
	// private GraphViewData[] listData;

	private List<DBDoubleAndDouble> listValues;

	private int dataLength = 8;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graphs);

		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText(getResources().getString(R.string.titelAddGlucose));

		btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityGroupTracking.group.back();
			}
		});

		btForward = (Button) findViewById(R.id.ButtonForward);
		btForward.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				View view = ActivityGroupTracking.group
						.getLocalActivityManager()
						.startActivity(
								DataParser.activityIDTracking,
								new Intent(ActivityGroupTracking.group,
										ShowTrackingDefaultInsulineChart.class)
										.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();
				ActivityGroupTracking.group.setContentView(view);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		new AsyncGetData().execute();
	}

	private class AsyncGetData extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			getData();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			createChart();
		}
	}

	// get data for the last datalength days
	private void getData() {
		DbAdapter db = new DbAdapter(this);

		listValues = new ArrayList<DBDoubleAndDouble>();

		// listData = new GraphViewData[dataLength];

		Calendar time = Calendar.getInstance();
		db.open();

		// loop
		for (int i = 0; i < dataLength; i++) {
			Cursor cInsuline = db.fetchBloodGlucoseEventByDate(new Functions()
					.getYearMonthDayAsStringFromDate(time.getTime()));

			// if we have insuline for that day we calculated the middle
			if (cInsuline.getCount() > 0) {
				int middle = 0;

				cInsuline.moveToFirst();

				do {
					middle += cInsuline
							.getInt(cInsuline
									.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_AMOUNT));
				} while (cInsuline.moveToNext());

				// do / amount of insuline of that day
				middle = middle / cInsuline.getCount();

				if (middle != 0) {
					// add it to the list
					listValues.add(new DBDoubleAndDouble(new Double(i),
							new Double(middle)));
				}
				// listData[i] = new GraphViewData(i, middle);

			} else {
				// else we add zero
				// listData[i] = new GraphViewData(i, 0);
			}

			// date - 1 day
			time.add(Calendar.DAY_OF_MONTH, -1);
			cInsuline.close();
		}
		db.close();
	}

	/*private boolean checkDecimalPart(Double value) {
		Double dValue = value;
		int iValue = (int) Math.floor(value);
		dValue -= new Double(iValue);

		// if decimals behind comma == 0 we return true
		if (dValue == 0)
			return true;
		else
			// else return false
			return false;
	}*/

	private void createChart() {
		if (listValues.size() > 0) {
			// create graphview
			GraphViewData[] listData = new GraphViewData[listValues.size()];

			for (int i = listValues.size() - 1; i >= 0; i--) {
				listData[listValues.size() - i - 1] = new GraphViewData(
						listValues.get(i).getValueX(), listValues.get(i)
								.getValueY());
			}
			
			//listData[listValues.size()+1] = new GraphViewData(-1, 0);
			
			GraphViewSeries data = new GraphViewSeries(listData);

			
			GraphView graphView = new BarGraphView(ActivityGroupTracking.group, "") {

				@Override
				protected String formatLabel(double value, boolean isValueX) {
					if (isValueX) {
						//if (checkDecimalPart(value))
							return new Functions()
									.getDayAndMonthFromDouble(-value);
						//else
						//	return "";
					} else
						return super.formatLabel(value, isValueX);
				}
			};

			graphView.addSeries(data);

			// draw background
			//((LineGraphView) graphView).setDrawBackground(true);

			// graphView.setShowLegend(true);

			LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
			layout.addView(graphView);
		}
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
