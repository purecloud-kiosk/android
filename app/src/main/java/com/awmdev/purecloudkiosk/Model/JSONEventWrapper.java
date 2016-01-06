package com.awmdev.purecloudkiosk.Model;


import org.json.JSONException;
import org.json.JSONObject;


public class JSONEventWrapper extends JSONObject
{
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
}
