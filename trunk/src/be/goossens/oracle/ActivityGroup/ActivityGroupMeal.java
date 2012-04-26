package be.goossens.oracle.ActivityGroup;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import be.goossens.oracle.Objects.DBFoodComparable;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Show.Food.ShowLoadingFoodData;
import be.goossens.oracle.Show.Food.ShowFoodList;
import be.goossens.oracle.Show.Food.ShowManageOwnFood;
import be.goossens.oracle.Show.Food.ShowSelectedFood;
import be.goossens.oracle.Show.Food.ShowUpdateOwnFood;

public class ActivityGroupMeal extends ActivityGroup {

	// keep this in a static variable to make it accessible
	// activities, let them manipulate the view
	public static ActivityGroupMeal group;

	// Need to keep track of the history so the back button works properly
	public ArrayList<View> history;

	// if newFoodID != 0 the showFoodList will add the id to the list, order the
	// objects, go to the ID and set it back to 0
	// This long != 0 when showCreateFood created a new foodItem
	public long newFoodID;

	// this boolean is used to check if we added a fooditem to the selectedFood
	// the addFoodToSelection page will set this boolean on true, the
	// showFoodList page will animate the button and set it back on false
	public boolean addedFoodItemToList;

	// if this long != 0 we have to delete the fooditem with that id from the
	// list ( becaus it is not in the datase anymore )
	// this id gets set by showupdateownfood and gets set back to 0 in
	// showFoodList
	public long deleteFoodIDFromList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.history = new ArrayList<View>();
		group = this;

		// make a root activity when the history size = 0
		if (history.size() == 0) {
			startMainActivity();
		}
	}

	private void startMainActivity() {
		history = new ArrayList<View>();
		// Start the root activity within the group and get its view
		// This activity is the activity that hold our objects for showFoodList
		View view = getLocalActivityManager().startActivity(
				DataParser.activityIDShowFoodList,
				new Intent(this, ShowLoadingFoodData.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();
		replaceView(view);
	}

	// this method is called from showfoodlist when we click on the back button
	// this method will kill the application
	public void killApplication() {
		// finish the tab activity so everything will close
		this.getParent().finish();
	}

	// The foodData will always be in history place 0
	public ShowLoadingFoodData getFoodData() {
		try {
			return (ShowLoadingFoodData) history.get(0).getContext();
		} catch (Exception e) {
			return null;
		}
	}

	// The showFoodList will always be in history place 1
	// It is not sure that this activity is running! becaus when we go back to
	// the activity we destroy it and create a new one
	// This means that history only will have 1 object in its list and that is
	// on position 0 so position 1 is empty
	public ShowFoodList getShowFoodList() {
		try {
			return (ShowFoodList) history.get(1).getContext();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
	}

	@Override
	public void setContentView(View view) {
		// every time we switch from view we hide the keyboard
		keyboardDissapear();

		replaceView(view);
	}

	// let the keyboard dissapear
	public void keyboardDissapear() {
		// get a inputManager
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// try to hide the keyboard
		// when it fail we will get a nullpointerexception
		// example: it will fail when we are running a asynctask
		// and the user press on a other tab
		try {
			inputManager.hideSoftInputFromInputMethod(this.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (NullPointerException e) {
		}
	}

	private void replaceView(View view) {
		// Adds the old one to history
		history.add(view);
		// changes this group view to the new view
		super.setContentView(view);
	}

	public void back() {
		// if the size > 2 then we are in a activity that is not the home
		// activity ( show food list )
		if (history.size() > 2) {
			// remove the view from the history list
			history.remove(history.size() - 1);

			// if history.size == 2 we are in our home activity ( show food list
			// ) and we have to recreate it
			// otherwise we cant handle the keyboard events.
			if (history.size() == 2) {
				// remove the view from the list
				history.remove(history.size() - 1);
				// add it again
				View view = getLocalActivityManager().startActivity(
						DataParser.activityIDShowFoodList,
						new Intent(this, ShowFoodList.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();
				replaceView(view);
			}

			// call the super.setContent view! so set the real view
			super.setContentView(history.get(history.size() - 1));

		}
	}

	// when we didnt overide the onkeydown method in the activity this onkeydown
	// will be triggered
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			// destroy current activity and go back to the last one in the
			// history
			back();
			// return true so other onkeydowns wont get called
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private View getView(int sizeMinusNumber) {
		return history.get(history.size() - sizeMinusNumber);
	}

	// showFoodList refresh listview
	public void showFoodListRefreshListView() {
		try {
			View v = history.get(0);
			ShowFoodList currentActivity = (ShowFoodList) v.getContext();
			// currentActivity.refreshListView();
		} catch (Exception e) {
		}
	}

	// show mange own food
	public void refreshShowManageOwnFood(int sizeMinusNumber) {
		try {
			View v = getView(sizeMinusNumber);
			ShowManageOwnFood currentActivity = (ShowManageOwnFood) v
					.getContext();
			currentActivity.onResume();
		} catch (Exception e) {
		}
	}

	// show selected food
	public void refreshShowSelectedFood(int sizeMinusNumber) {
		try {
			View v = getView(sizeMinusNumber);
			ShowSelectedFood currentActivity = (ShowSelectedFood) v
					.getContext();
			currentActivity.onResume();
		} catch (Exception e) {
		}
	}

	// show update own food
	public void refreshShowUpdateOwnFood(int sizeMinusNumber) {
		try {
			View v = getView(sizeMinusNumber);
			ShowUpdateOwnFood currentActivity = (ShowUpdateOwnFood) v
					.getContext();
			currentActivity.onResume();
		} catch (Exception e) {
		}
	}

	@Override
	public void finish() {

	}
}
