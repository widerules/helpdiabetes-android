package be.goossens.oracle;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DbSQLiteOpenHelper extends SQLiteOpenHelper {
	// The android default system path to my application database
	private static String DB_PATH = "/data/data/be/goossens/oracle/databases/";
	private static String DB_NAME = "dbhelpdiabetes";
	private SQLiteDatabase myDataBase;
	private final Context myContext;

	public DbSQLiteOpenHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	/*
	 * Create a empty database on the system and rewrites it with the
	 * databasefile
	 */
	public void createDatabase() throws IOException {
		if (!checkDatabase()) {
			/*
			 * This method will create a empty database
			 */
			this.getReadableDatabase();
			copyDatabase();
		}
	}

	// Copy your database from the local asset folder to the just created empty
	// database
	private void copyDatabase() {
		try {
			// Open the local db as the input stream
			InputStream myInput = myContext.getAssets().open(DB_NAME);
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
			//close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();
			
		} catch (IOException e) {
		}
	}

	public void openDatabase() throws SQLException{
		//Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	}
	
	@Override
	public synchronized void close(){
		if(myDataBase != null){
			myDataBase.close();
		}
		super.close();
	}
	
	// Check if the database already exists
	private boolean checkDatabase() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// The database does not exists
		}
		return checkDB != null ? true : false;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
