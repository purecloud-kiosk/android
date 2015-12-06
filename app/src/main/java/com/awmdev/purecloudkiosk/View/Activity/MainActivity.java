package com.awmdev.purecloudkiosk.View.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.View.Fragment.LoginFragment;


/*
    This is the first attempt at model view presenter, as far as I understand all view code such as fragments should remain in
    the view and not be moved to the presenter layer or model. This should include the code required for the changing of the
    fragments, im not sure if saving and restoring should be moved to the model layer.
 */

public class MainActivity extends AppCompatActivity
{
    //global variable for fragment, to be used for instance state saving
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Call the super class
        super.onCreate(savedInstanceState);
        //Set the content view which contains the frame for the fragments
        setContentView(R.layout.activity_main);
        //check the bundle to see if its null
        if(savedInstanceState == null)
            selectFragment(R.layout.fragment_login);
        else
            restoreFragment(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        //Call the super method
        super.onSaveInstanceState(outState);
        //Save the fragment instance to the bundle
        getSupportFragmentManager().putFragment(outState, "saved_fragment", currentFragment);
    }

    public void selectFragment(int fragmentId)
    {
        //Grab the Fragment Manager From The Activity
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        //Choose The Fragment Based On The Input
        switch(fragmentId)
        {
            case R.layout.fragment_login:
                ft.replace(R.id.main_container,currentFragment = new LoginFragment());
                break;
        }
        //Commit The Changes
        ft.commit();
    }

    private void restoreFragment(Bundle savedInstanceState)
    {
        //Grab the support fragment manager and fragment transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        //grab the instance state from the bundle
        currentFragment = fragmentManager.getFragment(savedInstanceState,"saved_fragment");
        //check to see if the restored fragment is not null
        if(currentFragment != null)
            ft.add(R.id.main_container,currentFragment);
        //no fragment was saved to the bundle, restore to login
        else
            selectFragment(R.layout.fragment_login);
    }
}
