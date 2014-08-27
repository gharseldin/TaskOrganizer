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

import com.gharseldin.taskorganizer.datastructures.TaskRecord;

public class TaskDatabaseManager {

	private DatabaseHelper dataHelper;
	private SQLiteDatabase mDb;
	private Context ctx;
	private static final String DATABASE_NAME = "OrganizerData";
	private static String TABLE_NAME = "tasks";
	private static final int DATABASE_VERSION = 1;
	private String DATABASE_PATH;

	private TaskRecord[] resultArray;
	private Cursor cur;

	public TaskDatabaseManager(Context ctx) {
		this.ctx = ctx;
		dataHelper = new DatabaseHelper(ctx, DATABASE_NAME, DATABASE_VERSION);
		DATABASE_PATH = ctx.getFilesDir().getPath() // data/data/com.android.sqlitedbdemo/files
				.replace("files", "databases") // Then replaces files with
												// databases.
				+ "/";
	}

	// Check the existance of a database
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
	public TaskDatabaseManager openDB() throws SQLException {

		mDb = dataHelper.getWritableDatabase();
		return this;
	}

	// get the records for entire Task table and returns them in an array of
	// TaskRecord
	public TaskRecord[] getRecords(int count) {
		try {
			cur = mDb.rawQuery("SELECT * FROM " + TABLE_NAME, null);

		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		Log.d("DataBase", "count =" + count);
		Log.d("DataBase", "Query performed successfully");

		resultArray = new TaskRecord[count];

		int x = 0;

		try {
			if (cur.moveToFirst()) {
				Log.d("DataBase", "Curser moved to first position");
				do {
					resultArray[x] = new TaskRecord();

					resultArray[x].setTaskID(cur.getInt(cur
							.getColumnIndex("_id"))); // cur.getColumnIndex("_id")

					resultArray[x].setUserID(cur.getInt(cur
							.getColumnIndex("users_id")));

					resultArray[x].setContent(cur.getString(cur
							.getColumnIndex("content")));

					resultArray[x].setDateDue(cur.getString(cur
							.getColumnIndex("date_due")));

					resultArray[x].setDateSet(cur.getString(cur
							.getColumnIndex("date_set")));

					resultArray[x].setEmailNotify(cur.getString(cur
							.getColumnIndex("email_notify")));

					resultArray[x].setSmsNotify(cur.getString(cur
							.getColumnIndex("sms_notify")));

					resultArray[x].setPicture(cur.getString(cur
							.getColumnIndex("picture")));

					resultArray[x].setStatus(cur.getString(cur
							.getColumnIndex("status")));

					resultArray[x].setPriority(cur.getString(cur
							.getColumnIndex("priority")));

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
			cur = mDb.rawQuery("select count(_id) from " + TABLE_NAME, null); // use
																				// distinct
																				// name
																				// to
																				// get
																				// distinct
																				// names
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

	// get number of tasks for a specific userID
	public int getUserTasksCount(int userId) {
		int count = 0;
		try {

			cur = mDb.rawQuery("select count(_id) from " + TABLE_NAME
					+ " where users_id = " + userId, null);

		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		if (cur.moveToFirst()) {
			count = cur.getInt(0);
		}
		cur.close(); // never forget to close the curser object
		return count;
	}

	// get an array of the tasks for a specific userID
	public TaskRecord[] getUserTaks(int count, int userId) {
		int position = 0;
		resultArray = new TaskRecord[count];

		try {
			cur = mDb.rawQuery("select * from " + TABLE_NAME
					+ " where users_id = " + userId, null);

			while (cur.moveToNext()) {

				resultArray[position] = new TaskRecord();
				resultArray[position].setTaskID(cur.getInt(cur
						.getColumnIndex("_id")));
				resultArray[position].setUserID(cur.getInt(cur
						.getColumnIndex("users_id")));
				resultArray[position].setContent(cur.getString(cur
						.getColumnIndex("content")));
				resultArray[position].setPicture(cur.getString(cur
						.getColumnIndex("picture")));
				resultArray[position].setDateSet(cur.getString(cur
						.getColumnIndex("date_set")));
				resultArray[position].setDateDue(cur.getString(cur
						.getColumnIndex("date_due")));
				resultArray[position].setEmailNotify(cur.getString(cur
						.getColumnIndex("email_notify")));
				resultArray[position].setSmsNotify(cur.getString(cur
						.getColumnIndex("sms_notify")));
				resultArray[position].setStatus(cur.getString(cur
						.getColumnIndex("status")));
				resultArray[position].setPriority(cur.getString(cur
						.getColumnIndex("priority")));

				position++;
			}
			Log.d("Progress", "Finished initializing the results task array");
			return resultArray;
		} catch (Exception e) {

			return null;
		}
	}
	
	// Update a specific task record
	public void updateRecord(TaskRecord updateTask) {
		try {
			// cur = mDb.rawQuery("update " + TABLE_NAME + " set number='"
			// + number + "' where name='" + name + "'", null);
			ContentValues cv = new ContentValues();
			// cv.put("_id", updateTask.getTaskID()); //no need to update this
			// field
			cv.put("users_id", updateTask.getUserID());
			cv.put("content", updateTask.getContent());
			cv.put("date_due", updateTask.getDateDue());
			cv.put("date_set", updateTask.getDateSet());
			cv.put("email_notify", updateTask.getEmailNotify());
			cv.put("sms_notify", updateTask.getSmsNotify());
			cv.put("picture", updateTask.getPicture());
			cv.put("status", updateTask.getStatus());
			cv.put("priority", updateTask.getPriority());

			mDb.update(TABLE_NAME, cv, "_id='" + updateTask.getTaskID() + "'",
					null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		// cur.close();
	}

	// insert a new Task record
	public void insertRecord(TaskRecord insertTask) {
		try {
			ContentValues cv = new ContentValues();
			cv.put("users_id", insertTask.getUserID());
			cv.put("content", insertTask.getContent());
			cv.put("date_due", insertTask.getDateDue());
			cv.put("date_set", insertTask.getDateSet());
			cv.put("email_notify", insertTask.getEmailNotify());
			cv.put("sms_notify", insertTask.getSmsNotify());
			cv.put("picture", insertTask.getPicture());
			cv.put("status", insertTask.getStatus());
			cv.put("priority", insertTask.getPriority());

			mDb.insert(TABLE_NAME, null, cv);

		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}

	// TODO implement functionality of delete profile in Organizer

	//delete a specific record
	public void deleteRecord(TaskRecord deletetRecord) {
		try {
			mDb.delete(TABLE_NAME, "_id='" + deletetRecord.getTaskID() + "'",
					null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}

	//close the database
	public void closeDB() {
		try {
			mDb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
