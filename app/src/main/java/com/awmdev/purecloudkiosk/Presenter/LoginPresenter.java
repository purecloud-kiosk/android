package com.awmdev.purecloudkiosk.Presenter;

import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.View.Fragment.LoginFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Reese on 11/23/2015.
 */
public class LoginPresenter implements LoginPresenterInterface
{
    private LoginFragment loginFragment;

    public LoginPresenter(LoginFragment loginFragment)
    {
        this.loginFragment = loginFragment;
    }

    @Override
    public void validatingUserField(String username)
    {

    }

    @Override
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

    @Override
    public void sendHttpLoginRequest(String email,String password)
    {
        //Create a request queue for volley and pass the application context
        RequestQueue queue = Volley.newRequestQueue(loginFragment.getActivity());
        //URL for the request
        String url = "http://charlie-duong.com:8000/purecloud/login";
        //Create a map for the json request
        Map<String,String> jsonMap = new HashMap<>();
        jsonMap.put("email",email);
        jsonMap.put("password",password);
        JSONObject jsonObject = new JSONObject(jsonMap);
        //Request a json response from the provided url
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    System.out.println(response.getJSONObject("res").get("X-OrgBook-Auth-Key").toString());
                    loginFragment.setError(R.string.login_successful);
                }
                catch (JSONException e)
                {
                    loginFragment.setError(R.string.login_error_invalid);
                }
            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error)
            {
                loginFragment.setError(R.string.login_error_server_timeout);
            }
        });
        //Add the request to the volley queue to be executed
        queue.add(jsonRequest);
    }


}
