package com.awmdev.purecloudkiosk.View.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.View.Fragment.DetailedEventFragment;
import com.awmdev.purecloudkiosk.View.Interfaces.LaunchKioskInterface;

public class EventListActivity extends AppCompatActivity implements LaunchKioskInterface
{
    private Toolbar activityToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //call the super class
        super.onCreate(savedInstanceState);
        //check device size to see if you need request landscape only
        if(isDeviceXtraLarge(getApplicationContext()))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //Set the content view which contains the frame for the fragments
        setContentView(R.layout.activity_event_list);
        //grab the toolbar from the view
        activityToolbar = (Toolbar) findViewById(R.id.activity_event_list_toolbar);
        //assign the toolbar to the activity
        setSupportActionBar(activityToolbar);
    }

    private boolean isDeviceXtraLarge(Context context)
    {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public void onEventItemSelected(Parcelable jsonEventParcelable)
    {
        //try and grab the fragment
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.detailed_fragment);
        //check to see if the fragment exists, if not start another activity
        if(fragment == null || !fragment.isInLayout())
        {
            //create the intent for the new activity
            Intent intent = new Intent(getApplicationContext(),DetailedEventActivity.class);
            intent.putExtra("parcelable", jsonEventParcelable);
            //start the activity
            startActivity(intent);
        }
        else
        {
            //send the data to the other fragment
            ((DetailedEventFragment)(fragment)).assignDataToView(jsonEventParcelable);
        }
    }

    @Override
    public void onLaunchKioskSelected(Parcelable jsonEventParcelable)
    {
        if(jsonEventParcelable != null)
        {
            //create the intent and pass the json object associated with the event
            Intent intent = new Intent(getApplicationContext(), KioskActivity.class);
            intent.putExtra("parcelable", jsonEventParcelable);
            //start the activity
            startActivity(intent);
        }
    }



}
