package com.awmdev.purecloudkiosk.Model;

import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

/**
 * Created by Reese on 2/15/2016.
 */
public class BarcodeModel
{
    private JSONDecorator jsonEventDecorator;
    private Queue recentScans = new ArrayDeque(10);

    public void setJsonEventDecorator(JSONDecorator jsonEventDecorator)
    {
        this.jsonEventDecorator = jsonEventDecorator;
    }

    public String getStringFromDecorator(String request)
    {
        return jsonEventDecorator.getString(request);
    }

    public boolean recentlyScanned(String barcodeDetection)
    {
        for(Iterator<String> it = recentScans.iterator(); it.hasNext();)
            if(barcodeDetection.equals(it.next()))
                return true;
        return false;
    }

    public void addScannedBarcode(String barcodeDetection)
    {
        if(recentScans.size() == 10)
            recentScans.remove();
        recentScans.add(barcodeDetection);
    }
}
