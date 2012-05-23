// Please read info.txt for license and legal information

package be.goossens.oracle.Show;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupExercise;
import be.goossens.oracle.ActivityGroup.ActivityGroupGlucose;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.ActivityGroup.ActivityGroupMedicine;
import be.goossens.oracle.ActivityGroup.ActivityGroupSettings;
import be.goossens.oracle.ActivityGroup.ActivityGroupTracking;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.TrackingValues;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class ShowHomeTab extends TabActivity {
	private TabHost tabHost;
	public static TabActivity context;
	public GoogleAnalyticsTracker tracker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_home_tab);

		context = this;

		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(TrackingValues.trackingCode, this);
		
		//to upload all data to the cloud
		tracker.setSampleRate(100);
		
		tabHost = getTabHost();

		setupTabHost();
	}

	public void trackPageView(String value){
		tracker.trackPageView(value);
		tracker.dispatch();
	}
	
	public void trackEvent(String categorie, String action){
		tracker.setCustomVar(0, categorie, action);
		tracker.dispatch();
	}
	
	@Override
	public void finish() {
		tracker.stopSession();
		super.finish();
	}
	
	// when we press the home button we set startup back to true!
	@Override
	protected void onUserLeaveHint() {
		//mark food data startUp true
		ActivityGroupMeal.group.getFoodData().startUp = true;
		super.onUserLeaveHint();
	}

	private void setupTabHost() {
		int counter = 0;

		Intent in = new Intent(this, ActivityGroupTracking.class);
		setupTab(new TextView(this), DataParser.activityIDTracking,
				R.drawable.ic_tab_tracking, in);
		counter++;

		in = new Intent(this, ActivityGroupMeal.class);
		setupTab(new TextView(this), DataParser.activityIDMeal,
				R.drawable.ic_tab_meal, in);
		counter++;

		in = new Intent(this, ActivityGroupExercise.class);
		setupTab(new TextView(this), DataParser.activityIDExercise,
				R.drawable.ic_tab_exercise, in);
		counter++;

		in = new Intent(this, ActivityGroupGlucose.class);
		setupTab(new TextView(this), DataParser.activityIDGlucose,
				R.drawable.ic_tab_glucose, in);
		counter++;
 
		in = new Intent(this, ActivityGroupMedicine.class);
		setupTab(new TextView(this), DataParser.activityIDMedicine,
				R.drawable.ic_tab_medicine, in);
		counter++;

		in = new Intent(this, ActivityGroupSettings.class);
		setupTab(new TextView(this), DataParser.activityIDSettings,
				R.drawable.ic_tab_settings, in);
		counter++;
		
		// set default tab on show food list
		goToTab(DataParser.activityIDMeal);
	}

	private void setupTab(final View view, final String tag, final int image,
			Intent in) {

		View tabview = createTabView(tabHost.getContext(), image);

		TabSpec spec = tabHost.newTabSpec(tag).setIndicator(tabview)
				.setContent(new TabContentFactory() {
					public View createTabContent(String tag) {
						return view;
					}
				}).setContent(in);
		tabHost.addTab(spec);
	}

	public void goToTab(String tag) {
		tabHost.setCurrentTabByTag(tag);
	}

	private static View createTabView(final Context context, final int image) {
		View view = LayoutInflater.from(context)
				.inflate(R.layout.tabs_bg, null);
		ImageView iv = (ImageView) view.findViewById(R.id.imageViewTabs);
		iv.setImageResource(image);
		return view;
	}

}