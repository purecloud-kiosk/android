package com.awmdev.purecloudkiosk.Decorator;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class JSONDecorator extends JSONObject implements Parcelable
{
    //tag for all logging operations
    private static String tag = JSONDecorator.class.getSimpleName();

    public JSONDecorator(JSONObject jsonObject) throws JSONException
    {
        super(jsonObject.toString());
    }

    public JSONDecorator(String jsonString)throws JSONException
    {
        super(jsonString);
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

    public Map<String,Object> getMap()
    {
        //create the map
        Map<String,Object> map = new HashMap<>();
        //iterate through the keys to create the map
        for(Iterator<String> it = keys(); it.hasNext();)
        {
            //save the key locally
            String key = it.next();
            //add it to the map
            map.put(key,getString(key));
        }
        //return the map
        return map;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        //convert the json object back into a json string
        String convertedString = toString();
        //write the string to the parcel
        dest.writeString(convertedString);
    }

    //Class to handle the recreating of the json object
    public static final Parcelable.Creator<JSONDecorator> CREATOR = new Parcelable.Creator()
    {

        @Override
        public Object createFromParcel(Parcel source)
        {
            try
            {
                return new JSONDecorator(source.readString());
            }
            catch(JSONException ex)
            {
                //log the exception
                Log.d(tag, ex.toString());
                //return a null object
                return null;
            }
        }

        @Override
        public Object[] newArray(int size)
        {
            return new JSONDecorator[size];
        }
    };
}