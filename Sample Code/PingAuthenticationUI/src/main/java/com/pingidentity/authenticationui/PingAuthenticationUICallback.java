package com.pingidentity.authenticationui;

import com.pingidentity.authcore.AuthenticationCallback;
import com.pingidentity.authcore.models.AuthenticationState;
/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class PingAuthenticationUICallback implements AuthenticationCallback {


    private final AuthenticationStateViewModel authenticationStateViewModel;

    PingAuthenticationUICallback(AuthenticationStateViewModel authenticationStateViewModel){
        this.authenticationStateViewModel = authenticationStateViewModel;
    }


    @Override
    public void onStateChanged(AuthenticationState state) {
        authenticationStateViewModel.updateAuthenticationStateMutableLiveDataFromWorkerThread(state);
    }
    
}
