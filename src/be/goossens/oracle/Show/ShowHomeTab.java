package be.goossens.oracle.Show;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import be.goossens.oracle.R;
import be.goossens.oracle.ActivityGroup.ActivityGroupExercise;
import be.goossens.oracle.ActivityGroup.ActivityGroupMeal;
import be.goossens.oracle.ActivityGroup.ActivityGroupTracking;
import be.goossens.oracle.Rest.DataParser;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Show.Tracking.ShowTracking;

public class ShowHomeTab extends TabActivity {
	private DbAdapter dbHelper;

	private TabSpec spec1, spec2, spec3, spec4, spec5;
	private TabHost tabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_home_tab);
		dbHelper = new DbAdapter(this);
		dbHelper.createDatabase();

		tabHost = getTabHost();

		spec1 = tabHost.newTabSpec("Tab tracking");
		spec1.setIndicator("", getResources()
				.getDrawable(R.drawable.notracking));
		Intent in1 = new Intent(this, ActivityGroupTracking.class);
		spec1.setContent(in1);

		spec2 = tabHost.newTabSpec("Tab meal");
		spec2.setIndicator("", getResources().getDrawable(R.drawable.nomeal));
		Intent in2 = new Intent(this, ActivityGroupMeal.class);
		spec2.setContent(in2);

		spec3 = tabHost.newTabSpec("Tab exercise");
		spec3.setIndicator("", getResources()
				.getDrawable(R.drawable.noexercise));
		Intent in3 = new Intent(this, ActivityGroupExercise.class);
		spec3.setContent(in3);

		spec4 = tabHost.newTabSpec("Tab glucose");
		spec4.setIndicator("", getResources().getDrawable(R.drawable.noglucose));
		Intent in4 = new Intent(this, ShowTracking.class);
		spec4.setContent(in1);

		spec5 = tabHost.newTabSpec("Tab medicine");
		spec5.setIndicator("",
				getResources().getDrawable(R.drawable.nomedicines));
		Intent in5 = new Intent(this, ShowTracking.class);
		spec5.setContent(in1);

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
		tabHost.addTab(spec4);
		tabHost.addTab(spec5);
	}

	public void goToTab(int number){
		tabHost.setCurrentTab(number);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return true;
	}
}
