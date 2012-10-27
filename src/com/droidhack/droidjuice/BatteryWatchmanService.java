package com.droidhack.droidjuice;
 

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.widget.Toast;

public class BatteryWatchmanService extends Service{

//	@Override
//	public IBinder onBind(Intent arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	
   // NotificationManager mNM;

    @Override
    public void onCreate() {
       // mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // show the icon in the status bar
       // showNotification();

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.
    	HelperMethods.log("OnCreate of the service");
        Thread thr = new Thread(null, mTask, "AlarmService_Service");
        thr.start();
    }

    @Override
    public void onDestroy() {
        // Cancel the notification -- we use the same ID that we had used to start it
        //mNM.cancel(R.string.alarm_service_started);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.alarm_service_finished, Toast.LENGTH_SHORT).show();
    }

    /**
     * The function that runs in our worker thread
     */
    Runnable mTask = new Runnable() {
        public void run() {
//            // Normally we would do some work here...  for our sample, we will
//            // just sleep for 30 seconds.
//            long endTime = System.currentTimeMillis() + 15*1000;
//            while (System.currentTimeMillis() < endTime) {
//                synchronized (mBinder) {
//                    try {
//                        mBinder.wait(endTime - System.currentTimeMillis());
//                    } catch (Exception e) {
//                    }
//                }
//            }
//
//            // Done with our work...  stop the service!
//            BatteryWatchmanService.this.stopSelf();
        	HelperMethods.log("Inside Runnable");
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Show a notification while this service is running.
     */
//    private void showNotification() {
//        // In this sample, we'll use the same text for the ticker and the expanded notification
//        CharSequence text = getText(R.string.alarm_service_started);
//
//        // Set the icon, scrolling text and timestamp
//        Notification notification = new Notification(R.drawable.stat_sample, text,
//                System.currentTimeMillis());
//
//        // The PendingIntent to launch our activity if the user selects this notification
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, AlarmService.class), 0);
//
//        // Set the info for the views that show in the notification panel.
//        notification.setLatestEventInfo(this, getText(R.string.alarm_service_label),
//                       text, contentIntent);
//
//        // Send the notification.
//        // We use a layout id because it is a unique number.  We use it later to cancel.
//        mNM.notify(R.string.alarm_service_started, notification);
//    }

    /**
     * This is the object that receives interactions from clients.  See RemoteService
     * for a more complete example.
     */
    private final IBinder mBinder = new Binder() {
        @Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
		        int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
    };

}
