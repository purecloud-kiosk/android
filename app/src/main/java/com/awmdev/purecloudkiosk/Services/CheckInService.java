package com.awmdev.purecloudkiosk.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;

import com.awmdev.purecloudkiosk.R;

import java.util.Map;

public class CheckInService extends Service implements CheckInRunnableListener
{
    //variables
    private static final String TAG = CheckInService.class.getSimpleName();
    private InternetReceiver internetReceiver = new InternetReceiver();
    private CheckInBinder checkInBinder = new CheckInBinder();
    private NotificationManager notificationManager;
    private final int ONGOING_NOTIFICATION_ID = 1;
    private CheckInRunnable checkInRunnable;
    private boolean bounded = false;
    private Thread checkInThread;

    @Override
    public void onCreate()
    {
        //create the runnable
        checkInRunnable = new CheckInRunnable(this);
        //add the listener to the runnable
        checkInRunnable.addRunnableFinishedListener(this);
        //create the thread
        checkInThread = new Thread(checkInRunnable);
        //grab the notification manager
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //start the service in the foreground
        startForeground(ONGOING_NOTIFICATION_ID,buildNotification(R.string.notification_waiting_data));
        //create the broadcast receiver
        registerReceiver(internetReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //check to see if the broadcast receiver started the service
        if(intent.getExtras().getBoolean("broadcast"))
        {
            if(checkInThread.getState() == Thread.State.TERMINATED || !checkInThread.isAlive())
            {
                checkInThread = new Thread(checkInRunnable);
                checkInThread.start();
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        //set the bounded status to true
        checkInRunnable.updateBoundedStatus(true);
        //set the local bounded variable to true
        bounded = true;
        //start the thread
        if(!checkInThread.isAlive())
        {
            checkInThread = new Thread(checkInRunnable);
            checkInThread.start();
        }
        //return the binder
        return checkInBinder;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        //updated the bounded status on the thread
        checkInRunnable.updateBoundedStatus(false);
        //set bounded to false as the service is no longer attached
        bounded = false;
        //return true so rebind will be called
        return true;
    }

    @Override
    public void onRebind(Intent intent)
    {
        //set the local bounded variable to true
        bounded = true;
        //update the bounded status
        checkInRunnable.updateBoundedStatus(true);
        //check to see if the thread needs to be restarted
        if(!checkInThread.isAlive())
        {
            checkInThread = new Thread(checkInRunnable);
            checkInThread.start();
        }
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(internetReceiver);
        super.onDestroy();
    }

    @Override
    public void onRunnableFinished()
    {
        //just make sure someone didn't connect as were finishing the runnable
        if(!bounded)
            //stop the service
            stopSelf();
    }

    @Override
    public void onStateChange(int stringID)
    {
        Notification notification = buildNotification(stringID);
        notificationManager.notify(ONGOING_NOTIFICATION_ID,notification);
    }

    public void postCheckIn(Map<String,Object> checkInMap)
    {
        //add the check in to the queue for the producer to add to the database
        checkInRunnable.postCheckIn(checkInMap);
    }

    public Notification buildNotification(int stringID)
    {
        Notification.Builder notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentInfo(getResources().getString(stringID));
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            notificationBuilder.setColor(getResources().getColor(R.color.notificationColor));
        return notificationBuilder.build();
    }

    public class CheckInBinder extends Binder
    {
        public CheckInService getService()
        {
            //return the current instance of this service
            return CheckInService.this;
        }
    }

    private class InternetReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //grab the connectivity service to see if we have a connection
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            //check the connection status
            if(activeNetwork != null && activeNetwork.isConnectedOrConnecting())
                checkInRunnable.updateConnectivityState(true);
            else
                checkInRunnable.updateConnectivityState(false);
        }
    }

}
