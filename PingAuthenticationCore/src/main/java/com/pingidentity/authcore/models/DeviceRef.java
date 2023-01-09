package com.pingidentity.authcore.models;

import com.google.gson.annotations.SerializedName;
import com.pingidentity.authcore.beans.PingAuthenticationApiContract;

/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class DeviceRef {

    @SerializedName(PingAuthenticationApiContract.JSON.ID)
    String deviceId;

    public void setDeviceId(String deviceId){
        this.deviceId = deviceId;
    }
}
