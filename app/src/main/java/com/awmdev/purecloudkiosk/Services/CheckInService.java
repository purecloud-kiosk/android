package com.awmdev.purecloudkiosk.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.toolbox.RequestFuture;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CheckInService extends Service
{
    private BlockingQueue<Map<String,Object>> checkInQueue = new ArrayBlockingQueue(30,true);
    private static final String TAG = CheckInService.class.getSimpleName();
    private CheckInBinder checkInBinder = new CheckInBinder();
    private boolean serviceBound = true;
    private Database database;
    private String authKey;

    @Override
    public void onCreate()
    {
        try
        {
            Manager manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);
            //grab the database
            database = manager.getDatabase("check_in_database");
            //create the thread pool for the producer and consumer
            ExecutorService threadPool = Executors.newFixedThreadPool(2);
            threadPool.submit(new CheckInProducer());
            threadPool.submit(new CheckInConsumer());
            //grab the authentication token from the preference
            SharedPreferences sharedPreferences = getSharedPreferences("authenticationPreference", Context.MODE_PRIVATE);
            authKey = sharedPreferences.getString("authenticationToken","");
        }
        catch(IOException | CouchbaseLiteException e)
        {
            Log.e(TAG, "Unable to create instance of CouchLite Database,service stopping");
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        //return the binder
        return checkInBinder;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }

    public void postCheckIn(Map<String,Object> checkInMap)
    {
        //add the check in to the queue for the producer to add to the database
        try
        {
            checkInQueue.put(checkInMap);
        }
        catch(InterruptedException ex)
        {
            Log.e(TAG,"Unable to add check in to queue for processing");
        }

    }

    public class CheckInBinder extends Binder
    {
        public CheckInService getService()
        {
            return CheckInService.this;
        }
    }

    private class CheckInConsumer implements Runnable
    {
        private final String TAG = CheckInConsumer.class.getSimpleName();

        @Override
        public void run()
        {
            //grab an instance of the http requester
            HttpRequester httpRequester = HttpRequester.getInstance(null);
            //grab the view from the database
            View checkInView = database.getView("check_in_view");
            //create the mapper for the view
            checkInView.setMap(new Mapper()
            {
                @Override
                public void map(Map<String, Object> document, Emitter emitter)
                {
                    //we want to emit all documents and all of their content
                    emitter.emit("checkIn", document);
                }
            }, "1.0");
            //create the query for the navigating the results
            Query query = checkInView.createQuery();
            //save the old result for comparison
            QueryEnumerator oldResult = null;
            //loop until the view is empty and the service is not longer bound
            while (serviceBound || checkInView.getCurrentTotalRows() > 0)
            {
                try
                {
                    //check if the old result is stale
                    if (oldResult == null || oldResult.isStale())
                    {
                        //rerun the query
                        QueryEnumerator newResult = query.run();
                        //if the result actually isn't stale, then grab the results
                        if (!oldResult.equals(newResult))
                        {
                            //save the new result for comparison
                            oldResult = newResult;
                            //iterate through the results
                            for (Iterator<QueryRow> it = newResult; it.hasNext();)
                            {
                                //grab the result from the iterator
                                QueryRow row = it.next();
                                //create the future for the request
                                RequestFuture<JSONArray> future = RequestFuture.newFuture();
                                //create the json object from the map
                                JSONObject jsonObject = new JSONObject(row.asJSONDictionary());
                                //send the request
                                httpRequester.sendEventCheckInRequest(authKey,future,jsonObject);
                                //wait for the response
                                JSONArray jsonArray = future.get(30, TimeUnit.SECONDS);
                                //print the response
                                Log.d(TAG,jsonArray.toString());
                            }
                        }
                    }
                }
                catch(CouchbaseLiteException cble)
                {
                    Log.e(TAG,"Unable to run query for result, exception follows: " + cble.getMessage());
                }
                catch(InterruptedException | ExecutionException ex)
                {
                    Log.e(TAG,"Error in handling future response, exception follows: "+ ex.getMessage());
                }
                catch(TimeoutException te)
                {
                    Log.e(TAG,"Timeout occurred when waiting for response");
                    //set the old result to null to allow searching for the same results again
                    oldResult = null;
                }
            }
        }
    }

    private class CheckInProducer implements Runnable
    {
        private final String TAG = CheckInProducer.class.getSimpleName();

        @Override
        public void run()
        {
            while(serviceBound || !checkInQueue.isEmpty())
            {
                try
                {
                    //grab the check in from the queue
                    Map<String, Object> docContent = checkInQueue.take();
                    Log.d(TAG,"Grabbing document: "+docContent);
                    //create an empty document
                    Document document = database.createDocument();
                    //place our content into the document
                    document.putProperties(docContent);
                }
                catch(CouchbaseLiteException | InterruptedException ex)
                {
                    Log.e(TAG,"Unable to put check in into database");
                }
            }
        }

    }
}
