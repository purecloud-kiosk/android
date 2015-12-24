package com.awmdev.purecloudkiosk.View.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.awmdev.purecloudkiosk.Presenter.LoginPresenter;
import com.awmdev.purecloudkiosk.R;

public class LoginFragment extends Fragment implements View.OnClickListener
{
    private LoginPresenter loginPresenter;
    private EditText userEditText;
    private EditText passwordEditText;
    private TextView errorText;
    private Button loginButton;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //create the presenter and pass the view it is associated with
        loginPresenter = new LoginPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //grab the layout associated with this view
        RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.fragment_login,container,false);
        //grab all of the associated object from the layout
        loginButton = (Button)layout.findViewById(R.id.flogin_login_button);
        userEditText = (EditText)layout.findViewById(R.id.flogin_user_edit);
        passwordEditText = (EditText)layout.findViewById(R.id.flogin_password_edit);
        errorText = (TextView)layout.findViewById(R.id.flogin_error_text);
        //register the onclick listener for the button
        loginButton.setOnClickListener(this);
        //return to the view
        return layout;
    }

    public void setError(int resourceID)
    {
        //set the error from a pre-made string
        errorText.setText(resourceID);
    }

    public void removeErrorText()
    {
        errorText.setText(null);
    }

    public void saveAuthorizationToken(String authToken)
    {
        //Grab an instance of the shared preference library
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("authorization_preference",Context.MODE_PRIVATE);
        //grab an instance of the editor
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //save the string to the shared preferences
        editor.putString("authToken",authToken);
        //apply the change
        editor.apply();
    }

    @Override
    public void onClick(View v)
    {
        loginPresenter.validateCredentials(userEditText.getText().toString(),passwordEditText.getText().toString());
    }

}
