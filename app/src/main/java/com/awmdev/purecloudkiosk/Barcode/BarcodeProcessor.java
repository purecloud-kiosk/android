package com.awmdev.purecloudkiosk.Barcode;

import com.awmdev.purecloudkiosk.Presenter.BarcodePresenter;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by Reese on 1/28/2016.
 */
public class BarcodeProcessor implements Detector.Processor<Barcode>
{
    private BarcodePresenter barcodePresenter;

    public BarcodeProcessor(BarcodePresenter barcodePresenter)
    {
        this.barcodePresenter = barcodePresenter;
    }

    @Override
    public void release()
    {
        //nothing to release since there is no tracker associated with the processor
    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections)
    {
        if(detections.getDetectedItems().size() != 0)
            barcodePresenter.onBarcodeDetected(detections);
    }
}
