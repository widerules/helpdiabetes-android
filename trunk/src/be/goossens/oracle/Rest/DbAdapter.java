package be.goossens.oracle.Rest;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import be.goossens.oracle.R;

/*
 * This class provides the add, delete, create of all the database components
 */

public class DbAdapter extends SQLiteOpenHelper {
	// The android default system path to my application database
	private static String DB_PATH = "/data/data/be.goossens.oracle/databases/";
	private static String DB_NAME = "dbhelpdiabetes";
	private static final int DATABASE_VERSION = 3;
	private SQLiteDatabase mDb;
	private final Context mCtx;

	// BloodGlucoseEvent
	private static final String DATABASE_BLOODGLUCOSEEVENT_TABLE = "BloodGlucoseEvent";
	public static final String DATABASE_BLOODGLUCOSEEVENT_ID = "_id";
	public static final String DATABASE_BLOODGLUCOSEEVENT_AMOUNT = "Amount";
	public static final String DATABASE_BLOODGLUCOSEEVENT_EVENTDATETIME = "EventDateTime";
	public static final String DATABASE_BLOODGLUCOSEEVENT_BGUNITID = "BGUnitID";
	public static final String DATABASE_BLOODGLUCOSEEVENT_USERID = "UserID";

	// BloodGlucoseUnit
	private static final String DATABASE_BLOODGLUCOSEUNIT_TABLE = "BloodGlucoseUnit";
	public static final String DATABASE_BLOODGLUCOSEUNIT_ID = "_id";
	public static final String DATABASE_BLOODGLUCOSEUNIT_UNITDESCRIPTION = "UnitDescription";
	public static final String DATABASE_BLOODGLUCOSEUNIT_UNIT = "Unit";
	public static final String DATABASE_BLOODGLUCOSEUNIT_VISIBLE = "Visible";

	// MealFood
	private static final String DATABASE_MEALFOOD_TABLE = "MealFood";
	public static final String DATABASE_MEALFOOD_ID = "_id";
	public static final String DATABASE_MEALFOOD_MEALEVENTID = "MealEventID";
	public static final String DATABASE_MEALFOOD_FOODID = "FoodID";
	public static final String DATABASE_MEALFOOD_AMOUNT = "Amount";

	// MealEvent
	private static final String DATABASE_MEALEVENT_TABLE = "MealEvent";
	public static final String DATABASE_MEALEVENT_ID = "_id";
	public static final String DATABASE_MEALEVENT_INSULINERATIO = "InsulineRatio";
	public static final String DATABASE_MEALEVENT_CORRECTIONFACTOR = "CorrectionFactor";
	public static final String DATABASE_MEALEVENT_CALCULATEDINSULINEAMOUNT = "CalculatedInsulineAmount";
	public static final String DATABASE_MEALEVENT_EVENTDATETIME = "EventDateTime";
	public static final String DATABASE_MEALEVENT_USERID = "UserID";

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
	public static final String DATABASE_EXERCISEEVENT_EVENTDATETIME = "EventDateTime";
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

	// Food language
	private static final String DATABASE_FOODLANGUAGE_TABLE = "FoodLanguage";
	public static final String DATABASE_FOODLANGUAGE_ID = "_id";
	public static final String DATABASE_FOODLANGUAGE_LANGUAGE = "Language";
	public static final String DATABASE_FOODLANGUAGE_NAME = "Name";
	public static final String DATABASE_FOODLANGUAGE_VALUE = "Value";

	public DbAdapter(Context ctx) {
		super(ctx, DB_NAME, null, DATABASE_VERSION);
		this.mCtx = ctx;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public boolean createDataBase() throws IOException {
		if (!checkDatabase()) {
			// close the db
			this.close();
			// create a empty database
			File newDBFile = new File(DB_PATH);
			newDBFile.mkdirs();
			// newDBFile.createNewFile();
			// copy the zip database to the phone
			copyFromZipFile();
			return true;
		}
		return false;
	} 

	public void open() { 
		String myPath = DB_PATH + DB_NAME;
		mDb = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}  
 
	private boolean checkDatabase() {
		SQLiteDatabase checkDB = null;
		try {
			// The database exists
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
			checkDB.close();
			return true; 
		} catch (SQLiteException e) {
			// The database does not exists
			return false;
		}
	}      

	private void copyFromZipFile() throws IOException {
		// open the zip file as inputstream
		InputStream is = mCtx.getResources().openRawResource(
				R.raw.dbhelpdiabetes);

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(DB_PATH + DB_NAME);

		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
		try {
			while ((zis.getNextEntry()) != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int count;
				while ((count = zis.read(buffer)) != -1) {
					baos.write(buffer, 0, count);
				}
				baos.writeTo(myOutput);
			}
		} finally {
			zis.close();
			myOutput.flush();
			myOutput.close();
			is.close();
		}

	}

	@Override
	public synchronized void close() {
		if (mDb != null) {
			mDb.close();
		}
		super.close();
	}

	// get all timestamps
	public Cursor fetchAllTimestamps() {
		return mDb.rawQuery("SELECT DISTINCT date(eventdatetime) as '"
				+ new DataParser().timestamp + "' from (select "
				+ DATABASE_EXERCISEEVENT_EVENTDATETIME + " from "
				+ DATABASE_EXERCISEEVENT_TABLE + " UNION select "
				+ DATABASE_MEALEVENT_EVENTDATETIME + " from "
				+ DATABASE_MEALEVENT_TABLE + " UNION select "
				+ DATABASE_BLOODGLUCOSEEVENT_EVENTDATETIME + " from "
				+ DATABASE_BLOODGLUCOSEEVENT_TABLE + ") order by 1 desc", null);
	}

	// BloodGlucose Events Functions
	// create
	public long createBloodGlucoseEvent(float amount, String timestamp,
			long bloodglucoseUnitID) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_BLOODGLUCOSEEVENT_AMOUNT, amount);
		initialValues.put(DATABASE_BLOODGLUCOSEEVENT_EVENTDATETIME, timestamp);
		initialValues.put(DATABASE_BLOODGLUCOSEEVENT_BGUNITID,
				bloodglucoseUnitID);
		initialValues.put(DATABASE_BLOODGLUCOSEEVENT_USERID, "0");
		return mDb
				.insert(DATABASE_BLOODGLUCOSEEVENT_TABLE, null, initialValues);
	}

	// get all blood glucose events
	public Cursor fetchAllBloodGlucoseEvents() {
		return mDb.query(DATABASE_BLOODGLUCOSEEVENT_TABLE, new String[] {
				DATABASE_BLOODGLUCOSEEVENT_ID,
				DATABASE_BLOODGLUCOSEEVENT_AMOUNT,
				DATABASE_BLOODGLUCOSEEVENT_EVENTDATETIME,
				DATABASE_BLOODGLUCOSEEVENT_BGUNITID,
				DATABASE_BLOODGLUCOSEEVENT_USERID }, null, null, null, null,
				null);
	}

	// get blood glucose events by timestamp
	public Cursor fetchBloodGlucoseEventByDate(String date) {
		return mDb.query(DATABASE_BLOODGLUCOSEEVENT_TABLE, new String[] {
				DATABASE_BLOODGLUCOSEEVENT_ID,
				DATABASE_BLOODGLUCOSEEVENT_AMOUNT,
				DATABASE_BLOODGLUCOSEEVENT_EVENTDATETIME,
				DATABASE_BLOODGLUCOSEEVENT_BGUNITID,
				DATABASE_BLOODGLUCOSEEVENT_USERID }, "date("
				+ DATABASE_BLOODGLUCOSEEVENT_EVENTDATETIME + ") " + " = "
				+ "date('" + date + "')", null, null, null, null);
	}

	// BloodGlucose Unit Functions
	// get all blood glucose units
	public Cursor fetchAllBloodGlucoseUnits() {
		return mDb.query(DATABASE_BLOODGLUCOSEUNIT_TABLE, new String[] {
				DATABASE_BLOODGLUCOSEUNIT_ID,
				DATABASE_BLOODGLUCOSEUNIT_UNITDESCRIPTION,
				DATABASE_BLOODGLUCOSEUNIT_UNIT,
				DATABASE_BLOODGLUCOSEUNIT_VISIBLE }, null, null, null, null,
				null);
	}

	// get blood glucose units by id
	public Cursor fetchBloodGlucoseUnitsByID(long id) {
		return mDb
				.query(DATABASE_BLOODGLUCOSEUNIT_TABLE, new String[] {
						DATABASE_BLOODGLUCOSEUNIT_ID,
						DATABASE_BLOODGLUCOSEUNIT_UNITDESCRIPTION,
						DATABASE_BLOODGLUCOSEUNIT_UNIT,
						DATABASE_BLOODGLUCOSEUNIT_VISIBLE },
						DATABASE_BLOODGLUCOSEUNIT_ID + "=" + id, null, null,
						null, null);
	}

	// Food Language Functions
	// get all food languages
	public Cursor fetchAllFoodLanguages() {
		return mDb.query(DATABASE_FOODLANGUAGE_TABLE, new String[] {
				DATABASE_FOODLANGUAGE_ID, DATABASE_FOODLANGUAGE_LANGUAGE,
				DATABASE_FOODLANGUAGE_NAME, DATABASE_FOODLANGUAGE_VALUE },
				null, null, DATABASE_FOODLANGUAGE_LANGUAGE, null, null);
	}

	// Meal Food Functions
	// create
	public long createMealFood(long mealEventID, int foodID, float amount) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_MEALFOOD_MEALEVENTID, mealEventID);
		initialValues.put(DATABASE_MEALFOOD_FOODID, foodID);
		initialValues.put(DATABASE_MEALFOOD_AMOUNT, amount);
		return mDb.insert(DATABASE_MEALFOOD_TABLE, null, initialValues);
	}

	// Meal Event Functions
	// create
	public long createMealEvent(float insulineRatio, float correctionFactor,
			float calculatedInsulineAmount, String timestamp) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_MEALEVENT_INSULINERATIO, insulineRatio);
		initialValues
				.put(DATABASE_MEALEVENT_CORRECTIONFACTOR, correctionFactor);
		initialValues.put(DATABASE_MEALEVENT_CALCULATEDINSULINEAMOUNT,
				calculatedInsulineAmount);
		initialValues.put(DATABASE_MEALEVENT_EVENTDATETIME, timestamp);
		initialValues.put(DATABASE_MEALEVENT_USERID, 0);
		return mDb.insert(DATABASE_MEALEVENT_TABLE, null, initialValues);
	}

	// get all meal events
	public Cursor fetchAllMealEvents() {
		return mDb.query(DATABASE_MEALEVENT_TABLE, new String[] {
				DATABASE_MEALEVENT_ID, DATABASE_MEALEVENT_INSULINERATIO,
				DATABASE_MEALEVENT_CORRECTIONFACTOR,
				DATABASE_MEALEVENT_CALCULATEDINSULINEAMOUNT,
				DATABASE_MEALEVENT_EVENTDATETIME, DATABASE_MEALEVENT_USERID },
				null, null, null, null, null);
	}

	// get all meal events by timestamp
	public Cursor fetchMealEventsByDate(String date) {
		return mDb.query(DATABASE_MEALEVENT_TABLE, new String[] {
				DATABASE_MEALEVENT_ID, DATABASE_MEALEVENT_INSULINERATIO,
				DATABASE_MEALEVENT_CORRECTIONFACTOR,
				DATABASE_MEALEVENT_CALCULATEDINSULINEAMOUNT,
				DATABASE_MEALEVENT_EVENTDATETIME, DATABASE_MEALEVENT_USERID },
				"date(" + DATABASE_MEALEVENT_EVENTDATETIME + ") " + " = "
						+ "date('" + date + "')", null, null, null, null);
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
	public long createExerciseEvent(String description, int startTime,
			int stopTime, long exerciseTypeID, String timestamp) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DATABASE_EXERCISEEVENT_DESCRIPTION, description);
		initialValues.put(DATABASE_EXERCISEEVENT_STARTTIME, startTime);
		initialValues.put(DATABASE_EXERCISEEVENT_STOPTIME, stopTime);
		initialValues
				.put(DATABASE_EXERCISEEVENT_EXERCISETYPEID, exerciseTypeID);
		initialValues.put(DATABASE_EXERCISEEVENT_EVENTDATETIME, timestamp);
		initialValues.put(DATABASE_EXERCISEEVENT_USERID, "0");
		return mDb.insert(DATABASE_EXERCISEEVENT_TABLE, null, initialValues);
	}

	// get all exercise events
	public Cursor fetchAllExerciseEvents() {
		return mDb.query(DATABASE_EXERCISEEVENT_TABLE, new String[] {
				DATABASE_EXERCISEEVENT_ID, DATABASE_EXERCISEEVENT_DESCRIPTION,
				DATABASE_EXERCISEEVENT_STARTTIME,
				DATABASE_EXERCISEEVENT_STOPTIME,
				DATABASE_EXERCISEEVENT_EVENTDATETIME,
				DATABASE_EXERCISEEVENT_EXERCISETYPEID,
				DATABASE_EXERCISEEVENT_USERID }, null, null, null, null, null);
	}

	// get exercise event by ID
	public Cursor fetchExerciseEventByID(long id) {
		return mDb.query(DATABASE_EXERCISEEVENT_TABLE, new String[] {
				DATABASE_EXERCISEEVENT_ID, DATABASE_EXERCISEEVENT_DESCRIPTION,
				DATABASE_EXERCISEEVENT_STARTTIME,
				DATABASE_EXERCISEEVENT_STOPTIME,
				DATABASE_EXERCISEEVENT_EVENTDATETIME,
				DATABASE_EXERCISEEVENT_EXERCISETYPEID,
				DATABASE_EXERCISEEVENT_USERID }, DATABASE_EXERCISEEVENT_ID
				+ "=" + id, null, null, null, null);
	}

	// get exercise event by date

	public Cursor fetchExerciseEventByDate(String date) {
		return mDb.query(DATABASE_EXERCISEEVENT_TABLE, new String[] {
				DATABASE_EXERCISEEVENT_ID, DATABASE_EXERCISEEVENT_DESCRIPTION,
				DATABASE_EXERCISEEVENT_STARTTIME,
				DATABASE_EXERCISEEVENT_STOPTIME,
				DATABASE_EXERCISEEVENT_EVENTDATETIME,
				DATABASE_EXERCISEEVENT_EXERCISETYPEID,
				DATABASE_EXERCISEEVENT_USERID }, "date("
				+ DATABASE_EXERCISEEVENT_EVENTDATETIME + ")" + " = " + "date('"
				+ date + "')", null, null, null, null);
	}

	public String test(String date) {
		return "SELECT * FROM " + DATABASE_EXERCISEEVENT_TABLE
				+ " WHERE date('" + DATABASE_EXERCISEEVENT_EVENTDATETIME
				+ "') = date('" + date + "')";
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
		return mDb.delete(DATABASE_EXERCISEEVENT_TABLE,
				DATABASE_EXERCISEEVENT_ID + "=" + id, null) > 0;
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

	// get food by language ID
	public Cursor fetchFoodByLanguageID(long languageID) {
		return mDb.query(DATABASE_FOOD_TABLE, new String[] { DATABASE_FOOD_ID,
				DATABASE_FOOD_NAME, DATABASE_FOOD_ISFAVORITE,
				DATABASE_FOOD_VISIBLE, DATABASE_FOOD_PLATFORM,
				DATABASE_FOOD_FOODLANGUAGEID }, DATABASE_FOOD_FOODLANGUAGEID
				+ "=" + languageID, null, DATABASE_FOOD_NAME, null, null);
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
