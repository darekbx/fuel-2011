package com.Fuel.Storage;

import com.Fuel.Static;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StorageHelper extends SQLiteOpenHelper {

	public StorageHelper(Context context) {
		
		super(context, Static.DATABASE_NAME, null, Static.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(QueryHelper.createHistoryTableQuery());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL(String.format("DROP TABLE IF EXISTS %s", Static.HISTORY_TABLE_NAME));
	}
}