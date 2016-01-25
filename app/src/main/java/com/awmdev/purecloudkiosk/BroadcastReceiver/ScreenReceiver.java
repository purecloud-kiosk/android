package com.awmdev.purecloudkiosk.BroadcastReceiver;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.PowerManager;

public class ScreenReceiver extends BroadcastReceiver implements SensorEventListener
{
    private final IntentFilter intentFilter;
    private SensorManager sensorManager;
    private Context context;
    private Sensor sensor;

    public ScreenReceiver(Context context)
    {
        //create the intent filter for the broadcast receiver
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        //save the context locally for the use of the trigger
        this.context = context;
        //get the sensor manager
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        System.out.println("Sensor value: "+ sensor );
    }

    public void registerReceiver()
    {
        context.registerReceiver(this, intentFilter);
    }

    public void removeReceiver()
    {
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction() == Intent.ACTION_SCREEN_OFF)
        {
            //start the trigger for proximity
            sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
        else
        {
            //remove the trigger since the device is awake
            sensorManager.unregisterListener(this);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event)
    {
        System.out.println("EVENT Accurarcy: "+ event.accuracy);
        System.out.println("Event: "+ event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        System.out.println("Accuracy: "+ accuracy);
    }

    /*
            //grab the power manager and full wake lock. Unfortunately there's not a better way to
            //wake the device and turn the screen on
            PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "Kiosk WakeLock");
            //grab the wakelock and then release.
            wakeLock.acquire(500);
     */
}
