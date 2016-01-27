package com.dinamalarnellai.app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DBUtil extends SQLiteOpenHelper {

	private String DB_PATH;
	private String dbname;
	private String tblname;
	private SQLiteDatabase db;
	private Context myContext;
	private static AppController controller;

	public DBUtil(Context ctx, String dbname, String dbpath) {
		super(ctx, dbname, null, 2);
		this.myContext = ctx;
		this.dbname = dbname;
		this.DB_PATH = dbpath;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getTblname() {
		return tblname;
	}

	public void setTblname(String tblname) {
		this.tblname = tblname;
	}
	
	public static String getIMEINumber() {
		String deviceId = "";
		try {
			TelephonyManager telephonyManager = (TelephonyManager) controller
					.getSystemService(Context.TELEPHONY_SERVICE);
			deviceId = telephonyManager.getDeviceId();
		} catch (Exception e) {
			return "0";
		}
		if (deviceId == null)
			return "0";
		else
			return deviceId;
	}

	public void insertSQL(String tbl, String columns, String content) {
		String sql = "insert into " + tbl + "(" + columns + ") values("
				+ content + ")";
		Log.d(this.getClass().getName(), sql);
		db.execSQL(sql);
	}

	public void updateSQL(String sql) {
		Log.d(this.getClass().getName(), sql);
		db.execSQL(sql);

	}

	public void deleteSQL(String tbl, String where, boolean all) {
		String sql;
		if (all)
			sql = "delete from " + tbl;
		else
			sql = "delete from " + tbl + " where " + where;
		Log.d(this.getClass().getName(), sql);
		db.execSQL(sql);
	}

	public void executeQ(String sql) {
		Log.d(this.getClass().getName(), sql);
		db.execSQL(sql);

	}

	public Cursor selectSQL(String sql) {
		Log.d(this.getClass().getName(), sql);
		Cursor cs = db.rawQuery(sql, null);
		return cs;
	}

	public Cursor selectSQL(String sql, String[] args) {
		Log.d(this.getClass().getName(), sql);
		Cursor cs = db.rawQuery(sql, args);
		return cs;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		this.db = db;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	public void closeDB() {
		this.db.close();
	}

	public void createDataBase() {
		boolean dbExist = checkDataBase();
		if (dbExist) {
			// do nothing - database already exist
		} else {
			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			getWritableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				e.printStackTrace();
				throw new Error("Error copying database");
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH + dbname;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// database does't exist yet.
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
		// abbas
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {
		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(dbname);
		// Path to the just created empty db
		String outFileName = DB_PATH + dbname;
		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void openDataBase() throws SQLException {
		// Open the database
		String myPath = DB_PATH + dbname;
		db = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);

	}

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

	public boolean multiInsert(String sql) {
		Log.d(this.getClass().getName(), sql);
		db.execSQL(sql);
		return true;
	}

	public boolean emptyTable(String tableName) {
		if (tableName != null) {
			if (!tableName.equals("")) {
				String sql = "DELETE FROM " + tableName;
				// Logs.query(TAG, sql);
				db.execSQL(sql);
				return true;
			}
		}

		return false;
	}

}
