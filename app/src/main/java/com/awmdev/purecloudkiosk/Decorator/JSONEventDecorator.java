package com.awmdev.purecloudkiosk.Decorator;


import org.json.JSONException;
import org.json.JSONObject;


public class JSONEventDecorator extends JSONObject
{
    public JSONEventDecorator(JSONObject jsonObject) throws JSONException
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
}
