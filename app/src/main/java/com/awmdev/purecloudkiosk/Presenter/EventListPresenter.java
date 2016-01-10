package com.awmdev.purecloudkiosk.Presenter;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.awmdev.purecloudkiosk.Model.EventListModel;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.Decorator.JSONEventDecorator;
import com.awmdev.purecloudkiosk.Service.EventSearchAsyncTask;
import com.awmdev.purecloudkiosk.View.Fragment.EventListFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class EventListPresenter
{
    private String tag = EventListPresenter.class.getSimpleName();
    private EventSearchAsyncTask eventSearchAsyncTask;
    private EventListFragment eventListFragment;
    private EventListModel eventListModel;
    private boolean loading = true;
    private int previousTotal = 0;
    private int pageNumber = 0;

    public EventListPresenter(EventListFragment eventListFragment, EventListModel eventListModel)
    {
        this.eventListFragment = eventListFragment;
        this.eventListModel = eventListModel;
    }

    public void getEventListData(String authToken)
    {
        //create the callback for the json response
        Response.Listener<JSONArray> callback = new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                //check to see if there was a response, array size of zero means no more data
                if(response.length() > 0)
                {
                    //create a collection to store the parsed json data
                    List<JSONEventDecorator> jsonEventDecoratorList = new ArrayList<>();
                    //parse the json response into jsoneventwrapper class
                    for (int i = 0; i < response.length(); ++i)
                    {
                        try
                        {
                            jsonEventDecoratorList.add(new JSONEventDecorator(response.getJSONObject(i).getJSONObject("event")));
                        }
                        catch (JSONException e)
                        {
                            Log.d(tag, "Improperly formatted json, exception follows: " + e);
                        }
                    }
                    //pass the data to the event adapter
                    eventListModel.appendDataSet(jsonEventDecoratorList);
                    //notify the adapter of the dataset change
                    eventListFragment.notifyEventAdapterOfDataSetChange();
                    //increment the page number
                    pageNumber++;
                }
            }
        };
        //create the callback for the error response
        Response.ErrorListener errorCallback = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d(tag, "Unable to reach server, volley error follow: " + error);
            }
        };
        //create an instance of http requester.
        HttpRequester httpRequester = HttpRequester.getInstance(null);
        //make a volley request for the event data
        httpRequester.sendEventDataRequest(authToken,callback,errorCallback,Integer.toString(pageNumber));
    }


    public void onScrolled(int visibleItemCount,int totalItemCount,int firstVisibleItem, int visibleThreshold,String authToken)
    {
        if(loading)
        {
            if(totalItemCount > previousTotal)
            {
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        if(!loading)
        {
            //check to see if your within the specified distance from the end of the list
            if((totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold))
            {
                //set loading to true since your the specified distance from the end of the list
                loading = true;
                //call getEventListData to retrieve more data
                getEventListData(authToken);
            }
        }
    }

    public void onSearchTextEntered(String searchPattern)
    {
        //if there is already a task running, kill it!
        if(eventSearchAsyncTask != null && eventSearchAsyncTask.getStatus() == AsyncTask.Status.RUNNING)
        {
            //cancel the task
            eventSearchAsyncTask.cancel(true);
        }

        //check to see if the pattern is greater than zero,if so execute task
        if(searchPattern.length() > 0)
        {
            //create an instance of the async task
            eventSearchAsyncTask = new EventSearchAsyncTask(eventListFragment,eventListModel);
            //start the task
            eventSearchAsyncTask.execute(searchPattern);
        }
        else
        {
            //remove the filter since there no longer any text in the search box
            eventListModel.removeFilterFromDataSet();
            //notify the adapter of the dataset change
            eventListFragment.notifyEventAdapterOfDataSetChange();
        }

    }

}
