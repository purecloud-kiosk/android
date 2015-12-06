package com.awmdev.purecloudkiosk.Presenter;

/**
 * Created by Reese on 11/23/2015.
 */
public interface LoginPresenterInterface
{
    public void validatingUserField(String username);

    public void validateCredentials(String username, String password);

    public void sendHttpLoginRequest(String email,String password);
}
