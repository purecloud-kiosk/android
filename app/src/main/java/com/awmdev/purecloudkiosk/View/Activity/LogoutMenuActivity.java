package com.awmdev.purecloudkiosk.View.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.awmdev.purecloudkiosk.R;

/**
 * Created by Reese on 3/17/2016.
 */
public class LogoutMenuActivity extends AppCompatActivity
{
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.log_out_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.menu.log_out_menu)
        {
            //remove the old token
            SharedPreferences sharedPreferences = getSharedPreferences("authenticationPreference", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("authenticationTokenSaved",false);
            editor.apply();
            //create the intent
            Intent intent = new Intent(getApplicationContext(),LauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            //start the activity
            startActivity(intent);
            //finish the current activity
            finish();
            //return true since we handled this action
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
