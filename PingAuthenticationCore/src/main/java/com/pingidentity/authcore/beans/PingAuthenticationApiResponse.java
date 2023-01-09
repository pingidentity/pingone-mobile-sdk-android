package com.pingidentity.authcore.beans;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.pingidentity.authcore.models.AuthorizationResponse;
/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class PingAuthenticationApiResponse {

    @SerializedName(PingAuthenticationApiContract.JSON.ID)
    private String flowId;

    @SerializedName(PingAuthenticationApiContract.JSON.STATUS)
    private String status;

    /**
     * If the response includes a code parameter, a one-time authorization code that your server can
     * exchange for an access token and ID token.
     */
    @SerializedName(PingAuthenticationApiContract.JSON.CODE)
    private String code;

    @SerializedName(PingAuthenticationApiContract.JSON.SERVER_PAYLOAD)
    private String serverPayload;

    @SerializedName(PingAuthenticationApiContract.JSON.LINKS)
    private JsonObject actionsAsJson;

    @SerializedName(PingAuthenticationApiContract.JSON.DEVICES)
    private JsonArray devicesAsJsonArray;

    @SerializedName(PingAuthenticationApiContract.JSON.AUTHORIZE_RESPONSE)
    private AuthorizationResponse authorizationResponse;

    public PingAuthenticationApiResponse() {
    }

    public String getFlowId() {
        return flowId;
    }

    public String getStatus() {
        return status;
    }

    public String getServerPayload() {
        return serverPayload;
    }

    public JsonObject getActions() {
        return actionsAsJson;
    }

    public JsonArray getDevices(){
        return devicesAsJsonArray;
    }

    public AuthorizationResponse getAuthorizationResponse() {
        return authorizationResponse;
    }

    public String getCode() {
        return code;
    }

    public void setAuthorizationResponse(AuthorizationResponse authorizationResponse) {
        this.authorizationResponse = authorizationResponse;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
