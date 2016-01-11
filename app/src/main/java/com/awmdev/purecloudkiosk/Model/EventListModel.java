package com.awmdev.purecloudkiosk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.awmdev.purecloudkiosk.Decorator.JSONEventDecorator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EventListModel implements Parcelable
{
    private List<JSONEventDecorator> jsonEventDecoratorList = new ArrayList();
    private List<Integer> eventDecoratorFilter = new ArrayList();
    private boolean filtered = false;
    private boolean loading = true;
    private int previousTotal = 0;
    private int pageNumber = 0;

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
            return jsonEventDecoratorList.get(eventDecoratorFilter.get(position));
        else
            return jsonEventDecoratorList.get(position);
    }

    public int getEventListDataSize()
    {
        if(filtered)
            return eventDecoratorFilter.size();
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
                eventDecoratorFilter.clear();
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
            if (!eventDecoratorFilter.isEmpty())
                eventDecoratorFilter.clear();
            //add the new filter to the dataset
            eventDecoratorFilter.addAll(filter);
            //set the filter boolean to true since the data now contains a filter
            filtered = true;
        }
    }

    public int getPageNumber()
    {
        return pageNumber;
    }

    public void incrementPageNumber()
    {
        pageNumber++;
    }

    public int getPreviousTotal()
    {
        return previousTotal;
    }

    public void setLoadingStatus(boolean status)
    {
        loading = status;
    }

    public void setPreviousTotal(int newTotal)
    {
        previousTotal = newTotal;
    }

    public boolean getLoadingStatus()
    {
        return loading;
    }

    /*
        All Code Beyond this point is used for saving the model to the bundle for screen
        rotation handling. I'm hoping this doesn't break the mvp pattern by allowing android
        dependencies into the model :(
     */

    protected void initializeModel(int pageNumber, int previousTotal, boolean loading)
    {
        this.pageNumber = pageNumber;
        this.previousTotal = previousTotal;
        this.loading = loading;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        //write the filtered and loading status to the parcel
        dest.writeBooleanArray(new boolean[]{filtered,loading});
        //write the filtered list to the parcel, if the list is filtered
        if(filtered)
            dest.writeList(eventDecoratorFilter);
        //write the jsondecoratorlist to the parcel
        dest.writeTypedList(jsonEventDecoratorList);
        //write the previous total and pagenumber to the parcel
        dest.writeIntArray(new int[]{pageNumber,previousTotal});
    }

    public static final Parcelable.Creator<EventListModel> CREATOR = new Parcelable.Creator()
    {

        @Override
        public Object createFromParcel(Parcel source)
        {
            //create a new model object
            EventListModel eventListModel = new EventListModel();
            //create a list for the parcel to store temporary list items into
            List parcelList = new ArrayList<>();
            //create a boolean array to grab the values from
            boolean booleanArray[] = new boolean[2];
            //read the boolean array from the parcel
            source.readBooleanArray(booleanArray);
            //grab the value from the array
            boolean filtered = booleanArray[0];
            //create an integer array and load from the parcel
            int integerArray[] = new int[2];
            source.readIntArray(integerArray);
            //load the values from the array and set them to the model
            eventListModel.initializeModel(integerArray[0],integerArray[1],booleanArray[1]);
            //check to see if the list is filtered, if so load the filtered list
            if(filtered)
            {
                ///grab the filter values from the parcel, use default class loader
                source.readList(parcelList,null);
                //set the list to the model
                eventListModel.applyFilterToDataSet(parcelList);
            }
            //clear the list
            parcelList.clear();
            //grab the jsondecoratorlist from the parcel
            source.readTypedList(parcelList,JSONEventDecorator.CREATOR);
            //set the list to the model
            eventListModel.appendDataSet(parcelList);
            //return the completed model object
            return eventListModel;
        }

        @Override
        public Object[] newArray(int size)
        {
            return new EventListModel[size];
        }
    };
}
