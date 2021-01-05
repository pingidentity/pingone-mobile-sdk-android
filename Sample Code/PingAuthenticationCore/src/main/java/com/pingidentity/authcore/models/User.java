package com.pingidentity.authcore.models;

import com.google.gson.annotations.SerializedName;
import com.pingidentity.authcore.beans.PingAuthenticationApiContract;

import java.util.Date;
/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class User {

    @SerializedName(PingAuthenticationApiContract.USER_ID)
    private String id;

    @SerializedName(PingAuthenticationApiContract.USER_FIRST_NAME)
    private String firstName;

    @SerializedName(PingAuthenticationApiContract.USER_LAST_NAME)
    private String lastName;

    /*
     * The user’s status in PingID SDK. Possible values: NOT_ACTIVE, ACTIVE, SUSPENDED.
     */
    @SerializedName(PingAuthenticationApiContract.USER_STATUS)
    private String status;

    /*
     * The last date and time the user authenticated successfully.
     */
    @SerializedName(PingAuthenticationApiContract.USER_LAST_LOGIN)
    private Date lastLogin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
}
