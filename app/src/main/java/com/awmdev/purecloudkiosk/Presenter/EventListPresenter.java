package com.awmdev.purecloudkiosk.Presenter;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.awmdev.purecloudkiosk.Model.EventListModel;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.View.Interfaces.EventListViewInterface;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class EventListPresenter
{
    private String tag = EventListPresenter.class.getSimpleName();
    private EventListViewInterface eventListViewInterface;
    private EventListModel eventListModel;

    public EventListPresenter(EventListViewInterface eventListViewInterface, EventListModel eventListModel)
    {
        this.eventListViewInterface = eventListViewInterface;
        this.eventListModel = eventListModel;
    }

    public void onDestroy()
    {
        eventListViewInterface = null;
        eventListModel = null;
    }

    public void getEventListData()
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
                    //remove the no event splash image just in case it exists
                    eventListViewInterface.setEmptyStateViewVisibility(false);
                    //create a collection to store the parsed json data
                    List<JSONDecorator> jsonDecoratorList = new ArrayList<>();
                    //parse the json response into jsoneventwrapper class
                    for (int i = 0; i < response.length(); ++i)
                    {
                        try
                        {
                            jsonDecoratorList.add(new JSONDecorator(response.getJSONObject(i)));
                        }
                        catch (JSONException e)
                        {
                            Log.d(tag, "Improperly formatted json, exception follows: " + e);
                        }
                    }
                    //pass the data to the event adapter
                    eventListModel.appendDataSet(jsonDecoratorList);
                    //notify the adapter of the dataset change
                    eventListViewInterface.notifyEventAdapterOfDataSetChange();
                    //increment the page number
                    eventListModel.incrementPageNumber();
                }
                else
                {
                    //check to see if the state is empty and produce the required view
                    if(eventListModel.getEventListDataSize() == 0)
                        eventListViewInterface.setEmptyStateViewVisibility(true);
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
        httpRequester.sendEventDataRequest(eventListModel.getAuthenticationToken(),callback,errorCallback,Integer.toString(eventListModel.getPageNumber()));
    }

    public void onScrolled(int visibleItemCount,int totalItemCount,int firstVisibleItem, int visibleThreshold)
    {
        if(eventListModel.getLoadingStatus())
        {
            if(totalItemCount > eventListModel.getPreviousTotal())
            {
                eventListModel.setLoadingStatus(false);
                eventListModel.setPreviousTotal(totalItemCount);
            }
        }

        if(!eventListModel.getLoadingStatus())
        {
            //check to see if your within the specified distance from the end of the list
            if((totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold))
            {
                //set loading to true since your the specified distance from the end of the list
                eventListModel.setLoadingStatus(true);
                //check to see if the data is filtered and call the appropriate function
                if(eventListModel.isFiltered())
                    sendSearchRequest(eventListModel.getCurrentSearchPattern(), Integer.toString(eventListModel.getPageNumber()), false);
                else
                    getEventListData();
            }
        }
    }

    private void resetScrollAdapter()
    {
        //reset the previous total to zero
        eventListModel.setPreviousTotal(0);
        //set the loading status to true so the scroll adapter will reset its self
        eventListModel.setLoadingStatus(true);
    }

    public void onSearchTextChanged(String searchPattern)
    {
        if(searchPattern.length() > 0)
        {
            //save the search pattern to the model
            eventListModel.setCurrentSearchPattern(searchPattern);
            //reset the scroll adapter
            resetScrollAdapter();
            //send the request
            sendSearchRequest(searchPattern, "0", true);
        }
        else
        {
            //remove the filtered data set from the model
            eventListModel.removeFilterFromDataSet();
            //reset the scroll adapter
            resetScrollAdapter();
            //notify the adapter of the data set change
            eventListViewInterface.notifyEventAdapterOfDataSetChange();
        }
    }

    private void sendSearchRequest(String searchPattern, String pageNumber,final boolean firstSearch)
    {
        //create the callback for the json response
        Response.Listener<JSONArray> callback = new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                //check to see if there was a response, array size of zero means no data was sent
                if (response.length() > 0)
                {
                    //remove the splash image if it exists
                    eventListViewInterface.setEmptyStateViewVisibility(false);
                    //create a collection to store the parsed json data
                    List<JSONDecorator> jsonDecoratorList = new ArrayList<>();
                    //parse the json response into jsoneventwrapper class
                    for (int i = 0; i < response.length(); ++i)
                    {
                        try
                        {
                            jsonDecoratorList.add(new JSONDecorator(response.getJSONObject(i)));
                        }
                        catch (JSONException e)
                        {
                            Log.d(tag, "Improperly formatted json, exception follows: " + e);
                        }
                    }
                    //check to see if this is the first search or subsequent search
                    if(firstSearch)
                        eventListModel.applyFilterToDataSet(jsonDecoratorList);
                    else
                        eventListModel.appendFilterToDataSet(jsonDecoratorList);
                    //notify the adapter of the dataset change
                    eventListViewInterface.notifyEventAdapterOfDataSetChange();
                    //increment the page number
                    eventListModel.incrementPageNumber();
                }
                else
                {
                    //if this is the first search performed and there is no data then clear the old
                    //data from the filtered data set while keeping the flag set to true
                    if(firstSearch)
                        eventListModel.clearFilterDataSet();
                    //notify the adapter of the change in data set
                    eventListViewInterface.notifyEventAdapterOfDataSetChange();
                    //check to see if the state is empty and produce the required view
                    if(eventListModel.getEventListDataSize() == 0)
                        eventListViewInterface.setEmptyStateViewVisibility(true);
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
        httpRequester.sendEventDataSearchRequest(eventListModel.getAuthenticationToken(), callback, errorCallback, pageNumber, searchPattern);
    }
}
