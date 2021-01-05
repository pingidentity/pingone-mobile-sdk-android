package com.pingidentity.authenticationui.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pingidentity.authcore.PingAuthenticationCore;
import com.pingidentity.authcore.beans.PingAuthenticationApiContract;
import com.pingidentity.authcore.models.AuthenticationState;
import com.pingidentity.authcore.models.RequestParams;
import com.pingidentity.authenticationui.PingAuthenticationUIActivity;
import com.pingidentity.authenticationui.R;
/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class PromptFragment extends Fragment {

    public PromptFragment(){
        //required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prompt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText editTextOtp = view.findViewById(R.id.otp_input);
        Button checkOtp = view.findViewById(R.id.button_check_otp);
        checkOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams requestParams = new RequestParams();
                if (getArguments()!=null && getArguments().containsKey("state")){
                    requestParams.setFlowId(((AuthenticationState)getArguments().get("state")).getFlowId());
                }
                if (getArguments()!=null && getArguments().containsKey("state")){
                    requestParams.setMobilePayload
                            (((AuthenticationState)getArguments().get("state")).getMobilePayload());
                }
                requestParams.setAction(PingAuthenticationApiContract.ACTIONS.CHECK_OTP);
                requestParams.setOtp(editTextOtp.getText().toString());
                PingAuthenticationCore.authenticate(requestParams, ((PingAuthenticationUIActivity)getActivity()).getPingAuthenticationUICallback());
            }
        });
    }
}
