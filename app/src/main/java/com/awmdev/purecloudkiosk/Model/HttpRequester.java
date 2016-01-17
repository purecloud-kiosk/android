package com.awmdev.purecloudkiosk.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

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

/*
    Known Issues:
        -Singleton Class should be changed to create and grab instance instead of only grabbing instance and pass a null context.
        -malformed url throw exceptions, not sure to verify with apache or have them verified server side?
 */

public class HttpRequester
{
    private RequestQueue singleThreadedRequestQueue;
    private static HttpRequester httpRequester;
    private Request currentSearchRequest;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;


    private HttpRequester(Context context)
    {
        //create the default request queue for all request but searching
        requestQueue = Volley.newRequestQueue(context);
        //create the image loader
        imageLoader = new ImageLoader(requestQueue,new ImageLoader.ImageCache()
        {
            private final LruCache<String, Bitmap> cache = new LruCache<>(30);

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
        jsonMap.put("password", password);
        JSONObject jsonObject = new JSONObject(jsonMap);
        //Request a json response from the provided url
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject,callback,errorCallback);
        //Add the request to the volley queue to be executed
        requestQueue.add(jsonRequest);
    }

    public void sendEventDataRequest(final String authKey, Response.Listener<JSONArray> callback, Response.ErrorListener errorCallback,String pageNumber)
    {
        //URL for the request
        String url = String.format("http://charlie-duong.com:8000/events/managing?limit=25&page=%1$s",pageNumber);
        //create the request
        Request request = createJsonArrayRequest(url, authKey, callback, errorCallback);
        //send the request
        requestQueue.add(request);
    }

    public void sendEventDataSearchRequest(final String authKey,Response.Listener<JSONArray> callback, Response.ErrorListener errorCallback,String pageNumber, String searchRequest)
    {
        //construct the url
        String url = String.format("http://charlie-duong.com:8000/events/searchManagedEvents?limit=25&page=%1$s&q=%2$s",pageNumber,searchRequest);
        //cancel the current request, if it exists
        if(currentSearchRequest != null)
            requestQueue.cancelAll(currentSearchRequest);
        //create the request
        currentSearchRequest = createJsonArrayRequest(url, authKey, callback, errorCallback);
        //send the new request
        requestQueue.add(currentSearchRequest);
    }

    private Request createJsonArrayRequest(final String url, final String authKey, Response.Listener<JSONArray> callback, Response.ErrorListener errorCallback)
    {
        //add the auth token to the header
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET,url,callback,errorCallback)
        {
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError
            {
                Map<String,String> authHeader = new HashMap<>();
                authHeader.put("Authorization","bearer "+authKey);
                return authHeader;
            }
        };
        return getRequest;
    }

    public ImageLoader getImageLoader()
    {
        return imageLoader;
    }
}
