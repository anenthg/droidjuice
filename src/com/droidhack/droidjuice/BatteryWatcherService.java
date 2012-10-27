package com.droidhack.droidjuice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.Toast;

public class BatteryWatcherService extends BroadcastReceiver{

	Context parentApplicationContext;
	
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
		      Toast.makeText(parentApplicationContext, displayMessage, Toast.LENGTH_SHORT).show();
		      unBindBroadcastService();
		}
	  };
	@Override
	public void onReceive(Context context, Intent intent) {
		parentApplicationContext=context;
		//Toast.makeText(context, "Toasting from service", Toast.LENGTH_SHORT).show();
		
		parentApplicationContext.registerReceiver(mBatInfoReceiver, 
        	    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		
	}
	
	 public void unBindBroadcastService()
	    {
		 parentApplicationContext.unregisterReceiver(mBatInfoReceiver);
	    }
	
 public void StartService(Context context)
    {
	 	parentApplicationContext=context;
	 	
	 	HelperMethods.log("STARTED");
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BatteryWatcherService.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 5 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5 , pi); 
        
		//Toast.makeText(context, "Started service", Toast.LENGTH_SHORT).show();

    }
 
public void StopService(Context context)
    {
		parentApplicationContext=context;
	
		HelperMethods.log("STOPPED");
        Intent intent = new Intent(context, BatteryWatcherService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        
		//Toast.makeText(context, "Stopped service", Toast.LENGTH_SHORT).show();

    }

}
