package com.awmdev.purecloudkiosk.Model;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Reese on 12/21/2015.
 */
public class HttpRequester
{
    private RequestQueue requestQueue;

    public HttpRequester(Context context)
    {
       requestQueue = Volley.newRequestQueue(context);
    }

    public void sendHttpLoginRequest(String email,String password, Response.Listener<JSONObject> callback,Response.ErrorListener errorCallback)
    {
        //URL for the request
        String url = "http://charlie-duong.com:8000/purecloud/login";
        //Create a map for the json request
        Map<String,String> jsonMap = new HashMap<>();
        jsonMap.put("email",email);
        jsonMap.put("password",password);
        JSONObject jsonObject = new JSONObject(jsonMap);
        //Request a json response from the provided url
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject,callback,errorCallback);
        //Add the request to the volley queue to be executed
        requestQueue.add(jsonRequest);
    }

    public void sendEventDataRequest(final String authKey, Response.Listener<JSONArray> callback, Response.ErrorListener errorCallback)
    {
        //URL for the request
        String url = "http://charlie-duong.com:8000/events/managing";
        //add the auth token to the header
        JsonArrayRequest postRequest = new JsonArrayRequest(Request.Method.GET,url,callback,errorCallback)
        {
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError
            {
                Map<String,String> authHeader = new HashMap<>();
                authHeader.put("Authorization","bearer "+authKey);
                return authHeader;
            }
        };
        requestQueue.add(postRequest);
    }

    public void sendAmazonBucketRequest(String url, Response.Listener<String> callback, Response.ErrorListener errorCallback)
    {

    }
}
