package com.Fuel;

import com.Fuel.Storage.DbIntiator;
import com.Fuel.Storage.StorageHelper;
import com.Fuel.Storage.StorageManager;
import com.Fuel.View.LitersView;
import com.Fuel.View.PriceView;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class Statistics extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

    	// set portrait orientation
    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
		super.onCreate(savedInstanceState);
		
		int type = getIntent().getIntExtra(getString(R.string.type), 0);

        SQLiteDatabase db = DbIntiator.getDB();
        
		if (type == 0)
			this.setContentView(new PriceView(this, new StorageManager(db)));
		else
			this.setContentView(new LitersView(this, new StorageManager(db)));
	}
}