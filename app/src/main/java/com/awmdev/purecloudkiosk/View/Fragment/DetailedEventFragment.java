package com.awmdev.purecloudkiosk.View.Fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.Presenter.DetailedEventPresenter;
import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.View.Interfaces.DetailedEventViewInterface;
import com.awmdev.purecloudkiosk.View.Interfaces.LaunchKioskInterface;

public class DetailedEventFragment extends Fragment implements DetailedEventViewInterface
{
    //variables to store all of the views in the fragment
    private DetailedEventPresenter detailedEventPresenter;
    private Parcelable jsonEventParcelable;
    private NetworkImageView eventImage;
    private TextView eventDescription;
    private ImageView splashImageView;
    private TextView eventLocation;
    private ScrollView scrollView;
    private TextView eventPrivacy;
    private TextView eventName;
    private TextView eventStartDate;
    private TextView eventEndDate;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //call the super class
        super.onCreate(savedInstanceState);
        //create the presenter
        detailedEventPresenter = new DetailedEventPresenter(this);
        //set the option menu to true
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //inflate the view
        RelativeLayout relativeLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_detailed_event,container,false);
        //grab the components from the view,starting with event name
        eventName = (TextView)relativeLayout.findViewById(R.id.fdevent_event_name);
        //grab the privacy textview
        eventPrivacy = (TextView)relativeLayout.findViewById(R.id.fdevent_privacy);
        //grab the event location textview
        eventLocation = (TextView)relativeLayout.findViewById(R.id.fdevent_location);
        //grab the startDate textview
        eventStartDate = (TextView)relativeLayout.findViewById(R.id.fdevent_start_date);
        // grab the endDate textView
        eventEndDate = (TextView)relativeLayout.findViewById(R.id.fdevent_end_date);
        //grab the description of the event
        eventDescription = (TextView)relativeLayout.findViewById(R.id.fdevent_description);
        //grab the network image view banner
        eventImage = (NetworkImageView)relativeLayout.findViewById(R.id.fdevent_imageview);
        //grab the splash view
        splashImageView = (ImageView)relativeLayout.findViewById(R.id.fdevent_splash_view);
        //grab the main layout for event information
        scrollView = (ScrollView)relativeLayout.findViewById(R.id.fdevent_main_layout);
        //populate the view with the passed bundle
        assignDataToView(getDecoratorFromIntent());
        //return the inflated view
        return relativeLayout;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater)
    {
        // Inflate the menu
        inflater.inflate(R.menu.detailed_event_menu, menu);
        //grab the launch menu item
        MenuItem menuItem = menu.findItem(R.id.menu_start_kiosk);
        //add the item on click listener
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                ((LaunchKioskInterface)getActivity()).onLaunchKioskSelected(jsonEventParcelable);
                return true;
            }
        });
        //call the super class
        super.onCreateOptionsMenu(menu, inflater);
    }


    public void assignDataToView(Parcelable jsonEventDecorator)
    {
        //save the parcelable locally for starting the kiosk
        jsonEventParcelable = jsonEventDecorator;
        //check to see if the decorator is null and change the view appropriately
        if(jsonEventDecorator == null)
        {
           scrollView.setVisibility(View.GONE);
           splashImageView.setVisibility(View.VISIBLE);
        }
        else
        {
            scrollView.setVisibility(View.VISIBLE);
            splashImageView.setVisibility(View.GONE);
            detailedEventPresenter.populateView((JSONDecorator) jsonEventDecorator);
        }
    }


    private JSONDecorator getDecoratorFromIntent()
    {
        try
        {
            return (JSONDecorator) getActivity().getIntent().getExtras().getParcelable("parcelable");
        }
        catch(NullPointerException npe)
        {
            return null;
        }
    }

    public void assignTextView(int selection, String textSelection)
    {
        //temp variable to store the selected textview into
        TextView textView = null;
        //switch to select the appropriate text view
        switch(selection)
        {
            case EVENT_NAME:
                textView = eventName;
                break;
            case PRIVACY:
                textView = eventPrivacy;
                break;
            case LOCATION:
                textView = eventLocation;
                break;
            case START_DATE:
                textView = eventStartDate;
                break;
            case END_DATE:
                textView = eventEndDate;
                break;
            case DESCRIPTION:
                textView = eventDescription;
                break;
        }
        //set the text to the selected textview
        textView.setText(textSelection);
    }

    public void setImageUrl(String url,ImageLoader imageLoader)
    {
        eventImage.setImageUrl(url,imageLoader);
    }
}
