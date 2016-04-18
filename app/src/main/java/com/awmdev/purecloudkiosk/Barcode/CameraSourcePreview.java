package com.awmdev.purecloudkiosk.Barcode;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

public class CameraSourcePreview extends SurfaceView implements SurfaceHolder.Callback
{
    private String TAG = CameraSourcePreview.class.getSimpleName();
    private boolean surfaceAvailable = false;
    private boolean startRequested = false;
    private CameraSource cameraSource;

    public CameraSourcePreview(Context context)
    {
        super(context);
        //add the callback to the holder
        getHolder().addCallback(this);
    }

    public void start(CameraSource cameraSource) throws IOException, SecurityException
    {
        //check if a null source was passed and stop the surface view
        if(cameraSource == null)
        {
            stop();
        }
        //save the source locally
        this.cameraSource = cameraSource;
        //set start requested to true
        if(cameraSource != null)
        {
            startRequested = true;
            startIfReady();
        }

    }

    public void stop()
    {
        if(cameraSource != null)
            cameraSource.stop();
    }

    public void release()
    {
        if(cameraSource != null)
        {
            cameraSource.release();
            cameraSource = null;
        }
    }

    public void startIfReady() throws IOException, SecurityException
    {
        if(startRequested && surfaceAvailable)
        {
            //start the camera
            cameraSource.start(getHolder());
            //return start requested to false
            startRequested = false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        surfaceAvailable = true;
        try
        {
            startIfReady();
        }
        catch(SecurityException se)
        {
            Log.e(TAG, "You do not have permission to start the camera");
        }
        catch(IOException ie)
        {
            Log.e(TAG,"Could not start the camera source");
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        surfaceAvailable = false;
    }
}
