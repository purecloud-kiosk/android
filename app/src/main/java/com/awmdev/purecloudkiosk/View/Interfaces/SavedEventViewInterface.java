package com.awmdev.purecloudkiosk.View.Interfaces;

import android.os.Parcelable;

import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;

/**
 * Created by Reese on 4/10/2016.
 */
public interface SavedEventViewInterface
{
    void onLaunchKioskSelected(Parcelable jsonEventParcelable);
    void displaySnackbarForDeletedItem(JSONDecorator jsonDecorator,int itemPosition);
}
