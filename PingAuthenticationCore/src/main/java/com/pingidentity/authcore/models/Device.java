package com.pingidentity.authcore.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.pingidentity.authcore.beans.PingAuthenticationApiContract;

import java.util.ArrayList;
import java.util.Date;

/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class Device implements Parcelable {

    /*
     * Unique identifier of a trusted device in PingID SDK server.
     */
    @SerializedName(PingAuthenticationApiContract.DEVICE_ID)
    private String id;

    @SerializedName(PingAuthenticationApiContract.DEVICE_TYPE)
    private String type;

    /*
     * The device target. For example, if the device type is EMAIL, the target would be an email address.
     * Email addresses and phone numbers are masked by default.
     */
    @SerializedName(PingAuthenticationApiContract.DEVICE_TARGET)
    private String target;

    /*
     * Model of the device, for example, iPhone 5S. Empty for OTP devices (SMS, voice, email).
     */
    @SerializedName(PingAuthenticationApiContract.DEVICE_NAME)
    private String name;

    @SerializedName(PingAuthenticationApiContract.DEVICE_NICKNAME)
    private String nickname;

    /*
     * Role of the device in the user's network. Possible values: Primary, Trusted.
     */
    @SerializedName(PingAuthenticationApiContract.DEVICE_ROLE)
    private String role;

//    /*
//     * The date and time of the first registration of the device,
//     * in the context of the application
//     */
//    @SerializedName(PingAuthenticationApiContract.DEVICE_ENROLLMENT_TIME)
//    private Date date;

    /*
     * The ID of the PingID SDK customer mobile application.
     */
    @SerializedName(PingAuthenticationApiContract.DEVICE_APP_ID)
    private String applicationId;


    /*
     * The date and time when the user device's bypass mode will expire.
     */
    @SerializedName(PingAuthenticationApiContract.DEVICE_BYPASS_EXP)
    private String bypassExpiration;

    /*
     * Indicates whether the user’s device is in bypass mode.
     */
    @SerializedName(PingAuthenticationApiContract.DEVICE_IS_BYPASS)
    private boolean bypassed;

    /*
     * Indicates whether the device can receive a push notification, when the device is a mobile
     * application. For other device types, this attribute is always false.
     */
    @SerializedName(PingAuthenticationApiContract.DEVICE_PUSH_ENABLED)
    private boolean pushEnabled;

    /*
     * The device's operating system and version, if the device is a mobile application.
     * For other device types, this attribute is empty.
     */
    @SerializedName(PingAuthenticationApiContract.DEVICE_OS_VERSION)
    private String osVersion;

    /*
     * The device's application version, if the device is a mobile application.
     * For other device types, this attribute is empty.
     */
    @SerializedName(PingAuthenticationApiContract.DEVICE_APP_VERSION)
    private String applicationVersion;

    @SerializedName(PingAuthenticationApiContract.DEVICE_IS_USABLE)
    private boolean usable;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//    public Date getDate() {
//        return date;
//    }
//
//    public void setDate(Date date) {
//        this.date = date;
//    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getBypassExpiration() {
        return bypassExpiration;
    }

    public void setBypassExpiration(String bypassExpiration) {
        this.bypassExpiration = bypassExpiration;
    }

    public boolean isBypassed() {
        return bypassed;
    }

    public void setBypassed(boolean bypassed) {
        this.bypassed = bypassed;
    }

    public boolean isPushEnabled() {
        return pushEnabled;
    }

    public void setPushEnabled(boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    public boolean isUsable() {
        return usable;
    }

    public void setUsable(boolean usable) {
        this.usable = usable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }
        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    protected Device(Parcel in) {
        id = in.readString();
        type = in.readString();
        target = in.readString();
        name = in.readString();
        nickname = in.readString();
        role = in.readString();
       // date = in.readSerializable();
        applicationId = in.readString();
        bypassExpiration = in.readString();
        bypassed = Boolean.parseBoolean(in.readString());
        pushEnabled = Boolean.parseBoolean(in.readString());
        osVersion = in.readString();
        applicationVersion = in.readString();
        usable = Boolean.parseBoolean(in.readString());
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(target);
        dest.writeString(name);
        dest.writeString(nickname);
        dest.writeString(role);
        dest.writeString(applicationId);
        dest.writeString(bypassExpiration);
        dest.writeString(String.valueOf(bypassed));
        dest.writeString(String.valueOf(pushEnabled));
        dest.writeString(osVersion);
        dest.writeString(applicationVersion);
        dest.writeString(String.valueOf(usable));
    }
}
