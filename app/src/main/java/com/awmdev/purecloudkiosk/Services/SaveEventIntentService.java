package com.awmdev.purecloudkiosk.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.android.volley.toolbox.RequestFuture;
import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SaveEventIntentService extends IntentService
{
    private final String TAG = SaveEventIntentService.class.getSimpleName();

    public SaveEventIntentService()
    {
        super("SaveEventIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        //grab the power manager
        PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        //grab the wake lock from the power manager
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"Event Saving WakeLock");
        //acquire the wake lock
        wakeLock.acquire();
        //process the intent
        processIntent(intent);
        //release the wakelock
        wakeLock.release();
    }

    private void processIntent(Intent intent)
    {
        //grab the json decorator from the intent
        JSONDecorator jsonDecorator = intent.getExtras().getParcelable("parcelable");
        //check to see if the event already exists in the database
        if(!checkForExistingEntry(jsonDecorator.getString("id")))
        {
            //create the default directory if it doesn't exist
            String rootPath = createDirectory(jsonDecorator.getString("id"));
            //save the root path to the json
            jsonDecorator.putString("rootPath",rootPath);
            //grab the url from the decorator for the thumb
            String thumbURL = jsonDecorator.getString("thumbnailUrl");
            //attempt to download the thumb nail
            String thumbPath = saveImage(rootPath + "/thumbnail.img", thumbURL);
            //save the thumb file path to the jsonDecorator
            jsonDecorator.putString("thumbPath",thumbPath);
            //grab the url for banner image
            String bannerURL =  jsonDecorator.getString("imageUrl");
            //grab the path for the banner
            String bannerPath = saveImage(rootPath+"/banner.img",bannerURL);
            //save the banner image location to the decorator
            jsonDecorator.putString("bannerPath", bannerPath);
            //save the newly created event to the couch base db
            saveDecoratorToDatabase(jsonDecorator);
        }
    }

    private boolean checkForExistingEntry(String id)
    {
        try
        {
            //create an instance of the manager
            Manager manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
            //grab the database from the manager
            Database database = manager.getDatabase("saved_events_database");
            //grab the associated view from the database
            View savedEventsView = database.getView("saved_events_view");
            //assign a mapper to the view
            savedEventsView.setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    emitter.emit("saved_event", document);
                }
            }, "1.0");
            //produce the query that will used to grab the data
            Query query = savedEventsView.createQuery();
            //run the query to produce the results
            QueryEnumerator queryEnumerator = query.run();
            //iterate through the results from the query
            for (Iterator<QueryRow> it = queryEnumerator; it.hasNext(); )
            {
                //grab the query row from the iterator
                QueryRow queryRow = it.next();
                //grab the result from the database
                Map<String,Object> eventMap = (Map<String,Object>)queryRow.getValue();
                //check to see if the event id match if so, don't save the event
                if(((String)eventMap.get("id")).matches(id))
                    return true;
            }
            return false;
        }
        catch(IOException | CouchbaseLiteException ex)
        {
            //Log the exception
            Log.d(TAG,"Exception trying to check for existing entry, exception follows: "+ ex);
            //return true that the event exists since there was an exception trying to check the database
            return true;
        }

    }

    private String createDirectory(String eventID)
    {
        //grab the storage folder for saving events
        File storageFolder = new File(getFilesDir().getAbsolutePath()+"/SavedEventData");
        //check to see if the directory exist, if not we will create it
        if(!storageFolder.isDirectory())
        {
            //the storage folder doesn't exist, lets create it
            storageFolder.mkdir();
        }
        //create the directory for the event
        File eventStorage = new File(storageFolder.getAbsolutePath() + "/"+eventID);
        //create the new directory
        eventStorage.mkdir();
        //return the path of the new directory
        return eventStorage.getAbsolutePath();
    }

    private String saveImage(String path, String url)
    {
        try
        {
            //create a file for the banner and thumbnail
            File imageFile = new File(path);
            //create the file
            imageFile.createNewFile();
            //create a byte output stream
            OutputStream outputStream = new FileOutputStream(imageFile);
            //grab the byte array by downloading the image
            byte[] imageByteArray = downloadImage(url);
            //write the byte array to the file
            outputStream.write(imageByteArray);
            //close the output stream
            outputStream.close();
            //return the string which is the path to the file
            return imageFile.getAbsolutePath();
        }
        catch(IOException ex)
        {
            //log the exception
            Log.d(TAG,"Unable to export image file, exception follows: "+ ex);
            //return null to show the file was not successful saved
            return null;
        }
    }

    private byte[] downloadImage(String imageURL)
    {
        try
        {
            //create the future for the volley request
            RequestFuture<byte[]> imageFuture = RequestFuture.newFuture();
            //send the request with volley
            HttpRequester.getInstance(this).sendDownloadImageRequest(imageURL, imageFuture, imageFuture);
            //return the byte array
            return imageFuture.get(60, TimeUnit.SECONDS);
        }
        catch(TimeoutException | InterruptedException | ExecutionException ex)
        {
            //log the error
            Log.d(TAG, "Unable to download image from specified url: " + imageURL + " ,error follows: "+ex);
            //return an empty byte array was not able to be downloaded, just so we dont fail to write to file
            return new byte[0];
        }
    }

    private void saveDecoratorToDatabase(JSONDecorator jsonDecorator)
    {
        try
        {
            //create an instance of the manager
            Manager manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
            //grab the database from the manager
            Database database = manager.getDatabase("saved_events_database");
            //create a document to store our event into
            Document document = database.createDocument();
            //grab the map from the json decorator
            Map<String,Object> map = jsonDecorator.getMap();
            //remove the version number from the map
            map.remove("__v");
            //store the map into the document
            document.putProperties(map);
        }
        catch(IOException | CouchbaseLiteException ex)
        {
            Log.d(TAG,"Unable to save decorator to database, exception follows: "+ ex);
        }
    }
}
