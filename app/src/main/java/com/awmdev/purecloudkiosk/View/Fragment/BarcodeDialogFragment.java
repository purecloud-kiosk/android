package com.awmdev.purecloudkiosk.View.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.awmdev.purecloudkiosk.Presenter.BarcodePresenter;
import com.awmdev.purecloudkiosk.R;

/**
 * Created by Reese on 2/8/2016.
 */
public class BarcodeDialogFragment extends DialogFragment implements DialogInterface.OnClickListener
{
    private BarcodePresenter barcodePresenter;
    private ImageLoader imageLoader;
    private String name;
    private String url;

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
        //set the url if its not null
        if(!url.equalsIgnoreCase("null"))
            dialogImageView.setImageUrl(url,imageLoader);
        dialogTextView.setText(name);
        //assign the view to the dialog
        builder.setView(linearLayout);
        //set the positive/negative button
        builder.setPositiveButton(R.string.check_in, this)
                .setNegativeButton(R.string.not_now, this);
        //create the dialog
        AlertDialog alertDialog = builder.create();
        //prevent the dialog from being dismissible
        alertDialog.setCanceledOnTouchOutside(false);
        //return the dialog
        return alertDialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        if(which == Dialog.BUTTON_POSITIVE)
            barcodePresenter.onCheckInSuccessful();
        else
        {
            if(which == Dialog.BUTTON_NEGATIVE)
                barcodePresenter.detectionComplete();
        }
    }

    public void setResources(ImageLoader imageLoader,String url, String name)
    {
        this.imageLoader = imageLoader;
        this.name = name;
        this.url = url;
    }

    public void setBarcodePresenter(BarcodePresenter barcodePresenter)
    {
        this.barcodePresenter = barcodePresenter;
    }
}
