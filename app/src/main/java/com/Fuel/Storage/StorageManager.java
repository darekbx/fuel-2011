package com.Fuel.Storage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.Fuel.Entry;
import com.Fuel.Static;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class StorageManager {

	private SQLiteDatabase db;
	
	public StorageManager(SQLiteDatabase db) {
	
		this.setDb(db);
	}

	public boolean addEntry(Entry entry) {
	
		return this.addEntry(entry, Calendar.getInstance().getTimeInMillis() / 1000);
	}
	
	public boolean addEntry(Entry entry, long date) {
		
		String sql = String.format(Static.HISTORY_INSERT, 
				Static.HISTORY_TABLE_NAME);
		
		SQLiteStatement stmt = this.getDb().compileStatement(sql);
		stmt.bindString(1, String.valueOf(date));
		stmt.bindDouble(2, entry.getLiters());
		stmt.bindDouble(3, entry.getCost());
		stmt.bindLong(4, entry.getType());
		
		return stmt.executeInsert() > 0;
	}
	
	public Entry getEntry(int id) {
	
		Cursor cursor = this.db.query(Static.HISTORY_TABLE_NAME, Static.HISTORY_COLUMNS,
				Static.WHERE_ID, new String[] { String.valueOf(id) }, null, null, null);
		
		cursor.moveToFirst();
		Entry entry = this.getEntryFromCursor(cursor);
		
		if (cursor != null && !cursor.isClosed())
			cursor.close();
		
		return entry;
	}
	
	public List<Entry> getEntries() {
	
		Cursor cursor = this.db.query(Static.HISTORY_TABLE_NAME, Static.HISTORY_COLUMNS,
				null, null, null, null, null);
		
		List<Entry> applications = new ArrayList<Entry>();
		
		if (cursor.moveToFirst()) {
		
			applications.add(this.getEntryFromCursor(cursor));
			
			while (cursor.moveToNext())
				applications.add(this.getEntryFromCursor(cursor));
		}

		if (cursor != null && !cursor.isClosed())
			cursor.close();
		
		return applications;
	}
	
	public boolean deleteEntry(int id) {
	
		return this.db.delete(Static.HISTORY_TABLE_NAME, 
				Static.WHERE_ID, new String[] { String.valueOf(id) }) > 0;
	}
	
	public boolean reset() {
	
		return this.getDb().delete(Static.HISTORY_TABLE_NAME, null, null) > 0;
	}
	private Entry getEntryFromCursor(Cursor cursor) {

		Entry entry = new Entry();
		entry.setId(cursor.getInt(0));
		entry.setDate(Long.parseLong(cursor.getString(1)));
		entry.setLiters(cursor.getDouble(2));
		entry.setCost(cursor.getDouble(3));
		entry.setType(cursor.getInt(4));
		
		return entry;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}
 
	public SQLiteDatabase getDb() {
		return db;
	}
}