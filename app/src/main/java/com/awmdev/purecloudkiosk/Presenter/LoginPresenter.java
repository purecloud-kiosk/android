package com.awmdev.purecloudkiosk.Presenter;

import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.View.Activity.LoginActivity;
import com.awmdev.purecloudkiosk.View.Fragment.LoginFragment;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginPresenter
{
    private LoginFragment loginFragment;

    public LoginPresenter(LoginFragment loginFragment)
    {
        this.loginFragment = loginFragment;
    }

    public void validateCredentials(String username, String password)
    {
        //check to see if the username/password fields contain data
        if(!TextUtils.isEmpty(username)&& !TextUtils.isEmpty(password))
        {
            //remove the old error from the view
            loginFragment.removeErrorText();
            //fields contain data, send http login request
            sendHttpLoginRequest(username, password);
        }
        else
        {
            if(TextUtils.isEmpty(username) && TextUtils.isEmpty(password))
            {
                //Username and password are both empty, set appropriate error
                loginFragment.setError(R.string.login_error_missing_fields);
            }
            else
            {
                if(TextUtils.isEmpty(username))
                {
                    //username is empty, set username error
                    loginFragment.setError(R.string.login_error_missing_username);
                }
                else
                {
                    //only error left is the missing password field
                    loginFragment.setError(R.string.login_error_missing_password);
                }
            }
        }
    }

    public void sendHttpLoginRequest(String email,String password)
    {
        //Create an instance of the httprequester
        HttpRequester httpRequester = HttpRequester.getInstance(loginFragment.getActivity().getApplicationContext());
        //Create a callback for the json response
        Response.Listener<JSONObject>  callback = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    //grab the authentication token from the response
                    String authenticationToken = response.getJSONObject("res").get("X-OrgBook-Auth-Key").toString();
                    //switch to the next activity
                    ((LoginActivity)loginFragment.getActivity()).onLoginSuccessful(authenticationToken);
                }
                catch (JSONException e)
                {
                    loginFragment.setError(R.string.login_error_invalid);
                }
            }

        };
        //Create a callback for the error response
        Response.ErrorListener errorCallback = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                NetworkResponse networkResponse = error.networkResponse;
                if(networkResponse != null && networkResponse.statusCode == HttpStatus.SC_FORBIDDEN)
                    loginFragment.setError(R.string.login_error_invalid);
                else
                    loginFragment.setError(R.string.login_error_server_timeout);
            }
        };
        //send the request to volley
        httpRequester.sendHttpLoginRequest(email,password,callback,errorCallback);
    }


}
