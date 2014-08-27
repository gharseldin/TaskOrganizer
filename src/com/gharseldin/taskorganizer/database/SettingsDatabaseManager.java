package com.gharseldin.taskorganizer.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.gharseldin.taskorganizer.datastructures.SettingsUnit;

public class SettingsDatabaseManager {

	private DatabaseHelper dataHelper;
	private static String DATABASE_PATH;
	private static final String DATABASE_NAME = "OrganizerData";
	private static final int DATABASE_VERSION = 1;
	private static String TABLE_NAME = "settings";
	private SQLiteDatabase mDb;
	private Cursor cur;

	public SettingsDatabaseManager(Context ctx) {
		dataHelper = new DatabaseHelper(ctx, DATABASE_NAME, DATABASE_VERSION);
		// TODO try to use the getDatabaseDir
		DATABASE_PATH = ctx.getFilesDir().getPath()
				.replace("files", "databases")
				+ "/";
	}

	// Opens the database and return a SettingDatabaseManger to be used for
	// database operations
	public SettingsDatabaseManager openDb() throws SQLException {
		mDb = dataHelper.getWritableDatabase();
		return this;
	}

	// Closes the database after finishing with the operations to prevent the
	// leak of resources
	public void closeDB() {
		try {
			mDb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check to see if user has settings in the database or is it the first time
	 * the user enters the settings screen
	 * 
	 * @param userId
	 *            representing the user ID to be checked
	 * @return true if a settings record was found for the user in the database
	 *         and returns false otherwise
	 */
	public boolean checkUserSettingsRecord(int userId) {
		boolean found = false;
		try {
			cur = mDb.rawQuery("select * from " + TABLE_NAME
					+ " where users_id = " + userId, null);

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cur.moveToFirst()) {

			found = true;
		}
		cur.close(); // never forget to close the cursor object
		return found;
	}

	public ArrayList<SettingsUnit> getUserSettingsRecord(int userId) {
		ArrayList<SettingsUnit> profile = new ArrayList<SettingsUnit>();
		SettingsUnit keyValuePair;
		try {
			openDb();
			cur = mDb.rawQuery("select * from " + TABLE_NAME
					+ " where users_id = " + userId, null);

			// make sure you check sequence of add and get from the profile
			for (int i = 0; i < 6; i++) {
				cur.moveToNext();
				keyValuePair = new SettingsUnit();
				keyValuePair
						.setSettingId(cur.getInt(cur.getColumnIndex("_id")));
				keyValuePair.setUserId(cur.getInt(cur
						.getColumnIndex("users_id")));
				keyValuePair.setKey(cur.getString(cur.getColumnIndex("key")));
				keyValuePair
						.setValue(cur.getString(cur.getColumnIndex("value")));

				profile.add(keyValuePair);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		cur.close();
		return profile;
	}

	public void insertUserSettings(ArrayList<SettingsUnit> p) {
		try {
			ContentValues cv = new ContentValues();
			SettingsUnit unit = new SettingsUnit();

			// Loop six times for each time you pull a settings value pair and
			// insert it
			// try to use a different for loop
			for (int i = 5; i >= 0; i--) {

				unit = p.get(i);

				cv.put("users_id", unit.getUserId());
				cv.put("key", unit.getKey());
				cv.put("value", unit.getValue());

				mDb.insert(TABLE_NAME, null, cv);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}

	public void updateUserSettings(ArrayList<SettingsUnit> p) {
		try {
			ContentValues cv = new ContentValues();
			SettingsUnit unit = new SettingsUnit();

			// Loop six times for each time you pull a settings value pair and
			// insert it
			// try to use a different for loop
			for (int i = 5; i >= 0; i--) {

				unit = p.get(i);

				cv.put("users_id", unit.getUserId());
				cv.put("key", unit.getKey());
				cv.put("value", unit.getValue());

				int rowsAffected = mDb.update(TABLE_NAME, cv,
						" users_id = " + unit.getUserId() + " AND key = '"+unit.getKey()+"'", null);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}
}
