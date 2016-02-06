package com.awmdev.purecloudkiosk.Presenter;

import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.View.Interfaces.LoginViewInterface;
import com.awmdev.purecloudkiosk.View.Interfaces.OnLoginFinishedListener;
import com.awmdev.purecloudkiosk.Verifier.LoginVerifier;

public class LoginPresenter implements OnLoginFinishedListener
{
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
    public void onLoginSuccessful(JSONDecorator jsonDecorator)
    {
        loginViewInterface.navigateToEventList(jsonDecorator.getString("X-OrgBook-Auth-Key"));
    }

    @Override
    public void setOrganizationWrapperVisibility(int visibility)
    {
        loginViewInterface.setOrganizationWrapperVisibility(visibility);
    }
}
