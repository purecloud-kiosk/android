package com.awmdev.purecloudkiosk.View.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.Presenter.BarcodePresenter;
import com.awmdev.purecloudkiosk.R;

/**
 * Created by Reese on 2/8/2016.
 */
public class BarcodeDialogFragment extends DialogFragment
{
    private JSONDecorator jsonDecorator;
    private ImageLoader imageLoader;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        //create a builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //inflate the view
        LinearLayout linearLayout = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.dialog_confirm_checkin,null);
        //grab the component from the view
        NetworkImageView dialogImageView = (NetworkImageView) linearLayout.findViewById(R.id.dialog_confirm_imageview);
        TextView dialogTextView = (TextView)linearLayout.findViewById(R.id.dialog_confirm_textview);
        //assign data to the view
        dialogImageView.setDefaultImageResId(R.drawable.default_profile);
        //grab the url
        String url = jsonDecorator.getString("image");
        //set the url if its not null
        if(!url.equalsIgnoreCase("null"))
            dialogImageView.setImageUrl(url,imageLoader);
        dialogTextView.setText(jsonDecorator.getString("name"));
        //assign the view to the dialog
        builder.setView(linearLayout);
        //create the dialog
        final AlertDialog alertDialog = builder.create();
        //prevent the dialog from being dismissible
        alertDialog.setCanceledOnTouchOutside(false);
        //set the timer for the dialog
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialog)
            {
                final Handler handler = new Handler();
                final Runnable runnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        alertDialog.dismiss();
                    }
                };
                handler.postDelayed(runnable,1500);
            }
        });
        //return the dialog
        return alertDialog;
    }

    public void setResources(ImageLoader imageLoader,JSONDecorator jsonDecorator)
    {
        this.imageLoader = imageLoader;
        this.jsonDecorator = jsonDecorator;
    }
}
