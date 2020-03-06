package com.Fuel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Path;
import android.os.Environment;
import android.widget.Toast;

import com.Fuel.Storage.DbIntiator;
import com.Fuel.Storage.StorageHelper;
import com.Fuel.Storage.StorageManager;

public class BackupStorage {
	
	public static void restore(Context context) {

		StorageHelper th = new StorageHelper(context);
        SQLiteDatabase db = th.getWritableDatabase();
        StorageManager manager = new StorageManager(db);
        
		String dir = Environment.getExternalStorageDirectory() + context.getString(R.string.dir);
		File boardDirectory = new File(dir);
		File inputFile = new File(boardDirectory, context.getString(R.string.backup_file));
		
		try {
			
			FileInputStream fis = new FileInputStream(inputFile);
			String taskDelimiter = context.getString(R.string.fuel_delimiter);
			String itemDelimiter = context.getString(R.string.item_delimiter);
			
			// read
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();

			String[] tasks = new String(buffer).split(taskDelimiter);
			String[] items;
			Entry e;
			
			for (String task : tasks) {

				if (task.length() < 5)
					continue;
				
				items = task.split(itemDelimiter);
				
				e = new Entry();			
				e.setId(Integer.parseInt(items[0]));
				e.setLiters(Double.parseDouble(items[1]));
				e.setCost(Double.parseDouble(items[2]));
				e.setDate(Long.parseLong(items[3]));
				e.setType(Integer.parseInt(items[4]));
				
				manager.addEntry(e, Long.parseLong(items[3]));
			}
			
			Toast.makeText(context, R.string.restore_success, 0).show();
		} 
		catch (IOException e) {

			Toast.makeText(context, e.getMessage(), 1).show();
		} 
	}

	public static void backup(Context context) {

        SQLiteDatabase db = DbIntiator.getDB();
        StorageManager manager = new StorageManager(db);
        
        List<Entry> entries = manager.getEntries();
        
		StringBuilder builder = new StringBuilder();
		String taskDelimiter = context.getString(R.string.fuel_delimiter);
		String itemDelimiter = context.getString(R.string.item_delimiter);
		
		for (Entry e : entries) {
		
			builder.append(taskDelimiter);
			builder.append(e.getId());
			builder.append(itemDelimiter);
			builder.append(e.getLiters());
			builder.append(itemDelimiter);
			builder.append(e.getCost());
			builder.append(itemDelimiter);
			builder.append(e.getDate());
			builder.append(itemDelimiter);
			builder.append(e.getType());
		}
		
		String dir = Environment.getExternalStorageDirectory() + context.getString(R.string.dir);
		File boardDirectory = new File(dir);
		
		if (!boardDirectory.isDirectory())
				boardDirectory.mkdirs();
		
		File outputFile = new File(boardDirectory, context.getString(R.string.backup_file));

		try {
			
			// write
			FileOutputStream fos = new FileOutputStream(outputFile);
			fos.write(builder.toString().getBytes());
			fos.close();
			
			Toast.makeText(context, R.string.backup_success, 0).show();
		} 
		catch (IOException e) {
			
			Toast.makeText(context, e.getMessage(), 1).show();
		}
	}
}