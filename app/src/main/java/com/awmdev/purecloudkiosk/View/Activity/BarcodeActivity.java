package com.awmdev.purecloudkiosk.View.Activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.awmdev.purecloudkiosk.Presenter.BarcodePresenter;
import com.awmdev.purecloudkiosk.R;

import com.awmdev.purecloudkiosk.Barcode.*;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.MultiProcessor;

import java.io.IOException;

public class BarcodeActivity extends AppCompatActivity implements View.OnClickListener
{
    private final String TAG = BarcodeActivity.class.getSimpleName();
    private CameraSourcePreview cameraSourcePreview;
    private BarcodePresenter barcodePresenter;
    private CameraSource cameraSource;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //set the content view for the activity
        setContentView(R.layout.activity_barcode);
        //set the landscape view
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //create the presenter
        barcodePresenter = new BarcodePresenter(this);
        //create the camera source preview object
        cameraSourcePreview = new CameraSourcePreview(getApplicationContext());
        //grab the layout components
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.activity_barcode_camera_preview);
        frameLayout.addView(cameraSourcePreview);
        //register the camera source with the view
        createCameraSource();
        //grab an instance of the button
        Button button = (Button) findViewById(R.id.activity_barcode_button);
        button.setOnClickListener(this);
    }

    @Override
    protected void onResume()
    {
        //call the super
        super.onResume();
        //resume the camera
        startCameraSource();
    }

    @Override
    protected void onPause()
    {
        //call the super
        super.onPause();
        //stop the preview
        if(cameraSourcePreview != null)
            cameraSourcePreview.stop();
    }

    @Override
    public void onDestroy()
    {
        //call the super
        super.onDestroy();
        //check if the camera source is null, if not release the resources
        if(cameraSourcePreview != null)
            cameraSourcePreview.release();
    }

    @Override
    public void onClick(View v)
    {

    }

    public void createCameraSource()
    {
        //set up the barcode detector
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getApplicationContext()).build();
        //set the processor to the factory
        barcodeDetector.setProcessor(new BarcodeProcessor(barcodePresenter));
        //grab the display size for the camera source
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        //set up the camera source
        cameraSource = new CameraSource.Builder(getApplicationContext(),barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK).setRequestedFps(15.0F)
                .setAutoFocusEnabled(true).setRequestedPreviewSize((size.x-100), (size.y-100)).build();
    }

    public void startCameraSource()
    {

        if (cameraSource != null)
        {
            try
            {
                cameraSourcePreview.start(cameraSource);
            }
            catch (IOException e)
            {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
            catch(SecurityException se)
            {
                Log.e(TAG,"Missing camera permission");
            }
        }

    }

}
