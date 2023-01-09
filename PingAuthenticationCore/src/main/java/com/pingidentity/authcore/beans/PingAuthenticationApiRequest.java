package com.pingidentity.authcore.beans;


import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pingidentity.authcore.models.DeviceRef;

import java.util.Hashtable;
/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class PingAuthenticationApiRequest {


    private static final String AUTH_ACTION_PLACEHOLDER = "application/vnd.pingidentity.%s+json";
    private transient Hashtable<String, String> headers;

    private transient String flowId;

    public PingAuthenticationApiRequest(String flowId, String action){
        this.flowId = flowId;
        headers = new Hashtable<>();
        headers.put(PingAuthenticationApiContract.XSRF_HEADER, "PingFederate");
        headers.put(PingAuthenticationApiContract.CONTENT_TYPE_ACTION_HEADER,
                action.equalsIgnoreCase(PingAuthenticationApiContract.ACTIONS.TOKEN_EXCHANGE)
                        ?action:
                        String.format(AUTH_ACTION_PLACEHOLDER, action));
    }

    public Hashtable<String, String> getHeaders(){
        return headers;
    }

    @Expose
    @SerializedName(PingAuthenticationApiContract.JSON.MOBILE_PAYLOAD)
    private String mobilePayload;

    @SerializedName(PingAuthenticationApiContract.JSON.USERNAME)
    private String username;

    @SerializedName(PingAuthenticationApiContract.JSON.PASSWORD)
    private String password;

    @SerializedName(PingAuthenticationApiContract.JSON.DEVICE_ID)
    private DeviceRef deviceId;

    @SerializedName(PingAuthenticationApiContract.JSON.OTP)
    private String otp;

    public String toJsonString(){
        return new Gson().toJson(this);
    }

    public String getFlowId() {
        return flowId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void setMobilePayload(String mobilePayload) {
        this.mobilePayload = mobilePayload;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = new DeviceRef();
        this.deviceId.setDeviceId(deviceId);
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
