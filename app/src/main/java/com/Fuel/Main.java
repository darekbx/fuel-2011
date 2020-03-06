package com.Fuel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.Fuel.List.IconFile;
import com.Fuel.List.IconLayoutAdapter;
import com.Fuel.Override.CustomGestureOverlayView;
import com.Fuel.Storage.DbIntiator;
import com.Fuel.Storage.StorageHelper;
import com.Fuel.Storage.StorageManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class Main extends Activity  {
	
	/**
	 * Type:
	 * 0 - Diesel
	 * 1 - 95
	 * 2 - 98
	 * 3 - Lpg
	 */
	
	private ListView list;
	private TextView status;
	private StorageManager manager;
	private IconLayoutAdapter adapter;

	private SQLiteDatabase db;
	private GestureLibrary library;
	private boolean gesturesStarted = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

		// set portrait orientation
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		this.loadPreferences();

		Static.SCREEN_WIDTH = getWindowManager().getDefaultDisplay().getWidth();

		this.setList((ListView) findViewById(R.id.list));
		this.setStatus((TextView) findViewById(R.id.status));

		this.db = DbIntiator.getDB();
		this.setManager(new StorageManager(this.db));

		this.adapter = new IconLayoutAdapter(this);
		this.getList().setAdapter(adapter);

		this.registerForContextMenu(this.getList());
		final Context parent = this;

		// Default values
		boolean setDefault = false;

		if (setDefault) {

			java.util.Calendar date = java.util.Calendar.getInstance();
			this.manager.reset();

			date.set(2011, 4 - 1, 10);
			this.manager.addEntry(new Entry(20.01, 100.9, 0), date.getTimeInMillis() / 1000);

			date.set(2011, 4 - 1, 16);
			this.manager.addEntry(new Entry(30.03, 151.3, 0), date.getTimeInMillis() / 1000);

			date.set(2011, 4 - 1, 24);
			this.manager.addEntry(new Entry(19.84, 100.04, 0), date.getTimeInMillis() / 1000);

			date.set(2011, 4 - 1, 29);
			this.manager.addEntry(new Entry(39.73, 200.24, 0), date.getTimeInMillis() / 1000);

			date.set(2011, 5 - 1, 7);
			this.manager.addEntry(new Entry(25.00, 126.00, 0), date.getTimeInMillis() / 1000);

			date.set(2011, 5 - 1, 13);
			this.manager.addEntry(new Entry(20.13, 100.05, 0), date.getTimeInMillis() / 1000);
		}

		this.getList().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {

				IconFile item = (IconFile) adapter.getItem(position);
				showInformation(item);
			}
		});

		this.getList().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {

				IconFile item = (IconFile) adapter.getItem(position);
				final int itemId = item.getId();

				new AlertDialog.Builder(parent)
						.setTitle(R.string.delete_title)
						.setMessage(R.string.delete_entry)
						.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

								manager.deleteEntry(itemId);
								fill();
							}
						})
						.setNegativeButton(R.string.button_no, null)
						.show();

				return false;
			}
		});

		this.fill();
		this.gesturesStarted = false;


		findViewById(R.id.menu_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PopupMenu popupMenu = new PopupMenu(Main.this, v);
				popupMenu.inflate(R.menu.main_menu);
				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						onOptionsItemSelected(item);
						return false;
					}
				});
				popupMenu.show();
			}
		});
	}
    
    @Override
    protected void onDestroy() {
    	
    	this.db.close();
    	super.onDestroy();
    }
    
    @Override
    public void onBackPressed() {
    	
    	this.db.close();
    	super.onBackPressed();
    }
    
    @Override
    protected void onResume() {

    	this.gesturesStarted = true;
    	this.loadGestureLibrary(); 	
    	super.onResume();
    }
    
    private void loadGestureLibrary() {
    	
    	if (!this.gesturesStarted)
    		return;
    	
		String dir = Environment.getExternalStorageDirectory() + this.getString(R.string.dir);
		File gestureFile = new File(dir, this.getString(R.string.gestures_file));
        this.library = GestureLibraries.fromFile(gestureFile);
        this.library.load();
        
        boolean createGesture = false;
        if (this.library.getGestureEntries().size() == 0) {
        
        	// add new gesture for add entry action
        	Toast.makeText(this, R.string.create_gesture, Toast.LENGTH_SHORT).show();
        	createGesture = true;
        }

        CustomGestureOverlayView gestureView = (CustomGestureOverlayView)this.findViewById(R.id.gestures);
        gestureView.setCreateNew(createGesture);
        gestureView.addOnGesturePerformedListener(new OnGesturePerformedListener() {
			
			@Override
			public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {

				CustomGestureOverlayView customOverlay = (CustomGestureOverlayView)overlay;
				
				if (customOverlay.isCreateNew()) {
					
					library.addGesture(getString(R.string.add_gesture_name), gesture);
					library.save();
					
					customOverlay.setCreateNew(false);
				}
				else {
				
					ArrayList<Prediction> predictions = library.recognize(gesture);
					
					if (predictions.size() > 0 && predictions.get(0).score > 2.5f)
						addEntry();
				}
			}
		});
    }
    
    private void loadPreferences() {

    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
    	Static.DEFAULT_CONSUMPTION = preferences.getFloat(getString(R.string.storage_liters), Static.DEFAULT_CONSUMPTION);
    	Static.DEFAULT_CONSUMPTION_PB = preferences.getFloat(getString(R.string.storage_liters_pb), Static.DEFAULT_CONSUMPTION_PB);
    	Static.DEFAULT_TYPE = (int)preferences.getFloat(getString(R.string.storage_type), Static.DEFAULT_TYPE);
    }
    
    private void showInformation(IconFile item) {

    	TextView message = new TextView(this);
    	message.setPadding(6, 6, 6, 6);
    	message.setTextSize(12);
    	message.setTextColor(Color.WHITE);
    	message.setTypeface(Typeface.MONOSPACE);
    	
    	String type = getString(R.string.diesel);
    	
    	switch (item.getType()) {
    	
	    	case 0: type = getString(R.string.diesel); break;
	    	case 1: type = getString(R.string.fuel95); break;
	    	case 2: type = getString(R.string.fuel98); break;
    		case 3: type = getString(R.string.lpg); break;
    	}
    	
    	String information = String.format(getString(R.string.information_message), 
    			getString(R.string.i_liters), item.getLiters(), 
    			getString(R.string.i_cost), item.getCost(), 
    			getString(R.string.i_liter_price), item.getPrice(),
    			getString(R.string.i_distance), item.getDistance(),
    			getString(R.string.i_date), item.getDate(),
    			getString(R.string.i_type), type);
    	
    	message.setText(information);
    	
		Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(message);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
    }
    
    private void fill() {

    	this.adapter.clear();
    	List<Entry> entries = this.manager.getEntries();
    	
    	Collections.reverse(entries);
    	
    	double liters = 0.0;
    	double cost = 0.0;
    	int count = 0;
    	int icon = R.drawable.diesel;
    	
    	for (Entry e : entries) {
    	
    		switch (e.getType()) {
    		
	    		case 0: icon = R.drawable.diesel; break;
	    		case 1: icon = R.drawable.fuel95; break;
	    		case 2: icon = R.drawable.fuel98; break;
	    		case 3: icon = R.drawable.lpg; break;
    		}

			this.adapter.addItem(e.getId(), icon, e.getLiters(), e.getCost(), 
					e.getDate(), e.getDistance(), e.getPrice(), e.getType());
			
    		liters += e.getLiters();
    		cost += e.getCost();
    		count++;
    	}
    	
    	this.getStatus().setText(String.format(getString(R.string.status_format), liters, cost));
    	
    	this.adapter.notifyDataSetChanged();

		this.setTitle(String.format(getString(R.string.title_count), count));
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	if (resultCode == Static.SETTINGS_ACTION) {
    		
    		this.loadPreferences();
    		this.fill();
    	}
    	
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	MenuInflater inflater = this.getMenuInflater();
    	inflater.inflate(R.menu.main_menu, menu);

    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    
    	Intent statistics = null;
    	
    	switch (item.getItemId()) {
				
    		case R.id.menu_exit:
    			System.exit(0);
    			break;
    		
    		case R.id.menu_add:
    			this.addEntry();
    			break;
    			
    		case R.id.menu_statistics_price:
    			
    			statistics = new Intent(this, Statistics.class);
    			statistics.putExtra(getString(R.string.type), 0);
    			this.startActivity(statistics);
    			 
    			break;

    		case R.id.menu_statistics_liters:

    			statistics = new Intent(this, Statistics.class);
    			statistics.putExtra(getString(R.string.type), 1);
    			this.startActivity(statistics);
    			
    			break;

    		case R.id.menu_statistics_summary:
    			this.summary();
    			break;

    		case R.id.menu_settings:

    			Intent settings = new Intent(this, Settings.class);
    			this.startActivityForResult(settings, Static.SETTINGS_ACTION);
    			
    			break;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    
    private void addEntry() {
    	
    	final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.add_dialog);
		dialog.setCanceledOnTouchOutside(true);
		
		Button ok = (Button)dialog.findViewById(R.id.button_ok);
		
		int id = R.drawable.fuel95;
		
		switch (Static.DEFAULT_TYPE) {

    		case 0: id = R.drawable.diesel; break;
    		case 1: id = R.drawable.fuel95; break;
    		case 2: id = R.drawable.fuel98; break;
    		case 3: id = R.drawable.lpg; break;
		}
		
		Drawable drawable = this.getResources().getDrawable(id);
		drawable.setBounds(0, 0, 35, 36);
		ok.setCompoundDrawables(drawable, null, null, null);
		 
		final EditText valueText = (EditText)dialog.findViewById(R.id.value);
		final EditText costText = (EditText)dialog.findViewById(R.id.cost);
		
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				Entry entry = new Entry();
				entry.setLiters(Double.parseDouble(valueText.getText().toString()));
				entry.setCost(Double.parseDouble(costText.getText().toString()));
				entry.setType(Static.DEFAULT_TYPE);
				
				manager.addEntry(entry);
				
				dialog.dismiss();
				fill();
			}
		});
		
		dialog.show();
    }
    
    private void summary() {
    
    	List<Entry> entries = this.manager.getEntries();
    	
    	double averagePrice = 0.0;
    	double cost = 0.0;
    	double liters = 0.0;
    	int distance = 0;
    	int entriesCount = entries.size();
    	
    	for (Entry entry : entries) {
    		
    		cost += entry.getCost();
    		liters += entry.getLiters();
    		distance += entry.getDistance();
    		averagePrice += entry.getPrice();
    	}
    	
    	averagePrice /= entriesCount;
		
    	TextView message = new TextView(this);
    	message.setPadding(6, 6, 6, 6);
    	message.setTextSize(12);
    	message.setTextColor(Color.WHITE);
    	message.setTypeface(Typeface.MONOSPACE);
    	
    	String information = String.format(getString(R.string.summary_message), 
    			getString(R.string.i_entries), entriesCount,
    			getString(R.string.i_liters), liters, 
    			getString(R.string.i_cost), cost, 
    			getString(R.string.i_distance), distance,
    			getString(R.string.a_liter_price), averagePrice);
    	
    	message.setText(information);

    	Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(message);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
    }
    
	private void setManager(StorageManager manager) {
		
		this.manager = manager;
	}
	
	private void setList(ListView list) {
		this.list = list;
	}

	private ListView getList() {
		return list;
	}

	private void setStatus(TextView status) {
		this.status = status;
	}

	private TextView getStatus() {
		return status;
	}

}