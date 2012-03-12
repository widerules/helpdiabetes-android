package be.goossens.oracle;

import android.app.Activity;
import android.os.Bundle;

public class ShowCreateData extends Activity {
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

		int id = 1;

		for (int i = 1; i < 5; i++) {
			// test create food
			dbHelper.createData("insert into " + DbAdapter.DATABASE_FOOD_TABLE
					+ "(" + DbAdapter.DATABASE_FOOD_ID + ","
					+ DbAdapter.DATABASE_FOOD_NAME + ","
					+ DbAdapter.DATABASE_FOOD_ISFAVORITE + ","
					+ DbAdapter.DATABASE_FOOD_USERID + ","
					+ DbAdapter.DATABASE_FOOD_CATEGORYID + ","
					+ DbAdapter.DATABASE_FOOD_VISIBLE + ","
					+ DbAdapter.DATABASE_FOOD_FOODLANGUAGEID + ") "
					+ "values (" + i + ",'lasagne" + i + "',0,0,0,0,0)");

			// test create unit
			dbHelper.createData("insert into "
					+ DbAdapter.DATABASE_FOODUNIT_TABLE + "("
					+ DbAdapter.DATABASE_FOODUNIT_ID + ","
					+ DbAdapter.DATABASE_FOODUNIT_NAME + ","
					+ DbAdapter.DATABASE_FOODUNIT_DESCRIPTION + ","
					+ DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT + ","
					+ DbAdapter.DATABASE_FOODUNIT_KCAL + ","
					+ DbAdapter.DATABASE_FOODUNIT_PROTEIN + ","
					+ DbAdapter.DATABASE_FOODUNIT_CARBS + ","
					+ DbAdapter.DATABASE_FOODUNIT_FAT + ","
					+ DbAdapter.DATABASE_FOODUNIT_VISIBLE + ","
					+ DbAdapter.DATABASE_FOODUNIT_FOODID + ") " + "values ("
					+ id + ",'gram','test 200 gram',200,100,10,20,30,40," + i + ")");
			id++;
			dbHelper.createData("insert into "
					+ DbAdapter.DATABASE_FOODUNIT_TABLE + "("
					+ DbAdapter.DATABASE_FOODUNIT_ID + ","
					+ DbAdapter.DATABASE_FOODUNIT_NAME + ","
					+ DbAdapter.DATABASE_FOODUNIT_DESCRIPTION + ","
					+ DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT + ","
					+ DbAdapter.DATABASE_FOODUNIT_KCAL + ","
					+ DbAdapter.DATABASE_FOODUNIT_PROTEIN + ","
					+ DbAdapter.DATABASE_FOODUNIT_CARBS + ","
					+ DbAdapter.DATABASE_FOODUNIT_FAT + ","
					+ DbAdapter.DATABASE_FOODUNIT_VISIBLE + ","
					+ DbAdapter.DATABASE_FOODUNIT_FOODID + ") " + "values ("
					+ id + ",'gram','test 400 gram',400,200,20,40,60,80," + i + ")");
			id++;

			if (i == 2) {
				dbHelper.createData("insert into "
						+ DbAdapter.DATABASE_FOODUNIT_TABLE + "("
						+ DbAdapter.DATABASE_FOODUNIT_ID + ","
						+ DbAdapter.DATABASE_FOODUNIT_NAME + ","
						+ DbAdapter.DATABASE_FOODUNIT_DESCRIPTION + ","
						+ DbAdapter.DATABASE_FOODUNIT_STANDARDAMOUNT + ","
						+ DbAdapter.DATABASE_FOODUNIT_KCAL + ","
						+ DbAdapter.DATABASE_FOODUNIT_PROTEIN + ","
						+ DbAdapter.DATABASE_FOODUNIT_CARBS + ","
						+ DbAdapter.DATABASE_FOODUNIT_FAT + ","
						+ DbAdapter.DATABASE_FOODUNIT_VISIBLE + ","
						+ DbAdapter.DATABASE_FOODUNIT_FOODID + ") "
						+ "values (" + id + ",'gram','test 1 gram',1,2,2,4,6,8,"
						+ i + ")");
				id++;
			}

		}
		setResult(RESULT_OK);
		finish();
	}
}
