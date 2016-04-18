package com.awmdev.purecloudkiosk.Adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.awmdev.purecloudkiosk.Model.EventListModel;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.View.Activity.EventListActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>
{
    private EventListActivity eventListActivity;
    private EventListModel eventListModel;

    public EventAdapter(EventListModel eventListModel, EventListActivity eventListActivity)
    {
        this.eventListActivity = eventListActivity;
        this.eventListModel = eventListModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //inflate the view and store its returned value into layout
        LinearLayout layout = (LinearLayout)LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item,parent,false);
        //store the inflated view in a view holder
        ViewHolder holder = new ViewHolder(layout);
        //set the onclick listener for the holder
        layout.setOnClickListener(holder);
        //return the holder
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.assignEventData(eventListModel.getEventListItem(position));
    }

    @Override
    public int getItemCount()
    {
        return eventListModel.getEventListDataSize();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView eventTitleTextView;
        private TextView eventDescriptionTextView;
        private TextView eventDateTextView;
        private NetworkImageView eventImageView;
        private JSONDecorator jsonDecorator;

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
            eventImageView = (NetworkImageView)layout.findViewById(R.id.rli_event_image);
        }

        public void assignEventData(JSONDecorator jsonDecorator)
        {
            //save the jsoneventdecorator for the onclick handling
            this.jsonDecorator = jsonDecorator;
            //assign the data from the json object
            eventTitleTextView.setText(jsonDecorator.getString("title"));
            eventDescriptionTextView.setText(jsonDecorator.getString("description"));
            //grab the time since the epoch from the event
            Long epoch = Long.parseLong(jsonDecorator.getString("startDate"));
            //Create a date instance from the epoch
            Date date = new Date(epoch);
            //format and set the date
            eventDateTextView.setText(new SimpleDateFormat("hh:mm a 'on' MM-dd-yyyy").format(date));
            //string to store image url
            String imageURL;
            //check to see if event has image associated
            if(!(imageURL = jsonDecorator.getString("thumbnailUrl")).equalsIgnoreCase("null"))
            {
                //grab image from url
                eventImageView.setImageUrl(imageURL,HttpRequester.getInstance(null).getImageLoader());
            }
            else
            {
                //place default image instead, this is not the final image nor will it be an http request
               eventImageView.setImageUrl("https://www.google.com/logos/doodles/2015/new-years-eve-2015-5985438795825152-hp.gif",HttpRequester.getInstance(null).getImageLoader());
            }

        }

        @Override
        public void onClick(View v)
        {
            eventListActivity.onEventItemSelected(jsonDecorator);
        }
    }
}
