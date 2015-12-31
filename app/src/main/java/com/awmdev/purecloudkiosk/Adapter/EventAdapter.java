package com.awmdev.purecloudkiosk.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.awmdev.purecloudkiosk.Model.JSONEventWrapper;
import com.awmdev.purecloudkiosk.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>
{
    private List<JSONEventWrapper> JSONEventWrapperList = new ArrayList();

    public void appendDataSet(Collection<JSONEventWrapper> eventDataCollection)
    {
        //assign the passed in dataset to the list
        JSONEventWrapperList.addAll(eventDataCollection);
        //notify the adapter of the dataset change
        notifyDataSetChanged();
    }

    public void clearDataSet()
    {
        //clear the dataset from the list
        JSONEventWrapperList.clear();
        //notify of a dataset change
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //inflate the view and store its returned value into layout
        LinearLayout layout = (LinearLayout)LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_item,parent,false);
        //store the inflated view in a view holder
        ViewHolder holder = new ViewHolder(layout);
        //return the holder
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.assignEventData(JSONEventWrapperList.get(position));
    }

    @Override
    public int getItemCount()
    {
        //return the size of the dataset
        return JSONEventWrapperList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView eventTitleTextView;
        private TextView eventDescriptionTextView;
        private TextView eventDateTextView;
        private ImageView eventImageView;

        public ViewHolder(LinearLayout layout)
        {
            //pass the layout to the super class
            super(layout);
            //grab all the layout components
            registerViews(layout);
        }

        private void registerViews(LinearLayout layout)
        {
            //grab all the associated components from the view
            eventTitleTextView = (TextView)layout.findViewById(R.id.rli_event_title);
            eventDescriptionTextView = (TextView)layout.findViewById(R.id.rli_event_description);
            eventDateTextView = (TextView)layout.findViewById(R.id.rli_event_time);
            eventImageView = (ImageView)layout.findViewById(R.id.rli_event_image);
        }

        public void assignEventData(JSONEventWrapper jsonEventWrapper)
        {
            //assign the data from the json object
            eventTitleTextView.setText(jsonEventWrapper.getString("title"));
            eventDescriptionTextView.setText(jsonEventWrapper.getString("description"));
            eventDateTextView.setText(jsonEventWrapper.getString("date"));
            //check to see if the image is null

            //not null image, set the image in the view
            System.out.println(jsonEventWrapper.getString("image_url"));
            //download image as it is null, then set it in the wrapper and apply it
        }

        @Override
        public void onClick(View v)
        {

        }
    }
}
