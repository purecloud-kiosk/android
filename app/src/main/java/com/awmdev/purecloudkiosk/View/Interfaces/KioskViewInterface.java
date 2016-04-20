package com.awmdev.purecloudkiosk.View.Interfaces;

import android.graphics.Bitmap;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Reese on 2/5/2016.
 */
public interface KioskViewInterface
{
    void setEventImage(Bitmap bitmap);
    void setEventImageByResId(int resId);
    void setEventNameTextView(String eventName);
}
