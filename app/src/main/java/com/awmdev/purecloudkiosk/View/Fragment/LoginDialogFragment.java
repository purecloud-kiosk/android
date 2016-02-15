package com.awmdev.purecloudkiosk.View.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.Presenter.BarcodePresenter;
import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.Verifier.LoginVerifier;
import com.awmdev.purecloudkiosk.View.Interfaces.OnLoginFinishedListener;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by Reese on 2/8/2016.
 */
public class LoginDialogFragment extends DialogFragment implements OnShowListener,OnLoginFinishedListener,DialogInterface.OnClickListener, View.OnClickListener
{
    private BarcodePresenter barcodePresenter;
    private EditText organizationEdit;
    private AlertDialog alertDialog;
    private EditText usernameEdit;
    private EditText passwordEdit;
    private TextView errorText;

    public void setBarcodePresenter(BarcodePresenter barcodePresenter)
    {
        this.barcodePresenter = barcodePresenter;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        //create a builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //inflate the view
        ScrollView layout = (ScrollView)getActivity().getLayoutInflater().inflate(R.layout.dialog_log_in, null);
        //grab the components from the view
        usernameEdit = (EditText)layout.findViewById(R.id.dlogin_user_edit);
        passwordEdit = (EditText)layout.findViewById(R.id.dlogin_password_edit);
        organizationEdit = (EditText)layout.findViewById(R.id.dlogin_organization_edit);
        errorText = (TextView)layout.findViewById(R.id.dlogin_error_text);
        //set the view on the builder
        builder.setView(layout);
        //set the button text and listeners
        builder.setNegativeButton(R.string.not_now,this)
                .setPositiveButton(R.string.check_in, null);
        //set the title for the dialog
        builder.setTitle("Log In To PureCloud");
        //build the dialog
        alertDialog = builder.create();
        //prevent the dialog from being dismissible
        alertDialog.setCanceledOnTouchOutside(false);
        //Override the on show functionality
        alertDialog.setOnShowListener(this);
        //return the dialog
        return alertDialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        barcodePresenter.detectionComplete();
    }

    @Override
    public void onShow(DialogInterface dialog)
    {
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        //set the listener
        positiveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        //create an instance of the login verifier and pass the onloginfinishedlistener
        LoginVerifier loginVerifier = new LoginVerifier(this);
        //verify the login credentials
        loginVerifier.validateCredentials(usernameEdit.getText().toString(),
                passwordEdit.getText().toString(), organizationEdit.getText().toString());
    }

    @Override
    public void setError(int resID)
    {
        errorText.setText(resID);
        errorText.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeError()
    {
        errorText.setText(null);
        errorText.setVisibility(View.GONE);
    }

    @Override
    public void onLoginSuccessful(JSONDecorator jsonDecorator)
    {
        barcodePresenter.onCheckInSuccessful(jsonDecorator);
    }

    @Override
    public void setOrganizationWrapperVisibility(int visibility)
    {
        //no need to do anything, as the org edit text is visible in the dialog at all times
    }

}
