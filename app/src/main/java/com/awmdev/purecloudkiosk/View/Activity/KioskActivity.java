package com.awmdev.purecloudkiosk.View.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.awmdev.purecloudkiosk.BroadcastReceiver.ScreenReceiver;
import com.awmdev.purecloudkiosk.Decorator.JSONEventDecorator;
import com.awmdev.purecloudkiosk.Presenter.KioskPresenter;
import com.awmdev.purecloudkiosk.R;

public class KioskActivity extends AppCompatActivity implements View.OnClickListener
{
    private ScreenReceiver screenReceiver;
    private NetworkImageView eventImage;
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
        eventImage = (NetworkImageView)findViewById(R.id.akiosk_image_view);
        eventNameTextView = (TextView)findViewById(R.id.akiosk_event_name);
        checkInButton =(Button)findViewById(R.id.akiosk_button);
        //set the onclick listener for the button
        checkInButton.setOnClickListener(this);
        //yes im creating a presenter, all just to follow convention
        new KioskPresenter(this).populateView(grabDecoratorFromIntent());
        //register the broadcast receiver
        screenReceiver = new ScreenReceiver(getApplicationContext());
        //register the receiver
        screenReceiver.registerReceiver();
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
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus)
        {
            //Intent closeWindow = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            //sendBroadcast(closeWindow);
        }
    }

    @Override
    protected void onDestroy()
    {
        //call the super
        super.onDestroy();
        //disable our screen receiver
        screenReceiver.removeReceiver();
    }

    @Override
    public void onBackPressed()
    {
        onBackPressed();
    }

    public JSONEventDecorator grabDecoratorFromIntent()
    {
       return (JSONEventDecorator)getIntent().getExtras().getParcelable("parcelable");
    }

    public void setEventImage(ImageLoader imageLoader, String url)
    {
        eventImage.setImageUrl(url, imageLoader);
    }

    public void setEventNameTextView(String eventName)
    {
        eventNameTextView.setText(eventName);
    }

    @Override
    public void onClick(View v)
    {
        //navigate to the next activity
        //finish();
        Intent intent = new Intent(getApplicationContext(),BarcodeActivity.class);
        startActivity(intent);

    }
}
