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
	private static final int DATABASE_VERSION = 4;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mCtx;

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
	private static final String DATABASE_FOOD_CREATE = "create table "
			+ DATABASE_FOOD_TABLE + " (" + DATABASE_FOOD_ID
			+ " integer primary key autoincrement, " + DATABASE_FOOD_NAME
			+ " text not null, " + DATABASE_FOOD_ISFAVORITE
			+ " integer not null, " + DATABASE_FOOD_USERID
			+ " integer not null, " + DATABASE_FOOD_CATEGORYID
			+ " integer not null, " + DATABASE_FOOD_VISIBLE
			+ " integer not null, " + DATABASE_FOOD_FOODLANGUAGEID
			+ " integer not null);";

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

	/*
	 * // User private static final String DATABASE_USERS_TABLE = "users";
	 * public static final String DATABASE_USERS_ID = "_id"; public static final
	 * String DATABASE_USERS_USERNAME = "usernam"; public static final String
	 * DATABASE_USERS_PASSWORD = "password"; public static final String
	 * DATABASE_USERS_EMAIL = "email"; private static final String
	 * DATABASE_USERS_CREATE = "create table " + DATABASE_USERS_TABLE + " (" +
	 * DATABASE_USERS_ID + " integer primary key autoincrement, " +
	 * DATABASE_USERS_USERNAME + " text not null, " + DATABASE_USERS_PASSWORD +
	 * " text not null, " + DATABASE_USERS_EMAIL + " text not null);";
	 * 
	 * // MealCategory private static final String DATABASE_MEALCATEGORY_TABLE =
	 * "mealcategory"; public static final String DATABASE_MEALCATEGORY_ID =
	 * "_id"; public static final String DATABASE_MEALCATEGORY_NAME = "name";
	 * private static final String DATABASE_MEALCATEGORY_CREATE =
	 * "create table " + DATABASE_MEALCATEGORY_TABLE + " (" +
	 * DATABASE_MEALCATEGORY_ID + " integer primary key autoincrement, " +
	 * DATABASE_MEALCATEGORY_NAME + " text not null);";
	 * 
	 * // foocategory private static final String DATABASE_FOODCATEGORY_TABLE =
	 * "foodcategory"; public static final String DATABASE_FOODCATEGORY_ID =
	 * "_id"; public static final String DATABASE_FOODCATEGORY_NAME = "name";
	 * private static final String DATABASE_FOODCATEGORY_CREATE =
	 * "create table " + DATABASE_FOODCATEGORY_TABLE + " (" +
	 * DATABASE_FOODCATEGORY_ID + " integer primary key autoincrement, " +
	 * DATABASE_FOODCATEGORY_NAME + " text not null);";
	 */

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context ctx) {
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		}

		// this will create the database tables
		@Override
		public void onCreate(SQLiteDatabase db) {
			// db.execSQL(DATABASE_USERS_CREATE);
			// db.execSQL(DATABASE_MEALCATEGORY_CREATE);
			// db.execSQL(DATABASE_FOODCATEGORY_CREATE);
			db.execSQL(DATABASE_FOOD_CREATE);
			db.execSQL(DATABASE_FOODUNIT_CREATE);
			db.execSQL(DATABASE_SELECTEDFOOD_CREATE);
		}

		// this will destroy the data in the database and recreate the tables
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// db.execSQL("DROP TABLE IF EXISTS " + DATABASE_USERS_TABLE);
			// db.execSQL("DROP TABLE IF EXISTS " +
			// DATABASE_MEALCATEGORY_TABLE);
			// db.execSQL("DROP TABLE IF EXISTS " +
			// DATABASE_FOODCATEGORY_TABLE);
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

	/*
	 * // USERS Functions // add user to database public long createUser(String
	 * userName, String password, String email) { ContentValues initialValues =
	 * new ContentValues(); initialValues.put(DATABASE_USERS_USERNAME,
	 * userName); initialValues.put(DATABASE_USERS_PASSWORD, password);
	 * initialValues.put(DATABASE_USERS_EMAIL, email); return
	 * mDb.insert(DATABASE_USERS_TABLE, null, initialValues); }
	 * 
	 * // get all users from database public Cursor fetchAllUsers() { return
	 * mDb.query(DATABASE_USERS_TABLE, new String[] { DATABASE_USERS_ID,
	 * DATABASE_USERS_USERNAME, DATABASE_USERS_PASSWORD, DATABASE_USERS_EMAIL },
	 * null, null, null, null, null); }
	 * 
	 * // get a user by id public Cursor fetchUser(Long id) throws SQLException
	 * { Cursor mCursor = mDb.query(true, DATABASE_USERS_TABLE, new String[] {
	 * DATABASE_USERS_ID, DATABASE_USERS_USERNAME, DATABASE_USERS_PASSWORD,
	 * DATABASE_USERS_EMAIL }, DATABASE_USERS_ID + "=" + id, null, null, null,
	 * null, null); if (mCursor != null) { mCursor.moveToFirst(); } return
	 * mCursor; }
	 * 
	 * // MEALCATEGORY Functions // add mealcategory public long
	 * createMealCategory(String name) { ContentValues initialValues = new
	 * ContentValues(); initialValues.put(DATABASE_MEALCATEGORY_NAME, name);
	 * return mDb.insert(DATABASE_MEALCATEGORY_TABLE, null, initialValues); }
	 * 
	 * // get all mealcategorys public Cursor fetchAllMealCategorys() { return
	 * mDb.query(DATABASE_MEALCATEGORY_TABLE, new String[] {
	 * DATABASE_MEALCATEGORY_ID, DATABASE_MEALCATEGORY_NAME }, null, null, null,
	 * null, null); }
	 * 
	 * // get a mealCategory by id public Cursor fetchMealCategory(Long id)
	 * throws SQLException { Cursor mCursor = mDb.query(true,
	 * DATABASE_MEALCATEGORY_TABLE, new String[] { DATABASE_MEALCATEGORY_ID,
	 * DATABASE_MEALCATEGORY_NAME }, DATABASE_MEALCATEGORY_ID + "=" + id, null,
	 * null, null, null, null); if (mCursor != null) { mCursor.moveToFirst(); }
	 * return mCursor; }
	 * 
	 * // FOOCATEGORY Functions // add foodcategory public long
	 * createFoodCategory(String name) { ContentValues initialValues = new
	 * ContentValues(); initialValues.put(DATABASE_FOODCATEGORY_NAME, name);
	 * return mDb.insert(DATABASE_FOODCATEGORY_TABLE, null, initialValues); }
	 * 
	 * // get all foodCategorys public Cursor fetchAllFoodCategorys() { return
	 * mDb.query(DATABASE_FOODCATEGORY_TABLE, new String[] {
	 * DATABASE_FOODCATEGORY_ID, DATABASE_FOODCATEGORY_NAME }, null, null, null,
	 * null, null); }
	 * 
	 * // get a foodCategory by id public Cursor fetchFoodCategory(Long id)
	 * throws SQLException { Cursor mCursor = mDb.query(true,
	 * DATABASE_FOODCATEGORY_TABLE, new String[] { DATABASE_FOODCATEGORY_ID,
	 * DATABASE_FOODCATEGORY_NAME }, DATABASE_FOODCATEGORY_ID + "=" + id, null,
	 * null, null, null, null); if (mCursor != null) { mCursor.moveToFirst(); }
	 * return mCursor; }
	 */
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
		return mDb.insert(DATABASE_FOOD_TABLE, null, initialValues);
	}

	// get all food
	public Cursor fetchAllFood() {
		return mDb.query(DATABASE_FOOD_TABLE, new String[] { DATABASE_FOOD_ID,
				DATABASE_FOOD_NAME, DATABASE_FOOD_ISFAVORITE,
				DATABASE_FOOD_VISIBLE }, null, null, null, null, null);
	}

	// get a food by id
	public Cursor fetchFood(Long id) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_FOOD_TABLE, new String[] {
				DATABASE_FOOD_ID, DATABASE_FOOD_NAME, DATABASE_FOOD_ISFAVORITE,
				DATABASE_FOOD_VISIBLE }, DATABASE_FOOD_ID + "=" + id, null,
				null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// get all food filter on food name
	public Cursor fetchFoodWithFilterByName(String filter) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_FOOD_TABLE, new String[] {
				DATABASE_FOOD_ID, DATABASE_FOOD_NAME, DATABASE_FOOD_ISFAVORITE,
				DATABASE_FOOD_VISIBLE }, DATABASE_FOOD_NAME + " LIKE '%"
				+ filter + "%'", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// get all own create food
	public Cursor fetchAllOwnCreatedFood() {
		return mDb.query(DATABASE_FOOD_TABLE, new String[] { DATABASE_FOOD_ID,
				DATABASE_FOOD_NAME, DATABASE_FOOD_ISFAVORITE,
				DATABASE_FOOD_VISIBLE }, null, null, null, null, null);
	}

	// Delete food
	public boolean deleteFood(long foodId) {
		return mDb.delete(DATABASE_FOOD_TABLE, DATABASE_FOOD_ID + "=" + foodId,
				null) > 0;
	}

	// FoodUnit Fuctions
	// add FoodUnit
	public long createFoodUnit(Long foodId, String name, String description,
			float standardAmount, float kcal, float protein, float carbs,
			float fat, Integer visible) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_FOODUNIT_FOODID, foodId);
		initialValues.put(DATABASE_FOODUNIT_NAME, name);
		initialValues.put(DATABASE_FOODUNIT_DESCRIPTION, description);
		initialValues.put(DATABASE_FOODUNIT_STANDARDAMOUNT, standardAmount);
		initialValues.put(DATABASE_FOODUNIT_KCAL, kcal);
		initialValues.put(DATABASE_FOODUNIT_PROTEIN, protein);
		initialValues.put(DATABASE_FOODUNIT_CARBS, carbs);
		initialValues.put(DATABASE_FOODUNIT_FAT, fat);
		initialValues.put(DATABASE_FOODUNIT_VISIBLE, visible);
		return mDb.insert(DATABASE_FOODUNIT_TABLE, null, initialValues);
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
}
