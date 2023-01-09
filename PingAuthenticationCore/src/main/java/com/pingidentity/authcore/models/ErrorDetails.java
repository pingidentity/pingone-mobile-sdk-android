package com.pingidentity.authcore.models;

import com.google.gson.annotations.SerializedName;
import com.pingidentity.authcore.beans.PingAuthenticationApiContract;

/**
 * The model representing additional details for an error returned by the Authentication API.
 *
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class ErrorDetails {

    /**
     * The error code.
     */
    @SerializedName(PingAuthenticationApiContract.JSON.CODE)
    private String code;

    /**
     * The developer-facing error message.
     */
    @SerializedName(PingAuthenticationApiContract.JSON.MESSAGE)
    private String message;

    /**
     * The user-facing error message.
     */
    @SerializedName(PingAuthenticationApiContract.JSON.USER_MESSAGE)
    private String userMessage;

    public String getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }

    public String getUserMessage(){
        return userMessage;
    }
}
