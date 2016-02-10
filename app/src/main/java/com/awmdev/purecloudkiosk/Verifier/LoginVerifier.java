package com.awmdev.purecloudkiosk.Verifier;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.View.Interfaces.OnLoginFinishedListener;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.R;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Reese on 2/4/2016.
 */
public class LoginVerifier
{
    private final String TAG = LoginVerifier.class.getSimpleName();
    private OnLoginFinishedListener listener;

    public LoginVerifier(OnLoginFinishedListener listener)
    {
        this.listener = listener;
    }

    public void validateCredentials(String username, String password,String organization)
    {
        //check to see if the username/password fields contain data
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            //remove the old error from the view
            listener.removeError();
            //fields contain data, send http login request
            onVerificationSuccessful(username, password, organization);
        } else {
            if (TextUtils.isEmpty(username) && TextUtils.isEmpty(password)) {
                //Username and password are both empty, set appropriate error
                listener.setError(R.string.login_error_missing_fields);
            } else {
                if (TextUtils.isEmpty(username)) {
                    //username is empty, set username error
                    listener.setError(R.string.login_error_missing_username);
                } else {
                    //only error left is the missing password field
                    listener.setError(R.string.login_error_missing_password);
                }
            }
        }
    }

    private void onVerificationSuccessful(String username, String password, String organization)
    {
        //Create an instance of the httprequester
        HttpRequester httpRequester = HttpRequester.getInstance(null);
        //Create a callback for the json response
        Response.Listener<JSONObject>  callback = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    listener.onLoginSuccessful(new JSONDecorator(response.getJSONObject("res")));
                }
                catch(JSONException je)
                {
                    Log.d(TAG,"Improperly formatted json, exception follows: " +je);
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
                //we have to do this because purecloud doesn't format their 401 correctly
                if((networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED )
                        || (error.getMessage()!= null && error.getMessage().equalsIgnoreCase("java.io.IOException: No authentication challenges found")))
                {
                    listener.setError(R.string.login_error_invalid);
                }
                else
                {
                    if(networkResponse != null && networkResponse.statusCode == HttpStatus.SC_MULTIPLE_CHOICES)
                    {
                        //change the visibility of the org view
                        listener.setOrganizationWrapperVisibility(View.VISIBLE);
                        //set the error
                        listener.setError(R.string.login_error_missing_organization);
                    }
                    else
                        listener.setError(R.string.login_error_server_timeout);
                }
            }
        };
        //send the request to volley
        httpRequester.sendHttpLoginRequest(username,password,organization,callback,errorCallback);
    }
}
