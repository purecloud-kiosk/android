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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.awmdev.purecloudkiosk.Barcode.BarcodeProcessor;
import com.awmdev.purecloudkiosk.Barcode.CameraSourcePreview;
import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.Presenter.BarcodePresenter;
import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.Verifier.LoginVerifier;
import com.awmdev.purecloudkiosk.View.Fragment.LoginFragment;
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


    public void displayCheckInDialog(final ImageLoader imageLoader,final  String url, final String name)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                //create a builder object
                AlertDialog.Builder builder = new AlertDialog.Builder(BarcodeActivity.this);
                //inflate the view
                LinearLayout linearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.dialog_confirm_checkin,null);
                //grab the component from the view
                NetworkImageView dialogImageView = (NetworkImageView) linearLayout.findViewById(R.id.dialog_confirm_imageview);
                TextView dialogTextView = (TextView)linearLayout.findViewById(R.id.dialog_confirm_textview);
                //assign data to the view
                dialogImageView.setDefaultImageResId(R.drawable.default_profile);
                //check to see if a url was given, if it was set it to the network image view
                if(!url.equalsIgnoreCase("null"))
                    dialogImageView.setImageUrl(url, imageLoader);
                dialogTextView.setText(name);
                //assign the view to the dialog
                builder.setView(linearLayout);
                //set the positive button
                builder.setPositiveButton(R.string.check_in, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        barcodePresenter.onCheckInSuccessful();
                    }
                });
                //set the negative button
                builder.setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        barcodePresenter.detectionComplete();
                    }
                });
                //create the dialog
                AlertDialog alertDialog = builder.create();
                //prevent the dialog from being dismissible
                alertDialog.setCanceledOnTouchOutside(false);
                //display the dialog
                alertDialog.show();
            }
        });
    }

    public void displayLogInDialog()
    {
        //create a builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //inflate the view
        LinearLayout linearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.dialog_log_in,null);
        //grab the components from the view
        final EditText usernameEdit = (EditText)linearLayout.findViewById(R.id.dlogin_user_edit);
        final EditText passwordEdit = (EditText)linearLayout.findViewById(R.id.dlogin_password_edit);
        final EditText organizationEdit = (EditText)linearLayout.findViewById(R.id.dlogin_organization_edit);
        final TextView errorText = (TextView)linearLayout.findViewById(R.id.dlogin_error_text);
        //set the view on the builder
        builder.setView(linearLayout);
        //set the positive and negative button texts
        builder.setNegativeButton(R.string.not_now,null)
                .setPositiveButton(R.string.check_in, null);
        //set the title
        builder.setTitle("Log In To PureCloud");
        //create the dialog
        final AlertDialog alertDialog = builder.create();
        //Override the on show functionality
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialog)
            {
                //grab the positive button and override its default behavior
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                //set the listener
                positiveButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //create an instance of the listener
                        OnLoginFinishedListener onLoginFinishedListener = new OnLoginFinishedListener()
                        {
                            @Override
                            public void setError(int resID)
                            {
                                errorText.setText(resID);
                            }

                            @Override
                            public void removeError()
                            {
                                errorText.setText(null);
                            }

                            @Override
                            public void onLoginSuccessful(JSONDecorator jsonDecorator)
                            {
                                barcodePresenter.onCheckInSuccessful();
                            }

                            @Override
                            public void setOrganizationWrapperVisibility(int visibility)
                            {
                                //do nothing as the organization edit text will be shown by default
                            }
                        };
                        //create an instance of the login verifier and pass the onloginfinishedlistener
                        LoginVerifier loginVerifier = new LoginVerifier(onLoginFinishedListener);
                        //verify the login credentials
                        loginVerifier.validateCredentials(usernameEdit.getText().toString(),
                                passwordEdit.getText().toString(),organizationEdit.getText().toString());
                    }
                });
            }
        });
        alertDialog.show();
    }

}
