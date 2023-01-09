package com.pingidentity.authcore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pingidentity.authcore.models.RequestParams;

/**
 * This class represents public API for the PingAuthenticationCore module.
 * 
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class PingAuthenticationCore {

    /**
     * Executes a request according to the {@link RequestParams} and delivers response through
     * {@link AuthenticationCallback}
     * @param params - RequestParams object, can be null at initial request
     * @param callback - AuthenticationCallback that will be triggered on
     * {@link com.pingidentity.authcore.models.AuthenticationState} change
     */
    public static void authenticate(@Nullable RequestParams params, @NonNull AuthenticationCallback callback){
        LogicLayer logicLayer = new LogicLayer();
        AuthenticationCoreStateMachine authenticationCoreStateMachine = new AuthenticationCoreStateMachine(callback);
        logicLayer.executeRequest(params, authenticationCoreStateMachine);
    }

    private PingAuthenticationCore(){}

}
