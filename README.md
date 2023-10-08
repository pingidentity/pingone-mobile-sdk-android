# PingOne MFA Mobile SDK

## Overview
[![Maven Central](https://img.shields.io/maven-central/v/com.pingidentity.pingonemfa/android-sdk.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:com.pingidentity.pingonemfa)

PingOne MFA Mobile SDK is a set of components and services targeted at enabling organizations to include multifactor authentication (MFA) into native applications.  
This solution leverages Ping Identity’s expertise in MFA technology, as a component that can be embedded easily and quickly into a new or existing application. 
Release notes can be found [here](./release-notes.md).

### Documentation

Reference documentation is available for PingOne MFA Mobile SDK, describing its capabilities, features, installation and setup, integration with mobile apps, deployment and more:

* [Introduction to PingOne MFA](https://docs.pingidentity.com/csh?Product=p1&context=p1mfa_c_introduction)
* [PingOne MFA Mobile SDK Overview](https://apidocs.pingidentity.com/pingone/native-sdks/v1/api/#pingone-mfa-sdk-for-android)
* [PingOne MFA Mobile SDK API Documentation](https://pingidentity.github.io/pingone-mobile-sdk-android/index.html)
* [PingOne MFA Mobile SDK Sample App](https://github.com/pingidentity/pingone-sample-app-android)
* [PingOne MFA SDK Ready-For-Use Authenticator App](https://github.com/pingidentity/pingone-authenticator-sample-app-android)

### Content
1. [Prerequisites](#1-prerequisites)
   1. [Minimum requirements](#11-minimum-requirements-)
   2. [Known limitations](#12-known-limitations-)
   3. [Prerequisites for using FCM push kit](#13-prerequisites-for-using-fcm-push-kit-)
   4. [Prerequisites for using HMS push kit](#14-prerequisites-for-using-hms-push-kit-)
   5. [Configure push messaging on the PingOne Portal](#15-configure-push-messaging-on-the-pingone-portal)
      1. [FCM Push Notification](#151-fcm-push-notification-)
      2. [HMS Push Notification](#152-hms-push-notification-)
   6. [Add the PingOne MFA SDK component into your existing project](#16-add-the-pingone-mfa-sdk-component-into-your-existing-project)
   7. [Working with push messages in Android](#17-working-with-push-messages-in-android)
      1. [Register device token on PingOne server](#171-register-device-token-on-pingone-server)
   8. [Handling Push Notifications](#18-handling-push-notifications)
      1. [FCM](#181-fcm)
      2. [HMS](#182-hms)
2. [Device Integrity Validation](#2-device-integrity-validation)

## 1. Prerequisites

### 1.1 Minimum requirements: 

PingOne MFA Mobile SDK supports Android 8.0 (API level 26) and up, Gradle 7.2 and up, Java 17 and up. Starting Android 13 (API level 33) the application needs to request the 'Post Notifications' permission from the user in order to show notifications. For more information see [Notification Runtime Permission Documentation](https://developer.android.com/guide/topics/ui/notifiers/notification-permission).


### 1.2 Known limitations:

* Version 1.9.0 of the PingOne MFA SDK for Android did not support SL4J dependency version 2.0.0 or higher. This limitation affects users who require SL4J version 2.0.0 or higher to run their application. SL4J is a logging library that is commonly used by Java applications. The PingOne MFA SDK uses SL4J as a dependency to log its own internal messages.
**Workaround**:
Use a compatible version of SL4J (lower than 2.0.0).

This issue was fixed in version 1.10.0.


### 1.3 Prerequisites for using FCM push kit:

Prepare the FCM push messaging mandatory data from Firebase developers console:

* Package name
* Server key
* google-services.json

Refer to: [Add Firebase to your Android project](https://firebase.google.com/docs/android/setup).


### 1.4 Prerequisites for using HMS push kit:

Prepare the HMS push messaging mandatory data from Huawei developers console:

* Package name
* App ID
* Client ID
* Client secret
* agconnect-services.json

Refer to: [Integrating Push Kit](https://developer.huawei.com/consumer/en/codelabsPortal/carddetails/HMSPushKit).


### 1.5 Configure push messaging on the PingOne Portal

#### 1.5.1 FCM Push Notification:

Add the google-services.json retrieved from the Firebase developers console to your project.

When configuring your PingOne MFA SDK application in the PingOne admin web console you should fill in the Package Name and the Server Key. See [Edit an application](https://docs.pingidentity.com/bundle/pingone/page/avw1564020489881.html) in the administration guide.

#### 1.5.2 HMS Push Notification:

Add the agconnect-services.json retrieved from the Huawei developers console to your project.

When configuring your PingOne MFA SDK application in the PingOne admin web console you should fill in the Package Name, App ID, Client ID and the Client Secret. See [Edit an application](https://docs.pingidentity.com/bundle/pingone/page/avw1564020489881.html) in the administration guide.



### 1.6 Add the PingOne MFA SDK component into your existing project

1. In the Project `build.gradle` file, make sure you have the `mavenCentral` repository:
```groovy
// ... 
repositories {
   mavenCentral()
}
// ...  
 ```
2. In the application `build.gradle` file add the  [latest version of the PingOne MFA Android SDK](https://search.maven.org/search?q=g:com.pingidentity.pingonemfa):
```groovy
 dependencies {
   // Check for the latest version at https://search.maven.org/search?q=g:com.pingidentity.pingonemfa 
   implementation 'com.pingidentity.pingonemfa:android-sdk:1.10.0'
}  
 ```  


### 1.7 Working with push messages in Android

PingOne MFA SDK utilizes push messaging in order to authenticate end users. PingOne MFA SDK can work side by side within an app that uses push messaging. This page details the steps needed in order to work with push messages in Android. Your application may receive push messages from the PingOne SDK server, and also from other sources. As a result, your implementation of the FirebaseMessagingService or HmsMessageService will have to differentiate between push messages sent from the PingOne SDK server and other messages, and pass them to the PingOne SDK component for processing.  
In your app, add the appropriate section in your AndroidManifest.xml file (FCM or HMS messaging service), and add the appropriate class.


#### 1.7.1 Register device token on PingOne server

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
Make sure you set the device’s push token before you call `PingOne.pair`, and make sure you update the PingOne MFA SDK Library with the new device's push token each time it changes.

### 1.8 Handling Push Notifications

PingOne MFA SDK will only handles push notifications which were issued by the PingOne SDK server. For other push notifications, the `PingOneSDKError` object with the code `10002, unrecognizedRemoteNotification` will be returned.
You can use the "category" field to customize the notification behavior according to the value set on the PingOne server. Retrieve the category of the push message by calling `remoteMessage.getData().get("category")`.
For information on selecting a category on the server side, see: [edit a notification template](https://docs.pingidentity.com/r/en-us/pingone/p1_c_edit_notification).

#### 1.8.1 FCM
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


#### 1.8.2 HMS
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

## 2. Device Integrity Validation

Beginning with version 1.9.0, PingOne Android SDK uses the [Google Play Integrity API](https://developer.android.com/google/play/integrity/overview#security-considerations) to perform device integrity validation for threat protection. Previously, the SDK used Google's SafetyNet API.
Use of the SafetyNet API has been deprecated, and device integrity validation will fail for applications using SDK version 1.9.0 and higher if they have not been updated to use the Play Integrity API.

To use the Play Integrity API:

1. Setup a Google Cloud project and [enable Play Integrity API](https://developer.android.com/google/play/integrity/setup) in the project. Find the project number in the project settings.
2. Add a Play Integrity API dependency in your application:
   ```groovy
   dependencies{
      implementation "com.google.android.play:integrity:1.1.0"
   }
   ```
3. Pass your Google Cloud project number to the SDK by calling:
   ```java  
   public static void setGooglePlayIntegrityProjectNumber(Context context, String projectNumber);  
   ```  

Refer to [Use the Play Integrity API](https://support.google.com/googleplay/android-developer/answer/11395166) for details on setting up and managing the Play Integrity API.

See the **Mobile device integrity check** section in the [PingOne MFA SDK for Android](https://apidocs.pingidentity.com/pingone/native-sdks/v1/api/#pingone-mfa-sdk-for-android) for detailed step-by-step instructions.


## Disclaimer

THE SAMPLE CODE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
OUT OF OR IN CONNECTION WITH THE SAMPLE CODE OR THE USE OR OTHER DEALINGS IN  
THE SAMPLE CODE.  FURTHERMORE, THIS SAMPLE CODE IS NOT COMMERCIALLY SUPPORTED BY PING IDENTITY BUT QUESTIONS MAY BE ADDRESSED TO PING'S SUPPORT CENTER OR MAY BE OTHERWISE ADDRESSED IN THE RELATED DOCUMENTATION.

Any questions or issues should go to the support center, or may be discussed in the [Ping Identity developer communities](https://support.pingidentity.com/s/topic/0TO1W000000atTxWAI/pingone-mfa).
