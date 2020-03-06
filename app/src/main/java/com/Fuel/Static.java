package com.Fuel;

public class Static {
	
	public static final int MENU_BACKUP = 8;
	public static final int MENU_RESTORE = 9;
	
	public static final int MINIMUM_LITERS = 1;
	public static final int DEFAULT_LITERS = 10;
	
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "fuel";
	public static final String WHERE_ID = "_id = ?";
	public static final String ID_COLUMN = "_id";
	
	public static int SCREEN_WIDTH = 0;
	
	public static final int SETTINGS_ACTION = 1;
	public static float DEFAULT_CONSUMPTION = 6; // Liters/100km
	public static float DEFAULT_CONSUMPTION_PB = 8; // Liters/100km
	public static int DEFAULT_TYPE = 0; // Fuel type

	/**
	 * Table: history
	 * 
	 */
	public static final String HISTORY_TABLE_NAME = "history";
	public static final String HISTORY_ORDER_DATE = "date DESC";
	public static final String HISTORY_INSERT = "INSERT INTO %s VALUES (null, ?, ?, ?, ?)";
	public static final String[] HISTORY_COLUMNS = new String[] {
		"_id", // *
		"date", // TEXT
		"liters", // double
		"cost", // double
		"type" // integer
	};
}