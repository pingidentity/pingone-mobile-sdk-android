package com.pingidentity.authcore.beans;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pingidentity.authcore.BuildConfig;
/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class TokenExchangeRequest extends PingAuthenticationApiRequest {

    @SerializedName(PingAuthenticationApiContract.JSON.CODE)
    private String code;

    @SerializedName(PingAuthenticationApiContract.JSON.GRANT_TYPE)
    private String grantType = BuildConfig.GRANT_TYPE;

    @SerializedName(PingAuthenticationApiContract.JSON.CLIENT_ID)
    private String clientId = BuildConfig.CLIENT_ID;

    @SerializedName(PingAuthenticationApiContract.JSON.CLIENT_SECRET)
    private String clientSecret = BuildConfig.CLIENT_SECRET;

    public TokenExchangeRequest(String flowId) {
        super(flowId, PingAuthenticationApiContract.ACTIONS.TOKEN_EXCHANGE);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String toJsonString(){
        return new Gson().toJson(this);
    }
}
