/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.droidhack.droidjuice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast; 

/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application.  The {@link LocalServiceActivities.Controller}
 * and {@link LocalServiceActivities.Binding} classes show how to interact with the
 * service.
 *
 * <p>Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service.  This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */

public class LocalService extends Service {
   //private NotificationManager mNM;
    
    
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
		      
		      JSONObject singleBatteryInfo=new JSONObject();
		      
		      
		      
		     // Toast.makeText(getApplicationContext(), displayMessage, Toast.LENGTH_SHORT).show();
		      
		      
		      
		      unBindBroadcastService();
		       
		}
	  };
	  
	public void unBindBroadcastService()
	{
		this.stopSelf();
	}

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        LocalService getService() {
            return LocalService.this;
        }
    }
    
    @Override
    public void onCreate() {
        //mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
       // showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        
        registerReceiver(mBatInfoReceiver, 
        	    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
    	unregisterReceiver(mBatInfoReceiver);
    	
        // Cancel the persistent notification.
       // mNM.cancel(R.string.alarm_service_started);

        // Tell the user we stopped.
        //Toast.makeText(this, R.string.alarm_service_finished, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
//    private void showNotification() {
//        // In this sample, we'll use the same text for the ticker and the expanded notification
//        CharSequence text = getText(R.string.alarm_service_started);
//
//        // Set the icon, scrolling text and timestamp
//        Notification notification = new Notification(R.drawable.ic_action_search, text,
//                System.currentTimeMillis());
//
//        // The PendingIntent to launch our activity if the user selects this notification
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, SettingsActivity.class), 0);
//
//        // Set the info for the views that show in the notification panel.
//        notification.setLatestEventInfo(this, getText(R.string.local_service_label),
//                       text, contentIntent);
//
//        // Send the notification.
//        // We use a layout id because it is a unique number.  We use it later to cancel.
//        mNM.notify(R.string.alarm_service_started, notification);
//    }
    
    
    
	private class SendBatteryDataToServer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {
            
        	
        	 String requestURL = "http://droidjuice.me/api.php?q=create_user&data="+URLEncoder.encode(arg0[0]);
             
        	 HelperMethods.log("Final URL: "+requestURL);
//             HttpClient httpclient = new DefaultHttpClient();
//             HttpResponse response;
//
//             try {
//                 response = httpclient.execute(new HttpGet(requestURL));
//                 StatusLine statusLine = response.getStatusLine();
//                 if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
//                     ByteArrayOutputStream out = new ByteArrayOutputStream();
//                     response.getEntity().writeTo(out);
//                     out.close();
//                     String responseString = out.toString().trim();
//                     
//                     HelperMethods.log("RESPONSE: " + responseString);
//                     
//                     JSONObject responseJSON=null;
// 					 String responseStatus=null;
// 					 //String responseUserID=null;
//                     
//						try {
//							responseJSON = new JSONObject(responseString);
//							responseStatus=responseJSON.getString("response");
//							//responseUserID=responseJSON.getString("user_id");
//							
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						
//						 
//					
//						return responseStatus;
//                    
//                   
//
//                 } else {
//                     response.getEntity().getContent().close();
//
//                     HelperMethods.log("Data : Exception1");
//                 }
//             } catch (ClientProtocolException e) {
//            	 HelperMethods.log("Data : Exception2 "+e.getMessage());
//             } catch (IOException e) {
//            	 HelperMethods.log("Data : Exception3 "+e.getMessage());
//             }  
        	
        	
            return null;
        }

        @Override
        protected void onPostExecute(String responseStatus) {
            super.onPostExecute(responseStatus);
              
           
//             if(responseStatus.equals("-1"))
//             {
// 	           // HelperMethods.log("Twitter ID Taken");
//  	            Toast.makeText(getApplicationContext(), "Oops, this Droid Juice handle is already taken :(", Toast.LENGTH_LONG).show();
//             }
//             else if (responseStatus.equals("0"))
//             {
//   	            Toast.makeText(getApplicationContext(), "Oops, something went wrong :( \n Check your network connection!", Toast.LENGTH_LONG).show();
//             }
//             else 
//             {
// 	            //HelperMethods.log("Data sent: Success");
// 	            Toast.makeText(getApplicationContext(), "Profile Created", Toast.LENGTH_SHORT).show();
// 	            
// 	            prefEditor.putString("user_name", responseStatus);
// 	            prefEditor.commit();
// 	            
// 	            Intent settingsPageIntent=new Intent(getApplicationContext(), SettingsActivity.class);
// 	            settingsPageIntent.putExtra("first_use", true);
// 	        	startActivity(settingsPageIntent);
// 	        	
// 	        	finish();
// 	            
//             }
             
        }
 

    }
}

