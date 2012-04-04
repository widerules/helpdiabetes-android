package be.goossens.oracle.ActivityGroup;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Show.ShowHomeTab;
import be.goossens.oracle.Show.Tracking.ShowTracking;

public class ActivityGroupTracking extends ActivityGroup {

	// keep this in a static variable to make it accessible for all the nesten
	// activities, let them manipulate the view
	public static ActivityGroupTracking group;
 
	// Need to keep track of the history so the back button works properly
	private ArrayList<View> history;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.history = new ArrayList<View>();
		group = this;
		
		// Start the root activity within the group and get its view
		View view = getLocalActivityManager().startActivity(
				DataParser.activityIDTracking,
				new Intent(this, ShowTracking.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();
		// Replace the view of this activityGroup
		replaceView(view);
	}

	private void replaceView(View view) {
		// Adds the old one to history
		history.add(view);
		// changes this group view to the new view
		setContentView(view);
	}

	public void back() {
		if (history.size() > 0) {
			history.remove(history.size() - 1);
			setContentView(history.get(history.size() - 1));
		} else {
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
		}
		return super.onKeyDown(keyCode, event);
	}
}
