package com.awmdev.purecloudkiosk.View.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.awmdev.purecloudkiosk.Adapter.EventAdapter;
import com.awmdev.purecloudkiosk.Decorator.RecyclerListSeparator;
import com.awmdev.purecloudkiosk.Decorator.VerticalSpacingDecorator;
import com.awmdev.purecloudkiosk.Model.JSONEventWrapper;
import com.awmdev.purecloudkiosk.Presenter.EventListPresenter;
import com.awmdev.purecloudkiosk.R;

import java.util.List;

public class EventListFragment extends Fragment
{
    private EventListPresenter eventListPresenter;
    private EventAdapter eventAdapter;
    private RecyclerView recyclerView;
    private String authToken;

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
        //add the scroll listener to the recycler view
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager)recyclerView.getLayoutManager(),5));
        //create the event adapter
        eventAdapter = new EventAdapter();
        //grab an instance of the shared preferences and grab the auth token
        authToken = getActivity().getSharedPreferences("authorization_preference", Context.MODE_PRIVATE).getString("authToken", "");
        //call the presenter to get the event data
        eventListPresenter.getEventListData(authToken);
        //add the adapter to the recycler
        recyclerView.setAdapter(eventAdapter);
        //set that the fragment has a options menu
        setHasOptionsMenu(true);
        //return the layout
        return layout;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater)
    {
        // Inflate the menu
        inflater.inflate(R.menu.event_list_menu, menu);
        //grab the action item from the menu
        final MenuItem menuItem = menu.findItem(R.id.menu_action_search);
        //add the item on click listener
        menuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.performIdentifierAction(menuItem.getItemId(), 0);
            }
        });
        //grab the layout for the edit text
        RelativeLayout layout = (RelativeLayout) menuItem.getActionView();
        EditText editText = (EditText) layout.findViewById(R.id.search_edit_text);
        //Add the text watcher to the edit text
        editText.addTextChangedListener(new SearchTextWatcher());
        //call the super class
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void applyFilterToEventAdapter(List<Integer> filteredItems)
    {
        eventAdapter.applyFilterToDataSet(filteredItems);
    }

    public void removeFilterFromEventAdapter()
    {
        eventAdapter.removeFilter();
    }

    public List<JSONEventWrapper> getEventAdapterDataSet()
    {
        return eventAdapter.getEventAdapterDataSet();
    }

    public void appendDataToEventAdapter(List<JSONEventWrapper> jsonEventWrapperList)
    {
        eventAdapter.appendDataSet(jsonEventWrapperList);
    }

    public class SearchTextWatcher implements TextWatcher
    {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after){}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){}

        @Override
        public void afterTextChanged(Editable editable)
        {
            eventListPresenter.onSearchTextEntered(editable.toString());
        }
    }

    public class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener
    {
        private LinearLayoutManager linearLayoutManager;
        private int visibleThreshold;


        public EndlessRecyclerViewScrollListener(LinearLayoutManager linearLayoutManager, int visibleThreshold)
        {
            this.linearLayoutManager = linearLayoutManager;
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
        {
            //call the super class
            super.onScrolled(recyclerView, dx, dy);
            //number of items current visible in the view
            int visibleItemCount = recyclerView.getChildCount();
            //number of items in the layout
            int totalItemCount = linearLayoutManager.getItemCount();
            //the position of the item in the top of the view
            int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
            //call the presenter
            eventListPresenter.onScrolled(visibleItemCount,totalItemCount,firstVisibleItem, visibleThreshold,authToken);
        }
    }
}
