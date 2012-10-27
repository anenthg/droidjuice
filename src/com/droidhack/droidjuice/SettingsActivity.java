package com.droidhack.droidjuice;
   
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	
	
	private int LOCAL_CACHING_INTERVAL_MINUTES=1;
	 
	private Button battery_button,start_service_button,stop_service_button;
	//private BatteryWatcherService batteryService;
	private ImageView syncImageView;
	
    private PendingIntent mAlarmSender;
    SharedPreferences appSettings;
    SharedPreferences.Editor prefEditor;

	 
	  
	  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        syncImageView=(ImageView)findViewById(R.id.syncImageView);

        appSettings = getSharedPreferences("DroidJuiceAppPref", MODE_PRIVATE);
        prefEditor = appSettings.edit();
        
        if(appSettings.getBoolean("sync_status", true)==true)
        {
        	syncImageView.setImageResource(R.drawable.sync_image_on);
        }
        else
        {
        	syncImageView.setImageResource(R.drawable.sync_image_off);
        }
        
        //batteryService=new BatteryWatcherService();
        mAlarmSender = PendingIntent.getService(SettingsActivity.this,
                0, new Intent(SettingsActivity.this, LocalService.class), 0);
        
        
        //switch in first use
        if(getIntent().getBooleanExtra("first_use", false)==true)
        {
        	HelperMethods.log("First time use- Intent");
        	startWatchmanService();
        }
        
        
        syncImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				 if(appSettings.getBoolean("sync_status", false)==true)
				 {
					 prefEditor.putBoolean("sync_status", false);
					 prefEditor.commit();
					 
					 syncImageView.setImageResource(R.drawable.sync_image_off);
					// syncOnOffButton.refreshDrawableState();
					 HelperMethods.log("OFF");
					 stopWatchmanService();
				 }
				 else
				 {
					 prefEditor.putBoolean("sync_status", true);
					 prefEditor.commit();
					 
 					 syncImageView.setImageResource(R.drawable.sync_image_on);
					 //syncOnOffButton.refreshDrawableState();
					 HelperMethods.log("ON");
					 
					 startWatchmanService();
				 }
			}
		});
       
    }
    
    public void startWatchmanService()
    {
    	long firstTime = SystemClock.elapsedRealtime();
 
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,
                        firstTime, LOCAL_CACHING_INTERVAL_MINUTES*60*1000, mAlarmSender); 
        
        Toast.makeText(getApplicationContext(), "STARTED SERVICE",
                Toast.LENGTH_SHORT).show();
    }
    
    public void stopWatchmanService()
    {
    	  AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
          am.cancel(mAlarmSender);
			 
          Toast.makeText(getApplicationContext(), "STOPPED SERVICE",
                  Toast.LENGTH_SHORT).show();
    }
     

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_intro, menu);
        return true;
    }
}
