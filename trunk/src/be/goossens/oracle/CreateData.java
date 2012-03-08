package be.goossens.oracle;

import android.app.Activity;
import android.os.Bundle;

public class CreateData extends Activity {
	private DbAdapter dbHelper;
	private Thread thread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_data);
		dbHelper = new DbAdapter(this);
		thread = new Thread(new Runnable() {
			public void run() {
				createDataInDatabase();
			}
		});
		thread.start();
	}
	
	private void createDataInDatabase() {
		dbHelper.open();
		
		Long kroketId = dbHelper.createFood("aardappelkroketten");
		Long broodId = dbHelper.createFood("brood bruin");
		Long bramenId = dbHelper.createFood("bramen");
		
		dbHelper.createFoodUnit(kroketId, "gram", "", 100, 220, 3, 27, 12, 1);
		dbHelper.createFoodUnit(kroketId, "stuk (33 g)", "", 1, 73, 1, 9, 4, 1);
		dbHelper.createFoodUnit(bramenId, "gram", "", 100, 37, 1, 6, 1, 1);
		dbHelper.createFoodUnit(broodId, "gram", "", 100, 360, 14, 67, 4, 1);
		dbHelper.createFoodUnit(broodId, "snede (50 g)", "", 1, 120, 5, 23, 1, 1);
		dbHelper.createFoodUnit(broodId, "snede (groot/75 g)", "", 1, 179, 7, 34, 1, 1);
		
		setResult(RESULT_OK);
		finish();
	}
}
