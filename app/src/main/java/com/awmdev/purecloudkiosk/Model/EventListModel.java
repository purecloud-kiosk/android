package com.awmdev.purecloudkiosk.Model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.awmdev.purecloudkiosk.Decorator.JSONEventDecorator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Reese on 1/10/2016.
 */
public class EventListModel implements Parcelable
{
    private List<JSONEventDecorator> jsonEventDecoratorList = new ArrayList();
    private List<Integer> eventWrapperFilter = new ArrayList();
    private boolean filtered = false;


    public void appendDataSet(Collection<JSONEventDecorator> eventDataCollection)
    {
        //assign the passed in dataset to the list
        jsonEventDecoratorList.addAll(eventDataCollection);
    }

    public List<JSONEventDecorator> getEventListDataSet()
    {
        return jsonEventDecoratorList;
    }

    public JSONEventDecorator getEventListItem(int position)
    {
        if(filtered)
            return jsonEventDecoratorList.get(eventWrapperFilter.get(position));
        else
            return jsonEventDecoratorList.get(position);
    }

    public int getEventListDataSize()
    {
        if(filtered)
            return eventWrapperFilter.size();
        else
            return jsonEventDecoratorList.size();
    }

    public void removeFilterFromDataSet()
    {
        synchronized (this)
        {
            if(filtered)
            {
                //clear the dataset filter
                eventWrapperFilter.clear();
                //set filter to false
                filtered = false;
            }
        }
    }

    public void applyFilterToDataSet(List<Integer> filter)
    {
        synchronized (this)
        {
            //if there is already a filter, remove it
            if (!eventWrapperFilter.isEmpty())
                eventWrapperFilter.clear();
            //add the new filter to the dataset
            eventWrapperFilter.addAll(filter);
            //set the filter boolean to true since the data now contains a filter
            filtered = true;
        }
    }

    //IMPLEMENT LATER, BUNDLE SAVING
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {

    }

    public static final Parcelable.Creator<EventListModel> CREATOR = new Parcelable.ClassLoaderCreator()
    {

        @Override
        public Object createFromParcel(Parcel source)
        {
            return null;
        }

        @Override
        public Object[] newArray(int size)
        {
            return new EventListModel[size];
        }

        @Override
        public Object createFromParcel(Parcel source, ClassLoader loader)
        {
            return null;
        }
    };
}
