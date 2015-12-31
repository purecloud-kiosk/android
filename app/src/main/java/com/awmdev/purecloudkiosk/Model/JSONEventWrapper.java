package com.awmdev.purecloudkiosk.Model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;

import org.json.JSONException;
import org.json.JSONObject;

/*
    Wrapper Class to handle the storing of images associate with JSON Event Data. This class also
    overrides the default implementation of getString to handle errors and pass back null due to the
    amount of calls to get string there are.
 */

public class JSONEventWrapper extends JSONObject
{
    private Drawable eventDrawable;

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

    public void setEventDrawable(Drawable drawable)
    {
        eventDrawable = drawable;
    }

    public Drawable getEventDrawable()
    {
        return eventDrawable;
    }

}
