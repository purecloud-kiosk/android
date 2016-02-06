package com.awmdev.purecloudkiosk.View.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.awmdev.purecloudkiosk.View.Interfaces.LoginViewInterface;
import com.awmdev.purecloudkiosk.Presenter.LoginPresenter;
import com.awmdev.purecloudkiosk.R;
import com.awmdev.purecloudkiosk.View.Activity.LoginActivity;

public class LoginFragment extends Fragment implements View.OnClickListener,LoginViewInterface
{
    private LoginPresenter loginPresenter;
    private EditText organizationEditText;
    private View organizationViewWrapper;
    private EditText passwordEditText;
    private EditText userEditText;
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
        organizationEditText = (EditText)layout.findViewById(R.id.flogin_organization_edit);
        organizationViewWrapper = (View)layout.findViewById(R.id.flogin_organization_wrapper);
        //register the onclick listener for the button
        loginButton.setOnClickListener(this);
        //return to the view
        return layout;
    }

    @Override
    public void setError(int resourceID)
    {
        //set the error from a pre-made string
        errorText.setText(resourceID);
    }

    @Override
    public void removeError()
    {
        errorText.setText(null);
    }

    @Override
    public void setOrganizationWrapperVisibility(int visibility)
    {
        organizationViewWrapper.setVisibility(visibility);
    }

    @Override
    public void navigateToEventList(String authenticationToken)
    {
        ((LoginActivity)getActivity()).onLoginSuccessful(authenticationToken);
    }

    @Override
    public void onClick(View v)
    {
        loginPresenter.validateCredentials(userEditText.getText().toString(),
                passwordEditText.getText().toString(), organizationEditText.getText().toString());
    }

}
