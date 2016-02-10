package com.awmdev.purecloudkiosk.View.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.awmdev.purecloudkiosk.Barcode.BarcodeProcessor;
import com.awmdev.purecloudkiosk.Barcode.CameraSourcePreview;
import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.Presenter.BarcodePresenter;
import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.Verifier.LoginVerifier;
import com.awmdev.purecloudkiosk.View.Fragment.BarcodeDialogFragment;
import com.awmdev.purecloudkiosk.View.Fragment.LoginDialogFragment;
import com.awmdev.purecloudkiosk.View.Interfaces.BarcodeViewInterface;
import com.awmdev.purecloudkiosk.View.Interfaces.OnLoginFinishedListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class BarcodeActivity extends AppCompatActivity implements View.OnClickListener, BarcodeViewInterface
{
    private final String TAG = BarcodeActivity.class.getSimpleName();
    private CameraSourcePreview cameraSourcePreview;
    private BarcodePresenter barcodePresenter;
    private CameraSource cameraSource;

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
        //create the presenter
        barcodePresenter = new BarcodePresenter(this);
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
        //grab a lock for checking in
        barcodePresenter.onLoginSelected();
        //create the dialog window for logging in
        displayLogInDialog();
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


    public void displayCheckInDialog(final ImageLoader imageLoader,final String url, final String name)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                FragmentManager fragmentManager = getSupportFragmentManager();
                BarcodeDialogFragment barcodeDialogFragment = new BarcodeDialogFragment();
                barcodeDialogFragment.setBarcodePresenter(barcodePresenter);
                barcodeDialogFragment.setResources(imageLoader,url,name);
                barcodeDialogFragment.show(fragmentManager,"BarcodeDialog");
            }
        });
    }

    public void displayLogInDialog()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
        loginDialogFragment.setBarcodePresenter(barcodePresenter);
        loginDialogFragment.show(fragmentManager,"LoginDialog");
    }

}
