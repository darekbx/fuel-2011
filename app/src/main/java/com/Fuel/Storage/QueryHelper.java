package com.Fuel.Storage;

import com.Fuel.Static;

public class QueryHelper {

	public static String createHistoryTableQuery() {
		
		return String.format(
				"CREATE TABLE IF NOT EXISTS %s (" +
				"_id integer primary key autoincrement, " +
				"date TEXT," +
				"liters double, " +
				"cost double, " +
				"type integer " +
				")", 
			Static.HISTORY_TABLE_NAME);
	}
}