package com.awmdev.purecloudkiosk.View.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.awmdev.purecloudkiosk.Adapter.SavedEventAdapter;
import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.Decorator.RecyclerListSeparator;
import com.awmdev.purecloudkiosk.Decorator.VerticalSpacingDecorator;
import com.awmdev.purecloudkiosk.Model.SavedEventModel;
import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.View.Interfaces.SavedEventViewInterface;

public class SavedEventActivity extends AppCompatActivity
        implements View.OnClickListener, SavedEventViewInterface
{
    private SavedEventAdapter savedEventAdapter;
    private SavedEventModel savedEventModel;
    private RecyclerView recyclerView;
    private Snackbar deletionSnackBar;


    public void onCreate(Bundle savedInstanceState)
    {
        //call the super and pass the bundle
        super.onCreate(savedInstanceState);
        //set the content view
        setContentView(R.layout.activity_saved_event_list);
        //create the model, if the bundle is null
        savedEventModel = new SavedEventModel(this);
        //grab the associated components from the view
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_event_list_toolbar);
        //set the toolbar as the support action bar
        setSupportActionBar(toolbar);
        //set the toolbar to have a back navigation button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //add a listener to the toolbar back button
        toolbar.setNavigationOnClickListener(this);
        //change the title on the toolbar to reflect the correct view
        getSupportActionBar().setTitle("Saved Events");
        //grab the recycler from the list
        recyclerView = (RecyclerView) findViewById(R.id.activty_save_recycler);
        //set the layout for the recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //assign the decorator to the recycler
        recyclerView.addItemDecoration(new VerticalSpacingDecorator(2));
        recyclerView.addItemDecoration(new RecyclerListSeparator(this,R.drawable.recycler_list_divider));
        //create the saved event adapter
        savedEventAdapter = new SavedEventAdapter(this,savedEventModel);
        //set the adapter on the recycler
        recyclerView.setAdapter(savedEventAdapter);
    }

    @Override
    protected void onDestroy()
    {
        //delete any pending events
        savedEventModel.deleteStagedSavedEvent();
        //call the super
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        super.onBackPressed();
    }

    @Override
    public void onLaunchKioskSelected(Parcelable jsonEventParcelable)
    {
        Intent intent =  new Intent(getApplicationContext(),KioskActivity.class);
        intent.putExtra("parcelable",jsonEventParcelable);
        startActivity(intent);
    }

    @Override
    public void displaySnackbarForDeletedItem(JSONDecorator jsonDecorator, int itemPosition)
    {
        //stage the event for deletion
        savedEventModel.stageSavedEventForDeletion(itemPosition);
        //notify the adapter of the dataset change
        savedEventAdapter.notifyDataSetChanged();
        //grab the localized string for deleted
        String snackbarString = jsonDecorator.getString("title")+" "+getResources().getString(R.string.snackbar_deleted);
        //build the snackbar to be displayed
        deletionSnackBar = Snackbar.make(recyclerView, snackbarString,Snackbar.LENGTH_LONG)
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event)
                    {
                       if(event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT
                               || event == Snackbar.Callback.DISMISS_EVENT_SWIPE
                               || event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE)
                       {
                            savedEventModel.deleteStagedSavedEvent();
                       }
                    }
                })
                .setAction("UNDO",new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //restore the deleted event
                        savedEventModel.restoreStagedSavedEvent();
                        //notify the adapter of the dataset change
                        savedEventAdapter.notifyDataSetChanged();
                    }
                });
        //display the snackbar
        deletionSnackBar.show();
    }
}
