package com.awmdev.purecloudkiosk.Presenter;

import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.View.Interfaces.KioskViewInterface;

/**
 * Created by Reese on 1/22/2016.
 */
public class KioskPresenter
{
    private KioskViewInterface kioskViewInterface;

    public KioskPresenter(KioskViewInterface kioskViewInterface)
    {
        this.kioskViewInterface = kioskViewInterface;
    }

    public void populateView(JSONDecorator jsonDecorator)
    {
        //grab an instance of the http requestor
        HttpRequester httpRequester = HttpRequester.getInstance(null);
        //set the network image view
        kioskViewInterface.setEventImage(httpRequester.getImageLoader(), jsonDecorator.getString("image_url"));
        //set the title
        kioskViewInterface.setEventNameTextView(jsonDecorator.getString("title"));
    }
}
