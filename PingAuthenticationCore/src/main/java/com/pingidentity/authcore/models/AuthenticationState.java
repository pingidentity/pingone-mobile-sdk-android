package com.pingidentity.authcore.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class AuthenticationState implements Parcelable {

    private final String flowId;
    private String status;

    private String serverPayload;
    private String mobilePayload;

    private String message;
    private String userMessage;
    private String details;

    private String code;

    private String accessToken;
    private final ArrayList<String> actions;
    private ArrayList<Device> devices;

    private String fullErrorJson;

    public AuthenticationState(String flowId, String status){
        this.flowId = flowId;
        this.status = status;
        this.actions = new ArrayList<>();
        this.devices = new ArrayList<>();
    }

    protected AuthenticationState(Parcel in) {
        flowId = in.readString();
        status = in.readString();
        code = in.readString();
        serverPayload = in.readString();
        accessToken = in.readString();
        fullErrorJson = in.readString();
        actions = new ArrayList<>();
        in.readList(actions, null);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(flowId);
        dest.writeString(status);
        dest.writeString(code);
        dest.writeString(serverPayload);
        dest.writeString(accessToken);
        dest.writeString(fullErrorJson);
        dest.writeStringList(actions);
        dest.writeTypedList(devices);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AuthenticationState> CREATOR = new Creator<AuthenticationState>() {
        @Override
        public AuthenticationState createFromParcel(Parcel in) {
            return new AuthenticationState(in);
        }

        @Override
        public AuthenticationState[] newArray(int size) {
            return new AuthenticationState[size];
        }
    };

    public String getFlowId() {
        return flowId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getCode(){
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getServerPayload() {
        return serverPayload;
    }

    public void setServerPayload(String serverPayload) {
        this.serverPayload = serverPayload;
    }

    public void addAction(String action){
        actions.add(action);
    }

    public void removeAction(String action){
        actions.remove(action);
    }

    public ArrayList<String> getActions(){
        return actions;
    }

    public void addDevice(Device device){
        devices.add(device);
    }

    public ArrayList<Device> getDevices(){
        return devices;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }


    public String getMobilePayload() {
        return mobilePayload;
    }

    public void setMobilePayload(String mobilePayload) {
        this.mobilePayload = mobilePayload;
    }

    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

    public String getAccessToken(){
        return accessToken;
    }

    public String getFullErrorJson(){
        return fullErrorJson;
    }

    public void setFullErrorJson(String errorJson){
        this.fullErrorJson = errorJson;
    }
}
