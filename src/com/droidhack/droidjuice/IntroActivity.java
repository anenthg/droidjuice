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
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class IntroActivity extends Activity {
	 
	private Button battery_button,start_service_button,stop_service_button;
	//private BatteryWatcherService batteryService;
	
    private PendingIntent mAlarmSender;

	
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context arg0, Intent intent) {
		      int level = intent.getIntExtra("level", 0);
		      
		      int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		      boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
		                           status == BatteryManager.BATTERY_STATUS_FULL;
		      
		      String chargingState= isCharging?"Charging":"Not Charging";
		      String displayMessage=chargingState+"\n"+level+" %";
		      
		      HelperMethods.log(displayMessage);
		      Toast.makeText(getApplicationContext(), displayMessage, Toast.LENGTH_SHORT).show();
		      unBindBroadcastService();
		}
	  };
	  
	  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        
        //batteryService=new BatteryWatcherService();
        mAlarmSender = PendingIntent.getService(IntroActivity.this,
                0, new Intent(IntroActivity.this, LocalService.class), 0);
        
        battery_button=(Button)findViewById(R.id.battery_button);
        
        battery_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				registerReceiver(mBatInfoReceiver, 
		        	    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			}
		});
        
        start_service_button=(Button)findViewById(R.id.start_service_button);
		        
        start_service_button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// batteryService.StartService(getApplicationContext());
						
						 long firstTime = SystemClock.elapsedRealtime();

				            // Schedule the alarm!
				            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
				            am.setRepeating(AlarmManager.RTC_WAKEUP,
				                            firstTime, 5*1000, mAlarmSender);
						
						// startService(new Intent(IntroActivity.this,
			           //             LocalService.class));
				            
				            Toast.makeText(getApplicationContext(), "STARTED SERVICE",
				                    Toast.LENGTH_SHORT).show();
					}
				});
		        
		stop_service_button=(Button)findViewById(R.id.stop_service_button);
		        
		stop_service_button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// batteryService.StopService(getApplicationContext());
						
						 // And cancel the alarm.
			            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
			            am.cancel(mAlarmSender);
						
//						 stopService(new Intent(IntroActivity.this,
//			                        LocalService.class));

			            // Tell the user about what we did.
			            Toast.makeText(getApplicationContext(), "STOPPED SERVICE",
			                    Toast.LENGTH_SHORT).show();
					}
				});
    }
    
    public void unBindBroadcastService()
    {
    	this.unregisterReceiver(mBatInfoReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_intro, menu);
        return true;
    }
}
