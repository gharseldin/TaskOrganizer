package com.gharseldin.taskorganizer.database;

import java.io.File;
import java.io.FileNotFoundException;
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
import android.util.Log;

import com.gharseldin.taskorganizer.datastructures.UserRecord;

public class UserDatabaseManager {

	private DatabaseHelper dataHelper;
	private SQLiteDatabase mDb;
	private Context ctx;
	private static final String DATABASE_NAME = "OrganizerData";
	private static String TABLE_NAME = "users";
	private static final int DATABASE_VERSION = 1;
	private String DATABASE_PATH;

	private UserRecord[] resultArray;
	private Cursor cur;

	public UserDatabaseManager(Context ctx) {
		this.ctx = ctx;
		dataHelper = new DatabaseHelper(ctx,DATABASE_NAME , DATABASE_VERSION);

		// data/data/com.android.sqlitedbdemo/files Then replaces files with
		// databases. Since Databases is not present yet at the first time of
		// creation
		DATABASE_PATH = ctx.getFilesDir().getPath()
				.replace("files", "databases")
				+ "/";
	}

	// Check the existence of a database
	public boolean checkDataBase() {

		String myPath = DATABASE_PATH + DATABASE_NAME;
		File f = new File(myPath);
		return f.exists();
	}

	// copy the database from the apk file to the phone path
	public void createDataBase() {

		openDB();
		try {
			InputStream myInput = ctx.getAssets().open(DATABASE_NAME + ".db");
			OutputStream myOutput = new FileOutputStream(DATABASE_PATH
					+ DATABASE_NAME);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}

			if (mDb.isOpen())
				mDb.close();
			myOutput.flush();
			myOutput.close();
			myInput.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Opens the database
	public UserDatabaseManager openDB() throws SQLException {

		mDb = dataHelper.getWritableDatabase();
		return this;
	}

	
	// get the records for entire users table and returns them in an array of
	// UserRecord
	public UserRecord[] getRecords(int count) {
		try {// TODO make sure * works fine
			cur = mDb.rawQuery("SELECT * FROM " + TABLE_NAME, null);

		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		resultArray = new UserRecord[count];
		int x = 0;
		try {
			if (cur.moveToFirst()) {
				do {
					// FIXME Initialize the array first! Not sure
					resultArray[x].setUsername(cur.getString(cur
							.getColumnIndex("username")));
					resultArray[x].setPassword(cur.getString(cur
							.getColumnIndex("pass")));
					resultArray[x].setFirstName(cur.getString(cur
							.getColumnIndex("first_name")));
					resultArray[x].setLastName(cur.getString(cur
							.getColumnIndex("last_name")));
					resultArray[x].setEmail(cur.getString(cur
							.getColumnIndex("email_address")));
					resultArray[x].setPhone(cur.getString(cur
							.getColumnIndex("phone_number")));
					// Log.i("DB", x + " = " + resultArray[x] + " , "
					// + resultArray[x + 1]);
					x++;
				} while (cur.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		cur.close();
		return resultArray;
	}

	// get number of records
	public int getCount() {
		int count = 0;
		try {
			cur = mDb.rawQuery("select count(username) from " + TABLE_NAME,
					null); // use distinct name to get distinct names
			// cur = mDb.rawQuery(
			// "select count(distinct name) from " + TABLE_NAME, null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		if (cur.moveToFirst()) {
			count = cur.getInt(0); // getInt(0) -> because this is the first
									// column in the "Result set" Not table.
		}
		cur.close(); // never forget to close the curser object
		return count;
	}

	// TODO
	// overwrite this method and clean it.
	// redundency in checking

	// Checks username and password together for login
	public boolean chkLogin(UserRecord user) {
		try {
			cur = mDb
					.rawQuery(
							"select * from " + TABLE_NAME + " where username='"
									+ user.getUsername()
									+ "' AND pass='"
									+ user.getPassword() + "'", null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		if (cur.moveToFirst()) {
			
			//initialize the passed user record with the required data to be passed later to the post login activity
			user.setUserId(cur.getInt(cur.getColumnIndex("_id")));
			user.setUsername(cur.getString(cur.getColumnIndex("username")));
			user.setPassword(cur.getString(cur.getColumnIndex("pass")));
			user.setFirstName(cur.getString(cur.getColumnIndex("first_name")));
			user.setLastName(cur.getString(cur.getColumnIndex("last_name")));
			user.setEmail(cur.getString(cur.getColumnIndex("email_address")));
			user.setPhone(cur.getString(cur.getColumnIndex("phone_number")));
			
			cur.close();
			return true;
		} else {
			cur.close();
			return false;
		}
	}

	public boolean chkRecord(UserRecord testUser) {
		try {
			cur = mDb.rawQuery(
					"select * from " + TABLE_NAME + " where username='"
							+ testUser.getUsername() + "'"/*
																 * AND pass='" +
																 * testUser.
																 * getReturnPassword
																 * () + "'"
																 */, null);

			// not required for update check. only username required for update
			// check
			// select all from Records where username='xyz' AND pass='987650'
			Log.d("SQLite", "select Statement excuted");
		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		if (cur.moveToFirst()) { // when the table is empty returns null point
									// exception
			Log.d("SQLite", "curser test succeeded");
			// user.setReturnUsername(cur.getString(cur.getColumnIndex("username")));
			// user.setReturnPassword(cur.getString(cur.getColumnIndex("pass")));
			cur.close();
			return true;
		} else {
			cur.close();
			return false;
		}

		// true && true = AND = true, all other 3 cases - false - boolean
		// multiplication
		// false || false = OR = false, all other 3 cases - true - boolean
		// addition

		// if (user.getReturnUsername().equalsIgnoreCase(
		// testUser.getReturnUsername())
		// && user.getReturnUsername().equalsIgnoreCase(
		// testUser.getReturnUsername()))
		// return true;
		// else
		// return false;
	}

	// TODO
	// why did we not close our cursor here?
	// Is it because we didn't use the rawQuery and we used built in function?
	public void updateRecord(UserRecord updateUser) {
		try {
			// cur = mDb.rawQuery("update " + TABLE_NAME + " set number='"
			// + number + "' where name='" + name + "'", null);
			ContentValues cv = new ContentValues();
			cv.put("username", updateUser.getUsername());
			cv.put("pass", updateUser.getPassword());
			cv.put("first_name", updateUser.getFirstName());
			cv.put("last_name", updateUser.getLastName());
			cv.put("email_address", updateUser.getEmail());
			cv.put("phone_number", updateUser.getPhone());

			mDb.update(TABLE_NAME, cv,
					"username='" + updateUser.getUsername() + "'", null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		// cur.close();
	}

	public void insertRecord(UserRecord insertUser) {
		try {
			ContentValues cv = new ContentValues();
			cv.put("username", insertUser.getUsername());
			cv.put("pass", insertUser.getPassword());
			cv.put("first_name", insertUser.getFirstName());
			cv.put("last_name", insertUser.getLastName());
			cv.put("email_address", insertUser.getEmail());
			cv.put("phone_number", insertUser.getPhone());
			mDb.insert(TABLE_NAME, null, cv);

		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}

	// TODO implement functionality of delete profile in Organizer

	public void deleteRecord(UserRecord deletetUser) {
		try {
			mDb.delete(TABLE_NAME,
					"username='" + deletetUser.getUsername() + "'", null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}

	public void closeDB() {
		try {
			mDb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
