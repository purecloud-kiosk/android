package com.awmdev.purecloudkiosk.Presenter;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.View.Interfaces.DetailedEventViewInterface;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by Reese on 1/12/2016.
 */
public class DetailedEventPresenter
{
    private String TAG = DetailedEventPresenter.class.getSimpleName();
    private DetailedEventViewInterface detailedEventViewInterface;

    public DetailedEventPresenter(DetailedEventViewInterface detailedEventViewInterface)
    {
        this.detailedEventViewInterface = detailedEventViewInterface;
    }

    public void populateView(JSONDecorator jsonDecorator)
    {
        //grab the content from the json and set it to the textviews
        detailedEventViewInterface.assignTextView(detailedEventViewInterface.DESCRIPTION, jsonDecorator.getString("description"));
        //grab the boolean from the privacy to convert to public / private
        if(Boolean.parseBoolean(jsonDecorator.getString("private")))
            detailedEventViewInterface.assignTextView(detailedEventViewInterface.PRIVACY,"Private");
        else
            detailedEventViewInterface.assignTextView(detailedEventViewInterface.PRIVACY,"Public");
        detailedEventViewInterface.assignTextView(detailedEventViewInterface.EVENT_NAME, jsonDecorator.getString("title"));
        detailedEventViewInterface.assignTextView(detailedEventViewInterface.LOCATION, jsonDecorator.getString("location"));
        //grab the date in the form of the epoch
        Long epochStart = Long.parseLong(jsonDecorator.getString("startDate"));
        Long epochEnd = Long.parseLong(jsonDecorator.getString("endDate"));
        //create a instance of date from the epoch
        Date startDate = new Date(epochStart);
        //set the date using simple date format
        detailedEventViewInterface.assignTextView(detailedEventViewInterface.START_DATE,new SimpleDateFormat("hh:mm a 'on' MM-dd-yyyy").format(startDate));
        //create a instance of date from the epoch
        Date endDate = new Date(epochEnd);
        //set the date using simple date format
        detailedEventViewInterface.assignTextView(detailedEventViewInterface.END_DATE,new SimpleDateFormat("hh:mm a 'on' MM-dd-yyyy").format(endDate));
        //string to store image url
        String imageURL;
        //check to see if event has image associated
        if(!(imageURL = jsonDecorator.getString("imageUrl")).equalsIgnoreCase("null"))
        {
            //grab an instance of the http requester
            HttpRequester httpRequester = HttpRequester.getInstance(null);
            //send the image request and set the response
            httpRequester.getImageLoader().get(jsonDecorator.getString("imageUrl"), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    detailedEventViewInterface.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    detailedEventViewInterface.setImageByResId(R.drawable.no_image_available);
                }
            });

        }
        else
        {
            //set the default image
            detailedEventViewInterface.setImageByResId(R.drawable.no_image_available);
        }
    }
}
