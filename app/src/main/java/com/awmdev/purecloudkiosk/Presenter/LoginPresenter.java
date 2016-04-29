package com.awmdev.purecloudkiosk.Presenter;

import android.util.Log;

import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.View.Interfaces.LoginViewInterface;
import com.awmdev.purecloudkiosk.Verifier.OnLoginFinishedListener;
import com.awmdev.purecloudkiosk.Verifier.LoginVerifier;

import org.json.JSONException;

public class LoginPresenter implements OnLoginFinishedListener
{
    private final String TAG = LoginPresenter.class.getSimpleName();
    private LoginViewInterface loginViewInterface;
    private LoginVerifier loginVerifier;

    public LoginPresenter(LoginViewInterface loginViewInterface)
    {
        //pass the fragment instance to the presenter
        this.loginViewInterface = loginViewInterface;
        //create a login verifier
        loginVerifier = new LoginVerifier(this);
    }

    public void validateCredentials(String username, String password,String organization)
    {
        loginVerifier.validateCredentials(username,password,organization);
    }

    @Override
    public void setError(int resID)
    {
        loginViewInterface.setError(resID);
    }

    @Override
    public void removeError()
    {
        loginViewInterface.removeError();
    }

    @Override
    public void onLoginSuccessful(JSONDecorator jsonDecorator, String organization)
    {
        //check to see if the organization field is empty
        if(organization.isEmpty())
        {
            //since organization is empty , they belong to only one organization.
            try
            {
                 // Grab the organization
                 organization = jsonDecorator.getJSONObject("org").getJSONObject("general")
                        .getJSONArray("shortName").getJSONObject(0).getString("value");
            }
            catch(JSONException ex)
            {
                //error grab the required values, log error and return
                Log.d(TAG,"Unable to grab the organization, ex follows: " + ex);
                //return without calling the navigation
                return;
            }
        }
        //navigate to the event list
        loginViewInterface.navigateToEventList(jsonDecorator.getString("X-OrgBook-Auth-Key"),organization);

    }

    @Override
    public void setOrganizationWrapperVisibility(int visibility)
    {
        loginViewInterface.setOrganizationWrapperVisibility(visibility);
    }
}
