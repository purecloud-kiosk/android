package com.awmdev.purecloudkiosk.View.Interfaces;

/**
 * Created by Reese on 2/4/2016.
 */
public interface LoginViewInterface
{
    void setOrganizationWrapperVisibility(int visibility);
    void removeError();
    void setError(int resourceID);
    void navigateToEventList(String authenticationToken, String organization);
}
