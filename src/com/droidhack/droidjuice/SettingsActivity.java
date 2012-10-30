package com.droidhack.droidjuice;
   
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	
	
	private int LOCAL_CACHING_INTERVAL_MINUTES=15;
	 
	private Button battery_button,start_service_button,stop_service_button;
	//private BatteryWatcherService batteryService;
	private ImageView syncImageView;
	
    private PendingIntent mAlarmSender;
    SharedPreferences appSettings;
    SharedPreferences.Editor prefEditor;
    RadioGroup pingFreqRadio;
    Button profileButton;
	  
	  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        appSettings = getSharedPreferences("DroidJuiceAppPref", MODE_PRIVATE);

        syncImageView=(ImageView)findViewById(R.id.syncImageView);
        pingFreqRadio=(RadioGroup)findViewById(R.id.pingFrequencyRadio);
        profileButton=(Button)findViewById(R.id.profileButton);
        profileButton.setText("http://droidjuice.me/?"+appSettings.getString("twitter_name", ""));
        
        profileButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String url = profileButton.getText().toString();
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
        
        
        
        
        LOCAL_CACHING_INTERVAL_MINUTES=appSettings.getInt("ping_freq", 15);
 
//        pingFreqRadio.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				 Toast.makeText(getApplicationContext(), text, duration)
//			}
//		});
        pingFreqRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				
				
				
				if(arg1==R.id.radio0)
				{
					Toast.makeText(getApplicationContext(), "Ping interval is set to 15 Mins", Toast.LENGTH_SHORT).show();
					LOCAL_CACHING_INTERVAL_MINUTES=15;					
			         
				}
				else if(arg1==R.id.radio1)
				{
					Toast.makeText(getApplicationContext(), "Ping interval is set to 30 Mins", Toast.LENGTH_SHORT).show();
					LOCAL_CACHING_INTERVAL_MINUTES=30;

				}
				else if(arg1==R.id.radio2)
				{
					Toast.makeText(getApplicationContext(), "Ping interval is set to 60 Mins", Toast.LENGTH_SHORT).show();
					LOCAL_CACHING_INTERVAL_MINUTES=60;

				}
				
				if(appSettings.getBoolean("sync_status", true)==true)
		        {
					stopWatchmanService();
		        	startWatchmanService();
		        }
				prefEditor = appSettings.edit();
				prefEditor.putInt("ping_freq", LOCAL_CACHING_INTERVAL_MINUTES);
				prefEditor.commit();
				
			
			
			}
		});
        
        
        
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
					 prefEditor = appSettings.edit();
					 prefEditor.putBoolean("sync_status", false);
					 prefEditor.commit();
					 
					 syncImageView.setImageResource(R.drawable.sync_image_off);
					// syncOnOffButton.refreshDrawableState();
					 HelperMethods.log("OFF");
					 stopWatchmanService();
				 }
				 else
				 {
					 prefEditor = appSettings.edit();
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
