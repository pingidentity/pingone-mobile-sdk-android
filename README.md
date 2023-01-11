# PingOne for Customers Mobile SDK

## Overview

PingOne for Customers Mobile SDK is a set of components and services targeted at enabling organizations to include multifactor authentication (MFA) into native applications.  
This solution leverages Ping Identity’s expertise in MFA technology, as a component that can be embedded easily and quickly into a new or existing application. The PingOne for Customers Mobile SDK repository comprises of the following components:

* A sample app example source code for Android.
* Mobile Authentication Framework for Android developers integrated inside a sample app.

The repository contains  Android project that is ready to be built after all the prerequisites are satisfied. To open the Sample Code as a Project in Android Studio, clone the whole directory and go to: File -> New -> Import Project.

Release notes can be found [here](./release-notes.md).

### Documentation

Reference documentation is available for PingOne for Customers Mobile SDK, describing its capabilities, features, installation and setup, integration with mobile apps, deployment and more:

* [PingOne for Customers Mobile SDK release notes and admin related documentation](https://docs.pingidentity.com/csh?Product=p1&context=p1mfa_c_introduction)
* [PingOne for Customers Mobile SDK developer documentation](https://apidocs.pingidentity.com/pingone/native-sdks/v1/api/#pingone-mfa-native-sdks)

### Content
1. [Set up a mobile app using the PingOne SDK sample code](#1-set-up-a-mobile-app-using-the-pingone-sdk-sample-code)
   1. [Prerequisites for using FCM push kit](#11-prerequisites-for-using-fcm-push-kit)
   2. [Prerequisites for using HMS push kit](#12-prerequisites-for-using-hms-push-kit)
   3. [Configure push messaging on the PingOne Portal](#13-configure-push-messaging-on-the-pingone-portal)
      1. [FCM Push Notification](#131-fcm-push-notification)
      2. [HMS Push Notification](#132-hms-push-notification)
   4. [Add the PingOne SDK component into your existing project](#14-add-the-pingone-sdk-component-into-your-existing-project)
   5. [Working with push messages in Android](#15-working-with-push-messages-in-android)
      1. [Register device token on PingOne server](#151-register-device-token-on-pingone-server)
   6. [Handling Push Notifications](#16-handling-push-notifications)
      1. [FCM](#161-fcm)
      2. [HMS](#162-hms)
2. [PingOne Mobile SDK Sample App](#2-pingone-mobile-sdk-sample-app)
   1. [Pairing](#21-pairing)
   2. [Send Logs](#22-send-logs)
   3. [Get One Time Passcode](#23-get-one-time-passcode)
   4. [Device Integrity Validation](#24-device-integrity-validation)
   5. [Authentication via QR code scanning](#25-authentication-via-qr-code-scanning)
3. [Mobile Authentication framework](#3-mobile-authentication-framework)
4. [Migrate from PingID SDK to PingOne SDK](#4-migrate-from-pingid-sdk-to-pingone-sdk)
   1. [Manual flow](#41-manual-flow)
   2. [Automatic flow](#42-automatic-flow)

## 1. Set up a mobile app using the PingOne SDK sample code

Note: PingOne for Customers Mobile SDK supports Android 8.0 (API level 26) and up, Gradle 7.2 and up, Java 11 and up. Starting Android 13 (API level 33) the application needs to request the 'Post Notifications' permission from the user to be able to show notifications. For more information see [Notification Runtime Permission Documentation](https://developer.android.com/guide/topics/ui/notifiers/notification-permission).


### 1.1 Prerequisites for using FCM push kit:

Prepare the FCM push messaging mandatory data from Firebase developers console:

* Package name
* Server key
* google-services.json

Refer to: [Add Firebase to your Android project](https://firebase.google.com/docs/android/setup).


### 1.2 Prerequisites for using HMS push kit:

Prepare the HMS push messaging mandatory data from Huawei developers console:

* Package name
* App ID
* Client ID
* Client secret
* agconnect-services.json

Refer to: [Integrating Push Kit](https://developer.huawei.com/consumer/en/codelabsPortal/carddetails/HMSPushKit).


### 1.3 Configure push messaging on the PingOne Portal

#### 1.3.1 FCM Push Notification:

Add the google-services.json retrieved from the Firebase developers console to your project.

When configuring your PingOne SDK application in the PingOne admin web console you should fill in the Package Name and the Server Key. See [Edit an application](https://docs.pingidentity.com/bundle/pingone/page/avw1564020489881.html) in the administration guide.

#### 1.3.2 HMS Push Notification:

Add the agconnect-services.json retrieved from the Huawei developers console to your project.

When configuring your PingOne SDK application in the PingOne admin web console you should fill in the Package Name, App ID, Client ID and the Client Secret. See [Edit an application](https://docs.pingidentity.com/bundle/pingone/page/avw1564020489881.html) in the administration guide.



### 1.4 Add the PingOne SDK component into your existing project
[![Maven Central](https://img.shields.io/maven-central/v/com.pingidentity.pingonemfa/android-sdk.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:com.pingidentity.pingonemfa)

1. In the Project `build.gradle` file, make sure you have the `mavenCentral` repository:
```groovy
// ... 
repositories { 
    mavenCentral() 
}
// ...  
 ```
2. In the application `build.gradle` file add the  [latest version of the PingOne Android SDK](https://search.maven.org/search?q=g:com.pingidentity.pingonemfa):
```groovy
 dependencies { 
     // Check for the latest version at https://search.maven.org/search?q=g:com.pingidentity.pingonemfa 
     implementation 'com.pingidentity.pingonemfa:android-sdk:1.8.1' 
 }  
 ```  


### 1.5 Working with push messages in Android

PingOne SDK utilizes push messaging in order to authenticate end users. PingOne SDK can work side by side within an app that uses push messaging. This page details the steps needed in order to work with push messages in Android. Your application may receive push messages from the PingOne SDK server, and also from other sources. As a result, your implementation of the FirebaseMessagingService or HmsMessageService will have to differentiate between push messages sent from the PingOne SDK server and other messages, and pass them to the PingOne SDK component for processing.  
In your app, add the appropriate section in your AndroidManifest.xml file (FCM or HMS messaging service), and add the appropriate class.


#### 1.5.1 Register device token on PingOne server

Retrieve the Push Registration Token from the FCM or HMS and set it in the PingOne Library by calling
```java
public static void setDeviceToken(Context context, String token, NotificationProvider provider, PingOneSDKCallback callback);
```
For FCM:
 ```java
 PingOne.setDeviceToken(context, token, NotificationProvider.FCM, new PingOne.PingOneSDKCallback())  
```  
For HMS:
 ```java
 PingOne.setDeviceToken(context, token, NotificationProvider.HMS, new PingOne.PingOneSDKCallback())  
```  
Make sure you set the device’s push token before you call `PingOne.pair`, and make sure you update the PingOne SDK Library with the new device's push token each time it changes.

### 1.6 Handling Push Notifications

PingOne SDK will only handle push notifications which were issued by the PingOne SDK server. For other push notifications, `PingOneSDKError` with the code `10002, unrecognizedRemoteNotification` will be returned. 
PingOne SDK uses "category" field to add the possibility to customize the notification behavior according to the value set on the PingOne server. Retrieve the category of the push message by calling `remoteMessage.getData().get("category")`. 
For information on selecting a category on the server side, see: [edit a notification template](https://docs.pingidentity.com/r/en-us/pingone/p1_c_edit_notification).

#### 1.6.1 FCM
Implement the PingOne library’s push handling by passing the RemoteMessage received from FCM to the PingOne Library. (Note: you must override the `onMessageReceived` method of the `FirebaseMessagingService`)

```java  
@Override  
public void onMessageReceived(final RemoteMessage remoteMessage) {
    PingOne.processRemoteNotification(context, remoteMessage, new PingOne.PingOneNotificationCallback() {
        @Override public void onComplete(@Nullable NotificationObject notificationObject, PingOneSDKError error) { 
            if (notificationObject == null){ 
                //the push is not from PingOne - apply your customized application logic
            }else{ 
                //the object contains two options - approve and deny - present them to the user            
            }  
        }
    });
}  
```    


#### 1.6.2 HMS
Implement the PingOne library’s push handling by passing the RemoteMessage **data** received from HMS to the PingOne Library.  (Note: you must override the `onMessageReceived` method of the `HmsMessageService`)
```java  
@Override  
public void onMessageReceived(final RemoteMessage remoteMessage) {
    PingOne.processRemoteNotification(context, remoteMessage.getData(), new PingOne.PingOneNotificationCallback() {
        @Override public void onComplete(@Nullable NotificationObject notificationObject, PingOneSDKError error) { 
            if (notificationObject == null){ 
                //the push is not from PingOne - apply your customized application logic
            }else{ 
                //the object contains two options - approve and deny - present them to the user            
            }  
        }
    });
}  
```  


## 2. PingOne Mobile SDK sample app

The PingOne Mobile SDK bundle provides a sample app that includes all the basic flows in order to help you get started.


### 2.1 Pairing

To manually pair the device, call the following method with your pairing key:

```java  
public static void pair(Context context, String pairingKey, PingOneSDKPairingCallback callback);  
```  

To automatically pair the device using OpenID Connect:

1. call this function to get the PingOne SDK mobile payload:
```java  
public static String generateMobilePayload(Context context);  
```  
2. pass the received mobile payload on the OIDC request as the value of query param: `mobilePayload`
3. call this function with the ID token after the OIDC authentication completes:
```java  
public static void processIdToken(String idToken, PingOnePairingObjectCallback callback);  
```  

### 2.2 Send Logs

The PingOne Mobile SDK bundle writes fixed size, encrypted log messages to memory. To send these logs to our server for support, call the ```public static void sendLogs(Context context, PingOneSendLogsCallback callback)``` method.  
For example:
 ```java
 PingOne.sendLogs(context, new PingOne.PingOneSendLogsCallback() {  
     @Override public void onComplete(@Nullable final String supportId, @Nullable PingOneSDKError pingOneSDKError) {
         if(supportId!=null){ 
             // pass this supportId value to PingOne support team 
         } 
     }
});  
```  

### 2.3 Get One Time Passcode

Requests the SDK to provide One Time Passcode.

Signature:
```java
public static void getOneTimePassCode(Context context, PingOneOneTimePasscodeCallback callback)  
```  

For example:
 ```java  
 PingOne.getOneTimePassCode(context, new PingOne.PingOneOneTimePasscodeCallback() {
     @Override public void onComplete(@Nullable OneTimePasscodeInfo otpData, @Nullable PingOneSDKError error) {
         //handle response 
      }
 };  
```  

### 2.4 Device Integrity Validation

PingOne uses Google's SafetyNet to perform device integrity validation for threat protection.  
To use this feature, you should obtain a SafetyNet API Key. Refer to [Obtain a SafetyNet API Key](https://developer.android.com/training/safetynet/attestation#obtain-api-key).  
The retrieved API key should be passed to the PingOne SDK using the following new API method:

```java  
PingOne.setSafetyNetApiKey(Context context, String apiKey);  
```  

### 2.5 Authentication via QR code scanning

PingOne SDK provides an ability to authenticate via scanning the QR code (or typing the code manually). The code should  be passed to the PingOne SDK using the following API method:

```java  
PingOne.authenticate(context, authCode, new PingOne.PingOneAuthenticationCallback() {
    @Override public void onComplete(@Nullable AuthenticationObject authObject, @Nullable PingOneSDKError error){
        if (authObject != null){
            //parse authObject (see below) 
        } 
    }
});  
```  

authCode should be passed as is or inside a URI. For example: "7F45HGf5", "https://myapp.com/pingonesdk?authentication_code=7F45HGf5", "pingonesdk?authentication_code=7F45HGf5"

AuthenticationObject is implemented as Parcelable to provide the developers an ability to  
pass it between activities and/or fragments and contains the following fields and methods:
```java  
public class AuthenticationObject {
    //for inner use
    String requestId;
    //for inner use 
    String authCode; 
    /* 
     * a JsonArray of users. See UserModel below for further understanding 
     * what it contains. 
     */ 
    JsonArray users;
    //for passing any data from server to end-user 
    String clientContext; 
    /* 
     * a status String value returned from a server when user calls an authenticate API method 
     * Possible values at this step: 
     * CLAIMED 
     * EXPIRED 
     * COMPLETED 
     */ 
     String status; 
    /* 
     * String that determines if user approval is required to complete an authentication
     * Possible values: 
     * REQUIRED 
     * NOT_REQUIRED 
     */ 
     String needsApproval;
     /* 
      * if userApproval is "REQUIRED" the approve or deny method should be called with a userId
      * of the user, who triggered the method. The application should register for a callback 
      * that will return Status with one of the following values: 
      * COMPLETED 
      * EXPIRED 
      * DENIED 
      */ 
      public void approve(Context context, String userId, PingOneAuthenticationStatusCallback callback); 
      public void deny(Context context, String userId, PingOneAuthenticationStatusCallback callback);
}  
```  
The JsonArray of users may be parsed to the array of following model:
```java  
public class UserModel{  
    String userId; 
    String email; 
    String given; 
    String family; 
    String username;
}  
```  

## 3. Mobile Authentication Framework

The sample code contains two modules: `PingAuthenticationUI` and `PingAuthenticationCore`.  
The following method starts an authentication process when the user taps "Authentication API" on the main screen. The authentication process is completed by the PingFederate Authentication API.  
**Note:** Before calling this method, you need to update your `OIDC_ISSUER` and `CLIENT_ID` in the `gradle.properties` class at `PingAuthenticationCore` module. See [Authentication API for Android Developers](https://github.com/pingidentity/mobile-authentication-framework-android)
 ```java 
 public void authenticate(@NonNull Activity context, @NonNull String mobilePayload, @Nullable String dynamicData)  
```  
This is public method of PingAuthenticationUI module, which should be instantiated first as follows:
```java  
PingAuthenticationUI authenticationUI = new PingAuthenticationUI();  
authenticationUI.authenticate(context, mobilePayload, dynamicData);  
```  

## 4. Migrate from PingID SDK to PingOne SDK 

If your application is currently integrated with PingID SDK, it is possible to migrate to PingOne SDK.
First, make sure to set up the PingOne environment in the admin console following the convergence documentation.
Then set up mobile application as follows:
 1. Remove the `PingID_SDK.aar` library file from the `libs` folder of your application and any methods that call that SDK.
 2. Setup a PingOne mobile SDK as described in [set-up section](#1-set-up-a-mobile-app-using-the-pingone-sdk-sample-code) and implement the API methods as described in the [PingOne Mobile SDK sample app](#2-pingone-mobile-sdk-sample-app).

### 4.1 Manual flow    

Call the migration API method:
```java 
/**
 * Migrates the PingID SDK application to the PingOne platform
 *
 * @param context the context of calling application
 * @param callback the PingOneMigrationStatusCallback object that will receive the result
 */
PingOne.migrateFromPingID(Context context, PingOneMigrationStatusCallback callback);
```
The `onComplete()` method of the callback will be triggered at the migration process completion and will receive `MigrationStatus` object, `PairingInfo` object and `PingOneSDKError` object, where `MigrationStatus` is one of the following:
```java
/**
 * enum that represents the migration status returned from the SDK
 */
public enum MigrationStatus {
   /*
    * There is no PingID data that has to be migrated
    */
   NOT_NEEDED,
   /*
    * The migration process was completed successfully
    */
   DONE,
   /*
    * The migration process failed
    */
   FAILED,
   /*
    * The migration process failed due to server error, client can try again
    */
   TEMPORARILY_FAILED,
   /*
    * The migration process is in progress
    */
   IN_PROGRESS
}
```
For example:
```java 
PingOne.migrateFromPingID(context, new PingOne.PingOneMigrationStatusCallback() {
   @Override
   public void onComplete(MigrationStatus migrationStatus, @Nullable PairingInfo pairingInfo, @Nullable PingOneSDKError error) {
       /*
        * check migrationStatus and continue accordingly:
        * if the status is FAILED the error object will contain the details of the error
        * if the status is TEMPORARILY_FAILED the client can retry the process (may happen due to connectivity issues, etc.)
        */  
   } 
});
```
Possible errors returned from the migration API:
```java 
MIGRATION_ALREADY_RUNNING(10014, "Migration is already in progress - you cannot make another API call until it is completed")
MIGRATION_NOT_NEEDED(10015, "The device does not have to be migrated because it is already paired.")
```

### 4.2 Push notification flow 

Upon getting authentication push notification, the migration will start ***automatically*** in a background thread. 
When migration is completed, the PingOne `NotificationObject` will be returned to the application in the `PingOne.processRemoteNotification()` callback response. 

## Disclaimer

THE SAMPLE CODE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
OUT OF OR IN CONNECTION WITH THE SAMPLE CODE OR THE USE OR OTHER DEALINGS IN  
THE SAMPLE CODE.  FURTHERMORE, THIS SAMPLE CODE IS NOT COMMERCIALLY SUPPORTED BY PING IDENTITY BUT QUESTIONS MAY BE ADDRESSED TO PING'S SUPPORT CENTER OR MAY BE OTHERWISE ADDRESSED IN THE RELATED DOCUMENTATION.

Any questions or issues should go to the support center, or may be discussed in the [Ping Identity developer communities](https://community.pingidentity.com/collaborate).
