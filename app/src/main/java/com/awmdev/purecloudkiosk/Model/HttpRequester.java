package com.awmdev.purecloudkiosk.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpRequester
{
    private final String TAG = HttpRequester.class.getSimpleName();
    private static HttpRequester httpRequester;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private Request currentRequest;

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

    public void sendHttpLoginRequest(String email,String password,String organization, Response.Listener<JSONObject> callback,Response.ErrorListener errorCallback)
    {
        //URL for the request
        String url = "https://apps.mypurecloud.com/api/v2/login";
        //Create a map for the json request
        Map<String,String> jsonMap = new HashMap<>();
        jsonMap.put("email",email);
        jsonMap.put("password", password);
        jsonMap.put("orgName",organization);
        JSONObject jsonObject = new JSONObject(jsonMap);
        //Request a json response from the provided url
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject,callback,errorCallback);
        //Add the request to the volley queue to be executed
        requestQueue.add(jsonRequest);
    }

    public void sendEventDataRequest(final String authKey, Response.Listener<JSONArray> callback, Response.ErrorListener errorCallback,String pageNumber)
    {
        //URL for the request
        String url = String.format("http://www.charlie-duong.xyz:8080/events/managing?limit=25&page=%1$s",pageNumber);
        //create the request
        Request request = createJsonArrayRequest(url, authKey, callback, errorCallback);
        //cancel the current request, if it exists
        if(currentRequest != null)
            requestQueue.cancelAll(currentRequest);
        //send the request
        requestQueue.add(request);
    }

    public void sendEventDataSearchRequest(final String authKey,Response.Listener<JSONArray> callback, Response.ErrorListener errorCallback,String pageNumber, String searchRequest)
    {
        //construct the url
        String url = String.format("http://www.charlie-duong.xyz:8080/events/searchEvents?managing=true&limit=25&page=%1$s&query=%2$s",pageNumber,searchRequest);
        //create the request
        currentRequest = createJsonArrayRequest(url, authKey, callback, errorCallback);
        //cancel the current request, if it exists
        if(currentRequest != null)
            requestQueue.cancelAll(currentRequest);
        //send the new request
        requestQueue.add(currentRequest);
    }

    public void sendEventCheckInRequest(final String authKey, RequestFuture<JSONObject> future, JSONObject checkIn)
    {
        //create the url
        String url = "http://www.charlie-duong.xyz:8080/events/bulkCheckIn";
        //create the post request to handle future
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,url,checkIn,future,future)
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

    public void sendDownloadImageRequest(String url,Response.Listener<byte[]> callback,Response.ErrorListener errorCallback)
    {
        InputStreamVolleyRequest inputStreamVolleyRequest = new InputStreamVolleyRequest(Request.Method.GET,url,callback,errorCallback);
        requestQueue.add(inputStreamVolleyRequest);
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


    private class InputStreamVolleyRequest extends Request<byte[]>
    {
        private final Response.Listener<byte[]> listener;

        public InputStreamVolleyRequest(int method,String url,Response.Listener<byte[]> listener,
                                        Response.ErrorListener errorListener)
        {
            //call the super
            super(method, url, errorListener);
            //enable caching
            setShouldCache(true);
            //save the listener
            this.listener = listener;
        }

        @Override
        protected Response<byte[]> parseNetworkResponse(NetworkResponse response)
        {
            return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
        }

        @Override
        protected void deliverResponse(byte[] response)
        {
            listener.onResponse(response);
        }
    }
}
