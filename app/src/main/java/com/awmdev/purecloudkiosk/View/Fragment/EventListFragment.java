package com.awmdev.purecloudkiosk.View.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.awmdev.purecloudkiosk.Adapter.EventAdapter;
import com.awmdev.purecloudkiosk.Decorator.RecyclerListSeparator;
import com.awmdev.purecloudkiosk.Decorator.VerticalSpacingDecorator;
import com.awmdev.purecloudkiosk.Presenter.EventListPresenter;
import com.awmdev.purecloudkiosk.R;

public class EventListFragment extends Fragment
{
    private EventListPresenter eventListPresenter;
    private EventAdapter eventAdapter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //Call the super class
        super.onCreate(savedInstanceState);
        //Create the presenter
        eventListPresenter = new EventListPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //inflate the layout
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.fragment_event_list,container,false);
        //grab the recycler view from the layout
        recyclerView = (RecyclerView)layout.findViewById(R.id.feventlist_recycler_view);
        //assign the layout to the recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //assign the decorator to the recycler
        recyclerView.addItemDecoration(new VerticalSpacingDecorator(2));
        recyclerView.addItemDecoration(new RecyclerListSeparator(getContext(),R.drawable.recycler_list_divider));
        //create the event adapter
        eventAdapter = new EventAdapter();
        //grab an instance of the shared preferences and grab the auth token
        String authToken = getActivity().getSharedPreferences("authorization_preference", Context.MODE_PRIVATE).getString("authToken", "");
        //call the presenter to get the event data
        eventListPresenter.getEventListData(eventAdapter,authToken,0);
        //add the adapter to the recycler
        recyclerView.setAdapter(eventAdapter);
        //return the layout
        return layout;
    }

    public class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener
    {

    }
}
