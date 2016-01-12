package com.awmdev.purecloudkiosk.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.awmdev.purecloudkiosk.R;

public class EventListActivity extends AppCompatActivity
{
    private Toolbar activityToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //call the super class
        super.onCreate(savedInstanceState);
        //Set the content view which contains the frame for the fragments
        setContentView(R.layout.activity_event_list);
        //grab the toolbar from the view
        activityToolbar = (Toolbar) findViewById(R.id.activity_event_list_toolbar);
        //assign the toolbar to the activity
        setSupportActionBar(activityToolbar);
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
            intent.putExtra("event", jsonEventParcelable);
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
