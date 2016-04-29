package com.awmdev.purecloudkiosk.View.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.awmdev.purecloudkiosk.Decorator.JSONDecorator;
import com.awmdev.purecloudkiosk.Model.HttpRequester;
import com.awmdev.purecloudkiosk.Presenter.BarcodePresenter;
import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.Verifier.LoginVerifier;
import com.awmdev.purecloudkiosk.Verifier.OnLoginFinishedListener;
import com.awmdev.purecloudkiosk.View.Interfaces.BarcodeViewInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Reese on 2/8/2016.
 */
public class LoginDialogFragment extends DialogFragment implements OnShowListener,OnLoginFinishedListener,DialogInterface.OnClickListener, View.OnClickListener
{
    private final String TAG = LoginDialogFragment.class.getSimpleName();
    private BarcodeViewInterface barcodeViewInterface;
    private BarcodePresenter barcodePresenter;
    private AlertDialog alertDialog;
    private EditText usernameEdit;
    private EditText passwordEdit;
    private TextView errorText;

    public void setBarcodePresenter(BarcodePresenter barcodePresenter, BarcodeViewInterface barcodeViewInterface)
    {
        this.barcodePresenter = barcodePresenter;
        this.barcodeViewInterface = barcodeViewInterface;
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
        //grab the organization from the shared preferences
        SharedPreferences sharedPreferences = (SharedPreferences)
                getContext().getSharedPreferences("authenticationPreference", Context.MODE_PRIVATE);
        //verify the login credentials
        loginVerifier.validateCredentials(usernameEdit.getText().toString(),
                passwordEdit.getText().toString(), sharedPreferences.getString("organizationValue",""));
        Log.d(TAG, sharedPreferences.getString("organizationValue",""));
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
    public void onLoginSuccessful(JSONDecorator jsonDecorator, String organization)
    {
        //construct the decorator for the check in dialog
        Map<String, Object> checkInMap = new HashMap<String,Object>();
        //variable for constructing the object
        String username = null, imageURL = null;
        try
        {
            //grab the org json object from res
            JSONObject jsonPersonObject = jsonDecorator.getJSONObject("person");
            //grab the users name from the json
            username = jsonPersonObject.getJSONObject("general")
                    .getJSONArray("name").getJSONObject(0).getString("value");
            //attempt to grab the image url, it doesn't exist if no image is available
            imageURL = jsonPersonObject.getJSONObject("images")
                    .getJSONArray("profile").getJSONObject(0)
                    .getJSONObject("ref").getString("x128");
        }
        catch(JSONException ex)
        {
            //there may have been an error attempting to grab the image, do nothing it is normal
        }
        finally
        {
           //construct the json object with the value retrieved
            checkInMap.put("name",username);
            checkInMap.put("image",imageURL);
        }
        //close the dialog before displaying the prompt
        dismiss();
        //send the check in to the presenter to be processed
        barcodePresenter.onCheckInSuccessful(jsonDecorator);
        //display the check in dialog to notify the user of the successful login
        barcodeViewInterface.displayCheckInDialog(
                HttpRequester.getInstance(getContext()).getImageLoader()
                ,new JSONDecorator(checkInMap));
    }

    @Override
    public void setOrganizationWrapperVisibility(int visibility)
    {
        //no need to do anything, as the org edit text is not available in this view
    }

}
