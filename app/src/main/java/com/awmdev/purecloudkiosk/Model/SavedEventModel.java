package com.awmdev.purecloudkiosk.Model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.support.FileDirUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SavedEventModel
{
    private final String TAG = SavedEventModel.class.getSimpleName();
    private List<Map<String, Object>> savedEventsList = new ArrayList<>();
    private Map<String, Object> eventStagedForDeletion;
    private Database savedEventDatabase;
    private Query savedEventQuery;

    public SavedEventModel(Context context)
    {
        //initialize the couch database
        initializeCouchDatabase(context);
        //populate the model with data from couch
        populateDataSet();
    }

    public void initializeCouchDatabase(Context context)
    {
        try
        {
            //grab an instance of the manager
            Manager manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
            //grab the database the holds the saved events
            savedEventDatabase = manager.getDatabase("saved_events_database");
            //grab the associated view from the database
            View savedEventsView = savedEventDatabase.getView("saved_events_view");
            //assign a mapper to the view
            savedEventsView.setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    emitter.emit("saved_event", document);
                }
            }, "1.0");
            //produce the query that will used to grab the data
            savedEventQuery = savedEventsView.createQuery();
        }
        catch (CouchbaseLiteException | IOException cble)
        {
            Log.d(TAG, "Unable to initialize couch database, exception follows: " + cble);
        }

    }

    private void populateDataSet()
    {
        try
        {
            //grab the current epoch from the system
            long epoch = System.currentTimeMillis();
            //run the query to produce the results
            QueryEnumerator queryEnumerator = savedEventQuery.run();
            //iterate through the results from the query
            for (Iterator<QueryRow> it = queryEnumerator; it.hasNext(); )
            {
                //grab a query row from the database
                QueryRow queryRow = it.next();
                //grab the map from the database
                Map<String, Object> eventMap = queryRow.asJSONDictionary();
                //grab the string value for the start data in millisecs
                String eventStringEpoch = (String) ((Map<String, Object>) eventMap.get("value")).get("startDate");
                //parse the epoch back into a long
                long eventStartTime = Long.parseLong(eventStringEpoch);
                //check to see if the event is old and should be deleted
                if (eventStartTime < epoch)
                    //the event is old and should be removed
                    deleteSavedEventData(eventMap);
                else
                    //the event has not yet happened and as such should be added
                    savedEventsList.add(eventMap);
            }
        }
        catch (CouchbaseLiteException cble)
        {
            Log.d(TAG, "Unable to populate saved event model, exception follows: " + cble);
        }
    }

    private void deleteSavedEventData(Map<String,Object> eventMap) throws CouchbaseLiteException
    {
        //grab the root path
        String rootPath = (String) ((Map<String, Object>) eventMap.get("value")).get("rootPath");
        //check to see if the folder exists
        File file = new File(rootPath);
        //delete the folder and all of the contents
        FileDirUtils.deleteRecursive(file);
        //delete the document from the database
        savedEventDatabase.getDocument((String) eventMap.get("id")).delete();
    }

    public int getSavedEventDataSetSize()
    {
        return savedEventsList.size();
    }

    public JSONDecorator getSavedEventListItem(int position)
    {
        //grab the map that hold the event data
        Map<String, Object> jsonMap = ((Map<String, Object>) savedEventsList.get(position).get("value"));
        //return a new json decorator with the supplied map
        return new JSONDecorator(jsonMap);
    }

    public void stageSavedEventForDeletion(int itemPosition)
    {
        //check to see if another event is already staged for deletion
        if (eventStagedForDeletion != null)
            deleteStagedSavedEvent();
        //remove the event from the list and save it locally for restoring purposes
        eventStagedForDeletion = savedEventsList.remove(itemPosition);
        //add a new field to the staged event which would be its old position
        eventStagedForDeletion.put("listPosition", itemPosition);
    }

    public void deleteStagedSavedEvent()
    {
        try
        {
            if (eventStagedForDeletion != null)
            {
                //grab the document id from the map
                String id = (String) eventStagedForDeletion.get("id");
                //delete the document from the database
                savedEventDatabase.getDocument(id).delete();
                //remove the staged event
                eventStagedForDeletion = null;
            }
        }
        catch (CouchbaseLiteException cble)
        {
            Log.d(TAG, "Unable to delete saved document from database, exception follows: " + cble);
        }
    }

    public void restoreStagedSavedEvent()
    {
        //grab the old list position from the item
        int itemPosition = (int) eventStagedForDeletion.get("listPosition");
        //insert the item back into the array at its old position
        savedEventsList.add(itemPosition, eventStagedForDeletion);
        //delete the event saved for deletion
        eventStagedForDeletion = null;
    }

}