package com.awmdev.purecloudkiosk.Services;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.Receiver.ConnectivityReceiver;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CheckInRunnable implements Runnable
{
    private Queue<Map<String,Object>> checkInQueue = new ArrayDeque(30);
    private CheckInRunnableListener checkInRunnableListener = null;
    private final String TAG = CheckInRunnable.class.getSimpleName();
    private ConnectivityManager connectivityManager;
    private boolean bounded = false, internet = true;
    private boolean checkInWaiting = false;
    private HttpRequester httpRequester;
    private Database database;
    private View checkInView;
    private Context context;
    private String authKey;
    private Query query;


    public CheckInRunnable(Context context)
    {
        try
        {
            //save the context locally
            this.context = context;
            //initialize couchbase lite
            initializeCouchBaseLite();
            //initialize the sender and get the current status
            initializeCheckInSender();
        }
        catch(CouchbaseLiteException | IOException ex)
        {
            Log.e(TAG, "Unable to create instance of CouchLite Database");
        }
    }

    private void initializeCheckInSender()
    {
        //grab the auth token
        SharedPreferences sharedPreferences =
                context.getSharedPreferences("authenticationPreference", Context.MODE_PRIVATE);
        authKey = sharedPreferences.getString("authenticationToken", "");
        //get an instance of the connectivity manager
        connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //grab the current internet state
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        //check the current state
        if(networkInfo != null && networkInfo.isConnectedOrConnecting())
            updateConnectivityState(true);
        else
            updateConnectivityState(false);
    }

    private void initializeCouchBaseLite() throws CouchbaseLiteException, IOException
    {
        Manager manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
        //grab the database
        database = manager.getDatabase("check_in_database");
        //grab an instance of the HTTP Requester
        httpRequester = HttpRequester.getInstance(context);
        //grab the view from the database
        checkInView = database.getView("check_in_view");
        //create the mapper for the view
        checkInView.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                //grab all of our content from the doc
                Map<String, Object> map = new HashMap();
                map.put("eventID", document.get("eventID"));
                //grab the check in map from the doc
                map.put("checkIn", document.get("checkIn"));
                emitter.emit("checkIn", map);
            }
        }, "1.1.2");
        //create the query for the navigating the results
        query = checkInView.createQuery();
        //set the query size for fifty, since it is the max post size
        query.setLimit(50);
    }

    @Override
    public void run()
    {
        //disable the broadcast receiver in the manifest since were running
        setBroadcastReceiverState(false);
        //set the notification
        notifyStateChange(R.string.notification_processing);
        //check to see if we should continue running
        while(determineState())
        {
            //consume the check in from the queue and add them to the database
            processCheckIn();
            //send the check in to the server
            sendCheckInToServer();
        }
        //notify the service to stop its self
        notifyFinishListener();
    }

    private synchronized boolean determineState()
    {
        //check to see if the thread should be awake, waiting for state change or dying since it is
        //no longer needed
        if (bounded)
        {
            if (getDataCount() <= 0)
            {
                while (bounded && getDataCount() <= 0)
                {
                    Log.i(TAG,"Thread Waiting For Data While In Bounded State");
                    notifyStateChange(R.string.notification_waiting_data);
                    waitThread();
                }
            }
            else
            {
                if (!internet)
                {
                    while (bounded && !internet && checkInQueue.isEmpty())
                    {
                        Log.i(TAG,"Thread Waiting For Internet While In Bounded State");
                        notifyStateChange(R.string.notification_waiting_internet);
                        waitThread();
                    }
                }
            }
        }
        else
        {
            if (getDataCount() <= 0)
            {
                Log.i(TAG,"Thread Finishing Since Unbounded And Contains No Data");
                return false;
            }
            else
            {
                if(!internet && checkInQueue.isEmpty())
                {
                    Log.i(TAG, "Thread Finishing, Starting Broadcast Receiver");
                    //enable the broadcast receiver for when there is internet
                    setBroadcastReceiverState(true);
                    //stop the thread
                    return false;
                }

            }
        }
        notifyStateChange(R.string.notification_processing);
        return true;
    }

    private void waitThread()
    {
        try
        {
            wait();
        }
        catch (InterruptedException ex)
        {
            Log.e(TAG, "Thread interrupted while waiting for state change");
        }
    }

    private void setBroadcastReceiverState(boolean enable)
    {
        //grab the package manager
        PackageManager pm = context.getPackageManager();
        //grab the receiver
        ComponentName receiver = new ComponentName(context, ConnectivityReceiver.class);
        if(enable)
        {
            //enable the broadcast receiver in the manifest
            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                   PackageManager.DONT_KILL_APP);
        }
        else
        {
            //disable the broadcast receiver in the manifest
            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                   PackageManager.DONT_KILL_APP);
        }
    }

    private synchronized int getDataCount()
    {
        return checkInView.getTotalRows() + checkInQueue.size();
    }

    public synchronized void updateBoundedStatus(boolean status)
    {
        //update the bounded status
        bounded = status;
        //notify the thread of the status change
        notifyAll();
    }

    public synchronized void updateConnectivityState(boolean state)
    {
        //update the internet state
        internet = state;
        //notify the thread of the change
        notifyAll();
    }

    public synchronized void postCheckIn(Map<String,Object> checkInMap)
    {
        //add the check in to the map
        checkInQueue.add(checkInMap);
        //set check posted to true so the thread will process it
        checkInWaiting = true;
        //notify the thread of the new data
        notifyAll();
    }

    private synchronized void processCheckIn()
    {
        //add all of the check in's to the database
        while (!checkInQueue.isEmpty())
        {
            try
            {
                //grab the check in from the queue
                Map<String, Object> docContent = checkInQueue.remove();
                //create an empty document
                Document document = database.createDocument();
                //place our content into the document
                document.putProperties(docContent);
            }
            catch (CouchbaseLiteException ex)
            {
                Log.e(TAG, "Unable to put check in into database");
            }
        }
        //set check in posted to false since we processed the check in
        checkInWaiting = false;
    }

    private void sendCheckInToServer()
    {
        try
        {
            //check if there are any check in to be sent to the server
            if (internet && checkInView.getTotalRows() > 0)
            {
                //rerun the query
                QueryEnumerator newResult = query.run();
                //create a collection to store the json objects in
                List<JSONObject> jsonObjectList = new ArrayList<>();
                //create a map of person id to document id for deletion purpose
                List<String> deletionList = new ArrayList<>();
                //iterate through the results
                for (Iterator<QueryRow> it = newResult; it.hasNext(); )
                {
                    //grab the result from the database
                    QueryRow row = it.next();
                    //create the map from the document
                    Map<String, Object> document = (Map<String, Object>) row.getValue();
                    //insert the jsonObject into the collection
                    jsonObjectList.add(new JSONObject(document));
                    //add the document to the deletion list
                    deletionList.add(row.getDocumentId());
                }
                //create the map for the json array
                Map<String,JSONArray> jsonArrayMap = new HashMap<>();
                //put the array into the map
                jsonArrayMap.put("checkIns",new JSONArray(jsonObjectList));
                //create the json object with the pre-created map
                JSONObject jsonObject = new JSONObject(jsonArrayMap);
                //create a future object for the json array
                RequestFuture<JSONObject> future = RequestFuture.newFuture();
                //send the request
                httpRequester.sendEventCheckInRequest(authKey, future,jsonObject);
                //get the response from the server
                getResponse(future, deletionList);
            }
        }
        catch(CouchbaseLiteException cble)
        {
            Log.e(TAG,"Unable to run query for result");
        }
    }

    private void getResponse(RequestFuture future,List<String> deletionList)
    {
        try
        {
            try
            {
                //wait for the response
                future.get(30, TimeUnit.SECONDS);
                //we received a response, so we can delete the sent documents
                for(String documentID: deletionList)
                    database.getDocument(documentID).delete();
            }
            catch (InterruptedException | ExecutionException ex)
            {
                if (ex.getCause() instanceof VolleyError)
                {
                    //cast the throwable back to the volley error
                    VolleyError volleyError = (VolleyError) ex.getCause();
                    //grab the network response
                    NetworkResponse networkResponse = volleyError.networkResponse;
                    //check to see if there is a response
                    if(networkResponse == null)
                    {
                        //check to make sure we have an internet connection
                        checkInternetConnectivityState();
                    }
                }
                else
                {
                    Log.d(TAG,"Exception when sending event check in to server: " + ex);
                }
            }
            catch (TimeoutException te)
            {
                //timeout, most likely due to no internet connection or slow response
                Log.e(TAG, "Timeout occurred when waiting for response");
                //check to make sure its not internet connection problem
                checkInternetConnectivityState();
            }
        }
        catch(CouchbaseLiteException cble)
        {
            Log.e(TAG,"Unable to get document, exception follows: " + cble);
        }
    }

    private void checkInternetConnectivityState()
    {
        //check network connectivity state to make sure we actually have an internet connection
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        //check the connection status
        if(activeNetwork == null || !activeNetwork.isConnected())
            updateConnectivityState(false);
    }

    public void addRunnableFinishedListener(CheckInRunnableListener listener)
    {
        checkInRunnableListener = listener;
    }

    private void notifyFinishListener()
    {
        if(checkInRunnableListener != null)
            checkInRunnableListener.onRunnableFinished();
    }

    private void notifyStateChange(int stringID)
    {
        if(checkInRunnableListener != null)
            checkInRunnableListener.onStateChange(stringID);
    }
}
