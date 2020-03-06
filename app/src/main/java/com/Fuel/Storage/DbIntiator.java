package com.Fuel.Storage;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;

public class DbIntiator {

    public static SQLiteDatabase getDB() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dbPath = new File(path, "data/data/com.Fuel/databases/fuel").getAbsolutePath();
        return SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
}
