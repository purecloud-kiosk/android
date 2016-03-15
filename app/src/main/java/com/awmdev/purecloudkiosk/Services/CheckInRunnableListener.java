package com.awmdev.purecloudkiosk.Services;

/**
 * Created by Reese on 3/13/2016.
 */
public interface CheckInRunnableListener
{
    void onRunnableFinished();
    void onStateChange(int stringID);
}
