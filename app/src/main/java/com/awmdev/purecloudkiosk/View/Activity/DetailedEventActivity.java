package com.awmdev.purecloudkiosk.View.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.awmdev.purecloudkiosk.R;

public class DetailedEventActivity extends AppCompatActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //pass to the super class
        super.onCreate(savedInstanceState);
        //set the layout for the activity
        setContentView(R.layout.activity_detailed_event);
    }
}

