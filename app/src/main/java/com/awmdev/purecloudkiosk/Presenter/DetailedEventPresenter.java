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
        //set the image view
        //string to store image url
        String imageURL;
        //check to see if event has image associated
        if(!(imageURL = jsonDecorator.getString("imageUrl")).equalsIgnoreCase("null"))
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
