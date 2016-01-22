package com.awmdev.purecloudkiosk.Presenter;

import com.awmdev.purecloudkiosk.Decorator.JSONEventDecorator;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.View.Activity.KioskActivity;

/**
 * Created by Reese on 1/22/2016.
 */
public class KioskPresenter
{
    private KioskActivity kioskActivity;

    public KioskPresenter(KioskActivity kioskActivity)
    {
        this.kioskActivity = kioskActivity;
    }

    public void populateView(JSONEventDecorator jsonEventDecorator)
    {
        //grab an instance of the http requestor
        HttpRequester httpRequester = HttpRequester.getInstance(null);
        //set the network image view
        kioskActivity.setEventImage(httpRequester.getImageLoader(),jsonEventDecorator.getString("image_url"));
        //set the title
        kioskActivity.setEventNameTextView(jsonEventDecorator.getString("title"));
    }
}
