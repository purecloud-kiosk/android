package com.awmdev.purecloudkiosk.View.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.android.volley.toolbox.ImageLoader;
import com.awmdev.purecloudkiosk.Barcode.BarcodeProcessor;
import com.awmdev.purecloudkiosk.Barcode.CameraSourcePreview;
import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.Model.BarcodeModel;
import com.awmdev.purecloudkiosk.Presenter.BarcodePresenter;
import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.Services.CheckInService;
import com.awmdev.purecloudkiosk.View.Fragment.BarcodeDialogFragment;
import com.awmdev.purecloudkiosk.View.Fragment.LoginDialogFragment;
import com.awmdev.purecloudkiosk.View.Interfaces.BarcodeViewInterface;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.Map;

public class BarcodeActivity extends AppCompatActivity implements View.OnClickListener, BarcodeViewInterface, ServiceConnection
{
    private final String TAG = BarcodeActivity.class.getSimpleName();
    private CameraSourcePreview cameraSourcePreview;
    private BarcodePresenter barcodePresenter;
    private CheckInService checkInService;
    private CameraSource cameraSource;
    private BarcodeModel barcodeModel;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //set the landscape view
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //set the content view for the activity
        setContentView(R.layout.activity_barcode);
        //grab the window and set the flags
        Window window = getWindow();
        //set the params for the window to prevent the screen from dimming
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //create the model
        barcodeModel = new BarcodeModel();
        //set the decorator to the model
        barcodeModel.setJsonEventDecorator(grabDecoratorFromIntent());
        //create the presenter
        barcodePresenter = new BarcodePresenter(this,barcodeModel);
        //create the camera source preview object
        cameraSourcePreview = new CameraSourcePreview(getApplicationContext());
        //grab the layout components
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.activity_barcode_camera_preview);
        frameLayout.addView(cameraSourcePreview);
        //register the camera source with the view
        createCameraSource();
        //grab an instance of the button and set the onclick listener
        Button button = (Button) findViewById(R.id.activity_barcode_button);
        button.setOnClickListener(this);
        //start the service, before binding to prevent it closing on unBind
        Intent intent = new Intent(getApplicationContext(),CheckInService.class);
        startService(intent);
        bindService(intent,this,Context.BIND_AUTO_CREATE);
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
        if(checkInService != null)
            unbindService(this);
    }

    @Override
    public void onClick(View v)
    {
        //grab a lock for checking in
        barcodePresenter.onLoginSelected();
        //create the dialog window for logging in
        displayLogInDialog();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service)
    {
        //cast the binder
        CheckInService.CheckInBinder checkInBinder = (CheckInService.CheckInBinder)service;
        //grab the service from the binder
        checkInService = checkInBinder.getService();
        Log.d(TAG,"Service Bound");
    }

    @Override
    public void onServiceDisconnected(ComponentName name)
    {
        //connection from the service has been lost, set the service to null
        checkInService = null;
        Log.d(TAG,"Service Unbound");
    }

    @Override
    public void postCheckIn(Map<String,Object> jsonMap)
    {
        if(checkInService != null)
            checkInService.postCheckIn(jsonMap);
    }

    @Override
    public void displayCheckInDialog(final ImageLoader imageLoader,final JSONDecorator jsonDecorator)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                FragmentManager fragmentManager = getSupportFragmentManager();
                BarcodeDialogFragment barcodeDialogFragment = new BarcodeDialogFragment();
                barcodeDialogFragment.setResources(imageLoader,jsonDecorator);
                barcodeDialogFragment.show(fragmentManager,"BarcodeDialog");
            }
        });
    }

    @Override
    public void displayLogInDialog()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
        loginDialogFragment.setBarcodePresenter(barcodePresenter);
        loginDialogFragment.show(fragmentManager, "LoginDialog");
    }

    public JSONDecorator grabDecoratorFromIntent()
    {
        return (JSONDecorator)getIntent().getExtras().getParcelable("parcelable");
    }

    private void createCameraSource()
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
                .setAutoFocusEnabled(true).setRequestedPreviewSize((size.x), (size.y)).build();
    }

    private void startCameraSource()
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
