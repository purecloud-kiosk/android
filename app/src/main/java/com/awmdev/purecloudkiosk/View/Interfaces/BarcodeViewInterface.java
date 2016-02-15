package com.awmdev.purecloudkiosk.View.Interfaces;

import com.android.volley.toolbox.ImageLoader;
import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;

import java.util.Map;

/**
 * Created by Reese on 2/5/2016.
 */
public interface BarcodeViewInterface
{
    void displayLogInDialog();
    void displayCheckInDialog(final ImageLoader imageLoader,final JSONDecorator jsonDecorator);
    void postCheckIn(Map<String,Object> jsonMap);
}
