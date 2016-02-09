package com.awmdev.purecloudkiosk.Presenter;

import android.util.Log;
import android.util.SparseArray;

import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.View.Interfaces.BarcodeViewInterface;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;

public class BarcodePresenter
{
    private final String TAG = BarcodePresenter.class.getSimpleName();
    private BarcodeViewInterface barcodeViewInterface;
    private boolean checkInProgress = false;

    public BarcodePresenter(BarcodeViewInterface barcodeViewInterface)
    {
        this.barcodeViewInterface = barcodeViewInterface;
    }

    public void onBarcodeDetected(Detector.Detections<Barcode> detections)
    {
        synchronized (this)
        {
            if(checkInProgress)
                return;
            checkInProgress =true;
        }
        try
        {
            //grab the barcode array
            SparseArray<Barcode> barcodeArray = detections.getDetectedItems();
            //grab the individual barcode from the array
            Barcode barcode = barcodeArray.valueAt(0);
            //grab the raw value from the string array and create the json object
            final JSONDecorator jsonBarcodeObject = new JSONDecorator(barcode.rawValue);
            //grab an instance of the http requester for the image
            final HttpRequester httpRequester = HttpRequester.getInstance(null);
            //display the dialog
            barcodeViewInterface.displayCheckInDialog(httpRequester.getImageLoader(),jsonBarcodeObject.getString("image")
                    ,jsonBarcodeObject.getString("name"));
        }
        catch(JSONException jx)
        {
            Log.d(TAG,"Improperly formatted barcode");
        }
    }

    public void onCheckInSuccessful()
    {
        //this is where we will send our event check in

        //this where we release our lock
        detectionComplete();
    }

    public void onLoginSelected()
    {
        synchronized (this)
        {
            if(checkInProgress)
                return;
            checkInProgress = true;
        }
    }

    public void detectionComplete()
    {
        synchronized (this)
        {
            checkInProgress = false;
        }
    }
}
