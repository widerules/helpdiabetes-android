package be.goossens.oracle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * This class provides the add, delete, create of all the database components
 */

public class DbAdapter {
	// create database
	private static final String DATABASE_NAME = "db";
	private static final int DATABASE_VERSION = 37;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mCtx;

	//settings
	private static final String DATABASE_SETTINGS_TABLE = "settings";
	public static final String DATBASE_SETTINGS_ID = "_id";
	public static final String DATBASE_SETTINGS_USERID = "userid";
	public static final String DATBASE_SETTINGS_INSULINE_RATIO_BREAKFAST = "insulinebreakfast";
	public static final String DATBASE_SETTINGS_INSULINE_RATIO_LUNCH = "insulinelunch";
	public static final String DATBASE_SETTINGS_INSULINE_RATIO_SNACK = "insulinesnack";
	public static final String DATBASE_SETTINGS_INSULINE_RATIO_DINNER = "insulinedinner";
	public static final String DATBASE_SETTINGS_MEAL_TIME_BREAKFAST = "mealtimebreakfast";
	public static final String DATBASE_SETTINGS_MEAL_TIME_LUNCH = "mealtimelunch";
	public static final String DATBASE_SETTINGS_MEAL_TIME_SNACK = "mealtimesnack";
	public static final String DATBASE_SETTINGS_MEAL_TIME_DINNER = "mealtimedinner";
	
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

	private static final String DATABASE_SELECTEDFOOD_CREATE = "create table "
			+ DATABASE_SELECTEDFOOD_TABLE + " (" + DATABASE_SELECTEDFOOD_ID
			+ " integer primary key autoincrement, "
			+ DATABASE_SELECTEDFOOD_AMOUNT + " float not null, "
			+ DATABASE_SELECTEDFOOD_FOODNAME + " text not null, "
			+ DATABASE_SELECTEDFOOD_UNITNAME + " text not null, "
			+ DATABASE_SELECTEDFOOD_KCAL + " float nog null, "
			+ DATABASE_SELECTEDFOOD_PROTEIN + " float not null, "
			+ DATABASE_SELECTEDFOOD_CARBS + " float not null, "
			+ DATABASE_SELECTEDFOOD_FAT + " float not null, "
			+ DATABASE_SELECTEDFOOD_STANDARDAMOUNT + " float not null, "
			+ DATABASE_SELECTEDFOOD_FOODID + " float not null, "
			+ DATABASE_SELECTEDFOOD_UNITID + " float not null);";

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
	private static final String DATABASE_FOOD_CREATE = "create table "
			+ DATABASE_FOOD_TABLE + " (" + DATABASE_FOOD_ID
			+ " integer primary key autoincrement, " + DATABASE_FOOD_NAME
			+ " text not null, " + DATABASE_FOOD_ISFAVORITE
			+ " integer not null, " + DATABASE_FOOD_USERID
			+ " integer not null, " + DATABASE_FOOD_CATEGORYID
			+ " integer not null, " + DATABASE_FOOD_VISIBLE
			+ " integer not null, " + DATABASE_FOOD_FOODLANGUAGEID
			+ " integer not null, " + DATABASE_FOOD_PLATFORM
			+ " text not null);";

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
	private static final String DATABASE_FOODUNIT_CREATE = "create table "
			+ DATABASE_FOODUNIT_TABLE + " (" + DATABASE_FOODUNIT_ID
			+ " integer primary key autoincrement, " + DATABASE_FOODUNIT_FOODID
			+ " integer not null, " + DATABASE_FOODUNIT_NAME
			+ " text not null, " + DATABASE_FOODUNIT_DESCRIPTION
			+ " text null, " + DATABASE_FOODUNIT_STANDARDAMOUNT
			+ " float not null, " + DATABASE_FOODUNIT_KCAL
			+ " float not null, " + DATABASE_FOODUNIT_PROTEIN
			+ " float not null, " + DATABASE_FOODUNIT_CARBS
			+ " float not null, " + DATABASE_FOODUNIT_FAT + " float not null, "
			+ DATABASE_FOODUNIT_VISIBLE + " integer not null);";

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context ctx) {
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		}

		// this will create the database tables
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_FOOD_CREATE);
			db.execSQL(DATABASE_FOODUNIT_CREATE);
			db.execSQL(DATABASE_SELECTEDFOOD_CREATE);
		}

		// this will destroy the data in the database and recreate the tables
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_FOOD_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_FOODUNIT_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_SELECTEDFOOD_TABLE);
			onCreate(db);
		}
	}

	public DbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public DbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public void createData(String query) {
		mDb.execSQL(query);
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
		//initialValues.put(DATABASE_FOODUNIT_DESCRIPTION, " ");
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
}
