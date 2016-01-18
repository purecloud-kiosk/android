package com.awmdev.purecloudkiosk.View.Fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.awmdev.purecloudkiosk.Decorator.JSONEventDecorator;
import com.awmdev.purecloudkiosk.Presenter.DetailedEventPresenter;
import com.awmdev.purecloudkiosk.R;

public class DetailedEventFragment extends Fragment
{
    //variables to store all of the views in the fragment
    private DetailedEventPresenter detailedEventPresenter;
    private NetworkImageView eventImage;
    private TextView eventOrganization;
    private TextView eventDescription;
    private TextView eventLocation;
    private TextView eventPrivacy;
    private TextView eventName;
    private TextView eventDate;
    //int to store the selection for the textviews
    public static final int EVENT_NAME = 0;
    public static final int DATE = EVENT_NAME + 1;
    public static final int DESCRIPTION = DATE + 1;
    public static final int LOCATION = DESCRIPTION + 1;
    public static final int ORGANIZATION = LOCATION + 1;
    public static final int PRIVACY = ORGANIZATION + 1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //call the super class
        super.onCreate(savedInstanceState);
        //create the presenter
        detailedEventPresenter = new DetailedEventPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //inflate the view
        ScrollView scrollView = (ScrollView)inflater.inflate(R.layout.fragment_detailed_event,container,false);
        //grab the components from the view,starting with event name
        eventName = (TextView)scrollView.findViewById(R.id.fdevent_event_name);
        //grab the organization textview
        eventOrganization = (TextView)scrollView.findViewById(R.id.fdevent_organization);
        //grab the privacy textview
        eventPrivacy = (TextView)scrollView.findViewById(R.id.fdevent_privacy);
        //grab the event location textview
        eventLocation = (TextView)scrollView.findViewById(R.id.fdevent_location);
        //grab the location textview
        eventDate = (TextView)scrollView.findViewById(R.id.fdevent_date);
        //grab the description of the event
        eventDescription = (TextView)scrollView.findViewById(R.id.fdevent_description);
        //grab the network image view banner
        eventImage = (NetworkImageView)scrollView.findViewById(R.id.fdevent_imageview);
        //populate the view with the passed bundle
        JSONEventDecorator jsonEventDecorator;
        if((jsonEventDecorator = getDecoratorFromIntent()) != null)
            detailedEventPresenter.populateView(jsonEventDecorator);
        //return the inflated view
        return scrollView;
    }

    public void assignDataFromActivity(Parcelable jsonEventDecorator)
    {
        detailedEventPresenter.populateView((JSONEventDecorator)jsonEventDecorator);
    }

    private JSONEventDecorator getDecoratorFromIntent()
    {
        return (JSONEventDecorator) getActivity().getIntent().getExtras().getParcelable("parcelable");
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
            case ORGANIZATION:
                textView = eventOrganization;
                break;
            case PRIVACY:
                textView = eventPrivacy;
                break;
            case LOCATION:
                textView = eventLocation;
                break;
            case DATE:
                textView = eventDate;
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
