package com.awmdev.purecloudkiosk.View.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.awmdev.purecloudkiosk.R;

public class LoginActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Call the super class
        super.onCreate(savedInstanceState);
        //Set the content view which contains the frame for the fragments
        setContentView(R.layout.activity_login);
    }

    public void onLoginSuccessful()
    {
        //switch to the next activity
        Intent intent = new Intent(getApplicationContext(),EventListActivity.class);
        startActivity(intent);
    }
}