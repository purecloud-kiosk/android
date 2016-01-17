package com.awmdev.purecloudkiosk.View.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.awmdev.purecloudkiosk.Adapter.EventAdapter;
import com.awmdev.purecloudkiosk.Decorator.RecyclerListSeparator;
import com.awmdev.purecloudkiosk.Decorator.VerticalSpacingDecorator;
import com.awmdev.purecloudkiosk.Model.EventListModel;
import com.awmdev.purecloudkiosk.Presenter.EventListPresenter;
import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.View.Activity.EventListActivity;

public class EventListFragment extends Fragment
{
    private EventListPresenter eventListPresenter;
    private ImageView emptyStateImageView;
    private EventListModel eventListModel;
    private EventAdapter eventAdapter;
    private RecyclerView recyclerView;
    private Bundle savedInstanceState;
    private Menu fragmentMenu;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //Call the super class
        super.onCreate(savedInstanceState);
        //check to see if the bundle is null, if not null restore the bundle else create a new one
        if(savedInstanceState != null)
            eventListModel = (EventListModel)savedInstanceState.getParcelable("parcelable");
        else
        {
            //create a new instance of the model
            eventListModel = new EventListModel();
            //grab the authentication token from the activity and set it
            eventListModel.setAuthenticationToken(getActivity().getSharedPreferences("authorization_preference", Context.MODE_PRIVATE).getString("authToken", ""));
        }
        //Create the presenter
        eventListPresenter = new EventListPresenter(this,eventListModel);
        //temporally save the bundle to restore the menu state
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //inflate the layout
        RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.fragment_event_list,container,false);
        //grab the recycler view from the layout
        recyclerView = (RecyclerView)layout.findViewById(R.id.feventlist_recycler_view);
        //grab the image view from the layout
        emptyStateImageView = (ImageView)layout.findViewById(R.id.feventlist_imageview);
        //assign the layout to the recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //assign the decorator to the recycler
        recyclerView.addItemDecoration(new VerticalSpacingDecorator(2));
        recyclerView.addItemDecoration(new RecyclerListSeparator(getContext(),R.drawable.recycler_list_divider));
        //add the scroll listener to the recycler view
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager)recyclerView.getLayoutManager(),5));
        //create the event adapter
        eventAdapter = new EventAdapter(eventListModel,(EventListActivity)getActivity());
        //if the bundle is null, request data from the presenter
        if(savedInstanceState == null)
            eventListPresenter.getEventListData();
        //add the adapter to the recycler
        recyclerView.setAdapter(eventAdapter);
        //set that the fragment has a options menu
        setHasOptionsMenu(true);
        //return the layout
        return layout;
    }

    public void notifyEventAdapterOfDataSetChange()
    {
        eventAdapter.notifyDataSetChanged();
    }

    public void setEmptyStateViewVisibility(boolean visible)
    {
        if(visible)
            emptyStateImageView.setVisibility(View.VISIBLE);
        else
            emptyStateImageView.setVisibility(View.GONE);
    }

    public int getAdapterViewPosition()
    {
        return ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
    }

    public void setAdapterViewPosition(int position)
    {
        ((LinearLayoutManager)recyclerView.getLayoutManager()).scrollToPosition(position);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        //pass the bundle to the super class
        super.onSaveInstanceState(outState);
        //write the model to the bundle
        outState.putParcelable("parcelable",eventListModel);
        //grab the searchview from the menu
        MenuItem searchViewMenuItem = fragmentMenu.findItem(R.id.menu_action_search);
        //write the current state of the menu
        outState.putBoolean("actionMenuExpanded", searchViewMenuItem.isActionViewExpanded());
        //grab the edit text from the search box
        EditText editText = (EditText)searchViewMenuItem.getActionView().findViewById(R.id.search_edit_text);
        //grab the string from the edit text and store it in the bundle
        outState.putString("searchBoxValue", editText.getText().toString());
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater)
    {
        // Inflate the menu
        inflater.inflate(R.menu.event_list_menu, menu);
        //save the menu state locally
        fragmentMenu = menu;
        //grab the action item from the menu
        final MenuItem menuItem = menu.findItem(R.id.menu_action_search);
        //add the item on click listener
        menuItem.getActionView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                menu.performIdentifierAction(menuItem.getItemId(), 0);
            }
        });
        //grab the layout for the edit text
        RelativeLayout layout = (RelativeLayout) menuItem.getActionView();
        EditText editText = (EditText) layout.findViewById(R.id.search_edit_text);
        //restore the menu state before adding the text change listener to prevent duplicate searches
        restoreMenuState();
        //Add the text watcher to the edit text
        editText.addTextChangedListener(new SearchTextWatcher());
        //call the super class
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void restoreMenuState()
    {
        //check to see if the bundle is null
        if(savedInstanceState != null)
        {
            //grab the search view from the menu
            MenuItem searchViewMenuItem = fragmentMenu.findItem(R.id.menu_action_search);
            //grab the edit text from the search view
            EditText editText = (EditText) searchViewMenuItem.getActionView().findViewById(R.id.search_edit_text);
            //restore the edittext state
            editText.setText(savedInstanceState.getString("searchBoxValue"));
            //restore the searchview state
            if(savedInstanceState.getBoolean("actionMenuExpanded",false))
                searchViewMenuItem.expandActionView();
            //set the bundle to null to free memory
            savedInstanceState = null;
        }
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
            eventListPresenter.onSearchTextChanged(editable.toString());
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
            eventListPresenter.onScrolled(visibleItemCount,totalItemCount,firstVisibleItem, visibleThreshold);
        }
    }
}
