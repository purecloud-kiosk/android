package com.awmdev.purecloudkiosk.View.Activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.Presenter.KioskPresenter;
import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.View.Interfaces.KioskViewInterface;

public class KioskActivity extends AppCompatActivity implements View.OnClickListener,KioskViewInterface
{
    private final int requestCode = 100;
    private ImageView eventImage;
    private TextView eventNameTextView;
    private Button checkInButton;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //create layout params for the system error view
        setContentView(R.layout.activity_kiosk);
        //set the rotation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //grab the window and set the flags
        Window window = getWindow();
        //set the params for the window to prevent the screen from dimming
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //grab the components from the view
        eventImage = (ImageView)findViewById(R.id.akiosk_image_view);
        eventNameTextView = (TextView)findViewById(R.id.akiosk_event_name);
        checkInButton =(Button)findViewById(R.id.akiosk_button);
        //set the onclick listener for the button
        checkInButton.setOnClickListener(this);
        //creating the presenter to populate the view
        new KioskPresenter(this).populateView(grabDecoratorFromIntent());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            //do nothing and return true since were handling the key
            return true;
        }
        else
        {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onClick(View v)
    {
        int hasCameraPermission = ContextCompat.checkSelfPermission(KioskActivity.this, android.Manifest.permission.CAMERA);
        if(hasCameraPermission != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(KioskActivity.this,android.Manifest.permission.CAMERA))
            {
                showRationalDialog("Camera Permission Is Required For The Barcode Scanner To Scan Barcodes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(KioskActivity.this,
                                new String[]{android.Manifest.permission.CAMERA}, requestCode);
                    }
                });
            }
            else
            {
                ActivityCompat.requestPermissions(KioskActivity.this,
                        new String[]{android.Manifest.permission.CAMERA}, requestCode);
            }
        }
        else
        {
            //navigate to the next activity
            navigateToCameraActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(requestCode == this.requestCode)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //navigate to the camera activity
                navigateToCameraActivity();
            }
            else
            {
                //show error toast notifying the user that permission denied prevent scanner
                Toast.makeText(KioskActivity.this,"Unable to start barcode scanner, permission denied",Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy()
    {
        //call the super
        super.onDestroy();
    }

    private void navigateToCameraActivity()
    {
        //navigate to the next activity
        Intent intent = new Intent(getApplicationContext(),BarcodeActivity.class);
        intent.putExtra("parcelable",grabDecoratorFromIntent());
        startActivity(intent);
    }

    public JSONDecorator grabDecoratorFromIntent()
    {
       return (JSONDecorator)getIntent().getExtras().getParcelable("parcelable");
    }

    @Override
    public void setEventImage(Bitmap bitmap)
    {
        eventImage.setImageBitmap(bitmap);
    }

    @Override
    public void setEventNameTextView(String eventName)
    {
        eventNameTextView.setText(eventName);
    }

    @Override
    public void setEventImageByResId(int resId)
    {
        eventImage.setImageBitmap(BitmapFactory.decodeResource(getResources(),resId));
    }

    private void showRationalDialog(String message, DialogInterface.OnClickListener buttonListener)
    {
        new AlertDialog.Builder(KioskActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setMessage(message)
                .setPositiveButton("OK", buttonListener)
                .create()
                .show();
    }

}
