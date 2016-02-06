package com.awmdev.purecloudkiosk.Presenter;

import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.View.Interfaces.DetailedEventViewInterface;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Reese on 1/12/2016.
 */
public class DetailedEventPresenter
{
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
        Long epoch = Long.parseLong(jsonDecorator.getString("date"));
        //create a instance of date from the epoch
        Date date = new Date(epoch);
        //set the date using simple date format
        detailedEventViewInterface.assignTextView(detailedEventViewInterface.DATE,new SimpleDateFormat("hh:mm a 'on' MM-dd-yyyy").format(date));
        //set the image view
        //string to store image url
        String imageURL;
        //check to see if event has image associated
        if(!(imageURL = jsonDecorator.getString("image_url")).equalsIgnoreCase("null"))
        {
            //grab image from url
            detailedEventViewInterface.setImageUrl(imageURL,HttpRequester.getInstance(null).getImageLoader());
        }
        else
        {
            //place default image instead, this is not the final image nor will it be an http request
            detailedEventViewInterface.setImageUrl("https://www.google.com/logos/doodles/2015/new-years-eve-2015-5985438795825152-hp.gif", HttpRequester.getInstance(null).getImageLoader());
        }
    }
}
