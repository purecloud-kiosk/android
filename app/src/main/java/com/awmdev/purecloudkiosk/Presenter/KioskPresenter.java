package com.awmdev.purecloudkiosk.Presenter;

import android.graphics.BitmapFactory;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
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
        //set the title
        kioskViewInterface.setEventNameTextView(jsonDecorator.getString("title"));
        //create a string to store the banner file path
        String bannerPath;
        //check to see if the event data is saved locally by checking for banner path
        if((bannerPath = jsonDecorator.getString("bannerPath")) != null)
            kioskViewInterface.setEventImage(BitmapFactory.decodeFile(bannerPath));
        else
        {
            try
            {
                //grab an instance of the http requester
                HttpRequester httpRequester = HttpRequester.getInstance(null);
                //send the image request and set the response
                httpRequester.getImageLoader().get(jsonDecorator.getString("imageUrl"), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        kioskViewInterface.setEventImage(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //create an default image
                    }
                });
            }
            catch(NullPointerException ex)
            {
                //there was no url specified, as such set the default image
            }
        }
    }
}
