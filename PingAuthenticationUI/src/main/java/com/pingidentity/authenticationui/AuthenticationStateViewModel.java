package com.pingidentity.authenticationui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pingidentity.authcore.models.AuthenticationState;

/**
 * This class is used as a communication layer between user interface and its underlying logic.
 * The logic will update a value {@link #authenticationStateMutableLiveData} and any UI
 * component can register an observer to this value to receive updates at the runtime.
 * @version 1.0.0
 * @see androidx.lifecycle.ViewModel
 *
 * See LICENSE.txt for the Ping Authentication licensing information.
 *
 */
public class AuthenticationStateViewModel extends ViewModel {

    private final MutableLiveData<AuthenticationState> authenticationStateMutableLiveData;

    //required public c-tor 
    public AuthenticationStateViewModel(){
        authenticationStateMutableLiveData = new MutableLiveData<>();
    }

    public void updateAuthenticationStateMutableLiveDataFromWorkerThread(AuthenticationState state){
        this.authenticationStateMutableLiveData.postValue(state);
    }

    public MutableLiveData<AuthenticationState> getAuthenticationStateMutableLiveData(){
        return authenticationStateMutableLiveData;
    }
}
