package com.pingidentity.authcore;

import android.util.Log;

import com.pingidentity.authcore.beans.PingAuthenticationApiContract;
import com.pingidentity.authcore.connection.CommunicationManager;
import com.pingidentity.authcore.models.AuthenticationState;

/**
 * This class represents a simple state machine, that acts accordingly on every change of the state.
 * It holds a reference to the user interface callback to pass to it states that should be handled
 * in UI (require user interaction)
 *
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class AuthenticationCoreStateMachine {

    private final AuthenticationCallback uiCallback;

    protected AuthenticationCoreStateMachine(AuthenticationCallback callback){
        this.uiCallback = callback;
    }

    public void onStateChanged(AuthenticationState state){
        Log.d("CoreStateMachine", String.format("AuthenticationCoreStateMachine received state change event %s", state.getStatus()));
        switch (state.getStatus()){
            case PingAuthenticationApiContract.STATES.COMPLETED:
                uiCallback.onStateChanged(state);
                CommunicationManager.getInstance().sendCodeForTokenExchangeRequest(state.getCode(), AuthenticationCoreStateMachine.this);
                break;
            case PingAuthenticationApiContract.STATES.TOKEN_EXCHANGE_COMPLETED:
            default:
                Log.d("CoreStateMachine", String.format("The received state %s should be handled by UI module", state.getStatus()));
                uiCallback.onStateChanged(state);
        }
    }



}
