package com.awmdev.purecloudkiosk.Verifier;

import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;

/**
 * Created by Reese on 2/4/2016.
 */
public interface OnLoginFinishedListener
{
    void setError(int resID);
    void removeError();
    void onLoginSuccessful(JSONDecorator jsonDecorator, String organization);
    void setOrganizationWrapperVisibility(int visibility);
}
