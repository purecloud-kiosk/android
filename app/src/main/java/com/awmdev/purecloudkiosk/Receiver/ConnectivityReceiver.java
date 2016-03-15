package com.awmdev.purecloudkiosk.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.awmdev.purecloudkiosk.Services.CheckInService;

public class ConnectivityReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //check to see if an internet connection has been made
        if(intent.getAction() == ConnectivityManager.CONNECTIVITY_ACTION)
        {
            //grab the connectivity service to see if we have a connection
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            //check the connection status
            if(activeNetwork != null && activeNetwork.isConnectedOrConnecting())
            {
                //restart the service with the intent to process old check ins
                Intent serviceIntent = new Intent(context, CheckInService.class);
                //add the intent extra for starting from a unbinded state
                serviceIntent.putExtra("broadcast",true);
                //start the service
                context.startService(serviceIntent);
            }
        }
    }
}
