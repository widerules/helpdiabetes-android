package be.goossens.oracle.ActivityGroup;

import java.util.ArrayList;

import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Show.Exercise.ShowAddExerciseEvent;
import be.goossens.oracle.Show.Tracking.ShowTracking;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

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

		// make a root activity when the history size = 0
		if (history.size() == 0) {
			// Start the root activity within the group and get its view
			View view = getLocalActivityManager().startActivity(
					DataParser.activityIDTracking,
					new Intent(this, ShowTracking.class).addFlags(
							Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
			replaceView(view);
		}
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

	@Override
	public void onContentChanged() {
		keyboardDissapear();
		super.onContentChanged();
	}

	@Override
	public void setContentView(View view) {
		replaceView(view);
	}

	private void replaceView(View view) {
		// Adds the old one to history
		history.add(view);
		// changes this group view to the new view
		super.setContentView(view);
	}

	public void back() {
		try {
			// if we set history.size() > 0 and we press back key on home
			// activity
			// and then on another activity we wont get back!
			if (history.size() > 1) {
				history.remove(history.size() - 1);
				// call the super.setContent view! so set the real view
				super.setContentView(history.get(history.size() - 1));
			} else {
				Toast.makeText(this, "you cant close this app! \n hehe :-)",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			if (history.size() >= 0)
				super.setContentView(history.get(0));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			back();
		}
		return true;
	}

	@Override
	public void finish() {
		Toast.makeText(this, "lol no finish for u ;)", Toast.LENGTH_LONG)
				.show();
	}
}
