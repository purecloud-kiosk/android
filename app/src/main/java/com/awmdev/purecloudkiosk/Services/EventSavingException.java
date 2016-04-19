package com.awmdev.purecloudkiosk.Services;

/**
 * Created by Reese on 4/18/2016.
 */
public class EventSavingException extends Exception
{
    public EventSavingException()
    {
        super("Failed To Save Event To Database");
    }
}
