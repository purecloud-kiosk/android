package com.awmdev.purecloudkiosk.View.Activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.awmdev.purecloudkiosk.Decorator.JSONEventDecorator;
import com.awmdev.purecloudkiosk.Presenter.KioskPresenter;
import com.awmdev.purecloudkiosk.R;

import org.w3c.dom.Text;

public class KioskActivity extends AppCompatActivity implements View.OnClickListener
{
    private NetworkImageView eventImage;
    private TextView eventNameTextView;
    private Button checkInButton;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //set the orientation to be landscape only
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //set the content view
        setContentView(R.layout.activity_kiosk);
        //grab the components from the view
        eventImage = (NetworkImageView)findViewById(R.id.akiosk_image_view);
        eventNameTextView = (TextView)findViewById(R.id.akiosk_event_name);
        checkInButton =(Button)findViewById(R.id.akiosk_button);
        //yes im creating a presenter, all just to follow convention
        new KioskPresenter(this).populateView(grabDecoratorFromIntent());
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

    }
}
