package be.goossens.oracle.ActivityGroup;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Show.Food.ShowFoodList;
import be.goossens.oracle.Show.Food.ShowManageOwnFood;
import be.goossens.oracle.Show.Food.ShowSelectedFood;
import be.goossens.oracle.Show.Food.ShowUpdateOwnFood;

public class ActivityGroupMeal extends ActivityGroup {

	// keep this in a static variable to make it accessible
	// activities, let them manipulate the view
	public static ActivityGroupMeal group;

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
					DataParser.activityIDMeal,
					new Intent(this, ShowFoodList.class)
							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
					.getDecorView();
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
			// if we come back to the showFoodList we have to clear the
			// edittextbox and update the button
			// if (history.size() == 0) {
			// clear the editTextBox from the showFoodListPage
			showFoodListClearEditTextbox();
			// update the button from showFoodList
			showFoodListUpdateButton();
			// }
		}
		return true;
	}

	private View getView(int sizeMinusNumber) {
		return history.get(history.size() - sizeMinusNumber);
	}

	// showFoodList get countSelectedFood
	public int showFoodListGetCountSelectedFood() {
		try {
			View v = history.get(0);
			ShowFoodList currentActivity = (ShowFoodList) v.getContext();
			return currentActivity.getCountSelectedFood();
		} catch (Exception e) {
			return -1;
		}
	}

	// showFoodList set countSelectedFood
	public void showFoodListsetCountSelectedFood(int countSelectedFood) {
		try {
			View v = history.get(0);
			ShowFoodList currentActivity = (ShowFoodList) v.getContext();
			currentActivity.setCountSelectedFood(countSelectedFood);
		} catch (Exception e) {
		}
	}
	
	// showFoodList clear editTextBox
	public void showFoodListClearEditTextbox() {
		try {
			View v = history.get(0);
			ShowFoodList currentActivity = (ShowFoodList) v.getContext();
			currentActivity.clearEditTextSearch();
		} catch (Exception e) {
		}
	}

	// showFoodList clear editTextBox
	public void showFoodListUpdateButton() {
		try {
			View v = history.get(0);
			ShowFoodList currentActivity = (ShowFoodList) v.getContext();
			currentActivity.updateButton();
		} catch (Exception e) {
		}
	}

	// showFoodList add food item to list
	public void showFoodListAddFoodItem(long foodId) {
		try {
			View v = history.get(0);
			ShowFoodList currentActivity = (ShowFoodList) v.getContext();
			currentActivity.addFoodItemToList(foodId);
		} catch (Exception e) {
		}
	}

	// showFoodList delete food item from list
	public void showFoodListDeleteFoodItem(long foodId) {
		try {
			View v = history.get(0);
			ShowFoodList currentActivity = (ShowFoodList) v.getContext();
			currentActivity.deleteFoodItemFromList(foodId);
		} catch (Exception e) {
		}
	}

	// showFoodList refresh listview
	public void showFoodListRefreshListView() {
		try {
			View v = history.get(0);
			ShowFoodList currentActivity = (ShowFoodList) v.getContext();
			currentActivity.refreshListView();
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
		Toast.makeText(this, "lol no finish for u ;)", Toast.LENGTH_LONG)
				.show();
	}
}
