package com.awmdev.purecloudkiosk.Model;

import android.media.Image;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONEventWrapper extends JSONObject
{
    private Image eventImage;

    public JSONEventWrapper(JSONObject jsonObject) throws JSONException
    {
        super(jsonObject.toString());
    }

    @Override
    public String getString(String parameter)
    {
        try
        {
            return super.getString(parameter);
        }
        catch(JSONException ex)
        {
            //improperly formatted json, return null
            return null;
        }
    }

    public void setEventImage(Image image)
    {
        eventImage = image;
    }

    public Image getEventImage()
    {
        return eventImage;
    }
}
