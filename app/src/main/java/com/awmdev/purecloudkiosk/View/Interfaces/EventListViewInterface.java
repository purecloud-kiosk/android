package com.awmdev.purecloudkiosk.View.Interfaces;

/**
 * Created by Reese on 2/5/2016.
 */
public interface EventListViewInterface
{
    void notifyEventAdapterOfDataSetChange();
    void notifySuccessfulEventDataRefresh();
    void setEmptyStateViewVisibility(boolean visible);
}
