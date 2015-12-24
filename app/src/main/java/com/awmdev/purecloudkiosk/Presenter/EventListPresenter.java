package com.awmdev.purecloudkiosk.Presenter;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.awmdev.purecloudkiosk.Adapter.EventAdapter;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.View.Fragment.EventListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
I know the presenter layer is not suppose to have any android specific code called in it but i have no
other way of creating an instance of volley without it. I may need to find a work around or I could possible
pass the context but that would also violate the above principle. Brian if you have any ideas please let me know,
thanks.
 */

public class EventListPresenter
{
    private EventListFragment eventListFragment;

    public EventListPresenter(EventListFragment eventListFragment)
    {
        this.eventListFragment = eventListFragment;
    }

    public void getEventListData(EventAdapter eventAdapter, String authToken, int pageNumber)
    {
        //create the callback for the json response
        Response.Listener<JSONArray> callback = new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                //create a collection to store the parsed json data
                List<EventDataContainer> eventDataContainerList = new ArrayList<>();
                //parse the json response into event class
                for(int i = 0; i < response.length(); ++i)
                {
                    try
                    {
                        //grab the json object from the response
                        JSONObject jsonObject = response.getJSONObject(i);
                        //grab the event object from json object
                        JSONObject eventObject = jsonObject.getJSONObject("event");
                        //create an instance of the event data container

                    }
                    catch (JSONException e)
                    {
                        System.out.println("Error " + e );
                       //do nothing, for now i guess
                    }
                }
            }
        };
        //create the callback for the error response
        Response.ErrorListener errorCallback = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                System.out.println("ERROR!!!!! " + error);
            }
        };
        //create an instance of http requester.
        HttpRequester httpRequester = new HttpRequester(eventListFragment.getContext());
        //make a volley request for the event data
        httpRequester.sendEventDataRequest(authToken,callback,errorCallback);
    }
}
