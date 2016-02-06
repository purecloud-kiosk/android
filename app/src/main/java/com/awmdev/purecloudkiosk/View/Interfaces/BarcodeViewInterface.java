package com.awmdev.purecloudkiosk.View.Interfaces;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Reese on 2/5/2016.
 */
public interface BarcodeViewInterface
{
    void displayLogInDialog();
    void displayCheckInDialog(final ImageLoader imageLoader,final  String url, final String name);
}
