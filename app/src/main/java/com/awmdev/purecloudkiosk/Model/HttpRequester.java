package com.awmdev.purecloudkiosk.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Reese on 12/21/2015.
 */

/*
    Known Issues:
        -Singleton Class should be changed to create and grab instance instead of only grabbing instance and pass a null context.
        -Grab Images from an amazon bucket doesn't have an error image nor does it a default image for volley
        -malformed url throw exceptions, not sure to verify with apache or have them verified server side?
    Notes:
        -Volley LRU cache is set small to help with the loading of duplicate images, not to be used to store all of the images
        for the events. Those are stored in the JSONEventWrapper, so duplicate calls to volley are not required to load an image
        for an event again. This keeps images associated with the event and not associated with volley.
 */

public class HttpRequester
{
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static HttpRequester httpRequester;

    private HttpRequester(Context context)
    {
        //create the request queue
        requestQueue = Volley.newRequestQueue(context);
        //create the image loader
        imageLoader = new ImageLoader(requestQueue,new ImageLoader.ImageCache()
        {
            private final LruCache<String, Bitmap> cache = new LruCache<>(5);

            @Override
            public Bitmap getBitmap(String url)
            {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap)
            {
                cache.put(url,bitmap);
            }
        });
    }

    public static synchronized HttpRequester getInstance(Context context)
    {
        return httpRequester == null ? httpRequester = new HttpRequester(context) : httpRequester;
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

    public void sendAmazonBucketRequest(final String url, ImageView imageView,int maxWidth, int maxHeight)
    {
        imageLoader.get(url, ImageLoader.getImageListener(imageView, 0, 0), maxWidth, maxHeight);
    }
}
