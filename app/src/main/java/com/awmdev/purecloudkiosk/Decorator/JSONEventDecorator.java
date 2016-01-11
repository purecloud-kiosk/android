package com.awmdev.purecloudkiosk.Decorator;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


public class JSONEventDecorator extends JSONObject implements Parcelable
{
    //tag for all logging operations
    private static String tag = JSONEventDecorator.class.getSimpleName();

    public JSONEventDecorator(JSONObject jsonObject) throws JSONException
    {
        super(jsonObject.toString());
    }

    public JSONEventDecorator(String jsonString)throws JSONException
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
    public static final Parcelable.Creator<JSONEventDecorator> CREATOR = new Parcelable.Creator()
    {

        @Override
        public Object createFromParcel(Parcel source)
        {
            try
            {
                return new JSONEventDecorator(source.readString());
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
            return new JSONEventDecorator[size];
        }
    };
}
