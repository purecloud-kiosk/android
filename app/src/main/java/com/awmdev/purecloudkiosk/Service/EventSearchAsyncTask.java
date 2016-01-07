package com.awmdev.purecloudkiosk.Service;

import android.os.AsyncTask;

import com.awmdev.purecloudkiosk.Model.JSONEventWrapper;
import com.awmdev.purecloudkiosk.View.Fragment.EventListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Reese on 1/6/2016.
 */
public class EventSearchAsyncTask extends AsyncTask <String,Void,List<Integer>>
{
    private EventListFragment eventListFragment;

    public EventSearchAsyncTask(EventListFragment eventListFragment)
    {
        this.eventListFragment = eventListFragment;
    }

    @Override
    protected List<Integer> doInBackground(String... patterns)
    {
        //List to hold all of the matched events
        List<Integer> matches = new ArrayList();
        //Pattern that were searching for
        String pattern = patterns[0].toLowerCase();
        //length of the pattern
        int n = pattern.length();
        //map of all of the bad character shifts for the pattern
        Map<Character, Integer> rightMostIndexes = preprocessForBadCharacterShift(pattern);
        //grab the list that were going to search through
        List<JSONEventWrapper> searchableList = eventListFragment.getEventAdapterDataSet();
        //outer loop to handle iterating through the event
        for(JSONEventWrapper wrapper: searchableList)
        {
            //grab the event name from the list
            String text = wrapper.getString("title").toLowerCase();
            //length of the text
            int m = text.length();
            //search the text for the pattern
            int alignedAt = 0;
            //loop to handle the searching of the text, actually boyermoore algorithm
            searchingLoop:
            while (alignedAt + (n - 1) < m)
            {
                for (int indexInPattern = n - 1; indexInPattern >= 0; indexInPattern--)
                {
                    int indexInText = alignedAt + indexInPattern;
                    char x = text.charAt(indexInText);
                    char y = pattern.charAt(indexInPattern);
                    if (indexInText >= m)
                        break;
                    if (x != y)
                    {
                        Integer r = rightMostIndexes.get(x);
                        if (r == null)
                        {
                            alignedAt = indexInText + 1;
                        }
                        else
                        {
                            int shift = indexInText - (alignedAt + r);
                            alignedAt += shift > 0 ? shift : alignedAt + 1;
                        }
                        break;
                    }
                    else
                    {
                        if (indexInPattern == 0)
                        {
                            //super inefficient, will fix later
                            matches.add(searchableList.indexOf(wrapper));
                            break searchingLoop;
                        }
                    }
                }
            }
        }
        return matches;
    }

    @Override
    protected void onPostExecute(List<Integer> filteredEvents)
    {
        eventListFragment.applyFilterToEventAdapter(filteredEvents);
    }

    private Map<Character, Integer> preprocessForBadCharacterShift(String pattern)
    {
        Map<Character, Integer> map = new HashMap<Character, Integer>();
        for (int i = pattern.length() - 1; i >= 0; i--)
        {
            char c = pattern.charAt(i);
            if (!map.containsKey(c)) map.put(c, i);
        }
        return map;
    }
}
