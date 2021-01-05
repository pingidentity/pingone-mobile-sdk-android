package com.pingidentity.authenticationui.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pingidentity.authcore.PingAuthenticationCore;
import com.pingidentity.authcore.beans.PingAuthenticationApiContract;
import com.pingidentity.authcore.models.AuthenticationState;
import com.pingidentity.authcore.models.RequestParams;
import com.pingidentity.authenticationui.BuildConfig;
import com.pingidentity.authenticationui.PingAuthenticationUIActivity;
import com.pingidentity.authenticationui.R;

/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class LoginFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText usernameEditText = view.findViewById(R.id.username);
        final EditText passwordEditText = view.findViewById(R.id.password);
        final Button loginButton = view.findViewById(R.id.login);
        final TextView versionTextView = view.findViewById(R.id.version_text_view);
        versionTextView.setText(String.format("v.%s", BuildConfig.VERSION_NAME));
        usernameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    passwordEditText.setActivated(true);
                }
                return false;
            }
        });
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    authenticate(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticate(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });
    }

    private void authenticate(String username, String password){

        RequestParams requestParams = new RequestParams();
        if (getArguments()!=null && getArguments().containsKey(PingAuthenticationApiContract.STATE)){
            requestParams.setFlowId(((AuthenticationState)getArguments().get(PingAuthenticationApiContract.STATE)).getFlowId());
        }
        requestParams.setAction(PingAuthenticationApiContract.ACTIONS.CHECK_USERNAME_PASSWORD);
        requestParams.setUsername(username);
        requestParams.setPassword(password);
        PingAuthenticationCore.authenticate(requestParams, ((PingAuthenticationUIActivity)getActivity()).getPingAuthenticationUICallback());
    }

}