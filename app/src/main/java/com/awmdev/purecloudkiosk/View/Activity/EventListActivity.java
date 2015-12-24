package com.awmdev.purecloudkiosk.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.awmdev.purecloudkiosk.R;

public class EventListActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //call the super class
        super.onCreate(savedInstanceState);
        //Set the content view which contains the frame for the fragments
        setContentView(R.layout.activity_event_list);
    }

    public void onEventItemSelected(String eventID)
    {
        //try and grab the fragment
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.detailed_fragment);
        //check to see if the fragment exists, if not start another activity
        if(fragment == null || !fragment.isInLayout())
        {
            //create the intent for the new activity
            Intent intent = new Intent(getApplicationContext(),DetailedEventActivity.class);
            intent.putExtra("eventID", eventID);
            //start the activity
            startActivity(intent);
        }
        else
        {
            //send the data to the other fragment

            //TO DO
        }
    }

}
