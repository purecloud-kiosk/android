package com.awmdev.purecloudkiosk.View.Interfaces;

import android.os.Parcelable;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Reese on 2/5/2016.
 */
public interface DetailedEventViewInterface
{
    //int to store the selection for the textviews
    int EVENT_NAME = 0;
    int START_DATE = EVENT_NAME + 1;
    int END_DATE = START_DATE + 1;
    int DESCRIPTION = END_DATE + 1;
    int LOCATION = DESCRIPTION + 1;
    int PRIVACY = LOCATION + 1;
    //methods
    void assignDataToView(Parcelable jsonEventDecorator);
    void assignTextView(int selection, String textSelection);
    void setImageUrl(String url,ImageLoader imageLoader);
}
