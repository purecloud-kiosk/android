package com.awmdev.purecloudkiosk.Presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;
import android.widget.Toast;

import com.awmdev.purecloudkiosk.View.Activity.BarcodeActivity;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class BarcodePresenter
{
    private boolean detectionInProgress = false;
    private BarcodeActivity barcodeActivity;

    public BarcodePresenter(BarcodeActivity barcodeActivity)
    {
        this.barcodeActivity = barcodeActivity;
    }

    public void onBarcodeDetected(Detector.Detections<Barcode> detections)
    {
        synchronized (this)
        {
            if(detectionInProgress)
                return;
            detectionInProgress=true;
        }
        SparseArray<Barcode> barcodeArray = detections.getDetectedItems();
        detectionComplete();
    }

    public void detectionComplete()
    {
        synchronized (this)
        {
            detectionInProgress = false;
        }
    }
}
