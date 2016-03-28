package com.awmdev.purecloudkiosk.Presenter;


import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.Model.EventListModel;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
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

    private void resetScrollAdapter()
    {
        //reset the previous total to zero
        eventListModel.setPreviousTotal(0);
        //set the loading status to true so the scroll adapter will reset its self
        eventListModel.setLoadingStatus(true);
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
                //check if the data set is filtered
                if(eventListModel.isFiltered())
                    sendSearchRequest();
                else
                    sendEventDataRequest();
            }
        }
    }

    public void onSearchTextChanged(String searchPattern)
    {
        //set the pattern even if its empty
        eventListModel.setCurrentSearchPattern(searchPattern);
        //reset the page number due to new request
        eventListModel.resetPageNumber();
        //clear the old search data since were requesting new data
        eventListModel.clearEventListDataSet();
        //set the filtered status to true
        eventListModel.setFilteredStatus(searchPattern.length() > 0);
        //reset the scroll adapter
        resetScrollAdapter();
        //send the request if there is a search pattern
        if(searchPattern.length() > 0)
            sendSearchRequest();
        //no request to be made, notify the adapter that the data set has changed
        else
            eventListViewInterface.notifyEventAdapterOfDataSetChange();
    }

    public void sendEventDataRequest()
    {
        //grab the authentication token from the model
        String authenticationToken = eventListModel.getAuthenticationToken();
        //grab the page number
        String pageNumber = Integer.toString(eventListModel.getPageNumber());
        //grab an instance of volley and send the request
        HttpRequester.getInstance(null).sendEventDataRequest(authenticationToken,
                new JSONEventDataCallback(),new JSONEventErrorCallback(),pageNumber);
    }

    public void sendSearchRequest()
    {
        //grab the current string from the model
        String searchPattern = eventListModel.getCurrentSearchPattern();
        //grab the page number
        String pageNumber = Integer.toString(eventListModel.getPageNumber());
        //grab the authentication token from the model
        String authenticationToken = eventListModel.getAuthenticationToken();
        //grab an instance of volley
        HttpRequester.getInstance(null).sendEventDataSearchRequest(authenticationToken,new JSONEventDataCallback(),
                new JSONEventErrorCallback(),pageNumber,searchPattern);
    }

    private class JSONEventDataCallback implements Response.Listener<JSONArray>
    {
        @Override
        public void onResponse(JSONArray response)
        {
            //check to see if there was a response, array size of zero means no more data
            if (response.length() > 0)
            {
                //remove the no event splash image just in case it exists
                eventListViewInterface.setEmptyStateViewVisibility(false);
                //create a collection to store the parsed json data
                List<JSONDecorator> jsonDecoratorList = new ArrayList<>();
                //parse the json response into JsonDecorator class
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
                //notify the adapter of the data set change
                eventListViewInterface.notifyEventAdapterOfDataSetChange();
                //increment the page number
                eventListModel.incrementPageNumber();
            }
            else
            {
                //check to see if this is the first request and clear the view if necessary
                if(eventListModel.getPageNumber() == 0)
                    eventListModel.clearEventListDataSet();
                //notify the recycler of the data set change
                eventListViewInterface.notifyEventAdapterOfDataSetChange();
                //check to see if the state is empty and produce the required view
                if (eventListModel.getEventListDataSize() == 0)
                    eventListViewInterface.setEmptyStateViewVisibility(true);
            }
        }
    }

    private class JSONEventErrorCallback implements Response.ErrorListener
    {
        @Override
        public void onErrorResponse(VolleyError error)
        {
            Log.d(tag, "Unable to reach server, volley error follow: " + error);
        }
    }
}
