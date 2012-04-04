package be.goossens.oracle.Rest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

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
	private static String DB_NAME = "dbhelpdiabetes";
	private static final int DATABASE_VERSION = 1;
	private SQLiteDatabase mDb;
	private final Context mCtx;

	// ExerciseType
	private static final String DATABASE_EXERCISETYPE_TABLE = "ExerciseType";
	public static final String DATABASE_EXERCISETYPE_ID = "_id";
	public static final String DATABASE_EXERCISETYPE_NAME = "Name";
	public static final String DATABASE_EXERCISETYPE_DESCRIPTION = "Description";
	public static final String DATABASE_EXERCISETYPE_VISIBLE = "Visible";

	// ExerciseEvent
	private static final String DATABASE_EXERCISEEVENT_TABLE = "ExerciseEvent";
	public static final String DATABASE_EXERCISEEVENT_ID = "_id";
	public static final String DATABASE_EXERCISEEVENT_DESCRIPTION = "Description";
	public static final String DATABASE_EXERCISEEVENT_STARTTIME = "StartTime";
	public static final String DATABASE_EXERCISEEVENT_STOPTIME = "StopTime";
	public static final String DATABASE_EXERCISEEVENT_TIMESTAMP = "TimeStamp";
	public static final String DATABASE_EXERCISEEVENT_EXERCISETYPEID = "ExerciseTypeID";
	public static final String DATABASE_EXERCISEEVENT_USERID = "UserID";

	// meal type
	private static final String DATABASE_MEALTYPE_TABLE = "MealType";
	public static final String DATABASE_MEALTYPE_ID = "_id";
	public static final String DATABASE_MEALTYPE_MEALTYPENAME = "MealTypeName";
	public static final String DATABASE_MEALTYPE_STARTTIME = "StartTime";
	public static final String DATABASE_MEALTYPE_ENDTIME = "EndTime";
	public static final String DATABASE_MEALTYPE_PRESCRIPTIONRATIO = "PrescriptionRatio";
	public static final String DATABASE_MEALTYPE_USERID = "UserID";
	public static final String DATABASE_MEALTYPE_STARTDATE = "StartDate";
	public static final String DATABASE_MEALTYPE_ENDDATE = "EndDate";
	public static final String DATABASE_MEALTYPE_MEALCATEGORY_ID = "MealCategoryID";

	// food template
	private static final String DATABASE_FOODTEMPLATE_TABLE = "FoodTemplate";
	public static final String DATABASE_FOODTEMPLATE_ID = "_id";
	public static final String DATABASE_FOODTEMPLATE_MEALTYPEID = "MealTypeID";
	public static final String DATABASE_FOODTEMPLATE_USERID = "UserID";
	public static final String DATABASE_FOODTEMPLATE_VISIBLE = "Visible";
	public static final String DATABASE_FOODTEMPLATE_FOODTEMPLATENAME = "FoodTemplateName";

	// template_food
	private static final String DATABASE_TEMPLATEFOOD_TABLE = "template_food";
	public static final String DATABASE_TEMPLATEFOOD_ID = "_id";
	public static final String DATABASE_TEMPLATEFOOD_FOODTEMPLATEID = "FoodTemplateID";
	public static final String DATABASE_TEMPLATEFOOD_UNITID = "UnitID";
	public static final String DATABASE_TEMPLATEFOOD_AMOUNT = "amount";

	// settings
	private static final String DATABASE_SETTINGS_TABLE = "settings";
	public static final String DATABASE_SETTINGS_ID = "_id";
	public static final String DATABASE_SETTINGS_NAME = "Name";
	public static final String DATABASE_SETTINGS_VALUE = "Value";
	public static final String DATABASE_SETTINGS_USERID = "UserID";

	// SelectedFood
	private static final String DATABASE_SELECTEDFOOD_TABLE = "selectedfood";
	public static final String DATABASE_SELECTEDFOOD_ID = "_id";
	public static final String DATABASE_SELECTEDFOOD_AMOUNT = "amount";
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

	// Exercise Type Functions
	// create
	public long createExerciseType(String name, String description) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_EXERCISETYPE_NAME, name);
		initialValues.put(DATABASE_EXERCISETYPE_DESCRIPTION, description);
		initialValues.put(DATABASE_EXERCISETYPE_VISIBLE, "0");
		return mDb.insert(DATABASE_EXERCISETYPE_TABLE, null, initialValues);
	}

	// get all exercise types
	public Cursor fetchAllExerciseTypes() {
		return mDb.query(DATABASE_EXERCISETYPE_TABLE, new String[] {
				DATABASE_EXERCISETYPE_ID, DATABASE_EXERCISETYPE_NAME,
				DATABASE_EXERCISETYPE_DESCRIPTION,
				DATABASE_EXERCISETYPE_VISIBLE }, null, null, null, null, null);
	}

	// get exercise type by ID
	public Cursor fetchExerciseTypeByID(long id) {
		return mDb.query(DATABASE_EXERCISETYPE_TABLE, new String[] {
				DATABASE_EXERCISETYPE_ID, DATABASE_EXERCISETYPE_NAME,
				DATABASE_EXERCISETYPE_DESCRIPTION,
				DATABASE_EXERCISETYPE_VISIBLE }, DATABASE_EXERCISETYPE_ID + "="
				+ id, null, null, null, null);
	}

	// update exercise type by ID
	public boolean updateExerciseTypeByID(long id, String name,
			String description) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_EXERCISETYPE_NAME, name);
		initialValues.put(DATABASE_EXERCISETYPE_DESCRIPTION, description);
		return mDb.update(DATABASE_EXERCISETYPE_TABLE, initialValues,
				DATABASE_EXERCISETYPE_ID + "=" + id, null) > 0;
	}

	// delete exercise type by ID
	public boolean deleteExerciseTypeByID(long id) {
		return mDb.delete(DATABASE_EXERCISETYPE_TABLE, DATABASE_EXERCISETYPE_ID
				+ "=" + id, null) > 0;
	}

	// Exercise Event Funtions
	// create
	public long createExerciseEvent(String description, String startTime,
			String stopTime, Date timeStamp, long exerciseTypeID) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_EXERCISEEVENT_DESCRIPTION, description);
		initialValues.put(DATABASE_EXERCISEEVENT_STARTTIME,
				startTime.toString());
		initialValues.put(DATABASE_EXERCISEEVENT_STOPTIME, stopTime.toString());
		initialValues.put(DATABASE_EXERCISEEVENT_TIMESTAMP,
				timeStamp.toString());
		initialValues
				.put(DATABASE_EXERCISEEVENT_EXERCISETYPEID, exerciseTypeID);
		initialValues.put(DATABASE_EXERCISEEVENT_USERID, "0");
		return mDb.insert(DATABASE_EXERCISEEVENT_TABLE, null, initialValues);
	}

	// get all exercise events
	public Cursor fetchAllExerciseEvents() {
		return mDb.query(DATABASE_EXERCISEEVENT_TABLE, new String[] {
				DATABASE_EXERCISEEVENT_ID, DATABASE_EXERCISEEVENT_DESCRIPTION,
				DATABASE_EXERCISEEVENT_STARTTIME,
				DATABASE_EXERCISEEVENT_STOPTIME,
				DATABASE_EXERCISEEVENT_TIMESTAMP,
				DATABASE_EXERCISEEVENT_EXERCISETYPEID,
				DATABASE_EXERCISEEVENT_USERID }, null, null, null, null, null);
	}

	// get exercise event by ID
	public Cursor fetchExerciseEventByID(long id) {
		return mDb.query(DATABASE_EXERCISEEVENT_TABLE, new String[] {
				DATABASE_EXERCISEEVENT_ID, DATABASE_EXERCISEEVENT_DESCRIPTION,
				DATABASE_EXERCISEEVENT_STARTTIME,
				DATABASE_EXERCISEEVENT_STOPTIME,
				DATABASE_EXERCISEEVENT_TIMESTAMP,
				DATABASE_EXERCISEEVENT_EXERCISETYPEID,
				DATABASE_EXERCISEEVENT_USERID }, DATABASE_EXERCISEEVENT_ID
				+ "=" + id, null, null, null, null);
	}

	// update exercise event by ID
	public boolean updateExerciseEventByID(long id, String description,
			String startTime, String stopTime, long exerciseTypeID) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_EXERCISEEVENT_DESCRIPTION, description);
		initialValues.put(DATABASE_EXERCISEEVENT_STARTTIME,
				startTime.toString());
		initialValues.put(DATABASE_EXERCISEEVENT_STOPTIME, stopTime.toString());
		initialValues
				.put(DATABASE_EXERCISEEVENT_EXERCISETYPEID, exerciseTypeID);
		return mDb.update(DATABASE_EXERCISEEVENT_TABLE, initialValues,
				DATABASE_EXERCISEEVENT_ID + "=" + id, null) > 0;
	}

	// delete exercise event by ID
	public boolean deleteExerciseEventByID(long id) {
		return mDb.delete(DATABASE_EXERCISEEVENT_TABLE, DATABASE_EXERCISEEVENT_ID
				+ "=" + id, null) > 0;
	}

	// Meal Type Functions
	// create
	public long createMealType(String mealtypename) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_MEALTYPE_MEALTYPENAME, mealtypename);
		initialValues.put(DATABASE_MEALTYPE_STARTTIME, "00:00");
		initialValues.put(DATABASE_MEALTYPE_ENDTIME, "00:00");
		initialValues.put(DATABASE_MEALTYPE_PRESCRIPTIONRATIO, "0");
		initialValues.put(DATABASE_MEALTYPE_USERID, "0");
		initialValues.put(DATABASE_MEALTYPE_STARTDATE, "0");
		initialValues.put(DATABASE_MEALTYPE_ENDDATE, "0");
		initialValues.put(DATABASE_MEALTYPE_MEALCATEGORY_ID, "0");
		return mDb.insert(DATABASE_MEALTYPE_TABLE, null, initialValues);
	}

	// get all mealtypes
	public Cursor fetchAllMealTypes() {
		return mDb.query(DATABASE_MEALTYPE_TABLE, new String[] {
				DATABASE_MEALTYPE_ID, DATABASE_MEALTYPE_MEALTYPENAME,
				DATABASE_MEALTYPE_STARTTIME, DATABASE_MEALTYPE_ENDTIME,
				DATABASE_MEALTYPE_PRESCRIPTIONRATIO,
				DATABASE_MEALTYPE_MEALCATEGORY_ID, DATABASE_MEALTYPE_USERID,
				DATABASE_MEALTYPE_STARTDATE, DATABASE_MEALTYPE_ENDDATE }, null,
				null, null, null, null);
	}

	// FoodTemplate Functions
	// create
	public long createFoodTemplate(long mealtypeid, String name) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_FOODTEMPLATE_MEALTYPEID, mealtypeid);
		initialValues.put(DATABASE_FOODTEMPLATE_USERID, 0);
		initialValues.put(DATABASE_FOODTEMPLATE_VISIBLE, 1);
		initialValues.put(DATABASE_FOODTEMPLATE_FOODTEMPLATENAME, name);
		return mDb.insert(DATABASE_FOODTEMPLATE_TABLE, null, initialValues);
	}

	// get all foodTemplates
	public Cursor fetchAllFoodTemplates() {
		return mDb.query(DATABASE_FOODTEMPLATE_TABLE, new String[] {
				DATABASE_FOODTEMPLATE_ID, DATABASE_FOODTEMPLATE_MEALTYPEID,
				DATABASE_FOODTEMPLATE_USERID, DATABASE_FOODTEMPLATE_VISIBLE,
				DATABASE_FOODTEMPLATE_FOODTEMPLATENAME }, null, null,
				DATABASE_FOODTEMPLATE_FOODTEMPLATENAME, null, null);
	}

	// Delete foodTemplate
	public boolean deleteFoodTemplate(long foodTemplate) {
		return mDb.delete(DATABASE_FOODTEMPLATE_TABLE, DATABASE_FOODTEMPLATE_ID
				+ "=" + foodTemplate, null) > 0;
	}

	// TEMPLATE_FOOD Functions
	// create
	public long createTemplateFood(long foodtemplateid, long unitid,
			float amount) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_TEMPLATEFOOD_FOODTEMPLATEID, foodtemplateid);
		initialValues.put(DATABASE_TEMPLATEFOOD_UNITID, unitid);
		initialValues.put(DATABASE_TEMPLATEFOOD_AMOUNT, amount);
		return mDb.insert(DATABASE_TEMPLATEFOOD_TABLE, null, initialValues);
	}

	// get all template foods
	public Cursor fetchAllTemplateFoods() {
		return mDb.query(DATABASE_TEMPLATEFOOD_TABLE, new String[] {
				DATABASE_TEMPLATEFOOD_ID, DATABASE_TEMPLATEFOOD_FOODTEMPLATEID,
				DATABASE_TEMPLATEFOOD_UNITID, DATABASE_TEMPLATEFOOD_AMOUNT },
				null, null, null, null, null);
	}

	// get template foods by foodTemplateID
	public Cursor fetchTemplateFoodsByFoodTemplateID(long foodTemplateID) {
		return mDb.query(DATABASE_TEMPLATEFOOD_TABLE, new String[] {
				DATABASE_TEMPLATEFOOD_ID, DATABASE_TEMPLATEFOOD_FOODTEMPLATEID,
				DATABASE_TEMPLATEFOOD_UNITID, DATABASE_TEMPLATEFOOD_AMOUNT },
				DATABASE_TEMPLATEFOOD_FOODTEMPLATEID + "=" + foodTemplateID,
				null, null, null, null);
	}

	// get template foods by unitID
	public Cursor fetchTemplateFoodsByUnitID(long unitID) {
		return mDb.query(DATABASE_TEMPLATEFOOD_TABLE, new String[] {
				DATABASE_TEMPLATEFOOD_ID, DATABASE_TEMPLATEFOOD_FOODTEMPLATEID,
				DATABASE_TEMPLATEFOOD_UNITID, DATABASE_TEMPLATEFOOD_AMOUNT },
				DATABASE_TEMPLATEFOOD_UNITID + "=" + unitID, null, null, null,
				null);
	}

	// Delete template food
	public boolean deleteTemplateFoodByFoodTemplateID(long foodTemplateID) {
		return mDb.delete(DATABASE_TEMPLATEFOOD_TABLE,
				DATABASE_TEMPLATEFOOD_FOODTEMPLATEID + "=" + foodTemplateID,
				null) > 0;
	}

	// SETTINGS Functions
	// create settings
	public long createSettings(String name, String value) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_SETTINGS_NAME, name);
		initialValues.put(DATABASE_SETTINGS_VALUE, value);
		initialValues.put(DATABASE_SETTINGS_USERID, "0");
		return mDb.insert(DATABASE_SETTINGS_TABLE, null, initialValues);
	}

	// get all settings
	public Cursor fetchAllSettings() {
		return mDb.query(DATABASE_SETTINGS_TABLE, new String[] {
				DATABASE_SETTINGS_ID, DATABASE_SETTINGS_NAME,
				DATABASE_SETTINGS_USERID, DATABASE_SETTINGS_VALUE }, null,
				null, null, null, null);
	}

	// get setting by name
	public Cursor fetchSettingByName(String name) {
		return mDb.query(DATABASE_SETTINGS_TABLE, new String[] {
				DATABASE_SETTINGS_ID, DATABASE_SETTINGS_NAME,
				DATABASE_SETTINGS_USERID, DATABASE_SETTINGS_VALUE },
				DATABASE_SETTINGS_NAME + "='" + name + "'", null, null, null,
				null);
	}

	// update settings by name
	public boolean updateSettingsByName(String name, String value) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_SETTINGS_VALUE, value);
		return mDb.update(DATABASE_SETTINGS_TABLE, initialValues,
				DATABASE_SETTINGS_NAME + "='" + name + "'", null) > 0;
	}

	// SELECTED FOOD Functions
	public long createSelectedFood(float amound, long unitId) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_SELECTEDFOOD_AMOUNT, amound);
		initialValues.put(DATABASE_SELECTEDFOOD_UNITID, unitId);
		return mDb.insert(DATABASE_SELECTEDFOOD_TABLE, null, initialValues);
	}

	// get all selected food
	public Cursor fetchAllSelectedFood() {
		return mDb.query(DATABASE_SELECTEDFOOD_TABLE, new String[] {
				DATABASE_SELECTEDFOOD_ID, DATABASE_SELECTEDFOOD_AMOUNT,
				DATABASE_SELECTEDFOOD_UNITID }, null, null, null, null, null);
	}

	// get selected food by foodUnitId
	public Cursor fetchSelectedFoodByFoodUnitId(Long foodUnitId)
			throws SQLException {
		Cursor mCursor = mDb.query(DATABASE_SELECTEDFOOD_TABLE, new String[] {
				DATABASE_SELECTEDFOOD_ID, DATABASE_SELECTEDFOOD_AMOUNT,
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
				DATABASE_SELECTEDFOOD_UNITID }, DATABASE_SELECTEDFOOD_ID + "="
				+ id, null, null, null, null);
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
	public boolean updateSelectedFood(long id, float amound, long unitId) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_SELECTEDFOOD_AMOUNT, amound);
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
			float carbs, float prot, float fat, float kcal) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_FOODUNIT_FOODID, foodId);
		initialValues.put(DATABASE_FOODUNIT_NAME, name);
		initialValues.put(DATABASE_FOODUNIT_STANDARDAMOUNT, standardAmount);
		initialValues.put(DATABASE_FOODUNIT_CARBS, carbs);
		initialValues.put(DATABASE_FOODUNIT_PROTEIN, prot);
		initialValues.put(DATABASE_FOODUNIT_FAT, fat);
		initialValues.put(DATABASE_FOODUNIT_KCAL, kcal);
		initialValues.put(DATABASE_FOODUNIT_VISIBLE, 1);
		return mDb.insert(DATABASE_FOODUNIT_TABLE, null, initialValues);
	}

	// get all foodUnits
	public Cursor fetchAllFoodUnit() throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_FOODUNIT_TABLE, new String[] {
				DATABASE_FOODUNIT_ID, DATABASE_FOODUNIT_FOODID,
				DATABASE_FOODUNIT_NAME, DATABASE_FOODUNIT_DESCRIPTION,
				DATABASE_FOODUNIT_STANDARDAMOUNT, DATABASE_FOODUNIT_CARBS,
				DATABASE_FOODUNIT_PROTEIN, DATABASE_FOODUNIT_FAT,
				DATABASE_FOODUNIT_KCAL, DATABASE_FOODUNIT_VISIBLE }, null,
				null, null, null, null, null);
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
				DATABASE_FOODUNIT_STANDARDAMOUNT, DATABASE_FOODUNIT_CARBS,
				DATABASE_FOODUNIT_PROTEIN, DATABASE_FOODUNIT_FAT,
				DATABASE_FOODUNIT_KCAL, DATABASE_FOODUNIT_VISIBLE },
				DATABASE_FOODUNIT_ID + "=" + id, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// get all foodUnit by foodId
	public Cursor fetchFoodUnitByFoodId(Long foodId) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_FOODUNIT_TABLE, new String[] {
				DATABASE_FOODUNIT_ID, DATABASE_FOODUNIT_FOODID,
				DATABASE_FOODUNIT_NAME, DATABASE_FOODUNIT_DESCRIPTION,
				DATABASE_FOODUNIT_STANDARDAMOUNT, DATABASE_FOODUNIT_CARBS,
				DATABASE_FOODUNIT_PROTEIN, DATABASE_FOODUNIT_FAT,
				DATABASE_FOODUNIT_KCAL, DATABASE_FOODUNIT_VISIBLE },
				DATABASE_FOODUNIT_FOODID + "=" + foodId, null, null, null,
				null, null);

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
			float standardAmount, float carbs, float prot, float fat, float kcal) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_FOODUNIT_NAME, name);
		initialValues.put(DATABASE_FOODUNIT_DESCRIPTION, "");
		initialValues.put(DATABASE_FOODUNIT_STANDARDAMOUNT, standardAmount);
		initialValues.put(DATABASE_FOODUNIT_CARBS, carbs);
		initialValues.put(DATABASE_FOODUNIT_PROTEIN, prot);
		initialValues.put(DATABASE_FOODUNIT_FAT, fat);
		initialValues.put(DATABASE_FOODUNIT_KCAL, kcal);
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
