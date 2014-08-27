package com.gharseldin.taskorganizer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DatabaseHelper extends SQLiteOpenHelper{

	public DatabaseHelper(Context context,String DatabaseName, int  DataBaseVersion) {
		super(context, DatabaseName, null, DataBaseVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("DBHelper", "Upgrading database from version " + oldVersion
				+ " to " + newVersion + ", which will destroy all old data");
		onCreate(db);
	}
}
