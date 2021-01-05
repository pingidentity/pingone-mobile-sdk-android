package com.pingidentity.authcore.models;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pingidentity.authcore.beans.PingAuthenticationApiContract;

/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class ErrorState {

    /**
     * The error code.
     */
    @SerializedName(PingAuthenticationApiContract.JSON.CODE)
    private String code;

    /**
     * The developer-facing error message.
     */
    @SerializedName(PingAuthenticationApiContract.JSON.MESSAGE)
    private final String message = null;

    /**
     * The user-facing error message.
     */
    @SerializedName(PingAuthenticationApiContract.JSON.USER_MESSAGE)
    private final String userMessage = null;

    /**
     * Array of the additional details for an error returned by the Authentication API
     */
    @SerializedName(PingAuthenticationApiContract.JSON.DETAILS)
    private final ErrorDetails[] details = null;

    /**
     * In a case Error comes from PingFederate itself it will look different:
     * {error:String, error_description:String}
     */
    @SerializedName(PingAuthenticationApiContract.JSON.ERROR)
    private final String error = null;

    /**
     * The user-facing error message.
     */
    @SerializedName(PingAuthenticationApiContract.JSON.ERROR_DESCRIPTION)
    private final String errorDescription = null;

    public void setCode(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public ErrorDetails[] getDetails() {
        return details;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    @NonNull
    public String toString(){
        return new Gson().newBuilder().disableHtmlEscaping().create().toJson(this);
    }
}
