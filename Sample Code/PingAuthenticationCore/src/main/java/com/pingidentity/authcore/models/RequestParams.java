package com.pingidentity.authcore.models;

/**
 * Generic helper class that includes all possible request parameters.
 *
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class RequestParams {

    /*
     * mandatory for every request, except the initial one
     */
    private String flowId;
    private String action;

    /*
     * Set values to this parameters when action is checkUsernamePassword
     */
    private String username;
    private String password;

    private String mobilePayload;

    private String otp;
    private String deviceRef;

    /*
     * Used to transfer any String from customers server.
     */
    private String dynamicData;

    public String getMobilePayload() {
        return mobilePayload;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public void setMobilePayload(String mobilePayload) {
        this.mobilePayload = mobilePayload;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setDeviceRef(String deviceRef){
        this.deviceRef = deviceRef;
    }

    public String getDeviceRef(){
        return deviceRef;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getOtp() {
        return otp;
    }

    public String getDynamicData() {
        return dynamicData;
    }

    public void setDynamicData(String dynamicData) {
        this.dynamicData = dynamicData;
    }
}
