package com.pingidentity.authenticationui;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class PingAuthenticationUI {

    public static final int AUTHENTICATION_UI_ACTIVITY_REQUEST_CODE = 112;

    /*
     * A static variable is common to all the instances (or objects) of the class because it is a
     * class level variable. In other words you can say that only a single copy of static variable
     * is created and shared among all the instances of the class. Memory allocation for such
     * variables only happens once when the class is loaded in the memory.
     */
    private static String flowId;

    /*
     * Static Methods can access class variables(static variables) without using object(instance) of the class
     */
    static void setFlowId(String id){
        flowId = id;
    }

    /**
     * Starts an Authentication process with PingIdentity Authentication API. Calling this method
     * will open a new Activity in the context of the provided one. The provided activity will
     * also get the result of the authentication process.
     * @param context - an Activity that will retrieve a result of the authentication process.
     * @param mobilePayload - a String retrieved from PingID SDK or PingOne SDK
     * @param dynamicData - a String for passing any additional data.
     */
    public void authenticate(@NonNull Activity context, @NonNull String mobilePayload, @Nullable String dynamicData){
        Intent intent = new Intent(context, PingAuthenticationUIActivity.class);
        intent.putExtra("mobilePayload", mobilePayload);
        intent.putExtra("dynamicData", dynamicData);
        if(flowId!=null) {
            intent.putExtra("flowId", flowId);
        }
        context.startActivityForResult(intent, AUTHENTICATION_UI_ACTIVITY_REQUEST_CODE);
    }

    /**
     * The authentication flow with Mobile Authentication framework may require from the end-user to pair current mobile device.
     * In this case it will return to hosting Activity with a serverPayload. After pairing your device you need to call this
     * method to continue authentication process.
     * @param context - an Activity which will retrieve the result of the authentication process.
     */
    public void continueAuthentication(Activity context){
        if(flowId==null){
            Log.e(PingAuthenticationUI.class.getSimpleName(), "flowId is null at continueAuthentication");
            return;
        }
        Intent intent = new Intent(context, PingAuthenticationUIActivity.class);
        intent.putExtra("flowId", flowId);
        context.startActivityForResult(intent, AUTHENTICATION_UI_ACTIVITY_REQUEST_CODE);
    }
}
