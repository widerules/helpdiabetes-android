package be.goossens.oracle.Show;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class ShowHomeTab extends TabActivity {
	private TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_home_tab);
		tabHost = getTabHost();
		setupTabHost();
	}

	private void setupTabHost() {
		Intent in = new Intent(this, ActivityGroupMeal.class);
		setupTab(new TextView(this), "tab1", R.drawable.ic_tab_meal, in);

		in = new Intent(this, ActivityGroupTracking.class);
		setupTab(new TextView(this), "tab2", R.drawable.ic_tab_tracking, in);

		in = new Intent(this, ActivityGroupExercise.class);
		setupTab(new TextView(this), "tab3", R.drawable.ic_tab_exercise, in);

		in = new Intent(this, ActivityGroupGlucose.class);
		setupTab(new TextView(this), "tab4", R.drawable.ic_tab_glucose, in);

		in = new Intent(this, ActivityGroupMedicine.class);
		setupTab(new TextView(this), "tab5", R.drawable.ic_tab_medicine, in);

		in = new Intent(this, ActivityGroupSettings.class);
		setupTab(new TextView(this), "tab6", R.drawable.ic_tab_settings, in);
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
		keyboardDissapear();
	}

	// let the keyboard dissapear
	private void keyboardDissapear() {
		try {
			InputMethodManager inputManager = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
		}
	}

	private static View createTabView(final Context context, final int image) {
		View view = LayoutInflater.from(context)
				.inflate(R.layout.tabs_bg, null);
		ImageView iv = (ImageView) view.findViewById(R.id.imageViewTabs);
		iv.setImageResource(image);
		return view;

	}

}