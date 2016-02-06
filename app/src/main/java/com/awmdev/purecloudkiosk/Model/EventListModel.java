package com.awmdev.purecloudkiosk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EventListModel implements Parcelable
{
    private List<JSONDecorator> jsonDecoratorList = new ArrayList();
    private List<JSONDecorator> eventDecoratorFilter = new ArrayList();
    private int firstVisibleNonFilteredItemLocation;
    private int firstVisibleFilteredItemLocation;
    private String currentSearchPattern;
    private String authenticationToken;
    private boolean filtered = false;
    private int filterPageNumber = 0;
    private boolean loading = true;
    private int previousTotal = 0;
    private int pageNumber = 0;

    public void setAuthenticationToken(String authenticationToken)
    {
        this.authenticationToken = authenticationToken;
    }

    public String getAuthenticationToken()
    {
        return authenticationToken;
    }

    public void appendDataSet(Collection<JSONDecorator> eventDataCollection)
    {
        //assign the passed in dataset to the list
        jsonDecoratorList.addAll(eventDataCollection);
    }

    public synchronized List<JSONDecorator> getEventListDataSet()
    {
        return jsonDecoratorList;
    }

    public synchronized JSONDecorator getEventListItem(int position)
    {
        if(filtered)
            return eventDecoratorFilter.get(position);
        else
            return jsonDecoratorList.get(position);
    }

    public synchronized int getEventListDataSize()
    {
        if(filtered)
            return eventDecoratorFilter.size();
        else
            return jsonDecoratorList.size();
    }


    /** This function will remove the data set associated
     *  with the filter and reset the filter flag to false.
     *  This will switch the data set the recycler view uses back
     *  to the event list data set.
     */
    public synchronized void removeFilterFromDataSet()
    {
        if(filtered)
        {
            //clear the dataset filter
            eventDecoratorFilter.clear();
            //set filter to false
            filtered = false;
            //reset the filter page number to zero
            filterPageNumber = 0;
        }
    }


    /** This function wil clear the data set from the filter without
     * removing the filtered flag from the model.
     */
    public synchronized  void clearFilterDataSet()
    {
        //clear the filter data set but keep filter true
        eventDecoratorFilter.clear();
    }

    /**
     *  This function will append another data set to the filter. This should be used
     *  in cases when onScrolled is called. Never use this when appended the initial
     *  data set since filtered will not be set to true.
     *
     * @param filter the data set that should be appended to the filter
     */
    public synchronized void appendFilterToDataSet(List<JSONDecorator> filter)
    {
            //add the new filter to the dataset
            eventDecoratorFilter.addAll(filter);
    }

    /**
     *   Use this function to append the initial data set to the filter. This method
     *   will also set filtered to true and update the data for the recycler view.
     *   You must call notifyEventAdapterOfDataSetChange for the changes to be updated.
     *   Do not call this method to append additional data such as when onScrolled is
     *   called.
     *
     *   @param filter the initial data set that should be appended to the filter
     */
    public synchronized void applyFilterToDataSet(List<JSONDecorator> filter)
    {
        //remove the old data from the set
        eventDecoratorFilter.clear();
        //set the new data set to the filter
        eventDecoratorFilter.addAll(filter);
        //reset the page number to zero
        filterPageNumber = 0;
        //set the filtered status to true since the data set now contains a filter
        filtered = true;
    }

    public synchronized boolean isFiltered()
    {
        return filtered;
    }

    public void setCurrentSearchPattern(String searchPattern)
    {
        this.currentSearchPattern = searchPattern;
    }

    public String getCurrentSearchPattern()
    {
        return currentSearchPattern;
    }


    public int getPageNumber()
    {
        return filtered ? filterPageNumber : pageNumber;
    }

    public void incrementPageNumber()
    {
        if(filtered)
            filterPageNumber++;
        else
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

    public void setFirstVisibleFilteredItemLocation(int location)
    {
        firstVisibleFilteredItemLocation = location;
    }

    public void setFirstVisibleNonFilteredItemLocation(int location)
    {
        firstVisibleNonFilteredItemLocation = location;
    }

    public int getFirstVisibleItemLocation()
    {
        return filtered ? firstVisibleFilteredItemLocation : firstVisibleNonFilteredItemLocation;
    }

    /*
        All Code Beyond this point is used for saving the model to the bundle for screen
        rotation handling. I'm hoping this doesn't break the mvp pattern by allowing android
        dependencies into the model :(
     */

    private void initializeModel(int pageNumber, int filterPageNumber)
    {
        this.pageNumber = pageNumber;
        this.filterPageNumber = filterPageNumber;
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
        dest.writeBooleanArray(new boolean[]{filtered});
        //write the filtered list to the parcel, if the list is filtered
        if(filtered)
            dest.writeList(eventDecoratorFilter);
        //write the jsondecoratorlist to the parcel
        dest.writeTypedList(jsonDecoratorList);
        //write the previous total and pagenumber to the parcel
        dest.writeIntArray(new int[]{pageNumber,filterPageNumber});
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
            boolean booleanArray[] = new boolean[1];
            //read the boolean array from the parcel
            source.readBooleanArray(booleanArray);
            //grab the value from the array
            boolean filtered = booleanArray[0];
            //create an integer array and load from the parcel
            int integerArray[] = new int[2];
            source.readIntArray(integerArray);
            //load the values from the array and set them to the model
            eventListModel.initializeModel(integerArray[0],integerArray[1]);
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
            source.readTypedList(parcelList, JSONDecorator.CREATOR);
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
