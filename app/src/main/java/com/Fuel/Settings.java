package com.Fuel;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.Fuel.Storage.DbIntiator;
import com.Fuel.Storage.StorageHelper;
import com.Fuel.Storage.StorageManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class Settings extends Activity {

	private RefreshHandler redrawHandler = new RefreshHandler();
	private RefreshHandlerPB redrawHandlerPB = new RefreshHandlerPB();

	private class RefreshHandler extends Handler {
		
	    @Override
	    public void handleMessage(Message msg) {
	    	
	    	updateConsumption();
	    }

	    public void sleep(long delayMillis) {
	        
	    	this.removeMessages(0);
	        sendMessageDelayed(obtainMessage(0), delayMillis);
	    }
	};

	private class RefreshHandlerPB extends Handler {
		
	    @Override
	    public void handleMessage(Message msg) {
	    	
	    	updateConsumptionPB();
	    }

	    public void sleep(long delayMillis) {
	        
	    	this.removeMessages(0);
	        sendMessageDelayed(obtainMessage(0), delayMillis);
	    }
	};
	
	private StorageManager manager;
	private EditText lkm;
	private EditText lkmPB;
	private Timer timer;
	private boolean increment = true;
	private boolean incrementPB = true;
	private float consumption;
	private float consumptionPB;
	private int type;
	
	private final int AUTO_DELAY = 1000;
	private final int AUTO_INTERVAL = 100;
	
	private ImageButton buttonDiesel;
	private ImageButton button95;
	private ImageButton button98;
	private ImageButton buttonLPG;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

    	// set portrait orientation
    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.settings);

        findViewById(R.id.main).startAnimation(new ViewAnimation());
        
        // set defaults
        boolean setDefaults = false;
        
        if (setDefaults)
        	this.setDefaultPreferences();

        SQLiteDatabase db = DbIntiator.getDB();
        this.setManager(new StorageManager(db));
        
        this.lkm = (EditText)findViewById(R.id.value);
        this.lkmPB = (EditText)findViewById(R.id.value_pb);
        this.timer = new Timer();
        
        // load preferences
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
    	
    	// diesel
    	this.consumption = preferences.getFloat(getString(R.string.storage_liters), Static.DEFAULT_CONSUMPTION);
    	this.consumption = Math.round(this.consumption * 10f) / 10f;

    	// PB
    	this.consumptionPB = preferences.getFloat(getString(R.string.storage_liters_pb), 0);//Static.DEFAULT_CONSUMPTION);
    	this.consumptionPB = Math.round(this.consumptionPB * 10f) / 10f;
    	
    	this.type = (int)preferences.getFloat(getString(R.string.storage_type), Static.DEFAULT_TYPE);
    	this.lkm.setText(String.valueOf(this.consumption));
    	this.lkmPB.setText(String.valueOf(this.consumptionPB));

    	this.buttonDiesel = (ImageButton)findViewById(R.id.button_diesel);
    	this.button95 = (ImageButton)findViewById(R.id.button_95);
    	this.button98 = (ImageButton)findViewById(R.id.button_98);
    	this.buttonLPG = (ImageButton)findViewById(R.id.button_lpg);
    	
    	final Context parent = this;
    	
    	// load type
    	switch (this.type) {
    	
	    	case 0: this.buttonDiesel.setEnabled(false); break;
	    	case 1: this.button95.setEnabled(false); break;
	    	case 2: this.button98.setEnabled(false); break;
    		case 3: this.buttonLPG.setEnabled(false); break;
    	}
    	
        // close
        ((Button)findViewById(R.id.button_close)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) { finish(); }
		});
        
        // reset
        ((Button)findViewById(R.id.button_reset)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) { reset(); }
		});
        
        // reset gesture
        ((Button)findViewById(R.id.button_gesture_remove)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) { resetGesture(); }
		});
        
        
        // diesel liters / km
        Button minus = (Button)findViewById(R.id.button_minus);
        Button plus = (Button)findViewById(R.id.button_plus);
        plus.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					increment = true;
					
			        timer = new Timer();
					timer.scheduleAtFixedRate(mUpdateTask, AUTO_DELAY, AUTO_INTERVAL);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP) {

					timer.cancel();
					timer.purge();
				}
				
				return false;
			}
		});
        plus.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View v) {

				consumption += 0.1;
				updateConsumption();
			}
		});
        minus.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					increment = false;
					
			        timer = new Timer();
					timer.scheduleAtFixedRate(mUpdateTask, AUTO_DELAY, AUTO_INTERVAL);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP) {

					timer.cancel();
					timer.purge();
				}
				
				return false;
			}
		});
        minus.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View v) {

				if (consumption <= 0)
					return;
				
				consumption -= 0.1;
				updateConsumption();
			}
		});
        
        // PB liters / km
        Button minusPB = (Button)findViewById(R.id.button_minus_pb);
        Button plusPB = (Button)findViewById(R.id.button_plus_pb);
        plusPB.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					incrementPB = true;
					
			        timer = new Timer();
					timer.scheduleAtFixedRate(mUpdateTaskPB, AUTO_DELAY, AUTO_INTERVAL);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP) {

					timer.cancel();
					timer.purge();
				}
				
				return false;
			}
		});
        plusPB.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View v) {

				consumptionPB += 0.1;
				updateConsumptionPB();
			}
		});
        minusPB.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					incrementPB = false;
					
			        timer = new Timer();
					timer.scheduleAtFixedRate(mUpdateTaskPB, AUTO_DELAY, AUTO_INTERVAL);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP) {

					timer.cancel();
					timer.purge();
				}
				
				return false;
			}
		});
        minusPB.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View v) {

				if (consumptionPB <= 0)
					return;
				
				consumptionPB -= 0.1;
				updateConsumptionPB();
			}
		});
        
        // fuel type
        this.buttonDiesel.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		
        		type = 0;
        		updateType();
        		v.setEnabled(false);
        	}
        });
        this.button95.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		
        		type = 1;
        		updateType();
        		v.setEnabled(false);
        	}
        });
        this.button98.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		
        		type = 2;
        		updateType();
        		v.setEnabled(false);
        	}
        });
        this.buttonLPG.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				type = 3;
				updateType();
        		v.setEnabled(false);
			}
		});
        

        // backup
        ((Button)findViewById(R.id.button_backup)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) { BackupStorage.backup(getBaseContext()); }
		});

        // resotre
        ((Button)findViewById(R.id.button_restore)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) { 
				
				new AlertDialog.Builder(parent)
				.setTitle(R.string.restore_text)
				.setMessage(R.string.restore_message)
				.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {

						BackupStorage.restore(getBaseContext());
					}
				})
				.setNegativeButton(R.string.button_no, null)
				.show();
			}
        });
	}
	
	private TimerTask mUpdateTask = new TimerTask() {

		@Override
		public void run() {

			if (increment)
				consumption += 0.1;
			else
				consumption -= 0.1;
			
			redrawHandler.sleep(10);
		}		
	};

	private TimerTask mUpdateTaskPB = new TimerTask() {

		@Override
		public void run() {

			if (incrementPB)
				consumptionPB += 0.1;
			else
				consumptionPB -= 0.1;
			
			redrawHandlerPB.sleep(10);
		}		
	};
	
	private void reset() {

		new AlertDialog.Builder(this)
			.setTitle(R.string.reset_title)
			.setMessage(R.string.reset_all)
			.setPositiveButton(R.string.button_ok, new android.content.DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {

					manager.reset();
				}
			})
			.setNegativeButton(R.string.button_cancel, null)
			.show();
	}
	
	private void resetGesture() {

		new AlertDialog.Builder(this)
			.setTitle(R.string.gesture_reset_title)
			.setMessage(R.string.gesture_reset_all)
			.setPositiveButton(R.string.button_ok, new android.content.DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {

					String dir = Environment.getExternalStorageDirectory() + getString(R.string.dir);
					File gestureFile = new File(dir, getString(R.string.gestures_file));
					gestureFile.delete();
				}
			})
			.setNegativeButton(R.string.button_cancel, null)
			.show();
	}
	
	private void enableAllButtons() {
		
		this.buttonDiesel.setEnabled(true);
		this.button95.setEnabled(true);
		this.button98.setEnabled(true);
		this.buttonLPG.setEnabled(true);
	}

	private void updateType() {

		this.enableAllButtons();
    	this.updatePreference(getString(R.string.storage_type), this.type);
	}
	
	private void updateConsumption() {

    	this.lkm.setText(String.valueOf(Math.round(this.consumption * 10f) / 10f ));
    	this.updatePreference(getString(R.string.storage_liters), this.consumption);
	}

	private void updateConsumptionPB() {

    	this.lkmPB.setText(String.valueOf(Math.round(this.consumptionPB * 10f) / 10f ));
    	this.updatePreference(getString(R.string.storage_liters_pb), this.consumptionPB);
	}

	private void updatePreference(String key, float value) {

    	Editor e = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit();
    	//e.clear();
        e.putFloat(key, value);
        e.commit();
	}
	
	private void updatePreferences(float liters, float litersPB, int type) {

    	Editor e = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit();
        e.clear();
        e.putFloat(getString(R.string.storage_liters), liters);
        e.putFloat(getString(R.string.storage_liters_pb), litersPB);
        e.putInt(getString(R.string.storage_type), type);
        e.commit();
	}

    private void setDefaultPreferences() {

    	this.updatePreferences(Static.DEFAULT_CONSUMPTION, Static.DEFAULT_CONSUMPTION, Static.DEFAULT_TYPE);
    }
    
	@Override
	public void finish() {

		this.setResult(Static.SETTINGS_ACTION);
		super.finish();
	}
	
	private void setManager(StorageManager manager) {
		
		this.manager = manager;
	}
}