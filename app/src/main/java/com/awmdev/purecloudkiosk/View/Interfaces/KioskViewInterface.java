package com.awmdev.purecloudkiosk.View.Interfaces;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Reese on 2/5/2016.
 */
public interface KioskViewInterface
{
    void setEventImage(ImageLoader imageLoader, String url);
    void setEventNameTextView(String eventName);
}
