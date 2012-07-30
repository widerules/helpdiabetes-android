package com.hippoandfriends.helpdiabetes.Show.Tracking;

import java.util.Calendar;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hippoandfriends.helpdiabetes.R;
import com.hippoandfriends.helpdiabetes.ActivityGroup.ActivityGroupTracking;
import com.hippoandfriends.helpdiabetes.Rest.DbAdapter;
import com.hippoandfriends.helpdiabetes.Rest.Functions;

public class ShowTrackingAScatterChartBloodGlucose extends Activity {
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private GraphicalView mChartView;
	private XYSeries series = new XYSeries("");
	private Calendar mCalendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xy_chart);

		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText(getResources().getString(R.string.titelAddGlucose));

		Button btBack = (Button) findViewById(R.id.buttonBack);
		btBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ActivityGroupTracking.group.back();
			}
		});

		Button btForward = (Button) findViewById(R.id.ButtonForward);
		btForward.setVisibility(View.INVISIBLE);

		mCalendar = Calendar.getInstance();

		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		mChartView = ChartFactory
				.getScatterChartView(this, mDataset, mRenderer);
		layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		mDataset.addSeries(series);

		XYSeriesRenderer renderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(renderer);
		renderer.setPointStyle(PointStyle.DIAMOND);
		renderer.setFillPoints(true);
		renderer.setColor(Color.WHITE);
		
		mChartView.addZoomListener(new ZoomListener() {
			
			public void zoomReset() {
				
			}
			
			public void zoomApplied(ZoomEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		}, false, true);
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
			mRenderer.getYAxisMax();
			mRenderer.getYAxisMin();
			mRenderer.getXAxisMin();
			mRenderer.getXAxisMax();

			mChartView.repaint();
			super.onPostExecute(result);
		}

	}

	private void getData() {
		// claer old data
		series.clear();
		
		// set new title 
		series.setTitle(android.text.format.DateFormat.getDateFormat(this).format(mCalendar.getTime()));

		// used to hold the min time , max time , min glucose and max glucose
		//int minGlucose = 99999999;
		//int maxGlucose = 0;
		//int minTime = 99999999;
		//int maxTime = 0;

		DbAdapter db = new DbAdapter(this);
		db.open();

		Cursor cGlucose = db.fetchBloodGlucoseEventByDate(new Functions()
				.getYearMonthDayAsStringFromDate(mCalendar.getTime()));

		// draw every glucose
		if (cGlucose.getCount() > 0) {
			cGlucose.moveToFirst();
			do {
				int calcTime = new Functions()
						.getHourAndMinutesFromString(cGlucose.getString(cGlucose
								.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_EVENTDATETIME)));

				int glucose = cGlucose
						.getInt(cGlucose
								.getColumnIndexOrThrow(DbAdapter.DATABASE_BLOODGLUCOSEEVENT_AMOUNT));

				// add the time & glucose to the graph
				series.add(calcTime, glucose);

				// check if we have new max || min values
				/*if (minGlucose > glucose)
					minGlucose = glucose;
				
				if (maxGlucose < glucose)
					maxGlucose = glucose;
				
				if (minTime > calcTime)
					minTime = calcTime;
				
				if (maxTime < calcTime)
					maxTime = calcTime;*/
				
			} while (cGlucose.moveToNext());
		}
		cGlucose.close();		
		db.close();
		 
		//do minGlucose - 50, minTime -50
		//maxGlucose + 50, maxTime +50
		//and add them to the list
		/*minGlucose -= 10;
		minTime -= 10;
		
		maxGlucose += 10;
		maxTime += 10;
		
		series.add(maxTime, maxGlucose);
		series.add(minTime, minGlucose);*/
	}
}
