package com.awmdev.purecloudkiosk.View.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.R;

public class LoginActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Call the super class
        super.onCreate(savedInstanceState);
        //Set the content view which contains the login fragment
        setContentView(R.layout.activity_login);
        //create the instance of volley
        HttpRequester.getInstance(getApplicationContext());
    }

    public void onLoginSuccessful(String authenticationKey)
    {
        //switch to the next activity
        Intent intent = new Intent(getApplicationContext(),EventListActivity.class);
        intent.putExtra("authenticationToken",authenticationKey);
        startActivity(intent);
    }
}
