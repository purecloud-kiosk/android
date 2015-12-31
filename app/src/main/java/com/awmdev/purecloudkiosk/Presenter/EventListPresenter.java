package com.awmdev.purecloudkiosk.Presenter;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.awmdev.purecloudkiosk.Adapter.EventAdapter;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.Model.JSONEventWrapper;
import com.awmdev.purecloudkiosk.View.Fragment.EventListFragment;

import org.json.JSONArray;
import org.json.JSONException;

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
    private String tag = EventListPresenter.class.getName();

    public EventListPresenter(EventListFragment eventListFragment)
    {
        this.eventListFragment = eventListFragment;
    }

    public void getEventListData(final EventAdapter eventAdapter, String authToken, int pageNumber)
    {
        //create the callback for the json response
        Response.Listener<JSONArray> callback = new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                //create a collection to store the parsed json data
                List<JSONEventWrapper> jsonEventWrapperList = new ArrayList<>();
                //parse the json response into jsoneventwrapper class
                for(int i = 0; i < response.length(); ++i)
                {
                    try
                    {
                        jsonEventWrapperList.add(new JSONEventWrapper(response.getJSONObject(i).getJSONObject("event")));
                    }
                    catch (JSONException e)
                    {
                        Log.d(tag, "Improperly formatted json, exception follows: " + e);
                    }
                }
                //pass the data to the event adapter
                eventAdapter.appendDataSet(jsonEventWrapperList);
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
        httpRequester.sendEventDataRequest(authToken,callback,errorCallback);
    }


}
