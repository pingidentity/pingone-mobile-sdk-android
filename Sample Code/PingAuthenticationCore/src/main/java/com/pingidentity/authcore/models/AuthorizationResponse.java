package com.pingidentity.authcore.models;

import com.google.gson.annotations.SerializedName;
import com.pingidentity.authcore.beans.PingAuthenticationApiContract;

/**
 * This class represents an object returned when device authorization is completed.
 * Depending on request it may contain access token, id token or code, which
 * can be exchanged to access token.
 *
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class AuthorizationResponse {

    @SerializedName(PingAuthenticationApiContract.JSON.CODE)
    private String code;

    @SerializedName(PingAuthenticationApiContract.JSON.ACCESS_TOKEN)
    private String accessToken;

    @SerializedName(PingAuthenticationApiContract.JSON.ID_TOKEN)
    private String idToken;

    @SerializedName(PingAuthenticationApiContract.JSON.TOKEN_TYPE)
    private String tokenType;

    @SerializedName(PingAuthenticationApiContract.JSON.EXPIRES_IN)
    private long expiresIn;

    public String getCode() {
        return code;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}
