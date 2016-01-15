package com.awmdev.purecloudkiosk.View.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.awmdev.purecloudkiosk.R;

public class DetailedEventActivity extends AppCompatActivity implements View.OnClickListener
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //pass to the super class
        super.onCreate(savedInstanceState);
        //set the layout for the activity
        setContentView(R.layout.activity_detailed_event);
        //set the support toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_detailed_event_toolbar);
        //set the support toolbar
        setSupportActionBar(toolbar);
        //set the toolbar to have a back navigation button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //add a listener to the toolbar back button
        toolbar.setNavigationOnClickListener(this);
        //remove the title bar from the toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onClick(View v)
    {
        //navigate to the last activity
        onBackPressed();
    }
}

