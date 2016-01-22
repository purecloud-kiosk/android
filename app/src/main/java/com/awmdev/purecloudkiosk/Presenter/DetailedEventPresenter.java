package com.awmdev.purecloudkiosk.Presenter;

import com.awmdev.purecloudkiosk.Decorator.JSONEventDecorator;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.View.Activity.EventListActivity;
import com.awmdev.purecloudkiosk.View.Fragment.DetailedEventFragment;
import com.awmdev.purecloudkiosk.View.Fragment.EventListFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Reese on 1/12/2016.
 */
public class DetailedEventPresenter
{
    private DetailedEventFragment detailedEventFragment;

    public DetailedEventPresenter(DetailedEventFragment detailedEventFragment)
    {
        this.detailedEventFragment = detailedEventFragment;
    }

    public void populateView(JSONEventDecorator jsonEventDecorator)
    {
        //grab the content from the json and set it to the textviews
        detailedEventFragment.assignTextView(detailedEventFragment.DESCRIPTION, jsonEventDecorator.getString("description"));
        //grab the boolean from the privacy to convert to public / private
        if(Boolean.parseBoolean(jsonEventDecorator.getString("private")))
            detailedEventFragment.assignTextView(detailedEventFragment.PRIVACY,"Private");
        else
            detailedEventFragment.assignTextView(detailedEventFragment.PRIVACY,"Public");
        detailedEventFragment.assignTextView(detailedEventFragment.EVENT_NAME,jsonEventDecorator.getString("title"));
        detailedEventFragment.assignTextView(detailedEventFragment.LOCATION,jsonEventDecorator.getString("location"));
        //grab the date in the form of the epoch
        Long epoch = Long.parseLong(jsonEventDecorator.getString("date"));
        //create a instance of date from the epoch
        Date date = new Date(epoch);
        //set the date using simple date format
        detailedEventFragment.assignTextView(detailedEventFragment.DATE,new SimpleDateFormat("hh:mm a 'on' MM-dd-yyyy").format(date));
        //set the image view
        //string to store image url
        String imageURL;
        //check to see if event has image associated
        if(!(imageURL = jsonEventDecorator.getString("image_url")).equalsIgnoreCase("null"))
        {
            //grab image from url
            detailedEventFragment.setImageUrl(imageURL,HttpRequester.getInstance(null).getImageLoader());
        }
        else
        {
            //place default image instead, this is not the final image nor will it be an http request
            detailedEventFragment.setImageUrl("https://www.google.com/logos/doodles/2015/new-years-eve-2015-5985438795825152-hp.gif", HttpRequester.getInstance(null).getImageLoader());
        }
    }
}
