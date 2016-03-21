package com.awmdev.purecloudkiosk.Presenter;

import android.util.Log;
import android.util.SparseArray;

import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.Model.BarcodeModel;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.View.Interfaces.BarcodeViewInterface;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class BarcodePresenter
{
    private final String TAG = BarcodePresenter.class.getSimpleName();
    private BarcodeViewInterface barcodeViewInterface;
    private boolean checkInProgress = false;
    private BarcodeModel barcodeModel;

    public BarcodePresenter(BarcodeViewInterface barcodeViewInterface,BarcodeModel barcodeModel)
    {
        this.barcodeViewInterface = barcodeViewInterface;
        this.barcodeModel = barcodeModel;
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
            //check to see if the current barcode is the same as the last
            if(!barcodeModel.recentlyScanned(barcode.rawValue))
            {
                //save the current barcode to the model to prevent duplicate scans
                barcodeModel.addScannedBarcode(barcode.rawValue);
                //grab the raw value from the string array and create the json object
                JSONDecorator jsonBarcodeObject = new JSONDecorator(barcode.rawValue);
                //grab an instance of the http requester for the image
                HttpRequester httpRequester = HttpRequester.getInstance(null);
                //display the dialog
                barcodeViewInterface.displayCheckInDialog(httpRequester.getImageLoader(),jsonBarcodeObject);
                //post the check in
                onCheckInSuccessful(jsonBarcodeObject);
            }
            else
            {
                //release the lock since this barcode has already been scanned
                detectionComplete();
            }
        }
        catch(JSONException jx)
        {
            //log the error
            Log.e(TAG,"Improperly formatted barcode");
            //release the lock
            detectionComplete();
        }
    }

    public void onCheckInSuccessful(JSONDecorator jsonCheckInDecorator)
    {
        //create a map to store the check-in
        Map<String, Object> map = new HashMap<>();
        //add the event id
        map.put("eventID",barcodeModel.getStringFromDecorator("id"));
        //grab the json map from checkInDecorator
        Map<String,Object> checkInMap = jsonCheckInDecorator.getMap();
        //update the check in time for the check in
        checkInMap.put("timestamp",System.currentTimeMillis());
        //add the check in data to the map
        map.put("checkIn",checkInMap);
        //send the results to the service
        barcodeViewInterface.postCheckIn(map);
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
