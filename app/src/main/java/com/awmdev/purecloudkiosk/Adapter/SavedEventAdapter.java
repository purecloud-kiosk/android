package com.awmdev.purecloudkiosk.Adapter;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.Model.SavedEventModel;
import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.View.Interfaces.SavedEventViewInterface;
import com.awmdev.purecloudkiosk.Adapter.Listener.SwipeToDeleteTouchListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SavedEventAdapter extends RecyclerView.Adapter<SavedEventAdapter.ViewHolder>
{
    private SavedEventViewInterface savedEventViewInterface;
    private SavedEventModel savedEventModel;

    public SavedEventAdapter(SavedEventViewInterface savedEventViewInterface, SavedEventModel savedEventModel)
    {
        this.savedEventViewInterface = savedEventViewInterface;
        this.savedEventModel = savedEventModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //inflate the view
        LinearLayout linearLayout = (LinearLayout) LayoutInflater
                .from(parent.getContext()).inflate(R.layout.saved_event_list_item,parent,false);
        //create the view holder
        ViewHolder viewHolder = new ViewHolder(linearLayout);
        //set the onclick listener
        linearLayout.setOnClickListener(viewHolder);
        //create the swipe to delete touch listener
        SwipeToDeleteTouchListener swipeToDeleteTouchListener =
                new SwipeToDeleteTouchListener(linearLayout);
        //set the on touch listener
        linearLayout.setOnTouchListener(swipeToDeleteTouchListener);
        //set the listener for when the swipe action is complete
        swipeToDeleteTouchListener.setSwipeDetectedListener(viewHolder);
        //return the holder
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.bindDataSetToViewHolder(position);
    }

    @Override
    public int getItemCount()
    {
        return savedEventModel.getSavedEventDataSetSize();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, SwipeToDeleteTouchListener.OnSwipeDetectedListener
    {

        private TextView eventTitleTextView;
        private TextView eventDescriptionTextView;
        private TextView eventDateTextView;
        private ImageView eventImageView;
        private int itemPosition;

        public ViewHolder(View itemView)
        {
            //call the super and pass the view
            super(itemView);
            //initialize the view
            initializeViewHolder(itemView);
        }

        @Override
        public void onClick(View v)
        {
            //call the activity to navigate to the next activity
            savedEventViewInterface.onLaunchKioskSelected(savedEventModel.getSavedEventListItem(itemPosition));
        }

        @Override
        public void onSwipeComplete()
        {
            //grab the json decorator that we are deleting
            JSONDecorator jsonDecorator = savedEventModel.getSavedEventListItem(itemPosition);
            //display the snackbar for the deleted item
            savedEventViewInterface.displaySnackbarForDeletedItem(jsonDecorator,itemPosition);
        }

        private void initializeViewHolder(View view)
        {
            //grab all the associated components from the view
            eventTitleTextView = (TextView) view.findViewById(R.id.seli_event_title);
            eventDescriptionTextView = (TextView) view.findViewById(R.id.seli_event_description);
            eventDateTextView = (TextView) view.findViewById(R.id.seli_event_time);
            eventImageView = (ImageView) view.findViewById(R.id.seli_event_image);
        }

        public void bindDataSetToViewHolder(int itemPosition)
        {
            //save the item position locally for deletion and retrieval purposes
            this.itemPosition = itemPosition;
            //grab the json decorator from the model
            JSONDecorator jsonDecorator = savedEventModel.getSavedEventListItem(itemPosition);
            //assign the data from the json object
            eventTitleTextView.setText(jsonDecorator.getString("title"));
            eventDescriptionTextView.setText(jsonDecorator.getString("description"));
            //grab the time since the epoch from the event
            Long epoch = Long.parseLong(jsonDecorator.getString("startDate"));
            //Create a date instance from the epoch
            Date date = new Date(epoch);
            //format and set the date
            eventDateTextView.setText(new SimpleDateFormat("hh:mm a 'on' MM-dd-yyyy").format(date));
            //load the image from the supplied file
            eventImageView.setImageBitmap(BitmapFactory.decodeFile((jsonDecorator.getString("thumbPath"))));
        }
    }
}
