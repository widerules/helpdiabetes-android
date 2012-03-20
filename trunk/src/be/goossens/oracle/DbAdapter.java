package be.goossens.oracle;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * This class provides the add, delete, create of all the database components
 */

public class DbAdapter extends SQLiteOpenHelper {
	// The android default system path to my application database
	private static String DB_PATH = "/data/data/be.goossens.oracle/databases/";
	private static String DB_NAME = "dbhelpdiabetesnl";
	private static final int DATABASE_VERSION = 1;
	private SQLiteDatabase mDb;
	private final Context mCtx;

	// settings
	private static final String DATABASE_SETTINGS_TABLE = "settings";
	public static final String DATABASE_SETTINGS_ID = "_id";
	public static final String DATABASE_SETTINGS_INSULINE_RATIO_BREAKFAST = "insulinebreakfast";
	public static final String DATABASE_SETTINGS_INSULINE_RATIO_LUNCH = "insulinelunch";
	public static final String DATABASE_SETTINGS_INSULINE_RATIO_SNACK = "insulinesnack";
	public static final String DATABASE_SETTINGS_INSULINE_RATIO_DINNER = "insulinedinner";
	public static final String DATABASE_SETTINGS_MEAL_TIME_BREAKFAST = "mealtimebreakfast";
	public static final String DATABASE_SETTINGS_MEAL_TIME_LUNCH = "mealtimelunch";
	public static final String DATABASE_SETTINGS_MEAL_TIME_SNACK = "mealtimesnack";
	public static final String DATABASE_SETTINGS_MEAL_TIME_DINNER = "mealtimedinner";

	// SelectedFood
	private static final String DATABASE_SELECTEDFOOD_TABLE = "selectedfood";
	public static final String DATABASE_SELECTEDFOOD_ID = "_id";
	public static final String DATABASE_SELECTEDFOOD_AMOUNT = "amount";
	public static final String DATABASE_SELECTEDFOOD_FOODNAME = "foodname";
	public static final String DATABASE_SELECTEDFOOD_UNITNAME = "unitname";
	public static final String DATABASE_SELECTEDFOOD_KCAL = "kcal";
	public static final String DATABASE_SELECTEDFOOD_PROTEIN = "protein";
	public static final String DATABASE_SELECTEDFOOD_CARBS = "carbs";
	public static final String DATABASE_SELECTEDFOOD_FAT = "fat";
	public static final String DATABASE_SELECTEDFOOD_STANDARDAMOUNT = "standardamount";
	public static final String DATABASE_SELECTEDFOOD_FOODID = "foodid";
	public static final String DATABASE_SELECTEDFOOD_UNITID = "unitid";

	// Food
	public static final String DATABASE_FOOD_TABLE = "food";
	public static final String DATABASE_FOOD_ID = "_id";
	public static final String DATABASE_FOOD_NAME = "name";
	public static final String DATABASE_FOOD_ISFAVORITE = "isfavorite";
	public static final String DATABASE_FOOD_USERID = "userid";
	public static final String DATABASE_FOOD_CATEGORYID = "foodcategoryid";
	public static final String DATABASE_FOOD_VISIBLE = "visible";
	public static final String DATABASE_FOOD_FOODLANGUAGEID = "foodlanguageid";
	public static final String DATABASE_FOOD_PLATFORM = "platform";

	// FOODUNIT
	public static final String DATABASE_FOODUNIT_TABLE = "foodunit";
	public static final String DATABASE_FOODUNIT_ID = "_id";
	public static final String DATABASE_FOODUNIT_NAME = "name";
	public static final String DATABASE_FOODUNIT_DESCRIPTION = "description";
	public static final String DATABASE_FOODUNIT_STANDARDAMOUNT = "standardamount";
	public static final String DATABASE_FOODUNIT_KCAL = "kcal";
	public static final String DATABASE_FOODUNIT_PROTEIN = "protein";
	public static final String DATABASE_FOODUNIT_CARBS = "carbs";
	public static final String DATABASE_FOODUNIT_FAT = "fat";
	public static final String DATABASE_FOODUNIT_VISIBLE = "visible";
	public static final String DATABASE_FOODUNIT_FOODID = "foodid";

	public DbAdapter(Context ctx) {
		super(ctx, DB_NAME, null, DATABASE_VERSION);
		this.mCtx = ctx;
	}

	/*
	 * Create a empty database on the system and rewrites it with the
	 * databasefile
	 */
	public void createDatabase() {
		if (!checkDatabase()) {
			/*
			 * This method will create a empty database
			 */
			this.close();
			this.getReadableDatabase();
			copyDatabase();
		}
	}

	// Copy your database from the local asset folder to the just created empty
	// database
	private void copyDatabase() {
		try {
			// Open the local db as the input stream
			InputStream myInput = mCtx.getAssets().open(DB_NAME);
			// Path to the just created empty db
			String outFileName = DB_PATH + DB_NAME;
			// Open the empty db as the output stream
			OutputStream myOutput = new FileOutputStream(outFileName);
			// transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}
			// close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();

		} catch (IOException e) {
		}
	}

	@Override
	public synchronized void close() {
		if (mDb != null) {
			mDb.close();
		}
		super.close();
	}

	// Check if the database already exists
	public boolean checkDatabase() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// The database does not exists
		}
		return checkDB != null ? true : false;
		// return false;
	}

	public void open() throws SQLException {
			String myPath = DB_PATH + DB_NAME;
			mDb = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
	}

	// SETTINGS Functions 
	// create settings
	public long createSettings() {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_SETTINGS_INSULINE_RATIO_BREAKFAST, "0");
		initialValues.put(DATABASE_SETTINGS_INSULINE_RATIO_LUNCH, "0");
		initialValues.put(DATABASE_SETTINGS_INSULINE_RATIO_SNACK, "0");
		initialValues.put(DATABASE_SETTINGS_INSULINE_RATIO_DINNER, "0");
		initialValues.put(DATABASE_SETTINGS_MEAL_TIME_BREAKFAST, "06:00");
		initialValues.put(DATABASE_SETTINGS_MEAL_TIME_LUNCH, "12:00");
		initialValues.put(DATABASE_SETTINGS_MEAL_TIME_SNACK, "16:00");
		initialValues.put(DATABASE_SETTINGS_MEAL_TIME_DINNER, "18:00");
		return mDb.insert(DATABASE_SETTINGS_TABLE, null, initialValues);
	}

	// get all settings
	public Cursor fetchAllSettings() {
		return mDb.query(DATABASE_SETTINGS_TABLE, new String[] {
				DATABASE_SETTINGS_ID,
				DATABASE_SETTINGS_INSULINE_RATIO_BREAKFAST,
				DATABASE_SETTINGS_INSULINE_RATIO_LUNCH,
				DATABASE_SETTINGS_INSULINE_RATIO_SNACK,
				DATABASE_SETTINGS_INSULINE_RATIO_DINNER,
				DATABASE_SETTINGS_MEAL_TIME_BREAKFAST,
				DATABASE_SETTINGS_MEAL_TIME_LUNCH,
				DATABASE_SETTINGS_MEAL_TIME_SNACK,
				DATABASE_SETTINGS_MEAL_TIME_DINNER }, null, null, null, null,
				null);
	}

	// Update settings insuline ratio
	public boolean updateSettingsInsulineRatio(long id, float breakfastRatio,
			float lunchRatio, float snackRatio, float dinnerRatio) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_SETTINGS_INSULINE_RATIO_BREAKFAST,
				breakfastRatio);
		initialValues.put(DATABASE_SETTINGS_INSULINE_RATIO_LUNCH, lunchRatio);
		initialValues.put(DATABASE_SETTINGS_INSULINE_RATIO_SNACK, snackRatio);
		initialValues.put(DATABASE_SETTINGS_INSULINE_RATIO_DINNER, dinnerRatio);
		return mDb.update(DATABASE_SETTINGS_TABLE, initialValues,
				DATABASE_SETTINGS_ID + "=" + id, null) > 0;
	}

	// Update settings meal times
	public boolean updateSettingsMealTimes(long id, String breakfastTime,
			String lunchTime, String snackTime, String dinnerTime) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_SETTINGS_MEAL_TIME_BREAKFAST, breakfastTime);
		initialValues.put(DATABASE_SETTINGS_MEAL_TIME_LUNCH, lunchTime);
		initialValues.put(DATABASE_SETTINGS_MEAL_TIME_SNACK, snackTime);
		initialValues.put(DATABASE_SETTINGS_MEAL_TIME_DINNER, dinnerTime);
		return mDb.update(DATABASE_SETTINGS_TABLE, initialValues,
				DATABASE_SETTINGS_ID + "=" + id, null) > 0;
	}

	// SELECTED FOOD Functions
	public long createSelectedFood(String foodname, String unitname,
			float kcal, float amound, float standardAmound, float protein,
			float carbs, float fat, float foodId, float unitId) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_SELECTEDFOOD_FOODNAME, foodname);
		initialValues.put(DATABASE_SELECTEDFOOD_UNITNAME, unitname);
		initialValues.put(DATABASE_SELECTEDFOOD_AMOUNT, amound);
		initialValues.put(DATABASE_SELECTEDFOOD_KCAL, kcal);
		initialValues.put(DATABASE_SELECTEDFOOD_STANDARDAMOUNT, standardAmound);
		initialValues.put(DATABASE_SELECTEDFOOD_PROTEIN, protein);
		initialValues.put(DATABASE_SELECTEDFOOD_CARBS, carbs);
		initialValues.put(DATABASE_SELECTEDFOOD_FAT, fat);
		initialValues.put(DATABASE_SELECTEDFOOD_FOODID, foodId);
		initialValues.put(DATABASE_SELECTEDFOOD_UNITID, unitId);
		return mDb.insert(DATABASE_SELECTEDFOOD_TABLE, null, initialValues);
	}

	// get all selected food
	public Cursor fetchAllSelectedFood() {
		return mDb.query(DATABASE_SELECTEDFOOD_TABLE, new String[] {
				DATABASE_SELECTEDFOOD_ID, DATABASE_SELECTEDFOOD_AMOUNT,
				DATABASE_SELECTEDFOOD_FOODNAME, DATABASE_SELECTEDFOOD_KCAL,
				DATABASE_SELECTEDFOOD_UNITNAME,
				DATABASE_SELECTEDFOOD_STANDARDAMOUNT,
				DATABASE_SELECTEDFOOD_PROTEIN, DATABASE_SELECTEDFOOD_CARBS,
				DATABASE_SELECTEDFOOD_FAT, DATABASE_SELECTEDFOOD_FOODID,
				DATABASE_SELECTEDFOOD_UNITID }, null, null, null, null, null);
	}

	// get selected food by foodUnitId
	public Cursor fetchSelectedFoodByFoodUnitId(Long foodUnitId)
			throws SQLException {
		Cursor mCursor = mDb.query(DATABASE_SELECTEDFOOD_TABLE, new String[] {
				DATABASE_SELECTEDFOOD_ID, DATABASE_SELECTEDFOOD_AMOUNT,
				DATABASE_SELECTEDFOOD_FOODNAME, DATABASE_SELECTEDFOOD_KCAL,
				DATABASE_SELECTEDFOOD_UNITNAME,
				DATABASE_SELECTEDFOOD_STANDARDAMOUNT,
				DATABASE_SELECTEDFOOD_PROTEIN, DATABASE_SELECTEDFOOD_CARBS,
				DATABASE_SELECTEDFOOD_FAT, DATABASE_SELECTEDFOOD_FOODID,
				DATABASE_SELECTEDFOOD_UNITID }, DATABASE_SELECTEDFOOD_UNITID
				+ "=" + foodUnitId, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// get a single selected food by id
	public Cursor fetchSelectedFood(Long id) throws SQLException {
		Cursor mCursor = mDb.query(DATABASE_SELECTEDFOOD_TABLE, new String[] {
				DATABASE_SELECTEDFOOD_ID, DATABASE_SELECTEDFOOD_AMOUNT,
				DATABASE_SELECTEDFOOD_FOODNAME, DATABASE_SELECTEDFOOD_KCAL,
				DATABASE_SELECTEDFOOD_UNITNAME,
				DATABASE_SELECTEDFOOD_STANDARDAMOUNT,
				DATABASE_SELECTEDFOOD_PROTEIN, DATABASE_SELECTEDFOOD_CARBS,
				DATABASE_SELECTEDFOOD_FAT, DATABASE_SELECTEDFOOD_FOODID,
				DATABASE_SELECTEDFOOD_UNITID }, DATABASE_SELECTEDFOOD_ID + "="
				+ id, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// get a single selected food by Foodid
	public Cursor fetchSelectedFoodByFoodId(Long foodId) throws SQLException {
		Cursor mCursor = mDb.query(DATABASE_SELECTEDFOOD_TABLE, new String[] {
				DATABASE_SELECTEDFOOD_ID, DATABASE_SELECTEDFOOD_AMOUNT,
				DATABASE_SELECTEDFOOD_FOODNAME, DATABASE_SELECTEDFOOD_KCAL,
				DATABASE_SELECTEDFOOD_UNITNAME,
				DATABASE_SELECTEDFOOD_STANDARDAMOUNT,
				DATABASE_SELECTEDFOOD_PROTEIN, DATABASE_SELECTEDFOOD_CARBS,
				DATABASE_SELECTEDFOOD_FAT, DATABASE_SELECTEDFOOD_FOODID,
				DATABASE_SELECTEDFOOD_UNITID }, DATABASE_SELECTEDFOOD_FOODID
				+ "=" + foodId, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// Delete selected food
	public boolean deleteSelectedFood(long selectedFoodId) {
		return mDb.delete(DATABASE_SELECTEDFOOD_TABLE, DATABASE_SELECTEDFOOD_ID
				+ "=" + selectedFoodId, null) > 0;
	}

	// Update selected food
	public boolean updateSelectedFood(long id, String foodname,
			String unitname, float kcal, float amound, float standardAmound,
			float protein, float carbs, float fat, float foodId, float unitId) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_SELECTEDFOOD_FOODNAME, foodname);
		initialValues.put(DATABASE_SELECTEDFOOD_UNITNAME, unitname);
		initialValues.put(DATABASE_SELECTEDFOOD_AMOUNT, amound);
		initialValues.put(DATABASE_SELECTEDFOOD_KCAL, kcal);
		initialValues.put(DATABASE_SELECTEDFOOD_STANDARDAMOUNT, standardAmound);
		initialValues.put(DATABASE_SELECTEDFOOD_PROTEIN, protein);
		initialValues.put(DATABASE_SELECTEDFOOD_CARBS, carbs);
		initialValues.put(DATABASE_SELECTEDFOOD_FAT, fat);
		initialValues.put(DATABASE_SELECTEDFOOD_FOODID, foodId);
		initialValues.put(DATABASE_SELECTEDFOOD_UNITID, unitId);
		return mDb.update(DATABASE_SELECTEDFOOD_TABLE, initialValues,
				DATABASE_SELECTEDFOOD_ID + "=" + id, null) > 0;
	}

	// Food Functions
	// add food
	public long createFood(String name) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_FOOD_NAME, name);
		initialValues.put(DATABASE_FOOD_ISFAVORITE, "0");
		initialValues.put(DATABASE_FOOD_VISIBLE, "1");
		initialValues.put(DATABASE_FOOD_USERID, "1");
		initialValues.put(DATABASE_FOOD_CATEGORYID, "1");
		initialValues.put(DATABASE_FOOD_FOODLANGUAGEID, "1");
		initialValues.put(DATABASE_FOOD_PLATFORM, "android");
		return mDb.insert(DATABASE_FOOD_TABLE, null, initialValues);
	}

	// get all food
	public Cursor fetchAllFood() {
		return mDb.query(DATABASE_FOOD_TABLE, new String[] { DATABASE_FOOD_ID,
				DATABASE_FOOD_NAME, DATABASE_FOOD_ISFAVORITE,
				DATABASE_FOOD_VISIBLE, DATABASE_FOOD_PLATFORM }, null, null,
				DATABASE_FOOD_NAME, null, null);
	}

	// get a food by id
	public Cursor fetchFood(Long id) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_FOOD_TABLE, new String[] {
				DATABASE_FOOD_ID, DATABASE_FOOD_NAME, DATABASE_FOOD_ISFAVORITE,
				DATABASE_FOOD_VISIBLE, DATABASE_FOOD_PLATFORM },
				DATABASE_FOOD_ID + "=" + id, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// get all food filter on food name
	public Cursor fetchFoodWithFilterByName(String filter) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_FOOD_TABLE, new String[] {
				DATABASE_FOOD_ID, DATABASE_FOOD_NAME, DATABASE_FOOD_ISFAVORITE,
				DATABASE_FOOD_VISIBLE, DATABASE_FOOD_PLATFORM },
				DATABASE_FOOD_NAME + " LIKE '%" + filter + "%'", null,
				DATABASE_FOOD_NAME, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// get all own create food
	public Cursor fetchAllOwnCreatedFood() {
		return mDb.query(DATABASE_FOOD_TABLE, new String[] { DATABASE_FOOD_ID,
				DATABASE_FOOD_NAME, DATABASE_FOOD_ISFAVORITE,
				DATABASE_FOOD_VISIBLE, DATABASE_FOOD_PLATFORM },
				DATABASE_FOOD_PLATFORM + " LIKE 'android' ", null, null, null,
				null);
	}

	// Delete food
	public boolean deleteFood(long foodId) {
		return mDb.delete(DATABASE_FOOD_TABLE, DATABASE_FOOD_ID + "=" + foodId,
				null) > 0;
	}

	// Update food name
	public boolean updateFoodName(long id, String foodname) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_FOOD_NAME, foodname);
		return mDb.update(DATABASE_FOOD_TABLE, initialValues, DATABASE_FOOD_ID
				+ "=" + id, null) > 0;
	}

	// FoodUnit Fuctions
	// add FoodUnit
	public long createFoodUnit(Long foodId, String name, float standardAmount,
			float kcal, float protein, float carbs, float fat) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_FOODUNIT_FOODID, foodId);
		initialValues.put(DATABASE_FOODUNIT_NAME, name);
		// initialValues.put(DATABASE_FOODUNIT_DESCRIPTION, " ");
		initialValues.put(DATABASE_FOODUNIT_STANDARDAMOUNT, standardAmount);
		initialValues.put(DATABASE_FOODUNIT_KCAL, kcal);
		initialValues.put(DATABASE_FOODUNIT_PROTEIN, protein);
		initialValues.put(DATABASE_FOODUNIT_CARBS, carbs);
		initialValues.put(DATABASE_FOODUNIT_FAT, fat);
		initialValues.put(DATABASE_FOODUNIT_VISIBLE, 1);
		return mDb.insert(DATABASE_FOODUNIT_TABLE, null, initialValues);
	}

	// get all foodUnits
	public Cursor fetchAllFoodUnit() throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_FOODUNIT_TABLE, new String[] {
				DATABASE_FOODUNIT_ID, DATABASE_FOODUNIT_FOODID,
				DATABASE_FOODUNIT_NAME, DATABASE_FOODUNIT_DESCRIPTION,
				DATABASE_FOODUNIT_STANDARDAMOUNT, DATABASE_FOODUNIT_KCAL,
				DATABASE_FOODUNIT_PROTEIN, DATABASE_FOODUNIT_CARBS,
				DATABASE_FOODUNIT_FAT, DATABASE_FOODUNIT_VISIBLE }, null, null,
				null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// get all foodUnit by id
	public Cursor fetchFoodUnit(Long id) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_FOODUNIT_TABLE, new String[] {
				DATABASE_FOODUNIT_ID, DATABASE_FOODUNIT_FOODID,
				DATABASE_FOODUNIT_NAME, DATABASE_FOODUNIT_DESCRIPTION,
				DATABASE_FOODUNIT_STANDARDAMOUNT, DATABASE_FOODUNIT_KCAL,
				DATABASE_FOODUNIT_PROTEIN, DATABASE_FOODUNIT_CARBS,
				DATABASE_FOODUNIT_FAT, DATABASE_FOODUNIT_VISIBLE },
				DATABASE_FOODUNIT_ID + "=" + id, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// get all foodUnit by foodId
	public Cursor fetchFoodUnitByFoodId(Long foodId) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_FOODUNIT_TABLE, new String[] {
				DATABASE_FOODUNIT_ID, DATABASE_FOODUNIT_NAME,
				DATABASE_FOODUNIT_STANDARDAMOUNT }, DATABASE_FOODUNIT_FOODID
				+ "=" + foodId, null, null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// Delete foodUnit
	public boolean deleteFoodUnit(long foodUnitId) {
		return mDb.delete(DATABASE_FOODUNIT_TABLE, DATABASE_FOODUNIT_ID + "="
				+ foodUnitId, null) > 0;
	}

	// Update foodUnit
	public boolean updateFoodUnit(Long unitId, String name,
			float standardAmount, float kcal, float protein, float carbs,
			float fat) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_FOODUNIT_NAME, name);
		initialValues.put(DATABASE_FOODUNIT_DESCRIPTION, "");
		initialValues.put(DATABASE_FOODUNIT_STANDARDAMOUNT, standardAmount);
		initialValues.put(DATABASE_FOODUNIT_KCAL, kcal);
		initialValues.put(DATABASE_FOODUNIT_PROTEIN, protein);
		initialValues.put(DATABASE_FOODUNIT_CARBS, carbs);
		initialValues.put(DATABASE_FOODUNIT_FAT, fat);
		return mDb.update(DATABASE_FOODUNIT_TABLE, initialValues,
				DATABASE_FOODUNIT_ID + "=" + unitId, null) > 0;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
}
