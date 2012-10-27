package com.droidhack.droidjuice;

import android.os.BatteryManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class IntroActivity extends Activity {
	 
	Button battery_button;
	
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
        battery_button=(Button)findViewById(R.id.battery_button);
        
        battery_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				registerReceiver(mBatInfoReceiver, 
		        	    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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
