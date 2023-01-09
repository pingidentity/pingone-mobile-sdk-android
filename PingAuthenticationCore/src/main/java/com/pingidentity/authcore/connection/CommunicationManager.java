package com.pingidentity.authcore.connection;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.pingidentity.authcore.AuthenticationCoreStateMachine;
import com.pingidentity.authcore.beans.PingAuthenticationApiRequest;
import com.pingidentity.authcore.beans.PingAuthenticationApiResponse;
import com.pingidentity.authcore.beans.PingAuthenticationApiContract;
import com.pingidentity.authcore.beans.TokenExchangeRequest;
import com.pingidentity.authcore.models.AuthenticationState;
import com.pingidentity.authcore.models.Device;
import com.pingidentity.authcore.models.ErrorState;

import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Singleton based class, that manages client-server communication
 *
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class CommunicationManager {

    private static CommunicationManager instance;

    /**
     * an empty c-tor to avoid explicit class initialization
     */
    private CommunicationManager(){}

    /**
     * Creates a thread-safe singleton class by implementing synchronized global access method
     * so that only one thread can execute this method at a time, while double checked locking
     * principle is used. In this approach, the synchronized block is used inside the if condition
     * with an additional check to ensure that only one instance of a singleton class is created.
     */
    public static CommunicationManager getInstance() {
        if (instance == null){
            synchronized (CommunicationManager.class){
                if(instance==null){
                    instance = new CommunicationManager();
                }
            }

        }
        return instance;
    }

    /**
     * Adds a new {@link CommunicationTask} to the execution queue
     */
    private void sendRequest(final PingAuthenticationApiRequest request, final AuthenticationCoreStateMachine authenticationCoreStateMachine){
        CommunicationTask communicationTask = new CommunicationTask(request, new CommunicationCallback() {

            @Override
            public void onSuccess(PingAuthenticationApiResponse response) {
                Log.d("Communication manager", "Communication task finished successfully");
                if (response == null){
                    return;
                }
                authenticationCoreStateMachine.onStateChanged(responseToState(response));
            }

            @Override
            public void onError(ErrorState error) {
                authenticationCoreStateMachine.onStateChanged(errorResponseToState(error));
            }

            @Override
            public void onException(Exception e) {
                authenticationCoreStateMachine.onStateChanged(exceptionToState(e.getMessage()));
            }
        });
        Executors.newSingleThreadExecutor().submit(communicationTask);
    }


    public void sendInitialRequest(AuthenticationCoreStateMachine callback){
        sendRequest(new PingAuthenticationApiRequest(null, PingAuthenticationApiContract.ACTIONS.INITIALIZE), callback);
    }

    public void sendCodeForTokenExchangeRequest(String code, AuthenticationCoreStateMachine callback){
        TokenExchangeRequest tokenExchangeRequest = new TokenExchangeRequest("");
        tokenExchangeRequest.setCode(code);
        sendRequest(tokenExchangeRequest, callback);
    }

    public void sendCheckUsernamePasswordRequest(String flowId, String username, String password, AuthenticationCoreStateMachine callback){
        PingAuthenticationApiRequest checkUsernamePasswordRequest = new PingAuthenticationApiRequest(flowId, PingAuthenticationApiContract.ACTIONS.CHECK_USERNAME_PASSWORD);
        checkUsernamePasswordRequest.setUsername(username);
        checkUsernamePasswordRequest.setPassword(password);
        sendRequest(checkUsernamePasswordRequest, callback);
    }

    public void sendAuthenticationRequest(String flowId, String mobilePayload, AuthenticationCoreStateMachine callback){
        PingAuthenticationApiRequest authenticationRequest = new PingAuthenticationApiRequest(flowId, PingAuthenticationApiContract.ACTIONS.AUTHENTICATE);
        authenticationRequest.setMobilePayload(mobilePayload);
        sendRequest(authenticationRequest, callback);
    }

    public void sendDeviceSelectionRequest(String flowId, String deviceRef, String mobilePayload, final AuthenticationCoreStateMachine callback){
        PingAuthenticationApiRequest selectDeviceRequest = new PingAuthenticationApiRequest(flowId, PingAuthenticationApiContract.ACTIONS.SELECT_DEVICE);
        selectDeviceRequest.setDeviceId(deviceRef);
        selectDeviceRequest.setMobilePayload(mobilePayload);
        sendRequest(selectDeviceRequest, callback);
    }

    public void sendCheckOtpRequest(String flowId, String otp, String mobilePayload, AuthenticationCoreStateMachine callback){
        PingAuthenticationApiRequest checkOtpRequest = new PingAuthenticationApiRequest(flowId, PingAuthenticationApiContract.ACTIONS.CHECK_OTP);
        checkOtpRequest.setOtp(otp);
        checkOtpRequest.setMobilePayload(mobilePayload);
        sendRequest(checkOtpRequest, callback);

    }

    public void sendGenericActionRequest(String flowId, String action, final AuthenticationCoreStateMachine callback){
        sendRequest(new PingAuthenticationApiRequest(flowId, action), callback);
    }

     void sendPollingRequest(String flowId, AuthenticationCoreStateMachine callback){
        sendGenericActionRequest(flowId, PingAuthenticationApiContract.ACTIONS.POLL, callback);
    }

    /**
     * Converts received successful {@link PingAuthenticationApiResponse} into a parcelable
     * {@link AuthenticationState} object.
     * @see android.os.Parcelable
     */
    private AuthenticationState responseToState(@NonNull PingAuthenticationApiResponse response){
        if (response.getAuthorizationResponse()!=null && response.getAuthorizationResponse().getAccessToken()!=null){
            AuthenticationState authenticationState = new AuthenticationState(null,
                    PingAuthenticationApiContract.STATES.TOKEN_EXCHANGE_COMPLETED);
            authenticationState.setAccessToken(response.getAuthorizationResponse().getAccessToken());
            return authenticationState;
        }
        AuthenticationState authenticationState = new AuthenticationState(response.getFlowId(), response.getStatus());
        authenticationState.setCode(response.getCode());
        for(Map.Entry<String, JsonElement> e : response.getActions().entrySet()){
            if (e.getKey().equals(PingAuthenticationApiContract.ACTIONS.SELF)){
                continue;
            }
            if(authenticationState.getStatus().equalsIgnoreCase(PingAuthenticationApiContract.STATES.AUTHENTICATION_REQUIRED)
                    && e.getKey().equals(PingAuthenticationApiContract.ACTIONS.SELECT_DEVICE)){
                continue;
            }
            authenticationState.addAction(e.getKey());
        }
        switch (authenticationState.getStatus()){
            case PingAuthenticationApiContract.STATES.MOBILE_PAIRING_REQUIRED:
                authenticationState.setServerPayload(response.getServerPayload());
                break;
            case PingAuthenticationApiContract.STATES.COMPLETED:
                authenticationState.setCode(response.getAuthorizationResponse().getCode());
                break;
        }
        if (response.getDevices()!=null && response.getDevices().size()!=0){
            for(JsonElement deviceAsJson : response.getDevices()){
                Device device = new Gson().fromJson(deviceAsJson, Device.class);
                authenticationState.addDevice(device);
            }
        }
        return authenticationState;
    }

    /**
     * Converts received {@link ErrorState} into a parcelable {@link AuthenticationState} object.
     * @see android.os.Parcelable
     */
    private AuthenticationState errorResponseToState(ErrorState errorState){
        AuthenticationState authenticationState = new AuthenticationState(null, PingAuthenticationApiContract.STATES.ERROR_RECEIVED);
        authenticationState.setCode(errorState.getCode()==null?errorState.getError():errorState.getCode());
        authenticationState.setFullErrorJson(errorState.toString());
        return authenticationState;
    }

    /**
     * Converts {@link Exception} into a parcelable {@link AuthenticationState} object.
     * @see android.os.Parcelable
     */
    private AuthenticationState exceptionToState(String exceptionStackTrace){
        AuthenticationState authenticationState = new AuthenticationState(null, PingAuthenticationApiContract.STATES.ERROR_RECEIVED);
        authenticationState.setFullErrorJson(exceptionStackTrace);
        return authenticationState;
    }


}
