package com.awmdev.purecloudkiosk.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.awmdev.purecloudkiosk.Model.EventDataContainer;
import com.awmdev.purecloudkiosk.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>
{
    private List<EventDataContainer> eventDataContainer = new ArrayList();

    public EventAdapter(){}

    public EventAdapter(Collection<EventDataContainer> eventDataCollection)
    {
        eventDataContainer.addAll(eventDataCollection);
    }

    public void appendDataSet(Collection<EventDataContainer> eventDataCollection)
    {
        //assign the passed in dataset to the list
        eventDataContainer.addAll(eventDataCollection);
        //notify the adapter of the dataset change
        notifyDataSetChanged();
    }

    public void clearDataSet()
    {
        //clear the dataset from the list
        eventDataContainer.clear();
        //notify of a dataset change
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //inflate the view and store its returned value into layout
        RelativeLayout layout = (RelativeLayout)LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_item,parent,false);
        //store the inflated view in a view holder
        ViewHolder holder = new ViewHolder(layout);
        //return the holder
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.assignEventData(eventDataContainer.get(position));
    }

    @Override
    public int getItemCount()
    {
        //return the size of the dataset
        return eventDataContainer.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private RelativeLayout layout;

        public ViewHolder(RelativeLayout layout)
        {
            super(layout);
        }

        private void registerViews(RelativeLayout layout)
        {

        }

        public void assignEventData(EventDataContainer dataSet)
        {

        }

        @Override
        public void onClick(View v)
        {

        }
    }
}
