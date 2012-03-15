package be.goossens.oracle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ShowPreferencesMealTimes extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_preferences_meal_times);
	}

	// If we click on go to page Show insuline ratios
	public void onClickGoToInsulineRatios(View view) {
		// call finish becaus this activity is finished ( so when we press the
		// back button on our phone we go to the list of food and not to the
		// other preference )
		finish();
		Intent i = new Intent(this, ShowPreferencesInsulineRatio.class);
		startActivity(i);
	}

	//on click back button
	public void onClickBack(View view) {
		finish();
	}

	//on click update
	public void onClickUpdate(View view) {
	}
}
