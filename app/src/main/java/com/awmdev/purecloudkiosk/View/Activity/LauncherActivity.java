package com.awmdev.purecloudkiosk.View.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Reese on 3/15/2016.
 */
public class LauncherActivity extends AppCompatActivity
{
    private final long monthInMilliSecs = 2592000000L;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //grab the shared preferences
        SharedPreferences sharedPreferences = (SharedPreferences) getSharedPreferences("authenticationPreference", Context.MODE_PRIVATE);
        //check to see if a token has been saved
        if(sharedPreferences.getBoolean("authenticationTokenSaved",false))
        {
            //grab the time that token was saved
            long tokenCreationTime = sharedPreferences.getLong("authenticationTimeStamp",0L);
            //grab the currentTime
            long currentTime = System.currentTimeMillis();
            //check to see if the token is still valid
            if((currentTime - tokenCreationTime) < monthInMilliSecs)
            {
                //launch the event list activity since the token is still valid
                Intent intent = new Intent(getApplicationContext(),EventListActivity.class);
                startActivity(intent);
            }
            else
            {
                //launch the login activity with the intent of expired token
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                intent.putExtra("tokenExpired",true);
                startActivity(intent);
            }
        }
        else
        {
            //launch the login activity since there is no token
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        }
    }


}
